import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import model.*;
import service.ViacepService;
import dao.ClienteDAO;
import dao.UsuarioDAO;
import dao.ProdutoDAO;

/**
 * Ponto de entrada do sistema em modo console.
 * Permite: cadastro de cliente, login de cliente e login da equipe
 * interna (admin/vendedor) com gerenciamento de produtos.
 */
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ClienteDAO clienteDAO = new ClienteDAO();
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        ProdutoDAO produtoDAO = new ProdutoDAO();
        ViacepService viacepService = new ViacepService();

        try {
            System.out.println("=== Bem-vindo à Loja ===");
            System.out.println("1 - Cadastrar novo cliente");
            System.out.println("2 - Login como cliente");
            System.out.println("3 - Login como equipe interna (admin/vendedor)");
            System.out.print("Escolha uma opção: ");
            String opcao = scanner.nextLine().trim();

            switch (opcao) {
                case "1" -> cadastrarCliente(scanner, clienteDAO, viacepService);
                case "2" -> loginCliente(scanner, clienteDAO);
                case "3" -> loginEquipeInterna(scanner, usuarioDAO, produtoDAO);
                default -> System.out.println("Opção inválida.");
            }

        } catch (IOException e) {
            System.out.println("Erro ao consultar o CEP: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Erro no banco de dados: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }

    /**
     * Fluxo de cadastro de um novo cliente, incluindo busca
     * automática de endereço via API do ViaCEP.
     */
    private static void cadastrarCliente(Scanner scanner, ClienteDAO clienteDAO, ViacepService viacepService)
            throws IOException, SQLException {

        System.out.print("Nome: ");
        String nome = scanner.nextLine().trim();
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Telefone: ");
        String telefone = scanner.nextLine().trim();
        System.out.print("CPF: ");
        String cpf = scanner.nextLine().trim();
        System.out.print("Senha: ");
        String senha = scanner.nextLine().trim();
        System.out.print("CEP (somente números): ");
        String cep = scanner.nextLine().trim().replaceAll("[^0-9]", "");

        if (cep.length() != 8) {
            System.out.println("CEP inválido. Digite 8 números.");
            return;
        }

        Endereco endereco = viacepService.getEndereco(cep);
        if (endereco == null || endereco.getCep() == null) {
            System.out.println("CEP não encontrado.");
            return;
        }

        Cliente cliente = new Cliente(nome, email, telefone, cpf, endereco);
        clienteDAO.salvar(cliente, senha);
    }

    /**
     * Fluxo de login de cliente. Em caso de sucesso, o próximo passo
     * seria abrir a tela/fluxo de compras (hoje feito pela interface gráfica).
     */
    private static void loginCliente(Scanner scanner, ClienteDAO clienteDAO) throws SQLException {
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Senha: ");
        String senha = scanner.nextLine().trim();

        Cliente cliente = clienteDAO.login(email, senha);

        if (cliente == null) {
            System.out.println("Email ou senha incorretos.");
        } else {
            System.out.println("Bem-vindo, " + cliente.getNome() + "!");
        }
    }

    /**
     * Fluxo de login da equipe interna (admin ou vendedor).
     * Em caso de sucesso, abre o menu de gerenciamento de produtos.
     */
    private static void loginEquipeInterna(Scanner scanner, UsuarioDAO usuarioDAO, ProdutoDAO produtoDAO)
            throws SQLException {

        System.out.print("Login: ");
        String login = scanner.nextLine().trim();
        System.out.print("Senha: ");
        String senha = scanner.nextLine().trim();

        Usuario usuario = usuarioDAO.login(login, senha);

        if (usuario == null) {
            System.out.println("Login ou senha incorretos.");
            return;
        }

        System.out.println("Login realizado! Bem-vindo, " + usuario.getLogin() + " (" + usuario.getTipo() + ")");
        menuEquipeInterna(scanner, usuario, produtoDAO);
    }

    /**
     * Menu de gerenciamento de produtos, disponível para admin e vendedor.
     */
    private static void menuEquipeInterna(Scanner scanner, Usuario usuario, ProdutoDAO produtoDAO) throws SQLException {
        boolean continuar = true;

        while (continuar) {
            System.out.println("\n=== Menu " + (usuario.isAdmin() ? "Admin" : "Colaborador") + " ===");
            System.out.println("1 - Cadastrar produto");
            System.out.println("2 - Listar produtos");
            System.out.println("3 - Alterar preço de produto");
            System.out.println("4 - Excluir produto");

            if (usuario.isAdmin()) {
                System.out.println("5 - Gerenciar usuários (em breve)");
            }

            System.out.println("0 - Sair");
            System.out.print("Escolha uma opção: ");
            String opcao = scanner.nextLine().trim();

            switch (opcao) {
                case "1" -> {
                    System.out.print("Nome do produto: ");
                    String nome = scanner.nextLine().trim();
                    System.out.print("Preço: ");
                    double preco = Double.parseDouble(scanner.nextLine().trim());
                    System.out.print("Estoque: ");
                    int estoque = Integer.parseInt(scanner.nextLine().trim());
                    System.out.print("Categoria (código numérico): ");
                    int categoria = Integer.parseInt(scanner.nextLine().trim());

                    try {
                        Produto produto = new Produto(nome, preco, estoque, categoria);
                        produtoDAO.cadastrar(produto);
                    } catch (IllegalArgumentException e) {
                        System.out.println("Erro: " + e.getMessage());
                    }
                }
                case "2" -> {
                    List<Produto> produtos = produtoDAO.listarTodos();
                    for (Produto p : produtos) {
                        System.out.printf("ID: %d | %s | R$ %.2f | Estoque: %d%n",
                                p.getId(), p.getNome(), p.getPreco(), p.getEstoque());
                    }
                }
                case "3" -> {
                    System.out.print("ID do produto: ");
                    int id = Integer.parseInt(scanner.nextLine().trim());
                    System.out.print("Novo preço: ");
                    double novoPreco = Double.parseDouble(scanner.nextLine().trim());

                    produtoDAO.alterarPreco(id, novoPreco);
                }
                case "4" -> {
                    System.out.print("ID do produto a excluir: ");
                    int id = Integer.parseInt(scanner.nextLine().trim());

                    produtoDAO.excluir(id);
                }
                case "5" -> {
                    if (usuario.isAdmin()) {
                        System.out.println("Funcionalidade de gerenciar usuários ainda não implementada.");
                    } else {
                        System.out.println("Você não tem permissão para essa ação.");
                    }
                }
                case "0" -> continuar = false;
                default -> System.out.println("Opção inválida.");
            }
        }
    }
}