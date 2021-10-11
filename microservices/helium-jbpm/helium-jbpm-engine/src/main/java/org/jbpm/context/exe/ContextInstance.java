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
package org.jbpm.context.exe;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.conselldemallorca.helium.jbpm3.integracio.Jbpm3HeliumBridge;
import org.jbpm.JbpmException;
import org.jbpm.graph.exe.Token;
import org.jbpm.module.exe.ModuleInstance;

/**
 * maintains all the key-variable pairs for a process instance. You can obtain a
 * ContextInstance from a processInstance from a process instance like this :
 * <pre>
 * ProcessInstance processInstance = ...;
 * ContextInstance contextInstance = processInstance.getContextInstance();
 * </pre>
 * More information on context and process variableInstances can be found in
 * <a href="../../../../../userguide/en/html/reference.html#context">the userguide, section context</a>
 */
public class ContextInstance extends ModuleInstance
{

    private static final long serialVersionUID = 1L;

    // maps Token's to TokenVariableMap's
    protected Map tokenVariableMaps = null;
    // maps variablenames (String) to values (Object)
    protected transient Map transientVariables = null;
    protected transient List updatedVariableContainers = null;

    public ContextInstance()
    {
    }

    // normal variableInstances (persistent) ////////////////////////////////////

    /*
     * creates a variable on the root-token (= process-instance scope) and calculates the actual VariableInstance-type from the value.
     */
    public void createVariable(String name, Object value)
    {
        setVariableLocally(name, value, getRootToken());
    }

    /*
     * sets the variable on the root token, creates the variable if necessary and calculates the actual VariableInstance-type from the value.
     */
    public void setVariableLocally(String name, Object value)
    {
        setVariableLocally(name, value, getRootToken());
    }

    /*
     * creates a variable in the scope of the given token and calculates the actual VariableInstance-type from the value.
     */
    public void createVariable(String name, Object value, Token token)
    {
        setVariableLocally(name, value, token);
    }

    /*
     * creates a variable in the scope of the given token and calculates the actual VariableInstance-type from the value.
     */
    public void setVariableLocally(String name, Object value, Token token)
    {
        TokenVariableMap tokenVariableMap = createTokenVariableMap(token);
        tokenVariableMap.setVariableLocally(name, value);
    }

    /*
     * gets all the variableInstances on the root-token (= process-instance scope).
     */
    public Map getVariables()
    {
        return getVariables(getRootToken());
    }

    /*
     * retrieves all the variableInstances in scope of the given token.
     */
    public Map getVariables(Token token)
    {
        Map variables = null;

//        Jbpm3HeliumBridge.getInstanceService().

        TokenVariableMap tokenVariableMap = getTokenVariableMap(token);
        if (tokenVariableMap != null)
        {
            variables = tokenVariableMap.getVariables();
        }

        return variables;
    }

    /*
     * adds all the variableInstances on the root-token (= process-instance scope).
     */
    public void addVariables(Map variables)
    {
        setVariables(variables, getRootToken());
    }

    /*
     * The method setVariables is the same as the {@link #addVariables(Map, Token)}, but it was added for more consistency.
     */
    public void setVariables(Map variables)
    {
        setVariables(variables, getRootToken());
    }

    /*
     * adds all the variableInstances to the scope of the given token. This method delegates to {@link #setVariables(Map, Token)}. The method setVariables was added for
     * more consistency.
     */
    public void addVariables(Map variables, Token token)
    {
        setVariables(variables, token);
    }

    /*
     * adds all the variableInstances to the scope of the given token. The method setVariables is the same as the {@link #addVariables(Map, Token)}, but it was added for
     * more consistency.
     */
    public void setVariables(Map variables, Token token)
    {
        // [JBPM-1778] Empty map variables on process creation is set as null
        TokenVariableMap tokenVariableMap = getOrCreateTokenVariableMap(token);
        Iterator iter = variables.entrySet().iterator();
        while (iter.hasNext())
        {
            Map.Entry entry = (Map.Entry)iter.next();
            String name = (String)entry.getKey();
            Object value = entry.getValue();
            tokenVariableMap.setVariable(name, value);
        }
    }

    /*
     * gets the variable with the given name on the root-token (= process-instance scope).
     */
    public Object getVariable(String name)
    {
        return getVariable(name, getRootToken());
    }

    /*
     * retrieves a variable in the scope of the token. If the given token does not have a variable for the given name, the variable is searched for up the token
     * hierarchy.
     */
    public Object getVariable(String name, Token token)
    {
        Object variable = null;
        TokenVariableMap tokenVariableMap = getTokenVariableMap(token);
        if (tokenVariableMap != null)
        {
            variable = tokenVariableMap.getVariable(name);
        }
        return variable;
    }

    /*
     * retrieves a variable which is local to the token. Method {@link #getVariableLocally(String, Token)} is the same as this method and it was added for naming
     * consistency.
     */
    public Object getLocalVariable(String name, Token token)
    {
        return getVariableLocally(name, token);
    }

    /*
     * retrieves a variable which is local to the token. this method was added for naming consistency. it is the same as {@link #getLocalVariable(String, Token)}.
     */
    public Object getVariableLocally(String name, Token token)
    {
        Object variable = null;
        if (tokenVariableMaps != null && tokenVariableMaps.containsKey(token))
        {
            TokenVariableMap tokenVariableMap = (TokenVariableMap)tokenVariableMaps.get(token);
            if (tokenVariableMap != null)
            {
                variable = tokenVariableMap.getVariableLocally(name);
            }
        }
        return variable;
    }

    /*
     * sets a variable on the process instance scope.
     */
    public void setVariable(String name, Object value)
    {
        setVariable(name, value, getRootToken());
    }

    /*
     * sets a variable. If a variable exists in the scope given by the token, that variable is updated. Otherwise, the variable is created on the root token (=process
     * instance scope).
     */
    public void setVariable(String name, Object value, Token token)
    {
        TokenVariableMap tokenVariableMap = getOrCreateTokenVariableMap(token);
        tokenVariableMap.setVariable(name, value);
    }

    /*
     * checks if a variable is present with the given name on the root-token (= process-instance scope).
     */
    public boolean hasVariable(String name)
    {
        return hasVariable(name, getRootToken());
    }

    /*
     * checks if a variable is present with the given name in the scope of the token.
     */
    public boolean hasVariable(String name, Token token)
    {
        boolean hasVariable = false;
        TokenVariableMap tokenVariableMap = getTokenVariableMap(token);
        if (tokenVariableMap != null)
        {
            hasVariable = tokenVariableMap.hasVariable(name);
        }
        return hasVariable;
    }

    /*
     * deletes the given variable on the root-token (=process-instance scope).
     */
    public void deleteVariable(String name)
    {
        deleteVariable(name, getRootToken());
    }

    /*
     * deletes a variable from the given token. For safety reasons, this method does not propagate the deletion to parent tokens in case the given token does not contain
     * the variable.
     */
    public void deleteVariable(String name, Token token)
    {
        TokenVariableMap tokenVariableMap = getTokenVariableMap(token);
        if (tokenVariableMap != null)
        {
            tokenVariableMap.deleteVariable(name);
        }
    }

    // transient variableInstances //////////////////////////////////////////////

    /*
     * retrieves the transient variable for the given name.
     */
    public Object getTransientVariable(String name)
    {
        Object transientVariable = null;
        if (transientVariables != null)
        {
            transientVariable = transientVariables.get(name);
        }
        return transientVariable;
    }

    /*
     * sets the transient variable for the given name to the given value.
     */
    public void setTransientVariable(String name, Object value)
    {
        if (transientVariables == null)
        {
            transientVariables = new HashMap();
        }
        transientVariables.put(name, value);
    }

    /*
     * tells if a transient variable with the given name is present.
     */
    public boolean hasTransientVariable(String name)
    {
        if (transientVariables == null)
        {
            return false;
        }
        return transientVariables.containsKey(name);
    }

    /*
     * retrieves all the transient variableInstances map. note that no deep copy is performed, changing the map leads to changes in the transient variableInstances of
     * this context instance.
     */
    public Map getTransientVariables()
    {
        return transientVariables;
    }

    /*
     * replaces the transient variableInstances with the given map.
     */
    public void setTransientVariables(Map transientVariables)
    {
        this.transientVariables = transientVariables;
    }

    /*
     * removes the transient variable.
     */
    public void deleteTransientVariable(String name)
    {
        if (transientVariables == null)
            return;
        transientVariables.remove(name);
    }

    Token getRootToken()
    {
        return processInstance.getRootToken();
    }

    /*
     * searches for the first token-variable-map for the given token and creates it on the root token if it doesn't exist.
     */
    public TokenVariableMap getOrCreateTokenVariableMap(Token token)
    {
        if (token == null)
        {
            throw new JbpmException("can't get variables for token 'null'");
        }

        // if the given token has a variable map
        TokenVariableMap tokenVariableMap = null;
        if ((tokenVariableMaps != null) && (tokenVariableMaps.containsKey(token)))
        {
            tokenVariableMap = (TokenVariableMap)tokenVariableMaps.get(token);

        }
        else if (!token.isRoot())
        {
            tokenVariableMap = getOrCreateTokenVariableMap(token.getParent());

        }
        else
        {
            tokenVariableMap = createTokenVariableMap(token);
        }

        return tokenVariableMap;
    }

    TokenVariableMap createTokenVariableMap(Token token)
    {
        if (tokenVariableMaps == null)
        {
            tokenVariableMaps = new HashMap();
        }
        TokenVariableMap tokenVariableMap = (TokenVariableMap)tokenVariableMaps.get(token);
        if (tokenVariableMap == null)
        {
            tokenVariableMap = new TokenVariableMap(token, this);
            tokenVariableMaps.put(token, tokenVariableMap);
        }
        return tokenVariableMap;
    }

    /*
     * looks for the first token-variable-map that is found up the token-parent hirarchy.
     */
    public TokenVariableMap getTokenVariableMap(Token token)
    {
        TokenVariableMap tokenVariableMap = null;
        if (tokenVariableMaps != null)
        {
            if (tokenVariableMaps.containsKey(token))
            {
                tokenVariableMap = (TokenVariableMap)tokenVariableMaps.get(token);
            }
            else if (!token.isRoot())
            {
                tokenVariableMap = getTokenVariableMap(token.getParent());
            }
        }
        return tokenVariableMap;
    }

    public VariableInstance getVariableInstance(String name, Token token)
    {
        VariableInstance variableInstance = null;
        TokenVariableMap tokenVariableMap = getTokenVariableMap(token);
        if (tokenVariableMap != null)
        {
            tokenVariableMap.getVariableInstances();
        }
        return variableInstance;
    }

    public Map getTokenVariableMaps()
    {
        return tokenVariableMaps;
    }
}
