/**
 * 
 */
package net.conselldemallorca.helium.core.model.dao;

import net.conselldemallorca.helium.core.model.hibernate.Usuari;
import net.conselldemallorca.helium.core.util.PasswordDigester;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Dao pels objectes de tipus usuari
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Repository
public class UsuariDao extends HibernateGenericDao<Usuari, String> {

	private PasswordDigester passwordDigester;



	public UsuariDao() {
		super(Usuari.class);
	}

	public void canviContrasenya(String id, String contrasenya) throws Exception {
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
