package net.conselldemallorca.helium.test.integracio;

import static org.junit.Assert.assertTrue;
import net.conselldemallorca.helium.test.util.BaseTest;
import net.conselldemallorca.helium.ws.backoffice.BantelV3BackofficeServiceLocator;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;

import ReferenciaEntrada.model.v2.ws.bantel.caib.es.ReferenciaEntrada;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Sistra extends BaseTest {
	
						//EXP.1.2.5 - Comprovar integració amb tramits sistra
	
	/**
	*   A C T I V A R   A   T R U E   L A   P R O P I E D A D:   app.selenium.ws.integracion del archivo helium.properties del modulo helium-webapp
	*/
	
	String entorn		= carregarPropietat("tipexp.integracio.sistra.entorn.nom", "Nom de l'entorn de proves no configurat al fitxer de properties");
	String titolEntorn	= carregarPropietat("tipexp.integracio.sistra.entorn.titol", "Titol de l'entorn de proves no configurat al fitxer de properties");
	
	String usuari		= carregarPropietat("test.base.usuari.configuracio", "Usuari configuració de l'entorn de proves no configurat al fitxer de properties");
	String usuariDis	= carregarPropietat("test.base.usuari.disseny", "Usuari configuració de l'entorn de proves no configurat al fitxer de properties");
	
	String nomTipusExp	= carregarPropietat("tipexp.integracio.sistra.tipus.expedient.nom", "Nom del tipus d'expedient de proves no configurat al fitxer de properties");
	String codTipusExp	= carregarPropietat("tipexp.integracio.sistra.tipus.expedient.codi", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	
	String pathExport	= carregarPropietatPath("tipexp.integracio.sistra.deploy.arxiu.path", "Ruta de l´arxiu del tipus d´expedient exportat no configurat al fitxer de properties");
	String nomDefProc	= "Cons1";
	
	// XPATHS
	String linkAccesInfoDefProc		= "//*[@id='registre']/tbody/tr[contains(td/a, '"+nomDefProc+"')]/td/a[contains(@href, '/definicioProces/info.html')]";
	String pestanyaTasquesDefProc  	= "//*[@id='tabnav']/li/a[contains(@href, '/definicioProces/tascaLlistat.html')]";
	String botoModificarTasca		= "//*[@id='command']/div[@class='buttonHolder']/button[text() = 'Modificar']";
	
	String botoConsultarExpLlistat = "//*[@id='command']/div/div[@class='buttonHolder']/button[text() = 'Consultar']";
	String linkAccedirExpedient = "//*[@id='registre']/tbody/tr[1]/td/a[contains(@href, '/tasca/personaLlistat.html')]";
	String linkAccedirTasca = "//*[@id='registre']/tbody/tr/td[1]/a";
	
	String botoObrirFormExtern = "//*[@id='content']/form/button";
	
	String enllaçTipusExpLlistat = "//*[@id='registre']/tbody/tr/td[1]/a[contains(@href, '/expedientTipus/info.html')]";
	String pestanyaDefProcExpedient = "//*[@id='tabnav']/li/a[contains(@href, '/expedientTipus/definicioProcesLlistat.html')]";
	String botoMarcaProcesInicial = "//*[@id='registre']/tbody/tr/td/form[contains(@action, '/expedientTipus/definicioProcesInicial.html')]/button[contains(text(), 'inicial')]";
	
	String iframeFormExt = "//*[@id='formExtern']";
	String urlFormExt = "http://localhost:8080/helium/ws/IniciFormulari";
	String idFormExt = "command";
	String botoGuardarDadesIntegracio = "//*[@id='command']/div[@class='buttonHolder']/button[text() = 'Guardar']";
	
	String aspaTancarDialogForm = "/html/body/div[@class='ui-dialog']/div[@class='ui-dialog-titlebar']/a";
	
	String variableResultatRetornFormulari = "//*[@id='command']/div/div/label[text() = 'Variable Boolean']";
	
	//XPATHS
	//String botoDesplegarDefProc	= "//*[@id='command']/div[@class='buttonHolder']/button[text() = 'Guardar']";	
	
	String botoGuardaNomTramit	= "//*[@id='command']/div[@class='buttonHolder']/button[text() = 'Guardar']";
	String dataCreacioExpedient = "//*[@id='registre']/tbody/tr/td[4]";
	String linkInfoExpedient	= "//*[@id='registre']/tbody/tr[1]/td/a[contains(@href, '/expedient/info.html')]";
	
	String linkDocuments		= "//*[@id='tabnav']/li/a[contains(@href, '/expedient/documents.html')]";
	String tdDocument1			= "//*[@id='codi']/tbody/tr/td[contains(text(), 'Nom document 1')]";
	String tdDocument2			= "//*[@id='codi']/tbody/tr/td[contains(text(), 'SitraCodiAdjunt')]";
	
	String linkDades			= "//*[@id='tabnav']/li/a[contains(@href, '/expedient/dades.html')]";
	String tdDadaMapejada		= "//*[@id='codi']/tbody/tr/td[contains(text(), 'Etiqueta modificada')]";
	String tdValorDadaMapejada	= "//*[@id='codi']/tbody/tr/td[contains(text(), '7122')]";

	@Test
	public void a0_inicialitzacio() {
		carregarUrlConfiguracio();
		crearEntorn(entorn, titolEntorn);
		assignarPermisosEntorn(entorn, usuari, "DESIGN", "ORGANIZATION", "READ", "ADMINISTRATION");
		assignarPermisosEntorn(entorn, usuariDis, "DESIGN", "ORGANIZATION", "READ", "ADMINISTRATION");
		seleccionarEntorn(titolEntorn);
		crearTipusExpedient(nomTipusExp, codTipusExp);
		importarDadesTipExp(codTipusExp, pathExport);
		assignarPermisosTipusExpedient(codTipusExp, usuari,    "DESIGN","CREATE","SUPERVISION","WRITE","MANAGE","DELETE","READ","ADMINISTRATION");
		assignarPermisosTipusExpedient(codTipusExp, usuariDis, "DESIGN","CREATE","SUPERVISION","WRITE","MANAGE","DELETE","READ","ADMINISTRATION");
		//accedirPipellaDefProcExpedient(codTipusExp);
	}
	
	@Test
	public void b1_prova_integracio() {

		//Assignar DefProc a l´expedient que contingui alguna tasca inicial amb variables
		//accedirPipellaDefProcExpedient(codTipusExp);
		//driver.findElement(By.id("codiTramit0")).clear();
		
		/*accedirPipellaSistraExpedient(codTipusExp);
		driver.findElement(By.id("codiTramit0")).clear();
		driver.findElement(By.id("codiTramit0")).sendKeys("TramitIntegracio4321");
		driver.findElement(By.xpath(botoGuardaNomTramit)).click();
		existeixElementAssert("//*[@class='missatgesOk']", "No s'ha pogut canviar el nom al tramit sistra.");*/
		
		/**
		 * 2.- Provocamos la llamada de aviso de entradas de tramites finalizados de Sistra a Helium.
		 * Para ello usamos el cliente WS creado en helium-selenium-integraciones para que realize la llamada 
		 * http://localhost:8080/helium/ws/NotificacioEntradaV3?wsdl
		 */
		
		BantelV3BackofficeServiceLocator banTelSL = null;
		
		ReferenciaEntrada[] refEnt = new ReferenciaEntrada[1];
		ReferenciaEntrada re = new ReferenciaEntrada("1", "clave");
		refEnt[0] = re;
		
		try {
			banTelSL = new BantelV3BackofficeServiceLocator();
			banTelSL.getBantelV3BackofficePort().avisoEntradas(refEnt);
		}catch (Exception ex) {
			System.out.println(" # # Ha fallado la llamada a  BantelV3.avisoEntradas() # #  ");
			ex.printStackTrace();
		}
		
		carregarUrlDisseny();
		seleccionarEntorn(titolEntorn);
		
		consultarExpedientes(null, null, codTipusExp);
		
		screenshotHelper.saveScreenshot("integracions/sistra/b1_1_conprova_expedient-llistat_inicial.png");
		
		// 3.- Anam a cercar l´expedient iniciat a comprovar dades y documents adjunts
		driver.findElement(By.xpath(linkInfoExpedient)).click();
		
		driver.findElement(By.xpath(linkDades)).click();
		screenshotHelper.saveScreenshot("integracions/sistra/b1_1_conprova_expedient-dades.png");
			//Comprovam que existeixen les dades
			assertTrue("No s´ha trobat la dada: Etiqueta Modificada", driver.findElement(By.xpath(tdDadaMapejada))!=null);
			assertTrue("No s´ha trobat el valor de la dada *Etiqueta Modificada* : 7122", driver.findElement(By.xpath(tdValorDadaMapejada))!=null);
		
		driver.findElement(By.xpath(linkDocuments)).click();
		screenshotHelper.saveScreenshot("integracions/sistra/b1_1_conprova_expedient-documents.png");
			//Comprovam que existeixen els documents
			assertTrue("No s´ha trobat el document: Nom document 1", driver.findElement(By.xpath(tdDocument1))!=null);
			assertTrue("No s´ha trobat el document: Nom document 1", driver.findElement(By.xpath(tdDocument2))!=null);
	}
	
	@Test
	public void z0_limpiar() {
		
		carregarUrlConfiguracio();
		
		seleccionarEntorn(titolEntorn);
		
		// Eliminam tots els expedients
		consultarExpedientes(null, null, null, false);
		while (existeixElement("//*[@id='registre']/tbody/tr[1]/td[contains(a/img/@src,'/helium/img/cross.png')]/a/img")) {
			borrarPrimerExpediente();
		}

		eliminarTipusExpedient(codTipusExp);		
		eliminarEntorn(entorn);
	}
}