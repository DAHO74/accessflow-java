package com.accessflow.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class Navigator {

    private static Stage primaryStage;
    private static BorderPane mainRoot;

    public static void setPrimaryStage(Stage stage) { primaryStage = stage; }
    public static void setMainRoot(BorderPane root) { mainRoot = root; }
    public static Stage getPrimaryStage() { return primaryStage; }

    public static void showLogin() throws IOException {
        loadScene("/fxml/Login.fxml", 460, 560);
        primaryStage.setResizable(false);
    }

    public static void showRegister() throws IOException {
        loadScene("/fxml/Register.fxml", 480, 620);
        primaryStage.setResizable(false);
    }

    public static void showMain() throws IOException {
        loadScene("/fxml/Main.fxml", 1200, 760);
        primaryStage.setResizable(true);
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);
    }

    public static void navigateTo(String fxml) {
        try {
            URL url = Navigator.class.getResource("/fxml/" + fxml);
            FXMLLoader loader = new FXMLLoader(url);
            Node content = loader.load();
            mainRoot.setCenter(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadScene(String fxml, double width, double height) throws IOException {
        FXMLLoader loader = new FXMLLoader(Navigator.class.getResource(fxml));
        Scene scene = new Scene(loader.load(), width, height);
        scene.getStylesheets().add(Navigator.class.getResource("/css/styles.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
    }
}
