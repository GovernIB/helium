/**
 * 
 */
package net.conselldemallorca.helium.core.helperv26;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import net.conselldemallorca.helium.core.extern.domini.DominiHelium;
import net.conselldemallorca.helium.core.extern.domini.DominiHeliumException;
import net.conselldemallorca.helium.core.extern.domini.FilaResultat;
import net.conselldemallorca.helium.core.extern.domini.ParellaCodiValor;
import net.conselldemallorca.helium.core.model.hibernate.Domini;
import net.conselldemallorca.helium.core.model.hibernate.Domini.OrigenCredencials;
import net.conselldemallorca.helium.core.model.hibernate.Domini.TipusAuthDomini;
import net.conselldemallorca.helium.core.model.hibernate.Domini.TipusDomini;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.core.util.ws.WsClientUtils;
import net.conselldemallorca.helium.integracio.plugins.persones.DadesPersona;
import net.conselldemallorca.helium.integracio.plugins.persones.PersonesPlugin;
import net.conselldemallorca.helium.integracio.plugins.persones.PersonesPluginException;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.exception.DominiConsultaException;
import net.conselldemallorca.helium.v3.core.repository.DominiRepository;
import net.conselldemallorca.helium.v3.core.repository.EntornRepository;

/**
 * Helper per cache
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class CacheHelper {

	private static final String CACHE_DOMINI_ID = "dominiCache";
	private static final String CACHE_PERSONA_ID = "personaPluginV26Cache";

	private static final String CACHE_KEY_SEPARATOR = "#";

	private Map<String, Long> dominiCacheTime = new HashMap<String, Long>();
	private Map<Long, DominiHelium> wsCache = new HashMap<Long, DominiHelium>();
	private Map<Long, NamedParameterJdbcTemplate> jdbcTemplates = new HashMap<Long, NamedParameterJdbcTemplate>();

	private CacheManager cacheManager;
	private EntornRepository entornRepository;
	private DominiRepository dominiRepository;

	@Resource
	private MesuresTemporalsHelper mesuresTemporalsHelper;



	@SuppressWarnings("unchecked")
	public List<FilaResultat> getResultatConsultaDomini(
			Long entornId,
			Long dominiId,
			String dominiConsultaWsId,
			Map<String, Object> parametres) {
		boolean cachear = false;
		Domini domini = null;
		if (dominiId == 0) {
			domini = new Domini();
			domini.setId((long) 0);
			domini.setCacheSegons(30);
			domini.setCodi("intern");
			domini.setNom("Domini intern");
			domini.setTipus(TipusDomini.CONSULTA_WS);
			domini.setTipusAuth(TipusAuthDomini.NONE);
			domini.setEntorn(entornRepository.findOne(entornId));
			domini.setUrl(GlobalProperties.getInstance().getProperty("app.domini.intern.url", "http://localhost:8080/helium/ws/DominiIntern"));
		} else {
			if (dominiId != null) {
				domini = dominiRepository.findOne(dominiId);
			}
		}
		String cacheKey = getDominiCacheKey(domini.getId(), dominiConsultaWsId, parametres);
		if (dominiCacheTime.containsKey(cacheKey)) {
			if ((new Date().getTime() - dominiCacheTime.get(cacheKey)) - (domini.getCacheSegons() * 1000) > 0) {
				cacheManager.getCache(CACHE_DOMINI_ID).evict(cacheKey);
				cachear = true;
			} else {
				try {
					return (List<FilaResultat>)cacheManager.getCache(CACHE_DOMINI_ID).get(cacheKey).get();
				} catch (Exception ex) {
					logger.error(
							"No s'ha pogut consultar el domini (CACHE) (" +
									"id=" + domini.getId() + ", " +
									"codi=" + domini.getCodi() + ", " +
									"tipus=" + domini.getTipus() + ")",
							ex);
					throw new DominiConsultaException(
							"No s'ha pogut consultar el domini (CACHE) (" +
									"id=" + domini.getId() + ", " +
									"codi=" + domini.getCodi() + ", " +
									"tipus=" + domini.getTipus() + ")",
							ex);
				}
			}
		} else if (domini.getCacheSegons() > 0) {
			cachear = true;
		}
		List<FilaResultat> resultat = null;
		try {
			if (domini.getTipus().equals(TipusDomini.CONSULTA_WS))
				resultat = dominiConsultaWs(domini, dominiConsultaWsId, parametres);
			else if (domini.getTipus().equals(TipusDomini.CONSULTA_SQL))
				resultat = dominiConsultaSql(domini, parametres);
			if (resultat == null)
				resultat = new ArrayList<FilaResultat>();
			if (cachear) {
				cacheManager.getCache(CACHE_DOMINI_ID).put(cacheKey, resultat);
				dominiCacheTime.put(cacheKey, new Date().getTime());
			}
		} catch (Exception ex) {
			logger.error(
					"No s'ha pogut consultar el domini (" +
							"id=" + domini.getId() + ", " +
							"codi=" + domini.getCodi() + ", " +
							"tipus=" + domini.getTipus() + ")",
					ex);
			throw new DominiConsultaException(
					"No s'ha pogut consultar el domini (" +
							"id=" + domini.getId() + ", " +
							"codi=" + domini.getCodi() + ", " +
							"tipus=" + domini.getTipus() + ")",
					ex);
		}
		return resultat;
	}

	public PersonaDto getPersonaFromPlugin(
			String personaCodi,
			PersonesPlugin personesPlugin) throws PersonesPluginException {
		String cacheKey = personaCodi;
		ValueWrapper cacheValue = cacheManager.getCache(CACHE_PERSONA_ID).get(cacheKey);
		PersonaDto p;
		if (cacheValue != null) {
			p = (PersonaDto)cacheValue.get();
		} else {
			DadesPersona persona = personesPlugin.findAmbCodi(personaCodi);
			if (persona == null) {
				return null;
			}
			PersonaDto.Sexe sexe = null;
			if (persona.getSexe().equals(DadesPersona.Sexe.SEXE_HOME))
				sexe = PersonaDto.Sexe.SEXE_HOME;
			else
				sexe = PersonaDto.Sexe.SEXE_DONA;
			p = new PersonaDto(
					persona.getCodi(),
					persona.getNomSencer(),
					persona.getEmail(),
					sexe);
			p.setDni(persona.getDni());
			cacheManager.getCache(CACHE_PERSONA_ID).put(cacheKey, p);
		}
		return p;
	}

	@Autowired
	public void setCacheManager(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}



	private String getDominiCacheKey(
			Long dominiId,
			String dominiWsId,
			Map<String, Object> parametres) {
		StringBuffer sb = new StringBuffer();
		sb.append(dominiId.toString());
		sb.append(CACHE_KEY_SEPARATOR);
		sb.append(dominiWsId);
		sb.append(CACHE_KEY_SEPARATOR);
		if (parametres != null) {
			for (String clau : parametres.keySet()) {
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

	private List<FilaResultat> dominiConsultaWs(
			Domini domini,
			String id,
			Map<String, Object> parametres) throws DominiHeliumException {
		mesuresTemporalsHelper.mesuraIniciar("DOMINI WS: " + domini.getCodi(), "domini");
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
		logger.debug("Petició de domini de tipus WS (" +
				"id=" + domini.getId() + ", " +
				"codi=" + domini.getCodi() + ", " +
				"params=" + parametresToString(parametres) + ")");
		List<FilaResultat> resposta = client.consultaDomini(id, paramsConsulta);
		mesuresTemporalsHelper.mesuraCalcular("DOMINI WS: " + domini.getCodi(), "domini");
		return resposta;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List<FilaResultat> dominiConsultaSql(
			Domini domini,
			Map<String, Object> parametres) throws NamingException {
		logger.debug("Petició de domini de tipus SQL (" +
				"id=" + domini.getId() + ", " +
				"codi=" + domini.getCodi() + ", " +
				"params=" + parametresToString(parametres) + ")");
		mesuresTemporalsHelper.mesuraIniciar("DOMINI SQL: " + domini.getCodi(), "domini");
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
		mesuresTemporalsHelper.mesuraCalcular("DOMINI SQL: " + domini.getCodi(), "domini");
		return resultat;
	}

	private DominiHelium getClientWsFromDomini(Domini domini) {
		DominiHelium clientWs = wsCache.get(domini.getId());
		if (clientWs == null) {
			String usuari = null;
			String contrasenya = null;
			if (domini.getTipusAuth() != null && !TipusAuthDomini.NONE.equals(domini.getTipusAuth())) {
				if (OrigenCredencials.PROPERTIES.equals(domini.getOrigenCredencials())) {
					usuari = GlobalProperties.getInstance().getProperty(domini.getUsuari());
					contrasenya = GlobalProperties.getInstance().getProperty(domini.getContrasenya());
				} else {
					usuari = domini.getUsuari();
					contrasenya = domini.getContrasenya();
				}
			}
			String auth = "NONE";
			if (TipusAuthDomini.HTTP_BASIC.equals(domini.getTipusAuth()))
				auth = "BASIC";
			if (TipusAuthDomini.USERNAMETOKEN.equals(domini.getTipusAuth()))
				auth = "USERNAMETOKEN";
			clientWs = (DominiHelium)WsClientUtils.getWsClientProxy(
					DominiHelium.class,
					domini.getUrl(),
					usuari,
					contrasenya,
					auth,
					false,
					false,
					true);
			wsCache.put(domini.getId(), clientWs);
		}
		return clientWs;
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

	private static final Log logger = LogFactory.getLog(CacheHelper.class);

}
