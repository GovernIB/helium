package net.conselldemallorca.helium.test.disseny;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import net.conselldemallorca.helium.test.util.BaseTest;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DefinicioProcesTasques extends BaseTest {

	String entorn = carregarPropietat("defproc.entorn.nom", "Nom de l'entorn de proves no configurat al fitxer de properties");
	String titolEntorn = carregarPropietat("defproc.entorn.titol", "Titol de l'entorn de proves no configurat al fitxer de properties");
	String pathExportEntorn = carregarPropietatPath("defproc.export.entorn.arxiu.path", "Ruta de l'exportació de l'entorn de proves no configurat al fitxer de properties");
	String usuari = carregarPropietat("test.base.usuari.disseny", "Usuari disseny de l'entorn de proves no configurat al fitxer de properties");
	String usuariAdmin = carregarPropietat("test.base.usuari.configuracio", "Usuari configuracio de l'entorn de proves no configurat al fitxer de properties");
	String nomDefProc = carregarPropietat("defproc.deploy.definicio.proces.nom", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String pathDefProc = carregarPropietatPath("defproc.export.arxiu.path", "Ruta de l'exportació de la definició de procés de proves no configurat al fitxer de properties"); 
	String codTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.codi", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	String tasca1 = carregarPropietat("defproc.tasca.tasca1", "Tasca de la definició de procés de prova no configurat al fitxer de properties");
	String codiVarStr = carregarPropietat("defproc.variable.string.codi", "Codi de la variable de prova tipus string no configurat al fitxer de properties");
	String codiVarInt = carregarPropietat("defproc.variable.int.codi", "Codi de la variable de prova tipus integer no configurat al fitxer de properties");
	String codiVarFloat = carregarPropietat("defproc.variable.float.codi", "Codi de la variable de prova tipus float no configurat al fitxer de properties");
	String codiVarDate = carregarPropietat("defproc.variable.date.codi", "Codi de la variable de prova tipus date no configurat al fitxer de properties");
	String codiVarPrice = carregarPropietat("defproc.variable.price.codi", "Codi de la variable de prova tipus price no configurat al fitxer de properties");
	String codiVarBoolean = carregarPropietat("defproc.variable.bool.codi", "Codi de la variable de prova tipus boolean no configurat al fitxer de properties");
	String codiVarSelEnum = carregarPropietat("defproc.variable.enum.codi", "Codi de la variable de prova tipus selecció no configurat al fitxer de properties");
	String codiVarRegistre = carregarPropietat("defproc.variable.reg.codi", "Codi de la variable de prova tipus registre no configurat al fitxer de properties");
	String codiDocPla = carregarPropietat("defproc.document.plant.codi", "Codi del document de prova amb plantilla no configurat al fitxer de properties");
	String codiDocNopla = carregarPropietat("defproc.document.noplant.codi", "Codi del document de prova sense plantilla no configurat al fitxer de properties");
	String nomDocPla = carregarPropietat("defproc.document.plant.nom", "Nom del document de prova amb plantilla no configurat al fitxer de properties");
	String nomDocNopla = carregarPropietat("defproc.document.noplant.nom", "Nom del document de prova sense plantilla no configurat al fitxer de properties");
	
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
		//saveEntornActual();
		marcarEntornDefecte(titolEntorn);
		seleccionarEntorn(titolEntorn);
		desplegarDefPro(TipusDesplegament.EXPORTDEFPRC, nomDefProc, null, pathDefProc, null, false, false);
		seleccionarDefinicioProces(nomDefProc);
		importarDadesEntorn(entorn, pathExportEntorn);
	}
	
	@Test
	public void a2_inicialitzacio() {
		carregarUrlDisseny();
		marcarEntornDefecte(titolEntorn);
	}	
	
	@Test
	public void b_assignaVarTasca() {
		carregarUrlDisseny();
		seleccionarDefinicioProces(nomDefProc);
		assignarVarTasca(tasca1, codiVarStr, true, true, false, false);
		assignarVarTasca(tasca1, codiVarInt + "_R", true, true, true, false, true);
		assignarVarTasca(tasca1, codiVarFloat, true, true, false, true, true);
		assignarVarTasca(tasca1, codiVarDate, true, false, false, false, true);
		assignarVarTasca(tasca1, codiVarPrice + "_R", false, true, false, false, true);
		assignarVarTasca(tasca1, codiVarBoolean + "_R", true, true, false, false, true);
		assignarVarTasca(tasca1, codiVarSelEnum, true, true, false, false, true);
		assignarVarTasca(tasca1, codiVarRegistre, true, true, false, false, true);
		assignarVarTasca(tasca1, codiVarRegistre + "_M", true, true, false, false, true);
	}
	
	@Test
	public void c_modificarVarTasca() {
		carregarUrlDisseny();
		seleccionarDefinicioProces(nomDefProc);
		modificarVarTasca(tasca1, codiVarInt + "_R", false, false, false, true);
		modificarVarTasca(tasca1, codiVarRegistre, true, true, true, false, true);
	}
	
	@Test
	public void d_ordenaVars() {
		carregarUrlDisseny();
		seleccionarDefinicioProces(nomDefProc);
		driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/tascaLlistat.html')]")).click();	
		// Click al botó de variables
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + tasca1 + "')]/td[3]/form/button")).click();
		sortTable("registre", 2, 5);
		sortTable("registre", 8, 1);
	}
	
	@Test
	public void e_desassignaVarTasca() {
		carregarUrlDisseny();
		seleccionarDefinicioProces(nomDefProc);
		desassignarVarTasca(tasca1, codiVarInt + "_R");
		desassignarVarTasca(tasca1, codiVarRegistre, true);
	}
	
	@Test
	public void f_assignaDocTasca() {
		carregarUrlDisseny();
		seleccionarDefinicioProces(nomDefProc);
		assignarDoc(tasca1, codiDocPla, true, false);
		assignarDoc(tasca1, codiDocNopla, false, false, true);
	}

	@Test
	public void g_modificarDocTasca() {
		carregarUrlDisseny();
		seleccionarDefinicioProces(nomDefProc);
		modificarDoc(tasca1, codiDocNopla, true, true);
	}

	@Test
	public void h_ordenaDocs() {
		carregarUrlDisseny();
		seleccionarDefinicioProces(nomDefProc);
		driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/tascaLlistat.html')]")).click();	
		// Click al botó de documents
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + tasca1 + "')]/td[4]/form/button")).click();
		sortTable("registre", 1, 2);
	}
	
	@Test
	public void i_assignarSigTasca() {
		carregarUrlDisseny();
		seleccionarDefinicioProces(nomDefProc);
		assignarSig(tasca1, codiDocPla, nomDocPla, true);
		assignarSig(tasca1, codiDocNopla, nomDocNopla, false, true);
	}

	@Test
	public void j_modificarSigTasca() {
		carregarUrlDisseny();
		seleccionarDefinicioProces(nomDefProc);
		modificarSig(tasca1, codiDocNopla, nomDocNopla, true);
	}
	
	@Test
	public void k_ordenaSigs() {
		carregarUrlDisseny();
		seleccionarDefinicioProces(nomDefProc);
		driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/tascaLlistat.html')]")).click();	
		// Click al botó de signatures
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + tasca1 + "')]/td[5]/form/button")).click();
		sortTable("registre", 1, 2);
	}

	@Test
	public void l_desassignarSigTasca() {
		carregarUrlDisseny();
		seleccionarDefinicioProces(nomDefProc);
		desassignarSig(tasca1, codiDocNopla, nomDocNopla);
	}
	
	@Test
	public void m_desassignaDocTasca() {
		carregarUrlDisseny();
		seleccionarDefinicioProces(nomDefProc);
		desassignarDoc(tasca1, codiDocNopla);
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
	protected void assignarVarTasca(String tasca, String codiVar, boolean isRf, boolean isWt, boolean isRq, boolean isRo) {
		assignarVarTasca(tasca, codiVar, isRf, isWt, isRq, isRo, false);
	}
	protected void assignarVarTasca(String tasca, String codiVar, boolean isRf, boolean isWt, boolean isRq, boolean isRo, boolean inPlace) {
		// Accedir a la fitxa de tasques
		if (!inPlace) {
			driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/tascaLlistat.html')]")).click();	
			existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + tasca1 + "')]", "defproces/tasques/assignar/var/" + codiVar + "/01_crea_var.png", "La tasca a la que assignar variables no existeix");
			// Click al botó de variables
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + tasca1 + "')]/td[3]/form/button")).click();
		}
		noExisteixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + codiVar + "')]", "Variable ja assignada");
		
		// Seleccionar variable
		List<WebElement> allOptions = driver.findElement(By.id("campId0")).findElements(By.tagName("option"));
		boolean trobat = false;
		for (WebElement option : allOptions) {
		    if (option.getText().startsWith(codiVar + "/")) {
		    	option.click();
		    	trobat = true;
		    	break;
		    }
		}
		assertTrue("No existeix la variable", trobat);
		
		if (!isRf) {driver.findElement(By.id("readFrom0")).click();}
    	if (!isWt) {driver.findElement(By.id("writeTo0")).click();}
    	if (isRq) {driver.findElement(By.id("required0")).click();}
    	if (isRo) {driver.findElement(By.id("readOnly0")).click();}

    	screenshotHelper.saveScreenshot("defproces/tasques/assignar/var/" + codiVar + "/02_crea_var.png");
    	
		// Botó crear variable
	    driver.findElement(By.xpath("//button[@value='submit']")).click();

	    // Comprovar que s'ha creat
	    existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + codiVar + "')]", "defproces/tasques/assignar/var/" + codiVar + "/03_crea_var.png", "Variable no assignada correctament");
	    String href = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codiVar + "')]/td[6]/a")).getAttribute("href");
	    String id = href.substring(href.lastIndexOf("=") + 1);
	    
	    checkboxSelectedAssert("//*[@id='rf_" + id + "']", "Propietat Llegir del procés no assignada correctament", isRf);
	    checkboxSelectedAssert("//*[@id='wt_" + id + "']", "Propietat Escriure al procés no assignada correctament", isWt);
	    checkboxSelectedAssert("//*[@id='rq_" + id + "']", "Propietat obligatòria no assignada correctament", isRq);
	    checkboxSelectedAssert("//*[@id='ro_" + id + "']", "Propietat Només llectura no assignada correctament", isRo);
	    
	}
	protected void modificarVarTasca(String tasca, String codiVar, boolean isRf, boolean isWt, boolean isRq, boolean isRo) {
		modificarVarTasca(tasca, codiVar, isRf, isWt, isRq, isRo, false);
	}
	protected void modificarVarTasca(String tasca, String codiVar, boolean isRf, boolean isWt, boolean isRq, boolean isRo, boolean inPlace) {
		// Accedir a la fitxa de tasques
		if (!inPlace) {
			driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/tascaLlistat.html')]")).click();	
			existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + tasca1 + "')]", "defproces/tasques/modificar/var/" + codiVar + "/01_mod_var.png", "La tasca a la que assignar variables no existeix");
			// Click al botó de variables
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + tasca1 + "')]/td[3]/form/button")).click();
		}
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + codiVar + "')]", "Variable no assignada");
		
		String href = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codiVar + "')]/td[6]/a")).getAttribute("href");
	    String id = href.substring(href.lastIndexOf("=") + 1);
	    
		boolean rf = checkboxSelected("//*[@id='rf_" + id + "']", true);
		boolean wt = checkboxSelected("//*[@id='wt_" + id + "']", true);
		boolean rq = checkboxSelected("//*[@id='rq_" + id + "']", true);
		boolean ro = checkboxSelected("//*[@id='ro_" + id + "']", true);
		
		if (rf != isRf) {driver.findElement(By.id("rf_" + id)).click();}
    	if (wt != isWt) {driver.findElement(By.id("wt_" + id)).click();}
    	if (rq != isRq) {driver.findElement(By.id("rq_"+ id)).click();}
    	if (ro != isRo) {driver.findElement(By.id("ro_"+ id)).click();}

    	screenshotHelper.saveScreenshot("defproces/tasques/modificar/var/" + codiVar + "/02_mod_var.png");
    	driver.findElement(By.id("actualitzar_" + id)).click();
    	
	    // Comprovar que s'ha modificat
    	driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/tascaLlistat.html')]")).click();
    	driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + tasca1 + "')]/td[3]/form/button")).click();
	    
	    checkboxSelectedAssert("//*[@id='rf_" + id + "']", "Propietat Llegir del procés no assignada correctament", isRf);
	    checkboxSelectedAssert("//*[@id='wt_" + id + "']", "Propietat Escriure al procés no assignada correctament", isWt);
	    checkboxSelectedAssert("//*[@id='rq_" + id + "']", "Propietat obligatòria no assignada correctament", isRq);
	    checkboxSelectedAssert("//*[@id='ro_" + id + "']", "Propietat Només llectura no assignada correctament", isRo);
	    
	}

	protected void desassignarVarTasca(String tasca, String codiVar) {
		desassignarVarTasca(tasca, codiVar, false);
	}
	protected void desassignarVarTasca(String tasca, String codiVar, boolean inPlace) {
		// Accedir a la fitxa de tasques
		if (!inPlace) {
			driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/tascaLlistat.html')]")).click();	
			existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + tasca1 + "')]", "defproces/tasques/desassignar/var/" + codiVar + "/01_borra_var.png", "La tasca a la que assignar variables no existeix");
			// Click al botó de variables
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + tasca1 + "')]/td[3]/form/button")).click();
		}
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + codiVar + "/')]", "Variable no assignada");
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codiVar + "/')]/td[6]/a")).click();
		noExisteixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + codiVar + "/')]", "defproces/tasques/desassignar/var/" + codiVar + "/02_borra_var.png", "Variable no desassignada");
	}
	
	protected void assignarDoc(String tasca, String codiDoc, boolean isRq, boolean isRo) {
		assignarDoc(tasca, codiDoc, isRq, isRo, false);
	}
	protected void assignarDoc(String tasca, String codiDoc, boolean isRq, boolean isRo, boolean inPlace) {
		// Accedir a la fitxa de tasques
		if (!inPlace) {
			driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/tascaLlistat.html')]")).click();	
			existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + tasca1 + "')]", "defproces/tasques/assignar/doc/" + codiDoc + "/01_crea_doc.png", "La tasca a la que assignar documents no existeix");
			// Click al botó de documents
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + tasca1 + "')]/td[4]/form/button")).click();
		}
		noExisteixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + codiDoc + "')]", "Document ja assignat");
		
		// Seleccionar document
		List<WebElement> allOptions = driver.findElement(By.id("documentId0")).findElements(By.tagName("option"));
		for (WebElement option : allOptions) {
		    if (option.getText().startsWith(codiDoc + "/")) {
		    	option.click();
		    	break;
		    }
		}
    	if (isRq) {driver.findElement(By.id("required0")).click();}
    	if (isRo) {driver.findElement(By.id("readOnly0")).click();}

    	screenshotHelper.saveScreenshot("defproces/tasques/assignar/doc/" + codiDoc + "/02_crea_doc.png");
    	
		// Botó crear document
	    driver.findElement(By.xpath("//button[@value='submit']")).click();

	    // Comprovar que s'ha creat
	    existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + codiDoc + "')]", "defproces/tasques/assignar/doc/" + codiDoc + "/03_crea_doc.png", "Document no assignat correctament");
	    String propietats = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codiDoc + "')]/td[2]")).getText();

	    assertTrue("Propietat obligatòri no assignada correctament", isRq == propietats.contains("rq"));
	    assertTrue("Propietat Només llectura no assignada correctament", isRo == propietats.contains("ro"));
	    
	}
	
	protected void modificarDoc(String tasca, String codiDoc, boolean isRq, boolean isRo) {
		modificarDoc(tasca, codiDoc, isRq, isRo, false);
	}
	protected void modificarDoc(String tasca, String codiDoc, boolean isRq, boolean isRo, boolean inPlace) {
		// Accedir a la fitxa de tasques
		if (!inPlace) {
			driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/tascaLlistat.html')]")).click();	
			existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + tasca1 + "')]", "defproces/tasques/modificar/doc/" + codiDoc + "/01_mod_doc.png", "La tasca a la que assignar documents no existeix");
			// Click al botó de variables
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + tasca1 + "')]/td[4]/form/button")).click();
		}
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + codiDoc + "')]", "Document no assignat");
		
		String propietats = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codiDoc + "')]/td[2]")).getText();
		boolean rq = propietats.contains("rq");
		boolean ro = propietats.contains("ro");
		
		if (rq != isRq || ro != isRo) {
			// Seleccionar document
			List<WebElement> allOptions = driver.findElement(By.id("documentId0")).findElements(By.tagName("option"));
			for (WebElement option : allOptions) {
			    if (option.getText().startsWith(codiDoc + "/")) {
			    	option.click();
			    	break;
			    }
			}
	    	if (rq != isRq) {driver.findElement(By.id("required0")).click();}
	    	if (ro != isRo) {driver.findElement(By.id("readOnly0")).click();}
	
	    	screenshotHelper.saveScreenshot("defproces/tasques/modificar/doc/" + codiDoc + "/02_mod_doc.png");
	    	
			// Botó crear document
		    driver.findElement(By.xpath("//button[@value='submit']")).click();
	
		    // Comprovar que s'ha modificat
		    propietats = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codiDoc + "')]/td[2]")).getText();
	
		    assertTrue("Propietat obligatòri no assignada correctament", isRq == propietats.contains("rq"));
		    assertTrue("Propietat Només llectura no assignada correctament", isRo == propietats.contains("ro"));
		} else {
			fail("No s'ha indicat cap modificació");
		}
	}
	
	protected void desassignarDoc(String tasca, String codiDoc) {
		desassignarDoc(tasca, codiDoc, false);
	}
	protected void desassignarDoc(String tasca, String codiDoc, boolean inPlace) {
		// Accedir a la fitxa de tasques
		if (!inPlace) {
			driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/tascaLlistat.html')]")).click();	
			existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + tasca1 + "')]", "defproces/tasques/desassignar/doc/" + codiDoc + "/01_borra_doc.png", "La tasca a la que assignar documents no existeix");
			// Click al botó de variables
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + tasca1 + "')]/td[4]/form/button")).click();
		}
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + codiDoc + "/')]", "Variable no assignada");
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codiDoc + "/')]/td[3]/a")).click();
		if (isAlertPresent()) { acceptarAlerta(); }
		
		existeixElementAssert("//*[@id='infos']/p", "No s´ha desassignat el document de la tasca.");
	}
	
	protected void assignarSig(String tasca, String codiDoc, String nomDoc, boolean isRq) {
		assignarSig(tasca, codiDoc, nomDoc, isRq, false);
	}
	protected void assignarSig(String tasca, String codiDoc, String nomDoc, boolean isRq, boolean inPlace) {
		// Accedir a la fitxa de tasques
		if (!inPlace) {
			driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/tascaLlistat.html')]")).click();	
			existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + tasca1 + "')]", "defproces/tasques/assignar/sig/" + codiDoc + "/01_crea_sig.png", "La tasca a la que assignar signatures no existeix");
			// Click al botó de signatures
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + tasca1 + "')]/td[5]/form/button")).click();
		}
		noExisteixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + nomDoc + "')]", "Signatura ja assignada");
		
		// Seleccionar document
		List<WebElement> allOptions = driver.findElement(By.id("documentId0")).findElements(By.tagName("option"));
		for (WebElement option : allOptions) {
		    if (option.getText().startsWith(codiDoc + "/")) {
		    	option.click();
		    	break;
		    }
		}
    	if (!isRq) {driver.findElement(By.id("required0")).click();}

    	screenshotHelper.saveScreenshot("defproces/tasques/assignar/sig/" + codiDoc + "/02_crea_sig.png");
    	
		// Botó crear signatura
	    driver.findElement(By.xpath("//button[@value='submit']")).click();

	    // Comprovar que s'ha creat
	    existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + nomDoc + "')]", "defproces/tasques/assignar/sig/" + codiDoc + "/03_crea_sig.png", "Signatura no assignat correctament");
	    String propietats = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + nomDoc + "')]/td[2]")).getText();

	    assertTrue("Propietat obligatòri no assignada correctament", isRq == propietats.contains("rq"));
	    
	}
	
	protected void modificarSig(String tasca, String codiDoc, String nomDoc, boolean isRq) {
		modificarSig(tasca, codiDoc, nomDoc, isRq, false);
	}
	protected void modificarSig(String tasca, String codiDoc, String nomDoc, boolean isRq, boolean inPlace) {
		// Accedir a la fitxa de tasques
		if (!inPlace) {
			driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/tascaLlistat.html')]")).click();	
			existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + tasca1 + "')]", "defproces/tasques/modificar/sig/" + codiDoc + "/01_mod_sig.png", "La tasca a la que assignar documents no existeix");
			// Click al botó de variables
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + tasca1 + "')]/td[5]/form/button")).click();
		}
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + nomDoc + "')]", "Signatura no assignat");
		
		String propietats = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + nomDoc + "')]/td[2]")).getText();
		boolean rq = propietats.contains("rq");
		
		if (rq != isRq) {
			// Seleccionar document
			List<WebElement> allOptions = driver.findElement(By.id("documentId0")).findElements(By.tagName("option"));
			for (WebElement option : allOptions) {
			    if (option.getText().startsWith(codiDoc + "/")) {
			    	option.click();
			    	break;
			    }
			}
	    	if (!isRq) {driver.findElement(By.id("required0")).click();}
	
	    	screenshotHelper.saveScreenshot("defproces/tasques/modificar/sig/" + codiDoc + "/02_mod_sig.png");
	    	
			// Botó crear document
		    driver.findElement(By.xpath("//button[@value='submit']")).click();
	
		    // Comprovar que s'ha modificat
		    propietats = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + nomDoc + "')]/td[2]")).getText();
	
		    assertTrue("Propietat obligatòri no assignada correctament", isRq == propietats.contains("rq"));
		} else {
			fail("No s'ha indicat cap modificació");
		}
	}
	
	protected void desassignarSig(String tasca, String codiDoc, String nomDoc) {
		desassignarSig(tasca, codiDoc, nomDoc, false);
	}
	protected void desassignarSig(String tasca, String codiDoc, String nomDoc, boolean inPlace) {
		// Accedir a la fitxa de tasques
		if (!inPlace) {
			driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/tascaLlistat.html')]")).click();	
			existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + tasca1 + "')]", "defproces/tasques/desassignar/sig/" + codiDoc + "/01_borra_sig.png", "La tasca a la que assignar documents no existeix");
			// Click al botó de variables
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + tasca1 + "')]/td[5]/form/button")).click();
		}
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + nomDoc + "')]", "Signatura no assignada");
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + nomDoc + "')]/td[4]/a")).click();
		if (isAlertPresent()) { acceptarAlerta(); }
		
		existeixElementAssert("//*[@id='infos']/p", "No s´ha desassignat la signatura de la tasca.");
	}
	
}
