package com.juraj.sqlclient.controllers;

import com.juraj.sqlclient.controllers.TreeItems.*;
import com.juraj.sqlclient.connectionManagement.ConnectionFactory;
import com.juraj.sqlclient.connectionManagement.ConnectionManager;
import com.juraj.sqlclient.connectionManagement.DBCredentials;
import com.juraj.sqlclient.executablesManagement.Function;
import com.juraj.sqlclient.executablesManagement.FunctionManager;
import com.juraj.sqlclient.executablesManagement.FunctionManagerPostgres;
import com.juraj.sqlclient.queryManagement.QueryManager;
import com.juraj.sqlclient.queryManagement.QueryManagerPostgres;
import com.juraj.sqlclient.schemaManagement.SchemaManager;
import com.juraj.sqlclient.schemaManagement.SchemaManagerPostgres;
import com.juraj.sqlclient.tableManagement.TableMetadataManager;
import com.juraj.sqlclient.tableManagement.TableMetadataManagerPostgres;
import com.juraj.sqlclient.utils.DiagramFactory;
import com.juraj.sqlclient.utils.ErrorHandler;
import com.juraj.sqlclient.utils.ToObservableList2D;
import com.juraj.sqlclient.viewManagement.ViewManager;
import com.juraj.sqlclient.viewManagement.ViewManagerPostgres;
import com.mindfusion.diagramming.DiagramView;
import com.mindfusion.diagramming.ZoomControl;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.apache.metamodel.data.DataSet;
import org.apache.metamodel.jdbc.JdbcDataContext;
import org.apache.metamodel.schema.Column;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Juraj on 16.11.2017..
 */
public class MainWindowController {

    private ConnectionManager connectionManager;
    private JdbcDataContext dataContext;


    @FXML
    private TreeView trv_database;

    @FXML
    private MenuBar mbr_mainMenu;

    @FXML
    private TabPane tbp_tabs;

    @FXML
    private TableView tbv_results;

    @FXML
    private Button btn_execute;

    @FXML
    private Label lbl_queryExecutionTime;

    //used by treeView
    private String dbName;
    private TreeItem<String> dbItem;
    private Map<String, TreeItem<String>> schemaItems;
    private Map<String, TreeItem<String>> functionItems;
    private Map<String, TreeItem<String>> tableItems;
    private Map<String, TreeItem<String>> viewItems;

    private TreeItem<String> tablesBranch;
    private TreeItem<String> viewsBranch;
    private TreeItem<String> functionsBranch;

    private Map<String, Tab> tabs;

    private int tabNumber = 0;
    private long queryExecutionTime = 0;

    public MainWindowController(DBCredentials credentials){
        dbName = credentials.getDbName();
        tabs = new HashMap<>();
        try {
            connectionManager = ConnectionFactory.createPostgresManager(
                    credentials.getServerURL(),
                    credentials.getDbName(),
                    credentials.getUsername(),
                    credentials.getPassword()
            );
            dataContext = connectionManager.getDataContext();

        } catch (Exception e) {
            new ErrorHandler(e);
            System.exit(-1);
        }
    }

    @FXML
    public void initialize(){

        initTreeView();
        setTabSelectionListening();

    }

    //if a query tab is selected then enable execute button; else disable it; if there are no tabs left then disable
    //same for execution time label
    private void setTabSelectionListening() {
        tbp_tabs.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {

            @Override
            public void changed(ObservableValue<? extends Tab> observable, Tab oldTab, Tab newTab) {
                if(newTab == null){
                    btn_execute.setDisable(true);
                    lbl_queryExecutionTime.setText("");
                    lbl_queryExecutionTime.setVisible(false);
                }
                else if(newTab.getText().contains("Query on ")) {
                    btn_execute.setDisable(false);
                    lbl_queryExecutionTime.setVisible(true);
                }else {
                    btn_execute.setDisable(true);
                    lbl_queryExecutionTime.setVisible(false);
                }
            }
        });
    }

    private void addQueryTab(String schemaName) {
        Tab queryTab = new Tab("Query on " + schemaName);

        TextArea txa_query = new TextArea();


        AnchorPane.setTopAnchor(txa_query, 0.0);
        AnchorPane.setBottomAnchor(txa_query, 0.0);
        AnchorPane.setRightAnchor(txa_query, 0.0);
        AnchorPane.setLeftAnchor(txa_query, 0.0);


        queryTab.setContent(txa_query);

        tabs.put("queryTab"+tabNumber++, queryTab);
        tbp_tabs.getTabs().add(queryTab);

    }

    private void setResultsTableFromRaw(ResultSet resultSet){
        tbv_results.getItems().clear();
        tbv_results.getColumns().clear();
        tbv_results.refresh();


        tbv_results.setEditable(true);

        int columnNumber = 0;
        List<String> columnNames = new ArrayList<>();
        try {
            columnNumber = resultSet.getMetaData().getColumnCount();
            for(int i = 1; i <= columnNumber; i++){
                columnNames.add(resultSet.getMetaData().getColumnName(i));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ObservableList<ObservableList<Object>> data;

        int i = 0;
        for(String columnName: columnNames){
            final int j = i;
            TableColumn _column = new TableColumn(columnName);

            _column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList,String>,ObservableValue<String>>() {

                public ObservableValue call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                    return new SimpleStringProperty(String.valueOf(param.getValue().get(j)));
                }
            });
            i++;
            tbv_results.getColumns().addAll(_column);
        }

        data = ToObservableList2D.convertFromResultSet(resultSet);

        tbv_results.setItems(data);

        tbv_results.refresh();

    }

    private void setResultsTable(DataSet dataSet){

        tbv_results.getItems().clear();
        tbv_results.getColumns().clear();
        tbv_results.refresh();


        tbv_results.setEditable(true);

        ObservableList<ObservableList<Object>> data;

        java.util.List<Column> columns = Arrays.asList(dataSet.getSelectItems()).stream().map(x->x.getColumn())
                .collect(Collectors.toList());

        int i = 0;
        for(Column column: columns){
            final int j = i;
            TableColumn _column = new TableColumn(column.getName());

            _column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList,String>,ObservableValue<String>>() {

                public ObservableValue call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                    return new SimpleStringProperty(String.valueOf(param.getValue().get(j)));
                }
            });
            i++;
            tbv_results.getColumns().addAll(_column);
        }

        data = ToObservableList2D.convertFromDataSet(dataSet);

        tbv_results.setItems(data);

        tbv_results.refresh();
    }

    private void initTableDiagramTab(String schemaName){
        try {

            DiagramFactory diagramFactory = new DiagramFactory(dataContext, schemaName);
            DiagramView diagramView = diagramFactory.getTableDiagramView();

            diagramView.setVisible(true);
            diagramView.setAllowInplaceEdit(false);

            //TODO: zoomer does not appear bcs it is not a javafx comp; problems placing it
            //provide a zoomer for the diagram
            ZoomControl zoomer = new ZoomControl();
            zoomer.setView(diagramView);
            zoomer.setPreferredSize(new Dimension(70, 50));
            zoomer.setVisible(true);




            //use a scroll pane to host large diagrams
            JScrollPane _scrollPane = new JScrollPane(diagramView);
            _scrollPane.setVisible(true);
            _scrollPane.setAutoscrolls(true);

            final SwingNode swingNode = new SwingNode();

            createSwingContent(swingNode, _scrollPane);

            AnchorPane.setTopAnchor(swingNode, 0.0);
            AnchorPane.setBottomAnchor(swingNode, 0.0);
            AnchorPane.setRightAnchor(swingNode, 0.0);
            AnchorPane.setLeftAnchor(swingNode, 0.0);


            Tab diagramTab = new Tab("Tables diagram of " + schemaName);
            tabs.put("tableDiagramTab" + tabNumber++, diagramTab);
            diagramTab.setContent(swingNode);

            tbp_tabs.getTabs().add(diagramTab);

        } catch (Exception e) {
            new ErrorHandler(e);
        }
    }

    private void initViewDiagramTab(String schemaName){
        try {

            DiagramFactory diagramFactory = new DiagramFactory(dataContext, schemaName);
            DiagramView diagramView = diagramFactory.getViewDiagramView();

            diagramView.setVisible(true);
            diagramView.setAllowInplaceEdit(false);

            //TODO: zoomer does not appear bcs it is not a javafx comp; problems placing it
            //provide a zoomer for the diagram
            ZoomControl zoomer = new ZoomControl();
            zoomer.setView(diagramView);
            zoomer.setPreferredSize(new Dimension(70, 50));
            zoomer.setVisible(true);




            //use a scroll pane to host large diagrams
            JScrollPane _scrollPane = new JScrollPane(diagramView);
            _scrollPane.setVisible(true);
            _scrollPane.setAutoscrolls(true);

            final SwingNode swingNode = new SwingNode();

            createSwingContent(swingNode, _scrollPane);

            AnchorPane.setTopAnchor(swingNode, 0.0);
            AnchorPane.setBottomAnchor(swingNode, 0.0);
            AnchorPane.setRightAnchor(swingNode, 0.0);
            AnchorPane.setLeftAnchor(swingNode, 0.0);


            Tab diagramTab = new Tab("Views diagram of " + schemaName);
            tabs.put("viewDiagramTab" + tabNumber++, diagramTab);
            diagramTab.setContent(swingNode);

            tbp_tabs.getTabs().add(diagramTab);

        } catch (Exception e) {
            new ErrorHandler(e);
        }
    }

    private void initTreeView(){
        //set DB item
        ImageView dbImage = new ImageView(new Image(getClass().getResourceAsStream("/database.png")));
        dbItem = new DBTreeItem<String> (dbName, dbImage);


        //set schema items
        schemaItems = new HashMap<>();
        Arrays.asList(dataContext.getSchemaNames()).forEach(sch -> {
            ImageView schemaImage = new ImageView(new Image(getClass().getResourceAsStream("/schema.png")));
            SchemaTreeItem<String> item = new SchemaTreeItem<>(sch, schemaImage, this::initTableDiagramTab, this::addQueryTab, this::initViewDiagramTab);
            schemaItems.put(sch, item);
        });


        //foreach schema set tables into schema item
        //should use schema manager

        tableItems = new HashMap<>();
        schemaItems.forEach((k,v) ->{
            tablesBranch = new TreeItem<>("Tables", new ImageView(new Image(getClass().getResourceAsStream("/tables.png"))));
            try {
                (new SchemaManagerPostgres(dataContext, k)).getTables().forEach(t->{
                    ImageView tableImage = new ImageView(new Image(getClass().getResourceAsStream("/table.png")));
                    tableItems.put(k + "." + t, new TableTreeItem<>(t.getSchema().getName(), t.getName(), tableImage, this::showTop100InWindow, this::showAllInWindow, this::showTableDefinition));
                    tablesBranch.getChildren().add(tableItems.get(k+"."+t));
                });
            } catch (Exception e) {
                new ErrorHandler(e);
            }
            schemaItems.get(k).getChildren().add(tablesBranch);
        });


        functionItems = new HashMap<>();
        schemaItems.forEach((k,v) -> {
            functionsBranch = new TreeItem<>("Functions & Procedures", new ImageView((new Image(getClass().getResourceAsStream("/functions.png")))));
            try {
                (new SchemaManagerPostgres(dataContext, k)).getFunctions().forEach(f->{
                    ImageView functionImage = new ImageView((new Image(getClass().getResourceAsStream("/function.png"))));
                    functionItems.put(k+"."+f.getFunctionName(), new FunctionTreeItem<>(f.getSchemaName(), f.getFunctionName(), functionImage, this::showFunctionDefinitionOverviewWindow));
                    functionsBranch.getChildren().add(functionItems.get(k+"."+f.getFunctionName()));
                });
            } catch (Exception e) {
                new ErrorHandler(e);
            }
            schemaItems.get(k).getChildren().add(functionsBranch);
        });


        viewItems = new HashMap<>();
        schemaItems.forEach((k,v) ->{
            viewsBranch = new TreeItem<>("Views", new ImageView((new Image(getClass().getResourceAsStream("/views.png")))));
            try {
                (new SchemaManagerPostgres(dataContext, k)).getViews().forEach(view->{
                    ImageView viewImage = new ImageView((new Image(getClass().getResourceAsStream("/view.png"))));
                    viewItems.put(k+"."+view.getName(), new ViewTreeItem<>(view.getSchema().getName(), view.getName(), viewImage, this::showTop100InWindow, this::showAllInWindow, this::showViewDefinition));
                    viewsBranch.getChildren().add(viewItems.get(k+"."+view.getName()));
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            schemaItems.get(k).getChildren().add(viewsBranch);
        });

        //set the schema items into db item
        schemaItems.forEach((k,v) ->{
            dbItem.getChildren().add(v);
        });


        trv_database.setRoot(dbItem);

        trv_database.setOnContextMenuRequested(e ->{


            if(SchemaTreeItem.class.isInstance(trv_database.getSelectionModel().getSelectedItem())){
                ((SchemaTreeItem)trv_database.getSelectionModel().getSelectedItem()).getContextMenu()
                        .show(trv_database, e.getScreenX(), e.getScreenY());
            }
            if(TableTreeItem.class.isInstance(trv_database.getSelectionModel().getSelectedItem())){
                ((TableTreeItem)trv_database.getSelectionModel().getSelectedItem()).getContextMenu()
                        .show(trv_database, e.getScreenX(), e.getScreenY());
            }
            if(DBTreeItem.class.isInstance(trv_database.getSelectionModel().getSelectedItem())){
                ((DBTreeItem)trv_database.getSelectionModel().getSelectedItem()).getContextMenu()
                        .show(trv_database, e.getScreenX(), e.getScreenY());
            }
            if(ViewTreeItem.class.isInstance(trv_database.getSelectionModel().getSelectedItem())){
                ((ViewTreeItem)trv_database.getSelectionModel().getSelectedItem()).getContextMenu()
                        .show(trv_database, e.getScreenX(), e.getScreenY());
            }
            if(FunctionTreeItem.class.isInstance(trv_database.getSelectionModel().getSelectedItem())){
                ((FunctionTreeItem)trv_database.getSelectionModel().getSelectedItem()).getContextMenu()
                        .show(trv_database, e.getScreenX(), e.getScreenY());
            }
        });

        trv_database.refresh();
    }

    private void createSwingContent(final SwingNode swingNode, JComponent graphComponent) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                swingNode.setContent(graphComponent);
            }
        });
    }

    //also used for views
    private void showTop100InWindow(String schemaName, String tableName){

        //run the query
        DataSet result = dataContext.query().from(schemaName, tableName)
                                            .select("*")
                                            .limit(100)
                                            .execute();

        Parent root;
        Stage tableViewStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/TableViewWindow.fxml"));
        fxmlLoader.setController(new TableViewController(result));
        try {
            root = fxmlLoader.load();
            tableViewStage.setTitle("Top 100 Of " + schemaName + "." + tableName);
            tableViewStage.setScene(new Scene(root));

        } catch (IOException e) {
            new ErrorHandler(e);
        }


        tableViewStage.showAndWait();

        //setResultsTable(result);

    }

    //also used for views
    private void showAllInWindow(String schemaName, String tableName){
        //run the query
        DataSet result = dataContext.query().from(schemaName, tableName)
                .select("*")
                .execute();


        Parent root;
        Stage tableViewStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/TableViewWindow.fxml"));
        fxmlLoader.setController(new TableViewController(result));
        try {
            root = fxmlLoader.load();
            tableViewStage.setTitle("All Data Of "  + schemaName + "." + tableName);
            tableViewStage.setScene(new Scene(root));

        } catch (IOException e) {
            new ErrorHandler(e);
        }


        tableViewStage.showAndWait();

        //setResultsTable(result);
    }

    private void showFunctionDefinitionOverviewWindow(String schemaName, String functionName){
        try {
            FunctionManager fm = new FunctionManagerPostgres(schemaName, functionName, dataContext);

            Function func = fm.getFunction();

            showDefinitionOverviewWindow(func.getSchemaName()+"."+func.getFunctionName(), func.getDefinition());

        } catch (Exception e) {
            new ErrorHandler(e);
        }
    }

    private void showDefinitionOverviewWindow(String elementName, String definition){
        Parent root;
        Stage tableViewStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/DefinitionOverviewWindow.fxml"));
        fxmlLoader.setController(new DefinitionOverviewController(definition));
        try {
            root = fxmlLoader.load();
            tableViewStage.setTitle("Definition Of " + elementName);
            tableViewStage.setScene(new Scene(root));

        } catch (IOException e) {
            new ErrorHandler(e);
        }


        tableViewStage.showAndWait();
    }

    private void showTableDefinition(String schemaName, String tableName){

        try {
            TableMetadataManager tm = new TableMetadataManagerPostgres(dataContext, schemaName, tableName);
            String definition = tm.getDefinition();

            showDefinitionOverviewWindow(schemaName+"."+tableName, definition);

        } catch (Exception e) {
            new ErrorHandler(e);
        }
    }

    private void showViewDefinition(String schemaName, String viewName){
        try {
            ViewManager vm = new ViewManagerPostgres(dataContext, schemaName, viewName);
            String definition = vm.getDefinition();

            showDefinitionOverviewWindow(schemaName+"."+viewName, definition);

        } catch (Exception e) {
            new ErrorHandler(e);
        }
    }

    @FXML
    private void click_btn_executeRaw(MouseEvent me){


        TextArea txa = (TextArea) tbp_tabs.getSelectionModel().getSelectedItem().getContent();

        String query = txa.getText().trim();
        QueryManager queryManager = new QueryManagerPostgres(dataContext);

        try {
            long startTime = System.nanoTime();
            ResultSet resultSet = queryManager.executeQueryRaw(query);
            long stopTime = System.nanoTime();

            queryExecutionTime = (stopTime - startTime)/1000000;

            lbl_queryExecutionTime.setText("Query Execution Time: " + queryExecutionTime + " ms");

            setResultsTableFromRaw(resultSet);
        } catch (Exception e) {
            new ErrorHandler(e);

        }

    }

    @FXML
    private void click_btn_execute(MouseEvent me){


        TextArea txa = (TextArea) tbp_tabs.getSelectionModel().getSelectedItem().getContent();

        String query = txa.getText().trim().replace(";", "");
        QueryManager queryManager = new QueryManagerPostgres(dataContext);

        try {
            DataSet ds = queryManager.executeQuery(query);
            setResultsTable(ds);
        } catch (Exception e) {
            new ErrorHandler(e);

        }

    }

}
