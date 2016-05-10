package net.conselldemallorca.helium.v3.core.service;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.Repro;
import net.conselldemallorca.helium.v3.core.api.dto.ReproDto;
import net.conselldemallorca.helium.v3.core.api.service.ReproService;
import net.conselldemallorca.helium.v3.core.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.v3.core.helper.UsuariActualHelper;
import net.conselldemallorca.helium.v3.core.repository.ExpedientTipusRepository;
import net.conselldemallorca.helium.v3.core.repository.ReproRepository;

import org.json.simple.JSONValue;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servei per gestionar els terminis dels expedients
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service("reproServiceV3")
public class ReproServiceImpl implements ReproService {
	
	@Resource
	private ReproRepository reproRepository;
	@Resource
	private ExpedientTipusRepository expedientTipusRepository;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private UsuariActualHelper usuariActualHelper;

	@Transactional(readOnly=true)
	@Override
	public List<ReproDto> findReprosByUsuariTipusExpedient(Long expedientTipusId) {
		return conversioTipusHelper.convertirList(
				reproRepository.findByUsuariAndExpedientTipusIdOrderByIdDesc(
						usuariActualHelper.getUsuariActual(), 
						expedientTipusId), 
				ReproDto.class);
	}
	
	@Transactional(readOnly=true)
	@Override
	public ReproDto findById(Long id) {
		return conversioTipusHelper.convertir(reproRepository.findOne(id), ReproDto.class);
	}
	
	@Transactional
	@Override
	public ReproDto create(Long expedientTipusId, String nom, Map<String, Object> valors) {
		ExpedientTipus expedientTipus = expedientTipusRepository.findOne(expedientTipusId);
		String valors_s = JSONValue.toJSONString(valors);
		String usuariActual = usuariActualHelper.getUsuariActual();
		Repro repro = new Repro(usuariActual, expedientTipus, nom, valors_s);
		reproRepository.saveAndFlush(repro);
		return conversioTipusHelper.convertir(repro, ReproDto.class);
	}
	
	@Transactional
	@Override
	public void deleteById(Long id) {
		reproRepository.delete(id);
	}
}
