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

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

import net.conselldemallorca.helium.core.extern.domini.DominiHelium;
import net.conselldemallorca.helium.core.extern.domini.DominiHeliumException;
import net.conselldemallorca.helium.core.extern.domini.FilaResultat;
import net.conselldemallorca.helium.core.extern.domini.ParellaCodiValor;
import net.conselldemallorca.helium.core.helper.WsClientHelper.WsClientAuth;
import net.conselldemallorca.helium.core.model.hibernate.Domini;
import net.conselldemallorca.helium.core.model.hibernate.Domini.OrigenCredencials;
import net.conselldemallorca.helium.core.model.hibernate.Domini.TipusAuthDomini;
import net.conselldemallorca.helium.core.model.hibernate.Domini.TipusDomini;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.v3.core.api.dto.IntegracioAccioTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.IntegracioParametreDto;
import net.conselldemallorca.helium.v3.core.api.exception.DominiConsultaException;
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
	private MetricRegistry metricRegistry;
	@Autowired
	private CacheManager cacheManager;



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
							domini.getExpedientTipus().getCodi()));
			final Timer.Context contextTipexp = timerTipexp.time();
			Counter countTipexp = metricRegistry.counter(
					MetricRegistry.name(
							DominiHelper.class,
							"consultar.count",
							domini.getEntorn().getCodi(),
							domini.getExpedientTipus().getCodi()));
			countTipexp.inc();
			final Timer timerDomini = metricRegistry.timer(
					MetricRegistry.name(
							DominiHelper.class,
							"consultar",
							domini.getEntorn().getCodi(),
							domini.getExpedientTipus().getCodi(),
							domini.getCodi()));
			final Timer.Context contextDomini = timerDomini.time();
			Counter countDomini = metricRegistry.counter(
					MetricRegistry.name(
							DominiHelper.class,
							"consultar.count",
							domini.getEntorn().getCodi(),
							domini.getExpedientTipus().getCodi(),
							domini.getCodi()));
			countDomini.inc();
			try {
				if (domini.getTipus().equals(TipusDomini.CONSULTA_WS))
					resultat = consultaWs(domini, id, parametres);
				else if (domini.getTipus().equals(TipusDomini.CONSULTA_SQL))
					resultat = consultaSql(domini, parametres);
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
								domini.getExpedientTipus().getCodi()));
				counterOkTipexp.inc();
				final Counter counterOkDomini = metricRegistry.counter(
						MetricRegistry.name(
								DominiHelper.class,
								"consultar.ok",
								domini.getEntorn().getCodi(),
								domini.getExpedientTipus().getCodi(),
								domini.getCodi()));
				counterOkDomini.inc();
			} catch (DominiConsultaException ex) {
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
								domini.getExpedientTipus().getCodi()));
				counterErrorTipexp.inc();
				final Counter counterErrorDomini = metricRegistry.counter(
						MetricRegistry.name(
								DominiHelper.class,
								"consultar.error",
								domini.getEntorn().getCodi(),
								domini.getExpedientTipus().getCodi(),
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
		dominiIntern.setCodi("intern");
		dominiIntern.setNom("Domini intern");
		dominiIntern.setTipus(TipusDomini.CONSULTA_WS);
		dominiIntern.setTipusAuth(TipusAuthDomini.NONE);
		dominiIntern.setUrl(GlobalProperties.getInstance().getProperty("app.domini.intern.url","http://localhost:8080/helium/ws/DominiIntern"));
		return consultar(
				dominiIntern,
				id,
				parametres);
	}



	private List<FilaResultat> consultaWs(
			Domini domini,
			String id,
			Map<String, Object> parametres) {
		DominiHelium client = getClientWsFromDomini(domini);
		List<ParellaCodiValor> paramsConsulta = new ArrayList<ParellaCodiValor>();
		if ("intern".equalsIgnoreCase(domini.getCodi())) {
			paramsConsulta.add(
					new ParellaCodiValor(
							"entorn",
							domini.getEntorn().getCodi()));
		}
		if (parametres != null) {
			for (String codi: parametres.keySet()) {
				paramsConsulta.add(new ParellaCodiValor(
						codi,
						parametres.get(codi)));
			}
		}
		long t0 = System.currentTimeMillis();
		try {
			logger.debug("Petició de domini de tipus WS (" +
					"id=" + domini.getId() + ", " +
					"codi=" + domini.getCodi() + ", " +
					"params=" + parametresToString(parametres) + ")");
			List<FilaResultat> resposta = client.consultaDomini(id, paramsConsulta);
			monitorDominiHelper.addAccioOk(
					domini,
					"Consulta WS (id=" + id + ")",
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					toIntegracioParametres(parametres));
			return resposta;
		} catch (DominiHeliumException ex) {
			monitorDominiHelper.addAccioError(
					domini,
					"Consulta WS (id=" + id + ")",
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					ex.getMessage(),
					ex,
					toIntegracioParametres(parametres));
			throw new DominiConsultaException(
					"No s'ha pogut consultar el domini (" +
							"id=" + domini.getId() + ", " +
							"codi=" + domini.getCodi() + ", " +
							"tipus=" + domini.getTipus() + ")",
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
			throw new DominiConsultaException(
					"No s'ha pogut consultar el domini (" +
							"id=" + domini.getId() + ", " +
							"codi=" + domini.getCodi() + ", " +
							"tipus=" + domini.getTipus() + ")",
					ex);
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
		return wsClientHelper.getDominiService(
				domini.getUrl(),
				auth,
				username,
				password);
	}

	private NamedParameterJdbcTemplate getJdbcTemplateFromDomini(Domini domini) throws NamingException {
		NamedParameterJdbcTemplate jdbcTemplate = jdbcTemplates.get(domini.getId());
		if (jdbcTemplate == null) {
			Context initContext = new InitialContext();
			String dataSourceJndi = domini.getJndiDatasource();
			if (isDesplegamentTomcat() && dataSourceJndi.endsWith("java:/es.caib.helium.db"))
				dataSourceJndi = "java:/comp/env/jdbc/HeliumDS";
			DataSource ds = (DataSource)initContext.lookup(dataSourceJndi);
			jdbcTemplate = new NamedParameterJdbcTemplate(ds);
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

	private static final Logger logger = LoggerFactory.getLogger(DominiHelper.class);

}
