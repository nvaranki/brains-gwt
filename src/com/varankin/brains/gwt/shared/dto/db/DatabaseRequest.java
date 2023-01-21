package com.varankin.brains.gwt.shared.dto.db;

import java.io.Serializable;

/**
 *
 * @author &copy; 2023 Николай Варанкин
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
