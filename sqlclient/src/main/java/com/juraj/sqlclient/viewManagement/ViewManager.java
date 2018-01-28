package com.juraj.sqlclient.viewManagement;

import org.apache.metamodel.jdbc.JdbcDataContext;
import org.apache.metamodel.schema.Table;

/**
 * Created by Juraj on 10.1.2018..
 */
public interface ViewManager {
    String getDefinition();

    Table getView();

    String getViewName();

    String getSchemaName();

    JdbcDataContext getDataContext();
}
