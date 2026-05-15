/**
 *
 */
package es.caib.helium.ejb;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.helium.ejb.base.AbstractServiceEjb;
import lombok.experimental.Delegate;

import es.caib.helium.commons.dto.FestiuDto;
import es.caib.helium.commons.dto.TerminiDto;
import es.caib.helium.commons.dto.TerminiIniciatDto;
import es.caib.helium.commons.dto.regles.CampFormProperties;
import es.caib.helium.commons.exception.NoTrobatException;
import es.caib.helium.commons.exception.PermisDenegatException;
import es.caib.helium.commons.exception.ValidacioException;
import es.caib.helium.logic.intf.service.ExpedientTerminiService;

/**
 * EJB que implementa la interfície del servei ExpedientTerminiService.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class ExpedientTerminiServiceBean extends AbstractServiceEjb<ExpedientTerminiService> implements ExpedientTerminiService {

	@Delegate
	ExpedientTerminiService delegateService;

	protected void setDelegateService(ExpedientTerminiService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public TerminiIniciatDto iniciar(
			Long expedientId,
			String processInstanceId,
			Long terminiId,
			Date data,
			boolean esDataFi) throws NoTrobatException, PermisDenegatException {
		return delegateService.iniciar(
				expedientId,
				processInstanceId,
				terminiId,
				data,
				esDataFi);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void modificar(
			Long expedientId,
			String processInstanceId,
			Long terminiIniciatId,
			Date inicio,
			int anys,
			int mesos,
			int dies,
			boolean esDataFi) throws NoTrobatException, PermisDenegatException {
		delegateService.modificar(
				expedientId,
				processInstanceId,
				terminiIniciatId,
				inicio,
				anys,
				mesos,
				dies,
				esDataFi);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void suspendre(
			Long expedientId,
			String processInstanceId,
			Long terminiIniciatId, Date data) throws NoTrobatException, PermisDenegatException {
		delegateService.suspendre(
				expedientId,
				processInstanceId,
				terminiIniciatId,
				data);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void reprendre(
			Long expedientId,
			String processInstanceId,
			Long terminiIniciatId,
			Date data) throws NoTrobatException, PermisDenegatException {
		delegateService.reprendre(
				expedientId,
				processInstanceId,
				terminiIniciatId,
				data);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void cancelar(
			Long expedientId,
			String processInstanceId,
			Long terminiIniciatId,
			Date data) throws NoTrobatException, PermisDenegatException {
		delegateService.cancelar(
				expedientId,
				processInstanceId,
				terminiIniciatId,
				data);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<TerminiDto> findAmbProcessInstanceId(
			Long expedientId,
			String processInstanceId) throws NoTrobatException, PermisDenegatException {
		return delegateService.findAmbProcessInstanceId(
				expedientId,
				processInstanceId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<TerminiIniciatDto> iniciatFindAmbProcessInstanceId(
			Long expedientId,
			String processInstanceId) {
		return delegateService.iniciatFindAmbProcessInstanceId(
				expedientId,
				processInstanceId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public TerminiIniciatDto iniciatFindAmbId(
			Long expedientId,
			String processInstanceId,
			Long terminiIniciatId) throws NoTrobatException {
		return delegateService.iniciatFindAmbId(
				expedientId,
				processInstanceId,
				terminiIniciatId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public List<FestiuDto> festiuFindAmbAny(
			int any) {
		return delegateService.festiuFindAmbAny(any);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public void festiuCreate(
			String data) throws Exception {
		delegateService.festiuCreate(data);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public void festiuDelete(
			String data) throws ValidacioException, Exception {
		delegateService.festiuDelete(data);

	}

    @Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
    public Map<String, CampFormProperties> getTerminisFormProperties(Long expedientTipusId, String estatCodi) {
        return delegateService.getTerminisFormProperties(expedientTipusId, estatCodi);
    }

}
