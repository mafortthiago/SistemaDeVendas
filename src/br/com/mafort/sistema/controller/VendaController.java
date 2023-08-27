package br.com.mafort.sistema.controller;

import java.net.URL;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;

public class VendaController implements Initializable {
	@FXML
	private TextField txfQuantidade;
	@FXML
	private TextField txfValorTotal;
	@FXML
	private ComboBox<String> cmbprodutos;
	@FXML
	private ComboBox<String> cmbvendedores;
	@FXML
	private TableView<ItemVenda> tabelaVendas;
	@FXML
	private TableColumn<ItemVenda, Void> colunaExcluir;
	@FXML
	private TableColumn<ItemVenda, Integer> colunaQuantidade;
	@FXML
	private TableColumn<ItemVenda, String> colunaProduto;
	@FXML
	private TableColumn<ItemVenda, Double> colunaValorTotal;

	ProdutoDAO pr = new ProdutoDAO();
	private ObservableList<ItemVenda> listaVendas = FXCollections.observableArrayList();
	VendaRepository vr = new VendaRepository();
	VendedorRepository vndr = new VendedorRepository();
	ItemVendaRepository isr = new ItemVendaRepository();

	Venda v;

	public Date diaDaVenda() {
		Calendar calendar = Calendar.getInstance();
		java.util.Date dataUtil = calendar.getTime();
		Date dataSql = new Date(dataUtil.getTime());
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String formattedDate = dateFormat.format(dataSql);

		return dataSql;
	}

	public double calculaValorItemVenda() {
		Produto p = pr.retornaProdutoPeloNome(cmbprodutos.getValue());
		int quantidade = Integer.parseInt(txfQuantidade.getText());
		double valorTotal = quantidade * p.getValor();
		return limitarDuasCasasDecimais(valorTotal);
	}

	public double limitarDuasCasasDecimais(double valor) {
		return Math.floor(valor * 100) / 100;
	}

	public void adicionaNaListaDeVenda() {
		String nome = cmbprodutos.getValue();
		try {
			int quantidade = Integer.parseInt(txfQuantidade.getText());
			Produto produtoExistente = null;

			for (ItemVenda item : listaVendas) {
				if (item.getProduto().getNome().equals(nome)) {
					produtoExistente = item.getProduto();
					break;
				}
			}

			if (!pr.obtemProdutos().contains(nome)) {
				throw new CamposInvalidosException("O produto não existe!");
			} else if (produtoExistente == null && quantidade > pr.retornaProdutoPeloNome(nome).getEstoque()) {
				throw new CamposInvalidosException("O produto não tem estoque suficiente, que no momento é de: "
						+ pr.retornaProdutoPeloNome(nome).getEstoque());
			}

			if (produtoExistente != null) {
				for (ItemVenda item : listaVendas) {
					if (item.getProduto().equals(produtoExistente)) {
						int novaQuantidade = item.getQuantidade() + quantidade;
						double novoValor = produtoExistente.getValor() * novaQuantidade;
						if (novaQuantidade <= produtoExistente.getEstoque()) {
							item.setQuantidade(novaQuantidade);
							item.setValorTotal(novoValor);

						} else {
							throw new CamposInvalidosException("Quantidade excede o estoque disponível!");
						}
						break;
					}
				}
			} else {
				ItemVenda item = new ItemVenda(quantidade, pr.retornaProdutoPeloNome(nome), calculaValorItemVenda());
				listaVendas.add(item);
			}
			refreshTable();
			tabelaVendas.refresh();
		} catch (NumberFormatException e) {
			CommonMethods.mostrarAviso("Erro", "Valor inválido para quantidade!");
		} catch (CamposInvalidosException e) {
			CommonMethods.mostrarAviso("Erro", e.getMessage());
		}
	}

	private void refreshTable() {
		tabelaVendas.setItems(listaVendas);
		txfValorTotal.setText("Total: R$ " + somatoriaValores());
	}

	public void carregaVendedor() {
		List<String> vendedores = vndr.obtemNomeVendedores();
		ObservableList<String> vendedoresList = FXCollections.observableArrayList(vendedores);
		cmbvendedores.setItems(vendedoresList);
	}

	public void carregaProduto() {
		List<String> produtos = pr.obtemProdutos();
		ObservableList<String> produtosList = FXCollections.observableArrayList(produtos);
		cmbprodutos.setItems(produtosList);
	}

	private void limparCampos() {
		cmbvendedores.setValue("");
		cmbprodutos.setValue("");
		txfQuantidade.clear();
		listaVendas.clear();
		refreshTable();
	}

	public void concluiVenda() {
		try {
			if (!listaVendas.isEmpty()) {
				String vendedor = cmbvendedores.getValue();
				if (vendedor == null || !vndr.obtemNomeVendedores().contains(vendedor)) {
					throw new CamposInvalidosException("Selecione ou digite um fornecedor válido!");
				}
				v = new Venda(vndr.obtemVendedorPeloNome(vendedor).getId(), somatoriaValores(), diaDaVenda());

				int id = vr.insereVenda(v);
				for (ItemVenda itemVenda : listaVendas) {
					itemVenda.setIdVenda(id);
					isr.InsereItem(itemVenda);
					Produto produto = itemVenda.getProduto();
					int quantidadeVendida = itemVenda.getQuantidade();
					produto.setEstoque(-quantidadeVendida);
					pr.atualizaProduto(produto);
				}
				CommonMethods.mostrarAviso("Sucesso", "Venda concluida.");
				limparCampos();
			} else {
				CommonMethods.mostrarAviso("Atenção!", "Não exitem itens par se concretizar uma venda!");
			}
		} catch (CamposInvalidosException e) {
			CommonMethods.mostrarAviso("Erro", e.getMessage());
		}
	}

	private double somatoriaValores() {
		Double valorTotal = 0.0;
		for (ItemVenda itemVenda : listaVendas) {
			valorTotal += itemVenda.getValorTotal();
		}
		return valorTotal;
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		listaVendas = FXCollections.observableArrayList();
		colunaQuantidade.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
		colunaProduto.setCellValueFactory(new PropertyValueFactory<>("Produto"));
		colunaValorTotal.setCellValueFactory(new PropertyValueFactory<>("valorTotal"));
		colunaExcluir.setCellFactory(param -> new TableCell() {
			private final Button excluirButton = new Button("X");

			{

				excluirButton.setTextFill(Color.WHITE);
				excluirButton.setStyle("-fx-background-color: #D32F2F; " + "-fx-padding: 3px 8px; "
						+ "-fx-font-size: 10px; " + "-fx-font-weight: bold; " + "-fx-border-radius: 5px; "
						+ "-fx-background-radius: 5px; " + "-fx-cursor: hand; ");

				excluirButton.setOnAction(event -> {
					ItemVenda item = (ItemVenda) getTableRow().getItem();
					if (item != null) {
						int index = getTableRow().getIndex();
						listaVendas.remove(index);
						refreshTable();
					}
				});
			}

			protected void updateItem(Void item, boolean empty) {
				super.updateItem(item, empty);
				if (empty) {
					setGraphic(null);
				} else {
					setAlignment(Pos.CENTER);
					setGraphic(excluirButton);
				}
			}
		});
		carregaVendedor();
		carregaProduto();
		CommonMethods.autoCompleteComboBox(cmbprodutos, AutoCompleteMode.STARTS_WITH);
		CommonMethods.autoCompleteComboBox(cmbvendedores, AutoCompleteMode.STARTS_WITH);
	}
}