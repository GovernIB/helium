/**
 * 
 */
package net.conselldemallorca.helium.core.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipusUnitatOrganitzativa;
import net.conselldemallorca.helium.core.model.hibernate.UnitatOrganitzativa;
import net.conselldemallorca.helium.v3.core.api.dto.ArbreDto;
import net.conselldemallorca.helium.v3.core.api.dto.ArbreNodeDto;
import net.conselldemallorca.helium.v3.core.api.dto.PermisDto;
import net.conselldemallorca.helium.v3.core.api.dto.TipusTransicioEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.UnitatOrganitzativaDto;
import net.conselldemallorca.helium.v3.core.api.dto.UnitatOrganitzativaEstatEnumDto;
import net.conselldemallorca.helium.v3.core.api.exception.SistemaExternException;
import net.conselldemallorca.helium.v3.core.repository.ExpedientTipusUnitatOrganitzativaRepository;
import net.conselldemallorca.helium.v3.core.repository.UnitatOrganitzativaRepository;


/**
 * Helper per a operacions amb unitats organitzatives.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class UnitatOrganitzativaHelper {

	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	
	private MapperFactory mapperFactory;
	
	@Resource
	private PluginHelper pluginHelper;

	@Resource(name = "permisosHelperV3") 
	private PermisosHelper permisosHelper;
	
	@Resource 
	private ParametreHelper parametreHelper;
	
	@Resource
	private UnitatOrganitzativaRepository unitatOrganitzativaRepository;
	@Resource
	private ExpedientTipusUnitatOrganitzativaRepository expedientTipusUnitatOrganitzativaRepository;

	public UnitatOrganitzativaHelper() {
		
	}
	
	public List<ExpedientTipusUnitatOrganitzativa> findRelacionsExpTipusUnitOrgList(Long expedientTipusId, Long unitatOrgId) {
		if(expedientTipusId!=null && unitatOrgId==null)
			return expedientTipusUnitatOrganitzativaRepository.findByExpedientTipusId(expedientTipusId); 
		else if(expedientTipusId==null && unitatOrgId!=null)
			return expedientTipusUnitatOrganitzativaRepository.findByUnitatOrganitzativaId(unitatOrgId);
		return null;	
	}
	
	public ExpedientTipusUnitatOrganitzativa findRelacioExpTipusUnitOrg(Long expedientTipusId, Long unitatOrgId) {
		if(expedientTipusId!=null && unitatOrgId!=null) {	
			return expedientTipusUnitatOrganitzativaRepository.findByExpedientTipusIdAndUnitatOrganitzativaId(expedientTipusId, unitatOrgId);
		}
		return null;
	}
	
	public UnitatOrganitzativa findById(Long unitatOrganitzativaId) {
		return unitatOrganitzativaRepository.findOne(unitatOrganitzativaId);	
	}
	
	public UnitatOrganitzativaDto toDto(UnitatOrganitzativa entity) {
		UnitatOrganitzativaDto unitat = null;
		if(entity!=null) {
			unitat = new UnitatOrganitzativaDto(
					entity.getId(),
					entity.getCodi(),
					entity.getDenominacio()  , //String denominacio,
					entity.getNifCif()  , //String nifCif,
					entity.getDataCreacioOficial() , //Date dataCreacioOficial,
					entity.getEstat()!=null? entity.getEstat().toString() : null  , //String estat,
					entity.getCodiUnitatSuperior()  , //String codiUnitatSuperior,
					entity.getCodiUnitatArrel() , //String codiUnitatArrel,
					entity.getCodiPais()!=null ? Long.valueOf(entity.getCodiPais()) : null , //Long codiPais,
					entity.getCodiComunitat()!=null ? Long.valueOf(entity.getCodiComunitat()): null , //Long codiComunitat,
					entity.getCodiProvincia()!=null ? Long.valueOf(entity.getCodiProvincia()): null  , //Long codiProvincia,
					entity.getCodiPostal()  , //String codiPostal,
					entity.getNomLocalitat()  , //String nomLocalitat,
					entity.getTipusVia() , //Long tipusVia,
					entity.getNomVia()  , //String nomVia,
					entity.getNumVia()  , //String numVia,
					null); //List<String> historicosUO);
		}
		if (unitat != null) {
			unitat.setAdressa(unitat.getTipusVia() + " " 
								+ unitat.getNomVia() + " " 
								+ unitat.getNumVia());

			if (unitat.getCodiPais() != null && !"".equals(unitat.getCodiPais())) {
					unitat.setCodiPais(("000" + unitat.getCodiPais()).substring(unitat.getCodiPais().length()));
			}
			if (unitat.getCodiComunitat() != null && !"".equals(unitat.getCodiComunitat())) {
					unitat.setCodiComunitat(("00" + unitat.getCodiComunitat()).substring(unitat.getCodiComunitat().length()));
			}
			if ((unitat.getCodiProvincia() == null || "".equals(unitat.getCodiProvincia())) && 
					unitat.getCodiComunitat() != null && !"".equals(unitat.getCodiComunitat())) {
//					List<ProvinciaDto> provincies = cacheHelper.findProvinciesPerComunitat(unitat.getCodiComunitat());
//					if (provincies != null && provincies.size() == 1) {
//						unitat.setCodiProvincia(provincies.get(0).getCodi());
//					}		
			}
			if (unitat.getCodiProvincia() != null && !"".equals(unitat.getCodiProvincia())) {
				unitat.setCodiProvincia(("00" + unitat.getCodiProvincia()).substring(unitat.getCodiProvincia().length()));
				if (unitat.getLocalitat() == null && unitat.getNomLocalitat() != null) {
//						MunicipiDto municipi = findMunicipiAmbNom(
//								unitat.getCodiProvincia(), 
//								unitat.getNomLocalitat());
//						if (municipi != null)
//							unitat.setLocalitat(municipi.getCodi());
//						else
//							logger.error("UNITAT ORGANITZATIVA. No s'ha trobat la localitat amb el nom: '" + unitat.getNomLocalitat() + "'");
				}
			}
		}
		return unitat;
	}
		
	public void sincronizarOActualizar(UnitatOrganitzativa entitat) {
			List<UnitatOrganitzativaDto> unitats;
			unitats =  pluginHelper.findAmbPare(
						entitat.getCodi(), 
						parametreHelper.getDataActualitzacioUos(),
						parametreHelper.getDataSincronitzacioUos());
			// Takes all the unitats from WS and saves them to database. If unitat did't exist in db it creates new one if it already existed it overrides existing one.  
			for (UnitatOrganitzativaDto unitatDto : unitats) {
				sincronizarUnitat(unitatDto, entitat.getCodi());
			}
			// historicos
			for (UnitatOrganitzativaDto unitatDto : unitats) {
				UnitatOrganitzativa unitat = unitatOrganitzativaRepository
						.findByCodi(unitatDto.getCodi());
				sincronizarHistoricosUnitat(unitat, unitatDto);
			}
			List<UnitatOrganitzativa> obsoleteUnitats = unitatOrganitzativaRepository
					.findByCodiAndEstatNotV(entitat.getCodi());
			// setting type of transition
			for (UnitatOrganitzativa obsoleteUnitat : obsoleteUnitats) {
				if (obsoleteUnitat.getNoves().size() > 1) {
					obsoleteUnitat.updateTipusTransicio(TipusTransicioEnumDto.DIVISIO);
				} else {
					if (obsoleteUnitat.getNoves().size() == 1) {
						if (obsoleteUnitat.getNoves().get(0).getAntigues().size() > 1) {
							obsoleteUnitat.updateTipusTransicio(TipusTransicioEnumDto.FUSIO);
						} else if (obsoleteUnitat.getNoves().get(0).getAntigues().size() == 1) {
							obsoleteUnitat.updateTipusTransicio(TipusTransicioEnumDto.SUBSTITUCIO);
						}
					}
				}
			}
		}

		/**
		 * Creating new unitat (if it doesn't exist in db) or overriding existing one (if it exists in db)
		 * 
		 * @param unidadWS
		 * @param entidadId
		 * @throws Exception
		 */
		public UnitatOrganitzativa sincronizarUnitat(UnitatOrganitzativaDto unitatDto, String codiEntitat) {
			UnitatOrganitzativa unitat = null;
			if (unitatDto != null) {					
				// checks if unitat already exists in database
				unitat = unitatOrganitzativaRepository.findByCodi(unitatDto.getCodi());
				//if not it creates a new one
				if (unitat == null) {
					 unitat = UnitatOrganitzativa.getBuilder(
							unitatDto.getCodi(), // codi,
							unitatDto.getDenominacio(),//String denominacio,
							unitatDto.getNifCif(),//String nifCif,
							unitatDto.getCodiUnitatSuperior(),//String codiUnitatSuperior,
							unitatDto.getCodiUnitatArrel(),//String codiUnitatArrel,
							unitatDto.getDataCreacioOficial(),//Date dataCreacioOficial,
							unitatDto.getDataSupressioOficial(),//Date dataSupressioOficial,
							unitatDto.getDataExtincioFuncional(),//Date dataExtincioFuncional,
							unitatDto.getDataAnulacio(),//Date dataAnulacio,
							unitatDto.getEstat(),//String estat,
							unitatDto.getCodiPais(),//String codiPais,
							unitatDto.getCodiComunitat(),//String codiComunitat,
							unitatDto.getCodiProvincia(),//String codiProvincia,
							unitatDto.getCodiPostal(),//String codiPostal,
							unitatDto.getNomLocalitat(),//String nomLocalitat,
							unitatDto.getLocalitat(),//String localitat,
							unitatDto.getAdressa(),//String adressa,
							unitatDto.getTipusVia(),//Long tipusVia,
							unitatDto.getNomVia(),//String nomVia,
							unitatDto.getNumVia())//String numVia)
							.build();
				} else {
					unitat.update(
							unitatDto.getCodi(),
							unitatDto.getDenominacio(),
							unitatDto.getNifCif(),
							unitatDto.getCodiUnitatSuperior(),
							unitatDto.getCodiUnitatArrel(), 
							unitatDto.getDataCreacioOficial(),
							unitatDto.getDataSupressioOficial(),
							unitatDto.getDataExtincioFuncional(),
							unitatDto.getDataAnulacio(),
							unitatDto.getEstat(), 
							unitatDto.getCodiPais(),
							unitatDto.getCodiComunitat(),
							unitatDto.getCodiProvincia() ,
							unitatDto.getCodiPostal() ,
							unitatDto.getNomLocalitat() ,
							unitatDto.getLocalitat() ,
							unitatDto.getAdressa() ,
							unitatDto.getTipusVia() ,
							unitatDto.getNomVia() ,
							unitatDto.getNumVia());
//					unitat.setCodiDir3Entitat(codiEntitat);
				}
				// Guardem la Unitat
				unitat = unitatOrganitzativaRepository.save(unitat);
				unitatOrganitzativaRepository.flush();
			}
			return unitat;
		}
		
		public void sincronizarHistoricosUnitat(
				UnitatOrganitzativa unitat, 
				UnitatOrganitzativaDto unitatDto) {

			if (unitatDto.getHistoricosUO()!=null && !unitatDto.getHistoricosUO().isEmpty()) {
				for (String historicoCodi : unitatDto.getHistoricosUO()) {
					UnitatOrganitzativa nova = unitatOrganitzativaRepository.findByCodi(historicoCodi);
					unitat.addNova(nova);
					nova.addAntiga(unitat);
				}
			}
		}

		/**
		 * Takes the list of unitats from database and converts it to the tree
		 * 
		 * @param pareCodi 
		 * 				codiDir3
		 * @return tree of unitats
		 */
		public ArbreDto<UnitatOrganitzativaDto> unitatsOrganitzativesFindArbreByPareAndEstatVigent(String pareCodi) {

			List<UnitatOrganitzativa> unitatsOrganitzativesEntities = unitatOrganitzativaRepository
					.findByCodiAndEstatV(pareCodi);
			
			List<UnitatOrganitzativa> unitatsOrganitzativesAmbArrel = unitatOrganitzativaRepository.findByCodiUnitatArrel(pareCodi);
			ArbreDto<UnitatOrganitzativaDto> resposta = new ArbreDto<UnitatOrganitzativaDto>(false);
			// Cerca l'unitat organitzativa arrel
			UnitatOrganitzativa unitatOrganitzativaArrel = unitatsOrganitzativesEntities!=null ? unitatsOrganitzativesEntities.get(0): null;
//			for (UnitatOrganitzativa unitatOrganitzativa : unitatsOrganitzativesEntities) {
//				if (pareCodi.equalsIgnoreCase(unitatOrganitzativa.getCodi())) {
//					unitatOrganitzativaArrel = unitatOrganitzativa;
//					break;
//				}
//			}
			if (unitatOrganitzativaArrel != null) {
				// Omple l'arbre d'unitats organitzatives
				resposta.setArrel(getNodeArbreUnitatsOrganitzatives(unitatOrganitzativaArrel, unitatsOrganitzativesAmbArrel, null));
				return resposta;

			} else {
				return null;
			}
		}
		
		public ArbreDto<UnitatOrganitzativaDto> unitatsOrganitzativesFindArbreSuperior(String codiUO) {
			List<UnitatOrganitzativa> unitatsOrganitzatives = unitatOrganitzativaRepository
					.findByCodiOrderByDenominacioAsc(codiUO);
			ArbreDto<UnitatOrganitzativaDto> resposta = new ArbreDto<UnitatOrganitzativaDto>(false);
			// Cerca l'unitat organitzativa arrel
			UnitatOrganitzativa unitatOrganitzativaArrel = null;
			for (UnitatOrganitzativa unitatOrganitzativa : unitatsOrganitzatives) {
				if (codiUO.equalsIgnoreCase(unitatOrganitzativa.getCodi())) {
					unitatOrganitzativaArrel = unitatOrganitzativa;
					break;
				}
			}
			if (unitatOrganitzativaArrel != null) {
				// Omple l'arbre d'unitats organitzatives
				resposta.setArrel(getNodeArbreUnitatsOrganitzatives(unitatOrganitzativaArrel, unitatsOrganitzatives, null));
				return resposta;

			}
			return null;
		}

		public List<UnitatOrganitzativa> unitatsOrganitzativesFindLlistaTotesFilles(String arrel, String pareCodi, List<UnitatOrganitzativa> llistaFilles){
			List<UnitatOrganitzativa> unitatsOrganitzativesFilles = unitatOrganitzativaRepository.findByCodiUnitatSuperior(pareCodi);	
			List<UnitatOrganitzativa> unitatsNetes = null;
			List<UnitatOrganitzativa> resposta = new ArrayList<UnitatOrganitzativa>();
			if(llistaFilles!=null)
				resposta.addAll(llistaFilles);
			// Cerca si les unitats filles també tenen filles	
			String codiArrel = pareCodi;
			if(unitatsOrganitzativesFilles!=null && !unitatsOrganitzativesFilles.isEmpty()) {
				for (UnitatOrganitzativa unitatOrganitzativaFilla : unitatsOrganitzativesFilles) {
					//si la unitat filla té fills (unitatsNetes) seguim cridant recursivament aquest mètode
					unitatsNetes = unitatOrganitzativaRepository.findByCodiUnitatSuperior(unitatOrganitzativaFilla.getCodi());
					if(unitatsNetes!=null && !unitatsNetes.isEmpty()) {
						resposta.addAll(unitatsOrganitzativesFindLlistaTotesFilles(codiArrel, unitatOrganitzativaFilla.getCodi(),unitatsNetes));
					} else {
						resposta.add(unitatOrganitzativaFilla);						}
				}
			}
			return resposta;
		}

		/**
		 * 
		 * @param unitatOrganitzativa - in first call it is unitat arrel, later the children nodes
		 * @param unitatsOrganitzatives
		 * @param pare - in first call it is null, later pare
		 * @return
		 */
		public ArbreNodeDto<UnitatOrganitzativaDto> getNodeArbreUnitatsOrganitzatives(
				UnitatOrganitzativa unitatOrganitzativa,
				List<UnitatOrganitzativa> unitatsOrganitzatives,
				ArbreNodeDto<UnitatOrganitzativaDto> pare) {
			// creating current arbre node and filling it with pare arbre node and dades as current unitat
			ArbreNodeDto<UnitatOrganitzativaDto> currentArbreNode = new ArbreNodeDto<UnitatOrganitzativaDto>(
					pare,
					conversioTipusHelper.convertir(
							unitatOrganitzativa,
							UnitatOrganitzativaDto.class));
			String codiUnitat = (unitatOrganitzativa != null) ? unitatOrganitzativa.getCodi() : null;
			// for every child of current unitat call recursively getNodeArbreUnitatsOrganitzatives()
			for (UnitatOrganitzativa uo: unitatsOrganitzatives) {
				//searches for children of current unitat
				if (	(codiUnitat == null && uo.getCodiUnitatSuperior() == null) ||
						(uo.getCodiUnitatSuperior() != null && uo.getCodiUnitatSuperior().equals(codiUnitat))) {
					currentArbreNode.addFill(
							getNodeArbreUnitatsOrganitzatives(
									uo,
									unitatsOrganitzatives,
									currentArbreNode));
				}
			}
			return currentArbreNode;
		}
	
		/**
		 * Getting unitats that are new (not transitioned from any other unitat)
		 * @param entidadId
		 * @return
		 */
		public List<UnitatOrganitzativaDto> getNewFromWS(Long entidadId){
			UnitatOrganitzativa entitat = unitatOrganitzativaRepository.findOne(entidadId);
			// getting list of syncronization unitats from webservices
			List<UnitatOrganitzativaDto> unitatsWS = pluginHelper.findAmbPare(
												entitat.getCodi(),
												parametreHelper.getDataActualitzacioUos(), 
												parametreHelper.getDataSincronitzacioUos());
			// getting all vigent unitats from database
			List<UnitatOrganitzativa> vigentUnitatsDB = unitatOrganitzativaRepository
					.findByCodiAndEstatV(entitat.getCodi());
			//List of new unitats that are vigent
			List<UnitatOrganitzativaDto> vigentUnitatsWS = new ArrayList<UnitatOrganitzativaDto>();
			//List of new unitats that are vigent and does not exist in database
			List<UnitatOrganitzativaDto> vigentNotInDBUnitatsWS = new ArrayList<UnitatOrganitzativaDto>();
			//List of new unitats (that are vigent, not pointed by any obsolete unitat and does not exist in database)
			List<UnitatOrganitzativaDto> newUnitatsWS = new ArrayList<UnitatOrganitzativaDto>();
			//Filtering to only obtain vigents 
			for (UnitatOrganitzativaDto unitatWS : unitatsWS) {
				if (unitatWS.getEstat().equals("V") && !unitatWS.getCodi().equals(entitat.getCodi())) {
					vigentUnitatsWS.add(unitatWS);
				}
			}
			// Filtering to only obtain vigents that does not already exist in
			// database
			for (UnitatOrganitzativaDto vigentUnitat : vigentUnitatsWS) {
				boolean found = false;
				for (UnitatOrganitzativa vigentUnitatDB : vigentUnitatsDB) {
					if (vigentUnitatDB.getCodi().equals(vigentUnitat.getCodi())) {
						found = true;
					}
				}
				if (found == false) {
					vigentNotInDBUnitatsWS.add(vigentUnitat);
				}
			}
			// Filtering to obtain unitats that are vigent, not pointed by any obsolete unitat and does not already exist in database
			for (UnitatOrganitzativaDto vigentNotInDBUnitatWS : vigentNotInDBUnitatsWS) {
				boolean pointed = false;
				for (UnitatOrganitzativaDto unitatWS : unitatsWS) {
					if(unitatWS.getHistoricosUO()!=null){
						for(String novaCodi: unitatWS.getHistoricosUO()){
							if(novaCodi.equals(vigentNotInDBUnitatWS.getCodi())){
								pointed = true;
							}
						}
					}
				}
				if (pointed == false) {
					newUnitatsWS.add(vigentNotInDBUnitatWS);
				}
			}
			// converting from UnitatOrganitzativa to UnitatOrganitzativaDto
			List<UnitatOrganitzativaDto> newUnitatsDto = new ArrayList<UnitatOrganitzativaDto>();
			for (UnitatOrganitzativaDto vigent : newUnitatsWS){
				newUnitatsDto.add(vigent);
			}
			return newUnitatsDto;
		}

		/**
		 * Method to get last historicos (recursive to cover cumulative synchro case)
		 * @param unitat
		 * @param lastHistorcos
		 */
		public void getLastHistoricosRecursive(
				UnitatOrganitzativaDto unitat, 
				List<UnitatOrganitzativaDto> lastHistorcos) {

			if (unitat.getNoves() == null || unitat.getNoves().isEmpty()) {
				lastHistorcos.add(unitat);
			} else {
				for (UnitatOrganitzativaDto unitatI : unitat.getNoves()) {
					getLastHistoricosRecursive(unitatI, lastHistorcos);
				}
			}
		}

		/**
		 * Starting point, calls recursive method to get last historicos
		 * and sets last historicos and sets the type of transition
		 * 
		 * @param uo
		 * @return
		 */
		public UnitatOrganitzativaDto getLastHistoricos(UnitatOrganitzativaDto uo) {
			// if there are no historicos leave them empty and don´t go into recursive method
			if (uo.getNoves() == null || uo.getNoves().isEmpty()) {
				return uo;
			} else {
				List<UnitatOrganitzativaDto> lastHistorcos = new ArrayList<UnitatOrganitzativaDto>();
				getLastHistoricosRecursive(uo, lastHistorcos);
				uo.setLastHistoricosUnitats(lastHistorcos);

				return uo;
			}
		}
		
		public List<UnitatOrganitzativaDto> predictFirstSynchronization(Long entidadId) throws SistemaExternException{
			UnitatOrganitzativa entitat = unitatOrganitzativaRepository.findOne(entidadId);
			List<UnitatOrganitzativaDto> unitatsVigentsWS = pluginHelper.findAmbPare(
					entitat.getCodi(),
					parametreHelper.getDataActualitzacioUos()!=null? parametreHelper.getDataActualitzacioUos(): null,//new Date(), 
							parametreHelper.getDataSincronitzacioUos()!=null? parametreHelper.getDataSincronitzacioUos():null);
			return unitatsVigentsWS;
		}
		
		/**
		 * Getting unitats that didn't transition to any other unitats, they only got some properties changed 
		 * @param entidadId
		 * @return
		 */
		public List<UnitatOrganitzativaDto> getVigentsFromWebService(Long entidadId){
			UnitatOrganitzativa entitat = unitatOrganitzativaRepository.findOne(entidadId);
			// getting list of last changes from webservices
			List<UnitatOrganitzativaDto> unitatsWS = pluginHelper.findAmbPare(
					entitat.getCodi(),
					parametreHelper.getDataActualitzacioUos(), 
					parametreHelper.getDataSincronitzacioUos());
			// getting all vigent unitats from database
			List<UnitatOrganitzativa> vigentUnitats = unitatOrganitzativaRepository
					.findByCodiAndEstatV(entitat.getCodi());
			// list of vigent unitats from webservice
			List<UnitatOrganitzativaDto> unitatsVigentsWithChangedAttributes = new ArrayList<UnitatOrganitzativaDto>();
			for (UnitatOrganitzativa unitatV : vigentUnitats) {
				for (UnitatOrganitzativaDto unitatWS : unitatsWS) {
					if (unitatV.getCodi().equals(unitatWS.getCodi()) && unitatWS.getEstat().equals("V")
							&& (unitatWS.getHistoricosUO() == null || unitatWS.getHistoricosUO().isEmpty())
							&& !unitatV.getCodi().equals(entitat.getCodi())) {
						unitatsVigentsWithChangedAttributes.add(unitatWS);
					}
				}
			}
			// converting from UnitatOrganitzativa to UnitatOrganitzativaDto
			List<UnitatOrganitzativaDto> unitatsVigentsWithChangedAttributesDto = new ArrayList<UnitatOrganitzativaDto>();
			for(UnitatOrganitzativaDto vigent : unitatsVigentsWithChangedAttributes){
				unitatsVigentsWithChangedAttributesDto.add(conversioTipusHelper.convertir(
						vigent, 
						UnitatOrganitzativaDto.class));
			}
			return unitatsVigentsWithChangedAttributesDto;
		}
		
		
		/**
		 * Getting from WS obsolete unitats (with pointer to vigent unitat)  
		 * @param entidadId
		 * @return
		 * @throws SistemaExternException
		 */
		public List<UnitatOrganitzativaDto> getObsoletesFromWS(Long entidadId) throws SistemaExternException{
			UnitatOrganitzativa entitat = unitatOrganitzativaRepository.findOne(entidadId);
			/*UnitatOrganitzativa unidadPadreWS = pluginHelper.findUnidad(entitat.getCodiDir3(),
					null, null);*/
			// getting list of last changes from webservices
			List<UnitatOrganitzativaDto> unitatsWS = pluginHelper.findAmbPare(
					entitat.getCodi(),
					parametreHelper.getDataActualitzacioUos(), 
					parametreHelper.getDataSincronitzacioUos());

			// getting all vigent unitats from database
			List<UnitatOrganitzativa> vigentUnitats = unitatOrganitzativaRepository
					.findByCodiAndEstatV(entitat.getCodi());
			logger.debug("Consulta d'unitats vigents a DB");
			for(UnitatOrganitzativa uv: vigentUnitats){
				logger.debug(ToStringBuilder.reflectionToString(uv));
			}
			// list of obsolete unitats from ws that were vigent after last sincro (are vigent in DB and obsolete in WS)
			// the reason why we don't just return all obsolete unitats from ws is because it is possible to have cumulative changes:
			// If since last sincro unitat A changed to B and then to C then in our DB will be A(Vigent) but from WS we wil get: A(Extinguished) -> B(Extinguished) -> C(Vigent) 
			// we only want to return A (we dont want to return B) because prediction must show this transition (A -> C) [between A(that is now vigent in database) and C (that is now vigent in WS)]     
			List<UnitatOrganitzativaDto> unitatsVigentObsolete = new ArrayList<UnitatOrganitzativaDto>();
			for (UnitatOrganitzativa unitatV : vigentUnitats) { 
				for (UnitatOrganitzativaDto unitatWS : unitatsWS) {
					if (unitatV.getCodi().equals(unitatWS.getCodi()) && !unitatWS.getEstat().equals("V")
							&& !unitatV.getCodi().equals(entitat.getCodi())) {
						unitatsVigentObsolete.add(unitatWS);
					}
				}
			}
			logger.debug("Consulta unitats obsolete ");
			for (UnitatOrganitzativaDto vigentObsolete : unitatsVigentObsolete) {
				logger.debug(vigentObsolete.getCodi()+" "+vigentObsolete.getEstat()+" "+vigentObsolete.getHistoricosUO());
			}
			for (UnitatOrganitzativaDto vigentObsolete : unitatsVigentObsolete) {
				
				// setting obsolete unitat to point to the last one it transitioned into 
				// Name of the field historicosUO is totally wrong because this field shows future unitats not historic
				// but that's how it is named in WS and we cant change it 
				// lastHistoricosUnitats field should be pointing to the last unitat it transitioned into. We need to recursively find last one because it is possible there will be cumulative changes:
				// If since last sincro unitat A changed to B and then to C then from WS we will get unitat A pointing to B (A -> B) and unitat B pointing to C (B -> C)  
				// what we want is to add direct pointer from unitat A to C (A -> C)
				vigentObsolete.setLastHistoricosUnitats(getLastHistoricos(vigentObsolete, unitatsWS));
			}
			// converting from UnitatOrganitzativa to UnitatOrganitzativaDto
			List<UnitatOrganitzativaDto> unitatsVigentObsoleteDto = new ArrayList<UnitatOrganitzativaDto>();
			for(UnitatOrganitzativaDto vigentObsolete : unitatsVigentObsolete){
				unitatsVigentObsoleteDto.add(conversioTipusHelper.convertir(
						vigentObsolete, 
						UnitatOrganitzativaDto.class));
			}
			return unitatsVigentObsoleteDto;
		}

		
		/**
		 * It gives the last (most current) unitat/unitats to which given obsolete unitat transitioned
		 * Starting point of recursivie method
		 * @param unitat - unitats which historicos we are looking for
		 * @param unitatsFromWebService - unitats used for getting unitat object from codi 
		 * @param addHistoricos - initially empty list to add the last historicos (unitats that dont have historicos)
		 */
		private List<UnitatOrganitzativaDto> getLastHistoricos(
				UnitatOrganitzativaDto unitat, 
				List<UnitatOrganitzativaDto> unitatsFromWebService){
			
			List<UnitatOrganitzativa> lastHistoricos = new ArrayList<UnitatOrganitzativa>();
			getLastHistoricosRecursive(
					unitat, 
					unitatsFromWebService, 
					lastHistoricos);
			List<UnitatOrganitzativaDto> unitatsDtoLastHistoricos = new ArrayList<UnitatOrganitzativaDto>();
			for(UnitatOrganitzativa lastHistorico: lastHistoricos ) {
				unitatsDtoLastHistoricos.add(this.toDto(lastHistorico));
			}
			return unitatsDtoLastHistoricos;
		}
		
		private void getLastHistoricosRecursive(
				UnitatOrganitzativaDto unitat,
				List<UnitatOrganitzativaDto> unitatsFromWebService,
				List<UnitatOrganitzativa> lastHistoricos) {

			logger.debug("Coloca historicos recursiu(" + "unitatCodi=" + unitat.getCodi() + ")");

//			if (unitat.getHistoricosUO() == null || unitat.getHistoricosUO().isEmpty()) {
//				lastHistoricos.add(
//						unitat);
//			} else {
//				for (String historicoCodi : unitat.getHistoricosUO()) {
//					UnitatOrganitzativaDto unitatFromCodi = getUnitatFromCodi(
//							historicoCodi,
//							unitatsFromWebService);
//					if (unitatFromCodi == null) {
//						// Looks for historico in database
//						UnitatOrganitzativa entity = unitatOrganitzativaRepository.findByCodi(historicoCodi);
//						if (entity != null) {
//							UnitatOrganitzativa uo = conversioTipusHelper.convertir(entity, UnitatOrganitzativa.class);
//							lastHistoricos.add(uo);						
//						} else {
//							String errorMissatge = "Error en la sincronització amb DIR3. La unitat orgánica (" + unitat.getCodi()
//									+ ") té l'estat (" + unitat.getEstat() + ") i l'històrica (" + historicoCodi
//									+ ") però no s'ha retornat la unitat orgánica (" + historicoCodi
//									+ ") en el resultat de la consulta del WS ni en la BBDD.";
//							throw new SistemaExternException(IntegracioHelper.INTCODI_UNITATS, errorMissatge);
//						}
//					} else {
//						getLastHistoricosRecursive(
//								unitatFromCodi,
//								unitatsFromWebService,
//								lastHistoricos);
//					}
//				}
//			}
		}
		
		/**
		 * Searches for and returns the unitat object with the given codi from the given list of unitats
		 * @param codi
		 * @param allUnitats
		 * @return
		 */
		private UnitatOrganitzativa getUnitatFromCodi(
				String codi, 
				List<UnitatOrganitzativa> allUnitats){
			
			UnitatOrganitzativa unitatFromCodi = null;
			for (UnitatOrganitzativa unitatWS : allUnitats) {
				if (unitatWS.getCodi().equals(codi)) {
					unitatFromCodi = unitatWS;
				}
			}
			return unitatFromCodi;
		}
		
		public String getEstatBBDD(UnitatOrganitzativaEstatEnumDto estat) {
			String estatBBDD=null;
			if(estat!=null){
//			V: Vigente, E: Extinguido, A: Anulado, T: Transitorio
				if(UnitatOrganitzativaEstatEnumDto.VIGENTE.equals(estat))
					estatBBDD="V";
				if(UnitatOrganitzativaEstatEnumDto.EXTINGUIDO.equals(estat))
					estatBBDD="E";
				if(UnitatOrganitzativaEstatEnumDto.ANULADO.equals(estat))
					estatBBDD="A";
				if(UnitatOrganitzativaEstatEnumDto.TRANSITORIO.equals(estat))
					estatBBDD="T";
			} 
			return estatBBDD;
		}
		
		public UnitatOrganitzativaEstatEnumDto convertUnitatOrganitzativaEstatToEnum(String estat) {
			UnitatOrganitzativaEstatEnumDto estatEnum=null;
			if(estat!=null){
//			V: Vigente, E: Extinguido, A: Anulado, T: Transitorio
				if("V".equals(estat))
					estatEnum=UnitatOrganitzativaEstatEnumDto.VIGENTE;
				if("E".equals(estat))
					estatEnum=UnitatOrganitzativaEstatEnumDto.EXTINGUIDO;
				if("A".equals(estat))
					estatEnum=UnitatOrganitzativaEstatEnumDto.ANULADO;
				if("T".equals(estat))
					estatEnum=UnitatOrganitzativaEstatEnumDto.TRANSITORIO;
			} 
			return estatEnum;
		}
		
		/**
		 * Finds path of unitats that starts from given unitat and stops with unitata arrel unitat arrel
		 * @param codiDir3
		 * @param unitatOrganitzativaCodi
		 * @return
		 */
		public List<UnitatOrganitzativaDto> findPath(
				String arrel,
				String unitatOrganitzativaCodi) {
			ArbreDto<UnitatOrganitzativaDto> arbre = unitatsOrganitzativesFindArbreSuperior(arrel);
			List<UnitatOrganitzativaDto> superiors = new ArrayList<UnitatOrganitzativaDto>();

			String codiActual = unitatOrganitzativaCodi;
			do {
				arbre = unitatsOrganitzativesFindArbreSuperior(codiActual);
				UnitatOrganitzativaDto unitatActual = cercaDinsArbreAmbCodi(
						arbre,
						codiActual);
				if (unitatActual != null) {
					superiors.add(unitatActual);
					codiActual = unitatActual.getCodiUnitatSuperior();
				} else {
					codiActual = null;
				}
			} while (codiActual != null);	
			return superiors;
		}
		
		private UnitatOrganitzativaDto cercaDinsArbreAmbCodi(
				ArbreDto<UnitatOrganitzativaDto> arbre,
				String unitatOrganitzativaCodi) {
			UnitatOrganitzativaDto trobada = null;
			if(arbre!=null) {
				for (UnitatOrganitzativaDto unitat: arbre.toDadesList()) {
					if (unitat.getCodi().equals(unitatOrganitzativaCodi)) {
						trobada = unitat;
						break;
					}
				}
			}
			return trobada;
		}
		
		
		/**Funció que cerca els organs/unitats organitzatives en els que l'usuari té permisos i ademés afegeix els òrgans fills*/
	    public List<Long> getOrgansAfegintFillsPermisAdminOrRead(String nomUsuari) {
	    	List<Long> resposta = new ArrayList<Long>();
	    	List<ExpedientTipusUnitatOrganitzativa> expTipusUnitOrgList = expedientTipusUnitatOrganitzativaRepository.findAll();
			List<PermisDto> permisos = new ArrayList<PermisDto>();
			for(ExpedientTipusUnitatOrganitzativa expTipusUnitOrg: expTipusUnitOrgList) { //mirem si existeixen permisos
				permisos = permisosHelper.findPermisos(
						expTipusUnitOrg.getId(),
						ExpedientTipusUnitatOrganitzativa.class);
				List<PermisDto> permisosReadOAdminUsuariList = new ArrayList<PermisDto>();
				for(PermisDto permis: permisos) {
					if(permis.getPrincipalNom()!=null
							&& permis.getPrincipalNom().equals(nomUsuari)
							&& (permis.isAdministration() || permis.isRead())) {
						permisosReadOAdminUsuariList.add(permis);
					}
				}
				if(!permisosReadOAdminUsuariList.isEmpty()) {
					//Busquem fills ja que tambÃ© tindrÃ  permisos sobre els fills de les UnitatOrg on tingui permisos 
					List<UnitatOrganitzativa> unitatsFilles = unitatOrganitzativaRepository.findByCodiUnitatArrel(expTipusUnitOrg.getUnitatOrganitzativa().getCodi());		 
					 for(UnitatOrganitzativa unitatOrgFilla: unitatsFilles) {
						//DANI: no seria millor crear els permisos sobre les unitats filles un cop es crea el permÃ­s sobre una unitat pare?
						 List<ExpedientTipusUnitatOrganitzativa> expTipusUnitOrgListFills = expedientTipusUnitatOrganitzativaRepository.findByUnitatOrganitzativaId(unitatOrgFilla.getId());
						 for(ExpedientTipusUnitatOrganitzativa expTipusUnitOrgFill: expTipusUnitOrgListFills) {
							 ExpedientTipus expTipus = expTipusUnitOrgFill.getExpedientTipus();
							 if(!resposta.contains(expTipus.getId()))
								 resposta.add(expTipus.getId());
						 }
					 }
				}
			}
	        return resposta;
	    }		
	public <T> T convertir(Object source, Class<T> targetType) {
		if (source == null)
			return null;
		return getMapperFacade().map(source, targetType);
	}
	public <T> List<T> convertirList(List<?> items, Class<T> targetType) {
		if (items == null)
			return null;
		return getMapperFacade().mapAsList(items, targetType);
	}
	public <T> Set<T> convertirSet(Set<?> items, Class<T> targetType) {
		if (items == null)
			return null;
		return getMapperFacade().mapAsSet(items, targetType);
	}

	private MapperFacade getMapperFacade() {
		return mapperFactory.getMapperFacade();
	}
	private static final Logger logger = LoggerFactory.getLogger(UnitatOrganitzativaHelper.class);
}
