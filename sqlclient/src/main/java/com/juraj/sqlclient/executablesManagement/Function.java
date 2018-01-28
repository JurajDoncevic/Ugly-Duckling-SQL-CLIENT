package com.juraj.sqlclient.executablesManagement;

/**
 * Created by Juraj on 10.12.2017..
 */
//extension to the apache metadatamodel
public class Function {

    private String functionName;
    private String definition;
    private String schemaName;


    public Function(String schemaName, String functionName, String definition){
        this.functionName = functionName;
        this.schemaName = schemaName;
        this.definition = definition;
    }

    public String getFunctionName() {
        return functionName;
    }

    public String getSchemaName(){ return schemaName; }

    public String getDefinition() {
        return definition;
    }

}
