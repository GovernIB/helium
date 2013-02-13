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
package org.jbpm.security.authentication;

import org.jbpm.security.AuthenticationService;
import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContextHolder;

public class DefaultAuthenticationService implements AuthenticationService {

  private static final long serialVersionUID = 1L;

  String actorId = null;
  
  public void setActorId(String actorId) {
    this.actorId = actorId;
  }

  public String getActorId() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null)
      return auth.getName();
    else
      return actorId;
  }

  public void close() {
    actorId = null;
  }
}
