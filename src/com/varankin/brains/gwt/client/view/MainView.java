package com.varankin.brains.gwt.client.view;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import static com.varankin.brains.gwt.client.view.ArchiveView.NBSP;

/**
 * Построитель главной экранной формы приложения.
 * 
 * @author &copy; 2023 Николай Варанкин
 */
public class MainView extends DockLayoutPanel
{
    private static final int LOG_LINES = 500; //TODO config

    private final ArchiveView archive;
    private final BrowserView browser;
    private final TabLayoutPanel tabs;
    private final TextArea log;

    public MainView()
    {
        super( Style.Unit.PX );
        browser = new BrowserView();
        archive = new ArchiveView();
        
        StackLayoutPanel browsers = new StackLayoutPanel( Unit.EM );
        browsers.add( archive, "Archives", 2 );
        browsers.add( new ScrollPanel( browser ), "Processes", 2 );

        tabs = new TabLayoutPanel( 2, Unit.EM );
        tabs.add( new Label("TODO Quick Start is here"), "Quick Start" + NBSP + "×" );
        tabs.add( new Label("TODO Analyzer is here"), "Analyzer" + NBSP + "×" );
        
        log = new TextArea();
        log.setText( "" );
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
        addToLog( "The Thinker™ started." );
    }
    
    public final void addToLog( String text )
    {
        String[] current = log.getText().split( "\n" );
        String[] update = text.split( "\n" );
        int strip = Math.min( Math.max( 0, current.length + update.length - LOG_LINES ), current.length );
        String buffer[] = new String[ Math.min( current.length - strip + update.length, LOG_LINES ) ];
        System.arraycopy( current, strip, buffer, 0, current.length - strip );
        System.arraycopy( update, Math.max( update.length - LOG_LINES, 0 ), 
                buffer, current.length - strip, Math.min( update.length, LOG_LINES ) );
        StringBuilder sb = new StringBuilder();
        for( String line : buffer )
            sb.append( line ).append( '\n' );
        log.setText( sb.toString() );
        log.getElement().setScrollTop( log.getElement().getScrollHeight() ); // show last line
        log.getElement().setScrollLeft( 0 ); // show beginning of the last line
    }

    public final TabLayoutPanel getTabs()
    {
        return tabs;
    }
    
    static MainView getRootPanel( Widget widget )
    {
        for( ; widget != null; widget = widget.getParent() )
            if( widget instanceof MainView )
                return (MainView) widget;
        System.err.println( "No MainView found to hold " + widget );
        return null;
    }
    
}
