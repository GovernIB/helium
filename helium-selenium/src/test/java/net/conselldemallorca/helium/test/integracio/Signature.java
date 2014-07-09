package net.conselldemallorca.helium.test.integracio;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import net.conselldemallorca.helium.test.util.BaseTest;
import net.conselldemallorca.helium.test.util.CheckFileHash;
import net.conselldemallorca.helium.test.util.HashType;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Signature extends BaseTest {

	String entorn = carregarPropietat("tramsel.entorn.nom", "Nom de l'entorn de proves no configurat al fitxer de properties");
	String titolEntorn = carregarPropietat("tramsel.entorn.titol", "Titol de l'entorn de proves no configurat al fitxer de properties");
	String usuari = carregarPropietat("test.base.usuari.configuracio", "Usuari configuració de l'entorn de proves no configurat al fitxer de properties");
	String codTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.codi", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	String nomDefProc = carregarPropietat("defproc.deploy.definicio.proces.nom", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String nomSubDefProc = carregarPropietat("defproc.deploy.definicio.subproces.nom", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String nomTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.nom", "Nom del tipus d'expedient de proves no configurat al fitxer de properties");
	String pathDefProc = carregarPropietatPath("tramsel_accio.deploy.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String exportTipExpProc = carregarPropietatPath("tramsel_signature.export.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String pathArxiuPDF = carregarPropietatPath("deploy.arxiu.pdf.tramitacio_1", "Documento PDF a adjuntar 1");
	String hashArxiuPDF = carregarPropietat("deploy.arxiu.pdf.tramitacio_1.hash", "Hash documento PDF a adjuntar 1");
	
	@Test
	public void a0_inicialitzacio() {
		carregarUrlConfiguracio();
		crearEntorn(entorn, titolEntorn);
		assignarPermisosEntorn(entorn, usuari, "DESIGN", "ORGANIZATION", "READ", "ADMINISTRATION");
		seleccionarEntorn(titolEntorn);
		crearTipusExpedient(nomTipusExp, codTipusExp);
	}
	
	@Test
	public void a_crear_dades() throws InterruptedException {
		carregarUrlConfiguracio();
		
		seleccionarEntorn(titolEntorn);

		desplegarDefinicioProcesEntorn(nomTipusExp, nomDefProc, pathDefProc);
		
		importarDadesTipExp(codTipusExp, exportTipExpProc);
		
		screenshotHelper.saveScreenshot("tramitar/dadesexpedient/crear_dades/1.png");
					
		assignarPermisosTipusExpedient(codTipusExp, usuari, "DESIGN","CREATE","SUPERVISION","WRITE","MANAGE","DELETE","READ","ADMINISTRATION");
	}

	@Test
	public void b_firmarDocumento() throws InterruptedException, IOException {
		carregarUrlConfiguracio(); 
		
		seleccionarEntorn(titolEntorn);

		screenshotHelper.saveScreenshot("ExpedientPestanyaTasques/iniciar_expedient/1.png");
		
		iniciarExpediente(codTipusExp,"SE-1/2014", "Expedient de prova Selenium " + (new Date()).getTime() );
		
		consultarTareas(null, null, nomTipusExp, false);
		
		driver.findElement(By.xpath("//*[@id='registre']//td[contains(a/text(), 'tasca1')]/a")).click();
		
		// Documento
		driver.findElement(By.xpath("//a[contains(@href,'/helium/tasca/documents.html')]")).click();
		
		driver.findElement(By.xpath("//*[@id='documentCommand_exp_doc']//parent::div//input[@id='contingut0']")).sendKeys(pathArxiuPDF);
		driver.findElement(By.xpath("//*[@id='documentCommand_exp_doc']//parent::div//button[contains(text(), 'Guardar')]")).click();
		
		existeixElementAssert("//*[@id='infos']/p", "No se guardó correctamente");
		
		// Signature
		driver.findElement(By.xpath("//a[contains(@href,'/helium/tasca/signatures.html')]")).click();
		
		// El certificado no tiene contraseña			
		driver.findElement(By.xpath("//*[@id='command']//button[1]")).click();
		
		Thread.sleep(1000*20);
		
		existeixElementAssert("//*[@id='infos']/p", "No se firmó correctamente");
		
		// Comprobamos que el hsh cambió
		byte[] archivo = downloadFile("//*[@id='content']//a[contains(@href,'/helium/document/arxiuMostrar.html')]", "blank.pdf");
		CheckFileHash fileToCheck = new CheckFileHash();
		fileToCheck.fileToCheck(archivo);
		fileToCheck.hashDetails(hashArxiuPDF, HashType.MD5);
		
		assertFalse("El fichero no se firmó" , fileToCheck.hasAValidHash());
				
		List<WebElement> tagsA = driver.findElements(By.xpath("//*[@class='missatgesDocumentGris']//a"));
		WebElement verificar = driver.findElement(By.xpath("//*[@alt='Verificar signatura']"));
		verificar.click();
		
		boolean hayModal = false;
		for (WebElement tag : tagsA) {
			String href = tag.getAttribute("href");
			if (href.contains("/helium/signatura/verificar.html")) {
				hayModal = modalOberta(href, "tramitar/tasca/firmarDocumento3.png");
			}
		}
		assertTrue("No se firmó el documento correctamente", hayModal);
		
		screenshotHelper.saveScreenshot("TasquesDadesTasca/visualizacio_tasca_dades/5.png");
		
		tornaAPare();
		assertTrue("No se firmó el documento correctamente", hayModal);
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
}
