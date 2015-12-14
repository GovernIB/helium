/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jbpm.db.hibernate;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmException;
import org.jbpm.configuration.ObjectFactory;
import org.jbpm.context.exe.Converter;
import org.jbpm.util.ClassLoaderUtil;

/**
 * provides access to the list of converters and ensures that the converter objects are unique.
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public abstract class Converters {
  
  static final int CONVERTERS_BY_CLASS_NAMES = 0;
  static final int CONVERTERS_BY_DATABASE_ID = 1;
  static final int CONVERTERS_IDS = 2;

  static Map converterMapsMap = new HashMap();
  
  // public methods

  public static Converter getConverterByClassName(String className) {
    Converter converter = (Converter) getConvertersByClassNames().get(className);
    if (converter==null) {
      throw new JbpmException("converter '"+className+"' is not declared in jbpm.converter.properties");
    }
    return converter; 
  }

  public static Converter getConverterByDatabaseId(String converterDatabaseId) {
    return (Converter) getConvertersByDatabaseId().get(converterDatabaseId);
  }

  public static String getConverterId(Converter converter) {
    //
	// return  (String) getConvertersIds().get(converter);
	//
    if (converter == null) return null;
	return  (String) getConvertersIds().get(converter.getClass().getName());
  }

  // maps class names to unique converter objects
  static Map getConvertersByClassNames() {
    return getConverterMaps()[CONVERTERS_BY_CLASS_NAMES];
  }

  // maps converter database-id-strings to unique converter objects 
  static Map getConvertersByDatabaseId() {
    return getConverterMaps()[CONVERTERS_BY_DATABASE_ID];
  }
  
  // maps unique converter objects to their database-id-string
  static Map getConvertersIds() {
    return getConverterMaps()[CONVERTERS_IDS];
  }

  static Map[] getConverterMaps() {
    Map[] converterMaps = null;
    synchronized(converterMapsMap) {
      ObjectFactory objectFactory = JbpmConfiguration.Configs.getObjectFactory();
      converterMaps = (Map[]) converterMapsMap.get(objectFactory);
      if (converterMaps==null) {
        converterMaps = createConverterMaps(objectFactory);
        converterMapsMap.put(objectFactory, converterMaps);
      }
    }
    return converterMaps;
  }


static Map[] createConverterMaps(ObjectFactory objectFactory) {
    Map[] converterMaps = new Map[3];
    converterMaps[CONVERTERS_BY_CLASS_NAMES] = new HashMap();
    converterMaps[CONVERTERS_BY_DATABASE_ID] = new HashMap();
    converterMaps[CONVERTERS_IDS] = new HashMap();

    Map convertersByClassNames = converterMaps[CONVERTERS_BY_CLASS_NAMES];
    Map convertersByDatabaseId = converterMaps[CONVERTERS_BY_DATABASE_ID];
    Map convertersIds = converterMaps[CONVERTERS_IDS];

    Properties converterProperties = null;
    if (objectFactory.hasObject("resource.converter")) {
      String resource = (String) objectFactory.createObject("resource.converter");
      converterProperties = ClassLoaderUtil.getProperties(resource);
    } else {
      converterProperties = new Properties();
    }

    Iterator iter = converterProperties.keySet().iterator();
    while (iter.hasNext()) {
      String converterDatabaseId = (String) iter.next();
      if (converterDatabaseId.length()!=1) throw new JbpmException("converter-ids must be of length 1 (to be stored in a char)");
      if (convertersByDatabaseId.containsKey(converterDatabaseId)) throw new JbpmException("duplicate converter id : '"+converterDatabaseId+"'");
      String converterClassName = converterProperties.getProperty(converterDatabaseId);
      try {
        Class converterClass = ClassLoaderUtil.loadClass(converterClassName);
        Converter converter = (Converter) converterClass.newInstance();
        log.debug("adding converter '"+converterDatabaseId+"', '"+converterClassName+"'");
        convertersByClassNames.put(converterClassName, converter);
        convertersByDatabaseId.put(converterDatabaseId, converter);
        //
        // convertersIds.put(converter, converterDatabaseId);
        //
        convertersIds.put(converterClassName, converterDatabaseId);
        //
      } catch (Exception e) {
        // NOTE that Error's are not caught because that might halt the JVM and mask the original Error.
        log.debug("couldn't instantiate converter '"+converterClassName+"': "+e);
      }
    }

    return converterMaps;
  }

  private static Log log = LogFactory.getLog(Converters.class);
}
