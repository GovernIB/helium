/**
 * 
 */
package es.caib.helium.logic.service;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import es.caib.helium.logic.helper.ConversioTipusHelper;
import es.caib.helium.logic.intf.dto.AlertaDto;
import es.caib.helium.logic.intf.exception.NoTrobatException;
import es.caib.helium.logic.intf.service.AlertaService;
import es.caib.helium.persist.entity.Alerta;
import es.caib.helium.persist.repository.AlertaRepository;

/**
 * Servei per gestionar els tokens dels expedients
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class AlertaServiceImpl implements AlertaService {

	@Resource
	private AlertaRepository alertaRepository;

	@Resource
	private ConversioTipusHelper conversioTipusHelper;

	@Override
	public AlertaDto marcarLlegida(Long alertaId) {
		Alerta alerta = alertaRepository.findById(alertaId)
				.orElseThrow(() -> new NoTrobatException(Alerta.class, alertaId));

		alerta.setDataLectura(new Date());
		alertaRepository.save(alerta);

		return conversioTipusHelper.convertir(alerta, AlertaDto.class);
	}

	@Override
	public AlertaDto marcarNoLlegida(Long alertaId) {
		Alerta alerta = alertaRepository.findById(alertaId)
				.orElseThrow(() -> new NoTrobatException(Alerta.class, alertaId));

		alerta.setDataLectura(null);
		alertaRepository.save(alerta);

		return conversioTipusHelper.convertir(alerta, AlertaDto.class);
	}

	@Override
	public AlertaDto marcarEsborrada(Long alertaId) {
		Alerta alerta = alertaRepository.findById(alertaId)
				.orElseThrow(() -> new NoTrobatException(Alerta.class, alertaId));

		alerta.setDataEliminacio(new Date());
		alertaRepository.save(alerta);

		return conversioTipusHelper.convertir(alerta, AlertaDto.class);
	}
}
