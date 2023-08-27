package modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class VendaRepository {
	private Connection connection;
	private String instrucaoSQL;
	private ResultSet rs;
	private PreparedStatement stmt;
	
	
	public VendaRepository(){
		this.connection = new ConnectionFactory().getConnection();
	}
	public int insereVenda(Venda v) {
	    int idGerado = -1; // Valor padrão em caso de falha
	    this.instrucaoSQL = "INSERT INTO venda(data_venda, valor, vendedor_id) VALUES (?, ?, ?)";
	    try {
	        this.stmt = this.connection.prepareStatement(instrucaoSQL, Statement.RETURN_GENERATED_KEYS);
			System.out.println(v.getData_venda());
	        this.stmt.setDate(1, v.getData_venda());
	        this.stmt.setDouble(2, v.getValor());
	        this.stmt.setInt(3, v.getVendedorId());
	        this.stmt.executeUpdate();
	        try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
	            if (generatedKeys.next()) {
	                idGerado = generatedKeys.getInt(1);
	            }
	        }
	    } catch (SQLException e) {
	        System.out.println(e.getMessage() + "Falha ao inserir venda.");
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

	    return idGerado;
	}

}
