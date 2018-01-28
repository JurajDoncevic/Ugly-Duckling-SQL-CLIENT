package com.juraj.sqlclient.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by Juraj on 12.11.2017..
 */
public class ErrorController {

    @FXML
    private Label lbl_message;

    @FXML
    private  Label lbl_header;

    @FXML
    private TextArea txa_stackTrace;

    @FXML
    private Button btn_OK;

    private Exception exception;

    public ErrorController(Exception e){
        exception = e;

    }

    @FXML
    public void initialize(){
        lbl_header.setText("A wild error appears!");
        lbl_message.setText(exception.getMessage());
        StringWriter stackTraceWriter = new StringWriter();
        exception.printStackTrace(new PrintWriter(stackTraceWriter));
        txa_stackTrace.setText(stackTraceWriter.toString());

    }

    @FXML
    private void click_btn_OK(MouseEvent me){
        //close the window
        Stage stage = (Stage) btn_OK.getScene().getWindow();
        stage.close();
    }
}
