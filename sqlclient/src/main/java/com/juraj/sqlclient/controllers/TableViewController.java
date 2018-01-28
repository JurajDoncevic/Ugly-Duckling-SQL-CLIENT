package com.juraj.sqlclient.controllers;

import com.juraj.sqlclient.utils.ToObservableList2D;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import org.apache.metamodel.data.DataSet;
import org.apache.metamodel.schema.Column;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Created by Juraj on 3.1.2018..
 */
public class TableViewController {

    private DataSet dataSet;
    private ObservableList<ObservableList<Object>> observableData;

    @FXML
    public TableView tbv_results;

    public TableViewController(DataSet dataSet){
        this.dataSet = dataSet;
        this.observableData = ToObservableList2D.convertFromDataSet(this.dataSet);
    }

    @FXML
    public void initialize(){
        setResultsTable();
    }

    private void setResultsTable(){

        tbv_results.getItems().clear();
        tbv_results.getColumns().clear();
        tbv_results.refresh();


        tbv_results.setEditable(true);



        java.util.List<Column> columns = Arrays.asList(dataSet.getSelectItems()).stream().map(x->x.getColumn())
                .collect(Collectors.toList());

        int i = 0;
        for(Column column: columns){
            final int j = i;
            TableColumn _column = new TableColumn(column.getName());

            _column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList,String>,ObservableValue<String>>() {

                public ObservableValue call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                    return new SimpleStringProperty(String.valueOf(param.getValue().get(j)));
                }
            });
            i++;
            tbv_results.getColumns().addAll(_column);
        }



        tbv_results.setItems(observableData);

        tbv_results.refresh();
    }
}
