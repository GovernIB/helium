package net.conselldemallorca.helium.ms.domini;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import net.conselldemallorca.helium.ms.BaseMs;
import net.conselldemallorca.helium.ms.HeliumMsPropietats;
import net.conselldemallorca.helium.ms.domini.client.DominiMsClient;
import net.conselldemallorca.helium.ms.domini.client.model.Domini;
import net.conselldemallorca.helium.ms.domini.client.model.DominiPagedList;
import net.conselldemallorca.helium.ms.domini.client.model.ResultatDomini;
import net.conselldemallorca.helium.v3.core.api.dto.DominiDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;

/** Bean per interactuar amb el Micro Servei de Dominis. Encapsula un client
 * de l'API REST pel disseny i consulta dels dominis.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 *
 */
@Service
public class DominiMs extends BaseMs {
	
	@Autowired
	private HeliumMsPropietats heliumMsPropietats;

	/** Referència a la instànca de client de l'API REST de Dominis. */
	private DominiMsClient dominiClient;
			
	/** Mètode per configurar el client de l'API REST de dominis.
	 */
	@PostConstruct
    public void init() {
		this.dominiClient = new DominiMsClient(
				heliumMsPropietats.getBaseUrl(), 
				heliumMsPropietats.getUsuari(), 
				heliumMsPropietats.getPassword());
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
		String sort = null;
		if (paginacioParams.getOrdres() != null && paginacioParams.getOrdres().size() > 0)
			sort = paginacioParams.getOrdres().get(0).getCamp() + "," + paginacioParams.getOrdres().get(0).getDireccio();
		
		DominiPagedList page = this.dominiClient.listDominisV1(
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
		if (expedientTipusId == null) {
			filtreRsql += "AND expedientTipus=isnull=";
		}
		DominiPagedList page = dominiClient.listDominisV1(
				entornId, 
				filtreRsql, 
				expedientTipusId, 
				null, null, null, null);
		
		if (page.getTotalElements() > 0) {
			domini = page.getContent().get(0);
		}
		return this.conversioTipusHelperMs.convertir(domini, DominiDto.class);
	}

	/** Obté la informació del domini per ID
	 * 
	 * @param dominiId
	 * @return
	 */
	public DominiDto get(Long dominiId) {
		Domini dominiMs = this.dominiClient.getDominiV1(dominiId);
		return this.conversioTipusHelperMs.convertir(dominiMs, DominiDto.class);
	}
	
	/** Crea el domini al microservei i retorna el seu identificador.
	 * 
	 * @param domini
	 * @return
	 */
	public long create(DominiDto dominiDto) {
		
		Domini domini = this.conversioTipusHelperMs.convertir(dominiDto, Domini.class);
		long dominiId = this.dominiClient.createDominiV1(domini);		
		return dominiId;
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
		Domini dominiMs = this.conversioTipusHelperMs.convertir(domini, Domini.class);
		this.dominiClient.updateDominiV1(
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
	public ResultatDomini consultarDomini(Long dominiId, String identificador, Map<String, Object> parametres) {
		ResultatDomini resultatDomini = 
				this.dominiClient.consultaDominiV1(
						dominiId, 
						identificador, 
						parametres);
		return resultatDomini;
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
		DominiPagedList pagedList = this.dominiClient.llistaDominiByEntorn(entornId, filtre, page, size, sort);
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
		DominiPagedList pagedList = this.dominiClient.llistaDominiByExpedientTipus(expedientTipusId, filtre, null, null, null);
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
						this.dominiClient.listDominisV1(entornId, null, expedientTipusId, null, null, null, null).getContent(),
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
						this.dominiClient.listDominisV1(entornId, filtreRsql, expedientTipusId, null, null, null, null).getContent(),
						DominiDto.class);
		if (dominis.size() > 0)
			domini = dominis.get(0);
		return domini;
	}
}
