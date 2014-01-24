package net.conselldemallorca.helium.v3.core.ejb;

import java.util.Date;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import net.conselldemallorca.helium.v3.core.api.dto.FestiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.TerminiIniciatDto;
import net.conselldemallorca.helium.v3.core.api.service.TerminiService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class TerminiServiceBean implements TerminiService {
	
	@Autowired
	TerminiService delegate;
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public TerminiIniciatDto iniciar(Long terminiId, String processInstanceId, Date data, boolean esDataFi) {
		return delegate.iniciar(terminiId, processInstanceId, data, esDataFi);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public TerminiIniciatDto iniciar(Long terminiId, String processInstanceId, Date data, int anys, int mesos, int dies, boolean esDataFi) {
		return delegate.iniciar(terminiId, processInstanceId, data, anys, mesos, dies, esDataFi);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void pausar(Long terminiIniciatId, Date data) {
		delegate.pausar(terminiIniciatId, data);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void continuar(Long terminiIniciatId, Date data) {
		delegate.continuar(terminiIniciatId, data);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void cancelar(Long terminiIniciatId, Date data) {
		delegate.cancelar(terminiIniciatId, data);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public Date getDataFiTermini(Date inici, int anys, int mesos, int dies, boolean laborable) {
		return delegate.getDataFiTermini(inici, anys, mesos, dies, laborable);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public Date getDataIniciTermini(Date fi, int anys, int mesos, int dies, boolean laborable) {
		return delegate.getDataIniciTermini(fi, anys, mesos, dies, laborable);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<TerminiIniciatDto> findIniciatsAmbProcessInstanceId(String processInstanceId) {
		return delegate.findIniciatsAmbProcessInstanceId(processInstanceId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<TerminiIniciatDto> findIniciatsAmbTaskInstanceIds(String[] taskInstanceIds) {
		return delegate.findIniciatsAmbTaskInstanceIds(taskInstanceIds);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public TerminiIniciatDto findIniciatAmbTerminiIdIProcessInstanceId(Long terminiId, String processInstanceId) {
		return delegate.findIniciatAmbTerminiIdIProcessInstanceId(terminiId, processInstanceId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public FestiuDto getFestiuById(Long id) {
		return delegate.getFestiuById(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void configurarTerminiIniciatAmbDadesJbpm(Long terminiIniciatId, String taskInstanceId, Long timerId) {
		delegate.configurarTerminiIniciatAmbDadesJbpm(terminiIniciatId, taskInstanceId, timerId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public FestiuDto createFestiu(FestiuDto entity) {
		return delegate.createFestiu(entity);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void deleteFestiu(Long id) {
		delegate.deleteFestiu(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public FestiuDto updateFestiu(FestiuDto entity) {
		return delegate.updateFestiu(entity);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public FestiuDto findFestiuAmbData(Date data) {
		return delegate.findFestiuAmbData(data);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<FestiuDto> findFestiuAmbAny(int any) {
		return delegate.findFestiuAmbAny(any);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void comprovarTerminisIniciats() {
		delegate.comprovarTerminisIniciats();
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void modificarTerminiIniciat(Long terminiIniciatId, Date dataInici, int anys, int mesos, int dies) {
		delegate.modificarTerminiIniciat(terminiIniciatId, dataInici, anys, mesos, dies);
	}

}
