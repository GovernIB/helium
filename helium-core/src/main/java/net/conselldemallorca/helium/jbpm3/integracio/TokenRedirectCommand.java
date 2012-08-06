/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.integracio;

import java.util.Map;

import org.jbpm.JbpmContext;
import org.jbpm.command.AbstractGetObjectBaseCommand;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.Node.NodeType;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;
import org.jbpm.taskmgmt.exe.TaskInstance;

/**
 * Command per redirigir un token cap a un altre node
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class TokenRedirectCommand extends AbstractGetObjectBaseCommand {

	private static final long serialVersionUID = -1908847549444051495L;
	private long id;
	private String nodeName;
	private boolean cancelTasks = true;
	private boolean enterNodeIfTask = true;

	public TokenRedirectCommand() {}

	public TokenRedirectCommand(long id, String nodeName){
		super();
		this.id = id;
		this.nodeName = nodeName;
	}

	public Object execute(JbpmContext jbpmContext) throws Exception {
		Token token = jbpmContext.getToken(id);
		Map<String, Token> children = token.getChildren();
		Node desti = token.getProcessInstance().getProcessDefinition().getNode(nodeName);
		/*/ Verifica que es pugui fer la redirecció
		if (token.getEnd() != null)
			throw new JbpmException("Aquest token ja està finalitzat");
		if (	(!token.isRoot() && token.getNode().equals(token.getParent().getNode())) 
			||	desti.getNodeType().equals(NodeType.StartState))
			throw new JbpmException("Aquest token no es pot retrocedir més");
		for (String key: children.keySet()) {
			if (!children.get(key).hasEnded() && !children.get(key).getNode().equals(token.getNode()))
				throw new JbpmException("Retrocedeixi primer els fills d'aquest token");
		}*/
		// Si el token té fills actius els desactiva
		if (token.getChildren() != null) {
			for (String key: children.keySet()) {
				Token child = children.get(key);
				child.setAbleToReactivateParent(false);
				if (child.getEnd() == null && child.getId() != token.getId())
					child.end(false);
			}
		}
		// Cancel·la les tasques si s'ha de fer
		if (cancelTasks) {
			for (TaskInstance ti: token.getProcessInstance().getTaskMgmtInstance().getTaskInstances()) {
				if (ti.getToken().equals(token)) {
					if (ti.getEnd() == null) {
						ti.setSignalling(false);
						ti.cancel();
					}
				}
			}
		}
		// Fa la redirecció
		// v.2
		if (enterNodeIfTask && desti.getNodeType().equals(NodeType.Task)) {
			ExecutionContext exc = new ExecutionContext(token);
			desti.enter(exc);
		} else {
			token.setNode(desti);
		}
		// v.1
		/*token.setNode(desti);
		if (desti.getNodeType().equals(NodeType.Task)) {
			ExecutionContext exc = new ExecutionContext(token);
			desti.enter(exc);
		} else {
			token.setNode(desti);
		}*/
		// v.0
		/*if (desti.getNodeType().equals(NodeType.Task)) {
			Node origen = token.getNode();
			Transition transition = new Transition();
			transition.setFrom(origen);
			transition.setTo(desti);
			token.signal(transition);
		} else {
			token.setNode(desti);
		}*/
		return null;
	}

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getNodeName() {
		return nodeName;
	}
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	public boolean isCancelTasks() {
		return cancelTasks;
	}
	public void setCancelTasks(boolean cancelTasks) {
		this.cancelTasks = cancelTasks;
	}
	public boolean isEnterNodeIfTask() {
		return enterNodeIfTask;
	}
	public void setEnterNodeIfTask(boolean enterNodeIfTask) {
		this.enterNodeIfTask = enterNodeIfTask;
	}

	@Override
	public String getAdditionalToStringInformation() {
	    return "id=" + id;
	}

	// methods for fluent programming
	public TokenRedirectCommand id(long id, String nodeName) {
		setId(id);
		setNodeName(nodeName);
	    return this;
	}

}
