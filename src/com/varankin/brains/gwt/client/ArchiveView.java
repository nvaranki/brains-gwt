package com.varankin.brains.gwt.client;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

/**
 *
 * @author nvara
 */
public class ArchiveView extends Tree //TODO CellTree
{

    public ArchiveView()
    {
        TreeItem a0 = new TreeItem( new Label("Archive #1 TODO") );
        
        a0.addTextItem("summer TODO");
        a0.addTextItem("logic TODO");
        
        addItem( a0 );

        TreeItem a1 = new TreeItem( new Label("Archive #2 TODO") );
        
        a1.addTextItem("learner TODO");
        
        addItem( a1 );
    }
    
}
