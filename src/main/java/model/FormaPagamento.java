package model;

public class FormaPagamento {
    private int id;
    private String descricao;

    public FormaPagamento(int id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }

    public int getId() {
        return id;
    }
    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao; // usado para exibir no JComboBox
    }
}