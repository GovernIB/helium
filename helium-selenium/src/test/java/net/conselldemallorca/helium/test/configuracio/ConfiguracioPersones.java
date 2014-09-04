package net.conselldemallorca.helium.test.configuracio;

import static org.junit.Assert.fail;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;

import net.conselldemallorca.helium.test.util.BaseTest;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ConfiguracioPersones extends BaseTest {

								//DFG.1 - Persones: Consulta per codi, nom i email.
	
	String entorn 		= carregarPropietat("config.aplicacio.entorn.nom", "Nom de l'entorn de proves no configurat al fitxer de properties");
	String titolEntorn	= carregarPropietat("config.persones.entorn.titol", "Titol de l'entorn de proves no configurat al fitxer de properties");
	String usuariAdmin  = carregarPropietat("test.base.usuari.configuracio", "Usuari configuració de l'entorn de proves no configurat al fitxer de properties");

	//XPATHS
	String botoNovaPersona = "//*[@id='content']/form[contains(@action, 'form.html')]/button[text() = 'Nova persona']";
	String botoelimPersona = "//*[@id='registre']/tbody/tr/td/a[contains(@href, '/persona/delete.html')]";
	
	@Test
	public void a1_inicialitzacio() {
		carregarUrlConfiguracio();
		crearEntorn(entorn, titolEntorn);
		assignarPermisosEntorn(entorn, usuariAdmin, "DESIGN", "ORGANIZATION", "READ", "ADMINISTRATION");
		marcarEntornDefecte(titolEntorn);
		seleccionarEntorn(titolEntorn);
	}
	
	@Test
	public void b1_crear_persones_proves() {
		
		carregarUrlConfiguracio();
		seleccionarEntorn(titolEntorn);
		accedirConfiguracioPersones();
		
		screenshotHelper.saveScreenshot("configuracio/persones/b1_1_llistat_inicial.png");
		
		driver.findElement(By.xpath(botoNovaPersona)).click();
		
		crearNovaPersona("Pers1", "P_er_son_a", "Primera",null, null, "persona.primera@helium.com", "SEXE_HOME", false);
		screenshotHelper.saveScreenshot("configuracio/persones/b1_2_primera_persona.png");
		driver.findElement(By.xpath(botoNovaPersona)).click();
		crearNovaPersona("Pers2", "P_er_son_a", "Segona",null, null, "persona.Segona@helium.com", "SEXE_HOME", false);
		screenshotHelper.saveScreenshot("configuracio/persones/b1_3_segona_persona.png");
		driver.findElement(By.xpath(botoNovaPersona)).click();
		crearNovaPersona("Pers3", "P_er_son_a", "Tercera",null, null, "persona.Tercera@helium.com", "SEXE_DONA", false);
		screenshotHelper.saveScreenshot("configuracio/persones/b1_4_tercera_persona.png");
		driver.findElement(By.xpath(botoNovaPersona)).click();
		crearNovaPersona("Pers4", "P_er_son_a", "Quarta",null, null, "persona.Quarta@helium.com", "SEXE_DONA", false);
		screenshotHelper.saveScreenshot("configuracio/persones/b1_5_quarta_persona.png");
		driver.findElement(By.xpath(botoNovaPersona)).click();
		crearNovaPersona("Pers5", "P_er_son_a", "Quinta",null, null, "persona.Quinta@helium.com", "SEXE_DONA", false);
		screenshotHelper.saveScreenshot("configuracio/persones/b1_6_quinta_persona.png");
	}
	
	@Test
	public void c1_filtrar_eliminar_persones_proves() {
		
		carregarUrlConfiguracio();
		seleccionarEntorn(titolEntorn);
		accedirConfiguracioPersones();
		
		screenshotHelper.saveScreenshot("configuracio/persones/c1_1_filtrar_eliminar_persones_inici.png");
		
		filtraPersones("Pers1",  "P_er_son_a", "a@helium.com");
		
		int numPerSearch = numPersonesTrobades();
		if (numPerSearch!=1) {
			screenshotHelper.saveScreenshot("configuracio/persones/c1_21_primer_filtre_ko.png");
			fail("El nombre de resultats de persones ("+numPerSearch+") no es l´esperat (1)!");
		}else{
			screenshotHelper.saveScreenshot("configuracio/persones/c1_21_primer_filtre_ok.png");
		}
		
		filtraPersones("",  "P_er_son_a", "a@helium.com");

		numPerSearch = numPersonesTrobades();
		if (numPerSearch!=5) {
			screenshotHelper.saveScreenshot("configuracio/persones/c1_31_primer_filtre_ko.png");
			fail("El nombre de resultats de persones ("+numPerSearch+") no es l´esperat (5)!");
		}else{
			screenshotHelper.saveScreenshot("configuracio/persones/c1_32_primer_filtre_ok.png");
		}
		
		int borrades = 0;
		while (existeixElement(botoelimPersona) && borrades<=numPerSearch) {
			driver.findElement(By.xpath(botoelimPersona)).click();
			if (isAlertPresent()) {acceptarAlerta();}
			existeixElementAssert("//*[@class='missatgesOk']", "No s'ha pogut borrar la persona a la configuració de Helium.");
			borrades++;
			screenshotHelper.saveScreenshot("configuracio/persones/c1_3"+borrades+"_persona_borrada.png");
		}
	}
	
	@Test
	public void z0_finalitzacio() {
		carregarUrlConfiguracio();
		eliminarEntorn(entorn);
	}
	
	// M E T O D E S   P R I V A T S
	
	private int numPersonesTrobades() {
		// Número de expedientes
		String numExpedientes = driver.findElement(By.xpath("//*[@id='content']/span[@class='pageBanner']")).getText();
		if (numExpedientes.contains("trobat un persona")) {
			return 1;
		} else {
			numExpedientes = numExpedientes.substring("S'han trobat ".length(), numExpedientes.indexOf(" persona,"));
			return Integer.parseInt(numExpedientes);
		}
	}
}
