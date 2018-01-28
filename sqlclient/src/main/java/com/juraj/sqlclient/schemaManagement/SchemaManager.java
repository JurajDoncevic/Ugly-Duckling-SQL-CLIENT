package com.juraj.sqlclient.schemaManagement;

import com.juraj.sqlclient.executablesManagement.Function;
import com.mindfusion.diagramming.DiagramView;
import org.apache.metamodel.DataContext;
import org.apache.metamodel.schema.Relationship;
import org.apache.metamodel.schema.Table;

import java.util.List;

/**
 * Created by Juraj on 10.1.2018..
 */
public interface SchemaManager {

    String getSchemaName();

    DataContext getDataContext();

    List<Table> getTables();

    List<Relationship> getRelationships();

    List<Function> getFunctions();

    List<Table> getViews();
}
