/**
 * 
 */
package es.caib.helium.service;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import es.caib.helium.commons.dto.AlertaDto;
import es.caib.helium.commons.exception.NoTrobatException;
import es.caib.helium.logic.intf.service.AlertaService;
import es.caib.helium.persistence.entity.Alerta;
import es.caib.helium.persistence.repository.AlertaRepository;
import es.caib.helium.service.helper.ConversioTipusHelper;

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
		Alerta alerta = alertaRepository.findById(alertaId).orElse(null);
		
		if (alerta == null)
			throw new NoTrobatException(Alerta.class, alertaId);
		
		alerta.setDataLectura(new Date());
		alertaRepository.save(alerta);
		
		return conversioTipusHelper.convertir(alerta, AlertaDto.class);
	}
	
	@Override
	public AlertaDto marcarNoLlegida(Long alertaId) {
		Alerta alerta = alertaRepository.findById(alertaId).orElse(null);
		
		if (alerta == null)
			throw new NoTrobatException(Alerta.class, alertaId);
		
		alerta.setDataLectura(null);
		alertaRepository.save(alerta);
		
		return conversioTipusHelper.convertir(alerta, AlertaDto.class);
	}
	
	@Override
	public AlertaDto marcarEsborrada(Long alertaId) {
		Alerta alerta = alertaRepository.findById(alertaId).orElse(null);
		
		if (alerta == null)
			throw new NoTrobatException(Alerta.class, alertaId);
		
		alerta.setDataEliminacio(new Date());
		alertaRepository.save(alerta);
		
		return conversioTipusHelper.convertir(alerta, AlertaDto.class);
	}	
}
