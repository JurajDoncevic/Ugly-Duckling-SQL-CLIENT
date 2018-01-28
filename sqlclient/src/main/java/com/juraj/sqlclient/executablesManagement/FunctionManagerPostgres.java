package com.juraj.sqlclient.executablesManagement;

import com.juraj.sqlclient.utils.ErrorHandler;
import org.apache.metamodel.jdbc.JdbcDataContext;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Juraj on 10.12.2017..
 */

public class FunctionManagerPostgres implements FunctionManager {

    private JdbcDataContext dataContext;
    private String functionName;
    private String schemaName;

    public FunctionManagerPostgres(String schemaName, String functionName, JdbcDataContext dataContext) throws Exception{

        this.schemaName = schemaName;
        this.functionName = functionName;
        this.dataContext = dataContext;

        validatePath();

    }

    private void validatePath() throws Exception{
        List<Function> functions = new ArrayList<>();

        Connection raw_connection = dataContext.getConnection();

        Statement statement = null;
        int count = 0;
        String functionsQuery = "SELECT COUNT(*) " +
                "FROM information_schema.routines " +
                String.format("WHERE routine_schema='%s' AND routine_name='%s';", schemaName, functionName);

        try {
            statement = raw_connection.createStatement();

            ResultSet resultSet = statement.executeQuery(functionsQuery);

            while (resultSet.next()){

                count = resultSet.getInt(1);

            }

        } catch (SQLException e) {
            new ErrorHandler(e);
        }
        if(count == 0)
            throw new Exception("No such function: " + schemaName + "." + functionName);

    }

    @Override
    public Function getFunction(){
        Function function = null;

        Connection raw_connection = dataContext.getConnection();

        Statement statement = null;

        String functionQuery =
                "SELECT p.proname AS fname, pg_get_functiondef(p.oid) AS definition " +
                        "FROM pg_proc p, pg_namespace n " +
                        "WHERE n.oid=p.pronamespace " +
                        String.format("AND n.nspname='%s' AND p.proname='%s' AND p.proisagg <> true;", schemaName, functionName);


        try {
            statement = raw_connection.createStatement();

            ResultSet resultSet = statement.executeQuery(functionQuery);

            while (resultSet.next()){

                //String name = resultSet.getString("fname");
                String definition = resultSet.getString("definition");
                function = new Function(schemaName, functionName, definition);
            }

        } catch (SQLException e) {
            new ErrorHandler(e);
        }

        return function;
    }

    @Override
    public JdbcDataContext getDataContext() {
        return dataContext;
    }

    @Override
    public String getFunctionName() {
        return functionName;
    }

    @Override
    public String getSchemaName() {
        return schemaName;
    }

}
