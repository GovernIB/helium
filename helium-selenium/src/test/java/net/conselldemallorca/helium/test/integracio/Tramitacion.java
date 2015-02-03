package net.conselldemallorca.helium.test.integracio;


import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.activation.FileDataSource;

import net.conselldemallorca.helium.test.util.BaseTest;
import net.conselldemallorca.helium.ws.tramitacio.CampTasca;
import net.conselldemallorca.helium.ws.tramitacio.DocumentTasca;
import net.conselldemallorca.helium.ws.tramitacio.ParellaCodiValor;
import net.conselldemallorca.helium.ws.tramitacio.TascaTramitacio;
import net.conselldemallorca.helium.ws.tramitacio.TramitacioException;
import net.conselldemallorca.helium.ws.tramitacio.TramitacioService;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Tramitacion extends BaseTest {
	private TramitacioService clientTramitacio = null;
	String entorn = carregarPropietat("tramsel.entorn.nom", "Nom de l'entorn de proves no configurat al fitxer de properties");
	String titolEntorn = carregarPropietat("tramsel.entorn.titol", "Titol de l'entorn de proves no configurat al fitxer de properties");
	String nomDefProc = carregarPropietat("defproc.deploy.definicio.proces.nom", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String usuari = carregarPropietat("test.base.usuari.configuracio", "Usuari configuració de l'entorn de proves no configurat al fitxer de properties");
	String nomSubDefProc = carregarPropietat("defproc.deploy.definicio.subproces.nom", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String exportTipExp = carregarPropietatPath("tipexp.tasca_dades_serv_tram.exp.export_flux.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String nomTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.nom", "Nom del tipus d'expedient de proves no configurat al fitxer de properties");
	String codTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.codi", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	String filename = carregarPropietatPath("deploy.arxiu.portasignatures", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	
	@BeforeClass
	public static void beforeClass() {
		System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.Log4JLogger");
		System.setProperty("org.apache.commons.logging.LogFactory", "org.apache.commons.logging.impl.LogFactoryImpl");
	}
	
	@Test
	public void a0_inicialitzacio() {
		carregarUrlConfiguracio();
		crearEntorn(entorn, titolEntorn);
		assignarPermisosEntorn(entorn, usuari, "DESIGN", "ORGANIZATION", "READ", "ADMINISTRATION");
		seleccionarEntorn(titolEntorn);
		crearTipusExpedient(nomTipusExp, codTipusExp);
		assignarPermisosTipusExpedient(codTipusExp, usuari, "DESIGN","CREATE","SUPERVISION","WRITE","MANAGE","DELETE","READ","ADMINISTRATION");
	}
	
	@Test
	public void a_crear_dades() throws InterruptedException {
		carregarUrlConfiguracio();
		
		seleccionarEntorn(titolEntorn);
		
		importarDadesTipExp(codTipusExp, exportTipExp);
		
		screenshotHelper.saveScreenshot("tramitar/dadesexpedient/crear_dades/1.png");
	}

	@Test
	public void b_main() throws InterruptedException {
		try {
			setUpBeforeClass();	        			
			new Tramitacion().tramitacion();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private void tramitacion() {		
		try {
			String processInstanceId = getClientTramitacio().iniciExpedient(
					entorn, // entorn,
					usuari,
					codTipusExp, //tipo de expediente
					null, //String numero,
					"Expedient de prova Selenium " + (new Date()).getTime(), //String titol,
					null //List<ParellaCodiValor> valorsFormulari
			);
			
			// Comprobamos que se ha iniciado
			assertTrue("El expediente no se inició correctamente" , processInstanceId != null);
			
			List<TascaTramitacio> listaTareasPersonales = getClientTramitacio().consultaTasquesPersonals(entorn, usuari);
			assertTrue("Había una tarea personal" , listaTareasPersonales == null || !listaTareasPersonales.isEmpty());
			
			// Cogemos la tarea de grupo
			List<TascaTramitacio> listaTareasGrupo = getClientTramitacio().consultaTasquesGrup(entorn, usuari);
			boolean agafar = false;
			if (listaTareasGrupo != null) {
				for (TascaTramitacio tasca : listaTareasGrupo) {
					getClientTramitacio().agafarTasca(entorn, usuari, tasca.getId());
					agafar = true;
				}
			}
			
			assertTrue("No se agafó la tarea" , agafar);
			
			TascaTramitacio tascaTramitacio = null;
			listaTareasPersonales = getClientTramitacio().consultaTasquesPersonals(entorn, usuari);
			if (listaTareasPersonales != null) {
				for (TascaTramitacio tasca : listaTareasPersonales) {
					if (processInstanceId.equals(tasca.getProcessInstanceId())) {
						tascaTramitacio = tasca;
						break;
					}
				}
			}
			
			assertTrue("No se encontró la tarea" , tascaTramitacio != null);
			
			List<ParellaCodiValor> listParellaCodiValor = new ArrayList<ParellaCodiValor>();
			List<CampTasca> campsTasca = getClientTramitacio().consultaFormulariTasca(entorn, usuari, tascaTramitacio.getId());
			for (CampTasca camp : campsTasca) {
				if ("BOOLEAN".equals(camp.getTipus())) {
					listParellaCodiValor.add(new ParellaCodiValor(camp.getCodi(), Boolean.TRUE));
				} else if ("DATE".equals(camp.getTipus())) {
					listParellaCodiValor.add(new ParellaCodiValor(camp.getCodi(), new Date()));
				} else if ("STRING".equals(camp.getTipus()) && camp.isMultiple()) {
					List<String> aMult = new ArrayList<String>();
					aMult.add("Texto 1");
					aMult.add("Texto 2");
					aMult.add("Texto 3");
					listParellaCodiValor.add(new ParellaCodiValor(camp.getCodi(), aMult));
				} else if ("STRING".equals(camp.getTipus()) || "TEXTAREA".equals(camp.getTipus())) {
					listParellaCodiValor.add(new ParellaCodiValor(camp.getCodi(), "Un texto"));
				} else if ("TERMINI".equals(camp.getTipus())) {
					listParellaCodiValor.add(new ParellaCodiValor(camp.getCodi(), "12/4/2"));
				}
			}
			
			tascaTramitacio.getTransicionsSortida();
			
			// Validamos
			getClientTramitacio().setDadesFormulariTasca(entorn, usuari, tascaTramitacio.getId(), listParellaCodiValor);
			
			// Los documentos
			FileDataSource ds = new FileDataSource(filename);

			byte[] contingut = IOUtils.toByteArray(ds.getInputStream());
			getClientTramitacio().setDocumentTasca(entorn, usuari, tascaTramitacio.getId(), "documento_1", "nombre", new Date(), contingut);		
					
			List<DocumentTasca> listaDocumentos = getClientTramitacio().consultaDocumentsTasca(entorn, usuari, tascaTramitacio.getId());
			boolean contieneDocumento = false;
			if (listaDocumentos != null) {
				for (DocumentTasca documento : listaDocumentos) {
					if ("documento_1".equals(documento.getCodi()) && !documento.getArxiu().isEmpty()) {
						contieneDocumento = true;
					}		
				}
			}
			
			assertTrue("No se encontró el documento en la tarea" , contieneDocumento);
			
			// Finalizamos
			getClientTramitacio().finalitzarTasca(entorn, usuari, tascaTramitacio.getId(), null);
			
			assertTrue("Error al tramitar la tarea", !tascaTramitacio.getCodi().isEmpty());
		} catch (TramitacioException e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
		} 
		catch (IOException e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
		}
	}

	@Test
	public void z_limpiar() throws InterruptedException {
		carregarUrlConfiguracio();
		
		seleccionarEntorn(titolEntorn);
		
		eliminarExpedient(null, null, nomTipusExp);
		
		// Eliminar la def de proceso
		eliminarDefinicioProces(nomDefProc);
		eliminarDefinicioProces(nomSubDefProc);
		
		// Eliminar el tipo de expediente
		eliminarTipusExpedient(codTipusExp);
		
		eliminarEntorn(entorn);
		
		screenshotHelper.saveScreenshot("TasquesDadesTasca/finalizar_expedient/1.png");	
	}

	private TramitacioService getClientTramitacio() {
		if (clientTramitacio == null) {
			String urlEndPoint = properties.getProperty("app.tramitacion.url");
			clientTramitacio = (TramitacioService)net.conselldemallorca.helium.test.integracio.utils.WsClientUtils.getWsClientProxy(
				TramitacioService.class,
				urlEndPoint,
				null,
				null,
				"NONE",
				false,
				true,
				false);
		}
		return clientTramitacio;
	}

	private static Logger logger = Logger.getLogger(Tramitacion.class);
}
