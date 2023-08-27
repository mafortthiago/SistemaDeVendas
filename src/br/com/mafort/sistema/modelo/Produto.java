package modelo;

public class Produto {
	private double valor;
	private String nome;
	private int fornecedor_id;
	private int estoque;
	private int id;
	
	public Produto(int id, String nome,int estoque, int fornecedor_id,double valor) {
		this.id = id;
		this.valor = valor;
		this.nome = nome;
		this.estoque = estoque;
		this.fornecedor_id = fornecedor_id;
	}
	public void setValor(double valor) {
		this.valor = valor;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public void setFornecedor_id(int fornecedor_id) {
		this.fornecedor_id = fornecedor_id;
	}
	public Produto( String nome,int estoque, int fornecedor_id,double valor) {
		this.valor = valor;
		this.nome = nome;
		this.estoque = estoque;
		this.fornecedor_id = fornecedor_id;
	}
	public int getFornecedor_id() {
		return this.fornecedor_id;
	}

	public int getEstoque() {
		return this.estoque;
	}
	public void setEstoque(int estoque) {
		this.estoque = estoque;
	}
	public int getId() {
		return this.id;
	}
	public String getNome() {
		return this.nome;
	}

	public double getValor() {
		return this.valor;
	}

	public String toString() {
		return this.nome;
	}
}
