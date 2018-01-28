package com.juraj.sqlclient.utils;

import com.juraj.sqlclient.controllers.ErrorController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Created by Juraj on 12.11.2017..
 */
public class ErrorHandler {

    public ErrorHandler(Exception exception){
        displayError(exception);
    }

    private void displayError(Exception exception){
        Parent root;
        Stage errorStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ErrorWindow.fxml"));
        fxmlLoader.setController(new ErrorController(exception));
        try {
            root = fxmlLoader.load();
            errorStage.setTitle("Error");
            errorStage.setScene(new Scene(root));

        } catch (IOException e) {
            e.printStackTrace();
        }

        //primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/appIcon.png")));


        errorStage.showAndWait();
    }
}
