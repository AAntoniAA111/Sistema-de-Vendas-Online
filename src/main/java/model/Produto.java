package model;

public class Produto {
    private int id;
    private String nome;
    private double preco;
    private int estoque;
    private int categoria;

    public Produto() {} //CONSTRUTOR VAZIO QUE RECEBE OQUE VEM DO ResultSet

    public Produto(String nome, double preco, int estoque, int categoria) {
        this.nome = nome;
        this.preco = preco;
        this.estoque = estoque;
        this.categoria = categoria;
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
    public double getPreco() {
        return preco;
    }
    public void setPreco(double preco) {
        this.preco = preco;
    }
    public int getEstoque() {
        return estoque;
    }
    public void setEstoque(int estoque) {
        this.estoque = estoque;
    }
    public int getCategoria() {
        return categoria;
    }
    public void setCategoria(int categoria) {
        this.categoria = categoria;
    }

    @Override
    public String toString() {
        return "Produto [id=" + id + ", nome=" + nome + ", preco=" + preco
                + ", estoque=" + estoque + ", categoria=" + categoria + "]";
    }
}
