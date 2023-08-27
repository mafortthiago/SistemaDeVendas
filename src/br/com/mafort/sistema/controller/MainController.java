package vendas;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

public class MainController implements Initializable {
	@FXML
	private AnchorPane acpFornecedor;
	@FXML
	private AnchorPane acpVenda;
	@FXML
	private AnchorPane acpProduto;
	@FXML
	private AnchorPane acpVendedor;
	@FXML
	private AnchorPane acpRelatorio;
    @FXML 
    private Button btnInserirVenda;
    @FXML
    private Button btnInserirVendedor;
    @FXML
    private Button btnInserirProduto;
    @FXML
    private Button btnInserirFornecedor;
    @FXML
    private Button btnRelatorios;
    @FXML
    private Pane paneVenda;
    @FXML
    private Pane paneVendedor;
    @FXML
    private Pane paneProduto;
    @FXML
  	private Pane paneRelatorio;
    VendaController vc = new VendaController();
    
   

    public void inHover(Button btn) {
        btn.setOnMouseEntered(event -> {
            btn.setStyle("-fx-background-color: #2A3C50");
        });
        outHover(btn);
    }

    public void outHover(Button btn) {
        btn.setOnMouseExited(event -> {
            btn.setStyle("-fx-background-color: #1F3247");
        });
    }
    @FXML
    private void handleButtonAction(ActionEvent event) {
        FXMLLoader fxmlLoader;
        try {
        if (event.getSource() == btnInserirVenda) {
            fxmlLoader = new FXMLLoader(getClass().getResource("PaneVenda.fxml"));
            Parent vendaRoot = fxmlLoader.load();
            acpVenda.getChildren().setAll(vendaRoot);
            VendaController controladorVenda = fxmlLoader.getController();
            controladorVenda.carregaProduto();
            controladorVenda.carregaVendedor();
            acpVenda.toFront();
        } else if (event.getSource() == btnInserirVendedor) {
            acpVendedor.toFront();
        } else if (event.getSource() == btnInserirProduto) {
            fxmlLoader = new FXMLLoader(getClass().getResource("PaneProduto.fxml"));
            Parent produtoRoot = fxmlLoader.load();
            acpProduto.getChildren().setAll(produtoRoot);
            ProdutoController controladorproduto = fxmlLoader.getController();
            controladorproduto.carregaProdutosParaExcluir();
            controladorproduto.carregaFornecedoresParaEditar();
            controladorproduto.carregaFornecedores();
            controladorproduto.carregaProdutos();
            controladorproduto.carregaProdutosParaEditar();
            acpProduto.toFront();
        } else if (event.getSource() == btnInserirFornecedor) {
            acpFornecedor.toFront();
        } else if(event.getSource() == btnRelatorios) {
            fxmlLoader = new FXMLLoader(getClass().getResource("PaneRelatorio.fxml"));

                Parent relatorioRoot = fxmlLoader.load();
                acpProduto.getChildren().setAll(relatorioRoot);
                RelatorioController relatorioController = fxmlLoader.getController();
                relatorioController.carregaFornecedor();
                relatorioController.carregaProdutos();
                relatorioController.carregaVendedor();
                acpRelatorio.toFront();
        }
        } catch (IOException e) {
            // Trate a exceção aqui (carregamento do FXML ou obtenção do controlador)
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        inHover(btnInserirVenda);
        inHover(btnInserirVendedor);
        inHover(btnInserirProduto);
        inHover(btnInserirFornecedor);
        inHover(btnRelatorios);
    }
}
