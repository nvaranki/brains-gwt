package com.varankin.brains.gwt.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.varankin.brains.gwt.client.model.DbNode;
import com.varankin.brains.gwt.client.service.db.DbNodeService;
import com.varankin.brains.gwt.client.service.db.DbNodeServiceAsync;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author nvara
 */
public class ArchiveView extends Tree //TODO CellTree
{
    private DbNodeServiceAsync dbService;

    public ArchiveView()
    {
        addOpenHandler( this::onOpenEvent );
        addCloseHandler( this::onCloseEvent );
    }
    
    private static TreeItem itemOf( DbNode dbn )
    {
        TreeItem item = new TreeItem();
        item.setText( "[" + dbn.type().replace( "#", "" ) + "] " + dbn.name() );
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
                    ArchiveView target = ArchiveView.this;
                    target.removeItems();
                    Arrays.stream( result ).forEach( c -> target.addItem( itemOf( c ) ) );
                }
            } );
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
