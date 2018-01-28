package com.juraj.sqlclient.tableManagement;

import com.juraj.sqlclient.utils.ErrorHandler;
import org.apache.metamodel.jdbc.JdbcDataContext;
import org.apache.metamodel.schema.Column;
import org.apache.metamodel.schema.Relationship;
import org.apache.metamodel.schema.Table;
import org.apache.metamodel.schema.TableType;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Juraj on 31.10.2017..
 */

//used to get data about a table
public class TableMetadataManagerPostgres implements TableMetadataManager {
    private JdbcDataContext dataContext;
    private String tableName;
    private String schemaName;

    public TableMetadataManagerPostgres(JdbcDataContext dataContext, String schemaName, String tableName) throws Exception{
        this.dataContext = dataContext;
        this.tableName = tableName;
        this.schemaName = schemaName;

        validatePath();

    }

    private boolean validatePath() throws Exception{
        if (Arrays.asList(dataContext.getSchemaNames()).contains(schemaName))
            if(Arrays.asList(dataContext.getSchemaByName(schemaName).getTables(TableType.TABLE)).stream().map(Table::getName).collect(Collectors.toList()).contains(tableName))
                return true;
            else
                throw new Exception("No such table found");
        else
            throw new Exception("No such schema found");
    }

    @Override
    public List<Relationship> getRelationships(){
        return Arrays.asList(dataContext.getSchemaByName(schemaName)
                .getTableByName(tableName)
                .getRelationships());
    }

    @Override
    public List<Column> getPrimaryKeys(){
        return Arrays.asList(dataContext.getSchemaByName(schemaName)
                .getTableByName(tableName)
                .getPrimaryKeys());
    }

    @Override
    public List<Column> getForeignKeys(){
        return Arrays.asList(dataContext.getSchemaByName(schemaName)
                .getTableByName(tableName)
                .getForeignKeys());
    }

    @Override
    public List<Relationship> getPrimaryKeyRelationships(){
        return Arrays.asList(dataContext.getSchemaByName(schemaName)
                .getTableByName(tableName)
                .getPrimaryKeyRelationships());
    }

    @Override
    public List<Relationship> getForeignKeyRelationships(){
        return Arrays.asList(dataContext.getSchemaByName(schemaName)
                .getTableByName(tableName)
                .getForeignKeyRelationships());
    }

    @Override
    public List<String> getColumnNames(){
        return Arrays.asList(dataContext.getSchemaByName(schemaName)
                .getTableByName(tableName)
                .getColumnNames());
    }

    @Override
    public List<Column> getColumns(){
        return Arrays.asList(dataContext.getSchemaByName(schemaName)
                .getTableByName(tableName)
                .getColumns());
    }

    @Override
    public int getColumnCount(){
        return dataContext
                .getSchemaByName(schemaName)
                .getTableByName(tableName)
                .getColumnCount();
    }

    @Override
    public String getDefinition(){
        String definition = null;

        Connection raw_connection = dataContext.getConnection();

        Statement statement = null;

        String delimiter = "\n";

        String tableDefinitionQuery =
                String.format("SELECT 'CREATE TABLE ' || '%2$s' || ' (' || '%3$s' || '' || " +
                        "    string_agg(column_list.column_expr, ', ' || '%3$s' || '') || " +
                        "    '' || '%3$s' || ');' " +
                        "FROM ( " +
                        "  SELECT '    ' || column_name || ' ' || data_type ||  " +
                        "       coalesce('(' || character_maximum_length || ')', '') ||  " +
                        "       case when is_nullable = 'YES' then '' else ' NOT NULL' end as column_expr " +
                        "  FROM information_schema.columns " +
                        "  WHERE table_schema = '%1$s' AND table_name = '%2$s' " +
                        "  ORDER BY ordinal_position) column_list;", schemaName, tableName, delimiter);


        try {
            statement = raw_connection.createStatement();

            ResultSet resultSet = statement.executeQuery(tableDefinitionQuery);

            while (resultSet.next()){

                //String name = resultSet.getString("fname");
                definition = resultSet.getString(1);

            }

        } catch (SQLException e) {
            new ErrorHandler(e);
        }

        return definition;
    }

    @Override
    public JdbcDataContext getDataContext() {
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
