package com.juraj.sqlclient.utils;

import com.mindfusion.diagramming.*;
import com.mindfusion.diagramming.Column;
import com.mindfusion.diagramming.LayeredLayout;
import com.mindfusion.diagramming.Orientation;
import com.mindfusion.diagramming.Relationship;
import com.mindfusion.diagramming.jlayout.*;
import com.mindfusion.drawing.Align;
import com.mindfusion.drawing.SolidBrush;
import com.mindfusion.drawing.TextFormat;
import org.apache.metamodel.jdbc.JdbcDataContext;
import org.apache.metamodel.schema.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by Juraj on 11.1.2018..
 */
public class DiagramFactory {

    private JdbcDataContext dataContext;
    private String schemaName;
    private Diagram tableDiagram;
    private Diagram viewDiagram;
    private DiagramView diagramView;

    public DiagramFactory(JdbcDataContext dataContext, String schemaName) throws Exception {
        validatePath(dataContext, schemaName);

        this.schemaName = schemaName;
        this.dataContext = dataContext;
    }

    public DiagramView getTableDiagramView(){

        generateTableDiagram();
        return diagramView;
    }

    private void generateTableDiagram(){
        tableDiagram = new Diagram();
        tableDiagram.setAutoResize(AutoResize.RightAndDown);

        //initialize a diagramView that will render the diagram.
        diagramView = new DiagramView(tableDiagram);
        diagramView.setVisible(true);


        //diagram settings
        tableDiagram.setTableColumnCount(4);
        tableDiagram.setTableRowHeight(10f);
        tableDiagram.setShadowsStyle(ShadowsStyle.None);
        tableDiagram.setEnableStyledText(true);

        //set tables
        for(Table table: dataContext.getSchemaByName(schemaName).getTables(TableType.TABLE)){

            Dimension tableSize = new Dimension(50, 30);
            TableNode _table = tableDiagram.getFactory()
                    .createTableNode(10, 10, 50, table.getColumnCount() * 8, 4, table.getColumnCount());

            _table.setCaption("<b>" + table.getName() + "</b>");
            _table.setId(table.getName());

            //set style
            _table.setCaptionFormat(new TextFormat(Align.Center, Align.Center));
            _table.setCaptionHeight(7f);
            _table.setRowHeight(10f);
            _table.setAllowResizeColumns(true);
            _table.getColumns().get(0).setWidth(22f);
            _table.setShape(SimpleShape.RoundedRectangle);
            _table.setBrush(new SolidBrush(new Color((int)153, (int)179, (int)255)));

            int rowIndex = 0;
            for(org.apache.metamodel.schema.Column column: table.getColumns()){
                _table.getCell(1, rowIndex).setText("<b>" + column.getName() + "</b>");
                _table.getCell(2, rowIndex).setText(column.getNativeType());
                _table.getCell(3, rowIndex).setText(column.getColumnSize().toString());


                //if the column is a primary key - set an image. If it's a foreign key - set and image
                if(column.isPrimaryKey())
                {
                    try {

                        Image image = ImageIO.read(getClass().getResourceAsStream("/pkey.png"));
                        _table.getCell(0,rowIndex).setImage(image);


                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }else if(Arrays.asList(table.getForeignKeys()).contains(column)){
                    try {

                        Image image = ImageIO.read(getClass().getResourceAsStream("/fkey.png"));
                        _table.getCell(0,rowIndex).setImage(image);


                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }

                rowIndex++;
            }
            //make everything fit
            _table.resizeToFitText(true);
            Rectangle2D.Float t_size = _table.getBounds();
            _table.getColumns().get(0).setWidth(7);
            _table.resize(t_size.width + 7, t_size.height);
            _table.resizeToFitImage();
        }


        //set relationships
        for(org.apache.metamodel.schema.Relationship relationship: dataContext.getSchemaByName(schemaName).getRelationships()){
            TableNode source = (TableNode)tableDiagram.findNodeById(relationship.getPrimaryTable().getName());
            TableNode destination = (TableNode)tableDiagram.findNodeById(relationship.getForeignTable().getName());

            if(source != null && destination != null) {
                int pk_index = -1;
                int fk_index = -1;

                int rowCount = source.getRowCount();

                for (int i = 0; i < rowCount; i++) {
                    Cell cell = source.getCell(1, i);
                    //TODO does not work for multi-relations
                    if (cell.getText().equals("<b>" + relationship.getPrimaryColumns()[0].getName() + "</b>")) {
                        pk_index = i;
                        break;
                    }
                }

                rowCount = destination.getRows().size();

                for (int i = 0; i < rowCount; i++) {
                    Cell cell = destination.getCell(1, i);
                    if (cell.getText().equals("<b>" + relationship.getForeignColumns()[0].getName() + "</b>")) {
                        fk_index = i;
                        break;
                    }
                }

                DiagramLink link = tableDiagram.getFactory().createDiagramLink(source, pk_index, destination, fk_index);
                link.setBaseShape(ArrowHeads.RevWithLine);
                link.setBaseShapeSize(3f);
                link.setHeadShapeSize(3f);
                link.setShape(LinkShape.Cascading);
            }
        }

        arrangeTableDiagram();

    }

    private void arrangeTableDiagram() {
        //use LayeredLayout with some initial customization
        LayeredLayout layout = new LayeredLayout(com.mindfusion.diagramming.jlayout.Orientation.Horizontal, 30f, 25f, 5f, 5f);
        layout.arrange(tableDiagram);

        //adjust link position
        for (DiagramLink link: tableDiagram.getLinks()) {

            if (link.getOrigin().getBounds().getX() < link.getDestination().getBounds().getX())
                link.setEndPoint(new Point2D.Float(link.getDestination().getBounds().x, link.getEndPoint().y));
        }
        //re-route all links
        tableDiagram.setLinkRouter(new GridRouter());
        tableDiagram.routeAllLinks();

        //resize the diagram after the layout to fit all items
        tableDiagram.resizeToFitItems(20);
        diagramView.scrollTo(tableDiagram.getBounds().width/2, 0);

        //customize the links
        tableDiagram.setLinkCrossings(LinkCrossings.Arcs);
        tableDiagram.setRoundedLinks(true);
        tableDiagram.setRoundedLinksRadius(3);
        //redraw the control
        tableDiagram.repaint();
    }

    public DiagramView getViewDiagramView(){

        generateViewDiagram();
        return diagramView;
    }

    private void generateViewDiagram(){
        viewDiagram = new Diagram();
        viewDiagram.setAutoResize(AutoResize.RightAndDown);

        //initialize a diagramView that will render the diagram.
        diagramView = new DiagramView(viewDiagram);
        diagramView.setVisible(true);


        //diagram settings
        viewDiagram.setTableColumnCount(4);
        viewDiagram.setTableRowHeight(10f);
        viewDiagram.setShadowsStyle(ShadowsStyle.None);
        viewDiagram.setEnableStyledText(true);

        //set tables
        for(Table table: dataContext.getSchemaByName(schemaName).getTables(TableType.VIEW)){

            Dimension tableSize = new Dimension(50, 30);
            TableNode _table = viewDiagram.getFactory()
                    .createTableNode(10, 10, 50, table.getColumnCount() * 8, 4, table.getColumnCount());

            _table.setCaption("<b>" + table.getName() + "</b>");
            _table.setId(table.getName());

            //set style
            _table.setCaptionFormat(new TextFormat(Align.Center, Align.Center));
            _table.setCaptionHeight(7f);
            _table.setRowHeight(10f);
            _table.setAllowResizeColumns(true);
            _table.getColumns().get(0).setWidth(22f);
            _table.setShape(SimpleShape.RoundedRectangle);
            _table.setBrush(new SolidBrush(new Color((int)153, (int)179, (int)255)));

            int rowIndex = 0;
            for(org.apache.metamodel.schema.Column column: table.getColumns()){
                _table.getCell(1, rowIndex).setText("<b>" + column.getName() + "</b>");
                _table.getCell(2, rowIndex).setText(column.getNativeType());
                _table.getCell(3, rowIndex).setText(column.getColumnSize().toString());


                //if the column is a primary key - set an image. If it's a foreign key - set and image
                if(column.isPrimaryKey())
                {
                    try {
                        Image image = ImageIO.read(getClass().getResourceAsStream("/pkey.png"));
                        _table.getCell(0,rowIndex).setImage(image);


                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }else if(Arrays.asList(table.getForeignKeys()).contains(column)){
                    try {
                        Image image = ImageIO.read(getClass().getResourceAsStream("/fkey.png"));
                        _table.getCell(0,rowIndex).setImage(image);


                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }

                rowIndex++;
            }
            //make everything fit
            _table.resizeToFitText(true);
            Rectangle2D.Float t_size = _table.getBounds();
            _table.getColumns().get(0).setWidth(7);
            _table.resize(t_size.width + 7, t_size.height);
            _table.resizeToFitImage();
        }


        arrangeViewDiagram();

    }

    private void arrangeViewDiagram() {
        //use LayeredLayout with some initial customization
        LayeredLayout layout = new LayeredLayout(com.mindfusion.diagramming.jlayout.Orientation.Horizontal, 30f, 25f, 5f, 5f);
        layout.arrange(viewDiagram);

        //adjust link position
        for (DiagramLink link: viewDiagram.getLinks()) {

            if (link.getOrigin().getBounds().getX() < link.getDestination().getBounds().getX())
                link.setEndPoint(new Point2D.Float(link.getDestination().getBounds().x, link.getEndPoint().y));
        }
        //re-route all links
        viewDiagram.setLinkRouter(new GridRouter());
        viewDiagram.routeAllLinks();

        //resize the diagram after the layout to fit all items
        viewDiagram.resizeToFitItems(20);
        diagramView.scrollTo(viewDiagram.getBounds().width/2, 0);

        //customize the links
        viewDiagram.setLinkCrossings(LinkCrossings.Arcs);
        viewDiagram.setRoundedLinks(true);
        viewDiagram.setRoundedLinksRadius(3);
        //redraw the control
        viewDiagram.repaint();
    }

    private static boolean validatePath(JdbcDataContext dataContext, String schemaName) throws Exception{
        if (Arrays.asList(dataContext.getSchemaNames()).contains(schemaName))
            return true;
        else
            throw new Exception("No such schema found");
    }
}
