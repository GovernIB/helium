/**
 * 
 */
package net.conselldemallorca.helium.v3.core.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.MesuraTemporalDto;
import net.conselldemallorca.helium.v3.core.api.dto.UsuariPreferenciesDto;
import net.conselldemallorca.helium.v3.core.api.service.AdminService;
import net.conselldemallorca.helium.v3.core.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.v3.core.helper.MesuresTemporalsHelper;
import net.conselldemallorca.helium.v3.core.helper.UsuariActualCacheHelper;
import net.conselldemallorca.helium.v3.core.repository.UsuariPreferenciesRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Servei per gestionar la configuració de l'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class AdminServiceImpl implements AdminService {

	@Resource
	private UsuariPreferenciesRepository usuariPreferenciesRepository;
	
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private UsuariActualCacheHelper usuariActualCacheHelper;
	@Resource
	private MesuresTemporalsHelper mesuresTemporalsHelper;



	@Override
	public List<EntornDto> findEntornAmbPermisReadUsuariActual() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String usuariActual = auth.getName();
		logger.debug("Cercant entorns permesos per a l'usuari actual (codi=" + usuariActual + ")");
		return usuariActualCacheHelper.findEntornsPermesosUsuariActual(auth.getName());
	}

	@Override
	public UsuariPreferenciesDto getPreferenciesUsuariActual() {
		logger.debug("Obtenint preferències per a l'usuari actual");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String usuariActual = auth.getName();
		logger.debug("Obtenint preferències per a l'usuari actual (codi=" + usuariActual + ")");
		return conversioTipusHelper.convertir(
				usuariPreferenciesRepository.findByCodi(usuariActual),
				UsuariPreferenciesDto.class);
	}

	@Override
	public List<MesuraTemporalDto> findMesuresTemporals() {
		logger.debug("Consultant el llistat de les mesures temporals");
		List<MesuraTemporalDto> resposta = new ArrayList<MesuraTemporalDto>();
		for (String clau: mesuresTemporalsHelper.getClausMesures()) {
			MesuraTemporalDto dto = new MesuraTemporalDto();
			dto.setClau(clau);
			dto.setDarrera(mesuresTemporalsHelper.getDarreraMesura(clau));
			dto.setMitja(mesuresTemporalsHelper.getMitja(clau));
			dto.setNumMesures(mesuresTemporalsHelper.getNumMesures(clau));
			resposta.add(dto);
		}
		return resposta;
	}



	private static final Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);

}
