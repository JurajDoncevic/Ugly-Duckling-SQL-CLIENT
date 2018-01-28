package com.juraj.sqlclient.schemaManagement;

import com.juraj.sqlclient.executablesManagement.Function;

import com.juraj.sqlclient.utils.ErrorHandler;
import com.mindfusion.diagramming.*;
import com.mindfusion.diagramming.LayeredLayout;
import com.mindfusion.diagramming.jlayout.Orientation;
import com.mindfusion.drawing.Align;
import com.mindfusion.drawing.SolidBrush;
import com.mindfusion.drawing.TextFormat;

import org.apache.metamodel.DataContext;
import org.apache.metamodel.jdbc.JdbcDataContext;
import org.apache.metamodel.schema.Column;
import org.apache.metamodel.schema.Relationship;
import org.apache.metamodel.schema.Table;
import org.apache.metamodel.schema.TableType;


import javax.imageio.ImageIO;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.List;


/**
 * Created by Juraj on 31.10.2017..
 */

//used to get data about a schema and it's contents
//TODO: get functions and procedures
public class SchemaManagerPostgres implements SchemaManager {

    private JdbcDataContext dataContext;
    private String schemaName;

    public SchemaManagerPostgres(JdbcDataContext dataContext, String schemaName) throws Exception{
        this.dataContext = dataContext;
        this.schemaName = schemaName;

        validatePath();

    }

    private boolean validatePath() throws Exception{
        if (Arrays.asList(dataContext.getSchemaNames()).contains(schemaName))
            return true;
        else
            throw new Exception("No such schema found");
    }

    @Override
    public String getSchemaName() {
        return schemaName;
    }

    @Override
    public DataContext getDataContext() {
        return dataContext;
    }

    @Override
    public List<Table> getTables(){

        List<Table> tables = Arrays.asList(dataContext.getSchemaByName(schemaName).getTables(TableType.TABLE));
        return tables;

    }

    @Override
    public List<Relationship> getRelationships(){
        List<Relationship> relationships = Arrays.asList(dataContext.getSchemaByName(schemaName).getRelationships());

        return relationships;
    }

    @Override
    public List<Function> getFunctions(){

        List<Function> functions = new ArrayList<>();

        Connection raw_connection = dataContext.getConnection();

        Statement statement = null;

        String functionsQuery =
                "SELECT p.proname AS fname, pg_get_functiondef(p.oid) AS definition " +
                        "FROM pg_proc p, pg_namespace n " +
                        "WHERE n.oid=p.pronamespace " +
                        String.format("AND n.nspname='%s' AND p.proisagg <> true;", schemaName);


        try {
            statement = raw_connection.createStatement();

            ResultSet resultSet = statement.executeQuery(functionsQuery);

            while (resultSet.next()){

                String name = resultSet.getString("fname");
                String definition = resultSet.getString("definition");
                functions.add(new Function(schemaName, name, definition));
            }

        } catch (SQLException e) {
            new ErrorHandler(e);
        }

        return functions;
    }

    @Override
    public List<Table> getViews(){

        List<Table> views = Arrays.asList(dataContext.getSchemaByName(schemaName).getTables(TableType.VIEW));
        return views;
    }



}
