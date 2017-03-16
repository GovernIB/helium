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
public class DadesExpedient extends BaseTest {
	
	String entorn = carregarPropietat("tramsel.entorn.nom", "Nom de l'entorn de proves no configurat al fitxer de properties");
	String titolEntorn = carregarPropietat("tramsel.entorn.titol", "Titol de l'entorn de proves no configurat al fitxer de properties");
	String nomDefProc = carregarPropietat("defproc.deploy.definicio.proces.nom", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String nomSubDefProc = carregarPropietat("defproc.deploy.definicio.subproces.nom", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String usuari = carregarPropietat("test.base.usuari.configuracio", "Usuari configuració de l'entorn de proves no configurat al fitxer de properties");
	String exportTipExpProc = carregarPropietatPath("tipexp.tasca_dades_doc.exp.export.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String nomTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.nom", "Nom del tipus d'expedient de proves no configurat al fitxer de properties");
	String codTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.codi", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	
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
		
		screenshotHelper.saveScreenshot("tramitar/dadesexpedient/crear_dades/1.png");
	}

	@Test
	public void b1_visualizacio_dades_process() throws InterruptedException {		
		carregarUrlConfiguracio();
		
		seleccionarEntorn(titolEntorn);
		
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/definicioProces/llistat.html')]")));
		actions.click();
		actions.build().perform();
		
		screenshotHelper.saveScreenshot("tramitar/dadesexpedient/visualizacio_dades_process/1.png");
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'"+nomDefProc+"')]")).click();
				
		driver.findElement(By.xpath("//*[@id='tabnav']//a[contains(@href,'/definicioProces/campLlistat.html')]")).click();
		
		int i = 1;
		while(i <= driver.findElements(By.xpath("//*[@id='registre']/tbody/tr")).size()) {
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+i+"]")).click();
			
			String codi = driver.findElement(By.xpath("//*[@id='codi0']")).getAttribute("value");
			String etiqueta = driver.findElement(By.xpath("//*[@id='etiqueta0']")).getAttribute("value");
			String tipus = driver.findElement(By.xpath("//*[@id='tipus0']")).getAttribute("value");
			boolean multiple = driver.findElement(By.xpath("//*[@id='multiple0']")).isSelected();
			
			screenshotHelper.saveScreenshot("tramitar/dadesexpedient/visualizacio_dades_process/2"+i+".png");
			
			driver.findElement(By.xpath("//*[@id='command']/div[4]/button[2]")).click();
			
			String codiTabla = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+i+"]/td[1]")).getText();
			String etiquetaTabla = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+i+"]/td[2]")).getText();
			String tipusTabla = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+i+"]/td[3]")).getText();
			String multipleTabla = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+i+"]/td[4]")).getText();
			
			assertTrue("Error en el código de la variable: " + codi, codi.equals(codiTabla));
			assertTrue("Error en la etiqueta de la variable: " + codi, etiqueta.equals(etiquetaTabla));
			assertTrue("Error en el campo tipo de la variable: " + codi, tipus.equals(tipusTabla));
			assertTrue("Error en el campo múltiple de la variable: " + codi, (multiple?"Si":"No").equals(multipleTabla));
			
			if ("REGISTRE".equals(tipus)) {
				WebElement button = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+i+"]/td[6]/form/button"));
				String text = button.getText();
				int elementos = Integer.valueOf(text.substring(text.indexOf("(")+1, text.indexOf(")")));
				button.click();
				
				int elementosTabla = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr")).size();
				
				driver.findElement(By.xpath("//*[@id='command']/div[2]/button[2]")).click();
				
				assertTrue("Error en el número de elementos de la variable de registro: " + codi, elementos == elementosTabla);
			}
			screenshotHelper.saveScreenshot("tramitar/dadesexpedient/visualizacio_dades_process/3"+i+".png");
			i++;
		}
	}

	@Test
	public void b2_visualizacio_dades_subprocess() throws InterruptedException {		
		carregarUrlConfiguracio();
		
		seleccionarEntorn(titolEntorn);
		
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/definicioProces/llistat.html')]")));
		actions.click();
		actions.build().perform();
		
		screenshotHelper.saveScreenshot("tramitar/dadesexpedient/visualizacio_dades_subprocess/1.png");
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'"+nomDefProc+"')]")).click();
		
		WebElement element = driver.findElement(By.xpath("//*[@id='content']/dl"));
		List<WebElement> claves = element.findElements(By.xpath("dt"));
		List<WebElement> valores = element.findElements(By.xpath("dd"));
		String nomSubDef = null;
		for (int i = 0; i < claves.size(); i++) {
			if ("Subdefinicions de procés".equals(claves.get(i).getText())) {
				nomSubDef = valores.get(i).getText().trim();
				break;
			}
		}
		
		assertTrue("No había ninguna definición de proceso", nomSubDef != null);
		
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/definicioProces/llistat.html')]")));
		actions.click();
		actions.build().perform();
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'"+nomSubDefProc+"')]")).click();
		
		WebElement elementSub = driver.findElement(By.xpath("//*[@id='content']/dl"));
		List<WebElement> clavesSub = elementSub.findElements(By.xpath("dt"));
		List<WebElement> valoresSub = elementSub.findElements(By.xpath("dd"));
		String nomSubDefSub = null;
		for (int i = 0; i < clavesSub.size(); i++) {
			if ("Id".equals(clavesSub.get(i).getText())) {
				nomSubDefSub = valoresSub.get(i).getText().trim();
				break;
			}
		}
		
		assertTrue("No se corresponde el subproceso con el de la definición de proceso", nomSubDef.equals(nomSubDefSub));
		
		screenshotHelper.saveScreenshot("tramitar/dadesexpedient/visualizacio_dades_subprocess/2.png");		
		
		driver.findElement(By.xpath("//*[@id='tabnav']//a[contains(@href,'/definicioProces/campLlistat.html')]")).click();
		
		int i = 1;
		while(i <= driver.findElements(By.xpath("//*[@id='registre']/tbody/tr")).size()) {
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+i+"]")).click();
			
			String codi = driver.findElement(By.xpath("//*[@id='codi0']")).getAttribute("value");
			String etiqueta = driver.findElement(By.xpath("//*[@id='etiqueta0']")).getAttribute("value");
			String tipus = driver.findElement(By.xpath("//*[@id='tipus0']")).getAttribute("value");
			boolean multiple = driver.findElement(By.xpath("//*[@id='multiple0']")).isSelected();
			
			driver.findElement(By.xpath("//*[@id='command']/div[4]/button[2]")).click();
			
			String codiTabla = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+i+"]/td[1]")).getText();
			String etiquetaTabla = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+i+"]/td[2]")).getText();
			String tipusTabla = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+i+"]/td[3]")).getText();
			String multipleTabla = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+i+"]/td[4]")).getText();
			screenshotHelper.saveScreenshot("tramitar/dadesexpedient/visualizacio_dades_subprocess/3"+i+".png");
			assertTrue("Error en el código de la variable: " + codi, codi.equals(codiTabla));
			assertTrue("Error en la etiqueta de la variable: " + codi, etiqueta.equals(etiquetaTabla));
			assertTrue("Error en el campo tipo de la variable: " + codi, tipus.equals(tipusTabla));
			assertTrue("Error en el campo múltiple de la variable: " + codi, (multiple?"Si":"No").equals(multipleTabla));
			
			if ("REGISTRE".equals(tipus)) {
				WebElement button = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+i+"]/td[6]/form/button"));
				String text = button.getText();
				int elementos = Integer.valueOf(text.substring(text.indexOf("(")+1, text.indexOf(")")));
				button.click();
				
				int elementosTabla = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr")).size();
				
				driver.findElement(By.xpath("//*[@id='command']/div[2]/button[2]")).click();
				
				assertTrue("Error en el número de elementos de la variable de registro: " + codi, elementos == elementosTabla);
			}
			screenshotHelper.saveScreenshot("tramitar/dadesexpedient/visualizacio_dades_subprocess/4"+i+".png");
			i++;
		}
	}

	@Test
	public void c_ordre_i_agrupacions() throws InterruptedException {		
		carregarUrlConfiguracio();
		
		seleccionarEntorn(titolEntorn);
		
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/definicioProces/llistat.html')]")));
		actions.click();
		actions.build().perform();
		
		screenshotHelper.saveScreenshot("tramitar/dadesexpedient/ordre_i_agrupacions/1.png");
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'"+nomDefProc+"')]")).click();
				
		driver.findElement(By.xpath("//a[contains(@href,'/definicioProces/campAgrupacioLlistat.html')]")).click();
		
		int i = 1;
		int numAgrupaciones = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr")).size();
		while(i <= numAgrupaciones) {
			int j = 1;
			while (existeixElement("//*[@id='registre']/tbody/tr/td[1]//a[contains(text(), 'Agrupacion_"+j+"')]")) {
				j++;
			}
			String codiAgrupacio = "Agrupacion_" + j;
			
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+i+"]//a")).click();
			
			driver.findElement(By.xpath("//*[@id='codi0']")).clear();
			driver.findElement(By.xpath("//*[@id='codi0']")).sendKeys(codiAgrupacio);
			
			driver.findElement(By.xpath("//*[@id='nom0']")).clear();
			driver.findElement(By.xpath("//*[@id='nom0']")).sendKeys("Nom " + codiAgrupacio);
			
			driver.findElement(By.xpath("//*[@id='descripcio0']")).clear();
			driver.findElement(By.xpath("//*[@id='descripcio0']")).sendKeys("Descripción de " + codiAgrupacio);
						
			screenshotHelper.saveScreenshot("tramitar/dadesexpedient/ordre_i_agrupacions/2"+i+".png");
			
			driver.findElement(By.xpath("//*[@id='command']//button[contains(text(), 'Modificar')]")).click();
			
			existeixElementAssert("//*[@id='infos']/p", "No se modificó la agrupación de la fila " + i);

			WebElement button = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+i+"]/td[3]/form/button"));
			String text = button.getText();
			int elementos = Integer.valueOf(text.substring(text.indexOf("(")+1, text.indexOf(")")));
			button.click();
			
			int elementosTabla = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr")).size();
			
			screenshotHelper.saveScreenshot("tramitar/dadesexpedient/ordre_i_agrupacions/3"+i+".png");
			
			assertTrue("Error en el número de elementos de la variable Agrupacion_"+i, elementos == elementosTabla);
			
			// Cambiar de orden las variables
			if (elementosTabla > 1) {
			int k = 1;			
				while (k <= elementosTabla) {
					sortTable("registre", k, (k < elementosTabla) ? k+1 : k-1);
					
					screenshotHelper.saveScreenshot("tramitar/dadesexpedient/ordre_i_agrupacions/4"+i+"-"+k+".png");
					
					k++;
				}
			}
			// Crear y borrar
			WebElement selectTipusExpedient = driver.findElement(By.xpath("//*[@id='id0']"));
			selectTipusExpedient.findElements(By.tagName("option")).get(1).click();
			
			driver.findElement(By.xpath("//*[@id='command']/fieldset//button[1]")).click();
			existeixElementAssert("//*[@id='infos']/p", "No se añadió la agrupación de la fila " + i);
			
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+(elementosTabla+1)+"]/td[2]/a/img")).click();
			acceptarAlerta();
			existeixElementAssert("//*[@id='infos']/p", "No se borró la agrupación de la fila " + i);
			
			screenshotHelper.saveScreenshot("tramitar/dadesexpedient/ordre_i_agrupacions/5"+i+".png");
			
			driver.findElement(By.xpath("//*[@id='command']/fieldset//button[2]")).click();
			
			i++;
		}
		
		int j = 1;
		while (existeixElement("//*[@id='registre']/tbody/tr/td[1]//a[contains(text(), 'Agrupacion_"+j+"')]")) {
			j++;
		}
		String codiAgrupacio = "Agrupacion_" + j;
		
		// Crear y borrar
		driver.findElement(By.xpath("//form[contains(@action,'/definicioProces/campAgrupacioForm.html')]/button[1]")).click();
		
		driver.findElement(By.xpath("//*[@id='codi0']")).clear();
		driver.findElement(By.xpath("//*[@id='codi0']")).sendKeys(codiAgrupacio);
		
		driver.findElement(By.xpath("//*[@id='nom0']")).clear();
		driver.findElement(By.xpath("//*[@id='nom0']")).sendKeys("Nom " + codiAgrupacio);
		
		driver.findElement(By.xpath("//*[@id='descripcio0']")).clear();
		driver.findElement(By.xpath("//*[@id='descripcio0']")).sendKeys("Descripción " + codiAgrupacio);
					
		screenshotHelper.saveScreenshot("tramitar/dadesexpedient/ordre_i_agrupacions/6"+i+".png");
		
		driver.findElement(By.xpath("//button[contains(text(), 'Crear')]")).click();
		
		existeixElementAssert("//*[@id='infos']/p", "No se creó la agrupación de la fila " + numAgrupaciones+1);
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+(numAgrupaciones+1)+"]//a[contains(@href,'/definicioProces/campAgrupacioDelete.html')]")).click();
		acceptarAlerta();
		
		screenshotHelper.saveScreenshot("tramitar/dadesexpedient/ordre_i_agrupacions/6"+i+".png");
		
		existeixElementAssert("//*[@id='infos']/p", "No se borró la agrupación de la fila " + numAgrupaciones+i);
	}
	
	@Test
	public void d_iniciar_expediente() throws InterruptedException {
		carregarUrlConfiguracio();
		
		seleccionarEntorn(titolEntorn);
					
		iniciarExpediente(codTipusExp,"SE-22/2014", "Expedient de prova Selenium " + (new Date()).getTime() );
		
		screenshotHelper.saveScreenshot("tramitar/dadesexpedient/iniciar_expediente/1.png");		
	}
	
	@Test
	public void e_afegir_nova_dada() throws InterruptedException {
		carregarUrlConfiguracio();
		
		seleccionarEntorn(titolEntorn);			
		
		consultarExpedientes(null, null, nomTipusExp);
		
		screenshotHelper.saveScreenshot("tramitar/dadesexpedient/afegir_nova_dada/2.png");
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]//a[contains(@href,'/expedient/info.html')]")).click();	
					
		// Empezamos a modificar los datos
		driver.findElement(By.xpath("//*[@id='tabnav']//a[contains(@href,'/expedient/dades.html')]")).click();
		
		driver.findElement(By.xpath("//form[contains(@action,'/expedient/dadaCrear.html')]/button[1]")).click();
		
		WebElement selectVars = driver.findElement(By.xpath("//*[@id='camp0']"));
		int numOptionsVars = selectVars.findElements(By.tagName("option")).size();
		boolean comprobarAnadido = true;
		for (int i = 2; i < numOptionsVars; i++) {			
			selectVars = driver.findElement(By.xpath("//*[@id='camp0']"));
			WebElement option = selectVars.findElements(By.tagName("option")).get(1);
			String codi = option.getAttribute("value");
			
			screenshotHelper.saveScreenshot("tramitar/dadesexpedient/afegir_nova_dada/1"+i+".png");
			String variable = option.getText();
			option.click();

			driver.findElement(By.xpath("//*[@id='varCodi0']")).clear();
			driver.findElement(By.xpath("//*[@id='varCodi0']")).sendKeys(codi);

			driver.findElement(By.xpath("//form[contains(@action,'dadaCrear.html')]//button[1]")).click();

			existeixElementAssert("//*[@id='infos']/p", "No se añadió la variable");

			// Inputs
			if (existeixElement("//*[@id='command']//input")) {
				for (WebElement input1 : driver.findElements(By.xpath("//*[@id='command']//input"))) {
					if ("text".equals(input1.getAttribute("type"))) {
						input1.clear();
						if (input1.getAttribute("class").contains("textInput hasDatepicker")) {
							input1.sendKeys("12/12/2015");
						} else {
							input1.sendKeys(codi);
						}
					} else if ("button".equals(input1.getAttribute("type"))) {
						input1.click();
	
						for (WebElement input2 : driver.findElements(By.xpath("//*[@id='command']//input"))) {
							if ("text".equals(input2.getAttribute("type"))) {
								input2.clear();
								if (input2.getAttribute("class").contains("textInput hasDatepicker")) {
									input2.sendKeys("12/12/2015");
								} else {
									input2.sendKeys(codi);
								}
							}
						}
					} 
				}
			}
			
			// Buttons
			if (existeixElement("//*[@id='command']/div[1]/div/div/button")) {
				driver.findElement(By.xpath("//*[@id='command']/div[1]/div/div/button")).click();			
				if (isAlertPresent()) {
					acceptarAlerta();
				}
				String modal = "varRegistre.html?id=";
				if (modalOberta(modal)) {
					vesAModal(modal);
					
					for (WebElement input : driver.findElements(By.xpath("//*/input[@type='text']"))) {
						input.clear();
						if (input.getAttribute("class").contains("textInput hasDatepicker")) {
							input.sendKeys("12/12/2015");
						} else {
							input.sendKeys(codi);
						}
					}
					
					driver.findElement(By.xpath("//*[@id='command']//button[1]")).click();
					
					tornaAPare();
				}
				
				comprobarAnadido = false;
			}
			
			// Textareas
			if (existeixElement("//*[@id='command']//textarea")) {
				for (WebElement textarea : driver.findElements(By.xpath("//*[@id='command']//textarea"))) {
					textarea.clear();
					textarea.sendKeys(codi);
				}
			}
			
			// Selects
			if (existeixElement("//*[@id='command']//select")) {
				for (WebElement select : driver.findElements(By.xpath("//*[@id='command']//select"))) {
					List<WebElement> sels = select.findElements(By.tagName("option"));
					sels.get(sels.size()-1).click();
				}
			}
			
			// Termini
			if (existeixElement("//*[@id='var_term_anys']")) {
				WebElement selectAnys = driver.findElement(By.xpath("//*[@id='var_term_anys']"));
				selectAnys.findElements(By.tagName("option")).get(3).click();
				WebElement selectMesos = driver.findElement(By.xpath("//*[@id='var_term_mesos']"));
				selectMesos.findElements(By.tagName("option")).get(2).click();
				WebElement input = driver.findElement(By.xpath("//*[@id='var_term_dies']"));
				input.clear();
				input.sendKeys("20");				
			}
			
			if (existeixElement("//form[contains(@action,'dadaModificar.html')]//div[@class='buttonHolder']//button[1]"))
				driver.findElement(By.xpath("//form[contains(@action,'dadaModificar.html')]//div[@class='buttonHolder']//button[1]")).click();
			
			if (comprobarAnadido)
				existeixElementAssert("//*[@id='infos']/p", "No se añadió la variable con código: " + variable + " - Posición: " + i);
			
			screenshotHelper.saveScreenshot("tramitar/dadesexpedient/afegir_nova_dada/2"+i+".png");
			
			if (i < numOptionsVars-1)
				driver.findElement(By.xpath("//form[contains(@action,'dadaCrear.html')]//button[1]")).click();
			
			comprobarAnadido = true;
		}
		
		screenshotHelper.saveScreenshot("tramitar/dadesexpedient/afegir_nova_dada/3.png");
	}
	
	@Test
	public void f_modificar_dada() throws InterruptedException {
		carregarUrlConfiguracio();
		
		seleccionarEntorn(titolEntorn);
					
		consultarExpedientes(null, null, nomTipusExp);	
		
		screenshotHelper.saveScreenshot("tramitar/dadesexpedient/modificar_dada/2.png");
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]//a[contains(@href,'/expedient/info.html')]")).click();
		
		driver.findElement(By.xpath("//*[@id='tabnav']//a[contains(@href,'/expedient/dades.html')]")).click();
		
		// Empezamos a modificar los datos
		boolean comprobarModificado = true;
		int i = 1;
		while (existeixElement("//*[@id='codi']/tbody/tr["+i+"]//a[contains(@href,'/expedient/dadaModificar.html')]")) {
			driver.findElement(By.xpath("//*[@id='codi']/tbody/tr["+i+"]//a[contains(@href,'/expedient/dadaModificar.html')]")).click();
			acceptarAlerta();			
			
			// Inputs
			if (existeixElement("//*[@id='command']//input")) {
				for (WebElement input1 : driver.findElements(By.xpath("//*[@id='command']//input"))) {
					if ("text".equals(input1.getAttribute("type"))) {
						if (input1.getAttribute("class").contains("textInput hasDatepicker")) {
							input1.sendKeys("12/12/2015");
						} else {
							input1.sendKeys("1234");
						}
					} else if ("button".equals(input1.getAttribute("type"))) {
						input1.click();
	
						for (WebElement input2 : driver.findElements(By.xpath("//*[@id='command']//input"))) {
							if ("text".equals(input2.getAttribute("type"))) {
								if (input2.getAttribute("class").contains("textInput hasDatepicker")) {
									input2.sendKeys("12/12/2015");
								} else {
									input2.sendKeys("1234");
								}
							}
						}
					} 
				}
			}
			
			// Buttons
			if (existeixElement("//*[@id='command']/div[1]/div/div/button")) {
				driver.findElement(By.xpath("//*[@id='command']/div[1]/div/div/button")).click();			
				if (isAlertPresent()) {
					acceptarAlerta();
				}
				String modal = "varRegistre.html?id=";
				if (modalOberta(modal)) {
					vesAModal(modal);
					
					for (WebElement input : driver.findElements(By.xpath("//*/input[@type='text']"))) {
						if (input.getAttribute("class").contains("textInput hasDatepicker")) {
							input.sendKeys("12/12/2015");
						} else {
							input.sendKeys("1234");
						}
					}
					
					driver.findElement(By.xpath("//*[@id='command']//button[1]")).click();
					
					tornaAPare();
				}
				
				comprobarModificado = false;
			}
			
			// Textareas
			if (existeixElement("//*[@id='command']//textarea")) {
				for (WebElement textarea : driver.findElements(By.xpath("//*[@id='command']//textarea"))) {
					textarea.sendKeys("1234");
				}
			}
			
			// Selects
			if (existeixElement("//*[@id='command']//select")) {
				for (WebElement select : driver.findElements(By.xpath("//*[@id='command']//select"))) {
					List<WebElement> sels = select.findElements(By.tagName("option"));
					sels.get(sels.size()-1).click();
				}
			}
			
			// Termini
			if (existeixElement("//*[@id='var_term_anys']")) {
				WebElement selectAnys = driver.findElement(By.xpath("//*[@id='var_term_anys']"));
				selectAnys.findElements(By.tagName("option")).get(1).click();
				WebElement selectMesos = driver.findElement(By.xpath("//*[@id='var_term_mesos']"));
				selectMesos.findElements(By.tagName("option")).get(4).click();
				WebElement input = driver.findElement(By.xpath("//*[@id='var_term_dies']"));
				input.clear();
				input.sendKeys("15");				
			}
			
			// Registre
			if (existeixElement("//*[@id='registre']/tbody/tr[1]")) {
				driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]")).click();
				
				String modal = "varRegistre.html?id=";
				if (modalOberta(modal)) {
					vesAModal(modal);
					
					for (WebElement input : driver.findElements(By.xpath("//*/input[@type='text']"))) {
						if (input.getAttribute("class").contains("textInput hasDatepicker")) {
							input.sendKeys("12/12/2015");
						} else {
							input.sendKeys("1234");
						}
					}
					
					driver.findElement(By.xpath("//*[@id='command']//button[1]")).click();
					
					tornaAPare();
				}
				
				comprobarModificado = false;	
			}
			
			if (existeixElement("//form[contains(@action,'dadaModificar.html')]//div[@class='buttonHolder']//button[1]"))
				driver.findElement(By.xpath("//form[contains(@action,'dadaModificar.html')]//div[@class='buttonHolder']//button[1]")).click();
			
			if (comprobarModificado)
				existeixElementAssert("//*[@id='infos']/p", "No se modificó la variable: " + i);
			
			screenshotHelper.saveScreenshot("tramitar/dadesexpedient/modificar_dada/3"+i+".png");
			i++;
			
			comprobarModificado = true;
		}
		
		// Modificar las agrupaciones
		int j = 1;
		while (existeixElement("//*[@id='dades-proces']/div["+j+"]/h4/img")) {			
			int k = 1;
			while (existeixElement("//*[contains(@id, 'dades-agrup-Agrupacion_"+j+"')]/table/tbody/tr["+k+"]/td[3]/a/img")) {
				driver.findElement(By.xpath("//*[contains(@id, 'dades-agrup-Agrupacion_"+j+"')]//parent::div/h4/img")).click();
				
				driver.findElement(By.xpath("//*[contains(@id, 'dades-agrup-Agrupacion_"+j+"')]/table/tbody/tr["+k+"]/td[3]/a/img")).click();
				acceptarAlerta();				
				
				// Inputs
				if (existeixElement("//*[@id='command']//input")) {
					for (WebElement input1 : driver.findElements(By.xpath("//*[@id='command']//input"))) {
						if ("text".equals(input1.getAttribute("type"))) {
							if (input1.getAttribute("class").contains("textInput hasDatepicker")) {
								input1.sendKeys("12/12/2015");
							} else {
								input1.sendKeys("1234");
							}
						} else if ("button".equals(input1.getAttribute("type"))) {
							input1.click();
		
							for (WebElement input2 : driver.findElements(By.xpath("//*[@id='command']//input"))) {
								if ("text".equals(input2.getAttribute("type"))) {
									if (input2.getAttribute("class").contains("textInput hasDatepicker")) {
										input2.sendKeys("12/12/2015");
									} else {
										input2.sendKeys("1234");
									}
								}
							}
						} 
					}
				}
				
				// Buttons
				if (existeixElement("//*[@id='command']/div[1]/div/div/button")) {
					driver.findElement(By.xpath("//*[@id='command']/div[1]/div/div/button")).click();
					if (isAlertPresent()) {
						acceptarAlerta();
					}
					String modal = "varRegistre.html?id=";
					if (modalOberta(modal)) {
						vesAModal(modal);
						
						for (WebElement input : driver.findElements(By.xpath("//*/input[@type='text']"))) {
							if (input.getAttribute("class").contains("textInput hasDatepicker")) {
								input.sendKeys("12/12/2015");
							} else {
								input.sendKeys("1234");
							}
						}
						
						driver.findElement(By.xpath("//*[@id='command']//button[1]")).click();
						
						tornaAPare();
					}
					
					comprobarModificado = false;
				}
				
				// Registre
				if (existeixElement("//*[@id='registre']/tbody/tr[1]")) {
					driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]")).click();
					
					String modal = "varRegistre.html?id=";
					if (modalOberta(modal)) {
						vesAModal(modal);
						
						for (WebElement input : driver.findElements(By.xpath("//*/input[@type='text']"))) {
							if (input.getAttribute("class").contains("textInput hasDatepicker")) {
								input.sendKeys("12/12/2015");
							} else {
								input.sendKeys("1234");
							}
						}
						
						driver.findElement(By.xpath("//*[@id='command']//button[1]")).click();
						
						tornaAPare();
					}
					
					comprobarModificado = false;
				}
				
				// Textareas
				if (existeixElement("//*[@id='command']//textarea")) {
					for (WebElement textarea : driver.findElements(By.xpath("//*[@id='command']//textarea"))) {
						textarea.sendKeys("1234");
					}
				}
				
				// Selects
				if (existeixElement("//*[@id='command']//select")) {
					for (WebElement select : driver.findElements(By.xpath("//*[@id='command']//select"))) {
						List<WebElement> sels = select.findElements(By.tagName("option"));
						sels.get(sels.size()-1).click();
					}
				}
				
				// Termini
				if (existeixElement("//*[@id='var_term_anys']")) {
					WebElement selectAnys = driver.findElement(By.xpath("//*[@id='var_term_anys']"));
					selectAnys.findElements(By.tagName("option")).get(1).click();
					WebElement selectMesos = driver.findElement(By.xpath("//*[@id='var_term_mesos']"));
					selectMesos.findElements(By.tagName("option")).get(4).click();
					WebElement input = driver.findElement(By.xpath("//*[@id='var_term_dies']"));
					input.clear();
					input.sendKeys("15");				
				}
				
				if (existeixElement("//form[contains(@action,'dadaModificar.html')]//div[@class='buttonHolder']//button[1]"))
					driver.findElement(By.xpath("//form[contains(@action,'dadaModificar.html')]//div[@class='buttonHolder']//button[1]")).click();
				
				if (comprobarModificado)
					existeixElementAssert("//*[@id='infos']/p", "No se modificó la variable agrupada: " + i + "-" + j);
				
				screenshotHelper.saveScreenshot("tramitar/dadesexpedient/modificar_dada/4"+i+"-"+j+"-"+k+".png");
				
				k++;
				
				comprobarModificado = true;
			}
			screenshotHelper.saveScreenshot("tramitar/dadesexpedient/modificar_dada/4"+i+"-"+j+".png");
			j++;
		}
		
		screenshotHelper.saveScreenshot("tramitar/dadesexpedient/modificar_dada/5.png");
	}
	
	@Test
	public void g_eliminar_dada() throws InterruptedException {
		carregarUrlConfiguracio();
		
		seleccionarEntorn(titolEntorn);
					
		consultarExpedientes(null, null, nomTipusExp);
		
		screenshotHelper.saveScreenshot("tramitar/dadesexpedient/eliminar_dada/2.png");
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]//a[contains(@href,'/expedient/info.html')]")).click();
		
		driver.findElement(By.xpath("//*[@id='tabnav']//a[contains(@href,'/expedient/dades.html')]")).click();
		
		int i = 1;
		
		// Empezamos a eliminar los datos		
		while (existeixElement("//*[@id='codi']/tbody/tr//a[contains(@href,'/expedient/dadaProcesEsborrar.html')]")) {
			driver.findElement(By.xpath("//*[@id='codi']/tbody/tr[1]//a[contains(@href,'/expedient/dadaProcesEsborrar.html')]")).click();
			acceptarAlerta();
			
			existeixElementAssert("//*[@id='infos']/p", "No se borró la variable: " + i);
			screenshotHelper.saveScreenshot("tramitar/dadesexpedient/eliminar_dada/3"+i+".png");
			i++;
		}
		
		// Eliminar las agrupaciones
		while (existeixElement("//*[@id='dades-proces']//img[contains(@src,'/img/magnifier_zoom_in.png')]")) {
			driver.findElement(By.xpath("//*[@id='dades-proces']//img[contains(@src,'/img/magnifier_zoom_in.png')]")).click();
			
			driver.findElement(By.xpath("//*[@id='campAgrup']/tbody/tr[1]//a[contains(@href,'/expedient/dadaProcesEsborrar.html')]")).click();
			acceptarAlerta();
			
			existeixElementAssert("//*[@id='infos']/p", "No se borró la variable agrupada: " + i);
			screenshotHelper.saveScreenshot("tramitar/dadesexpedient/eliminar_dada/3"+i+".png");
			i++;
		}
		
		screenshotHelper.saveScreenshot("tramitar/dadesexpedient/eliminar_dada/4.png");
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
