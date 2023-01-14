package com.varankin.brains.gwt.client;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;

/**
 *
 * @author nvara
 */
public class MainView extends DockLayoutPanel
{

    public MainView()
    {
        super( Style.Unit.PX );
        
        StackLayoutPanel browsers = new StackLayoutPanel( Unit.EM );
        browsers.add( new BrowserView(), "Browser", 2 );
        browsers.add( new ArchiveView(), "Archive", 2 );

        TabLayoutPanel tabs = new TabLayoutPanel( 2, Unit.EM );
        tabs.add( new Label("TODO Quick Start is here"), "Quick Start" );
        tabs.add( new Label("TODO Analyzer is here"), "Analyzer" );
        tabs.add( new Label("TODO Summer is here"), "Summer" );
        
        TextArea log = new TextArea();
        log.setText( "TODO status log is here\nAnd here...\nAnd here...\nAnd here...\nAnd here..." );
        log.setReadOnly( true );
        
        // buildup order is significant
        addNorth( new MenuView(), 30 );
        addSouth( log, 100 );
        addWest( browsers, 300 );
        add( tabs );
    }
    
}
