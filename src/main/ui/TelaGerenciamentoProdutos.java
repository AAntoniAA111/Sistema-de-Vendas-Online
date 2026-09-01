package ui;

import dao.ProdutoDAO;
import model.Produto;
import model.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Tela de gerenciamento de produtos, usada pela equipe interna
 * (admin e vendedor) para cadastrar, listar, alterar preço
 * e excluir produtos do catálogo.
 */
public class TelaGerenciamentoProdutos extends JFrame {

    private final Usuario usuarioLogado;
    private final ProdutoDAO produtoDAO = new ProdutoDAO();

    private DefaultTableModel modeloTabela;
    private JTable tabelaProdutos;

    private JTextField campoNome;
    private JTextField campoPreco;
    private JTextField campoEstoque;
    private JTextField campoCategoria;

    public TelaGerenciamentoProdutos(Usuario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
        configurarJanela();
        criarComponentes();
        carregarProdutos();
    }

    private void configurarJanela() {
        setTitle("Gerenciamento de Produtos - " + usuarioLogado.getLogin() + " (" + usuarioLogado.getTipo() + ")");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void criarComponentes() {
        setLayout(new BorderLayout(10, 10));

        // ----- Tabela de produtos -----
        modeloTabela = new DefaultTableModel(new String[]{"ID", "Nome", "Preço", "Estoque", "Categoria"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaProdutos = new JTable(modeloTabela);
        tabelaProdutos.getSelectionModel().addListSelectionListener(e -> preencherCamposComSelecao());
        JScrollPane scrollTabela = new JScrollPane(tabelaProdutos);
        scrollTabela.setBorder(BorderFactory.createTitledBorder("Produtos cadastrados"));

        // ----- Formulário -----
        JPanel painelFormulario = new JPanel(new GridLayout(4, 2, 5, 5));
        painelFormulario.setBorder(BorderFactory.createTitledBorder("Dados do produto"));

        painelFormulario.add(new JLabel("Nome:"));
        campoNome = new JTextField();
        painelFormulario.add(campoNome);

        painelFormulario.add(new JLabel("Preço:"));
        campoPreco = new JTextField();
        painelFormulario.add(campoPreco);

        painelFormulario.add(new JLabel("Estoque:"));
        campoEstoque = new JTextField();
        painelFormulario.add(campoEstoque);

        painelFormulario.add(new JLabel("Categoria (código):"));
        campoCategoria = new JTextField();
        painelFormulario.add(campoCategoria);

        // ----- Botões -----
        JButton botaoCadastrar = new JButton("Cadastrar novo");
        botaoCadastrar.addActionListener(e -> cadastrarProduto());

        JButton botaoAlterarPreco = new JButton("Alterar preço");
        botaoAlterarPreco.addActionListener(e -> alterarPreco());

        JButton botaoExcluir = new JButton("Excluir selecionado");
        botaoExcluir.addActionListener(e -> excluirProduto());

        JButton botaoLimpar = new JButton("Limpar campos");
        botaoLimpar.addActionListener(e -> limparCampos());

        JPanel painelBotoes = new JPanel(new GridLayout(1, 4, 5, 5));
        painelBotoes.add(botaoCadastrar);
        painelBotoes.add(botaoAlterarPreco);
        painelBotoes.add(botaoExcluir);
        painelBotoes.add(botaoLimpar);

        JPanel painelInferior = new JPanel(new BorderLayout(5, 5));
        painelInferior.add(painelFormulario, BorderLayout.CENTER);
        painelInferior.add(painelBotoes, BorderLayout.SOUTH);

        add(scrollTabela, BorderLayout.CENTER);
        add(painelInferior, BorderLayout.SOUTH);
    }

    /**
     * Busca todos os produtos no banco e preenche a tabela.
     */
    private void carregarProdutos() {
    try {
        List<Produto> produtos = produtoDAO.listarTodos();
        modeloTabela.setRowCount(0);

        for (Produto p : produtos) {
            modeloTabela.addRow(new Object[]{
                    p.getId(), p.getNome(), p.getPreco(), p.getEstoque(), p.getCategoria()
            });
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Erro ao carregar produtos: " + e.getMessage(),
                "Erro", JOptionPane.ERROR_MESSAGE);
    }
}

    /**
     * Preenche automaticamente o formulário com os dados
     * do produto selecionado na tabela, facilitando a edição.
     */
    private void preencherCamposComSelecao() {
        int linha = tabelaProdutos.getSelectedRow();
        if (linha == -1) return;

        campoNome.setText(modeloTabela.getValueAt(linha, 1).toString());
        campoPreco.setText(modeloTabela.getValueAt(linha, 2).toString());
        campoEstoque.setText(modeloTabela.getValueAt(linha, 3).toString());
        campoCategoria.setText(modeloTabela.getValueAt(linha, 4).toString());
    }

    private void cadastrarProduto() {
        try {
            String nome = campoNome.getText().trim();
            double preco = Double.parseDouble(campoPreco.getText().trim());
            int estoque = Integer.parseInt(campoEstoque.getText().trim());
            int categoria = Integer.parseInt(campoCategoria.getText().trim());

            if (nome.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Informe o nome do produto.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Produto produto = new Produto(nome, preco, estoque, categoria);
            produtoDAO.cadastrar(produto);

            JOptionPane.showMessageDialog(this, "Produto cadastrado com sucesso!");
            limparCampos();
            carregarProdutos();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Preço, estoque e categoria devem ser números.",
                    "Erro de formato", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao cadastrar: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void alterarPreco() {
        int linha = tabelaProdutos.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um produto na tabela.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int idProduto = (int) modeloTabela.getValueAt(linha, 0);
            double novoPreco = Double.parseDouble(campoPreco.getText().trim());

            produtoDAO.alterarPreco(idProduto, novoPreco);

            JOptionPane.showMessageDialog(this, "Preço atualizado com sucesso!");
            carregarProdutos();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Informe um preço válido.", "Erro de formato", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao alterar preço: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluirProduto() {
        int linha = tabelaProdutos.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um produto na tabela.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idProduto = (int) modeloTabela.getValueAt(linha, 0);
        String nomeProduto = modeloTabela.getValueAt(linha, 1).toString();

        int confirmacao = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja excluir \"" + nomeProduto + "\"?",
                "Confirmar exclusão", JOptionPane.YES_NO_OPTION);

        if (confirmacao != JOptionPane.YES_OPTION) return;

        try {
            produtoDAO.excluir(idProduto);
            JOptionPane.showMessageDialog(this, "Produto excluído com sucesso!");
            limparCampos();
            carregarProdutos();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao excluir: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparCampos() {
        campoNome.setText("");
        campoPreco.setText("");
        campoEstoque.setText("");
        campoCategoria.setText("");
        tabelaProdutos.clearSelection();
    }
}