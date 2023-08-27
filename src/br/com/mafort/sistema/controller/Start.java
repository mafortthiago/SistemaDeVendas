package br.com.mafort.sistema.controller;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Start extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../br.com.mafort.sistema.controller/TelaAbertura.fxml"));
        Parent telaAbertura = loader.load();
        Scene cenaAbertura = new Scene(telaAbertura);
        Stage janelaAbertura = new Stage();
        janelaAbertura.setTitle("Bem-vindo(a) ao sistema de vendas");
        janelaAbertura.setScene(cenaAbertura);
        janelaAbertura.show();
        Text textoBemVindo = (Text) telaAbertura.lookup("#textoBemVindo");
        Line linha1 = (Line) telaAbertura.lookup("#linha1");
        Line linha2 = (Line) telaAbertura.lookup("#linha2");
        FadeTransition fadeOutTexto = new FadeTransition(Duration.seconds(3), textoBemVindo);
        fadeOutTexto.setFromValue(1.0);
        fadeOutTexto.setToValue(0.0);
        TranslateTransition moveRight = new TranslateTransition(Duration.seconds(3), linha1);
        moveRight.setFromX(0);
        moveRight.setToX(400);
        TranslateTransition moveLeft = new TranslateTransition(Duration.seconds(3), linha2);
        moveLeft.setFromX(0);
        moveLeft.setToX(-400);
        FadeTransition fadeOutLinha1 = new FadeTransition(Duration.seconds(3), linha1);
        fadeOutLinha1.setFromValue(1.0);
        fadeOutLinha1.setToValue(0.0);
        FadeTransition fadeOutLinha2 = new FadeTransition(Duration.seconds(3), linha2);
        fadeOutLinha2.setFromValue(1.0);
        fadeOutLinha2.setToValue(0.0);
        fadeOutTexto.setOnFinished(event -> {
            Parent root = null;
            try {
                root = FXMLLoader.load(getClass().getResource("../br.com.mafort.sistema.controller/Main.fxml"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            Scene scene = new Scene(root);
            primaryStage.setTitle("Sistema de vendas");
            primaryStage.setScene(scene);
            primaryStage.show();
            janelaAbertura.close();
        });
        ParallelTransition parallelTransition = new ParallelTransition();
        parallelTransition.getChildren().addAll(fadeOutTexto, moveRight, moveLeft, fadeOutLinha1, fadeOutLinha2);
        parallelTransition.play();
    }

    public static void main(String[] args) {
        launch();
    }
}