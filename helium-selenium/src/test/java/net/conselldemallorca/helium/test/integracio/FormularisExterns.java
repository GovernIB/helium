package net.conselldemallorca.helium.test.integracio;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import net.conselldemallorca.helium.test.util.BaseTest;
import net.conselldemallorca.helium.ws.formulari.GuardarFormulari;
import net.conselldemallorca.helium.ws.formulari.ParellaCodiValor;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FormularisExterns extends BaseTest {

							//EXP.10.4 - Integracions
								//EXP.10.4.1 - Formularis externs
									//EXP.10.4.1.1 - Desenvolupament aplicacio externa. (Instalació de WS extern per simular que l´aplicació helium reb els parametres)
									//EXP.10.4.1.2 - Execució de formularis externs
									//EXP.10.4.1.3 - Comprovació importació de dades de formularis
	
	/**
	 *   A C T I V A R   A   T R U E   L A   P R O P I E D A D:   app.selenium.ws.integracion del archivo helium.properties del modulo helium-webapp
	 */
	
	String entorn		= carregarPropietat("tipexp.integracio.forms.ext.entorn.nom", "Nom de l'entorn de proves no configurat al fitxer de properties");
	String titolEntorn	= carregarPropietat("tipexp.integracio.forms.ext.entorn.titol", "Titol de l'entorn de proves no configurat al fitxer de properties");
	
	String usuari		= carregarPropietat("test.base.usuari.configuracio", "Usuari configuració de l'entorn de proves no configurat al fitxer de properties");
	String usuariDis	= carregarPropietat("test.base.usuari.disseny", "Usuari configuració de l'entorn de proves no configurat al fitxer de properties");

	String nomTipusExp	= carregarPropietat("tipexp.deploy.info.tipus.expedient.nom", "Nom del tipus d'expedient de proves no configurat al fitxer de properties");
	String codTipusExp	= carregarPropietat("tipexp.deploy.info.tipus.expedient.codi", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	
	String pathExport	= carregarPropietatPath("tipexp.deploy.arxiu.path", "Ruta de l´arxiu del tipus d´expedient exportat no configurat al fitxer de properties");
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
	String urlFormExt = carregarPropietat("test.url.ws.inici.formulari", "URL de WS de formularis externs no configurat al fitxer de properties");
	String urlFormExt2 = carregarPropietat("test.url.ws.formulari.extern", "URL de WS de formularis externs no configurat al fitxer de properties");
	String idFormExt = "command";
	String botoGuardarDadesIntegracio = "//*[@id='command']/div[@class='buttonHolder']/button[text() = 'Guardar']";
	
	String aspaTancarDialogForm = "/html/body/div[8]/div[1]/a";//"/html/body/div[contains(@class, 'ui-dialog')]/div[@class='ui-dialog-titlebar']/a";	

	String variableResultatRetornFormulari = "//*[@id='command']/div/div/label[text() = 'Variable Boolean']";

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
		//Desmarcam de l´expedient alguns checks restrictius per poder-los consultar al llistat
		accedirInformacioExpedient(codTipusExp);
		if (driver.findElement(By.id("restringirPerGrup0")).isSelected()) {
			driver.findElement(By.id("restringirPerGrup0")).click();
		}
		driver.findElement(By.xpath("//*[@id='command']/div[@class='buttonHolder']/button[text() = 'Modificar']")).click();
	}
	
	@Test
	public void b1_preparar_tasca_defproc() {
		
		carregarUrlDisseny();
		seleccionarEntorn(titolEntorn);
		
		//Modificam la URL de servei de integracio amb forms del tipus d´expedient per la prova
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/expedientTipus/llistat.html')]")));
		actions.click();
		actions.build().perform();
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'Expedient Importat')]")).click();
		
		driver.findElement(By.xpath("//a[contains(@href, '/helium/expedientTipus/formext.html')]")).click();
		
		driver.findElement(By.id("url0")).clear();
		driver.findElement(By.id("url0")).sendKeys(urlFormExt);
		driver.findElement(By.id("usuari0")).clear();
		driver.findElement(By.id("contrasenya0")).clear();
		
		screenshotHelper.saveScreenshot("integracions/formsExterns/b1_0_prepara_tipexp-dades_servei.png");
		
		driver.findElement(By.xpath(botoGuardarDadesIntegracio)).click();

		//Modificam la tasca 1 per afegirli un formulari extern
		accedirPestanyaTasquesDefProc();
		
		screenshotHelper.saveScreenshot("integracions/formsExterns/b1_1_prepara_tasca-llistat_inicial.png");
		
		driver.findElement(By.xpath(linkAccedirTasca)).click();
		
		driver.findElement(By.id("recursForm0")).sendKeys(urlFormExt);
		driver.findElement(By.id("formExtern0")).sendKeys(idFormExt);
		
		screenshotHelper.saveScreenshot("integracions/formsExterns/b1_2_prepara_tasca-modifica_tasca.png");
		
		driver.findElement(By.xpath(botoModificarTasca)).click();
		
		screenshotHelper.saveScreenshot("integracions/formsExterns/b1_3_prepara_tasca-resultat_modificacio.png");
		
		//Assignam la def de proces com a proces inicial
		accedirDissenyTipusExpedient();
		
		driver.findElement(By.xpath(enllaçTipusExpLlistat)).click();
		
		driver.findElement(By.xpath(pestanyaDefProcExpedient)).click();
		
		driver.findElement(By.xpath(botoMarcaProcesInicial)).click();
		
		screenshotHelper.saveScreenshot("integracions/formsExterns/b1_4_prepara_tasca-assignar_proces_inicial.png");
	}
	
	@Test
	public void c1_provar_tasca_form_ext() {

		carregarUrlDisseny();
		
		seleccionarEntorn(titolEntorn);

		iniciarExpediente(codTipusExp, "1", "Prova integracio formularis externs");
		
		screenshotHelper.saveScreenshot("integracions/formsExterns/c1_1_provar_formulari_extern-iniciar_expedient.png");
		try{
			consultarTareas(null, null, null, false);
		}catch (Exception ex) {}
		
		screenshotHelper.saveScreenshot("integracions/formsExterns/c1_2_provar_formulari_extern-llistat_tasques.png");
		
		driver.findElement(By.xpath(linkAccedirTasca)).click();
		
		screenshotHelper.saveScreenshot("integracions/formsExterns/c1_3_provar_formulari_extern-accedir_tasca.png");
		
		driver.findElement(By.xpath(botoObrirFormExtern)).click();

		try {Thread.sleep(5000);}catch(Exception ex){};
		
		screenshotHelper.saveScreenshot("integracions/formsExterns/c1_4_provar_formulari_extern-obrir_form_extern.png");
		
		existeixElementAssert(iframeFormExt, "No s´ha obert l'iframe amb el formulari extern.");

		String URL_actual = driver.getCurrentUrl();
		String idTasca = URL_actual.substring(URL_actual.indexOf("id=")+3, URL_actual.length());		
		
		//Abans de tancar el formulari, guardam les dades com ho faria el WS de la aplicacio externa
		guardarFormulariExtern(idTasca);

		//Tancam el formulari
		driver.findElement(By.xpath(aspaTancarDialogForm)).click();
		
		//Comprovam que han aparegut les variables de la resposta a la cridada a guardarFormulariExtern()
		existeixElementAssert(variableResultatRetornFormulari, "No s´ha trobat la variable de retorn del formulari extern.");
		
		screenshotHelper.saveScreenshot("integracions/formsExterns/c1_5_provar_formulari_extern-resultat_retorn_ws.png");
	}
	
	@Test
	public void z_limpiar() throws InterruptedException {
		
		carregarUrlConfiguracio();
		
		seleccionarEntorn(titolEntorn);
		
		eliminarExpedient(null, null, nomTipusExp);
		
		eliminarConsultaTipus("codCons");
		
		// Eliminar la def de proceso
		eliminarDefinicioProces(nomDefProc);
		
		// Eliminar el tipo de expediente
		eliminarTipusExpedient(codTipusExp);
		
		eliminarEntorn(entorn);
		
		screenshotHelper.saveScreenshot("TasquesDadesTasca/finalizar_expedient/1.png");	
	}
	
	// **********************************************
	// F U N C I O N S   P R I V A D E S
	// **********************************************
	
	private void accedirPestanyaTasquesDefProc() {
		accedirPantallaDissenyDefProc();
		driver.findElement(By.xpath(linkAccesInfoDefProc)).click();
		driver.findElement(By.xpath(pestanyaTasquesDefProc)).click();
	}
	
	private void accedirDissenyTipusExpedient() {
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/expedientTipus/llistat.html')]")));
		actions.click();
		actions.build().perform();
	}
	
	private void guardarFormulariExtern (String codTasca) {
		try {
			List<ParellaCodiValor> valors = new ArrayList<ParellaCodiValor>();
			valors.add(new ParellaCodiValor("import", new BigDecimal("100")));
			GuardarFormulari gf = (GuardarFormulari)net.conselldemallorca.helium.test.integracio.utils.WsClientUtils.getWsClientProxy(GuardarFormulari.class, urlFormExt2, null,	null, "NONE", false, true, false);
			gf.guardar(codTasca, valors);
		}catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
