package com.varankin.brains.gwt.server;

import com.varankin.brains.db.neo4j.local.NeoАрхив;
import com.varankin.brains.gwt.shared.dto.db.DatabaseRequest;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 *
 * @author nvara
 */
public class DbManager
{
    private static final File DB_ROOT = new File( "../mind-sample" ); //TODO "db" TODO config
    private static final String DB_REPO = "repo";
    private static final DbManager INSTANCE = new DbManager();
    
    static DbManager getInstance() { return INSTANCE; }
    
    private final Map<NeoАрхив,Integer> archives;

    private DbManager()
    {
        archives = new HashMap<>();
    }
    
    static File pack( String ref )
    {
        return new File( DB_ROOT.getPath() + File.separatorChar + ref + File.separatorChar + DB_REPO ).getAbsoluteFile(); //TODO .getCanonicalFile();
    }
    
    static String unpack( File loc )
    {
        if( loc.isDirectory() && DB_REPO.equals( loc.getName() ) )
        {
            File ref = loc.getParentFile();
            if( DB_ROOT.getName().equals( ref.getParentFile().getName() ) ) //TODO long path
                return ref.getName();
            else
                return loc.getAbsolutePath();
        }
        else
            return loc.getAbsolutePath();
    }
    
    synchronized NeoАрхив open( DatabaseRequest request ) throws Exception
    {
        File repo = pack( request.path );
        System.err.println( "Received " + ( request.create ? "create" : "open" ) + " request for DB at " + repo );
        Optional<NeoАрхив> search = archives.keySet().stream()
            .filter( a -> new File( a.расположение() ).equals( repo ) )
            .findAny();
        if( search.isPresent() )
            if( request.create )
            {
                System.err.println( "Archive already exists, can't create: \n" + repo );
                return null;
            }
            else
            {
                // the archive is open and in use
                NeoАрхив архив = search.get();
                archives.put( архив, archives.get( архив ) + 1 );
                return архив;
            }
        else 
            try
            {
                // open/create the archive
                NeoАрхив архив = new NeoАрхив( repo, request.create, new HashMap<>() );
                archives.put( архив, 1 );
                return архив;
            }
            catch( Exception ex )
            {
                ex.printStackTrace();
                return null;
            }
    }
    
    synchronized void close( NeoАрхив архив )
    {
        int count = archives.get( архив ) - 1;
        if( count > 0 )
            archives.put( архив, count );
        else
            archives.remove( архив );
    }
    
    synchronized Collection<NeoАрхив> all()
    {
        return new ArrayList<>( archives.keySet() ); //TODO map per Session
    }
    
    synchronized void destroy()
    {
        archives.keySet().forEach( NeoАрхив::закрыть );
        archives.clear();
    }

}
