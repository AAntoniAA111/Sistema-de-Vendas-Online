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
    //DEFINIÇÃO PARA QUE O ESTOQUE NÃO SEJA NEGATIVO, CASO SEJA LANÇA UMA EXCEÇÃO
    //ENCAPSULAMENTO: GARANTE QUE NENHUM OUTRO LUGAR DO SISTEMA IRÁ CONSEGUIR CRIAR UM PRODUTO COM ESTOQUE NEGATIVO, O OBJETO SE PROTEJE SOZINHO
    public void setEstoque(int estoque) {
        if (estoque < 0){
            throw new IllegalArgumentException("O estoque não pode ser negativo.");
        }
        this.estoque = estoque;
    }


    public int getCategoria() {
        return categoria;
    }
    public void setCategoria(int categoria) {
        this.categoria = categoria;
    }

    //UM PRODUTO SÓ PODE SER VENDIDO SE TIVER PELO MENOS 1 UNIDADE EM ESTOQUE
    public boolean podeSerVendido(){
        return estoque > 0;
    }

    @Override
    public String toString() {
        return "Produto [id=" + id + ", nome=" + nome + ", preco=" + preco
                + ", estoque=" + estoque + ", categoria=" + categoria + "]";
    }
}
