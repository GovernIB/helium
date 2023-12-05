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
import net.conselldemallorca.helium.core.helper.ProcedimentHelper;
import net.conselldemallorca.helium.core.model.hibernate.UnitatOrganitzativa;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.procediment.ProcedimentDto;
import net.conselldemallorca.helium.v3.core.api.dto.procediment.ProcedimentFiltreDto;
import net.conselldemallorca.helium.v3.core.api.dto.procediment.ProgresActualitzacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.procediment.ProgresActualitzacioDto.NivellInfo;
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
	private ProcedimentHelper procedimentHelper;
	@Autowired
	private ProcedimentRepository procedimentRepository;
	@Autowired
	private UnitatOrganitzativaRepository unitatOrganitzativaRepository;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private PaginacioHelper paginacioHelper;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;

	/** Progrés d'acualització actual.*/
	private static ProgresActualitzacioDto progresActualitzacio = null;

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
	
	@Override
	@Transactional
	public void actualitzaProcediments() {
		

		ProgresActualitzacioDto progres = null;
		try {
			// Comprova si hi ha una altre instància del procés en execució
			if (isUpdatingProcediments()) {
				logger.debug("Ja existeix un altre procés que està executant l'actualització.");
				return;	// S'està executant l'actualitzacio
			} else {
				progresActualitzacio = new ProgresActualitzacioDto();
				progres = progresActualitzacio;

			}

			String msgInfo = "Inici del procés d'actualització de procediments";
			logger.info(msgInfo );
			progres.addInfo(msgInfo);
			
			// Consultar els procediments de l'entitat amb 3 reintents
			progres.addInfo("Obtenint tots els procediments per l'entitat " + "A04003003" + "...");
			List<net.conselldemallorca.helium.integracio.plugins.procediment.Procediment> procedimentsRolsac = null;
			int reintents = 1;
			boolean errorRolsac = false;
			Exception exRolsac = null;
			String errMsg = "-";
			do {
				try {
					procedimentsRolsac = pluginHelper.procedimentFindByCodiDir3("A04003003");
				} catch (Exception e) {
					exRolsac = e;
					errMsg = "Error consultant els procediments a Rolsac: " + exRolsac.toString();
					errorRolsac = reintents++ >= 3;
				}
			} 
			while (procedimentsRolsac == null 
					&& !errorRolsac);
			// Comprova si hi ha hagut errors consultant els procediments
			if (errorRolsac) {
				throw new Exception(errMsg, exRolsac);
			}
			if (procedimentsRolsac == null || procedimentsRolsac.isEmpty()) {
				throw new Exception("No s'ha obtingut cap llista o resultat per la consulta de procediments: (llista " 
										+ (procedimentsRolsac == null? "nul·la" :  "buida") + ")");
			}
			
			// Processa els procediments consultats
			progres.addInfo("S'han obtingut " + procedimentsRolsac.size() + " procediments vigents de Rolsac.");
			// Crea un Map amb els procediments de Rolsac per codi
			Map<String, net.conselldemallorca.helium.integracio.plugins.procediment.Procediment> procedimentsRolsacMap =
					new HashMap<String, net.conselldemallorca.helium.integracio.plugins.procediment.Procediment>();
			for (net.conselldemallorca.helium.integracio.plugins.procediment.Procediment procedimentRolsac 
					: procedimentsRolsac) {
				procedimentsRolsacMap.put(procedimentRolsac.getCodi(), procedimentRolsac);
			}
			
			// Deshabilita els procediments que no hagi retornat Rolsac
			procedimentHelper.actualtizarProcedimentsNoVigents(procedimentsRolsacMap, progres);

			// Processa tots els procediments, actualitza-ne la informació, donant-los d'alta i revisant la seva UO
			progres.setNumOperacions(procedimentsRolsac.size());
			progres.addInfo("Es procedeix a processar els " + progres.getNumOperacions() + " uprocediments consultats a Rolsac.");

			// Map<codi unitat rolsac, unitatOrganitzativa> per no haver de consultar la UO de totes les unitats per codi rolsac
			Map<String, UnitatOrganitzativa> unitatsOrganitzatives = new HashMap<String, UnitatOrganitzativa>();
			for (net.conselldemallorca.helium.integracio.plugins.procediment.Procediment procedimentRolsac : procedimentsRolsac) {
				// Tracata el procediment en una transacció a part.
				procedimentHelper.actualitzaProcediment(procedimentRolsac, unitatsOrganitzatives, progres);
			}
			// Posar el resultat del progrés i acabar
			progres.addInfo("Fi del procés de sincronització", "El procés d'actualització ha finalitzat correctament", NivellInfo.INFO, null, false);
		} catch(Exception e) {
			String errMsg = "Error sincronitzant els procediments: " + e.toString();
			logger.error(errMsg, e);
			if (progres != null ) {
				progres.addInfo(errMsg);
				progres.setError(true);
				progres.setErrorMsg(errMsg);
			}
			throw new RuntimeException(errMsg, e);
		} finally {
			// Marca el procés com a finalitzat
			if (progres != null) {
				progres.setFinished(true);
			}
			logger.info("Fi de la sincronització de procediments.");
			if (progres != null) {
				logger.info("- Resultat: " + (progres.isError() ? "Error: " + progres.getErrorMsg() : "Correcte"));
				logger.info("- Elements actualitzats: " + progres.getNumElementsActualitzats());
				logger.info("- Elements actualitzats: " + progres.getNumElementsActualitzats());
				logger.info("- Avisos: " + progres.getAvisos().size());
				for (String avis : progres.getAvisos()) {
					logger.warn("\t- " + avis);
				}
			}
		}
	}



	@Override
	public boolean isUpdatingProcediments() {
		ProgresActualitzacioDto progres = progresActualitzacio;
		return progres != null && !progres.isError() && !progres.isFinished();
	}

	@Override
	public ProgresActualitzacioDto getProgresActualitzacio() {
		return progresActualitzacio;
	}

	
	private static final Logger logger = LoggerFactory.getLogger(ProcedimentServiceImpl.class);
}
