package com.varankin.brains.gwt.client.model;

import java.io.Serializable;

/**
 * 
 * @author nvara
 */
//public record DbNode( String name, String type, String zone )
public final class DbNode implements Serializable
{
    private String name;
    private String type;
    private String zone;
    private String tag;
    
    public DbNode()
    {
    }
    
    public DbNode( String name, String type, String zone, String tag )
    {
        this.name = name;
        this.type = type;
        this.zone = zone;
        this.tag  = tag;
    }

    public String name()
    {
        return name;
    }

    public String type()
    {
        return type;
    }

    public String zone()
    {
        return zone;
    }

    public String tag()
    {
        return tag;
    }
}
