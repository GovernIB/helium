package net.conselldemallorca.helium.webapp.v3.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import es.caib.helium.logic.intf.dto.CampDto;
import es.caib.helium.logic.intf.dto.CampTascaDto;
import es.caib.helium.logic.intf.dto.CampTipusDto;
import es.caib.helium.logic.intf.dto.DocumentDto;
import es.caib.helium.logic.intf.dto.DocumentTascaDto;
import es.caib.helium.logic.intf.dto.FirmaTascaDto;
import es.caib.helium.logic.intf.dto.ParellaCodiValorDto;
import es.caib.helium.logic.intf.dto.TascaDto;
import es.caib.helium.logic.intf.service.DocumentService;

/**
 * Controlador base per als controladors de disseny de tasques del tipus d'expedient
 * i de les definicions de procés.
 * Conté mètodes compartits i exten del BaseDissenyController que conté serveis i inicialitzacions.
 */
public class BaseTascaDissenyController extends BaseDissenyController {
	
	@Autowired
	DocumentService documentService;
	
	/**
	 * Retorna les parelles codi i valor per a les possibles variables per als camps de les consultes.
	 * Lleva les variables que s'han utilitzat ja en la tasca.
	 * @param expedientTipusId
	 * @param definicioProcesId
	 * @param tascaId
	 * @return
	 */
	protected List<ParellaCodiValorDto> obtenirParellesVariables(
			Long expedientTipusId,
			Long definicioProcesId,
			List<CampDto> variables,
			Long tascaId) {
		List<ParellaCodiValorDto> resposta = new ArrayList<ParellaCodiValorDto>();
		// Tasca els camps de la tasca segons el tipus
		List<CampTascaDto> camps = definicioProcesService.tascaCampFindAll(expedientTipusId, tascaId);
		// Lleva les variables que ja pertanyin a algun camp
		Iterator<CampDto> it = variables.iterator();
		while (it.hasNext()) {
			CampDto variable = it.next();
			for (CampTascaDto camp : camps) {
				if (variable.getId().equals(camp.getCamp().getId())) {
					it.remove();
					break;
				}
			}
		}
		// Si és la tasca inicial treu les variables de tipus acció
		String startTaskName = definicioProcesService.consultarStartTaskName(definicioProcesId);
		TascaDto tasca = definicioProcesService.tascaFindAmbId(expedientTipusId, tascaId);
		if (startTaskName != null && tasca.getNom().equals(startTaskName)) {
			it = variables.iterator();
			CampDto camp;
			while (it.hasNext()) {
				camp = it.next();
				if (camp.getTipus().equals(CampTipusDto.ACCIO))
					it.remove();
			}
		}
		// Crea les parelles de codi i valor
		for (CampDto variable : variables) {
			resposta.add(new ParellaCodiValorDto(
					variable.getId().toString(), 
					variable.getCodi() + " / " + variable.getEtiqueta()));
		}			
		return resposta;
	}	
	
	/**
	 * Retorna les parelles codi i valor per a les possibles documents per als documents de les consultes.
	 * Lleva les documents que s'han utilitzat ja en la tasca.
	 * @param expedientTipusId
	 * @param definicioProcesId
	 * @param documents 
	 * @param tascaId
	 * @return
	 */
	protected List<ParellaCodiValorDto> documentObtenirParellesDocuments(
			Long expedientTipusId,
			List<DocumentDto> documents, 
			Long tascaId) {
		List<ParellaCodiValorDto> resposta = new ArrayList<ParellaCodiValorDto>();
		// Documents de la tasca existents
		List<DocumentTascaDto> documentsTasca = definicioProcesService.tascaDocumentFindAll(expedientTipusId, tascaId);
		// Lleva les documents que ja pertanyin a algun document
		Iterator<DocumentDto> it = documents.iterator();
		while (it.hasNext()) {
			DocumentDto document = it.next();
			for (DocumentTascaDto documentTasca : documentsTasca) {
				if (document.getId().equals(documentTasca.getDocument().getId())) {
					it.remove();
					break;
				}
			}
		}
		// Crea les parelles de codi i valor
		for (DocumentDto document : documents) {
			resposta.add(new ParellaCodiValorDto(
					document.getId().toString(), 
					document.getCodi() + " / " + document.getNom()));
		}			
		return resposta;
	}
	
	/**
	 * Retorna les parelles codi i valor per als possibles docuemnts per a les firmes de les tasques.
	 * Lleva els documents que s'han utilitzat ja en la tasca.
	 * @param definicioProcesId
	 * @param tascaId
	 * @return
	 */
	protected List<ParellaCodiValorDto> firmaObtenirParellesDocuments(
			Long expedientTipusId,
			List<DocumentDto> documents,
			Long tascaId) {
		List<ParellaCodiValorDto> resposta = new ArrayList<ParellaCodiValorDto>();
		// Documents de la tasca existents
		List<FirmaTascaDto> firmesTasca = definicioProcesService.tascaFirmaFindAll(expedientTipusId, tascaId);
		// Lleva les firmes que ja pertanyin a algun firma
		Iterator<DocumentDto> it = documents.iterator();
		while (it.hasNext()) {
			DocumentDto firma = it.next();
			for (FirmaTascaDto firmaTasca : firmesTasca) {
				if (firma.getId().equals(firmaTasca.getDocument().getId())) {
					it.remove();
					break;
				}
			}
		}
		// Crea les parelles de codi i valor
		for (DocumentDto firma : documents) {
			resposta.add(new ParellaCodiValorDto(
					firma.getId().toString(), 
					firma.getCodi() + " / " + firma.getNom()));
		}			
		return resposta;
	}		
}
