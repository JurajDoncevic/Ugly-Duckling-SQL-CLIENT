package com.juraj.sqlclient.tableManagement;

import org.apache.metamodel.DataContext;

import java.util.List;

/**
 * Created by Juraj on 10.1.2018..
 */
public interface TableDataManager {
    List<List<Object>> fetchAllRows();

    DataContext getDataContext();

    String getTableName();

    String getSchemaName();
}
