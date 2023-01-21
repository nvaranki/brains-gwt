package com.varankin.brains.gwt.client.view;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.varankin.brains.gwt.shared.dto.db.DatabaseRequest;
import java.util.function.Consumer;

/**
 * Форма получения доступа к БД. 
 * 
 * @author &copy; 2023 Николай Варанкин
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
        
        textBoxPath = new TextBox();
        textBoxPath.addValueChangeHandler( this::onDbNameChange );
        
        HorizontalPanel path = new HorizontalPanel();
        path.add( textBoxPath );
        
        checkBoxCreate = new CheckBox();
        checkBoxCreate.addClickHandler( this::onChangeCreate );
        
        HorizontalPanel create = new HorizontalPanel();
        create.add( checkBoxCreate );
        create.add( new Label( "Create new database" ) );
        
        VerticalPanel fields = new VerticalPanel();
        fields.add( new Label( "Database name:" ) );
        fields.add( path );
        fields.add( create );
        
        buttonOpen = new Button( "Open", this::onClickOpen );
        buttonOpen.setEnabled( false );
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
    
    private void onDbNameChange( ValueChangeEvent<String> event )
    {
        String text = event.getValue();
        boolean disable = text.trim().isEmpty()
            || text.contains( "." ) || text.contains( "/" ) || text.contains( "\\" ); //TODO allow long path
        buttonOpen.setEnabled( ! disable );
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
    
    private void onChangeCreate( ClickEvent event )
    {
        buttonOpen.setText( checkBoxCreate.getValue()  ? "Create" : "Open" );
    }
    
}
