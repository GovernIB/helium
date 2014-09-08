package net.conselldemallorca.helium.test.disseny;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import net.conselldemallorca.helium.test.util.BaseTest;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Entorn extends BaseTest {

	String entorn			= "EntProvesEnt"; //carregarPropietat("entorn.nom", "Nom de l'entorn de proves no configurat al fitxer de properties");
	String titolEntorn		= "Entorn Proves Selenium - ENT - Entorn"; //carregarPropietat("entorn.titol", "Titol de l'entorn de proves no configurat al fitxer de properties");
	String entorn_aux 		= "EntProvesAux";
	String titolEntorn_aux	= "Entorn Proves Selenium - ENT - Auxixiar Entorn";
	String usuari	 = carregarPropietat("test.base.usuari.configuracio", "Usuari configuració de l'entorn de proves no configurat al fitxer de properties");
	String usuariDis = carregarPropietat("test.base.usuari.disseny", "Usuari configuració de l'entorn de proves no configurat al fitxer de properties");
	String rol		 = carregarPropietat("test.base.rol.configuracio", "Rol configuració de l'entorn de proves no configurat al fitxer de properties");
	
	String nomTipusExp	= carregarPropietat("entorn.reindexacio.tipus.expedient.nom", "Nom del tipus d'expedient de proves no configurat al fitxer de properties");
	String codTipusExp	= carregarPropietat("entorn.reindexacio.tipus.expedient.codi", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	String tipusExpPath	= carregarPropietatPath("entorn.reindexacio.tipexp.deploy.path", "Path del tipus d'expedient de proves no configurat al fitxer de properties");
	
	//XPATHS
	String botoNouDomini		= "//*[@id='content']/form/button";
	String botoGuardaDomini		= "//*[@id='command']/div[4]/button[1]";
	String botoProvarDominiSQL	= "//*[@id='registre']/tbody/tr[contains(td[1],'sqlDomId')]/td[4]/img";
	String botoProvarDominiWS	= "//*[@id='registre']/tbody/tr[contains(td[1],'wsDomId')]/td[4]/img";
	String enllaçElimDomini		= "//*[@id='registre']/tbody/tr[1]/td[5]/a";

	String formReidexarEntorn = "//*[@id='registre']/tbody/tr[contains(td,'"+entorn+"')]/td/form[contains(@action, 'reindexar.html')]";
	
	String linkBorrarExpedient = "//*[@id='registre']/tbody/tr[1]/td/a[contains(@href, '/expedient/delete.html')]";
	
	@Test
	public void b_createEntorns() {
		
		carregarUrlConfiguracio();
		
		// Comprovam que tenim permisos
		existeixElementAssert("//li[@id='menuConfiguracio']", "No te permisos de configuració a Helium");
		
		// Anem a la pantalla de configuració d'entorns
		actions.moveToElement(driver.findElement(By.id("menuConfiguracio")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/entorn/llistat.html')]")));
		actions.click();
		actions.build().perform();
		
		screenshotHelper.saveScreenshot("entorns/crear/1_entornsExistents_01.png");
		noExisteixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + entorn + "')]", "L\'entorn a crear ja existeix.");
			
		// Entorn no existeix. Cream entorn
		driver.findElement(By.xpath("//div[@id='content']/form/button[@class='submitButton']")).click();
			
		driver.findElement(By.id("codi0")).clear();
		driver.findElement(By.id("codi0")).sendKeys(entorn);
		driver.findElement(By.id("nom0")).clear();
		driver.findElement(By.id("nom0")).sendKeys(titolEntorn);
		
		screenshotHelper.saveScreenshot("entorns/crear/1_entornCrearActiu_01.png");
		driver.findElement(By.xpath("//button[@value='submit']")).click();

		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + entorn + "')]", "entorns/crear/3_entornCrearActiu_02.png", "No s'ha pogut crear l'entorn");

		// Cream el segon entorn auxiliar
		
		// Comprovam que tenim permisos
		existeixElementAssert("//li[@id='menuConfiguracio']", "No te permisos de configuració a Helium");
		
		// Anem a la pantalla de configuració d'entorns
		actions.moveToElement(driver.findElement(By.id("menuConfiguracio")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/entorn/llistat.html')]")));
		actions.click();
		actions.build().perform();
		
		screenshotHelper.saveScreenshot("entorns/crear/1_entornsExistents_02.png");
		noExisteixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + entorn_aux + "')]", "L\'entorn auxiliar a crear ja existeix.");
			
		// Entorn no existeix. Cream entorn
		driver.findElement(By.xpath("//div[@id='content']/form/button[@class='submitButton']")).click();
			
		driver.findElement(By.id("codi0")).clear();
		driver.findElement(By.id("codi0")).sendKeys(entorn_aux);
		driver.findElement(By.id("nom0")).clear();
		driver.findElement(By.id("nom0")).sendKeys(titolEntorn_aux);
		
		screenshotHelper.saveScreenshot("entorns/crear/1_entornCrearActiu_02.png");
		driver.findElement(By.xpath("//button[@value='submit']")).click();		
	}
	
	@Test
	public void c_activacioEntorn() {
		
		carregarUrlConfiguracio();
		
		existeixElementAssert("//li[@id='menuConfiguracio']", "No te permisos de configuració a Helium");
		
		// Anem a la pantalla de configuració d'entorns
		actions.moveToElement(driver.findElement(By.id("menuConfiguracio")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/entorn/llistat.html')]")));
		actions.click();
		actions.build().perform();
		
		screenshotHelper.saveScreenshot("entorns/activacio/1_entornsExistents_01.png");
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + entorn + "')]", "L\'entorn de proves no existeix.");
		
		String actiu = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + entorn + "')]/td[3]")).getText().trim();
		
		// Accedim a l'entorn
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + entorn + "')]/td[1]/a")).click();
		
		// Desactivam entorn
		driver.findElement(By.id("actiu0")).click();
		screenshotHelper.saveScreenshot("entorns/activacio/2_entornDesactivar.png");
		driver.findElement(By.xpath("//button[@value='submit']")).click();
		screenshotHelper.saveScreenshot("entorns/actiu/3_entornsExistents_02.png");
		
		String nouActiu = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + entorn + "')]/td[3]")).getText().trim();
		assertNotEquals("No s'ha pogut desactivar l'entorn de test", actiu, nouActiu);
		
		// Acctivam entorn
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + entorn + "')]/td[1]/a")).click();
		driver.findElement(By.id("actiu0")).click();
		screenshotHelper.saveScreenshot("entorns/activacio/4_entornActivar.png");
		driver.findElement(By.xpath("//button[@value='submit']")).click();
		screenshotHelper.saveScreenshot("entorns/actiu/5_entornsExistents_02.png");
		
		nouActiu = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + entorn + "')]/td[3]")).getText().trim();
		assertEquals("No s'ha pogut activar l'entorn de test", actiu, nouActiu);
	}
	
	@Test
	public void d_canviTitolEntorn() {
		
		carregarUrlConfiguracio();
		existeixElementAssert("//li[@id='menuConfiguracio']", "No te permisos de configuració a Helium");
		// Anem a la pantalla de configuració d'entorns
		actions.moveToElement(driver.findElement(By.id("menuConfiguracio")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/entorn/llistat.html')]")));
		actions.click();
		actions.build().perform();
		
		screenshotHelper.saveScreenshot("entorns/titol/1_entornsExistents_01.png");
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + entorn + "')]", "L\'entorn de proves no existeix.");

		String titol = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + entorn + "')]/td[2]")).getText().trim();
		
		// Modificam el títol de l'entorn
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + entorn + "')]/td[1]/a")).click();
		screenshotHelper.saveScreenshot("entorns/titol/2_canviTitol.png");
		driver.findElement(By.id("nom0")).clear();
		driver.findElement(By.id("nom0")).sendKeys(titolEntorn+"Mod");

		screenshotHelper.saveScreenshot("entorns/titol/3_canviTitol.png");
		driver.findElement(By.xpath("//button[@value='submit']")).click();

		screenshotHelper.saveScreenshot("entorns/titol/4_entornsExistents_02.png");
		String nouTitol = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + entorn + "')]/td[2]")).getText().trim();
		assertNotEquals("No s'ha pogut canviar el titol de l'entorn de test", titol, nouTitol);
		
		//Tornam a canviar el titol com estaba abans per les futures proves
		
		actions.moveToElement(driver.findElement(By.id("menuConfiguracio")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/entorn/llistat.html')]")));
		actions.click();
		actions.build().perform();
		
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + entorn + "')]", "L\'entorn de proves no existeix.");

		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + entorn + "')]/td[1]/a")).click();
		driver.findElement(By.id("nom0")).clear();
		driver.findElement(By.id("nom0")).sendKeys(titolEntorn);
		
		driver.findElement(By.xpath("//button[@value='submit']")).click();		
	}
	
	@Test
	public void e_assignarPermisosUsuari() {
		
		carregarUrlConfiguracio();
		
		existeixElementAssert("//li[@id='menuConfiguracio']", "No te permisos de configuració a Helium");
		
		assignarPermisos("crear/usuari", usuari, true);
		
		assignarPermisosEntorn(entorn, usuari, "DESIGN", "READ", "ORGANIZATION", "ADMINISTRATION");
		
		assignarPermisosEntorn(entorn_aux, usuari, "DESIGN", "READ");
		
		assignarPermisosEntorn(entorn, usuariDis, "DESIGN", "READ");
	}
	
	@Test
	public void f_assignarPermisosRol() {
		
		carregarUrlConfiguracio();
		
		existeixElementAssert("//li[@id='menuConfiguracio']", "No te permisos de configuració a Helium");
		
		assignarPermisos("crear/rol", rol, false);
	}
	
	@Test
	public void g_seleccioEntorn() {
		
		carregarUrlConfiguracio();
				
		existeixElementAssert("//li[@id='menuEntorn']", "No te permisos sobre cap entorn");
		
		/*String entornActual = driver.findElement(By.xpath("//div[@id='page-entorn-title']/h2/span")).getText().trim();
		existeixElementAssert("//li[@id='menuEntorn']/ul[@class='llista-entorns']/li[contains(., '" + titolEntorn + "')]", 
				"entorns/seleccionar/directe/1_entornActual.png", "L'entorn de proves no existeix.");*/
		
		String entornActual = titolEntorn_aux;
		
		// Selecció directe
		actions.moveToElement(driver.findElement(By.id("menuEntorn")));
		actions.build().perform();
		//actions.moveToElement(driver.findElement(By.xpath("//li[@id='menuEntorn']/ul[@class='llista-entorns']/li[contains(., '" + titolEntorn + "')]/a")));
		actions.moveToElement(driver.findElement(By.xpath("//*[@id='menuEntorn']/ul[@class='llista-entorns']/li/a[contains(text(), '"+titolEntorn+"')]")));
		actions.click();
		actions.build().perform();
	
		screenshotHelper.saveScreenshot("entorns/seleccionar/directe/1_entornActiu.png");
		assertEquals("No s'ha pogut seleccionar l'entorn de forma directe.", titolEntorn, driver.findElement(By.xpath("//div[@id='page-entorn-title']/h2/span")).getText().trim());
		
		// Anam a l'entorn auxiliar
		actions.moveToElement(driver.findElement(By.id("menuEntorn")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//*[@id='menuEntorn']/ul[@class='llista-entorns']/li/a[contains(text(), '"+entornActual+"')]")));
		actions.click();
		actions.build().perform();
	
		screenshotHelper.saveScreenshot("entorns/seleccionar/form/1_entornActiu.png");
		assertEquals("No s'ha pogut retornar a l'entorn original.", entornActual, driver.findElement(By.xpath("//div[@id='page-entorn-title']/h2/span")).getText().trim());
		
		// Selecció via formulari
		driver.findElement(By.id("menuEntorn")).findElement(By.tagName("a")).click();
		screenshotHelper.saveScreenshot("entorns/seleccionar/form/2_entorns.png");
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[2],'" + titolEntorn + "')]/td[3]/form/button")).click();
		
		screenshotHelper.saveScreenshot("entorns/seleccionar/form/3_entornActiu.png");
		
		assertEquals("No s'ha pogut seleccionar l'entorn via formulari.", titolEntorn, driver.findElement(By.xpath("//div[@id='page-entorn-title']/h2/span")).getText().trim());
		
		// Anam a l'entorn auxiliar
		actions.moveToElement(driver.findElement(By.id("menuEntorn")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//li[@id='menuEntorn']/ul[@class='llista-entorns']/li[contains(., '" + entornActual + "')]/a")));
		actions.click();
		actions.build().perform();
			
		assertEquals("No s'ha pogut retornar a l'entorn original.", entornActual, driver.findElement(By.xpath("//div[@id='page-entorn-title']/h2/span")).getText().trim());
	}
	
	@Test
	public void h_marcarDefecteEntorn() {
		
		carregarUrlConfiguracio();
		
		existeixElementAssert("//li[@id='menuEntorn']", "No te permisos sobre cap entorn");

		//1.- Miram si existeix l´entorn auxiliar y el marcam com a preferit
		driver.findElement(By.id("menuEntorn")).findElement(By.tagName("a")).click();
		existeixElementAssert("//li[@id='menuEntorn']/ul[@class='llista-entorns']/li[contains(., '" + titolEntorn_aux + "')]", "entorns/default/1_entornActual.png", "L'entorn de proves auxiliar no existeix.");
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[2],'" + titolEntorn_aux + "')]/td[1]/a")).click();
		
		//2.- Miram si existeix l´entorn principal de proves i veim en quin estat es troba
		driver.findElement(By.id("menuEntorn")).findElement(By.tagName("a")).click();
		existeixElementAssert("//li[@id='menuEntorn']/ul[@class='llista-entorns']/li[contains(., '" + titolEntorn + "')]", "entorns/default/1_entornActual.png", "L'entorn de proves no existeix.");
		String src = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[2],'" + titolEntorn + "')]/td[1]/a/img")).getAttribute("src");
		
		if (src.endsWith("star.png")) {
			fail("L'entorn ja està marcat per defecte");
		} else {
			
			screenshotHelper.saveScreenshot("entorns/default/2_entornDefecte.png");
			
			//El marcam com a per defecte
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[2],'" + titolEntorn + "')]/td[1]/a")).click();			
			driver.findElement(By.id("menuEntorn")).findElement(By.tagName("a")).click();
			
			screenshotHelper.saveScreenshot("entorns/default/3_entornDefecte.png");
			
			//Comprovam que s´ha marcat com a per defecte
			src = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[2],'" + titolEntorn + "')]/td[1]/a/img")).getAttribute("src");
			assertTrue("No s'ha pogut marcar l'entorn per defecte.", src.endsWith("star.png"));
		}
	}
	
	// ---------------------------------------------------
	// R E I N D E X A C I O
	// ---------------------------------------------------

	@Test
    public void h_reindexar_expedients() throws InterruptedException {
		
        carregarUrlConfiguracio();
        
        seleccionarEntorn(titolEntorn);
        
        crearTipusExpedient(nomTipusExp, codTipusExp);
        assignarPermisosTipusExpedient(codTipusExp, usuari, "CREATE", "MANAGE", "WRITE", "DELETE", "ADMINISTRATION", "DESIGN", "SUPERVISION", "READ");
        importarDadesTipExp(codTipusExp, tipusExpPath);
        
        screenshotHelper.saveScreenshot("entorns/reindexar/1_tipusExpedientImportat.png");
        
        // Iniciamos n expedientes con la última versión
        List<String[]> expedientes = new ArrayList<String[]>();
        for (int i = 0; i < 5; i++) {
            String[] expediente = iniciarExpediente(codTipusExp,"SE-"+i+"/2014", "Expedient de prova Selenium " + (new Date()).getTime() );
            screenshotHelper.saveScreenshot("entorns/reindexar/2_"+(i+1)+"_expedientcreat.png");
            expedientes.add(expediente);
        }
        
        int contadorExpedientsScript = 1;
        // Eliminamos los expedientes en Lucene
        for (String[] expediente : expedientes) {
            
        	String script = ""
                    + "String processInstanceId = net.conselldemallorca.helium.jbpm3.integracio.Jbpm3HeliumBridge.getInstanceService().getExpedientAmbEntornITipusINumero(net.conselldemallorca.helium.jbpm3.integracio.Jbpm3HeliumBridge.getInstanceService().getEntornActual().getId(), \""+codTipusExp+"\", \""+expediente[0]+"\").getProcessInstanceId();"
                    + "net.conselldemallorca.helium.jbpm3.integracio.Jbpm3HeliumBridge.getInstanceService().luceneDeleteExpedient(processInstanceId);";       	
        	
            actions.moveToElement(driver.findElement(By.id("menuConsultes")));
            actions.build().perform();
            actions.moveToElement(driver.findElement(By.xpath("//*[@id='menuConsultes']//a[contains(@href, '/expedient/consultaDisseny.html')]")));
            actions.click();
            actions.build().perform();

            try {
            	//A vegades passa directament a la pantalla de consulta disseny sense pasar per la seleccio de tipus Expedient / Consulta
            	driver.findElement(By.xpath("//*[@id='expedientTipusId0']")).findElements(By.tagName("option")).get(1).click();
            	driver.findElement(By.xpath("//*[@id='consultaId0']")).findElements(By.tagName("option")).get(1).click();
            }catch (Exception ex) {}
            
            driver.findElement(By.xpath("//button[contains(text(), 'Consultar')]")).click();
            
            screenshotHelper.saveScreenshot("entorns/reindexar/3_"+contadorExpedientsScript+"_1_llistaExpedients.png");
            
            //Executam l´script per aquest expedient
            driver.findElement(By.xpath("//*[@id='registre']//a[contains(@href,'/expedient/info.html')][1]")).click();
            driver.findElement(By.xpath("//*[@id='tabnav']//a[contains(@href,'/expedient/eines.html')]")).click();
            
            screenshotHelper.saveScreenshot("entorns/reindexar/3_"+contadorExpedientsScript+"_2_dinsExpedientEines.png");
            
            driver.findElement(By.xpath("//*[@id='content']/div/h3[contains(a/text(), \"Execució d'scripts\")]/a")).click();
            
            screenshotHelper.saveScreenshot("entorns/reindexar/3_"+contadorExpedientsScript+"_3_execucioScriptsExpedient.png");
            
            driver.findElement(By.xpath("//*[@id='script0']")).sendKeys(script);
            
            screenshotHelper.saveScreenshot("entorns/reindexar/3_"+contadorExpedientsScript+"_4_acceptarScript.png");
            
            driver.findElement(By.xpath("//button[contains(text(), 'Executar')]")).click();
            if (isAlertPresent()) { acceptarAlerta(); }
            existeixElementAssert("//*[@id='infos']/p", "No se ejecutó el script correctamente del expediente: " + expediente);
            
            screenshotHelper.saveScreenshot("entorns/reindexar/3_"+contadorExpedientsScript+"_5_resultatScript.png");
            
            contadorExpedientsScript++;
        }
        
        //Reindexam desde l´entorn
			actions.moveToElement(driver.findElement(By.id("menuConfiguracio")));
			actions.build().perform();
			actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/entorn/llistat.html')]")));
			actions.click();
			actions.build().perform();

			screenshotHelper.saveScreenshot("entorns/reindexar/4_entorns.png");
			
			driver.findElement(By.xpath(formReidexarEntorn)).submit();

			if (isAlertPresent()) { acceptarAlerta(); }

			Wait<WebDriver> wait = new WebDriverWait(driver, 10);
	        WebElement element = wait.until(visibilityOfElementLocated(By.id("infos")));
			
	        screenshotHelper.saveScreenshot("entorns/reindexar/5_resultatIndexacioEntorn.png");
	        
	        if (element==null) { fail("La reindexació a nivell d' entorn no s´ha executat correctament."); }
	        
        // Comprobamos que aparezcan
        actions.moveToElement(driver.findElement(By.id("menuConsultes")));
        actions.build().perform();
        actions.moveToElement(driver.findElement(By.xpath("//*[@id='menuConsultes']//a[contains(@href, '/expedient/consultaDisseny.html')]")));
        actions.click();
        actions.build().perform();
        
        try {
        	//A vegades passa directament a la pantalla de consulta disseny sense pasar per la seleccio de tipus Expedient / Consulta
        	driver.findElement(By.xpath("//*[@id='expedientTipusId0']")).findElements(By.tagName("option")).get(1).click();
        	driver.findElement(By.xpath("//*[@id='consultaId0']")).findElements(By.tagName("option")).get(1).click();
        }catch (Exception ex) {}
        
        driver.findElement(By.xpath("//button[contains(text(), 'Consultar')]")).click();

        screenshotHelper.saveScreenshot("entorns/reindexar/6_llistaExpedientsDespresIndexar.png");
        
        //Comprovam que els expedients tornen a apareixer
        for (String[] expediente : expedientes) {
            existeixElementAssert("//td[contains(a/text(),'"+expediente[0]+"')]", "No se encontró el expediente: " + expediente[0]);            
        }
        
        //Els eliminam
        while (existeixElement(linkBorrarExpedient)) {
        	driver.findElement(By.xpath(linkBorrarExpedient)).click();
        	if (isAlertPresent()) { acceptarAlerta(); }
        }
        
        eliminarTipusExpedient(codTipusExp);
    }
	
	// ---------------------------------------------------
	// E N U M E R A C I O N S
	// ---------------------------------------------------
	
	@Test
	public void i1_creaEnumeracio() {
		
		carregarUrlDisseny();
		
		seleccionarEntorn(titolEntorn);
		
		existeixElementAssert("//li[@id='menuDisseny']", "No te permisos de disseny");
		
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/enumeracio/llistat.html')]")));
		actions.click();
		actions.build().perform();
		
		noExisteixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'enumsel')]", "entorns/enumeracio/crear/1_enumeracionsActuals.png", "La enumeració ja existeix");
		
		driver.findElement(By.xpath("//div[@id='content']/form/button[@class='submitButton']")).click();
		driver.findElement(By.id("codi0")).sendKeys("enumsel");
		driver.findElement(By.id("nom0")).sendKeys("Enumerat selenium");
  	    driver.findElement(By.xpath("//button[@value='submit']")).click();
  	    existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'enumsel')]", "entorns/enumeracio/crear/2_enumeracionCreada.png", "No s'ha pogut crear la enumeració");
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'enumsel')]/td[3]/form/button")).click();
		actions.build().perform();
		driver.findElement(By.id("codi0")).sendKeys("A");
		driver.findElement(By.id("nom0")).sendKeys("Tipus A");
		driver.findElement(By.xpath("//button[@value='submit']")).click();
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'A')]", "No s'han pogut crear els elements de la enumeració");
		
		driver.findElement(By.id("codi0")).sendKeys("B");
		driver.findElement(By.id("nom0")).sendKeys("Tipus B");
		driver.findElement(By.xpath("//button[@value='submit']")).click();
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'B')]", "entorns/enumeracio/crear/3_enumeracionValos.png", "No s'han pogut crear els elements de la enumeració");
		
		driver.findElement(By.xpath("//button[@value='cancel']")).click();
	}
	
	//@Test
	
	@Test
	public void i2_eliminaEnumeracio() {
		
		carregarUrlDisseny();
		
		seleccionarEntorn(titolEntorn);
		
		existeixElementAssert("//li[@id='menuDisseny']", "No te permisos de disseny");
		
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/enumeracio/llistat.html')]")));
		actions.click();
		actions.build().perform();
		
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'enumsel')]", "No existeix l'enumeració a eliminar");
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'enumsel')]/td[4]/a")).click();
		acceptarAlerta();
		noExisteixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'enumsel')]", "No s'han pogut eliminar l'enumeració");
	}


	// ---------------------------------------------------
	// D O M I N I S
	// ---------------------------------------------------

	@Test
	public void j1_crearDominisEntorn() {
		
		carregarUrlDisseny();
		
		seleccionarEntorn(titolEntorn);
		
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/domini/llistat.html')]")));
		actions.click();
		actions.build().perform();
		
		screenshotHelper.saveScreenshot("entorns/domini/j1_1_creacioDominis.png");
		
		driver.findElement(By.xpath(botoNouDomini)).click();
		
		emplenaDadesDominiSQL("sqlDomId", "Nom consulta SQL", "0", "Descripció consulta SQL", "java:/comp/env/jdbc/HeliumDS", "select distinct enum.codi CODI, enum.nom DESCRIPCIO from hel_enumeracio enum");
		
		screenshotHelper.saveScreenshot("entorns/domini/j1_2_creacioDominis_dades.png");
		
		driver.findElement(By.xpath(botoGuardaDomini)).click();
		
		screenshotHelper.saveScreenshot("entorns/domini/j1_3_creacioDominis_resultat.png");
		
		existeixElementAssert("//*[@class='missatgesOk']", "No s'ha pogut crear el domini SQL sqlDomId a l'entorn "+titolEntorn+".");
		
		driver.findElement(By.xpath(botoNouDomini)).click();
		
		emplenaDadesDominiWS("wsDomId", "Nom consulta WS", "0", "Descripció consulta WS", "http://localhost:8080/helium/ws/DominiIntern", "NONE", "ATRIBUTS", "", "");
		
		screenshotHelper.saveScreenshot("entorns/domini/j1_4_creacioDominis_dades.png");
		
		driver.findElement(By.xpath(botoGuardaDomini)).click();
		
		screenshotHelper.saveScreenshot("entorns/domini/j1_5_creacioDominis_resultat.png");
		
		existeixElementAssert("//*[@class='missatgesOk']", "No s'ha pogut crear el domini WS wsDomId per l'entorn "+titolEntorn+".");
	}
	
	@Test
	public void j2_provarDominisEntorn() {
		
		carregarUrlDisseny();
		
		seleccionarEntorn(titolEntorn);
		
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/domini/llistat.html')]")));
		actions.click();
		actions.build().perform();
		
		screenshotHelper.saveScreenshot("entorns/domini/j2_1_provantDominis.png");
		
		// -- Prova Domini WS -- //
		
		driver.findElement(By.xpath(botoProvarDominiWS)).click();
		driver.findElement(By.xpath("//*[@id='dialog-form-WS']/form/div[2]/img")).click();
		
		driver.findElement(By.id("codiDomini")).sendKeys("PERSONA_AMB_CODI");
		driver.findElement(By.id("codi_0")).sendKeys("persona");
		for (WebElement option : driver.findElement(By.id("tipusParam_0")).findElements(By.tagName("option"))) {
			if ("string".equals(option.getText())) {
				option.click();
				break;
			}
		}
		
		screenshotHelper.saveScreenshot("entorns/domini/j2_3_provantDominis.png");
		
		driver.findElement(By.id("par_0")).sendKeys(usuari);	
		
		screenshotHelper.saveScreenshot("entorns/domini/j2_4_provantDominis.png");
		
		driver.findElement(By.xpath("/html/body/div[9]/div[11]/button[1]")).click();
		
		//Si intentes capturar pantalla amb un alert obert peta ¿?¿??
		//screenshotHelper.saveScreenshot("entorns/domini/3_provantDominis.png");
		
		if (isAlertPresent()) {			
			boolean condicioProvaSQLok = getTexteAlerta().startsWith("[") && getTexteAlerta().endsWith("],"); 
			assertTrue("El missatge retornat per el boto de prova no es l´esperat", condicioProvaSQLok);
			acceptarAlerta();
		}else{
			fail("No ha aparegut alert al pulsar el boto de provar domini.");
		}
		
		// -- Prova Domini SQL -- //
		
		screenshotHelper.saveScreenshot("entorns/domini/j2_3_provantDominis.png");
		
		driver.findElement(By.xpath(botoProvarDominiSQL)).click();

		//screenshotHelper.saveScreenshot("entorns/domini/5_provantDominis.png");
		
		if (isAlertPresent()) {			
			boolean condicioProvaSQLok = getTexteAlerta().startsWith("[") && getTexteAlerta().endsWith("],"); 
			assertTrue("El missatge retornat per el boto de prova no es l´esperat", condicioProvaSQLok);
			acceptarAlerta();
		}else{
			fail("No ha aparegut alert al pulsar el boto de provar domini.");
		}
	}
	
	@Test
	public void j3_eliminarDominisEntorn() {
		
		carregarUrlDisseny();
		
		seleccionarEntorn(titolEntorn);
		
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/domini/llistat.html')]")));
		actions.click();
		actions.build().perform();
		
		screenshotHelper.saveScreenshot("entorns/domini/j3_1_eliminarDominis.png");
		
		while (existeixElement(enllaçElimDomini)) {
			driver.findElement(By.xpath(enllaçElimDomini)).click();
			if (isAlertPresent()) {acceptarAlerta();}
			existeixElementAssert("//*[@class='missatgesOk']", "Error al esborrar un domini a l'entorn "+titolEntorn+".");
		}
		
		screenshotHelper.saveScreenshot("entorns/domini/j3_2_eliminarDominis_resultat.png");
	}
	
	// -------------- Fi dominis Entorn --------------------
	
	@Test
	public void k_desassignarPermisosUsuari() {
		
		carregarUrlConfiguracio();
		
		existeixElementAssert("//li[@id='menuConfiguracio']", "No te permisos de configuració a Helium");
		
		eliminaPermisos("borrar/usuari", usuari);
	}
	
	@Test
	public void l_desassignarPermisosRol() {
		carregarUrlConfiguracio();
		existeixElementAssert("//li[@id='menuConfiguracio']", "No te permisos de configuració a Helium");
		
		eliminaPermisos("borrar/rol", rol);
	}

	@Test
	public void m_esborrarEntorns() {
		
		carregarUrlConfiguracio();
		
		existeixElementAssert("//li[@id='menuConfiguracio']", "No te permisos de configuració a Helium");
		
		assignarPermisosEntorn(entorn, usuari, "READ", "DESIGN", "ORGANIZATION", "ADMINISTRATION");
		
		// Anem a la pantalla de configuració d'entorns
		actions.moveToElement(driver.findElement(By.id("menuConfiguracio")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/entorn/llistat.html')]")));
		actions.click();
		actions.build().perform();
				
		screenshotHelper.saveScreenshot("entorns/borrar/1_entornsExistents.png");		

		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + entorn + "')]", "L'entorn de proves no existeix.");		

		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + entorn + "')]/td[6]/a")).click();
		
		acceptarAlerta();
		
		noExisteixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + entorn + "')]", "entorns/borrar/2_entornsExistents.png", "No s'ha pogut eliminar l'entorn");
		
		eliminarEntorn(entorn_aux);
	}
	
	// ---------------------------------------------------
	// FUNCIONS PRIVADES
	// ---------------------------------------------------
	
	private void assignarPermisos(String tipus, String usurol, boolean isUsuari){
		// Anem a la pantalla de configuració d'entorns
		actions.moveToElement(driver.findElement(By.id("menuConfiguracio")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/entorn/llistat.html')]")));
		actions.click();
		actions.build().perform();
		
		screenshotHelper.saveScreenshot("entorns/permisos/" + tipus + "/1_entornsExistents_01.png");
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + entorn + "')]", "L\'entorn de proves no existeix.");

		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + entorn + "')]/td[4]/form/button")).click();
		screenshotHelper.saveScreenshot("entorns/permisos/" + tipus + "/permisos_01.png");
		
		// Crear els permis READ
		updatePermisos(isUsuari, usurol, false, false, false, true, tipus, 3, new String[]{"+READ"}, true);
		// ORGANIZATION, DESIGN i READ
		updatePermisos(isUsuari, usurol, true, false, true, false, tipus, 4, new String[]{"+READ", "+DESIGN", "+ORGANIZATION"}, false);
		// ADMINISTRATION
		updatePermisos(isUsuari, usurol, false, true, false, false, tipus, 5, new String[]{"+ADMINISTRATION"}, true);
		// DESIGN i READ
		updatePermisos(isUsuari, usurol, false, false, true, true, tipus, 6, new String[]{"+DESIGN", "+READ"}, true);
		// TOTS
		updatePermisos(isUsuari, usurol, true, true, false, false, tipus, 7, new String[]{"+DESIGN", "+READ", "+ORGANIZATION", "+ADMINISTRATION"}, false);
	}
	
	private void updatePermisos(
			boolean isUser, 
			String userol,
			boolean organization,
			boolean administration,
			boolean design,
			boolean read,
			String tipus,
			int pos,
			String[] permisosEsperats,
			boolean borraActuals) {

		if (existeixElement("//*[@id='registre']/tbody/tr[contains(td[2],'" + userol + "')]") && borraActuals) {
			// eliminam els permisos actuals
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[2],'" + userol + "')]/td[4]/a")).click();
			acceptarAlerta();
			noExisteixElementAssert("//*[@id='registre']/tbody/tr[contains(td[2],'" + userol + "')]", 
					"entorns/permisos/" + tipus + "/2_permisos.png", "No s'han pogut eliminar els permisos");
		}
		
		driver.findElement(By.id("nom0")).sendKeys(userol);
		if (design)	
			driver.findElement(By.xpath("//input[@value='DESIGN']")).click();
		if (organization)	
			driver.findElement(By.xpath("//input[@value='ORGANIZATION']")).click();
		if (read)			
			driver.findElement(By.xpath("//input[@value='READ']")).click();
		if (administration)			
			driver.findElement(By.xpath("//input[@value='ADMINISTRATION']")).click();
		if (!isUser)
			driver.findElement(By.id("usuari0")).click();
		screenshotHelper.saveScreenshot("entorns/permisos/" + tipus + "/" + pos + "_permisos_afegir.png");
		driver.findElement(By.xpath("//button[@value='submit']")).click();
		
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[2],'" + userol + "')]", 
				"entorns/permisos/" + tipus + "/" + pos + "_permisos.png", "No s'han pogut assignar permisos");
		
		String[] permisos = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[2],'" + userol + "')]/td[3]")).getText().trim().split(" ");
		Arrays.sort(permisos);
		Arrays.sort(permisosEsperats);
		assertArrayEquals("No s'han assignar els permisos correctament", permisosEsperats, permisos);
	}
	
	private void eliminaPermisos(String tipus, String usurol) {
		// Anem a la pantalla de configuració d'entorns
		actions.moveToElement(driver.findElement(By.id("menuConfiguracio")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/entorn/llistat.html')]")));
		actions.click();
		actions.build().perform();
		
		screenshotHelper.saveScreenshot("entorns/permisos/" + tipus + "/1_entornsExistents_01.png");
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + entorn + "')]", "L'entorn de proves no existeix.");
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + entorn + "')]/td[4]/form/button")).click();
		screenshotHelper.saveScreenshot("entorns/permisos/" + tipus + "/2_permisos.png");
		
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[2],'" + usurol + "')]", usurol + " no té permisos per aquest entorn.");
		// eliminam els permisos actuals
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[2],'" + usurol + "')]/td[4]/a")).click();
		acceptarAlerta();
		screenshotHelper.saveScreenshot("entorns/permisos/" + tipus + "/3_permisos.png");
		
		noExisteixElementAssert("//*[@id='registre']/tbody/tr[contains(td[2],'" + usurol + "')]", "No s'han pogut eliminar els permisos");
	}

    private ExpectedCondition<WebElement> visibilityOfElementLocated(final By by) {
    	
        return new ExpectedCondition<WebElement>() {
        	
          public WebElement apply(WebDriver driver) {
        	  
        	  try {
        		  
		    	WebElement element = driver.findElement(by);
		    	
		        if (element!=null && element.isDisplayed()) {
		        	return element;
		        }else{
		        	return null;
		        }
        	  }catch (Exception ex) {
        		  return null;        		  
        	  }
          }
          
        };

      }
}