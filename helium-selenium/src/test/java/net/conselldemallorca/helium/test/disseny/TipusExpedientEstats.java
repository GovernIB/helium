package net.conselldemallorca.helium.test.disseny;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;

import net.conselldemallorca.helium.test.util.BaseTest;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TipusExpedientEstats extends BaseTest {

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
		crearEstatTipusExpedient(codTipusExp, codiEstatTipExp, nomEstatTipExp);
	}
	
	@Test
	public void b1_modificarEstat() {
		
		carregarUrlDisseny();
		
		seleccionarEntorn(titolEntorn);
		
		accedirEstatsExpedient(codTipusExp);
		
		existeixElementAssert(enllaçModifEstat, "No s´ha trobat l´estat a llistat.");
		
		driver.findElement(By.xpath(enllaçModifEstat)).click();
		
		driver.findElement(By.id("codi0")).sendKeys("_mod");
		driver.findElement(By.id("nom0")).sendKeys("_mod");
		
		driver.findElement(By.xpath(botoModifEstat)).click();
		
		existeixElementAssert("//*[@class='missatgesOk']", "Error al modificar l'estat per el tipus d´expedient.");
	}
	
	@Test
	public void c1_ordenarEstats() {
		carregarUrlDisseny();
		seleccionarEntorn(titolEntorn);
		//Cream un segon estat per poder comprobar la ordenació
		crearEstatTipusExpedient(codTipusExp, codiEstatTipExp_2, nomEstatTipExp_2);
		sortTable("registre", 1, 2);
		sortTable("registre", 1, 2);
	}
	
	@Test
	public void d1_eliminarEstat() {
		carregarUrlConfiguracio();
		eliminarTotsEstatsTipusExpedient(codTipusExp);
	}
	
	@Test
	public void e1_importarEstat() {
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