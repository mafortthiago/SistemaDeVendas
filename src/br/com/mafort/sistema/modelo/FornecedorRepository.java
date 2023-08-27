package br.com.mafort.sistema.modelo;


import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import  vendas.CommonMethods;

public class FornecedorRepository {
	private Connection connection;
	private String instrucaoSQL;
	private ResultSet rs;
	private PreparedStatement stmt;
	
	public FornecedorRepository(){
		this.connection = new ConnectionFactory().getConnection();
	}
	public void insereFornecedor(Fornecedor f) {
		this.instrucaoSQL = "INSERT INTO fornecedor(nome,CNPJ,numero,rua,bairro,cidade_id) VALUES (?,?,?,?,?,?)";
		try {
			this.stmt = this.connection.prepareStatement(instrucaoSQL);
			this.stmt.setString(1, f.getNome());
			this.stmt.setString(2,f.getCnpj());
			this.stmt.setString(3,f.getEndereco().getNumero());
			this.stmt.setString(4,f.getEndereco().getRua());
			this.stmt.setString(5,f.getEndereco().getBairro());
			this.stmt.setInt(6,f.getEndereco().getCidade_id());
			this.stmt.execute();
		}catch(SQLException e) {
			System.out.println("Erro ao inserir, "+e.getMessage());
		} finally {
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
	public Fornecedor obtemFornecedorPeloId(int id) {
		this.instrucaoSQL = "SELECT * FROM fornecedor WHERE id = ?";
		String nome;
		String cnpj;
		String numero;
		String rua;
		String bairro;
		int cidade_id;
		Endereco endereco = null;
		Fornecedor f = null;
		try {
			this.stmt = this.connection.prepareStatement(instrucaoSQL);
			this.stmt.setInt(1, id);
			this.rs = this.stmt.executeQuery();
			while(rs.next()) {
				nome = new String(rs.getBytes("nome"), StandardCharsets.UTF_8);
				cnpj = new String(rs.getBytes("CNPJ"), StandardCharsets.UTF_8);
				numero = new String(rs.getBytes("numero"));
				rua = new String(rs.getBytes("rua"), StandardCharsets.UTF_8);
				bairro = new String(rs.getBytes("bairro"), StandardCharsets.UTF_8);
				cidade_id = this.rs.getInt("cidade_id");
				endereco = new Endereco(rua, bairro, numero, cidade_id);
				f = new Fornecedor(nome, cnpj, endereco);
			}
		} catch (SQLException e) {
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
		return f;
	}
	public List<String> obtemFornecedor(){
		this.instrucaoSQL = "SELECT nome FROM fornecedor";
		List<String> nomeFornecedores = new ArrayList<>();;
		try {
			this.stmt = this.connection.prepareStatement(instrucaoSQL);
			this.rs = this.stmt.executeQuery();
			while(this.rs.next()) {
				String fornecedor = new String(rs.getBytes("nome"), StandardCharsets.UTF_8);
                nomeFornecedores.add(fornecedor);
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
		return nomeFornecedores;
	}
	public List<String> obtemCpnjFornecedores(){
		this.instrucaoSQL = "SELECT cnpj FROM fornecedor";
		List<String> cnpjFornecedores = new ArrayList<>();;
		try {
			this.stmt = this.connection.prepareStatement(instrucaoSQL);
			this.rs = this.stmt.executeQuery();
			while(this.rs.next()) {
				String cnpj = new String(rs.getBytes("cnpj"), StandardCharsets.UTF_8);
                cnpjFornecedores.add(cnpj);
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
		return cnpjFornecedores;
	}
	public void atualizaFornecedor(Fornecedor f, int id) {
		this.instrucaoSQL = "UPDATE fornecedor SET nome = ?, CNPJ = ?, numero = ?, rua = ?, bairro = ?, cidade_id = ? WHERE id = ?";
		try {
			this.stmt = this.connection.prepareStatement(this.instrucaoSQL);
			this.stmt.setString(1, f.getNome());
			this.stmt.setString(2, f.getCnpj());
			this.stmt.setString(3, f.getEndereco().getNumero());
			this.stmt.setString(4, f.getEndereco().getRua());
			this.stmt.setString(5, f.getEndereco().getBairro());
			this.stmt.setInt(6, f.getEndereco().getCidade_id());
			this.stmt.setInt(7, id);
			this.stmt.execute();
			
		} catch (SQLException e) {
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
	}
	public int obtemIdFornecedor(String nome) {
		int idfornecedor = 0;
		try {
		if (nome == null) {
	        throw new CamposInvalidosException("Preencha os campos por favor!");
	    }
		this.instrucaoSQL = "SELECT id FROM fornecedor WHERE nome = ?";
		
			this.stmt = this.connection.prepareStatement(instrucaoSQL);
			this.stmt.setString(1, nome);
			rs = stmt.executeQuery();
            if (rs.next()) {
                idfornecedor = rs.getInt("id");
            }
		} catch (SQLException e) {
			CommonMethods.mostrarAviso("Erro!",e.getMessage());
		} catch (CamposInvalidosException e) {
			CommonMethods.mostrarAviso("Erro!", e.getMessage());
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
		return idfornecedor;
		
	}
}
