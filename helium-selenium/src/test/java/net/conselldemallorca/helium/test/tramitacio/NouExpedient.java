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
public class NouExpedient extends BaseTest {

	String entorn = carregarPropietat("tramsel.entorn.nom", "Nom de l'entorn de proves no configurat al fitxer de properties");
	String titolEntorn = carregarPropietat("tramsel.entorn.titol", "Titol de l'entorn de proves no configurat al fitxer de properties");
	String codTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.codi", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	String nomDefProc = carregarPropietat("defproc.deploy.definicio.proces.nom", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String exportTipExpProc = carregarPropietatPath("tipexp.tasca_dades_doc.exp.export.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String nomTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.nom", "Nom del tipus d'expedient de proves no configurat al fitxer de properties");
	String usuari = carregarPropietat("test.base.usuari.configuracio", "Usuari configuració de l'entorn de proves no configurat al fitxer de properties");
	
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
	public void b_listadoTipExp() throws InterruptedException {
		carregarUrlConfiguracio();

		seleccionarEntorn(titolEntorn);
		
		existeixElementAssert("//li[@id='menuIniciar']", "No tiene permisos para iniciar un expediente");
		driver.findElement(By.xpath("//*[@id='menuIniciar']/a")).click();

		screenshotHelper.saveScreenshot("NouExpedient/listadoTipExp/11.png");

		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + codTipusExp + "')]", "No s'ha trobat el tipus d'expedient");
		
		screenshotHelper.saveScreenshot("NouExpedient/listadoTipExp/2.png");
	}

	@Test
	public void c_inici_any() throws InterruptedException {
		carregarUrlConfiguracio();

		seleccionarEntorn(titolEntorn);
				
		existeixElementAssert("//li[@id='menuDisseny']", "No te permisos sobre disseny entorn");
			
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//*[@id='menuDisseny']//a[contains(@href,'/expedientTipus/llistat.html')]")));
		actions.click();
		actions.build().perform();
	
		screenshotHelper.saveScreenshot("NouExpedient/inici_any/1.png");
			
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codTipusExp + "')]/td[1]/a")).click();
		
		screenshotHelper.saveScreenshot("NouExpedient/inici_any/2.png");
		
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

		screenshotHelper.saveScreenshot("NouExpedient/inici_any/3.png");
		
		String[] res = iniciarExpediente(codTipusExp,"SE-22/2014", null);
		
		// Eliminar el expediente
		eliminarExpedient(res[0], res[1]);
		
		screenshotHelper.saveScreenshot("NouExpedient/inici_any/4.png");
	}

	@Test
	public void d_inici_titol() throws InterruptedException {
		carregarUrlConfiguracio();

		seleccionarEntorn(titolEntorn);
		
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//*[@id='menuDisseny']//a[contains(@href,'/expedientTipus/llistat.html')]")));
		actions.click();
		actions.build().perform();
	
		screenshotHelper.saveScreenshot("NouExpedient/inici_titol/1.png");
			
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codTipusExp + "')]/td[1]/a")).click();
		
		screenshotHelper.saveScreenshot("NouExpedient/inici_titol/2.png");
		
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
		
		screenshotHelper.saveScreenshot("NouExpedient/inici_titol/3.png");
		
		String[] res = iniciarExpediente(codTipusExp,null, "Expedient de prova Selenium " + (new Date()).getTime() );
		
		// Eliminar el expediente
		eliminarExpedient(res[0], res[1]);
		
		screenshotHelper.saveScreenshot("NouExpedient/inici_titol/4.png");
	}

	@Test
	public void e_iniciVersioExp() throws InterruptedException {
		carregarUrlConfiguracio();

		seleccionarEntorn(titolEntorn);
		
		eliminarExpedient(null, null, nomTipusExp);
		
		String pathDefProc = carregarPropietatPath("defproc.nou_tasca_ini.exp.deploy.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
		String exportDefProc = carregarPropietatPath("defproc.nou_tasca_ini.exp.export.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
		
		desplegarDefinicioProcesEntorn(nomTipusExp, nomDefProc, pathDefProc);
		importarDadesDefPro(nomDefProc, exportDefProc);
		
		driver.findElement(By.xpath("//*[@id='menuIniciar']/a")).click();
		
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + codTipusExp + "')]", "No s'ha trobat el tipus d'expedient");
				
		WebElement selectTipusExpedient = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codTipusExp + "')]/td[3]/form/select"));
		List<WebElement> allOptions = selectTipusExpedient.findElements(By.tagName("option"));
		
		assertTrue("Sólo se disponía de 1 número de versión",allOptions.size() > 2);
		
		// Iniciamos con la primera versión
		String versioProc = allOptions.get(allOptions.size()-1).getText();
		allOptions.get(allOptions.size()-1).click();
		
		String[] res = new String[2];
		
		existeixElementAssert("//li[@id='menuIniciar']", "No tiene permisos para iniciar un expediente");
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codTipusExp + "')]/td[3]/form/button")).click();
		
		String numero = "SE-22/2014";
		String titulo = "Expedient de prova Selenium " + (new Date()).getTime();
		
		if (isAlertPresent()) {
			acceptarAlerta();
		}
		String tituloPantalla = driver.findElement(By.xpath("//*[@id='page-title']/h2")).getText().trim();
		if (tituloPantalla.startsWith("Iniciar expedient:")) {	
			if (existeixElement("//*[@id='var_str010']")) {
				driver.findElement(By.xpath("//*[@id='var_str010']")).clear();
				driver.findElement(By.xpath("//*[@id='var_str010']")).sendKeys("Un nombre de variable");
				driver.findElement(By.xpath("//button[@value='submit']")).click();
			}			
			if (existeixElement("//*[@id='numero0']")) {
				driver.findElement(By.xpath("//*[@id='numero0']")).clear();
				driver.findElement(By.xpath("//*[@id='numero0']")).sendKeys(numero);
			} else {
				numero = null;
			}
			if (existeixElement("//*[@id='titol0']")) {
				driver.findElement(By.xpath("//*[@id='titol0']")).clear();
				driver.findElement(By.xpath("//*[@id='titol0']")).sendKeys(titulo);
			} else {
				titulo = null;
			}
			driver.findElement(By.xpath("//button[@value='submit']")).click();
		}
		
		acceptarAlerta();
		
		existeixElementAssert("//*[@id='infos']/p", "No se inició el expediente");
		
		String textoInfo = driver.findElement(By.xpath("//*[@id='infos']/p")).getText();
		if (textoInfo.indexOf("]") != -1) {
			res[0] = textoInfo.substring(textoInfo.indexOf("[")+1, textoInfo.indexOf("]")).trim();
			res[1] = textoInfo.substring(textoInfo.indexOf("]")+1).trim();
		} else {
			if (numero != null)
				res[0] = textoInfo.substring(textoInfo.indexOf(":")+1).trim();
			if (titulo != null)
				res[1] = textoInfo.substring(textoInfo.indexOf(":")+1).trim();
		}
		
		screenshotHelper.saveScreenshot("NouExpedient/iniciVersioExp/3.png");
		
		// Comprobamos el número de versión del expediente
		consultarExpedientes(numero, titulo, nomTipusExp);
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]//img[@src='/helium/img/information.png']")).click();
		
		// Versión del proceso
		String versioAct = driver.findElement(By.xpath("//dd[contains(text(), '"+nomDefProc+"')]")).getText().trim();
		assertTrue("No coincidía la versión del expediente", versioProc.equals(versioAct));	
	}

	@Test
	public void f_iniciAmbTascaIniExp() throws InterruptedException {
		carregarUrlConfiguracio();

		seleccionarEntorn(titolEntorn);
		
		eliminarExpedient(null, null, nomTipusExp);
				
		String pathDefProc = carregarPropietatPath("defproc.nou_tasca_ini.exp.deploy.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
		String exportDefProc = carregarPropietatPath("defproc.nou_tasca_ini.exp.export.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
		
		desplegarDefinicioProcesEntorn(nomTipusExp, nomDefProc, pathDefProc);
		importarDadesDefPro(nomDefProc, exportDefProc);
		
		existeixElementAssert("//li[@id='menuIniciar']", "No tiene permisos para iniciar un expediente");
		driver.findElement(By.xpath("//*[@id='menuIniciar']/a")).click();

		screenshotHelper.saveScreenshot("NouExpedient/iniciAmbTascaIniExp/1.png");

		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + codTipusExp + "')]", "No s'ha trobat el tipus d'expedient");
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codTipusExp + "')]/td[3]/form/button")).click();
		
		driver.findElement(By.id("var_str010")).sendKeys("Una cadena de texto");

		screenshotHelper.saveScreenshot("NouExpedient/iniciAmbTascaIniExp/2.png");
		
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
		
		screenshotHelper.saveScreenshot("NouExpedient/iniciAmbTascaIniExp/3.png");
		
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
		
		screenshotHelper.saveScreenshot("NouExpedient/iniciAmbTascaIniExp/4.png");
	}

	@Test
	public void z_limpiar() throws InterruptedException {
		carregarUrlConfiguracio();
		
		seleccionarEntorn(titolEntorn);
		
		eliminarExpedient(null, null, nomTipusExp);
		
		// Eliminar el tipo de expediente
		eliminarTipusExpedient(codTipusExp);
		
		eliminarEntorn(entorn);
		
		screenshotHelper.saveScreenshot("TasquesDadesTasca/finalizar_expedient/1.png");	
	}
}
