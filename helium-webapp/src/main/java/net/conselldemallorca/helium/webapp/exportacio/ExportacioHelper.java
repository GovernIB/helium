package net.conselldemallorca.helium.webapp.exportacio;

import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import net.conselldemallorca.helium.core.model.hibernate.Alerta;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.v3.core.api.dto.CampAgrupacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDadaDto;
import net.conselldemallorca.helium.v3.core.api.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientDadaService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.v3.core.repository.ExpedientRepository;

@Component
public class ExportacioHelper {
	
	private static final Log logger = LogFactory.getLog(ExportacioHelper.class);
	
	@Autowired
	private ExpedientService expedientService;
	
	@Autowired
	private ExpedientDadaService expedientDadaService;
	
	@Autowired
	private ExpedientRepository expedientRepository;
	
	//@Autowired
	private RestTemplate restTemplate;
	
	private List<Expedient> expedients;
	
	public void exportarExpedients() throws Exception {
		
		try {
			logger.info("EXPORTANT EXPEDIENTS");
			restTemplate = new RestTemplate();
			expedients = expedientRepository.findAll();
	//		logger.info("Expedients " + expedients.size());
	//		List<ExpedientExportacio> expedientsExportacio = prepararExpedients(expedients);
	//		logger.info("Enviant al micro-servei: ");
	//		URI uri = URI.create(GlobalProperties.getInstance().getProperty("app.exportacio.expedients.url.servei"));
	//		restTemplate.postForEntity(uri, expedientsExportacio, ResponseEntity.class);	
	
		} catch (Exception ex) {
			throw new Exception("Error al exportar els expedients", ex);
		}
	}
	
	private List<ExpedientExportacio> prepararExpedients(List<Expedient> expedients) {
		
		List<ExpedientExportacio> expedientsExportacio = new ArrayList<ExpedientExportacio>();
		long tempsInicial = System.currentTimeMillis();
		int size = expedients.size();
		//TODO reduir efficiencia del for = reduir drasticament temps exportacio
		//for (Expedient expedient : expedients) {
		for (int foo = 0;foo < size; foo++)	{
			Expedient expedient = expedients.get(foo);
			ExpedientExportacio exp = new ExpedientExportacio();
			exp.setId(expedient.getId());
			exp.setEntornId(expedient.getEntorn().getId());
			exp.setExpedientTipusId(expedient.getTipus().getId());
			exp.setProcessInstanceId(expedient.getProcessInstanceId());
			exp.setNumero(expedient.getNumero() != null ? expedient.getNumero() : expedient.getNumeroDefault());
			exp.setTitol(expedient.getTitol());
			exp.setDataInici(expedient.getDataInici());
			exp.setDataFi(expedient.getDataFi());
			
			//Actualment si HEL_EXPEDIENT.ESTAT_ID és NULL llavors si té data fi és FINALITZAT, 
			//si no té data fi INICIAT 
			//i si ESTAT_ID és una FK a HEL_ESTAT llavors és un estat definit a nivell de tipus d'expedient (CUSTOM)
			
			exp.setEstatTipus(ExpedientEstatTipusEnum.INICIAT);
			if (expedient.getEstat() == null && expedient.getDataFi() != null) {
				exp.setEstatTipus(ExpedientEstatTipusEnum.FINALITZAT);
			}
			
			if (expedient.getEstat() != null) {
				exp.setEstatTipus(ExpedientEstatTipusEnum.CUSTOM);
				exp.setEstatId(expedient.getEstat().getId());
			}
			
			exp.setAturat(expedient.isAturat());
			exp.setInfoAturat(expedient.getInfoAturat());
			exp.setAnulat(expedient.isAnulat());
			exp.setComentariAnulat(expedient.getComentariAnulat());
			exp.setAlertesTotals(Long.valueOf(expedient.getAlertes().size()));
			Object[] alertes = expedient.getAlertes().toArray();
			int pendents = 0;
			for (int bar=0;bar<alertes.length;bar++) {
				if (((Alerta)alertes[bar]).getDataEliminacio() == null) {
					pendents++;
				}
			}
			exp.setAlertesPendents(Long.valueOf(pendents));
			
			//Per exemple, només amb error es traduiria a  mirar si l'expedien té descripció d'error, 
			//errors d'integració  o de reindexació (la reindexació desapareix)
			
			exp.setAmbErrors(expedient.getErrorDesc() != null || expedient.isErrorsIntegracions());
			expedientsExportacio.add(exp);
		}
		long tempsFinal = System.currentTimeMillis();
		logger.info("Fi preparacio expedients - Duracio: " +  (tempsFinal - tempsInicial) + " milliseconds");
		return expedientsExportacio;
	}
	
	public void exportarTasques() throws Exception {
		
		if (expedients == null) {
			logger.error("No s'exporten tasques. No hi han expedients");
			return;
		}
		
		String dbURL = "jdbc:oracle:thin:@10.35.3.242:1521:ORCL";
		String username = "helium";
		String password = "helium";
		Connection conn = DriverManager.getConnection(dbURL, username, password);
		try {
			
			List<TascaExportacio> tasques = new ArrayList<TascaExportacio>();
			for (Expedient expedient : expedients) {
				
				logger.info("Expedient: " + expedient.getId());
				
				/*
				Per emplenar es.caib.helium.expedient.model.TascaDto i donar d'alta tasques tens les següents taules d'Helium:
					- JBPM_TASK_INSTANCE: taula amb les instàncies de les tasques a partir de la qual començar la consulta, 
						conté la major informació. Si està assignada crec recordar que és el camp ACTOR_ID. 
						Amb el camp PROCESS_ID pots fer un inner join cap a l'expedient 
						i amb le camp TASK cap a la definició de la tasca a JBPM_TASK
					
					- JPBM_TASK és la definició de la tasca dins JBPM i conté el codi (nom) de la tasca
						que no té per què ser igual al títol de la tasca JBPM_TASK_INSTANCE.name.
					
					- JBPM_TASKACTPOOL: no em vull equivocar però si la tasca està assignada a un grup 
						llavors es relaciona la instància de la tasca amb un pool d'actors però no trobaràs la informació del grup assignat enlloc, 
						així que pots deixar-la buida de moment!
				*/
				
				PreparedStatement ps = conn.prepareStatement("SELECT"
						+ "	jt.NAME_ AS nom,\n"
						+ "	jti.NAME_ AS titol,\n"
						+ "	jti.ACTORID_,\n"
						+ "	jti.ISCANCELLED_ ,\n"
						+ "	jti.ISSUSPENDED_ ,\n"
						+ "	jti.MARCADAFINALITZAR_,\n"
						+ "	jti.ERRORFINALITZACIO_,\n"
						+ "	jti.DUEDATE_,\n"
						+ "	jti.END_,\n"
						+ "	jti.INICIFINALITZACIO_,\n"
						+ "	jti.CREATE_,\n"
						+ "	he.RESPONSABLE_CODI\n"
						+ "FROM JBPM_TASKINSTANCE jti\n"
						+ "INNER JOIN JBPM_TASK jt ON jt.ID_ = TASK_ \n"
						+ "INNER JOIN HEL_EXPEDIENT he on he.PROCESS_INSTANCE_ID = PROCINST_  \n"
						+ "WHERE he.ID = ?");
				ps.setLong(1, expedient.getId());
				ResultSet rs = ps.executeQuery();
				try {
					// TODO REVISAR CAMPS COMENTATS I CAMPS QUE ES COMPAREN AMB NULL
					while (rs.next()) {
						TascaExportacio tasca = new TascaExportacio();
						tasca.setExpedientId(expedient.getId());
						tasca.setNom(rs.getString("nom"));
						tasca.setTitol(rs.getString("titol"));
						tasca.setAfagada(rs.getString("ACTORID_") != null);
						tasca.setCancelada(rs.getBoolean("ISCANCELLED_"));
						tasca.setSuspesa(rs.getBoolean("ISSUSPENDED_"));
						//tasca.setCompletada();
						//tasca.setAssignada();
						tasca.setMarcadaFinalitzar(rs.getDate("MARCADAFINALITZAR_") != null);
						tasca.setErrorFinalitzacio(rs.getString("ERRORFINALITZACIO_") != null);
						tasca.setDataFins(rs.getDate("DUEDATE_"));
						tasca.setDataFi(rs.getDate("END_"));
						tasca.setIniciFinalitzacio(rs.getDate("INICIFINALITZACIO_"));
						tasca.setDataCreacio(rs.getDate("CREATE_"));
						tasca.setUsuariAssignat(rs.getString("RESPONSABLE_CODI"));
						//tasca.setGrupAssignat();
						
						tasques.add(tasca);
					}
				} catch (Exception ex) {
					throw new Exception("Error al llegit el result set", ex);
				} finally {
					rs.close();
					ps.close();
				}
			
			}
			
			logger.info("Tasques exportades - Cridant el microservei " + tasques.size());
			
			//URI uri = URI.create(GlobalProperties.getInstance().getProperty("app.exportacio.tasques.url.servei"));
			//restTemplate.postForEntity(uri, tasques, ResponseEntity.class);	
			
		
		} catch (Exception ex) {
			throw new Exception("Error al exportar les tasques", ex);
		} finally  {
			conn.close();
		}
	}
	
	public void exportarDades() throws Exception {
		
		if (expedients == null) {
			logger.error("No s'exporten dades. No hi han expedients");
			return;
		}

		try {
			for (Expedient expedient : expedients) {
				
				logger.info("Expedient: " + expedient.getId());
				
				List<InstanciaProcesDto> arbreProcessos = expedientService.getArbreInstanciesProces(Long.parseLong(expedient.getProcessInstanceId()));
				Map<InstanciaProcesDto, Map<CampAgrupacioDto, List<ExpedientDadaDto>>> dades = new LinkedHashMap<InstanciaProcesDto, Map<CampAgrupacioDto,List<ExpedientDadaDto>>>();
				Map<InstanciaProcesDto,Integer> totalsPerProces = new LinkedHashMap<InstanciaProcesDto, Integer>();
				// Per a cada instància de procés ordenem les dades per agrupació  
				// (si no tenen agrupació les primeres) i per ordre alfabètic de la etiqueta
				for (InstanciaProcesDto instanciaProces: arbreProcessos) {
					Map<CampAgrupacioDto, List<ExpedientDadaDto>> dadesInstancia = null;
					int contadorTotals = 0;
					if (!instanciaProces.getId().equals(expedient.getProcessInstanceId())) {
						continue;
					}
					dadesInstancia = getDadesInstanciaProces(expedient.getId(), instanciaProces.getId(), true);
					if (dadesInstancia == null) {
						continue;
					}
					for(List<ExpedientDadaDto> list: dadesInstancia.values()){
						contadorTotals += list.size();
					}
					dades.put(instanciaProces, dadesInstancia);
				}
			}
			
			logger.info("Dades exportades - Cridant el microservei");
		
		} catch (Exception ex) {
			throw new Exception("Error al exportar les dades", ex);
		}
	}
	
	/** Retorna les dades de la instància de procés agrupades per agrupació. S'ha de tenir en compte
	 * que les agrupacions poden estar sobreescrites, per tant preval la agrupació sobreescrita del fill
	 * i s'ha de determinar la agrupació pel codi en comptes de l'identificador. 
	 * @param expedientId
	 * @param instaciaProcesId
	 * @param ambOcults
	 * @return
	 */
	private Map<CampAgrupacioDto, List<ExpedientDadaDto>> getDadesInstanciaProces(
			Long expedientId,
			String instaciaProcesId,
			boolean ambOcults) {		
		// definirem un mapa. La clau serà el nom de l'agrupació, i el valor el llistat de variables de l'agrupació
		Map<CampAgrupacioDto, List<ExpedientDadaDto>> dadesProces = new TreeMap<CampAgrupacioDto, List<ExpedientDadaDto>>(
				// Comparador d'ordre d'agrupacions, primer la null, després heretades i finalment pròpies
				new Comparator<CampAgrupacioDto>() {
                    @Override
                    public int compare(CampAgrupacioDto a1, CampAgrupacioDto a2) {
                    	// El null va davant
                    	if (a1 == null && a2 == null)
                    		return 0;
                    	else if (a1 == null)
                    		return -1;
                    	else if (a2 == null )
                    		return 1;
                    	else {
                    		// Després van els heretats
                    		if (a1.isHeretat() && a2.isHeretat())
                    			return Integer.compare(a1.getOrdre(), a2.getOrdre());
                    		else if (a1.isHeretat() && !a2.isHeretat())
                    			return -1;
                    		else if (!a1.isHeretat() && a2.isHeretat())
                    			return 1;
                    		else
                    			// Si no retorna l'ordre normal
                    			return Integer.compare(a1.getOrdre(), a2.getOrdre());
                    	}
                    }
                });
		// Obtenim les dades de la definició de procés
		List<ExpedientDadaDto> dadesInstancia = expedientDadaService.findAmbInstanciaProces(
				expedientId,
				instaciaProcesId);
		if (dadesInstancia == null || dadesInstancia.isEmpty())
			return null;
		
		// Obtenim les agrupacions de la definició de procés o del tipus d'expedient
		List<CampAgrupacioDto> agrupacions = expedientDadaService.agrupacionsFindAmbInstanciaProces(
				expedientId,
				instaciaProcesId);

		// Construeix els maps per poder recuperar fàcilment les agrupacions per id i per codi
		Map<Long, CampAgrupacioDto> mapAgrupacionsPerId = new HashMap<Long, CampAgrupacioDto>();
		Map<String, CampAgrupacioDto> mapAgrupacionsPerCodi = new HashMap<String, CampAgrupacioDto>();
		resoldreAgrupacionsSobreescrites(agrupacions, mapAgrupacionsPerId, mapAgrupacionsPerCodi);
		
		// Agrupa les dades per agrupacions
		CampAgrupacioDto agrupacio;
		List<ExpedientDadaDto> dades;
		for (ExpedientDadaDto dada: dadesInstancia) {
			if (ambOcults || !dada.isCampOcult()) {
				// resol la agrupació
				if (dada.getAgrupacioId() == null)
					agrupacio = null;
				else {
					agrupacio = mapAgrupacionsPerCodi.get( mapAgrupacionsPerId.get(dada.getAgrupacioId()).getCodi() );
				}
				// Esbrina la llista
				if (dadesProces.containsKey(agrupacio)) { 
					dades = dadesProces.get(agrupacio);
				} else { //if agrupacio of current dada iteration changed or first dada iteration
					dades = new ArrayList<ExpedientDadaDto>();
					dadesProces.put(agrupacio, dades);
				}
				// Afegeix la dada
				dades.add(dada);
			}
		}
		
		for (Map.Entry<CampAgrupacioDto, List<ExpedientDadaDto>> entry : dadesProces.entrySet()) {
		    Collections.sort(entry.getValue(), new Comparator<ExpedientDadaDto>() {
		    	@Override
		    	public int compare(ExpedientDadaDto a1, ExpedientDadaDto a2) {
		    		// El null va davant
		    		if (a1 == null && a2 == null)
		    			return 0;
		    		else if (a1 == null)
		    			return -1;
		    		else if (a2 == null )
		    			return 1;
		    		else {
		    			// Si no retorna l'ordre normal
		    			return Integer.compare(a1.getOrdre(), a2.getOrdre());
		    		}
		    	}
		    });
		}
		
		return dadesProces;	
	}
	
	/** Mètode per posar totes les agrupacions en el map per identificador i posar només
	 * les agrupacions que no estan sobreescrites en el map per codi.
	 * @param agrupacions Totes les agrupacions
	 * @param mapAgrupacionsPerId Map on es posaran les agrupacions per id
	 * @param mapAgrupacionsPerCodi Map on es posaran per codi les agrupacions no sobreescrites
	 */
	private void resoldreAgrupacionsSobreescrites(
			List<CampAgrupacioDto> agrupacions,
			Map<Long, CampAgrupacioDto> mapAgrupacionsPerId, 
			Map<String, CampAgrupacioDto> mapAgrupacionsPerCodi) {

		// Afegeix les agrupacions per id i guarda el codi de les sobreescrites
		Set<String> codisSobreescrites = new HashSet<String>();
		for (CampAgrupacioDto agrupacio : agrupacions) {
			mapAgrupacionsPerId.put(agrupacio.getId(), agrupacio);
			if (agrupacio.isSobreescriu())
				codisSobreescrites.add(agrupacio.getCodi());
		}
		// Construeix el map de les agrupacions per codi no sobreescrites
		mapAgrupacionsPerCodi.put(null, null);
		for (CampAgrupacioDto agrupacio : agrupacions) 
			if (!codisSobreescrites.contains(agrupacio.getCodi()) || agrupacio.isSobreescriu())
				mapAgrupacionsPerCodi.put(agrupacio.getCodi(), agrupacio);
	}
}
