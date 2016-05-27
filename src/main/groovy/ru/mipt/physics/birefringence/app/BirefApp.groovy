package ru.mipt.physics.birefringence.app

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage

/**
 * Created by darksnake on 21-May-16.
 */
class BirefApp extends Application {

    @Override
    void start(Stage primaryStage) throws Exception {
        Locale.setDefault(Locale.US);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Biref.fxml"));
//        loader.setController(new BirefView());
        Parent parent = loader.load()
        primaryStage.setTitle("Лабораторная работа 4.7.1: Двулучепреломление")
        primaryStage.setScene(new Scene(parent, 800, 600));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(BirefApp, args);
    }
}
