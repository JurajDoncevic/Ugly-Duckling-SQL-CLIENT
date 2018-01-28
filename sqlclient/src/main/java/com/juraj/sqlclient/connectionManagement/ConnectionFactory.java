package com.juraj.sqlclient.connectionManagement;

/**
 * Created by Juraj on 12.11.2017..
 */


public class ConnectionFactory {

    public static ConnectionManagerPostgres createPostgresManager(String serverAddress, String dbName,
                                                                  String username, String password) throws Exception{

        String url = String.format("jdbc:postgresql://%s/%s", serverAddress, dbName);

        ConnectionManagerPostgres connectionManager = new ConnectionManagerPostgres(
                    url,
                    "jdbc",
                    username,
                    password,
                    "org.postgres.Driver"
            );


        return connectionManager;
    }
}
