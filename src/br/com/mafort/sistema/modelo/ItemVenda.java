package br.com.mafort.sistema.modelo;


public class ItemVenda {
    private int quantidade;
    private Produto produto;
    private double valorTotal;
    private int id_venda;

    public ItemVenda(int quantidade, Produto produto, double valorTotal,int id_venda) {
        this.quantidade = quantidade;
        this.produto = produto;
        this.valorTotal = valorTotal;
        this.id_venda = id_venda;
    }
    public ItemVenda(int quantidade, Produto produto, double valorTotal) {
        this.quantidade = quantidade;
        this.produto = produto;
        this.valorTotal = valorTotal;
        
    }

    public int getQuantidade() {
        return quantidade;
    }

    public int getIdVenda() {
        return this.id_venda;
    }

    public Produto getProduto() {
        return produto;
    }

    public double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(double valorTotal) {
        this.valorTotal = valorTotal;
    }

	public void setIdVenda(int id) {
		this.id_venda = id;
		
	}
	public void setQuantidade(int novaQuantidade) {
		this.quantidade = novaQuantidade;
		
	}
}
