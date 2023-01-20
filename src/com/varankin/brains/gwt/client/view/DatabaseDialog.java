package com.varankin.brains.gwt.client.view;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import java.util.function.Consumer;

/**
 *
 * @author nvara
 */
public class DatabaseDialog
        extends DialogBox
{
    public final TextBox textBoxPath;
    public final CheckBox checkBoxCreate;
    public final Button buttonOpen;
    private final Consumer<DatabaseRequest> actor;

    public DatabaseDialog( Consumer<DatabaseRequest> actor )
    {
        this.actor = actor;
        setText( "Create/Open Database" ); // caption
        
        HorizontalPanel path = new HorizontalPanel();
        textBoxPath = new TextBox();
        path.add( textBoxPath );
        
        HorizontalPanel create = new HorizontalPanel();
        checkBoxCreate = new CheckBox();
        checkBoxCreate.addClickHandler( this::onClickCreate );
        create.add( checkBoxCreate );
        create.add( new Label( "Create new database" ) );
        
        VerticalPanel fields = new VerticalPanel();
        fields.add( new Label( "Path to database:" ) );
        fields.add( path );
        fields.add( create );
        
        buttonOpen = new Button( "Open", this::onClickOpen );
        Button cancel = new Button( "Cancel", this::onClickCancel );
        
        HorizontalPanel buttons = new HorizontalPanel();
        buttons.add( buttonOpen );
        buttons.add( cancel );
        
        DockLayoutPanel form = new DockLayoutPanel( Style.Unit.PX );
        form.setHeight( "150px" );
        form.setWidth( "450px" );
        form.addSouth( buttons, 40 );
        form.add( fields );
        setWidget( form );
        
        textBoxPath.setFocus( true );
    }
    
    private void onClickOpen( ClickEvent event )
    {
        hide();
        actor.accept( new DatabaseRequest( textBoxPath.getValue(), checkBoxCreate.getValue() ) ); //TODO async
    }
    
    private void onClickCancel( ClickEvent event )
    {
        hide();
    }
    
    private void onClickCreate( ClickEvent event )
    {
        buttonOpen.setText( checkBoxCreate.getValue()  ? "Create" : "Open" );
    }
    
    public static class DatabaseRequest
    {
        public final String path;
        public final boolean create;

        public DatabaseRequest( String path, boolean create )
        {
            this.path = path;
            this.create = create;
        }
    }
    
}
