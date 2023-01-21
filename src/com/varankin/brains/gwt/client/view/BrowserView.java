package com.varankin.brains.gwt.client.view;

import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

/**
 * Панель навигатора по процессам. 
 * 
 * @author &copy; 2023 Николай Варанкин
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

    void init()
    {
        //throw new UnsupportedOperationException( "Not supported yet." ); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
}
