package com.juraj.sqlclient.controllers.TreeItems;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

import java.util.function.BiConsumer;

/**
 * Created by Juraj on 10.12.2017..
 */

//TODO: implement functions
public class FunctionTreeItem<String>  extends ExtendedTreeItem<String> {

    //private ContextMenu contextMenu;
    private MenuItem viewFunctionDefinition;

    public FunctionTreeItem(String value, Node graphic){
        super(value, graphic);

        contextMenu = new ContextMenu();
        contextMenu.setAutoHide(true);
        contextMenu.setHideOnEscape(true);
        contextMenu.getItems().add(new MenuItem("View Function Definition"));

    }

    public FunctionTreeItem(String schemaName, String functionName, Node graphic, BiConsumer<String, String> f_viewFunctionDefinition){
        super(functionName, graphic);

        contextMenu = new ContextMenu();
        contextMenu.setAutoHide(true);
        contextMenu.setHideOnEscape(true);

        viewFunctionDefinition = new MenuItem("View Function Definition");

        viewFunctionDefinition.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                f_viewFunctionDefinition.accept(schemaName, functionName);

            }
        });

        contextMenu.getItems().add(viewFunctionDefinition);


    }

    public ContextMenu getContextMenu(){
        return contextMenu;
    }
}
