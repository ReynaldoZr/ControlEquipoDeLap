package com.example.controlequipolap;

import javafx.application.Application;
import com.example.controlequipolap.database.JPAUtil;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("ControlEquipoLap - Equipos de laboratorio");
        stage.setScene(scene);
        stage.show();
    }
    @Override
    public void stop() {
        JPAUtil.cerrar();
    }
}
