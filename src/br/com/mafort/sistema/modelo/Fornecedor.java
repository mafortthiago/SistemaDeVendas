package br.com.mafort.sistema.modelo;


public class Fornecedor {
	private int id;
	private String nome;
	private String cnpj;
	private Endereco endereco;
	
	public Fornecedor(String nome, String cnpj,Endereco endereco) {
		this.nome = nome;
		this.cnpj = cnpj;
		this.endereco = endereco;
	}

	public String getNome() {
		return this.nome;
	}

	public String getCnpj() {
		return this.cnpj;
	}

	public Endereco getEndereco() {
		return this.endereco;
	}

	

}
