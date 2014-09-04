package net.conselldemallorca.helium.test.disseny;

import java.text.SimpleDateFormat;
import java.util.Calendar;

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
	String usuariFeina 	= carregarPropietat("test.base.usuari.feina", "Usuari feina de l'entorn de proves no configurat al fitxer de properties");
	String usuariAdmin  = carregarPropietat("test.base.usuari.configuracio", "Usuari configuració de l'entorn de proves no configurat al fitxer de properties");
	
	String nomTipusExp	= carregarPropietat("tipexp.redireccio.tipus.expedient.titol", "Nom del tipus d'expedient de proves no configurat al fitxer de properties");
	String codTipusExp	= carregarPropietat("tipexp.redireccio.tipus.expedient.nom", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	String pathTipExp	= carregarPropietatPath("tipexp.redireccio.tipus.expedient.path", "Ruta de l´arxiu del tipus d´expedient exportat no configurat al fitxer de properties");
	
	// XPATHS
	String pestanyaRedir  = "//*[@id='tabnav']/li/a[contains(@href, '/expedientTipus/redireccioLlistat.html')]";
	String botoCrearRedir = "//*[@id='command']/div/div[@class='buttonHolder']/button[text() = 'Crear']";
	String enllaçBorrarRedir = "//*[@id='registre']/tbody/tr[1]/td/a[contains(@href, '/expedientTipus/cancelar.html')]";
	
	
	
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
			if (usuariFeina.equals(option.getAttribute("value"))) {
				option.click();
				break;
			}
		}
		
		//Seleccionar usuari desti
		for (WebElement option : driver.findElement(By.id("usuariDesti0")).findElements(By.tagName("option"))) {
			if (usuariAdmin.equals(option.getAttribute("value"))) {
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
		
		//Iniciam una tasca amb l´usuari feina y comprovam que el responsable s´ha redireccionat cap a l´usuari admin
		//que és tal i com s´ha configurat la redirecció a la passa anterior
	}
	
	@Test
	public void d1_eliminar_redireccio() {
		
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
	
}