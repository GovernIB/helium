package es.caib.helium.commons.config;

/**
 * Configuració de les propietats de l'aplicació. Conté constants per les entrades de les diferents propietats.
 *
 * @author Limit Tecnologies
 */
public class PropertyConfig {

	private static final String PROPERTY_PREFIX = BaseConfig.BASE_PACKAGE + ".";
	public static final String PROPERTY_PLUGIN_PREFIX = PROPERTY_PREFIX + "plugin.";

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
	public static final String PROP_SECURITY_RESOURCEACCESS_API_INTERNA = PROPERTY_PREFIX + "security.resourceAcces.api-interna";
	/** Timeout opcional per establir un temps màxim en segons per a la creació d'un expedient. */
	public static final String PROP_EXPEDIENT_CREACIO_TIMOUT = PROPERTY_PREFIX + "expedient.creacio.timout";

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

	/** Número de reintents per provar la consulta d'una anotació pendent */
	public static final String PROP_ANOTACIONS_CONSULTA_NUM_INTENTS = PROPERTY_PREFIX + "anotacions.consulta.num.intents";

	public static final String PROP_PERSONES_PLUGIN_CLASS = PROPERTY_PLUGIN_PREFIX + "persones.class";
	public static final String PROP_PERSONES_PLUGIN_JDBC_FILTER_CODE = PROPERTY_PLUGIN_PREFIX + "persones.jdbc.filter.code";
	public static final String PROP_PERSONES_PLUGIN_JDBC_FILTER_NAME = PROPERTY_PLUGIN_PREFIX + "persones.jdbc.filter.name";
	public static final String PROP_PERSONES_PLUGIN_JDBC_FILTER_CODENAME = PROPERTY_PLUGIN_PREFIX + "persones.jdbc.filter.codename";
	public static final String PROP_PERSONES_PLUGIN_JDBC_FILTER_ROLES = PROPERTY_PLUGIN_PREFIX + "persones.jdbc.filter.roles";
	public static final String PROP_PERSONES_PLUGIN_JDBC_FILTER_GRUP = PROPERTY_PLUGIN_PREFIX + "persones.jdbc.filter.grup";
	public static final String PROP_PERSONES_PLUGIN_JDBC_JNDI_NAME = PROPERTY_PLUGIN_PREFIX + "persones.jdbc.jndi.parameter";
	public static final String PROP_PERSONES_PLUGIN_JDBC_NOM_LLINATGES_JUNT = PROPERTY_PLUGIN_PREFIX + "persones.jdbc.nom.llinatges.junt";
	public static final String PROP_PERSONES_PLUGIN_LDAP_URL = PROPERTY_PLUGIN_PREFIX + "persones.ldap.url";
	public static final String PROP_PERSONES_PLUGIN_LDAP_PRINCIPAL = PROPERTY_PLUGIN_PREFIX + "persones.ldap.principal";
	public static final String PROP_PERSONES_PLUGIN_LDAP_CREDENTIALS = PROPERTY_PLUGIN_PREFIX + "persones.ldap.credentials";
	public static final String PROP_PERSONES_PLUGIN_LDAP_SEARCHBASE = PROPERTY_PLUGIN_PREFIX + "persones.ldap.searchbase";
	public static final String PROP_PERSONES_PLUGIN_LDAP_SEARCH_BASE = PROPERTY_PLUGIN_PREFIX + "persones.ldap.search.base";
	public static final String PROP_PERSONES_PLUGIN_LDAP_ATTRIBUTES = PROPERTY_PLUGIN_PREFIX + "persones.ldap.attributes";
	public static final String PROP_PERSONES_PLUGIN_LDAP_ATTRIBUTE_ROLE = PROPERTY_PLUGIN_PREFIX + "persones.ldap.attribute.role";
	public static final String PROP_PERSONES_PLUGIN_LDAP_ATTRIBUTE_ROLE_NAME = PROPERTY_PLUGIN_PREFIX + "persones.ldap.attribute.role.name";
	public static final String PROP_PERSONES_PLUGIN_LDAP_EMAIL_DOMINI = PROPERTY_PLUGIN_PREFIX + "persones.ldap.email.domini";
	public static final String PROP_PERSONES_PLUGIN_LDAP_FILTER_USER = PROPERTY_PLUGIN_PREFIX + "persones.ldap.filter.user";
	public static final String PROP_PERSONES_PLUGIN_LDAP_SEARCH_FILTER_USER = PROPERTY_PLUGIN_PREFIX + "persones.ldap.search.filter.user";
	public static final String PROP_PERSONES_PLUGIN_LDAP_SEARCH_FILTER_GRUP = PROPERTY_PLUGIN_PREFIX + "persones.ldap.search.filter.grup";
	public static final String PROP_PERSONES_PLUGIN_LDAP_SEARCH_FILTER_LIKE = PROPERTY_PLUGIN_PREFIX + "persones.ldap.search.filter.like";
	public static final String PROP_PERSONES_PLUGIN_LDAP_SEARCH_FILTER_USERLIKE = PROPERTY_PLUGIN_PREFIX + "persones.ldap.search.filter.userlike";

	public static final String PROP_PERSONES_PLUGIN_KEYCLOAK_CLIENT_ID = PROPERTY_PLUGIN_PREFIX + "persones.keycloak.client_id";
	public static final String PROP_PERSONES_PLUGIN_KEYCLOAK_CLIENT_ID_FOR_USER_AUTENTICATION = PROPERTY_PLUGIN_PREFIX + "persones.keycloak.client_id_for_user_autentication";
	public static final String PROP_PERSONES_PLUGIN_KEYCLOAK_REALM = PROPERTY_PLUGIN_PREFIX + "persones.keycloak.realm";

	public static final String PROP_PERSONES_PLUGIN_SYNC_ACTIU = PROPERTY_PLUGIN_PREFIX + "persones.sync.actiu";

	public static final String PROP_REGISTRE_PLUGIN_CLASS = PROPERTY_PLUGIN_PREFIX + "registre.class";
	public static final String PROP_REGISTRE_PLUGIN_RW3_CLASS = PROPERTY_PLUGIN_PREFIX + "registre.rw3.class";
	public static final String PROP_REGISTRE_PLUGIN_WS_URL = PROPERTY_PLUGIN_PREFIX + "registre.ws.url";
	public static final String PROP_REGISTRE_PLUGIN_WS_HOST = PROPERTY_PLUGIN_PREFIX + "registre.ws.host";
	public static final String PROP_REGISTRE_PLUGIN_WS_USUARI = PROPERTY_PLUGIN_PREFIX + "registre.ws.usuari";
	public static final String PROP_REGISTRE_PLUGIN_WS_PASSWORD = PROPERTY_PLUGIN_PREFIX + "registre.ws.password";

	public static final String PROP_NOTIFICACIO_PLUGIN_CLASS = PROPERTY_PLUGIN_PREFIX + "notificacio.class";
	public static final String PROP_NOTIFICACIO_PLUGIN_URL = PROPERTY_PLUGIN_PREFIX + "notificacio.url";
	public static final String PROP_NOTIFICACIO_PLUGIN_USERNAME = PROPERTY_PLUGIN_PREFIX + "notificacio.username";
	public static final String PROP_NOTIFICACIO_PLUGIN_PASSWORD = PROPERTY_PLUGIN_PREFIX + "notificacio.password";

	public static final String PROP_PORTASIGNATURES_PLUGIN_CLASS = PROPERTY_PLUGIN_PREFIX + "portasignatures.class";
	public static final String PROP_PORTASIGNATURES_PLUGIN_PORTAFIB_URL = PROPERTY_PLUGIN_PREFIX + "portasignatures.portafib.url";
	public static final String PROP_PORTASIGNATURES_PLUGIN_PORTAFIB_USERNAME = PROPERTY_PLUGIN_PREFIX + "portasignatures.portafib.username";
	public static final String PROP_PORTASIGNATURES_PLUGIN_PORTAFIB_PASSWORD = PROPERTY_PLUGIN_PREFIX + "portasignatures.portafib.password";
	public static final String PROP_PORTASIGNATURES_PLUGIN_PORTAFIB_PERFIL = PROPERTY_PLUGIN_PREFIX + "portasignatures.portafib.perfil";
	public static final String PROP_PORTASIGNATURES_PLUGIN_PORTAFIB_FIRMASIMPLEASYNC_URL = PROPERTY_PLUGIN_PREFIX + "portasignatures.portafib.firmasimpleasync.url";
	public static final String PROP_PORTASIGNATURES_PLUGIN_USUARI_ID = PROPERTY_PLUGIN_PREFIX + "portasignatures.usuari.id";

	public static final String PROP_PORTAFIRMES_PLUGIN_FLUX_FIRMA_URL = PROPERTY_PLUGIN_PREFIX + "portafirmes.flux.firma.url";
	public static final String PROP_PORTAFIRMES_PLUGIN_FLUX_FIRMA_USUARI = PROPERTY_PLUGIN_PREFIX + "portafirmes.flux.firma.username";
	public static final String PROP_PORTAFIRMES_PLUGIN_FLUX_FIRMA_PASSWORD = PROPERTY_PLUGIN_PREFIX + "portafirmes.flux.firma.password";
	public static final String PROP_PORTAFIRMES_PLUGIN_FLUX_ENTITATWS_URL = PROPERTY_PLUGIN_PREFIX + "portafirmes.flux.entitatws.url";
	public static final String PROP_PORTAFIRMES_PLUGIN_FLUX_ENTITATWS_USERNAME = PROPERTY_PLUGIN_PREFIX + "portafirmes.flux.entitatws.username";
	public static final String PROP_PORTAFIRMES_PLUGIN_FLUX_ENTITATWS_PASSWORD = PROPERTY_PLUGIN_PREFIX + "portafirmes.flux.entitatws.password";
	public static final String PROP_PORTAFIRMES_PLUGIN_FLUX_ENTITATWS_MOSTRAR_PERSONA_CARREC = PROPERTY_PLUGIN_PREFIX + "portafirmes.flux.entitatws.password";

	public static final String PROP_SIGNATURA_PLUGIN_CLASS = PROPERTY_PLUGIN_PREFIX + "signatura.class";
	public static final String PROP_SIGNATURA_PLUGIN_FILE_ATTACHED = PROPERTY_PLUGIN_PREFIX + "signatura.file.attached";

	public static final String PROP_FIRMA_PLUGIN_CLASS = PROPERTY_PLUGIN_PREFIX + "firma.class";

	public static final String PROP_GESDOC_PLUGIN_CLASS = PROPERTY_PLUGIN_PREFIX + "gesdoc.class";
	public static final String PROP_GESDOC_PLUGIN_TIPUS_NOU = PROPERTY_PLUGIN_PREFIX + "gesdoc.tipus.nou";
	public static final String PROP_GESDOC_PLUGIN_TIPUS_DIRECTE = PROPERTY_PLUGIN_PREFIX + "gesdoc.tipus.directe";

	public static final String PROP_PROCEDIMENTS_PLUGIN_CLASS = PROPERTY_PLUGIN_PREFIX + "procediments.class";
	public static final String PROP_PLUGINS_PROCEDIMENTS_ROLSAC_SERVICE_URL = PROPERTY_PLUGIN_PREFIX + "procediments.rolsac.service.url";
	public static final String PROP_PLUGINS_PROCEDIMENTS_ROLSAC_SERVICE_USERNAME = PROPERTY_PLUGIN_PREFIX + "procediments.rolsac.service.username";
	public static final String PROP_PLUGINS_PROCEDIMENTS_ROLSAC_SERVICE_PASSWORD = PROPERTY_PLUGIN_PREFIX + "procediments.rolsac.service.password";
	public static final String PROP_PLUGINS_PROCEDIMENTS_ROLSAC_SERVICE_TIMEOUT  = PROPERTY_PLUGIN_PREFIX + "procediments.rolsac.service.timeout";

	public static final String PROP_ARXIU_PLUGIN_CLASS = PROPERTY_PLUGIN_PREFIX + "arxiu.class";
	public static final String PROP_PLUGIN_ARXIU_CAIB_BASE_URL = PROPERTY_PLUGIN_PREFIX + "arxiu.caib.base.url";

	public static final String PROP_PINBAL_PLUGIN_CLASS = PROPERTY_PLUGIN_PREFIX + "pinbal.class";
	public static final String PROP_PINBAL_PLUGIN_URL = PROPERTY_PLUGIN_PREFIX + "pinbal.url";
	public static final String PROP_PINBAL_PLUGIN_USERNAME = PROPERTY_PLUGIN_PREFIX + "pinbal.username";
	public static final String PROP_PINBAL_PLUGIN_PASSWORD = PROPERTY_PLUGIN_PREFIX + "pinbal.password";
	public static final String PROP_PINBAL_PLUGIN_ISJBOSS = PROPERTY_PLUGIN_PREFIX + "pinbal.isJBoss";

	public static final String PROP_DISTRIBUCIO_BACKOFFICE_INTEGRACIO_WS_URL = PROPERTY_PREFIX + "helium.distribucio.backofficeIntegracio.ws.url";
	public static final String PROP_DISTRIBUCIO_BACKOFFICE_INTEGRACIO_WS_USERNAME = PROPERTY_PREFIX + "helium.distribucio.backofficeIntegracio.ws.username";
	public static final String PROP_DISTRIBUCIO_BACKOFFICE_INTEGRACIO_WS_PASSWORD = PROPERTY_PREFIX + "helium.distribucio.backofficeIntegracio.ws.password";

	public static final String PROP_VALIDATESIGNATURE_PLUGIN_CLASS = PROPERTY_PLUGIN_PREFIX + "validatesignature.class";
	public static final String PROP_PLUGINS_VALIDATESIGNATURE_AFIRMACXF_ENDPOINT = PROPERTY_PLUGIN_PREFIX + "validatesignature.afirmacxf.endpoint";

	public static final String PROP_DADESEXT_DIR3_PLUGIN_SERVICE_CLASS = PROPERTY_PLUGIN_PREFIX + "dadesext.dir3.service.class";
	public static final String PROP_DADESEXT_DIR3_PLUGIN_SERVICE_URL = PROPERTY_PLUGIN_PREFIX + "dadesext.dir3.service.url";
	public static final String PROP_DADESEXT_DIR3_PLUGIN_SERVICE_USERNAME = PROPERTY_PLUGIN_PREFIX + "dadesext.dir3.service.username";
	public static final String PROP_DADESEXT_DIR3_PLUGIN_SERVICE_PASSWORD = PROPERTY_PLUGIN_PREFIX + "dadesext.dir3.service.password";

	public static final String PROP_UNITATS_ORGANIQUES_DIR3_PLUGIN_SERVICE_CLASS = PROPERTY_PLUGIN_PREFIX + "unitats.organiques.dir3.service.class";
	public static final String PROP_UNITATS_ORGANIQUES_DIR3_PLUGIN_SERVICE_URL = PROPERTY_PLUGIN_PREFIX + "unitats.organiques.dir3.service.url";
	public static final String PROP_UNITATS_ORGANIQUES_DIR3_PLUGIN_SERVICE_USERNAME = PROPERTY_PLUGIN_PREFIX + "unitats.organiques.dir3.service.username";
	public static final String PROP_UNITATS_ORGANIQUES_DIR3_PLUGIN_SERVICE_PASSWORD = PROPERTY_PLUGIN_PREFIX + "unitats.organiques.dir3.service.password";
	public static final String PROP_UNITATS_ORGANIQUES_DIR3_PLUGIN_SERVICE_CERCA_URL = PROPERTY_PLUGIN_PREFIX + "unitats.organiques.dir3.service.cerca.url";

	public static final String PROP_PLUGIN_PASSARELAFIRMA_CLASS = PROPERTY_PLUGIN_PREFIX + "passarelafirma.class";
	public static final String PROP_PLUGIN_PASSARELAFIRMA_1_SIGNATUREWEBAPIFIRMAWEBSIMPLE_ENDPOINT = PROPERTY_PLUGIN_PREFIX + "passarelafirma.1.signatureweb.portafib.apifirmawebsimple.endpoint";
	public static final String PROP_PLUGIN_FIRMA_PORTAFIB_PLUGINS_SIGNATURESERVER_PASSARELA_URL = PROPERTY_PLUGIN_PREFIX + "firma.portafib.signatureserver.portafib.api_passarela_url";

	public static final String PROP_TRAMITACIO_PLUGIN_CLASS = PROPERTY_PLUGIN_PREFIX + "tramitacio.class";
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

	public static final String PROP_BASE_PREFIX_PLUGIN_ARXIU = PROPERTY_PLUGIN_PREFIX + "arxiu.";
	public static final String PROP_BASE_PREFIX_PLUGIN_VALIDATESIGNATURE = PROPERTY_PLUGIN_PREFIX;
	public static final String PROP_BASE_PREFIX_PLUGIN_PERSONES = PROPERTY_PLUGIN_PREFIX + "persones.";
	public static final String PROP_BASE_PREFIX_SIGNATURA_PLUGIN = PROPERTY_PLUGIN_PREFIX + "signatura.";
	public static final String PROP_BASE_PREFIX_PLUGIN_FIRMA_PORTAFIB = PROPERTY_PLUGIN_PREFIX + "firma.portafib.";
	public static final String PROP_BASE_PREFIX_PLUGIN_PASSARELAFIRMA = PROPERTY_PLUGIN_PREFIX + "passarelafirma.signatureweb.portafib.apifirmawebsimple.";

	public static final String PERSISTENCE_CONTAINER_TRANSACTIONS_DISABLED = PROPERTY_PREFIX + "persist.container-transactions-disabled";

}
