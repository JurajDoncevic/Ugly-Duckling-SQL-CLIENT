package com.juraj.sqlclient.executablesManagement;

import org.apache.metamodel.jdbc.JdbcDataContext;

/**
 * Created by Juraj on 10.1.2018..
 */
public interface FunctionManager {

    Function getFunction();

    JdbcDataContext getDataContext();

    String getFunctionName();

    String getSchemaName();
}
