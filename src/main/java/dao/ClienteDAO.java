package dao;

import model.Cliente;
import util.ConexaoDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ClienteDAO {
        public void salvar(Cliente cliente) throws SQLException{
            String sql = "INSERT INTO CLIENTES" 
            + "(nome_cliente, email, telefone, CPF, cep, logradouro, complemento, bairro, cidade, uf) " 
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

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

                    stmt.executeUpdate();
                    System.out.println("Cliente cadastrado com sucesso!");
                }
        }
}
