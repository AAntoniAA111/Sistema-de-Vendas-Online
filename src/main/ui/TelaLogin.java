package ui;

import dao.ClienteDAO;
import dao.UsuarioDAO;
import model.Cliente;
import model.Usuario;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

/**
 * Tela de login gráfica. Permite que tanto clientes quanto
 * membros da equipe interna (admin/vendedor) façam login,
 * cada um usando sua própria tabela e regra de autenticação.
 */
public class TelaLogin extends JFrame {

    private JComboBox<String> comboTipoUsuario;
    private JTextField campoLogin;
    private JPasswordField campoSenha;

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public TelaLogin() {
        configurarJanela();
        criarComponentes();
    }

    private void configurarJanela() {
        setTitle("Login - Sistema de Vendas");
        setSize(350, 220);
        setLocationRelativeTo(null); // centraliza na tela
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
    }

    private void criarComponentes() {
        JPanel painel = new JPanel();
        painel.setLayout(new GridLayout(4, 2, 8, 8));
        painel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Tipo de usuário
        painel.add(new JLabel("Entrar como:"));
        comboTipoUsuario = new JComboBox<>(new String[]{"Cliente", "Equipe Interna"});
        painel.add(comboTipoUsuario);

        // Login (email para cliente, login para equipe)
        painel.add(new JLabel("Login / Email:"));
        campoLogin = new JTextField();
        painel.add(campoLogin);

        // Senha
        painel.add(new JLabel("Senha:"));
        campoSenha = new JPasswordField();
        painel.add(campoSenha);

        // Botão de entrar
        JButton botaoEntrar = new JButton("Entrar");
        botaoEntrar.addActionListener(e -> tentarLogin());
        painel.add(new JLabel()); // espaço vazio para alinhar
        painel.add(botaoEntrar);

        add(painel);
    }

    /**
     * Executa a tentativa de login de acordo com o tipo
     * selecionado (Cliente ou Equipe Interna).
     */
    private void tentarLogin() {
        String tipoSelecionado = (String) comboTipoUsuario.getSelectedItem();
        String login = campoLogin.getText().trim();
        String senha = new String(campoSenha.getPassword()).trim();

        if (login.isEmpty() || senha.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha login e senha.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            if ("Cliente".equals(tipoSelecionado)) {
                autenticarCliente(login, senha);
            } else {
                autenticarEquipeInterna(login, senha);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro no banco de dados: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void autenticarCliente(String email, String senha) throws SQLException {
        Cliente cliente = clienteDAO.login(email, senha);

        if (cliente == null) {
            JOptionPane.showMessageDialog(this, "Email ou senha incorretos.", "Erro", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Bem-vindo, " + cliente.getNome() + "!");
            // TODO: abrir a tela de compras do cliente aqui
            dispose(); // fecha a tela de login
        }
    }

    private void autenticarEquipeInterna(String login, String senha) throws SQLException {
        Usuario usuario = usuarioDAO.login(login, senha);

        if (usuario == null) {
            JOptionPane.showMessageDialog(this, "Login ou senha incorretos.", "Erro", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Bem-vindo, " + usuario.getLogin() + " (" + usuario.getTipo() + ")!");
            // TODO: abrir a tela de gerenciamento de produtos aqui
            dispose(); // fecha a tela de login
        }
    }

    public static void main(String[] args) {
        // SwingUtilities garante que a interface gráfica rode na thread correta
        SwingUtilities.invokeLater(() -> {
            TelaLogin tela = new TelaLogin();
            tela.setVisible(true);
        });
    }
}