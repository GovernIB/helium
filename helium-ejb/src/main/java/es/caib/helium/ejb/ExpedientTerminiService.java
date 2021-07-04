/**
 * 
 */
package es.caib.helium.ejb;

import es.caib.helium.logic.intf.dto.FestiuDto;
import es.caib.helium.logic.intf.dto.TerminiDto;
import es.caib.helium.logic.intf.dto.TerminiIniciatDto;
import es.caib.helium.logic.intf.exception.NoTrobatException;
import es.caib.helium.logic.intf.exception.PermisDenegatException;
import es.caib.helium.logic.intf.exception.ValidacioException;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.util.Date;
import java.util.List;

/**
 * EJB que implementa la interfície del servei ExpedientTerminiService.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class ExpedientTerminiService extends AbstractService<es.caib.helium.logic.intf.service.ExpedientTerminiService> implements es.caib.helium.logic.intf.service.ExpedientTerminiService {
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public TerminiIniciatDto iniciar(
			Long expedientId,
			String processInstanceId,
			Long terminiId,
			Date data,
			boolean esDataFi) throws NoTrobatException, PermisDenegatException {
		return getDelegateService().iniciar(
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
		getDelegateService().modificar(
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
		getDelegateService().suspendre(
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
		getDelegateService().reprendre(
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
		getDelegateService().cancelar(
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
		return getDelegateService().findAmbProcessInstanceId(
				expedientId,
				processInstanceId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<TerminiIniciatDto> iniciatFindAmbProcessInstanceId(
			Long expedientId,
			String processInstanceId) {
		return getDelegateService().iniciatFindAmbProcessInstanceId(
				expedientId,
				processInstanceId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public TerminiIniciatDto iniciatFindAmbId(
			Long expedientId,
			String processInstanceId,
			Long terminiIniciatId) throws NoTrobatException {
		return getDelegateService().iniciatFindAmbId(
				expedientId,
				processInstanceId,
				terminiIniciatId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public List<FestiuDto> festiuFindAmbAny(
			int any) {
		return getDelegateService().festiuFindAmbAny(any);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public void festiuCreate(
			String data) throws Exception {
		getDelegateService().festiuCreate(data);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public void festiuDelete(
			String data) throws ValidacioException, Exception {
		getDelegateService().festiuDelete(data);
		
	}

}
