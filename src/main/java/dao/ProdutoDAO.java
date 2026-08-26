package dao;

import model.Produto;
import util.ConexaoDB;

import java.sql.*;

public class ProdutoDAO {

    //CADASTRO DE PRODUTOS E SALVAR INFO NO BANCO DE DADOS
    public void cadastrar(Produto produto) throws SQLException {
        String sql = "INSERT INTO PRODUTO (nome_produto, preco_produto, estoque, categoria) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConexaoDB.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, produto.getNome());
            stmt.setDouble(2, produto.getPreco());
            stmt.setInt(3, produto.getEstoque());
            stmt.setInt(4, produto.getCategoria());

            stmt.executeUpdate();
            System.out.println("Produto cadastrado com sucesso!");
        }
    }

    //EXCLUIR PRODUTO NO SISTEMA 
    //PESQUISA FEITA PELO ID
    public void excluir(int idProduto) throws SQLException {
        String sql = "DELETE FROM PRODUTO WHERE id_produto = ?";

        try (Connection conn = ConexaoDB.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idProduto);
            int linhasAfetadas = stmt.executeUpdate();

            if (linhasAfetadas > 0) {
                System.out.println("Produto excluído com sucesso!");
            } else {
                System.out.println("Nenhum produto encontrado com esse ID.");
            }
        }
    }

    //ALTERAÇÃO DE PREÇO
    //PESQUISA FEITA PELO ID
    public void alterarPreco(int idProduto, double novoPreco) throws SQLException {
        String sql = "UPDATE PRODUTO SET preco_produto = ? WHERE id_produto = ?";

        try (Connection conn = ConexaoDB.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, novoPreco);
            stmt.setInt(2, idProduto);
            int linhasAfetadas = stmt.executeUpdate();

            if (linhasAfetadas > 0) {
                System.out.println("Preço atualizado com sucesso!");
            } else {
                System.out.println("Nenhum produto encontrado com esse ID.");
            }
        }
    }

    //LISTAGEM DE TODOS OS PRODUTOS CADASTRADOS
    //ResultSet É O OBJETO QUE GUARDA O RESULTADO DE UMA CONSULTA SQL FEITA NO BANCO
        //"TABELA RESPOSTA" QUANDO EXECUTA UM SELECT
    public void listarTodos() throws SQLException {
        String sql = "SELECT * FROM PRODUTO";

        try (Connection conn = ConexaoDB.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                System.out.printf("ID: %d | %s | R$ %.2f | Estoque: %d%n",
                        rs.getInt("id_produto"),
                        rs.getString("nome_produto"),
                        rs.getDouble("preco_produto"),
                        rs.getInt("estoque"));
            }
        }
    }
}
