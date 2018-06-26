package net.conselldemallorca.helium.core.model.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.hibernate.criterion.Restrictions;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import net.conselldemallorca.helium.core.extern.domini.DominiHelium;
import net.conselldemallorca.helium.core.helperv26.MesuresTemporalsHelper;
import net.conselldemallorca.helium.core.model.hibernate.Domini;

/**
 * Dao pels objectes de tipus Domini
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class DominiDao extends HibernateGenericDao<Domini, Long> {

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
		List<Domini> dominis = findByCriteria(
				Restrictions.eq("entorn.id", entornId),
				Restrictions.eq("codi", codi));
		if (dominis.size() > 0)
			return dominis.get(0);
		return null;
	}

	public void makeDirty(Long dominiId) {
		wsCache.remove(dominiId);
		jdbcTemplates.remove(dominiId);
	}

	/*private List<FilaResultat> consultaWs(
			Domini domini,
			String id,
			Map<String, Object> parametres) throws Exception {
		mesuresTemporalsHelper.mesuraIniciar("DOMINI WS: " + domini.getCodi(), "domini");
		DominiHelium client = getClientWsFromDomini(domini);
		List<ParellaCodiValor> paramsConsulta = new ArrayList<ParellaCodiValor>();
		if (DominiHelper.DOMINI_INTERN_CODI.equalsIgnoreCase(domini.getCodi())) {
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
	private List<FilaResultat> consultaSql(
			Domini domini,
			Map<String, Object> parametres) throws DominiException {
		try {
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
		} catch (Exception ex) {
			throw new DominiException("No s'ha pogut consultar el domini: " + domini.getCodi(), ex);
		}
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

	public List<FilaResultat> getResultat(
			Domini domini,
			String dominiConsultaWsId,
			Map<String, Object> parametres) throws Exception {
		List<FilaResultat> resultat = null;
		if (domini.getTipus().equals(TipusDomini.CONSULTA_WS))
			resultat = consultaWs(domini, dominiConsultaWsId, parametres);
		else if (domini.getTipus().equals(TipusDomini.CONSULTA_SQL))
			resultat = consultaSql(domini, parametres);
		if (resultat == null)
			resultat = new ArrayList<FilaResultat>();
		return resultat;
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

	private static final Log logger = LogFactory.getLog(DominiDao.class);*/

}
