package com.varankin.brains.gwt.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.varankin.brains.gwt.client.model.DbNode;
import com.varankin.brains.gwt.client.service.db.DbNodeService;
import com.varankin.brains.gwt.shared.FieldVerifier;

/**
 * The server-side implementation of the RPC service.
 */
public class DbNodeServiceImpl 
        extends RemoteServiceServlet 
        implements DbNodeService 
{

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
        // lookup DB service
        // locate node by path
        // return combined children
        //TODO NOT IMPL
        return path.length == 0 ? 
                new DbNode[]
                { 
                    //new DbNode( "Archive AAA", "#archive", "brains" ) 
                    new DbNode( "Archive-s #1 TODO", "#archive", "brains" ),
                    new DbNode( "Archive-s #2 TODO", "#archive", "brains" )
                } : 
                new DbNode[] 
                { 
                    new DbNode( "Child #1 of TODO " + path[path.length-1].name(), "#element", "brains" ),
                    new DbNode( "Child #2 of TODO " + path[path.length-1].name(), "#element", "brains" )
                };
    }
}
