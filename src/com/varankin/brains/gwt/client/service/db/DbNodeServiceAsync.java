package com.varankin.brains.gwt.client.service.db;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.varankin.brains.gwt.client.model.DbNode;

/**
 * The async counterpart of <code>DbService</code>.
 */
public interface DbNodeServiceAsync 
{
  void nodesFrom( DbNode[] path, AsyncCallback<DbNode[]> callback ) throws IllegalArgumentException;
  void archiveNodes( DbNode[] expected, AsyncCallback<DbNode[]> callback ) throws IllegalArgumentException;
  void archiveNodeAt( DatabaseRequest request, AsyncCallback<DbNode> callback ) throws IllegalArgumentException;
}
