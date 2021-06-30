/**
 * 
 */
package net.conselldemallorca.helium.v3.core.ejb;

import java.util.Date;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.emiserv.logic.intf.exception.NoTrobatException;
import es.caib.emiserv.logic.intf.exception.PermisDenegatException;
import es.caib.emiserv.logic.intf.exception.ValidacioException;
import es.caib.helium.logic.intf.dto.FestiuDto;
import es.caib.helium.logic.intf.dto.TerminiDto;
import es.caib.helium.logic.intf.dto.TerminiIniciatDto;
import es.caib.helium.logic.intf.service.ExpedientTerminiService;

/**
 * EJB que implementa la interfície del servei ExpedientTerminiService.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class ExpedientTerminiServiceBean implements ExpedientTerminiService {
	
	@Autowired
	ExpedientTerminiService delegate;

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public TerminiIniciatDto iniciar(
			Long expedientId,
			String processInstanceId,
			Long terminiId,
			Date data,
			boolean esDataFi) throws NoTrobatException, PermisDenegatException {
		return delegate.iniciar(
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
		delegate.modificar(
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
		delegate.suspendre(
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
		delegate.reprendre(
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
		delegate.cancelar(
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
		return delegate.findAmbProcessInstanceId(
				expedientId,
				processInstanceId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<TerminiIniciatDto> iniciatFindAmbProcessInstanceId(
			Long expedientId,
			String processInstanceId) {
		return delegate.iniciatFindAmbProcessInstanceId(
				expedientId,
				processInstanceId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public TerminiIniciatDto iniciatFindAmbId(
			Long expedientId,
			String processInstanceId,
			Long terminiIniciatId) throws NoTrobatException {
		return delegate.iniciatFindAmbId(
				expedientId,
				processInstanceId,
				terminiIniciatId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public List<FestiuDto> festiuFindAmbAny(
			int any) {
		return delegate.festiuFindAmbAny(any);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public void festiuCreate(
			String data) throws Exception {
		delegate.festiuCreate(data);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public void festiuDelete(
			String data) throws ValidacioException, Exception {
		delegate.festiuDelete(data);
		
	}

}
