package com.juraj.sqlclient.connectionManagement;


import org.apache.metamodel.factory.DataContextPropertiesImpl;
import org.apache.metamodel.jdbc.JdbcDataContext;
import org.apache.metamodel.jdbc.JdbcDataContextFactory;

/**
 * Created by Juraj on 31.10.2017..
 */
public class ConnectionManagerPostgres implements ConnectionManager {

    private String url;
    private String connectionType;
    private String username;
    private String password;
    private String driverClass;
    private DataContextPropertiesImpl properties;
    private JdbcDataContext dataContext;

    protected ConnectionManagerPostgres(String url, String connectionType, String username, String password, String driverClass) throws Exception{

        this.url = url;
        this.connectionType = connectionType;
        this.username = username;
        this.password = password;
        this.driverClass = driverClass;



        createDataContext();


    }

    @Override
    public void refreshProperties(){
        properties = new DataContextPropertiesImpl();
        properties.put("type", connectionType);
        properties.put("url", url);
        properties.put("username", username);
        properties.put("password", password);
        properties.put("driver-class", driverClass);
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String getConnectionType() {
        return connectionType;
    }

    @Override
    public void setConnectionType(String connectionType) {
        this.connectionType = connectionType;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getDriverClass() {
        return driverClass;
    }

    @Override
    public void setDriverClass(String driverClass) {
        this.driverClass = driverClass;
    }

    protected void createDataContext() throws Exception{
        refreshProperties();
        //dataContext = DataContextFactoryRegistryImpl.getDefaultInstance().createDataContext(properties);
        dataContext = (JdbcDataContext) (new JdbcDataContextFactory()).create(properties, null);
    }

    @Override
    public JdbcDataContext getDataContext() throws Exception{
        if(dataContext == null){
            createDataContext();
        }
        return dataContext;
    }

    @Override
    public DataContextPropertiesImpl getProperties() {
        return properties;
    }
}
