package net.conselldemallorca.helium.test.configuracio;

import static org.junit.Assert.fail;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;

import net.conselldemallorca.helium.test.util.BaseTest;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ConfiguracioRols extends BaseTest {

								//DFG.2 - Rols
									//DFG.2.1 - Crear
									//DFG.2.2 - Modificar
									//DFG.2.3 - Eliminar
	
	String entorn 		= carregarPropietat("config.aplicacio.entorn.nom", "Nom de l'entorn de proves no configurat al fitxer de properties");
	String titolEntorn	= carregarPropietat("config.rols.entorn.titol", "Titol de l'entorn de proves no configurat al fitxer de properties");
	String usuariAdmin  = carregarPropietat("test.base.usuari.configuracio", "Usuari configuració de l'entorn de proves no configurat al fitxer de properties");

	String codiRol  = "RolCFG21";
	String titolRol = "Rol Proves Selenium CGF2.1";
	
	@Test
	public void a1_inicialitzacio() {
		carregarUrlConfiguracio();
		crearEntorn(entorn, titolEntorn);
		assignarPermisosEntorn(entorn, usuariAdmin, "DESIGN", "ORGANIZATION", "READ", "ADMINISTRATION");
		marcarEntornDefecte(titolEntorn);
		seleccionarEntorn(titolEntorn);
	}
	
	@Test
	public void b1_crear_rol() {
		carregarUrlConfiguracio();
		seleccionarEntorn(titolEntorn);
		crearRol(codiRol, titolRol, "configuracio/rols/b1_1_crear");
	}
	
	//@Test
	//TODO: Es produeix un error quant intentes modificar el rol. Reportat per mail 02/09/2014 17:04
	public void c1_modificar_rol() {
		
		carregarUrlConfiguracio();
		
		seleccionarEntorn(titolEntorn);
		
		//Modificar i comprovar les modificacions d´un rol
		actions.moveToElement(driver.findElement(By.id("menuConfiguracio")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/rol/llistat.html')]")));
		actions.click();
		actions.build().perform();
		
		screenshotHelper.saveScreenshot("configuracio/rols/c1_1_modificar_rol_llista.png");
		
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td/a, '"+codiRol+"')]", "El Rol a modificar no existeix.");
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td/a, '"+codiRol+"')]/td/a")).click();
		
		driver.findElement(By.id("descripcio0")).sendKeys("mod");
		
		screenshotHelper.saveScreenshot("configuracio/rols/c1_2_modificar_rol_canviat.png");
		
		driver.findElement(By.xpath("//button[@value='submit']")).click();
		
		existeixElementAssert("//*[@class='missatgesOk']", "Missatge d´error inesperat al modificar el rol ("+codiRol+").");
		
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td/a, '"+codiRol+"')]", "El Rol modificat no es troba a la llista.");

		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td/a, '"+codiRol+"')]/td/a")).click();
		
		String titolModificat = titolRol+"mod";
		if (!titolModificat.equals(driver.findElement(By.id("nom0")).getAttribute("value"))) {
			screenshotHelper.saveScreenshot("configuracio/rols/c1_3_modificar_rol_ko.png");
			fail("El titol del rol ("+titolModificat+") no s´ha modificat!");
		}else{
			screenshotHelper.saveScreenshot("configuracio/rols/c1_3_modificar_rol_ok.png");
		}
	}
	
	@Test
	public void d1_eliminar_rol() {
		carregarUrlConfiguracio();
		seleccionarEntorn(titolEntorn);
		eliminarRol(codiRol, "configuracio/rols/d1_eliminar");
	}
		
	@Test
	public void z0_finalitzacio() {
		carregarUrlConfiguracio();
		eliminarEntorn(entorn);
	}
}
