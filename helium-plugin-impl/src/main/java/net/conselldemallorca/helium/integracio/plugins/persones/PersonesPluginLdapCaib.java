package net.conselldemallorca.helium.integracio.plugins.persones;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;

import org.fundaciobit.plugins.userinformation.ldap.LdapUserInformationPlugin;
import org.fundaciobit.pluginsib.userinformation.RolesInfo;
import org.fundaciobit.pluginsib.userinformation.UserInfo;
import org.fundaciobit.pluginsib.userinformation.UserInfo.Gender;
import org.fundaciobit.pluginsib.utils.ldap.LDAPUser;
import org.fundaciobit.pluginsib.utils.ldap.LDAPUserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.integracio.plugins.persones.DadesPersona.Sexe;

public class PersonesPluginLdapCaib extends LdapUserInformationPlugin implements PersonesPlugin {
	private static final Logger LOGGER = LoggerFactory.getLogger(PersonesPluginLdapCaib.class);

	/**
	 * Constructor per cridar el super constructor amb les propietats i el prefix de
	 * les propietats.
	 */
	public PersonesPluginLdapCaib() {
		super("es.caib.helium.plugin.persones.", GlobalProperties.getProperties());
	}

	@Override
	public List<DadesPersona> findLikeNomSencer(String text) throws PersonesPluginException {
		LOGGER.debug("Consulta usuaris per nom LDAP CAIB (usuariCodi=" + text + ")");
		List<DadesPersona> dadesPersones = new ArrayList<DadesPersona>();
		try {
			SearchUsersResult result = this.getUsersByPartialNameOrPartialSurnames(text);
			if (SearchStatus.RESULT_OK != result.getStatus().getResultCode()) {
				throw new PersonesPluginException("Resultat incorrecte consultant usuaris per nom LDAP CAIB (text="
						+ text + "): " + result.getStatus());
			}
			if (result.getUsers() != null) {
				for (UserInfo userInfo : result.getUsers()) {
					dadesPersones.add(toDadesPersona(userInfo));
				}
			}
		} catch (Exception ex) {
			throw new PersonesPluginException("Error consultant usuaris per nom LDAP CAIB (text=" + text, ex);
		}
		return dadesPersones;
	}

	@Override
	public List<DadesPersona> findLikeCodiOrNomSencer(String text) throws PersonesPluginException {
		LOGGER.debug("Consulta usuaris per codi o nom LDAP CAIB (usuariCodi=" + text + ")");
		List<DadesPersona> dadesPersones = new ArrayList<DadesPersona>();
		DadesPersona dadaPersona = this.findAmbCodi(text);
		if (dadaPersona == null) {
			dadesPersones = this.findLikeNomSencer(text);
		} else {
			dadesPersones.add(dadaPersona);
		}
		return dadesPersones;
	}

	@Override
	public DadesPersona findAmbCodi(String usuariCodi) throws PersonesPluginException {
		LOGGER.debug("Consulta de les dades de l'usuari LDAP CAIB (usuariCodi=" + usuariCodi + ")");
		try {
			UserInfo userInfo = getUserInfoByUserName(usuariCodi);
			return toDadesPersona(userInfo);
		} catch (Exception ex) {
			throw new PersonesPluginException("Error al consultar l'usuari amb codi " + usuariCodi, ex);
		}
	}

	@Override
	public List<String> findRolsAmbCodi(String usuariCodi) throws PersonesPluginException {
		LOGGER.debug("Consulta dels rols per usuari LDAP CAIB (usuariCodi=" + usuariCodi + ")");
		try {
			RolesInfo info = getRolesByUsername(usuariCodi);
			return info != null && info.getRoles() != null ? eliminarDuplicados(Arrays.asList(info.getRoles()))
					: new ArrayList<String>();
		} catch (Exception e) {
			return new ArrayList<String>();
		}
	}

	@Override
	public List<DadesPersona> findAmbGrup(String grupCodi) throws PersonesPluginException {
		LOGGER.debug("Consulta dels usuaris del grup LDAP CAIB (grupCodi=" + grupCodi + ")");
		try {
			List<DadesPersona> dadesPersones = new ArrayList<DadesPersona>();
			UserInfo[] usersInfo = this.getUserInfoByRol(grupCodi);
			if (usersInfo != null) {
				for (int i = 0; i < usersInfo.length; i++) {
					dadesPersones.add(toDadesPersona(usersInfo[i]));
				}
			}
			return dadesPersones;
		} catch (Exception ex) {
			throw new PersonesPluginException("Error al consultar els usuaris del grup LDAP CAIB " + grupCodi, ex);
		}
	}
	
	@Override
	public List<DadesPersona> findAll() throws PersonesPluginException {
		LDAPUserManager ldapManager = getLDAPUserManager();
		try {
			List<DadesPersona> dadesPersones = new ArrayList<DadesPersona>();
			List<String> allUsernames = ldapManager.getAllUserNames();
			for(String usuariCodi : allUsernames) {
				dadesPersones.add(toDadesPersona(getUserInfoByUserName(usuariCodi)));
			}
			return dadesPersones;
		} catch (Exception e) {
			throw new PersonesPluginException(e.getMessage(), e);
		}
	}

	private UserInfo[] getUserInfoByRol(String rol) throws Exception {
		LDAPUserManager ldapManager = getLDAPUserManager();
		List<String> allUsernames = ldapManager.getAllUserNames();

		List<UserInfo> usernames = new ArrayList<UserInfo>();
		for (String un : allUsernames) {
			List<String> roles = ldapManager.getRolesOfUser(un);
			if (roles.contains(rol)) {
				LDAPUser u = ldapManager.getUserByUsername(un);
				usernames.add(ldapUserToUserInfo(u));
			}
		}
		return usernames.toArray(new UserInfo[] {});
	}

	private List<String> eliminarDuplicados(List<String> lista) {
		return new ArrayList<String>(new LinkedHashSet<String>(lista)); // Set elimina duplicados y mantiene el orden
	}

	private SearchUsersResult getUsersByPartialNameOrPartialSurnames(String partialNameOrSurname)
			throws NamingException, IOException, Exception {
		final String partialValue = partialNameOrSurname;

		LDAPUserManager ldapManager = getLDAPUserManager();

		String surname1Key = ldapManager.getLdapProperty(LDAPUserManager.LDAP_SURNAME1_ATTRIBUTE);
		String surname2Key = ldapManager.getLdapProperty(LDAPUserManager.LDAP_SURNAME2_ATTRIBUTE);

		final String[] attributeKeys;
		if (surname1Key == null && surname2Key == null) {

			attributeKeys = new String[] { LDAPUserManager.LDAP_NAME_ATTRIBUTE,
					LDAPUserManager.LDAP_SURNAMES_ATTRIBUTE };
		} else {
			attributeKeys = new String[] { LDAPUserManager.LDAP_NAME_ATTRIBUTE, LDAPUserManager.LDAP_SURNAME1_ATTRIBUTE,
					LDAPUserManager.LDAP_SURNAME2_ATTRIBUTE };
		}
		final boolean operationOR = true;
		return internalPartialSearch(partialValue, attributeKeys, operationOR);
	}

	private SearchUsersResult internalPartialSearch(String partialValue, String[] attributeKeys, boolean operationOr)
			throws NamingException, IOException, Exception {
		Map<String, String> partialValuesByAttributeKey = new HashMap<String, String>();
		for (String attributeKey : attributeKeys) {
			partialValuesByAttributeKey.put(attributeKey, partialValue);
		}
		return getUsersByPartialMultipleValues(partialValuesByAttributeKey, operationOr);
	}

	/**
	 * 
	 * @param partialValuesByAttributeKey
	 *            Key => Key Values of LDAPConstants. Values => Partial pattern to
	 *            search
	 * @param operationOr
	 * @return
	 * @throws Exception
	 */
	private SearchUsersResult getUsersByPartialMultipleValues(Map<String, String> partialValuesByAttributeKey,
			boolean operationOr) throws Exception {

		LDAPUserManager ldapManager = getLDAPUserManager();

		if (partialValuesByAttributeKey == null || partialValuesByAttributeKey.size() == 0) {
			return new SearchUsersResult(new SearchStatus(SearchStatus.RESULT_PARTIAL_STRING_NULL_OR_EMPTY));
		}

		StringBuilder filterB = new StringBuilder();
		for (Entry<String, String> entry : partialValuesByAttributeKey.entrySet()) {

			String attributeKey = entry.getKey();
			String partialValue = entry.getValue();
			String ldapAttrib = ldapManager.getLdapProperties().getProperty(attributeKey);
			if (ldapAttrib == null || ldapAttrib.trim().length() == 0) {
				return new SearchUsersResult(new SearchStatus(SearchStatus.RESULT_CLIENT_ERROR,
						"L´atribut " + attributeKey + " no està definit en els propietats"));
			}
			filterB.append("(").append(ldapAttrib).append("=").append(partialValue).append(")");
		}
		try {
			String filter;
			if (partialValuesByAttributeKey.size() == 1) {
				filter = filterB.toString();
			} else {
				if (operationOr) {
					filter = "(|" + filterB.toString() + ")";
				} else {
					filter = "(&" + filterB.toString() + ")";
				}
			}
			NamingEnumeration<SearchResult> enumeration = ldapManager.searchLDAP(filter);

			return ldapNamingEnumeration2UserInfoList(ldapManager, enumeration);
		} catch (javax.naming.SizeLimitExceededException e) {
			return new SearchUsersResult(new SearchStatus(SearchStatus.RESULT_TOO_MANY_RESULTS_MATCH, e.getMessage()));
		}
	}

	private SearchUsersResult ldapNamingEnumeration2UserInfoList(LDAPUserManager ldapManager,
			NamingEnumeration<SearchResult> enumeration) throws NamingException, IOException, Exception {

		List<UserInfo> list = new ArrayList<UserInfo>();
		while (enumeration.hasMore()) {
			SearchResult sr = enumeration.next();

			// ldapManager.

			list.add(ldapUserToUserInfo(convertAttributesToLdapUser(sr.getAttributes())));
		}
		return new SearchUsersResult(list);
	}

	private UserInfo ldapUserToUserInfo(LDAPUser ldapUser) throws IOException {
		if (ldapUser == null) {
			return null;
		}

		UserInfo info = new UserInfo();

		info.setName(ldapUser.getName());
		if (ldapUser.getSurname1() == null) {
			info.setSurname1(ldapUser.getSurnames());
		} else {
			info.setSurname1(ldapUser.getSurname1());
		}
		info.setSurname2(ldapUser.getSurname2());

		info.setAdministrationID(ldapUser.getAdministrationID());
		info.setUsername(ldapUser.getUserName());
		info.setEmail(ldapUser.getEmail());

		info.setPhoneNumber(ldapUser.getTelephoneNumber());

		// https://github.com/GovernIB/pluginsib-userinformation/issues/16

		// (1) Default language
		{
			String defLang = getProperty(LDAP_BASE_PROPERTIES + "defaultlanguage");
			if (defLang == null || defLang.trim().length() == 0) {
				defLang = "ca";
			}
			info.setLanguage(defLang);
		}

		// (2) Mail Expression Language
		{
			String mailEL = getProperty(LDAP_BASE_PROPERTIES + "mailEL");
			if (mailEL != null && mailEL.trim().length() != 0) {
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("userInfo", info);
				info.setEmail(mailEL);
			}
		}
		return info;
	}

	private DadesPersona toDadesPersona(UserInfo userInfo) {
		if (userInfo != null) {
			DadesPersona dadesPersona = new DadesPersona(userInfo.getUsername(), userInfo.getName(),
					userInfo.getSurname1() + (userInfo.getSurname2() != null ? " " + userInfo.getSurname2() : ""),
					userInfo.getEmail(), mapSexe(userInfo.getGender()));
			dadesPersona.setDni(userInfo.getAdministrationID());
			dadesPersona.setEmail(userInfo.getEmail());
			return dadesPersona;
		} else {
			return null;
		}
	}

	/**
	 * Convert SearchResult into User instance.
	 * 
	 * @param ldapUser
	 *            List of attributes of user i LDAP server.
	 * @return Instance of User.
	 * @throws Exception
	 *             If error.
	 */
	public LDAPUser convertAttributesToLdapUser(Attributes attrib) throws Exception {

		// Attributes attrib = ldapUser.getAttributes();
		// SearchResult ldapUser

		// TODO Imprimir tots els attributs

		String userName = searchResultToUserName(attrib); // ldapUser);

		LDAPUser user = new LDAPUser();
		user.setUserName(userName);

		String givenNameKey = GlobalProperties.getProperties().getProperty(LDAPUserManager.LDAP_NAME_ATTRIBUTE);
		Attribute givenName = attrib.get(givenNameKey);
		if (givenName == null) {
			user.setName(null);
		} else {
			user.setName((String) givenName.get());
		}

		String surname1Key = GlobalProperties.getProperties().getProperty(LDAPUserManager.LDAP_SURNAME1_ATTRIBUTE);
		String surname2Key = GlobalProperties.getProperties().getProperty(LDAPUserManager.LDAP_SURNAME2_ATTRIBUTE);

		if (surname1Key == null || surname2Key == null) {
			// Els llinatges es troben només a dins una sola clau
			String surnameKey = GlobalProperties.getProperties().getProperty(LDAPUserManager.LDAP_SURNAMES_ATTRIBUTE);

			Attribute surname = attrib.get(surnameKey);
			if (surname == null) {
				user.setSurnames(null);
			} else {
				user.setSurnames((String) surname.get());
			}
		} else {
			// Està definit el llinatge emprant dos camps: llinatge1 i llinatge2
			Attribute surname1 = attrib.get(surname1Key);
			if (surname1 == null) {
				user.setSurname1(null);
			} else {
				user.setSurname1((String) surname1.get());
			}

			Attribute surname2 = attrib.get(surname2Key);
			if (surname2 == null) {
				user.setSurname2(null);
			} else {
				user.setSurname2((String) surname2.get());
			}
		}

		String memberOfKey = GlobalProperties.getProperties().getProperty(LDAPUserManager.LDAP_MEMBEROF_ATTRIBUTE);
		Attribute memberOfAtt = attrib.get(memberOfKey);
		if (memberOfAtt == null) {
			user.setMemberOf(null);
		} else {
			String prefix = GlobalProperties.getProperties().getProperty(LDAPUserManager.PREFIX_ROLE_MATCH_MEMBEROF);
			if (prefix == null) {
				prefix = "";
			}
			String suffix = GlobalProperties.getProperties().getProperty(LDAPUserManager.SUFFIX_ROLE_MATCH_MEMBEROF);
			if (suffix == null) {
				suffix = "";
			}
			int num = memberOfAtt.size();
			List<String> values = new ArrayList<String>(num);
			List<String> roles = new ArrayList<String>(num);
			for (int i = 0; i < num; i++) {
				String memberOfOk = (String) memberOfAtt.get(i);
				values.add(memberOfOk);
				if (memberOfOk.endsWith(suffix) && memberOfOk.startsWith(prefix)) {
					String role = memberOfOk.substring(prefix.length(), memberOfOk.length() - suffix.length());
					roles.add(role);
				}
			}
			user.setMemberOf(values.toArray(new String[values.size()]));
		}

		String mailKey = GlobalProperties.getProperties().getProperty(LDAPUserManager.LDAP_EMAIL_ATTRIBUTE);
		Attribute mail = attrib.get(mailKey);
		if (mail == null) {
			user.setEmail(null);
		} else {
			user.setEmail((String) mail.get());
		}

		String telephoneKey = GlobalProperties.getProperties().getProperty(LDAPUserManager.LDAP_TELEPHONE_ATTRIBUTE);
		if (telephoneKey == null) {
			user.setTelephoneNumber(null);
		} else {
			Attribute telephone = attrib.get(telephoneKey);
			if (telephone == null) {
				user.setTelephoneNumber(null);
			} else {
				user.setTelephoneNumber((String) telephone.get());
			}
		}

		String nifKey = GlobalProperties.getProperties().getProperty(LDAPUserManager.LDAP_ADMINISTRATIONID_ATTRIBUTE);
		Attribute nif = attrib.get(nifKey);
		if (nif == null) {
			user.setAdministrationID(null);
		} else {
			user.setAdministrationID((String) nif.get());
		}

		return user;

	}

	/**
	 * Obtains username from SearchResult.
	 * 
	 * @param ldapUser
	 *            Instance of SearchResult
	 * @return Username
	 * @throws Exception
	 *             If error.
	 */
	public String searchResultToUserName(Attributes attrib) throws Exception {

		// SearchResult ldapUser
		String userNameAttribKey = GlobalProperties.getProperties().getProperty(LDAPUserManager.LDAP_USERNAME_ATTRIBUTE);
		// Attributes attrib = ldapUser.getAttributes();
		Attribute userNameAttrib = attrib.get(userNameAttribKey);
		if (userNameAttrib == null) {
			throw new Exception("Cannot convert 'SearchResult' into 'username' due attribute " + "[" + userNameAttribKey
					+ "] is not in ldap user attributes: [" + attrib.toString() + "]", new Exception()); // ldapUser.toString()
		}
		String userName = (String) userNameAttrib.get();
		if (userName == null) {
			throw new Exception("Cannot convert 'SearchResult' into 'username' due attribute " + "["
					+ LDAPUserManager.LDAP_USERNAME_ATTRIBUTE + "] has value null: [" + attrib.toString() + "]", new Exception()); // ldapUser.toString()
		}
		return userName;
	}

	private Sexe mapSexe(Gender gender) {
		if (gender == null)
			return null;
		switch (gender) {
		case FEMALE:
			return Sexe.SEXE_DONA;
		case MALE:
		case UNKNOWN:
		default:
			return Sexe.SEXE_HOME;
		}
	}
	
}
