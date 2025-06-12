package net.conselldemallorca.helium.v3.core.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.conselldemallorca.helium.core.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.core.helper.DocumentHelperV3;
import net.conselldemallorca.helium.core.helper.ExpedientHelper;
import net.conselldemallorca.helium.core.helper.PaginacioHelper;
import net.conselldemallorca.helium.core.helper.UsuariActualHelper;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.Portasignatures;
import net.conselldemallorca.helium.v3.core.api.dto.ConsultesPortafibFiltreDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PortafirmesEstatEnum;
import net.conselldemallorca.helium.v3.core.api.dto.PortasignaturesDto;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientTipusService;
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
	@Resource private ExpedientTipusService expedientTipusService;
	
	@Override
	@Transactional(readOnly=true)
	public PaginaDto<PortasignaturesDto> findAmbFiltrePaginat(PaginacioParamsDto paginacioParams, ConsultesPortafibFiltreDto filtreDto) {
		
		Long entornActualId = null;
		List<Long> tipusPermesos = null;
		//Si ets admin, no es filtra per entorn seleccionat
		if (!usuariActualHelper.isAdministrador()) {
			
			/**
			 * Si ets admin, no es filtra per entorn seleccionat ni per tipus permesos
			 * Pero si has seleccionat un expedient tipus al filtre, si que es té en compte
			 */
			//Si no ets admin, es filtra per l'entorn del filtre, que es de la sessió.
			entornActualId = filtreDto.getEntornId();
			//També es filtra per els tipus de expedient amb permis admin
			tipusPermesos = new ArrayList<Long>();
			if (entornActualId!=null) {
				List<ExpedientTipusDto> tipusPermisAdmin = expedientTipusService.findAmbEntornPermisAdmin(entornActualId);
				if (tipusPermisAdmin!=null) {
					for (ExpedientTipusDto etDto: tipusPermisAdmin) {
						tipusPermesos.add(etDto.getId());
					}
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
		
		Date dataInicial = null;
		if (filtreDto.getDataPeticioIni() != null) {
			// Corregeix la data final per arribar a les 00:00:00h del dia següent.
			Calendar c = new GregorianCalendar();
			c.setTime(filtreDto.getDataPeticioIni());
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MILLISECOND, 0);
			dataInicial = c.getTime();
		}
		
		Date dataFinal = null;
		if (filtreDto.getDataPeticioFi() != null) {
			// Corregeix la data final per arribar a les 00:00:00h del dia següent.
			Calendar c = new GregorianCalendar();
			c.setTime(filtreDto.getDataPeticioFi());
			c.set(Calendar.HOUR_OF_DAY, 23);
			c.set(Calendar.MINUTE, 59);
			c.set(Calendar.SECOND, 59);
			c.set(Calendar.MILLISECOND, 999);
			dataFinal = c.getTime();
		}
		
		paginacioParams.canviaCamp("tipusExpedientNom", "expedient.tipus.nom");
		paginacioParams.canviaCamp("expedientIdentificador", "expedient.numero");
		
		 PaginaDto<PortasignaturesDto> pagina = paginacioHelper.toPaginaDto(
				portasignaturesRepository.findByFiltrePaginat(
						entornActualId == null,
						entornActualId,
						(tipusPermesos==null || tipusPermesos.size()==0),
						tipusPermesos,
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
						dataInicial == null,
						dataInicial,
						dataFinal == null,
						dataFinal,
						filtreDto.getDocumentId() == null,
						filtreDto.getDocumentId(),
						paginacioHelper.toSpringDataPageable(paginacioParams)),
						PortasignaturesDto.class);
		
		for (PortasignaturesDto pf : pagina.getContingut() ) {
			
			if (filtreDto.getEstat()!=null) {
				pf.setEstat(filtreDto.getEstat().toString());
			}
			Expedient expedient = expedientHelper.getExpedientComprovantPermisos(pf.getExpedientId(), true, false, false, false);
			ExpedientDocumentDto document = documentHelperV3.findDocumentPerDocumentStoreId(
					pf.getProcessInstanceId(),
					pf.getDocumentStoreId(),
					expedient.isArxiuActiu());
			String nom = pf.getDocumentNom();
			if (document != null) {
//				nom = document.getDocumentNom();
				nom = document.getArxiuNom();
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
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(ps.getExpedient().getId(), true, false, false, false);
		PortasignaturesDto resultat = conversioTipusHelper.convertir(ps, PortasignaturesDto.class);
		
		ExpedientDocumentDto document = documentHelperV3.findDocumentPerDocumentStoreId(
				resultat.getProcessInstanceId(),
				resultat.getDocumentStoreId(),
				expedient.isArxiuActiu());
		String nom = resultat.getDocumentNom();
		if (document != null) {
			nom = document.getDocumentNom();
		}
		resultat.setDocumentNom(nom);
		resultat.setDocumentUUID(document.getArxiuUuid());
		
		return resultat;
	}
}