package modelo;

public class Vendedor extends Pessoa {
	private double ComissaoPercentual;
	public Vendedor(String nome, String cpf,  Endereco endereco, double comissaoPercentual) {
		super(nome, cpf,  endereco);
		this.ComissaoPercentual = comissaoPercentual;
	}
	public Vendedor(int id, String nome, String cpf,  Endereco endereco, double comissaoPercentual) {
		super(id, nome, cpf,  endereco);
		this.ComissaoPercentual = comissaoPercentual;
	}
	public double getComissaoPercentual() {
		return ComissaoPercentual;
	}


}
