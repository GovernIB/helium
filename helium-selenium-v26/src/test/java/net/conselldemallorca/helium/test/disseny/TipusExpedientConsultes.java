package net.conselldemallorca.helium.test.disseny;

import static org.junit.Assert.fail;

import java.util.Calendar;

import net.conselldemallorca.helium.test.util.BaseTest;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TipusExpedientConsultes extends BaseTest {
	
								//TEX.9 - Consultes
								//TEX.9.1 - Crear consulta sense informe amb diferents opcions
								//TEX.9.2 - Crear consulta amb informe (simple i multiple)
								//TEX.9.3 - Modificar consulta
								//TEX.9.4 - Variables de informe
									//TEX.9.4.1 - Assignar variables de l'informe (si ja esta assignada no ha de apareixer al desplegable)
									//TEX.9.4.2 - Dessasignar variables de l'informe
									//TEX.9.4.3 - Ordenar variables de l´informe
								//TEX.9.5 - Variables de filtre
									//TEX.9.5.1 - Assignar variables de filtre (si ja esta assignada no ha de apareixer al desplegable)
									//TEX.9.5.2 - Dessasignar variables de filtre
									//TEX.9.5.3 - Ordenar variables de filtre
								//TEX.9.6 - Eliminar consulta
								//TEX.9.7 - Parámetres *
									//TEX.9.7.1 - Assignar parametres a la consulta *
									//TEX.9.7.2 - Eliminar parametres de la consulta *     (* No contemplat al document de proves Selenium HEL3002 de 01/04/2014)	

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
	
	String anyActual = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));
	
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
		
		screenshotHelper.saveScreenshot("tipusExpedient/consultes/b1_1_crear-pipella_consultes.png");
		
		driver.findElement(By.xpath(botoNovaConsulta)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/consultes/b1_2_nova_consulta-estat_inicial.png");
		
		emplenaDadesConsulta(codiConsulta, titolConsulta, descConsulta, "", "PDF", "Valors Predef. Consulta 1111", false, false);
		
		screenshotHelper.saveScreenshot("tipusExpedient/consultes/b1_3_nova_consulta-dades_emplenades.png");
		
		driver.findElement(By.xpath(botoGuardaConsulta)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/consultes/b1_4_nova_consulta-resultat.png");
		
		existeixElementAssert("//*[@class='missatgesOk']", "No s'ha pogut crear la consulta "+codiConsulta+" per el tipus d´expedient "+codTipusExp+".");
		
		screenshotHelper.saveScreenshot("tipusExpedient/consultes/b1_5_nova_consulta-llistat.png");
		
		driver.findElement(By.xpath(botoNovaConsulta)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/consultes/b1_6_nova_consulta_dos-estat_inicial.png");
		
		emplenaDadesConsulta(codiConsulta+"2", titolConsulta+" 2", descConsulta+" 2", "", "CSV", "Valors Predef. Consulta 2222", true, true);
		
		screenshotHelper.saveScreenshot("tipusExpedient/consultes/b1_7_nova_consulta_dos-dades_emplenades.png");
		
		driver.findElement(By.xpath(botoGuardaConsulta)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/consultes/b1_8_nova_consulta_dos-resultat.png");
		
		existeixElementAssert("//*[@class='missatgesOk']", "No s'ha pogut crear la consulta "+codiConsulta+" per el tipus d´expedient "+codTipusExp+".");
	}
	
	@Test
	public void b2_crear_consulta_amb_informe_simple() {
		
		carregarUrlDisseny();
		
		seleccionarEntorn(titolEntorn);
		
		seleccionarTipExp(codTipusExp);
		
		driver.findElement(By.xpath(pestanyaConsultes)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/consultes/b2_1_nova_consulta_simple-pipella_consultes.png");
		
		driver.findElement(By.xpath(botoNovaConsulta)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/consultes/b2_2_nova_consulta_simple-estat_inicial.png");
		
		emplenaDadesConsulta(codiConsulta+"3", titolConsulta+" 3", descConsulta+" 3", informePath, "PDF", "Valors Predef. Consulta 3333", true, false);
		
		screenshotHelper.saveScreenshot("tipusExpedient/consultes/b2_3_nova_consulta_simple-dades_emplenades.png");
		
		driver.findElement(By.xpath(botoGuardaConsulta)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/consultes/b2_4_nova_consulta_simple-resultat.png");
		
		existeixElementAssert("//*[@class='missatgesOk']", "No s'ha pogut crear la consulta "+codiConsulta+" per el tipus d´expedient "+codTipusExp+".");
	}
	
	@Test
	public void b3_crear_consulta_amb_informe_multiple() {
		
		carregarUrlDisseny();
		
		seleccionarEntorn(titolEntorn);
		
		seleccionarTipExp(codTipusExp);
		
		driver.findElement(By.xpath(pestanyaConsultes)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/consultes/b3_1_nova_consulta_simple-pipella_consultes.png");
		
		driver.findElement(By.xpath(botoNovaConsulta)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/consultes/b3_2_nova_consulta_simple-estat_inicial.png");
		
		emplenaDadesConsulta(codiConsulta+"4", titolConsulta+" 4", descConsulta+" 4", informeMPath, "PDF", "Valors Predef. Consulta 4444", true, false);
		
		screenshotHelper.saveScreenshot("tipusExpedient/consultes/b3_3_nova_consulta_simple-dades_emplenades.png");
		
		driver.findElement(By.xpath(botoGuardaConsulta)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/consultes/b3_4_nova_consulta_simple-resultat.png");
		
		existeixElementAssert("//*[@class='missatgesOk']", "No s'ha pogut crear la consulta "+codiConsulta+" amb informe multiple per el tipus d´expedient "+codTipusExp+".");
		
		afegirParametreAconsulta(codTipusExp, codiConsulta+"4", "par4Data", "Desc. Param 4444", "DATE", "tipusExpedient/consultes/b3_5_afegir_param-");
	}
	
	@Test
	public void b4_provar_descarrega_informes() {
		
		carregarUrlDisseny();
		
		seleccionarEntorn(titolEntorn);
		
		iniciarExpediente(codTipusExp, null, null);
		
		screenshotHelper.saveScreenshot("tipusExpedient/consultes/b4_1_descarrega_informes-expedient_iniciat.png");
		
		actions.moveToElement(driver.findElement(By.id("menuConsultes")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/expedient/consultaDisseny.html')]")));
		actions.click();
		actions.build().perform();
		
		screenshotHelper.saveScreenshot("tipusExpedient/consultes/b4_2_descarrega_informes-pantalla_consultes.png");

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
			
			screenshotHelper.saveScreenshot("tipusExpedient/consultes/b4_3_descarrega_informes-seleccionada_consulta_simple.png");
			
			//Boto consultar
			driver.findElement(By.xpath("//*[@id='commandFiltre']//div[@class='buttonHolder']/button[text() = 'Consultar']")).click();
			
			screenshotHelper.saveScreenshot("tipusExpedient/consultes/b4_4_descarrega_informes-consulta_simple_1_consultar.png");
			
			//Boto mostrar informe
			driver.findElement(By.xpath("//*[@id='commandFiltre']//div[@class='buttonHolder']/button[text() = 'Mostrar informe']")).click();
						
			String[] paramNames = {"submit"};
			String[] paramValues = {"informe"};
			postDownloadFile("//*[@id='commandFiltre']", paramNames, paramValues, "expedient/consultaDissenyResultat.html", "expedient/consultaDissenyInforme.html");

			// INFORME 4 (MULTIPLE)

			screenshotHelper.saveScreenshot("tipusExpedient/consultes/b4_4_descarrega_informes-consulta_simple_2_descarregar.png");
			
			//Tornam arrera a la pantalla de selecció de consulta
			driver.findElement(By.xpath("//*[@id='content']/div[@class='missatgesGris']//button[text() = 'Canviar']")).click();			
			
			screenshotHelper.saveScreenshot("tipusExpedient/consultes/b4_5_descarrega_informes-seleccio_consulta.png");
			
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
			
			screenshotHelper.saveScreenshot("tipusExpedient/consultes/b4_6_a_descarrega_informes-seleccionada_consulta_multiple.png");
			
			//Boto consultar
			driver.findElement(By.xpath("//*[@id='commandFiltre']//div[@class='buttonHolder']/button[text() = 'Consultar']")).click();
			
			screenshotHelper.saveScreenshot("tipusExpedient/consultes/b4_6_descarrega_informes-consulta_multiple_1_consultar.png");
			
			//Boto mostrar informe
			driver.findElement(By.xpath("//*[@id='commandFiltre']//div[@class='buttonHolder']/button[text() = 'Mostrar informe']")).click();
			
			driver.findElement(By.id("par4Data0")).sendKeys("12/12/"+anyActual);
			
			screenshotHelper.saveScreenshot("tipusExpedient/consultes/b4_6_descarrega_informes-consulta_multiple_2_descarregar.png");
			
			//driver.findElement(By.xpath("/html/body/div/div/button[text() = 'Generar']")).click();

//			postDownloadFile("//*[@id='paramsCommand']");
			
			screenshotHelper.saveScreenshot("tipusExpedient/consultes/b4_6_descarrega_informes-consulta_multiple_2_descarregar_b.png");
	}
	
	@Test
	public void d1_assignar_variables_filtre() {
		
		carregarUrlDisseny();
		
		seleccionarEntorn(titolEntorn);
		
		seleccionarTipExp(codTipusExp);
		
		driver.findElement(By.xpath(pestanyaConsultes)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/consultes/d1_1_assignar_variables_filtes-pipella_consultes.png");
		
		driver.findElement(By.xpath(botonVarsFiltro)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/consultes/d1_2_assignar_variables_filtes-form_inicial.png");
		
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
		
		screenshotHelper.saveScreenshot("tipusExpedient/consultes/d1_3_1_assignar_variables_filtes-dades.png");
		
		driver.findElement(By.xpath(botoGuarVarFil)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/consultes/d1_3_2_assignar_variables_filtes-resultat.png");
		
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
		
		screenshotHelper.saveScreenshot("tipusExpedient/consultes/d1_3_3_assignar_variables_filtes-dades.png");
		
		driver.findElement(By.xpath(botoGuarVarFil)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/consultes/d1_3_4_assignar_variables_filtes-resultat.png");
		
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
		
		screenshotHelper.saveScreenshot("tipusExpedient/consultes/e1_1_ordenar_variables_filtes-pipella_consultes.png");
		
		driver.findElement(By.xpath(botonVarsFiltro)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/consultes/e1_2_ordenar_variables_filtes-variables_filtre.png");
		
		sortTable("consultaCamp", 1, 2);
		
		screenshotHelper.saveScreenshot("tipusExpedient/consultes/e1_3_ordenar_variables_filtes-canvi_ordenacio_1.png");
		
		sortTable("consultaCamp", 1, 2);
		
		screenshotHelper.saveScreenshot("tipusExpedient/consultes/e1_3_ordenar_variables_filtes-canvi_ordenacio_2.png");
	}
	
	@Test
	public void f1_eliminar_variables_filtre() {
		
		carregarUrlDisseny();
		
		seleccionarEntorn(titolEntorn);
		
		seleccionarTipExp(codTipusExp);
		
		driver.findElement(By.xpath(pestanyaConsultes)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/consultes/f1_1_eliminar_variables_filtes-pipella_consultes.png");
		
		driver.findElement(By.xpath(botonVarsFiltro)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/consultes/f1_2_eliminar_variables_filtes-variables_filtre.png");
		
		int contadorVarsElim = 1;
		while (existeixElement(botonElimVarsFiltro)) {
			driver.findElement(By.xpath(botonElimVarsFiltro)).click();
			if (isAlertPresent()) {acceptarAlerta();}
			
			screenshotHelper.saveScreenshot("tipusExpedient/consultes/f1_3_"+contadorVarsElim+"_eliminar_variables_filtes-resultat_eliminacio.png");
			
			existeixElementAssert("//*[@class='missatgesOk']", "Error al esborrar una variable de una consulta per el tipus d´expedient "+codTipusExp+".");
			
			contadorVarsElim++;
		}
	}
	
	@Test
	public void g1_assignar_variables_informe() {
		
		carregarUrlDisseny();
		
		seleccionarEntorn(titolEntorn);
		
		seleccionarTipExp(codTipusExp);
		
		driver.findElement(By.xpath(pestanyaConsultes)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/consultes/g1_1_assignar_variables_informe-pipella_consultes.png");
		
		driver.findElement(By.xpath(botonVarsInform)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/consultes/g1_2_assignar_variables_informe-variables_informe.png");
		
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
		
		screenshotHelper.saveScreenshot("tipusExpedient/consultes/g1_3_1_assignar_variables_informe-variable1.png");
		
		driver.findElement(By.xpath(botoGuarVarInf)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/consultes/g1_3_2_assignar_variables_informe-variable1_resultat.png");
		
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
		
		screenshotHelper.saveScreenshot("tipusExpedient/consultes/g1_3_3_assignar_variables_informe-variable2.png");
		
		driver.findElement(By.xpath(botoGuarVarInf)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/consultes/g1_3_4_assignar_variables_informe-variable2_resultat.png");
		
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
		
		screenshotHelper.saveScreenshot("tipusExpedient/consultes/h1_1_ordenar_variables_informe-pipella_consultes.png");
		
		driver.findElement(By.xpath(botonVarsInform)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/consultes/h1_2_ordenar_variables_informe-variables_informe.png");
		
		sortTable("consultaCamp", 1, 2);
		
		screenshotHelper.saveScreenshot("tipusExpedient/consultes/h1_3_ordenar_variables_informe-canvi_ordenacio_1.png");
		
		sortTable("consultaCamp", 1, 2);
		
		screenshotHelper.saveScreenshot("tipusExpedient/consultes/h1_4_ordenar_variables_informe-canvi_ordenacio_2.png");
	}
	
	@Test
	public void i1_eliminar_variables_informe() {
		
		carregarUrlDisseny();
		
		seleccionarEntorn(titolEntorn);
		
		seleccionarTipExp(codTipusExp);
		
		driver.findElement(By.xpath(pestanyaConsultes)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/consultes/i1_1_eliminar_variables_informe-pipella_consultes.png");
		
		driver.findElement(By.xpath(botonVarsInform)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/consultes/i1_2_eliminar_variables_informe-llistat.png");
		
		int contadorVarsElim = 1;
		while (existeixElement(botonElimVarsInform)) {
			
			driver.findElement(By.xpath(botonElimVarsInform)).click();
			
			if (isAlertPresent()) {acceptarAlerta();}
			
			screenshotHelper.saveScreenshot("tipusExpedient/consultes/i1_3_"+contadorVarsElim+"_eliminar_variables_informe-resultat_eliminacio.png");
			
			existeixElementAssert("//*[@class='missatgesOk']", "Error al esborrar una variable de una consulta per el tipus d´expedient "+codTipusExp+".");
			
			contadorVarsElim++;
		}
	}

	@Test
	public void j1_assignar_parametres() {
		
		carregarUrlDisseny();
		
		seleccionarEntorn(titolEntorn);
		
		afegirParametreAconsulta(codTipusExp, codiConsulta, "codParam1", "Desc. Param 1111", "TEXT", "tipusExpedient/consultes/j1_1_afegir_parametre");
		
		afegirParametreAconsulta(codTipusExp, codiConsulta, "codParam2", "Desc. Param 2222", "DATE", "tipusExpedient/consultes/j1_2_afegir_parametre");
	}

	@Test
	public void k1_eliminar_parametres() {
		
		carregarUrlDisseny();
		
		seleccionarEntorn(titolEntorn);
		
		seleccionarTipExp(codTipusExp);
		
		driver.findElement(By.xpath(pestanyaConsultes)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/consultes/k1_1_eliminar_parametres-pipella_consultes.png");
		
		driver.findElement(By.xpath(botonParametres)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/consultes/k1_2_eliminar_parametres-llistat.png");
		
		int contadorVarsElim = 1;
		while (existeixElement(botonElimParametres)) {
			
			driver.findElement(By.xpath(botonElimParametres)).click();
			
			if (isAlertPresent()) {acceptarAlerta();}
			
			screenshotHelper.saveScreenshot("tipusExpedient/consultes/k1_3_"+contadorVarsElim+"_eliminar_parametres-resultat_eliminacio.png");
			
			existeixElementAssert("//*[@class='missatgesOk']", "Error al esborrar un parametre de una consulta per el tipus d´expedient "+codTipusExp+".");
			
			contadorVarsElim++;
		}
	}
	
	@Test
	public void l1_modificar_consulta() {
		
		carregarUrlDisseny();
		
		seleccionarEntorn(titolEntorn);
		
		seleccionarTipExp(codTipusExp);
		
		driver.findElement(By.xpath(pestanyaConsultes)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/consultes/l1_1_modificar_consulta-pipella_consultes.png");
		
		driver.findElement(By.xpath(linkConsulta)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/consultes/l1_2_modificar_consulta-dades_inicials.png");
		
		emplenaDadesConsulta(codiConsulta+"mod", titolConsulta+"mod", descConsulta+"mod", "", "RTF", "Valors Predef. Consulta 1111 mod", true, false);
		
		screenshotHelper.saveScreenshot("tipusExpedient/consultes/l1_3_modificar_consulta-dades_modificades.png");
		
		driver.findElement(By.xpath(botoModificaConsulta)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/consultes/l1_4_modificar_consulta-resultat_modificacio.png");
		
		existeixElementAssert("//*[@class='missatgesOk']", "No s'ha pogut modificar la consulta "+codiConsulta+" per el tipus d´expedient "+codTipusExp+".");
		
		driver.findElement(By.xpath(linkConsulta)).click();
			
		comprovaDadesConsulta(codiConsulta+"mod", titolConsulta+"mod", descConsulta+"mod", "", "RTF", "Valors Predef. Consulta 1111 mod", true, false);

		screenshotHelper.saveScreenshot("tipusExpedient/consultes/l1_5_modificar_consulta-comprobar_dades_modificades.png");
	}
	
	@Test
	public void m1_eliminar_consulta() {
		
		carregarUrlDisseny();
		
		seleccionarEntorn(titolEntorn);

		//Eliminam el expedients que s´haguin pogut iniciar abans de borrar les consultes
		eliminarExpedient(null, null, nomTipusExp);
		
		seleccionarTipExp(codTipusExp);
		
		driver.findElement(By.xpath(pestanyaConsultes)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/consultes/m1_1_eliminar_consulta-pipella_consultes.png");
		
		int contadorVarsElim = 1;
		while (existeixElement(botoElimConsul)) {
			
			driver.findElement(By.xpath(botoElimConsul)).click();
			
			if (isAlertPresent()) {acceptarAlerta();}
			
			screenshotHelper.saveScreenshot("tipusExpedient/consultes/m1_2_"+contadorVarsElim+"_eliminar_consulta-resultat_eliminacio.png");
			
			existeixElementAssert("//*[@class='missatgesOk']", "Error al esborrar una consulta per el tipus d´expedient "+codTipusExp+".");
			
			contadorVarsElim++;
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