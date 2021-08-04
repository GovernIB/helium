package es.caib.helium.logic.ms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.client.RestClientException;

import es.caib.helium.client.domini.domini.DominiClient;
import es.caib.helium.client.domini.domini.model.ResultatDomini;
import es.caib.helium.client.domini.entorn.EntornClient;
import es.caib.helium.client.domini.expedientTipus.ExpedientTipusClient;
import es.caib.helium.client.model.PagedList;
import es.caib.helium.logic.helper.ConversioTipusServiceHelper;
import es.caib.helium.logic.helper.PaginacioHelper;
import es.caib.helium.logic.intf.dto.ConsultaDominiDto;
import es.caib.helium.logic.intf.dto.DominiDto;
import es.caib.helium.logic.intf.dto.PaginaDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto.OrdreDireccioDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto.OrdreDto;
import es.caib.helium.logic.intf.extern.domini.FilaResultat;

/** Bean per interactuar amb el Micro Servei de Dominis. Encapsula les crides al client
 * de l'API REST pel disseny i consulta dels dominis.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 *
 */
@Component
public class DominiMs {
	
	/** Client MS dominis */
	@Autowired
	private DominiClient dominiClient;
	
	/** Client MS dominis cerca per tipus d'expedient. */
	@Autowired
	private ExpedientTipusClient expedientTipusClient;
	
	/** Client MS dominis cerca per entorn. */
	@Autowired
	private EntornClient entornClient;
	
	@Resource
	private ConversioTipusServiceHelper conversioTipusServiceHelper;
	@Resource
	private PaginacioHelper paginacioHelper;

	/// Mètode de Disseny

	/** Cerca paginada amb filtre per nom o codi
	 * 
	 * @param entornId
	 * @param filtre
	 * @param expedientTipusId
	 * @param expedientTipusPareId
	 * @param paginacioParams
	 * @return
	 */
	public PaginaDto<DominiDto> findByFiltrePaginat(
			Long entornId, 
			String filtre, 
			Long expedientTipusId, 
			Long expedientTipusPareId,
			PaginacioParamsDto paginacioParams) {
		
		String filtreRsql = "codi=ic=*" + filtre +"* or nom=ic=*" + filtre ;
		
		PagedList<es.caib.helium.client.domini.entorn.model.DominiDto> page  = this.dominiClient.listDominisV1(
				entornId, 
				filtreRsql, 
				expedientTipusId, 
				expedientTipusPareId, 
				paginacioHelper.toSpringDataPageable(paginacioParams), 
				this.getSort(paginacioParams));

		PaginaDto<DominiDto> pagina = this.toPaginaDto(
				page,
				DominiDto.class);

		return pagina; 
	}
	
	/** Troba el domini per codi pel tipus d'expedient o per l'entorn si el tipus
	 * d'expedient és null
	 * 
	 * @param entornId
	 * @param expedientTipusId
	 * @param codi
	 * @return
	 */
	public DominiDto findAmbCodi(Long entornId, Long expedientTipusId, String codi) {
		
		es.caib.helium.client.domini.entorn.model.DominiDto domini = null;
		String filtreRsql = "codi==" + codi;
		
		PagedList<es.caib.helium.client.domini.entorn.model.DominiDto> page  = this.dominiClient.listDominisV1(
				entornId, 
				filtreRsql, 
				expedientTipusId, 
				null, 
				null, 
				null);
		
		if (page != null && page.getTotalElements() > 0) {
			if (expedientTipusId != null) {
				domini = page.getContent().get(0);
			} else {
				// Cerca el primer amb tipus expedient null
				for (es.caib.helium.client.domini.entorn.model.DominiDto d : page.getContent())
					if (d.getExpedientTipus() == null) {
						domini = d;
						break;
					}
			}
		}
		
		return this.conversioTipusServiceHelper.convertir(domini, DominiDto.class);
	}

	/** Obté la informació del domini per ID
	 * 
	 * @param dominiId
	 * @return
	 */
	public DominiDto get(Long dominiId) {
		es.caib.helium.client.domini.entorn.model.DominiDto dominiMs = this.dominiClient.getDominiV1(dominiId);
		return this.conversioTipusServiceHelper.convertir(dominiMs, DominiDto.class);
	}
	
	/** Crea el domini al microservei i retorna el seu identificador. En cas de rollback
	 * invoca al mètode per esborrar el domini creat.
	 * 
	 * @param domini
	 * @return
	 */
	@Transactional
	public long create(DominiDto dominiDto) {
		
		es.caib.helium.client.domini.entorn.model.DominiDto domini = 
				this.conversioTipusServiceHelper.convertir(
						dominiDto, 
						es.caib.helium.client.domini.entorn.model.DominiDto.class);
		long dominiId = this.dominiClient.createDominiV1(domini);
		
		// Enregistra el rollback en la transacció
		TransactionSynchronizationManager.registerSynchronization(new CreateDominiRollback(dominiId));
		
		return dominiId;
	}
	
	/** Classe que implementa la sincronització de transacció per esborrar el domini en el cas que la transacció
	 * no finalitzi correctament. D'aquesta forma s'eborra el domini creat.
	 */
	private class CreateDominiRollback implements TransactionSynchronization {
		
		/** Identificador del domini a esborrar. */
		private Long id = null;
		
		/** Constructor per passar l'identificador del donimi per fer rollback.
		 * 
		 * @param id Identificador del domini a esborrar.
		 */
		public CreateDominiRollback(Long id) {
			this.id = id;
		}

		@Override
		public void afterCommit() {}
		@Override
		public void suspend() {}
		@Override
		public void resume() {}
		@Override
		public void flush() {}
		@Override
		public void beforeCommit(boolean readOnly) {}
		@Override
		public void beforeCompletion() {}
		
		/** Mètode que s'executa després de completar la transacció per comprovar si s'ha
		 * fet un rollback.
		 * @param status
		 */
		@Override
		@Transactional
		public void afterCompletion(int status) {
			if (status == TransactionSynchronization.STATUS_ROLLED_BACK) {
				try {
					dominiClient.deleteDominiV1(this.id);
				} catch (Exception e) {
					// s'ignora l'error
					System.err.println("Error esborrant el domini " + id + " en el rollback: " + e.getMessage());
				}
			}
		}
	}
	
	/** Elimina la informació d'un domini
	 * 
	 * @param dominiId
	 */
	public void delete(Long dominiId) {
		this.dominiClient.deleteDominiV1(dominiId);
		
	}

	/** Mètode per actualitzar les dades d'un domini
	 * 
	 * @param domini
	 */
	public DominiDto update(DominiDto domini) {
		es.caib.helium.client.domini.entorn.model.DominiDto dominiMs = 
				this.conversioTipusServiceHelper.convertir(
						domini, 
						es.caib.helium.client.domini.entorn.model.DominiDto.class);
		this.dominiClient.updateDominiV1(
				domini.getId(), 
				dominiMs);
		return domini;
	}

	
	/// Mètodes de Consulta
	
	/** Consulta del domini i dels seus resultats
	 * 
	 * @param dominiId
	 * @param identificador
	 * @param parametres
	 * @return
	 */
	public List<FilaResultat> consultarDomini(Long dominiId, String identificador, Map<String, Object> parametres) {
		
		Map<String, String> parametresString = new HashMap<String, String>();
		for (Map.Entry<String, Object> entry : parametres.entrySet()) {
			parametresString.put(entry.getKey(), entry.getValue().toString());
		}
		
		ResultatDomini resultatDomini = this.dominiClient.consultaDominiV1(dominiId, parametresString);

		return this.conversioTipusServiceHelper.convertirList(resultatDomini, FilaResultat.class);
	}
	
	/** Mètode per consultar múltiples dominis a la vegada i retornar els resultats com a llista amb 
	 * l'identifidadord de la consulta inicial.
	 * @param consultesDominis
	 * @return
	 */
	public Map<Integer, List<FilaResultat>> consultarDominis(List<ConsultaDominiDto> consultesDominis) {
		Map<Integer, List<FilaResultat>> resultats = new HashMap<Integer, List<FilaResultat>>();
		List<FilaResultat> resultat;
		//TODO DANIEL: cridar a un mètode de consulta múltiple a la vegada.
		for (ConsultaDominiDto consultaDomini : consultesDominis) {
			resultat = this.consultarDomini( 
					consultaDomini.getDominiId(), 
					 consultaDomini.getDominiWsId(), 
					 consultaDomini.getParametres());
			resultats.put(consultaDomini.getIdentificadorConsulta(), resultat);
		}
		return resultats;
	}
	
	/** Obté la llista de dominis només amb entorn.
	 * 
	 * @param entornId
	 * @param filtre
	 * @param page
	 * @param size
	 * @param sort
	 * @return
	 * @throws RestClientException
	 */
	public List<DominiDto> llistaDominiByEntorn(Long entornId)
	{		
		PagedList pagedList = this.entornClient.listDominisByEntorn(entornId, null, null, null);
		
		return this.conversioTipusServiceHelper.convertirList(
				pagedList.getContent(), 
				DominiDto.class);
	}
	
	/** Obté la llista de dominis per a un tipus d'expedient
	 * 
	 * @param expedientTipusId
	 * @param filtre
	 * @return
	 */
	public List<DominiDto> llistaDominiByExpedientTipus(Long expedientTipusId, String filtre)
	{
		PagedList<DominiClient> page = this.expedientTipusClient.llistaDominiByExpedientTipus(expedientTipusId, filtre, null, null);
		return conversioTipusServiceHelper.convertirList(
				page.getContent(), 
				DominiDto.class);
	}

	/** Consulta dels dominis globals i del tipus d'expedient
	 * 
	 * @param entornId
	 * @param expedientTipusId
	 * @return
	 */
	public List<DominiDto> findAmbExpedientTipusIGlobals(Long entornId, Long expedientTipusId) {
		List<DominiDto> dominis = 
				this.conversioTipusServiceHelper.convertirList(
						this.dominiClient.listDominisV1(entornId, null, expedientTipusId, null, null, null).getContent(),
						DominiDto.class);
		return dominis;
	}

	/** Consulta del domini per codi per a un tipus d'expedient amb herència i globals.
	 * 
	 * @param entornId
	 * @param expedientTipusId
	 * @param expedientTipusPareId
	 * @param codi
	 * @return
	 */
	public DominiDto findByExpedientTipusAndCodiAmbHerencia(Long entornId, Long expedientTipusId, Long expedientTipusPareId, String codi) {
		DominiDto domini = null;
		String filtreRsql = "codi==" + codi;
		List<DominiDto> dominis = 
				this.conversioTipusServiceHelper.convertirList(
						this.dominiClient.listDominisV1(entornId, filtreRsql, expedientTipusId, null, null, null).getContent(),
						DominiDto.class);
		if (dominis.size() > 0)
			domini = dominis.get(0);
		return domini;
	}
	
	/** Retorna un string amb les propietats i ordenacions.
	 * 
	 * @param paginacioParams
	 * @return
	 */
	protected Sort getSort(PaginacioParamsDto paginacioParams) {
		
		List<Order> ordres = new ArrayList<Order>();
		if (paginacioParams.getOrdres() != null && paginacioParams.getOrdres().size() > 0) {
			for (OrdreDto ordreDto : paginacioParams.getOrdres()) {
				String propietat = ordreDto.getCamp();
				Direction direccio = OrdreDireccioDto.DESCENDENT.equals(ordreDto.getDireccio()) ? Sort.Direction.DESC : Sort.Direction.ASC;
				ordres.add(new Order(
						direccio,
						propietat));
			}
		}
		return Sort.by(ordres);
	}
	
	protected <T> PaginaDto<T> toPaginaDto(PagedList page, Class<T> classT) {

		PaginaDto<T> dto = new PaginaDto<T>();
		if (page != null) {
			dto.setNumero(page.getNumber());
			dto.setTamany(page.getSize());
			dto.setTotal(page.getTotalPages());
			dto.setElementsTotal(page.getTotalElements());
			dto.setAnteriors(page.getNumber() < page.getTotalPages());
			dto.setPrimera(page.getNumber() == 0);
			dto.setPosteriors(page.getNumber() < page.getTotalPages());
			dto.setDarrera(page.getNumber() == page.getTotalPages() - 1);
			if (page.getContent() != null) {
				dto.setContingut(
						this.conversioTipusServiceHelper.convertirList(
								page.getContent(),
								classT));
			}
		} else {
			// Pàgina buida
			dto.setNumero(0);
			dto.setTamany(0);
			dto.setTotal(1);
			dto.setElementsTotal(0);
			dto.setAnteriors(false);
			dto.setPrimera(true);
			dto.setPosteriors(false);
			dto.setDarrera(true);
			dto.setContingut(null);
		}
		return dto;
	}
}
