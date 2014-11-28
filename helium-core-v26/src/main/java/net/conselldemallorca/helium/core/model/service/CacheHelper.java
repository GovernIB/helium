/**
 * 
 */
package net.conselldemallorca.helium.core.model.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.conselldemallorca.helium.core.extern.domini.FilaResultat;
import net.conselldemallorca.helium.core.model.dao.DominiDao;
import net.conselldemallorca.helium.core.model.dao.EntornDao;
import net.conselldemallorca.helium.core.model.exception.DominiException;
import net.conselldemallorca.helium.core.model.hibernate.Domini;
import net.conselldemallorca.helium.core.model.hibernate.Domini.TipusAuthDomini;
import net.conselldemallorca.helium.core.model.hibernate.Domini.TipusDomini;
import net.conselldemallorca.helium.core.util.GlobalProperties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

/**
 * Helper per cache
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class CacheHelper {
	private static final String CACHE_KEY_SEPARATOR = "#";

	private Map<String, Long> dominiCacheTime = new HashMap<String, Long>();

	@Resource
	private CacheManager cacheManager;
	private EntornDao entornDao;
	private DominiDao dominiDao;

	@Autowired
	public void setEntornDao(EntornDao entornDao) {
		this.entornDao = entornDao;
	}

	@Autowired
	public void setDominiDao(DominiDao dominiDao) {
		this.dominiDao = dominiDao;
	}

	@SuppressWarnings("unchecked")
	// @Cacheable(value="dominiCache", key="cacheKey", condition="domini.cacheSegons > 0")
	public List<FilaResultat> getResultatDomini(Long entornId, Long dominiId, String dominiConsultaWsId, Map<String, Object> parametres) {
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
			domini.setEntorn(entornDao.getById(entornId, false));
			domini.setUrl(GlobalProperties.getInstance().getProperty("app.domini.intern.url", "http://localhost:8080/helium/ws/DominiIntern"));
		} else {
			if (dominiId != null) {
				domini = dominiDao.getById(dominiId, false);
			}
		}
		String cacheKey = getCacheKey(domini.getId(), dominiConsultaWsId, parametres);
		if (dominiCacheTime.containsKey(cacheKey)) {
			if ((new Date().getTime() - dominiCacheTime.get(cacheKey)) - (domini.getCacheSegons() * 1000) > 0) {
				cacheManager.getCache("dominiCache").evict(cacheKey);
				cachear = true;
			} else {
				try {
					return (List<FilaResultat>) cacheManager.getCache("dominiCache").get(cacheKey).get();
				} catch (Exception ex) {
					logger.error("ERROR: No s'ha pogut consultar el domini cache: " + domini.getCodi(), ex);
					throw new DominiException("No s'ha pogut consultar el domini cache: " + domini.getCodi(), ex);
				}
			}
		} else if (domini.getCacheSegons() > 0) {
			cachear = true;
		}

		List<FilaResultat> resultat;
		try {
			resultat = dominiDao.getResultat(domini, dominiConsultaWsId, parametres);
			if (cachear) {
				cacheManager.getCache("dominiCache").put(cacheKey, resultat);
				dominiCacheTime.put(cacheKey, new Date().getTime());
			}
		} catch (Exception ex) {
			logger.error("ERROR: No s'ha pogut consultar el domini resultat: " + domini.getCodi(), ex);
			throw new DominiException("No s'ha pogut consultar el domini resultat: " + domini.getCodi(), ex);
		}
		return resultat;
	}

	private String getCacheKey(Long dominiId, String dominiWsId, Map<String, Object> parametres) {
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

	private static final Log logger = LogFactory.getLog(CacheHelper.class);
}
