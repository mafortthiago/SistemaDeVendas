package modelo;

import java.sql.Date;

public class Venda {
	private double valor;
	private Date data_venda;
	private int vendedor_id;
	
	public Venda(int v_id,double valor, Date data_venda) {
		this.vendedor_id = v_id;
		this.valor = valor;
		this.data_venda = data_venda;
	}
	public int getVendedorId() {
		return this.vendedor_id;
	}
	public double getValor() {
		return this.valor;
	}
	public Date getData_venda() {
		return this.data_venda;
	} 
}
