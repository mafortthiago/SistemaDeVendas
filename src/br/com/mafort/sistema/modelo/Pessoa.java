package modelo;

public class Pessoa {
	private String nome;
	private String cpf;
	private int id;
	private Endereco endereco;

	public Pessoa(String nome, String cpf, Endereco endereco) {
		this.nome = nome;
		this.cpf = cpf;
		this.endereco = endereco;
	}
	
	public Pessoa(int id, String nome, String cpf, Endereco endereco) {
		this.nome = nome;
		this.cpf = cpf;
		this.endereco = endereco;
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public String getCpf() {
		return cpf;
	}

	public int getId() {
		return id;
	}

	public Endereco getEndereco() {
		return endereco;
	}

}
