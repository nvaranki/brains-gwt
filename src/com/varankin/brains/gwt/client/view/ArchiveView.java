package com.varankin.brains.gwt.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import static com.varankin.brains.db.xml.Xml.PI_ELEMENT;
import static com.varankin.brains.db.xml.Xml.XML_CDATA;
import static com.varankin.brains.db.xml.XmlBrains.*;
import com.varankin.brains.gwt.client.model.DbNode;
import com.varankin.brains.gwt.client.service.db.DbNodeService;
import com.varankin.brains.gwt.client.service.db.DbNodeServiceAsync;
import static com.varankin.io.xml.svg.XmlSvg.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author nvara
 */
public class ArchiveView extends DockLayoutPanel
{
    private static final String IPATH = "images/icons16x16/";
    private static final String XML_NS_TEMP = "#NS";
    private static final String XML_UN_TEMP = "#OTHER";
    private static final String XML_GRAPHIC = "#GRAPHIC";
    private static final Map<String,String> iconFileName;
    static
    {
        iconFileName = new HashMap<>();
        iconFileName.put( XML_ARHIVE, "archive.png" );
        iconFileName.put( XML_BRAINS, "package.png" );
        iconFileName.put( XML_LIBRARY, "new-library.png" );
        iconFileName.put( XML_PROJECT, "new-project.png" );
        iconFileName.put( XML_SIGNAL, "signal.png" );
        iconFileName.put( XML_FRAGMENT, "fragment.png" );
        iconFileName.put( XML_NOTE, "properties.png" );
        iconFileName.put( XML_PROCESSOR, "processor2.png" );
        iconFileName.put( XML_PARAMETER, "parameter.png" );
        iconFileName.put( XML_JAVA, "java.png" );
        iconFileName.put( XML_JOINT, "connector.png" );
        iconFileName.put( XML_PIN, "pin.png" );
        iconFileName.put( XML_COMPUTE, "function.png" );
        iconFileName.put( XML_MODULE, "module.png" );
        iconFileName.put( XML_TIMELINE, "timeline.png" );
        iconFileName.put( XML_FIELD, "field2.png" );
        iconFileName.put( XML_SENSOR, "sensor.png" );
        iconFileName.put( XML_POINT, "point.png" );
        iconFileName.put( PI_ELEMENT, "instruction.png" );
        iconFileName.put( XML_NS_TEMP, "namespace.png" );
        iconFileName.put( XML_BASKET, "remove.png" );
        iconFileName.put( XML_CDATA, "text.png" );
        iconFileName.put( XML_GRAPHIC, "preview.png" );
        iconFileName.put( SVG_ELEMENT_CIRCLE, "preview.png" );
        iconFileName.put( SVG_ELEMENT_ELLIPSE, "preview.png" );
        iconFileName.put( SVG_ELEMENT_A, "preview.png" );
        iconFileName.put( SVG_ELEMENT_G, "preview.png" );
        iconFileName.put( SVG_ELEMENT_LINE, "preview.png" );
        iconFileName.put( SVG_ELEMENT_POLYGON, "preview.png" );
        iconFileName.put( SVG_ELEMENT_POLYLINE, "preview.png" );
        iconFileName.put( SVG_ELEMENT_RECT, "preview.png" );
        iconFileName.put( SVG_ELEMENT_SYMBOL, "preview.png" );
        iconFileName.put( SVG_ELEMENT_TEXT, "preview.png" );
        iconFileName.put( SVG_ELEMENT_USE, "preview.png" );
        iconFileName.put( null, "properties.png" );
    }

    private DbNodeServiceAsync dbService;
    private final Tree tree; //TODO CellTree
    
    public ArchiveView()
    {
        super( Style.Unit.PX );

        VerticalPanel toolbar = new VerticalPanel();
        toolbar.setSpacing( 2 ); //TODO -> CSS
        toolbar.add( new PushButton( new Image( IPATH + "archive.png" ), this::onClickOpen ) );
        toolbar.add( new PushButton( new Image( IPATH + "load.png" ), this::onClickLoad ) );
        toolbar.add( new PushButton( new Image( IPATH + "new-library.png" ), this::onClickNew ) );
        toolbar.add( new PushButton( new Image( IPATH + "preview.png" ), this::onClickPreview ) );
        toolbar.add( new PushButton( new Image( IPATH + "edit.png" ), this::onClickEdit ) );
        toolbar.add( new PushButton( new Image( IPATH + "multiply.png" ), this::onClickMultiply ) );
        toolbar.add( new PushButton( new Image( IPATH + "remove.png" ), this::onClickRemove ) );
        toolbar.add( new PushButton( new Image( IPATH + "file-xml.png" ), this::onClickImportFile ) );
        toolbar.add( new PushButton( new Image( IPATH + "load-internet.png" ), this::onClickImportNet ) );
        toolbar.add( new PushButton( new Image( IPATH + "file-export.png" ), this::onClickExportXml ) );
        toolbar.add( new PushButton( new Image( IPATH + "file-export.png" ), this::onClickExportPic ) );
        toolbar.add( new PushButton( new Image( IPATH + "properties.png" ), this::onClickProperties ) );
        addWest( toolbar, 40 );
        
        tree = new Tree();
        tree.addOpenHandler( this::onOpenEvent );
        tree.addCloseHandler( this::onCloseEvent );
        add( new ScrollPanel( tree ) );
    }
    
    private static TreeItem itemOf( DbNode dbn )
    {
        HorizontalPanel hp = new HorizontalPanel();
        hp.add( new Image( IPATH + "" + iconFileName.getOrDefault( dbn.type(), iconFileName.get( null ) ) ) );
        hp.add( new Label( String.valueOf( Character.toChars( 0x00A0 ) ) + dbn.name() ) );

        TreeItem item = new TreeItem( hp );
        item.setTitle( dbn.type() + '/' + dbn.zone() );
        item.setUserObject( dbn );
        item.addTextItem( "Loading..." ); // a hidden placeholder, to allow to open/close children
        return item;
    }
    
    private static DbNode[] getItemPath( TreeItem item )
    {
        List<DbNode> path = new LinkedList<>();
        for( ; item != null; item = item.getParentItem() )
        {
            Object dbn = item.getUserObject();
            if( dbn instanceof DbNode )
                path.add( 0, (DbNode) dbn );
            else
                System.err.println( "TreeItem user object error" );
        }
        return path.toArray( new DbNode[0] );
    }
    
    void init()
    {
        dbService = GWT.create( DbNodeService.class );
        // obtain root items
        if( dbService != null )
            dbService.nodesFrom( new DbNode[0], new AsyncCallback<DbNode[]>()
            {
                @Override
                public void onFailure( Throwable caught )
                {
                    caught.printStackTrace();
                }

                @Override
                public void onSuccess( DbNode[] result )
                {
                    // refresh list of children
                    Tree target = ArchiveView.this.tree;
                    target.removeItems();
                    Arrays.stream( result ).forEach( c -> target.addItem( itemOf( c ) ) );
                }
            } );
    }
    
    private void onClickOpen( ClickEvent event )
    {
        
    }
    
    private void onClickLoad( ClickEvent event )
    {
        
    }
    
    private void onClickNew( ClickEvent event )
    {
        
    }
    
    private void onClickPreview( ClickEvent event )
    {
        
    }
    
    private void onClickEdit( ClickEvent event )
    {
        
    }
    
    private void onClickMultiply( ClickEvent event )
    {
        
    }
    
    private void onClickRemove( ClickEvent event )
    {
        
    }
    
    private void onClickImportFile( ClickEvent event )
    {
        
    }
    
    private void onClickImportNet( ClickEvent event )
    {
        
    }
    
    private void onClickExportXml( ClickEvent event )
    {
        
    }
    
    private void onClickExportPic( ClickEvent event )
    {
        
    }
    
    private void onClickProperties( ClickEvent event )
    {
        
    }
    
    private void onOpenEvent( OpenEvent<TreeItem> event )
    {
        // obtain actual children
        if( dbService == null ) return;
        dbService.nodesFrom( getItemPath( event.getTarget() ), new AsyncCallback<DbNode[]>()
        {
            @Override
            public void onFailure( Throwable caught )
            {
                caught.printStackTrace();
            }

            @Override
            public void onSuccess( DbNode[] result )
            {
                // refresh list of children
                TreeItem target = event.getTarget();
                target.removeItems();
                Arrays.stream( result ).forEach( c -> target.addItem( itemOf( c ) ) );
            }
        } );
    }
    
    private void onCloseEvent( CloseEvent<TreeItem> event )
    {
        //TODO no expansion then //event.getTarget().removeItems(); // to obtain actual list on the next open
    }
    
}
