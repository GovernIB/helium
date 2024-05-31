package net.conselldemallorca.helium.v3.core.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.security.core.context.SecurityContextHolder;
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
			//Si ets admin, no es filtra per entorn seleccionat
			if (usuariActualHelper.isAdministrador()) {
				filtreDto.setEntornId(null);
				List<EntornDto> entornsActiusAdmin = usuariActualHelper.findEntornsActiusPermesos(
						SecurityContextHolder.getContext().getAuthentication().getName());
				
				if (entornsActiusAdmin!=null && entornsActiusAdmin.size()>0) {
					entornsPermesos = new ArrayList<Long>();
					for (EntornDto entornDto: entornsActiusAdmin) {
						entornsPermesos.add(entornDto.getId());
					}
				}
			} else {
				//Si no ets admin, es filtra per l'entonr del filtre, que es de la sessió.
				//JA s'haurá comprovat al controller que tens permisos de administració sobre el entorn
				entornsPermesos = new ArrayList<Long>();
				entornsPermesos.add(filtreDto.getEntornId());
			}
		}
		
		paginacioParams.canviaCamp("expedient.identificador", "expedient.numero");
		
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
				dataInicial == null,
				dataInicial,
				dataFinal == null,
				dataFinal,
				(paginacioParams.getFiltre() == null || "".equals(paginacioParams.getFiltre())),
				paginacioParams.getFiltre(),
				paginacioHelper.toSpringDataPageable(paginacioParams)), PeticioPinbalDto.class);
		
		//HttpMessageNotWritableException: Could not write JSON: Infinite recursion (StackOverflowError) si el docStore forma part de un zip.
		for (PeticioPinbalDto peticioPinbal : pagina.getContingut() ) {
			if (peticioPinbal.getDocument()!=null) {
				peticioPinbal.getDocument().setZips(null);
				peticioPinbal.getDocument().setCodiDocument(null);
			}
		}
		
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