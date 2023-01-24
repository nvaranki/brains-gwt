package com.varankin.brains.gwt.server;

import com.varankin.brains.db.Транзакция;
import com.varankin.brains.db.neo4j.local.NeoАрхив;
import com.varankin.brains.db.type.DbАтрибутный;
import com.varankin.brains.gwt.shared.dto.db.DatabaseRequest;
import com.varankin.brains.gwt.shared.dto.db.DbNode;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Контроллер операций над базами данных.
 * 
 * @author &copy; 2023 Николай Варанкин
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
    
    private static boolean equals( DbАтрибутный dba, DbNode dbn )
    {
        String name = DbNodeServiceImpl.name( dba );
        String type = dba.тип().НАЗВАНИЕ;
        String zone = Objects.requireNonNullElse( dba.тип().ЗОНА, "" );
        String tag  = dba.отметка();
        //System.out.printf( "%s <> %s, %s <> %s, %s <> %s \n", dbn.name(), name, dbn.type(), type, dbn.zone(), zone );
        return Objects.equals( dbn.getName(), name )
                && Objects.equals( dbn.getType(), type )
                && Objects.equals( dbn.getZone(), zone )
                && Objects.equals( dbn.getTag(),  tag  );
    }
    
    static <T extends DbАтрибутный> T lookup( DbNode dbn, Collection<T> candidates )
    {
        T target = null;
        for( T dba : candidates )
            try( Транзакция транзакция = dba.транзакция() )
            {
                if( equals( dba, dbn ) )
                {
                    target = dba;
                    break;
                }
                транзакция.завершить( true );
            }
            catch( Exception ex )
            {
                ex.printStackTrace();
                return null;
            }
        return target;
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
        int count = archives.get( архив );
        System.err.println( "Number of archives serviced: " + archives.size() );
        System.err.println( "Number of archives serviced at \"" + архив.расположение() + "\": " + count );
        if( --count > 0 )
            archives.put( архив, count );
        else
        {
            archives.remove( архив );
            архив.закрыть();
        }
        System.err.println( "Number of archives serviced at \"" + архив.расположение() + "\": " + count );
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
