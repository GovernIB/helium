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
package org.jbpm.taskmgmt.exe;

import java.util.Date;
import java.util.Set;

import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.taskmgmt.def.Task;

/**
 * is one task instance that can be assigned to an actor (read: put in someones
 * task list) and that can trigger the coninuation of execution of the token
 * upon completion.
 */
public class MvTaskInstance{

	long id = 0;
	int version = 0;
	protected String description = null;
	protected String actorId = null;
	protected Date create = null;
	protected Date end = null;
	protected Date dueDate = null;
	protected int priority = Task.PRIORITY_NORMAL;
	protected boolean isSuspended = false;
	protected boolean isOpen = true;
	protected Task task = null;
	protected SwimlaneInstance swimlaneInstance = null;
	protected ProcessInstance processInstance = null;
	protected Set<PooledActor> pooledActors = null;



	/**
	 * gets the pool of actors for this task instance. If this task has a
	 * simlaneInstance and no pooled actors, the pooled actors of the swimlane
	 * instance are returned.
	 */
	public Set<PooledActor> getPooledActors() {
		if ((swimlaneInstance != null)
				&& ((pooledActors == null) || (pooledActors.isEmpty()))) {
			return swimlaneInstance.getPooledActors();
		}
		return pooledActors;
	}

	public boolean hasEnded() {
		return (end != null);
	}



	// getters and setters
	// //////////////////////////////////////////////////////

	public String getActorId() {
		return actorId;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public void setCreate(Date create) {
		this.create = create;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getCreate() {
		return create;
	}

	public Task getTask() {
		return task;
	}

	public SwimlaneInstance getSwimlaneInstance() {
		return swimlaneInstance;
	}

	public void setSwimlaneInstance(SwimlaneInstance swimlaneInstance) {
		this.swimlaneInstance = swimlaneInstance;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public boolean isOpen() {
		return isOpen;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isSuspended() {
		return isSuspended;
	}

	public ProcessInstance getProcessInstance() {
		return processInstance;
	}

	public void setProcessInstance(ProcessInstance processInstance) {
		this.processInstance = processInstance;
	}

}
