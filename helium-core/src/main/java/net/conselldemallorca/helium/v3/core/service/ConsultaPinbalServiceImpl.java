package net.conselldemallorca.helium.v3.core.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.conselldemallorca.helium.core.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.core.helper.DocumentHelperV3;
import net.conselldemallorca.helium.core.helper.ExpedientHelper;
import net.conselldemallorca.helium.core.helper.PaginacioHelper;
import net.conselldemallorca.helium.core.helper.UsuariActualHelper;
import net.conselldemallorca.helium.core.model.hibernate.Document;
import net.conselldemallorca.helium.core.model.hibernate.PeticioPinbal;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PeticioPinbalDto;
import net.conselldemallorca.helium.v3.core.api.dto.PeticioPinbalFiltreDto;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;
import net.conselldemallorca.helium.v3.core.api.service.ConsultaPinbalService;
import net.conselldemallorca.helium.v3.core.repository.PeticioPinbalRepository;

@Service
public class ConsultaPinbalServiceImpl implements ConsultaPinbalService {

	@Resource private PeticioPinbalRepository peticioPinbalRepository;
	@Resource private ConversioTipusHelper conversioTipusHelper;
	@Resource private PaginacioHelper paginacioHelper;
	@Resource private UsuariActualHelper usuariActualHelper;
	@Resource private ExpedientHelper expedientHelper;
	@Resource private DocumentHelperV3 documentHelperV3;
	
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
		
		PaginaDto<PeticioPinbalDto> pagina = paginacioHelper.toPaginaDto(peticioPinbalRepository.findByFiltrePaginat(
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
		
		for (PeticioPinbalDto peticioPinbal : pagina.getContingut() ) {
			Document document = documentHelperV3.findDocumentPerInstanciaProcesICodi(
					peticioPinbal.getExpedient().getProcessInstanceId(),
					peticioPinbal.getDocument().getCodiDocument());
			String nom = peticioPinbal.getDocument().getCodiDocument();
			if (document != null) {
				nom = document.getNom();
			}
			peticioPinbal.getDocument().setNom(nom);
		}
		
		// Map per document.id i document.nom
//		Map<Long, String> documentsMap = new HashMap<Long, String>();
//		for (PeticioPinbalDto peticioPinbal : pagina.getContingut() ) {
//			String nom = peticioPinbal.getDocument().getCodiDocument();
//			if (documentsMap.containsKey(peticioPinbal.getDocument().getId())) {
//				nom = documentsMap.get(peticioPinbal.getDocument().getId());
//			} else {
//				Document document = documentHelperV3.findDocumentPerInstanciaProcesICodi(
//						peticioPinbal.getExpedient().getProcessInstanceId(),
//						peticioPinbal.getDocument().getCodiDocument());
//				if (document != null) {
//					nom = document.getNom();
//				}
//				documentsMap.put(document.getId(), nom);
//			}
//			peticioPinbal.getDocument().setNom(nom);
//		}
		
		return pagina;
	}

	@Override
	@Transactional(readOnly=true)
	public PeticioPinbalDto findById(Long peticioPinbalId) throws PermisDenegatException {
		PeticioPinbal pi = peticioPinbalRepository.findOne(peticioPinbalId);
		expedientHelper.getExpedientComprovantPermisos(pi.getExpedient().getId(), true, false, false, false);
		return conversioTipusHelper.convertir(pi, PeticioPinbalDto.class);
	}
	
	@Override
	@Transactional(readOnly=true)
	public PeticioPinbalDto findByExpedientAndDocumentStore(Long expedientId, Long documentStoreId) {
		List<PeticioPinbal> pins = peticioPinbalRepository.findByExpedientIdAndDocumentIdOrderByDataPeticioDesc(expedientId, documentStoreId);
		if (pins!=null && pins.size()>0) {
			return conversioTipusHelper.convertir(pins.get(0), PeticioPinbalDto.class);
		} else {
			return null;
		}
	}

	@Override
	@Transactional(readOnly=true)
	public List<PeticioPinbalDto> findConsultesPinbalPerExpedient(Long expedientId) {
		return conversioTipusHelper.convertirList(peticioPinbalRepository.findByExpedientId(expedientId), PeticioPinbalDto.class);
	}
}