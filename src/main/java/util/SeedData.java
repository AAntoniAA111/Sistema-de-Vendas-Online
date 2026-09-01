package util;

import dao.ProdutoDAO;
import dao.UsuarioDAO;
import model.Produto;

import java.sql.SQLException;

/**
 * Programa auxiliar para popular o banco com dados iniciais:
 * um usuário admin, um vendedor de teste e alguns produtos.
 *
 * Rodar apenas uma vez (executar a classe diretamente).
 * Depois de rodar, pode apagar ou ignorar este arquivo.
 */
public class SeedData {

    public static void main(String[] args) {
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        ProdutoDAO produtoDAO = new ProdutoDAO();

        try {
            // Usuário admin de teste
            usuarioDAO.cadastrar("admin", "admin123456789", "admin");
            System.out.println("Admin criado: login=admin | senha=admin123456789");

            // Usuário vendedor de teste
            usuarioDAO.cadastrar("vendedor1", "vendedor123456", "vendedor");
            System.out.println("Vendedor criado: login=vendedor1 | senha=vendedor123456");

            // Produtos de teste
            produtoDAO.cadastrar(new Produto("Camiseta Básica", 49.90, 50, 1));
            produtoDAO.cadastrar(new Produto("Tênis Esportivo", 199.90, 20, 2));
            produtoDAO.cadastrar(new Produto("Boné", 39.90, 30, 1));
            System.out.println("Produtos de teste cadastrados!");

        } catch (SQLException e) {
            System.out.println("Erro ao popular dados: " + e.getMessage());
        }
    }
}