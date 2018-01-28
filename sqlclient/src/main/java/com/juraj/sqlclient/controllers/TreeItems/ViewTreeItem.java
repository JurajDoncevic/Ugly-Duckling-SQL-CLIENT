package com.juraj.sqlclient.controllers.TreeItems;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

import java.util.function.BiConsumer;

/**
 * Created by Juraj on 2.1.2018..
 */
public class ViewTreeItem<String> extends ExtendedTreeItem<String> {

    //private ContextMenu contextMenu;
    private MenuItem viewTop100;
    private MenuItem viewAll;
    private MenuItem viewDefinition;

    public ViewTreeItem(String value, Node graphic) {
        super(value, graphic);

        contextMenu = new ContextMenu();
        contextMenu.setAutoHide(true);
        contextMenu.setHideOnEscape(true);
        contextMenu.getItems().add(new MenuItem("Display View"));
    }

    public ViewTreeItem(String schemaName, String viewName, Node graphic, BiConsumer<String, String> f_viewTop100,
                        BiConsumer<String, String> f_viewAll, BiConsumer<String, String> f_viewDefinition){
        super(viewName, graphic);

        contextMenu = new ContextMenu();
        contextMenu.setAutoHide(true);
        contextMenu.setHideOnEscape(true);

        viewTop100 = new MenuItem("Display TOP 100");

        viewTop100.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                f_viewTop100.accept(schemaName, viewName);

            }
        });

        viewAll = new MenuItem("Display Full View");
        viewAll.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                f_viewAll.accept(schemaName, viewName);

            }
        });

        viewDefinition = new MenuItem("View Definition");
        viewDefinition.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                f_viewDefinition.accept(schemaName, viewName);
            }
        });


        contextMenu.getItems().add(viewTop100);
        contextMenu.getItems().add(viewAll);
        contextMenu.getItems().add(viewDefinition);


    }

    public ContextMenu getContextMenu() {
        return contextMenu;
    }
}
