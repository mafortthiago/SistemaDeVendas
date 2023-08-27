package modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UfRepository {
	private String instrucaoSQL;
	private Connection connection;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	

	public UfRepository() {
		connection = new ConnectionFactory().getConnection();
	}
	public Uf obtemUfPeloId(int id) {
		this.instrucaoSQL = "SELECT nome, sigla FROM uf WHERE id = ?";
		Uf uf = null;
		try {
			this.stmt = this.connection.prepareStatement(instrucaoSQL);
			this.stmt.setInt(1, id);
			this.rs = this.stmt.executeQuery();
			while(this.rs.next()) {
				
				uf = new Uf(id,this.rs.getString("sigla"),this.rs.getString("nome"));
			}
		}catch(SQLException e) {
			System.out.println(e.getMessage());
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
			return uf;
	}
	public List<String> obtemsSiglaUfs() {
	    List<String> siglasUfs = new ArrayList<>();
	    this.instrucaoSQL = "SELECT sigla FROM uf";
	    try {
	        this.stmt = this.connection.prepareStatement(this.instrucaoSQL);
	        this.rs = this.stmt.executeQuery();
	        while (this.rs.next()) {
	            String sigla = this.rs.getString("sigla");
	            siglasUfs.add(sigla);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
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
	        return siglasUfs;

	}

}
