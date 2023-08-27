package br.com.mafort.sistema.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import br.com.mafort.sistema.view.*;
import br.com.mafort.sistema.utils.*;
import br.com.mafort.sistema.exceptions.*;
import br.com.mafort.sistema.modelo.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.stage.FileChooser;

public class RelatorioController implements Initializable {
	@FXML
	private ComboBox<String> cmbFornecedor;
	@FXML
	private ComboBox<String> cmbProduto;
	@FXML
	private ComboBox<String> cmbTipoRelatorio;
	@FXML
	private ComboBox<String> cmbVendedor;
	@FXML
	private CheckBox chbData;
	@FXML
	private CheckBox chbFornecedor;
	@FXML
	private CheckBox chbProduto;
	@FXML
	private CheckBox chbVendedor;
	@FXML
	private DatePicker dtpFim;
	@FXML
	private DatePicker dtpInicio;

	FornecedorRepository fr = new FornecedorRepository();
	VendedorRepository ver = new VendedorRepository();
	ProdutoDAO pd = new ProdutoDAO();

	private String instrucaoSQL;
	private String queryProdutos = "SELECT p.nome AS nome_produto, v.data_venda FROM produto p JOIN itemvenda i ON p.id = i.produto_id JOIN venda v ON i.venda_id = v.id JOIN fornecedor f ON p.fornecedor_id = f.id WHERE v.id = ?";
	private Connection connection;
	private PreparedStatement stmt;
	private PreparedStatement stmtNomeProdutos;
	private ResultSet rs;
	private ResultSet rsNomeProdutos;

	public RelatorioController() {
		connection = new ConnectionFactory().getConnection();
	}

	public File seletorDeArquivo() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Salvar arquivo de texto");
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Arquivos de Texto (*.txt)", "*.txt");
		fileChooser.getExtensionFilters().add(extFilter);
		File file = fileChooser.showSaveDialog(null);
		return file;

	}

	public List<String> queryProdutos() {
		List<String> nomeProdutos = new ArrayList<>();
		try {
			this.stmtNomeProdutos = this.connection.prepareStatement(queryProdutos);
			this.stmtNomeProdutos.setInt(1, rs.getInt("id")); // Certifique-se de que rs esteja definido antes disso
			System.out.println(this.queryProdutos);
			this.rsNomeProdutos = this.stmtNomeProdutos.executeQuery();

			while (this.rsNomeProdutos.next()) {
				nomeProdutos.add(rsNomeProdutos.getString("nome_produto"));
			}

		} catch (SQLException e) {
			CommonMethods.mostrarAviso("Erro", "Erro ao fazer seleção.");
		}
		return nomeProdutos;
	}
	public static String convertMillisToString(long millis) {
		Date date = new Date(millis);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return dateFormat.format(date);
	}
	public void gerarRelatorio() {
		try {
			String tipoRelatorio = cmbTipoRelatorio.getValue();

	
			boolean vendedorSelecionado = false;
			boolean produtoSelecionado = false;

			this.instrucaoSQL = "SELECT ";
			String titulo = null;

			if (tipoRelatorio == "Vendas") {
				titulo = "Lista de todas as vendas";
				this.instrucaoSQL += "f.nome,v.valor, v.id, ve.nome AS nome_vendedor, p.nome AS nome_produto, v.data_venda";
			} else if (tipoRelatorio == "Total de vendas") {
				titulo = "Valor total em vendas ";
				this.instrucaoSQL += " SUM(v.valor) AS soma";
			} else {
				throw new CamposInvalidosException("Selecione uma opção de relatório.");
			}
			this.instrucaoSQL += " FROM produto p JOIN itemvenda i ON p.id = i.produto_id JOIN venda v ON i.venda_id = v.id JOIN fornecedor f ON p.fornecedor_id = f.id JOIN vendedor ve ON v.vendedor_id = ve.id";

			if (this.chbData.isSelected()) {
				String dataInicioStr = dtpInicio.getEditor().getText();
				String dataFimStr = dtpFim.getEditor().getText();
				DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				LocalDate dataInicio = LocalDate.parse(dataInicioStr, dateFormatter);
				LocalDate dataFim = LocalDate.parse(dataFimStr, dateFormatter);
				long millisInicio = dataInicio.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli();
				long millisFim = dataFim.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli();
				if (dataInicio == null || dataFim == null) {
					throw new CamposInvalidosException("Selecione uma data.");
				} else if (dataFim.isBefore(dataInicio)) {
					throw new CamposInvalidosException("A data final deve ser posterior ou igual a data inicial.");
				} else {

					this.instrucaoSQL += " WHERE v.data_venda BETWEEN '" + millisInicio + "' AND '" + millisFim + "' ";
					titulo += " entre as datas " + dataInicio + " e " + dataFim;
					System.out.println(this.instrucaoSQL);

				}
			}

			if (this.chbFornecedor.isSelected()) {
				String nomeFornecedor = cmbFornecedor.getValue();
				int fornecedor_id = fr.obtemIdFornecedor(nomeFornecedor);
				this.queryProdutos += " AND f.id = "+ fornecedor_id;
				if (this.instrucaoSQL.contains("WHERE")) {
					this.instrucaoSQL += " AND f.id = " + fornecedor_id;
					titulo += " e pelo fornecedor " + nomeFornecedor;
				} else {
					this.instrucaoSQL += " WHERE f.id = " + fornecedor_id;
					titulo += " pelo fornecedor " + nomeFornecedor;

				}
			}
			if (this.chbVendedor.isSelected()) {
				String nomeVendedor = cmbVendedor.getValue();
				int vendedor_id = ver.obtemVendedorPeloNome(nomeVendedor).getId();
				if (this.instrucaoSQL.contains("WHERE")) {
					titulo += " e pelo vendedor " + nomeVendedor;
					this.instrucaoSQL += " AND ve.id = " + vendedor_id;
				} else {
					this.instrucaoSQL += " WHERE ve.id = " + vendedor_id;
					titulo += " pelo vendedor " + nomeVendedor;

				}
				System.out.println(this.instrucaoSQL);
				vendedorSelecionado = true;
			}
			if (this.chbProduto.isSelected()) {
				String nomeProduto = cmbProduto.getValue();
				int produto_id = pd.retornaProdutoPeloNome(nomeProduto).getId();
				this.queryProdutos += " AND p.id = "+ produto_id;
				if (this.instrucaoSQL.contains("WHERE")) {
					titulo += " e pelo produto " + nomeProduto;
					this.instrucaoSQL += " AND p.id = " + produto_id;
				} else {
					this.instrucaoSQL += " WHERE p.id = " + produto_id;
					titulo += " pelo produto " + nomeProduto;
				}
				produtoSelecionado = true;
			}

			this.stmt = this.connection.prepareStatement(this.instrucaoSQL);
			this.rs = this.stmt.executeQuery();
			File file = seletorDeArquivo();

			if (file != null) {

				FileWriter writer = new FileWriter(file);
				writer.write("-------------------------------------------------------------------------\n");
				writer.write(titulo);
				writer.write("\n-------------------------------------------------------------------------\n\n");
				if (tipoRelatorio == "Vendas") {
					int i = 1;
					while (rs.next()) {
						String campo1 = "VENDA " + i + ":";
						Double campo2 = rs.getDouble("valor");
						String campo4 = "Data: " + convertMillisToString (rs.getLong("data_venda"));
						String campo6 = "Vendedor: " + rs.getString("nome_vendedor");
						List<String> nomeProdutos = queryProdutos();
						writer.write(campo1 + "\n");
						writer.write("valor: R$ " + campo2 + "\n");
						if (!produtoSelecionado) {
							writer.write("Produtos:\n");
							for (String string : nomeProdutos) {
								writer.write(string + "\n");
							}

						}
						writer.write(campo4 + "\n");
						if (!vendedorSelecionado) {
							writer.write(campo6 + "\n");
						}
						writer.write("\n");
						i++;
					}
				} else {
					while (rs.next()) {
						writer.write("R$: " + rs.getDouble("soma"));
					}
				}
				writer.close();
				CommonMethods.mostrarAviso("Sucesso", "Relatório gerado com sucesso.");

			} else {
				CommonMethods.mostrarAviso("Erro", "Nenhum arquivo selecionado pelo usuário.");

			}

		} catch (CamposInvalidosException | SQLException | IOException e) {
			CommonMethods.mostrarAviso("Erro", e.getMessage());
		} catch (DateTimeParseException e) {
			CommonMethods.mostrarAviso("Erro", "Campo de data com valor inválido");
		}

	}

	public void carregaProdutos() {
		ProdutoDAO pd = new ProdutoDAO();
		List<String> produtos = pd.obtemProdutos();
		ObservableList<String> produtosList = FXCollections.observableArrayList(produtos);
		produtosList.sort(Comparator.naturalOrder());
		cmbProduto.setItems(produtosList);
	}

	public void carregaVendedor() {
		VendedorRepository vr = new VendedorRepository();
		List<String> vendedores = vr.obtemNomeVendedores();
		ObservableList<String> vendedoresList = FXCollections.observableArrayList(vendedores);
		vendedoresList.sort(Comparator.naturalOrder());
		cmbVendedor.setItems(vendedoresList);
	}

	public void carregaFornecedor() {
		FornecedorRepository fr = new FornecedorRepository();
		List<String> fornecedores = fr.obtemFornecedor();
		ObservableList<String> fornecedoresList = FXCollections.observableArrayList(fornecedores);
		fornecedoresList.sort(Comparator.naturalOrder());
		cmbFornecedor.setItems(fornecedoresList);
	}

	public void carregaOpcoes() {
		List<String> opcoesList = new ArrayList<>();
		opcoesList.add("Total de vendas");
		opcoesList.add("Vendas");
		ObservableList<String> opcoes = FXCollections.observableArrayList(opcoesList);
		cmbTipoRelatorio.setItems(opcoes);
	}

	public void ativaData() {
		if (chbData.isSelected()) {
			dtpInicio.setDisable(false);
			dtpFim.setDisable(false);
		} else {
			dtpInicio.setDisable(true);
			dtpFim.setDisable(true);
		}
	}

	public void ativaFornecedor() {
		if (chbFornecedor.isSelected()) {
			cmbFornecedor.setDisable(false);
		} else {
			cmbFornecedor.setDisable(true);
		}
	}

	public void ativaVendedor() {
		if (chbVendedor.isSelected()) {
			cmbVendedor.setDisable(false);
		} else {
			cmbVendedor.setDisable(true);
		}
	}

	public void ativaProduto() {
		if (chbProduto.isSelected()) {
			cmbProduto.setDisable(false);
		} else {
			cmbProduto.setDisable(true);
		}
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		carregaProdutos();
		carregaFornecedor();
		carregaVendedor();
		carregaOpcoes();
		CommonMethods.autoCompleteComboBox(cmbFornecedor, CommonMethods.AutoCompleteMode.STARTS_WITH);
		CommonMethods.autoCompleteComboBox(cmbVendedor, CommonMethods.AutoCompleteMode.STARTS_WITH);
		CommonMethods.autoCompleteComboBox(cmbProduto, CommonMethods.AutoCompleteMode.STARTS_WITH);

	}
}
