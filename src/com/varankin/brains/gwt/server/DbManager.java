package com.varankin.brains.gwt.server;

import com.varankin.brains.db.neo4j.local.NeoАрхив;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author nvara
 */
public class DbManager
{
    private static final DbManager DBM = new DbManager();
    static DbManager getInstance() { return DBM; }
    
    private final Map<NeoАрхив,Integer> archives;

    private DbManager()
    {
        archives = new HashMap<>();
        
        try
        {
            open( new File( "C:\\Users\\nvara\\Projects\\Thinker\\mind-sample\\neo4j\\repo" ) ); //TODO DEBUG
        }
        catch( Exception ex )
        {
            ex.printStackTrace();
        }
    }
    
    synchronized NeoАрхив open( File path ) throws Exception
    {
        NeoАрхив архив = new NeoАрхив( path, new HashMap<>() ); //TODO check it's open
        archives.put( архив, 1 );
        return архив;
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
