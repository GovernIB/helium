/**
 * 
 */
package net.conselldemallorca.helium.v3.core.helper;

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

import net.conselldemallorca.helium.core.extern.domini.DominiHelium;
import net.conselldemallorca.helium.core.extern.domini.FilaResultat;
import net.conselldemallorca.helium.core.extern.domini.ParellaCodiValor;
import net.conselldemallorca.helium.core.model.exception.DominiException;
import net.conselldemallorca.helium.core.model.hibernate.Domini.OrigenCredencials;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.core.util.ws.WsClientUtils;
import net.conselldemallorca.helium.v3.core.api.dto.DominiDto;
import net.conselldemallorca.helium.v3.core.api.dto.DominiDto.TipusAuthDomini;
import net.conselldemallorca.helium.v3.core.api.dto.DominiDto.TipusDomini;
import net.conselldemallorca.helium.v3.core.repository.DominiRepository;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Helper per a fer consultes a dominis i enumeracions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class DominiHelper {

	@Resource
	private DominiRepository dominiRepository;

	private static final String CACHE_KEY_SEPARATOR = "#";
	private Ehcache dominiCache;

	private Map<Long, DominiHelium> wsCache = new HashMap<Long, DominiHelium>();
	private Map<Long, NamedParameterJdbcTemplate> jdbcTemplates = new HashMap<Long, NamedParameterJdbcTemplate>();

	@SuppressWarnings("unchecked")
	public List<FilaResultat> consultar(
			DominiDto domini,
			String id,
			Map<String, Object> parametres) throws Exception {
		List<FilaResultat> resultat = null;
		String cacheKey = getCacheKey(domini.getId(), parametres);
		Element element = null;
		if (dominiCache != null)
			element = dominiCache.get(cacheKey);
		if (element == null) {
			if (domini.getTipus().equals(TipusDomini.CONSULTA_WS))
				resultat = consultaWs(domini, id, parametres);
			else if (domini.getTipus().equals(TipusDomini.CONSULTA_SQL))
				resultat = consultaSql(domini, parametres);
			if (domini.getCacheSegons() > 0) {
				element = new Element(cacheKey, resultat);
				element.setTimeToLive(domini.getCacheSegons());
				if (dominiCache != null) {
					dominiCache.put(element);
				}
			}
		} else {
			resultat = (List<FilaResultat>)element.getValue();
		}
		if (resultat == null)
			resultat = new ArrayList<FilaResultat>();
		return resultat;
	}

	public void makeDirty(Long dominiId) {
		wsCache.remove(dominiId);
		jdbcTemplates.remove(dominiId);
	}



	private List<FilaResultat> consultaWs(
			DominiDto domini,
			String id,
			Map<String, Object> parametres) throws Exception {
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
		List<FilaResultat> resposta = client.consultaDomini(id, paramsConsulta);
		return resposta;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List<FilaResultat> consultaSql(
			DominiDto domini,
			Map<String, Object> parametres) throws DominiException {
		try {
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
			return resultat;
		} catch (Exception ex) {
			throw new DominiException("No s'ha pogut consultar el domini", ex);
		}
	}

	private DominiHelium getClientWsFromDomini(DominiDto domini) {
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

	private NamedParameterJdbcTemplate getJdbcTemplateFromDomini(DominiDto domini) throws NamingException {
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
			Map<String, Object> parametres) {
		StringBuffer sb = new StringBuffer();
		sb.append(dominiId.toString());
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

}
