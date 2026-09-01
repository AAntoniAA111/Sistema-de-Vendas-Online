package dao;

import model.Cliente;
import util.ConexaoDB;

import java.sql.*;

import org.mindrot.jbcrypt.BCrypt;

public class ClienteDAO {
        public void salvar(Cliente cliente, String senhaPura) throws SQLException{
            String senhaCriptografada = BCrypt.hashpw(senhaPura, BCrypt.gensalt());
            String sql = "INSERT INTO CLIENTES" 
            + "(nome_cliente, email, telefone, CPF, cep, logradouro, complemento, bairro, cidade, uf, senha_hash) " 
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (Connection conn = ConexaoDB.getConexao();
                PreparedStatement stmt = conn.prepareStatement(sql)){
                    stmt.setString(1, cliente.getNome());
                    stmt.setString(2, cliente.getEmail());
                    stmt.setString(3, cliente.getTelefone());
                    stmt.setString(4, cliente.getCpf());

                    stmt.setString(5, cliente.getEndereco().getCep());
                    stmt.setString(6, cliente.getEndereco().getLogadouro());
                    stmt.setString(7, cliente.getEndereco().getComplemento());
                    stmt.setString(8, cliente.getEndereco().getBairro());
                    stmt.setString(9, cliente.getEndereco().getLocalidade());
                    stmt.setString(10, cliente.getEndereco().getUf());
                    stmt.setString(11, senhaCriptografada);
                    stmt.executeUpdate();
                    System.out.println("Cliente cadastrado com sucesso!");
                }
        }

        public Cliente login(String email, String senhaDigitada) throws SQLException {
        String sql = "SELECT * FROM CLIENTES WHERE email = ?";

        try (Connection conn = ConexaoDB.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String senhaHashSalva = rs.getString("senha_hash");

                if (BCrypt.checkpw(senhaDigitada, senhaHashSalva)) {
                    Cliente cliente = new Cliente();
                    cliente.setId(rs.getInt("id_cliente"));
                    cliente.setNome(rs.getString("nome_cliente"));
                    cliente.setEmail(rs.getString("email"));
                    cliente.setTelefone(rs.getString("telefone"));
                    cliente.setCpf(rs.getString("CPF"));
                    return cliente;
                }
            }
        }
        return null;
    }
}

