/**
 * 
 */
package net.conselldemallorca.helium.ws.domini;

import java.util.List;

import javax.jws.WebService;

import org.springframework.beans.factory.annotation.Autowired;

import es.caib.emiserv.logic.intf.extern.domini.DominiHelium;
import es.caib.emiserv.logic.intf.extern.domini.FilaResultat;
import es.caib.emiserv.logic.intf.extern.domini.ParellaCodiValor;
import es.caib.helium.logic.intf.service.DissenyService;

/**
 * Domini que implementa l'accés a dades internes de Helium
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@WebService(endpointInterface = "net.conselldemallorca.helium.core.extern.domini.DominiHelium")
public class DominiIntern implements DominiHelium {

//	private EntornService entornService;
//	private TascaService tascaService;
//	private ExpedientService expedientService;
//	private OrganitzacioService organitzacioService;
//	private PluginService pluginService;
	private DissenyService dissenyService;



	public List<FilaResultat> consultaDomini(
			String id,
			List<ParellaCodiValor> parametres) throws Exception {
//		Map<String, Object> parametersMap = getParametersMap(parametres);
//		if ("PERSONA_AMB_CODI".equals(id)) {
//			return personaAmbCodi(parametersMap);
//		} else if ("PERSONES_AMB_AREA".equals(id)) {
//			return personesAmbArea(parametersMap);
//		} else if ("PERSONA_AMB_CARREC_AREA".equals(id)) {
//			return personesAmbCarrecArea(parametersMap, true);
//		} else if ("PERSONES_AMB_CARREC_AREA".equals(id)) {
//			return personesAmbCarrecArea(parametersMap, false);
//		} else  if ("AREES_AMB_PARE".equals(id)) {
//			return areesAmbPare(parametersMap);
//		} else if ("VARIABLE_REGISTRE".equals(id)) {
//			return variableRegistre(parametersMap);
//		} else if ("AREES_AMB_PERSONA".equals(id)) {
//			return areesAmbPersona(parametersMap);
//		} else if ("ROLS_PER_USUARI".equals(id)) {
//			return rolsPerUsuari(parametersMap);
//		} else if ("USUARIS_PER_ROL".equals(id)) {
//			return usuarisPerRol(parametersMap);
//		/* Per suprimir */
//		} else if ("PERSONES_AMB_CARREC".equals(id)) {
//			return personesAmbCarrec(parametersMap);
//		}
//		return new ArrayList<FilaResultat>();
		return dissenyService.consultaDominiIntern(id, parametres);
	}

//	@Autowired
//	public void setEntornService(EntornService entornService) {
//		this.entornService = entornService;
//	}
//	@Autowired
//	public void setTascaService(TascaService tascaService) {
//		this.tascaService = tascaService;
//	}
//	@Autowired
//	public void setExpedientService(ExpedientService expedientService) {
//		this.expedientService = expedientService;
//	}
//	@Autowired
//	public void setOrganitzacioService(OrganitzacioService organitzacioService) {
//		this.organitzacioService = organitzacioService;
//	}
//	@Autowired
//	public void setPluginService(PluginService pluginService) {
//		this.pluginService = pluginService;
//	}
	@Autowired
	public void setDissenyService(DissenyService dissenyService) {
		this.dissenyService = dissenyService;
	}

	
//	private List<FilaResultat> personaAmbCodi(Map<String, Object> parametres) {
//		List<FilaResultat> resposta = new ArrayList<FilaResultat>();
//		PersonaDto persona = pluginService.findPersonaAmbCodi((String)parametres.get("persona"));
//		if (persona != null)
//			resposta.add(novaFilaPersona(persona));
//		return resposta;
//	}
//	private List<FilaResultat> personesAmbArea(Map<String, Object> parametres) {
//		List<FilaResultat> resposta = new ArrayList<FilaResultat>();
//		for (String personaCodi: getPersonesPerArea((String)parametres.get("entorn"), (String)parametres.get("area"))) {
//			PersonaDto persona = pluginService.findPersonaAmbCodi(personaCodi);
//			if (persona != null)
//				resposta.add(novaFilaPersona(persona));
//		}
//		return resposta;
//	}
//	private List<FilaResultat> personesAmbCarrecArea(Map<String, Object> parametres, boolean nomesUna) {
//		List<FilaResultat> resposta = new ArrayList<FilaResultat>();
//		List<String> personaCodis = getPersonesPerAreaCarrec(
//				(String)parametres.get("entorn"),
//				(String)parametres.get("area"),
//				(String)parametres.get("carrec"));
//		if (personaCodis != null) {
//			for (String personaCodi: personaCodis) {
//				PersonaDto persona = pluginService.findPersonaAmbCodi(personaCodi);
//				if (persona != null) {
//					resposta.add(novaFilaPersona(persona));
//					if (nomesUna)
//						break;
//				}
//			}
//		}
//		return resposta;
//	}
//	private List<FilaResultat> areesAmbPare(Map<String, Object> parametres) {
//		List<FilaResultat> resposta = new ArrayList<FilaResultat>();
//		for (Area area: getAreesAmbPare((String)parametres.get("entorn"), (String)parametres.get("pare"))) {
//			resposta.add(novaFilaArea(area));
//		}
//		return resposta;
//	}
//	@SuppressWarnings("unchecked")
//	private List<FilaResultat> variableRegistre(Map<String, Object> parametres) {
//		List<FilaResultat> resposta = new ArrayList<FilaResultat>();
//		Long taskInstanceId = (Long)parametres.get("taskInstanceId");
//		Long processInstanceId = (Long)parametres.get("processInstanceId");
//		String variable = (String)parametres.get("variable");
//		String filtreColumna = (String)parametres.get("filtreColumna");
//		Object filtreValor = parametres.get("filtreValor");
//		Object valor = null;
//		Object valorText = null;
//		Camp camp = null;
//		if (taskInstanceId != null) {
//			TascaDto tasca = tascaService.getByIdSenseComprovacio(taskInstanceId.toString());
//			valor = tasca.getVariable(variable);
//			valorText = tasca.getVarsComText().get(variable);
//			for (CampTasca ct: tasca.getCamps()) {
//				if (ct.getCamp().getCodi().equals(variable)) {
//					camp = ct.getCamp();
//					break;
//				}
//			}
//		} else if (processInstanceId != null) {
//			InstanciaProcesDto instanciaProces = expedientService.getInstanciaProcesById(processInstanceId.toString(), true, true, true);
//			valor = instanciaProces.getVariable(variable);
//			valorText = instanciaProces.getVarsComText().get(variable);
//			for (Camp c: instanciaProces.getCamps()) {
//				if (c.getCodi().equals(variable)) {
//					camp = c;
//					break;
//				}
//			}
//		}
//		if (valor != null && valor instanceof Object[] && camp.getTipus().equals(TipusCamp.REGISTRE)) {
//			Object[] registres = (Object[])valor;
//			List<String[]> registresText = (List<String[]>)valorText;
//			int indexFila = 0;
//			for (int i = 0; i < registres.length; i++) {
//				Object[] valors = (Object[])registres[i];
//				String[] texts = registresText.get(i);
//				boolean incloureAquest = true;
//				if (filtreColumna != null) {
//					incloureAquest = false;
//					int indexColumna = 0;
//					for (CampRegistre campRegistre: camp.getRegistreMembres()) {
//						if (filtreColumna.equals(campRegistre.getMembre().getCodi())) {
//							if (valors[indexColumna] == null && filtreValor == null)
//								incloureAquest = true;
//							else if (valors[indexColumna] != null && valors[indexColumna].equals(filtreValor))
//								incloureAquest = true;
//							else if (filtreValor != null && filtreValor.equals(valors[indexColumna]))
//								incloureAquest = true;
//							break;
//						}
//					}
//				}
//				if (incloureAquest) {
//					FilaResultat fila = new FilaResultat();
//					fila.addColumna(new ParellaCodiValor("_index", indexFila++));
//					int indexColumna = 0;
//					for (CampRegistre campRegistre: camp.getRegistreMembres()) {
//						fila.addColumna(new ParellaCodiValor("_valor_" + campRegistre.getMembre().getCodi(), valors[indexColumna]));
//						fila.addColumna(new ParellaCodiValor(campRegistre.getMembre().getCodi(), texts[indexColumna++]));
//					}
//					resposta.add(fila);
//				}
//			}
//		}
//		return resposta;
//	}
//
//	private List<FilaResultat> areesAmbPersona(Map<String, Object> parametres) {
//		List<FilaResultat> resposta = new ArrayList<FilaResultat>();
//		for (String grupCodi: getGrupsPerPersona((String)parametres.get("persona"))) {
//			FilaResultat fila = new FilaResultat();
//			fila.addColumna(new ParellaCodiValor("codi", grupCodi));
//			resposta.add(fila);
//		}
//		return resposta;
//	}
//	
//	private List<FilaResultat> usuarisPerRol(Map<String, Object> parametres) {
//		List<FilaResultat> resposta = new ArrayList<FilaResultat>();
//		for (String personaCodi: organitzacioService.findCodisPersonaAmbJbpmIdGroup((String)parametres.get("rol"))) {
//			PersonaDto persona = pluginService.findPersonaAmbCodi(personaCodi);
//			if (persona != null)
//				resposta.add(novaFilaPersona(persona));
//		}
//		return resposta;
//	}
//
//	private List<FilaResultat> rolsPerUsuari(Map<String, Object> parametres) {
//		List<FilaResultat> resposta = new ArrayList<FilaResultat>();
//		for (String rol: organitzacioService.findRolsJbpmIdMembre((String)parametres.get("persona"))) {
//			FilaResultat fila = new FilaResultat();
//			fila.addColumna(new ParellaCodiValor("rol", rol));
//			resposta.add(fila);
//		}
//		return resposta;
//	}
//	
//	/* Per suprimir */
//	private List<FilaResultat> personesAmbCarrec(Map<String, Object> parametres) {
//		List<FilaResultat> resposta = new ArrayList<FilaResultat>();
//		for (String personaCodi: getPersonesPerCarrec((String)parametres.get("entorn"), (String)parametres.get("carrec"))) {
//			PersonaDto persona = pluginService.findPersonaAmbCodi(personaCodi);
//			if (persona != null)
//				resposta.add(novaFilaPersona(persona));
//		}
//		return resposta;
//	}
//
//	private FilaResultat novaFilaPersona(PersonaDto persona) {
//		FilaResultat resposta = new FilaResultat();
//		resposta.addColumna(new ParellaCodiValor("codi", persona.getCodi()));
//		resposta.addColumna(new ParellaCodiValor("nom", persona.getNom()));
//		resposta.addColumna(new ParellaCodiValor("llinatge1", persona.getLlinatge1()));
//		resposta.addColumna(new ParellaCodiValor("llinatge2", persona.getLlinatge2()));
//		resposta.addColumna(new ParellaCodiValor("dni", persona.getDni()));
//		resposta.addColumna(new ParellaCodiValor("sexe", persona.getSexe().name()));
//		resposta.addColumna(new ParellaCodiValor("email", persona.getEmail()));
//		resposta.addColumna(new ParellaCodiValor("nomSencer", persona.getNomSencer()));
//		return resposta;
//	}
//	
//	private FilaResultat novaFilaArea(Area area) {
//		FilaResultat resposta = new FilaResultat();
//		resposta.addColumna(new ParellaCodiValor("codi", area.getCodi()));
//		resposta.addColumna(new ParellaCodiValor("nom", area.getNom()));
//		String pareCodi = (area.getPare() != null) ? area.getPare().getCodi() : null;
//		resposta.addColumna(new ParellaCodiValor("pareCodi", pareCodi));
//		return resposta;
//	}
//
//	private List<String> getPersonesPerArea(String entornCodi, String areaCodi) {
//		if (isHeliumIdentitySource()) {
//			Entorn entorn = entornService.findAmbCodi(entornCodi);
//			if (entorn != null) {
//				Area area = organitzacioService.findAreaAmbEntornICodi(entorn.getId(), areaCodi);
//				if (area != null)
//					return organitzacioService.findCodisPersonaAmbArea(area.getId());
//			}
//			return new ArrayList<String>();
//		} else {
//			return organitzacioService.findCodisPersonaAmbJbpmIdGroup(areaCodi);
//		}
//	}
//
//	private List<String> getPersonesPerCarrec(String entornCodi, String carrecCodi) {
//		if (isHeliumIdentitySource()) {
//			List<String> resposta = new ArrayList<String>();
//			Entorn entorn = entornService.findAmbCodi(entornCodi);
//			if (entorn != null) {
//				Carrec carrec = organitzacioService.findCarrecAmbEntornICodi(entorn.getId(), carrecCodi);
//				if (carrec != null)
//					resposta.add(carrec.getPersonaCodi());
//			}
//			return resposta;
//		} else {
//			return organitzacioService.findCodisPersonaAmbJbpmIdCarrec(carrecCodi);
//		}
//	}
//
//	private List<String> getPersonesPerAreaCarrec(
//			String entornCodi,
//			String areaCodi,
//			String carrecCodi) {
//		if (isHeliumIdentitySource()) {
//			Entorn entorn = entornService.findAmbCodi(entornCodi);
//			if (entorn != null) {
//				Carrec carrec = organitzacioService.findCarrecAmbEntornAreaICodi(
//						entorn.getId(),
//						areaCodi,
//						carrecCodi);
//				if (carrec != null) {
//					List<String> resposta = new ArrayList<String>();
//					resposta.add(carrec.getPersonaCodi());
//					return resposta;
//				}
//			}
//			return null;
//		} else {
//			return organitzacioService.findCodisPersonaAmbJbpmIdGroupCarrec(areaCodi, carrecCodi);
//		}
//	}
//
//	private List<Area> getAreesAmbPare(String entornCodi, String areaCodi) {
//		Entorn entorn = entornService.findAmbCodi(entornCodi);
//		return organitzacioService.findAreaAmbPare(entorn.getId(), areaCodi);
//	}
//
//	private List<String> getGrupsPerPersona(String personaCodi) {
//		if (isHeliumIdentitySource()) {
//			return organitzacioService.findAreesMembre(personaCodi);
//		} else {
//			return organitzacioService.findAreesJbpmIdMembre(personaCodi);
//		}
//	}
//	
//	private Map<String, Object> getParametersMap(List<ParellaCodiValor> parametres) {
//		Map<String, Object> resposta = new HashMap<String, Object>();
//		if (parametres != null) {
//			for (ParellaCodiValor parella: parametres)
//				resposta.put(parella.getCodi(), parella.getValor());
//		}
//		return resposta;
//	}
//	private boolean isHeliumIdentitySource() {
//		String organigramaActiu = GlobalProperties.getInstance().getProperty("app.jbpm.identity.source");
//		return "helium".equalsIgnoreCase(organigramaActiu);
//	}

}
