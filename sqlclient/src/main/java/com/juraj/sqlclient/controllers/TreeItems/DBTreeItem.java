package com.juraj.sqlclient.controllers.TreeItems;


import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

/**
 * Created by Juraj on 17.11.2017..
 */
public class DBTreeItem<String> extends ExtendedTreeItem<String> {

    //private ContextMenu contextMenu;

    public DBTreeItem(String value, Node graphic){
        super(value, graphic);

        contextMenu = new ContextMenu();
        contextMenu.setAutoHide(true);
        contextMenu.setHideOnEscape(true);
        contextMenu.getItems().add(new MenuItem("Show DB details"));

    }

    public ContextMenu getContextMenu(){
        return contextMenu;
    }
}
