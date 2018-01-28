package com.juraj.sqlclient.controllers.TreeItems;


import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

import java.util.function.BiConsumer;

/**
 * Created by Juraj on 17.11.2017..
 */
public class TableTreeItem<String> extends ExtendedTreeItem<String> {

    //private ContextMenu contextMenu;
    private MenuItem viewTop100;
    private MenuItem viewAll;
    private MenuItem viewDefinition;

    public TableTreeItem(String value, Node graphic){
        super(value, graphic);

        contextMenu = new ContextMenu();
        contextMenu.setAutoHide(true);
        contextMenu.setHideOnEscape(true);
        contextMenu.getItems().add(new MenuItem("View TOP 100"));
        contextMenu.getItems().add(new MenuItem("View All"));

    }

    public TableTreeItem(String schemaName, String tableName, Node graphic, BiConsumer<String, String> f_viewTop100,
                         BiConsumer<String, String> f_viewAll, BiConsumer<String, String> f_viewDefinition){
        super(tableName, graphic);

        contextMenu = new ContextMenu();
        contextMenu.setAutoHide(true);
        contextMenu.setHideOnEscape(true);

        viewTop100 = new MenuItem("Display TOP 100");
        viewTop100.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                f_viewTop100.accept(schemaName, tableName);

            }
        });

        viewAll = new MenuItem("Display Full Table");
        viewAll.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                f_viewAll.accept(schemaName, tableName);

            }
        });

        viewDefinition = new MenuItem("View Table Definition");
        viewDefinition.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                f_viewDefinition.accept(schemaName, tableName);
            }
        });


        contextMenu.getItems().add(viewTop100);
        contextMenu.getItems().add(viewAll);
        contextMenu.getItems().add(viewDefinition);


    }

    public ContextMenu getContextMenu(){
        return contextMenu;
    }
}
