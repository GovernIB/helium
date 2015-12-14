package net.conselldemallorca.helium.test.tramitacio;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.fail;

import net.conselldemallorca.helium.test.util.BaseTest;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TasquesSubprocessExpedient extends BaseTest {	
	
							//EXP.10.3 - Tramitar Tasca
								//EXP.10.3.1 - Dades
									//EXP.10.3.1.1 - Emplenar dades amb diferents opcions de definició de variable (tipus, múltiple, registre i configuracions)
									//EXP.10.3.1.2 - Comprovar el pas entre procés i subprocés
									//EXP.10.3.1.3 - Carrega de dominis sql, ws, intern
									//EXP.10.3.1.4 - Execució d´accions 
	
	String entorn = carregarPropietat("tramsel.entorn.nom", "Nom de l'entorn de proves no configurat al fitxer de properties");
	String titolEntorn = carregarPropietat("tramsel.entorn.titol", "Titol de l'entorn de proves no configurat al fitxer de properties");
	String nomDefProc = carregarPropietat("defproc.deploy.definicio.proces.nom", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String usuari = carregarPropietat("test.base.usuari.configuracio", "Usuari configuració de l'entorn de proves no configurat al fitxer de properties");
	String nomSubDefProc = carregarPropietat("defproc.deploy.definicio.subproces.nom", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String pathDefProc = carregarPropietatPath("defproc.deploy.definicio.subproces.main.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String pathSubDefProc = carregarPropietatPath("defproc.subproces.deploy.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String exportDefProc = carregarPropietatPath("defproc.tasca_dades.exp.export.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String pathTipExp = carregarPropietatPath("tramsel_subproces.export.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String nomTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.nom", "Nom del tipus d'expedient de proves no configurat al fitxer de properties");
	String codTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.codi", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	String urlWSdomini = carregarPropietat("defproc.deploy.url.ws.domini", "La URL del WS que ha de emplenar el desplegable amb valors no esta configurat al properties");

	// XPATHS
	
	String linkAccesInfoDefProc = "//*[@id='registre']/tbody/tr[contains(td/a, '"+nomDefProc+"')]/td/a[contains(@href, '/definicioProces/info.html')]";
	String pestanyaVarsDefProc  = "//*[@id='tabnav']/li/a[contains(@href, '/definicioProces/campLlistat.html')]";
	String botoNovaVariable     = "//*[@id='content']/form/button[contains(text(), 'Nova variable')]";
	String botoNouDomini		= "//*[@id='content']/form/button";
	String botoGuardaDomini		= "//*[@id='command']/div[4]/button[1]";
	String botoCrearVariable	= "//*[@id='command']/div[@class='buttonHolder']/button[text() = 'Crear']";
	
	String pestanyaTasquesDefProc  = "//*[@id='tabnav']/li/a[contains(@href, '/definicioProces/tascaLlistat.html')]";
	String linkAccesVarsTascaDefProc = "//*[@id='registre']/tbody/tr[1]/td/form/button[contains(text(), 'Variables')]";
	String botoAfegirVarTascaDefProc = "//*[@id='afegVar']/div[@class='buttonHolder']/button[text() = 'Afegir']";
	
	String botoConsultarExpLlistat = "//*[@id='command']/div/div[@class='buttonHolder']/button[text() = 'Consultar']";
	String linkAccedirExpedient = "//*[@id='registre']/tbody/tr[contains(td/a, '[1] Prova vars dominis SQL_WS')]/td/a[contains(@href, '/tasca/personaLlistat.html')]";
	String linkAccedirTasca = "//*[@id='registre']/tbody/tr/td[1]/a";
	
	
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
		
		importarDadesTipExp(codTipusExp, pathTipExp);
		
		desplegarDefinicioProcesEntorn(nomTipusExp, nomSubDefProc, pathSubDefProc);
		desplegarDefinicioProcesEntorn(nomTipusExp, nomDefProc, pathDefProc);
		
		screenshotHelper.saveScreenshot("tramitar/dadesexpedient/crear_dades/1.png");
	}

	@Test
	public void b_pas_proces_subproces() throws InterruptedException {
		carregarUrlConfiguracio();
		
		seleccionarEntorn(titolEntorn);
		
		screenshotHelper.saveScreenshot("tramitar/modificarInfoExp/1.png");
					
		String[] res = iniciarExpediente(codTipusExp,"SE-22/2014", "Expedient de prova Selenium " + (new Date()).getTime() );
				
		screenshotHelper.saveScreenshot("ExpedientPestanyaTasques/tramitar_delegar_tasca/1.png");

		consultarTareas(null, res[1], nomTipusExp, false);
		
		existeixElementAssert("//*[@id='registre']/tbody/tr/td[contains(a/text(), '01 - Entrada')]/a", "No se encontró la tarea: 01 - Entrada");
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr/td[contains(a/text(), '01 - Entrada')]/a")).click();
		
		screenshotHelper.saveScreenshot("ExpedientPestanyaTasques/tramitar_delegar_tasca/3.png");
						
		driver.findElement(By.xpath("//*[@id='formFinalitzar']/div/button[contains(text(), 'Subproceso')]")).click();
		acceptarAlerta();
		
		existeixElementAssert("//*[@id='infos']/p", "No se finalizó correctamente");
		
		// Comprobamos que haya una tarea del subproceso		
		consultarTareas(null, res[1], nomTipusExp, false);
		
		existeixElementAssert("//*[@id='registre']/tbody/tr/td[contains(a/text(), 'tasca1_subproces')]/a", "No se encontró la tarea: tasca1_subproces");
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr/td[contains(a/text(), 'tasca1_subproces')]/a")).click();
		
		screenshotHelper.saveScreenshot("ExpedientPestanyaTasques/tramitar_delegar_tasca/3.png");
						
		driver.findElement(By.xpath("//*[@id='formFinalitzar']//button[contains(text(), 'Finalitzar')]")).click();
		acceptarAlerta();
		
		existeixElementAssert("//*[@id='infos']/p", "No se finalizó correctamente");
		
		// Comprobamos que haya la 2ª tarea del subproceso		
		consultarTareas(null, res[1], nomTipusExp, false);
		
		existeixElementAssert("//*[@id='registre']/tbody/tr/td[contains(a/text(), 'tasca2_subproces')]/a", "No se encontró la tarea: tasca2_subproces");
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr/td[contains(a/text(), 'tasca2_subproces')]/a")).click();
		
		screenshotHelper.saveScreenshot("ExpedientPestanyaTasques/tramitar_delegar_tasca/3.png");
						
		driver.findElement(By.xpath("//*[@id='formFinalitzar']//button[contains(text(), 'Finalitzar')]")).click();
		acceptarAlerta();
		
		existeixElementAssert("//*[@id='infos']/p", "No se finalizó correctamente");
		
		// Comprobamos que vuelva al proceso principal	
		consultarTareas(null, res[1], nomTipusExp, false);
		
		existeixElementAssert("//*[@id='registre']/tbody/tr/td[contains(a/text(), '02 - Tancament')]/a", "No se encontró la tarea: 02 - Tancament");
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr/td[contains(a/text(), '02 - Tancament')]/a")).click();
		
		screenshotHelper.saveScreenshot("ExpedientPestanyaTasques/tramitar_delegar_tasca/3.png");
						
		driver.findElement(By.xpath("//*[@id='formFinalitzar']//button[contains(text(), 'Finalitzar')]")).click();
		acceptarAlerta();
		
		existeixElementAssert("//*[@id='infos']/p", "No se finalizó correctamente");
	}
	
	@Test
	public void c1_prepara_dominis() {
		
		carregarUrlConfiguracio();
		seleccionarEntorn(titolEntorn);
		
		accedirPantallaDissenyDominis();
		
		screenshotHelper.saveScreenshot("tramitar/dades/carregaDominis/c1_1_crear_dominis-llistat_inicial.png");
		
		//Cream un domini SQL
		driver.findElement(By.xpath(botoNouDomini)).click();
		emplenaDadesDominiSQL("sqlDomId", "Nom consulta SQL", "0", "Descripció consulta SQL", "java:/comp/env/jdbc/HeliumDS", "select distinct enum.codi CODI, enum.nom DESCRIPCIO from hel_enumeracio enum");
		driver.findElement(By.xpath(botoGuardaDomini)).click();
		
		screenshotHelper.saveScreenshot("tramitar/dades/carregaDominis/c1_2_crear_dominis-domini_sql_creat.png");
		
		//Cream un domini WS
		driver.findElement(By.xpath(botoNouDomini)).click();
		emplenaDadesDominiWS("wsDomId", "Nom consulta WS", "0", "Descripció consulta WS", urlWSdomini, "NONE", "ATRIBUTS", "", "");
		driver.findElement(By.xpath(botoGuardaDomini)).click();
		
		screenshotHelper.saveScreenshot("tramitar/dades/carregaDominis/c1_3_crear_dominis-domini_ws_creat.png");
	}
	
	@Test
	public void c2_prepara_variables() {
		
		carregarUrlConfiguracio();
		seleccionarEntorn(titolEntorn);
		
		/**
		 * Cream la nova variable amb valors desde consulta SQL
		 */
		
		accedirPantallaCreacioVariableDefProc();
		
		screenshotHelper.saveScreenshot("tramitar/dades/carregaDominis/c2_1_crear_variables-estat_inicial.png");
		
		driver.findElement(By.id("codi0")).sendKeys("listDomSQL");
		
		//Seleccionar Tipus de variable
		for (WebElement option : driver.findElement(By.id("tipus0")).findElements(By.tagName("option"))) {
			if ("SELECCIO".equals(option.getAttribute("value"))) {
				option.click();
				break;
			}
		}
		
		driver.findElement(By.id("etiqueta0")).sendKeys("Llista Domini SQL");
		
		//Seleccionar com a domini, el SQL creat al test anterior
		for (WebElement option : driver.findElement(By.id("domini0")).findElements(By.tagName("option"))) {
			if ("Nom consulta SQL".equals(option.getText())) {
				option.click();
				break;
			}
		}
		
		driver.findElement(By.id("dominiId0")).sendKeys("IDSQL");
		
		//Definim els camps de la consulta que montarán la llista desplegable
		driver.findElement(By.id("dominiCampValor0")).sendKeys("CODI");
		driver.findElement(By.id("dominiCampText0")).sendKeys("DESCRIPCIO");
		
		screenshotHelper.saveScreenshot("tramitar/dades/carregaDominis/c2_2_crear_variables-dades_var_sql.png");
		
		driver.findElement(By.xpath(botoCrearVariable)).click();
		
		screenshotHelper.saveScreenshot("tramitar/dades/carregaDominis/c2_3_crear_variables-resultat_var_sql.png");
		
		/**
		 * Cream la nova variable amb valors desde consulta WS
		 */
		
		driver.findElement(By.xpath(botoNovaVariable)).click();
		
		driver.findElement(By.id("codi0")).sendKeys("listDomWS");
		
		//Seleccionar Tipus de variable
		for (WebElement option : driver.findElement(By.id("tipus0")).findElements(By.tagName("option"))) {
			if ("SELECCIO".equals(option.getAttribute("value"))) {
				option.click();
				break;
			}
		}
		
		driver.findElement(By.id("etiqueta0")).sendKeys("Llista Domini WS");
		
		//Seleccionar com a domini, el SQL creat al test anterior
		for (WebElement option : driver.findElement(By.id("domini0")).findElements(By.tagName("option"))) {
			if ("Nom consulta WS".equals(option.getText())) {
				option.click();
				break;
			}
		}
		
		driver.findElement(By.id("dominiId0")).sendKeys("PERSONA_AMB_CODI");
		
		//Definim els camps de la consulta que montarán la llista desplegable
		driver.findElement(By.id("dominiCampValor0")).sendKeys("codi");
		driver.findElement(By.id("dominiCampText0")).sendKeys("nomSencer");
		driver.findElement(By.id("dominiParams0")).sendKeys("persona:#{'curso175'}");
		
		screenshotHelper.saveScreenshot("tramitar/dades/carregaDominis/c2_4_crear_variables-dades_var_ws.png");
		
		driver.findElement(By.xpath(botoCrearVariable)).click();
		
		screenshotHelper.saveScreenshot("tramitar/dades/carregaDominis/c2_5_crear_variables-resultat_var_ws.png");
	}
	
	@Test
	public void c3_prepara_tasques() {
		
		carregarUrlConfiguracio();
		seleccionarEntorn(titolEntorn);
		
		accedirPestanyaTasquesDefProc();
		
		screenshotHelper.saveScreenshot("tramitar/dades/carregaDominis/c3_1_praparar_tasques-estat_inicial.png");
		
		driver.findElement(By.xpath(linkAccesVarsTascaDefProc)).click();
		
		//Seleccionar com a domini, el SQL creat al test anterior
		for (WebElement option : driver.findElement(By.id("campId0")).findElements(By.tagName("option"))) {
			if ("listDomSQL/Llista Domini SQL".equals(option.getText())) {
				option.click();
				break;
			}
		}
		
		driver.findElement(By.xpath(botoAfegirVarTascaDefProc)).click();
		
		screenshotHelper.saveScreenshot("tramitar/dades/carregaDominis/c3_2_praparar_tasques-var_sql_afegida.png");
		
		//Seleccionar com a domini, el SQL creat al test anterior
		for (WebElement option : driver.findElement(By.id("campId0")).findElements(By.tagName("option"))) {
			if ("listDomWS/Llista Domini WS".equals(option.getText())) {
				option.click();
				break;
			}
		}
		
		driver.findElement(By.xpath(botoAfegirVarTascaDefProc)).click();
		
		screenshotHelper.saveScreenshot("tramitar/dades/carregaDominis/c3_3_praparar_tasques-var_ws_afegida.png");
	}
	
	@Test
	public void c4_comprova_variables() {
	
		carregarUrlConfiguracio();
		
		seleccionarEntorn(titolEntorn);
		
		iniciarExpediente(codTipusExp, "1", "Prova vars dominis SQL_WS");
		
		accedirConsultaLlistatExpedients();
		
		screenshotHelper.saveScreenshot("tramitar/dades/carregaDominis/c4_1_comprovar_valors_variables-llistat_expedients.png");
		
		driver.findElement(By.xpath(botoConsultarExpLlistat)).click();
		
		driver.findElement(By.xpath(linkAccedirExpedient)).click();	
		
		screenshotHelper.saveScreenshot("tramitar/dades/carregaDominis/c4_2_comprovar_valors_variables-accedim_expedient.png");
		
		driver.findElement(By.xpath(linkAccedirTasca)).click();
		
		try {Thread.sleep(5000);}catch(Exception ex){}
		
		// Comprovam que els desplegables han agafat algun valor de les consultes definides anteriorment		
		List<WebElement> llistaOpcionsFromSQL = driver.findElement(By.id("listDomSQL0")).findElements(By.tagName("option"));
		List<WebElement> llistaOpcionsFromWS  = driver.findElement(By.id("listDomWS0")).findElements(By.tagName("option"));
		
		screenshotHelper.saveScreenshot("tramitar/dades/carregaDominis/c4_3_comprovar_valors_variables-accedim_tasca.png");
		
		if (llistaOpcionsFromSQL==null || llistaOpcionsFromSQL.size()<=1) {
			fail("El desplegable listDomSQL0, de la tasca Prova vars dominis SQL_WS, no té valors.");
		}else{
			llistaOpcionsFromSQL.get(1).click();
		}
		
		if (llistaOpcionsFromWS==null || llistaOpcionsFromWS.size()<=1) {
			fail("El desplegable listDomWS0, de la tasca Prova vars dominis SQL_WS, no té valors.");
		}else{
			llistaOpcionsFromWS.get(1).click();
		}
		
		screenshotHelper.saveScreenshot("tramitar/dades/carregaDominis/c4_4_comprovar_valors_variables-desplegables amb valors.png");
	}

	@Test
	public void z_limpiar() throws InterruptedException {
		
		carregarUrlConfiguracio();
		
		seleccionarEntorn(titolEntorn);
		
		eliminarExpedient(null, null, nomTipusExp);
		
		// Eliminar la def de proceso
		eliminarDefinicioProces(nomDefProc);
		eliminarDefinicioProces(nomSubDefProc);
		
		// Eliminar el tipo de expediente
		eliminarTipusExpedient(codTipusExp);
		
		eliminarTotsElsDominis();
		
		eliminarEntorn(entorn);
		
		screenshotHelper.saveScreenshot("TasquesDadesTasca/finalizar_expedient/1.png");	
	}
	
	// **********************************************
	// F U N C I O N S   P R I V A D E S
	// **********************************************
	
	private void accedirPantallaCreacioVariableDefProc() {
		accedirPantallaDissenyDefProc();
		driver.findElement(By.xpath(linkAccesInfoDefProc)).click();
		driver.findElement(By.xpath(pestanyaVarsDefProc)).click();
		driver.findElement(By.xpath(botoNovaVariable)).click();
	}
	
	private void accedirPestanyaTasquesDefProc() {
		accedirPantallaDissenyDefProc();
		driver.findElement(By.xpath(linkAccesInfoDefProc)).click();
		driver.findElement(By.xpath(pestanyaTasquesDefProc)).click();
	}
}
