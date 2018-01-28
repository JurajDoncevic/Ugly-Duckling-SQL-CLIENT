package com.juraj.sqlclient.viewManagement;

/**
 * Created by Juraj on 2.1.2018..
 */
public class View {

    private String name;
    private String text;

    public View(String name, String text){
        this.name = name;
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }
}
