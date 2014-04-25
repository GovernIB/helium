package net.conselldemallorca.helium.test.disseny;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;

import net.conselldemallorca.helium.test.util.BaseTest;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Entorn extends BaseTest {

	String entorn = carregarPropietat("entorn.nom", "Nom de l'entorn de proves no configurat al fitxer de properties");
	String titolEntorn = carregarPropietat("entorn.titol", "Titol de l'entorn de proves no configurat al fitxer de properties");
	String usuari = carregarPropietat("test.base.usuari.configuracio", "Usuari configuració de l'entorn de proves no configurat al fitxer de properties");
	String rol = carregarPropietat("test.base.rol.configuracio", "Rol configuració de l'entorn de proves no configurat al fitxer de properties");
	
//	@Test
//	public void a_inicialitzacio() {
//
//	}

	@Test
	public void b_createEntorn() {
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
		driver.findElement(By.id("nom0")).sendKeys(entorn);
		
		screenshotHelper.saveScreenshot("entorns/crear/1_entornCrearActiu_01.png");
		driver.findElement(By.xpath("//button[@value='submit']")).click();

		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + entorn + "')]", "entorns/crear/3_entornCrearActiu_02.png", "No s'ha pogut crear l'entorn");

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
		driver.findElement(By.id("nom0")).sendKeys(titolEntorn);

		screenshotHelper.saveScreenshot("entorns/titol/3_canviTitol.png");
		driver.findElement(By.xpath("//button[@value='submit']")).click();

		screenshotHelper.saveScreenshot("entorns/titol/4_entornsExistents_02.png");
		String nouTitol = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + entorn + "')]/td[2]")).getText().trim();
		assertNotEquals("No s'ha pogut canviar el titol de l'entorn de test", titol, nouTitol);
	}
	
	@Test
	public void e_assignarPermisosUsuari() {
		carregarUrlConfiguracio();
		existeixElementAssert("//li[@id='menuConfiguracio']", "No te permisos de configuració a Helium");
		
		assignarPermisos("crear/usuari", usuari, true);
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
		
		String entornActual = driver.findElement(By.xpath("//div[@id='page-entorn-title']/h2/span")).getText().trim();
		existeixElementAssert("//li[@id='menuEntorn']/ul[@class='llista-entorns']/li[contains(., '" + titolEntorn + "')]", 
				"entorns/seleccionar/directe/1_entornActual.png", "L'entorn de proves no existeix.");
			
		// Selecció directe
		actions.moveToElement(driver.findElement(By.id("menuEntorn")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//li[@id='menuEntorn']/ul[@class='llista-entorns']/li[contains(., '" + entorn + "')]/a")));
		actions.click();
		actions.build().perform();
	
		screenshotHelper.saveScreenshot("entorns/seleccionar/directe/1_entornActiu.png");
		assertEquals("No s'ha pogut seleccionar l'entorn de forma directe.", titolEntorn, driver.findElement(By.xpath("//div[@id='page-entorn-title']/h2/span")).getText().trim());
		
		// Tornem a l'entorn anterior
		actions.moveToElement(driver.findElement(By.id("menuEntorn")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//li[@id='menuEntorn']/ul[@class='llista-entorns']/li[contains(., '" + entornActual + "')]/a")));
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
		
		// Tornem a l'entorn anterior
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
		
		String entornActual = driver.findElement(By.xpath("//div[@id='page-entorn-title']/h2/span")).getText().trim();
		existeixElementAssert("//li[@id='menuEntorn']/ul[@class='llista-entorns']/li[contains(., '" + entorn + "')]", 
				"entorns/default/1_entornActual.png", "L'entorn de proves no existeix.");
		
		driver.findElement(By.id("menuEntorn")).findElement(By.tagName("a")).click();
		String src = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[2],'" + entorn + "')]/td[1]/a/img")).getAttribute("src");
		
		if (src.endsWith("star.png")) {
			fail("L'entorn ja està marcat per defecte");
		} else {
			screenshotHelper.saveScreenshot("entorns/default/2_entornDefecte.png");
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[2],'" + entorn + "')]/td[1]/a")).click();
			
			driver.findElement(By.id("menuEntorn")).findElement(By.tagName("a")).click();
			screenshotHelper.saveScreenshot("entorns/default/3_entornDefecte.png");
			
			src = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[2],'" + entorn + "')]/td[1]/a/img")).getAttribute("src");
			assertTrue("No s'ha pogut marcar l'entorn per defecte.", src.endsWith("star.png"));
			
			driver.findElement(By.id("menuEntorn")).findElement(By.tagName("a")).click();
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[2],'" + entornActual + "')]/td[1]/a")).click();
		}
	}
	
	@Test
	public void i_creaEnumeracio() {
		carregarUrlDisseny();
		
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
	
	@Test
	public void j_eliminaEnumeracio() {
		carregarUrlDisseny();
		
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
	public void m_esborrarEntorn() {
		carregarUrlConfiguracio();
		existeixElementAssert("//li[@id='menuConfiguracio']", "No te permisos de configuració a Helium");
		
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
		
		noExisteixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + entorn + "')]", 
				"entorns/borrar/2_entornsExistents.png", "No s'ha pogut eliminar l'entorn");	
	}
	
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

}
