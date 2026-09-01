package dao;

import model.FormaPagamento;
import util.ConexaoDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FormaPagamentoDAO {

    public List<FormaPagamento> listarTodas() throws SQLException {
        String sql = "SELECT * FROM FORMA_PAGAMENTO";
        List<FormaPagamento> formas = new ArrayList<>();

        try (Connection conn = ConexaoDB.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                formas.add(new FormaPagamento(rs.getInt("id_formaPagamento"), rs.getString("descricao")));
            }
        }
        return formas;
    }
}