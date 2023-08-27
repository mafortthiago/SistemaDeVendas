package br.com.mafort.sistema.modelo;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ItemVendaRepository {
	private Connection connection;
	private String instrucaoSQL;
	private ResultSet rs;
	private PreparedStatement stmt;
	
	
	public ItemVendaRepository(){
		this.connection = new ConnectionFactory().getConnection();
	}
	public void InsereItem(ItemVenda i) {
		this.instrucaoSQL = "INSERT INTO itemvenda(produto_id,venda_id,quantidade,valor) VALUES (?,?,?,?)";
		try {
			this.stmt = this.connection.prepareStatement(instrucaoSQL);
			this.stmt.setInt(1, i.getProduto().getId());
			this.stmt.setInt(2, i.getIdVenda());
			this.stmt.setInt(3, i.getQuantidade());
			this.stmt.setDouble(4, i.getValorTotal());
			this.stmt.execute();
		}catch(SQLException e){
			System.out.println(e.getMessage() + "Falha ao inserir venda! ");
			
		}finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
