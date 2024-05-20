package net.conselldemallorca.helium.v3.core.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import net.conselldemallorca.helium.core.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.core.helper.DocumentHelperV3;
import net.conselldemallorca.helium.core.helper.EntornHelper;
import net.conselldemallorca.helium.core.helper.ExceptionHelper;
import net.conselldemallorca.helium.core.helper.PaginacioHelper;
import net.conselldemallorca.helium.core.helper.PluginHelper;
import net.conselldemallorca.helium.core.helper.UsuariActualHelper;
import net.conselldemallorca.helium.core.model.hibernate.PeticioPinbal;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmToken;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PeticioPinbalDto;
import net.conselldemallorca.helium.v3.core.api.dto.PeticioPinbalEstatEnum;
import net.conselldemallorca.helium.v3.core.api.dto.PeticioPinbalFiltreDto;
import net.conselldemallorca.helium.v3.core.api.dto.ScspRespostaPinbal;
import net.conselldemallorca.helium.v3.core.api.service.ConsultaPinbalService;
import net.conselldemallorca.helium.v3.core.repository.PeticioPinbalRepository;

@Service
public class ConsultaPinbalServiceImpl implements ConsultaPinbalService {

	@Resource private PeticioPinbalRepository peticioPinbalRepository;
	@Resource private ConversioTipusHelper conversioTipusHelper;
	@Resource private EntornHelper entornHelper;
	@Resource private PaginacioHelper paginacioHelper;
	@Resource private UsuariActualHelper usuariActualHelper;
	@Resource private PluginHelper pluginHelper;
	@Resource private JbpmHelper jbpmDao;
	@Resource private ExceptionHelper exceptionHelper;
	@Resource private DocumentHelperV3 documentHelper;
	
	@Override
	@Transactional(readOnly=true)
	public PaginaDto<PeticioPinbalDto> findAmbFiltrePaginat(PaginacioParamsDto paginacioParams, PeticioPinbalFiltreDto filtreDto) {
		
		List<Long> entornsPermesos = null;
		//Si el filtre ve del expedient, no filtrar per permisos de administració del entorn
		if (!filtreDto.isFromExpedient()) {
			//Si la cridada ve desde el llistat de peticions del menu Admin, sí que filtram
			if (!usuariActualHelper.isAdministrador()) {
				List<EntornDto> entornsAdminUsuari = usuariActualHelper.findEntornsActiusPermisAdmin();
				entornsPermesos = new ArrayList<Long>();
				if (entornsAdminUsuari==null || entornsAdminUsuari.size()==0) {
					entornsPermesos.add(0l);
				} else {
					for (EntornDto e: entornsAdminUsuari) {
						entornsPermesos.add(e.getId());
					}
				}
			}
		}
		
		return paginacioHelper.toPaginaDto(peticioPinbalRepository.findByFiltrePaginat(
				entornsPermesos == null,
				entornsPermesos,
				filtreDto.getTipusId() == null,
				filtreDto.getTipusId(),
				filtreDto.getExpedientId() == null,
				filtreDto.getExpedientId(),
				filtreDto.getNumeroExpedient() == null,
				filtreDto.getNumeroExpedient(),
				filtreDto.getProcediment() == null,
				filtreDto.getProcediment(),
				filtreDto.getUsuari() == null,
				filtreDto.getUsuari(),
				filtreDto.getEstat() == null,
				filtreDto.getEstat(),
				filtreDto.getDataPeticioIni() == null,
				filtreDto.getDataPeticioIni(),
				filtreDto.getDataPeticioFi() == null,
				filtreDto.getDataPeticioFi(),
				(paginacioParams.getFiltre() == null || "".equals(paginacioParams.getFiltre())),
				paginacioParams.getFiltre(),
				paginacioHelper.toSpringDataPageable(paginacioParams)), PeticioPinbalDto.class);

	}

	@Override
	@Transactional(readOnly=true)
	public PeticioPinbalDto findById(Long peticioPinbalId) {
		return conversioTipusHelper.convertir(peticioPinbalRepository.findOne(peticioPinbalId), PeticioPinbalDto.class);
	}

	@Override
	@Transactional(readOnly=true)
	public List<PeticioPinbalDto> findConsultesPinbalPerExpedient(Long expedientId) {
		return conversioTipusHelper.convertirList(peticioPinbalRepository.findByExpedientId(expedientId), PeticioPinbalDto.class);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public ScspRespostaPinbal tractamentPeticioAsincronaPendentPinbal(Long peticioPinbalId) {
		
		PeticioPinbal pi = peticioPinbalRepository.findOne(peticioPinbalId);
		ScspRespostaPinbal resultat = new ScspRespostaPinbal();
		
		try {
			resultat = pluginHelper.consultaEstatPeticioPinbal(pi.getPinbalId());
			if (PeticioPinbalEstatEnum.TRAMITADA.equals(resultat.getEstatAsincron())) {
				documentHelper.crearActualitzarDocument(
						null,
						pi.getDocument().getProcessInstanceId(),
						pi.getDocument().getCodiDocument(),
						Calendar.getInstance().getTime(),
						resultat.getJustificant().getNom(),
						resultat.getJustificant().getContingut(),
						null,
						null,
						null,
						null);
			}
			
			if (pi.getTokenId()!=null) {
				JbpmToken token = jbpmDao.getTokenById(pi.getTokenId().toString());
				if (token!=null) {
					jbpmDao.signalToken(pi.getTokenId().longValue(), "ok");
				}
			}
			
		} catch (Exception ex) {
			resultat.setEstatAsincron(PeticioPinbalEstatEnum.ERROR_PROCESSANT);
			String error = ex.getMessage() + ": " + exceptionHelper.getMissageFinalCadenaExcepcions(ex);
			if (error!=null && error.length()>4000) {
				error = error.substring(0, 4000);
			}
			resultat.setErrorProcessament(error);
		}
		
		//Actualitzam la registre de la BBDD
		pi.setEstat(resultat.getEstatAsincron());
		pi.setErrorProcessament(resultat.getErrorProcessament());
		if (pi.getDataProcessamentPrimer()==null) {
			pi.setDataProcessamentPrimer(Calendar.getInstance().getTime());
		} else {
			pi.setDataProcessamentDarrer(Calendar.getInstance().getTime());
		}
		
		//Retornam el objecte amb la informació per si es vol utilitzar per mostrar-la per pantalla
		return resultat;
	}
}
