package net.conselldemallorca.helium.test.disseny;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import net.conselldemallorca.helium.test.util.BaseTest;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DefinicioProcesVars extends BaseTest {

	String entorn = carregarPropietat("defproc.entorn.nom", "Nom de l'entorn de proves no configurat al fitxer de properties");
	String titolEntorn = carregarPropietat("defproc.entorn.titol", "Titol de l'entorn de proves no configurat al fitxer de properties");
	String pathExportEntorn = carregarPropietatPath("defproc.export.entorn.arxiu.path", "Ruta de l'exportació de l'entorn de proves no configurat al fitxer de properties");
	String usuari = carregarPropietat("test.base.usuari.disseny", "Usuari disseny de l'entorn de proves no configurat al fitxer de properties");
	String usuariAdmin = carregarPropietat("test.base.usuari.configuracio", "Usuari configuracio de l'entorn de proves no configurat al fitxer de properties");
	String nomDefProc = carregarPropietat("defproc.deploy.definicio.proces.nom", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String pathDefProc = carregarPropietatPath("defproc.deploy.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String nomTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.nom", "Nom del tipus d'expedient de proves no configurat al fitxer de properties");
	String codTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.codi", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	String codAgrupacio1 = carregarPropietat("defproc.agrupacio.1.codi", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	String nomAgrupacio1 = carregarPropietat("defproc.agrupacio.1.nom", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	String codAgrupacio2 = carregarPropietat("defproc.agrupacio.2.codi", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	String nomAgrupacio2 = carregarPropietat("defproc.agrupacio.2.nom", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	
	@Test
	public void a0_inicialitzacio() {
		carregarUrlConfiguracio();
		crearEntorn(entorn, titolEntorn);
		assignarPermisosEntorn(entorn, usuariAdmin, "DESIGN", "ORGANIZATION", "READ", "ADMINISTRATION");
		assignarPermisosEntorn(entorn, usuari, 		"DESIGN", "ORGANIZATION", "READ", "ADMINISTRATION");
		//seleccionarEntorn(titolEntorn);
		//crearTipusExpedient("prova", "provaExpImpDefProc");
		//assignarPermisosTipusExpedient("prova", usuariAdmin, "DESIGN","CREATE","SUPERVISION","WRITE","MANAGE","DELETE","READ","ADMINISTRATION");
		//assignarPermisosTipusExpedient("prova", usuari, "DESIGN","CREATE","SUPERVISION","WRITE","MANAGE","DELETE","READ","ADMINISTRATION");
	}
	
	@Test
	public void a1_inicialitzacio() {
		carregarUrlConfiguracio();
		//saveEntornActual();
		marcarEntornDefecte(titolEntorn);
		seleccionarEntorn(titolEntorn);
		desplegarDefinicioProcesEntorn(nomDefProc, pathDefProc);
		importarDadesEntorn(entorn, pathExportEntorn);
		crearAgrupacio(nomDefProc, codAgrupacio1, nomAgrupacio1);
		crearAgrupacio(nomDefProc, codAgrupacio2, nomAgrupacio2);
		//Necessaria per una de les variables y no incluida dins els arxius importats
		crearConsultaTipus("selcon", "selcon", nomTipusExp);
	}
	
	@Test
	public void b_crearVarString() {
		carregarUrlDisseny();
		seleccionarDefinicioProces(nomDefProc);
		crearVar(carregarPropietat("defproc.variable.string.codi", "Codi de la variable string no configurat al fitxer de properties"),
				carregarPropietat("defproc.variable.string.nom", "Nom de la variable string no configurat al fitxer de properties"),
				TipusVar.STRING,
				nomAgrupacio1,
				false,
				false,
				false);
	}
	
	@Test
	public void c_crearVarInteger() {
		carregarUrlDisseny();
		seleccionarDefinicioProces(nomDefProc);
		crearVar(carregarPropietat("defproc.variable.int.codi", "Codi de la variable string no configurat al fitxer de properties"),
				carregarPropietat("defproc.variable.int.nom", "Nom de la variable string no configurat al fitxer de properties"),
				TipusVar.INTEGER,
				nomAgrupacio1,
				false,
				false,
				true);
	}
	
	@Test
	public void d_crearVarFloat() {
		carregarUrlDisseny();
		seleccionarDefinicioProces(nomDefProc);
		crearVar(carregarPropietat("defproc.variable.float.codi", "Codi de la variable string no configurat al fitxer de properties"),
				carregarPropietat("defproc.variable.float.nom", "Nom de la variable string no configurat al fitxer de properties"),
				TipusVar.FLOAT,
				nomAgrupacio1,
				false,
				false,
				false);
	}
	
	@Test
	public void e_crearVarBoolean() {
		carregarUrlDisseny();
		seleccionarDefinicioProces(nomDefProc);
		crearVar(carregarPropietat("defproc.variable.bool.codi", "Codi de la variable string no configurat al fitxer de properties"),
				carregarPropietat("defproc.variable.bool.nom", "Nom de la variable string no configurat al fitxer de properties"),
				TipusVar.BOOLEAN,
				nomAgrupacio1,
				false,
				false,
				true);
	}
	
	@Test
	public void f_crearVarTextArea() {
		carregarUrlDisseny();
		seleccionarDefinicioProces(nomDefProc);
		crearVar(carregarPropietat("defproc.variable.tarea.codi", "Codi de la variable string no configurat al fitxer de properties"),
				carregarPropietat("defproc.variable.tarea.nom", "Nom de la variable string no configurat al fitxer de properties"),
				TipusVar.TEXTAREA,
				nomAgrupacio2,
				false,
				false,
				false);
	}
	
	@Test
	public void g_crearVarDate() {
		carregarUrlDisseny();
		seleccionarDefinicioProces(nomDefProc);
		crearVar(carregarPropietat("defproc.variable.date.codi", "Codi de la variable string no configurat al fitxer de properties"),
				carregarPropietat("defproc.variable.date.nom", "Nom de la variable string no configurat al fitxer de properties"),
				TipusVar.DATE,
				nomAgrupacio2,
				false,
				false,
				false);
	}
	
	@Test
	public void h_crearVarPrice() {
		carregarUrlDisseny();
		seleccionarDefinicioProces(nomDefProc);
		crearVar(carregarPropietat("defproc.variable.price.codi", "Codi de la variable string no configurat al fitxer de properties"),
				carregarPropietat("defproc.variable.price.nom", "Nom de la variable string no configurat al fitxer de properties"),
				TipusVar.PRICE,
				nomAgrupacio2,
				false,
				false,
				true);
	}
	
	@Test
	public void i_crearVartermini() {
		carregarUrlDisseny();
		seleccionarDefinicioProces(nomDefProc);
		crearVar(carregarPropietat("defproc.variable.term.codi", "Codi de la variable string no configurat al fitxer de properties"),
				carregarPropietat("defproc.variable.term.nom", "Nom de la variable string no configurat al fitxer de properties"),
				TipusVar.TERMINI,
				null,
				false,
				false,
				false);
	}
	
	@Test
	public void j_crearVarSelEnum() {
		carregarUrlDisseny();
		seleccionarDefinicioProces(nomDefProc);
		crearVar(carregarPropietat("defproc.variable.enum.codi", "Codi de la variable string no configurat al fitxer de properties"),
				carregarPropietat("defproc.variable.enum.nom", "Nom de la variable string no configurat al fitxer de properties"),
				TipusVar.SEL_ENUM,
				null,
				false,
				false,
				false,
				"Enumerat selenium");
	}
	
	@Test
	public void k_crearVarStringMultiple() {
		carregarUrlDisseny();
		seleccionarDefinicioProces(nomDefProc);
		crearVar(carregarPropietat("defproc.variable.string.codi", "Codi de la variable string no configurat al fitxer de properties"),
				carregarPropietat("defproc.variable.string.nom", "Nom de la variable string no configurat al fitxer de properties"),
				TipusVar.STRING,
				null,
				true,
				false,
				false);
	}
	
	@Test
	public void l_crearVarStringOculta() {
		carregarUrlDisseny();
		seleccionarDefinicioProces(nomDefProc);
		crearVar(carregarPropietat("defproc.variable.string.codi", "Codi de la variable string no configurat al fitxer de properties"),
				carregarPropietat("defproc.variable.string.nom", "Nom de la variable string no configurat al fitxer de properties"),
				TipusVar.STRING,
				null,
				false,
				true,
				false);
	}
	
	@Test
	public void m_crearVarRegistre() {
		carregarUrlDisseny();
		seleccionarDefinicioProces(nomDefProc);
		crearVar("reg_v1", "Registre 1", TipusVar.STRING, null, false, false, false);
		crearVar("reg_v2", "Registre 2", TipusVar.INTEGER, null, false, false, false);
		crearVar(carregarPropietat("defproc.variable.reg.codi", "Codi de la variable registre no configurat al fitxer de properties"),
				carregarPropietat("defproc.variable.reg.nom", "Nom de la variable registre no configurat al fitxer de properties"),
				TipusVar.REGISTRE,
				nomAgrupacio1,
				false,
				false,
				false,
				new String[]{"reg_v1/Registre 1", "reg_v2/Registre 2"});
	}
	
	@Test
	public void n_crearVarRegistreMultiple() {
		carregarUrlDisseny();
		seleccionarDefinicioProces(nomDefProc);
		crearVar("reg_v1", "Registre 1", TipusVar.STRING, null, true, false, false);
		crearVar("reg_v2", "Registre 2", TipusVar.INTEGER, null, true, false, false);
		crearVar(carregarPropietat("defproc.variable.reg.codi", "Codi de la variable registre no configurat al fitxer de properties"),
				carregarPropietat("defproc.variable.reg.nom", "Nom de la variable registre no configurat al fitxer de properties"),
				TipusVar.REGISTRE,
				nomAgrupacio1,
				true,
				false,
				false,
				new String[]{"reg_v1_M/Registre 1_M", "reg_v2_M/Registre 2_M"});
	}
	
	@Test
	public void o_crearVarDominiSql() {
		carregarUrlDisseny();
		seleccionarDefinicioProces(nomDefProc);
		crearVar(carregarPropietat("defproc.variable.dom.codi", "Codi de la variable domini no configurat al fitxer de properties"),
				carregarPropietat("defproc.variable.dom.nom", "Nom de la variable domini no configurat al fitxer de properties"),
				TipusVar.SEL_DOMINI,
				null,
				false,
				false,
				false,
				new String[]{
					carregarPropietat("defproc.variable.dom.domini", "Codi del domini no configurat al fitxer de properties"),
					carregarPropietat("defproc.variable.dom.params", "Paràmetres del domini no configurat al fitxer de properties"),
					carregarPropietat("defproc.variable.dom.valor", "Valor del domini no configurat al fitxer de properties"),
					carregarPropietat("defproc.variable.dom.text", "Text del domini no configurat al fitxer de properties")});
	}
	
	@Test
	public void p_crearVarDominiIntern() {
		carregarUrlDisseny();
		seleccionarDefinicioProces(nomDefProc);
		crearVar(carregarPropietat("defproc.variable.int.dom.cod", "Codi de la variable selecció per domini intern no configurat al fitxer de properties"),
				carregarPropietat("defproc.variable.int.dom.nom", "Nom de la variable selecció per domini intern no configurat al fitxer de properties"),
				TipusVar.SEL_INTERN,
				null,
				false,
				false,
				false,
				new String[]{
					carregarPropietat("defproc.variable.int.domini", "Codi del domini intern no configurat al fitxer de properties"),
					carregarPropietat("defproc.variable.int.params", "Paràmetres del domini intern no configurat al fitxer de properties"),
					carregarPropietat("defproc.variable.int.valor", "Valor del domini intern no configurat al fitxer de properties"),
					carregarPropietat("defproc.variable.int.text", "Text del domini intern no configurat al fitxer de properties")});
	}
	
	@Test
	public void q_crearVarConsulta() {
		
		carregarUrlDisseny();
		
		seleccionarDefinicioProces(nomDefProc);
		
		crearVar(carregarPropietat("defproc.variable.con.codi", "Codi de la variable selecció per consulta no configurat al fitxer de properties"),
				carregarPropietat("defproc.variable.con.nom", "Nom de la variable selecció per consulta no configurat al fitxer de properties"),
				TipusVar.SEL_CONSULTA,
				null,
				false,
				false,
				false,
				new String[]{
					carregarPropietat("defproc.variable.con.consulta", "Codi de la consulta no configurat al fitxer de properties"),
					carregarPropietat("defproc.variable.con.params", "Paràmetres de la consulta no configurat al fitxer de properties"),
					carregarPropietat("defproc.variable.con.valor", "Valor de la consulta no configurat al fitxer de properties"),
					carregarPropietat("defproc.variable.con.text", "Text de la consulta no configurat al fitxer de properties")});
	}
	
	@Test
	public void r_crearVarSuggest() {
		carregarUrlDisseny();
		seleccionarDefinicioProces(nomDefProc);
		crearVar(carregarPropietat("defproc.variable.sugenum.codi", "Codi de la variable suggest no configurat al fitxer de properties"),
				carregarPropietat("defproc.variable.sugenum.nom", "Nom de la variable suggest no configurat al fitxer de properties"),
				TipusVar.SUG_ENUM,
				null,
				false,
				false,
				false,
				"Enumerat selenium");
	}
	
	@Test
	public void s_crearVarAccio() {
		carregarUrlDisseny();
		seleccionarDefinicioProces(nomDefProc);
		crearVar(carregarPropietat("defproc.variable.accio.codi", "Codi de la variable acció no configurat al fitxer de properties"),
				carregarPropietat("defproc.variable.accio.nom", "Nom de la variable acció no configurat al fitxer de properties"),
				TipusVar.ACCIO,
				null,
				false,
				false,
				false,
				carregarPropietat("defproc.variable.accio.accio", "Nom de la acció no configurat al fitxer de properties"));
	}
	
	@Test
	public void t_modificarVariable() {
		carregarUrlDisseny();
		seleccionarDefinicioProces(nomDefProc);
		// Modificar el camp tipus d'una variable			
	
		// Accedir a la fitxa de les variables
		driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/campLlistat.html')]")).click();			
	    screenshotHelper.saveScreenshot("defproces/variable/modificar/1_variablesInici.png");

	    // Obtenir nom variable i cercar-la
	    String codVar = carregarPropietat("defproc.variable.string.codi", "Codi de la variable string no configurat al fitxer de properties");
	    existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + codVar + "')]", "No existeix la variable a modificar");

  	    // guardar valor inicial
  	    String nomIni = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codVar + "')]/td[2]")).getText().trim();
  	    String tipIni = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codVar + "')]/td[3]")).getText().trim();

		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codVar + "')]/td[1]/a")).click();
  	    screenshotHelper.saveScreenshot("defproces/variable/modificar/2_variableOriginal.png");

  	    driver.findElement(By.xpath("//*[@id='tipus0']/option[@value='INTEGER']")).click();
		driver.findElement(By.id("etiqueta0")).clear();
		driver.findElement(By.id("etiqueta0")).sendKeys("Etiqueta modificada");
		screenshotHelper.saveScreenshot("defproces/variable/modificar/3_variableModificada.png");
		driver.findElement(By.xpath("//button[@value='submit']")).click(); 	    
  	    screenshotHelper.saveScreenshot("defproces/variable/modificar/4_variablesFi.png");
  	    
  	    // comprovar si s'ha modificat la variable
  	    String nomFi = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codVar + "')]/td[2]")).getText().trim();
  	    String tipFi = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codVar + "')]/td[3]")).getText().trim();
		assertNotEquals("No s'ha pogut canviar el nom de la variable", nomIni, nomFi);
		assertEquals("No s'ha canviar correctament el nom de la variable", "Etiqueta modificada", nomFi);
		assertNotEquals("No s'ha pogut canviar el tipus de la variable", tipIni, tipFi);
		assertEquals("No s'ha canviar correctament el tipus de la variable", "INTEGER", tipFi);
	}

	@Test
	public void u_validacioVariable() {
		carregarUrlDisseny();
		seleccionarDefinicioProces(nomDefProc);
		 // Afegeix una validació a una variable			
	
		// Accedir a la fitxa de les variables
		driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/campLlistat.html')]")).click();			
	    screenshotHelper.saveScreenshot("defproces/variable/validacio/1_variablesInici.png");
	    
	    // Obtenir nom variable i cercar-la
	    String codVar = carregarPropietat("defproc.variable.string.codi", "Codi de la variable string no configurat al fitxer de properties");
	    existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + codVar + "')]", "No existeix la variable a afegir validació");
	    
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codVar + "')]/td[5]/form/button")).click();
		screenshotHelper.saveScreenshot("defproces/variable/validacio/2_variableInici.png");

		String valIni = "NOT(var_enum01='S' and var_dat01 is blank)";
		driver.findElement(By.id("expressio0")).sendKeys(valIni);
		driver.findElement(By.id("missatge0")).sendKeys("S'ha d'especificar una data");

		screenshotHelper.saveScreenshot("defproces/variable/validacio/3_variableFi.png");
		driver.findElement(By.xpath("//button[@value='submit']")).click(); 	    
  	    
		// Comprovar que s'ha creat
  	    existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1], ' and var_dat01 is blank)')]", "defproces/variable/validacio/4_variableValidacions.png", "No s'ha pogut crear la validació de la variable");
	}

	@Test
	public void v_esborrarVar() {
		carregarUrlDisseny();
		seleccionarDefinicioProces(nomDefProc);
		// Esborra una variable en una definició de procés			
	
		// Accedir a la fitxa de les variables
		driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/campLlistat.html')]")).click();			
		screenshotHelper.saveScreenshot("defproces/variable/esborra/1_variablesInici.png");

	    // Obtenir nom variable i cercar-la
		String codVar = carregarPropietat("defproc.variable.string.codi", "Codi de la variable string no configurat al fitxer de properties");
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + codVar + "')]", "No existeix la variable a esborrar");

		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codVar + "')]/td[7]/a")).click();
		
		acceptarAlerta();

		existeixElementAssert("//*[@id='infos']/p", "No se borró la variable");
	}
	
	@Test
	public void z0_finalitzacio() {
		carregarUrlConfiguracio();
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
}
