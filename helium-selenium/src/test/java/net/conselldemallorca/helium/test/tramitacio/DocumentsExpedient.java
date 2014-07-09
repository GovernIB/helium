package net.conselldemallorca.helium.test.tramitacio;

import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import net.conselldemallorca.helium.test.util.BaseTest;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DocumentsExpedient extends BaseTest {

	String entorn = carregarPropietat("tramsel.entorn.nom", "Nom de l'entorn de proves no configurat al fitxer de properties");
	String titolEntorn = carregarPropietat("tramsel.entorn.titol", "Titol de l'entorn de proves no configurat al fitxer de properties");
	String codTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.codi", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	String nomSubDefProc = carregarPropietat("defproc.deploy.definicio.subproces.nom", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String nomDefProc = carregarPropietat("defproc.deploy.definicio.proces.nom", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String exportTipExpProc = carregarPropietatPath("tipexp.tasca_dades_doc.exp.export.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String pathDefProc = carregarPropietatPath("defproc.mod.exp.deploy.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String pathDefProcTermini = carregarPropietatPath("defproc.termini.exp.export.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String nomTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.nom", "Nom del tipus d'expedient de proves no configurat al fitxer de properties");
	
	String pathArxiuPDF1 = carregarPropietatPath("deploy.arxiu.pdf.tramitacio_1", "Documento PDF a adjuntar 1");
	String hashArxiuPDF1 = carregarPropietat("deploy.arxiu.pdf.tramitacio_1.hash", "Hash documento PDF a adjuntar 1");
	
	String pathArxiuPDF2 = carregarPropietatPath("deploy.arxiu.pdf.tramitacio_2", "Documento PDF a adjuntar 1");
	String hashArxiuPDF2 = carregarPropietat("deploy.arxiu.pdf.tramitacio_2.hash", "Hash documento PDF a adjuntar 2");
	
	String usuari = carregarPropietat("test.base.usuari.configuracio", "Usuari configuració de l'entorn de proves no configurat al fitxer de properties");
	
//	@Test
	public void a0_inicialitzacio() {
		carregarUrlConfiguracio();
		crearEntorn(entorn, titolEntorn);
		assignarPermisosEntorn(entorn, usuari, "DESIGN", "ORGANIZATION", "READ", "ADMINISTRATION");
		seleccionarEntorn(titolEntorn);
		crearTipusExpedient(nomTipusExp, codTipusExp);
		assignarPermisosTipusExpedient(codTipusExp, usuari, "DESIGN","CREATE","SUPERVISION","WRITE","MANAGE","DELETE","READ","ADMINISTRATION");
	}
	
//	@Test
	public void a_crear_dades() throws InterruptedException {
		carregarUrlConfiguracio();
		
		seleccionarEntorn(titolEntorn);
		
		importarDadesTipExp(codTipusExp, exportTipExpProc);
		
		iniciarExpediente(codTipusExp,"SE-22/2014", "Expedient de prova Selenium " + (new Date()).getTime() );
		
		screenshotHelper.saveScreenshot("tramitar/dadesexpedient/crear_dades/1.png");
	}
	
//	@Test
	public void b_adjuntar_documents() throws InterruptedException {
		carregarUrlConfiguracio(); 
		
		seleccionarEntorn(titolEntorn);
		
		// Adjuntamos 2 documentos
		adjuntarDocExpediente(null, null, "Título del documento 1", "12/12/2014", pathArxiuPDF1);
		screenshotHelper.saveScreenshot("documentsexpedient/adjuntar_documents/1.png");
		
		adjuntarDocExpediente(null, null, "Título del documento 2", "13/12/2014", pathArxiuPDF2);
		screenshotHelper.saveScreenshot("documentsexpedient/adjuntar_documents/2.png");		
	}
	
//	@Test
	public void c_visualizacio_documents_i_descarrega() throws InterruptedException {
		carregarUrlConfiguracio(); 
		
		seleccionarEntorn(titolEntorn);
		
		screenshotHelper.saveScreenshot("documentsexpedient/visualizacio_documents_i_descarrega/1.png");
		
		consultarExpedientes(null, null, nomTipusExp);
		
		screenshotHelper.saveScreenshot("documentsexpedient/visualizacio_documents_i_descarrega/2.png");
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]//img[@src='/helium/img/information.png']")).click();
		
		driver.findElement(By.xpath("//*[@id='tabnav']/li[3]/a")).click();
		
		// Comprobamos que haya 2 documentos
		assertTrue("No había 2 documentos adjuntos", driver.findElements(By.xpath("//*[@id='codi']/tbody/tr")).size() == 2);
		screenshotHelper.saveScreenshot("documentsexpedient/visualizacio_documents_i_descarrega/3.png");
		
		// Los descargamos
		downloadFileHash("//*[@id='codi']/tbody/tr[1]/td[3]/a", hashArxiuPDF1, "Título del documento 1");
		screenshotHelper.saveScreenshot("documentsexpedient/visualizacio_documents_i_descarrega/4.png");
		
		downloadFileHash("//*[@id='codi']/tbody/tr[2]/td[3]/a", hashArxiuPDF2, "Título del documento 2");
		screenshotHelper.saveScreenshot("documentsexpedient/visualizacio_documents_i_descarrega/5.png");
	}
	
	@Test
	public void d_generar_document() throws InterruptedException {
		carregarUrlConfiguracio(); 

		seleccionarEntorn(titolEntorn);
		
		screenshotHelper.saveScreenshot("documentsexpedient/generar_document/1.png");
		
		consultarExpedientes(null, null, nomTipusExp);
		
		screenshotHelper.saveScreenshot("documentsexpedient/generar_document/2.png");
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]//img[@src='/helium/img/information.png']")).click();
		
		driver.findElement(By.xpath("//*[@id='tabnav']/li[3]/a")).click();
		
		// Generamos los documentos
		assertTrue("No había documentos adjuntos", !driver.findElements(By.xpath("//*[@id='codi']/tbody/tr")).isEmpty());
				
		screenshotHelper.saveScreenshot("documentsexpedient/generar_document/3.png");
		
		for (int i = 1; i <= driver.findElements(By.xpath("//*[@id='codi']/tbody/tr")).size(); i++) {
			driver.findElement(By.xpath("//*[@id='codi']/tbody/tr["+i+"]/td[4]/a/img")).click();
			acceptarAlerta();
			
			// Generamos un documento
			if (existeixElement("//*[@id='content']/div/h4/a/img")) {
				String titulo = driver.findElement(By.xpath("//*[@id='content']/div/h4")).getText();
				byte[] document = downloadFile("//*[@id='content']/div/h4/a[1]", titulo+".doc");
				assertTrue("No se generó el documento num "+i+" Título: "+titulo+".doc", document.length > 0);
			}
			
			screenshotHelper.saveScreenshot("documentsexpedient/generar_document/4-"+i+".png");
			
			driver.findElement(By.xpath("//*[@id='command']/div[3]/button[2]")).click();
		}		
	}
	
	@Test
	public void e_descarregar_document() throws InterruptedException {
		carregarUrlConfiguracio(); 
		
		seleccionarEntorn(titolEntorn);
		
		screenshotHelper.saveScreenshot("documentsexpedient/descarregar_document/1.png");
		
		consultarExpedientes(null, null, nomTipusExp);
		
		screenshotHelper.saveScreenshot("documentsexpedient/descarregar_document/2.png");
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]//img[@src='/helium/img/information.png']")).click();
		
		driver.findElement(By.xpath("//*[@id='tabnav']/li[3]/a")).click();
		
		// Descargamos el documento
		assertTrue("No había documentos que descargar", !driver.findElements(By.xpath("//*[@id='codi']/tbody/tr")).isEmpty());
		
		int i = 1;
		while (!driver.findElements(By.xpath("//*[@id='codi']/tbody/tr["+i+"]/td[4]/a/img")).isEmpty()) {
			driver.findElement(By.xpath("//*[@id='codi']/tbody/tr["+i+"]/td[4]/a/img")).click();
			acceptarAlerta();		
			downloadFileHash("//*[@id='iconsFileInput_contingut0']/a[1]", hashArxiuPDF1, "Título del documento 1");		
			screenshotHelper.saveScreenshot("documentsexpedient/descarregar_document/3-"+i+".png");		
			driver.findElement(By.xpath("//*[@id='command']/div[3]/button[2]")).click();
			i++;
		}
		
		screenshotHelper.saveScreenshot("documentsexpedient/descarregar_document/4.png");		
	}
	
//	@Test
	public void f_modificar_data_document() throws InterruptedException {
		carregarUrlConfiguracio(); 
		
		seleccionarEntorn(titolEntorn);
		
		screenshotHelper.saveScreenshot("documentsexpedient/modificar_data_document/1.png");
		
		consultarExpedientes(null, null, nomTipusExp);
		
		screenshotHelper.saveScreenshot("documentsexpedient/modificar_data_document/2.png");
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]//img[@src='/helium/img/information.png']")).click();
		
		driver.findElement(By.xpath("//*[@id='tabnav']/li[3]/a")).click();
		
		// Modificamos los documentos
		assertTrue("No había documentos adjuntos", !driver.findElements(By.xpath("//*[@id='codi']/tbody/tr")).isEmpty());
		
		driver.findElement(By.xpath("//*[@id='codi']/tbody/tr[1]/td[4]/a/img")).click();
		acceptarAlerta();		
		driver.findElement(By.xpath("//*[@id='nom0']")).clear();
		driver.findElement(By.xpath("//*[@id='nom0']")).sendKeys("Título mod 1");
		driver.findElement(By.xpath("//*[@id='data0']")).clear();
		driver.findElement(By.xpath("//*[@id='data0']")).sendKeys("15/12/2014");
		driver.findElement(By.xpath("//*[@id='iconsFileInput_contingut0']/a[2]/img")).click();
		driver.findElement(By.id("contingut0")).sendKeys(pathArxiuPDF1);
		screenshotHelper.saveScreenshot("documentsexpedient/modificar_data_document/3-1.png");
		driver.findElement(By.xpath("//*[@id='command']/div[3]/button[1]")).click();
		existeixElementAssert("//*[@id='infos']", "No se modificó el docuento");
		
		driver.findElement(By.xpath("//*[@id='codi']/tbody/tr[2]/td[4]/a/img")).click();
		acceptarAlerta();
		driver.findElement(By.xpath("//*[@id='nom0']")).clear();
		driver.findElement(By.xpath("//*[@id='nom0']")).sendKeys("Título mod 2");
		driver.findElement(By.xpath("//*[@id='data0']")).clear();
		driver.findElement(By.xpath("//*[@id='data0']")).sendKeys("16/12/2014");
		driver.findElement(By.xpath("//*[@id='iconsFileInput_contingut0']/a[2]/img")).click();
		driver.findElement(By.id("contingut0")).sendKeys(pathArxiuPDF2);		
		screenshotHelper.saveScreenshot("documentsexpedient/modificar_data_document/3-2.png");	
		driver.findElement(By.xpath("//*[@id='command']/div[3]/button[1]")).click();
		existeixElementAssert("//*[@id='infos']", "No se modificó el docuento");
		
		screenshotHelper.saveScreenshot("documentsexpedient/modificar_data_document/4.png");
	}
	
//	@Test
	public void g_esborrar_document_adjunt() throws InterruptedException {
		carregarUrlConfiguracio(); 
		
		seleccionarEntorn(titolEntorn);
		
		screenshotHelper.saveScreenshot("documentsexpedient/esborrar_document_adjunt/1.png");
		
		consultarExpedientes(null, null, nomTipusExp);
		
		screenshotHelper.saveScreenshot("documentsexpedient/esborrar_document_adjunt/2.png");
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]//img[@src='/helium/img/information.png']")).click();
		
		driver.findElement(By.xpath("//*[@id='tabnav']/li[3]/a")).click();
		
		// Borramos los documentos
		assertTrue("No había documentos adjuntos", !driver.findElements(By.xpath("//*[@id='codi']/tbody/tr")).isEmpty());
		
		while (!driver.findElements(By.xpath("//*[@id='codi']/tbody/tr")).isEmpty()) {
			driver.findElement(By.xpath("//*[@id='codi']/tbody/tr[1]/td[5]/a/img")).click();
			acceptarAlerta();
			existeixElementAssert("//*[@id='infos']", "No se borró el docuento");
		}
		
		screenshotHelper.saveScreenshot("documentsexpedient/esborrar_document_adjunt/3.png");
	}
	
//	@Test
	public void h_esborrar_document_tipus_expedient() throws InterruptedException {
		carregarUrlConfiguracio(); 
		
		seleccionarEntorn(titolEntorn);
		
		screenshotHelper.saveScreenshot("documentsexpedient/esborrar_document_tipus_expedient/1.png");
		
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//*[@id='menuDisseny']/ul/li[3]/a")));
		actions.click();
		actions.build().perform();
				
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr/td[contains(a/text(),'"+codTipusExp+"')]")).click();
		
		driver.findElement(By.xpath("//*[@id='tabnav']/li[7]/a")).click();
		screenshotHelper.saveScreenshot("documentsexpedient/esborrar_document_tipus_expedient/2.png");
		
		WebElement select = driver.findElement(By.xpath("//*[@id='content']/div/form/select"));
		List<WebElement> options = select.findElements(By.tagName("option"));
		for (WebElement option : options) {
			if (option.getText().equals(properties.getProperty("defproc.deploy.definicio.proces.nom"))) {
				option.click();
				break;
			}
		}
		
		// Entramos a modificar el documento y lo borramos
		assertTrue("No había documentos adjuntos", !driver.findElements(By.xpath("//*[@id='registre']/tbody/tr/td/a/img[1]")).isEmpty());
		screenshotHelper.saveScreenshot("documentsexpedient/esborrar_document_tipus_expedient/3.png");
		
		while (!driver.findElements(By.xpath("//*[@id='registre']/tbody/tr/td/a/img[1]")).isEmpty()) {
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr/td/a/img[1]//parent::a//parent::td//parent::tr/td[1]/a")).click();
			
			if (existeixElement("//*[@id='iconsFileInput_arxiuContingut0']/a[2]/img")) {
				driver.findElement(By.xpath("//*[@id='iconsFileInput_arxiuContingut0']/a[2]/img")).click();
				
				driver.findElement(By.xpath("//*[@id='command']/div[3]/button[1]")).click();
				
				existeixElementAssert("//*[@id='infos']", "No se borró el docuento");
			}
		}
		
		screenshotHelper.saveScreenshot("documentsexpedient/esborrar_document_tipus_expedient/4.png");
	}

//	@Test
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
