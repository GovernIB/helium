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
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DadesExpedient extends BaseTest {

	String entorn = carregarPropietat("entorn.nom", "Nom de l'entorn de proves no configurat al fitxer de properties");
	String nomDefProc = carregarPropietat("defproc.deploy.definicio.proces.nom", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String nomSubDefProc = carregarPropietat("defproc.deploy.definicio.subproces.nom", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String pathDefProc = carregarPropietat("defproc.deploy.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String pathSubDefProc = carregarPropietat("defproc.subproces.deploy.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String exportDefProc = carregarPropietat("defproc.tasca_dades.exp.export.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String nomTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.nom", "Nom del tipus d'expedient de proves no configurat al fitxer de properties");
	String codTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.codi", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	
	static String entornActual;
	
	@Test
	public void a_crear_dades() throws InterruptedException {
		carregarUrlConfiguracio();
		
		// Selecció directe
		actions.moveToElement(driver.findElement(By.id("menuEntorn")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//li[@id='menuEntorn']/ul[@class='llista-entorns']/li[contains(., '" + entorn + "')]/a")));
		actions.click();
		actions.build().perform();

		desplegarDefinicioProcesEntorn(nomTipusExp, nomDefProc, pathSubDefProc);
		desplegarDefinicioProcesEntorn(nomTipusExp, nomDefProc, pathDefProc);
		
		importarDadesDefPro(nomDefProc, exportDefProc);
		importarDadesDefPro(nomSubDefProc, exportDefProc);
		
		screenshotHelper.saveScreenshot("tramitar/dadesexpedient/crear_dades/1.png");
	}

	@Test
	public void b_visualizacio_dades_process() throws InterruptedException {
		// Básicos, múltiples y registros
		carregarUrlConfiguracio();
		
		// Selecció directe
		actions.moveToElement(driver.findElement(By.id("menuEntorn")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//li[@id='menuEntorn']/ul[@class='llista-entorns']/li[contains(., '" + entorn + "')]/a")));
		actions.click();
		actions.build().perform();
		
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/llistat.html')]")));
		actions.click();
		actions.build().perform();
		
		screenshotHelper.saveScreenshot("tramitar/dadesexpedient/visualizacio_dades_process/1.png");
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'"+nomDefProc+"')]")).click();
				
		driver.findElement(By.xpath("//*[@id='tabnav']/li[3]/a")).click();
		
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
	public void c_visualizacio_dades_subprocess() throws InterruptedException {
		// Básicos, múltiples y registros
		carregarUrlConfiguracio();
		
		// Selecció directe
		actions.moveToElement(driver.findElement(By.id("menuEntorn")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//li[@id='menuEntorn']/ul[@class='llista-entorns']/li[contains(., '" + entorn + "')]/a")));
		actions.click();
		actions.build().perform();
		
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/llistat.html')]")));
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
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/llistat.html')]")));
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
		
		driver.findElement(By.xpath("//*[@id='tabnav']/li[3]/a")).click();
		
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
		// Básicos, múltiples y registros
		carregarUrlConfiguracio();
		
		// Selecció directe
		actions.moveToElement(driver.findElement(By.id("menuEntorn")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//li[@id='menuEntorn']/ul[@class='llista-entorns']/li[contains(., '" + entorn + "')]/a")));
		actions.click();
		actions.build().perform();
		
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/llistat.html')]")));
		actions.click();
		actions.build().perform();
		
		screenshotHelper.saveScreenshot("tramitar/dadesexpedient/ordre_i_agrupacions/1.png");
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'"+nomDefProc+"')]")).click();
				
		driver.findElement(By.xpath("//*[@id='tabnav']/li[6]/a")).click();
		
		int i = 1;
		int numAgrupaciones = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr")).size();
		while(i <= numAgrupaciones) {
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+i+"]")).click();
			
			driver.findElement(By.xpath("//*[@id='codi0']")).clear();
			driver.findElement(By.xpath("//*[@id='codi0']")).sendKeys("Agrupacion_"+i);
			
			driver.findElement(By.xpath("//*[@id='nom0']")).clear();
			driver.findElement(By.xpath("//*[@id='nom0']")).sendKeys("Agrupación "+i);
			
			driver.findElement(By.xpath("//*[@id='descripcio0']")).clear();
			driver.findElement(By.xpath("//*[@id='descripcio0']")).sendKeys("Descripción de agrupación "+i);
						
			screenshotHelper.saveScreenshot("tramitar/dadesexpedient/ordre_i_agrupacions/2"+i+".png");
			
			driver.findElement(By.xpath("//*[@id='command']/div[3]/button[1]")).click();
			
			existeixElementAssert("//*[@id='infos']/p", "No se modificó la agrupación de la fila " + i);

			WebElement button = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+i+"]/td[3]/form/button"));
			String text = button.getText();
			int elementos = Integer.valueOf(text.substring(text.indexOf("(")+1, text.indexOf(")")));
			button.click();
			
			int elementosTabla = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr")).size();
			
			screenshotHelper.saveScreenshot("tramitar/dadesexpedient/ordre_i_agrupacions/3"+i+".png");
			
			assertTrue("Error en el número de elementos de la variable Agrupacion_"+i, elementos == elementosTabla);
			
			// Cambiar de orden las variables
			int j = 1;
			while (j <= elementosTabla) {
				WebElement el1 = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+j+"]/td[1]"));
				
				WebElement el2;				
				if (j < elementosTabla) {
					el2 = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+(j+1)+"]/td[1]"));	
				} else {
					el2 = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+(j-1)+"]/td[1]"));
				}
				
				Actions builder = new Actions(driver);

				Action dragAndDrop = builder.clickAndHold(el1)
				   .moveToElement(el2)
				   .release(el2)
				   .build();

				dragAndDrop.perform();
				
				screenshotHelper.saveScreenshot("tramitar/dadesexpedient/ordre_i_agrupacions/4"+i+"-"+j+".png");
				
				j++;
			}
			
			// Crear y borrar
			WebElement selectTipusExpedient = driver.findElement(By.xpath("//*[@id='id0']"));
			selectTipusExpedient.findElements(By.tagName("option")).get(1).click();
			
			driver.findElement(By.xpath("//*[@id='command']/fieldset/div[3]/button[1]")).click();
			existeixElementAssert("//*[@id='infos']/p", "No se añadió la agrupación de la fila " + i);
			
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+(elementosTabla+1)+"]/td[2]/a/img")).click();
			acceptarAlerta();
			existeixElementAssert("//*[@id='infos']/p", "No se borró la agrupación de la fila " + i);
			
			screenshotHelper.saveScreenshot("tramitar/dadesexpedient/ordre_i_agrupacions/5"+i+".png");
			
			driver.findElement(By.xpath("//*[@id='command']/fieldset/div[3]/button[2]")).click();
			
			i++;
		}
		
		// Crear y borrar
		driver.findElement(By.xpath("//*[@id='content']/form/button")).click();
		
		driver.findElement(By.xpath("//*[@id='codi0']")).clear();
		driver.findElement(By.xpath("//*[@id='codi0']")).sendKeys("Agrupacion_"+numAgrupaciones+1);
		
		driver.findElement(By.xpath("//*[@id='nom0']")).clear();
		driver.findElement(By.xpath("//*[@id='nom0']")).sendKeys("Agrupación "+numAgrupaciones+1);
		
		driver.findElement(By.xpath("//*[@id='descripcio0']")).clear();
		driver.findElement(By.xpath("//*[@id='descripcio0']")).sendKeys("Descripción de agrupación "+numAgrupaciones+1);
					
		screenshotHelper.saveScreenshot("tramitar/dadesexpedient/ordre_i_agrupacions/6"+i+".png");
		
		driver.findElement(By.xpath("//*[@id='command']/div[3]/button[1]")).click();
		
		existeixElementAssert("//*[@id='infos']/p", "No se creó la agrupación de la fila " + numAgrupaciones+1);
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+(numAgrupaciones+1)+"]/td[4]/a/img")).click();
		acceptarAlerta();
		
		screenshotHelper.saveScreenshot("tramitar/dadesexpedient/ordre_i_agrupacions/6"+i+".png");
		
		existeixElementAssert("//*[@id='infos']/p", "No se borró la agrupación de la fila " + numAgrupaciones+i);
	}
	
	@Test
	public void d_iniciar_expediente() throws InterruptedException {
		// Básicos, múltiples y registros
		carregarUrlConfiguracio();
		
		// Selecció directe
		actions.moveToElement(driver.findElement(By.id("menuEntorn")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//li[@id='menuEntorn']/ul[@class='llista-entorns']/li[contains(., '" + entorn + "')]/a")));
		actions.click();
		actions.build().perform();
					
		iniciarExpediente(nomDefProc,codTipusExp,"SE-22/2014", "Expedient de prova Selenium " + (new Date()).getTime() );
		
		screenshotHelper.saveScreenshot("tramitar/dadesexpedient/iniciar_expediente/1.png");		
	}
	
	@Test
	public void e_afegir_nova_dada() throws InterruptedException {
		// Básicos, múltiples y registros
		carregarUrlConfiguracio();
		
		// Selecció directe
		actions.moveToElement(driver.findElement(By.id("menuEntorn")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//li[@id='menuEntorn']/ul[@class='llista-entorns']/li[contains(., '" + entorn + "')]/a")));
		actions.click();
		actions.build().perform();					
		
		actions.moveToElement(driver.findElement(By.id("menuConsultes")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//*[@id='menuConsultes']/ul/li[1]/a")));
		actions.click();
		actions.build().perform();
		
		WebElement selectTipusExpedient = driver.findElement(By.xpath("//*[@id='expedientTipus0']"));
		List<WebElement> options = selectTipusExpedient.findElements(By.tagName("option"));
		for (WebElement option : options) {
			if (option.getText().equals(properties.getProperty("defproc.deploy.tipus.expedient.nom"))) {
				option.click();
				break;
			}
		}
		
		screenshotHelper.saveScreenshot("tramitar/dadesexpedient/afegir_nova_dada/1.png");
		
		driver.findElement(By.xpath("//*[@id='command']/div[2]/div[6]/button[1]")).click();	
		
		screenshotHelper.saveScreenshot("tramitar/dadesexpedient/afegir_nova_dada/2.png");
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]/td[6]/a/img")).click();	
					
		// Empezamos a modificar los datos
		driver.findElement(By.xpath("//*[@id='tabnav']/li[2]/a")).click();
		
		driver.findElement(By.xpath("//*[@id='content']/form/button")).click();
		
		WebElement selectVars = driver.findElement(By.xpath("//*[@id='camp0']"));
		int numOptionsVars = selectVars.findElements(By.tagName("option")).size();
		boolean comprobarAnadido = true;
		for (int i = 1; i < numOptionsVars; i++) {
			
			selectVars = driver.findElement(By.xpath("//*[@id='camp0']"));
			WebElement option = selectVars.findElements(By.tagName("option")).get(1);
			String codi = option.getAttribute("value");
			
			screenshotHelper.saveScreenshot("tramitar/dadesexpedient/afegir_nova_dada/1"+i+".png");
			option.click();

			driver.findElement(By.xpath("//*[@id='varCodi0']")).clear();
			driver.findElement(By.xpath("//*[@id='varCodi0']")).sendKeys(codi);

			driver.findElement(By.xpath("//*[@id='command']/div[3]/button[1]")).click();

			existeixElementAssert("//*[@id='infos']/p", "No se añadió la variable");

			// Inputs
			if (existeixElement("//*[@id='command']/div/*/input")) {
				for (WebElement input1 : driver.findElements(By.xpath("//*[@id='command']/div/*/input"))) {
					if ("text".equals(input1.getAttribute("type"))) {
						input1.clear();
						if (input1.getAttribute("class").contains("textInput hasDatepicker")) {
							input1.sendKeys("12/12/2015");
						} else {
							input1.sendKeys(codi);
						}
					} else if ("button".equals(input1.getAttribute("type"))) {
						input1.click();
	
						for (WebElement input2 : driver.findElements(By.xpath("//*[@id='command']/div/*/input"))) {
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
					
					driver.findElement(By.xpath("//*[@id='command']/div[3]/button[1]")).click();
					
					tornaAPare();
				}
				
				comprobarAnadido = false;
			}
			
			// Textareas
			if (existeixElement("//*[@id='command']/div/*/textarea")) {
				for (WebElement textarea : driver.findElements(By.xpath("//*[@id='command']/div/*/textarea"))) {
					textarea.clear();
					textarea.sendKeys(codi);
				}
			}
			
			// Selects
			if (existeixElement("//*[@id='command']/div/*/select")) {
				for (WebElement select : driver.findElements(By.xpath("//*[@id='command']/div/*/select"))) {
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
			
			driver.findElement(By.xpath("//*[@id='command']/div[3]/button[1]")).click();
			
			if (comprobarAnadido)
				existeixElementAssert("//*[@id='infos']/p", "No se añadió la variable");
			
			screenshotHelper.saveScreenshot("tramitar/dadesexpedient/afegir_nova_dada/2"+i+".png");
			
			if (i < numOptionsVars-1)
				driver.findElement(By.xpath("//*[@id='content']/form/button")).click();
			
			comprobarAnadido = true;
		}
		
		screenshotHelper.saveScreenshot("tramitar/dadesexpedient/afegir_nova_dada/3.png");
	}
	
	@Test
	public void f_modificar_dada() throws InterruptedException {
		carregarUrlConfiguracio();
		
		// Selecció directe
		actions.moveToElement(driver.findElement(By.id("menuEntorn")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//li[@id='menuEntorn']/ul[@class='llista-entorns']/li[contains(., '" + entorn + "')]/a")));
		actions.click();
		actions.build().perform();
					
		actions.moveToElement(driver.findElement(By.id("menuConsultes")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//*[@id='menuConsultes']/ul/li[1]/a")));
		actions.click();
		actions.build().perform();
		
		WebElement selectTipusExpedient = driver.findElement(By.xpath("//*[@id='expedientTipus0']"));
		List<WebElement> options = selectTipusExpedient.findElements(By.tagName("option"));
		for (WebElement option : options) {
			if (option.getText().equals(properties.getProperty("defproc.deploy.tipus.expedient.nom"))) {
				option.click();
				break;
			}
		}
		
		screenshotHelper.saveScreenshot("tramitar/dadesexpedient/modificar_dada/1.png");
		
		driver.findElement(By.xpath("//*[@id='command']/div[2]/div[6]/button[1]")).click();	
		
		screenshotHelper.saveScreenshot("tramitar/dadesexpedient/modificar_dada/2.png");
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]/td[6]/a/img")).click();
		
		driver.findElement(By.xpath("//*[@id='tabnav']/li[2]/a")).click();
		
		// Empezamos a modificar los datos
		boolean comprobarModificado = true;
		int i = 1;
		while (existeixElement("//*[@id='codi']/tbody/tr["+i+"]/td[4]/a/img")) {
			driver.findElement(By.xpath("//*[@id='codi']/tbody/tr["+i+"]/td[3]/a/img")).click();
			acceptarAlerta();			
			
			// Inputs
			if (existeixElement("//*[@id='command']/div/*/input")) {
				for (WebElement input1 : driver.findElements(By.xpath("//*[@id='command']/div/*/input"))) {
					if ("text".equals(input1.getAttribute("type"))) {
						if (input1.getAttribute("class").contains("textInput hasDatepicker")) {
							input1.sendKeys("12/12/2015");
						} else {
							input1.sendKeys("1234");
						}
					} else if ("button".equals(input1.getAttribute("type"))) {
						input1.click();
	
						for (WebElement input2 : driver.findElements(By.xpath("//*[@id='command']/div/*/input"))) {
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
					
					driver.findElement(By.xpath("//*[@id='command']/div[3]/button[1]")).click();
					
					tornaAPare();
				}
				
				comprobarModificado = false;
			}
			
			// Textareas
			if (existeixElement("//*[@id='command']/div/*/textarea")) {
				for (WebElement textarea : driver.findElements(By.xpath("//*[@id='command']/div/*/textarea"))) {
					textarea.sendKeys("1234");
				}
			}
			
			// Selects
			if (existeixElement("//*[@id='command']/div/*/select")) {
				for (WebElement select : driver.findElements(By.xpath("//*[@id='command']/div/*/select"))) {
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
			
			driver.findElement(By.xpath("//*[@id='command']/div[3]/button[1]")).click();			
			
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
				if (existeixElement("//*[@id='command']/div/*/input")) {
					for (WebElement input1 : driver.findElements(By.xpath("//*[@id='command']/div/*/input"))) {
						if ("text".equals(input1.getAttribute("type"))) {
							if (input1.getAttribute("class").contains("textInput hasDatepicker")) {
								input1.sendKeys("12/12/2015");
							} else {
								input1.sendKeys("1234");
							}
						} else if ("button".equals(input1.getAttribute("type"))) {
							input1.click();
		
							for (WebElement input2 : driver.findElements(By.xpath("//*[@id='command']/div/*/input"))) {
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
						
						driver.findElement(By.xpath("//*[@id='command']/div[3]/button[1]")).click();
						
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
						
						driver.findElement(By.xpath("//*[@id='command']/div[3]/button[1]")).click();
						
						tornaAPare();
					}
					
					comprobarModificado = false;
				}
				
				// Textareas
				if (existeixElement("//*[@id='command']/div/*/textarea")) {
					for (WebElement textarea : driver.findElements(By.xpath("//*[@id='command']/div/*/textarea"))) {
						textarea.sendKeys("1234");
					}
				}
				
				// Selects
				if (existeixElement("//*[@id='command']/div/*/select")) {
					for (WebElement select : driver.findElements(By.xpath("//*[@id='command']/div/*/select"))) {
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
				
				driver.findElement(By.xpath("//*[@id='command']/div[3]/button[1]")).click();
				
				if (comprobarModificado)
					existeixElementAssert("//*[@id='infos']/p", "No se modificó la variable agrupada: " + i);
				
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
		// Básicos, múltiples y registros
		carregarUrlConfiguracio();
		
		// Selecció directe
		actions.moveToElement(driver.findElement(By.id("menuEntorn")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//li[@id='menuEntorn']/ul[@class='llista-entorns']/li[contains(., '" + entorn + "')]/a")));
		actions.click();
		actions.build().perform();
					
		actions.moveToElement(driver.findElement(By.id("menuConsultes")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//*[@id='menuConsultes']/ul/li[1]/a")));
		actions.click();
		actions.build().perform();
		
		WebElement selectTipusExpedient = driver.findElement(By.xpath("//*[@id='expedientTipus0']"));
		List<WebElement> options = selectTipusExpedient.findElements(By.tagName("option"));
		for (WebElement option : options) {
			if (option.getText().equals(properties.getProperty("defproc.deploy.tipus.expedient.nom"))) {
				option.click();
				break;
			}
		}
		
		screenshotHelper.saveScreenshot("tramitar/dadesexpedient/eliminar_dada/1.png");
		
		driver.findElement(By.xpath("//*[@id='command']/div[2]/div[6]/button[1]")).click();	
		
		screenshotHelper.saveScreenshot("tramitar/dadesexpedient/eliminar_dada/2.png");
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]/td[6]/a/img")).click();
		
		driver.findElement(By.xpath("//*[@id='tabnav']/li[2]/a")).click();
		
		int i = 1;
		
		// Empezamos a eliminar los datos		
		while (existeixElement("//*[@id='codi']/tbody/tr/td[4]/a/img")) {
			driver.findElement(By.xpath("//*[@id='codi']/tbody/tr[1]/td[4]/a/img")).click();
			acceptarAlerta();
			
			existeixElementAssert("//*[@id='infos']/p", "No se borró la variable: " + i);
			screenshotHelper.saveScreenshot("tramitar/dadesexpedient/eliminar_dada/3"+i+".png");
			i++;
		}
		
		// Eliminar las agrupaciones
		while (existeixElement("//*[@id='dades-proces']/div/h4/img")) {
			driver.findElement(By.xpath("//*[@id='dades-proces']/div[1]/h4/img")).click();
			
			driver.findElement(By.xpath("//*[@id='campAgrup']/tbody/tr[1]/td[4]/a/img")).click();
			acceptarAlerta();
			
			existeixElementAssert("//*[@id='infos']/p", "No se borró la variable agrupada: " + i);
			screenshotHelper.saveScreenshot("tramitar/dadesexpedient/eliminar_dada/3"+i+".png");
			i++;
		}
		
		screenshotHelper.saveScreenshot("tramitar/dadesexpedient/eliminar_dada/4.png");
	}
	
	@Test
	public void y_eliminar_ultimo_expediente() throws InterruptedException {
		carregarUrlConfiguracio();
		
		// Selecció directe
		actions.moveToElement(driver.findElement(By.id("menuEntorn")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//li[@id='menuEntorn']/ul[@class='llista-entorns']/li[contains(., '" + entorn + "')]/a")));
		actions.click();
		actions.build().perform();	
		
		eliminarExpedient(null, null);
		
		screenshotHelper.saveScreenshot("tramitar/dadesexpedient/eliminar_ultimo_expediente/1.png");		
	}
	
	@Test
	public void z_borrar_dades() throws InterruptedException {
		carregarUrlConfiguracio();
		
		// Selecció directe
		actions.moveToElement(driver.findElement(By.id("menuEntorn")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//li[@id='menuEntorn']/ul[@class='llista-entorns']/li[contains(., '" + entorn + "')]/a")));
		actions.click();
		actions.build().perform();
		
		eliminarDefinicioProces(nomSubDefProc);
		eliminarDefinicioProces(nomDefProc);

		screenshotHelper.saveScreenshot("tramitar/dadesexpedient/borrar_dades/2.png");
	}
}
