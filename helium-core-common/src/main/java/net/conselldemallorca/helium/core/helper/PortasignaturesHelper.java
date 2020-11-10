/**
 * 
 */
package net.conselldemallorca.helium.core.helper;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;

import net.conselldemallorca.helium.core.model.hibernate.Portasignatures;
import net.conselldemallorca.helium.v3.core.repository.PortasignaturesRepository;

/**
 * Classe helper per operar amb les dades de la taula de peticions
 * al portasignatures.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class PortasignaturesHelper {

	@Autowired
	PortasignaturesRepository portasignaturesRepository;
	
	public Portasignatures getByDocumentId(Integer documentId) {
		Portasignatures portasignatures = null;
		if (documentId != null) {
			portasignatures = portasignaturesRepository.findByDocumentId(documentId);
		}
		return portasignatures;
	}
	
	/** Classe auxiliar per fer el rollback del portafirmes en cas que s'hagi produit un error
	 * en la transacció i que s'hagi d'eliminar el document del portafirmes.
	 * @author danielm
	 *
	 */
	public static class PortasignaturesRollback implements TransactionSynchronization {
		
		private Integer documentId;
		PluginHelper pluginHelper;
		
		/** Constructor passant el valor del document per cancel·lar i la referència al plugin per
		 * fer la crida.
		 * 
		 * @param documentId
		 * @param pluginHelper
		 */
		public PortasignaturesRollback(Integer documentId, PluginHelper pluginHelper) {
			this.documentId = documentId;
			this.pluginHelper = pluginHelper;
		}

		/** En cas de rollback fa la crida a elimininar la petició de firma del portafirmes. */
		@Override
		public void afterCompletion(int status) {
			if ( TransactionSynchronization.STATUS_ROLLED_BACK == status) {
				try {					
					// Rollback. Cancel·lar la petició que s'acaba de fer al portafirmes.
					List<Integer> documentsIds = new ArrayList<Integer>();
					documentsIds.add(documentId);
					pluginHelper.portasignaturesCancelar(documentsIds);
					logger.debug("PortasignaturesHelper: Rollback de la petició de firma al portafirmes " + documentId + " realitzada correctament");
				} catch (Exception e) {
					logger.error("PortasignaturesHelper: Error en el rollback de firma al portafirmes " + documentId + ": " + e.getMessage(), e);
				}
			}
		}

		@Override
		public void suspend() {
		}

		@Override
		public void resume() {
		}

		@Override
		public void flush() {
		}

		@Override
		public void beforeCommit(boolean readOnly) {
		}

		@Override
		public void beforeCompletion() {
		}

		@Override
		public void afterCommit() {
		}

	}
		
	private static final Log logger = LogFactory.getLog(PortasignaturesHelper.class);
}
