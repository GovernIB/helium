package net.conselldemallorca.helium.test.disseny;

import static org.junit.Assert.fail;
import net.conselldemallorca.helium.test.util.BaseTest;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TipusExpedientConsultes extends BaseTest {
	
	String entorn 		= carregarPropietat("tipexp.entorn.nom", "Nom de l'entorn de proves no configurat al fitxer de properties");
	String titolEntorn	= carregarPropietat("tipexp.entorn.titol", "Titol de l'entorn de proves no configurat al fitxer de properties");
	
	String usuari 		= carregarPropietat("test.base.usuari.disseny", "Usuari feina de l'entorn de proves no configurat al fitxer de properties");
	String usuariAdmin  = carregarPropietat("test.base.usuari.configuracio", "Usuari configuració de l'entorn de proves no configurat al fitxer de properties");
	
	String nomTipusExp	= carregarPropietat("tipexp.deploy.tipus.expedient.nom", "Nom del tipus d'expedient de proves no configurat al fitxer de properties");
	String codTipusExp	= carregarPropietat("tipexp.deploy.tipus.expedient.codi", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	String pathExportTipExp = carregarPropietatPath("informe.consulta.tipus.expedient.path", "Path de l'entorn de proves no configurat al fitxer de properties");
	String pathExport	= carregarPropietatPath("tipexp.deploy.arxiu.path", "Ruta de l´arxiu del tipus d´expedient exportat no configurat al fitxer de properties");
		
	String defProcNom	= carregarPropietat("informe.deploy.definicio.proces.nom", "Codi de la definicio de proces de proves no configurat al fitxer de properties");
	String defProcPath	= carregarPropietatPath("informe.deploy.definicio.proces.path", "Ruta de l´arxiu de definicio de proces exportada no configurat al fitxer de properties");
	
	String informePath	= carregarPropietatPath("informe.consulta.informe.path", "Ruta de l´arxiu de informe de consulta exportada no configurat al fitxer de properties");
	String informeMPath	= carregarPropietatPath("informe.consulta.informe.multiple.path", "Ruta de l´arxiu de informe de consulta exportada no configurat al fitxer de properties");
	
	String codiEstatTipExp = carregarPropietat("tipexp.deploy.tipus.expedient.estat.codi", "Codi de l'estat del tipus de expedient");
	String nomEstatTipExp  = carregarPropietat("tipexp.deploy.tipus.expedient.estat.nom",  "Nom de l'estat del tipus de expedient");
	
	//XPATHS DE LA CLASSE DE PROVES SOBRE CONSULTES D´UN TIPUS D'EXPEDIENT
	String pestanyaDefsProces	= "//a[contains(@href, '/helium/expedientTipus/definicioProcesLlistat.html')]";
	String pestanyaConsultes	= "//a[contains(@href, '/helium/expedientTipus/consultaLlistat.html')]";
	String botoNovaConsulta		= "//*[@id='content']/form/button";
	String botoGuardaConsulta	= "//*[@id='command']/div[@class='buttonHolder']/button[text() = 'Crear']"; //"//*[@id='command']/div[3]/button[1]";
	String botoModificaConsulta	= "//*[@id='command']/div[@class='buttonHolder']/button[text() = 'Modificar']";
	String botoCancelarConsulta = "//*[@id='command']/div[@class='buttonHolder']/button[text() = 'Cancel·lar']"; //"//*[@id='command']/div[3]/button[2]";
	String enllaçElimConsulta	= "//*[@id='registre']/tbody/tr/td/a[contains(@href, '/expedientTipus/dominiEsborrar.html')]";
								   
	String codiConsulta		= "Consulta Selenium 1";
	String titolConsulta	= "Titol Consulta 1111";
	String descConsulta		= "Descripcio Consulta 1111";
	String pathConsulta		= "";
	String linkConsulta		= "//*[@id='registre']/tbody/tr/td[contains(a, '"+codiConsulta+"')]/a";
	String botoElimConsul	= "//*[@id='registre']/tbody/tr[contains(td/a, '"+codiConsulta+"')]/td/a[contains(@href, '/expedientTipus/consultaEsborrar.html')]"; //"//*[@id='registre']/tbody/tr/td[8]/a";	
	String botoDownlArxiu	= "//*[@id='iconsFileInput_informeContingut0']/a[contains(@href, '/consulta/informeDownload.html')]";
	
	String botonVarsFiltro  = "//*[@id='registre']/tbody/tr[contains(td/a, '"+codiConsulta+"')]/td/form/button[contains(text(), 'filtre')]";
	String botonVarsInform  = "//*[@id='registre']/tbody/tr[contains(td/a, '"+codiConsulta+"')]/td/form/button[contains(text(), 'informe')]";
	String botonParametres  = "//*[@id='registre']/tbody/tr[contains(td/a, '"+codiConsulta+"')]/td/form/button[contains(text(), 'arams')]";

	String botoGuarVarFil	= "//*[@id='command']/div[2]/button[1]";
	String botoGuarVarInf	= "//*[@id='command']/div[2]/button[1]";
	String botoGuarParams	= "//*[@id='command']/div[2]/button[1]";
	
	String botonElimVarsFiltro = "//*[@id='consultaCamp']/tbody/tr[1]/td[4]/a";
	String botonElimVarsInform = "//*[@id='consultaCamp']/tbody/tr[1]/td[4]/a";
	String botonElimParametres = "//*[@id='consultaParam']/tbody/tr[1]/td[4]/a";
		
	@Test
	public void a1_inicialitzacio() {
		
		carregarUrlConfiguracio();
		crearEntorn(entorn, titolEntorn);
		
		assignarPermisosEntorn(entorn, usuariAdmin, "DESIGN", "ORGANIZATION", "READ", "ADMINISTRATION");
		assignarPermisosEntorn(entorn, usuari, "DESIGN", "ORGANIZATION", "READ", "WRITE", "MANAGE", "ADMINISTRATION");
		
		marcarEntornDefecte(titolEntorn);
		seleccionarEntorn(titolEntorn);
		crearTipusExpedient(nomTipusExp, codTipusExp);
		importarDadesTipExp(codTipusExp, pathExportTipExp);
		assignarPermisosTipusExpedient(codTipusExp, usuari, "CREATE", "DESIGN", "MANAGE", "WRITE", "READ", "DELETE");
		
		//desplegarDefPro(TipusDesplegament.EXPORTDEFPRC, defProcNom, nomTipusExp, defProcPath, null, false, false);
		
		//Marcar un proces inicial per poder iniciar expedients
		seleccionarTipExp(codTipusExp);
		driver.findElement(By.xpath(pestanyaDefsProces)).click();
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr/td/form[contains(@action, '/expedientTipus/definicioProcesInicial.html')]/button")).click();
	}
	
	@Test
	public void b1_crear_consulta_sense_informe() {
		
		carregarUrlDisseny();
		
		seleccionarEntorn(titolEntorn);
		
		seleccionarTipExp(codTipusExp);
		
		driver.findElement(By.xpath(pestanyaConsultes)).click();
		
		driver.findElement(By.xpath(botoNovaConsulta)).click();
		
		emplenaDadesConsulta(codiConsulta, titolConsulta, descConsulta, "", "PDF", "Valors Predef. Consulta 1111", false, false);
		
		driver.findElement(By.xpath(botoGuardaConsulta)).click();
		
		existeixElementAssert("//*[@class='missatgesOk']", "No s'ha pogut crear la consulta "+codiConsulta+" per el tipus d´expedient "+codTipusExp+".");
		
		driver.findElement(By.xpath(botoNovaConsulta)).click();
		
		emplenaDadesConsulta(codiConsulta+"2", titolConsulta+" 2", descConsulta+" 2", "", "CSV", "Valors Predef. Consulta 2222", true, true);
		
		driver.findElement(By.xpath(botoGuardaConsulta)).click();
		
		existeixElementAssert("//*[@class='missatgesOk']", "No s'ha pogut crear la consulta "+codiConsulta+" per el tipus d´expedient "+codTipusExp+".");
	}
	
	@Test
	public void b2_crear_consulta_amb_informe_simple() {
		
		carregarUrlDisseny();
		
		seleccionarEntorn(titolEntorn);
		
		seleccionarTipExp(codTipusExp);
		
		driver.findElement(By.xpath(pestanyaConsultes)).click();
		
		driver.findElement(By.xpath(botoNovaConsulta)).click();
		
		emplenaDadesConsulta(codiConsulta+"3", titolConsulta+" 3", descConsulta+" 3", informePath, "PDF", "Valors Predef. Consulta 3333", true, false);
		
		driver.findElement(By.xpath(botoGuardaConsulta)).click();
		
		existeixElementAssert("//*[@class='missatgesOk']", "No s'ha pogut crear la consulta "+codiConsulta+" per el tipus d´expedient "+codTipusExp+".");
	}
	
	@Test
	public void b3_crear_consulta_amb_informe_multiple() {
		
		carregarUrlDisseny();
		
		seleccionarEntorn(titolEntorn);
		
		seleccionarTipExp(codTipusExp);
		
		driver.findElement(By.xpath(pestanyaConsultes)).click();
		
		driver.findElement(By.xpath(botoNovaConsulta)).click();
		
		emplenaDadesConsulta(codiConsulta+"4", titolConsulta+" 4", descConsulta+" 4", informeMPath, "PDF", "Valors Predef. Consulta 4444", true, false);
		
		driver.findElement(By.xpath(botoGuardaConsulta)).click();
		
		existeixElementAssert("//*[@class='missatgesOk']", "No s'ha pogut crear la consulta "+codiConsulta+" amb informe multiple per el tipus d´expedient "+codTipusExp+".");
		
		afegirParametreAconsulta(codTipusExp, codiConsulta+"4", "par4Data", "Desc. Param 4444", "DATE");
	}
	
	@Test
	public void b4_provar_descarrega_informes() {
		
		carregarUrlDisseny();
		
		seleccionarEntorn(titolEntorn);
		
		iniciarExpediente(codTipusExp, null, null);
		
		actions.moveToElement(driver.findElement(By.id("menuConsultes")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/expedient/consultaDisseny.html')]")));
		actions.click();
		actions.build().perform();

		//Seleccionar las consultes 3 i 4 (simple i multiple) i descarregarles amb el boto de mostrar informe
		
			// INFORME 3 (SIMPLE)
		
			//Seleccionar Tipus d´expedient
			for (WebElement option : driver.findElement(By.id("expedientTipusId0")).findElements(By.tagName("option"))) {
				if (codTipusExp.equals(option.getText())) {
					option.click();
					break;
				}
			}
			
			//Seleccionar consulta
			String idConsulta = titolConsulta+" 3";
			for (WebElement option : driver.findElement(By.id("consultaId0")).findElements(By.tagName("option"))) {
				if (idConsulta.equals(option.getText())) {
					option.click();
					break;
				}
			}
			
			//Boto consultar
			driver.findElement(By.xpath("//*[@id='commandFiltre']//div[@class='buttonHolder']/button[text() = 'Consultar']")).click();
			
			//Boto mostrar informe
			driver.findElement(By.xpath("//*[@id='commandFiltre']//div[@class='buttonHolder']/button[text() = 'Mostrar informe']")).click();
			//TODO: Pendent de mirar aixo de aquesta descarrega. Cas especial.
			String[] paramNames = {"submit"};
			String[] paramValues = {"informe"};
			postDownloadFile("//*[@id='commandFiltre']", paramNames, paramValues, "expedient/consultaDissenyResultat.html", "expedient/consultaDissenyInforme.html");
			
			
			// INFORME 4 (MULTIPLE)
						
			//Tornam arrera a la pantalla de selecció de consulta
			driver.findElement(By.xpath("//*[@id='content']/div[@class='missatgesGris']//button[text() = 'Canviar']")).click();			
			
			//Seleccionar Tipus d´expedient
			for (WebElement option : driver.findElement(By.id("expedientTipusId0")).findElements(By.tagName("option"))) {
				if (codTipusExp.equals(option.getText())) {
					option.click();
					break;
				}
			}
			
			//Seleccionar consulta
			idConsulta = titolConsulta+" 4";
			for (WebElement option : driver.findElement(By.id("consultaId0")).findElements(By.tagName("option"))) {
				if (idConsulta.equals(option.getText())) {
					option.click();
					break;
				}
			}
			
			//Boto consultar
			driver.findElement(By.xpath("//*[@id='commandFiltre']//div[@class='buttonHolder']/button[text() = 'Consultar']")).click();
			
			//Boto mostrar informe
			driver.findElement(By.xpath("//*[@id='commandFiltre']//div[@class='buttonHolder']/button[text() = 'Mostrar informe']")).click();
			//TODO: Pendent de mirar aixo de aquesta descarrega. Cas especial.
			postDownloadFile("//*[@id='commandFiltre']", paramNames, paramValues, "expedient/consultaDissenyResultat.html", "expedient/consultaDissenyInforme.html");
	}
	
	@Test
	public void d1_assignar_variables_filtre() {
		
		carregarUrlDisseny();
		
		seleccionarEntorn(titolEntorn);
		
		seleccionarTipExp(codTipusExp);
		
		driver.findElement(By.xpath(pestanyaConsultes)).click();
		
		driver.findElement(By.xpath(botonVarsFiltro)).click();
		
		for (WebElement option : driver.findElement(By.id("defprocJbpmKey0")).findElements(By.tagName("option"))) {
			if (defProcNom.equals(option.getText())) {
				option.click();
				break;
			}
		}
		
		for (WebElement option : driver.findElement(By.id("campCodi0")).findElements(By.tagName("option"))) {
			if ("v1".equals(option.getAttribute("value"))) {
				option.click();
				break;
			}
		}
		
		driver.findElement(By.xpath(botoGuarVarFil)).click();
		
		existeixElementAssert("//*[@class='missatgesOk']", "Error a l'insertar una variable de filtre per la consulta "+codiConsulta+" per el tipus d´expedient "+codTipusExp+".");
		
		for (WebElement option : driver.findElement(By.id("defprocJbpmKey0")).findElements(By.tagName("option"))) {
			if (defProcNom.equals(option.getText())) {
				option.click();
				break;
			}
		}
		
		for (WebElement option : driver.findElement(By.id("campCodi0")).findElements(By.tagName("option"))) {
			if ("v3".equals(option.getAttribute("value"))) {
				option.click();
				break;
			}
		}
		
		driver.findElement(By.xpath(botoGuarVarFil)).click();
		
		//Comprovam que les variables de informe ja insertades no apareixen al desplegable
		for (WebElement option : driver.findElement(By.id("defprocJbpmKey0")).findElements(By.tagName("option"))) {
			if (defProcNom.equals(option.getText())) {
				option.click();
				break;
			}
		}
		
		for (WebElement option : driver.findElement(By.id("defprocJbpmKey0")).findElements(By.tagName("option"))) {
			if ("v1".equals(option.getText()) || "v3".equals(option.getText())) {
				fail("La variable booleana o la price segueixen estant a la llista de Variables dins la asignació de Var. Filtre de la consulta del tipus de expedient!");
			}
		}
	}
	
	@Test
	public void e1_ordenar_variables_filtre() {
		
		carregarUrlDisseny();
		
		seleccionarEntorn(titolEntorn);
		
		seleccionarTipExp(codTipusExp);
		
		driver.findElement(By.xpath(pestanyaConsultes)).click();
		
		driver.findElement(By.xpath(botonVarsFiltro)).click();
		
		sortTable("consultaCamp", 1, 2);
		
		sortTable("consultaCamp", 1, 2);
	}
	
	@Test
	public void f1_eliminar_variables_filtre() {
		
		carregarUrlDisseny();
		
		seleccionarEntorn(titolEntorn);
		
		seleccionarTipExp(codTipusExp);
		
		driver.findElement(By.xpath(pestanyaConsultes)).click();
		
		driver.findElement(By.xpath(botonVarsFiltro)).click();
		
		while (existeixElement(botonElimVarsFiltro)) {
			driver.findElement(By.xpath(botonElimVarsFiltro)).click();
			if (isAlertPresent()) {acceptarAlerta();}
			existeixElementAssert("//*[@class='missatgesOk']", "Error al esborrar una variable de una consulta per el tipus d´expedient "+codTipusExp+".");
		}
	}
	
	@Test
	public void g1_assignar_variables_informe() {
		
		carregarUrlDisseny();
		
		seleccionarEntorn(titolEntorn);
		
		seleccionarTipExp(codTipusExp);
		
		driver.findElement(By.xpath(pestanyaConsultes)).click();
		
		driver.findElement(By.xpath(botonVarsInform)).click();
		
		for (WebElement option : driver.findElement(By.id("defprocJbpmKey0")).findElements(By.tagName("option"))) {
			if (defProcNom.equals(option.getText())) {
				option.click();
				break;
			}
		}
		
		for (WebElement option : driver.findElement(By.id("campCodi0")).findElements(By.tagName("option"))) {
			if ("v1".equals(option.getAttribute("value"))) {
				option.click();
				break;
			}
		}
		
		driver.findElement(By.xpath(botoGuarVarInf)).click();
		
		existeixElementAssert("//*[@class='missatgesOk']", "Error a l'insertar una variable de filtre per la consulta "+codiConsulta+" per el tipus d´expedient "+codTipusExp+".");
		
		for (WebElement option : driver.findElement(By.id("defprocJbpmKey0")).findElements(By.tagName("option"))) {
			if (defProcNom.equals(option.getText())) {
				option.click();
				break;
			}
		}
		
		for (WebElement option : driver.findElement(By.id("campCodi0")).findElements(By.tagName("option"))) {
			if ("v2".equals(option.getAttribute("value"))) {
				option.click();
				break;
			}
		}
		
		driver.findElement(By.xpath(botoGuarVarInf)).click();
		
		//Comprovam que les variables de informe ja insertades no apareixen al desplegable
		for (WebElement option : driver.findElement(By.id("defprocJbpmKey0")).findElements(By.tagName("option"))) {
			if (defProcNom.equals(option.getText())) {
				option.click();
				break;
			}
		}
		
		for (WebElement option : driver.findElement(By.id("defprocJbpmKey0")).findElements(By.tagName("option"))) {
			if ("v1".equals(option.getText()) || "v2".equals(option.getText())) {
				fail("La variable booleana o la price segueixen estant a la llista de Variables dins la asignació de Var. Filtre de la consulta del tipus de expedient!");
			}
		}
	}
	
	@Test
	public void h1_ordenar_variables_informe() {
		
		carregarUrlDisseny();
		
		seleccionarEntorn(titolEntorn);
		
		seleccionarTipExp(codTipusExp);
		
		driver.findElement(By.xpath(pestanyaConsultes)).click();
		
		driver.findElement(By.xpath(botonVarsInform)).click();
		
		sortTable("consultaCamp", 1, 2);
		
		sortTable("consultaCamp", 1, 2);
	}
	
	@Test
	public void i1_eliminar_variables_informe() {
		
		carregarUrlDisseny();
		
		seleccionarEntorn(titolEntorn);
		
		seleccionarTipExp(codTipusExp);
		
		driver.findElement(By.xpath(pestanyaConsultes)).click();
		
		driver.findElement(By.xpath(botonVarsInform)).click();
		
		while (existeixElement(botonElimVarsInform)) {
			driver.findElement(By.xpath(botonElimVarsInform)).click();
			if (isAlertPresent()) {acceptarAlerta();}
			existeixElementAssert("//*[@class='missatgesOk']", "Error al esborrar una variable de una consulta per el tipus d´expedient "+codTipusExp+".");
		}
	}

	@Test
	public void j1_assignar_parametres() {
		
		carregarUrlDisseny();
		
		seleccionarEntorn(titolEntorn);
		
		afegirParametreAconsulta(codTipusExp, codiConsulta, "codParam1", "Desc. Param 1111", "TEXT");
		
		afegirParametreAconsulta(codTipusExp, codiConsulta, "codParam2", "Desc. Param 2222", "DATE");
	}

	@Test
	public void k1_eliminar_parametres() {
		
		carregarUrlDisseny();
		
		seleccionarEntorn(titolEntorn);
		
		seleccionarTipExp(codTipusExp);
		
		driver.findElement(By.xpath(pestanyaConsultes)).click();
		
		driver.findElement(By.xpath(botonParametres)).click();
		
		while (existeixElement(botonElimParametres)) {
			driver.findElement(By.xpath(botonElimParametres)).click();
			if (isAlertPresent()) {acceptarAlerta();}
			existeixElementAssert("//*[@class='missatgesOk']", "Error al esborrar un parametre de una consulta per el tipus d´expedient "+codTipusExp+".");
		}
	}
	
	@Test
	public void l1_modificar_consulta() {
		
		carregarUrlDisseny();
		
		seleccionarEntorn(titolEntorn);
		
		seleccionarTipExp(codTipusExp);
		
		driver.findElement(By.xpath(pestanyaConsultes)).click();
		
		driver.findElement(By.xpath(linkConsulta)).click();
		
		emplenaDadesConsulta(codiConsulta+"mod", titolConsulta+"mod", descConsulta+"mod", "", "RTF", "Valors Predef. Consulta 1111 mod", true, false);
		
		driver.findElement(By.xpath(botoModificaConsulta)).click();
		
		existeixElementAssert("//*[@class='missatgesOk']", "No s'ha pogut modificar la consulta "+codiConsulta+" per el tipus d´expedient "+codTipusExp+".");
		
		driver.findElement(By.xpath(linkConsulta)).click();
		
		comprovaDadesConsulta(codiConsulta+"mod", titolConsulta+"mod", descConsulta+"mod", "", "RTF", "Valors Predef. Consulta 1111 mod", true, false);
	}
	
	@Test
	public void m1_eliminar_consulta() {
		
		carregarUrlDisseny();
		
		seleccionarEntorn(titolEntorn);

		//Eliminam el expedients que s´haguin pogut iniciar abans de borrar les consultes
		eliminarExpedient(null, null, nomTipusExp);
		
		seleccionarTipExp(codTipusExp);
		
		driver.findElement(By.xpath(pestanyaConsultes)).click();
		
		while (existeixElement(botoElimConsul)) {
			driver.findElement(By.xpath(botoElimConsul)).click();
			if (isAlertPresent()) {acceptarAlerta();}
			existeixElementAssert("//*[@class='missatgesOk']", "Error al esborrar una consulta per el tipus d´expedient "+codTipusExp+".");
		}
	}

	@Test
	public void z0_finalitzacio() {
		carregarUrlConfiguracio();
		eliminarTotsEstatsTipusExpedient(codTipusExp);
		eliminarEnumeracio("enumeracio");
		eliminarTipusExpedient(codTipusExp);
		eliminarDefinicioProces(defProcNom);
		eliminarEntorn(entorn);
	}

}