package com.juraj.sqlclient.queryManagement;

import com.juraj.sqlclient.utils.ErrorHandler;
import org.apache.metamodel.data.DataSet;
import org.apache.metamodel.jdbc.JdbcDataContext;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Juraj on 31.10.2017..
 */

//used to send specialized queries to DB
public class QueryManagerPostgres implements QueryManager {

    private String queryText;
    private JdbcDataContext dataContext;

    public QueryManagerPostgres(JdbcDataContext dataContext){
        this.dataContext = dataContext;
    }

    @Override
    public DataSet executeQuery(String queryText) throws Exception{
        DataSet dataSet =
                dataContext.executeQuery(queryText);
        return dataSet;
    }

    @Override
    public ResultSet executeQueryRaw(String queryText){
        Connection raw_connection = dataContext.getConnection();

        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = raw_connection.createStatement();
            resultSet = statement.executeQuery(queryText);



        } catch (SQLException e) {
            new ErrorHandler(e);
        }

        return resultSet;
    }

    @Override
    public String getQueryText() {
        return queryText;
    }

    @Override
    public void setQueryText(String queryText) {
        this.queryText = queryText;
    }
}
