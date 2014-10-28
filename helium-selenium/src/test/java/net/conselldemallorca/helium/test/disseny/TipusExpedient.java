package net.conselldemallorca.helium.test.disseny;

import static org.junit.Assert.fail;

import java.util.Calendar;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import net.conselldemallorca.helium.test.util.BaseTest;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TipusExpedient extends BaseTest {

										//TEX.1 - Crear
											//TEX.1.1 - Desplegar exportació i comprovar dades desplegades
											//TEX.1.2 - Nou tipus de expedient basic (codi i titol)
											//TEX.1.3 - Definir reinici de sequencia anual
											//TEX.1.4 - Definir sequencia anual i provar la seva sintaxi

	String entorn 		= carregarPropietat("tipexp.entorn.nom", "Nom de l'entorn de proves no configurat al fitxer de properties");
	String titolEntorn	= carregarPropietat("tipexp.entorn.titol", "Titol de l'entorn de proves no configurat al fitxer de properties");

	String usuari 		= carregarPropietat("test.base.usuari.disseny", "Usuari feina de l'entorn de proves no configurat al fitxer de properties");
	String usuariAdmin  = carregarPropietat("test.base.usuari.configuracio", "Usuari configuració de l'entorn de proves no configurat al fitxer de properties");

	String nomTipusExp	= carregarPropietat("tipexp.deploy.info.tipus.expedient.nom", "Nom del tipus d'expedient de proves no configurat al fitxer de properties");
	String codTipusExp	= carregarPropietat("tipexp.deploy.info.tipus.expedient.codi", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	String pathExport	= carregarPropietatPath("tipexp.deploy.arxiu.path", "Ruta de l´arxiu del tipus d´expedient exportat no configurat al fitxer de properties");

	String defProcNom	= carregarPropietat("informe.deploy.definicio.proces.nom", "Codi de la definicio de proces de proves no configurat al fitxer de properties");
	String defProcPath	= carregarPropietatPath("informe.deploy.definicio.proces.path", "Ruta de l´arxiu de definicio de proces exportada no configurat al fitxer de properties");

	String codiEstatTipExp = carregarPropietat("tipexp.deploy.tipus.expedient.estat.codi", "Codi de l'estat del tipus de expedient");
	String nomEstatTipExp  = carregarPropietat("tipexp.deploy.tipus.expedient.estat.nom",  "Nom de l'estat del tipus de expedient");

	String responsableTipusExp = carregarPropietat("test.base.usuari.disseny.nom",  "Nom del responsable del tipus de expedient no configurat al fitxer de properties");

	String expressioSeq = "CZ ${seq}/${any}";

	int anyActual = Calendar.getInstance().get(Calendar.YEAR);
	int seqTipExp = 10;

	int anyActual_2 = anyActual-1;
	int seqTipExp_2 = 20;

	@Test
	public void a1_inicialitzacio() {
		carregarUrlConfiguracio();
		crearEntorn(entorn, titolEntorn);
		assignarPermisosEntorn(entorn, usuariAdmin, "DESIGN", "ORGANIZATION", "READ", "ADMINISTRATION");
		assignarPermisosEntorn(entorn, usuari, "DESIGN", "ORGANIZATION", "READ", "ADMINISTRATION");
		marcarEntornDefecte(titolEntorn);
		seleccionarEntorn(titolEntorn);
	}
	
	@Test
	public void b1_crear_basic() {
		carregarUrlConfiguracio();
		seleccionarEntorn(titolEntorn);
		crearTipusExpedient(nomTipusExp, codTipusExp, "tipusExpedient/crear/b1_");
		assignarPermisosTipusExpedient(codTipusExp, usuariAdmin, "CREATE", "DESIGN", "MANAGE", "READ", "DELETE");
		assignarPermisosTipusExpedient(codTipusExp, usuari, 	 "CREATE", "DESIGN", "MANAGE", "READ", "DELETE");
		eliminarTipusExpedient(codTipusExp);
	}

	@Test
	public void c1_crear_reinici_sequencia_anual() {
		//Cream el tipus d´expedient i assignam permisos perque l´usuari "feina" el pugui inicia i comprovar
		carregarUrlConfiguracio();
		seleccionarEntorn(titolEntorn);
		creaTipusExpedientAmbSequenciaAnual();
		assignarPermisosTipusExpedient(codTipusExp, usuariAdmin, "CREATE", "DESIGN", "MANAGE", "READ", "DELETE");
		assignarPermisosTipusExpedient(codTipusExp, usuari, 	 "CREATE", "DESIGN", "MANAGE", "READ", "DELETE");
		//Assignem la definició de proces a l´expedient
		desplegarDefPro(TipusDesplegament.EXPORTDEFPRC, defProcNom, nomTipusExp, defProcPath, null, false, false);
		//Marcam com a procés inicial
		marcarDefinicioProcesInicial(codTipusExp, defProcNom);
	}
	
	@Test
	public void c2_comprova_sequencia_anual() {
		//Un cop creat el tipus d´expedient i iniciat, comprovam que es fà ús de la sequencia creada
		carregarUrlDisseny();
		seleccionarEntorn(titolEntorn);
		crearEstatTipusExpedient(codTipusExp, codiEstatTipExp, nomEstatTipExp);
		comprovaSeqAnual();
	}
		
	@Test
	public void e1_desplegar_exportacio() {
		carregarUrlDisseny();
		seleccionarEntorn(titolEntorn);
		importarDadesTipExp(codTipusExp, pathExport, "tipusExpedient/crear/e1");
	}
	
	@Test
	public void e2_comprovar_desplegament_exportacio() throws Exception {
		
		carregarUrlDisseny();
		seleccionarEntorn(titolEntorn);
		
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/expedientTipus/llistat.html')]")));
		actions.click();
		actions.build().perform();

		screenshotHelper.saveScreenshot("tipusExpedient/crear/e2_1_comprovar_importacio-inici.png");
		
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'Expedient Importat')]", "No es troba el tipus d´expedient Importat");
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'Expedient Importat')]")).click();
		
		//Pestanya de dades generals (Informació)
		driver.findElement(By.xpath("//*[@id='content']/div[3]/form[1]/button")).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/crear/e2_2_comprovar_importacio-dades_generals.png");
		
		if (!"Expedient Desplegat".equals(driver.findElement(By.id("nom0")).getAttribute("value"))) { fail("El nom del tipus d´expedient no coincideix amb l´esperat (Expedient Desplegat)"); }
		if (!checkboxSelected("//*[@id='teTitol0']", true)) {fail("El check 'Amb títol' del tipus d´expedient hauria de estar seleccionat"); }
		if (!checkboxSelected("//*[@id='demanaTitol0']", true)) {fail("El check 'Demana títol' del tipus d´expedient hauria de estar seleccionat"); }
		if (!checkboxSelected("//*[@id='teNumero0']", true)) {fail("El check 'Amb numero' del tipus d´expedient hauria de estar seleccionat"); }
		if (!checkboxSelected("//*[@id='demanaNumero0']", true)) {fail("El check 'Demana numero' del tipus d´expedient hauria de estar seleccionat"); }
		if (!"EXP_CALC_NUM".equals(driver.findElement(By.id("expressioNumero0")).getAttribute("value"))) { fail("La expressió calculada del tipus d´expedient no coincideix amb l´esperada (EXP_CALC_NUM)"); }
		if (!"1".equals(driver.findElement(By.id("sequencia0")).getAttribute("value"))) { fail("La sequencia actual del tipus d´expedient no coincideix amb l´esperada (1)"); }
		
		if (!checkboxSelected("//*[@id='restringirPerGrup0']", true)) {fail("El check 'Restringir accés segons el grups de l'usuari' del tipus d´expedient hauria de estar seleccionat"); }
		if (!checkboxSelected("//*[@id='seleccionarAny0']", true)) {fail("El check 'Permetre seleccionar any d'inici d'expedient' del tipus d´expedient hauria de estar seleccionat"); }
		
		if (!responsableTipusExp.equals(driver.findElement(By.id("suggest_responsableDefecteCodi0")).getAttribute("value"))) { fail("El responsable del tipus d´expedient no coincideix amb l´esperat ("+responsableTipusExp+")"); }
		
		driver.findElement(By.xpath("//*[@id='command']/div[3]/button[2]")).click();
		
		//Pestanya Estats

		driver.findElement(By.xpath("//a[contains(@href, '/helium/expedientTipus/estats.html')]")).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/crear/e2_3_comprovar_importacio-estats.png");
		
		existeixElementAssert("//*[@id='registre']/tbody/tr/td/a[contains(@href, '/helium/expedientTipus/estatsForm.html')]", "L´estat de l´expedient amb codi (estat1) no es troba");
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[2],'ESTAT IMPORTAT')]", "L´estat de l´expedient amb nom (ESTAT IMPORTAT) no es troba");
		
		//Pestanya Definició procés

		driver.findElement(By.xpath("//a[contains(@href, '/helium/expedientTipus/definicioProcesLlistat.html')]")).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/crear/e2_4_comprovar_importacio-def_proc.png");
		
		Thread.sleep(2000);
		
		existeixElementAssert("//a[contains(@href, '/helium/definicioProces/info.html')]", "La definició de proces amb codi (Cons1) no es troba");
		//existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[2],'1')]", "La definició de proces amb versio (1) no es troba");
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[4],'Si')]", "La definició de proces amb inicial=(Si) no es troba");
		
		//Pestanya integració amb tramits
		
		driver.findElement(By.xpath("//a[contains(@href, '/helium/expedientTipus/sistra.html')]")).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/crear/e2_5_comprovar_importacio-tramits.png");
		
		if (!checkboxSelected("//*[@id='actiu0']", true)) {fail("El check 'Activar' de la integració amb tramits del tipus d´expedient hauria de estar seleccionat"); }
		if (!"TramitImportacio".equals(driver.findElement(By.id("codiTramit0")).getAttribute("value"))) { fail("L Identificador del tràmit del tipus d´expedient no coincideix amb l´esperada (TramitImportacio)"); }
		
			screenshotHelper.saveScreenshot("tipusExpedient/crear/e2_5_1_comprovar_importacio-tramit_inside.png");
		
			//Passam a comprovar les variables definides per la integració
			driver.findElement(By.xpath("//*[@id='variables']/div/button")).click();
			existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'var_float')]", "La variable de integració amb tramits del tipus d´expedient amb codi (var_float) no es troba");
			existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[2],'CodSistraVarFloat')]", "La variable de integració amb tramits del tipus d´expedient amb nom (CodSistraVarFloat) no es troba");
			
			screenshotHelper.saveScreenshot("tipusExpedient/crear/e2_5_1_comprovar_importacio-tramit_inside_variables.png");
			
			//Tornam arrere a la pantalla general de la integració amb tramits
			driver.findElement(By.xpath("//a[contains(@href, '/helium/expedientTipus/sistra.html')]")).click();
			
			//Passam a comprovar el mapeig de documents definits per la integració
			driver.findElement(By.xpath("//*[@id='documents']/div/button")).click();
			existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'CodDocDefPro')]", "El codi helium del mapeig de documents de integració amb tramits del tipus d´expedient amb codi (SitraCodiAdjunt) no es troba");
			existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[2],'Cod Doc Sistra')]", "El codi sistra del mapeig de documents de integració amb tramits del tipus d´expedient amb codi (SitraCodiAdjunt) no es troba");
			
			screenshotHelper.saveScreenshot("tipusExpedient/crear/e2_5_2_comprovar_importacio-tramit_inside_mapeig.png");
			
			//Tornam arrere a la pantalla general de la integració amb tramits
			driver.findElement(By.xpath("//a[contains(@href, '/helium/expedientTipus/sistra.html')]")).click();
			
			//Passam a comprovar els docs. adjunts definits per la integració
			driver.findElement(By.xpath("//*[@id='adjunts.html']/div/button")).click();
			existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'SitraCodiAdjunt')]", "El document adjunt de integració amb tramits del tipus d´expedient amb codi (SitraCodiAdjunt) no es troba");
			
			screenshotHelper.saveScreenshot("tipusExpedient/crear/e2_5_3_comprovar_importacio-tramit_inside_adjunts.png");
			
		//Pestanya integració amb forms
			
		driver.findElement(By.xpath("//a[contains(@href, '/helium/expedientTipus/formext.html')]")).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/crear/e2_6_comprovar_importacio-forms.png");
		
		if (!checkboxSelected("//*[@id='actiu0']", true)) {fail("El check 'Activar' de la integració amb forms del tipus d´expedient hauria de estar seleccionat"); }
		if (!"http://forms.extern.prova".equals(driver.findElement(By.id("url0")).getAttribute("value"))) { fail("La URL de la integració amb forms del tipus d´expedient no coincideix amb l´esperada (http://forms.extern.prova)"); }
		if (!"UsuariServ".equals(driver.findElement(By.id("usuari0")).getAttribute("value"))) { fail("La expressió calculada de la integració amb forms del tipus d´expedient no coincideix amb l´esperada (UsuariServ)"); }
		if (!"ContraServ".equals(driver.findElement(By.id("contrasenya0")).getAttribute("value"))) { fail("La expressió calculada de la integració amb forms del tipus d´expedient no coincideix amb l´esperada (ContraServ)"); }
		
		//Pestanya de Enumeracions
		driver.findElement(By.xpath("//a[contains(@href, '/helium/expedientTipus/enumeracioLlistat.html')]")).click();

		screenshotHelper.saveScreenshot("tipusExpedient/crear/e2_7_comprovar_importacio-enum.png");
		
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'enumeracio')]", "L'enumeracio amb codi (enumeracio) del tipus d´expedient no es troba.");

			//Revisam els valors de l´enumerat
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr/td[3]/form/button")).click();
			
			screenshotHelper.saveScreenshot("tipusExpedient/crear/e2_7_1_comprovar_importacio-enum_valors.png");

			existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'vEnum1')]", "El valor de l´enumeracio amb codi (vEnum1) del tipus d´expedient no es troba.");
			existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[2],'1111')]", "El valor de l´enumeracio amb nom (1111) del tipus d´expedient no es troba.");
			existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[3],'0')]", "El valor de l´enumeracio amb ordre (0) del tipus d´expedient no es troba.");

			existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'vEnum2')]", "El valor de l´enumeracio amb codi (vEnum2) del tipus d´expedient no es troba.");
			existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[2],'2222')]", "El valor de l´enumeracio amb nom (2222) del tipus d´expedient no es troba.");
			existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[3],'1')]", "El valor de l´enumeracio amb ordre (1) del tipus d´expedient no es troba.");

		//Pestanya de documents
		driver.findElement(By.xpath("//a[contains(@href, '/helium/expedientTipus/documentLlistat.html')]")).click();

		screenshotHelper.saveScreenshot("tipusExpedient/crear/e2_8_comprovar_importacio-documents.png");
		
		//Canviam el valor de l´any y tornarem a comprovar el valor del numero (sequencia)
		WebElement selectDefinicionsProces = driver.findElement(By.name("definicioProcesId"));
		List<WebElement> options = selectDefinicionsProces.findElements(By.tagName("option"));
		for (WebElement option : options) {
			if (option.getText().equals(defProcNom)) {
				option.click();
				break;
			}
		}
		
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'CodDocDefPro')]", "El codi del document (CodDocDefPro) del tipus d´expedient no es troba.");
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[2],'Document DefPro #1')]", "El nom del document (Document DefPro #1) del tipus d´expedient no es troba.");
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[3],'No')]", "El valor del camp es plantilla del document (No) del tipus d´expedient no es troba.");
			
		//Pestanya de dominis
		
		driver.findElement(By.xpath("//a[contains(@href, '/helium/expedientTipus/dominiLlistat.html')]")).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/crear/e2_9_comprovar_importacio-dominis.png");
		
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'CodDomini')]", "El domini amb codi (CodDomini) del tipus d´expedient no es troba.");
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'CodDomini')]//a")).click();
		
			screenshotHelper.saveScreenshot("tipusExpedient/crear/e2_9_1_comprovar_importacio-domini_dades.png");
		
			//Passam a les dades del domini
			if (!"CodDomini".equals(driver.findElement(By.id("codi0")).getAttribute("value"))) { fail("El codi del domini del tipus d´expedient no coincideix amb l´esperat (CodDomini)"); }
			if (!"DominiNom".equals(driver.findElement(By.id("nom0")).getAttribute("value"))) { fail("El codi del domini del tipus d´expedient no coincideix amb l´esperat (DominiNom)"); }
			if (!"CONSULTA_SQL".equals(driver.findElement(By.id("tipus0")).getAttribute("value"))) { fail("El codi del domini del tipus d´expedient no coincideix amb l´esperat (CONSULTA_SQL)"); }
			if (!"25".equals(driver.findElement(By.id("cacheSegons0")).getAttribute("value"))) { fail("El codi del domini del tipus d´expedient no coincideix amb l´esperat (25)"); }
			if (!"Desc consulta prova importacin.".equals(driver.findElement(By.id("descripcio0")).getAttribute("value"))) { fail("El codi del domini del tipus d´expedient no coincideix amb l´esperat (Desc consulta prova importacin.)"); }
			if (!"JNDI:data.source:3600".equals(driver.findElement(By.id("jndiDatasource0")).getAttribute("value"))) { fail("El codi del domini del tipus d´expedient no coincideix amb l´esperat (JNDI:data.source:3600)"); }
			if (!"Select from importacio where result='ok';".equals(driver.findElement(By.id("sql0")).getAttribute("value"))) { fail("El codi del domini del tipus d´expedient no coincideix amb l´esperat (Select from importacio where result='ok';)"); }
	
		//Pestanya de consultes
		
		driver.findElement(By.xpath("//a[contains(@href, '/helium/expedientTipus/consultaLlistat.html')]")).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/crear/e2_10_comprovar_importacio-consultes.png");
		
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'codCons')]", "El codi de la consulta del tipus d´expedient (codCons) no es troba.");
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[2],'TitolConsulta')]", "El titol de la consulta del tipus d´expedient (TitolConsulta) no es troba.");
		
			//Passam a revisar les Var.s filtre
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr/td[4]/form/button")).click();
			
			screenshotHelper.saveScreenshot("tipusExpedient/crear/e2_10_1_comprovar_importacio-consultes_vFiltre.png");
			
			existeixElementAssert("//*[@id='consultaCamp']/tbody/tr[contains(td[1],'var_boolean/Variable Boolean')]", "El valor de la variable de la consulta del tipus d´expedient (var_boolean/Variable Boolean) no es troba.");
			existeixElementAssert("//*[@id='consultaCamp']/tbody/tr[contains(td[2],'BOOLEAN')]", "El valor del tipus de la consulta del tipus d´expedient (BOOLEAN) no es troba.");
			
		driver.findElement(By.xpath("//a[contains(@href, '/helium/expedientTipus/consultaLlistat.html')]")).click();
		
			//Passam a revisar les vars informe
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr/td[5]/form/button")).click();
			
			screenshotHelper.saveScreenshot("tipusExpedient/crear/e2_10_2_comprovar_importacio-consultes_vInforme.png");
			
			existeixElementAssert("//*[@id='consultaCamp']/tbody/tr[contains(td[1],'var_textarea/Variable Textarea')]", "El valor de la variable d'informe del tipus d´expedient (var_textarea/Variable Textarea) no es troba.");
			existeixElementAssert("//*[@id='consultaCamp']/tbody/tr[contains(td[2],'TEXTAREA')]", "El valor del tipus d'informe del tipus d´expedient (TEXTAREA) no es troba.");

		driver.findElement(By.xpath("//a[contains(@href, '/helium/expedientTipus/consultaLlistat.html')]")).click();
		
			//Passam a revisar els parametres ()
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr/td[6]/form/button")).click();
			
			screenshotHelper.saveScreenshot("tipusExpedient/crear/e2_10_3_comprovar_importacio-consultes_params.png");
			
			existeixElementAssert("//*[@id='consultaParam']/tbody/tr[contains(td[1],'CodParam')]", "El codi del parametre de la consulta del tipus d´expedient (CodParam) no es troba.");
			existeixElementAssert("//*[@id='consultaParam']/tbody/tr[contains(td[2],'DescParam')]", "La descripció del parametre de la consulta del tipus d´expedient (DescParam) no es troba.");
			existeixElementAssert("//*[@id='consultaParam']/tbody/tr[contains(td[3],'SENCER')]", "El tipus del parametre de la consulta del del tipus d'expedient (SENCER) no es troba.");
	}
	
	@Test
	public void z0_finalitzacio() {
		carregarUrlConfiguracio();
		eliminarExpedient(null, null, nomTipusExp);
		eliminarTotsEstatsTipusExpedient(codTipusExp);
		eliminarTipusExpedient(codTipusExp);
		eliminarDefinicioProces(defProcNom);
	}

	@Test
	public void z1_finalitzacio() {
		carregarUrlConfiguracio();
		eliminarEntorn(entorn);
	}
	
	// ***********************************************
	// F U N C I O N S   P R I V A D E S
	// ***********************************************
	
	private void creaTipusExpedientAmbSequenciaAnual() {
		
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/expedientTipus/llistat.html')]")));
		actions.click();
		actions.build().perform();
		
		if (noExisteixElement("//*[@id='registre']/tbody/tr[contains(td[1],'" + codTipusExp + "')]")) {
			
			driver.findElement(By.xpath("//div[@id='content']/form/button[@class='submitButton']")).click();
			driver.findElement(By.id("codi0")).sendKeys(codTipusExp);
			driver.findElement(By.id("nom0")).sendKeys(nomTipusExp);
			
			driver.findElement(By.id("teNumero0")).click();
			driver.findElement(By.id("demanaNumero0")).click();
			
			//driver.findElement(By.id("expressioNumero0")).sendKeys("CZ 10/"+anyActual);
			driver.findElement(By.id("expressioNumero0")).sendKeys(expressioSeq);
			
			//TODO: WARN: per a la versio 2.11 del driver de chrome, l´expressió no s´envia bé
			//A continuacio proves realitzades per intentar solvertar-ho enviant un char
			
				/*driver.findElement(By.id("expressioNumero0")).clear();
				driver.findElement(By.id("expressioNumero0")).sendKeys("CZ $\\{seq}/$\\{any}");
				driver.findElement(By.id("expressioNumero0")).clear();
				driver.findElement(By.id("expressioNumero0")).sendKeys("CZ $\\\\{seq}/$\\\\{any}");
				driver.findElement(By.id("expressioNumero0")).sendKeys("CZ #{seq}/#{any}");
				String expressioReparada = driver.findElement(By.id("expressioNumero0")).getAttribute("value").replace("#", "${");
				driver.findElement(By.id("expressioNumero0")).sendKeys(expressioReparada);*/
				//char abrirLLave = Character.toChars(0x007B)[0];
				
				//driver.findElement(By.id("expressioNumero0")).sendKeys(Keys.getKeyFromUnicode('\u007B'));
				
				/*Keys[] caracteresRaros = Keys.values();
				for (int k=0; k<caracteresRaros.length; k++) {
					Keys kactual = caracteresRaros[k];
					System.out.println("KEY " + k + " - " + kactual.name() + " = " + Keys.valueOf(kactual.name()));
				}*/
				
				//String s = "\\u007B";
			    //char c = (char)Integer.parseInt(s.substring(2), 16);
			    //char uni = '\007';
				
				//driver.findElement(By.id("expressioNumero0")).sendKeys(Keys.getKeyFromUnicode('c'));
			
			driver.findElement(By.id("reiniciarCadaAny0")).click();
			driver.findElement(By.xpath("//div[@id='seqMultiple']//button[@class='submitButton']")).click();
			
			driver.findElement(By.id("seqany_0")).sendKeys(Integer.toString(anyActual));
			driver.findElement(By.id("seqseq_0")).sendKeys(Integer.toString(seqTipExp));
			driver.findElement(By.xpath("//div[@id='seqMultiple']//button[@class='submitButton']")).click();
			
			driver.findElement(By.id("seqany_1")).sendKeys(Integer.toString(anyActual_2));
			driver.findElement(By.id("seqseq_1")).sendKeys(Integer.toString(seqTipExp_2));
			
			driver.findElement(By.id("seleccionarAny0")).click();
			
			screenshotHelper.saveScreenshot("tipusExpedient/crear/c1_1_crear_tipexp_reinici_seq_anual.png");
			
			driver.findElement(By.xpath("//button[@value='submit']")).click();
			
			actions.moveToElement(driver.findElement(By.id("menuDisseny")));
			actions.build().perform();
			actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/expedientTipus/llistat.html')]")));
			actions.click();
			actions.build().perform();
			existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + codTipusExp + "')]", "No s'ha pogut crear el tipus d'expedient de test");
			
			screenshotHelper.saveScreenshot("tipusExpedient/crear/c1_2_crear_tipexp_comprova_existeix.png");
		}
	}
	
	private void comprovaSeqAnual() {
		
		existeixElementAssert("//li[@id='menuIniciar']", "No tiene permisos para iniciar un expediente");
		
		driver.findElement(By.xpath("//*[@id='menuIniciar']/a")).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/crear/c2_1_comprova_seq_anual-expedient_existeix.png");
		
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + codTipusExp + "')]", "No s'ha trobat el tipus d'expedient");
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codTipusExp + "')]/td[3]/form/button")).click();
		
		if (isAlertPresent()) {
			acceptarAlerta();
		}
		
		String valorNumero = driver.findElement(By.id("numero0")).getAttribute("value");
		String valorEsperat = "CZ "+ seqTipExp +"/" + anyActual;
		
		if (!valorEsperat.equals(valorNumero)) {
			screenshotHelper.saveScreenshot("tipusExpedient/crear/c2_2_comprova_seq_anual-valor_ko.png");
			fail("El numero de l´expedient ("+valorNumero+") no era el que s´esperava ("+valorEsperat+").");
		}else{
			screenshotHelper.saveScreenshot("tipusExpedient/crear/c2_2_comprova_seq_anual-valor_ok.png");
		}
		
		String valorAny = driver.findElement(By.id("any0")).getAttribute("value");
		valorEsperat = Integer.toString(anyActual);
		
		if (!valorEsperat.equals(valorAny)) {
			screenshotHelper.saveScreenshot("tipusExpedient/crear/c2_3_comprova_seq_anual-valor_ko.png");
			fail("L´any per a generar el num expedient ("+valorAny+") no era el que s´esperava ("+valorEsperat+").");
		}else{
			screenshotHelper.saveScreenshot("tipusExpedient/crear/c2_3_comprova_seq_anual-valor_ok.png");
		}
		
		//Canviam el valor de l´any y tornarem a comprovar el valor del numero (sequencia)
		WebElement selectTipusExpedient = driver.findElement(By.id("any0"));
		List<WebElement> options = selectTipusExpedient.findElements(By.tagName("option"));
		for (WebElement option : options) {
			if (option.getText().equals(Integer.toString(anyActual_2))) {
				option.click();
				break;
			}
		}
		
		screenshotHelper.saveScreenshot("tipusExpedient/crear/c3_comprova_seq_anual-canviam_any.png");
		
		valorNumero = driver.findElement(By.id("numero0")).getAttribute("value");
		valorEsperat = "CZ "+ seqTipExp_2 +"/" + anyActual_2;
		
		if (!valorEsperat.equals(valorNumero)) {
			screenshotHelper.saveScreenshot("tipusExpedient/crear/c4_comprova_seq_anual-canvi_any_ko.png");
			fail("El numero de l´expedient ("+valorNumero+") no era el que s´esperava ("+valorEsperat+").");
		}else{
			screenshotHelper.saveScreenshot("tipusExpedient/crear/c4_comprova_seq_anual-canvi_any_ok.png");
		}
	}
}
