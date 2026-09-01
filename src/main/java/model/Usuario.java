package model;

public class Usuario {
    private int id;
    private String login;
    private String senhaHash;
    private String tipo; 

    public Usuario(){};

    public Usuario(String login, String tipo){
        this.login = login;
        this.tipo = tipo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public void setSenhaHash(String senhaHash) {
        this.senhaHash = senhaHash;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public boolean isAdmin(){
        return "admin".equals(tipo);
    }

    @Override
    public String toString() {
        return "Usuario [id=" + id + ", login=" + login + ", senhaHash=" + senhaHash + ", tipo=" + tipo + "]";
    }

}
