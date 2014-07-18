package net.conselldemallorca.helium.test.tramitacio;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.conselldemallorca.helium.test.util.BaseTest;
import net.conselldemallorca.helium.test.util.DocumentoExpedient;
import net.conselldemallorca.helium.test.util.VariableExpedient;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TasquesDadesDocumentsTasca extends BaseTest {
	String entorn = carregarPropietat("tramsel.entorn.nom", "Nom de l'entorn de proves no configurat al fitxer de properties");
	String titolEntorn = carregarPropietat("tramsel.entorn.titol", "Titol de l'entorn de proves no configurat al fitxer de properties");
	String usuari = carregarPropietat("test.base.usuari.configuracio", "Usuari configuració de l'entorn de proves no configurat al fitxer de properties");
	String nomDefProc = carregarPropietat("defproc.deploy.definicio.proces.nom", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String nomSubDefProc = carregarPropietat("defproc.deploy.definicio.subproces.nom", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String pathDefProc = carregarPropietatPath("defproc.deploy.definicio.subproces.main_direct.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String pathSubDefProc = carregarPropietatPath("defproc.subproces.deploy.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String exportDefProc = carregarPropietatPath("defproc.tasca_dades.exp.export.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String nomTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.nom", "Nom del tipus d'expedient de proves no configurat al fitxer de properties");
	String codTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.codi", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	String accioPathDefProc = carregarPropietatPath("tramsel_accio.deploy.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String exportTipExpProc = carregarPropietatPath("tramsel_accio.export.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String pathDefExpDocs = carregarPropietatPath("defproc.mod.exp_documents.export.arxiu.path", "Documento PDF a adjuntar 1");
	String pathArxiuPDF1 = carregarPropietatPath("deploy.arxiu.pdf.tramitacio_1", "Documento PDF a adjuntar 1");
	String hashArxiuPDF1 = carregarPropietat("deploy.arxiu.pdf.tramitacio_1.hash", "Hash documento PDF a adjuntar 1");
	String pathArxiuPDF2 = carregarPropietatPath("deploy.arxiu.pdf.tramitacio_2", "Documento PDF a adjuntar 1");
	String hashArxiuPDF2 = carregarPropietat("deploy.arxiu.pdf.tramitacio_2.hash", "Hash documento PDF a adjuntar 2");
	
	String nomCert = carregarPropietat("tramsel.firma_nom", "Hash documento PDF a adjuntar 2");
	String passCert = carregarPropietat("tramsel.firma_pass", "Hash documento PDF a adjuntar 2");
	
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

		importarDadesTipExp(codTipusExp, exportTipExpProc);
		
		desplegarDefinicioProcesEntorn(nomTipusExp, nomSubDefProc, pathSubDefProc);
		desplegarDefinicioProcesEntorn(nomTipusExp, nomDefProc, pathDefProc);
		
		importarDadesDefPro(nomDefProc, exportDefProc);
		importarDadesDefPro(nomSubDefProc, exportDefProc);
		
		importarDadesDefPro(nomDefProc, pathDefExpDocs);		
		
		screenshotHelper.saveScreenshot("tramitar/dadesexpedient/crear_dades/1.png");
	}

	@Test
	public void b_visualizacio_tasca_dades() throws InterruptedException {		
		carregarUrlConfiguracio();
		
		seleccionarEntorn(titolEntorn);
		
		iniciarExpediente( codTipusExp, "SE-22/2014", "Expedient de prova Selenium " + (new Date()).getTime());

		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/definicioProces/llistat.html')]")));
		actions.click();
		actions.build().perform();
		
		screenshotHelper.saveScreenshot("tramitar/dadesexpedient/visualizacio_dades_process/1.png");
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'"+nomDefProc+"')]")).click();
				
		driver.findElement(By.xpath("//*[@id='tabnav']//a[contains(@href,'/helium/definicioProces/campLlistat.html')]")).click();
		
		List<VariableExpedient> listaVariables = new ArrayList<VariableExpedient>();
		
		// Leemos las variables
		int i = 1;
		while(i <= driver.findElements(By.xpath("//*[@id='registre']/tbody/tr")).size()) {
			VariableExpedient variable = new VariableExpedient();
			
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+i+"]")).click();
			
			String codi = driver.findElement(By.xpath("//*[@id='codi0']")).getAttribute("value");
			String etiqueta = driver.findElement(By.xpath("//*[@id='etiqueta0']")).getAttribute("value");
			String tipo = driver.findElement(By.xpath("//*[@id='tipus0']")).getAttribute("value");
			String observaciones = driver.findElement(By.xpath("//*[@id='observacions0']")).getText();
			boolean multiple = driver.findElement(By.xpath("//*[@id='multiple0']")).isSelected();
			boolean oculta = driver.findElement(By.xpath("//*[@id='ocult0']")).isSelected();
			
			variable.setCodi(codi);
			variable.setEtiqueta(etiqueta);
			variable.setTipo(tipo);
			variable.setObservaciones(observaciones);
			variable.setOculta(oculta);
			variable.setMultiple(multiple);
			
			screenshotHelper.saveScreenshot("tramitar/dadesexpedient/visualizacio_dades_process/2"+i+".png");
			
			driver.findElement(By.xpath("//*[@id='command']/div[4]/button[2]")).click();
			
			if ("REGISTRE".equals(tipo)) {
				WebElement button = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+i+"]/td[6]/form/button"));
				button.click();
				int j = 1;
				while(j <= driver.findElements(By.xpath("//*[@id='registre']/tbody/tr")).size()) {
					VariableExpedient variableReg = new VariableExpedient();
					
					driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+j+"]")).click();
					
					String[] var = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+j+"]/td[1]")).getText().split("/");
					String codiReg = var[0];
					String etiquetaReg = var[1];
					boolean obligatorioReg = "Si".equals(driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+j+"]/td[3]")).getText());
					String tipoReg = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+j+"]/td[2]")).getText();

					variableReg.setCodi(codiReg);
					variableReg.setEtiqueta(etiquetaReg);
					variableReg.setTipo(tipoReg);
					variableReg.setObligatorio(obligatorioReg);
					variableReg.setMultiple(multiple); // Si lo era la variable padre del registro
					
					variable.getRegistro().add(variableReg);
					
					screenshotHelper.saveScreenshot("tramitar/dadesexpedient/visualizacio_dades_process/3"+i+"-"+j+".png");
					j++;
				}
				
				driver.findElement(By.xpath("//*[@id='command']/div[2]/button[2]")).click();
			}
			
			listaVariables.add(variable);
			
			screenshotHelper.saveScreenshot("tramitar/dadesexpedient/visualizacio_dades_process/3"+i+".png");
			i++;
		}
		
		// Vemos el resto de parámetros de la primera tarea
		driver.findElement(By.xpath("//*[@id='tabnav']//a[contains(@href,'/definicioProces/tascaLlistat.html')]")).click();
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr/td[3]/form/button")).click();
				
		Iterator<VariableExpedient> it = listaVariables.iterator();
		while(it.hasNext()) {
			VariableExpedient var = it.next();
			if (existeixElement("//td[contains(text(),'"+var.getCodi()+"/"+var.getEtiqueta()+"')]")) {
				boolean obligatorio = driver.findElement(By.xpath("//td[contains(text(),'"+var.getCodi()+"/"+var.getEtiqueta()+"')]/parent::tr/td[4]/input")).isSelected();
				var.setObligatorio(obligatorio);
				
				boolean readonly = driver.findElement(By.xpath("//td[contains(text(),'"+var.getCodi()+"/"+var.getEtiqueta()+"')]/parent::tr/td[5]/input")).isSelected();
				var.setReadOnly(readonly);
			} else {
				it.remove();
			}
		}
		
		consultarTareas(null, null, nomTipusExp, false);
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr/td[contains(a/text(), '01 - Entrada')]/a")).click();
				
		// Comprobamos que se muestran los labels, variables, observaciones y botones según el tipo de variable
				
		for (VariableExpedient variable : listaVariables) {
			comprobarVariable(variable, false);			
		}
		
		// Guardamos y validamos
		driver.findElement(By.xpath("//*/button[contains(text(), 'Guardar')]")).click();
		existeixElementAssert("//*[@id='infos']/p", "No se guardó correctamente");
		
		driver.findElement(By.xpath("//*/button[contains(text(), 'Validar')]")).click();
		existeixElementAssert("//*[@id='infos']/p", "No se validó correctamente");
		noExisteixElementAssert("//*/div[contains(@class, 'error')]", "Existían errores de validación");
		
		existeixElementAssert("//*/button[contains(text(), 'Modificar')]", "No existía el botón modificar");
		
		screenshotHelper.saveScreenshot("TasquesDadesTasca/visualizacio_tasca_dades/5.png");
	}
	
	@Test
	public void c_documents() throws InterruptedException {		
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
		
		List<DocumentoExpedient> documentosExpedient = new ArrayList<DocumentoExpedient>();
		
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
			documentosExpedient.add(documento);
			
			screenshotHelper.saveScreenshot("tramitar/dadesexpedient/visualizacio_dades_process/2"+i+".png");
			
			driver.findElement(By.xpath("//*/button[contains(text(), 'Cancel·lar')]")).click();
			
			screenshotHelper.saveScreenshot("tramitar/dadesexpedient/visualizacio_dades_process/3"+i+".png");
			i++;
		}
		
		// Vemos el resto de parámetros de la primera tarea
		driver.findElement(By.xpath("//*[@id='tabnav']//a[contains(@href,'/definicioProces/tascaLlistat.html')]")).click();
		driver.findElement(By.xpath("//tr[1]//form[contains(@action,'tascaDocuments.html')]//button[1]")).click();
				
		Iterator<DocumentoExpedient> it = documentosExpedient.iterator();
		while(it.hasNext()) {
			DocumentoExpedient var = it.next();
			if (!existeixElement("//td[contains(text(),'"+var.getCodi()+"/"+var.getNom()+"')]")) {
				it.remove();
			}
		}
		
		consultarTareas(null, null, nomTipusExp, false);
		
		driver.findElement(By.xpath("//*[@id='registre']//a[contains(text(), '01 - Entrada')]")).click();
				
		// Comprobamos que se muestran los labels, variables, observaciones y botones según el tipo de variable
				
		driver.findElement(By.xpath("//*[@id='tabnav']//a[contains(@href,'/tasca/documents.html')]")).click();
		
		for (DocumentoExpedient documento : documentosExpedient) {
			existeixElement("//*[@id='documentCommand_"+documento.getCodi()+"']/parent::div/h4", "No existía el nombre del documento");
			existeixElement("//*[@id='documentCommand_"+documento.getCodi()+"']/parent::div//input[@type='text']", "Ya se subió el docuento");
			
			String data = driver.findElement(By.xpath("//*[@id='documentCommand_"+documento.getCodi()+"']/parent::div//input[@type='text']")).getAttribute("value");
			
			if (documento.isEsPlantilla()) {
				// Comprobamos que existen los 3 botones
				existeixElement("//*/h4[contains(label/text(), '"+documento.getNom()+"')]/a[contains(@href,'/tasca/documentGenerar.html')]", "no existía el botón de generar");
				existeixElement("//*/h4[contains(label/text(), '"+documento.getNom()+"')]/a[contains(@href,'/document/arxiuMostrar.html')]", "no existía el botón de descargar");
				existeixElement("//*/h4[contains(label/text(), '"+documento.getNom()+"')]/a[contains(@href,'/tasca/documentEsborrar.html')]", "no existía el botón de eliminar");

				driver.findElement(By.xpath("//*/h4[contains(label/text(), '"+documento.getNom()+"')]/a[contains(@href,'/tasca/documentGenerar.html')]")).click();
				acceptarAlerta();
				
				existeixElementAssert("//*[@id='infos']/p", "No se generó correctamente");
				
				// Lo borramos y lo volvemos a generar
				driver.findElement(By.xpath("//*/h4[contains(label/text(), '"+documento.getNom()+"')]/a[contains(@href,'/tasca/documentEsborrar.html')]")).click();
				existeixElementAssert("//*[@id='infos']/p", "No se borró correctamente");
				
				driver.findElement(By.xpath("//*/h4[contains(label/text(), '"+documento.getNom()+"')]/a[contains(@href,'/tasca/documentGenerar.html')]")).click();
				acceptarAlerta();
				
				existeixElementAssert("//*[@id='infos']/p", "No se generó correctamente");

				// Lo descargamos
				downloadFile("//*/h4[contains(label/text(), '"+documento.getNom()+"')]/a[2]", "");
			} else {
				driver.findElement(By.xpath("//*[@id='documentCommand_"+documento.getCodi()+"']/parent::div//input[@id='contingut0']")).sendKeys(pathArxiuPDF1);
				driver.findElement(By.xpath("//*[@id='documentCommand_"+documento.getCodi()+"']/parent::div//button[contains(text(), 'Guardar')]")).click();
				
				existeixElementAssert("//*[@id='infos']/p", "No se guardó correctamente");
				
				// Comprobamos que existen los 2 botones
				existeixElement("//*/h4[contains(label/text(), '"+documento.getNom()+"')]/a[contains(@href,'/document/arxiuMostrar.html')]", "no existía el botón de descargar");
				existeixElement("//*/h4[contains(label/text(), '"+documento.getNom()+"')]/a[contains(@href,'/tasca/documentEsborrar.html')]", "no existía el botón de eliminar");
				
				// Lo descargamos
				byte[] file = downloadFile("//*/h4[contains(label/text(), '"+documento.getNom()+"')]/a[contains(@href,'/document/arxiuMostrar.html')]", "blank.pdf");
				assertTrue("No se pudo descargar el fichero 1 '"+documento.getNom()+"'", file.length > 0);
				
				// Lo borramos y lo volvemos a subir
				driver.findElement(By.xpath("//*/h4[contains(label/text(), '"+documento.getNom()+"')]/a[contains(@href,'/tasca/documentEsborrar.html')]")).click();
				existeixElementAssert("//*[@id='infos']/p", "No se borró correctamente");
				
				driver.findElement(By.xpath("//*[@id='documentCommand_"+documento.getCodi()+"']/parent::div//input[@id='contingut0']")).sendKeys(pathArxiuPDF1);
				driver.findElement(By.xpath("//*[@id='documentCommand_"+documento.getCodi()+"']/parent::div//button[contains(text(), 'Guardar')]")).click();
				
				existeixElementAssert("//*[@id='infos']/p", "No se guardó correctamente");
				
				// Lo descargamos
				file = downloadFile("//*/h4[contains(label/text(), '"+documento.getNom()+"')]/a[contains(@href,'/document/arxiuMostrar.html')]", "blank.pdf");
				assertTrue("No se pudo descargar el fichero 2 '"+documento.getNom()+"'", file.length > 0);
			}
		
			String textoDoc = driver.findElement(By.xpath("//*/h4[contains(label/text(), '"+documento.getNom()+"')]/p")).getText();

			assertTrue("La fecha del documento no se corresponde", textoDoc.contains("Data del document: " + data));
		}		
		
		screenshotHelper.saveScreenshot("TasquesDadesTasca/visualizacio_tasca_dades/5.png");
	}

	@Test
	public void d_signatura() throws InterruptedException, IOException {		
		carregarUrlConfiguracio();
		
		seleccionarEntorn(titolEntorn);
		
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/definicioProces/llistat.html')]")));
		actions.click();
		actions.build().perform();
		
		screenshotHelper.saveScreenshot("tramitar/dadesexpedient/visualizacio_dades_process/1.png");
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'"+nomDefProc+"')]")).click();
				
		driver.findElement(By.xpath("//*[@id='tabnav']//a[contains(@href,'/definicioProces/tascaLlistat.html')]")).click();
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr/td[contains(text(),'01 - Entrada')]/parent::tr/td[5]/form/button")).click();
		
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
		
		consultarTareas(null, null, nomTipusExp, false);
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr/td[contains(a/text(), '01 - Entrada')]/a")).click();
				
		driver.findElement(By.xpath("//*[@id='tabnav']//a[contains(@href,'/tasca/signatures.html')]")).click();

		for (DocumentoExpedient documento : documentosExpedient) {
			byte[] archivoOriginal = downloadFile("//h4[contains(label/text(), '"+documento.getNom()+"')]/a[contains(@href,'/document/arxiuPerSignar.html')]", "blank.pdf");
			
			WebElement select = driver.findElement(By.xpath("//h4[contains(label/text(), '"+documento.getNom()+"')]/parent::div//select"));
			List<WebElement> options = select.findElements(By.tagName("option"));
			for (WebElement option : options) {
				if (option.getText().equals(nomCert)) {
					option.click();
					break;
				}
			}
			driver.findElement(By.xpath("//h4[contains(label/text(), '"+documento.getNom()+"')]/parent::div//input[@type='password']")).clear();
			driver.findElement(By.xpath("//h4[contains(label/text(), '"+documento.getNom()+"')]/parent::div//input[@type='password']")).sendKeys(passCert);
						
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
		
		// Finalizamos
		driver.findElement(By.xpath("//*[@id='formFinalitzar']//button")).click();
		Thread.sleep(1000*5);
		acceptarAlerta();
		existeixElementAssert("//*[@id='infos']/p", "No se finalizó la tarea correctamente");
	}
	
	@Test
	public void f_adjuntar_documents() throws InterruptedException {
		carregarUrlConfiguracio(); 
		
		seleccionarEntorn(titolEntorn);
		
		// Adjuntamos 2 documentos
		adjuntarDocExpediente(null, null, "Título del documento 1", "12/12/2014", pathArxiuPDF1);
		screenshotHelper.saveScreenshot("documentsexpedient/adjuntar_documents/1.png");
		
		adjuntarDocExpediente(null, null, "Título del documento 2", "13/12/2014", pathArxiuPDF2);
		screenshotHelper.saveScreenshot("documentsexpedient/adjuntar_documents/2.png");		
	}
	
	@Test
	public void g_visualizacio_documents_i_descarrega() throws InterruptedException {
		carregarUrlConfiguracio(); 
		
		seleccionarEntorn(titolEntorn);
		
		screenshotHelper.saveScreenshot("documentsexpedient/visualizacio_documents_i_descarrega/1.png");
		
		consultarExpedientes(null, null, nomTipusExp);
		
		screenshotHelper.saveScreenshot("documentsexpedient/visualizacio_documents_i_descarrega/2.png");
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]//a[contains(@href,'/expedient/info.html')]")).click();
		
		driver.findElement(By.xpath("//*[@id='tabnav']//a[contains(@href,'/expedient/documents.html')]")).click();
		
		for (int i = 1; i <= driver.findElements(By.xpath("//*[@id='codi']/tbody/tr")).size(); i++) {
			// Los descargamos
			downloadFile("//*[@id='codi']/tbody/tr["+i+"]//a[contains(@href,'/document/arxiuMostrar.html')]", "blank.pdf");
			screenshotHelper.saveScreenshot("documentsexpedient/visualizacio_documents_i_descarrega/4-"+i+".png");
		}
	}
	
	@Test
	public void h_generar_document() throws InterruptedException {
		carregarUrlConfiguracio(); 

		seleccionarEntorn(titolEntorn);
		
		screenshotHelper.saveScreenshot("documentsexpedient/generar_document/1.png");
		
		consultarExpedientes(null, null, nomTipusExp);
		
		screenshotHelper.saveScreenshot("documentsexpedient/generar_document/2.png");
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]//a[contains(@href,'/expedient/info.html')]")).click();
		
		driver.findElement(By.xpath("//*[@id='tabnav']//a[contains(@href,'/expedient/documents.html')]")).click();
		
		// Generamos los documentos
		assertTrue("No había documentos adjuntos", !driver.findElements(By.xpath("//*[@id='codi']/tbody/tr")).isEmpty());
				
		screenshotHelper.saveScreenshot("documentsexpedient/generar_document/3.png");
		
		boolean probado = false;
		for (int i = 1; i <= driver.findElements(By.xpath("//*[@id='codi']/tbody/tr")).size(); i++) {
			// Si estaba firmado, lo desfirmamos
			if (existeixElement("//*[@id='codi']/tbody/tr["+i+"]//a[contains(@href,'/expedient/signaturaEsborrar.html')]")) {
				driver.findElement(By.xpath("//*[@id='codi']/tbody/tr["+i+"]//a[contains(@href,'/expedient/signaturaEsborrar.html')]")).click();
				acceptarAlerta();
			}
			if (existeixElement("//*[@id='codi']/tbody/tr["+i+"]//a[contains(@href,'/expedient/documentModificar.html')]")) {
				driver.findElement(By.xpath("//*[@id='codi']/tbody/tr["+i+"]//a[contains(@href,'/expedient/documentModificar.html')]")).click();
				acceptarAlerta();
				
				// Generamos un documento
				if (existeixElement("//*[@id='content']//a[contains(@href,'/expedient/documentGenerar.html')]")) {
					String titulo = driver.findElement(By.xpath("//*[@id='content']/div/h4")).getText();
					byte[] document = downloadFile("//*[@id='content']//a[contains(@href,'/expedient/documentGenerar.html')]", titulo+".doc");
					assertTrue("No se generó el documento num "+i+" Título: "+titulo+".doc", document.length > 0);
					probado = true;
				}
				
				screenshotHelper.saveScreenshot("documentsexpedient/generar_document/4-"+i+".png");
				
				driver.findElement(By.xpath("//*[@id='command']/div[3]/button[2]")).click();
			}
		}
		assertTrue("No se encontró ningún documento para generar", probado);
	}
	
	@Test
	public void i_descarregar_document() throws InterruptedException {
		carregarUrlConfiguracio(); 
		
		seleccionarEntorn(titolEntorn);
		
		screenshotHelper.saveScreenshot("documentsexpedient/descarregar_document/1.png");
		
		consultarExpedientes(null, null, nomTipusExp);
		
		screenshotHelper.saveScreenshot("documentsexpedient/descarregar_document/2.png");
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]//a[contains(@href,'/expedient/info.html')]")).click();
		
		driver.findElement(By.xpath("//*[@id='tabnav']//a[contains(@href,'/expedient/documents.html')]")).click();
		
		// Descargamos el documento
		int i = 1;
		boolean probada = false;
		while (existeixElement("//*[@id='codi']/tbody/tr["+i+"]")) {
			if (existeixElement("//*[@id='codi']/tbody/tr["+i+"]//a[contains(@href,'/expedient/documentModificar.html')]")) {
				driver.findElement(By.xpath("//*[@id='codi']/tbody/tr["+i+"]//a[contains(@href,'/expedient/documentModificar.html')]")).click();
				acceptarAlerta();		
				downloadFile("//*[@id='iconsFileInput_contingut0']/a[1]", "Título del documento 1");		
				screenshotHelper.saveScreenshot("documentsexpedient/descarregar_document/3-"+i+".png");		
				driver.findElement(By.xpath("//*[@id='command']/div[3]/button[2]")).click();
				probada = true;
			}
			i++;
		}
		
		assertTrue("No había documentos que descargar", probada);
		screenshotHelper.saveScreenshot("documentsexpedient/descarregar_document/4.png");		
	}
	
	@Test
	public void j_modificar_data_document() throws InterruptedException {
		carregarUrlConfiguracio(); 
		
		seleccionarEntorn(titolEntorn);
		
		screenshotHelper.saveScreenshot("documentsexpedient/modificar_data_document/1.png");
		
		consultarExpedientes(null, null, nomTipusExp);
		
		screenshotHelper.saveScreenshot("documentsexpedient/modificar_data_document/2.png");
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]//a[contains(@href,'/expedient/info.html')]")).click();
		
		driver.findElement(By.xpath("//*[@id='tabnav']//a[contains(@href,'/expedient/documents.html')]")).click();
		
		int i = 1;
		boolean probada = false;
		while (existeixElement("//*[@id='codi']/tbody/tr["+i+"]")) {
			if (existeixElement("//*[@id='codi']/tbody/tr["+i+"]//a[contains(@href,'/expedient/documentModificar.html')]")) {
				driver.findElement(By.xpath("//*[@id='codi']/tbody/tr["+i+"]//a[contains(@href,'/expedient/documentModificar.html')]")).click();
				acceptarAlerta();
				if (existeixElement("//*[@id='nom0']")) {
					driver.findElement(By.xpath("//*[@id='nom0']")).clear();
					driver.findElement(By.xpath("//*[@id='nom0']")).sendKeys("Título mod 1");
				}
				if (existeixElement("//*[@id='data0']")) {
					driver.findElement(By.xpath("//*[@id='data0']")).clear();
					driver.findElement(By.xpath("//*[@id='data0']")).sendKeys("15/12/2014");
				}
				driver.findElement(By.xpath("//img[contains(@src,'/img/cross.png')]")).click();
				driver.findElement(By.id("contingut0")).sendKeys(pathArxiuPDF1);
				screenshotHelper.saveScreenshot("documentsexpedient/modificar_data_document/3-1.png");
				driver.findElement(By.xpath("//*[@id='command']/div[3]/button[1]")).click();
				existeixElementAssert("//*[@id='infos']", "No se modificó el docuento");
				probada = true;
			}
			i++;
		}
		
		assertTrue("No había documentos que modificar", probada);
		screenshotHelper.saveScreenshot("documentsexpedient/modificar_data_document/4.png");
	}
	
	@Test
	public void k_esborrar_document_adjunt() throws InterruptedException {
		carregarUrlConfiguracio(); 
		
		seleccionarEntorn(titolEntorn);
		
		screenshotHelper.saveScreenshot("documentsexpedient/esborrar_document_adjunt/1.png");
		
		consultarExpedientes(null, null, nomTipusExp);
		
		screenshotHelper.saveScreenshot("documentsexpedient/esborrar_document_adjunt/2.png");
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]//a[contains(@href,'/expedient/info.html')]")).click();
		
		driver.findElement(By.xpath("//*[@id='tabnav']//a[contains(@href,'/expedient/documents.html')]")).click();
		
		// Borramos los documentos
		assertTrue("No había documentos adjuntos", !driver.findElements(By.xpath("//*[@id='codi']/tbody/tr")).isEmpty());
		
		while (!driver.findElements(By.xpath("//*[@id='codi']/tbody/tr")).isEmpty()) {
			// Si estaba firmado, lo desfirmamos
			if (existeixElement("//*[@id='codi']/tbody/tr[1]//a[contains(@href,'/expedient/signaturaEsborrar.html')]")) {
				driver.findElement(By.xpath("//*[@id='codi']/tbody/tr[1]//a[contains(@href,'/expedient/signaturaEsborrar.html')]")).click();
				acceptarAlerta();
			}
			// Lo borramos
			driver.findElement(By.xpath("//*[@id='codi']/tbody/tr[1]//a[contains(@href,'/expedient/documentEsborrar.html')]")).click();
			acceptarAlerta();
			existeixElementAssert("//*[@id='infos']", "No se borró el docuento");
		}
		
		screenshotHelper.saveScreenshot("documentsexpedient/esborrar_document_adjunt/3.png");
	}
	
	@Test
	public void l_esborrar_document_tipus_expedient() throws InterruptedException {
		carregarUrlConfiguracio(); 
		
		seleccionarEntorn(titolEntorn);
		
		screenshotHelper.saveScreenshot("documentsexpedient/esborrar_document_tipus_expedient/1.png");
		
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//*[@id='menuDisseny']//a[contains(@href,'/expedientTipus/llistat.html')]")));
		actions.click();
		actions.build().perform();
				
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr/td[contains(a/text(),'"+codTipusExp+"')]")).click();
		
		driver.findElement(By.xpath("//*[@id='tabnav']//a[contains(@href,'/expedientTipus/documentLlistat.html')]")).click();
		screenshotHelper.saveScreenshot("documentsexpedient/esborrar_document_tipus_expedient/2.png");
		
		WebElement select = driver.findElement(By.xpath("//select[@name='definicioProcesId']"));
		List<WebElement> options = select.findElements(By.tagName("option"));
		for (WebElement option : options) {
			if (option.getText().equals(nomDefProc)) {
				option.click();
				break;
			}
		}
		
		// Entramos a modificar el documento y lo borramos
		assertFalse("No había documentos adjuntos", options.isEmpty());
		screenshotHelper.saveScreenshot("documentsexpedient/esborrar_document_tipus_expedient/3.png");
		
		boolean probado = false;
		while (existeixElement("//*[@id='registre']/tbody/tr/td/a/img[1]")) {
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr/td/a/img[1]//parent::a//parent::td//parent::tr/td[1]/a")).click();
			
			if (existeixElement("//*[@id='iconsFileInput_arxiuContingut0']/a[2]/img")) {
				driver.findElement(By.xpath("//*[@id='iconsFileInput_arxiuContingut0']/a[2]/img")).click();
				
				driver.findElement(By.xpath("//*[@id='command']/div[3]/button[1]")).click();
				
				existeixElementAssert("//*[@id='infos']", "No se borró el documento");
				probado = true;
			}
		}
		
		assertTrue("No se encontró ningún documento para borrar", probado);
		screenshotHelper.saveScreenshot("documentsexpedient/esborrar_document_tipus_expedient/4.png");
	}
	
	@Test
	public void m_execucio_accions() throws InterruptedException {
		carregarUrlConfiguracio();
		
		seleccionarEntorn(titolEntorn);

		String pathDefProc = carregarPropietatPath("tramsel_accio.deploy.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
		String exportTipExpProc = carregarPropietatPath("tramsel_accio.export.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
		
		desplegarDefinicioProcesEntorn(nomTipusExp, nomDefProc, pathDefProc);
		
		importarDadesTipExp(codTipusExp, exportTipExpProc);

		screenshotHelper.saveScreenshot("accions/executar/1.png");

		iniciarExpediente( codTipusExp, "SE-23/2014", "Expedient de prova Selenium " + (new Date()).getTime());
		
		consultarExpedientes(null, null, nomTipusExp);
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]//a[contains(@href,'/expedient/info.html')]")).click();

		driver.findElement(By.xpath("//*[@id='tabnav']//a[contains(text(), 'Dades')]")).click();
		if (existeixElement("//*[@id='codi']/tbody/tr/td[contains(text(),'message')]")) {
			driver.findElement(By.xpath("//*[@id='codi']/tbody/tr/td[contains(text(),'message')]/parent::tr//img[@src='/helium/img/cross.png']")).click();
			acceptarAlerta();
		}
		
		driver.findElement(By.xpath("//*[@id='tabnav']//a[contains(text(), 'Expedient')]")).click();		
		driver.findElement(By.xpath("//button[contains(text(),'Enviar mensaje')]")).click();
		acceptarAlerta();
		existeixElementAssert("//*[@id='infos']/p", "No se ejecutó la acción correctamente");
		
		driver.findElement(By.xpath("//*[@id='tabnav']//a[contains(text(), 'Dades')]")).click();
		existeixElementAssert("//*[@id='codi']/tbody/tr/td[contains(text(),'message')]","No se encontró la variable 'message'");
		
		String mensaje = driver.findElement(By.xpath("//*[@id='codi']/tbody/tr/td[contains(text(),'message')]/parent::tr/td[2]")).getText().trim();
		assertTrue("El valor de la variable 'message' no era el esperado", "Se ha ejecutado la acción".equals(mensaje));
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
