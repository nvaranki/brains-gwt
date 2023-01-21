package com.varankin.brains.gwt.client.service.db;

import com.varankin.brains.gwt.shared.dto.db.DatabaseRequest;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.varankin.brains.gwt.shared.dto.db.DbNode;

/**
 * The client-side stub for the RPC service.
 * 
 */
@RemoteServiceRelativePath("db/node/children") // copy path to war/WEB-INF/web.xml
public interface DbNodeService extends RemoteService 
{
    DbNode[] nodesFrom( DbNode[] path ) throws IllegalArgumentException;
    DbNode[] archiveNodes( DbNode[] expected ) throws IllegalArgumentException;
    DbNode archiveNodeAt( DatabaseRequest request ) throws IllegalArgumentException;
}
