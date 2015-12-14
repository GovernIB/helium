/**
 * 
 */
package net.conselldemallorca.helium.core.model.update;

import net.conselldemallorca.helium.core.model.service.UpdateService;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Classe per verificar la versió actual de les dades
 * de la base de dades i executar les actualitzacions
 * pertinents.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class Updater {

	private UpdateService updateService;



	@Autowired
	public Updater(
			UpdateService updateService) throws Exception {
		this.updateService = updateService;
		testInitialUpdate();
	}

	private void testInitialUpdate() throws Exception {
		updateService.updateToLastVersion();
	}

}
