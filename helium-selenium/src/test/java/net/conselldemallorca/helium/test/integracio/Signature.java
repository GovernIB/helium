package net.conselldemallorca.helium.test.integracio;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.conselldemallorca.helium.test.util.BaseTest;
import net.conselldemallorca.helium.test.util.DocumentoExpedient;

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
		assignarPermisosTipusExpedient(codTipusExp, usuari, "DESIGN","CREATE","SUPERVISION","WRITE","MANAGE","DELETE","READ","ADMINISTRATION");
	}
	
	@Test
	public void a_crear_dades() throws InterruptedException {
		carregarUrlConfiguracio();
		
		seleccionarEntorn(titolEntorn);

		desplegarDefinicioProcesEntorn(nomTipusExp, nomDefProc, pathDefProc);
		
		importarDadesTipExp(codTipusExp, exportTipExpProc);
		
		screenshotHelper.saveScreenshot("tramitar/dadesexpedient/crear_dades/1.png");
	}

	@Test
	public void b_firmarDocumento() throws InterruptedException, IOException {
		carregarUrlConfiguracio();
		
		seleccionarEntorn(titolEntorn);
		
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/llistat.html')]")));
		actions.click();
		actions.build().perform();
		
		screenshotHelper.saveScreenshot("tramitar/dadesexpedient/visualizacio_dades_process/1.png");
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'"+nomDefProc+"')]")).click();
				
		driver.findElement(By.xpath("//*[@id='tabnav']//a[contains(@href,'/definicioProces/tascaLlistat.html')]")).click();
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr/td[contains(text(),'tasca1')]/parent::tr/td[5]/form/button")).click();
		
		List<DocumentoExpedient> documentosExpedient = new ArrayList<DocumentoExpedient>();
		
		// Leemos las variables
		int i = 1;
		while(i <= driver.findElements(By.xpath("//*[@id='registre']/tbody/tr")).size()) {
			DocumentoExpedient documento = new DocumentoExpedient();
			String nom = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+i+"]/td[1]")).getText();
			documento.setNom(nom);
			
			documentosExpedient.add(documento);
			
			screenshotHelper.saveScreenshot("tramitar/dadesexpedient/visualizacio_dades_process/3"+i+".png");
			i++;
		}
		
		iniciarExpediente(codTipusExp,"SE-1/2014", "Expedient de prova Selenium " + (new Date()).getTime() );
		
		consultarTareas(null, null, nomTipusExp, false);
		
		driver.findElement(By.xpath("//*[@id='registre']//td[contains(a/text(), 'tasca1')]/a")).click();
				
		// Documento
		driver.findElement(By.xpath("//a[contains(@href,'/helium/tasca/documents.html')]")).click();
		
		driver.findElement(By.xpath("//*[@id='documentCommand_exp_doc']//parent::div//input[@id='contingut0']")).sendKeys(pathArxiuPDF);
		driver.findElement(By.xpath("//*[@id='documentCommand_exp_doc']//parent::div//button[contains(text(), 'Guardar')]")).click();
		
		existeixElementAssert("//*[@id='infos']/p", "No se guardó correctamente");
		
		// Signature
		driver.findElement(By.xpath("//*[@id='tabnav']//a[contains(@href,'/tasca/signatures.html')]")).click();

		for (DocumentoExpedient documento : documentosExpedient) {
			byte[] archivoOriginal = downloadFile("//h4[contains(label/text(), '"+documento.getNom()+"')]/a[contains(@href,'/document/arxiuPerSignar.html')]", "blank.pdf");
			
			// El certificado no tiene contraseña
			driver.findElement(By.xpath("//h4[contains(label/text(), '"+documento.getNom()+"')]/parent::div//button")).click();
			Thread.sleep(1000*20);
			
			existeixElementAssert("//*[@id='infos']/p", "No se firmó correctamente");			
			
			// Comprobamos que el hash cambió
			byte[] archivoFirmado = downloadFile("//*/h4[contains(label/text(), '"+documento.getNom()+"')]/a[contains(@href,'/document/arxiuMostrar.html')]", "blank.pdf");
			assertFalse("El fichero no se firmó" , getMd5(archivoOriginal).equals(getMd5(archivoFirmado)));
			
			boolean firmado = false;
			if (existeixElement("//h4[contains(label/text(), '"+documento.getNom()+"')]/a[contains(@href,'/signatura/verificar.html')]")) {
				WebElement verificar = driver.findElement(By.xpath("//h4[contains(label/text(), '"+documento.getNom()+"')]/a[contains(@href,'/signatura/verificar.html')]"));
				verificar.click();;
				Thread.sleep(1000*15);
				
				String href = verificar.getAttribute("href");
				firmado = modalOberta(href, "tramitar/tasca/firmarDocumento3.png");
				vesAModal(href);
				downloadFile("//a[contains(@href,'/signatura/arxiu.html')]", "blank.pdf");
				tornaAPare();
			}
			assertTrue("No se firmó el documento correctamente", firmado);
			
			screenshotHelper.saveScreenshot("TasquesDadesTasca/visualizacio_tasca_dades/5.png");
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
}
