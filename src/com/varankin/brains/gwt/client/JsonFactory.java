package com.varankin.brains.gwt.client;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

/**
 * JSON helper utility.
 * 
 * @author &copy; 2023 Николай Варанкин
 */
public interface JsonFactory extends AutoBeanFactory 
{
    AutoBean<DbNodeBean> dbNode();

    public static interface DbNodeBean
    {

        String getName();

        String getTag();

        String getType();

        String getZone();

        void setName( String value );

        void setTag( String value );

        void setType( String value );

        void setZone( String value );
    }
}
