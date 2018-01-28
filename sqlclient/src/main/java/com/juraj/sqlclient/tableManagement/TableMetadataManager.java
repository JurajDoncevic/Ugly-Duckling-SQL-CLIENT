package com.juraj.sqlclient.tableManagement;

import org.apache.metamodel.jdbc.JdbcDataContext;
import org.apache.metamodel.schema.Column;
import org.apache.metamodel.schema.Relationship;

import java.util.List;

/**
 * Created by Juraj on 10.1.2018..
 */
public interface TableMetadataManager {
    List<Relationship> getRelationships();

    List<Column> getPrimaryKeys();

    List<Column> getForeignKeys();

    List<Relationship> getPrimaryKeyRelationships();

    List<Relationship> getForeignKeyRelationships();

    List<String> getColumnNames();

    List<Column> getColumns();

    int getColumnCount();

    String getDefinition();

    JdbcDataContext getDataContext();

    String getTableName();

    String getSchemaName();
}
