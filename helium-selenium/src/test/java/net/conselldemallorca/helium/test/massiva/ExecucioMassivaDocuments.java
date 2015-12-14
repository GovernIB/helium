package net.conselldemallorca.helium.test.massiva;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.conselldemallorca.helium.test.util.BaseTest;
import net.conselldemallorca.helium.test.util.DocumentoExpedient;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ExecucioMassivaDocuments extends BaseTest {

	String entorn = carregarPropietat("tramas.entorn.nom", "Nom de l'entorn de proves no configurat al fitxer de properties");
	String titolEntorn = carregarPropietat("tramas.entorn.titol", "Titol de l'entorn de proves no configurat al fitxer de properties");
	String usuari = carregarPropietat("test.base.usuari.configuracio", "Usuari configuració de l'entorn de proves no configurat al fitxer de properties");
	String nomDefProc = carregarPropietat("defproc.deploy.definicio.proces.nom", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String nomSubDefProc = carregarPropietat("defproc.deploy.definicio.subproces.nom", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String pathDefProc = carregarPropietatPath("defproc.deploy.definicio.subproces.main_direct.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String pathDefProcScript = carregarPropietatPath("defproc.deploy.expexe_executar_script.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String pathSubDefProc = carregarPropietatPath("defproc.subproces.deploy.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String exportDefProc = carregarPropietatPath("defproc.tasca_dades.exp.export.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String nomTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.nom", "Nom del tipus d'expedient de proves no configurat al fitxer de properties");
	String codTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.codi", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	String accioPathDefProc = carregarPropietatPath("tramsel_accio.deploy.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String exportTipExpProc = carregarPropietatPath("tramsel_accio.export.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String exportTipExpMasProc = carregarPropietatPath("tramas_massivo.export.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	int numExpedientesTramMasiva = Integer.parseInt(carregarPropietat("tramas.num_expedientes_tram_masiva", "Número de espedientes para las pruebas de tramitación masiva al fitxer de properties"));
	String pathDocTipusExp = carregarPropietatPath("tipexp.deploy.expexe_document_massiva.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String pathArxiuPDF = carregarPropietatPath("deploy.arxiu.pdf.tramitacio_1", "Documento PDF a adjuntar 1");
	String pathArxiuPlantillaODT = carregarPropietatPath("tramas_massivo.plantilla", "Documento plantilla a adjuntar");

	//XPATHS
	String botoExecMassiva = "//*[@id='page-entorn-menu']/div/a";
	String linkDocMass = "//*[@id='registre']/tbody/tr[contains(td/a, 'exp_doc')]/td/a";
	String botoBorrarDoc = "//*[@id='iconsFileInput_arxiuContingut0']/a[2]";
	String botoModificarArxiu = "//*[@id='command']/div/button[contains(text(), 'Modificar')]";
	
	
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

		importarDadesTipExp(codTipusExp, pathDocTipusExp);
		
		//Canviar document importat per una plantilla
		accedirPipellaDocsExpedient(codTipusExp);
		
		//Seleccionar Def. Proces
		for (WebElement option : driver.findElement(By.name("definicioProcesId")).findElements(By.tagName("option"))) {
			if (nomDefProc.equals(option.getText())) {
				option.click();
				break;
			}
		}
		
		driver.findElement(By.xpath(linkDocMass)).click();
		
		Thread.sleep(20000);
		
		driver.findElement(By.xpath(botoBorrarDoc)).click();
		
		Thread.sleep(20000);
		
		driver.findElement(By.id("arxiuContingut0")).sendKeys(pathArxiuPlantillaODT);

		driver.findElement(By.xpath(botoModificarArxiu)).click();
		
		if (isAlertPresent()) { acceptarAlerta(); }
		
		existeixElementAssert("//*[@id='infos']/p", "No se pudo asociar el documento plantilla al expediente.");
		
		screenshotHelper.saveScreenshot("tramitar/dadesexpedient/crear_dades/1.png");
	}
	
	@Test
	public void b_visualitzar_document() throws InterruptedException {
		carregarUrlConfiguracio();
		
		seleccionarEntorn(titolEntorn);
		
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/definicioProces/llistat.html')]")));
		actions.click();
		actions.build().perform();
		
		screenshotHelper.saveScreenshot("tramitar/dadesexpedient/visualizacio_dades_process/1.png");
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'"+nomDefProc+"')]")).click();
				
		driver.findElement(By.xpath("//*[@id='tabnav']//a[contains(@href,'/definicioProces/documentLlistat.html')]")).click();
		
		Map<String, DocumentoExpedient> documentosExpedient = new HashMap<String, DocumentoExpedient>();
		
		// Leemos las variables
		int i = 1;
		while(existeixElement("//*[@id='registre']/tbody/tr["+i+"]")) {			
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+i+"]")).click();
			
			String codi = driver.findElement(By.xpath("//*[@id='codi0']")).getAttribute("value");
			String nom = driver.findElement(By.xpath("//*[@id='nom0']")).getAttribute("value");
			String descripcio = driver.findElement(By.xpath("//*[@id='descripcio0']")).getText();
			boolean esPlantilla = driver.findElement(By.xpath("//*[@id='plantilla0']")).isSelected();
			boolean adjuntarAutomaticamente = driver.findElement(By.xpath("//*[@id='adjuntarAuto0']")).isSelected();
			
			DocumentoExpedient documento = new DocumentoExpedient();			
			documento.setCodi(codi);
			documento.setNom(nom);
			documento.setDescripcio(descripcio);
			documento.setEsPlantilla(esPlantilla);
			documento.setAdjuntarAutomaticamente(adjuntarAutomaticamente);
			documentosExpedient.put(nom, documento);
			
			screenshotHelper.saveScreenshot("tramitar/dadesexpedient/visualizacio_dades_process/2"+i+".png");
			
			driver.findElement(By.xpath("//*/button[contains(text(), 'Cancel·lar')]")).click();
			
			screenshotHelper.saveScreenshot("tramitar/dadesexpedient/visualizacio_dades_process/3"+i+".png");
			i++;
		}
		
		assertFalse("No había ningún documento para visualizar", documentosExpedient.isEmpty());
		
		// Iniciamos los expedientes
		for (int j = 0; j < numExpedientesTramMasiva; j++) {
			iniciarExpediente(codTipusExp,"SE-"+j+"/2014", "Expedient de prova Selenium " + (new Date()).getTime() );
		}
		
		consultarExpedientes(null, null, nomTipusExp);
		
		driver.findElement(By.xpath("//*[@id='massivaInfoForm']/button[2]")).click();
				
		// Visualizamos que se muestren todos los documentos		
		WebElement selectVar = driver.findElement(By.xpath("//*[@id='nom0']"));
		List<WebElement> optionsVar = selectVar.findElements(By.tagName("option"));
		optionsVar.remove(0);
		for (WebElement var : optionsVar) {
			assertTrue("El documento '"+var.getText().trim()+"' no se encontró en la lista de documentos del expediente", documentosExpedient.containsKey(var.getText().trim()));			
		}
		
		assertTrue("El número de variables de la lista de variables del expediente y las variables mostradas en la pantalla de masivas no coincidia", documentosExpedient.size() == optionsVar.size());			
	}
	
	@Test
	public void c_generar_document() throws InterruptedException {
		
		carregarUrlConfiguracio();
		
		seleccionarEntorn(titolEntorn);
		
		consultarExpedientes(null, null, nomTipusExp);
		
		driver.findElement(By.xpath("//*[@id='massivaInfoForm']/button[2]")).click();
				
		// Visualizamos que se muestren todos los documentos		
		WebElement selectVar = driver.findElement(By.xpath("//*[@id='nom0']"));
		List<WebElement> optionsVar = selectVar.findElements(By.tagName("option"));
		optionsVar.get(1).click();
		String document = optionsVar.get(1).getText(); 
		
		existeixElementAssert("//*[@id='documentCommandForm']//button[contains(@onclick,'generar')]","No existía el botón de generar");
		assertTrue("El botón de 'Generar' no estaba habilitado", driver.findElement(By.xpath("//*[@id='documentCommandForm']//button[contains(@onclick,'generar')]")).isEnabled());
		driver.findElement(By.xpath("//*[@id='documentCommandForm']//button[contains(@onclick,'generar')]")).click();
		acceptarAlerta();
		
		esperaFinExecucioMassiva(botoExecMassiva);
		
		existeixElementAssert("//*[@id='infos']/p", "No se ejecutó la operación masiva correctamente");
				
		List<String> hash = new ArrayList<String>();
		// Comprobamos que se ha generado uno distinto para cada expediente
		for (int j = 1; j <= numExpedientesTramMasiva; j++) {
			consultarExpedientes(null, null, nomTipusExp);
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+j+"]//a[contains(@href,'/expedient/info.html')]")).click();
			
			driver.findElement(By.xpath("//*[@id='tabnav']//a[contains(text(), 'Documents')]")).click();
			existeixElementAssert("//*[@id='codi']/tbody/tr/td[contains(text(),'"+document+"')]","No se encontró el documento '"+document+"'");
			
			// Bajamos el documento
			byte[] archivo = downloadFile("//tr[contains(td/text(), '"+document+"')]//a[contains(@href,'/expedient/documentModificar.html')]", "blank.pdf");

			// Comprobamos que los hash de todos sean diferentes
			String md5 = getMd5(archivo);
			assertFalse("El documento '"+document+"' ya estaba en otro expediente",hash.contains(md5));
			hash.add(md5);			
		}
	}

	@Test
	public void d_adjuntar_documents() throws InterruptedException {
		carregarUrlConfiguracio();
		
		seleccionarEntorn(titolEntorn);
		
		consultarExpedientes(null, null, nomTipusExp);
		
		driver.findElement(By.xpath("//*[@id='massivaInfoForm']/button[2]")).click();
				
		// Visualizamos que se muestren todos los documentos		
		WebElement selectVar = driver.findElement(By.xpath("//*[@id='nom0']"));
		List<WebElement> optionsVar = selectVar.findElements(By.tagName("option"));
		optionsVar.get(1).click();
		String document = optionsVar.get(1).getText(); 
		
		existeixElementAssert("//*[@id='documentCommandForm']//button[contains(@onclick,'adjunt')]","No existía el botón de generar");
		assertTrue("El botón de 'Adjuntar document als expedients' no estaba habilitado", driver.findElement(By.xpath("//*[@id='documentCommandForm']//button[contains(@onclick,'adjunt')]")).isEnabled());
		driver.findElement(By.xpath("//*[@id='documentCommandForm']//button[contains(@onclick,'adjunt')]")).click();
		
		if (isAlertPresent()) acceptarAlerta();
		
		try {Thread.sleep(3000);}catch(Exception ex) {}
		
		// Introducimos los datos
		driver.findElement(By.xpath("//*[@id='nom0']")).clear();
		driver.findElement(By.xpath("//*[@id='nom0']")).sendKeys("El documento");
		driver.findElement(By.xpath("//input[@id='contingut0']")).sendKeys(pathArxiuPDF);
		driver.findElement(By.xpath("//button[contains(@onclick,'adjunt')]")).click();
				
		esperaFinExecucioMassiva(botoExecMassiva);		
		
		existeixElementAssert("//*[@id='infos']/p", "No se ejecutó la operación masiva correctamente");
				
		// Comprobamos que se ha adjuntado el mismo documento en cada expediente
		for (int j = 1; j <= numExpedientesTramMasiva; j++) {
			consultarExpedientes(null, null, nomTipusExp);
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+j+"]//a[contains(@href,'/expedient/info.html')]")).click();
			
			driver.findElement(By.xpath("//*[@id='tabnav']//a[contains(text(), 'Documents')]")).click();
			existeixElementAssert("//*[@id='codi']/tbody/tr/td[contains(text(),'"+document+"')]","No se encontró el documento '"+document+"'");
			
			// Bajamos el documento
			byte[] archivo = downloadFile("//tr[contains(td/text(), '"+document+"')]//a[contains(@href,'/expedient/documentModificar.html')]", "blank.pdf");
			assertTrue("El documento '"+document+"' no se podía descargar",archivo.length > 0);
		}
	}

	@Test
	public void e_esborrar_documents() throws InterruptedException {
		carregarUrlConfiguracio();
		
		seleccionarEntorn(titolEntorn);
		
		consultarExpedientes(null, null, nomTipusExp);
		
		driver.findElement(By.xpath("//*[@id='massivaInfoForm']/button[2]")).click();
				
		// Visualizamos que se muestren todos los documentos		
		WebElement selectVar = driver.findElement(By.xpath("//*[@id='nom0']"));
		List<WebElement> optionsVar = selectVar.findElements(By.tagName("option"));
		optionsVar.get(1).click();
		String document = optionsVar.get(1).getText(); 
		
		existeixElementAssert("//*[@id='documentCommandForm']//button[contains(@onclick,'delete')]","No existía el botón de generar");
		assertTrue("El botón de 'Esborrar' no estaba habilitado", driver.findElement(By.xpath("//*[@id='documentCommandForm']//button[contains(@onclick,'delete')]")).isEnabled());
		driver.findElement(By.xpath("//*[@id='documentCommandForm']//button[contains(@onclick,'delete')]")).click();
		acceptarAlerta();
				
		esperaFinExecucioMassiva(botoExecMassiva);		
		
		existeixElementAssert("//*[@id='infos']/p", "No se ejecutó la operación masiva correctamente");
				
		// Comprobamos que se ha borrado el documento en cada uno de los expedientes 
		for (int j = 1; j <= numExpedientesTramMasiva; j++) {
			consultarExpedientes(null, null, nomTipusExp);
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+j+"]//a[contains(@href,'/expedient/info.html')]")).click();
			
			driver.findElement(By.xpath("//*[@id='tabnav']//a[contains(text(), 'Documents')]")).click();
			noExisteixElementAssert("//*[@id='codi']/tbody/tr/td[contains(text(),'"+document+"')]","Se encontró el documento '"+document+"'");
		}
	}

	@Test
	public void f_modificar_documents_generar() throws InterruptedException, IOException {
		carregarUrlConfiguracio();
		
		seleccionarEntorn(titolEntorn);
		
		consultarExpedientes(null, null, nomTipusExp);
		
		driver.findElement(By.xpath("//*[@id='massivaInfoForm']/button[2]")).click();
				
		// Visualizamos que se muestren todos los documentos		
		WebElement selectVar = driver.findElement(By.xpath("//*[@id='nom0']"));
		List<WebElement> optionsVar = selectVar.findElements(By.tagName("option"));
		optionsVar.get(1).click();
		String document = optionsVar.get(1).getText(); 
		
		existeixElementAssert("//*[@id='documentCommandForm']//button[contains(@onclick,'subdoc')]","No existía el botón de generar");
		assertTrue("El botón de 'Modificar' no estaba habilitado", driver.findElement(By.xpath("//*[@id='documentCommandForm']//button[contains(@onclick,'subdoc')]")).isEnabled());
		driver.findElement(By.xpath("//*[@id='documentCommandForm']//button[contains(@onclick,'subdoc')]")).click();
		acceptarAlerta();
		
		// Introducimos los datos para generar el documento
		if (existeixElement("//*[@id='iconsFileInput_contingut0']//img[contains(@src,'img/cross.png')]")) {
			driver.findElement(By.xpath("//*[@id='iconsFileInput_contingut0']//img[contains(@src,'img/cross.png')]")).click();
		}
		
		byte[] archivo = downloadFile("//a[contains(@href,'/expedient/documentGenerarMas.html')]", "blank.pdf");
		File temp = File.createTempFile("real",".documento.odt");
		temp.deleteOnExit();
		copyFile(archivo, temp);
		driver.findElement(By.xpath("//input[@id='contingut0']")).sendKeys(temp.getAbsolutePath());		
		driver.findElement(By.xpath("//button[contains(@onclick,'submit')]")).click();
				
		esperaFinExecucioMassiva(botoExecMassiva);		
		
		existeixElementAssert("//*[@id='infos']/p", "No se ejecutó la operación masiva correctamente");
				
		List<String> hash = new ArrayList<String>();
		// Comprobamos que se ha generado uno distinto para cada expediente
		for (int j = 1; j <= numExpedientesTramMasiva; j++) {
			consultarExpedientes(null, null, nomTipusExp);
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+j+"]//a[contains(@href,'/expedient/info.html')]")).click();
			
			driver.findElement(By.xpath("//*[@id='tabnav']//a[contains(text(), 'Documents')]")).click();
			existeixElementAssert("//*[@id='codi']/tbody/tr/td[contains(text(),'"+document+"')]","No se encontró el documento '"+document+"'");
			
			// Bajamos el documento
			byte[] doc = downloadFile("//tr[contains(td/text(), '"+document+"')]//a[contains(@href,'/expedient/documentModificar.html')]", "blank.pdf");
			// Comprobamos que los hash de todos sean diferentes
			String md5 = getMd5(doc);
			assertFalse("El documento '"+document+"' ya estaba en otro expediente",hash.contains(md5));
			hash.add(md5);
		}
	}

	@Test
	public void g_modificar_documents_canviar_data() throws InterruptedException {
		carregarUrlConfiguracio();
		
		seleccionarEntorn(titolEntorn);
		
		consultarExpedientes(null, null, nomTipusExp);
		
		driver.findElement(By.xpath("//*[@id='massivaInfoForm']/button[2]")).click();
				
		// Visualizamos que se muestren todos los documentos		
		WebElement selectVar = driver.findElement(By.xpath("//*[@id='nom0']"));
		List<WebElement> optionsVar = selectVar.findElements(By.tagName("option"));
		optionsVar.get(1).click();
		String document = optionsVar.get(1).getText(); 
		
		existeixElementAssert("//*[@id='documentCommandForm']//button[contains(@onclick,'subdoc')]","No existía el botón de generar");
		assertTrue("El botón de 'Modificar' no estaba habilitado", driver.findElement(By.xpath("//*[@id='documentCommandForm']//button[contains(@onclick,'subdoc')]")).isEnabled());
		driver.findElement(By.xpath("//*[@id='documentCommandForm']//button[contains(@onclick,'subdoc')]")).click();
		acceptarAlerta();
		
		// Cambiamos la fecha
		driver.findElement(By.xpath("//*[@id='data0']")).clear();
		String fecha = "12/12/2015";
		driver.findElement(By.xpath("//*[@id='data0']")).sendKeys(fecha );
		driver.findElement(By.xpath("//button[contains(@onclick,'submit')]")).click();
				
		esperaFinExecucioMassiva(botoExecMassiva);
		
		existeixElementAssert("//*[@id='infos']/p", "No se ejecutó la operación masiva correctamente");
				
		for (int j = 1; j <= numExpedientesTramMasiva; j++) {
			consultarExpedientes(null, null, nomTipusExp);
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+j+"]//a[contains(@href,'/expedient/info.html')]")).click();
			
			driver.findElement(By.xpath("//*[@id='tabnav']//a[contains(text(), 'Documents')]")).click();
			existeixElementAssert("//*[@id='codi']/tbody/tr/td[contains(text(),'"+document+"')]","No se encontró el documento '"+document+"'");
			
			// Bajamos el documento
			byte[] archivo = downloadFile("//tr[contains(td/text(), '"+document+"')]//a[contains(@href,'/expedient/documentModificar.html')]", "blank.pdf");
			assertTrue("El documento '"+document+"' no se podía descargar",archivo.length > 0);
			// Comprobamos que la fecha haya cambiado
			String fechaDocumento = driver.findElement(By.xpath("//*[@id='codi']/tbody/tr[contains(td/text(),'"+document+"')]/td[2]")).getText();
			assertTrue("La fecha del documento '"+document+"' no era correcta",fecha.equals(fechaDocumento));
		}
	}

	@Test
	public void h_modificar_documents_adjuntar_document_nou() throws InterruptedException {
		carregarUrlConfiguracio();
		
		seleccionarEntorn(titolEntorn);
		
		consultarExpedientes(null, null, nomTipusExp);
		
		driver.findElement(By.xpath("//*[@id='massivaInfoForm']/button[2]")).click();
				
		// Visualizamos que se muestren todos los documentos		
		WebElement selectVar = driver.findElement(By.xpath("//*[@id='nom0']"));
		List<WebElement> optionsVar = selectVar.findElements(By.tagName("option"));
		optionsVar.get(1).click();
		String document = optionsVar.get(1).getText(); 
		
		existeixElementAssert("//*[@id='documentCommandForm']//button[contains(@onclick,'subdoc')]","No existía el botón de generar");
		assertTrue("El botón de 'Modificar' no estaba habilitado", driver.findElement(By.xpath("//*[@id='documentCommandForm']//button[contains(@onclick,'subdoc')]")).isEnabled());
		driver.findElement(By.xpath("//*[@id='documentCommandForm']//button[contains(@onclick,'subdoc')]")).click();
		acceptarAlerta();
		
		// Introducimos los datos para adjuntar el nuevo documento
		if (existeixElement("//*[@id='iconsFileInput_contingut0']//img[contains(@src,'img/cross.png')]")) {
			driver.findElement(By.xpath("//*[@id='iconsFileInput_contingut0']//img[contains(@src,'img/cross.png')]")).click();
		}
		
		driver.findElement(By.xpath("//input[@id='contingut0']")).sendKeys(pathArxiuPlantillaODT);
		driver.findElement(By.xpath("//button[contains(@onclick,'submit')]")).click();
				
		esperaFinExecucioMassiva(botoExecMassiva);		
		
		existeixElementAssert("//*[@id='infos']/p", "No se ejecutó la operación masiva correctamente");
				
		List<String> hash = new ArrayList<String>();
		// Comprobamos que se ha generado uno distinto para cada expediente
		for (int j = 1; j <= numExpedientesTramMasiva; j++) {
			consultarExpedientes(null, null, nomTipusExp);
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+j+"]//a[contains(@href,'/expedient/info.html')]")).click();
			
			driver.findElement(By.xpath("//*[@id='tabnav']//a[contains(text(), 'Documents')]")).click();
			existeixElementAssert("//*[@id='codi']/tbody/tr/td[contains(text(),'"+document+"')]","No se encontró el documento '"+document+"'");
			
			// Bajamos el documento
			byte[] archivo = downloadFile("//tr[contains(td/text(), '"+document+"')]//a[contains(@href,'/expedient/documentModificar.html')]", "blank.pdf");
			// Comprobamos que los hash de todos sean diferentes
			String md5 = getMd5(archivo);
			assertFalse("El documento '"+document+"' ya estaba en otro expediente",hash.contains(md5));
			hash.add(md5);
		}
	}

	@Test
	public void i_modificar_documents_esborrar_document() throws InterruptedException {
		carregarUrlConfiguracio();
		
		seleccionarEntorn(titolEntorn);
		
		consultarExpedientes(null, null, nomTipusExp);
		
		driver.findElement(By.xpath("//*[@id='massivaInfoForm']/button[2]")).click();
				
		// Visualizamos que se muestren todos los documentos		
		WebElement selectVar = driver.findElement(By.xpath("//*[@id='nom0']"));
		List<WebElement> optionsVar = selectVar.findElements(By.tagName("option"));
		optionsVar.get(1).click();
		String document = optionsVar.get(1).getText(); 
		
		existeixElementAssert("//*[@id='documentCommandForm']//button[contains(@onclick,'subdoc')]","No existía el botón de generar");
		assertTrue("El botón de 'Modificar' no estaba habilitado", driver.findElement(By.xpath("//*[@id='documentCommandForm']//button[contains(@onclick,'subdoc')]")).isEnabled());
		driver.findElement(By.xpath("//*[@id='documentCommandForm']//button[contains(@onclick,'subdoc')]")).click();
		acceptarAlerta();
		
		// Borramos el documento
		if (existeixElement("//*[@id='iconsFileInput_contingut0']//img[contains(@src,'img/cross.png')]")) {
			driver.findElement(By.xpath("//*[@id='iconsFileInput_contingut0']//img[contains(@src,'img/cross.png')]")).click();
		}
		
		driver.findElement(By.xpath("//button[contains(@onclick,'submit')]")).click();
				
		esperaFinExecucioMassiva(botoExecMassiva);		
		
		existeixElementAssert("//*[@id='infos']/p", "No se ejecutó la operación masiva correctamente");
				
		// Comprobamos que no exista el documento en cada expediente
		for (int j = 1; j <= numExpedientesTramMasiva; j++) {
			consultarExpedientes(null, null, nomTipusExp);
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+j+"]//a[contains(@href,'/expedient/info.html')]")).click();
			
			driver.findElement(By.xpath("//*[@id='tabnav']//a[contains(text(), 'Documents')]")).click();
			noExisteixElementAssert("//*[@id='codi']/tbody/tr/td[contains(text(),'"+document+"')]","Se encontró el documento '"+document+"'");
		}
	}
	
	@Test
	public void z_limpiar() throws InterruptedException {
		
		carregarUrlConfiguracio();
		
		seleccionarEntorn(titolEntorn);
		
		assignarPermisosEntorn(entorn, usuari, "DESIGN", "ORGANIZATION", "READ", "ADMINISTRATION");
		
		eliminarExpedient(null, null, nomTipusExp);
		
		// Eliminar el tipo de expediente
		eliminarTipusExpedient(codTipusExp);
		
		eliminarEntorn(entorn);
		
		screenshotHelper.saveScreenshot("TasquesDadesTasca/finalizar_expedient/1.png");	
	}
	
	private void copyFile(byte[] bFile, File dest) throws IOException {
		try {
			FileOutputStream fos = new FileOutputStream(dest.getAbsolutePath());
			fos.write(bFile);
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
