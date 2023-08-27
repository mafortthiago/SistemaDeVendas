package vendas;

import java.net.URL;
import java.util.ArrayList;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

import javafx.scene.paint.Color;

public class ProdutoController implements Initializable {
	@FXML
	private Label lblLimpaCampos;
	@FXML
	private Label lblLimpaCamposEditar;
	@FXML
	private TextField txfNome;
	@FXML
	private TextField txfValorProdutoNovo;
	@FXML
	private TextField txfValorProdutoExistente;
	@FXML
	private TextField txfQuantidade;
	@FXML
	private TextField txfQuantidade2;
	@FXML
	private ComboBox<String> cmbProdutos;
	@FXML
	private ComboBox<String> cmbProdutosParaExcluir;
	@FXML
	private ComboBox<String> cmbFornecedor;
	@FXML
	private Button btnInserir;
	@FXML
	private Button btnInserirProdutoExistente;
	@FXML
	private Button btnEditarExcluirProduto;
	@FXML
	private Pane paneExcluirEditarProduto;
	@FXML
	private Pane paneProduto;
	@FXML
	private Button btnIrParaInserirProduto;
	@FXML
	private Button btnExcluirProduto;
	@FXML
	private Button btnEditarProduto;
	@FXML
	private TextField tfxNomeParaEditar;
	@FXML
	private TextField tfxValorParaEditar;
	@FXML
	private ComboBox<String> cmbProdutosParaEditar;
	@FXML
	private ComboBox<String> cmbFornecedorParaEditar;
	private Produto produtoSelecionado;
	ProdutoDAO pd = new ProdutoDAO();

	public void excluirProduto() {

		if (produtoExisteNoBancoDeDados(cmbProdutosParaExcluir.getValue())) {
			pd.excluiProduto(cmbProdutosParaExcluir.getValue());
			CommonMethods.mostrarAviso("Informação", "Produto Excluido com sucesso!");
			carregaProdutos();
			carregaProdutosParaEditar();
			carregaProdutosParaExcluir();
		} else {
			CommonMethods.mostrarAviso("Informação", "Escolha um produto existente.");
		}

	}

	public void editarProduto() {
		String nome = tfxNomeParaEditar.getText();
		String valorText = tfxValorParaEditar.getText();
		if (nome != null && cmbFornecedorParaEditar.getValue() != null && !valorText.isEmpty()) {
			try {
				double valor = Double.parseDouble(valorText);
				int numLetras = 0;
				for (char c : nome.toCharArray()) {
					if (Character.isLetter(c)) {
						numLetras++;
					}
				}

				if (numLetras < 3) {
					throw new CamposInvalidosException("O nome do produto deve conter pelo menos três letras.");
				}

				if (valor < 0) {
					throw new ValorNegativoException();
				}

				FornecedorRepository fr = new FornecedorRepository();
				Produto produtoPreenchido = new Produto(nome, 0,
						fr.obtemIdFornecedor(cmbFornecedorParaEditar.getValue()), valor);
				Produto original = pd.retornaProdutoPeloNome(cmbProdutosParaEditar.getValue());

				if (!nome.equals(original.getNome()) && produtoExisteNoBancoDeDados(nome)) {
					throw new ProdutoExistenteException("Já existe um produto com esse nome!");
				}
				if (!fornecedorExisteNoBancoDeDados(cmbFornecedorParaEditar)) {
					throw new FornecedorInexistenteException("Fornecedor não existente!");
				}

				pd.editaProduto(original, produtoPreenchido);
				CommonMethods.mostrarAviso("Informação", "Produto editado com sucesso!");
				limpaCamposEditarProdutos();
			} catch (NumberFormatException e) {
				CommonMethods.mostrarAviso("Erro",
						"Valor inválido. Certifique-se de que o campo 'Valor' contenha um número válido.");
			} catch (CamposInvalidosException | ValorNegativoException | ProdutoExistenteException
					| FornecedorInexistenteException e) {
				CommonMethods.mostrarAviso("Erro", e.getMessage());
			}
		} else {
			CommonMethods.mostrarAviso("Erro", "Preencha todos os campos para edição.");
		}
	}

	public void limpaCamposEditarProdutos() {
		tfxNomeParaEditar.setText("");
		cmbProdutosParaEditar.getSelectionModel().clearSelection();
		cmbProdutosParaEditar.getEditor().clear();
		cmbProdutosParaExcluir.getSelectionModel().clearSelection();
		cmbProdutosParaExcluir.getEditor().clear();
		cmbFornecedorParaEditar.getSelectionModel().clearSelection();
		cmbFornecedorParaEditar.getEditor().clear();
		limparCampoTexto(tfxValorParaEditar);
	}

	public void preencheDadosProdutoSelecionado() {
		String produtoSelecionadoNome = cmbProdutosParaEditar.getValue();
		if (produtoSelecionadoNome != null && !produtoSelecionadoNome.isEmpty()) {
			produtoSelecionado = pd.retornaProdutoPeloNome(produtoSelecionadoNome);
			if (produtoSelecionado != null) {
				FornecedorRepository fr = new FornecedorRepository();
				tfxNomeParaEditar.setText(produtoSelecionado.getNome());
				tfxValorParaEditar.setText(Double.toString(produtoSelecionado.getValor()));
				cmbFornecedorParaEditar
						.setValue(fr.obtemFornecedorPeloId(produtoSelecionado.getFornecedor_id()).getNome());
				tfxNomeParaEditar.setDisable(false);
				tfxValorParaEditar.setDisable(false);
				cmbFornecedorParaEditar.setDisable(false);
				btnEditarProduto.setDisable(false);
				habilitaBtnEditar();
			}
		}
	}

	public boolean produtoExisteNoBancoDeDados(String produtoSelecionado) {

		if (produtoSelecionado == null || produtoSelecionado.isEmpty()) {
			return false;
		}
		List<String> produtosNoBanco = pd.obtemProdutos();

		return produtosNoBanco.contains(produtoSelecionado);
	}

	public void habilitaBtnExcluir() {
		if (cmbProdutosParaExcluir.getValue() != null) {
			btnExcluirProduto.setDisable(false);
		}

	}

	private void limparCampoTexto(TextField campoTexto) {
		campoTexto.setText("");
	}

	public void ativaEditar() {
		paneExcluirEditarProduto.toFront();
	}

	public void ativaInserir() {
		paneProduto.toFront();
	}

	public void limpaCampos() {
		limparCamposProdutoExistente();
		limparCamposProdutoNovo();
		btnInserir.setDisable(true);
		btnInserirProdutoExistente.setDisable(true);
	}

	public void limparCamposProdutoNovo() {
		txfNome.setText("");
		cmbFornecedor.getSelectionModel().clearSelection();
		limparCampoTexto(txfValorProdutoNovo);
		limparCampoTexto(txfQuantidade);
		cmbFornecedor.getEditor().clear();
	}

	public void limparCamposProdutoExistente() {
		cmbProdutos.getSelectionModel().clearSelection();
		limparCampoTexto(txfValorProdutoExistente);
		limparCampoTexto(txfQuantidade2);
		cmbProdutos.getEditor().clear();
	}

	public void insereProduto() {
		FornecedorRepository fr = new FornecedorRepository();
		ProdutoDAO pd = new ProdutoDAO();

		try {
			String nomeProduto = txfNome.getText();
			String quantidadeStr = txfQuantidade.getText();
			String valorStr = txfValorProdutoNovo.getText();

			int numLetras = 0;
			for (char c : nomeProduto.toCharArray()) {
				if (Character.isLetter(c)) {
					numLetras++;
				}
			}
			if (produtoExisteNoBancoDeDados(nomeProduto)) {
				throw new ProdutoExistenteException("Já existe um produto com esse nome!");
			}
			if (numLetras < 3) {
				throw new CamposInvalidosException("O nome do produto deve conter pelo menos três letras.");
			}

			int quantidade = Integer.parseInt(quantidadeStr);
			double valor = Double.parseDouble(valorStr);

			if (quantidade <= 0 || valor <= 0) {
				throw new CamposInvalidosException("A quantidade e o valor do produto devem ser números positivos.");
			}

			String fornecedorSelecionado = cmbFornecedor.getValue();
			if (!fornecedorExisteNoBancoDeDados(cmbFornecedor)) {
				throw new FornecedorInexistenteException("Fornecedor não existente!");
			}

			if (produtoExisteNoBancoDeDados(nomeProduto)) {

			}

			Produto p = new Produto(nomeProduto, quantidade, fr.obtemIdFornecedor(fornecedorSelecionado), valor);
			pd.insereProduto(p);

			limparCamposProdutoNovo();
			CommonMethods.mostrarAviso("Informação", "Produto inserido com sucesso!");
			carregaProdutos();
			carregaProdutosParaEditar();
			carregaProdutosParaExcluir();
			btnInserir.setDisable(true);

		} catch (NumberFormatException e) {
			CommonMethods.mostrarAviso("Erro", "Os campos de quantidade e valor devem conter apenas números válidos.");
		} catch (CamposInvalidosException | FornecedorInexistenteException | ProdutoExistenteException e) {
			CommonMethods.mostrarAviso("Erro", e.getMessage());
		}
	}

	public void insereProdutoExistente() {
		FornecedorRepository fr = new FornecedorRepository();
		ProdutoDAO pr = new ProdutoDAO();

		try {
			String nomeProduto = cmbProdutos.getValue();
			String quantidadeStr = txfQuantidade2.getText();
			String valorStr = txfValorProdutoExistente.getText();

			if (nomeProduto.isEmpty() || quantidadeStr.isEmpty() || valorStr.isEmpty()) {
				throw new CamposInvalidosException("Todos os campos devem ser preenchidos.");
			}
			if (!quantidadeStr.matches("\\d+")) {
				throw new CamposInvalidosException("A quantidade deve conter apenas números inteiros.");
			}
			if (!valorStr.matches("\\d+(\\.\\d{0,2})?")) {
				throw new CamposInvalidosException(
						"O valor deve conter apenas números e no máximo dois dígitos após o ponto decimal.");
			}
			int quantidade = Integer.parseInt(quantidadeStr);
			double valor = Double.parseDouble(valorStr);
			if (quantidade <= 0) {
				throw new CamposInvalidosException("A quantidade deve ser um número positivo.");
			}
			if (valor <= 0) {
				throw new CamposInvalidosException("O valor deve ser um número positivo.");
			}
			List<String> listaNomeProdutos = pr.obtemProdutos();
			if (!listaNomeProdutos.contains(nomeProduto)) {
				throw new ProdutoInexistenteException();
			}
			int fornecedorId = fr.obtemIdFornecedor(
					fr.obtemFornecedorPeloId(pr.retornaProdutoPeloNome(nomeProduto).getFornecedor_id()).getNome());
			Produto p = new Produto(nomeProduto, quantidade, fornecedorId, valor);
			pr.atualizaProduto(p);
			limparCamposProdutoExistente();
			carregaProdutos();
			carregaProdutosParaEditar();
			carregaProdutosParaExcluir();
			CommonMethods.mostrarAviso("Informação", "Produto adicionado com sucesso!");

		} catch (NumberFormatException e) {
			CommonMethods.mostrarAviso("Erro", "Digite valores numéricos válidos para quantidade e valor.");
		} catch (CamposInvalidosException e) {
			CommonMethods.mostrarAviso("Erro", e.getMessage());
		} catch (ProdutoInexistenteException e) {
			CommonMethods.mostrarAviso("Erro", "O produto não existe no banco de dados");
		}
	}

	public boolean verificaCamposProdutoNovo() {
		boolean habilita;
		if (txfNome.getText().equals("") || cmbFornecedor.getValue() == null || txfQuantidade.getText().equals("")
				|| txfValorProdutoNovo.getText().equals("")) {
			habilita = true;
		} else {
			habilita = false;
		}
		return habilita;
	}

	public void habilitabtnInserirProdutoNovo() {
		btnInserir.setDisable(verificaCamposProdutoNovo());
	}

	public void habilitabtnInserirProdutoExistente() {
		btnInserirProdutoExistente.setDisable(verificaCamposProdutoExistente());
	}

	public boolean verificaCamposProdutoExistente() {
		boolean habilita;
		if (cmbProdutos.getValue() == null || txfQuantidade2.getText().equals("")
				|| txfValorProdutoExistente.getText().equals("")) {
			habilita = true;
		} else {
			habilita = false;
		}
		return habilita;
	}

	public void carregaFornecedores() {
		FornecedorRepository fr = new FornecedorRepository();
		List<String> fornecedores = fr.obtemFornecedor();
		ObservableList<String> fornecedoresList = FXCollections.observableArrayList(fornecedores);
		cmbFornecedor.setItems(fornecedoresList);
	}

	public void carregaFornecedoresParaEditar() {
		FornecedorRepository fr = new FornecedorRepository();
		List<String> fornecedores = fr.obtemFornecedor();
		ObservableList<String> fornecedoresList = FXCollections.observableArrayList(fornecedores);
		cmbFornecedorParaEditar.setItems(fornecedoresList);
	}

	public boolean fornecedorExisteNoBancoDeDados(ComboBox<String> cmb) {
		String fornecedorSelecionado = cmb.getSelectionModel().getSelectedItem();
		if (fornecedorSelecionado == null || fornecedorSelecionado.isEmpty()) {
			return false;
		}
		FornecedorRepository fr = new FornecedorRepository();
		List<String> fornecedoresNoBanco = fr.obtemFornecedor();

		return fornecedoresNoBanco.contains(fornecedorSelecionado);
	}

	public void carregaProdutos() {
		List<String> produtos = pd.obtemProdutos();
		ObservableList<String> produtosList = FXCollections.observableArrayList(produtos);
		cmbProdutos.setItems(produtosList);
	}

	public void carregaProdutosParaExcluir() {
		List<String> produtos = pd.obtemProdutos();
		ObservableList<String> produtosList = FXCollections.observableArrayList(produtos);
		cmbProdutosParaExcluir.setItems(produtosList);
	}

	public void carregaProdutosParaEditar() {
		List<String> produtos = pd.obtemProdutos();
		ObservableList<String> produtosList = FXCollections.observableArrayList(produtos);
		cmbProdutosParaEditar.setItems(produtosList);
	}

	public void habilitaBtnEditar() {
		btnEditarProduto.setDisable(false);
	}

	public void carregaAutoCompleteEmComboBox() {
		CommonMethods.autoCompleteComboBox(cmbFornecedor, CommonMethods.AutoCompleteMode.STARTS_WITH);
		CommonMethods.autoCompleteComboBox(cmbFornecedorParaEditar, CommonMethods.AutoCompleteMode.STARTS_WITH);
		CommonMethods.autoCompleteComboBox(cmbProdutos, CommonMethods.AutoCompleteMode.STARTS_WITH);
		CommonMethods.autoCompleteComboBox(cmbProdutosParaEditar, CommonMethods.AutoCompleteMode.STARTS_WITH);
		CommonMethods.autoCompleteComboBox(cmbProdutosParaExcluir, CommonMethods.AutoCompleteMode.STARTS_WITH);
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		carregaFornecedores();
		carregaProdutos();
		carregaProdutosParaExcluir();
		carregaProdutosParaEditar();
		carregaFornecedoresParaEditar();
		carregaAutoCompleteEmComboBox();
		lblLimpaCampos.setOnMouseEntered(e -> {
			lblLimpaCampos.setTextFill(Color.web("#242D34"));
		});
		lblLimpaCampos.setOnMouseExited(e -> {
			lblLimpaCampos.setTextFill(Color.web("#485968"));
		});
		cmbProdutosParaEditar.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> {
					preencheDadosProdutoSelecionado();
					habilitaBtnEditar();

				});

	}
}
