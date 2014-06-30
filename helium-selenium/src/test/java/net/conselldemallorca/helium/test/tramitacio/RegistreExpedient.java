package net.conselldemallorca.helium.test.tramitacio;

import static org.junit.Assert.assertTrue;

import java.util.Date;

import net.conselldemallorca.helium.test.util.BaseTest;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RegistreExpedient extends BaseTest {

	String entorn = carregarPropietat("entorn.nom", "Nom de l'entorn de proves no configurat al fitxer de properties");
	String codTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.codi", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	String nomDefProc = carregarPropietat("defproc.deploy.definicio.proces.nom", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String pathDefProc = carregarPropietat("defproc.mod.exp.deploy.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String nomTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.nom", "Nom del tipus d'expedient de proves no configurat al fitxer de properties");
	String usuari = carregarPropietat("test.base.usuari.configuracio", "Usuari configuració de l'entorn de proves no configurat al fitxer de properties");
	String tipusExp = carregarPropietat("defproc.deploy.tipus.expedient.nom", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	
	@Test
	public void a_iniciar_expedient() throws InterruptedException {
		carregarUrlConfiguracio(); 
		
		seleccionarEntorno(entorn);

		screenshotHelper.saveScreenshot("RegistreExpedient/iniciar_expedient/1.png");

		crearTipusExpedientTest(nomTipusExp, codTipusExp);
		assignarPermisosTipusExpedient(codTipusExp, usuari, "DESIGN","CREATE","SUPERVISION","WRITE","MANAGE","DELETE","READ","ADMINISTRATION");
		desplegarDefinicioProcesEntorn(nomTipusExp, nomDefProc, pathDefProc);
		importarDadesDefPro(nomDefProc, properties.getProperty("defproc.termini.exp.export.arxiu.path"));

		screenshotHelper.saveScreenshot("RegistreExpedient/iniciar_expedient/2.png");
		
		iniciarExpediente(nomDefProc, codTipusExp, "SE-22/2014", "Expedient de prova Selenium " + (new Date()).getTime());

		screenshotHelper.saveScreenshot("RegistreExpedient/iniciar_expedient/3.png");
	}

	@Test
	public void b_visualizar_tasques() throws InterruptedException {
		carregarUrlConfiguracio();

		seleccionarEntorno(entorn);

		screenshotHelper.saveScreenshot("RegistreExpedient/visualizar_tasques/1.png");

		consultarExpedientes(null, null, properties.getProperty("defproc.deploy.tipus.expedient.nom"));

		screenshotHelper.saveScreenshot("RegistreExpedient/visualizar_tasques/2.png");

		assertTrue("No había ningún expediente", !driver.findElements(By.xpath("//*[@id='registre']/tbody/tr")).isEmpty());

		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]/td[6]/a/img")).click();

		driver.findElement(By.xpath("//*[@id='tabnav']/li[8]/a")).click();

		screenshotHelper.saveScreenshot("RegistreExpedient/visualizar_tasques/3.png");

		if (!existeixElement("//*[@id='content']/form/div/button[contains(text(), 'Veure registre detallat')]")) {
			driver.findElement(By.xpath("//*[@id='content']/form/div/button")).click();
		}

		int i = 1;
		while (existeixElement("//*[@id='registre']/tbody/tr[" + i + "]")) {
			comprobarDatosRegistro(i, false);

			comprobarDatosRegistroModal("//*[@id='registre']/tbody/tr[" + i + "]/td[4]/span/a");
			comprobarDatosRegistroModal("//*[@id='registre']/tbody/tr[" + i + "]/td[5]/a");
			
			i++;
		}
		
		screenshotHelper.saveScreenshot("RegistreExpedient/visualizar_tasques/4.png");
	}

	@Test
	public void c_visualizar_tasques_detall() throws InterruptedException {
		carregarUrlConfiguracio();

		seleccionarEntorno(entorn);

		screenshotHelper.saveScreenshot("RegistreExpedient/visualizar_tasques_detall/1.png");

		consultarExpedientes(null, null, properties.getProperty("defproc.deploy.tipus.expedient.nom"));

		screenshotHelper.saveScreenshot("RegistreExpedient/visualizar_tasques_detall/2.png");

		assertTrue("No había ningún expediente", !driver.findElements(By.xpath("//*[@id='registre']/tbody/tr")).isEmpty());

		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]/td[6]/a/img")).click();

		driver.findElement(By.xpath("//*[@id='tabnav']/li[8]/a")).click();

		screenshotHelper.saveScreenshot("RegistreExpedient/visualizar_tasques_detall/3.png");

		if (existeixElement("//*[@id='content']/form/div/button[contains(text(), 'Veure registre detallat')]")) {
			driver.findElement(By.xpath("//*[@id='content']/form/div/button")).click();
		}

		int i = 1;
		while (existeixElement("//*[@id='registre']/tbody/tr[" + i + "]")) {
			comprobarDatosRegistro(i, false);

			comprobarDatosRegistroModal("//*[@id='registre']/tbody/tr[" + i + "]/td[4]/span/a");
			comprobarDatosRegistroModal("//*[@id='registre']/tbody/tr[" + i + "]/td[5]/a");

			i++;
		}
		
		screenshotHelper.saveScreenshot("RegistreExpedient/visualizar_tasques_detall/4.png");
	}

	@Test
	public void e_visualizar_accions_tasca() throws InterruptedException {
		carregarUrlConfiguracio();

		seleccionarEntorno(entorn);

		screenshotHelper.saveScreenshot("RegistreExpedient/visualizar_accions_tasca/1.png");

		consultarExpedientes(null, null, properties.getProperty("defproc.deploy.tipus.expedient.nom"));

		screenshotHelper.saveScreenshot("RegistreExpedient/visualizar_accions_tasca/2.png");

		assertTrue("No había ningún expediente", !driver.findElements(By.xpath("//*[@id='registre']/tbody/tr")).isEmpty());

		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]/td[6]/a/img")).click();

		driver.findElement(By.xpath("//*[@id='tabnav']/li[8]/a")).click();

		screenshotHelper.saveScreenshot("RegistreExpedient/visualizar_accions_tasca/3.png");

		if (!existeixElement("//*[@id='content']/form/div/button[contains(text(), 'Veure registre detallat')]")) {
			driver.findElement(By.xpath("//*[@id='content']/form/div/button")).click();
		}
		
		// Comprobamos desde el registro no detallado
		int i = 1;
		while (existeixElement("//*[@id='registre']/tbody/tr[" + i + "]")) {
			// Buscamos la fila
			String pathUrl = "//*[@id='registre']/tbody/tr[" + i + "]/td[4]/span/a";
			
			// Pulsamos sobre su acción
			if (existeixElement(pathUrl)) {
				WebElement url = driver.findElement(By.xpath(pathUrl));
				url.click();
				if (modalObertaAssert(url.getAttribute("href"))) {
					vesAModal(url.getAttribute("href"));
					
					"Guardar dades de la tasca".equals(comprobarDatosRegistro(1, false));
					"Validar dades de la tasca".equals(comprobarDatosRegistro(2, false));
					"Completar tramitació de la tasca".equals(comprobarDatosRegistro(3, false));
					
					tornaAPare();

					if (existeixElement("html/body/div[8]/div[1]/a/span")) {
						driver.findElement(By.xpath("html/body/div[8]/div[1]/a/span")).click();
					} else {
						driver.findElement(By.xpath("//*[@id='content']/h3")).click();
					}
				}
			}
			i++;
		}

		if (existeixElement("//*[@id='content']/form/div/button[contains(text(), 'Veure registre detallat')]")) {
			driver.findElement(By.xpath("//*[@id='content']/form/div/button")).click();
		}
		
		// Comprobamos desde el registro detallado
		i = 1;
		while (existeixElement("//*[@id='registre']/tbody/tr[" + i + "]")) {
			// Buscamos la fila
			String pathUrl = "//*[@id='registre']/tbody/tr[" + i + "]/td[4]/span/a";
			
			// Pulsamos sobre su acción
			if (existeixElement(pathUrl)) {
				WebElement url = driver.findElement(By.xpath(pathUrl));
				url.click();
				if (modalObertaAssert(url.getAttribute("href"))) {
					vesAModal(url.getAttribute("href"));
					
					"Guardar dades de la tasca".equals(comprobarDatosRegistro(1, false));
					"Validar dades de la tasca".equals(comprobarDatosRegistro(2, false));
					"Completar tramitació de la tasca".equals(comprobarDatosRegistro(3, false));
					
					tornaAPare();

					if (existeixElement("html/body/div[8]/div[1]/a/span")) {
						driver.findElement(By.xpath("html/body/div[8]/div[1]/a/span")).click();
					} else {
						driver.findElement(By.xpath("//*[@id='content']/h3")).click();
					}
				}
			}
			i++;
		}
		
		screenshotHelper.saveScreenshot("RegistreExpedient/visualizar_accions_tasca/4.png");
	}
		
	// @Test
	public void f_retrocedir_tasca() throws InterruptedException {

	}

	// @Test
	public void g_retrocedir_tasca_detallat() throws InterruptedException {

	}

	 @Test
	public void z_finalizar_expedient() throws InterruptedException {
		carregarUrlConfiguracio();

		seleccionarEntorno(entorn);

		eliminarExpedient(null, null, tipusExp);

		// Eliminar la def de proceso
		eliminarDefinicioProces(nomDefProc);

		screenshotHelper.saveScreenshot("RegistreExpedient/finalizar_expedient/1.png");
	}

	private String comprobarDatosRegistro(int i, boolean modal) {
		String data = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[" + i + "]/td[1]")).getText();
		assertTrue("La fecha de la fila " + i + " no seguía el formato correcto", isDate(data, "dd/MM/yyyy HH:mm:ss"));

		String responsable = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[" + i + "]/td[2]")).getText();
		assertTrue("El responsable de la fila " + i + " no estaba informado", !responsable.isEmpty());

		String element = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[" + i + "]/td[3]")).getText();

		String accio = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[" + i + "]/td[4]")).getText();
		boolean mostrarAccio = existeixElement("//*[@id='registre']/tbody/tr[" + i + "]/td[4]/span/a/img");

		String informacio = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[" + i + "]/td[5]")).getText();
		boolean mostrarInformacio = existeixElement("//*[@id='registre']/tbody/tr[" + i + "]/td[5]/a/img");

		String token = null;
		if (!modal) {
			token = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[" + i + "]/td[6]")).getText();
		}
		boolean retroceder = existeixElement("//*[@id='registre']/tbody/tr[" + i + "]/td[7]/a/img"); 
		
		// Comprobamos que todo se ajusta a lo establecido
		if ("Iniciar expedient".equals(accio)) {
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", informacio.isEmpty());
			assertTrue("El campo 'token' de la fila " + i + " no era correcto", modal ? true : !token.isEmpty());
			assertTrue("El campo 'element' de la fila " + i + " no era correcto", "Expedient".equals(element));
			assertTrue("El campo 'retroceder' de la fila " + i + " no era correcto", modal ? true : !retroceder);
			assertTrue("El campo 'accio' de la fila " + i + " no era correcto", !mostrarAccio);
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", !mostrarInformacio);
		} else if ("Crear variable al procés".equals(accio)) {
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", informacio.contains("Variable:"));
			assertTrue("El campo 'token' de la fila " + i + " no era correcto", modal ? true : !token.isEmpty());
			assertTrue("El campo 'element' de la fila " + i + " no era correcto", element.contains("Procés:"));
			assertTrue("El campo 'retroceder' de la fila " + i + " no era correcto", modal ? true : retroceder);
			assertTrue("El campo 'accio' de la fila " + i + " no era correcto", !mostrarAccio);
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", !mostrarInformacio);
		} else if ("Modificar variable del procés".equals(accio)) {
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", informacio.contains("Variable:"));
			assertTrue("El campo 'token' de la fila " + i + " no era correcto", modal ? true : !token.isEmpty());
			assertTrue("El campo 'element' de la fila " + i + " no era correcto", element.contains("Procés:"));
			assertTrue("El campo 'retroceder' de la fila " + i + " no era correcto", modal ? true : retroceder);
			assertTrue("El campo 'accio' de la fila " + i + " no era correcto", !mostrarAccio);
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", !mostrarInformacio);
		} else if ("Esborrar variable del procés".equals(accio)) {
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", informacio.contains("Variable:"));
			assertTrue("El campo 'token' de la fila " + i + " no era correcto", modal ? true : !token.isEmpty());
			assertTrue("El campo 'element' de la fila " + i + " no era correcto", element.contains("Procés:"));
			assertTrue("El campo 'retroceder' de la fila " + i + " no era correcto", modal ? true : retroceder);
			assertTrue("El campo 'accio' de la fila " + i + " no era correcto", !mostrarAccio);
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", !mostrarInformacio);
		} else if ("Afegir document al procés".equals(accio)) {
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", informacio.contains("Document:"));
			assertTrue("El campo 'token' de la fila " + i + " no era correcto", modal ? true : !token.isEmpty());
			assertTrue("El campo 'element' de la fila " + i + " no era correcto", element.contains("Procés:"));
			assertTrue("El campo 'retroceder' de la fila " + i + " no era correcto", modal ? true : retroceder);
			assertTrue("El campo 'accio' de la fila " + i + " no era correcto", !mostrarAccio);
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", !mostrarInformacio);
		} else if ("Modificar document del procés".equals(accio)) {
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", informacio.contains("Document:"));
			assertTrue("El campo 'token' de la fila " + i + " no era correcto", modal ? true : !token.isEmpty());
			assertTrue("El campo 'element' de la fila " + i + " no era correcto", element.contains("Procés:"));
			assertTrue("El campo 'retroceder' de la fila " + i + " no era correcto", modal ? true : retroceder);
			assertTrue("El campo 'accio' de la fila " + i + " no era correcto", !mostrarAccio);
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", !mostrarInformacio);
		} else if ("Esborrar document del procés".equals(accio)) {
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", informacio.contains("Document:"));
			assertTrue("El campo 'token' de la fila " + i + " no era correcto", modal ? true : !token.isEmpty());
			assertTrue("El campo 'element' de la fila " + i + " no era correcto", element.contains("Procés:"));
			assertTrue("El campo 'retroceder' de la fila " + i + " no era correcto", modal ? true : retroceder);
			assertTrue("El campo 'accio' de la fila " + i + " no era correcto", !mostrarAccio);
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", !mostrarInformacio);
		} else if ("Adjuntar document al procés".equals(accio)) {
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", informacio.contains("Document:"));
			assertTrue("El campo 'token' de la fila " + i + " no era correcto", modal ? true : !token.isEmpty());
			assertTrue("El campo 'element' de la fila " + i + " no era correcto", element.contains("Procés:"));
			assertTrue("El campo 'retroceder' de la fila " + i + " no era correcto", modal ? true : retroceder);
			assertTrue("El campo 'accio' de la fila " + i + " no era correcto", !mostrarAccio);
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", !mostrarInformacio);
		} else if ("Canviar responsable de la tasca".equals(accio)) {
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", !informacio.isEmpty());
			assertTrue("El campo 'token' de la fila " + i + " no era correcto", modal ? true : !token.isEmpty());
			assertTrue("El campo 'element' de la fila " + i + " no era correcto", element.contains("Tasca:"));
			assertTrue("El campo 'retroceder' de la fila " + i + " no era correcto", modal ? true : retroceder);
			assertTrue("El campo 'accio' de la fila " + i + " no era correcto", !mostrarAccio);
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", !mostrarInformacio);
		} else if ("Guardar dades de la tasca".equals(accio)) {
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", informacio.isEmpty());
			assertTrue("El campo 'token' de la fila " + i + " no era correcto", modal ? true : !token.isEmpty());
			assertTrue("El campo 'element' de la fila " + i + " no era correcto", element.contains("Tasca:"));
			assertTrue("El campo 'retroceder' de la fila " + i + " no era correcto", modal ? true : retroceder);
			assertTrue("El campo 'accio' de la fila " + i + " no era correcto", !mostrarAccio);
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", !mostrarInformacio);
		} else if ("Validar dades de la tasca".equals(accio)) {
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", informacio.isEmpty());
			assertTrue("El campo 'token' de la fila " + i + " no era correcto", modal ? true : !token.isEmpty());
			assertTrue("El campo 'element' de la fila " + i + " no era correcto", element.contains("Tasca:"));
			assertTrue("El campo 'retroceder' de la fila " + i + " no era correcto", modal ? true : retroceder);
			assertTrue("El campo 'accio' de la fila " + i + " no era correcto", !mostrarAccio);
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", !mostrarInformacio);
		} else if ("Restaurar dades de la tasca".equals(accio)) {
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", informacio.isEmpty());
			assertTrue("El campo 'token' de la fila " + i + " no era correcto", modal ? true : !token.isEmpty());
			assertTrue("El campo 'element' de la fila " + i + " no era correcto", element.contains("Tasca:"));
			assertTrue("El campo 'retroceder' de la fila " + i + " no era correcto", modal ? true : retroceder);
			assertTrue("El campo 'accio' de la fila " + i + " no era correcto", !mostrarAccio);
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", !mostrarInformacio);
		} else if ("Executar accio de la tasca".equals(accio)) {
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", !informacio.isEmpty());
			assertTrue("El campo 'token' de la fila " + i + " no era correcto", modal ? true : !token.isEmpty());
			assertTrue("El campo 'element' de la fila " + i + " no era correcto", element.contains("Tasca:"));
			assertTrue("El campo 'retroceder' de la fila " + i + " no era correcto", modal ? true : retroceder);
			assertTrue("El campo 'accio' de la fila " + i + " no era correcto", !mostrarAccio);
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", !mostrarInformacio);
		} else if ("Afegir document a la tasca".equals(accio)) {
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", !informacio.isEmpty());
			assertTrue("El campo 'token' de la fila " + i + " no era correcto", modal ? true : !token.isEmpty());
			assertTrue("El campo 'element' de la fila " + i + " no era correcto", element.contains("Tasca:"));
			assertTrue("El campo 'retroceder' de la fila " + i + " no era correcto", modal ? true : retroceder);
			assertTrue("El campo 'accio' de la fila " + i + " no era correcto", !mostrarAccio);
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", !mostrarInformacio);
		} else if ("Modificar document de la tasca".equals(accio)) {
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", !informacio.isEmpty());
			assertTrue("El campo 'token' de la fila " + i + " no era correcto", modal ? true : !token.isEmpty());
			assertTrue("El campo 'element' de la fila " + i + " no era correcto", element.contains("Tasca:"));
			assertTrue("El campo 'retroceder' de la fila " + i + " no era correcto", modal ? true : retroceder);
			assertTrue("El campo 'accio' de la fila " + i + " no era correcto", !mostrarAccio);
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", !mostrarInformacio);
		} else if ("Esborrar document de la tasca".equals(accio)) {
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", !informacio.isEmpty());
			assertTrue("El campo 'token' de la fila " + i + " no era correcto", modal ? true : !token.isEmpty());
			assertTrue("El campo 'element' de la fila " + i + " no era correcto", element.contains("Tasca:"));
			assertTrue("El campo 'retroceder' de la fila " + i + " no era correcto", modal ? true : retroceder);
			assertTrue("El campo 'accio' de la fila " + i + " no era correcto", !mostrarAccio);
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", !mostrarInformacio);
		} else if ("Suspendre execució de la tasca".equals(accio)) {
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", informacio.isEmpty());
			assertTrue("El campo 'token' de la fila " + i + " no era correcto", modal ? true : token.isEmpty());
			assertTrue("El campo 'element' de la fila " + i + " no era correcto", element.contains("Tasca:"));
			assertTrue("El campo 'retroceder' de la fila " + i + " no era correcto", modal ? true : !retroceder);
			assertTrue("El campo 'accio' de la fila " + i + " no era correcto", !mostrarAccio);
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", !mostrarInformacio);
		} else if ("Continuar execució de la tasca".equals(accio)) {
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", informacio.isEmpty());
			assertTrue("El campo 'token' de la fila " + i + " no era correcto", modal ? true : token.isEmpty());
			assertTrue("El campo 'element' de la fila " + i + " no era correcto", element.contains("Tasca:"));
			assertTrue("El campo 'retroceder' de la fila " + i + " no era correcto", modal ? true : !retroceder);
			assertTrue("El campo 'accio' de la fila " + i + " no era correcto", !mostrarAccio);
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", !mostrarInformacio);
		} else if ("Cancel·lar execució de la tasca".equals(accio)) {
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", informacio.isEmpty());
			assertTrue("El campo 'token' de la fila " + i + " no era correcto", modal ? true : token.isEmpty());
			assertTrue("El campo 'element' de la fila " + i + " no era correcto", element.contains("Tasca:"));
			assertTrue("El campo 'retroceder' de la fila " + i + " no era correcto", modal ? true : !retroceder);
			assertTrue("El campo 'accio' de la fila " + i + " no era correcto", !mostrarAccio);
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", !mostrarInformacio);
		} else if ("Modificar dades de l'expedient".equals(accio)) {
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", informacio.isEmpty());
			assertTrue("El campo 'token' de la fila " + i + " no era correcto", modal ? true : token.isEmpty());
			assertTrue("El campo 'element' de la fila " + i + " no era correcto", "Expedient".equals(element));
			assertTrue("El campo 'retroceder' de la fila " + i + " no era correcto", modal ? true : !retroceder);
			assertTrue("El campo 'accio' de la fila " + i + " no era correcto", !mostrarAccio);
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", !mostrarInformacio);
		} else if ("Aturar tramitació de l'expedient".equals(accio)) {
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", informacio.contains("Missatges:"));
			assertTrue("El campo 'token' de la fila " + i + " no era correcto", modal ? true : token.isEmpty());
			assertTrue("El campo 'element' de la fila " + i + " no era correcto", "Expedient".equals(element));
			assertTrue("El campo 'retroceder' de la fila " + i + " no era correcto", modal ? true : !retroceder);
			assertTrue("El campo 'accio' de la fila " + i + " no era correcto", !mostrarAccio);
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", !mostrarInformacio);
			
			// Debe existir un mensaje de "Expedient aturat: XXXX"
			existeixElementAssert("//*[@class='missatgesAturat']", "No se encontró el mensaje de error de la fila " + i);
			String mensaje = informacio.replaceAll("Missatges: ", "");
			String mensajeAturar = driver.findElement(By.xpath("//*[@class='missatgesAturat']/p")).getText();
			assertTrue("El mensaje de la fila " + i + " era incorrecto", ("Expedient aturat: " + mensaje).equals(mensajeAturar));
		} else if ("Reprendre tramitació de l'expedient".equals(accio)) {
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", informacio.isEmpty());
			assertTrue("El campo 'token' de la fila " + i + " no era correcto", modal ? true : !token.isEmpty());
			assertTrue("El campo 'element' de la fila " + i + " no era correcto", "Expedient".equals(element));
			assertTrue("El campo 'retroceder' de la fila " + i + " no era correcto", modal ? true : !retroceder);
			assertTrue("El campo 'accio' de la fila " + i + " no era correcto", !mostrarAccio);
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", !mostrarInformacio);
		} else if ("Afegir expedient relacionat".equals(accio)) {
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", informacio.isEmpty());
			assertTrue("El campo 'token' de la fila " + i + " no era correcto", modal ? true : token.isEmpty());
			assertTrue("El campo 'element' de la fila " + i + " no era correcto", "Expedient".equals(element));
			assertTrue("El campo 'retroceder' de la fila " + i + " no era correcto", modal ? true : !retroceder);
			assertTrue("El campo 'accio' de la fila " + i + " no era correcto", !mostrarAccio);
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", !mostrarInformacio);
		} else if ("Esborrar expedient relacionat".equals(accio)) {
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", informacio.isEmpty());
			assertTrue("El campo 'token' de la fila " + i + " no era correcto", modal ? true : token.isEmpty());
			assertTrue("El campo 'element' de la fila " + i + " no era correcto", "Expedient".equals(element));
			assertTrue("El campo 'retroceder' de la fila " + i + " no era correcto", modal ? true : !retroceder);
			assertTrue("El campo 'accio' de la fila " + i + " no era correcto", !mostrarAccio);
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", !mostrarInformacio);
		} else if ("Executar acció de l'expedient".equals(accio)) {
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", informacio.contains("Acció:"));
			assertTrue("El campo 'token' de la fila " + i + " no era correcto", modal ? true : !token.isEmpty());
			assertTrue("El campo 'element' de la fila " + i + " no era correcto", "Expedient".equals(element));
			assertTrue("El campo 'retroceder' de la fila " + i + " no era correcto", modal ? true : retroceder);
			assertTrue("El campo 'accio' de la fila " + i + " no era correcto", !mostrarAccio);
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", !mostrarInformacio);
		} else if ("Retrocedir accions de l'expedient".equals(accio)) {
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", informacio.isEmpty());
			assertTrue("El campo 'token' de la fila " + i + " no era correcto", modal ? true : token.isEmpty());
			assertTrue("El campo 'element' de la fila " + i + " no era correcto", "Expedient".equals(element));
			assertTrue("El campo 'retroceder' de la fila " + i + " no era correcto", modal ? true : !retroceder);
			assertTrue("El campo 'accio' de la fila " + i + " no era correcto", !mostrarAccio);
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", mostrarInformacio);
		} else if ("Retrocedir tasques de l'expedient".equals(accio)) {
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", informacio.isEmpty());
			assertTrue("El campo 'token' de la fila " + i + " no era correcto", modal ? true : token.isEmpty());
			assertTrue("El campo 'element' de la fila " + i + " no era correcto", "Expedient".equals(element));
			assertTrue("El campo 'retroceder' de la fila " + i + " no era correcto", modal ? true : !retroceder);
			assertTrue("El campo 'accio' de la fila " + i + " no era correcto", !mostrarAccio);
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", mostrarInformacio);
		} else if ("Completar tramitació de la tasca".equals(accio)) {
			assertTrue("El campo 'token' de la fila " + i + " no era correcto", modal ? true : !token.isEmpty());
			assertTrue("El campo 'element' de la fila " + i + " no era correcto", element.contains("Tasca:"));
			assertTrue("El campo 'retroceder' de la fila " + i + " no era correcto", modal ? true : retroceder);
			assertTrue("El campo 'accio' de la fila " + i + " no era correcto", !mostrarAccio);
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", !mostrarInformacio);
		} else if ("Executar script al procés".equals(accio)) {
			if (modal) {
				assertTrue("Error en el título de la ventana modal de la fila " + i + "", driver.findElement(By.xpath("//*[@id='DOMWindow']/h3")).getText().contains("Executar script al procés"));
				assertTrue("No re recuperó el texto del Script de la fila " + i + "", !driver.findElement(By.xpath("//*[@id='DOMWindow']/p")).getText().isEmpty());
			} else {
				assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", informacio.isEmpty());
				assertTrue("El campo 'token' de la fila " + i + " no era correcto", modal ? true : !token.isEmpty());
				assertTrue("El campo 'element' de la fila " + i + " no era correcto", element.contains("Procés:"));
				assertTrue("El campo 'retroceder' de la fila " + i + " no era correcto", modal ? true : !retroceder);
				assertTrue("El campo 'accio' de la fila " + i + " no era correcto", !mostrarAccio);
				assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", mostrarInformacio);
			}
		} else if ("Signar document de la tasca".equals(accio)) {
			// No se guarda
		} else if ("Actualitzar versió del procés".equals(accio)) {
			// No se guarda
		} else if ("Procesar document del portasignatures".equals(accio)) {
			
		} else if ("Tasca".equals(element)) {
			assertTrue("El campo 'token' de la fila " + i + " no era correcto", modal ? true : !token.isEmpty());
			assertTrue("El campo 'retroceder' de la fila " + i + " no era correcto", modal ? true : retroceder);
			assertTrue("El campo 'accio' de la fila " + i + " no era correcto", mostrarAccio);
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", !mostrarInformacio);
		} else {
			// Otro caso
		}
		
		return accio;
	}

	private void comprobarDatosRegistroModal(String pathUrl) {
		if (existeixElement(pathUrl)) {
			WebElement url = driver.findElement(By.xpath(pathUrl));
			url.click();
			if (modalObertaAssert(url.getAttribute("href"))) {
				vesAModal(url.getAttribute("href"));
				
				int j = 1;
				while (existeixElement("//*[@id='registre']/tbody/tr[" + j + "]")) {
					comprobarDatosRegistro(j, true);
					
					j++;
				}
				
				tornaAPare();

				if (existeixElement("html/body/div[8]/div[1]/a/span")) {
					driver.findElement(By.xpath("html/body/div[8]/div[1]/a/span")).click();
				} else {
					driver.findElement(By.xpath("//*[@id='content']/h3")).click();
				}
			}
		}
	}
}
