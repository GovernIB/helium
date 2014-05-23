package net.conselldemallorca.helium.test.tramitacio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.conselldemallorca.helium.test.util.BaseTest;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class NouExpedient extends BaseTest {

	String entorn = carregarPropietat("entorn.nom", "Nom de l'entorn de proves no configurat al fitxer de properties");
	String codTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.codi", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	String nomDefProc = carregarPropietat("defproc.deploy.definicio.proces.nom", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String nomTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.nom", "Nom del tipus d'expedient de proves no configurat al fitxer de properties");
	String pathDefProc = carregarPropietat("defproc.nou.exp.deploy.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String exportDefProc = carregarPropietat("defproc.nou.exp.export.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
		
	static String entornActual;

	@Test
	public void b_listadoTipExp() throws InterruptedException {
		carregarUrlConfiguracio();

		// Selecció directe
		actions.moveToElement(driver.findElement(By.id("menuEntorn")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//li[@id='menuEntorn']/ul[@class='llista-entorns']/li[contains(., '" + entorn + "')]/a")));
		actions.click();
		actions.build().perform();
		
		existeixElementAssert("//li[@id='menuIniciar']", "No tiene permisos para iniciar un expediente");
		driver.findElement(By.xpath("//*[@id='menuIniciar']/a")).click();

		screenshotHelper.saveScreenshot("tramitar/listadoTipExp/11.png");
		
		// Obtenir nom del tipus d'expedient i cercar-lo
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		boolean isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codTipusExp + "')]")).size() > 0;
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

		assertTrue("No s'ha trobat el tipus d'expedient", isPresent);
		screenshotHelper.saveScreenshot("tramitar/listadoTipExp/2.png");
	}

	@Test
	public void c_inici_any() throws InterruptedException {
		carregarUrlConfiguracio();

		// Selecció directe
		actions.moveToElement(driver.findElement(By.id("menuEntorn")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//li[@id='menuEntorn']/ul[@class='llista-entorns']/li[contains(., '" + entorn + "')]/a")));
		actions.click();
		actions.build().perform();
		
		desplegarDefinicioProcesEntorn(nomTipusExp, nomDefProc, pathDefProc);
		importarDadesDefPro(nomDefProc, exportDefProc);
				
		existeixElementAssert("//li[@id='menuDisseny']", "No te permisos sobre disseny entorn");
			
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//*[@id='menuDisseny']/ul/li[3]/a")));
		actions.click();
		actions.build().perform();
	
		screenshotHelper.saveScreenshot("tramitar/inici_any/1.png");
		
		assertEquals("No s'ha pogut seleccionar l'entorn de forma directe.", entorn, driver.findElement(By.xpath("//div[@id='page-entorn-title']/h2/span")).getText().trim());
			
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codTipusExp + "')]/td[1]/a")).click();
		
		screenshotHelper.saveScreenshot("tramitar/inici_any/2.png");
		
		driver.findElement(By.xpath("//*[@id='content']/div[3]/form[1]/button")).click();
				
		WebElement check1 = driver.findElement(By.id("demanaNumero0"));
		if (!check1.isSelected()) {
			check1.click();
		}
		
		WebElement check2 = driver.findElement(By.id("teNumero0"));
		if (!check2.isSelected()) {
			check2.click();
		}
		
		WebElement check11 = driver.findElement(By.id("demanaTitol0"));
		if (check11.isSelected()) {
			check11.click();
		}
		
		WebElement check12 = driver.findElement(By.id("teTitol0"));
		if (check12.isSelected()) {
			check12.click();
		}
		
		driver.findElement(By.xpath("//*[@id='command']/div[3]/button[1]")).click();
		
		assertTrue("No se ha puesto con número correctamente","Si".equals(driver.findElement(By.xpath("//*[@id='content']/dl/dd[5]")).getText()));
		assertTrue("No se ha puesto que demande número correctamente","Si".equals(driver.findElement(By.xpath("//*[@id='content']/dl/dd[6]")).getText()));

		screenshotHelper.saveScreenshot("tramitar/inici_any/3.png");
		
		String[] res = iniciarExpediente(nomDefProc,codTipusExp,"SE-22/2014", null);
		
		// Eliminar el expediente
		eliminarExpedient(res[0], res[1]);
		
		eliminarDefinicioProces(nomDefProc);
		
		screenshotHelper.saveScreenshot("tramitar/inici_any/4.png");
	}

	@Test
	public void d_inici_titol() throws InterruptedException {
		carregarUrlConfiguracio();

		// Selecció directe
		actions.moveToElement(driver.findElement(By.id("menuEntorn")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//li[@id='menuEntorn']/ul[@class='llista-entorns']/li[contains(., '" + entorn + "')]/a")));
		actions.click();
		actions.build().perform();
			
		desplegarDefinicioProcesEntorn(nomTipusExp, nomDefProc, pathDefProc);
		importarDadesDefPro(nomDefProc, exportDefProc);
		
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//*[@id='menuDisseny']/ul/li[3]/a")));
		actions.click();
		actions.build().perform();
	
		screenshotHelper.saveScreenshot("tramitar/inici_titol/1.png");
		
		assertEquals("No s'ha pogut seleccionar l'entorn de forma directe.", entorn, driver.findElement(By.xpath("//div[@id='page-entorn-title']/h2/span")).getText().trim());
			
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codTipusExp + "')]/td[1]/a")).click();
		
		screenshotHelper.saveScreenshot("tramitar/inici_titol/2.png");
		
		driver.findElement(By.xpath("//*[@id='content']/div[3]/form[1]/button")).click();
		
		WebElement check11 = driver.findElement(By.id("demanaNumero0"));
		if (check11.isSelected()) {
			check11.click();
		}
		
		WebElement check12 = driver.findElement(By.id("teNumero0"));
		if (check12.isSelected()) {
			check12.click();
		}
		
		WebElement check1 = driver.findElement(By.id("demanaTitol0"));
		if (!check1.isSelected()) {
			check1.click();
		}
		
		WebElement check2 = driver.findElement(By.id("teTitol0"));
		if (!check2.isSelected()) {
			check2.click();
		}
		
		driver.findElement(By.xpath("//*[@id='command']/div[3]/button[1]")).click();
		
		assertTrue("No se ha puesto con título correctamente","Si".equals(driver.findElement(By.xpath("//*[@id='content']/dl/dd[3]")).getText()));
		assertTrue("No se ha puesto que demande título correctamente","Si".equals(driver.findElement(By.xpath("//*[@id='content']/dl/dd[4]")).getText()));
		
		screenshotHelper.saveScreenshot("tramitar/inici_titol/3.png");
		
		String[] res = iniciarExpediente(nomDefProc,codTipusExp,null, "Expedient de prova Selenium " + (new Date()).getTime() );
		
		// Eliminar el expediente
		eliminarExpedient(res[0], res[1]);
		
		eliminarDefinicioProces(nomDefProc);
		
		screenshotHelper.saveScreenshot("tramitar/inici_titol/4.png");
	}

	@Test
	public void e_iniciVersioExp() throws InterruptedException {
		carregarUrlConfiguracio();

		// Selecció directe
		actions.moveToElement(driver.findElement(By.id("menuEntorn")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//li[@id='menuEntorn']/ul[@class='llista-entorns']/li[contains(., '" + entorn + "')]/a")));
		actions.click();
		actions.build().perform();
		
		desplegarDefinicioProcesEntorn(nomTipusExp, nomDefProc, pathDefProc);
		importarDadesDefPro(nomDefProc, exportDefProc);
		
		driver.findElement(By.xpath("//*[@id='menuIniciar']/a")).click();
		WebElement selectTipusExpedient = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codTipusExp + "')]/td[3]/form/select"));
		List<WebElement> allOptions = selectTipusExpedient.findElements(By.tagName("option"));
		
		// Iniciamos con la primera versión
		allOptions.get(allOptions.size()-1).click();
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codTipusExp + "')]/td[3]/form/button")).click();
		acceptarAlerta();
		
		screenshotHelper.saveScreenshot("tramitar/iniciVersioExp/1.png");
		
		String numero = "SE-22/2014";
		String titulo = "Expedient de prova Selenium " + (new Date()).getTime();
		if (driver.findElements(By.xpath("//*[@id='numero0']")).size() > 0 && numero != null) {
			driver.findElement(By.xpath("//*[@id='numero0']")).clear();
			driver.findElement(By.xpath("//*[@id='numero0']")).sendKeys(numero);
		} else {
			numero = null;
		}
		if (driver.findElements(By.xpath("//*[@id='titol0']")).size() > 0 && titulo != null) {
			driver.findElement(By.xpath("//*[@id='titol0']")).clear();
			driver.findElement(By.xpath("//*[@id='titol0']")).sendKeys(titulo);
		} else {
			titulo = null;
		}
		driver.findElement(By.xpath("//button[@value='submit']")).click();
		acceptarAlerta();
		
		existeixElementAssert("//*[@id='infos']/p", "No se inició el expediente");
		
		screenshotHelper.saveScreenshot("tramitar/iniciAmbTascaIniExp/3.png");
		
		String textoInfo = driver.findElement(By.xpath("//*[@id='infos']/p")).getText();
		
		String[] res = new String[2];
		if (textoInfo.indexOf("]") != -1) {
			res[0] = textoInfo.substring(textoInfo.indexOf("[")+1, textoInfo.indexOf("]")).trim();
			res[1] = textoInfo.substring(textoInfo.indexOf("]")+1).trim();
		} else {
			if (numero != null)
				res[0] = textoInfo.substring(textoInfo.indexOf(":")+1).trim();
			else
				res[1] = textoInfo.substring(textoInfo.indexOf(":")+1).trim();
		}
		
		existeixElementAssert("//*[@id='infos']/p", "No se inició el expediente");
		
		screenshotHelper.saveScreenshot("tramitar/iniciVersioExp/3.png");
		
		// Eliminar el expediente
		eliminarExpedient(res[0], res[1]);
		
		eliminarDefinicioProces(nomDefProc);
		
		screenshotHelper.saveScreenshot("tramitar/iniciVersioExp/4.png");
	}

	@Test
	public void f_iniciAmbTascaIniExp() throws InterruptedException {
		carregarUrlConfiguracio();

		// Selecció directe
		actions.moveToElement(driver.findElement(By.id("menuEntorn")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//li[@id='menuEntorn']/ul[@class='llista-entorns']/li[contains(., '" + entorn + "')]/a")));
		actions.click();
		actions.build().perform();
				
		String pathDefProc = carregarPropietat("defproc.nou_tasca_ini.exp.deploy.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
		String exportDefProc = carregarPropietat("defproc.nou_tasca_ini.exp.export.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
		
		desplegarDefinicioProcesEntorn(nomTipusExp, nomDefProc, pathDefProc);
		importarDadesDefPro(nomDefProc, exportDefProc);
		
		existeixElementAssert("//li[@id='menuIniciar']", "No tiene permisos para iniciar un expediente");
		driver.findElement(By.xpath("//*[@id='menuIniciar']/a")).click();

		screenshotHelper.saveScreenshot("tramitar/iniciAmbTascaIniExp/1.png");
		
		// Obtenir nom del tipus d'expedient i cercar-lo
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		boolean isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codTipusExp + "')]")).size() > 0;
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

		assertTrue("No s'ha trobat el tipus d'expedient", isPresent);
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codTipusExp + "')]/td[3]/form/button")).click();
		
		driver.findElement(By.id("var_str010")).sendKeys("Una cadena de texto");

		screenshotHelper.saveScreenshot("tramitar/iniciAmbTascaIniExp/2.png");
		
		driver.findElement(By.xpath("//button[@value='submit']")).click();
		
		String numero = "SE-22/2014";
		String titulo = "Expedient de prova Selenium " + (new Date()).getTime();
		if (driver.findElements(By.xpath("//*[@id='numero0']")).size() > 0 && numero != null) {
			driver.findElement(By.xpath("//*[@id='numero0']")).clear();
			driver.findElement(By.xpath("//*[@id='numero0']")).sendKeys(numero);
		} else {
			numero = null;
		}
		if (driver.findElements(By.xpath("//*[@id='titol0']")).size() > 0 && titulo != null) {
			driver.findElement(By.xpath("//*[@id='titol0']")).clear();
			driver.findElement(By.xpath("//*[@id='titol0']")).sendKeys(titulo);
		} else {
			titulo = null;
		}
		driver.findElement(By.xpath("//button[@value='submit']")).click();
		acceptarAlerta();
		
		existeixElementAssert("//*[@id='infos']/p", "No se inició el expediente");
		
		screenshotHelper.saveScreenshot("tramitar/iniciAmbTascaIniExp/3.png");
		
		String textoInfo = driver.findElement(By.xpath("//*[@id='infos']/p")).getText();
		
		String[] res = new String[2];
		if (textoInfo.indexOf("]") != -1) {
			res[0] = textoInfo.substring(textoInfo.indexOf("[")+1, textoInfo.indexOf("]")).trim();
			res[1] = textoInfo.substring(textoInfo.indexOf("]")+1).trim();
		} else {
			if (numero != null)
				res[0] = textoInfo.substring(textoInfo.indexOf(":")+1).trim();
			else
				res[1] = textoInfo.substring(textoInfo.indexOf(":")+1).trim();
		}
		
		existeixElementAssert("//*[@id='infos']/p", "No se inició el expediente");
		
		screenshotHelper.saveScreenshot("tramitar/iniciAmbTascaIniExp/4.png");
		
		// Eliminar el expediente
		eliminarExpedient(res[0], res[1]);
		
		// Eliminar la def de proceso
		eliminarDefinicioProces(nomDefProc);
		
		screenshotHelper.saveScreenshot("tramitar/iniciAmbTascaIniExp/5.png");
	}
}
