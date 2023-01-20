package com.varankin.brains.gwt.shared;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;
import com.varankin.brains.gwt.client.model.DbNodeBean;

/**
 *
 * @author nvara
 */
public interface JsonFactory extends AutoBeanFactory 
{
    AutoBean<DbNodeBean> dbNode();
}
