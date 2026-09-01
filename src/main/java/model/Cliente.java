package model;

public class Cliente {
    private int id;
    private String nome;
    private String email; 
    private String telefone;
    private String cpf;
    private Endereco endereco;

    public Cliente(){};

    public Cliente(String nome, String email, String telefone, String cpf, Endereco endereco){
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.cpf = cpf;
        this.endereco = endereco;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    private String senhaHash;

    public String getSenhaHash() {
        return senhaHash;
    }
    public void setSenhaHash(String senhaHash) {
        this.senhaHash = senhaHash;
    }

    @Override
    public String toString() {
        return "Cliente [id=" + id + ", nome=" + nome + ", email=" + email + ", telefone=" + telefone + ", cpf=" + cpf
                + ", endereco=" + endereco + ", senhaHash=" + senhaHash + "]";
    }
}
