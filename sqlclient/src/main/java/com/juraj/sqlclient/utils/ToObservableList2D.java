package com.juraj.sqlclient.utils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.metamodel.data.DataSet;
import org.apache.metamodel.data.Row;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Juraj on 31.10.2017..
 */
public class ToObservableList2D {

    public static ObservableList<ObservableList<Object>> convertFrom2DList(List<List<Object>> input){
        ObservableList<ObservableList<Object>> rows = FXCollections.observableArrayList();

        for(List<Object> i: input){
            rows.add(FXCollections.observableArrayList(i));
        }

        return rows;
    }

    public static ObservableList<ObservableList<Object>> convertFromDataSet(DataSet input){
        List<List<Object>> rows = new ArrayList<>();

        for(Row r: input.toRows()){
            List<Object> row = new ArrayList<>();
            for(int i = 0; i < r.size(); i++){//might crash because of size()
                row.add(r.getValue(i));
            }
            rows.add(row);
        }

        return convertFrom2DList(rows);
    }

    public static ObservableList<ObservableList<Object>> convertFromResultSet(ResultSet input){
        List<List<Object>> rows = new ArrayList<>();

        try {
            while(input.next()){
                List<Object> row = new ArrayList<>();
                for(int i = 1; i <= input.getMetaData().getColumnCount(); i++){
                    row.add(input.getString(i));
                }
                rows.add(row);
            }
        } catch (SQLException e) {
            new ErrorHandler(e);
        }

        return convertFrom2DList(rows);
    }
}
