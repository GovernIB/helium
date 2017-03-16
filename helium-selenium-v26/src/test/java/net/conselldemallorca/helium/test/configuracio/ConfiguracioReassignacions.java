package net.conselldemallorca.helium.test.configuracio;

import static org.junit.Assert.fail;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import net.conselldemallorca.helium.test.util.BaseTest;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ConfiguracioReassignacions extends BaseTest {

								//DFG.4 - Reassignacions
									//DFG.4.1 - Crear
									//DFG.4.2 - Modificar
									//DFG.4.3 - Eliminar
		
		String entorn 		 = carregarPropietat("config.aplicacio.entorn.nom", "Nom de l'entorn de proves no configurat al fitxer de properties");
		String titolEntorn	 = carregarPropietat("config.reassig.entorn.titol", "Titol de l'entorn de proves no configurat al fitxer de properties");
		String usuariAdmin   = carregarPropietat("test.base.usuari.configuracio", "Usuari configuració de l'entorn de proves no configurat al fitxer de properties");
		String usuariFeina   = carregarPropietat("test.base.usuari.feina", "Usuari configuració de l'entorn de proves no configurat al fitxer de properties");
		
		String usuariOrigenNom  = carregarPropietat("test.base.usuari.configuracio.nom", "Nom de l'Usuari configuració de l'entorn de proves no configurat al fitxer de properties");
		String usuariDestiNom   = carregarPropietat("test.base.usuari.feina.nom", "Nom de l'Usuari feina de l'entorn de proves no configurat al fitxer de properties");
		
		
		//XPATHS		
		String botoNovaReass = "//*[@id='content']/form/button";
		String botoguardarReass = "//*[@id='command']/div/div[@class='buttonHolder']/button[text() = 'Crear']";
		String enllaçModifReass = "//*[@id='registre']/tbody/tr/td[1]/a";
		String botoModifReass  = "//*[@id='command']/div/div[@class='buttonHolder']/button[text() = 'Modificar']";
		String enllaçElimReass = "//*[@id='registre']/tbody/tr[1]/td/a[contains(@href, '/reassignar/cancelar.html')]";
		
		String seleccioUsuariOri = "//html/body/div[@class='ac_results']/ul/li[text() = '"+usuariOrigenNom+"']";
		String seleccioUsuariDes = "//html/body/div[@class='ac_results']/ul/li[text() = '"+usuariDestiNom+"']";
		
		String imatgeBorrarSugge = "//*[@id='suggest_usuariOrigen0_delete']";
		
		
		@Test
		public void a1_inicialitzacio() {
			carregarUrlConfiguracio();
			crearEntorn(entorn, titolEntorn);
			assignarPermisosEntorn(entorn, usuariAdmin, "DESIGN", "ORGANIZATION", "READ", "ADMINISTRATION");
			marcarEntornDefecte(titolEntorn);
			seleccionarEntorn(titolEntorn);
		}
		
		@Test
		public void b1_crear_reassignacio() {
			
			carregarUrlConfiguracio();
			
			accedirConfiguracioReassignacions();
			
			screenshotHelper.saveScreenshot("configuracio/reassignacions/b1_1_reassignacions_inicials.png");
			
			driver.findElement(By.xpath(botoNovaReass)).click();
			
			String suggestUsuOri = usuariOrigenNom;
			if (usuariOrigenNom.length()>3) { suggestUsuOri = usuariOrigenNom.substring(0, 3); }
			
			driver.findElement(By.id("suggest_usuariOrigen0")).sendKeys(suggestUsuOri);
			try { Thread.sleep(2000); }catch (Exception ex) {}
			driver.findElement(By.xpath("//html/body/div[@class='ac_results']/ul/li[contains(text(), '"+usuariOrigenNom+"')]")).click();
			
			String suggestUsuDes = usuariDestiNom;
			if (usuariDestiNom.length()>3) { suggestUsuDes = usuariDestiNom.substring(0, 3); }
			
			driver.findElement(By.id("suggest_usuariDesti0")).sendKeys(suggestUsuDes);
			try { Thread.sleep(2000); }catch (Exception ex) {}
			driver.findElement(By.xpath("//html/body/div[@class='ac_results']/ul/li[contains(text(), '"+usuariDestiNom+"')]")).click();
			
			//Posam com a dates per la redireció d'avull fins a demà
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			driver.findElement(By.id("dataInici0")).sendKeys(new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime()));
			
			calendar.add(Calendar.DAY_OF_MONTH, 5);
			driver.findElement(By.id("dataFi0")).sendKeys(new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime()));
			
			screenshotHelper.saveScreenshot("configuracio/reassignacions/b1_3_nova_reassignacio_dades_emplenades.png");
			
			driver.findElement(By.xpath(botoguardarReass)).click();
			
			screenshotHelper.saveScreenshot("configuracio/reassignacions/b1_4_nova_reassignacio_resutltat_guardat.png");
			
			existeixElementAssert("//*[@class='missatgesOk']", "Missatge d´error inesperat al crear la reassignacio.");
		}
		
		@Test
		public void c1_modificar_redireccio() {
			
			carregarUrlConfiguracio();
			accedirConfiguracioReassignacions();
			
			screenshotHelper.saveScreenshot("configuracio/reassignacions/c1_1_modificar_reassignacio_llista_inicial.png");
			
			driver.findElement(By.xpath(enllaçModifReass)).click();
			
			driver.findElement(By.id("suggest_usuariOrigen0_delete")).click();
			driver.findElement(By.id("suggest_usuariDesti0_delete")).click();
			driver.findElement(By.id("dataInici0")).clear();
			driver.findElement(By.id("dataFi0")).clear();
			
			//Intercanviam usuaris i augmentam dates
			
			String suggestUsuOri = usuariDestiNom;
			if (usuariDestiNom.length()>3) { suggestUsuOri = usuariDestiNom.substring(0, 3); }
			
			driver.findElement(By.id("suggest_usuariOrigen0")).sendKeys(suggestUsuOri);
			try { Thread.sleep(2000); }catch (Exception ex) {}
			driver.findElement(By.xpath("//html/body/div[@class='ac_results']/ul/li[contains(text(), '"+usuariDestiNom+"')]")).click();
			
			String suggestUsuDes = usuariOrigenNom;
			if (usuariOrigenNom.length()>3) { suggestUsuDes = usuariOrigenNom.substring(0, 3); }
			
			driver.findElement(By.id("suggest_usuariDesti0")).sendKeys(suggestUsuDes);
			try { Thread.sleep(2000); }catch (Exception ex) {}
			driver.findElement(By.xpath("//html/body/div[@class='ac_results']/ul/li[contains(text(), '"+usuariOrigenNom+"')]")).click();
			
			Calendar calendar = Calendar.getInstance();
			
			calendar.add(Calendar.DAY_OF_MONTH, 5);
			String fechaModIni = new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime());
			driver.findElement(By.id("dataInici0")).sendKeys(fechaModIni);
			
			calendar.add(Calendar.DAY_OF_MONTH, 7);
			String fechaModFi = new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime());
			driver.findElement(By.id("dataFi0")).sendKeys(fechaModFi);
			
			screenshotHelper.saveScreenshot("configuracio/reassignacions/c1_2_modificar_reassignacio_dades_canviades.png");
			
			driver.findElement(By.xpath(botoModifReass)).click();
			
			screenshotHelper.saveScreenshot("configuracio/reassignacions/c1_3_modificar_reassignacio_resultat_modificacio.png");
			
			existeixElementAssert("//*[@class='missatgesOk']", "Missatge d´error inesperat al crear la reassignacio.");
			
			//Tornam entrar i comprovam les dades modificades
					
			driver.findElement(By.xpath(enllaçModifReass)).click();
			
			if (!usuariFeina.equals(driver.findElement(By.id("suggest_usuariOrigen0")).getAttribute("value"))) { fail("L'usuari inicial de la reassignacio no s´ha modificat!"); }
			if (!usuariAdmin.equals(driver.findElement(By.id("suggest_usuariDesti0")).getAttribute("value"))) { fail("L'usuari final de la reassignacio no s´ha modificat!"); }
			if (!fechaModIni.equals(driver.findElement(By.id("dataInici0")).getAttribute("value"))) { fail("La fecha inici de la reassignacio no s´ha modificat!"); }
			if (!fechaModFi.equals(driver.findElement(By.id("dataFi0")).getAttribute("value"))) { fail("La fecha fi de la reassignacio no s´ha modificat!"); }
			
			screenshotHelper.saveScreenshot("configuracio/reassignacions/c1_4_modificar_reassignacio_comprovam_dades_canviades.png");
		}
		
		@Test
		public void d1_eliminar_redireccio() {
			
			carregarUrlConfiguracio();
			
			accedirConfiguracioReassignacions();
						
			screenshotHelper.saveScreenshot("configuracio/reassignacions/d1_1_eliminar_reassignacio_llista_inicial.png");
			
			int contadorScreen = 1;
			while (existeixElement(enllaçElimReass)) {
				driver.findElement(By.xpath(enllaçElimReass)).click();
				if (isAlertPresent()) {acceptarAlerta();}
				screenshotHelper.saveScreenshot("configuracio/reassignacions/d1_"+contadorScreen+"_eliminar_reassignacio.png");
				existeixElementAssert("//*[@class='missatgesOk']", "Error al borrar la redireccio a la configuració de l´entorn "+titolEntorn+".");
				contadorScreen++;
			}
		}
		
		@Test
		public void z0_finalitzacio() {
			carregarUrlConfiguracio();
			eliminarEntorn(entorn);
		}
}
