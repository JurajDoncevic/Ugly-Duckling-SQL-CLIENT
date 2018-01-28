package com.juraj.sqlclient.controllers;

import com.juraj.sqlclient.connectionManagement.DBCredentials;
import com.juraj.sqlclient.connectionManagement.DBCredentialsPostgres;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * Created by Juraj on 12.11.2017..
 */
public class DBLoginController {

    private DBCredentials credentials;

    @FXML
    private TextField tbx_serverURL;

    @FXML
    private TextField tbx_dbName;

    @FXML
    private TextField tbx_username;

    @FXML
    private PasswordField psf_password;

    @FXML
    private Button btn_connect;

    @FXML
    private Button btn_cancel;

    @FXML
    private Label lbl_message;

    public DBLoginController(){

    }

    @FXML
    private void initialize(){

        tbx_serverURL.setText("localhost:5432");
        tbx_dbName.setText("dvdrental");
        tbx_username.setText("postgres");
        psf_password.setText("admin");

    }

    @FXML
    private void click_btn_connect(MouseEvent me){
        credentials = new DBCredentialsPostgres(tbx_serverURL.getText().trim(),
                                        tbx_dbName.getText().trim(),
                                        tbx_username.getText().trim(),
                                        psf_password.getText().trim());

        if(getCredentials().isValid()){
            Stage stage = (Stage) btn_connect.getScene().getWindow();
            stage.close();
        }else {
            lbl_message.setText("Could not establish succesful connection!");
            lbl_message.setVisible(true);
        }
    }

    @FXML
    private void click_btn_cancel(MouseEvent me){
        Stage stage = (Stage) btn_connect.getScene().getWindow();
        stage.close();
    }

    public DBCredentials getCredentials() {
        return credentials;
    }
}
