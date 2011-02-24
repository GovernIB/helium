/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import net.conselldemallorca.helium.integracio.plugins.registre.SeientRegistral;

import org.jbpm.JbpmException;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per a interactuar amb el registre d'entrada.
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@SuppressWarnings("serial")
public class RegistreEntradaHandler extends RegistreHandler {

	public void execute(ExecutionContext executionContext) throws Exception {
		if (!getPluginRegistreDao().isRegistreActiu())
			throw new JbpmException("El plugin de registre no està configurat");
		SeientRegistral dadesRegistre = getDadesRegistre(executionContext);
		String[] resultat = getPluginRegistreDao().registrarEntrada(dadesRegistre);
		guardarInfoRegistre(
				executionContext,
				dadesRegistre.getData(),
				dadesRegistre.getHora(),
				dadesRegistre.getOficina(),
				resultat[0],
				resultat[1],
				true);
	}

}
