package net.conselldemallorca.helium.test.tramitacio;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;

import net.conselldemallorca.helium.test.util.BaseTest;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ReindexarAturarExpedient extends BaseTest {
	
							//EXP.12 - Reindexar expedient
							//EXP.13 - Aturar tramitacio expedient
	
	String entorn 		= carregarPropietat("tramitacio.expedient.reindexar.entorn.nom", "Nom de l'entorn de proves no configurat al fitxer de properties");
	String titolEntorn	= carregarPropietat("tramitacio.expedient.reindexar.entorn.titol", "Titol de l'entorn de proves no configurat al fitxer de properties");
	
	String usuari 		= carregarPropietat("test.base.usuari.disseny", "Usuari feina de l'entorn de proves no configurat al fitxer de properties");
	String usuariAdmin  = carregarPropietat("test.base.usuari.configuracio", "Usuari configuració de l'entorn de proves no configurat al fitxer de properties");
	
	String nomTipusExp	= carregarPropietat("tramitacio.expedient.reindexar.tipus.expedient.nom", "Nom del tipus d'expedient de proves no configurat al fitxer de properties");
	String codTipusExp	= carregarPropietat("tramitacio.expedient.reindexar.tipus.expedient.codi", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	String tipusExpPath	= carregarPropietatPath("tramitacio.expedient.reindexar.tipus.expedient.path", "Path del tipus d'expedient de proves no configurat al fitxer de properties");
	
	//Llista de expedients que es crearàn > Reindexarán > Aturaràn i borraran
	static List<String[]> expedientes = new ArrayList<String[]>();
	int numExpPrueba = 5;
	
	// XPATHS
	String pestanyaForms	= "//*[@id='tabnav']/li/a[contains(@href, '/expedientTipus/formext.html')]";
	String botoGuardarCanvis	= "//*[@id='command']//div[@class='buttonHolder']/button[text() = 'Guardar']";
	
	String linkBorrarExpedient = "//*[@id='registre']/tbody/tr[1]/td/a[contains(@href, '/expedient/delete.html')]";
	
	
	
	@Test
	public void a1_inicialitzacio() {
		
		carregarUrlConfiguracio();
		crearEntorn(entorn, titolEntorn);
		assignarPermisosEntorn(entorn, usuariAdmin, "DESIGN", "ORGANIZATION", "READ", "ADMINISTRATION");
		assignarPermisosEntorn(entorn, usuari, "DESIGN", "ORGANIZATION", "READ", "ADMINISTRATION");
		marcarEntornDefecte(titolEntorn);
		seleccionarEntorn(titolEntorn);
		
        crearTipusExpedient(nomTipusExp, codTipusExp);
        assignarPermisosTipusExpedient(codTipusExp, usuariAdmin, "CREATE", "MANAGE", "WRITE", "DELETE", "ADMINISTRATION", "DESIGN", "SUPERVISION", "READ");
        assignarPermisosTipusExpedient(codTipusExp, usuari, "CREATE", "MANAGE", "WRITE", "DELETE", "ADMINISTRATION", "DESIGN", "SUPERVISION", "READ");
        importarDadesTipExp(codTipusExp, tipusExpPath);	
	}
	
	@Test
	public void b1_reindexar_expedient() {
		
        carregarUrlDisseny();
        
        seleccionarEntorn(titolEntorn);
        
        // Iniciamos n expedientes con la última versión
        for (int i = 0; i < numExpPrueba; i++) {
            String[] expediente = iniciarExpediente(codTipusExp,"SE-"+i+"/2014", "Expedient de prova Selenium " + (new Date()).getTime() );
            screenshotHelper.saveScreenshot("tramitar/reindexar_aturar/b1_1_"+(i+1)+"_expedientcreat.png");
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
            driver.findElement(By.xpath("//*[@id='content']/div/h3[contains(a/text(), \"Execució d'scripts\")]/a")).click();
            
            screenshotHelper.saveScreenshot("entorns/reindexar/3_"+contadorExpedientsScript+"_2_execucioScriptsExpedient.png");
            
            driver.findElement(By.xpath("//*[@id='script0']")).sendKeys(script);
            
            screenshotHelper.saveScreenshot("entorns/reindexar/3_"+contadorExpedientsScript+"_3_acceptarScript.png");
            
            driver.findElement(By.xpath("//button[contains(text(), 'Executar')]")).click();
            if (isAlertPresent()) { acceptarAlerta(); }
            existeixElementAssert("//*[@id='infos']/p", "No se ejecutó el script correctamente del expediente: " + expediente);
            
            screenshotHelper.saveScreenshot("entorns/reindexar/3_"+contadorExpedientsScript+"_4_resultatScript.png");
            
            contadorExpedientsScript++;
        }
        
        //Comprobamos que no aparecen expedientes en la consulta
        //consultaExpedients();
        
        //Reindexam desde cada expedient
        contadorExpedientsScript=1;
        for (int e=0; e<numExpPrueba; e++) {

        	consultaExpedients();

            screenshotHelper.saveScreenshot("tramitar/reindexar_aturar/b1_2_"+contadorExpedientsScript+"_1_llistaExpedients.png");

            String xPathExpActual = "//*[@id='registre']/tbody/tr["+Integer.toString(e+1)+"]/td/a[contains(@href,'/expedient/info.html')]";
            
            //driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+(e+1)+"]/td/a[contains(@href,'/expedient/info.html')]")).click();
            //driver.findElement(By.xpath("//*[@id='registre']//a[contains(@href,'/expedient/info.html')]["+(e+1)+"]")).click();
            driver.findElement(By.xpath(xPathExpActual)).click();
            driver.findElement(By.xpath("//*[@id='tabnav']//a[contains(@href,'/expedient/eines.html')]")).click();
            driver.findElement(By.xpath("//*[@id='content']/h3[contains(a/text(), \"Reindexar l'expedient\")]/a")).click();

            if (isAlertPresent()) { acceptarAlerta(); }

            existeixElementAssert("//*[@id='infos']/p", "No se reindexó correctamente el expediente.");

            screenshotHelper.saveScreenshot("tramitar/reindexar_aturar/b1_2_"+contadorExpedientsScript+"_2_expedient_reindexat.png");

            contadorExpedientsScript++;
        }

        consultaExpedients();
        
        screenshotHelper.saveScreenshot("tramitar/reindexar_aturar/b1_3_llistaExpedientsDespresIndexar.png");
        
        //Comprovam que els expedients tornen a apareixer
        for (String[] expediente : expedientes) {
            existeixElementAssert("//td[contains(a/text(),'"+expediente[0]+"')]", "No se encontró el expediente: " + expediente[0]);            
        }
	}
	
	@Test
	public void c1_aturar_tramitacio_expedient() {
		
        carregarUrlDisseny();
        
        seleccionarEntorn(titolEntorn);
        
        consultaExpedients();
        
        screenshotHelper.saveScreenshot("tramitar/reindexar_aturar/c1_1_llistaExpedients.png");
        
        int contadorExpedientsScript=1;
        for (int e=0; e<numExpPrueba; e++) {
            
        	String xPathExpActual = "//*[@id='registre']/tbody/tr["+Integer.toString(e+1)+"]/td/a[contains(@href,'/expedient/info.html')]";
        	
            //Executam l´script per aquest expedient
            driver.findElement(By.xpath(xPathExpActual)).click();
            
            driver.findElement(By.xpath("//*[@id='tabnav']//a[contains(@href,'/expedient/eines.html')]")).click();
            
            screenshotHelper.saveScreenshot("tramitar/reindexar_aturar/c1_2_"+contadorExpedientsScript+"_1_eines_expedient.png");
            
            driver.findElement(By.xpath("//*[@id='content']/div/h3[contains(a/text(), \"Aturar la tram\")]/a")).click();
            
            driver.findElement(By.id("motiu0")).sendKeys("Motiu aturada "+contadorExpedientsScript);
            
            screenshotHelper.saveScreenshot("tramitar/reindexar_aturar/c1_2_"+contadorExpedientsScript+"_2_motiu_aturada.png");
            
            driver.findElement(By.xpath("//*[@id='aturarCommand']/div[@class='buttonHolder']/button[text() = 'Aturar']")).click();
            
            if (isAlertPresent()) { acceptarAlerta(); }
            
            existeixElementAssert("//*[@id='infos']/p", "No se paró correctamente el expediente.");
            
            existeixElementAssert("//*[@id='content']/div[@class='missatgesAturat']", "No se paró correctamente el expediente.");
            
            screenshotHelper.saveScreenshot("tramitar/reindexar_aturar/c1_2_"+contadorExpedientsScript+"_3_expedient_aturat.png");
            
            contadorExpedientsScript++;
            
            consultaExpedients();
        }
	}
	
	@Test
	public void z0_finalitzacio() {
		
		carregarUrlConfiguracio();

		consultaExpedients();
		
        while (existeixElement(linkBorrarExpedient)) {
        	driver.findElement(By.xpath(linkBorrarExpedient)).click();
        	if (isAlertPresent()) { acceptarAlerta(); }
        	consultaExpedients();
        }
		
		eliminarTipusExpedient(codTipusExp);
		
		eliminarEntorn(entorn);
	}
	
	// - - - - - - - - - - - - - - - - - - - - -
	// F U N C I O N S   P R I V A D E S
	// - - - - - - - - - - - - - - - - - - - - -
	
	private void consultaExpedients() {
		
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
	}

}