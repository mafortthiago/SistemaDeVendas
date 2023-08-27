package br.com.mafort.sistema.controller;

import java.net.URL;
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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;

public class VendedorController implements Initializable {
	@FXML
	private ComboBox<String> cmbUf;
	@FXML
	private ComboBox<String> cmbVendedor;
	@FXML
	private ComboBox<String> cmbCidade;
	@FXML
	private ComboBox<String> cmbUfEditar;
	@FXML
	private ComboBox<String> cmbCidadeEditar;
	@FXML
	private TextField txfNome;
	@FXML
	private TextField txfCPF;
	@FXML
	private TextField txfComissao;
	@FXML
	private TextField txfRua;
	@FXML
	private TextField txfBairro;
	@FXML
	private TextField txfnumero;
	@FXML
	private TextField txfNomeEditar;
	@FXML
	private TextField txfCPFEditar;
	@FXML
	private TextField txfRuaEditar;
	@FXML
	private TextField txfBairroEditar;
	@FXML
	private TextField txfnumeroEditar;
	@FXML
	private TextField txfComissaoEditar;
	@FXML
	private Button btnInserir;
	@FXML
	private Button btnIrParaEditar;
	@FXML
	private Button btnIrParaInserir;
	@FXML
	private Button btnEditar;
	@FXML
	private Pane paneVendedor;
	@FXML
	private Pane paneEditar;

	CidadeRepository cidades = new CidadeRepository();
	UfRepository uf = new UfRepository();
	VendedorRepository vr = new VendedorRepository();

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		UfRepository ufs = new UfRepository();
		cmbUf.getItems().addAll(ufs.obtemsSiglaUfs());

		pesquisaEmComboBoxVendedores();
	}
	public void preencheDadosVendedor() {
		VendedorRepository vr = new VendedorRepository();
		String fornecedorSelecionadoNome = cmbVendedor.getValue();
		if (fornecedorSelecionadoNome != null && !fornecedorSelecionadoNome.isEmpty()) {

			Vendedor vendedorSelecionado = vr.obtemVendedorPeloNome(fornecedorSelecionadoNome);
			if (vendedorSelecionado != null) {
				txfNomeEditar.setText(vendedorSelecionado.getNome());
				txfCPFEditar.setText(vendedorSelecionado.getCpf());
				txfRuaEditar.setText(vendedorSelecionado.getEndereco().getRua());
				txfBairroEditar.setText(vendedorSelecionado.getEndereco().getBairro());
				txfnumeroEditar.setText(vendedorSelecionado.getEndereco().getNumero());
				txfComissaoEditar.setText(String.valueOf(vendedorSelecionado.getComissaoPercentual()));
				cmbUfEditar.setValue(uf.obtemUfPeloId(
						cidades.retornaCidadePeloId(vendedorSelecionado.getEndereco().getCidade_id()).getUf_id())
						.getSigla());
				cmbCidadeEditar.setValue(
						cidades.retornaCidadePeloId(vendedorSelecionado.getEndereco().getCidade_id()).getNome());

				cmbUfEditar.setDisable(false);
				cmbCidadeEditar.setDisable(false);
				txfNomeEditar.setDisable(false);
				txfCPFEditar.setDisable(false);
				txfRuaEditar.setDisable(false);
				txfBairroEditar.setDisable(false);
				txfnumeroEditar.setDisable(false);
				txfComissaoEditar.setDisable(false);
				btnEditar.setDisable(false);
			}
		}
	}

	public void carregaCidadeEditar() {

		cmbCidadeEditar.getItems().clear();
		cmbCidadeEditar.getItems().addAll(cidades.obtemNomeCidade(cmbUfEditar.getValue()));
	}

	public void carregaUfEditar() {

		cmbUfEditar.getItems().addAll(uf.obtemsSiglaUfs());
		// cmbCidadeEditar.setValue("");
	}

	public void pesquisaEmComboBoxVendedores() {
		VendedorRepository vr = new VendedorRepository();
		List<String> vendedores = vr.obtemNomeVendedores();
		ObservableList<String> vendedorList = FXCollections.observableArrayList(vendedores);
		cmbVendedor.setItems(vendedorList);
	}

	public void ativaEditar() {
		paneEditar.toFront();
	}

	public void ativaInserir() {
		paneVendedor.toFront();
	}

	public void carregaCidade() {
		CidadeRepository cidades = new CidadeRepository();
		cmbCidade.getItems().clear();
		;
		cmbCidade.getItems().addAll(cidades.obtemNomeCidade(cmbUf.getValue()));
	}

	public boolean verificaCampos() {
		boolean habilita;
		if (txfBairro.getText().equals("") || txfCPF.getText().equals("") || txfnumero.getText().equals("")
				|| txfNome.getText().equals("") || txfComissao.getText().equals("") || cmbCidade.getValue() == null
				|| cmbUf.getValue() == null) {
			habilita = true;
		} else {
			habilita = false;
		}
		return habilita;
	}

	public boolean naoContemNumeros(String str) {
		for (char c : str.toCharArray()) {
			if (Character.isDigit(c)) {
				return false;
			}
		}
		return true;
	}

	public boolean cpfValido(String str) {
		return str.matches("\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}") && str.length() == 14;
	}

	public boolean cpfExistente(String cpf) {
		List<Vendedor> vendedores = vr.obtemVendedores();
		for (Vendedor vendedor : vendedores) {
			if (vendedor.getCpf().equals(cpf)) {
				return true;
			}
		}
		return false;
	}

	public boolean comissaoValida(String comissao) {
		return comissao.matches("\\d+(\\.\\d{1,2})?");
	}

	public void atualizaVendedor() {
		CidadeRepository cidades = new CidadeRepository();
		String nome = txfNomeEditar.getText();
		String cpfOriginal = vr.obtemVendedorPeloNome(cmbVendedor.getValue()).getCpf();
		int id = vr.obtemVendedorPeloNome(cmbVendedor.getValue()).getId();
		String cpf = txfCPFEditar.getText();

		String rua = txfRuaEditar.getText();
		String bairro = txfBairroEditar.getText();
		String numero = txfnumeroEditar.getText();
		int idCidade = cidades.obtemIdDaCidadeSelecionada(cmbCidadeEditar.getValue());

		try {

			if (nome.length() < 3) {
				throw new CamposInvalidosException("Nome de vendedor inválido!");
			} else if (!cpfValido(cpf)) {
				throw new CamposInvalidosException("CPF inválido!");
			} else if (rua.length() < 3) {
				throw new CamposInvalidosException("Nome de rua com tamanho pequeno demais!");
			} else if (bairro.length() < 3) {
				throw new CamposInvalidosException("Nome de bairro com tamanho pequeno demais!");
			} else if (!comissaoValida(txfComissaoEditar.getText())) {
				throw new CamposInvalidosException(
						"Valor de comissão contém letras ou não contém apenas dois algarismos.");
			} else if (Double.parseDouble(txfComissaoEditar.getText()) <= 0) {
				throw new CamposInvalidosException("Valor de comissão não pode ser negativo.");
			} else if (Double.parseDouble(txfComissaoEditar.getText()) > 30) {
				throw new CamposInvalidosException("O valor máximo de comissão é de 30%.");

			} else if (!camposPreenchidos(nome, cpf, rua, bairro, numero, cmbCidadeEditar.getValue(),
					cmbUfEditar.getValue(), txfComissaoEditar.getText())) {
				throw new CamposInvalidosException("Preencha todos os campos!");
			} else {
				double comissao = Double.parseDouble(txfComissaoEditar.getText());
				Endereco e = new Endereco(rua, bairro, numero, idCidade);
				Vendedor v = new Vendedor(id, nome, cpf, e, comissao);
				boolean editado = vr.atualizaVendedor(v);
				if (editado) {
					CommonMethods.mostrarAviso("Sucesso", "Vendedor atualizado!");
					limpaCamposEditar();

				}

			}

		} catch (CamposInvalidosException erro) {
			CommonMethods.mostrarAviso("Erro", erro.getMessage());
		}
	}

	private void limpaCamposEditar() {
		txfBairroEditar.setText("");
		txfCPFEditar.setText("");
		txfnumeroEditar.setText("");
		txfNomeEditar.setText("");
		txfRuaEditar.setText("");
		cmbCidadeEditar.setValue("");
		cmbUfEditar.setValue("");
		txfComissaoEditar.setText("");
		cmbVendedor.setValue("");
	}

	public void insereVendedor() {
		CidadeRepository cidades = new CidadeRepository();
		String nome = txfNome.getText();
		String cpf = txfCPF.getText();

		String rua = txfRua.getText();
		String bairro = txfBairro.getText();
		String numero = txfnumero.getText();
		int idCidade = cidades.obtemIdDaCidadeSelecionada(cmbCidade.getValue());

		try {

			if (!naoContemNumeros(nome) || nome.length() < 4) {
				throw new CamposInvalidosException("Nome de vendedor inválido!");
			} else if (!cpfValido(cpf)) {
				throw new CamposInvalidosException("CPF inválido!");
			} else if (cpfExistente(cpf)) {
				throw new CamposInvalidosException("CPF já cadastrado!");
			} else if (rua.length() < 3) {
				throw new CamposInvalidosException("Nome de rua com tamanho pequeno demais!");
			} else if (bairro.length() < 3) {
				throw new CamposInvalidosException("Nome de bairro com tamanho pequeno demais!");
			} else if (!comissaoValida(txfComissao.getText())) {
				throw new CamposInvalidosException(
						"Valor de comissão contém letras ou não contém apenas dois algarismos.");
			} else if (Double.parseDouble(txfComissao.getText()) <= 0) {
				throw new CamposInvalidosException("Valor de comissão não pode ser negativo.");
			} else if (Double.parseDouble(txfComissao.getText()) > 30) {
				throw new CamposInvalidosException("O valor máximo de comissão é de 30%.");

			} else if (!camposPreenchidos(nome, cpf, rua, bairro, numero, cmbCidade.getValue(), cmbUf.getValue(),
					txfComissao.getText())) {
				throw new CamposInvalidosException("Preencha todos os campos!");
			} else {
				double comissao = Double.parseDouble(txfComissao.getText());
				Endereco e = new Endereco(rua, bairro, numero, idCidade);
				Vendedor v = new Vendedor(nome, cpf, e, comissao);
				boolean inserido = vr.insereVendedor(v);;
				if (inserido) {
					CommonMethods.mostrarAviso("Sucesso!", "Vendedor inserido!");
					limpaCampos();

				}

			}

		} catch (CamposInvalidosException erro) {
			CommonMethods.mostrarAviso("Erro", erro.getMessage());
		}
	}

	public boolean camposPreenchidos(String nome, String cpf, String rua, String bairro, String numero, String cidade,
			String uf, String comissao) {
		return !(nome.isEmpty() || cpf.isEmpty() || rua.isEmpty() || bairro.isEmpty() || numero.isEmpty()
				|| cidade == null || uf == null || comissao.isEmpty());
	}

	public void limpaCampos() {
		txfBairro.setText("");
		txfCPF.setText("");
		txfnumero.setText("");
		txfNome.setText("");
		txfRua.setText("");
		cmbCidade.setValue("");
		cmbUf.setValue("");
		txfComissao.setText("");
	}

	public void habilitabtn() {
		 btnInserir.setDisable(verificaCampos());
	}
}
