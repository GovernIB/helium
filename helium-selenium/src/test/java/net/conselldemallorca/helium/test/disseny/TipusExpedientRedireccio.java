package net.conselldemallorca.helium.test.disseny;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import static org.junit.Assert.fail;
import net.conselldemallorca.helium.test.util.BaseTest;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TipusExpedientRedireccio extends BaseTest {
	
									//TEX.10 - Redirecció
										//TEX.10.1 - Crear i provar
										//TEX.10.2 - Eliminar
	
	String entorn 		= carregarPropietat("tipexp.redireccio.entorn.nom", "Nom de l'entorn de proves no configurat al fitxer de properties");
	String titolEntorn	= carregarPropietat("tipexp.redireccio.entorn.titol", "Titol de l'entorn de proves no configurat al fitxer de properties");
	
	String usuariDisse  = carregarPropietat("test.base.usuari.disseny", "Usuari disseny de l'entorn de proves no configurat al fitxer de properties");
	String usuariDisseNom  = carregarPropietat("test.base.usuari.disseny.nom", "Usuari disseny de l'entorn de proves no configurat al fitxer de properties");
	String usuariFeina 	= carregarPropietat("test.base.usuari.feina", "Usuari feina de l'entorn de proves no configurat al fitxer de properties");
	String usuariFeinaNom 	= carregarPropietat("test.base.usuari.feina.nom", "Usuari feina de l'entorn de proves no configurat al fitxer de properties");
	String usuariAdmin  = carregarPropietat("test.base.usuari.configuracio", "Usuari configuració de l'entorn de proves no configurat al fitxer de properties");
	String usuariAdminNom  = carregarPropietat("test.base.usuari.configuracio.nom", "Usuari configuració de l'entorn de proves no configurat al fitxer de properties");
	
	String nomTipusExp	= carregarPropietat("tipexp.redireccio.tipus.expedient.titol", "Nom del tipus d'expedient de proves no configurat al fitxer de properties");
	String codTipusExp	= carregarPropietat("tipexp.redireccio.tipus.expedient.nom", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	String pathTipExp	= carregarPropietatPath("tipexp.redireccio.tipus.expedient.path", "Ruta de l´arxiu del tipus d´expedient exportat no configurat al fitxer de properties");
	
	// XPATHS
	String pestanyaRedir  	  = "//*[@id='tabnav']/li/a[contains(@href, '/expedientTipus/redireccioLlistat.html')]";
	String botoCrearRedir 	  = "//*[@id='command']/div/div[@class='buttonHolder']/button[text() = 'Crear']";
	String enllaçBorrarRedir  = "//*[@id='registre']/tbody/tr[1]/td/a[contains(@href, '/expedientTipus/cancelar.html')]";
	String botoConsultarExps  = "//*[@id='command']/div/div[@class='buttonHolder']/button[text() = 'Consultar']";
	String botoInformacioExp  = "//*[@id='registre']/tbody/tr[1]/td/a[contains(@href, '/expedient/info.html')]";
	String pestanyaTasques    = "//*[@id='tabnav']/li/a[contains(@href, '/expedient/tasques.html')]";
	String columnaResponsable = "//*[@id='registre']/tbody/tr/td[8]";
	String botoEliminarExp    = "//*[@id='registre']/tbody/tr/td/a[contains(@href, '/expedient/delete.html')]";
	String botoElimReassign	  = "//*[@id='registre']/tbody/tr/td/a[contains(@href, '/reassignar/cancelar.html')]";
	
	
	
	@Test
	public void a1_inicialitzacio() {
		carregarUrlConfiguracio();
		crearEntorn(entorn, titolEntorn);
		assignarPermisosEntorn(entorn, usuariFeina, "DESIGN", "ORGANIZATION", "READ", "ADMINISTRATION");
		assignarPermisosEntorn(entorn, usuariAdmin, "DESIGN", "ORGANIZATION", "READ", "ADMINISTRATION");
		assignarPermisosEntorn(entorn, usuariDisse, "DESIGN", "ORGANIZATION", "READ", "ADMINISTRATION");
		marcarEntornDefecte(titolEntorn);
		seleccionarEntorn(titolEntorn);
	}
	
	@Test
	public void a2_crear_tipexp() {
		
		carregarUrlConfiguracio();
		
		seleccionarEntorn(titolEntorn);
		
		crearTipusExpedient(nomTipusExp, codTipusExp);
		importarDadesTipExp(codTipusExp, pathTipExp);
		
		assignarPermisosTipusExpedient(codTipusExp, usuariDisse, "CREATE", "DESIGN", "MANAGE", "READ", "DELETE");
		assignarPermisosTipusExpedient(codTipusExp, usuariFeina, "CREATE", "DESIGN", "MANAGE", "READ", "DELETE");
		assignarPermisosTipusExpedient(codTipusExp, usuariAdmin, "CREATE", "DESIGN", "MANAGE", "READ", "DELETE");
		
		//Quant tenim tot desplegat, comprovam que no hi ha cap reassignació activa. Ni a nivell de Tip Exp ni a d´Entorn
		
		actions.moveToElement(driver.findElement(By.id("menuConfiguracio")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/reassignar/llistat.html')]")));
		actions.click();
		actions.build().perform();
		
		while(existeixElement(botoElimReassign)) {
			driver.findElement(By.xpath(botoElimReassign)).click();
			if (isAlertPresent()) { acceptarAlerta(); }
		}
		
		seleccionarTipExp(codTipusExp);
		
		driver.findElement(By.xpath(pestanyaRedir)).click();
		
		while (existeixElement(enllaçBorrarRedir)) {			
			driver.findElement(By.xpath(enllaçBorrarRedir)).click();			
			if (isAlertPresent()) {acceptarAlerta();}
		}		
	}
	
	@Test
	public void b0_comprova_usuari_inicial() {
		
		//Comprovam que l´usuari inicial de la tasca es el 173 (Usuari Dissenyador)
		//Així es com està definit al process-definition
		
		carregarUrlDisseny();
		
		seleccionarEntorn(titolEntorn);
		
		iniciarExpediente(codTipusExp, "ExpIni1", "Expedient Redirec Usu Admin");
		
		screenshotHelper.saveScreenshot("tipusExpedient/redireccions/b0_1_comprobacio_inicial-nou_expedient.png");
		
		comprova_responsable_tasca(usuariDisseNom);
		
		screenshotHelper.saveScreenshot("tipusExpedient/redireccions/b0_2_comprobacio_inicial-usuari_defecte.png");
	}
	
	@Test
	public void b1_crear_redireccio() {
		
		carregarUrlDisseny();
		
		seleccionarEntorn(titolEntorn);
		
		seleccionarTipExp(codTipusExp);
		
		driver.findElement(By.xpath(pestanyaRedir)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/redireccions/b1_1_crear_redireccio-pipella_redireccions.png");
		
		//Seleccionar usuari origen
		for (WebElement option : driver.findElement(By.id("usuariOrigen0")).findElements(By.tagName("option"))) {
			if (usuariDisse.equals(option.getAttribute("value"))) {
				option.click();
				break;
			}
		}
		
		//Seleccionar usuari desti
		for (WebElement option : driver.findElement(By.id("usuariDesti0")).findElements(By.tagName("option"))) {
			if (usuariFeina.equals(option.getAttribute("value"))) {
				option.click();
				break;
			}
		}
		
		//Posam com a dates per la redireció d'avull fins a demà
		Calendar calendar = Calendar.getInstance();			
		driver.findElement(By.id("dataInici0")).sendKeys(new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime()));
		
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		driver.findElement(By.id("dataFi0")).sendKeys(new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime()));
		
		screenshotHelper.saveScreenshot("tipusExpedient/redireccions/b1_2_crear_redireccio-dades_emplenades.png");
		
		driver.findElement(By.xpath(botoCrearRedir)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/redireccions/b1_3_crear_redireccio-resultat_insercio.png");
		
		existeixElementAssert("//*[@class='missatgesOk']", "Error al crear la redireccio per el tipus d´expedient "+codTipusExp+".");	
	}
	
	@Test
	public void c1_comprovar_redireccio() {
		
		//Iniciam una tasca amb l´usuari disseny y comprovam que el responsable s´ha redireccionat cap a l´usuari feina
		//que és tal i com s´ha configurat la redirecció a la passa anterior.
		
		carregarUrlDisseny();
		
		seleccionarEntorn(titolEntorn);
		
		iniciarExpediente(codTipusExp, "ExpIni1", "Expedient Redirec Usu Feina");
		
		screenshotHelper.saveScreenshot("tipusExpedient/redireccions/c1_1_provar_redireccio-expedient_iniciat.png");
		
		comprova_responsable_tasca(usuariFeinaNom);
		
		screenshotHelper.saveScreenshot("tipusExpedient/redireccions/c1_2_provar_redireccio-comprovacio_responsable.png");
		
		//Borram l´expedient
		accedirPantallaConsultes();
		
		while (existeixElement(botoEliminarExp)) {
			driver.findElement(By.xpath(botoEliminarExp)).click();
			if (isAlertPresent()) {acceptarAlerta();}
		}
	}
	
	@Test
	public void d1_modificar_redireccio() {
		
		carregarUrlDisseny();
		
		seleccionarEntorn(titolEntorn);
		
		seleccionarTipExp(codTipusExp);
		
		driver.findElement(By.xpath(pestanyaRedir)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/redireccions/d1_1_modificar_redireccio-pipella_redireccions.png");

		//Borram la redireccio creada anteriorment que segueix activa
		while (existeixElement(enllaçBorrarRedir)) {
			driver.findElement(By.xpath(enllaçBorrarRedir)).click();
			if (isAlertPresent()) {acceptarAlerta();}
			existeixElementAssert("//*[@class='missatgesOk']", "Error al borrar la redireccio per el tipus d´expedient "+codTipusExp+".");
		}
		
		screenshotHelper.saveScreenshot("tipusExpedient/redireccions/d1_2_modificar_redireccio-redireccions_netejades.png");
		
		//Seleccionar usuari origen
		for (WebElement option : driver.findElement(By.id("usuariOrigen0")).findElements(By.tagName("option"))) {
			if (usuariDisse.equals(option.getAttribute("value"))) {
				option.click();
				break;
			}
		}
		
		//Canviam usuari desti per Usuari Administrador
		for (WebElement option : driver.findElement(By.id("usuariDesti0")).findElements(By.tagName("option"))) {
			if (usuariAdmin.equals(option.getAttribute("value"))) {
				option.click();
				break;
			}
		}
		
		//Canviam la data inici de la redireccio perque no començi fins d´aqui a 10 dies
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 10);
		driver.findElement(By.id("dataInici0")).sendKeys(new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime()));
		
		calendar.add(Calendar.DATE, 5);
		driver.findElement(By.id("dataFi0")).sendKeys(new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime()));
		
		screenshotHelper.saveScreenshot("tipusExpedient/redireccions/d1_3_modificar_redireccio-dades_emplenades.png");
		
		driver.findElement(By.xpath(botoCrearRedir)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/redireccions/d1_4_modificar_redireccio-resultat_modificacio.png");
		
		existeixElementAssert("//*[@class='missatgesOk']", "Error al modificar la redireccio per el tipus d´expedient "+codTipusExp+".");	
	}
	
	@Test
	public void e1_comprovar_redireccio_fora_de_rang() {
		
		//Iniciam una tasca amb l´usuari disseny y comprovam que el responsable NO s´ha redireccionat cap a l´usuari admin
		//que és tal i com s´ha modificat la redirecció a la passa anterior.
		
		carregarUrlDisseny();
		
		seleccionarEntorn(titolEntorn);
		
		iniciarExpediente(codTipusExp, "ExpIni2", "Expedient Redirec Usu Admin");
		
		screenshotHelper.saveScreenshot("tipusExpedient/redireccions/e1_1_provar_no_redireccio-expedient_iniciat.png");
		
		comprova_responsable_tasca(usuariDisseNom);
		
		screenshotHelper.saveScreenshot("tipusExpedient/redireccions/e1_2_provar_no_redireccio-comprovacio_responsable.png");
		
		//Borram l´expedient
		accedirPantallaConsultes();
		
		while (existeixElement(botoEliminarExp)) {
			driver.findElement(By.xpath(botoEliminarExp)).click();
			if (isAlertPresent()) {acceptarAlerta();}
		}
	}
	
	@Test
	public void f1_eliminar_redireccio() {
		
		carregarUrlDisseny();
		
		seleccionarEntorn(titolEntorn);
		
		seleccionarTipExp(codTipusExp);
		
		driver.findElement(By.xpath(pestanyaRedir)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/redireccions/d1_1_eliminar_redireccio-pipella_redireccions.png");
		
		int contadorEliminats = 1;
		while (existeixElement(enllaçBorrarRedir)) {
			
			driver.findElement(By.xpath(enllaçBorrarRedir)).click();
			
			if (isAlertPresent()) {acceptarAlerta();}
			
			screenshotHelper.saveScreenshot("tipusExpedient/redireccions/d1_2_"+contadorEliminats+"_eliminar_redireccio-resultat_eliminacio.png");
			
			existeixElementAssert("//*[@class='missatgesOk']", "Error al borrar la redireccio per el tipus d´expedient "+codTipusExp+".");
			
			contadorEliminats++;
		}
	}
	
	@Test
	public void z0_finalitzacio() {
		carregarUrlConfiguracio();
		eliminarExpedient(null, null, nomTipusExp);
		eliminarTipusExpedient(codTipusExp);
		eliminarEntorn(entorn);
	}
	
	// ******************************************************
	// F U N C I O N S   P R I V A D E S
	// ******************************************************
	
	private void comprova_responsable_tasca(String responsableTeoric) {
		
		accedirPantallaConsultes();
		
		driver.findElement(By.xpath(botoConsultarExps)).click();
		
		driver.findElement(By.xpath(botoInformacioExp)).click();
		
		driver.findElement(By.xpath(pestanyaTasques)).click();
		
		if (!responsableTeoric.equals(driver.findElement(By.xpath(columnaResponsable)).getText())) {
			fail("El responsable de la tasca no ha estat l´esperat ("+responsableTeoric+")");
		}	
	}
	
	private void accedirPantallaConsultes() {
		actions.moveToElement(driver.findElement(By.id("menuConsultes")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/expedient/consulta.html')]")));
		actions.click();
		actions.build().perform();
	}
}