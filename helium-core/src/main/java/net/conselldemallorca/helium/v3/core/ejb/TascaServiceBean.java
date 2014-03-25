/**
 * 
 */
package net.conselldemallorca.helium.v3.core.ejb;

import java.util.List;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import net.conselldemallorca.helium.v3.core.api.dto.CampDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.SeleccioOpcioDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDadaDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDocumentDto;
import net.conselldemallorca.helium.v3.core.api.exception.CampNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.TascaNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.TaskInstanceNotFoundException;
import net.conselldemallorca.helium.v3.core.api.service.TascaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

/**
 * Servei per a enllaçar les llibreries jBPM 3 amb la funcionalitat de Helium.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class TascaServiceBean implements TascaService {

	@Autowired
	TascaService delegate;

	/**
	 * Retorna les dades d'una instància de tasca.
	 * 
	 * @param tascaId
	 * @return
	 * @throws TaskInstanceNotFoundException
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<TascaDadaDto> findDadesPerTasca(String tascaId) throws TascaNotFoundException {
		return delegate.findDadesPerTasca(tascaId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<TascaDocumentDto> findDocumentsPerTasca(
			String tascaId) {
		return delegate.findDocumentsPerTasca(tascaId);
	}

	/**
	 * Consulta els possibles valors per a un camp de tipus selecció del formulari de la tasca.
	 * 
	 * @param tascaId
	 * @param campId
	 * @return
	 * @throws TaskInstanceNotFoundException
	 * @throws CampNotFoundException
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<SeleccioOpcioDto> findOpcionsSeleccioPerCampTasca(String tascaId, Long campId) throws TaskInstanceNotFoundException, CampNotFoundException {
		return delegate.findOpcionsSeleccioPerCampTasca(tascaId, campId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean isTascaValidada(Object task) {
		return delegate.isTascaValidada(task);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean isDocumentsComplet(Object task) {
		return delegate.isDocumentsComplet(task);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean isSignaturesComplet(Object task) {
		return delegate.isSignaturesComplet(task);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public Object getVariable(Long entornId, String taskId, String codiVariable) {
		return delegate.getVariable(entornId, taskId, codiVariable);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTascaDto getById(Long entornId, String taskId, String usuari, Map<String, Object> valorsCommand, boolean ambVariables, boolean ambTexts) throws TascaNotFoundException{
		return delegate.getById(entornId, taskId, usuari, valorsCommand, ambVariables, ambTexts);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTascaDto getByIdSenseComprovacio(String taskId) {
		return delegate.getByIdSenseComprovacio(taskId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTascaDto guardarVariables(Long entornId, String taskId, Map<String, Object> variables, String usuari) {
		return delegate.guardarVariables(entornId, taskId, variables, usuari);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTascaDto validar(Long entornId, String taskId, Map<String, Object> variables, boolean comprovarAssignacio) {
		return delegate.validar(entornId, taskId, variables, comprovarAssignacio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTascaDto validar(Long entornId, String taskId, Map<String, Object> variables, boolean comprovarAssignacio, String usuari) {
		return delegate.validar(entornId, taskId, variables, comprovarAssignacio, usuari);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTascaDto restaurar(Long entornId, String taskId) {
		return delegate.restaurar(entornId, taskId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTascaDto restaurar(Long entornId, String taskId, String user) {
		return delegate.restaurar(entornId, taskId, user);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void completar(Long entornId, String taskId, boolean comprovarAssignacio, String usuari) {
		delegate.completar(entornId, taskId, comprovarAssignacio, usuari);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void completar(Long entornId, String taskId, boolean comprovarAssignacio, String usuari, String outcome) {
		delegate.completar(entornId, taskId, comprovarAssignacio, usuari, outcome);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void esborrarRegistre(Long entornId, String taskId, String campCodi, int index) {
		delegate.esborrarRegistre(entornId, taskId, campCodi, index);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void esborrarRegistre(Long entornId, String taskId, String campCodi, int index, String usuari) {
		delegate.esborrarRegistre(entornId, taskId, campCodi, index, usuari);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void executarAccio(Long entornId, String taskId, String accio) {
		delegate.executarAccio(entornId, taskId, accio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void executarAccio(Long entornId, String taskId, String accio, String user) {
		delegate.executarAccio(entornId, taskId, accio, user);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void guardarRegistre(Long entornId, String taskId, String campCodi, Object[] valors) {
		delegate.guardarRegistre(entornId, taskId, campCodi, valors);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void guardarRegistre(Long entornId, String taskId, String campCodi, Object[] valors, int index) {
		delegate.guardarRegistre(entornId, taskId, campCodi, valors, index);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void guardarRegistre(Long entornId, String taskId, String campCodi, Object[] valors, String usuari) {
		delegate.guardarRegistre(entornId, taskId, campCodi, valors, usuari);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void guardarRegistre(Long entornId, String taskId, String campCodi, Object[] valors, int index, String usuari) {
		delegate.guardarRegistre(entornId, taskId, campCodi, valors, index, usuari);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void borrarVariables(Long entornId, String taskId, String variable, String usuari) {
		delegate.borrarVariables(entornId, taskId, variable, usuari);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<TascaDadaDto> findDadesPerTascaDto(ExpedientTascaDto tasca) {
		return delegate.findDadesPerTascaDto(tasca);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTascaDto guardarVariable(Long entornId, String taskId, String variable, Object valor) {
		return delegate.guardarVariable(entornId, taskId, variable, valor);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTascaDto guardarVariable(Long entornId, String taskId, String variable, Object valor, String usuari) {
		return delegate.guardarVariable(entornId, taskId, variable, valor, usuari);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void delegacioCancelar(Long entornId, String taskId) {
		delegate.delegacioCancelar(entornId, taskId);		
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTascaDto getTascaPerExpedientId(Long expedientId, String tascaId) {
		return delegate.getTascaPerExpedientId(expedientId, tascaId);		
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTascaDto agafar(Long entornId, String taskId) {
		return delegate.agafar(entornId, taskId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTascaDto alliberar(Long entornId, String tascaId, boolean comprovarResponsable) {
		return delegate.alliberar(entornId, tascaId, comprovarResponsable);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public CampDto findCampTasca(Long campId) {
		return delegate.findCampTasca(campId);
	}

}
