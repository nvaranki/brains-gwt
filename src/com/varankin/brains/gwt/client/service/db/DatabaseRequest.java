package com.varankin.brains.gwt.client.service.db;

import java.io.Serializable;

/**
 *
 * @author nvara
 */
public class DatabaseRequest implements Serializable
{
    
    public String path;
    public boolean create;

    public DatabaseRequest()
    {
        this( null, false );
    }

    public DatabaseRequest( String path, boolean create )
    {
        this.path = path;
        this.create = create;
    }
    
}