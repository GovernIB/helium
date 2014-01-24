/**
 * 
 */
package net.conselldemallorca.helium.core.model.dao;

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
import net.conselldemallorca.helium.core.model.hibernate.Domini;
import net.conselldemallorca.helium.core.model.hibernate.Domini.OrigenCredencials;
import net.conselldemallorca.helium.core.model.hibernate.Domini.TipusAuthDomini;
import net.conselldemallorca.helium.core.model.hibernate.Domini.TipusDomini;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.core.util.ws.WsClientUtils;
import net.conselldemallorca.helium.v3.core.helper.MesuresTemporalsHelper;

import org.hibernate.criterion.Restrictions;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Dao pels objectes de tipus Domini
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class DominiDao extends HibernateGenericDao<Domini, Long> {

	private static final String CACHE_KEY_SEPARATOR = "#";

	private Map<Long, DominiHelium> wsCache = new HashMap<Long, DominiHelium>();
	private Map<Long, NamedParameterJdbcTemplate> jdbcTemplates = new HashMap<Long, NamedParameterJdbcTemplate>();

	@Resource
	private MesuresTemporalsHelper mesuresTemporalsHelper;

	public DominiDao() {
		super(Domini.class);
	}

	public List<Domini> findAmbEntorn(Long entornId) {
		return findByCriteria(Restrictions.eq("entorn.id", entornId),
				Restrictions.isNull("expedientTipus.id"));
	}
	public List<Domini> findAmbEntornITipusExp(Long entornId, Long tipusExpedientId) {
		return findByCriteria(
				Restrictions.eq("entorn.id", entornId),
				Restrictions.eq("expedientTipus.id", tipusExpedientId));
	}
	public List<Domini> findAmbEntornITipusExpONull(Long entornId, Long tipusExpedientId) {
		List<Domini> dominis = findByCriteria(
				Restrictions.eq("entorn.id", entornId),
				Restrictions.isNull("expedientTipus.id"));
		if (tipusExpedientId != null){
			dominis.addAll(findAmbEntornITipusExp(entornId, tipusExpedientId));
		}
		return dominis;
	}
	public Domini findAmbEntornICodi(Long entornId, String codi) {
		if(codi.equalsIgnoreCase("intern")){
			Domini domini = new Domini();
			domini.setId((long) 0);
			domini.setCacheSegons(30);
			domini.setCodi("intern");
			domini.setNom("Domini intern");
			domini.setTipus(TipusDomini.CONSULTA_WS);
			domini.setTipusAuth(TipusAuthDomini.NONE);
			domini.setEntorn((Entorn)getSession().load(Entorn.class, entornId));
			domini.setUrl(GlobalProperties.getInstance().getProperty("app.domini.intern.url","http://localhost:8080/helium/ws/DominiIntern"));
			return domini;
		} else {
			List<Domini> dominis = findByCriteria(
					Restrictions.eq("entorn.id", entornId),
					Restrictions.eq("codi", codi));
			if (dominis.size() > 0)
				return dominis.get(0);
			return null;
		}
	}

	public List<FilaResultat> consultar(
			Long entornId,
			Long dominiId,
			String id,
			Map<String, Object> parametres) throws Exception {
		List<FilaResultat> resultat = null;
		Domini domini = new Domini();
		if(dominiId == 0){
			domini.setId((long) 0);
			domini.setCacheSegons(30);
			domini.setCodi("intern");
			domini.setNom("Domini intern");
			domini.setTipus(TipusDomini.CONSULTA_WS);
			domini.setTipusAuth(TipusAuthDomini.NONE);
			domini.setEntorn((Entorn)getSession().load(Entorn.class, entornId));
			domini.setUrl(GlobalProperties.getInstance().getProperty("app.domini.intern.url","http://localhost:8080/helium/ws/DominiIntern"));
		} else {
			if(dominiId != null){
				domini = getById(dominiId, false);
			}
		}
		String cacheKey = getCacheKey(domini.getId(), parametres);
		resultat = getResultatFromCache(
				domini,
				id,
				parametres,
				cacheKey);
		/*Element element = null;
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
					//logger.info("Cache domini '" + cacheKey + "': " + resultat.size() + " registres");
				}
			}
		} else {
			resultat = (List<FilaResultat>)element.getValue();
			//logger.info("Resultat en cache");
		}*/
		if (resultat == null)
			resultat = new ArrayList<FilaResultat>();
		return resultat;
	}

	public void makeDirty(Long dominiId) {
		wsCache.remove(dominiId);
		jdbcTemplates.remove(dominiId);
	}

	private List<FilaResultat> consultaWs(
			Domini domini,
			String id,
			Map<String, Object> parametres) throws Exception {
		mesuresTemporalsHelper.mesuraIniciar("DOMINI WS: " + domini.getCodi(), "domini");
		if ("intern".equalsIgnoreCase(domini.getCodi())) {
			parametres.put("entorn", domini.getEntorn().getCodi());
		}
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
		DominiHelium client = (DominiHelium)WsClientUtils.getWsClientProxy(
				DominiHelium.class,
				domini.getUrl(),
				usuari,
				contrasenya,
				auth,
				false,
				false,
				true);
		List<ParellaCodiValor> paramsConsulta = new ArrayList<ParellaCodiValor>();
		if (parametres != null) {
			for (String codi: parametres.keySet()) {
				paramsConsulta.add(new ParellaCodiValor(
						codi,
						parametres.get(codi)));
			}
		}
		List<FilaResultat> resposta = client.consultaDomini(id, paramsConsulta);
		mesuresTemporalsHelper.mesuraCalcular("DOMINI WS: " + domini.getCodi(), "domini");
		return resposta;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List<FilaResultat> consultaSql(
			Domini domini,
			Map<String, Object> parametres) throws DominiException {
		try {
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
		} catch (Exception ex) {
			throw new DominiException("No s'ha pogut consultar el domini", ex);
		}
	}

	private NamedParameterJdbcTemplate getJdbcTemplateFromDomini(Domini domini) throws NamingException {
		NamedParameterJdbcTemplate jdbcTemplate = jdbcTemplates.get(domini.getId());
		if (jdbcTemplate == null) {
			Context initContext = new InitialContext();
			DataSource ds = (DataSource)initContext.lookup(domini.getJndiDatasource());
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

	@Cacheable(value="dominiCache", key="cacheKey", condition="domini.cacheSegons > 0")
	private List<FilaResultat> getResultatFromCache(
			Domini domini,
			String dominiConsultaWsId,
			Map<String, Object> parametres,
			String cacheKey) throws Exception {
		List<FilaResultat> resultat = null;
		if (domini.getTipus().equals(TipusDomini.CONSULTA_WS))
			resultat = consultaWs(domini, dominiConsultaWsId, parametres);
		else if (domini.getTipus().equals(TipusDomini.CONSULTA_SQL))
			resultat = consultaSql(domini, parametres);
		/*if (domini.getCacheSegons() > 0) {
			element = new Element(cacheKey, resultat);
			element.setTimeToLive(domini.getCacheSegons());
			if (dominiCache != null) {
				dominiCache.put(element);
				//logger.info("Cache domini '" + cacheKey + "': " + resultat.size() + " registres");
			}
		}*/
		return resultat;
	}

}
