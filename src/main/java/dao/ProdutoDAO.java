package dao;

import model.Produto;
import util.ConexaoDB;

import java.util.ArrayList;
import java.util.List;

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

    public List<Produto> listarDisponiveis() throws SQLException{
        String sql = "SELECT * FROM PRODUTO WHERE estoque > 0";
        List<Produto> produtos = new ArrayList<>();

        try (Connection conn = ConexaoDB.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()){
                while(rs.next()){
                    Produto p = new Produto();
                    p.setId(rs.getInt("id_produto"));
                    p.setNome(rs.getString("nome_produto"));
                    p.setPreco(rs.getDouble("preco_produto"));
                    p.setEstoque(rs.getInt("estoque"));
                    p.setCategoria(rs.getInt("categoria"));
                    produtos.add(p);
                }
            }
            return produtos;
    }

    public Produto buscarPorId(int idProduto) throws SQLException{
        String sql = "SELECT * FROM PRODUTO WHERE id_produto = ?";

        try(Connection conn = ConexaoDB.getConexao();
            PreparedStatement stmt = conn.prepareStatement(sql)){
                stmt.setInt(1, idProduto);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()){
                    Produto p = new Produto();
                    p.setId(rs.getInt("id_produto"));
                    p.setNome(rs.getString("nome_produto"));
                    p.setPreco(rs.getDouble("preco_produto"));
                    p.setEstoque(rs.getInt("estoque"));
                    p.setCategoria(rs.getInt("categoria"));
                    return p;
                }
            }
            return null;
    }

    public void darBaixaEstoque(Connection conn, int idProduto, int quantidadeVendida) throws SQLException{
        String sqlSelect = "SELECT estoque FROM PRODUTO WHERE id_produto = ?";
        String sqlUpdate = "UPDATE PRODUTO SET estoque = ? WHERE id_produto = ?";

            int estoqueAtual;
            try(PreparedStatement stmtSelect = conn.prepareStatement(sqlSelect)){
                stmtSelect.setInt(1, idProduto);
                ResultSet rs = stmtSelect.executeQuery();

                if(!rs.next()){
                    throw new SQLException("Produto não encontrado.");
                }
                estoqueAtual = rs.getInt("estoque");
            }
            int novoEstoque = estoqueAtual - quantidadeVendida;
            if (novoEstoque < 0){
                throw new IllegalArgumentException("Estoque insuficiente. Disponível:" + estoqueAtual + ", solicitado: " + quantidadeVendida);
            }

            try(PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdate)){
                stmtUpdate.setInt(1, novoEstoque);
                stmtUpdate.setInt(2, idProduto);
                stmtUpdate.executeUpdate();
            }
    }
}
