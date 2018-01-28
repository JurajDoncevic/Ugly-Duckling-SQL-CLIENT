package com.juraj.sqlclient;

import com.juraj.sqlclient.controllers.DBLoginController;
import com.juraj.sqlclient.controllers.MainWindowController;
import com.juraj.sqlclient.connectionManagement.DBCredentials;
import com.juraj.sqlclient.utils.ErrorHandler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Created by Juraj on 29.10.2017..
 */
public class Main extends Application{

    @Override
    public void start(Stage primaryStage) throws Exception{
        Stage loginStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/DBLoginWindow.fxml"));
        DBLoginController loginController = new DBLoginController();
        fxmlLoader.setController(loginController);
        Parent root;
        try {
            root = fxmlLoader.load();
            loginStage.getIcons().add(new Image(getClass().getResourceAsStream("/appIcon.png")));
            loginStage.setTitle("Ugly Duckling - SQL CLIENT - Login");
            loginStage.setScene(new Scene(root));
            loginStage.setMinHeight(400.0);
            loginStage.setMinWidth(400.0);

        } catch (IOException e) {
            new ErrorHandler(e);
        }
        loginStage.showAndWait();
        DBCredentials credentials = loginController.getCredentials();

        if(credentials == null || !credentials.isValid()){
            System.exit(1);
        }


        fxmlLoader = new FXMLLoader(getClass().getResource("/MainWindow.fxml"));
        MainWindowController mainWindowController = new MainWindowController(credentials);
        fxmlLoader.setController(mainWindowController);
        try{
            root = fxmlLoader.load();
            primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/appIcon.png")));
            primaryStage.setTitle("Ugly Duckling - SQL CLIENT");
            primaryStage.setScene(new Scene(root));
            primaryStage.setMaximized(true);
        } catch (IOException e) {
            new ErrorHandler(e);
        }
        primaryStage.show();

    }

    public static void main(String args[]){

        launch(args);
    }
}
