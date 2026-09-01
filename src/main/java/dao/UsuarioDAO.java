package dao;

import model.Usuario;
import util.ConexaoDB;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

public class UsuarioDAO {

    public void cadastrar(String login, String senhaPura, String tipo) throws SQLException{
        String senhaCriptografada = BCrypt.hashpw(senhaPura, BCrypt.gensalt());

        String sql = "INSERT INTO USUARIO (login, senha_hash, tipo) VALUES (?, ?, ?)";

        try (Connection conn = ConexaoDB.getConexao();
            PreparedStatement stmt = conn.prepareStatement(sql)){
                stmt.setString(1, login);
                stmt.setString(2, senhaCriptografada);
                stmt.setString(3, tipo);

                stmt.executeUpdate();
                System.out.println("Usuário cadastrado com sucesso!");
            }
    }

    public Usuario login(String login, String senhaDigitada) throws SQLException{
        String sql = "SELECT * FROM USUARIO WHERE login = ?";

        try (Connection conn = ConexaoDB.getConexao();
            PreparedStatement stmt = conn.prepareStatement(sql)){
                stmt.setString(1, login);
                ResultSet rs = stmt.executeQuery();

                if(rs.next()){
                    String senhaHashSalva = rs.getString("senha_hash");

                    if (BCrypt.checkpw(senhaDigitada, senhaHashSalva)){
                        Usuario usuario = new Usuario();
                        usuario.setId(rs.getInt("id_usuario"));
                        usuario.setLogin(rs.getString("login"));
                        usuario.setTipo(rs.getString("tipo"));

                    return usuario;
                    }
                }
            }
            return null;
    }
}
