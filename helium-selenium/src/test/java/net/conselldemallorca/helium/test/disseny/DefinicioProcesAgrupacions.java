package net.conselldemallorca.helium.test.disseny;

import static org.junit.Assert.assertTrue;

import java.util.List;

import net.conselldemallorca.helium.test.util.BaseTest;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DefinicioProcesAgrupacions extends BaseTest {

	String entorn = carregarPropietat("defproc.entorn.nom", "Nom de l'entorn de proves no configurat al fitxer de properties");
	String titolEntorn = carregarPropietat("defproc.entorn.titol", "Titol de l'entorn de proves no configurat al fitxer de properties");
	String pathExportEntorn = carregarPropietatPath("defproc.export.entorn.arxiu.path", "Ruta de l'exportació de l'entorn de proves no configurat al fitxer de properties");
	String usuari = carregarPropietat("test.base.usuari.disseny", "Usuari disseny de l'entorn de proves no configurat al fitxer de properties");
	String usuariAdmin = carregarPropietat("test.base.usuari.configuracio", "Usuari configuracio de l'entorn de proves no configurat al fitxer de properties");
	String nomDefProc = carregarPropietat("defproc.deploy.definicio.proces.nom", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String pathDefProc = carregarPropietatPath("defproc.export.arxiu.path", "Ruta de l'exportació de la definició de procés de proves no configurat al fitxer de properties");
	String codTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.codi", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	String codiAgrup1 = carregarPropietat("defproc.agrupacio.1.codi", "Codi de la agrupació 1 no configurat al fitxer de properties");
	String nomAgrup1 = carregarPropietat("defproc.agrupacio.1.nom", "Nom de la agrupació 1 no configurat al fitxer de properties");
	String codiAgrup2 = carregarPropietat("defproc.agrupacio.2.codi", "Codi de la agrupació 2 no configurat al fitxer de properties");
	String nomAgrup2 = carregarPropietat("defproc.agrupacio.2.nom", "Nom de la agrupació 2 no configurat al fitxer de properties");
	String codiAgrup3 = carregarPropietat("defproc.agrupacio.3.codi", "Codi de la agrupació 3 no configurat al fitxer de properties");
	String nomAgrup3 = carregarPropietat("defproc.agrupacio.3.nom", "Nom de la agrupació 3 no configurat al fitxer de properties");
	String codiVarStr = carregarPropietat("defproc.variable.string.codi", "Codi de la variable de prova tipus string no configurat al fitxer de properties");
	String nomVarStr = carregarPropietat("defproc.variable.string.nom", "Nom de la variable de prova tipus string no configurat al fitxer de properties");
	String codiVarTerm = carregarPropietat("defproc.variable.term.codi", "Codi de la variable de prova tipus termini no configurat al fitxer de properties");
	String nomVarTerm = carregarPropietat("defproc.variable.term.nom", "Nom de la variable de prova tipus termini no configurat al fitxer de properties");
	
	@Test
	public void a0_inicialitzacio() {
		carregarUrlConfiguracio();
		crearEntorn(entorn, titolEntorn);
		assignarPermisosEntorn(entorn, usuariAdmin, "DESIGN", "ORGANIZATION", "READ", "ADMINISTRATION");
		assignarPermisosEntorn(entorn, usuari, "DESIGN", "ORGANIZATION", "READ");
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
	public void b_crearAgupacio() {
		carregarUrlDisseny();
		crearAgrupacio(nomDefProc, codiAgrup3, nomAgrup3);
	}
	
	@Test
	public void c_modificarAgrupacio() {
		carregarUrlDisseny();
		seleccionarDefinicioProces(nomDefProc);
		
		driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/campAgrupacioLlistat.html')]")).click();
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + codiAgrup3 + "')]", "No existeix la agrupació a modificar");
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codiAgrup3 + "')]/td[1]/a")).click();
		driver.findElement(By.id("codi0")).clear();
		driver.findElement(By.id("codi0")).sendKeys("codiModificat");
		driver.findElement(By.id("nom0")).clear();
		driver.findElement(By.id("nom0")).sendKeys("nomModificat");
		driver.findElement(By.xpath("//button[@value='submit']")).click();
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'codiModificat') and contains(td[2],'nomModificat')]", "No s'ha pogut modificar l'agrupació " + codiAgrup3);

		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'codiModificat')]/td[1]/a")).click();
		driver.findElement(By.id("codi0")).clear();
		driver.findElement(By.id("codi0")).sendKeys(codiAgrup3);
		driver.findElement(By.id("nom0")).clear();
		driver.findElement(By.id("nom0")).sendKeys(nomAgrup3);
		driver.findElement(By.xpath("//button[@value='submit']")).click();
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + codiAgrup3 + "') and contains(td[2],'" + nomAgrup3 + "')]", "No s'ha pogut modificar l'agrupació codiModificat");
	}
	
	@Test
	public void d_ordenaAgrupacio() {
		carregarUrlDisseny();
		seleccionarDefinicioProces(nomDefProc);
		driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/campAgrupacioLlistat.html')]")).click();	
		sortTable("registre", 1, 2);
	}
	
	@Test
	public void e_assignaVarAgrupacio() {
		carregarUrlDisseny();
		seleccionarDefinicioProces(nomDefProc);
		assignarVarAgrupacio(codiAgrup3, codiVarStr + "_M", nomVarStr + "_M");
		assignarVarAgrupacio(codiAgrup3, codiVarStr  + "_O", nomVarStr  + "_O", true);
		assignarVarAgrupacio(codiAgrup3, codiVarTerm, nomVarTerm, true);
	}
	
	@Test
	public void f_ordenaVarsAgrupacio() {
		carregarUrlDisseny();
		seleccionarDefinicioProces(nomDefProc);
		driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/campAgrupacioLlistat.html')]")).click();	
		// Click al botó de variables
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codiAgrup1 + "')]/td[3]/form/button")).click();
		sortTable("registre", 2, 5);
		sortTable("registre", 6, 3);
	}

	@Test
	public void g_desassignarVarAgrupacio() {
		carregarUrlDisseny();
		seleccionarDefinicioProces(nomDefProc);
		desassignarVarAgrupacio(codiAgrup3, nomVarStr + "_M");
		desassignarVarAgrupacio(codiAgrup3, nomVarTerm, true);
	}

	@Test
	public void h_eliminarAgrupacio() {
		carregarUrlDisseny();
		seleccionarDefinicioProces(nomDefProc);
		driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/campAgrupacioLlistat.html')]")).click();
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + codiAgrup3 + "')]", "defproces/agrupacio/eliminar/" + codiAgrup3 + "/01_eliminar.png", "La agrupació a eliminar no existeix");
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codiAgrup3 + "')]/td[4]/a")).click();
		acceptarAlerta();
		existeixElementAssert("//*[@id='infos']/p", "No se borro la agrupacion correctamente");
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
	protected void assignarVarAgrupacio(String codiAgrupacio, String codiVar, String nomVar) {
		assignarVarAgrupacio(codiAgrupacio, codiVar, nomVar, false);
	}
	protected void assignarVarAgrupacio(String codiAgrupacio, String codiVar, String nomVar, boolean inPlace) {
		// Accedir a la fitxa de agrupacions
		if (!inPlace) {
			driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/campAgrupacioLlistat.html')]")).click();	
			existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + codiAgrupacio + "')]", "defproces/agrupacio/assignar/var/" + codiVar + "/01_asignar_var.png", "La agrupació a la que assignar variables no existeix");
			// Click al botó de variables
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codiAgrupacio + "')]/td[3]/form/button")).click();
		}
		noExisteixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + nomVar + "')]", "Variable ja assignada");
		
		// Seleccionar variable
		List<WebElement> allOptions = driver.findElement(By.id("id0")).findElements(By.tagName("option"));
		boolean trobat = false;
		for (WebElement option : allOptions) {
		    if (option.getText().startsWith(codiVar + "/")) {
		    	option.click();
		    	trobat = true;
		    	break;
		    }
		}
		assertTrue("No existeix la variable", trobat);
		
    	screenshotHelper.saveScreenshot("defproces/agrupacio/assignar/var/" + codiVar + "/02_crea_var.png");
    	
		// Botó crear variable
	    driver.findElement(By.xpath("//button[@value='submit']")).click();

	    // Comprovar que s'ha assignat
	    existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + nomVar + "')]", "defproces/agrupacio/assignar/var/" + codiVar + "/03_asignar_var.png", "Variable no assignada correctament");
	}
	
	protected void desassignarVarAgrupacio(String codiAgrupacio, String nomVar) {
		desassignarVarAgrupacio(codiAgrupacio, nomVar, false);
	}
	protected void desassignarVarAgrupacio(String codiAgrupacio, String nomVar, boolean inPlace) {
		// Accedir a la fitxa de tasques
		if (!inPlace) {
			driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/campAgrupacioLlistat.html')]")).click();	
			existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + codiAgrupacio + "')]", "defproces/agrupacio/desassignar/var/" + nomVar + "/01_borra_var.png", "La agrupació a la que desassignar variables no existeix");
			// Click al botó de variables
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codiAgrupacio + "')]/td[3]/form/button")).click();
		}
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + nomVar + "')]", "Variable no assignada");
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + nomVar + "')]/td[2]/a")).click();
		acceptarAlerta();
		noExisteixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + nomVar + "/')]", "defproces/agrupacio/desassignar/var/" + nomVar + "/02_borra_var.png", "Variable no desassignada");
	}
	
}
