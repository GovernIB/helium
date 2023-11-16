package net.conselldemallorca.helium.v3.core.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.conselldemallorca.helium.core.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.core.helper.PaginacioHelper;
import net.conselldemallorca.helium.core.helper.PluginHelper;
import net.conselldemallorca.helium.core.helper.UnitatOrganitzativaHelper;
import net.conselldemallorca.helium.core.model.hibernate.Procediment;
import net.conselldemallorca.helium.core.model.hibernate.UnitatOrganitzativa;
import net.conselldemallorca.helium.v3.core.api.dto.ArbreDto;
import net.conselldemallorca.helium.v3.core.api.dto.ArbreNodeDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.UnitatOrganitzativaDto;
import net.conselldemallorca.helium.v3.core.api.dto.procediment.ProcedimentDto;
import net.conselldemallorca.helium.v3.core.api.dto.procediment.ProcedimentEstatEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.procediment.ProcedimentFiltreDto;
import net.conselldemallorca.helium.v3.core.api.service.ProcedimentService;
import net.conselldemallorca.helium.v3.core.repository.ProcedimentRepository;
import net.conselldemallorca.helium.v3.core.repository.UnitatOrganitzativaRepository;

/**
 * Implementació del servei de gestió de procediments.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

@Service
public class ProcedimentServiceImpl implements ProcedimentService{
	
	@Autowired
	private ProcedimentRepository procedimentRepository;
	@Autowired
	private UnitatOrganitzativaRepository unitatOrganitzativaRepository;
	@Autowired
	private UnitatOrganitzativaHelper unitatOrganitzativaHelper;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private PaginacioHelper paginacioHelper;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;

	@Override
	@Transactional(readOnly = true) 
	public PaginaDto<ProcedimentDto> findAmbFiltre(
			ProcedimentFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		logger.trace("Cercant els procediments segons el filtre ("
				+ "filtre=" + filtre + ")");
		
		PaginaDto<ProcedimentDto> llistaProcediments = null;

		UnitatOrganitzativa unitatOrganitzativa = unitatOrganitzativaRepository.findByCodi(filtre.getUnitatOrganitzativa());
		Map<String, String> mapeigPropietatsOrdenacio = new HashMap<String, String>();
		mapeigPropietatsOrdenacio.put("codiProcediment", "codi");
		llistaProcediments = paginacioHelper.toPaginaDto(
				procedimentRepository.findAmbFiltrePaginat(
						unitatOrganitzativa == null, 
						unitatOrganitzativa != null ? unitatOrganitzativa : null, 
						filtre.getCodi() == null || filtre.getCodi().isEmpty(), 
						filtre.getCodi() != null ? filtre.getCodi() : "", 
						filtre.getNom() == null || filtre.getNom().isEmpty(), 
						filtre.getNom() != null ? filtre.getNom() : "", 
						filtre.getCodiSia() == null || filtre.getCodiSia().isEmpty(), 
						filtre.getCodiSia() != null ? filtre.getCodiSia() : "", 
						filtre.getEstat() == null, 
						filtre.getEstat(),						
						paginacioHelper.toSpringDataPageable(paginacioParams, mapeigPropietatsOrdenacio)), 
				ProcedimentDto.class);

		
		return llistaProcediments;
	}

	@Override
	@Transactional	
	public void findAndUpdateProcediments() throws Exception {
		int max_intents = 5;
		List<UnitatOrganitzativa> llistaUnitatsOrganitzatives = unitatOrganitzativaRepository.findAll();
		logger.debug("Actualitzant els procediments amb " + llistaUnitatsOrganitzatives.size() + " unitats organitzatives.");
		
		// Marca'm els procediments de Distribució com a extingits
		List<Procediment> llistaProcedimentsDistribucio = procedimentRepository.findAll();
		for (Procediment procedimentDistribucio : llistaProcedimentsDistribucio) {
			procedimentDistribucio.update(
					procedimentDistribucio.getCodi(), 
					procedimentDistribucio.getNom(), 
					procedimentDistribucio.getCodiSia(),
					ProcedimentEstatEnumDto.EXTINGIT,
					procedimentDistribucio.getUnitatOrganitzativa());
			
			procedimentRepository.save(procedimentDistribucio);
		}
		
		// Consulta l'arbre
		//TODO: s'ha de consultar l'arbre, no només de la vigent, i el codi ha d'estar en un paràmetre de configuració
		String entitatCodi = "A04003003";
		ArbreDto<UnitatOrganitzativaDto> unitatsArbre = 
				unitatOrganitzativaHelper.unitatsOrganitzativesFindArbreByPareAndEstatVigent("A04003003");
		int reintents = 0;
		if (unitatsArbre != null) {
			logger.debug("Actualitzant els procediments de l'entitat " + entitatCodi + " amb " + unitatsArbre.toList().size() + " unitats.");
			List<ArbreNodeDto<UnitatOrganitzativaDto>> unitats = new ArrayList<ArbreNodeDto<UnitatOrganitzativaDto>>();
			unitats.add(unitatsArbre.getArrel());
			List<ArbreNodeDto<UnitatOrganitzativaDto>> nodesUosAmbError = new ArrayList<ArbreNodeDto<UnitatOrganitzativaDto>>();
			do {
				nodesUosAmbError = new ArrayList<ArbreNodeDto<UnitatOrganitzativaDto>>();
				for(ArbreNodeDto<UnitatOrganitzativaDto> unitat : unitats) {
					updateProcedimentsArbre(
							unitat, 
							nodesUosAmbError, 
							reintents, 
							max_intents);
				}
				unitats = nodesUosAmbError;
				reintents++;
			} while (reintents <= max_intents 
					&& !nodesUosAmbError.isEmpty());

			if (nodesUosAmbError.size() > 0) {
				// Llença excepció
				StringBuilder errMsg = new StringBuilder("No S'han pogut consultar i actualitzar correctament els procediments per les següents unitats organitzatives després de " + max_intents + " reintents :[");
				for (int i=0; i < nodesUosAmbError.size(); i++) {
					errMsg.append(nodesUosAmbError.get(i).getDades().getCodiAndNom());
					if (i < nodesUosAmbError.size()-1) {
						errMsg.append(", ");
					}
				}			
				errMsg.append("]");
				throw new Exception(errMsg.toString());
			}	
		}		
	}
	

	private void updateProcedimentsArbre(
			ArbreNodeDto<UnitatOrganitzativaDto> nodeUo, 
			List<ArbreNodeDto<UnitatOrganitzativaDto>> nodesUosAmbError, 
			int reintents, 
			int max_intents) {

		if (nodeUo != null) {
			UnitatOrganitzativaDto uoDto = nodeUo.getDades();	
			try {
				List<net.conselldemallorca.helium.integracio.plugins.procediment.Procediment> procediments = 
						pluginHelper.procedimentFindByCodiDir3(uoDto.getCodi());
				if (procediments != null && !procediments.isEmpty()) {
					updateProcediments(procediments, uoDto.getId());
				} else {
					logger.debug("No hi ha procediments associats al codiDir3 " + uoDto.getCodi());
				}
			}catch (Exception e) {
				logger.error("Error consultant els procediments de la UO " + uoDto.getCodiAndNom() + " intent " + reintents + " de " + max_intents + ": " + e.getMessage(), e);
				nodesUosAmbError.add(nodeUo);
			}
			if (nodeUo.getFills() != null) {
				for (ArbreNodeDto<UnitatOrganitzativaDto> fill : nodeUo.getFills()) {
					updateProcedimentsArbre(
							fill, 
							nodesUosAmbError, 
							reintents, 
							max_intents);
				}
			}
		}
	}
	

	@Transactional
	private void updateProcediments(
			List<net.conselldemallorca.helium.integracio.plugins.procediment.Procediment> procediments, 
			Long unitatOrganitzativaId) {
		UnitatOrganitzativa unitatOrganitzativa = unitatOrganitzativaRepository.findOne(unitatOrganitzativaId);
		
		for (net.conselldemallorca.helium.integracio.plugins.procediment.Procediment procediment: procediments) {
			if (procediment.getCodigo() != null 
					&& !procediment.getCodigo().isEmpty()) {
				Procediment Procediment = procedimentRepository.findByCodi(
																procediment.getCodigo());
				if (Procediment == null) {
					Procediment = net.conselldemallorca.helium.core.model.hibernate.Procediment.getBuilder(
							procediment.getCodigo(), 
							procediment.getNombre(), 
							procediment.getCodigoSIA(),
							ProcedimentEstatEnumDto.VIGENT,
							unitatOrganitzativa).built();
					
					procedimentRepository.save(Procediment);
				
				}else {		
					Procediment.update(
							procediment.getCodigo(), 
							procediment.getNombre(), 
							procediment.getCodigoSIA(),
							ProcedimentEstatEnumDto.VIGENT,
							unitatOrganitzativa);
					
					procedimentRepository.save(Procediment);
				}	
				
			}
		}
	}

	@Override
	@Transactional(readOnly = true)
	public ProcedimentDto findByCodiSia(String codiSia) {

		return conversioTipusHelper.convertir(
				procedimentRepository.findByCodiSia(
						codiSia), 
				ProcedimentDto.class);						
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProcedimentDto> findByNomOrCodiSia(String search) {
		if (search == null || search.isEmpty()) {
			return new ArrayList<ProcedimentDto>();
		}			
		return conversioTipusHelper.convertirList(
				procedimentRepository.findByNomOrCodiSia(
						search != null ? search : ""), 
				ProcedimentDto.class);
	}
	

	private static final Logger logger = LoggerFactory.getLogger(ProcedimentServiceImpl.class);

}
