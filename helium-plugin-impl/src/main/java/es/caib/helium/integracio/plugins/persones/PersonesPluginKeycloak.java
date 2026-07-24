package es.caib.helium.integracio.plugins.persones;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import es.caib.helium.commons.config.PropertyConfig;
import org.fundaciobit.pluginsib.userinformation.RolesInfo;
import org.fundaciobit.pluginsib.userinformation.SearchUsersResult;
import org.fundaciobit.pluginsib.userinformation.UserInfo;
import org.fundaciobit.pluginsib.userinformation.keycloak.KeyCloakUserInformationPlugin;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.ClientsResource;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import es.caib.helium.commons.utils.GlobalProperties;

public class PersonesPluginKeycloak extends KeyCloakUserInformationPlugin implements PersonesPlugin {

	public PersonesPluginKeycloak() {
		super(
			PropertyConfig.PROP_BASE_PREFIX_PLUGIN_PERSONES,
			GlobalProperties.getInstance().toPropertiesWithPrefix(PropertyConfig.PROP_BASE_PREFIX_PLUGIN_PERSONES));
	}

	@Override
	public List<DadesPersona> findLikeNomSencer(String text) throws PersonesPluginException {
		SearchUsersResult result;
		try {
			result = getUsersByPartialNameOrPartialSurnames(text);
			if(result.getUsers() == null)
				return new ArrayList<DadesPersona>();

			return result
				.getUsers()
				.stream()
				.map(ui -> toDadesPersona(ui))
				.collect(Collectors.toList());
		} catch (Exception e) {
			throw new PersonesPluginException("Error cercant dades persona per nom d'usuari [" + text + "]", e);
		}
	}

	@Override
	public List<DadesPersona> findLikeCodiOrNomSencer(String text) throws PersonesPluginException {
		return findLikeNomSencer(text);
	}

	@Override
	public DadesPersona findAmbCodi(String codi) throws PersonesPluginException {
		try {
			UserInfo userInfo = getUserInfoByUserName(codi);
			return toDadesPersona(userInfo);
		} catch (Exception e) {
			throw new PersonesPluginException("Error cercant dades persona per codi: " + codi, e);
		} catch(Throwable e) {
			throw new PersonesPluginException("Error cercant dades persona per codi: " + codi, e);
		}
	}

	@Override
	public List<String> findRolsAmbCodi(String codi) throws PersonesPluginException {
		try {
			RolesInfo rolesInfo = getRolesByUsername(codi);
			return Arrays.asList(rolesInfo.getRoles());
		} catch (Exception e) {
			throw new PersonesPluginException("Error cercant rols de persona amb codi: " + codi, e);
		}
	}

	@Override
	public List<DadesPersona> findAmbGrup(String grupCodi) throws PersonesPluginException {
		try {
			Collection<UserRepresentation> userRepresentacions = internalGetUserNamesByRol(grupCodi);
			return userRepresentacions
					.stream()
					.map(ur -> toDadesPersona(ur))
					.collect(Collectors.toList());
		} catch (Exception e) {
			throw new PersonesPluginException("Error cercant dades persona amb grup: " + grupCodi, e);
		}
	}

	private Collection<UserRepresentation> internalGetUserNamesByRol(String rol) {
		Set<UserRepresentation> usernamesClientApp = null;
		Set<UserRepresentation> usernamesClientPersons = null;
		Set<UserRepresentation> usersRealm = null;
		try {
			String appClient = this.getPropertyRequired(PropertyConfig.PROP_PERSONES_PLUGIN_KEYCLOAK_CLIENT_ID);
			usernamesClientApp = this.getUsernamesByRolOfClient(rol, appClient);
		} catch (Exception ex) {
			log.warn("No s'han obtingut usuaris per client d'aplicació: " + ex.toString(), (this.isDebug() ? ex : null));
		}
		try {
			String personsClient = this.getPropertyRequired(PropertyConfig.PROP_PERSONES_PLUGIN_KEYCLOAK_CLIENT_ID_FOR_USER_AUTENTICATION);
			usernamesClientPersons = this.getUsernamesByRolOfClient(rol, personsClient);
		} catch (Exception ex) {
			log.warn("No s'han obtingut usuaris per client de persones: " + ex.toString(), (this.isDebug() ? ex : null));
		}
		try {
			usersRealm = this.getUsernamesByRolOfRealm(rol);
		} catch (Exception ex) {
			log.warn("No s'han obtingut usuaris per realm: " + ex.toString(), (this.isDebug() ? ex : null));
		}
		if (usernamesClientApp == null && usernamesClientPersons == null && usersRealm == null) {
			return null;
		}
		Map<String, UserRepresentation> users = new HashMap<String, UserRepresentation>();
		if (usernamesClientApp != null) {
			usernamesClientApp.stream().forEach(u -> {
				users.put(u.getUsername(), u);
			});
		}
		if (usernamesClientPersons != null) {
			usernamesClientPersons.stream().forEach(u -> {
				users.put(u.getUsername(), u);
			});
		}
		if (usersRealm != null) {
			usersRealm.stream().forEach(u -> {
				users.put(u.getUsername(), u);
			});
		}
		return users.values();
	}

	private Set<UserRepresentation> getUsernamesByRolOfRealm(String rol) throws Exception {
		RolesResource roleres = this.getKeyCloakConnectionForRoles();
		return roleres.get(rol).getRoleUserMembers();
	}

	private Set<UserRepresentation> getUsernamesByRolOfClient(String rol, String client) throws Exception {
		Keycloak keycloak = this.getKeyCloakConnection();
		ClientsResource clientsApi = keycloak.realm(this.getPropertyRequired(PropertyConfig.PROP_PERSONES_PLUGIN_KEYCLOAK_REALM)).clients();
		List<ClientRepresentation> crList = clientsApi.findByClientId(client);
		if (crList == null || crList.isEmpty()) {
			return null;
		}
		ClientResource c = clientsApi.get((crList.get(0)).getId());
		RolesResource rrs = c.roles();
		RoleResource rr = rrs.get(rol);
		return rr.getRoleUserMembers();
	}

	private DadesPersona toDadesPersona(UserInfo ui) {
		DadesPersona.Sexe sexe;
		switch(ui.getGender()) {
		case FEMALE:
			sexe = DadesPersona.Sexe.SEXE_DONA;
			break;
		case MALE:
		case OTHER:
		case UNKNOWN:
		default:
			sexe = DadesPersona.Sexe.SEXE_HOME;
		}

		return DadesPersona
			.builder()
			.codi(ui.getUsername())
			.nom(ui.getName())
			.llinatge1(ui.getSurname1())
			.llinatge2(ui.getSurname2())
			.sexe(sexe)
			.dni(ui.getAdministrationID())
			.build();
	}

	private DadesPersona toDadesPersona(UserRepresentation ur) {
		return DadesPersona
				.builder()
				.codi(ur.getUsername())
				.nom(ur.getFirstName())
				.llinatge1(ur.getLastName())
				.build();
	}

}
