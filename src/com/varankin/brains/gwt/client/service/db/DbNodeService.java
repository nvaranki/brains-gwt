package com.varankin.brains.gwt.client.service.db;

import com.varankin.brains.gwt.shared.dto.db.DatabaseRequest;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.varankin.brains.gwt.shared.dto.db.DbNode;

/**
 * The client-side stub for the GWT RPC service.
 * 
 * @author &copy; 2023 Николай Варанкин
 */
@RemoteServiceRelativePath("db/node/children") // copy path to war/WEB-INF/web.xml
public interface DbNodeService extends RemoteService 
{
    DbNode[] nodesFrom( DbNode[] path ) throws IllegalArgumentException;
    DbNode[] archiveNodes( DbNode[] expected ) throws IllegalArgumentException;
    DbNode archiveNodeAt( DatabaseRequest request ) throws IllegalArgumentException;
    String svgImage( DbNode[] path ) throws IllegalArgumentException;
    String xmlBrains( DbNode[] path ) throws IllegalArgumentException;
}
