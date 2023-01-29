package com.varankin.brains.gwt.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import static com.varankin.brains.gwt.client.view.ArchiveView.fromJson;
import static com.varankin.brains.gwt.client.view.ArchiveView.itemOf;
import static com.varankin.brains.gwt.client.view.ArchiveView.toJson;
import com.varankin.brains.gwt.shared.dto.db.DbNode;

/**
 * Форма загрузки пакета в архив. 
 * 
 * @author &copy; 2023 Николай Варанкин
 */
public class UploadDialog
        extends DialogBox
{
    private static final String ENTRY_UPLOAD = "db/upload"; // copy to web.xml

    private final TreeItem archive;
    private final MainView logger;
    private final Hidden inputArchive;
    private final FileUpload inputFileUpload;
    private final FormPanel form;
    private final InputElement ie;

    public UploadDialog( TreeItem archive, MainView logger )
    {
        this.archive = archive;
        this.logger = logger;
        
        inputFileUpload = new FileUpload();
        inputFileUpload.setName( "uploader" );
        inputFileUpload.addChangeHandler( this::onFileNameChange );
        
        ie = inputFileUpload.getElement().cast();
        ie.setAccept( "text/xml" ); // adds file filter for *.xml
        ie.setPropertyBoolean( "required", true );
        
        inputArchive = new Hidden( "archive" );
        inputArchive.setValue( toJson( (DbNode) archive.getUserObject() ) );
        
        VerticalPanel block = new VerticalPanel();
        block.add( inputArchive );
        block.add( inputFileUpload );

        form = new FormPanel();
        form.add( block ); // only once cause SimplePanel
        form.setAction( GWT.getModuleBaseURL() + ENTRY_UPLOAD );
        form.setEncoding( FormPanel.ENCODING_MULTIPART );
        form.setMethod( FormPanel.METHOD_POST );
        form.addSubmitCompleteHandler( this::onSubmitComplete );
        
        setWidget( form );
    }
    
    void select()
    {
        ie.click();
    }
    
    private String getSelectedFileName()
    {
        String filename = inputFileUpload.getFilename().trim();
        int lsi = Math.max( filename.lastIndexOf( '/' ), filename.lastIndexOf( '\\' ) ); // -1 or index
        return filename.substring( lsi + 1 );
    }
    
    private void onFileNameChange( ChangeEvent event )
    {
        String filename = getSelectedFileName();
        if( filename.toLowerCase().endsWith( ".xml" ) ) // no other way to check the file
        {
            logger.addToLog( "Uploading \"" + filename + "\"..." );
            form.submit();
            // keep the form attached
        }
        else 
        {
            if( ! filename.isEmpty() )
                logger.addToLog( "File \"" + filename + "\" was ignored. Only XML file is allowed to upload." );
            hide();
        }
    }
    
    private void onSubmitComplete( FormPanel.SubmitCompleteEvent event ) 
    {
        String results = event.getResults();
        logger.addToLog( "Transmission finished." );
        if( results == null )
            logger.addToLog( "No response received." ); // result of import 
        else
        {
            String json = results.split( "\n" )[0].trim();
            DbNode dbn = json.startsWith( "{" ) && json.endsWith( "}" ) ? fromJson( json ) : null;
            if( dbn == null )
                logger.addToLog( "No package imported." ); // result of import 
            else
            {
                archive.addItem( itemOf( dbn ) ); 
                logger.addToLog( "Package imported: " + dbn.getName() ); 
            }
        }
        hide();
    }
}
