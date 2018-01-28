package com.juraj.sqlclient.controllers.TreeItems;


import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

import java.util.function.Consumer;

/**
 * Created by Juraj on 17.11.2017..
 */
public class SchemaTreeItem<String> extends ExtendedTreeItem<String> {

    //private ContextMenu contextMenu;
    private MenuItem viewSchemaDiagram;
    private MenuItem newQueryOnSchema;
    private MenuItem viewViewsDiagram;

    public SchemaTreeItem(String value, Node graphic){
        super(value, graphic);

        contextMenu = new ContextMenu();
        contextMenu.setAutoHide(true);
        contextMenu.setHideOnEscape(true);
        contextMenu.getItems().add(new MenuItem("View schema diagram"));
        contextMenu.getItems().add(new MenuItem("New query on schema"));
        contextMenu.getItems().add(new MenuItem("View views diagram"));


    }

    public SchemaTreeItem(String schemaName, Node graphic, Consumer<String> f_viewSchema, Consumer<String> f_newQuery, Consumer<String> f_viewViews){
        super(schemaName, graphic);

        contextMenu = new ContextMenu();

        viewSchemaDiagram = new MenuItem("View schema diagram");
        viewSchemaDiagram.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                f_viewSchema.accept(schemaName);
            }
        });

        newQueryOnSchema = new MenuItem("New query on schema");
        newQueryOnSchema.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                f_newQuery.accept(schemaName);
            }
        });

        viewViewsDiagram = new MenuItem("View views diagram");
        viewViewsDiagram.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                f_viewViews.accept(schemaName);
            }
        });

        contextMenu.getItems().add(viewSchemaDiagram);
        contextMenu.getItems().add(newQueryOnSchema);
        contextMenu.getItems().add(viewViewsDiagram);


    }

    public ContextMenu getContextMenu(){
        return contextMenu;
    }
}
