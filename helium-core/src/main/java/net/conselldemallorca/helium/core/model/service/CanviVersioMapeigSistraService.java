/**
 * 
 */
package net.conselldemallorca.helium.core.model.service;

import java.util.List;

import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.MapeigSistra;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * Servei per gestionar la inicialització del sistema i les
 * actualitzacions automàtiques
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class CanviVersioMapeigSistraService {

	private DissenyService dissenyService;

	public void canviarVersioMapeigSistra() throws Exception {
		
		if (dissenyService.findMapeigSistraTots().size() == 0){
			List<ExpedientTipus> expedientsTipus = dissenyService.findExpedientTipusTots();
			
			for (ExpedientTipus expedientTipus : expedientsTipus){
				if (expedientTipus.getSistraTramitMapeigCamps() != null){
					mapeigSistraCanviVersioVariables(expedientTipus);
				}
				if (expedientTipus.getSistraTramitMapeigDocuments() != null){
					mapeigSistraCanviVersioDocuments(expedientTipus);
				}
				if (expedientTipus.getSistraTramitMapeigAdjunts() != null){
					mapeigSistraCanviVersioAdjunts(expedientTipus);
				}			
			}
			
		}
	}
	
	private void mapeigSistraCanviVersioVariables(ExpedientTipus expedientTipus){
		String[] parts = expedientTipus.getSistraTramitMapeigCamps().split(";");
		for (int i = 0; i < parts.length; i++) {
			String[] parella = parts[i].split(":");
			if (parella.length > 1) {
				String varSistra = parella[0];
				String varHelium = parella[1];
				if (varHelium != null && (!"".equalsIgnoreCase(varHelium))){
					if (dissenyService.findMapeigSistraAmbExpedientTipusICodi(expedientTipus.getId(), varHelium) == null){
						dissenyService.createMapeigSistra(varHelium, varSistra, MapeigSistra.TipusMapeig.Variable, expedientTipus);
					}
				}
			}
		}
	}
	
	private void mapeigSistraCanviVersioDocuments(ExpedientTipus expedientTipus){
		String[] parts = expedientTipus.getSistraTramitMapeigDocuments().split(";");
		for (int i = 0; i < parts.length; i++) {
			String[] parella = parts[i].split(":");
			if (parella.length > 1) {
				String varSistra = parella[0];
				String varHelium = parella[1];
				if (varHelium != null && (!"".equalsIgnoreCase(varHelium))){
					if (dissenyService.findMapeigSistraAmbExpedientTipusICodi(expedientTipus.getId(), varHelium) == null){
						dissenyService.createMapeigSistra(varHelium, varSistra, MapeigSistra.TipusMapeig.Document, expedientTipus);
					}
				}
			}
		}
	}
	
	private void mapeigSistraCanviVersioAdjunts(ExpedientTipus expedientTipus){
		String[] parts = expedientTipus.getSistraTramitMapeigAdjunts().split(";");
		for (int i = 0; i < parts.length; i++) {
			String varSistra = parts[i];
				
			if (varSistra != null && (!"".equalsIgnoreCase(varSistra))){
				if (dissenyService.findMapeigSistraAmbExpedientTipusICodi(expedientTipus.getId(), varSistra) == null){
					dissenyService.createMapeigSistra(varSistra, varSistra, MapeigSistra.TipusMapeig.Adjunt, expedientTipus);
				}
			}
		}
	}

	@Autowired
	public void setDissenyService(DissenyService dissenyService) {
		this.dissenyService = dissenyService;
	}
	
	private static final Log logger = LogFactory.getLog(CanviVersioMapeigSistraService.class);

}
