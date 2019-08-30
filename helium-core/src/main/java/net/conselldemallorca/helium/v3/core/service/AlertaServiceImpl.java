/**
 * 
 */
package net.conselldemallorca.helium.v3.core.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import net.conselldemallorca.helium.core.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.core.helperv26.MesuresTemporalsHelper;
import net.conselldemallorca.helium.core.model.hibernate.Alerta;
import net.conselldemallorca.helium.v3.core.api.dto.AlertaDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.service.AlertaService;
import net.conselldemallorca.helium.v3.core.repository.AlertaRepository;

/**
 * Implementació del serveide gestió d'alertes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class AlertaServiceImpl implements AlertaService {
	
	@Autowired
	private AlertaRepository alertaRepository;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private MesuresTemporalsHelper mesuresTemporalsHelper;

	@Override
	@Cacheable(value="alertaCache")
	public int countActivesAmbEntornIUsuari(
			Long entornId,
			String usuariCodi,
			boolean llegides,
			boolean noLlegides) {
		String mesuraNom = "Obtenir count alertes " + llegides + "," + noLlegides;
		mesuresTemporalsHelper.mesuraIniciar(mesuraNom, "consulta");
		int count = alertaRepository.countAmbEntornAndDestinatari(
				entornId,
				usuariCodi,
				llegides,
				noLlegides);
		mesuresTemporalsHelper.mesuraCalcular(mesuraNom, "consulta");
		return count;
	}

	@Override
	@CacheEvict(value = "alertaCache", allEntries=true)
	public AlertaDto marcarLlegida(Long alertaId) {
		Alerta alerta = alertaRepository.findOne(alertaId);
		
		if (alerta == null)
			throw new NoTrobatException(Alerta.class, alertaId);
		
		alerta.setDataLectura(new Date());
		alertaRepository.save(alerta);
		
		return conversioTipusHelper.convertir(alerta, AlertaDto.class);
	}
	
	@Override
	@CacheEvict(value = "alertaCache", allEntries=true)
	public AlertaDto marcarNoLlegida(Long alertaId) {
		Alerta alerta = alertaRepository.findOne(alertaId);
		
		if (alerta == null)
			throw new NoTrobatException(Alerta.class, alertaId);
		
		alerta.setDataLectura(null);
		alertaRepository.save(alerta);
		
		return conversioTipusHelper.convertir(alerta, AlertaDto.class);
	}
	
	@Override
	@CacheEvict(value = "alertaCache", allEntries=true)
	public AlertaDto marcarEsborrada(Long alertaId) {
		Alerta alerta = alertaRepository.findOne(alertaId);
		
		if (alerta == null)
			throw new NoTrobatException(Alerta.class, alertaId);
		
		alerta.setDataEliminacio(new Date());
		alertaRepository.save(alerta);
		
		return conversioTipusHelper.convertir(alerta, AlertaDto.class);
	}

}
