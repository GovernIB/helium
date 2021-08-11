package es.caib.helium.logic.ms;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.caib.helium.client.expedient.expedient.ExpedientClientService;
import es.caib.helium.client.expedient.expedient.enums.ExpedientEstatTipusEnum;
import es.caib.helium.client.expedient.expedient.model.ConsultaExpedientDades;
import es.caib.helium.client.model.PagedList;
import es.caib.helium.logic.helper.MsHelper;
import es.caib.helium.logic.helper.PaginacioHelper;
import es.caib.helium.logic.intf.dto.EntornDto;
import es.caib.helium.logic.intf.dto.EstatDto;
import es.caib.helium.logic.intf.dto.ExpedientDto;
import es.caib.helium.logic.intf.dto.ExpedientDto.EstatTipusDto;
import es.caib.helium.logic.intf.dto.ExpedientTipusDto;
import es.caib.helium.logic.intf.dto.PaginaDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.service.EntornService;
import es.caib.helium.logic.intf.service.ExpedientTipusService;

/** Bean per interactuar amb el MS d'expedients i tasques..
 * 
 * @author Limit Tecnologies <limit@limit.es>
 *
 */
@Component
public class ExpedientMs {
	
	@Autowired
	private ExpedientClientService expedientClientService;

	@Autowired
	private EntornService entornService;
	
	@Resource
	private PaginacioHelper paginacioHelper;

	@Autowired
	private ExpedientTipusService expedientTipusService;

	@Autowired
	private MsHelper msHelper;

	/** Crida a la consulta d'expedients per filtre
	 */
	public PaginaDto<ExpedientDto> expedientFindByFiltre(
            Long entornId,
            String actorId,
            Collection<Long> tipusIdPermesos,
            String titol,
            String numero,
            Long tipusId,
            Date dataCreacioInici,
            Date dataCreacioFi,
			Date dataFiInici,
			Date dataFiFi,
            Long estatId,
            Double geoPosX,
            Double geoPosY,
            String geoReferencia,
            boolean nomesIniciats,
            boolean nomesFinalitzats,
            boolean mostrarAnulats,
            boolean mostrarNomesAnulats,
            boolean nomesAlertes,
            boolean nomesErrors,
            boolean nomesTasquesPersonals,
            boolean nomesTasquesGrup,
            boolean nomesTasquesMeves,
            PaginacioParamsDto paginacioParams,
            boolean nomesCount) {
		
		ConsultaExpedientDades consultaExpedientDades = new ConsultaExpedientDades(entornId, geoReferencia, actorId,
				tipusIdPermesos, titol, numero, tipusId, dataCreacioInici, dataCreacioFi, dataFiInici, dataFiFi,
				estatId, geoPosX, geoPosY, geoReferencia, nomesIniciats, nomesFinalitzats, mostrarAnulats,
				mostrarNomesAnulats, nomesAlertes, nomesErrors, nomesTasquesPersonals, nomesTasquesGrup,
				nomesTasquesMeves, nomesCount);
		consultaExpedientDades.setPage(paginacioParams.getPaginaNum());
		consultaExpedientDades.setSize(paginacioParams.getPaginaTamany());
		consultaExpedientDades.setSort(msHelper.getSortList(paginacioParams));
		
		PagedList<es.caib.helium.client.expedient.expedient.model.ExpedientDto> page  = this.expedientClientService.findExpedientsAmbFiltrePaginatV1(consultaExpedientDades);
		
		PaginaDto<ExpedientDto> pagina = paginacioHelper.toPaginaDto(
				page,
				ExpedientDto.class);
		
		this.completaDto(page.getContent(), pagina.getContingut());
		
		return pagina;                
	}
	
	/** Consulta els dto's dels entorns i tipus d'expedients i els informa al llistat de sortida.
	 * 
	 * @param content
	 * @param contingut
	 */
	private void completaDto(
			List<es.caib.helium.client.expedient.expedient.model.ExpedientDto> expedientsMs,
			List<ExpedientDto> expedientsDtos) {
		
		EntornDto entorn;
		Map<Long, EntornDto> entorns = new HashMap<Long, EntornDto>();
		ExpedientTipusDto expedientTipus;
		Map<Long, ExpedientTipusDto> expedientsTipus = new HashMap<Long, ExpedientTipusDto>();
		EstatDto estat;
		Map<Long, EstatDto> estats = new HashMap<Long, EstatDto>();
		es.caib.helium.client.expedient.expedient.model.ExpedientDto expedientMs;
		ExpedientDto expedientDto;
		for (int i = 0; i < expedientsMs.size(); i++) {
			expedientMs = expedientsMs.get(i);
			expedientDto = expedientsDtos.get(i);
			// expedient tipus
			if (expedientsTipus.containsKey(expedientMs.getExpedientTipusId())) {
				expedientTipus = expedientsTipus.get(expedientMs.getExpedientTipusId());
			} else {
				expedientTipus = expedientTipusService.findAmbId(expedientMs.getExpedientTipusId());
				expedientsTipus.put(expedientMs.getExpedientTipusId(), expedientTipus);
			}
			expedientDto.setTipus(expedientTipus);
			// entorn
			if (entorns.containsKey(expedientMs.getEntornId())) {
				entorn = entorns.get(expedientMs.getEntornId());
			} else {
				entorn = entornService.findOne(expedientMs.getEntornId());
				entorns.put(expedientMs.getEntornId(), entorn);
			}
			expedientDto.setEntorn(entorn);
			// estat
			if (ExpedientEstatTipusEnum.CUSTOM.equals(expedientMs.getEstatTipus()) ) {
				if (estats.containsKey(expedientMs.getEstatId())) {
					estat = estats.get(expedientMs.getEstatId());
				} else {
					estat = expedientTipusService.estatFindAmbId(expedientMs.getExpedientTipusId(), expedientMs.getEstatId());
					estats.put(expedientMs.getEstatId(), estat);
				}
				expedientDto.setEstat(estat);
			}
			expedientDto.setEstatTipus(EstatTipusDto.valueOf(expedientMs.getEstatTipus().toString()));
		}
	}
	
	/** Crida a la consulta d'identificadors d'expedients per filtre
	 */
	public PaginaDto<Long> expedientFindIdsByFiltre(
            Long entornId,
            String actorId,
            Collection<Long> tipusIdPermesos,
            String titol,
            String numero,
            Long tipusId,
            Date dataCreacioInici,
            Date dataCreacioFi,
			Date dataFiInici,
			Date dataFiFi,
            Long estatId,
            Double geoPosX,
            Double geoPosY,
            String geoReferencia,
            boolean nomesIniciats,
            boolean nomesFinalitzats,
            boolean mostrarAnulats,
            boolean mostrarNomesAnulats,
            boolean nomesAlertes,
            boolean nomesErrors,
            boolean nomesTasquesPersonals,
            boolean nomesTasquesGrup,
            boolean nomesTasquesMeves,
            PaginacioParamsDto paginacioParams,
            boolean nomesCount) {
		
		ConsultaExpedientDades consultaExpedientDades = new ConsultaExpedientDades(entornId, geoReferencia, actorId,
				tipusIdPermesos, titol, numero, tipusId, dataCreacioInici, dataCreacioFi, dataFiInici, dataFiFi,
				estatId, geoPosX, geoPosY, geoReferencia, nomesIniciats, nomesFinalitzats, mostrarAnulats,
				mostrarNomesAnulats, nomesAlertes, nomesErrors, nomesTasquesPersonals, nomesTasquesGrup,
				nomesTasquesMeves, nomesCount);
		consultaExpedientDades.setPage(paginacioParams.getPaginaNum());
		consultaExpedientDades.setSize(paginacioParams.getPaginaTamany());
		consultaExpedientDades.setSort(msHelper.getSortList(paginacioParams));
		

		PagedList<Long> page  = this.expedientClientService.findExpedientsIdsAmbFiltrePaginatV1(consultaExpedientDades);

		PaginaDto<Long> pagina = paginacioHelper.toPaginaDto(
				page,
				Long.class);
		return pagina;                
	}
	
}
