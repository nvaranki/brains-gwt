package com.varankin.brains.gwt.client.service.db;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.varankin.brains.gwt.client.model.DbNode;

/**
 * The client-side stub for the RPC service.
 * 
 */
@RemoteServiceRelativePath("children") // copy path to war/WEB-INF/web.xml
public interface DbService extends RemoteService 
{
    DbNode[] childrenOf( DbNode[] path ) throws IllegalArgumentException;
}
