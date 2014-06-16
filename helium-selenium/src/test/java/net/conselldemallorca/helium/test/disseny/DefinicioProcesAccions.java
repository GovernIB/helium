package net.conselldemallorca.helium.test.disseny;

import net.conselldemallorca.helium.test.util.BaseTest;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DefinicioProcesAccions extends BaseTest {

	String entorn = carregarPropietat("defproc.entorn.nom", "Nom de l'entorn de proves no configurat al fitxer de properties");
	String titolEntorn = carregarPropietat("defproc.entorn.titol", "Titol de l'entorn de proves no configurat al fitxer de properties");
	String pathExportEntorn = carregarPropietat("defproc.export.entorn.arxiu.path", "Ruta de l'exportació de l'entorn de proves no configurat al fitxer de properties");
	String usuari = carregarPropietat("test.base.usuari.disseny", "Usuari disseny de l'entorn de proves no configurat al fitxer de properties");
	String usuariAdmin = carregarPropietat("test.base.usuari.configuracio", "Usuari configuracio de l'entorn de proves no configurat al fitxer de properties");
	String nomDefProc = carregarPropietat("defproc.deploy.definicio.proces.nom", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String pathDefProc = carregarPropietat("defproc.export.arxiu.path", "Ruta de l'exportació de la definició de procés de proves no configurat al fitxer de properties");
	String codTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.codi", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	String codAccio = carregarPropietat("defproc.accio.1.codi", "Codi de la acció de proves no configurat al fitxer de properties");
	String nomAccio = carregarPropietat("defproc.accio.1.nom", "Nom de la acció de proves no configurat al fitxer de properties");
	String handlerAccio = carregarPropietat("defproc.accio.1.handler", "Handler de la acció de proves no configurat al fitxer de properties");
	
	@Test
	public void a0_inicialitzacio() {
		carregarUrlConfiguracio();
		crearEntorn(entorn, titolEntorn);
		assignarPermisosEntorn(entorn, usuariAdmin, "DESIGN", "ORGANIZATION", "READ", "ADMINISTRATION");
		assignarPermisosEntorn(entorn, usuari, "DESIGN", "ORGANIZATION", "READ");
	}
	
	@Test
	public void a1_inicialitzacio() {
		carregarUrlDisseny();
		saveEntornActual();
		marcarEntornDefecte(titolEntorn);
		seleccionarEntorn(titolEntorn);
		desplegarDefPro(TipusDesplegament.EXPORTDEFPRC, nomDefProc, null, pathDefProc, null, false, false);
		seleccionarDefinicioProces(nomDefProc);
		importarDadesEntorn(entorn, pathExportEntorn);
	}
	
	@Test
	public void b_crearAccio() {
		carregarUrlDisseny();
		seleccionarDefinicioProces(nomDefProc);
		
		crearAccio(
				nomDefProc, 
				codAccio, 
				nomAccio, 
				handlerAccio,
				true, 
				true, 
				"ROL_ACCIO");
	}
	
	@Test
	public void c_modificarAccio() {
		carregarUrlDisseny();
		seleccionarDefinicioProces(nomDefProc);
		
		modificarAccio(
				nomDefProc, 
				codAccio, 
				"Accio modificar", 
				handlerAccio,
				false, 
				false, 
				"");
	}
	
	@Test
	public void h_eliminarAccio() {
		carregarUrlDisseny();
		seleccionarDefinicioProces(nomDefProc);
		
		driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/accioLlistat.html')]")).click();
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + codAccio + "')]", "defproces/accio/eliminar/" + codAccio + "/01_eliminar.png", "El termini a eliminar no existeix");
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codAccio + "')]/td[4]/a")).click();
		acceptarAlerta();
		noExisteixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + codAccio + "')]", "defproces/accio/eliminar/" + codAccio + "/02_eliminar.png", "El termini no s'ha pogut eliminar");
	}
	
	@Test
	public void z0_finalitzacio() {
		carregarUrlDisseny();
		eliminarDefinicioProces(nomDefProc);
		eliminarEnumeracio("enumsel");
		eliminarDomini("enumerat");
		eliminarTipusExpedient(codTipusExp);
		if (entornActual != null && !"".equals(entornActual)) 
			marcarEntornDefecte(entornActual);
	}
	
	@Test
	public void z1_finalitzacio() {
		carregarUrlConfiguracio();
		eliminarEntorn(entorn);
	}

	
	
	// Funcions ajuda
	// ----------------------------------------------------------------------------------------
	protected void crearAccio(
			String nomDefProc, 
			String codi, 
			String nom,
			String handler,
			Boolean publica, 
			Boolean oculta, 
			String rols) {
		
		publica = publica != null && publica;
		oculta = oculta != null && oculta;
				
		// Accedir a la fitxa dels terminis
		driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/accioLlistat.html')]")).click();
		noExisteixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + codi + "')]", "defproces/accio/crear/" + codi + "/01_crear.png", "El termini a crear ja existeix");

		driver.findElement(By.xpath("//div[@id='content']/form/button[@class='submitButton']")).click();
		driver.findElement(By.id("codi0")).clear();
		driver.findElement(By.id("codi0")).sendKeys(codi);
		driver.findElement(By.id("nom0")).clear();
		driver.findElement(By.id("nom0")).sendKeys(nom);
		driver.findElement(By.id("descripcio0")).clear();
		driver.findElement(By.id("descripcio0")).sendKeys("Descripció de acció: " + nom);
		
		if (handler != null) {
			driver.findElement(By.xpath("//*[@id='jbpmAction0']/option[@value='" + handler + "']")).click();
		}
		if (publica) driver.findElement(By.id("publica0")).click();
		if (oculta) driver.findElement(By.id("oculta0")).click();
		driver.findElement(By.id("rols0")).clear();
		driver.findElement(By.id("rols0")).sendKeys(rols);
		screenshotHelper.saveScreenshot("defproces/termini/crear/" + codi + "/02_crear.png");
			
		driver.findElement(By.xpath("//button[@value='submit']")).click();
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + codi + "') and contains(td[2], '" + nom +"') and contains(td[3],'" + handler + "')]", "No s'ha pogut crear el termini de test " + nom + "correctament");
		
		// Comprovar tots els valors introduits al termini
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codi + "')]/td[1]/a")).click();
		existeixElementAssert("//*[@id='descripcio0' and normalize-space(text())=\"Descripció de acció: " + nom + "\"]", "La descripció de la acció no s'ha gravat correctament");
		checkboxSelectedAssert("//*[@id='publica0']", "El paràmetre execució pública de la acció no s'ha gravat correctament", publica);
		checkboxSelectedAssert("//*[@id='oculta0']", "El paràmetre ocultar acció no s'ha gravat correctament", oculta);
		existeixElementAssert("//*[@id='rols0' and @value='" + rols + "']", "El paràmetre restringir rols no s'ha gravat correctament");
	}
	
	protected void modificarAccio(
			String nomDefProc, 
			String codi, 
			String nom,
			String handler,
			Boolean publica, 
			Boolean oculta, 
			String rols) {
		
		publica = publica != null && publica;
		oculta = oculta != null && oculta;
				
		// Accedir a la fitxa dels terminis
		driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/accioLlistat.html')]")).click();
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + codi + "')]", "defproces/accio/modificar/" + codi + "/01_modificar.png", "El termini a modificar no existeix");

		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codi + "')]/td[1]/a")).click();
		if (nom != null) {
			driver.findElement(By.id("nom0")).clear();
			driver.findElement(By.id("nom0")).sendKeys(nom);
			driver.findElement(By.id("descripcio0")).clear();
			driver.findElement(By.id("descripcio0")).sendKeys("Descripció de acció: " + nom);
		}
		if (handler != null) {
			driver.findElement(By.xpath("//*[@id='jbpmAction0']/option[@value='" + handler + "']")).click();
		}
		
		if (!checkboxSelected("//*[@id='publica0']", publica)) driver.findElement(By.id("publica0")).click();
		if (!checkboxSelected("//*[@id='oculta0']", oculta)) driver.findElement(By.id("oculta0")).click();
		if (rols != null) {
			driver.findElement(By.id("rols0")).clear();
			driver.findElement(By.id("rols0")).sendKeys(rols);
		}
		screenshotHelper.saveScreenshot("defproces/accio/modificar/" + codi + "/02_modificar.png");
			
		driver.findElement(By.xpath("//button[@value='submit']")).click();
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + codi + "') and contains(td[2], '" + nom +"') and contains(td[3],'" + handler + "')]", "No s'ha pogut crear el termini de test " + nom + "correctament");
		
		// Comprovar tots els valors introduits al termini
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codi + "')]/td[1]/a")).click();
		if (nom != null) {
			existeixElementAssert("//*[@id='nom0' and @value='" + nom + "']", "El nom de la acció no s'ha gravat correctament");
			existeixElementAssert("//*[@id='descripcio0' and normalize-space(text())=\"Descripció de acció: " + nom + "\"]", "La descripció de la acció no s'ha gravat correctament");
		}
		if (handler != null) existeixElementAssert("//*[@id='jbpmAction0']/option[@selected='selected' and @value='" + handler + "']", "el handler de la acció no s'ha gravat correctament");
		checkboxSelectedAssert("//*[@id='publica0']", "El paràmetre execució pública de la acció no s'ha gravat correctament", publica);
		checkboxSelectedAssert("//*[@id='oculta0']", "El paràmetre ocultar acció no s'ha gravat correctament", oculta);
		if (rols != null) existeixElementAssert("//*[@id='rols0' and @value='" + rols + "']", "El paràmetre restringir rols no s'ha gravat correctament");
	}
}