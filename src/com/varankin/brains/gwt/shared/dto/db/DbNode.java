package com.varankin.brains.gwt.shared.dto.db;

import com.varankin.brains.gwt.client.model.DbNodeBean;
import java.io.Serializable;

/**
 * 
 * @author nvara
 */
public final class DbNode implements Serializable, DbNodeBean
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

    public DbNode( DbNodeBean bean )
    {
        this( bean.getName(), bean.getType(), bean.getZone(), bean.getTag() );
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String getType()
    {
        return type;
    }

    @Override
    public String getZone()
    {
        return zone;
    }

    @Override
    public String getTag()
    {
        return tag;
    }
    
    @Override
    public void setName( String value )
    {
        name = value;
    }

    @Override
    public void setType( String value )
    {
        type = value;
    }

    @Override
    public void setZone( String value )
    {
        zone = value;
    }

    @Override
    public void setTag( String value )
    {
        tag = value;
    }
}
