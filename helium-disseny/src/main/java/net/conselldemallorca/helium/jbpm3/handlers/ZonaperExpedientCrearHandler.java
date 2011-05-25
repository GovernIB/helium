/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import net.conselldemallorca.helium.jbpm3.handlers.ZonaperExpedientCrearHandlerInterface;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per a crear expedients a dins la zona personal del
 * ciutadà.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class ZonaperExpedientCrearHandler implements ActionHandler, ZonaperExpedientCrearHandlerInterface {

	public void execute(ExecutionContext executionContext) throws Exception {}

	public void setDescripcio(String descripcio) {}
	public void setVarDescripcio(String varDescripcio) {}
	public void setAvisosHabilitat(String avisosHabilitat) {}
	public void setAvisosEmail(String avisosEmail) {}
	public void setAvisosSms(String avisosSms) {}
	public void setVarAvisosHabilitat(String varAvisosHabilitat) {}
	public void setVarAvisosEmail(String varAvisosEmail) {}
	public void setVarAvisosSms(String varAvisosSms) {}
	public void setIdioma(String idioma) {}
	public void setUnitatAdministrativa(String unitatAdministrativa) {}
	public void setRepresentantNif(String representantNif) {}
	public void setRepresentatNif(String representatNif) {}
	public void setRepresentatNom(String representatNom) {}
	public void setVarIdioma(String varIdioma) {}
	public void setVarUnitatAdministrativa(String varUnitatAdministrativa) {}
	public void setVarRepresentantNif(String varRepresentantNif) {}
	public void setVarRepresentatNif(String varRepresentatNif) {}
	public void setVarRepresentatNom(String varRepresentatNom) {}
	public void setComprovarExistencia(String comprovarExistencia) {}

}
