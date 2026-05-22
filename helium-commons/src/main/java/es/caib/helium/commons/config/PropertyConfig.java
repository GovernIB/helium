package es.caib.helium.commons.config;

/**
 * Configuració de les propietats de l'aplicació. Conté constants per les entrades de les diferents propietats.
 *
 * @author Limit Tecnologies
 */
public class PropertyConfig {

	private static final String PROPERTY_PREFIX = BaseConfig.BASE_PACKAGE + ".";

	public static final String PROP_BASE_URL = PROPERTY_PREFIX + "base.url";
	public static final String PROP_BASE_VERIFICACIO_URL = PROPERTY_PREFIX + "base.verificacio.url";
	public static final String PROP_ENTORN_HELIUM = PROPERTY_PREFIX + "entorn.helium";
	public static final String PROP_HELIUM_LOGO = PROPERTY_PREFIX + "helium.logo";
	public static final String PROP_ENCRIPTACIO_CLAU = PROPERTY_PREFIX + "encriptacio.clau";
	public static final String PROP_NUMEXP_EXPRESSION = PROPERTY_PREFIX + "numexp.expression";
	public static final String PROP_HIBERNATE_DIALECT = PROPERTY_PREFIX + "hibernate.dialect";
	public static final String PROP_REGISTRE_SEGELL_ENTITAT = PROPERTY_PREFIX + "registre.segell.entitat";
	public static final String PROP_ARXIU_VERIFICACIO_BASEURL = PROPERTY_PREFIX + "arxiu.verificacio.baseurl";
	public static final String PROP_ARXIU_MIGRACIO_REINTENTS = PROPERTY_PREFIX + "arxiu.migracio.reintents";
	public static final String PROP_SIGNATURA_TOKEN_LLARG = PROPERTY_PREFIX + "signatura.token.llarg";
	public static final String PROP_ANOTACIONS_EMAILS_AGRUPATS_CRON = PROPERTY_PREFIX + "anotacions.emails.agrupats.cron";
	public static final String PROP_ANOTACIONS_PENDENTS_COMPROVAR_INTENTS = PROPERTY_PREFIX + "anotacions.pendents.comprovar.intents";
	public static final String PROP_MASSIU_PERIODE_EXECUCIONS = PROPERTY_PREFIX + "massiu.periode.execucions";
	public static final String PROP_MASSIU_PERIODE_NOVES = PROPERTY_PREFIX + "massiu.periode.noves";
	public static final String PROP_UNITATS_PROCEDIMENT_SYNC = PROPERTY_PREFIX + "unitats.procediments.sync";
	public static final String PROP_WS_CLIENT_CHUNKED = PROPERTY_PREFIX + "ws.client.chunked";
	public static final String PROP_CALENDARI_NOLABS = PROPERTY_PREFIX + "calendari.nolabs";
	public static final String PROP_GIS_PLUGIN_SITIBSA_URL_VISOR = PROPERTY_PREFIX + "gis.plugin.sitibsa.url.visor";
	public static final String PROP_JBPM_IDENTITY_SOURCE = PROPERTY_PREFIX + "jbpm.identity.source";
	public static final String PROP_NTI_CSV_DEFINICIO = PROPERTY_PREFIX + "nti.csv.definicio";
	public static final String PROP_CAPSALERA_COLOR_LLETRA = PROPERTY_PREFIX + "capsalera.color.lletra";
	public static final String PROP_CAPSALERA_COLOR_FONS = PROPERTY_PREFIX + "capsalera.color.fons";
	public static final String PROP_SEGONPLA_REFRESCAR_AUTO = PROPERTY_PREFIX + "segonpla.refrescar.auto";
	public static final String PROP_SEGONPLA_REFRESCAR_AUTO_PERIODE = PROPERTY_PREFIX + "segonpla.refrescar.auto.periode";
	public static final String PROP_EXPEDIENT_MONITOR = PROPERTY_PREFIX + "expedient.monitor";
	public static final String PROP_ORGANIGRAMA_ACTIU = PROPERTY_PREFIX + "organigrama.actiu";

	public static final String PROP_CONVERSIO_ACTIU = PROPERTY_PREFIX + "conversio.actiu";
	public static final String PROP_CONVERSIO_VISTA_ACTIU = PROPERTY_PREFIX + "conversio.vista.actiu";
	public static final String PROP_CONVERSIO_VISTA_EXTENSION = PROPERTY_PREFIX + "conversio.vista.extension";
	public static final String PROP_CONVERSIO_GENTASCA_EXTENSION = PROPERTY_PREFIX + "conversio.gentasca.extension";
	public static final String PROP_CONVERSIO_GENTASCA_ACTIU = PROPERTY_PREFIX + "conversio.gentasca.actiu";
	public static final String PROP_CONVERSIO_OPENOFFICE_HOST = PROPERTY_PREFIX + "conversio.openoffice.host";
	public static final String PROP_CONVERSIO_OPENOFFICE_PORT = PROPERTY_PREFIX + "conversio.openoffice.port";
	public static final String PROP_CONVERSIO_OPENOFFICE_TIMEOUT = PROPERTY_PREFIX + "conversio.openoffice.timeout";
	public static final String PROP_CONVERSIO_SIGNATURA_ACTIU = PROPERTY_PREFIX + "conversio.signatura.actiu";
	public static final String PROP_CONVERSIO_SIGNATURA_EXTENSION = PROPERTY_PREFIX + "conversio.signatura.extension";
	public static final String PROP_CONVERSIO_REGISTRE_EXTENSION = PROPERTY_PREFIX + "conversio.registre.extension";
	public static final String PROP_CONVERSIO_PORTASIGNATURES_ACTIU = PROPERTY_PREFIX + "conversio.portasignatures.actiu";
	public static final String PROP_CONVERSIO_PORTASIGNATURES_EXTENSION = PROPERTY_PREFIX + "conversio.portasignatures.extension";
	public static final String PROP_CONVERSIO_DEFAULT_EXTENSION = PROPERTY_PREFIX + "conversio.default.extension";

	public static final String PROP_DISTRIBUCIO_REGLES_API_REST_URL = PROPERTY_PREFIX + "helium.distribucio.regles.api.rest.url";
	public static final String PROP_DISTRIBUCIO_REGLES_API_REST_USUARI = PROPERTY_PREFIX + "helium.distribucio.regles.api.rest.usuari";
	public static final String PROP_DISTRIBUCIO_REGLES_API_REST_PASSWORD = PROPERTY_PREFIX + "helium.distribucio.regles.api.rest.password";
	public static final String PROP_DISTRIBUCIO_REGLES_API_REST_CODI_BACKOFFICE = PROPERTY_PREFIX + "helium.distribucio.regles.api.rest.codi.backoffice";

	public static final String PROP_ANOTACIONS_CONSULTA_NUM_THREADS = PROPERTY_PREFIX + "anotacions.consulta.num.threads";

	public static final String PROP_PERSONES_PLUGIN_CLASS = PROPERTY_PREFIX + "persones.plugin.class";
	public static final String PROP_PERSONES_PLUGIN_JDBC_FILTER_CODE = PROPERTY_PREFIX + "persones.plugin.jdbc.filter.code";
	public static final String PROP_PERSONES_PLUGIN_JDBC_FILTER_NAME = PROPERTY_PREFIX + "persones.plugin.jdbc.filter.name";
	public static final String PROP_PERSONES_PLUGIN_JDBC_FILTER_CODENAME = PROPERTY_PREFIX + "persones.plugin.jdbc.filter.codename";
	public static final String PROP_PERSONES_PLUGIN_JDBC_FILTER_ROLES = PROPERTY_PREFIX + "persones.plugin.jdbc.filter.roles";
	public static final String PROP_PERSONES_PLUGIN_JDBC_FILTER_GRUP = PROPERTY_PREFIX + "persones.plugin.jdbc.filter.grup";
	public static final String PROP_PERSONES_PLUGIN_JDBC_JNDI_NAME = PROPERTY_PREFIX + "persones.plugin.jdbc.jndi.parameter";
	public static final String PROP_PERSONES_PLUGIN_JDBC_NOM_LLINATGES_JUNT = PROPERTY_PREFIX + "persones.plugin.jdbc.nom.llinatges.junt";
	public static final String PROP_PERSONES_PLUGIN_LDAP_URL = PROPERTY_PREFIX + "persones.plugin.ldap.url";
	public static final String PROP_PERSONES_PLUGIN_LDAP_PRINCIPAL = PROPERTY_PREFIX + "persones.plugin.ldap.principal";
	public static final String PROP_PERSONES_PLUGIN_LDAP_CREDENTIALS = PROPERTY_PREFIX + "persones.plugin.ldap.credentials";
	public static final String PROP_PERSONES_PLUGIN_LDAP_SEARCHBASE = PROPERTY_PREFIX + "persones.plugin.ldap.searchbase";
	public static final String PROP_PERSONES_PLUGIN_LDAP_SEARCH_BASE = PROPERTY_PREFIX + "persones.plugin.ldap.search.base";
	public static final String PROP_PERSONES_PLUGIN_LDAP_ATTRIBUTES = PROPERTY_PREFIX + "persones.plugin.ldap.attributes";
	public static final String PROP_PERSONES_PLUGIN_LDAP_ATTRIBUTE_ROLE = PROPERTY_PREFIX + "persones.plugin.ldap.attribute.role";
	public static final String PROP_PERSONES_PLUGIN_LDAP_ATTRIBUTE_ROLE_NAME = PROPERTY_PREFIX + "persones.plugin.ldap.attribute.role.name";
	public static final String PROP_PERSONES_PLUGIN_LDAP_EMAIL_DOMINI = PROPERTY_PREFIX + "persones.plugin.ldap.email.domini";
	public static final String PROP_PERSONES_PLUGIN_LDAP_FILTER_USER = PROPERTY_PREFIX + "persones.plugin.ldap.filter.user";
	public static final String PROP_PERSONES_PLUGIN_LDAP_SEARCH_FILTER_USER = PROPERTY_PREFIX + "persones.plugin.ldap.search.filter.user";
	public static final String PROP_PERSONES_PLUGIN_LDAP_SEARCH_FILTER_GRUP = PROPERTY_PREFIX + "persones.plugin.ldap.search.filter.grup";
	public static final String PROP_PERSONES_PLUGIN_LDAP_SEARCH_FILTER_LIKE = PROPERTY_PREFIX + "persones.plugin.ldap.search.filter.like";
	public static final String PROP_PERSONES_PLUGIN_LDAP_SEARCH_FILTER_USERLIKE = PROPERTY_PREFIX + "persones.plugin.ldap.search.filter.userlike";
	public static final String PROP_PERSONES_PLUGIN_SYNC_ACTIU = PROPERTY_PREFIX + "persones.plugin.sync.actiu";

	public static final String PROP_REGISTRE_PLUGIN_CLASS = PROPERTY_PREFIX + "registre.plugin.class";
	public static final String PROP_REGISTRE_PLUGIN_RW3_CLASS = PROPERTY_PREFIX + "registre.plugin.rw3.class";
	public static final String PROP_REGISTRE_PLUGIN_WS_URL = PROPERTY_PREFIX + "registre.plugin.ws.url";
	public static final String PROP_REGISTRE_PLUGIN_WS_HOST = PROPERTY_PREFIX + "registre.plugin.ws.host";
	public static final String PROP_REGISTRE_PLUGIN_WS_USUARI = PROPERTY_PREFIX + "registre.plugin.ws.usuari";
	public static final String PROP_REGISTRE_PLUGIN_WS_PASSWORD = PROPERTY_PREFIX + "registre.plugin.ws.password";

	public static final String PROP_NOTIFICACIO_PLUGIN_CLASS = PROPERTY_PREFIX + "notificacio.plugin.class";
	public static final String PROP_NOTIFICACIO_PLUGIN_URL = PROPERTY_PREFIX + "notificacio.plugin.url";
	public static final String PROP_NOTIFICACIO_PLUGIN_USERNAME = PROPERTY_PREFIX + "notificacio.plugin.username";
	public static final String PROP_NOTIFICACIO_PLUGIN_PASSWORD = PROPERTY_PREFIX + "notificacio.plugin.password";

	public static final String PROP_PORTASIGNATURES_PLUGIN_CLASS = PROPERTY_PREFIX + "portasignatures.plugin.class";
	public static final String PROP_PORTASIGNATURES_PLUGIN_PORTAFIB_URL = PROPERTY_PREFIX + "portasignatures.plugin.portafib.url";
	public static final String PROP_PORTASIGNATURES_PLUGIN_PORTAFIB_USERNAME = PROPERTY_PREFIX + "portasignatures.plugin.portafib.username";
	public static final String PROP_PORTASIGNATURES_PLUGIN_PORTAFIB_PASSWORD = PROPERTY_PREFIX + "portasignatures.plugin.portafib.password";
	public static final String PROP_PORTASIGNATURES_PLUGIN_PORTAFIB_PERFIL = PROPERTY_PREFIX + "portasignatures.plugin.portafib.perfil";
	public static final String PROP_PORTASIGNATURES_PLUGIN_PORTAFIB_FIRMASIMPLEASYNC_URL = PROPERTY_PREFIX + "portasignatures.plugin.portafib.firmasimpleasync.url";
	public static final String PROP_PORTASIGNATURES_PLUGIN_USUARI_ID = PROPERTY_PREFIX + "portasignatures.plugin.usuari.id";

	public static final String PROP_PORTAFIRMES_PLUGIN_FLUX_FIRMA_URL = PROPERTY_PREFIX + "portafirmes.plugin.flux.firma.url";
	public static final String PROP_PORTAFIRMES_PLUGIN_FLUX_FIRMA_USUARI = PROPERTY_PREFIX + "portafirmes.plugin.flux.firma.username";
	public static final String PROP_PORTAFIRMES_PLUGIN_FLUX_FIRMA_PASSWORD = PROPERTY_PREFIX + "portafirmes.plugin.flux.firma.password";
	public static final String PROP_PORTAFIRMES_PLUGIN_FLUX_ENTITATWS_URL = PROPERTY_PREFIX + "portafirmes.plugin.flux.entitatws.url";
	public static final String PROP_PORTAFIRMES_PLUGIN_FLUX_ENTITATWS_USERNAME = PROPERTY_PREFIX + "portafirmes.plugin.flux.entitatws.username";
	public static final String PROP_PORTAFIRMES_PLUGIN_FLUX_ENTITATWS_PASSWORD = PROPERTY_PREFIX + "portafirmes.plugin.flux.entitatws.password";

	public static final String PROP_SIGNATURA_PLUGIN_CLASS = PROPERTY_PREFIX + "signatura.plugin.class";
	public static final String PROP_SIGNATURA_PLUGIN_FILE_ATTACHED = PROPERTY_PREFIX + "signatura.plugin.file.attached";

	public static final String PROP_FIRMA_PLUGIN_CLASS = PROPERTY_PREFIX + "firma.plugin.class";

	public static final String PROP_CUSTODIA_PLUGIN_CLASS = PROPERTY_PREFIX + "custodia.plugin.class";
	public static final String PROP_CUSTODIA_PLUGIN_CAIB_VERIFICACIO_BASEURL = PROPERTY_PREFIX + "custodia.plugin.caib.verificacio.baseurl";

	public static final String PROP_DADESEXT_DIR3_PLUGIN_SERVICE_CLASS = PROPERTY_PREFIX + "dadesext.dir3.plugin.service.class";
	public static final String PROP_DADESEXT_DIR3_PLUGIN_SERVICE_URL = PROPERTY_PREFIX + "dadesext.dir3.plugin.service.url";
	public static final String PROP_DADESEXT_DIR3_PLUGIN_SERVICE_USERNAME = PROPERTY_PREFIX + "dadesext.dir3.plugin.service.username";
	public static final String PROP_DADESEXT_DIR3_PLUGIN_SERVICE_PASSWORD = PROPERTY_PREFIX + "dadesext.dir3.plugin.service.password";

	public static final String PROP_PROCEDIMENTS_PLUGIN_CLASS = PROPERTY_PREFIX + "procediments.plugin.class";

	public static final String PROP_GESDOC_PLUGIN_CLASS = PROPERTY_PREFIX + "gesdoc.plugin.class";
	public static final String PROP_GESDOC_PLUGIN_TIPUS_NOU = PROPERTY_PREFIX + "gesdoc.plugin.tipus.nou";
	public static final String PROP_GESDOC_PLUGIN_TIPUS_DIRECTE = PROPERTY_PREFIX + "gesdoc.plugin.tipus.directe";

	public static final String PROP_PLUGINS_PROCEDIMENTS_ROLSAC_SERVICE_URL = PROPERTY_PREFIX + "plugins.procediments.rolsac.service.url";
	public static final String PROP_PLUGINS_PROCEDIMENTS_ROLSAC_SERVICE_USERNAME = PROPERTY_PREFIX + "plugins.procediments.rolsac.service.username";
	public static final String PROP_PLUGINS_PROCEDIMENTS_ROLSAC_SERVICE_PASSWORD = PROPERTY_PREFIX + "plugins.procediments.rolsac.service.password";
	public static final String PROP_PLUGINS_PROCEDIMENTS_ROLSAC_SERVICE_TIMEOUT  = PROPERTY_PREFIX + "plugins.procediments.rolsac.service.timeout";

	public static final String PROP_ARXIU_PLUGIN_CLASS = PROPERTY_PREFIX + "arxiu.plugin.class";
	public static final String PROP_PLUGIN_ARXIU_CAIB_BASE_URL = PROPERTY_PREFIX + "plugin.arxiu.caib.base.url";

	public static final String PROP_PINBAL_PLUGIN_CLASS = PROPERTY_PREFIX + "pinbal.plugin.class";
	public static final String PROP_PINBAL_PLUGIN_URL = PROPERTY_PREFIX + "pinbal.plugin.url";
	public static final String PROP_PINBAL_PLUGIN_USERNAME = PROPERTY_PREFIX + "pinbal.plugin.username";
	public static final String PROP_PINBAL_PLUGIN_PASSWORD = PROPERTY_PREFIX + "pinbal.plugin.password";
	public static final String PROP_PINBAL_PLUGIN_ISJBOSS = PROPERTY_PREFIX + "pinbal.plugin.isJBoss";

	public static final String PROP_DISTRIBUCIO_BACKOFFICE_INTEGRACIO_WS_URL = PROPERTY_PREFIX + "helium.distribucio.backofficeIntegracio.ws.url";
	public static final String PROP_DISTRIBUCIO_BACKOFFICE_INTEGRACIO_WS_USERNAME = PROPERTY_PREFIX + "helium.distribucio.backofficeIntegracio.ws.username";
	public static final String PROP_DISTRIBUCIO_BACKOFFICE_INTEGRACIO_WS_PASSWORD = PROPERTY_PREFIX + "helium.distribucio.backofficeIntegracio.ws.password";

	public static final String PROP_VALIDATESIGNATURE_PLUGIN_CLASS = PROPERTY_PREFIX + "validatesignature.plugin.class";
	public static final String PROP_PLUGINS_VALIDATESIGNATURE_AFIRMACXF_ENDPOINT = PROPERTY_PREFIX + "plugins.validatesignature.afirmacxf.endpoint";

	public static final String PROP_UNITATS_ORGANIQUES_DIR3_PLUGIN_SERVICE_CLASS = PROPERTY_PREFIX + "unitats.organiques.dir3.plugin.service.class";
	public static final String PROP_UNITATS_ORGANIQUES_DIR3_PLUGIN_SERVICE_URL = PROPERTY_PREFIX + "unitats.organiques.dir3.plugin.service.url";
	public static final String PROP_UNITATS_ORGANIQUES_DIR3_PLUGIN_SERVICE_USERNAME = PROPERTY_PREFIX + "unitats.organiques.dir3.plugin.service.username";
	public static final String PROP_UNITATS_ORGANIQUES_DIR3_PLUGIN_SERVICE_PASSWORD = PROPERTY_PREFIX + "unitats.organiques.dir3.plugin.service.password";
	public static final String PROP_UNITATS_ORGANIQUES_DIR3_PLUGIN_SERVICE_CERCA_URL = PROPERTY_PREFIX + "unitats.organiques.dir3.plugin.service.cerca.url";

	public static final String PROP_PLUGIN_PASSARELAFIRMA_CLASS = PROPERTY_PREFIX + "plugin.passarelafirma.class";
	public static final String PROP_PLUGIN_PASSARELAFIRMA_1_SIGNATUREWEBAPIFIRMAWEBSIMPLE_ENDPOINT = PROPERTY_PREFIX + "plugin.passarelafirma.1.plugins.signatureweb.portafib.apifirmawebsimple.endpoint";
	public static final String PROP_PLUGIN_FIRMA_PORTAFIB_PLUGINS_SIGNATURESERVER_PASSARELA_URL = PROPERTY_PREFIX + "plugin.firma.portafib.plugins.signatureserver.portafib.api_passarela_url";

	public static final String PROP_TRAMITACIO_PLUGIN_CLASS = PROPERTY_PREFIX + "tramitacio.plugin.class";
	public static final String PROP_BANTEL_ENTRADES_URL = PROPERTY_PREFIX + "bantel.entrades.url";

	public static final String PROP_COMANDA_API_URL = PROPERTY_PREFIX + "comanda.api.url";
	public static final String PROP_COMANDA_API_USER = PROPERTY_PREFIX + "comanda.api.user";
	public static final String PROP_COMANDA_API_PASSWORD = PROPERTY_PREFIX + "comanda.api.password";
	public static final String PROP_COMANDA_LOGS_LOCATION = PROPERTY_PREFIX + "comanda.logs.location";

	public static final String PROP_CORREU_REMITENT = PROPERTY_PREFIX + "correu.remitent";
	public static final String PROP_CORREU_METRICS_RECIPIENTS = PROPERTY_PREFIX + "correu.metrics.recipients";

	public static final String PROP_GEOREF_ACTIU = PROPERTY_PREFIX + "georef.actiu";
	public static final String PROP_GEOREF_TIPUS = PROPERTY_PREFIX + "georef.tipus";

	public static final String PROP_FORMS_SERVICE_URL = PROPERTY_PREFIX + "forms.service.url";
	public static final String PROP_FORMS_SERVICE_USERNAME = PROPERTY_PREFIX + "forms.service.username";
	public static final String PROP_FORMS_SERVICE_PASSWORD = PROPERTY_PREFIX + "forms.service.password";

	public static final String PROP_DOMINI_TIMEOUT = PROPERTY_PREFIX + "domini.timeout";
	public static final String PROP_DOMINI_DESPLEGAMENT_TOMCAT = PROPERTY_PREFIX + "domini.desplegament.tomcat";

	public static final String PROP_BASE_PREFIX_PLUGIN_ARXIU = PROPERTY_PREFIX;
	public static final String PROP_BASE_PREFIX_PLUGIN_VALIDATESIGNATURE = PROPERTY_PREFIX;
	public static final String PROP_BASE_PREFIX_PLUGIN_PERSONES = PROPERTY_PREFIX + "plugin.persones.";
	public static final String PROP_BASE_PREFIX_SIGNATURA_PLUGIN = PROPERTY_PREFIX + "signatura.plugin.";
	public static final String PROP_BASE_PREFIX_PLUGIN_FIRMA_PORTAFIB = PROPERTY_PREFIX + "plugin.firma.portafib.";
	public static final String PROP_BASE_PREFIX_PLUGIN_PASSARELAFIRMA = PROPERTY_PREFIX + "plugin.passarelafirma.plugins.signatureweb.portafib.apifirmawebsimple.";

	public static final String PERSISTENCE_CONTAINER_TRANSACTIONS_DISABLED = PROPERTY_PREFIX + "persist.container-transactions-disabled";

}
