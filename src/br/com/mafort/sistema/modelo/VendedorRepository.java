package br.com.mafort.sistema.modelo;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import vendas.CommonMethods;

public class VendedorRepository {
	private Connection connection;
	private String instrucaoSQL;
	private ResultSet rs;
	private PreparedStatement stmt;
	public VendedorRepository() {
		this.connection = new ConnectionFactory().getConnection();
	}
	public boolean insereVendedor(Vendedor v) {
		this.instrucaoSQL = "INSERT INTO vendedor(nome,cpf,numero,rua,bairro,cidade_id,comissao_percentual) VALUES (?,?,?,?,?,?,?)";
		try {
			this.stmt = this.connection.prepareStatement(instrucaoSQL);
			this.stmt.setString(1, v.getNome());
			this.stmt.setString(2, v.getCpf());
			this.stmt.setString(3, v.getEndereco().getNumero());
			this.stmt.setString(4, v.getEndereco().getRua());
			this.stmt.setString(5, v.getEndereco().getBairro());
			this.stmt.setInt(6, v.getEndereco().getCidade_id());
			this.stmt.setDouble(7, v.getComissaoPercentual());
			int rowsAffected = this.stmt.executeUpdate();
			if( rowsAffected > 0) {
				CommonMethods.mostrarAviso("Sucesso!", "Vendedor inserido com sucesso!");
			}
			return rowsAffected > 0;

		} catch (SQLException e) {
			CommonMethods.mostrarAviso("Erro!", e.getMessage());
			System.out.println(e.getMessage());
			return false;
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
	public boolean atualizaVendedor(Vendedor v) {
		this.instrucaoSQL = "UPDATE vendedor SET nome = ?, cpf = ?, numero = ?, rua = ?, bairro = ?, cidade_id = ?, comissao_percentual = ? WHERE id = ?";
		try {
			this.stmt = this.connection.prepareStatement(this.instrucaoSQL);
			this.stmt.setString(1, v.getNome());
			this.stmt.setString(2, v.getCpf());
			this.stmt.setString(3, v.getEndereco().getNumero());
			this.stmt.setString(4, v.getEndereco().getRua());
			this.stmt.setString(5, v.getEndereco().getBairro());
			this.stmt.setInt(6, v.getEndereco().getCidade_id());
			this.stmt.setDouble(7, v.getComissaoPercentual());
			this.stmt.setInt(8, v.getId());
			int rowsAffected = this.stmt.executeUpdate();
			if( rowsAffected > 0) {
				CommonMethods.mostrarAviso("Sucesso!", "Vendedor editado com sucesso!");
			}
			return rowsAffected > 0;
		} catch (SQLException e) {
			CommonMethods.mostrarAviso("Erro!", e.getMessage());
			return false;
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
	public Vendedor obtemVendedorPeloNome(String nomeCompleto) {
		Vendedor vendedor = null;
		try {
		if (nomeCompleto == null) {
	        throw new CamposInvalidosException("Preencha os campos por favor!");
	    }
			if (nomeCompleto.contains(" - ")) {
				String nome = nomeCompleto.split(" - ")[0];
				String cpf = nomeCompleto.split(" - ")[1];

				this.instrucaoSQL = "SELECT * FROM vendedor WHERE nome = ? AND cpf = ?";
				this.stmt = connection.prepareStatement(instrucaoSQL);
				this.stmt.setString(1, nome);
				this.stmt.setString(2, cpf);
			} else {
				String nome = nomeCompleto;

				this.instrucaoSQL = "SELECT * FROM vendedor WHERE nome = ?";
				this.stmt = connection.prepareStatement(instrucaoSQL);
				this.stmt.setString(1, nome);
			}

			this.rs = this.stmt.executeQuery();

			if (rs.next()) {
				Endereco endereco = new Endereco(rs.getString("rua"), rs.getString("bairro"), rs.getString("numero"),
						rs.getInt("cidade_id"));
				vendedor = new Vendedor(rs.getInt("id"), new String(rs.getBytes("nome"), StandardCharsets.UTF_8),
						rs.getString("cpf"), endereco, rs.getDouble("comissao_percentual"));
			}
		} catch (SQLException e) {
			CommonMethods.mostrarAviso("Erro!", e.getMessage());
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
		return vendedor;
	}

	public List<String> obtemNomeVendedores() {
		List<String> vendedoresComCpf = new ArrayList<>();
		this.instrucaoSQL = "SELECT nome, cpf FROM vendedor";
		try {
			this.stmt = connection.prepareStatement(instrucaoSQL);
			this.rs = this.stmt.executeQuery();
			Map<String, Integer> nomeFrequencia = new HashMap<>();
			while (rs.next()) {
				String nome = new String(rs.getBytes("nome"), StandardCharsets.UTF_8);
				String cpf = rs.getString("cpf");
				int frequencia = nomeFrequencia.getOrDefault(nome, 0) + 1;
				nomeFrequencia.put(nome, frequencia);
				if (frequencia > 1) {
					vendedoresComCpf.add(nome + " - " + cpf);
				} else {
					vendedoresComCpf.add(nome);
				}
			}
		} catch (SQLException e) {
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
		return vendedoresComCpf;
	}
	public List<Vendedor> obtemVendedores() {
		Vendedor v = null;
		List<Vendedor> vendedores = new ArrayList<>();
		this.instrucaoSQL = "SELECT * FROM vendedor";
		try {
			this.stmt = connection.prepareStatement(instrucaoSQL);
			this.rs = this.stmt.executeQuery();

			while (rs.next()) {

				Endereco endereco = new Endereco(rs.getString("rua"), rs.getString("bairro"), rs.getString("numero"),
						rs.getInt("cidade_id"));
				v = new Vendedor(new String(rs.getBytes("nome"), StandardCharsets.UTF_8), rs.getString("cpf"), endereco,
						rs.getDouble("comissao_percentual"));
				vendedores.add(v);
			}
		} catch (SQLException e) {
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
		return vendedores;
	}
}
