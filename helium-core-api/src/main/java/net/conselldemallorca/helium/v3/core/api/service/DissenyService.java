/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.service;

import java.util.List;

import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentDissenyDto;
import net.conselldemallorca.helium.v3.core.api.dto.EstatDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.TerminiDto;
import net.conselldemallorca.helium.v3.core.api.exception.DefinicioProcesNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.EntornNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.ExpedientTipusNotFoundException;


/**
 * Servei que proporciona la funcionalitat de disseny d'expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface DissenyService {

	public ExpedientTipusDto findExpedientTipusAmbEntornICodi(
			Long entornId,
			String expedientTipusCodi) throws EntornNotFoundException;

	public List<EstatDto> findEstatByExpedientTipus(
			Long expedientTipusId) throws ExpedientTipusNotFoundException;

	public EstatDto findEstatAmbExpedientTipusICodi(
			Long expedientTipusId,
			String estatCodi) throws ExpedientTipusNotFoundException;

	public DocumentDissenyDto getDocumentDisseny(
			Long definicioProcesId,
			String documentCodi) throws DefinicioProcesNotFoundException;

	public DefinicioProcesDto getDefinicioProcesAmbJbpmKeyIVersio(
			String jbpmKey,
			int versio);
	
	public DefinicioProcesDto getDarreraVersioAmbEntornIJbpmKey(
			Long entornId,
			String jbpmKey) throws EntornNotFoundException;
	
	public DefinicioProcesDto getDefinicioProcesPerProcessInstanceId(
			String processInstanceId) throws DefinicioProcesNotFoundException;
	
	public TerminiDto findTerminiAmbDefinicioProcesICodi(
			Long definicioProcesId,
			String codi) throws DefinicioProcesNotFoundException;

	public List<ExpedientTipusDto> findExpedientTipusAmbPermisReadUsuariActual(
			Long entornId) throws EntornNotFoundException;

	public List<ExpedientTipusDto> findExpedientTipusAmbPermisDissenyUsuariActual(
			Long entornId) throws EntornNotFoundException;

	public List<ExpedientTipusDto> findExpedientTipusAmbPermisGestioUsuariActual(
			Long entornId) throws EntornNotFoundException;

	public List<ExpedientTipusDto> findExpedientTipusAmbPermisCrearUsuariActual(
			Long entornId) throws EntornNotFoundException;

}
