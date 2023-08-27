package modelo;

public class Uf {
	private String sigla;
	private String nome;
	private int id;

	public Uf(int id, String sigla, String nome) {
		this.id = id;
		this.sigla = sigla;
		this.nome = nome;

	}

	public int getId() {
		return this.id;
	}

	public String getSigla() {
		return this.sigla;
	}

	public String getNome() {
		return this.nome;
	}

}
