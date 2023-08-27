package br.com.mafort.sistema.modelo;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import vendas.CommonMethods;

public class ProdutoDAO {

	private String instrucaoSQL;
	private Connection connection;
	private PreparedStatement stmt;
	private ResultSet rs;

	public ProdutoDAO(){
		connection = new ConnectionFactory().getConnection();
	}

	public List<String> obtemProdutos() {
		this.instrucaoSQL = "SELECT nome FROM produto";
		List<String> nomeProdutos = new ArrayList<>();
		try {
			this.stmt = this.connection.prepareStatement(instrucaoSQL);
			this.rs = this.stmt.executeQuery();
			while (rs.next()) {
				String produto = new String(rs.getBytes("nome"), StandardCharsets.UTF_8);
				nomeProdutos.add(produto);
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
		return nomeProdutos;
	}

	public void insereProduto(Produto p) {
		this.instrucaoSQL = "INSERT INTO produto(nome,estoque,fornecedor_id,valor) VALUES (?,?,?,?)";
		try {
			this.stmt = this.connection.prepareStatement(instrucaoSQL);
			this.stmt.setString(1, p.getNome());
			this.stmt.setInt(2, p.getEstoque());
			this.stmt.setInt(3, p.getFornecedor_id());
			this.stmt.setDouble(4, p.getValor());
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

	public void atualizaProduto(Produto p) {
		try {
			connection.setAutoCommit(false);
			String sql = "UPDATE produto SET estoque = estoque + ?, valor = ? WHERE id = ?";
			PreparedStatement stmt = connection.prepareStatement(sql);
			stmt.setInt(1, p.getEstoque());
			stmt.setDouble(2, p.getValor());
			stmt.setInt(3, retornaProdutoPeloNome(p.getNome()).getId());
			stmt.executeUpdate();
			connection.commit();
		} catch (SQLException e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			System.out.println("Erro na atualização do estoque: " + e.getMessage());
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public Produto retornaProdutoPeloNome(String s) {
		this.instrucaoSQL = "SELECT * FROM produto WHERE nome = ?";
		int id;
		String nome;

		int estoque;
		int fornecedor_id;
		double valor;
		Produto p = null;
		try {
			if (s == null) {
				throw new CamposInvalidosException("Preencha os campos por favor!");
			}
			this.stmt = this.connection.prepareStatement(instrucaoSQL);
			this.stmt.setString(1, s);
			this.rs = this.stmt.executeQuery();
			while (rs.next()) {
				id = this.rs.getInt("id");
				nome = new String(rs.getBytes("nome"), StandardCharsets.UTF_8);
				estoque = this.rs.getInt("estoque");
				fornecedor_id = this.rs.getInt("fornecedor_id");
				valor = this.rs.getDouble("valor");
				p = new Produto(id, nome, estoque, fornecedor_id, valor);
			}
		} catch (SQLException | CamposInvalidosException e) {
			CommonMethods.mostrarAviso("Erro", e.getMessage());
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

		return p;

	}

	public void excluiProduto(String nome) {
		this.instrucaoSQL = "DELETE FROM produto where nome = ?";
		try {
			this.stmt = this.connection.prepareStatement(instrucaoSQL);
			this.stmt.setString(1, nome);
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

	public void editaProduto(Produto antigo, Produto novo) {
		this.instrucaoSQL = "UPDATE produto SET nome = ?, valor = ?, fornecedor_id = ? WHERE id = ?";
		try {
			this.stmt = this.connection.prepareStatement(instrucaoSQL);
			this.stmt.setString(1, novo.getNome());
			this.stmt.setDouble(2, novo.getValor());
			this.stmt.setInt(3, novo.getFornecedor_id());
			this.stmt.setInt(4, antigo.getId());
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
}