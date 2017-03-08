package net.conselldemallorca.helium.v3.core.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.conselldemallorca.helium.core.helper.NotificacioHelper;
import net.conselldemallorca.helium.core.model.hibernate.Remesa;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentEnviamentEstatEnumDto;
import net.conselldemallorca.helium.v3.core.api.service.NotificacioService;
import net.conselldemallorca.helium.v3.core.repository.RemesaRepository;

/**
 * Servei per gestionar els terminis dels expedients
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service("notificacioServiceV3")
public class NotificacioServiceImpl implements NotificacioService {
	
	@Resource
	private NotificacioHelper notificacioHelper;
	@Resource
	private RemesaRepository remesaRepository;

	@Override
	@Transactional
	public void reenviarRemesa(Long remesaId, Long expedientTipusId) throws Exception {
		Remesa remesa = remesaRepository.findOne(remesaId);
		
		List<Long> expedientIds = remesaRepository.findExpedientIdsByRemesa(remesa);
		
		notificacioHelper.enviarRemesa(
				remesa.getCodi(), 
				remesa.getDataEmisio(), 
				remesa.getDataPrevistaDeposit(), 
				expedientTipusId, 
				expedientIds);
	}
	
	@Override
	@Transactional
	public void refrescarRemesa(Long remesaId) throws Exception {
		Remesa remesa = remesaRepository.findOne(remesaId);
		
		if (remesa.getEstat() == DocumentEnviamentEstatEnumDto.ENVIAT)
			notificacioHelper.comprovarRemesaEnviada(remesa);
		else if (remesa.getEstat() == DocumentEnviamentEstatEnumDto.VALIDAT)
			notificacioHelper.comprovarRemesaValidada(remesa);
	}
}
