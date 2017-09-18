/**
 * 
 */
package net.conselldemallorca.helium.core.helper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

import net.conselldemallorca.helium.core.api.WTaskInstance;
import net.conselldemallorca.helium.core.api.WorkflowEngineApi;
import net.conselldemallorca.helium.core.extern.domini.DominiHelium;
import net.conselldemallorca.helium.core.extern.domini.FilaResultat;
import net.conselldemallorca.helium.core.extern.domini.ParellaCodiValor;
import net.conselldemallorca.helium.core.helper.WsClientHelper.WsClientAuth;
import net.conselldemallorca.helium.core.model.hibernate.Area;
import net.conselldemallorca.helium.core.model.hibernate.AreaMembre;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.Camp.TipusCamp;
import net.conselldemallorca.helium.core.model.hibernate.CampRegistre;
import net.conselldemallorca.helium.core.model.hibernate.Carrec;
import net.conselldemallorca.helium.core.model.hibernate.Domini;
import net.conselldemallorca.helium.core.model.hibernate.Domini.OrigenCredencials;
import net.conselldemallorca.helium.core.model.hibernate.Domini.TipusAuthDomini;
import net.conselldemallorca.helium.core.model.hibernate.Domini.TipusDomini;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.Permis;
import net.conselldemallorca.helium.core.model.hibernate.Usuari;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDadaDto;
import net.conselldemallorca.helium.v3.core.api.dto.IntegracioAccioTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.IntegracioParametreDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDadaDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.SistemaExternException;
import net.conselldemallorca.helium.v3.core.api.exception.SistemaExternTimeoutException;
import net.conselldemallorca.helium.v3.core.repository.AreaJbpmIdRepository;
import net.conselldemallorca.helium.v3.core.repository.AreaMembreRepository;
import net.conselldemallorca.helium.v3.core.repository.AreaRepository;
import net.conselldemallorca.helium.v3.core.repository.CampRepository;
import net.conselldemallorca.helium.v3.core.repository.CarrecJbpmIdRepository;
import net.conselldemallorca.helium.v3.core.repository.CarrecRepository;
import net.conselldemallorca.helium.v3.core.repository.EntornRepository;
import net.conselldemallorca.helium.v3.core.repository.PermisRepository;
import net.conselldemallorca.helium.v3.core.repository.UsuariRepository;
import net.sf.ehcache.Element;

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

	@Autowired
	private MonitorDominiHelper monitorDominiHelper;
	@Autowired
	private WsClientHelper wsClientHelper;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private MetricRegistry metricRegistry;
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

	@SuppressWarnings("unchecked")
	public List<FilaResultat> consultar(
			Domini domini,
			String id,
			Map<String, Object> parametres) {
		Cache dominiCache = cacheManager.getCache(CACHE_DOMINI_ID);
		String cacheKey = getCacheKey(domini.getId(), id, parametres);
		List<FilaResultat> resultat = null;
		if (dominiCache.get(cacheKey) == null) {
			final Timer timerTotal = metricRegistry.timer(
					MetricRegistry.name(
							DominiHelper.class,
							"consultar"));
			final Timer.Context contextTotal = timerTotal.time();
			Counter countTotal = metricRegistry.counter(
					MetricRegistry.name(
							DominiHelper.class,
							"consultar.count"));
			countTotal.inc();
			final Timer timerEntorn = metricRegistry.timer(
					MetricRegistry.name(
							DominiHelper.class,
							"consultar",
							domini.getEntorn().getCodi()));
			final Timer.Context contextEntorn = timerEntorn.time();
			Counter countEntorn = metricRegistry.counter(
					MetricRegistry.name(
							DominiHelper.class,
							"consultar.count",
							domini.getEntorn().getCodi()));
			countEntorn.inc();
			final Timer timerTipexp = metricRegistry.timer(
					MetricRegistry.name(
							DominiHelper.class,
							"consultar",
							domini.getEntorn().getCodi(),
							domini.getExpedientTipus() == null ? null : domini.getExpedientTipus().getCodi()));
			final Timer.Context contextTipexp = timerTipexp.time();
			Counter countTipexp = metricRegistry.counter(
					MetricRegistry.name(
							DominiHelper.class,
							"consultar.count",
							domini.getEntorn().getCodi(),
							domini.getExpedientTipus() == null ? null : domini.getExpedientTipus().getCodi()));
			countTipexp.inc();
			final Timer timerDomini = metricRegistry.timer(
					MetricRegistry.name(
							DominiHelper.class,
							"consultar",
							domini.getEntorn().getCodi(),
							domini.getExpedientTipus() == null ? null : domini.getExpedientTipus().getCodi(),
							domini.getCodi()));
			final Timer.Context contextDomini = timerDomini.time();
			Counter countDomini = metricRegistry.counter(
					MetricRegistry.name(
							DominiHelper.class,
							"consultar.count",
							domini.getEntorn().getCodi(),
							domini.getExpedientTipus() == null ? null : domini.getExpedientTipus().getCodi(),
							domini.getCodi()));
			countDomini.inc();
			try {
				if (domini.isDominiIntern())
					resultat = this.consultaDominiIntern(domini, id, parametres);
				else if (domini.getTipus().equals(TipusDomini.CONSULTA_WS))
					resultat = consultaWs(domini, id, parametres);
				else if (domini.getTipus().equals(TipusDomini.CONSULTA_SQL))
					resultat = consultaSql(domini, parametres);
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
				final Counter counterOkTotal = metricRegistry.counter(
						MetricRegistry.name(
								DominiHelper.class,
								"consultar.ok"));
				counterOkTotal.inc();
				final Counter counterOkEntorn = metricRegistry.counter(
						MetricRegistry.name(
								DominiHelper.class,
								"consultar.ok",
								domini.getEntorn().getCodi()));
				counterOkEntorn.inc();
				final Counter counterOkTipexp = metricRegistry.counter(
						MetricRegistry.name(
								DominiHelper.class,
								"consultar.ok",
								domini.getEntorn().getCodi(),
								domini.getExpedientTipus() == null ? null : domini.getExpedientTipus().getCodi()));
				counterOkTipexp.inc();
				final Counter counterOkDomini = metricRegistry.counter(
						MetricRegistry.name(
								DominiHelper.class,
								"consultar.ok",
								domini.getEntorn().getCodi(),
								domini.getExpedientTipus() == null ? null : domini.getExpedientTipus().getCodi(),
								domini.getCodi()));
				counterOkDomini.inc();
			} catch (SistemaExternException ex) {
				final Counter counterErrorTotal = metricRegistry.counter(
						MetricRegistry.name(
								DominiHelper.class,
								"consultar.error"));
				counterErrorTotal.inc();
				final Counter counterErrorEntorn = metricRegistry.counter(
						MetricRegistry.name(
								DominiHelper.class,
								"consultar.error",
								domini.getEntorn().getCodi()));
				counterErrorEntorn.inc();
				final Counter counterErrorTipexp = metricRegistry.counter(
						MetricRegistry.name(
								DominiHelper.class,
								"consultar.error",
								domini.getEntorn().getCodi(),
								domini.getExpedientTipus() == null ? null : domini.getExpedientTipus().getCodi()));
				counterErrorTipexp.inc();
				final Counter counterErrorDomini = metricRegistry.counter(
						MetricRegistry.name(
								DominiHelper.class,
								"consultar.error",
								domini.getEntorn().getCodi(),
								domini.getExpedientTipus() == null ? null : domini.getExpedientTipus().getCodi(),
								domini.getCodi()));
				counterErrorDomini.inc();
				throw ex;
			} finally {
				contextTotal.stop();
				contextEntorn.stop();
				contextTipexp.stop();
				contextDomini.stop();
			}
		} else {
			resultat = (List<FilaResultat>)dominiCache.get(cacheKey).get();
		}
		return resultat;
	}

	public List<FilaResultat> consultarIntern(
			Entorn entorn,
			ExpedientTipus expedientTipus,
			String id,
			Map<String, Object> parametres) {
		Domini dominiIntern = new Domini();
		dominiIntern.setId((long)0);
		dominiIntern.setEntorn(entorn);
		dominiIntern.setExpedientTipus(expedientTipus);
		dominiIntern.setCacheSegons(30);
		dominiIntern.setCodi("");
		dominiIntern.setNom("Domini intern");
		return consultar(
				dominiIntern,
				id,
				parametres);
	}

	private List<FilaResultat> consultaDominiIntern(
			Domini domini,
			String id,
			Map<String, Object> parametres) {
		List<ParellaCodiValor> paramsConsulta = new ArrayList<ParellaCodiValor>();
		paramsConsulta.add(
				new ParellaCodiValor(
						"entorn",
						domini.getEntorn().getCodi()));
		if (parametres != null) {
			for (String codi: parametres.keySet()) {
				paramsConsulta.add(new ParellaCodiValor(
						codi,
						parametres.get(codi)));
			}
		}
		long t0 = System.currentTimeMillis();
		try {
			logger.debug("Petició de domini de tipus Intern (" +
					"id=" + domini.getId() + ", " +
					"params=" + parametresToString(parametres) + ")");
			List<FilaResultat> resposta = consultaDominiIntern(id, paramsConsulta);
			monitorDominiHelper.addAccioOk(
					domini,
					"Consulta domini intern (id=" + id + ")",
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					toIntegracioParametres(parametres));
			return resposta;
		} catch (Exception ex) {
			logger.error("ERROR SISTEMA DOMINI INTERN: ", ex);
			
			monitorDominiHelper.addAccioError(
					domini,
					"Consulta WS (id=" + id + ")",
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					ex.getMessage(),
					ex,
					toIntegracioParametres(parametres));
			
			throw SistemaExternException.tractarSistemaExternException(
					domini.getEntorn().getId(),
					domini.getEntorn().getCodi(), 
					domini.getEntorn().getNom(), 
					null, 
					null, 
					null, 
					domini.getExpedientTipus() == null ? null : domini.getExpedientTipus().getId(), 
					domini.getExpedientTipus() == null ? null : domini.getExpedientTipus().getCodi(), 
					domini.getExpedientTipus() == null ? null : domini.getExpedientTipus().getNom(), 
					"(Domini intern '" + domini.getCodi() + "')", 
					ex);
		}
	}

	private List<FilaResultat> consultaWs(
			Domini domini,
			String id,
			Map<String, Object> parametres) {
		List<ParellaCodiValor> paramsConsulta = new ArrayList<ParellaCodiValor>();
		if (parametres != null) {
			for (String codi: parametres.keySet()) {
				paramsConsulta.add(new ParellaCodiValor(
						codi,
						parametres.get(codi)));
			}
		}
		long t0 = System.currentTimeMillis();
		int puntControl = 0;
		try {
			logger.debug("Petició de domini de tipus WS (" +
					"id=" + domini.getId() + ", " +
					"codi=" + domini.getCodi() + ", " +
					"params=" + parametresToString(parametres) + ")");
			puntControl = 1;
			DominiHelium client = getClientWsFromDomini(domini);
			puntControl = 2;
			List<FilaResultat> resposta = client.consultaDomini(id, paramsConsulta);
			puntControl = 3;
			monitorDominiHelper.addAccioOk(
					domini,
					"Consulta WS (id=" + id + ")",
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					toIntegracioParametres(parametres));
			puntControl = 4;
			return resposta;
		} catch (Exception ex) {
			logger.error("ERROR SISTEMA EXTERN (Punt de control " + puntControl + "): ", ex);
			
			monitorDominiHelper.addAccioError(
					domini,
					"Consulta WS (id=" + id + ")",
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					ex.getMessage(),
					ex,
					toIntegracioParametres(parametres));
			
			throw SistemaExternException.tractarSistemaExternException(
					domini.getEntorn().getId(),
					domini.getEntorn().getCodi(), 
					domini.getEntorn().getNom(), 
					null, 
					null, 
					null, 
					domini.getExpedientTipus() == null ? null : domini.getExpedientTipus().getId(), 
					domini.getExpedientTipus() == null ? null : domini.getExpedientTipus().getCodi(), 
					domini.getExpedientTipus() == null ? null : domini.getExpedientTipus().getNom(), 
					"(Domini '" + domini.getCodi() + "')", 
					ex);
		}
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List<FilaResultat> consultaSql(
			Domini domini,
			Map<String, Object> parametres) {
		long t0 = System.currentTimeMillis();
		try {
			logger.debug("Petició de domini de tipus SQL (" +
					"id=" + domini.getId() + ", " +
					"codi=" + domini.getCodi() + ", " +
					"params=" + parametresToString(parametres) + ")");
			NamedParameterJdbcTemplate jdbcTemplate = getJdbcTemplateFromDomini(domini);
			MapSqlParameterSource parameterSource = new MapSqlParameterSource(parametres) {
				public boolean hasValue(String paramName) {
					return true;
				}
			};
			List<FilaResultat> resultat = jdbcTemplate.query(
					domini.getSql(),
					parameterSource,
					new RowMapper() {
						public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
							FilaResultat fr = new FilaResultat();
							ResultSetMetaData rsm = rs.getMetaData();
							for (int i = 1; i <= rsm.getColumnCount(); i++) {
								fr.addColumna(new ParellaCodiValor(
										rsm.getColumnName(i),
										rs.getObject(i)));
							}
							return fr;
						}
					});
			monitorDominiHelper.addAccioOk(
					domini,
					"Consulta SQL",
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					toIntegracioParametres(parametres));
			return resultat;
		} catch (Exception ex) {
			monitorDominiHelper.addAccioError(
					domini,
					"Consulta SQL",
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					ex.getMessage(),
					ex,
					toIntegracioParametres(parametres));
			
			if(ExceptionUtils.getRootCause(ex) != null && 
					(ExceptionUtils.getRootCause(ex).getClass().getName().contains("Timeout") ||
					 ExceptionUtils.getRootCause(ex).getClass().getName().contains("timeout"))) {
						
					throw new SistemaExternTimeoutException(
							domini.getEntorn().getId(),
							domini.getEntorn().getCodi(), 
							domini.getEntorn().getNom(), 
							null, 
							null, 
							null, 
							domini.getExpedientTipus() == null ? null : domini.getExpedientTipus().getId(), 
							domini.getExpedientTipus() == null ? null : domini.getExpedientTipus().getCodi(), 
							domini.getExpedientTipus() == null ? null : domini.getExpedientTipus().getNom(), 
							"(Domini SQL'" + domini.getCodi() + "')", 
							ex);
			} else {
				throw new SistemaExternException(
						domini.getEntorn().getId(),
						domini.getEntorn().getCodi(), 
						domini.getEntorn().getNom(), 
						null, 
						null, 
						null, 
						domini.getExpedientTipus() == null ? null : domini.getExpedientTipus().getId(), 
						domini.getExpedientTipus() == null ? null : domini.getExpedientTipus().getCodi(), 
						domini.getExpedientTipus() == null ? null : domini.getExpedientTipus().getNom(), 
						"(Domini SQL'" + domini.getCodi() + "')", 
						ex);
			}
		}
	}

	private DominiHelium getClientWsFromDomini(Domini domini) {
		String username = null;
		String password = null;
		if (domini.getTipusAuth() != null && !TipusAuthDomini.NONE.equals(domini.getTipusAuth())) {
			if (OrigenCredencials.PROPERTIES.equals(domini.getOrigenCredencials())) {
				username = GlobalProperties.getInstance().getProperty(domini.getUsuari());
				password = GlobalProperties.getInstance().getProperty(domini.getContrasenya());
			} else {
				username = domini.getUsuari();
				password = domini.getContrasenya();
			}
		}
		WsClientAuth auth;
		if (domini.getTipusAuth() != null)
			switch (domini.getTipusAuth()) {
			case HTTP_BASIC:
				auth = WsClientAuth.BASIC;
				break;
			case USERNAMETOKEN:
				auth = WsClientAuth.USERNAMETOKEN;
				break;
			default:
				auth = WsClientAuth.NONE;
				break;
			}
		else 
			auth = WsClientAuth.NONE;
		return wsClientHelper.getDominiService(
				domini.getUrl(),
				auth,
				username,
				password,
				getDominiTimeout(domini));
	}

	private NamedParameterJdbcTemplate getJdbcTemplateFromDomini(Domini domini) throws NamingException {
		NamedParameterJdbcTemplate jdbcTemplate = jdbcTemplates.get(domini.getId());
		if (jdbcTemplate == null) {
			Context initContext = new InitialContext();
			String dataSourceJndi = domini.getJndiDatasource();
			if (isDesplegamentTomcat() && dataSourceJndi.endsWith("java:/es.caib.helium.db"))
				dataSourceJndi = "java:/comp/env/jdbc/HeliumDS";
			DataSource ds = (DataSource)initContext.lookup(dataSourceJndi);
			JdbcTemplate template = new JdbcTemplate(ds);
			template.setQueryTimeout(getDominiTimeout(domini));
			jdbcTemplate = new NamedParameterJdbcTemplate(template);
			jdbcTemplates.put(domini.getId(), jdbcTemplate);
		}
		return jdbcTemplate;
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

	private boolean isDesplegamentTomcat() {
		String desplegamentTomcat = GlobalProperties.getInstance().getProperty("app.domini.desplegament.tomcat");
		return "true".equalsIgnoreCase(desplegamentTomcat);
	}

	private String parametresToString(
			Map<String, Object> parametres) {
		String separador = ", ";
		StringBuilder sb = new StringBuilder();
		if (parametres != null) {
			for (String key: parametres.keySet()) {
				sb.append(key);
				sb.append(":");
				sb.append(parametres.get(key));
				sb.append(separador);
			}
		}
		if (sb.length() > 0)
			sb.substring(0, sb.length() - separador.length());
		return sb.toString();
	}

	private IntegracioParametreDto[] toIntegracioParametres(
			Map<String, Object> params) {
		if (params == null)
			return null;
		IntegracioParametreDto[] ips = new IntegracioParametreDto[params.size()];
		int i = 0;
		for (String clau: params.keySet()) {
			Object valor = params.get(clau);
			String valorStr = (valor != null) ? valor.toString() : null;
			ips[i++] = new IntegracioParametreDto(
					clau,
					valorStr);
		}
		return ips;
	}
	
	private Integer getDominiTimeout (Domini domini) {
		Integer timeout = 10000; //valor per defecte
		if (domini.getTimeout() != null && domini.getTimeout() > 0)
			timeout = domini.getTimeout() * 1000; //valor específic de timeout del domini
		else if (GlobalProperties.getInstance().getProperty("app.domini.timeout") != null)
			timeout = Integer.parseInt(GlobalProperties.getInstance().getProperty("app.domini.timeout")); //valor global de timeout pels dominis
		return timeout;
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
		/* Per suprimir */
		} else if ("PERSONES_AMB_CARREC".equals(id)) {
			return personesAmbCarrec(parametersMap);
		}
		return new ArrayList<FilaResultat>();
	}
	
	public List<FilaResultat> personaAmbCodi(Map<String, Object> parametres) {
		List<FilaResultat> resposta = new ArrayList<FilaResultat>();
		PersonaDto persona = pluginHelper.personaFindAmbCodi((String)parametres.get("persona"));
		if (persona != null)
			resposta.add(novaFilaPersona(persona));
		return resposta;
	}
	
	public List<FilaResultat> personesAmbArea(Map<String, Object> parametres) {
		List<FilaResultat> resposta = new ArrayList<FilaResultat>();
		for (String personaCodi: getPersonesPerArea((String)parametres.get("entorn"), (String)parametres.get("area"))) {
			PersonaDto persona = pluginHelper.personaFindAmbCodi(personaCodi);
			if (persona != null)
				resposta.add(novaFilaPersona(persona));
		}
		return resposta;
	}
	
	public List<FilaResultat> personesAmbCarrecArea(Map<String, Object> parametres, boolean nomesUna) {
		List<FilaResultat> resposta = new ArrayList<FilaResultat>();
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
		List<FilaResultat> resposta = new ArrayList<FilaResultat>();
		for (Area area: getAreesAmbPare((String)parametres.get("entorn"), (String)parametres.get("pare"))) {
			resposta.add(novaFilaArea(area));
		}
		return resposta;
	}
	
	public List<FilaResultat> areesAmbPersona(Map<String, Object> parametres) {
		List<FilaResultat> resposta = new ArrayList<FilaResultat>();
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
			Permis rol = permisRepository.findOne((String)parametres.get("rol"));
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
			for (String personaCodi: carrecJbpmIdRepository.findPersonesCodiByGrupCodi((String)parametres.get("rol"))) {
				PersonaDto persona = pluginHelper.personaFindAmbCodi(personaCodi);
				if (persona != null)
					resposta.add(novaFilaPersona(persona));
			}
		}
		return resposta;
	}

	public List<FilaResultat> rolsPerUsuari(Map<String, Object> parametres) {
		List<FilaResultat> resposta = new ArrayList<FilaResultat>();
		if (isHeliumIdentitySource()) {
			Usuari usuari = usuariRepository.findOne((String)parametres.get("persona"));
			if (usuari != null) {
				for (Permis rol: usuari.getPermisos()) {
					FilaResultat fila = new FilaResultat();
					fila.addColumna(new ParellaCodiValor("rol", rol.getCodi()));
					resposta.add(fila);
				}
			}
		} else {
			for (String rol: areaJbpmIdRepository.findRolesAmbUsuariCodi((String)parametres.get("persona"))) {
				FilaResultat fila = new FilaResultat();
				fila.addColumna(new ParellaCodiValor("rol", rol));
				resposta.add(fila);
			}
		}
		return resposta;
	}
	
	public List<FilaResultat> variableRegistre(Map<String, Object> parametres) {
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
			
			camp = campRepository.findOne(dada.getCampId());
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
			
			camp = campRepository.findOne(dada.getCampId());
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
			return carrecJbpmIdRepository.findPersonesCodiByGrupCodi(areaCodi);
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
			return carrecJbpmIdRepository.findPersonesCodiByCarrecCodi(carrecCodi);
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
			return carrecJbpmIdRepository.findPersonaCodiByGrupCodiAndCarrecCodi(areaCodi, carrecCodi);
		}
	}
	
	private List<String> getGrupsPerPersona(String personaCodi) {
		if (isHeliumIdentitySource()) {
			return findAreesMembre(personaCodi);
		} else {
			return areaJbpmIdRepository.findAreesJbpmIdMembre(personaCodi);
		}
	}

	private List<Area> getAreesAmbPare(String entornCodi, String areaCodi) {
		Entorn entorn = entornRepository.findByCodi(entornCodi);
		return areaRepository.findByEntornAndPareCodi(entorn, areaCodi);
	}
	
	private List<String> findCodisPersonaAmbArea(Long areaId) {
		Area area = areaRepository.findOne(areaId);
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
		String organigramaActiu = GlobalProperties.getInstance().getProperty("app.jbpm.identity.source");
		return "helium".equalsIgnoreCase(organigramaActiu);
	}
	
	private static final Logger logger = LoggerFactory.getLogger(DominiHelper.class);

}
