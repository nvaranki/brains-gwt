package com.varankin.brains.gwt.client.view;

import com.varankin.brains.gwt.client.view.MenuView;
import com.varankin.brains.gwt.client.view.BrowserView;
import com.varankin.brains.gwt.client.view.ArchiveView;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.varankin.brains.gwt.client.service.db.DbServiceAsync;

/**
 *
 * @author nvara
 */
public class MainView extends DockLayoutPanel
{
    private final ArchiveView archive;
    private final BrowserView browser;

    public MainView()
    {
        super( Style.Unit.PX );
        browser = new BrowserView();
        archive = new ArchiveView();
        
        StackLayoutPanel browsers = new StackLayoutPanel( Unit.EM );
        browsers.add( browser, "Browser", 2 );
        browsers.add( archive, "Archive", 2 );

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

    public void init()
    {
        archive.init();
        browser.init();
    }
    
}
