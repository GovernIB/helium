package es.caib.helium.back.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import es.caib.helium.commons.config.PropertyConfig;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import es.caib.helium.commons.utils.GlobalProperties;

/**
 * Interceptor per guardar a la sessió les dades de la persona
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class PropertiesInterceptor implements HandlerInterceptor {

	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		if (request.getUserPrincipal() != null) {
			GlobalProperties globalProperties = GlobalProperties.getInstance();
			request.setAttribute(
				"propBaseUrl",
				globalProperties.getProperty(PropertyConfig.PROP_BASE_URL));
			request.setAttribute(
				"propCapsaleraColorLletra",
				globalProperties.getProperty(PropertyConfig.PROP_CAPSALERA_COLOR_LLETRA));
			request.setAttribute(
				"propCapsaleraColorFons",
				globalProperties.getProperty(PropertyConfig.PROP_CAPSALERA_COLOR_FONS));
			request.setAttribute(
				"propAnotacionsConsultaNumThreads",
				globalProperties.getProperty(PropertyConfig.PROP_ANOTACIONS_CONSULTA_NUM_THREADS));
			request.setAttribute(
				"propSegonplaRefrescarAuto",
				globalProperties.getProperty(PropertyConfig.PROP_SEGONPLA_REFRESCAR_AUTO));
			request.setAttribute(
				"propSegonplaRefrescarAutoPeriode",
				globalProperties.getProperty(PropertyConfig.PROP_SEGONPLA_REFRESCAR_AUTO_PERIODE));
			request.setAttribute(
				"propExpedientMonitor",
				globalProperties.getProperty(PropertyConfig.PROP_EXPEDIENT_MONITOR));
			request.setAttribute(
				"propJbpmIdentitySource",
				globalProperties.getProperty(PropertyConfig.PROP_JBPM_IDENTITY_SOURCE));
			request.setAttribute(
				"propGeorefActiu",
				globalProperties.getProperty(PropertyConfig.PROP_GEOREF_ACTIU));
			request.setAttribute(
				"propGeorefTipus",
				globalProperties.getProperty(PropertyConfig.PROP_GEOREF_TIPUS));
			request.setAttribute(
				"propOrganigramaActiu",
				globalProperties.getProperty(PropertyConfig.PROP_ORGANIGRAMA_ACTIU));
			request.setAttribute(
				"propUnitatsProcedimentSync",
				globalProperties.getProperty(PropertyConfig.PROP_UNITATS_PROCEDIMENT_SYNC));
		}
		return true;
	}

}
