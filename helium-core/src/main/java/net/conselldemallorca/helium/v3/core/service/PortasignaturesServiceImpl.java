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
import net.conselldemallorca.helium.core.model.hibernate.Portasignatures;
import net.conselldemallorca.helium.v3.core.api.dto.ConsultesPortafibFiltreDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PortafirmesEstatEnum;
import net.conselldemallorca.helium.v3.core.api.dto.PortasignaturesDto;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;
import net.conselldemallorca.helium.v3.core.api.service.PortasignaturesService;
import net.conselldemallorca.helium.v3.core.repository.PortasignaturesRepository;

@Service
public class PortasignaturesServiceImpl implements PortasignaturesService {

	@Resource private PortasignaturesRepository portasignaturesRepository;
	@Resource private ConversioTipusHelper conversioTipusHelper;
	@Resource private PaginacioHelper paginacioHelper;
	@Resource private ExpedientHelper expedientHelper;
	@Resource private UsuariActualHelper usuariActualHelper;
	@Resource private DocumentHelperV3 documentHelperV3;
	
	@Override
	@Transactional(readOnly=true)
	public PaginaDto<PortasignaturesDto> findAmbFiltrePaginat(PaginacioParamsDto paginacioParams, ConsultesPortafibFiltreDto filtreDto) {
		
		List<Long> entornsPermesos = null;
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
		
		//Intentam revertir la conversió del estat que es produirá a ConversioTipusHelper lin 532 
		Portasignatures.Transicio transicio = null;
		if (filtreDto.getEstat()!=null) {
			switch (filtreDto.getEstat()) {
			case BLOQUEJAT: break;	//Es queda com esta: Filtra per estat=BLOQUEJAT
			case PENDENT: break;	//Es queda com esta: Filtra per estat=PENDENT
			case SIGNAT:
				//Filtra per estat=PROCESSAT, TRANSCIO=SIGNAT
				filtreDto.setEstat(PortafirmesEstatEnum.PROCESSAT);
				transicio = Portasignatures.Transicio.SIGNAT;
				break;		
			case REBUTJAT:
				//Filtra per estat=PROCESSAT, TRANSCIO=REBUTJAT
				filtreDto.setEstat(PortafirmesEstatEnum.PROCESSAT);
				transicio = Portasignatures.Transicio.REBUTJAT;				
				break;
			case PROCESSAT: break;	//Es queda com esta: Filtra per estat=PROCESSAT
			case CANCELAT: break;	//Es queda com esta: Filtra per estat=CANCELAT
			case ERROR: break;		//Es queda com esta: Filtra per estat=ERROR
			case ESBORRAT: break;	//Es queda com esta: Filtra per estat=ESBORRAT
			default:
				break;
			}
		}
		
		 PaginaDto<PortasignaturesDto> pagina = paginacioHelper.toPaginaDto(
				portasignaturesRepository.findByFiltrePaginat(
						entornsPermesos == null,
						entornsPermesos,
						filtreDto.getTipusId() == null,
						filtreDto.getTipusId(),
						filtreDto.getExpedientId() == null,
						filtreDto.getExpedientId(),
						filtreDto.getNumeroExpedient() == null,
						filtreDto.getNumeroExpedient(),
						filtreDto.getDocumentNom() == null,
						filtreDto.getDocumentNom(),
						filtreDto.getEstat() == null,
						filtreDto.getEstat(),
						transicio == null,
						transicio,
						filtreDto.getDataPeticioIni() == null,
						filtreDto.getDataPeticioIni(),
						filtreDto.getDataPeticioFi() == null,
						filtreDto.getDataPeticioFi(),
						paginacioHelper.toSpringDataPageable(paginacioParams)),
						PortasignaturesDto.class);
		
		for (PortasignaturesDto pf : pagina.getContingut() ) {
			
			if (filtreDto.getEstat()!=null) {
				pf.setEstat(filtreDto.getEstat().toString());
			}
			
			ExpedientDocumentDto document = documentHelperV3.findDocumentPerDocumentStoreId(
					pf.getProcessInstanceId(),
					pf.getDocumentStoreId());
			String nom = pf.getDocumentNom();
			if (document != null) {
				nom = document.getDocumentNom();
			}
			pf.setDocumentNom(nom);
			pf.setDocumentUUID(document.getArxiuUuid());
		}
		 
		 return pagina;
	}

	@Override
	@Transactional(readOnly=true)
	public PortasignaturesDto findById(Long portafirmesId) throws PermisDenegatException {
		Portasignatures ps = portasignaturesRepository.findById(portafirmesId);
		expedientHelper.getExpedientComprovantPermisos(ps.getExpedient().getId(), true, false, false, false);
		PortasignaturesDto resultat = conversioTipusHelper.convertir(ps, PortasignaturesDto.class);
		
		ExpedientDocumentDto document = documentHelperV3.findDocumentPerDocumentStoreId(
				resultat.getProcessInstanceId(),
				resultat.getDocumentStoreId());
		String nom = resultat.getDocumentNom();
		if (document != null) {
			nom = document.getDocumentNom();
		}
		resultat.setDocumentNom(nom);
		resultat.setDocumentUUID(document.getArxiuUuid());
		
		return resultat;
	}
}