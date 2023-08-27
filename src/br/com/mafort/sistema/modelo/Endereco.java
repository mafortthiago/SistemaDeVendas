package br.com.mafort.sistema.modelo;

public class Endereco {
	private String rua;
	private String bairro;
	private String numero;
	private int cidade_id;
	public Endereco(String rua,String bairro,String numero,int cidade_id) {
		this.rua = rua;
		this.bairro = bairro;
		this.numero = numero;
		this.cidade_id = cidade_id;
	}
	public String getRua() {
		return this.rua;
	}

	public String getBairro() {
		return this.bairro;
	}

	public String getNumero() {
		return this.numero;
	}

	public int getCidade_id() {
		return this.cidade_id;
	}

}
