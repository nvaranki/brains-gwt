package com.varankin.brains.gwt.client.service.db;

import com.varankin.brains.gwt.shared.dto.db.DatabaseRequest;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.varankin.brains.gwt.shared.dto.db.DbNode;

/**
 * The async counterpart of <code>DbService</code>.
 * 
 * @author &copy; 2023 Николай Варанкин
 */
public interface DbNodeServiceAsync 
{
  void nodesFrom( DbNode[] path, AsyncCallback<DbNode[]> callback ) throws IllegalArgumentException;
  void archiveNodes( DbNode[] expected, AsyncCallback<DbNode[]> callback ) throws IllegalArgumentException;
  void archiveNodeAt( DatabaseRequest request, AsyncCallback<DbNode> callback ) throws IllegalArgumentException;
  void svgImage( DbNode[] path, AsyncCallback<String> callback ) throws IllegalArgumentException;
  void xmlBrains( DbNode[] path, AsyncCallback<String> callback ) throws IllegalArgumentException;
}
