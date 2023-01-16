package com.varankin.brains.gwt.client.service.db;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.varankin.brains.gwt.client.model.DbNode;

/**
 * The async counterpart of <code>DbService</code>.
 */
public interface DbServiceAsync 
{
  void childrenOf( DbNode[] path, AsyncCallback<DbNode[]> callback )
      throws IllegalArgumentException;
}
