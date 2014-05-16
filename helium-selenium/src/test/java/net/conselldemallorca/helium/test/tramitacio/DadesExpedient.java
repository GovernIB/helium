package net.conselldemallorca.helium.test.tramitacio;

import static org.junit.Assert.assertTrue;

import java.util.List;

import net.conselldemallorca.helium.test.util.BaseTest;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DadesExpedient extends BaseTest {

	String entorn = carregarPropietat("entorn.nom", "Nom de l'entorn de proves no configurat al fitxer de properties");
	String codTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.codi", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	String nomDefProc = carregarPropietat("defproc.deploy.definicio.proces.nom", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String nomSubDefProc = carregarPropietat("defproc.deploy.definicio.subproces.nom", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String pathDefProc = carregarPropietat("defproc.deploy.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String pathSubDefProc = carregarPropietat("defproc.subproces.deploy.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String exportDefProc = carregarPropietat("defproc.tasca_dades.exp.export.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String nomTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.nom", "Nom del tipus d'expedient de proves no configurat al fitxer de properties");
	
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
		
		importarDadesExpedient(nomDefProc, exportDefProc);
		importarDadesExpedient(nomSubDefProc, exportDefProc);
		
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
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'"+nomDefProc+"')]")).click();
				
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
			i++;
		}
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
