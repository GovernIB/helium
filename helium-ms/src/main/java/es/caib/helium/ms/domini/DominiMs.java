package es.caib.helium.ms.domini;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.client.RestClientException;

import es.caib.helium.logic.intf.dto.ConsultaDominiDto;
import es.caib.helium.logic.intf.dto.DominiDto;
import es.caib.helium.logic.intf.dto.PaginaDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.extern.domini.FilaResultat;
import es.caib.helium.ms.BaseMs;
import es.caib.helium.ms.HeliumMsPropietats;
import es.caib.helium.ms.domini.client.DominiApiClient;
import es.caib.helium.ms.domini.client.model.Domini;
import es.caib.helium.ms.domini.client.model.DominiPagedList;
import es.caib.helium.ms.domini.client.model.ResultatDomini;

/** Bean per interactuar amb el Micro Servei de Dominis. Encapsula un client
 * de l'API REST pel disseny i consulta dels dominis.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 *
 */
@Component
public class DominiMs extends BaseMs implements InitializingBean {
	
	@Autowired
	private HeliumMsPropietats heliumMsPropietats;

	/** Referència a la instànca de client de l'API REST de Dominis. */
	private DominiApiClient dominiApiClient;
			
	
	/** Mètode per configurar el client de l'API REST de dominis.
	 */
	@PostConstruct
    public void init() {
		this.dominiApiClient = new DominiApiClient(
				heliumMsPropietats.getBaseUrl(), 
				heliumMsPropietats.getUsuari(), 
				heliumMsPropietats.getPassword(),
				heliumMsPropietats.isDebugging());
    }
		
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
		String sort = super.getSort(paginacioParams);
		
		DominiPagedList page = this.dominiApiClient.listDominisV1(
				entornId, 
				filtreRsql, 
				expedientTipusId, 
				expedientTipusPareId, 
				paginacioParams.getPaginaNum(), 
				paginacioParams.getPaginaTamany(), 
				sort);

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
		
		Domini domini = null;
		String filtreRsql = "codi==" + codi;
		// TODO: no funciona afegir expressions rsql
//		if (expedientTipusId == null) {
//			filtreRsql += ",expedientTipus=isnull=";
//		}
		DominiPagedList page = dominiApiClient.listDominisV1(
				entornId, 
				filtreRsql, 
				expedientTipusId, 
				null, null, null, null);
		
		if (page != null && page.getTotalElements() > 0) {
			if (expedientTipusId != null) {
				domini = page.getContent().get(0);
			} else {
				// Cerca el primer amb tipus expedient null
				for (Domini d : page.getContent())
					if (d.getExpedientTipusId() == null) {
						domini = d;
						break;
					}
			}
		}
		return this.conversioTipusHelperMs.convertir(domini, DominiDto.class);
	}

	/** Obté la informació del domini per ID
	 * 
	 * @param dominiId
	 * @return
	 */
	public DominiDto get(Long dominiId) {
		Domini dominiMs = this.dominiApiClient.getDominiV1(dominiId);
		return this.conversioTipusHelperMs.convertir(dominiMs, DominiDto.class);
	}
	
	/** Crea el domini al microservei i retorna el seu identificador. En cas de rollback
	 * invoca al mètode per esborrar el domini creat.
	 * 
	 * @param domini
	 * @return
	 */
	@Transactional
	public long create(DominiDto dominiDto) {
		
		Domini domini = this.conversioTipusHelperMs.convertir(dominiDto, Domini.class);
		long dominiId = this.dominiApiClient.createDominiV1(domini);
		
		// Enregistra el rollback en la transacció
		TransactionSynchronizationManager.registerSynchronization(new CreateDominiRollback(dominiId));
		
		return dominiId;
	}
	
	/** Classe que implementa la sincronització de transacció pes esborrar els temporals només en el cas que la transacció
	 * hagi finalitzat correctament. D'aquesta forma no s'esborren els temporals si no s'han guardat correctament a l'arxiu
	 * amb la informació a BBDD.
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
					dominiApiClient.deleteDominiV1(this.id);
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
		this.dominiApiClient.deleteDominiV1(dominiId);
		
	}

	/** Mètode per actualitzar les dades d'un domini
	 * 
	 * @param domini
	 */
	public DominiDto update(DominiDto domini) {
		Domini dominiMs = this.conversioTipusHelperMs.convertir(domini, Domini.class);
		this.dominiApiClient.updateDominiV1(
				dominiMs, 
				domini.getId());
		return this.conversioTipusHelperMs.convertir(domini, DominiDto.class);
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
		ResultatDomini resultatDomini =
				this.dominiApiClient.consultaDominiV1(
						dominiId, 
						identificador, 
						parametres);
		return this.conversioTipusHelperMs.convertirList(resultatDomini, FilaResultat.class);
	}
	
	/** Mètode per consultar múltiples dominis a la vegada i retornar els resultats com a llista amb 
	 * l'identifidadord de la consulta inicial.
	 * @param consultesDominis
	 * @return
	 */
	public Map<Integer, List<FilaResultat>> consultarDominis(List<ConsultaDominiDto> consultesDominis) {
		Map<Integer, List<FilaResultat>> resultats = new HashMap<Integer, List<FilaResultat>>();
		List<FilaResultat> resultat;
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
	public List<DominiDto> llistaDominiByEntorn(Long entornId, String filtre, Integer page, Integer size, String sort)
	{		
		DominiPagedList pagedList = this.dominiApiClient.llistaDominiByEntorn(entornId, filtre, page, size, sort);
		return this.conversioTipusHelperMs.convertirList(
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
		DominiPagedList pagedList = this.dominiApiClient.llistaDominiByExpedientTipus(expedientTipusId, filtre, null, null, null);
		return this.conversioTipusHelperMs.convertirList(
				pagedList.getContent(), 
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
				this.conversioTipusHelperMs.convertirList(
						this.dominiApiClient.listDominisV1(entornId, null, expedientTipusId, null, null, null, null).getContent(),
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
				this.conversioTipusHelperMs.convertirList(
						this.dominiApiClient.listDominisV1(entornId, filtreRsql, expedientTipusId, null, null, null, null).getContent(),
						DominiDto.class);
		if (dominis.size() > 0)
			domini = dominis.get(0);
		return domini;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		System.out.println("DominiMs Bean");		
	}
}
