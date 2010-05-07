/*
 * 
 */
package net.conselldemallorca.helium.integracio.security;

import net.conselldemallorca.helium.util.GlobalProperties;

import org.securityfilter.realm.SimpleSecurityRealmBase;

/**
 * Realm per a l'autenticació en l'accés als ws
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class IntegracioSecurityRealm extends SimpleSecurityRealmBase {

	private String bantelAvisosRole;
	private String dominiSistraRole;
	private String dominiInternRole;
	private String portasignaturesRole;
	private String formulariExternRole;



	public boolean booleanAuthenticate(String username, String password) {
		if (authenticateBantelAvisos(username, password))
			return true;
		if (authenticateDominiSistra(username, password))
			return true;
		if (authenticateDominiIntern(username, password))
			return true;
		if (authenticatePortasignatures(username, password))
			return true;
		if (authenticateFormulariExtern(username, password))
			return true;
		return false;
	}

	public boolean isUserInRole(String username, String role) {
		if (isUserInRoleBantelAvisos(username, role))
			return true;
		if (isUserInRoleDominiSistra(username, role))
			return true;
		if (isUserInRoleDominiIntern(username, role))
			return true;
		if (isUserInRolePortasignatures(username, role))
			return true;
		if (isUserInRoleFormulariExtern(username, role))
			return true;
		return false;
	}



	public void setBantelAvisosRole(String bantelAvisosRole) {
		this.bantelAvisosRole = bantelAvisosRole;
	}
	public void setDominiSistraRole(String dominiSistraRole) {
		this.dominiSistraRole = dominiSistraRole;
	}
	public void setDominiInternRole(String dominiInternRole) {
		this.dominiInternRole = dominiInternRole;
	}
	public void setPortasignaturesRole(String portasignaturesRole) {
		this.portasignaturesRole = portasignaturesRole;
	}
	public void setFormulariExternRole(String formulariExternRole) {
		this.formulariExternRole = formulariExternRole;
	}



	private boolean authenticateBantelAvisos(String username, String password) {
		if (noAutenticacioBantelAvisos())
			return true;
		return getBantelAvisosUsername().equals(username) && getBantelAvisosPassword().equals(password);
	}
	private boolean isUserInRoleBantelAvisos(String username, String role) {
		if (noAutenticacioBantelAvisos())
			return true;
		return username.equals(getBantelAvisosUsername()) && role.equals(bantelAvisosRole);
	}
	private String getBantelAvisosUsername() {
		return GlobalProperties.getInstance().getProperty("app.bantel.avisos.username");
	}
	private String getBantelAvisosPassword() {
		return GlobalProperties.getInstance().getProperty("app.bantel.avisos.password");
	}
	private boolean noAutenticacioBantelAvisos() {
		boolean emptyUsername = (getBantelAvisosUsername() == null || "".equals(getBantelAvisosUsername()));
		boolean emptyPassword = (getBantelAvisosPassword() == null || "".equals(getBantelAvisosPassword()));
		return emptyUsername && emptyPassword;
	}

	private boolean authenticateDominiSistra(String username, String password) {
		if (noAutenticacioDominiSistra())
			return true;
		return getDominiSistraUsername().equals(username) && getDominiSistraPassword().equals(password);
	}
	private boolean isUserInRoleDominiSistra(String username, String role) {
		if (noAutenticacioDominiSistra())
			return true;
		return username.equals(getDominiSistraUsername()) && role.equals(dominiSistraRole);
	}
	private String getDominiSistraUsername() {
		return GlobalProperties.getInstance().getProperty("app.domini.consulta.username");
	}
	private String getDominiSistraPassword() {
		return GlobalProperties.getInstance().getProperty("app.domini.consulta.password");
	}
	private boolean noAutenticacioDominiSistra() {
		boolean emptyUsername = (getDominiSistraUsername() == null || "".equals(getDominiSistraUsername()));
		boolean emptyPassword = (getDominiSistraPassword() == null || "".equals(getDominiSistraPassword()));
		return emptyUsername && emptyPassword;
	}

	private boolean authenticateDominiIntern(String username, String password) {
		if (noAutenticacioDominiIntern())
			return true;
		return getDominiInternUsername().equals(username) && getDominiInternPassword().equals(password);
	}
	private boolean isUserInRoleDominiIntern(String username, String role) {
		if (noAutenticacioDominiIntern())
			return true;
		return username.equals(getDominiInternUsername()) && role.equals(dominiInternRole);
	}
	private String getDominiInternUsername() {
		return GlobalProperties.getInstance().getProperty("app.domini.intern.username");
	}
	private String getDominiInternPassword() {
		return GlobalProperties.getInstance().getProperty("app.domini.intern.password");
	}
	private boolean noAutenticacioDominiIntern() {
		boolean emptyUsername = (getDominiInternUsername() == null || "".equals(getDominiInternUsername()));
		boolean emptyPassword = (getDominiInternPassword() == null || "".equals(getDominiInternPassword()));
		return emptyUsername && emptyPassword;
	}

	private boolean authenticatePortasignatures(String username, String password) {
		if (noAutenticacioPortasignatures())
			return true;
		return getPortasignaturesUsername().equals(username) && getPortasignaturesPassword().equals(password);
	}
	private boolean isUserInRolePortasignatures(String username, String role) {
		if (noAutenticacioPortasignatures())
			return true;
		return username.equals(getPortasignaturesUsername()) && role.equals(portasignaturesRole);
	}
	private String getPortasignaturesUsername() {
		return GlobalProperties.getInstance().getProperty("app.portasignatures.callback.username");
	}
	private String getPortasignaturesPassword() {
		return GlobalProperties.getInstance().getProperty("app.portasignatures.callback.password");
	}
	private boolean noAutenticacioPortasignatures() {
		boolean emptyUsername = (getPortasignaturesUsername() == null || "".equals(getPortasignaturesUsername()));
		boolean emptyPassword = (getPortasignaturesPassword() == null || "".equals(getPortasignaturesPassword()));
		return emptyUsername && emptyPassword;
	}

	private boolean authenticateFormulariExtern(String username, String password) {
		if (noAutenticacioFormulariExtern())
			return true;
		return getFormulariExternUsername().equals(username) && getFormulariExternPassword().equals(password);
	}
	private boolean isUserInRoleFormulariExtern(String username, String role) {
		if (noAutenticacioFormulariExtern())
			return true;
		return username.equals(getFormulariExternUsername()) && role.equals(formulariExternRole);
	}
	private String getFormulariExternUsername() {
		return GlobalProperties.getInstance().getProperty("app.form.guardar.password");
	}
	private String getFormulariExternPassword() {
		return GlobalProperties.getInstance().getProperty("app.form.guardar.password");
	}
	private boolean noAutenticacioFormulariExtern() {
		boolean emptyUsername = (getFormulariExternUsername() == null || "".equals(getFormulariExternUsername()));
		boolean emptyPassword = (getFormulariExternPassword() == null || "".equals(getFormulariExternPassword()));
		return emptyUsername && emptyPassword;
	}

}
