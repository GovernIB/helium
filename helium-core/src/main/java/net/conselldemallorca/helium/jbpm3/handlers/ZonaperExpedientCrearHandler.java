/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import net.conselldemallorca.helium.core.model.dto.ExpedientDto;
import net.conselldemallorca.helium.core.model.dto.ExpedientIniciantDto;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.Expedient.IniciadorTipus;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.PublicarExpedientRequest;

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
		Expedient ex = ExpedientIniciantDto.getExpedient();
		if (ex != null) {
			getExpedientService().publicarExpedient(
					ex,
					getPublicarExpedientRequest(
							executionContext,
							ex));
		} else {
			ExpedientDto expedient = getExpedient(executionContext);
			if (!isComprovarExistencia() || expedient.getTramitExpedientIdentificador() == null) {
				getExpedientService().publicarExpedient(
						expedient,
						getPublicarExpedientRequest(
								executionContext,
								expedient));
			}
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



	private PublicarExpedientRequest getPublicarExpedientRequest(
			ExecutionContext executionContext,
			Expedient expedient) throws JbpmException {
		PublicarExpedientRequest request = new PublicarExpedientRequest();
		if (IniciadorTipus.SISTRA.equals(expedient.getIniciadorTipus())) {
			request.setIdioma(expedient.getIdioma());
			request.setUnitatAdministrativa(expedient.getUnitatAdministrativa());
			request.setTramitNumero(expedient.getNumeroEntradaSistra());
			request.setAutenticat(expedient.isAutenticat());
			request.setRepresentantNif(expedient.getRepresentantNif());
			request.setRepresentatNif(expedient.getInteressatNif());
			request.setRepresentatNom(expedient.getInteressatNom());
		} else {
			request.setIdioma(
					(String)getValorOVariable(
							executionContext,
							idioma,
							varIdioma));
			String ua = (String)getValorOVariable(
						executionContext,
						unitatAdministrativa,
						varUnitatAdministrativa);
			if (ua != null)
				request.setUnitatAdministrativa(
						Long.parseLong(ua));
			request.setAutenticat(true);
			request.setRepresentantNif(
					(String)getValorOVariable(
							executionContext,
							representantNif,
							varRepresentantNif));
			request.setRepresentatNif(
					(String)getValorOVariable(
							executionContext,
							representatNif,
							varRepresentatNif));
			request.setRepresentatNom(
					(String)getValorOVariable(
							executionContext,
							representatNom,
							varRepresentatNom));
		}
		String desc = (String)getValorOVariable(
				executionContext,
				descripcio,
				varDescripcio);
		request.setDescripcio(desc);
		String ah = (String)getValorOVariable(
				executionContext,
				avisosHabilitat,
				varAvisosHabilitat);
		if (ah != null) {
			request.setAvisosHabilitat(
					"S".equalsIgnoreCase(ah) || "true".equalsIgnoreCase(ah));
			request.setAvisosEmail(
					(String)getValorOVariable(
							executionContext,
							avisosEmail,
							varAvisosEmail));
			request.setAvisosSMS(
					(String)getValorOVariable(
							executionContext,
							avisosSms,
							varAvisosSms));
		} else {
			request.setAvisosHabilitat(expedient.isAvisosHabilitats());
			request.setAvisosEmail(expedient.getAvisosEmail());
			request.setAvisosSMS(expedient.getAvisosMobil());
		}
		return request;
	}

	private boolean isComprovarExistencia() {
		return "true".equalsIgnoreCase(comprovarExistencia) || "S".equalsIgnoreCase(comprovarExistencia);
	}

}
