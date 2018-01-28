package com.juraj.sqlclient.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

/**
 * Created by Juraj on 3.1.2018..
 */
public class DefinitionOverviewController {

    private String definition;

    @FXML
    public TextArea txa_definition;

    public DefinitionOverviewController(String definition){
        this.definition = definition;
    }

    @FXML
    public void initialize(){
        txa_definition.setText(definition);
    }
}
