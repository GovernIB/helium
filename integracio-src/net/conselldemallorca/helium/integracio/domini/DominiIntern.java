/**
 * 
 */
package net.conselldemallorca.helium.integracio.domini;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;

import net.conselldemallorca.helium.integracio.plugins.persones.Persona;
import net.conselldemallorca.helium.model.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.model.dto.TascaDto;
import net.conselldemallorca.helium.model.hibernate.Area;
import net.conselldemallorca.helium.model.hibernate.Camp;
import net.conselldemallorca.helium.model.hibernate.CampRegistre;
import net.conselldemallorca.helium.model.hibernate.CampTasca;
import net.conselldemallorca.helium.model.hibernate.Carrec;
import net.conselldemallorca.helium.model.hibernate.Entorn;
import net.conselldemallorca.helium.model.hibernate.Camp.TipusCamp;
import net.conselldemallorca.helium.model.service.EntornService;
import net.conselldemallorca.helium.model.service.ExpedientService;
import net.conselldemallorca.helium.model.service.OrganitzacioService;
import net.conselldemallorca.helium.model.service.PluginService;
import net.conselldemallorca.helium.model.service.TascaService;
import net.conselldemallorca.helium.util.GlobalProperties;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Domini que implementa l'accés a dades internes de Helium
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@WebService(endpointInterface = "net.conselldemallorca.helium.integracio.domini.DominiHelium")
public class DominiIntern implements DominiHelium {

	private EntornService entornService;
	private TascaService tascaService;
	private ExpedientService expedientService;
	private OrganitzacioService organitzacioService;
	private PluginService pluginService;



	public List<FilaResultat> consultaDomini(
			String id,
			List<ParellaCodiValor> parametres) throws DominiHeliumException {
		Map<String, Object> parametersMap = getParametersMap(parametres);
		if ("PERSONA_AMB_CODI".equals(id)) {
			return personaAmbCodi(parametersMap);
		} else if ("PERSONES_AMB_AREA".equals(id)) {
			return personesAmbArea(parametersMap);
		} else if ("PERSONES_AMB_CARREC".equals(id)) {
			return personesAmbCarrec(parametersMap);
		} else if ("VARIABLE_REGISTRE".equals(id)) {
			return variableRegistre(parametersMap);
		}
		return new ArrayList<FilaResultat>();
	}



	@Autowired
	public void setEntornService(EntornService entornService) {
		this.entornService = entornService;
	}
	@Autowired
	public void setTascaService(TascaService tascaService) {
		this.tascaService = tascaService;
	}
	@Autowired
	public void setExpedientService(ExpedientService expedientService) {
		this.expedientService = expedientService;
	}
	@Autowired
	public void setOrganitzacioService(OrganitzacioService organitzacioService) {
		this.organitzacioService = organitzacioService;
	}
	@Autowired
	public void setPluginService(PluginService pluginService) {
		this.pluginService = pluginService;
	}



	private List<FilaResultat> personaAmbCodi(Map<String, Object> parametres) {
		List<FilaResultat> resposta = new ArrayList<FilaResultat>();
		Persona persona = pluginService.findPersonaAmbCodi((String)parametres.get("persona"));
		if (persona != null)
			resposta.add(novaFilaPersona(persona));
		return resposta;
	}
	private List<FilaResultat> personesAmbArea(Map<String, Object> parametres) {
		List<FilaResultat> resposta = new ArrayList<FilaResultat>();
		for (String personaCodi: getPersonesPerArea((String)parametres.get("entorn"), (String)parametres.get("area"))) {
			Persona persona = pluginService.findPersonaAmbCodi(personaCodi);
			if (persona != null)
				resposta.add(novaFilaPersona(persona));
		}
		return resposta;
	}
	private List<FilaResultat> personesAmbCarrec(Map<String, Object> parametres) {
		List<FilaResultat> resposta = new ArrayList<FilaResultat>();
		for (String personaCodi: getPersonesPerCarrec((String)parametres.get("entorn"), (String)parametres.get("carrec"))) {
			Persona persona = pluginService.findPersonaAmbCodi(personaCodi);
			if (persona != null)
				resposta.add(novaFilaPersona(persona));
		}
		return resposta;
	}
	@SuppressWarnings("unchecked")
	private List<FilaResultat> variableRegistre(Map<String, Object> parametres) {
		List<FilaResultat> resposta = new ArrayList<FilaResultat>();
		Long taskInstanceId = (Long)parametres.get("taskInstanceId");
		Long processInstanceId = (Long)parametres.get("processInstanceId");
		String variable = (String)parametres.get("variable");
		String filtreColumna = (String)parametres.get("filtreColumna");
		Object filtreValor = parametres.get("filtreValor");
		Object valor = null;
		Object valorText = null;
		Camp camp = null;
		if (taskInstanceId != null) {
			TascaDto tasca = tascaService.getByIdSenseComprovacio(taskInstanceId.toString());
			valor = tasca.getVariable(variable);
			valorText = tasca.getVarsComText().get(variable);
			for (CampTasca ct: tasca.getCamps()) {
				if (ct.getCamp().getCodi().equals(variable)) {
					camp = ct.getCamp();
					break;
				}
			}
		} else if (processInstanceId != null) {
			InstanciaProcesDto instanciaProces = expedientService.getInstanciaProcesById(processInstanceId.toString(), true);
			valor = instanciaProces.getVariable(variable);
			valorText = instanciaProces.getVarsComText().get(variable);
			for (Camp c: instanciaProces.getCamps()) {
				if (c.getCodi().equals(variable)) {
					camp = c;
					break;
				}
			}
		}
		if (valor != null && valor instanceof Object[] && camp.getTipus().equals(TipusCamp.REGISTRE)) {
			Object[] registres = (Object[])valor;
			List<String[]> registresText = (List<String[]>)valorText;
			int indexFila = 0;
			for (int i = 0; i < registres.length; i++) {
				Object[] valors = (Object[])registres[i];
				String[] texts = registresText.get(i);
				boolean incloureAquest = true;
				if (filtreColumna != null) {
					incloureAquest = false;
					int indexColumna = 0;
					for (CampRegistre campRegistre: camp.getRegistreMembres()) {
						if (filtreColumna.equals(campRegistre.getMembre().getCodi())) {
							if (valors[indexColumna] == null && filtreValor == null)
								incloureAquest = true;
							else if (valors[indexColumna] != null && valors[indexColumna].equals(filtreValor))
								incloureAquest = true;
							else if (filtreValor != null && filtreValor.equals(valors[indexColumna]))
								incloureAquest = true;
							break;
						}
					}
				}
				if (incloureAquest) {
					FilaResultat fila = new FilaResultat();
					fila.addColumna(new ParellaCodiValor("_index", indexFila++));
					int indexColumna = 0;
					for (CampRegistre campRegistre: camp.getRegistreMembres()) {
						fila.addColumna(new ParellaCodiValor("_valor_" + campRegistre.getMembre().getCodi(), valors[indexColumna]));
						fila.addColumna(new ParellaCodiValor(campRegistre.getMembre().getCodi(), texts[indexColumna++]));
					}
					resposta.add(fila);
				}
			}
		}
		return resposta;
	}

	private FilaResultat novaFilaPersona(Persona persona) {
		FilaResultat resposta = new FilaResultat();
		resposta.addColumna(new ParellaCodiValor("codi", persona.getCodi()));
		resposta.addColumna(new ParellaCodiValor("nom", persona.getNom()));
		resposta.addColumna(new ParellaCodiValor("llinatge1", persona.getLlinatge1()));
		resposta.addColumna(new ParellaCodiValor("llinatge2", persona.getLlinatge2()));
		resposta.addColumna(new ParellaCodiValor("dni", persona.getDni()));
		resposta.addColumna(new ParellaCodiValor("sexe", persona.getSexe().name()));
		resposta.addColumna(new ParellaCodiValor("email", persona.getEmail()));
		resposta.addColumna(new ParellaCodiValor("nomSencer", persona.getNomSencer()));
		return resposta;
	}

	private List<String> getPersonesPerArea(String entornCodi, String areaCodi) {
		if (isHeliumIdentitySource()) {
			Entorn entorn = entornService.findAmbCodi(entornCodi);
			Area area = organitzacioService.findAreaAmbEntornICodi(entorn.getId(), areaCodi);
			return organitzacioService.findCodisPersonaAmbArea(area.getId());
		} else {
			return organitzacioService.findCodisPersonaAmbJbpmIdGroup(areaCodi);
		}
	}

	private List<String> getPersonesPerCarrec(String entornCodi, String carrecCodi) {
		if (isHeliumIdentitySource()) {
			List<String> resposta = new ArrayList<String>();
			Entorn entorn = entornService.findAmbCodi(entornCodi);
			Carrec carrec = organitzacioService.findCarrecAmbEntornICodi(entorn.getId(), carrecCodi);
			if (carrec != null)
				resposta.add(carrec.getPersonaCodi());
			return resposta;
		} else {
			return organitzacioService.findCodisPersonaAmbJbpmIdCarrec(carrecCodi);
		}
	}

	private Map<String, Object> getParametersMap(List<ParellaCodiValor> parametres) {
		Map<String, Object> resposta = new HashMap<String, Object>();
		if (parametres != null) {
			for (ParellaCodiValor parella: parametres)
				resposta.put(parella.getCodi(), parella.getValor());
		}
		return resposta;
	}
	private boolean isHeliumIdentitySource() {
		String organigramaActiu = GlobalProperties.getInstance().getProperty("app.jbpm.identity.source");
		return "helium".equalsIgnoreCase(organigramaActiu);
	}

}
