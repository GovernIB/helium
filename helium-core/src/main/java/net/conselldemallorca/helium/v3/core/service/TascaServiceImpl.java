/**
 * 
 */
package net.conselldemallorca.helium.v3.core.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.EnumeracioValors;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmTask;
import net.conselldemallorca.helium.v3.core.api.dto.SeleccioOpcioDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDadaDto;
import net.conselldemallorca.helium.v3.core.api.exception.CampNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.TaskInstanceNotFoundException;
import net.conselldemallorca.helium.v3.core.api.service.TascaService;
import net.conselldemallorca.helium.v3.core.helper.TascaHelper;
import net.conselldemallorca.helium.v3.core.helper.VariableHelper;
import net.conselldemallorca.helium.v3.core.repository.CampRepository;
import net.conselldemallorca.helium.v3.core.repository.EnumeracioValorsRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servei per gestionar terminis.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service("tascaServiceV3")
public class TascaServiceImpl implements TascaService {

	@Resource
	private EnumeracioValorsRepository enumeracioValorsRepository;
	@Resource
	private CampRepository campRepository;

	@Resource
	private TascaHelper tascaHelper;
	@Resource
	private VariableHelper variableHelper;


	@Transactional(readOnly = true)
	@Override
	public List<TascaDadaDto> findDadesPerTasca(
			String tascaId) {
		JbpmTask tasca = tascaHelper.getTascaComprovantAcces(tascaId);
		return variableHelper.findDadesPerInstanciaTasca(tasca);
	}

	@Transactional(readOnly = true)
	@Override
	public List<SeleccioOpcioDto> findOpcionsSeleccioPerCampTasca(
			String tascaId,
			Long campId) throws TaskInstanceNotFoundException, CampNotFoundException {
		JbpmTask tasca = tascaHelper.getTascaComprovantAcces(tascaId);
		Camp camp = campRepository.findOne(campId);
		if (camp == null)
			throw new CampNotFoundException();
		if (!tasca.getProcessDefinitionId().equals(camp.getDefinicioProces().getJbpmId()))
			throw new CampNotFoundException();
		List<SeleccioOpcioDto> resposta = new ArrayList<SeleccioOpcioDto>();
		if (camp.getDomini() != null) {
			resposta = new ArrayList<SeleccioOpcioDto>();
		} else if (camp.getEnumeracio() != null) {
			List<EnumeracioValors> valors = enumeracioValorsRepository.findByEnumeracioOrderByOrdreAsc(
					camp.getEnumeracio());
			for (EnumeracioValors valor: valors) {
				resposta.add(
						new SeleccioOpcioDto(
								valor.getCodi(),
								valor.getNom()));
			}
		}
		return resposta;
	}

}
