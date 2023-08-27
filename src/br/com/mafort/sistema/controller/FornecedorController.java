package br.com.mafort.sistema.controller;

import br.com.mafort.sistema.view.*;
import br.com.mafort.sistema.utils.*;
import br.com.mafort.sistema.exceptions.*;
import br.com.mafort.sistema.modelo.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

public class FornecedorController implements Initializable {
	@FXML
	private ComboBox<String> cmbUf;
	@FXML
	private ComboBox<String> cmbCidade;
	@FXML
	private ComboBox<String> cmbUfEditar;
	@FXML
	private ComboBox<String> cmbCidadeEditar;
	@FXML
	private ComboBox<String> cmbFornecedor;
	@FXML
	private TextField txfNome;
	@FXML
	private TextField txfCNPJ;
	@FXML
	private TextField txfRua;
	@FXML
	private TextField txfBairro;
	@FXML
	private TextField txfnumero;
	@FXML
	private TextField txfNomeEditar;
	@FXML
	private TextField txfCNPJEditar;
	@FXML
	private TextField txfRuaEditar;
	@FXML
	private TextField txfBairroEditar;
	@FXML
	private TextField txfnumeroEditar;
	@FXML
	private Button btnInserir;
	@FXML
	private Button btnIrParaInserir;
	@FXML
	private Button btnIrparaEditar;
	@FXML
	private Button btnAtualizar;
	@FXML
	private Pane paneFornecedor;
	@FXML
	private Pane paneEditar;
	CidadeRepository cidades = new CidadeRepository();
	FornecedorRepository fr = new FornecedorRepository();
	UfRepository uf = new UfRepository();

	public boolean naoContemNumeros(String str) {
		for (char c : str.toCharArray()) {
			if (Character.isDigit(c)) {
				return false;
			}
		}
		return true;
	}

	public void atualizaFornecedor() {

		String nomeAnterior = cmbFornecedor.getValue();
		String rua = txfRuaEditar.getText();
		String bairro = txfBairroEditar.getText();
		String numero = txfnumeroEditar.getText();
		String nome = txfNomeEditar.getText();
		String cnpj = txfCNPJEditar.getText();
		int idCidade = cidades.obtemIdDaCidadeSelecionada(cmbCidadeEditar.getValue());
		Endereco e = new Endereco(rua, bairro, numero, idCidade);
		Fornecedor fornecedorAtualizado = new Fornecedor(nome, cnpj, e);
		try {

			if (!naoContemNumeros(nome) || nome.length() < 4) {
				throw new CamposInvalidosException("Nome de fornecedor inválido!");
			} else if (!cnpjValido(cnpj)) {
				throw new CamposInvalidosException("CNPJ inválido!");
			} else if (cnpjExistente(cnpj)
					&& !cnpj.equals(fr.obtemFornecedorPeloId(fr.obtemIdFornecedor(nomeAnterior)).getCnpj())) {
				throw new CamposInvalidosException("CNPJ já cadastrado!");
			} else if (rua.length() < 3) {
				throw new CamposInvalidosException("Nome de rua com tamanho pequeno demais!");
			} else if (bairro.length() < 3) {
				throw new CamposInvalidosException("Nome de bairro com tamanho pequeno demais!");
			}else if(!naoContemNumeros(numero)) {
				throw new CamposInvalidosException("O número não pode conter letras.");

			}else if (!camposPreenchidos(nome, cnpj, rua, bairro, numero, cmbCidadeEditar.getValue(),
					cmbUfEditar.getValue())) {
				throw new CamposInvalidosException("Preencha todos os campos!");
			} else {
				fr.atualizaFornecedor(fornecedorAtualizado, fr.obtemIdFornecedor(cmbFornecedor.getValue()));
				CommonMethods.mostrarAviso("Sucesso", "Fornecedor editado com êxito!");
				limpaCampos(txfNomeEditar, txfCNPJEditar, txfRuaEditar, txfBairroEditar, txfnumeroEditar,
						cmbCidadeEditar, cmbUfEditar);
				cmbFornecedor.setValue("");

			}

		} catch (CamposInvalidosException erro) {
			CommonMethods.mostrarAviso("Erro", erro.getMessage());
		}

	}

	public boolean camposPreenchidos(String nome, String cnpj, String rua, String bairro, String numero, String cidade,
			String uf) {
		return !(nome.isEmpty() || cnpj.isEmpty() || rua.isEmpty() || bairro.isEmpty() || numero.isEmpty()
				|| cidade == null || uf == null);
	}

	public boolean cnpjValido(String str) {
		return str.matches("\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}") && str.length() == 18;
	}

	public boolean cnpjExistente(String cnpj) {
		return fr.obtemCpnjFornecedores().contains(cnpj);
	}

	public void ativaEditar() {
		paneEditar.toFront();
	}

	public void ativaInserir() {
		paneFornecedor.toFront();
	}

	public void preencheDadosFornecedor() {
		String fornecedorSelecionadoNome = cmbFornecedor.getValue();
		if (fornecedorSelecionadoNome != null && !fornecedorSelecionadoNome.isEmpty()) {

			Fornecedor fornecedorSelecionado = fr
					.obtemFornecedorPeloId(fr.obtemIdFornecedor(fornecedorSelecionadoNome));
			if (fornecedorSelecionado != null) {
				txfNomeEditar.setText(fornecedorSelecionado.getNome());
				txfCNPJEditar.setText(fornecedorSelecionado.getCnpj());
				txfRuaEditar.setText(fornecedorSelecionado.getEndereco().getRua());
				txfBairroEditar.setText(fornecedorSelecionado.getEndereco().getBairro());
				txfnumeroEditar.setText(fornecedorSelecionado.getEndereco().getNumero());
				cmbUfEditar.setValue(uf.obtemUfPeloId(
						cidades.retornaCidadePeloId(fornecedorSelecionado.getEndereco().getCidade_id()).getUf_id())
						.getSigla());
				cmbCidadeEditar.setValue(
						cidades.retornaCidadePeloId(fornecedorSelecionado.getEndereco().getCidade_id()).getNome());

				cmbUfEditar.setDisable(false);
				cmbCidadeEditar.setDisable(false);
				txfNomeEditar.setDisable(false);
				txfCNPJEditar.setDisable(false);
				txfRuaEditar.setDisable(false);
				txfBairroEditar.setDisable(false);
				txfnumeroEditar.setDisable(false);
				btnAtualizar.setDisable(false);
			}
		}
	}

	public void carregaFornecedores() {
		List<String> fornecedores = fr.obtemFornecedor();
		ObservableList<String> fornecedorList = FXCollections.observableArrayList(fornecedores);
		cmbFornecedor.setItems(fornecedorList);
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		carregaUf();
		carregaFornecedores();
		carregaAutoCompleteEmComboBox();

	}

	public void carregaUf() {
		cmbUf.getItems().addAll(uf.obtemsSiglaUfs());
		cmbCidade.setValue("");
	}

	public void carregaCidade() {

		cmbCidade.getItems().clear();
		cmbCidade.getItems().addAll(cidades.obtemNomeCidade(cmbUf.getValue()));
	}

	public void carregaCidadeEditar() {

		cmbCidadeEditar.getItems().clear();
		cmbCidadeEditar.getItems().addAll(cidades.obtemNomeCidade(cmbUfEditar.getValue()));
	}

	public void carregaUfEditar() {

		cmbUfEditar.getItems().addAll(uf.obtemsSiglaUfs());
		cmbCidadeEditar.setValue("");
	}

	public void insereFornecedor() {
		String rua = txfRua.getText();
		String bairro = txfBairro.getText();
		String numero = txfnumero.getText();
		String nome = txfNome.getText();
		String cnpj = txfCNPJ.getText();
		int idCidade = cidades.obtemIdDaCidadeSelecionada(cmbCidade.getValue());
		Endereco e = new Endereco(rua, bairro, numero, idCidade);
		Fornecedor f = new Fornecedor(nome, cnpj, e);

		try {

			if (!naoContemNumeros(nome) || nome.length() < 4) {
				throw new CamposInvalidosException("Nome de fornecedor inválido!");
			} else if (!cnpjValido(cnpj)) {
				throw new CamposInvalidosException("CNPJ inválido!");
			} else if (cnpjExistente(cnpj)) {
				throw new CamposInvalidosException("CNPJ já cadastrado!");
			} else if (rua.length() < 3) {
				throw new CamposInvalidosException("Nome de rua com tamanho pequeno demais!");
			} else if (bairro.length() < 3) {
				throw new CamposInvalidosException("Nome de bairro com tamanho pequeno demais!");
			} else if (!camposPreenchidos(nome, cnpj, rua, bairro, numero, cmbCidade.getValue(), cmbUf.getValue())) {
				throw new CamposInvalidosException("Preencha todos os campos!");
			} else {
				fr.insereFornecedor(f);
				CommonMethods.mostrarAviso("Sucesso", "fornecedor inserido com êxito!");
				limpaCampos(txfNome, txfCNPJ, txfRua, txfBairro, txfnumero, cmbCidade, cmbUf);

			}
		} catch (CamposInvalidosException erro) {
			CommonMethods.mostrarAviso("Erro", erro.getMessage());
		}
	}

	public void limpaCampos(TextField nome, TextField cnpj, TextField rua, TextField bairro, TextField numero,
			ComboBox<String> cidade, ComboBox<String> uf) {
		nome.setText("");
		cnpj.setText("");
		rua.setText("");
		bairro.setText("");
		numero.setText("");
		cidade.setValue("");
		uf.setValue("");
	}

	public void habilitabtn() {
		btnInserir.setDisable(verificaCampos());
	}

	public boolean verificaCampos() {
		boolean habilita;
		if (txfBairro.getText().equals("") || txfCNPJ.getText().equals("") || txfnumero.getText().equals("")
				|| txfNome.getText().equals("") || cmbCidade.getValue() == null || cmbUf.getValue() == null) {
			habilita = true;
		} else {
			habilita = false;
		}
		return habilita;
	}

	public void carregaAutoCompleteEmComboBox() {
		CommonMethods.autoCompleteComboBox(cmbFornecedor, CommonMethods.AutoCompleteMode.STARTS_WITH);
	}
}
