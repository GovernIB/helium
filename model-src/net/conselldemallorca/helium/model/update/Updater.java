/**
 * 
 */
package net.conselldemallorca.helium.model.update;

import net.conselldemallorca.helium.model.service.UpdateService;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Classe per verificar la versió actual de les dades
 * de la base de dades i executar les actualitzacions
 * pertinents.
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class Updater {

	private UpdateService updateService;



	@Autowired
	public Updater(
			UpdateService updateService) {
		this.updateService = updateService;
		testInitialUpdate();
	}

	private void testInitialUpdate() {
		updateService.updateToLastVersion();
	}

}
