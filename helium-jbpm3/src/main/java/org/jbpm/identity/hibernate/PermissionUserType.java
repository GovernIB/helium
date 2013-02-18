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
package org.jbpm.identity.hibernate;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.security.Permission;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;
import org.hibernate.usertype.CompositeUserType;

@SuppressWarnings("rawtypes")
public class PermissionUserType implements CompositeUserType {

  private static final String[] PROPERTY_NAMES = new String[]{"class", "name", "actions"};
  public String[] getPropertyNames() {
    return PROPERTY_NAMES;
  }

  private static final Type[] PROPERTY_TYPES = new Type[]{StandardBasicTypes.STRING, StandardBasicTypes.STRING, StandardBasicTypes.STRING};
  public Type[] getPropertyTypes() {
    return PROPERTY_TYPES;
  }

  public Object getPropertyValue(Object component, int property) throws HibernateException {
    Permission permission = (Permission) component;
    if (property==0) {
      return permission.getClass().getName();
    } else if (property==1) { 
      return permission.getName();
    } else if (property==2) { 
      return permission.getActions();
    } else {
      throw new IllegalArgumentException("illegal permission property '"+property+"'");
    }
  }

  public void setPropertyValue(Object arg0, int arg1, Object arg2) throws HibernateException {
    throw new UnsupportedOperationException("setting properties on a permission is not allowed");
  }

  public Class returnedClass() {
    return Permission.class;
  }

  public boolean equals(Object left, Object right) throws HibernateException {
    return left.equals(right);
  }

  public int hashCode(Object permission) throws HibernateException {
    return permission.hashCode();
  }
  
  private static final Class[] NAME_ACTIOS_CONSTRUCTOR_PARAMETER_TYPES = new Class[]{String.class, String.class};
  public Object nullSafeGet(ResultSet resultSet, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException {
    Object permission = null;
    String className = resultSet.getString(names[0]);
    String name = resultSet.getString(names[1]);
    String actions = resultSet.getString(names[2]);
    
    try {
      // TODO optimize performance by caching the constructors
      Class permissionClass = PermissionUserType.class.getClassLoader().loadClass(className);
      Constructor constructor = permissionClass.getDeclaredConstructor(NAME_ACTIOS_CONSTRUCTOR_PARAMETER_TYPES);
      permission = constructor.newInstance(new Object[]{name, actions});
    } catch (Exception e) {
      throw new HibernateException("couldn't create permission from database record ["+className+"|"+name+"|"+actions+"].  Does the permission class have a (String name,String actions) constructor ?", e);
    }
    
    return permission;
  }

  public void nullSafeSet(PreparedStatement preparedStatement, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
    Permission permission = (Permission) value;
    preparedStatement.setString(index, permission.getClass().getName());
    preparedStatement.setString(index+1, permission.getName());
    preparedStatement.setString(index+2, permission.getActions());
  }

  public Object deepCopy(Object permission) throws HibernateException {
    return permission;
  }

  public boolean isMutable() {
    return false;
  }

  public Serializable disassemble(Object value, SessionImplementor session) throws HibernateException {
    return (Serializable) value;
  }

  public Object assemble(Serializable cached, SessionImplementor session, Object owner) throws HibernateException {
    return cached;
  }

  public Object replace(Object original, Object target, SessionImplementor session, Object owner) throws HibernateException {
    return original;
  }
}
