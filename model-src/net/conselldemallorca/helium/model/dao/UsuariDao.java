/**
 * 
 */
package net.conselldemallorca.helium.model.dao;

import net.conselldemallorca.helium.model.hibernate.Usuari;
import net.conselldemallorca.helium.util.PasswordDigester;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Dao pels objectes de tipus usuari
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@Repository
public class UsuariDao extends HibernateGenericDao<Usuari, String> {

	private PasswordDigester passwordDigester;



	public UsuariDao() {
		super(Usuari.class);
	}

	public void canviContrasenya(String id, String contrasenya) {
		Usuari usuari = getById(id, false);
		if (contrasenya != null && contrasenya.length() > 0)
			usuari.setContrasenya(passwordDigester.digest(contrasenya));
		else
			usuari.setContrasenya(contrasenya);
	}

	@Autowired
	public void setPasswordDigester(PasswordDigester passwordDigester) {
		this.passwordDigester = passwordDigester;
	}

}
