package model;

//REPRESENTA UM ITEM TEMPORÁRIO NO CARRINHO DE COMPRAS, 
// ANTES DE O PEDIDO SER CONFIRMADO E SALVO NO BANCO

public class ItemCarrinho {
    
    private Produto produto;
    private int quantidade;

    public ItemCarrinho(Produto produto, int quantidade){
        this.produto = produto;
        this.quantidade = quantidade;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public double getSubtotal() {
        return produto.getPreco() * quantidade;
    }

    @Override
    public String toString() {
        return "ItemCarrinho [produto=" + produto + ", quantidade=" + quantidade + "]";
    }
}
