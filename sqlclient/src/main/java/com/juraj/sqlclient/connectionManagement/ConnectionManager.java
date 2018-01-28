package com.juraj.sqlclient.connectionManagement;

import org.apache.metamodel.factory.DataContextPropertiesImpl;
import org.apache.metamodel.jdbc.JdbcDataContext;

/**
 * Created by Juraj on 10.1.2018..
 */
public interface ConnectionManager {
    void refreshProperties();

    String getUrl();

    void setUrl(String url);

    String getConnectionType();

    void setConnectionType(String connectionType);

    String getUsername();

    void setUsername(String username);

    String getPassword();

    void setPassword(String password);

    String getDriverClass();

    void setDriverClass(String driverClass);

    JdbcDataContext getDataContext() throws Exception;

    DataContextPropertiesImpl getProperties();
}
