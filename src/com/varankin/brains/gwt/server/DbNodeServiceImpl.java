package com.varankin.brains.gwt.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.varankin.brains.appl.Сборка;
import static com.varankin.brains.appl.ЭкспортироватьSvg.БИБЛИОТЕКА;
import com.varankin.brains.db.neo4j.local.NeoАрхив;
import com.varankin.brains.db.type.DbАрхив;
import com.varankin.brains.db.type.DbАтрибутный;
import com.varankin.brains.db.type.DbЭлемент;
import com.varankin.brains.db.Коллекция;
import com.varankin.brains.db.Собираемый;
import com.varankin.brains.db.Транзакция;
import com.varankin.brains.gwt.client.service.db.DbNodeService;
import com.varankin.brains.gwt.shared.dto.db.DatabaseRequest;
import com.varankin.brains.gwt.shared.dto.db.DbNode;
import com.varankin.brains.io.svg.save.SvgФабрика;
import com.varankin.characteristic.Именованный;
import static com.varankin.filter.И.и;
import static com.varankin.filter.НЕ.не;
import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;


/**
 * The server-side implementation of the GWT RPC service.
 * 
 * @author &copy; 2023 Николай Варанкин
 */
public class DbNodeServiceImpl 
        extends RemoteServiceServlet 
        implements DbNodeService 
{
    @Override
    public void destroy()
    {
        DbManager.getInstance().destroy();
        super.destroy();
    }

    //<editor-fold defaultstate="expanded" desc="entries">
    
    @Override
    public DbNode archiveNodeAt( DatabaseRequest request ) throws IllegalArgumentException
    {
        try
        {
            NeoАрхив archive = DbManager.getInstance().open( request );
            if( archive != null )
                try( Транзакция транзакция = archive.транзакция() )
                {
                    DbNode dbn = instanceOfDbNode( archive );
                    транзакция.завершить( true );
                    return dbn;
                }
            else
                System.err.println( "No archive " + ( request.create ? "created" : "exists" ) + " at \"" + request.path + "\"" );
        }
        catch( Exception ex )
        {
            ex.printStackTrace();
        }
        return null;
    }
    
    @Override
    public DbNode[] archiveNodes( DbNode[] expected ) throws IllegalArgumentException
    {
        List<DbNode> result = new LinkedList<>();
        for( DbNode dbn : expected ) // expected is probably smaller than amount of loaded archives
        {
            System.err.println( "Loading saved node: " + dbn.getTag() );
            DbNode exists = null;
            for( DbАрхив архив : DbManager.getInstance().all() )
                try( Транзакция транзакция = архив.транзакция() )
                {
                    if( equals( архив, dbn ) )
                    {
                        // supply previously opened archive
                        exists = instanceOfDbNode( архив );
                        транзакция.завершить( true );
                        break;
                    }
                    транзакция.завершить( true );
                }
                catch( Exception ex )
                {
                    ex.printStackTrace();
                }
            if( exists == null )
                try
                {
                    // attempt to open referenced archive
                    DbАрхив архив = DbManager.getInstance().open(
                            new DatabaseRequest( DbManager.unpack( new File( dbn.getTag() ) ), false ) );
                    if( архив != null )
                        try( Транзакция транзакция = архив.транзакция() )
                        {
                            // supply archive that still exists
                            exists = instanceOfDbNode( архив );
                            транзакция.завершить( true );
                        }
                }
                catch( Exception ex )
                {
                    ex.printStackTrace();
                }
            if( exists != null )
                result.add( exists );
        }
        result.sort( Comparator.comparing( DbNode::getZone )
                .thenComparing( DbNode::getType ).thenComparing( DbNode::getName ) );
        System.err.println( "Archive nodes returned: " + result.size() );
        return result.toArray( DbNode[]::new );
    }
    
    @Override
    public DbNode[] nodesFrom( DbNode[] path ) throws IllegalArgumentException
    {
        // extract target's collections
        List<DbNode> result = new LinkedList<>();
        for( DbАтрибутный dba : allChildren( element( path ) ) )
            try( Транзакция транзакция = dba.транзакция() )
            {
                транзакция.завершить( result.add( instanceOfDbNode( dba ) ) );
            }
            catch( Exception ex )
            {
                ex.printStackTrace();
                return new DbNode[0];
            }
        result.sort( Comparator.comparing( DbNode::getZone )
                .thenComparing( DbNode::getType ).thenComparing( DbNode::getName ) );
        System.err.println( "Regular nodes returned: " + result.size() );
        return result.toArray( DbNode[]::new );
    }
    
    @Override
    public String svgImage( DbNode[] path ) throws IllegalArgumentException
    {
        DbАтрибутный dba = element( path );
        if( dba instanceof Собираемый ) 
            try( final Транзакция транзакция = dba.транзакция() )
            {
                транзакция.согласовать( Транзакция.Режим.ЧТЕНИЕ_БЕЗ_ЗАПИСИ, dba.архив() );
                Predicate<DbЭлемент> сборка = и( new Сборка( (Собираемый) dba ), не( БИБЛИОТЕКА ) );
                String data = SvgФабрика.getInstance().providerOf( dba, сборка ).get(); //TODO consider to apply Writer
                транзакция.завершить( true );
                System.err.println( "SVG returned: " + data.length() + " chars" );
                return data;
            }
            catch( Exception ex )
            {
                ex.printStackTrace();
            }
        return "<svg/>";
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="utilities">
    
    private static DbАтрибутный element( DbNode[] path )
    {
        DbАтрибутный target = null;
        for( DbNode dbn : Objects.requireNonNullElse( path, new DbNode[0] ) )
        {
            Collection<? extends DbАтрибутный> candidates = target == null ?
                    DbManager.getInstance().all() : allChildren( target );
            target = null;
            for( DbАтрибутный dba : candidates )
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
            if( target == null ) 
            {
                System.err.println( "No match of UI to DB" );
                break;
            }
        }
        return target;
    }
    
    private static Collection<DbАтрибутный> allChildren( DbАтрибутный target )
    {
        if( target == null )
        {
            return Collections.emptyList();
        }
        else
            try( Транзакция транзакция = target.транзакция() )
            {
                Collection<DbАтрибутный> result  = new LinkedList<>();
                for( Method m : target.getClass().getMethods() )
                    if( Modifier.isPublic( m.getModifiers() )
                            && ! Modifier.isStatic( m.getModifiers() )
                            && Коллекция.class.isAssignableFrom( m.getReturnType() )
                            && m.getParameterCount() == 0 )
                    {
                        m.setAccessible( true );
                        result.addAll( Collection.class.cast( m.invoke( target ) ) );
                    }
                транзакция.завершить( true );
                return result;
            }
            catch( Exception ex )
            {
                ex.printStackTrace();
                return Collections.emptyList();
            }
    }
    
    private static DbNode instanceOfDbNode( DbАтрибутный dba )
    {
        return new DbNode(
                name( dba ),
                dba.тип().НАЗВАНИЕ,
                Objects.requireNonNullElse( dba.тип().ЗОНА, "" ),
                dba.отметка() );
    }
    
    private static boolean equals( DbАтрибутный dba, DbNode dbn )
    {
        String name = name( dba );
        String type = dba.тип().НАЗВАНИЕ;
        String zone = Objects.requireNonNullElse( dba.тип().ЗОНА, "" );
        String tag  = dba.отметка();
        //System.out.printf( "%s <> %s, %s <> %s, %s <> %s \n", dbn.name(), name, dbn.type(), type, dbn.zone(), zone );
        return Objects.equals( dbn.getName(), name )
                && Objects.equals( dbn.getType(), type )
                && Objects.equals( dbn.getZone(), zone )
                && Objects.equals( dbn.getTag(),  tag  );
    }
    
    private static String name( DbАтрибутный dba )
    {
        return dba instanceof Именованный ?
                Objects.requireNonNullElse( nullable( ( (Именованный) dba ).название() ), dba.тип().НАЗВАНИЕ ) :
                dba.тип().НАЗВАНИЕ;
    }
    
    @Deprecated //TODO check why it happens
    private static String nullable( String text )
    {
        return text == null || text.isBlank() ? null : text;
    }
    
    //</editor-fold>
}
