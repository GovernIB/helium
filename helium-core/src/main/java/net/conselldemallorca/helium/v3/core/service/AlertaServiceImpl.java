/**
 * 
 */
package net.conselldemallorca.helium.v3.core.service;

import java.util.Date;

import javax.annotation.Resource;

import net.conselldemallorca.helium.core.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.core.model.hibernate.Alerta;
import net.conselldemallorca.helium.v3.core.api.dto.AlertaDto;
import net.conselldemallorca.helium.v3.core.api.service.AlertaService;
import net.conselldemallorca.helium.v3.core.repository.AlertaRepository;

import org.springframework.stereotype.Service;

/**
 * Servei per gestionar els tokens dels expedients
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class AlertaServiceImpl implements AlertaService{
	
	@Resource
	private AlertaRepository alertaRepository;
	
	@Resource
	private ConversioTipusHelper conversioTipusHelper;

	@Override
	public AlertaDto marcarLlegida(Long alertaId) {
		Alerta alerta = alertaRepository.findOne(alertaId);
		
		alerta.setDataLectura(new Date());
		alertaRepository.save(alerta);
		
		return conversioTipusHelper.convertir(alerta, AlertaDto.class);
	}
	
	@Override
	public AlertaDto marcarNoLlegida(Long alertaId) {
		Alerta alerta = alertaRepository.findOne(alertaId);
		
		alerta.setDataLectura(null);
		alertaRepository.save(alerta);
		
		return conversioTipusHelper.convertir(alerta, AlertaDto.class);
	}
	
	@Override
	public AlertaDto marcarEsborrada(Long alertaId) {
		Alerta alerta = alertaRepository.findOne(alertaId);
		
		alerta.setDataEliminacio(new Date());
		alertaRepository.save(alerta);
		
		return conversioTipusHelper.convertir(alerta, AlertaDto.class);
	}
}
