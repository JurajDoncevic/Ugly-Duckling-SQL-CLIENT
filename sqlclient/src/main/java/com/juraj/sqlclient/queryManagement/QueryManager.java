package com.juraj.sqlclient.queryManagement;

import org.apache.metamodel.data.DataSet;

import java.sql.ResultSet;

/**
 * Created by Juraj on 10.1.2018..
 */
public interface QueryManager {
    DataSet executeQuery(String queryText) throws Exception;

    ResultSet executeQueryRaw(String queryText);

    String getQueryText();

    void setQueryText(String queryText);
}
