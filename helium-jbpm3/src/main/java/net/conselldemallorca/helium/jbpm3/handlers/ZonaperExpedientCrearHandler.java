/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import net.conselldemallorca.helium.jbpm3.handlers.tipus.ExpedientInfo.IniciadorTipus;
import net.conselldemallorca.helium.jbpm3.integracio.Jbpm3HeliumBridge;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.ZonaperExpedientDto;

import org.jbpm.JbpmException;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per a crear expedients a dins la zona personal del
 * ciutadà.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class ZonaperExpedientCrearHandler extends AbstractHeliumActionHandler implements ZonaperExpedientCrearHandlerInterface {

	private String descripcio;
	private String varDescripcio;

	private String avisosHabilitat;
	private String avisosEmail;
	private String avisosSms;
	private String varAvisosHabilitat;
	private String varAvisosEmail;
	private String varAvisosSms;

	private String idioma;
	private String unitatAdministrativa;
	private String representantNif;
	private String representatNif;
	private String representatNom;
	private String varIdioma;
	private String varUnitatAdministrativa;
	private String varRepresentantNif;
	private String varRepresentatNif;
	private String varRepresentatNom;

	private String comprovarExistencia;



	public void execute(ExecutionContext executionContext) throws Exception {
		ExpedientDto expedient = getExpedientActual(executionContext);
		if (!isComprovarExistencia() || expedient.getTramitExpedientIdentificador() == null) {
			Jbpm3HeliumBridge.getInstanceService().zonaperExpedientCrear(
					expedient,
					getProcessInstanceId(executionContext),
					construirExpedient(
							executionContext,
							expedient));
		}
	}

	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}
	public void setVarDescripcio(String varDescripcio) {
		this.varDescripcio = varDescripcio;
	}
	public void setAvisosHabilitat(String avisosHabilitat) {
		this.avisosHabilitat = avisosHabilitat;
	}
	public void setAvisosEmail(String avisosEmail) {
		this.avisosEmail = avisosEmail;
	}
	public void setAvisosSms(String avisosSms) {
		this.avisosSms = avisosSms;
	}
	public void setVarAvisosHabilitat(String varAvisosHabilitat) {
		this.varAvisosHabilitat = varAvisosHabilitat;
	}
	public void setVarAvisosEmail(String varAvisosEmail) {
		this.varAvisosEmail = varAvisosEmail;
	}
	public void setVarAvisosSms(String varAvisosSms) {
		this.varAvisosSms = varAvisosSms;
	}
	public void setIdioma(String idioma) {
		this.idioma = idioma;
	}
	public void setUnitatAdministrativa(String unitatAdministrativa) {
		this.unitatAdministrativa = unitatAdministrativa;
	}
	public void setRepresentantNif(String representantNif) {
		this.representantNif = representantNif;
	}
	public void setRepresentatNif(String representatNif) {
		this.representatNif = representatNif;
	}
	public void setRepresentatNom(String representatNom) {
		this.representatNom = representatNom;
	}
	public void setVarIdioma(String varIdioma) {
		this.varIdioma = varIdioma;
	}
	public void setVarUnitatAdministrativa(String varUnitatAdministrativa) {
		this.varUnitatAdministrativa = varUnitatAdministrativa;
	}
	public void setVarRepresentantNif(String varRepresentantNif) {
		this.varRepresentantNif = varRepresentantNif;
	}
	public void setVarRepresentatNif(String varRepresentatNif) {
		this.varRepresentatNif = varRepresentatNif;
	}
	public void setVarRepresentatNom(String varRepresentatNom) {
		this.varRepresentatNom = varRepresentatNom;
	}
	public void setComprovarExistencia(String comprovarExistencia) {
		this.comprovarExistencia = comprovarExistencia;
	}



	private ZonaperExpedientDto construirExpedient(
			ExecutionContext executionContext,
			ExpedientDto expedient) throws JbpmException {
		ZonaperExpedientDto zonaperExpedient = new ZonaperExpedientDto();
		if (IniciadorTipus.SISTRA.equals(expedient.getIniciadorTipus())) {
			zonaperExpedient.setIdioma(expedient.getIdioma());
			zonaperExpedient.setUnitatAdministrativa(expedient.getUnitatAdministrativa());
			zonaperExpedient.setTramitNumero(expedient.getNumeroEntradaSistra());
			zonaperExpedient.setAutenticat(expedient.isAutenticat());
			zonaperExpedient.setRepresentantNif(expedient.getRepresentantNif());
			zonaperExpedient.setRepresentatNif(expedient.getInteressatNif());
			zonaperExpedient.setRepresentatNom(expedient.getInteressatNom());
		} else {
			zonaperExpedient.setIdioma(
					(String)getValorOVariable(
							executionContext,
							idioma,
							varIdioma));
			String ua = (String)getValorOVariable(
						executionContext,
						unitatAdministrativa,
						varUnitatAdministrativa);
			if (ua != null)
				zonaperExpedient.setUnitatAdministrativa(
						Long.parseLong(ua));
			zonaperExpedient.setAutenticat(true);
			zonaperExpedient.setRepresentantNif(
					(String)getValorOVariable(
							executionContext,
							representantNif,
							varRepresentantNif));
			zonaperExpedient.setRepresentatNif(
					(String)getValorOVariable(
							executionContext,
							representatNif,
							varRepresentatNif));
			zonaperExpedient.setRepresentatNom(
					(String)getValorOVariable(
							executionContext,
							representatNom,
							varRepresentatNom));
		}
		String desc = (String)getValorOVariable(
				executionContext,
				descripcio,
				varDescripcio);
		zonaperExpedient.setDescripcio(desc);
		String ah = (String)getValorOVariable(
				executionContext,
				avisosHabilitat,
				varAvisosHabilitat);
		if (ah != null) {
			zonaperExpedient.setAvisosHabilitat(
					"S".equalsIgnoreCase(ah) || "true".equalsIgnoreCase(ah));
			zonaperExpedient.setAvisosEmail(
					(String)getValorOVariable(
							executionContext,
							avisosEmail,
							varAvisosEmail));
			zonaperExpedient.setAvisosSMS(
					(String)getValorOVariable(
							executionContext,
							avisosSms,
							varAvisosSms));
		} else {
			zonaperExpedient.setAvisosHabilitat(expedient.isAvisosHabilitats());
			zonaperExpedient.setAvisosEmail(expedient.getAvisosEmail());
			zonaperExpedient.setAvisosSMS(expedient.getAvisosMobil());
		}
		return zonaperExpedient;
	}

	private boolean isComprovarExistencia() {
		return "true".equalsIgnoreCase(comprovarExistencia) || "S".equalsIgnoreCase(comprovarExistencia);
	}

}
