package com.juraj.sqlclient.tableManagement;

import org.apache.metamodel.DataContext;
import org.apache.metamodel.data.DataSet;
import org.apache.metamodel.data.Row;
import org.apache.metamodel.jdbc.JdbcDataContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Juraj on 31.10.2017..
 */

//Used to fetch data in a table
//TODO: add change data methods
public class TableDataManagerPostgres implements TableDataManager {

    private JdbcDataContext dataContext;
    private String tableName;
    private String schemaName;

    public TableDataManagerPostgres(JdbcDataContext dataContext, String schemaName, String tableName) throws Exception{
        this.dataContext = dataContext;
        this.tableName = tableName;
        this.schemaName = schemaName;

        validatePath();

    }

    private boolean validatePath() throws Exception{
        if (Arrays.asList(dataContext.getSchemaNames()).contains(schemaName))
            if(Arrays.asList(dataContext.getSchemaByName(schemaName).getTableNames()).contains(tableName))
                return true;
            else
                throw new Exception("No such table found");
        else
            throw new Exception("No such schema found");
    }


    @Override
    public List<List<Object>> fetchAllRows(){
        List<List<Object>> rows = new ArrayList<>();

        DataSet dataSet = dataContext.query()
                .from(tableName)
                .select("*").execute();

        for(Row r: dataSet.toRows()){
            List<Object> row = new ArrayList<>();
            for(int i = 0; i < r.size(); i++){//might crash because of size()
                row.add(r.getValue(i));
            }
            rows.add(row);
        }

        return rows;
    }

    @Override
    public DataContext getDataContext() {
        return dataContext;
    }

    @Override
    public String getTableName() {
        return tableName;
    }

    @Override
    public String getSchemaName() {
        return schemaName;
    }
}
