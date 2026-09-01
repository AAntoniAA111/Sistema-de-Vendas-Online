package ui;

import dao.FormaPagamentoDAO;
import dao.PedidoDAO;
import dao.ProdutoDAO;
import model.FormaPagamento;
import model.ItemCarrinho;
import model.Produto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Tela de compras do cliente: lista produtos disponíveis,
 * permite montar um carrinho e finalizar o pedido.
 */
public class TelaCompras extends JFrame {

    private final int idCliente;
    private final ProdutoDAO produtoDAO = new ProdutoDAO();
    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private final FormaPagamentoDAO formaPagamentoDAO = new FormaPagamentoDAO();

    private final List<ItemCarrinho> carrinho = new ArrayList<>();
    private List<Produto> produtosDisponiveis = new ArrayList<>();

    private DefaultTableModel modeloProdutos;
    private DefaultTableModel modeloCarrinho;
    private JTable tabelaProdutos;
    private JTable tabelaCarrinho;
    private JSpinner spinnerQuantidade;
    private JLabel labelTotal;
    private JComboBox<FormaPagamento> comboFormaPagamento;

    public TelaCompras(int idCliente) {
        this.idCliente = idCliente;
        configurarJanela();
        criarComponentes();
        carregarProdutos();
        carregarFormasPagamento();
    }

    private void configurarJanela() {
        setTitle("Loja - Fazer Compra");
        setSize(700, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void criarComponentes() {
        setLayout(new BorderLayout(10, 10));

        // ----- Tabela de produtos disponíveis -----
        modeloProdutos = new DefaultTableModel(new String[]{"ID", "Produto", "Preço", "Estoque"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // tabela somente leitura
            }
        };
        tabelaProdutos = new JTable(modeloProdutos);
        JScrollPane scrollProdutos = new JScrollPane(tabelaProdutos);
        scrollProdutos.setBorder(BorderFactory.createTitledBorder("Produtos disponíveis"));

        // ----- Painel de adicionar ao carrinho -----
        JPanel painelAdicionar = new JPanel();
        painelAdicionar.add(new JLabel("Quantidade:"));
        spinnerQuantidade = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        painelAdicionar.add(spinnerQuantidade);

        JButton botaoAdicionar = new JButton("Adicionar ao carrinho");
        botaoAdicionar.addActionListener(e -> adicionarAoCarrinho());
        painelAdicionar.add(botaoAdicionar);

        JPanel painelTopo = new JPanel(new BorderLayout());
        painelTopo.add(scrollProdutos, BorderLayout.CENTER);
        painelTopo.add(painelAdicionar, BorderLayout.SOUTH);

        // ----- Tabela do carrinho -----
        modeloCarrinho = new DefaultTableModel(new String[]{"Produto", "Qtd", "Subtotal"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaCarrinho = new JTable(modeloCarrinho);
        JScrollPane scrollCarrinho = new JScrollPane(tabelaCarrinho);
        scrollCarrinho.setBorder(BorderFactory.createTitledBorder("Carrinho"));

        // ----- Painel de finalização -----
        labelTotal = new JLabel("Total: R$ 0,00");
        labelTotal.setFont(new Font("Arial", Font.BOLD, 14));

        comboFormaPagamento = new JComboBox<>();

        JButton botaoFinalizar = new JButton("Finalizar compra");
        botaoFinalizar.addActionListener(e -> finalizarCompra());

        JPanel painelFinalizar = new JPanel(new GridLayout(3, 1, 5, 5));
        painelFinalizar.add(labelTotal);
        painelFinalizar.add(comboFormaPagamento);
        painelFinalizar.add(botaoFinalizar);

        JPanel painelInferior = new JPanel(new BorderLayout());
        painelInferior.add(scrollCarrinho, BorderLayout.CENTER);
        painelInferior.add(painelFinalizar, BorderLayout.SOUTH);

        add(painelTopo, BorderLayout.CENTER);
        add(painelInferior, BorderLayout.SOUTH);
    }

    /**
     * Busca os produtos com estoque disponível no banco e preenche a tabela.
     */
    private void carregarProdutos() {
        try {
            produtosDisponiveis = produtoDAO.listarDisponiveis();
            modeloProdutos.setRowCount(0);

            for (Produto p : produtosDisponiveis) {
                modeloProdutos.addRow(new Object[]{
                        p.getId(), p.getNome(), String.format("R$ %.2f", p.getPreco()), p.getEstoque()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar produtos: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void carregarFormasPagamento() {
        try {
            List<FormaPagamento> formas = formaPagamentoDAO.listarTodas();
            for (FormaPagamento f : formas) {
                comboFormaPagamento.addItem(f);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar formas de pagamento: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Adiciona o produto selecionado na tabela ao carrinho,
     * validando se a quantidade não excede o estoque disponível.
     */
    private void adicionarAoCarrinho() {
        int linhaSelecionada = tabelaProdutos.getSelectedRow();

        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um produto na tabela.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Produto produto = produtosDisponiveis.get(linhaSelecionada);
        int quantidade = (int) spinnerQuantidade.getValue();

        if (quantidade > produto.getEstoque()) {
            JOptionPane.showMessageDialog(this,
                    "Estoque insuficiente. Disponível: " + produto.getEstoque(),
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        carrinho.add(new ItemCarrinho(produto, quantidade));
        atualizarTabelaCarrinho();
    }

    private void atualizarTabelaCarrinho() {
        modeloCarrinho.setRowCount(0);
        double total = 0;

        for (ItemCarrinho item : carrinho) {
            modeloCarrinho.addRow(new Object[]{
                    item.getProduto().getNome(), item.getQuantidade(),
                    String.format("R$ %.2f", item.getSubtotal())
            });
            total += item.getSubtotal();
        }

        labelTotal.setText(String.format("Total: R$ %.2f", total));
    }

    /**
     * Envia o carrinho para o PedidoDAO, que salva pedido, itens,
     * baixa de estoque e pagamento em uma única transação.
     */
    private void finalizarCompra() {
        if (carrinho.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seu carrinho está vazio.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        FormaPagamento formaSelecionada = (FormaPagamento) comboFormaPagamento.getSelectedItem();
        if (formaSelecionada == null) {
            JOptionPane.showMessageDialog(this, "Selecione uma forma de pagamento.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int idPedido = pedidoDAO.finalizarPedido(idCliente, carrinho, formaSelecionada.getId());

            JOptionPane.showMessageDialog(this,
                    "Pedido #" + idPedido + " realizado com sucesso!",
                    "Compra finalizada", JOptionPane.INFORMATION_MESSAGE);

            carrinho.clear();
            atualizarTabelaCarrinho();
            carregarProdutos(); // atualiza estoque na tela

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao finalizar compra: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Estoque insuficiente", JOptionPane.WARNING_MESSAGE);
        }
    }
}