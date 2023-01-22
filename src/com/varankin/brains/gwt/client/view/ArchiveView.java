package com.varankin.brains.gwt.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import static com.varankin.brains.db.xml.Xml.PI_ELEMENT;
import static com.varankin.brains.db.xml.Xml.XML_CDATA;
import static com.varankin.brains.db.xml.XmlBrains.*;
import com.varankin.brains.gwt.client.JsonFactory;
import com.varankin.brains.gwt.client.JsonFactory.DbNodeBean;
import com.varankin.brains.gwt.client.service.db.DbNodeService;
import com.varankin.brains.gwt.client.service.db.DbNodeServiceAsync;
import com.varankin.brains.gwt.shared.dto.db.DatabaseRequest;
import com.varankin.brains.gwt.shared.dto.db.DbNode;
import static com.varankin.io.xml.svg.XmlSvg.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Панель навигатора по архивам. 
 * 
 * @author &copy; 2023 Николай Варанкин
 */
public class ArchiveView extends DockLayoutPanel
{
    private static final String IPATH = "images/icons16x16/";
    private static final String LS_ARCHIVES = "archives";
    private static final String LS_SPLITTER = "\n";
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
    private Storage localStorage;
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
        addWest( toolbar, 34 );
        
        tree = new Tree();
        tree.addOpenHandler( this::onOpenEvent );
        tree.addCloseHandler( this::onCloseEvent );
        tree.addSelectionHandler( this::onItemSelection );
        add( new ScrollPanel( tree ) );
    }
    
    void init()
    {
        localStorage = Storage.getLocalStorageIfSupported();
        dbService = GWT.create( DbNodeService.class );
        if( dbService != null )
            dbService.archiveNodes( readLastListOfArchives(), new RootNodesSetup() );
    }
    
    private void onDatabaseOpen( DatabaseRequest request )
    {
        //Window.alert("DB path: " + request.path + ( request.create ? " [new]" : " [open]" ) );
        if( dbService != null )
            dbService.archiveNodeAt( request, new AddRootNode() );
    }

    //<editor-fold defaultstate="collapsed" desc="events">
    
    private void onClickOpen( ClickEvent event )
    {
        DatabaseDialog dialog = new DatabaseDialog( this::onDatabaseOpen );
        dialog.showRelativeTo( (UIObject) event.getSource() );
//        dialog.setPopupPosition( 150, 100 );
//        dialog.show();
    }
    
    private void onClickLoad( ClickEvent event )
    {
        
    }
    
    private void onClickNew( ClickEvent event )
    {
        
    }
    
    private void onClickPreview( ClickEvent event )
    {
        TreeItem selection = tree.getSelectedItem();
        if( selection != null )
        {
            DbNode dbn = (DbNode) selection.getUserObject();
            if( dbService != null )
                dbService.svgImage( getItemPath( selection ), new AddPreviewTab( 
                    MainView.getRootPanel( tree ), dbn.getName() ) );
        }
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
        if( dbService != null )
        {
            TreeItem target = event.getTarget();
            dbService.nodesFrom( getItemPath( target ), new IncludedNodesSetup( target ) );
        }
    }
    
    private void onCloseEvent( CloseEvent<TreeItem> event )
    {
        //TODO no expansion then //event.getTarget().removeItems(); // to obtain actual list on the next open
    }

    private void onItemSelection( SelectionEvent<TreeItem> event )
    {
        TreeItem selectedItem = tree.getSelectedItem();
        DbNode dbn = (DbNode) selectedItem.getUserObject();
        //TODO manage toolbar enabled statuses
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="callbacks">
    
    private class AddRootNode implements AsyncCallback<DbNode>
    {
        final Tree target = ArchiveView.this.tree;
        
        @Override
        public void onFailure( Throwable caught )
        {
            caught.printStackTrace();
        }
        
        @Override
        public void onSuccess( DbNode result )
        {
            if( result != null )
            {
                // refresh list of children
                target.addItem( itemOf( result ) );
                // remember updated list of children
                int itemCount = target.getItemCount();
                List<DbNode> current = new ArrayList<>( itemCount );
                for( int i = 0; i < itemCount; i++ )
                    current.add( (DbNode) target.getItem( i ).getUserObject() );
                ArchiveView.this.saveActualListOfArchives( current.toArray( new DbNode[itemCount] ) );
            }
            else
            {
                Window.alert( "Archive wasn't open." );
                System.err.println( "Archive wasn't open." );
            }
        }
    }
    
    private class RootNodesSetup implements AsyncCallback<DbNode[]>
    {
        final Tree target = ArchiveView.this.tree;
        
        @Override
        public void onFailure( Throwable caught )
        {
            caught.printStackTrace();
        }
        
        @Override
        public void onSuccess( DbNode[] result )
        {
            // refresh list of children
            target.removeItems();
            Arrays.stream( result ).forEach( c -> target.addItem( itemOf( c ) ) );
            ArchiveView.this.saveActualListOfArchives( result );
        }
    }
    
    private static class IncludedNodesSetup implements AsyncCallback<DbNode[]>
    {
        final TreeItem target;
        
        IncludedNodesSetup( TreeItem target )
        {
            this.target = target;
        }
        
        @Override
        public void onFailure( Throwable caught )
        {
            caught.printStackTrace();
        }
        
        @Override
        public void onSuccess( DbNode[] result )
        {
            // refresh list of children
            target.removeItems();
            Arrays.stream( result ).forEach( c -> target.addItem( itemOf( c ) ) );
        }
    }
    
    private static class AddPreviewTab implements AsyncCallback<String>
    {
        final MainView target;
        final String title;
        
        AddPreviewTab( MainView target, String title )
        {
            this.target = target;
            this.title = title;
        }
        
        @Override
        public void onFailure( Throwable caught )
        {
            caught.printStackTrace();
        }
        
        @Override
        public void onSuccess( String svg )
        {
            target.getTabs().add( new ScrollPanel( new HTML( svg ) ), title ); // despite it's SVG
        }
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="items">

    private static TreeItem itemOf( DbNode dbn )
    {
        HorizontalPanel hp = new HorizontalPanel();
        hp.add( new Image( IPATH + "" + iconFileName.getOrDefault( dbn.getType(), iconFileName.get( null ) ) ) );
        hp.add( new Label( String.valueOf( Character.toChars( 0x00A0 ) ) + dbn.getName() ) );
        
        TreeItem item = new TreeItem( hp );
        item.setTitle( dbn.getType() + '/' + dbn.getZone() );
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
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="storage">
    
    private DbNode[] readLastListOfArchives()
    {
        List<DbNode> expected = new LinkedList<>();
        if( localStorage != null )
        {
            if( localStorage != null )
            {
                // obtain last list of archives
                String json = localStorage.getItem( LS_ARCHIVES );
                //Window.alert("saved local archives: " + json );
                if( json == null || json.trim().isEmpty() ) json = "";
                //Window.alert("old local archives: " + json );
                JsonFactory serializer = GWT.create( JsonFactory.class );
                for( String item : json.split( LS_SPLITTER ) )
                    if( ! item.trim().isEmpty() )
                    {
                        //Window.alert("saved local archive: " + item );
                        AutoBean<DbNodeBean> bean = AutoBeanCodex.decode( serializer, DbNodeBean.class, item );
                        expected.add( new DbNode( bean.as() ) );
                    }
            }
        }
        return expected.toArray( new DbNode[expected.size()] );
    }
    
    private void saveActualListOfArchives( DbNode[] nodes )
    {
        if( localStorage != null )
        {
            // reset actual list of archives
            StringBuilder json = new StringBuilder();
            for( DbNode dbn : nodes )
            {
                JsonFactory serializer = GWT.create( JsonFactory.class );
                AutoBean<DbNodeBean> bean = serializer.create( DbNodeBean.class );
                DbNodeBean t = bean.as();
                t.setName( dbn.getName() );
                t.setType( dbn.getType() );
                t.setZone( dbn.getZone() );
                t.setTag ( dbn.getTag()  );
                String item = AutoBeanCodex.encode( bean ).getPayload();
                if( json.length() > 0 ) json.append( LS_SPLITTER ); // API doesn't allow to handlie array
                json.append( item );
            }
            localStorage.setItem( LS_ARCHIVES, json.toString() );
            //Window.alert("new local archives: " + json );
        }
    }
    
    //</editor-fold>
    
}
