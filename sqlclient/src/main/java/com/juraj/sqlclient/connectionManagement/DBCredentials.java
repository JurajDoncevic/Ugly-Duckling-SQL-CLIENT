package com.juraj.sqlclient.connectionManagement;

/**
 * Created by Juraj on 10.1.2018..
 */
public interface DBCredentials {
    boolean isValid();

    String getServerURL();

    String getDbName();

    String getUsername();

    String getPassword();
}
