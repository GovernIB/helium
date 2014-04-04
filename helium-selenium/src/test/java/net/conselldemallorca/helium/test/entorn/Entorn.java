package net.conselldemallorca.helium.test.entorn;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.concurrent.TimeUnit;

import net.conselldemallorca.helium.test.util.BaseTest;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Entorn extends BaseTest {

	@Test
	public void a_createEntorn() {
		
		carregarUrlConfiguracio();
		
		// Comprovam que tenim permisos
		existeixElementAssert("//li[@id='menuConfiguracio']", 
						"No te permisos de configuració a Helium");
		
		// Anem a la pantalla de configuració d'entorns
		actions.moveToElement(driver.findElement(By.id("menuConfiguracio")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/entorn/llistat.html')]")));
		actions.click();
		actions.build().perform();
		
		screenshotHelper.saveScreenshot("entorns/crear/entornsExistents_01.png");
		
		String entorn = properties.getProperty("entorn.nom");
		noExisteixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + entorn + "')]", "L\'entorn a crear ja existeix.");
			
		// Entorn no existeix. Cream entorn
		driver.findElement(By.xpath("//div[@id='content']/form/button[@class='submitButton']")).click();
			
		driver.findElement(By.id("codi0")).clear();
		driver.findElement(By.id("codi0")).sendKeys(entorn);
		driver.findElement(By.id("nom0")).clear();
		driver.findElement(By.id("nom0")).sendKeys(entorn);
		
//		if (!actiu) 				
//			driver.findElement(By.id("actiu0")).click();
//		screenshotHelper.saveScreenshot("entorns/crear/entornCrear" + (actiu ? "Actiu" : "Inactiu") + "_01.png");
		
		screenshotHelper.saveScreenshot("entorns/crear/entornCrearActiu_01.png");
		driver.findElement(By.xpath("//button[@value='submit']")).click();

		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + entorn + "')]", "entorns/crear/entornCrearActiu_02.png", "No s'ha pogut crear l'entorn");

//		// Comprovam que tenim permisos
//		existeixElement("//a[@id='menuTaulesComuns']", 
//						"No te permisos per a modificar les taules comuns");
//		
//		// Anem a la pantalla de fons
//		driver.findElement(By.id("menuTaulesComuns")).click();
//		driver.findElement(By.id("submenuFons")).click();
//		
//		// Comprovam que ens trobam a la pàgina de fons
//		existeixElement("//table[@id='taulaFons']", 
//						"taulesComuns/fons/crear/00_llistatFonsInicial.png",
//						1000,
//						"No s'ha pogut accedir a la pàgina de Fons");
//		
//		// Comprovam que el Fons no existeix (Filtram primer la llista)
//		driver.findElement(By.id("nomUge_t")).sendKeys("FONS");
//		driver.findElement(By.id("taulaFons-filtreLink")).click();
//		noExisteixElement("//table[@id='taulaFons']/tbody/tr[contains(td[2],'FONS')]", 
//						"El fons a crear ja existeix");
//				
//		// Clicam al botó de nou fons
//		driver.findElement(By.id("taulaFons-ToolTables_taulaDades_new")).click();
//		
//		// Comprovam que s'ha obert la finestra modal
//		modalOberta("fons", "taulesComuns/fons/crear/01_modalNouFons.png");
//		
//		// Intentam crear un Fons sense introduir dades
//		driver.findElement(By.xpath("//button[@type='submit']")).click();
//		
//		// Passam a l'iframe
//		vesAModal("fons");
//		
//		// Comprovam que es mostren els errors en els dos camps
//		existeixElement("//span[@id='nomUge.errors']", 
//						"No s'ha generat error al deixar el camp nomUge buit");
//		existeixElement("//span[@id='nom.errors']", 
//						"taulesComuns/fons/crear/02_modalErrorCampsBuits.png", 
//						"No s'ha generat error al deixar el camp nom buit");
//		
//		// Intentam crear un Fons amb un nom UGE massa llarg
//		driver.findElement(By.id("nomUge")).sendKeys("ABCDEFGHIJKLMNOP");
//		
//		// Retornam
//		tornaAPare();
//		
//		// Guardam
//		driver.findElement(By.xpath("//button[@type='submit']")).click();
//		
//		// Passam a l'iframe
//		vesAModal("fons");
//		
//		// Comprovam que es mostren els errors en els dos camps
//		existeixElement("//span[@id='nomUge.errors']", 
//						"taulesComuns/fons/crear/03_modalErrorCampLlarg.png", 
//						"No s'ha generat error al definir el camp nomUge amb un valor massa llarg");
//		
//		// Omplim les dades correctament per a crear un nou Fons
//		driver.findElement(By.id("nomUge")).clear();
//		driver.findElement(By.id("nomUge")).sendKeys("FONS");
//		driver.findElement(By.id("nom")).sendKeys("Nom FONS");
//		screenshotHelper.saveScreenshot("taulesComuns/fons/crear/04_modalCampsOK.png");
//		
//		// Retornam
//		tornaAPare();
//		
//		// Guardam
//		driver.findElement(By.xpath("//button[@type='submit']")).click();
//		
//		// Filtram la llista
//		driver.findElement(By.id("nomUge_t")).sendKeys("FONS");
//		driver.findElement(By.id("taulaFons-filtreLink")).click();
//		
//		// Comprovam que el Fons s'ha guardat (es troba a la llista de Fons)
//		existeixElement("//table[@id='taulaFons']/tbody/tr[contains(td[2],'FONS') and contains(td[3], 'Nom FONS')]", 
//				"taulesComuns/fons/crear/05_llistaFonsCreat.png", 
//				"No s'ha creat el nou Fons");
	}
	
	@Test
	public void b_updateEntorn() {
		fail("Not yet implemented");
//		carregarUrlConfiguracio();
//		
//		existeixElement("//a[@id='menuTaulesComuns']", 
//				"No te permisos per a modificar les taules comuns");
//		
//		driver.findElement(By.id("menuTaulesComuns")).click();
//		driver.findElement(By.id("submenuFons")).click();
//		
//		// Comprovam que ens trobam a la pàgina de fons
//		existeixElement("//table[@id='taulaFons']", 
//						"taulesComuns/fons/modificar/00_llistatFonsInicial.png",
//						1000,
//						"No s'ha pogut accedir a la pàgina de Fons");
//		
//		// Comprovam que el Fons existeix (es troba a la llista de Fons)
//		existeixElement("//table[@id='taulaFons']/tbody/tr[contains(td[2],'FONS')]", 
//				"taulesComuns/fons/modificar/01_llistaFonsModificar.png", 
//				"No s'ha trobar el Fons a editar");
//		driver.findElement(By.xpath("//table[@id='taulaFons']/tbody/tr[contains(td[2],'FONS')]/td[1]/a")).click();
//		
//		acceptarAlerta();
//		
//		// Comprovam que s'ha obert la finestra modal
//		modalOberta("fons", "taulesComuns/fons/modificar/02_modalModificarFons.png");
//
//		// Intentam crear un Fons amb un nom massa llarg
//		vesAModal("fons");
//		driver.findElement(By.id("nom")).clear();
//		driver.findElement(By.id("nom")).sendKeys("ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz");
//		tornaAPare();
//		
//		// Guardam
//		driver.findElement(By.xpath("//button[@type='submit']")).click();
//		
//		// Indicam un nom de fons correcte
//		vesAModal("fons");
//		driver.findElement(By.id("nom")).sendKeys("Fons modificat");
//		tornaAPare();
//		driver.findElement(By.xpath("//button[@type='submit']")).click();
//		
//		// Filtram la llista
//		driver.findElement(By.id("nomUge_t")).sendKeys("FONS");
//		driver.findElement(By.id("taulaFons-filtreLink")).click();
//		
//		// Comprovam que el Fons s'ha guardat (es troba a la llista de Fons)
//		existeixElement("//table[@id='taulaFons']/tbody/tr[contains(td[2],'FONS') and contains(td[3], 'Fons modificat')]", 
//				"taulesComuns/fons/modificar/03_llistaFonsModificat.png", 
//				"No s'ha modificat el nou Fons");
	}
	
	@Test
	public void c_deleteFons() {
		fail("Not yet implemented");
	}

}
