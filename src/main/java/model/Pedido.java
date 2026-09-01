package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import model.ItemCarrinho;

//PEDIDO FINALIZADO DE UM CLIENTE

public class Pedido {
    private int id;
    private int idCliente;
    private LocalDate dataPedido;
    private String statusPedido;
    private double valorTotal;
    private List<ItemCarrinho> itens = new ArrayList<>();

    public Pedido(){}

    public Pedido(int idCliente, List<ItemCarrinho> itens){
        this.idCliente = idCliente;
        this.itens = itens;
        this.dataPedido = LocalDate.now();
        this.statusPedido = "pedido confirmado";
        this.valorTotal = itens.stream().mapToDouble(ItemCarrinho::getSubtotal).sum();
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public LocalDate getDataPedido() {
        return dataPedido;
    }

    public String getStatusPedido() {
        return statusPedido;
    }

    public double getValorTotal() {
        return valorTotal;
    }

    public List<ItemCarrinho> getItens() {
        return itens;
    }
}
