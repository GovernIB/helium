package net.conselldemallorca.helium.test.disseny;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;

import net.conselldemallorca.helium.test.util.BaseTest;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TipusExpedientEstats extends BaseTest {

							//TEX.4 - Estats
								//TEX.4.1 - Crear estat
								//TEX.4.2 - Modificar estat
								//TEX.4.3 - Ordenar estats
								//TEX.4.4 - Eliminar estats
								//TEX.4.5 - Importar estats (Funcionalitat coberta a la prova de Desplegar Exportació de TipusExpedient.java)
	
	String entorn 		= carregarPropietat("tipexp.entorn.nom", "Nom de l'entorn de proves no configurat al fitxer de properties");
	String titolEntorn	= carregarPropietat("tipexp.entorn.titol", "Titol de l'entorn de proves no configurat al fitxer de properties");
	
	String usuari 		= carregarPropietat("test.base.usuari.disseny", "Usuari feina de l'entorn de proves no configurat al fitxer de properties");
	String usuariAdmin  = carregarPropietat("test.base.usuari.configuracio", "Usuari configuració de l'entorn de proves no configurat al fitxer de properties");
	
	String nomTipusExp	= carregarPropietat("tipexp.deploy.tipus.expedient.nom", "Nom del tipus d'expedient de proves no configurat al fitxer de properties");
	String codTipusExp	= carregarPropietat("tipexp.deploy.tipus.expedient.codi", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	String pathExport	= carregarPropietatPath("tipexp.deploy.arxiu.path", "Ruta de l´arxiu del tipus d´expedient exportat no configurat al fitxer de properties");
	
	String codiEstatTipExp = carregarPropietat("tipexp.deploy.tipus.expedient.estat.codi", "Codi de l'estat del tipus de expedient");
	String nomEstatTipExp  = carregarPropietat("tipexp.deploy.tipus.expedient.estat.nom",  "Nom de l'estat del tipus de expedient");

	String codiEstatTipExp_2 = codiEstatTipExp + "_2";
	String nomEstatTipExp_2  = nomEstatTipExp  + "_2";
	
	//XPATH per les proves de estats
	
	String enllaçModifEstat = "//*[@id='registre']/tbody/tr/td/a[contains(@href, '/expedientTipus/estatsForm.html')]"; // "//*[@id='registre']/tbody/tr[contains(td[1],'" + codiEstatTipExp + "')]";
	
	String botoModifEstat	= "//*[@id='command']/div[@class='buttonHolder']/button[text() = 'Modificar']";
	
	
	@Test
	public void a1_inicialitzacio() {
		carregarUrlConfiguracio();
		crearEntorn(entorn, titolEntorn);
		assignarPermisosEntorn(entorn, usuariAdmin, "DESIGN", "ORGANIZATION", "READ", "ADMINISTRATION");
		assignarPermisosEntorn(entorn, usuari, "DESIGN", "ORGANIZATION", "READ");
		marcarEntornDefecte(titolEntorn);
		seleccionarEntorn(titolEntorn);
		crearTipusExpedient(nomTipusExp, codTipusExp);
	}
	
	@Test
	public void b1_crearEstat() {
		carregarUrlDisseny();
		seleccionarEntorn(titolEntorn);
		crearEstatTipusExpedient(codTipusExp, codiEstatTipExp, nomEstatTipExp, "tipusExpedient/estats/b1_crear");
	}
	
	@Test
	public void c1_modificarEstat() {
		
		carregarUrlDisseny();
		
		seleccionarEntorn(titolEntorn);
		
		accedirEstatsExpedient(codTipusExp);
		
		existeixElementAssert(enllaçModifEstat, "No s´ha trobat l´estat a llistat.");
		
		driver.findElement(By.xpath(enllaçModifEstat)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/estats/c1_1_modificar_dades_anteriors.png");
		
		driver.findElement(By.id("codi0")).sendKeys("_mod");
		driver.findElement(By.id("nom0")).sendKeys("_mod");
		
		screenshotHelper.saveScreenshot("tipusExpedient/estats/c1_2_modificar_dades_cambiades.png");
		
		driver.findElement(By.xpath(botoModifEstat)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/estats/c1_3_modificar_resultat.png");
		
		existeixElementAssert("//*[@class='missatgesOk']", "Error al modificar l'estat per el tipus d´expedient.");
	}
	
	@Test
	public void d1_ordenarEstats() {
		
		carregarUrlDisseny();
		
		seleccionarEntorn(titolEntorn);
		
		//Cream un segon estat per poder comprobar la ordenació
			
		crearEstatTipusExpedient(codTipusExp, codiEstatTipExp_2, nomEstatTipExp_2);
		
		screenshotHelper.saveScreenshot("tipusExpedient/estats/d1_1_ordenar_estats_cream_segon_estat.png");
		
		sortTable("registre", 1, 2);
		
		screenshotHelper.saveScreenshot("tipusExpedient/estats/d1_2_ordenar_estats_canvi_1.png");
		
		sortTable("registre", 1, 2);
		
		screenshotHelper.saveScreenshot("tipusExpedient/estats/d1_3_ordenar_estats_canvi_2.png");
	}
	
	@Test
	public void e1_eliminarEstat() {
		carregarUrlConfiguracio();
		eliminarTotsEstatsTipusExpedient(codTipusExp, "tipusExpedient/estats/e1_eliminar");
	}
	
	@Test
	public void f1_importarEstat() {
		//Funcionalitat coberta a la prova de Desplegar Exportació de TipusExpedient.java
	}
	
	@Test
	public void z0_finalitzacio() {
		carregarUrlConfiguracio();
		eliminarTipusExpedient(codTipusExp);
		//eliminarDefinicioProces(defProcNom);
		eliminarEntorn(entorn);
	}
}