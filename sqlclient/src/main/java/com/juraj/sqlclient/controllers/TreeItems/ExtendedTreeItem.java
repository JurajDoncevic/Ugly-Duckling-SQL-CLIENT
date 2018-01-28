package com.juraj.sqlclient.controllers.TreeItems;

import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TreeItem;

/**
 * Created by Juraj on 10.12.2017..
 */
public class ExtendedTreeItem<T> extends TreeItem<T> {

    public ContextMenu contextMenu;

    public ExtendedTreeItem(T value, Node graphic){
        super(value, graphic);
    }
}
