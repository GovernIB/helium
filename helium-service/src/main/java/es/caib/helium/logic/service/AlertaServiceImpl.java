/**
 * 
 */
package es.caib.helium.logic.service;

import es.caib.helium.logic.helper.ConversioTipusServiceHelper;
import es.caib.helium.logic.intf.dto.AlertaDto;
import es.caib.helium.logic.intf.exception.NoTrobatException;
import es.caib.helium.logic.intf.service.AlertaService;
import es.caib.helium.persist.entity.Alerta;
import es.caib.helium.persist.repository.AlertaRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

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
	private ConversioTipusServiceHelper conversioTipusServiceHelper;

	@Override
	public AlertaDto marcarLlegida(Long alertaId) {
		Alerta alerta = alertaRepository.findById(alertaId)
				.orElseThrow(() -> new NoTrobatException(Alerta.class, alertaId));

		alerta.setDataLectura(new Date());
		alertaRepository.save(alerta);

		return conversioTipusServiceHelper.convertir(alerta, AlertaDto.class);
	}

	@Override
	public AlertaDto marcarNoLlegida(Long alertaId) {
		Alerta alerta = alertaRepository.findById(alertaId)
				.orElseThrow(() -> new NoTrobatException(Alerta.class, alertaId));

		alerta.setDataLectura(null);
		alertaRepository.save(alerta);

		return conversioTipusServiceHelper.convertir(alerta, AlertaDto.class);
	}

	@Override
	public AlertaDto marcarEsborrada(Long alertaId) {
		Alerta alerta = alertaRepository.findById(alertaId)
				.orElseThrow(() -> new NoTrobatException(Alerta.class, alertaId));

		alerta.setDataEliminacio(new Date());
		alertaRepository.save(alerta);

		return conversioTipusServiceHelper.convertir(alerta, AlertaDto.class);
	}
}
