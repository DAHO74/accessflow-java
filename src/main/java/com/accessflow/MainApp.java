package com.accessflow;

import com.accessflow.service.AuthService;
import com.accessflow.util.HibernateUtil;
import com.accessflow.util.Navigator;
import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Navigator.setPrimaryStage(stage);
        stage.setTitle("AccessFlow");

        if (AuthService.existeAdmin()) {
            Navigator.showLogin();
        } else {
            Navigator.showRegister();
        }

        stage.show();
    }

    @Override
    public void stop() {
        HibernateUtil.shutdown();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
