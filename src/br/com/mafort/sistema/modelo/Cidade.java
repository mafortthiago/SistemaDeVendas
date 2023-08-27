package modelo;

public class Cidade {
	private String nome;
	private int uf_id;
	
	public Cidade(String nome, int uf_id) {
		this.nome = nome;
		this.uf_id = uf_id;
	}

	public String getNome() {
		return this.nome;
	}

	public int getUf_id() {
		return this.uf_id;
	}
}
