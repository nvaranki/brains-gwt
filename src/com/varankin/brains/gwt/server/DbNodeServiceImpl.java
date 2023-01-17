package com.varankin.brains.gwt.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.varankin.brains.db.Коллекция;
import com.varankin.brains.db.Транзакция;
import com.varankin.brains.db.neo4j.local.NeoАрхив;
import com.varankin.brains.db.type.DbАрхив;
import com.varankin.brains.db.type.DbАтрибутный;
import com.varankin.brains.gwt.client.model.DbNode;
import com.varankin.brains.gwt.client.service.db.DbNodeService;
import com.varankin.brains.gwt.shared.FieldVerifier;
import com.varankin.characteristic.Именованный;
import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * The server-side implementation of the RPC service.
 */
public class DbNodeServiceImpl 
        extends RemoteServiceServlet 
        implements DbNodeService 
{
    private final Collection<NeoАрхив> archives;

    public DbNodeServiceImpl()
    {
        archives = new LinkedList<>();
        try
        {
            archives.add( new NeoАрхив(
                    new File( "C:\\Users\\nvara\\Projects\\Thinker\\mind-sample\\neo4j\\repo" ), //TODO DEBUG
                    new HashMap<>() ) );
        }
        catch( Exception ex )
        {
            ex.printStackTrace();
        }
    }
    
    @Override
    public void destroy()
    {
        archives.forEach( NeoАрхив::закрыть );
        archives.clear();
        super.destroy();
    }

  public String greetServer(String input) throws IllegalArgumentException {
    // Verify that the input is valid. 
    if (!FieldVerifier.isValidName(input)) {
      // If the input is not valid, throw an IllegalArgumentException back to
      // the client.
      throw new IllegalArgumentException(
          "Name must be at least 4 characters long");
    }

    String serverInfo = getServletContext().getServerInfo();
    String userAgent = getThreadLocalRequest().getHeader("User-Agent");

    // Escape data from the client to avoid cross-site script vulnerabilities.
    input = escapeHtml(input);
    userAgent = escapeHtml(userAgent);

    return "Hello, " + input + "!<br><br>I am running " + serverInfo
        + ".<br><br>It looks like you are using:<br>" + userAgent;
  }

  /**
   * Escape an html string. Escaping data received from the client helps to
   * prevent cross-site script vulnerabilities.
   * 
   * @param html the html string to escape
   * @return the escaped string
   */
  private String escapeHtml(String html) {
    if (html == null) {
      return null;
    }
    return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(
        ">", "&gt;");
  }
        
    @Override
    public DbNode[] nodesFrom( DbNode[] path ) throws IllegalArgumentException
    {
        List<DbNode> result = new LinkedList<>();
        
        if( path == null || path.length == 0 )
        {
            for( DbАрхив архив : archives )
                try( Транзакция транзакция = архив.транзакция() )
                {
                    транзакция.завершить( result.add( instanceOfDbNode( архив ) ) );
                }
                catch( Exception ex )
                {
                    ex.printStackTrace();
                }
        }
        else
        {
            DbАтрибутный target = null;
            for( DbNode dbn : path )
            {
                Collection<? extends DbАтрибутный> candidates = target == null ? archives : allChildren( target );
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
                        break;
                    }
                if( target == null ) break;
            }
            // extract target's collections
            for( DbАтрибутный dba : allChildren( target ) )
                try( Транзакция транзакция = dba.транзакция() )
                {
                    транзакция.завершить( result.add( instanceOfDbNode( dba ) ) );
                }
                catch( Exception ex )
                {
                    ex.printStackTrace();
                    break;
                }
        }
        result.sort( Comparator.comparing( DbNode::zone )
                .thenComparing( DbNode::type ).thenComparing( DbNode::name ) );
        return result.toArray( DbNode[]::new );
    }
    
    private static Collection<DbАтрибутный> allChildren( DbАтрибутный target ) 
    {
        if( target == null ) 
        {
            System.err.println( "No match of UI to DB" );
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
            Objects.requireNonNullElse( dba.тип().ЗОНА, "" ) );
    }
    
    private static boolean equals( DbАтрибутный dba, DbNode dbn )
    {
        String name = name( dba );
        String type = dba.тип().НАЗВАНИЕ;
        String zone = Objects.requireNonNullElse( dba.тип().ЗОНА, "" );
        //System.out.printf( "%s <> %s, %s <> %s, %s <> %s \n", dbn.name(), name, dbn.type(), type, dbn.zone(), zone );
        return Objects.equals( dbn.name(), name ) 
            && Objects.equals( dbn.type(), type ) 
            && Objects.equals( dbn.zone(), zone );
    }
    
    private static String name( DbАтрибутный dba )
    {
        return dba instanceof Именованный ? 
            Objects.requireNonNullElse( ( (Именованный) dba ).название(), dba.тип().НАЗВАНИЕ ) : 
            dba.тип().НАЗВАНИЕ;
    }

}
