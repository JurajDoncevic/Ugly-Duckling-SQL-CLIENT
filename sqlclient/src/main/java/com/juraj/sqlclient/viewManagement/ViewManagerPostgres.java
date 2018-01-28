package com.juraj.sqlclient.viewManagement;

import com.juraj.sqlclient.utils.ErrorHandler;
import org.apache.metamodel.jdbc.JdbcDataContext;
import org.apache.metamodel.schema.Table;
import org.apache.metamodel.schema.TableType;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Created by Juraj on 2.1.2018..
 */
public class ViewManagerPostgres implements ViewManager {

    private String viewName;
    private String schemaName;
    private JdbcDataContext dataContext;

    public ViewManagerPostgres(JdbcDataContext dataContext, String schemaName, String viewName) throws Exception{
        this.dataContext = dataContext;
        this.viewName = viewName;
        this.schemaName = schemaName;

        validatePath();

    }

    private boolean validatePath() throws Exception{
        if (Arrays.asList(dataContext.getSchemaNames()).contains(schemaName))
            if(Arrays.asList(dataContext.getSchemaByName(schemaName).getTables(TableType.VIEW)).stream().map(Table::getName).collect(Collectors.toList()).contains(viewName))
                return true;
            else
                throw new Exception("No such table found");
        else
            throw new Exception("No such schema found");
    }

    @Override
    public String getDefinition(){
        String definition = null;

        Connection raw_connection = dataContext.getConnection();

        Statement statement = null;

        String delimiter = "\n";

        String tableDefinitionQuery =
                String.format("SELECT 'CREATE VIEW ' || '%2$s' || ' (' || '%3$s' || '' || " +
                        "    string_agg(column_list.column_expr, ', ' || '%3$s' || '') || " +
                        "    '' || '%3$s' || ');' " +
                        "FROM ( " +
                        "  SELECT '    ' || column_name || ' ' || data_type ||  " +
                        "       coalesce('(' || character_maximum_length || ')', '') ||  " +
                        "       case when is_nullable = 'YES' then '' else ' NOT NULL' end as column_expr " +
                        "  FROM information_schema.columns " +
                        "  WHERE table_schema = '%1$s' AND table_name = '%2$s' " +
                        "  ORDER BY ordinal_position) column_list;", schemaName, viewName, delimiter);


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
    public Table getView(){
        return dataContext.getSchemaByName(schemaName).getTableByName(viewName);
    }

    @Override
    public String getViewName() {
        return viewName;
    }

    @Override
    public String getSchemaName() {
        return schemaName;
    }

    @Override
    public JdbcDataContext getDataContext() {
        return dataContext;
    }
}
