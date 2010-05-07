/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.integracio;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jbpm.JbpmContext;
import org.jbpm.command.AbstractGetObjectBaseCommand;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.def.Node.NodeType;
import org.jbpm.graph.exe.Token;

/**
 * Command per cercar tots els nodes que arriben al node a on
 * es troba un token determinat
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class FindArrivingNodeNamesCommand extends AbstractGetObjectBaseCommand {

	private static final long serialVersionUID = -1908847549444051495L;
	private long id;

	public FindArrivingNodeNamesCommand() {}

	public FindArrivingNodeNamesCommand(long id){
		super();
		this.id = id;
	}

	public Object execute(JbpmContext jbpmContext) throws Exception {
		List<String> resposta = new ArrayList<String>();
		Token token = jbpmContext.getToken(id);
		if (token != null) {
			Node node = token.getNode();
			if (!node.getNodeType().equals(NodeType.Join)) {
				for (Transition transition: token.getNode().getArrivingTransitions())
					resposta.add(transition.getFrom().getName());
			} else {
				int joinsLeft = 1;
				Node parentNode = node;
				do {
					Set<Transition> arrivingTransitions = parentNode.getArrivingTransitions();
					if (arrivingTransitions != null && arrivingTransitions.size() > 0) {
						parentNode = arrivingTransitions.iterator().next().getFrom();
						if (parentNode.getNodeType().equals(NodeType.Fork))
							joinsLeft--;
					} else {
						break;
					}
				} while(joinsLeft > 0);
				if (joinsLeft == 0)
					resposta.add(parentNode.getName());
			}
		}
		return resposta;
	}

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

	@Override
	public String getAdditionalToStringInformation() {
	    return "id=" + id;
	}

	// methods for fluent programming
	public FindArrivingNodeNamesCommand id(long id) {
		setId(id);
	    return this;
	}

}
