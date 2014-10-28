package net.conselldemallorca.helium.test.informes;

import static org.junit.Assert.assertTrue;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import net.conselldemallorca.helium.test.util.BaseTest;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class InformesExpedients extends BaseTest {

										//INF.1 - Informes - Seleccio de consulta
										//INF.2 - Informes - Comprovar que les vars. del filtre son les definides a la consulta
										//INF.3 - Informes - Provar filtres
										//INF.4 - Informes - Netejar filtre
										//INF.5 - Informes - Comprovar que les vars. de l´informe son les definides a la consulta
										//INF.6 - Informes - Resultats per pagina
										//INF.7 - Informes - Descarregar excel
										//INF.8 - Informes - Informe Jasper reports (comprovacio manual)
										//INF.9 - Informes - Desplegar tasques expedient
										//INF.10 - Informes - Accés tramitacio de tasca
										//INF.11 - Informes - Obrir expedient: accés a la informacio de l´expedient
										//INF.12 - Informes - Aturar expedient
										//INF.13 - Informes - Anular expedient
										//INF.14 - Informes - Esborrar expedient

	String entorn 	    = carregarPropietat("informe.entorn.nom", "Nom de l'entorn de proves no configurat al fitxer de properties");
	String titolEntorn  = carregarPropietat("informe.entorn.titol", "Titol de l'entorn de proves no configurat al fitxer de properties");
	String usuariAdmin  = carregarPropietat("test.base.usuari.configuracio", "Usuari configuració de l'entorn de proves no configurat al fitxer de properties");
	String usuari 	    = carregarPropietat("test.base.usuari.disseny", "Usuari feina de l'entorn de proves no configurat al fitxer de properties");

	String nomDefProc   = carregarPropietat("informe.deploy.definicio.proces.nom", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String pathDefProc  = carregarPropietatPath("informe.deploy.definicio.proces.path", "Path de la definició de procés de proves no configurat al fitxer de properties");
	
	String codTipusExp1 = carregarPropietat("informe.deploy.tipus.expedient.codi", "Codi del tipus d'expedient de proves 1 no configurat al fitxer de properties");
	String nomTipusExp  = carregarPropietat("informe.deploy.tipus.expedient.nom", "Nom del tipus d'expedient de proves 2 no configurat al fitxer de properties");
	
	String nomConsulta	= carregarPropietat("informe.deploy.consulta.nom", "Nom del tipus d'expedient de proves 2 no configurat al fitxer de properties");
	
	String pathExportEntorn = carregarPropietatPath("informe.deploy.entorn.path", "Path de l'entorn de proves no configurat al fitxer de properties");
	String codTipusExp 		= carregarPropietat("informe.deploy.tipus.expedient.codi", "Codi del tipus d'expedient de proves 1 no configurat al fitxer de properties");
	String codi 			= carregarPropietat("informe.consulta.codi", "Codi de la consulta");
	String titol 			= carregarPropietat("informe.consulta.titol", "titol de la consulta");
	String jrxml			= carregarPropietat("informe.consulta.informe.path", "informe en format jrxml");
	String nomInforme 		= carregarPropietat("informe.consulta.titol", "Nom de l'informe");
	String codiTipusExp 	= carregarPropietat("informe.deploy.tipus.expedient.codi", "Codi del tipus d'expedient de l'informe");
	String pathExportTipExp = carregarPropietatPath("informe.deploy.tipus.expedient.path", "Path de l'entorn de proves no configurat al fitxer de properties");
	
	String anyActual = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));
	
	// X - P A T H S	
	String xPathBotoConsultarExpedients = "//*[@id='commandFiltre']//button[contains(@onclick,'submit')]";
	
	
	
	@Test
	public void a_inicialitzacio() {
		
		carregarUrlConfiguracio();
		crearEntorn(entorn, titolEntorn);
		importarDadesEntorn(entorn, pathExportEntorn);
		try {Thread.sleep(3000);}catch(Exception ex) {}
		assignarPermisosEntorn(entorn, usuariAdmin, "DESIGN", "ORGANIZATION", "READ", "ADMINISTRATION");
		assignarPermisosEntorn(entorn, usuari,      "DESIGN", "ORGANIZATION", "READ", "ADMINISTRATION");
		marcarEntornDefecte(titolEntorn);
		seleccionarEntorn(titolEntorn);
		
		desplegarDefPro(TipusDesplegament.EXPORTDEFPRC, nomDefProc, null, pathDefProc, null, false, false);
		//Incluye los datos de Diseño > Consultas por tipo ...
		importarDadesTipExp(codiTipusExp, pathExportTipExp);
		
		assignarPermisosTipusExpedient(codTipusExp1, usuariAdmin, "DESIGN","CREATE","SUPERVISION","WRITE","MANAGE","DELETE","READ","ADMINISTRATION");
		assignarPermisosTipusExpedient(codTipusExp1, usuari,      "DESIGN","CREATE","SUPERVISION","WRITE","MANAGE","DELETE","READ","ADMINISTRATION");
	}

	@Test
	public void b_inicialitzacio() throws InterruptedException, ParseException {
		
		carregarUrlDisseny();
		
		seleccionarEntorn(titolEntorn);
		
		//Iniciam expedient i emplenam les dades de les tasques
		for (int i = 1; i <= 24; i++) {
			iniciarExpediente(codTipusExp1, null, null);
			tramitarTasca(i);
		}
	}
	
	@Test
	public void c_seleccio_consulta() throws InterruptedException, ParseException {
		carregarUrlDisseny();
		seleccionarEntorn(titolEntorn);
		accedirPantallaConsultesDiseny();
	}
	
	@Test
	public void d_comprovar_variables_filtre() throws InterruptedException, ParseException {
		
		carregarUrlDisseny();
		seleccionarEntorn(titolEntorn);

		accedirPantallaConsultesDiseny();
		
		screenshotHelper.saveScreenshot("expedients/informes/d1_1_comprovar_variables_filtre.png");
		
		existeixElementAssert("//*[@id='Cons1_var_boolean0']",	"El campo var_boolean no existe en el filtro de la consulta ");
		
		existeixElementAssert("//*[@id='Cons1_var_date0']",		"El campo var_date(0) no existe en el filtro de la consulta");
		existeixElementAssert("//*[@id='Cons1_var_date1']",		"El campo var_date(1) no existe en el filtro de la consulta");
		
		existeixElementAssert("//*[@id='Cons1_var_float0']",	"El campo var_float(0) no existe en el filtro de la consulta");
		existeixElementAssert("//*[@id='Cons1_var_float1']",	"El campo var_float(1) no existe en el filtro de la consulta");
		
		existeixElementAssert("//*[@id='Cons1_var_int0']",		"El campo var_int(0) no existe en el filtro de la consulta");
		existeixElementAssert("//*[@id='Cons1_var_int1']",		"El campo var_int(1) no existe en el filtro de la consulta");
		
		existeixElementAssert("//*[@id='Cons1_var_price0']",	"El campo var_price(0) no existe en el filtro de la consulta");
		existeixElementAssert("//*[@id='Cons1_var_price1']",	"El campo var_price(1) no existe en el filtro de la consulta");
		
		existeixElementAssert("//*[@id='Cons1_var_seleccio0']",	"El campo var_seleccio no existe en el filtro de la consulta");
		
		existeixElementAssert("//*[@id='Cons1_var_textarea0']",	"El campo var_textarea no existe en el filtro de la consulta");
		
		existeixElementAssert("//*[@id='Cons1_vtitol0']",		"El campo vtitol no existe en el filtro de la consulta");
		
		existeixElementAssert("//*[@id='Cons1_v10']",			"El campo v1 no existe en el filtro de la consulta");
	}
	
	@Test
	public void e_filtrar() throws InterruptedException, ParseException {
		
		carregarUrlDisseny();
		seleccionarEntorn(titolEntorn);

		accedirPantallaConsultesDiseny();
		
		//********************************
		//Filtre amb variables sense valor
		//********************************
			filtraIcomprovaResultats("24", "expedients/informes/e1_1_");
			
		//********************************
		//Filtre amb cada un dels tipus de variable
		//********************************
		
			//Amb boolean n´hi ha d´haver 12 de cada tipus
			List<WebElement> optionsBoolean = driver.findElement(By.id("Cons1_var_boolean0")).findElements(By.tagName("option"));
			optionsBoolean.get(optionsBoolean.size()-1).click();
			filtraIcomprovaResultats("12", "expedients/informes/e1_2_");

			//Amb dates de l´1 al 10 d´agost, n´hi hauría d´haver 10. (cada cop netejam primer el filtre anterior)
			optionsBoolean = driver.findElement(By.id("Cons1_var_boolean0")).findElements(By.tagName("option"));
			optionsBoolean.get(0).click();
			driver.findElement(By.id("Cons1_var_date0")).sendKeys("08/01/14");
			driver.findElement(By.id("Cons1_var_date1")).sendKeys("08/10/14");
			filtraIcomprovaResultats("10", "expedients/informes/e1_3_");
			
			//Els floats van de 2.0 a 48.0. De 2.0 a 10.0 hi hauria de haver 5 resultats
			driver.findElement(By.id("Cons1_var_date0")).clear();
			driver.findElement(By.id("Cons1_var_date1")).clear();
			driver.findElement(By.id("Cons1_var_float0")).sendKeys("2.0");
			driver.findElement(By.id("Cons1_var_float1")).sendKeys("10.0");
			filtraIcomprovaResultats("5", "expedients/informes/e1_4_");
			
			//Els Integers van de 3 a 36. Filtrant de 10 a 20, hi hauria d´haver 3 resultats
			driver.findElement(By.id("Cons1_var_float0")).clear();
			driver.findElement(By.id("Cons1_var_float1")).clear();
			driver.findElement(By.id("Cons1_var_int0")).sendKeys("10");
			driver.findElement(By.id("Cons1_var_int1")).sendKeys("20");
			filtraIcomprovaResultats("3", "expedients/informes/e1_5_");
			
			//Els Prices van de 5,00 a 120,00. Filtrant de 100,00 a 120,00, hi hauria d´haver 5 resultats.
			driver.findElement(By.id("Cons1_var_int0")).clear();
			driver.findElement(By.id("Cons1_var_int1")).clear();
			driver.findElement(By.id("Cons1_var_price0")).sendKeys("100,00");
			driver.findElement(By.id("Cons1_var_price1")).sendKeys("120,00");
			filtraIcomprovaResultats("5", "expedients/informes/e1_6_");
			
			//La variable de selecció, esta amb la opció 1 per els 20 primers expedients. Option 2 per els 4 darrers
			driver.findElement(By.id("Cons1_var_price0")).clear();
			driver.findElement(By.id("Cons1_var_price1")).clear();
			List<WebElement> optionsSeleccio = driver.findElement(By.id("Cons1_var_seleccio0")).findElements(By.tagName("option"));
			optionsSeleccio.get(1).click();
			filtraIcomprovaResultats("20", "expedients/informes/e1_7_");
			
			//Respecte la variable textarea,s´ha posat un texte fixe "Textarea Observacion Expedient Tasca " mes un numero secuencial
			//si posam un 1 al final de l'string, trobara el num 1, mes tots els del 10 al 19 (11 resultats)
			optionsSeleccio = driver.findElement(By.id("Cons1_var_seleccio0")).findElements(By.tagName("option"));
			optionsSeleccio.get(0).click();
			driver.findElement(By.id("Cons1_var_textarea0")).sendKeys("Textarea Observacion Expedient Tasca 1");
			filtraIcomprovaResultats("11", "expedients/informes/e1_8_");
			
			//Variable títol, idem que textarea. Texte fixe: "Titol expedient - String - " mes un numero secuencial
			//si posam un 2 al final de l'string, trobara el num 2, mes tots els del 20 al 24 (6 resultats)
			driver.findElement(By.id("Cons1_var_textarea0")).clear();
			driver.findElement(By.id("Cons1_vtitol0")).sendKeys("Titol String 2");
			filtraIcomprovaResultats("6", "expedients/informes/e1_9_");
			
			//Variable 1, idem que les dues anteriors. Texte fixe: "Variable 1 - String - " mes un numero secuencial
			//En aquest cas si posam un 24 al final de l'string, trobara nomes el num 24 (1 resultat)
			driver.findElement(By.id("Cons1_vtitol0")).clear();
			driver.findElement(By.id("Cons1_v10")).sendKeys("Variable String 24");
			filtraIcomprovaResultats("1", "expedients/informes/e1_10_");
			
		//********************************
		//Filtre amb condicions mesclades
		//********************************
			
			driver.findElement(By.id("Cons1_v10")).clear(); //(Netejam el filtre anterior)
			
			//Filtram nomes el que tenen el booleà a "No" es a dir la meitat (12)
			optionsBoolean = driver.findElement(By.id("Cons1_var_boolean0")).findElements(By.tagName("option"));
			optionsBoolean.get(optionsBoolean.size()-1).click();
			//A més filtram per dates, ara haurien de ser nomes (5)
			driver.findElement(By.id("Cons1_var_date0")).sendKeys("08/01/14");
			driver.findElement(By.id("Cons1_var_date1")).sendKeys("08/10/14");
			filtraIcomprovaResultats("5", "expedients/informes/e1_11_");

			//Segona prova amb filtres amb condicions mesclades: fecha, textarea i titol
				optionsBoolean = driver.findElement(By.id("Cons1_var_boolean0")).findElements(By.tagName("option"));
				optionsBoolean.get(0).click();
				//driver.findElement(By.id("Cons1_var_date0")).clear();
				//driver.findElement(By.id("Cons1_var_date1")).clear();
			
			//Ara que tenim els filtres netejats. Primer, dates: aixi en quedaràn 15		
			driver.findElement(By.id("Cons1_var_date0")).sendKeys("08/10/14");
			driver.findElement(By.id("Cons1_var_date1")).sendKeys("08/24/14");		
			//Filtrant per titol, posant un 2, només quedaràn els de 20 a 24 (5)
			driver.findElement(By.id("Cons1_vtitol0")).sendKeys("Titol String 2");
			//La variable de selecció, esta amb la opció 1 per els 20 primers expedients. Option 2 per els 4 darrers
			//Aquest filtre no varia el resultat (segueixen quedan 5, del 20 al 24)
			optionsSeleccio = driver.findElement(By.id("Cons1_var_seleccio0")).findElements(By.tagName("option"));
			optionsSeleccio.get(2).click();
			filtraIcomprovaResultats("4", "expedients/informes/e1_12_");
	}
		
	@Test
	public void f_netejar_filtre() throws InterruptedException, ParseException {	
		
		carregarUrlDisseny();
		seleccionarEntorn(titolEntorn);
		
		accedirPantallaConsultesDiseny();
		
		screenshotHelper.saveScreenshot("expedients/informes/f1_1_netejar_filtre-estat_inicial.png");
		
		// Rellenamos los datos
		Calendar calendar = Calendar.getInstance();
		for (WebElement input : driver.findElements(By.xpath("//*[@id='commandFiltre']//input"))) {
			if (!input.getAttribute("type").equalsIgnoreCase("hidden")) {
				if (input.getAttribute("class").contains("hasDatepicker")) {
					calendar.add(Calendar.DATE, 1);
					input.sendKeys(new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime()));
				} else {
					input.sendKeys(input.getAttribute("name"));
				}
			}
		}
		
		for (WebElement select : driver.findElements(By.xpath("//*[@id='commandFiltre']/div/div/select"))) {
			List<WebElement> options = select.findElements(By.tagName("option"));
			options.get(options.size()-1).click();
		}
		
		screenshotHelper.saveScreenshot("expedients/informes/f1_2_netejar_filtre-dades_emplenades.png");
		
		//driver.findElement(By.xpath("//*[@id='command']//button[@value='submit']")).click();
		
		// Limpiamos los datos
		driver.findElement(By.xpath("//*[@id='commandFiltre']//button[@value='netejar']")).click();
		
		Thread.sleep(3000);
		
		screenshotHelper.saveScreenshot("expedients/informes/f1_3_netejar_filtre-camps_netejats.png");
		
		// Comprobamos que están vacíos
		for (WebElement input : driver.findElements(By.xpath("//*[@id='commandFiltre']//input"))) {
			if (!input.getAttribute("type").equalsIgnoreCase("hidden")) {
				assertTrue("El campo '"+input.getAttribute("name")+"' no se limpió", input.getAttribute("value").isEmpty());
			}
		}
		for (WebElement select : driver.findElements(By.xpath("//*[@id='commandFiltre']//select"))) {
			List<WebElement> options = select.findElements(By.tagName("option"));
			int i = 0;
			for (WebElement option : options) {
				if (option.isSelected()) {
					break;
				}
				i++;
			}
			assertTrue("El desplegable '"+select.getAttribute("name")+"' no se limpió", i == 0);
		}		
		//driver.findElement(By.xpath("//*[@id='commandFiltre']//button[@value='submit']")).click();
	}
	
	@Test
	public void g_comprovar_variables_informe() throws InterruptedException, ParseException {
		
		carregarUrlDisseny();
		seleccionarEntorn(titolEntorn);
		accedirPantallaConsultesDiseny();
		filtraIcomprovaResultats("24", "expedients/informes/g1_1_comprovar_columnes_informe");

		//Ara que hem realitzat una cerca, comprovam que apareixen totes les columnes que hem indicat a la configuració de l´informe
		existeixElementAssert("//*[@id='registre']/thead/tr//*[contains(@href, 'Cons1.var_boolean')]",	"La columna Variable Boolean no existe en los resultados de la consulta");
		existeixElementAssert("//*[@id='registre']/thead/tr//*[contains(@href, 'Cons1.var_date')]",		"La columna Variable Date no existe en los resultados de la consulta");
		existeixElementAssert("//*[@id='registre']/thead/tr//*[contains(@href, 'Cons1.var_float')]",	"La columna Variable Float no existe en los resultados de la consulta");
		existeixElementAssert("//*[@id='registre']/thead/tr//*[contains(@href, 'Cons1.var_int')]",		"La columna Variable Integer no existe en los resultados de la consulta");
		existeixElementAssert("//*[@id='registre']/thead/tr//*[contains(@href, 'Cons1.var_price')]",	"La columna Variable Price no existe en los resultados de la consulta");
		existeixElementAssert("//*[@id='registre']/thead/tr//*[contains(@href, 'Cons1.var_seleccio')]",	"La columna Variable Seleccio no existe en los resultados de la consulta");
		existeixElementAssert("//*[@id='registre']/thead/tr//*[contains(@href, 'Cons1.var_textarea')]",	"La columna Variable Textarea no existe en los resultados de la consulta");
		existeixElementAssert("//*[@id='registre']/thead/tr//*[contains(@href, 'Cons1.vtitol')]",		"La columna Titol de l´expedient no existe en los resultados de la consulta");
		existeixElementAssert("//*[@id='registre']/thead/tr//*[contains(@href, 'Cons1.v1')]",			"La columna Variable 1 no existe en los resultados de la consulta");
	}
		
	@Test
	public void h_resultats_per_pagina() throws InterruptedException, ParseException {

		carregarUrlDisseny();
		seleccionarEntorn(titolEntorn);

		accedirPantallaConsultesDiseny();
		driver.findElement(By.xpath(xPathBotoConsultarExpedients)).click();
		
		Thread.sleep(2000);
		
		// Número de expedientes
		String numExpedientes = driver.findElement(By.xpath("//*[@id='content']/span[1]")).getText();
		if (numExpedientes.contains("trobat un expedient")) {
			numExpedientes = "1";
		} else {
			numExpedientes = numExpedientes.substring("S'han trobat ".length(), numExpedientes.indexOf(" expedients,"));
		}
		
		screenshotHelper.saveScreenshot("expedients/informes/h1_0_resultats_per_pagina-estat_inicial.png");

		// Seleccionamos página por página
		int i = 0;
		int numPages = driver.findElement(By.xpath("//*[@id='objectsPerPage']")).findElements(By.tagName("option")).size()-1;

		while (i < numPages) {

			driver.findElement(By.xpath("//*[@id='objectsPerPage']")).findElements(By.tagName("option")).get(i).click();

			screenshotHelper.saveScreenshot("expedients/informes/h1_"+(i+1)+"_resultats_per_pagina.png");
			
			Thread.sleep(3000);
			
			driver.findElement(By.xpath(xPathBotoConsultarExpedients)).click();

			Thread.sleep(2000);
			
			int numElementsPage = Integer.valueOf(driver.findElement(By.xpath("//*[@id='objectsPerPage']")).findElements(By.tagName("option")).get(i).getAttribute("value"));

			if (Integer.valueOf(numExpedientes) >= numElementsPage) {
				assertTrue("El número de expedientes por página no coincidía", numElementsPage == driver.findElements(By.xpath("//*[@id='registre']/tbody/tr")).size());
			} else {
				assertTrue("El número de expedientes encontrados era mayor a la página", numElementsPage > driver.findElements(By.xpath("//*[@id='registre']/tbody/tr")).size());
				assertTrue("El número de expedientes encontrados no coincide con los mostrados", Integer.parseInt(numExpedientes) == driver.findElements(By.xpath("//*[@id='registre']/tbody/tr")).size());
			}

			i++;
		}
	}

	@Test
	public void i_descarregar() throws InterruptedException, ParseException {
		
		carregarUrlDisseny();
		seleccionarEntorn(titolEntorn);
		accedirPantallaConsultesDiseny();
		
		driver.findElement(By.xpath(xPathBotoConsultarExpedients)).click();
		
		downloadFile("//*[@id='content']/div[2]/a", "informe_Consulta Selenium.xls");
		
		screenshotHelper.saveScreenshot("expedients/informes/i1_1_descarregar_llistat_excel.png");
	}
	
	@Test
	public void j_mostrar_informe()  throws InterruptedException, ParseException {
		
		carregarUrlDisseny();
		
		seleccionarEntorn(titolEntorn);
		
		accedirPantallaConsultesDiseny();
		
		Thread.sleep(2000);
		
		driver.findElement(By.xpath(xPathBotoConsultarExpedients)).click();
		
		Thread.sleep(2000);
		
		screenshotHelper.saveScreenshot("expedients/informes/j1_1_mostrar_informe-estat_inicial.png");
		
		existeixElementAssert("//*[@id='commandFiltre']//button[contains(@onclick,'informe')]", "No se encuentra el botón de informe.");
		
		driver.findElement(By.xpath("//*[@id='commandFiltre']//button[contains(@onclick,'informe')]")).click();
		
		existeixElementAssert("//*[@id='perdat10']", "No se encuentra el parametro del informe en la ventana modal.");
		
		driver.findElement(By.id("perdat10")).sendKeys("Param Informe Consulta");
		
		screenshotHelper.saveScreenshot("expedients/informes/j1_2_mostrar_informe-emplenar_parametre.png");
		
		//driver.findElement(By.xpath("/html/body/div[contains(@class, 'ui-dialog')]/div[contains(@class, 'ui-dialog-button')]/button[text()='Generar']")).click();
		
		postDownloadFile("//*[@id='paramsCommand']");
		
		screenshotHelper.saveScreenshot("expedients/informes/j1_3_mostrar_informe-descarrega_informe.png");
	}
	
	@Test
	public void k_desplegar_tasques_expedient() throws InterruptedException, ParseException {
		
		carregarUrlDisseny();
		seleccionarEntorn(titolEntorn);
		
		accedirPantallaConsultesDiseny();
		driver.findElement(By.xpath(xPathBotoConsultarExpedients)).click();
		
		screenshotHelper.saveScreenshot("expedients/informes/k1_1_desplegar_tasques-estat_inicial.png");
		
//		// Asignamos permisos
//		assignarPermisosTipusExpedient(codTipusExp1, usuari, "DESIGN","CREATE","SUPERVISION","WRITE","MANAGE","DELETE","READ","ADMINISTRATION");
		
		existeixElementAssert("//*[@id='registre']/tbody/tr[1]/td[2]/a", "No se tenía ningún expediente");
		
		// Vamos a la tarea a tramitar del expediente
		String nomExpediente = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]/td[2]/a")).getText();
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]/td[2]/a")).click();
		
		screenshotHelper.saveScreenshot("expedients/informes/k1_2_desplegar_tasques-pantalla_desti.png");
		
		assertTrue("No se llegó a la pantalla de tasques pendents", "Tasques pendents".equals(driver.findElement(By.xpath("//*[@id='page-title']/h2/span")).getText()));
		assertTrue("No se realizó la búsqueda con el nombre del expediente correcto", nomExpediente.equals(driver.findElement(By.xpath("//*[@id='expedient0']")).getAttribute("value")));
	}
	
	@Test
	public void l_acces_a_tramitacio_de_tasca() throws InterruptedException, ParseException {
		
		carregarUrlDisseny();
		seleccionarEntorn(titolEntorn);
		
//		// Asignamos permisos
//		assignarPermisosTipusExpedient(codTipusExp1, usuari, "DESIGN","CREATE","SUPERVISION","WRITE","MANAGE","DELETE","READ","ADMINISTRATION");
		
		accedirPantallaConsultesDiseny();
		driver.findElement(By.xpath(xPathBotoConsultarExpedients)).click();
		
		existeixElementAssert("//*[@id='registre']/tbody/tr[1]/td[2]/a", "No se tenía ningún expediente");
		
		screenshotHelper.saveScreenshot("expedients/informes/l1_1_tramitacio_tasca-estat_inicial.png");
		
		// Vamos a la tarea a tramitar del expediente
		String nomExpediente = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]/td[2]/a")).getText();
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]/td[2]/a")).click();
		
		screenshotHelper.saveScreenshot("expedients/informes/l1_2_tramitacio_tasca-fitxa_expedient.png");
		
		assertTrue("No se llegó a la pantalla de tasques pendents", "Tasques pendents".equals(driver.findElement(By.xpath("//*[@id='page-title']/h2/span")).getText()));
		assertTrue("No se realizó la búsqueda con el nombre del expediente correcto", nomExpediente.equals(driver.findElement(By.xpath("//*[@id='expedient0']")).getAttribute("value")));
		
		if (existeixElement("//*[@id='registre']/tbody/tr[1]/td[1]/a")) {
			String nomTasca = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]/td[1]/a")).getText();
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]/td[1]/a")).click();
			
			screenshotHelper.saveScreenshot("expedients/informes/l1_3_tramitacio_tasca-pantalla_tasca.png");
			
			assertTrue("No se llegó a la pantalla de tramitación de la tarea", nomTasca.equals(driver.findElement(By.xpath("//*[@id='page-title']/h2/span")).getText()));
		}
	}

	@Test
	public void m_obrir_expedient() throws InterruptedException, ParseException {
	
		carregarUrlDisseny();
		seleccionarEntorn(titolEntorn);
		
		accedirPantallaConsultesDiseny();
		
		driver.findElement(By.xpath(xPathBotoConsultarExpedients)).click();
		
		screenshotHelper.saveScreenshot("expedients/informes/l2_1_obrir_expedient-estat_inicial.png");
		
		// Asignamos permisos
//		assignarPermisosTipusExpedient(codTipusExp1, usuari, "DESIGN","CREATE","SUPERVISION","WRITE","MANAGE","DELETE","READ","ADMINISTRATION");
		
		existeixElementAssert("//*[@id='registre']/tbody/tr[1]/td/a/img[@src = '/helium/img/information.png']", "No exitía la imagen de ver la información de un expediente");
		
		// Abrimos el expediente
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]/td/a/img[@src = '/helium/img/information.png']")).click();
		existeixElementAssert("//*[@id='tabnav']/li/a[contains(text(), 'Expedient')]", "No exitía la pestaña de expediente");
		
		screenshotHelper.saveScreenshot("expedients/informes/l2_2_obrir_expedient-accedit_per_boto_info.png");
	}

	@Test
	public void n1_aturar_expedient() throws InterruptedException, ParseException {
		
		carregarUrlConfiguracio();
		seleccionarEntorn(titolEntorn);
		
		// quitamos permisos de borrado
		assignarPermisosTipusExpedient(codTipusExp1, usuari, "READ");
	}
	
	@Test
	public void n2_aturar_expedient() throws InterruptedException, ParseException {
		
		carregarUrlDisseny();
		seleccionarEntorn(titolEntorn);
		
		// Comprobamos permisos de borrado
		accedirPantallaConsultesDiseny();
		driver.findElement(By.xpath(xPathBotoConsultarExpedients)).click();
		
		existeixElementAssert("//*[@id='registre']/tbody/tr[1]/td/a/img[@src = '/helium/img/information.png']", "No existía la imagen de ver la información de un expediente");
		
		// Abrimos el expediente
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]/td/a/img[@src = '/helium/img/information.png']")).click();
		
		noExisteixElementAssert("//*[@id='tabnav']/li/a[contains(text(), 'Eines')]", "Tenía permisos de eines");
	}
	
	@Test
	public void n3_aturar_expedient() throws InterruptedException, ParseException {
		
		carregarUrlConfiguracio();
		seleccionarEntorn(titolEntorn);
		
		// Comprobamos permisos de borrado
		// Ponemos los permisos
		assignarPermisosTipusExpedient(codTipusExp1, usuari, "CREATE","WRITE","DELETE","READ");
	}
	
	@Test
	public void n4_aturar_expedient() throws InterruptedException, ParseException {
		
		carregarUrlDisseny();
		seleccionarEntorn(titolEntorn);

		accedirPantallaConsultesDiseny();

		driver.findElement(By.xpath(xPathBotoConsultarExpedients)).click();
		
		Thread.sleep(5000);
		
		screenshotHelper.saveScreenshot("expedients/informes/n_1_aturar_expedient-estat_inicial.png");

		// Abrimos el expediente
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]/td/a/img[@src = '/helium/img/information.png']")).click();

		existeixElementAssert("//*[@id='tabnav']/li/a[contains(text(), 'Eines')]", "No tenía permisos de herramientas");

		noExisteixElementAssert("//*[@id='content']/div[@class='missatgesAturat']", "El expediente ya estaba parado");
		
		screenshotHelper.saveScreenshot("expedients/informes/n_2_aturar_expedient-info_expedient.png");
		
		// Paramos el expediente
		driver.findElement(By.xpath("//*[@id='tabnav']/li/a[contains(text(), 'Eines')]")).click();
		driver.findElement(By.xpath("//*[@id='content']/div/h3[1]/a")).click();
		driver.findElement(By.xpath("//*[@id='motiu0']")).sendKeys("El motivo");
		driver.findElement(By.xpath("//button[contains(text(), 'Aturar')]")).click();
		acceptarAlerta();
		
		existeixElementAssert("//*[@id='infos']/p", "No para el expediente correctamente");
		existeixElementAssert("//*[@id='content']/div[@class='missatgesAturat']", "No existía el mensaje de parar expediente");
		
		screenshotHelper.saveScreenshot("expedients/informes/n_3_aturar_expedient-expedient_aturat.png");
	}
	
	@Test
	public void o_anular_expedient() throws InterruptedException, ParseException {
		
		carregarUrlDisseny();
		seleccionarEntorn(titolEntorn);
		
//		// Comprobamos permisos de borrado
//		// Los quitamos
//		assignarPermisosTipusExpedient(codTipusExp1, usuari, "DESIGN","CREATE","SUPERVISION","DELETE","MANAGE","READ");
//		consultarExpedientes(null, null, nomTipusExp1);
//		noExisteixElementAssert("//*[@id='registre']/tbody/tr[1]/td[contains(a/img/@src,'/helium/img/delete.png')]/a/img", "Tenía permisos de anulado");
//		// Los ponemos
//		assignarPermisosTipusExpedient(codTipusExp1, usuari, "DESIGN","CREATE","SUPERVISION","WRITE","MANAGE","DELETE","READ","ADMINISTRATION");
		
		accedirPantallaConsultesDiseny();
		driver.findElement(By.xpath(xPathBotoConsultarExpedients)).click();
		
		screenshotHelper.saveScreenshot("expedients/informes/o_1_anular_expedient-estat_inicial.png");
		
		existeixElementAssert("//*[@id='registre']/tbody/tr[2]/td[contains(a/img/@src,'/helium/img/delete.png')]/a/img", "No tenía permisos de anulado");
		
		// Anulamos el primer expediente
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[2]/td[13]/a")).click();
		acceptarConfirm("El motivo");
		
		screenshotHelper.saveScreenshot("expedients/informes/o_2_anular_expedient-confirm_acceptat.png");
	}
	
	@Test
	public void p1_borrar_expedient() throws InterruptedException, ParseException {
		carregarUrlConfiguracio();
		seleccionarEntorn(titolEntorn);		
		Thread.sleep(3000);
		// Quitamos los permisos de borrado
		assignarPermisosTipusExpedient(codTipusExp1, usuari, "DESIGN","CREATE","SUPERVISION","WRITE","MANAGE","READ");
	}
	
	@Test
	public void p2_borrar_expedient() throws InterruptedException, ParseException {	
		//Comprobamos que no hay permisos de borrado
		carregarUrlDisseny();
		seleccionarEntorn(titolEntorn);		
		accedirPantallaConsultesDiseny();
		driver.findElement(By.xpath(xPathBotoConsultarExpedients)).click();		
		noExisteixElementAssert("//*[@id='registre']/tbody/tr[1]/td[contains(a/img/@src,'/helium/img/cross.png')]/a/img", "Tenía permisos de borrado");
	}		
	
	@Test
	public void p3_borrar_expedient() throws InterruptedException, ParseException {
		carregarUrlConfiguracio();
		seleccionarEntorn(titolEntorn);		
		// Asignamos permisos de borrado
		assignarPermisosTipusExpedient(codTipusExp1, usuari, "DESIGN","CREATE","SUPERVISION","WRITE","MANAGE","DELETE","READ","ADMINISTRATION");
	}
	
	@Test
	public void p4_borrar_expedient() throws InterruptedException, ParseException {	
		
		carregarUrlDisseny();
		seleccionarEntorn(titolEntorn);
		
		accedirPantallaConsultesDiseny();
		driver.findElement(By.xpath(xPathBotoConsultarExpedients)).click();
		
		screenshotHelper.saveScreenshot("expedients/informes/p_1_borrar_expedient-estat_inicial.png");
		
		existeixElementAssert("//*[@id='registre']/tbody/tr[1]/td[contains(a/img/@src,'/helium/img/cross.png')]/a/img", "No tenía permisos de borrado");
		
		// Borramos el primer expediente de la lista
		borrarPrimerExpediente();
		
		screenshotHelper.saveScreenshot("expedients/informes/p_2_borrar_expedient-primer_expedient_borrat.png");
	}
	
	@Test
	public void z0_limpiar() {
		carregarUrlConfiguracio();
		seleccionarEntorn(titolEntorn);
		// Eliminam tots els expedients
		consultarExpedientes(null, null, nomTipusExp, true);
		while (existeixElement("//*[@id='registre']/tbody/tr[1]/td[contains(a/img/@src,'/helium/img/cross.png')]/a/img")) {
			borrarPrimerExpediente();
		}
	}
	
	@Test
	public void z1_limpiar() {
		
		carregarUrlConfiguracio();
		seleccionarEntorn(titolEntorn);
		eliminarConsultaTipus(nomConsulta);
		eliminarDefinicioProces(nomDefProc);
		eliminarEnumeracio("enumeracio");		
		eliminarTipusExpedient(codTipusExp1);		
		eliminarEntorn(entorn);
	}
	
	// ***************************************************************************************
	// M E T O D E S    P R I V A T S
	// ***************************************************************************************
	
	/**
	 * Crea una tarea para el expediente
	 * @param i
	 */
	private void tramitarTasca(int i) {
		
		//String sequenciaExp = "CON1-"+i+"/"+anyActual;
		
		actions.moveToElement(driver.findElement(By.id("menuTasques")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/tasca/personaLlistat.html')]")));
		actions.click();
		actions.build().perform();
		
		//driver.findElement(By.xpath("//*[@id='registre']/tbody/tr/td/a[contains(text(), '"+sequenciaExp+"')]")).click();
		//driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td/a, '"+sequenciaExp+"')]/td/a[contains(@href, '/tasca/info.html')]")).click();
		////*[@id='registre']/tbody/tr[contains(td/a, 'CON1-1/2014')]/td/a[contains(@href, '/tasca/info.html')]
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]/td[1]/a")).click();
		
		try { Thread.sleep(2000); }catch (Exception ex) {}
		
		driver.findElement(By.id("v10")).clear();
		driver.findElement(By.id("v10")).sendKeys("Variable 1 - String - " + i);
		
		driver.findElement(By.id("vtitol0")).clear();
		driver.findElement(By.id("vtitol0")).sendKeys("Titol expedient - String - " + i);
		
		if (i%2==1) { //nomes els expedients inparells es marcará aquest booleà
			driver.findElement(By.id("var_boolean0")).click();
		}
		
		NumberFormat nf = new DecimalFormat("00");
		driver.findElement(By.id("var_date0")).clear();
		driver.findElement(By.id("var_date0")).sendKeys(nf.format(i)+"/08/2014");
		
		driver.findElement(By.id("var_float0")).clear();
		driver.findElement(By.id("var_float0")).sendKeys((i*2)+".0");
		
		driver.findElement(By.id("var_int0")).clear();
		driver.findElement(By.id("var_int0")).sendKeys(Integer.toString(i*3));
		
		driver.findElement(By.id("var_price0")).clear();
		driver.findElement(By.id("var_price0")).sendKeys("" + (i*500));
		
		driver.findElement(By.id("var_textarea0")).clear();
		driver.findElement(By.id("var_textarea0")).sendKeys("Textarea Observacion Expedient Tasca "+i);
		
		WebElement select = driver.findElement(By.xpath("//*[@id='var_seleccio0']"));
		List<WebElement> options = select.findElements(By.tagName("option"));		
		
		try { Thread.sleep(3000); }catch(Exception ex){}
		
		if (i<21) {
			options.get(1).click();
		}else{
			options.get(2).click();
		}
		
		screenshotHelper.saveScreenshot("expedients/informes/b1_"+i+"_crear_nova_tasca.png");
		
		driver.findElement(By.xpath("//*/button[contains(text(),'Finalitzar')]")).click();
		
		if (isAlertPresent()) {acceptarAlerta();}
		
		try { Thread.sleep(5000); }catch(Exception ex){}
		
		existeixElementAssert("//*[@id='infos']/p", "No se finalizó correctamente");
	}
	
	/**
	 * Accede a la pantalla de Consultas por tipo a traves del menu Consultas>Consultas por tipo.
	 * Selecciona el tipo de expediente definido en las variables iniciales, así como la consulta.
	 * Comprueba que estemos en la pantalla de la consulta adecuada
	 * @throws InterruptedException
	 */
	private void accedirPantallaConsultesDiseny() throws InterruptedException {
		
		actions.moveToElement(driver.findElement(By.id("menuConsultes")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/expedient/consultaDisseny.html')]")));
		actions.click();
		actions.build().perform();
		
		WebElement selectTipusExpedient = driver.findElement(By.id("expedientTipusId0"));
		List<WebElement> options = selectTipusExpedient.findElements(By.tagName("option"));
		//Seleccionaremos del desplegable de Tipos de expediente el actual
		boolean found = false;
		for (int o=0; (o<options.size() && !found); o++) {
			WebElement opcioSel = options.get(o);
			if (codTipusExp1.equals(opcioSel.getText())) {
				actions.moveToElement(selectTipusExpedient);
//				actions.click();
//				actions.build().perform();
				opcioSel.click();
				found=true;
			}
		}
		
		assertTrue("accedirPantallaConsultesDiseny >> El tipo de expediente "+codTipusExp1+" no está en la lista.", found);
		
		//Esperamos 3 segundos a que se recargue la pantalla con el desplegagle de consultas
		Thread.sleep(3000);
		
		WebElement selectConsultes = driver.findElement(By.id("consultaId0"));
		List<WebElement> optionsConsultes = selectConsultes.findElements(By.tagName("option"));
		//Seleccionaremos del desplegable de Tipos de expediente el actual
		found = false;
		for (int o=0; (o<optionsConsultes.size() && !found); o++) {
			WebElement opcioSel = optionsConsultes.get(o);
			if (nomConsulta.equals(opcioSel.getText())) {
				actions.moveToElement(selectConsultes);
//				actions.click();
//				actions.build().perform();
				opcioSel.click();
				found=true;
			}
		}
		
		assertTrue("accedirPantallaConsultesDiseny >> La consulta "+nomConsulta+" no está en la lista.", found);
		
		//Damos tiempo para que desaparezca la cortinilla.
		//Tarda mas o menos segun el numero de campos de filtro, sobre todo desplegables de enumerados
		Thread.sleep(5000);
		
		//Comprobamos que estamos en la pantalla de filtro correcta
		WebElement formfiltre = driver.findElement(By.xpath("//*[@id='content']/div/h4"));//"//h4[@class='titol-consulta']"));
		assertTrue("No se ha direccionado a la pantalla de consulta adecuada ", nomConsulta.equals(formfiltre.getText()));
	}
	
	private void filtraIcomprovaResultats(String numResTeorics, String prefixeScreenShot) throws InterruptedException {
		
		//Esperam 5 segons a que es carregui la pagina i desaparegui la cortineta
		Thread.sleep(5000);
		
		driver.findElement(By.xpath(xPathBotoConsultarExpedients)).click();
		
		//Esperamos 3s. a que se realize la busqueda
		Thread.sleep(3000);
		
		//Comprovam que hi ha 24 resultats, segons importacions
		WebElement labelResultats = driver.findElement(By.xpath("//*[@id='content']/span[1]"));
		String texteResultats = labelResultats.getText();
		texteResultats = texteResultats.split(",")[0];
		
		if ("1".equals(numResTeorics)) {
			numResTeorics = "un";
		}
		
		screenshotHelper.saveScreenshot(prefixeScreenShot+"_resultats_filtre-Teorics_"+numResTeorics+".png");
		
		//System.out.println("texteResultats="+texteResultats+", compleix teorics: "+(texteResultats.indexOf(numResTeorics)!=-1));
		
		assertTrue("El número de expedientes encontrados no coincide con los que se han importado.", texteResultats.indexOf(numResTeorics)!=-1);
	}

}