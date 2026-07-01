/**
 * 
 */
package es.caib.helium.integracio.plugins.persones;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.fundaciobit.pluginsib.userinformation.RolesInfo;
import org.fundaciobit.pluginsib.userinformation.SearchStatus;
import org.fundaciobit.pluginsib.userinformation.SearchUsersResult;
import org.fundaciobit.pluginsib.userinformation.UserInfo;
import org.fundaciobit.pluginsib.userinformation.ldap.LdapUserInformationPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.caib.helium.commons.config.PropertyConfig;
import es.caib.helium.commons.utils.GlobalProperties;

/**
 * Implementació del plugin de consulta de dades d'usuaris emprant el plugin de LDAP. Les propietats necessàries són les següents a partir
 * de es.caib.helium.plugin.persones.pluginsib.userinformation.ldap. :
 * 
 * - serverurl: Url del servidor de keycloak
 * - realm: Realm del keycloak.7
 * - client_id: Client ID del keycloak.
 * - client_id_for_user_autentication: Client ID per autenticació del keycloak.
 * - password_secret: Secret del client de keycloak.
 * - mapping.administrationID: Mapeig del administrationID de keycloak.
 * - debug: Activar el debug del plugin de keycloak.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PersonesPluginLdapCaib extends LdapUserInformationPlugin implements PersonesPlugin {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PersonesPluginLdapCaib.class);

	/** Constructor per cridar el super constructor amb les propietats i el prefix de les propietats.
	 */
	public PersonesPluginLdapCaib () {
		super(PropertyConfig.PROP_BASE_PREFIX_PLUGIN_PERSONES, GlobalProperties.getInstance().toProperties());
	}
	
	@Override
	public List<DadesPersona> findLikeNomSencer(String text) throws PersonesPluginException {
		LOGGER.debug("Consulta usuaris per nom LDAP CAIB (usuariCodi=" + text + ")");
		List<DadesPersona> dadesPersones = new ArrayList<>();
		try {
			SearchUsersResult result = this.getUsersByPartialNameOrPartialSurnames(text);
			if (SearchStatus.RESULT_OK != result.getStatus().getResultCode()) {
				throw new PersonesPluginException(
						"Resultat incorrecte consultant usuaris per nom LDAP CAIB (text=" + text + "): " + result.getStatus());
			}
			if (result.getUsers() != null) {
				for (UserInfo userInfo : result.getUsers()) {
					dadesPersones.add(toDadesPersona(userInfo));
				}
			}
		} catch (Exception ex) {
			throw new PersonesPluginException(
					"Error consultant usuaris per nom LDAP CAIB (text=" + text, ex);
		}
		return dadesPersones;
	}

	@Override
	public List<DadesPersona> findLikeCodiOrNomSencer(String text) throws PersonesPluginException {
		LOGGER.debug("Consulta usuaris per codi o nom LDAP CAIB (usuariCodi=" + text + ")");
		List<DadesPersona> dadesPersones = new ArrayList<>();
		DadesPersona dadaPersona = this.findAmbCodi(text);
		if (dadaPersona == null) {
			dadesPersones =  this.findLikeNomSencer(text);
		} else {
			dadesPersones.add(dadaPersona);
		}
		return dadesPersones;
	}
	
	@Override
	public DadesPersona findAmbCodi(
			String usuariCodi) throws PersonesPluginException {
		LOGGER.debug("Consulta de les dades de l'usuari LDAP CAIB (usuariCodi=" + usuariCodi + ")");
		try {
			UserInfo userInfo = getUserInfoByUserName(usuariCodi);
			return toDadesPersona(userInfo);
		} catch (Exception ex) {
			throw new PersonesPluginException(
					"Error al consultar l'usuari amb codi " + usuariCodi,
					ex);
		}
	}

	@Override
	public List<String> findRolsAmbCodi(String usuariCodi) throws PersonesPluginException {
		LOGGER.debug("Consulta dels rols per usuari LDAP CAIB (usuariCodi=" + usuariCodi + ")");
		try {
			RolesInfo info = getRolesByUsername(usuariCodi);
			return info != null && info.getRoles() != null ? eliminarDuplicados(List.of(info.getRoles())) : new ArrayList<String>();
		} catch (Exception e) {
			return new ArrayList<String>();
		}
	}

	@Override
	public List<DadesPersona> findAmbGrup(
			String grupCodi) throws PersonesPluginException {
		LOGGER.debug("Consulta dels usuaris del grup LDAP CAIB (grupCodi=" + grupCodi + ")");
		try {
			List<DadesPersona> dadesPersones = new ArrayList<>();
			UserInfo[] usersInfo = this.getUserInfoByRol(grupCodi);
			if (usersInfo != null) {
				for (int i = 0; i < usersInfo.length; i++) {
					dadesPersones.add(toDadesPersona(usersInfo[i]));
				}
			}
			return dadesPersones;
		} catch (Exception ex) {
			throw new PersonesPluginException(
					"Error al consultar els usuaris del grup LDAP CAIB " + grupCodi,
					ex);
		}
	}

	
	private List<String> eliminarDuplicados(List<String> lista) {
        return new ArrayList<>(new LinkedHashSet<>(lista)); // Set elimina duplicados y mantiene el orden
    }
	

	private DadesPersona toDadesPersona(UserInfo userInfo) {
		if (userInfo != null) {
			DadesPersona dadesPersona = new DadesPersona();
			dadesPersona.setCodi(userInfo.getUsername());
			dadesPersona.setNomSencer(userInfo.getFullName());
			dadesPersona.setNom(userInfo.getName());
			dadesPersona.setLlinatges(userInfo.getSurname1() + (userInfo.getSurname2() != null ? " " + userInfo.getSurname2() : ""));
			dadesPersona.setDni(userInfo.getAdministrationID());
			dadesPersona.setEmail(userInfo.getEmail());
			return dadesPersona;
		} else {
			return null;
		}
	}
}
