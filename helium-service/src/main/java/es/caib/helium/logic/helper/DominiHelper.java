/**
 * 
 */
package es.caib.helium.logic.helper;

import es.caib.helium.client.engine.model.WTaskInstance;
import es.caib.helium.integracio.plugins.unitat.UnitatOrganica;
import es.caib.helium.logic.intf.WorkflowEngineApi;
import es.caib.helium.logic.intf.dto.DominiDto;
import es.caib.helium.logic.intf.dto.ExpedientDadaDto;
import es.caib.helium.logic.intf.dto.PersonaDto;
import es.caib.helium.logic.intf.dto.PersonaDto.Sexe;
import es.caib.helium.logic.intf.dto.TascaDadaDto;
import es.caib.helium.logic.intf.exception.NoTrobatException;
import es.caib.helium.logic.intf.exception.SistemaExternException;
import es.caib.helium.logic.intf.extern.domini.FilaResultat;
import es.caib.helium.logic.intf.extern.domini.ParellaCodiValor;
import es.caib.helium.logic.intf.util.GlobalProperties;
import es.caib.helium.logic.ms.DominiMs;
import es.caib.helium.persist.entity.Area;
import es.caib.helium.persist.entity.AreaMembre;
import es.caib.helium.persist.entity.Camp;
import es.caib.helium.persist.entity.Camp.TipusCamp;
import es.caib.helium.persist.entity.CampRegistre;
import es.caib.helium.persist.entity.Carrec;
import es.caib.helium.persist.entity.Entorn;
import es.caib.helium.persist.entity.Permis;
import es.caib.helium.persist.entity.Usuari;
import es.caib.helium.persist.repository.AreaJbpmIdRepository;
import es.caib.helium.persist.repository.AreaMembreRepository;
import es.caib.helium.persist.repository.AreaRepository;
import es.caib.helium.persist.repository.CampRepository;
import es.caib.helium.persist.repository.CarrecJbpmIdRepository;
import es.caib.helium.persist.repository.CarrecRepository;
import es.caib.helium.persist.repository.EntornRepository;
import es.caib.helium.persist.repository.PermisRepository;
import es.caib.helium.persist.repository.UsuariRepository;
import net.sf.ehcache.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Helper per a fer consultes a dominis i enumeracions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class DominiHelper {

	private static final String CACHE_DOMINI_ID = "dominiCache";
	private static final String CACHE_KEY_SEPARATOR = "#";

	private Map<Long, NamedParameterJdbcTemplate> jdbcTemplates = new HashMap<Long, NamedParameterJdbcTemplate>();

//	@Autowired
//	private MonitorDominiHelper monitorDominiHelper;
//	@Autowired
//	private WsClientHelper wsClientHelper;
	@Autowired
	private ConversioTipusServiceHelper conversioTipusServiceHelper;
	@Autowired
	private PluginHelper pluginHelper;
//	@Autowired
//	private MetricRegistry metricRegistry;
	@Autowired
	private CacheManager cacheManager;
	@Resource
	private VariableHelper variableHelper;
	@Resource
	private WorkflowEngineApi workflowEngineApi;
	@Resource
	private EntornRepository entornRepository;
	@Resource
	private AreaRepository areaRepository;
	@Resource
	private AreaJbpmIdRepository areaJbpmIdRepository;
	@Resource
	private AreaMembreRepository areaMembreRepository;
	@Resource
	private CarrecRepository carrecRepository;
	@Resource
	private PermisRepository permisRepository;
	@Resource
	private UsuariRepository usuariRepository;
	@Resource
	private CarrecJbpmIdRepository carrecJbpmIdRepository;
	@Resource
	private CampRepository campRepository;
	
	@Autowired
	private DominiMs dominiMs;
	@Resource
	private GlobalProperties globalProperties;


	@SuppressWarnings("unchecked")
	public List<FilaResultat> consultar(
			Long dominiId,
			String dominiWsId,
			Map<String, Object> parametres) {
		Cache dominiCache = cacheManager.getCache(CACHE_DOMINI_ID);
		String cacheKey = getCacheKey(dominiId, dominiWsId, parametres);
		List<FilaResultat> resultat = null;
		if (dominiCache.get(cacheKey) == null) {
			DominiDto domini = dominiMs.get(dominiId);
			// TODO: Mètriques
//			final Timer timerTotal = metricRegistry.timer(
//					MetricRegistry.name(
//							DominiHelper.class,
//							"consultar"));
//			final Timer.Context contextTotal = timerTotal.time();
//			Counter countTotal = metricRegistry.counter(
//					MetricRegistry.name(
//							DominiHelper.class,
//							"consultar.count"));
//			countTotal.inc();
//			final Timer timerEntorn = metricRegistry.timer(
//					MetricRegistry.name(
//							DominiHelper.class,
//							"consultar",
//							domini.getEntornId().toString()));
//			final Timer.Context contextEntorn = timerEntorn.time();
//			Counter countEntorn = metricRegistry.counter(
//					MetricRegistry.name(
//							DominiHelper.class,
//							"consultar.count",
//							domini.getEntornId().toString()));
//			countEntorn.inc();
//			final Timer timerTipexp = metricRegistry.timer(
//					MetricRegistry.name(
//							DominiHelper.class,
//							"consultar",
//							domini.getEntornId().toString(),
//							domini.getExpedientTipusId() == null ? null : domini.getExpedientTipusId().toString()));
//			final Timer.Context contextTipexp = timerTipexp.time();
//			Counter countTipexp = metricRegistry.counter(
//					MetricRegistry.name(
//							DominiHelper.class,
//							"consultar.count",
//							domini.getEntornId().toString(),
//							domini.getExpedientTipusId() == null ? null : domini.getExpedientTipusId().toString()));
//			countTipexp.inc();
//			final Timer timerDomini = metricRegistry.timer(
//					MetricRegistry.name(
//							DominiHelper.class,
//							"consultar",
//							domini.getEntornId().toString(),
//							domini.getExpedientTipusId() == null ? null : domini.getExpedientTipusId().toString(),
//							domini.getCodi()));
//			final Timer.Context contextDomini = timerDomini.time();
//			Counter countDomini = metricRegistry.counter(
//					MetricRegistry.name(
//							DominiHelper.class,
//							"consultar.count",
//							domini.getEntornId().toString(),
//							domini.getExpedientTipusId() == null ? null : domini.getExpedientTipusId().toString(),
//							domini.getCodi()));
//			countDomini.inc();
			try {					
				resultat = conversioTipusServiceHelper.convertirList(
						dominiMs.consultarDomini(dominiId, dominiWsId, parametres), 
						FilaResultat.class);
				if (resultat == null)
					resultat = new ArrayList<FilaResultat>();
				if (domini.getCacheSegons() > 0) {
					net.sf.ehcache.Cache nativeCache = (net.sf.ehcache.Cache)dominiCache.getNativeCache();
					Element cacheElement = new Element(
							cacheKey,
							resultat);
					cacheElement.setTimeToLive(domini.getCacheSegons());
					nativeCache.put(cacheElement);
				}
//				final Counter counterOkTotal = metricRegistry.counter(
//						MetricRegistry.name(
//								DominiHelper.class,
//								"consultar.ok"));
//				counterOkTotal.inc();
//				final Counter counterOkEntorn = metricRegistry.counter(
//						MetricRegistry.name(
//								DominiHelper.class,
//								"consultar.ok",
//								domini.getEntornId().toString()));
//				counterOkEntorn.inc();
//				final Counter counterOkTipexp = metricRegistry.counter(
//						MetricRegistry.name(
//								DominiHelper.class,
//								"consultar.ok",
//								domini.getEntornId().toString(),
//								domini.getExpedientTipusId() == null ? null : domini.getExpedientTipusId().toString()));
//				counterOkTipexp.inc();
//				final Counter counterOkDomini = metricRegistry.counter(
//						MetricRegistry.name(
//								DominiHelper.class,
//								"consultar.ok",
//								domini.getEntornId().toString(),
//								domini.getExpedientTipusId() == null ? null : domini.getExpedientTipusId().toString(),
//								domini.getCodi()));
//				counterOkDomini.inc();
			} catch (SistemaExternException ex) {
//				final Counter counterErrorTotal = metricRegistry.counter(
//						MetricRegistry.name(
//								DominiHelper.class,
//								"consultar.error"));
//				counterErrorTotal.inc();
//				final Counter counterErrorEntorn = metricRegistry.counter(
//						MetricRegistry.name(
//								DominiHelper.class,
//								"consultar.error",
//								domini.getEntornId().toString()));
//				counterErrorEntorn.inc();
//				final Counter counterErrorTipexp = metricRegistry.counter(
//						MetricRegistry.name(
//								DominiHelper.class,
//								"consultar.error",
//								domini.getEntornId().toString(),
//								domini.getExpedientTipusId() == null ? null : domini.getExpedientTipusId().toString()));
//				counterErrorTipexp.inc();
//				final Counter counterErrorDomini = metricRegistry.counter(
//						MetricRegistry.name(
//								DominiHelper.class,
//								"consultar.error",
//								domini.getEntornId().toString(),
//								domini.getExpedientTipusId() == null ? null : domini.getExpedientTipusId().toString(),
//								domini.getCodi()));
//				counterErrorDomini.inc();
				throw ex;
			} finally {
//				contextTotal.stop();
//				contextEntorn.stop();
//				contextTipexp.stop();
//				contextDomini.stop();
			}
		} else {
			resultat = (List<FilaResultat>)dominiCache.get(cacheKey).get();
		}
		return resultat;
	}



	private String getCacheKey(
			Long dominiId,			
			String dominiWsId, 
			Map<String, Object> parametres) {
		StringBuffer sb = new StringBuffer();
		sb.append(dominiId.toString());
		sb.append(CACHE_KEY_SEPARATOR);
		sb.append(dominiWsId);
		sb.append(CACHE_KEY_SEPARATOR);
		if (parametres != null) {
			for (String clau: parametres.keySet()) {
				sb.append(clau);
				sb.append(CACHE_KEY_SEPARATOR);
				Object valor = parametres.get(clau);
				if (valor == null)
					sb.append("<null>");
				else
					sb.append(valor.toString());
				sb.append(CACHE_KEY_SEPARATOR);
			}
		}
		return sb.toString();
	}

	//////////////////////////////////////////////////////////////////////////////////////////////
	// DOMINI INTERN																			//
	//////////////////////////////////////////////////////////////////////////////////////////////
	
	public List<FilaResultat> consultaDominiIntern(
			String id,
			List<ParellaCodiValor> parametres) throws Exception {
		Map<String, Object> parametersMap = getParametersMap(parametres);
		if ("PERSONA_AMB_CODI".equals(id)) {
			return personaAmbCodi(parametersMap);
		} else if ("PERSONES_AMB_AREA".equals(id)) {
			return personesAmbArea(parametersMap);
		} else if ("PERSONA_AMB_CARREC_AREA".equals(id)) {
			return personesAmbCarrecArea(parametersMap, true);
		} else if ("PERSONES_AMB_CARREC_AREA".equals(id)) {
			return personesAmbCarrecArea(parametersMap, false);
		} else  if ("AREES_AMB_PARE".equals(id)) {
			return areesAmbPare(parametersMap);
		} else if ("VARIABLE_REGISTRE".equals(id)) {
			return variableRegistre(parametersMap);
		} else if ("AREES_AMB_PERSONA".equals(id)) {
			return areesAmbPersona(parametersMap);
		} else if ("ROLS_PER_USUARI".equals(id)) {
			return rolsPerUsuari(parametersMap);
		} else if ("USUARIS_PER_ROL".equals(id)) {
			return usuarisPerRol(parametersMap);
		} else if ("CARRECS_PER_PERSONA".equals(id)) {
			return carrecsPerPersona(parametersMap);
		} else if ("PERSONES_AMB_CARREC".equals(id)) {
			/* Per suprimir */
			return personesAmbCarrec(parametersMap);
		} else if ("UNITAT_PER_CODI".equals(id)) {
			return unitatOrganica(parametersMap);
		} else if ("UNITAT_PER_ARREL".equals(id)) {
			return unitatsOrganiques(parametersMap);
		}
		return new ArrayList<FilaResultat>();
	}
	
	public List<FilaResultat> personaAmbCodi(Map<String, Object> parametres) {
		List<FilaResultat> resposta = new ArrayList<>();
		PersonaDto persona = pluginHelper.personaFindAmbCodi((String)parametres.get("persona"));
		if (persona != null)
			resposta.add(novaFilaPersona(persona));
		return resposta;
	}
	
	public List<FilaResultat> personesAmbArea(Map<String, Object> parametres) {
		List<FilaResultat> resposta = new ArrayList<>();
		for (String personaCodi: getPersonesPerArea((String)parametres.get("entorn"), (String)parametres.get("area"))) {
			PersonaDto persona = pluginHelper.personaFindAmbCodi(personaCodi);
			if (persona != null)
				resposta.add(novaFilaPersona(persona));
		}
		return resposta;
	}
	
	public List<FilaResultat> personesAmbCarrecArea(Map<String, Object> parametres, boolean nomesUna) {
		List<FilaResultat> resposta = new ArrayList<>();
		List<String> personaCodis = getPersonesPerAreaCarrec(
				(String)parametres.get("entorn"),
				(String)parametres.get("area"),
				(String)parametres.get("carrec"));
		if (personaCodis != null) {
			for (String personaCodi: personaCodis) {
				PersonaDto persona = pluginHelper.personaFindAmbCodi(personaCodi);
				if (persona != null) {
					resposta.add(novaFilaPersona(persona));
					if (nomesUna)
						break;
				}
			}
		}
		return resposta;
	}
	
	public List<FilaResultat> areesAmbPare(Map<String, Object> parametres) {
		List<FilaResultat> resposta = new ArrayList<>();
		for (Area area: getAreesAmbPare((String)parametres.get("entorn"), (String)parametres.get("pare"))) {
			resposta.add(novaFilaArea(area));
		}
		return resposta;
	}
	
	public List<FilaResultat> areesAmbPersona(Map<String, Object> parametres) {
		List<FilaResultat> resposta = new ArrayList<>();
		for (String grupCodi: getGrupsPerPersona((String)parametres.get("persona"))) {
			FilaResultat fila = new FilaResultat();
			fila.addColumna(new ParellaCodiValor("codi", grupCodi));
			resposta.add(fila);
		}
		return resposta;
	}
	
	public List<FilaResultat> usuarisPerRol(Map<String, Object> parametres) {
		List<FilaResultat> resposta = new ArrayList<FilaResultat>();
		if (isHeliumIdentitySource()) {
			Permis rol = permisRepository.findById((String)parametres.get("rol")).orElse(null);
			if (rol != null) {
				for (Usuari usuari: rol.getUsuaris()) {
					try {
						PersonaDto persona = pluginHelper.personaFindAmbCodi(usuari.getCodi());
						if (persona != null)
							resposta.add(novaFilaPersona(persona));
					} catch (NoTrobatException nte) {}
				}
			}
		} else {
			List<String> persones = workflowEngineApi.findPersonesByGrup((String)parametres.get("rol"));
			for (String personaCodi: persones) { // carrecJbpmIdRepository.findPersonesCodiByGrupCodi((String)parametres.get("rol"))) {
				PersonaDto persona = pluginHelper.personaFindAmbCodi(personaCodi);
				if (persona != null)
					resposta.add(novaFilaPersona(persona));
			}
		}
		return resposta;
	}

	public List<FilaResultat> carrecsPerPersona(Map<String, Object> parametres) {
		List<FilaResultat> resposta = new ArrayList<FilaResultat>();
		PersonaDto persona = pluginHelper.personaFindAmbCodi((String)parametres.get("persona"));
		List<Carrec> carrecs = getCarrecsAmbPersonaCodi(
				(String)parametres.get("entorn"),
				(String)parametres.get("persona"));
		for (Carrec carrec: carrecs) {
			resposta.add(novaFilaCarrec(persona, carrec));
		}
		return resposta;
	}
	
	public List<FilaResultat> unitatOrganica(Map<String, Object> parametres) {
		List<FilaResultat> resposta = new ArrayList<FilaResultat>();
		UnitatOrganica uo = null;
		String codi = (String)parametres.get("unitat");
		if (codi != null)
			uo = pluginHelper.findUnitatOrganica(codi);
		if(uo != null) {
			FilaResultat fila = new FilaResultat();
			fila.addColumna(new ParellaCodiValor("codiDenominacio", uo.getCodi() + " - " + uo.getDenominacio()));
			fila.addColumna(new ParellaCodiValor("codi", uo.getCodi()));
			fila.addColumna(new ParellaCodiValor("denominacio", uo.getDenominacio()));
			fila.addColumna(new ParellaCodiValor("tipusEntitatPublica", uo.getTipusEntitatPublica()));
			fila.addColumna(new ParellaCodiValor("tipusUnitatOrganica", uo.getTipusUnitatOrganica()));
			fila.addColumna(new ParellaCodiValor("sigles", uo.getSigles()));
			fila.addColumna(new ParellaCodiValor("codiUnitatSuperior", uo.getCodiUnitatSuperior()));
			fila.addColumna(new ParellaCodiValor("codiUnitatArrel", uo.getCodiUnitatArrel()));
			fila.addColumna(new ParellaCodiValor("estat", uo.getEstat()));
			resposta.add(fila);
		}
		return resposta;
	}
	
	public List<FilaResultat> unitatsOrganiques(Map<String, Object> parametres) {
		List<FilaResultat> resposta = new ArrayList<FilaResultat>();
		String unitatArrel = (String)parametres.get("unitatArrel");
		List<UnitatOrganica> uos = pluginHelper.findUnitatsOrganiques(unitatArrel);
		if(uos != null && uos.size() > 1) {
			uos.remove(0);
		}
		for(UnitatOrganica uo : uos) {
			FilaResultat fila = new FilaResultat();
			fila.addColumna(new ParellaCodiValor("codiDenominacio", uo.getCodi() + " - " + uo.getDenominacio()));
			fila.addColumna(new ParellaCodiValor("codi", uo.getCodi()));
			fila.addColumna(new ParellaCodiValor("denominacio", uo.getDenominacio()));
			fila.addColumna(new ParellaCodiValor("tipusEntitatPublica", uo.getTipusEntitatPublica()));
			fila.addColumna(new ParellaCodiValor("tipusUnitatOrganica", uo.getTipusUnitatOrganica()));
			fila.addColumna(new ParellaCodiValor("sigles", uo.getSigles()));
			fila.addColumna(new ParellaCodiValor("codiUnitatSuperior", uo.getCodiUnitatSuperior()));
			fila.addColumna(new ParellaCodiValor("codiUnitatArrel", uo.getCodiUnitatArrel()));
			fila.addColumna(new ParellaCodiValor("estat", uo.getEstat()));
			resposta.add(fila);
		}
		return resposta;
	}

	public List<FilaResultat> rolsPerUsuari(Map<String, Object> parametres) {
		List<FilaResultat> resposta = new ArrayList<FilaResultat>();
		if (isHeliumIdentitySource()) {
			Usuari usuari = usuariRepository.findById((String)parametres.get("persona")).orElse(null);
			if (usuari != null) {
				for (Permis rol: usuari.getPermisos()) {
					FilaResultat fila = new FilaResultat();
					fila.addColumna(new ParellaCodiValor("rol", rol.getCodi()));
					resposta.add(fila);
				}
			}
		} else {
			List<String> rols = workflowEngineApi.findRolsByPersona((String)parametres.get("persona"));
			for (String rol: rols) { 	//areaJbpmIdRepository.findRolesAmbUsuariCodi((String)parametres.get("persona"))) {
				FilaResultat fila = new FilaResultat();
				fila.addColumna(new ParellaCodiValor("rol", rol));
				resposta.add(fila);
			}
		}
		return resposta;
	}
	
	public List<FilaResultat> variableRegistre(Map<String, Object> parametres) throws Exception {
		List<FilaResultat> resposta = new ArrayList<FilaResultat>();
		Object tiId = parametres.get("taskInstanceId");
		Object piId = parametres.get("processInstanceId");
		String taskInstanceId = tiId != null ? tiId instanceof Long ? ((Long)tiId).toString() : (String)tiId : null;
		String processInstanceId =  piId != null ? piId instanceof Long ? ((Long)piId).toString() : (String)piId : null;
		String variable = (String)parametres.get("variable");
		String filtreColumna = (String)parametres.get("filtreColumna");
		Object filtreValor = parametres.get("filtreValor");
		Object valor = null;
		Camp camp = null;
		List<String[]> registresText = new ArrayList<String[]>();
		if (taskInstanceId != null) {
			WTaskInstance task = workflowEngineApi.getTaskById(taskInstanceId);
			TascaDadaDto dada = variableHelper.findDadaPerInstanciaTasca(task, variable); 
			camp = campRepository.findById(dada.getCampId()).get();
			if (camp.getTipus().equals(TipusCamp.REGISTRE)) {
				if (camp.isMultiple()) {
					valor = dada.getVarValor();
					for (TascaDadaDto dm : dada.getMultipleDades()) {
						String[] regs = new String[dm.getRegistreDades().size()];
						int i = 0;
						for (TascaDadaDto d : dm.getRegistreDades()) {
							regs[i++] = d.getText();
						}
						registresText.add(regs);
					}
				} else {
					valor = new Object[] {dada.getVarValor()};
					String[] regs = new String[dada.getRegistreDades().size()];
					int i = 0;
					for (TascaDadaDto d : dada.getRegistreDades()) {
						regs[i++] = d.getText();
					}
					registresText.add(regs);
				}
			}
		} else if (processInstanceId != null) {
			ExpedientDadaDto dada = variableHelper.getDadaPerInstanciaProces(processInstanceId, variable);
			camp = campRepository.findById(dada.getCampId()).get();
			if (camp.getTipus().equals(TipusCamp.REGISTRE)) {
				if (camp.isMultiple()) {
					valor = dada.getVarValor();
					for (ExpedientDadaDto dm : dada.getMultipleDades()) {
						String[] regs = new String[dm.getRegistreDades().size()];
						int i = 0;
						for (ExpedientDadaDto d : dm.getRegistreDades()) {
							regs[i++] = d.getText();
						}
						registresText.add(regs);
					}
				} else {
					valor = new Object[] {dada.getVarValor()};
					String[] regs = new String[dada.getRegistreDades().size()];
					int i = 0;
					for (ExpedientDadaDto d : dada.getRegistreDades()) {
						regs[i++] = d.getText();
					}
					registresText.add(regs);
				}
			}
		}
		if (valor != null && valor instanceof Object[] && camp.getTipus().equals(TipusCamp.REGISTRE)) {
			Object[] registres = (Object[])valor;
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

	/* Per suprimir */
	public List<FilaResultat> personesAmbCarrec(Map<String, Object> parametres) {
		List<FilaResultat> resposta = new ArrayList<FilaResultat>();
		for (String personaCodi: getPersonesPerCarrec((String)parametres.get("entorn"), (String)parametres.get("carrec"))) {
			PersonaDto persona = pluginHelper.personaFindAmbCodi(personaCodi);
			if (persona != null)
				resposta.add(novaFilaPersona(persona));
		}
		return resposta;
	}
	
	private FilaResultat novaFilaPersona(PersonaDto persona) {
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

	private FilaResultat novaFilaArea(Area area) {
		FilaResultat resposta = new FilaResultat();
		resposta.addColumna(new ParellaCodiValor("codi", area.getCodi()));
		resposta.addColumna(new ParellaCodiValor("nom", area.getNom()));
		String pareCodi = (area.getPare() != null) ? area.getPare().getCodi() : null;
		resposta.addColumna(new ParellaCodiValor("pareCodi", pareCodi));
		return resposta;
	}

	private FilaResultat novaFilaCarrec(PersonaDto persona, Carrec carrec) {
		String nom;
		String tractament;
		if (Sexe.SEXE_HOME.equals(persona.getSexe())) {
			nom = carrec.getNomHome();
			tractament = carrec.getTractamentHome();
		} else {
			nom = carrec.getNomHome();
			tractament = carrec.getTractamentHome();
		}
		String nomAmbTractament = (tractament != null) ? tractament + " " + nom : nom;
		FilaResultat resposta = new FilaResultat();
		resposta.addColumna(new ParellaCodiValor("codi", carrec.getCodi()));
		resposta.addColumna(new ParellaCodiValor("nom", carrec.getNomHome()));
		resposta.addColumna(new ParellaCodiValor("tractament", carrec.getTractamentHome()));
		resposta.addColumna(new ParellaCodiValor("descripcio", carrec.getDescripcio()));
		resposta.addColumna(new ParellaCodiValor("nomAmbTractament", nomAmbTractament));
		return resposta;
	}

	private List<String> getPersonesPerArea(String entornCodi, String areaCodi) {
		if (isHeliumIdentitySource()) {
			Entorn entorn = entornRepository.findByCodi(entornCodi);
			if (entorn != null) {
				Area area = areaRepository.findByEntornAndCodi(entorn, areaCodi);
				if (area != null)
					return findCodisPersonaAmbArea(area.getId());
			}
			return new ArrayList<String>();
		} else {
			return workflowEngineApi.findPersonesByGrup(areaCodi);
//			return carrecJbpmIdRepository.findPersonesCodiByGrupCodi(areaCodi);
		}
	}
	
	private List<String> getPersonesPerCarrec(String entornCodi, String carrecCodi) {
		if (isHeliumIdentitySource()) {
			List<String> resposta = new ArrayList<String>();
			Entorn entorn = entornRepository.findByCodi(entornCodi);
			if (entorn != null) {
				Carrec carrec = carrecRepository.findByEntornAndCodi(entorn, carrecCodi);
				if (carrec != null)
					resposta.add(carrec.getPersonaCodi());
			}
			return resposta;
		} else {
			return workflowEngineApi.findPersonesByCarrec(carrecCodi);
//			return carrecJbpmIdRepository.findPersonesCodiByCarrecCodi(carrecCodi);
		}
	}

	private List<String> getPersonesPerAreaCarrec(
			String entornCodi,
			String areaCodi,
			String carrecCodi) {
		if (isHeliumIdentitySource()) {
			Entorn entorn = entornRepository.findByCodi(entornCodi);
			if (entorn != null) {
				Carrec carrec = carrecRepository.findByEntornAndAreaCodiAndCodi(
						entorn,
						areaCodi,
						carrecCodi);
				if (carrec != null) {
					List<String> resposta = new ArrayList<String>();
					resposta.add(carrec.getPersonaCodi());
					return resposta;
				}
			}
			return null;
		} else {
			return workflowEngineApi.findPersonesByGrupAndCarrec(areaCodi, carrecCodi);
//			return carrecJbpmIdRepository.findPersonaCodiByGrupCodiAndCarrecCodi(areaCodi, carrecCodi);
		}
	}
	
	private List<String> getGrupsPerPersona(String personaCodi) {
		if (isHeliumIdentitySource()) {
			return findAreesMembre(personaCodi);
		} else {
			return workflowEngineApi.findAreesByPersona(personaCodi);
//			return areaJbpmIdRepository.findAreesJbpmIdMembre(personaCodi);
		}
	}

	private List<Area> getAreesAmbPare(String entornCodi, String areaCodi) {
		Entorn entorn = entornRepository.findByCodi(entornCodi);
		return areaRepository.findByEntornAndPareCodi(entorn, areaCodi);
	}

	private List<Carrec> getCarrecsAmbPersonaCodi(
			String entornCodi,
			String personaCodi) {
		if (isHeliumIdentitySource()) {
			Entorn entorn = entornRepository.findByCodi(entornCodi);
			if (entorn != null) {
				return carrecRepository.findByEntornAndPersonaCodi(entorn, personaCodi);
			}
			return new ArrayList<Carrec>();
		} else {
			return new ArrayList<Carrec>(); // TODO
		}
	}

	private List<String> findCodisPersonaAmbArea(Long areaId) {
		Area area = areaRepository.findById(areaId).get();
		List<String> resposta = new ArrayList<String>();
		for (AreaMembre membre: area.getMembres())
			resposta.add(membre.getCodi());
		return resposta;
	}
	
	public List<String> findAreesMembre(String usuariCodi) {
		List<String> codisArea = new ArrayList<String>();
		List<AreaMembre> membres = areaMembreRepository.findByCodi(usuariCodi);
		for (AreaMembre membre: membres) {
			codisArea.add(membre.getArea().getCodi());
		}
		return codisArea;
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
		String organigramaActiu = globalProperties.getProperty("es.caib.helium.jbpm.identity.source");
		return "helium".equalsIgnoreCase(organigramaActiu);
	}

	/** Consulta els camps relacionats amb un domini. Serveix per validar si es pot esborrar abans
	 * d'esborrar el domini.
	 * 
	 * @param domini
	 * @return
	 */
	public List<Camp> findCampsPerDomini(Long domini) {
		List<Camp> camps = campRepository.findByDomini(domini);
		return camps;
	}

	private static final Logger logger = LoggerFactory.getLogger(DominiHelper.class);

}
