package dao;

import model.ItemCarrinho;
import model.Pedido;
import util.ConexaoDB;

import java.sql.*;
import java.util.List;

public class PedidoDAO {

    //FINALIZA UMA COMPRA
    //CRIA O PEDIDO, OS ITENS, DÁ BAIXA NO ESTOQUE DE CADA PRODUTO E REGISTRA O PAGAMENTO
    //CASO ALGO DÊ ERRADO -> TODO <- O PEDIDO É DESFEITO PARA NÃO FICAR "PELA METADE" NO BANCO

    public int finalizarPedido(int idCliente, List<ItemCarrinho> itens, int idFormaPagamento) throws SQLException {
        Connection conn = null;

        try {
            conn = ConexaoDB.getConexao();
            conn.setAutoCommit(false); // inicia a transação manualmente

            Pedido pedido = new Pedido(idCliente, itens);

            // 1. Insere o pedido e recupera o ID gerado
            int idPedido = inserirPedido(conn, pedido);

            // 2. Para cada item do carrinho: insere na ITEM_PEDIDO e dá baixa no estoque
            ProdutoDAO produtoDAO = new ProdutoDAO();
            for (ItemCarrinho item : itens) {
                inserirItemPedido(conn, idPedido, item);
                produtoDAO.darBaixaEstoque(conn, item.getProduto().getId(), item.getQuantidade());
            }

            // 3. Registra o pagamento
            inserirPagamento(conn, idPedido, idFormaPagamento, pedido.getValorTotal());

            conn.commit(); // se chegou até aqui, tudo deu certo — confirma de vez
            return idPedido;

        } catch (Exception e) {
            if (conn != null) {
                conn.rollback(); // desfaz tudo o que foi feito nessa transação
            }
            throw new SQLException("Não foi possível finalizar o pedido: " + e.getMessage(), e);

        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    private int inserirPedido(Connection conn, Pedido pedido) throws SQLException {
        String sql = "INSERT INTO PEDIDOS (id_cliente, data_pedido, status_pedido, valor_total) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, pedido.getIdCliente());
            stmt.setDate(2, Date.valueOf(pedido.getDataPedido()));
            stmt.setString(3, pedido.getStatusPedido());
            stmt.setDouble(4, pedido.getValorTotal());
            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }
            throw new SQLException("Falha ao obter o ID do pedido gerado.");
        }
    }

    private void inserirItemPedido(Connection conn, int idPedido, ItemCarrinho item) throws SQLException {
        String sql = "INSERT INTO ITEM_PEDIDO (id_pedido, id_produto, quantidade, preco_unitario) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idPedido);
            stmt.setInt(2, item.getProduto().getId());
            stmt.setInt(3, item.getQuantidade());
            stmt.setDouble(4, item.getProduto().getPreco());
            stmt.executeUpdate();
        }
    }

    private void inserirPagamento(Connection conn, int idPedido, int idFormaPagamento, double valor) throws SQLException {
        String sql = "INSERT INTO PAGAMENTO (id_pedido, id_formaPagamento, valor, status_pagamento, data_pagamento) "
                   + "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idPedido);
            stmt.setInt(2, idFormaPagamento);
            stmt.setDouble(3, valor);
            stmt.setString(4, "aprovado"); // simulação simples de pagamento aprovado
            stmt.setDate(5, Date.valueOf(java.time.LocalDate.now()));
            stmt.executeUpdate();
        }
    }
    
}
