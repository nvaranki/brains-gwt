package com.varankin.brains.gwt.client;

import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

/**
 *
 * @author nvara
 */
public class BrowserView extends Tree //TODO CellTree
{

    public BrowserView()
    {
        TreeItem root = new TreeItem();
        root.setText("Thinker TODO");
        
        root.addTextItem("Summer TODO");
        root.addTextItem("Processor TODO");
        
        addItem( root );
    }
    
}
