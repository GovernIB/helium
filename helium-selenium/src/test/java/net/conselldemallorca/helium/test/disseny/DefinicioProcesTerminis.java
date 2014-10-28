package net.conselldemallorca.helium.test.disseny;

import net.conselldemallorca.helium.test.util.BaseTest;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DefinicioProcesTerminis extends BaseTest {

	String entorn = carregarPropietat("defproc.entorn.nom", "Nom de l'entorn de proves no configurat al fitxer de properties");
	String titolEntorn = carregarPropietat("defproc.entorn.titol", "Titol de l'entorn de proves no configurat al fitxer de properties");
	String pathExportEntorn = carregarPropietatPath("defproc.export.entorn.arxiu.path", "Ruta de l'exportació de l'entorn de proves no configurat al fitxer de properties");
	String usuari = carregarPropietat("test.base.usuari.disseny", "Usuari disseny de l'entorn de proves no configurat al fitxer de properties");
	String usuariAdmin = carregarPropietat("test.base.usuari.configuracio", "Usuari configuracio de l'entorn de proves no configurat al fitxer de properties");
	String nomDefProc = carregarPropietat("defproc.deploy.definicio.proces.nom", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String pathDefProc = carregarPropietatPath("defproc.export.arxiu.path", "Ruta de l'exportació de la definició de procés de proves no configurat al fitxer de properties");
	String codTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.codi", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	String codTermini = carregarPropietat("defproc.termini.1.codi", "Codi del termini de proves no configurat al fitxer de properties");
	String nomTermini = carregarPropietat("defproc.termini.1.nom", "Nom del termini de proves no configurat al fitxer de properties");
	
	@Test
	public void a0_inicialitzacio() {
		carregarUrlConfiguracio();
		crearEntorn(entorn, titolEntorn);
		assignarPermisosEntorn(entorn, usuariAdmin, "DESIGN", "ORGANIZATION", "READ", "ADMINISTRATION");
		assignarPermisosEntorn(entorn, usuari, 		"DESIGN", "ORGANIZATION", "READ", "ADMINISTRATION");
	}
	
	@Test
	public void a1_inicialitzacio() {
		carregarUrlConfiguracio();
		marcarEntornDefecte(titolEntorn);
		seleccionarEntorn(titolEntorn);
		desplegarDefPro(TipusDesplegament.EXPORTDEFPRC, nomDefProc, null, pathDefProc, null, false, false);
		seleccionarDefinicioProces(nomDefProc);
		importarDadesEntorn(entorn, pathExportEntorn);
	}
	
	@Test
	public void b_crearTermini() {
		carregarUrlDisseny();
		seleccionarDefinicioProces(nomDefProc);
		
		crearTermini(
				nomDefProc, 
				codTermini, 
				nomTermini, 
				true, 
				0, 
				0, 
				10, 
				true, 
				true, 
				2, 
				true, 
				true, 
				false);
	}
	
	@Test
	public void c_modificarTermini() {
		carregarUrlDisseny();
		seleccionarDefinicioProces(nomDefProc);
		
		modificarTermini(
				nomDefProc, 
				codTermini, 
				"Termini modificar", 
				true, 
				0, 
				1, 
				4, 
				false, 
				false, 
				6, 
				false, 
				true, 
				true);
	}
	
	@Test
	public void h_eliminarTermini() {
		carregarUrlDisseny();
		seleccionarDefinicioProces(nomDefProc);
		
		driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/terminiLlistat.html')]")).click();
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + codTermini + "')]", "defproces/termini/eliminar/" + codTermini + "/01_eliminar.png", "El termini a eliminar no existeix");
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codTermini + "')]/td[4]/a")).click();
		acceptarAlerta();
		existeixElementAssert("//*[@id='infos']/p", "No se borro el termini correctamente");
	}
	
	@Test
	public void z0_finalitzacio() {
		carregarUrlDisseny();
		eliminarDefinicioProces(nomDefProc);
		eliminarEnumeracio("enumsel");
		eliminarDomini("enumerat");
		eliminarTipusExpedient(codTipusExp);
	}
	
	@Test
	public void z1_finalitzacio() {
		carregarUrlConfiguracio();
		eliminarEntorn(entorn);
	}

	
	
	// Funcions ajuda
	// ----------------------------------------------------------------------------------------
	protected void crearTermini(
			String nomDefProc, 
			String codi, 
			String nom, 
			Boolean duradaPredefinida, 
			Integer anys, 
			Integer mesos, 
			Integer dies, 
			Boolean laborables, 
			Boolean contolManual,
			Integer diesAlerta,
			Boolean alertaPrevia,
			Boolean alertaFinal,
			Boolean alertaTasca) {
		
		duradaPredefinida = duradaPredefinida != null && duradaPredefinida;
		laborables = laborables != null && laborables;
		contolManual = contolManual == null || contolManual;
		alertaPrevia = alertaPrevia != null && alertaPrevia;
		alertaFinal = alertaFinal != null && alertaFinal;
		alertaTasca = alertaTasca != null && alertaTasca;
				
		// Accedir a la fitxa dels terminis
		driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/terminiLlistat.html')]")).click();
		noExisteixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + codi + "')]", "defproces/termini/crear/" + codi + "/01_crear.png", "El termini a crear ja existeix");

		driver.findElement(By.xpath("//div[@id='content']/form/button[@class='submitButton']")).click();
		driver.findElement(By.id("codi0")).clear();
		driver.findElement(By.id("codi0")).sendKeys(codi);
		driver.findElement(By.id("nom0")).clear();
		driver.findElement(By.id("nom0")).sendKeys(nom);
		driver.findElement(By.id("descripcio0")).clear();
		driver.findElement(By.id("descripcio0")).sendKeys("Descripció de termini: " + nom);
		
		if (duradaPredefinida) {
			driver.findElement(By.id("duradaPredefinida0")).click();
			if (anys != null) driver.findElement(By.xpath("//*[@id='anys']/option[@value='" + anys + "']")).click();
			if (mesos != null) driver.findElement(By.xpath("//*[@id='mesos']/option[@value='" + mesos + "']")).click();
			if (dies != null) {
				driver.findElement(By.id("dies")).clear();
				driver.findElement(By.id("dies")).sendKeys(dies.toString());
			};
		}
		if (laborables) driver.findElement(By.id("laborable0")).click();
		if (!contolManual) driver.findElement(By.id("manual0")).click();
		if (diesAlerta != null) {
			driver.findElement(By.id("diesPrevisAvis0")).clear();
			driver.findElement(By.id("diesPrevisAvis0")).sendKeys(diesAlerta.toString());
		};
		if (alertaPrevia) driver.findElement(By.id("alertaPrevia0")).click();
		if (alertaFinal) driver.findElement(By.id("alertaFinal0")).click();
		if (alertaTasca) driver.findElement(By.id("alertaCompletat0")).click();
		screenshotHelper.saveScreenshot("defproces/termini/crear/" + codi + "/02_crear.png");
			
		driver.findElement(By.xpath("//button[@value='submit']")).click();
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + codi + "') and contains(td[2], '" + nom +"')]", "No s'ha pogut crear el termini de test " + nom);
		
		// Comprovar tots els valors introduits al termini
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codi + "')]/td[1]/a")).click();
		
		existeixElementAssert("//*[@id='descripcio0' and normalize-space(text())=\"Descripció de termini: " + nom + "\"]", "La descripció del termini no s'ha gravat correctament");
		checkboxSelectedAssert("//*[@id='duradaPredefinida0']", "El paràmetre durada predefinida del termini no s'ha gravat correctament", duradaPredefinida);
		if (duradaPredefinida) {
			if (anys != null) existeixElementAssert("//*[@id='anys']/option[@selected='selected' and @value='" + anys + "']", "els anys del termini no s'ha gravat correctament");
			if (mesos != null) existeixElementAssert("//*[@id='mesos']/option[@selected='selected' and @value='" + mesos + "']", "els mesos del termini no s'ha gravat correctament");
			if (dies != null) existeixElementAssert("//*[@id='dies' and @value='" + dies + "']", "els dies del termini no s'ha gravat correctament");
		}
		checkboxSelectedAssert("//*[@id='laborable0']", "El paràmetre dies laborables del termini no s'ha gravat correctament", laborables);
		checkboxSelectedAssert("//*[@id='manual0']", "El paràmetre permetre control manual del termini no s'ha gravat correctament", contolManual);
		existeixElementAssert("//*[@id='diesPrevisAvis0' and @value='" + diesAlerta + "']", "Els dies d'alerta del termini no s'ha gravat correctament");
		checkboxSelectedAssert("//*[@id='alertaPrevia0']", "El paràmetre generar alerta prèvia del termini no s'ha gravat correctament", alertaPrevia);
		checkboxSelectedAssert("//*[@id='alertaFinal0']", "El paràmetre generar alerta final del termini no s'ha gravat correctament", alertaFinal);
		checkboxSelectedAssert("//*[@id='alertaCompletat0']", "El paràmetre generar alerta al finalitzar tasca del termini no s'ha gravat correctament", alertaTasca);
	}
	
	protected void modificarTermini(
			String nomDefProc, 
			String codi, 
			String nom, 
			Boolean duradaPredefinida, 
			Integer anys, 
			Integer mesos, 
			Integer dies, 
			Boolean laborables, 
			Boolean contolManual,
			Integer diesAlerta,
			Boolean alertaPrevia,
			Boolean alertaFinal,
			Boolean alertaTasca) {
		
		duradaPredefinida = duradaPredefinida != null && duradaPredefinida;
		laborables = laborables != null && laborables;
		contolManual = contolManual == null || contolManual;
		alertaPrevia = alertaPrevia != null && alertaPrevia;
		alertaFinal = alertaFinal != null && alertaFinal;
		alertaTasca = alertaTasca != null && alertaTasca;
				
		// Accedir a la fitxa dels terminis
		driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/terminiLlistat.html')]")).click();
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + codi + "')]", "defproces/termini/modificar/" + codi + "/01_crear.png", "El termini a modificar no existeix");

		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codi + "')]/td[1]/a")).click();
		if (nom != null) {
			driver.findElement(By.id("nom0")).clear();
			driver.findElement(By.id("nom0")).sendKeys(nom);
			driver.findElement(By.id("descripcio0")).clear();
			driver.findElement(By.id("descripcio0")).sendKeys("Descripció de termini: " + nom);
		}
		
		if (!checkboxSelected("//*[@id='duradaPredefinida0']", duradaPredefinida)) 
			driver.findElement(By.id("duradaPredefinida0")).click();
		if (duradaPredefinida) {
			if (anys != null) driver.findElement(By.xpath("//*[@id='anys']/option[@value='" + anys + "']")).click();
			if (mesos != null) driver.findElement(By.xpath("//*[@id='mesos']/option[@value='" + mesos + "']")).click();
			if (dies != null) {
				driver.findElement(By.id("dies")).clear();
				driver.findElement(By.id("dies")).sendKeys(dies.toString());
			};
		}
		if (!checkboxSelected("//*[@id='laborable0']", laborables)) driver.findElement(By.id("laborable0")).click();
		if (!checkboxSelected("//*[@id='manual0']", contolManual)) driver.findElement(By.id("manual0")).click();
		if (diesAlerta != null) {
			driver.findElement(By.id("diesPrevisAvis0")).clear();
			driver.findElement(By.id("diesPrevisAvis0")).sendKeys(diesAlerta.toString());
		};
		if (!checkboxSelected("//*[@id='alertaPrevia0']", alertaPrevia)) driver.findElement(By.id("alertaPrevia0")).click();
		if (!checkboxSelected("//*[@id='alertaFinal0']", alertaFinal)) driver.findElement(By.id("alertaFinal0")).click();
		if (!checkboxSelected("//*[@id='alertaCompletat0']", alertaTasca)) driver.findElement(By.id("alertaCompletat0")).click();
		screenshotHelper.saveScreenshot("defproces/termini/modificar/" + codi + "/02_crear.png");
			
		driver.findElement(By.xpath("//button[@value='submit']")).click();
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + codi + "') and contains(td[2], '" + nom +"')]", "No s'ha pogut crear el termini de test " + nom);
		
		// Comprovar tots els valors introduits al termini
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codi + "')]/td[1]/a")).click();
		if (nom != null) existeixElementAssert("//*[@id='descripcio0' and normalize-space(text())=\"Descripció de termini: " + nom + "\"]", "La descripció del termini no s'ha gravat correctament");
		checkboxSelectedAssert("//*[@id='duradaPredefinida0']", "El paràmetre durada predefinida del termini no s'ha gravat correctament", duradaPredefinida);
		if (duradaPredefinida) {
			if (anys != null) existeixElementAssert("//*[@id='anys']/option[@selected='selected' and @value='" + anys + "']", "els anys del termini no s'ha gravat correctament");
			if (mesos != null) existeixElementAssert("//*[@id='mesos']/option[@selected='selected' and @value='" + mesos + "']", "els mesos del termini no s'ha gravat correctament");
			if (dies != null) existeixElementAssert("//*[@id='dies' and @value='" + dies + "']", "els dies del termini no s'ha gravat correctament");
		}
		checkboxSelectedAssert("//*[@id='laborable0']", "El paràmetre dies laborables del termini no s'ha gravat correctament", laborables);
		checkboxSelectedAssert("//*[@id='manual0']", "El paràmetre permetre control manual del termini no s'ha gravat correctament", contolManual);
		if (diesAlerta != null) existeixElementAssert("//*[@id='diesPrevisAvis0' and @value='" + diesAlerta + "']", "Els dies d'alerta del termini no s'ha gravat correctament");
		checkboxSelectedAssert("//*[@id='alertaPrevia0']", "El paràmetre generar alerta prèvia del termini no s'ha gravat correctament", alertaPrevia);
		checkboxSelectedAssert("//*[@id='alertaFinal0']", "El paràmetre generar alerta final del termini no s'ha gravat correctament", alertaFinal);
		checkboxSelectedAssert("//*[@id='alertaCompletat0']", "El paràmetre generar alerta al finalitzar tasca del termini no s'ha gravat correctament", alertaTasca);
	}
}
