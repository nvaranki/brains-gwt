package com.varankin.brains.gwt.server;

import com.google.gson.Gson;
import com.varankin.brains.db.type.DbАрхив;
import com.varankin.brains.db.type.DbПакет;
import com.varankin.brains.db.Коллекция;
import com.varankin.brains.db.Транзакция;
import static com.varankin.brains.gwt.server.DbNodeServiceImpl.instanceOfDbNode;
import com.varankin.brains.gwt.shared.dto.db.DbNode;
import com.varankin.brains.io.xml.load.SaxConfigurator;
import com.varankin.brains.io.xml.load.SaxImporter;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXParseException;

/**
 * Remote XML import to an archive.
 * 
 * @author &copy; 2023 Николай Варанкин
 */
@MultipartConfig
public class DbUploadServlet extends HttpServlet
{
    @Override
    protected void doPost( HttpServletRequest request, HttpServletResponse response ) 
            throws ServletException, IOException 
    {      
        try 
        {
            List<String> report = new LinkedList<>();
            File file = null;
            DbАрхив dba = null;

            for( Part part : request.getParts() )
            {
                String partName = part.getName();
                //System.out.println( partName + ", " + part.getContentType() + ", " + part.getSize() );
                if( "uploader".equals( partName ) )
                    file = loadPartToFile( part, report );
                else if( "archive".equals( partName ) )
                    dba = loadPartAsArchive( part, report );
                else
                    report.add( partName + ", " + part.getSize() + ", unused" );
            }

            if( file != null )
            {
                if( dba != null )
                {
                    DbNode dbp = importXML( file, dba, report );
                    if( dbp != null )
                    {
                        // send it back
                        String json = new Gson().toJson( dbp );
                        //System.out.println( json );
                        PrintWriter rw = response.getWriter();
                        rw.println( json ); //TODO return as "application/json"
                    }
                }
                file.delete();
            }

            response.setContentType( "text/html" );
            PrintWriter out = response.getWriter();
            for( String r : report )
                out.println( r + "<br/>" );
        } 
        catch( Exception ex ) 
        {
            ex.printStackTrace();
            PrintWriter out = response.getWriter();
            response.setContentType( "text/html" );
            out.println( "Error: " + ex.getMessage() );
            out.close();
        }
    }
    
    private DbNode importXML( File file, DbАрхив архив, List<String> report )
    {
        try( final Транзакция транзакция = архив.транзакция(); 
                final InputStream stream = new BufferedInputStream( 
                        new FileInputStream( file ) ); ) //TODO buffer size from params
        {
            // подготовить парсер
            SAXParserFactory фабрика = SaxConfigurator.getInstance().фабрика();
            фабрика.setValidating( true ); //TODO setup from config
            фабрика.setNamespaceAware( true );
            SAXParser parser = фабрика.newSAXParser();
            SaxImporter handler = new SaxImporter( архив );

            // парсировать 
            транзакция.согласовать( Транзакция.Режим.ЗАПРЕТ_ДОСТУПА, архив ); //TODO consider transaction inside handler
            parser.parse( stream, handler, SaxConfigurator.XML_SYSTEM_ROOT );
            DbПакет пакет = handler.getResult(); //TODO consider Function<InputStream,DbПакет>
            
            if( пакет != null )
            {
                архив.пакеты().add( пакет ); // insert in same transaction!
                архив.setPropertyValue( Коллекция.PROPERTY_UPDATED, true );
                DbNode dbn = instanceOfDbNode( пакет );
                транзакция.завершить( true );
                report.add( "Imported " + dbn.getName() );
                return dbn;
            }
            else
                report.add( "Import failed for " + file.getName() );
                
            транзакция.завершить( false );
        }
        catch( ParserConfigurationException ex )
        {
            report.add( "SAX parser is wrong: " + ex.getMessage() );
        }
        catch( SAXParseException ex )
        {
            report.add( "SAX parser found a problem: " + ex.getMessage() );
        }
        catch( Exception ex )
        {
            report.add( "Import failed: " + ex.getMessage() );
        }
        return null;
    }

    private static File loadPartToFile( Part part, List<String> report ) throws IOException
    {
        File file = File.createTempFile( "dbu", ".xml", new File( "uf" ) );
        try( InputStream inputStream = part.getInputStream();
                OutputStream outputStream = new FileOutputStream( file )  )
        {
            int c;
            while( ( c = inputStream.read() ) >= 0 )
                outputStream.write( c );
            report.add( part.getName() + ", " + part.getContentType() + ", " + part.getSize() );
        }
        catch( Exception ex )
        {
            ex.printStackTrace();
            report.add( part.getName() + ", " + part.getContentType() + ", " + ex.getMessage() );
            file = null;
        }
        return file;
    }
    
    private static DbАрхив loadPartAsArchive( Part part, List<String> report )
    {
        DbАрхив dba = null;
        try( Reader reader = new InputStreamReader( part.getInputStream(), Charset.forName( "utf-8" ) )  )
        {
            DbNode dbn = new Gson().fromJson( reader, DbNode.class );
            if( dbn != null )
            {
                dba = DbManager.lookup( dbn, DbManager.getInstance().all() );
                report.add( part.getName() + ", " + dba.отметка() + ", " + part.getSize() );
            }
            else
                report.add( part.getName() + ", " + part.getSize() + ", unresolved" );
        }
        catch( Exception ex )
        {
            ex.printStackTrace();
            report.add( part.getName() + ", " + part.getSize() + ", " + ex.getMessage() );
        }
        return dba;
    }

}
