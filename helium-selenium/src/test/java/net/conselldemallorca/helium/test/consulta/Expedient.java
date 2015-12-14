package net.conselldemallorca.helium.test.consulta;

import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.conselldemallorca.helium.test.util.BaseTest;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Expedient extends BaseTest {	
	
	String entorn = "Test"; //carregarPropietat("entorn.nom", "Nom de l'entorn de proves no configurat al fitxer de properties");
	String codTipusExp = "provesPep"; //carregarPropietat("defproc.deploy.tipus.expedient.codi", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	String tipusExp = "Proves pep"; //carregarPropietat("defproc.deploy.tipus.expedient.nom", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	String usuari = carregarPropietat("test.base.usuari.configuracio", "Usuari configuració de l'entorn de proves no configurat al fitxer de properties");
				
	static String entornActual;

	@Test
	public void c_netejar_filtre() throws InterruptedException, ParseException {	
		carregarUrlConfiguracio();
		
		seleccionarEntorn(entorn);
		
		actions.moveToElement(driver.findElement(By.id("menuConsultes")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//*[@id='menuConsultes']/ul/li[1]/a")));
		actions.click();
		actions.build().perform();
		
		// Rellenamos los datos
		for (WebElement input : driver.findElements(By.xpath("//*[@id='command']/div/div/input"))) {
			input.sendKeys(input.getAttribute("name"));
		}
		for (WebElement select : driver.findElements(By.xpath("//*[@id='command']/div/div/select"))) {
			List<WebElement> options = select.findElements(By.tagName("option"));
			options.get(options.size()-1).click();
		}		
		Calendar calendar = Calendar.getInstance();
		for (WebElement fecha : driver.findElements(By.xpath("//*[@id='command']/div/div/div/label/input"))) {
			calendar.add(Calendar.DATE, 1);
			fecha.sendKeys(new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime()));
		}		
		driver.findElement(By.xpath("//*[@id='command']/div[2]/div[6]/button[1]")).click();
		
		// Limpiamos los datos
		driver.findElement(By.xpath("//button[contains(text(), 'Netejar')]")).click();
		
		// Comprobamos que están vacíos
		for (WebElement input : driver.findElements(By.xpath("//*[@id='command']/div/div/input"))) {
			assertTrue("El campo '"+input.getAttribute("name")+"' no se limpió", input.getAttribute("value").isEmpty());
		}
		for (WebElement select : driver.findElements(By.xpath("//*[@id='command']/div/div/select"))) {
			List<WebElement> options = select.findElements(By.tagName("option"));
			int i = 0;
			for (WebElement option : options) {
				if (option.isSelected()) {
					break;
				}
				i++;
			}
			assertTrue("El campo '"+select.getAttribute("name")+"' no se limpió", i == 0);
		}		
		for (WebElement fecha : driver.findElements(By.xpath("//*[@id='command']/div/div/div/label/input"))) {
			assertTrue("El campo '"+fecha.getAttribute("name")+"' no se limpió", fecha.getAttribute("value").isEmpty());
		}		
		driver.findElement(By.xpath("//*[@id='command']/div[2]/div[6]/button[1]")).click();
		
	}
	
	@Test
	public void d_anular_expedient() throws InterruptedException, ParseException {	
		carregarUrlConfiguracio();
		
		seleccionarEntorn(entorn);
		
		// Comprobamos permisos de borrado
		
		// Los quitamos
		assignarPermisosTipusExpedient(codTipusExp, usuari, "DESIGN","CREATE","SUPERVISION","DELETE","MANAGE","READ");
		
		consultarExpedientes(null, null, tipusExp);
		
		noExisteixElementAssert("//*[@id='registre']/tbody/tr[1]/td[contains(a/img/@src,'/helium/img/delete.png')]/a/img", "Tenía permisos de anulado");
		
		// Los ponemos
		assignarPermisosTipusExpedient(codTipusExp, usuari, "DESIGN","CREATE","SUPERVISION","WRITE","MANAGE","DELETE","READ","ADMINISTRATION");
		
		consultarExpedientes(null, null, tipusExp);
		
		existeixElementAssert("//*[@id='registre']/tbody/tr[1]/td[contains(a/img/@src,'/helium/img/delete.png')]/a/img", "No tenía permisos de anulado");
		
		// Anulamos el primer expediente
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]/td[7]/a")).click();
		acceptarConfirm("El motivo");
	}

	@Test
	public void e_mostrar_anulats() throws InterruptedException, ParseException {
		carregarUrlConfiguracio();
		
		seleccionarEntorn(entorn);
		
		actions.moveToElement(driver.findElement(By.id("menuConsultes")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//*[@id='menuConsultes']/ul/li[1]/a")));
		actions.click();
		actions.build().perform();
		
		WebElement selectTipusExpedient = driver.findElement(By.xpath("//*[@id='expedientTipus0']"));
		List<WebElement> options = selectTipusExpedient.findElements(By.tagName("option"));
		for (WebElement option : options) {
			if (option.getText().equals(tipusExp)) {
				option.click();
				break;
			}
		}
		
		// Seleccionamos los activos y anulados
		WebElement select = driver.findElement(By.xpath("//*[@id='mostrarAnulats0']"));
		options = select.findElements(By.tagName("option"));
		options.get(2).click();	
		
		driver.findElement(By.xpath("//*[@id='command']/div[2]/div[6]/button[1]")).click();	
		
		// Seleccionamos todos
		select = driver.findElement(By.xpath("//*[@id='objectsPerPage']"));
		options = select.findElements(By.tagName("option"));
		options.get(options.size()-1).click();
		
		driver.findElement(By.xpath("//*[@id='command']/div[2]/div[6]/button[1]")).click();	
		
		// Contamos los anulados
		List<String> idsExpedient = new ArrayList<String>();
		
		for (WebElement input : driver.findElements(By.xpath("//img[@src='/helium/img/arrow_undo.png']/parent::a/parent::td/parent::tr/td[1]/input[@type='checkbox']"))) {
			idsExpedient.add(input.getAttribute("value"));
		}
		
		if (!idsExpedient.isEmpty()) {
			// Seleccionamos los anulados
			select = driver.findElement(By.xpath("//*[@id='mostrarAnulats0']"));
			options = select.findElements(By.tagName("option"));
			options.get(1).click();
			
			driver.findElement(By.xpath("//*[@id='command']/div[2]/div[6]/button[1]")).click();	
			
			// Número de expedientes
			String numExpedientes = driver.findElement(By.xpath("//*[@id='content']/span[1]")).getText();
			numExpedientes = numExpedientes.substring("S'han trobat ".length(), numExpedientes.indexOf(" items,"));
			
			assertTrue("El número de expedientes anulados no coincidía", idsExpedient.size() == Integer.valueOf(numExpedientes));
			
			for (WebElement input : driver.findElements(By.xpath("//img[@src='/helium/img/arrow_undo.png']/parent::a/parent::td/parent::tr/td[1]/input[@type='checkbox']"))) {
				String idExpedient = input.getAttribute("value");
				assertTrue("El expediente con ID '"+idExpedient+"' no se encontró en la anterior pantalla de activos y anulados", idsExpedient.contains(idExpedient));
			}
		}
	}

	@Test
	public void m_filtrar_nomes_amb_tasques_pendents() throws InterruptedException, ParseException {
	}

	@Test
	public void g_resultats_per_pagina() throws InterruptedException, ParseException {
		carregarUrlConfiguracio();
		
		seleccionarEntorn(entorn);
		
		consultarExpedientes(null, null, tipusExp);
		
		// Número de expedientes
		String numExpedientes = driver.findElement(By.xpath("//*[@id='content']/span[1]")).getText();
		numExpedientes = numExpedientes.substring("S'han trobat ".length(), numExpedientes.indexOf(" items,"));
						
		// Seleccionamos página por página
		int i = 0;
		int numPages = driver.findElement(By.xpath("//*[@id='objectsPerPage']")).findElements(By.tagName("option")).size()-1;
		while (i < numPages) {
			driver.findElement(By.xpath("//*[@id='objectsPerPage']")).findElements(By.tagName("option")).get(i).click();
			
			driver.findElement(By.xpath("//*[@id='command']/div[2]/div[6]/button[1]")).click();	
			
			int numElementsPage = Integer.valueOf(driver.findElement(By.xpath("//*[@id='objectsPerPage']")).findElements(By.tagName("option")).get(i).getAttribute("value"));
			
			if (Integer.valueOf(numExpedientes) >= numElementsPage) {
				assertTrue("El número de expedientes por página no coincidía", numElementsPage == driver.findElements(By.xpath("//*[@id='registre']/tbody/tr")).size());
			} else {
				assertTrue("El número de expedientes encontrados era mayor a la página", numElementsPage > driver.findElements(By.xpath("//*[@id='registre']/tbody/tr")).size());
			}
			
			i++;
		}
	}

	@Test
	public void f_desplegar_tasques_expedient() throws InterruptedException, ParseException {	
		carregarUrlConfiguracio();
		
		seleccionarEntorn(entorn);
		
		// Asignamos permisos
		assignarPermisosTipusExpedient(codTipusExp, usuari, "DESIGN","CREATE","SUPERVISION","WRITE","MANAGE","DELETE","READ","ADMINISTRATION");
		
		consultarExpedientes(null, null, tipusExp);
		
		existeixElementAssert("//*[@id='registre']/tbody/tr[1]/td[2]/a", "No se tenía ningún expediente");
		
		// Vamos a la tarea a tramitar del expediente
		String nomExpediente = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]/td[2]/a")).getText();
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]/td[2]/a")).click();
		
		assertTrue("No se llegó a la pantalla de tasques pendents", "Tasques pendents".equals(driver.findElement(By.xpath("//*[@id='page-title']/h2/span")).getText()));
		assertTrue("No se realizó la búsqueda con el nombre del expediente correcto", nomExpediente.equals(driver.findElement(By.xpath("//*[@id='expedient0']")).getAttribute("value")));
	}
	
	@Test
	public void h_acces_a_tramitacio_de_tasca() throws InterruptedException, ParseException {	

		carregarUrlConfiguracio();
		
		seleccionarEntorn(entorn);
		
		// Asignamos permisos
		assignarPermisosTipusExpedient(codTipusExp, usuari, "DESIGN","CREATE","SUPERVISION","WRITE","MANAGE","DELETE","READ","ADMINISTRATION");
		
		consultarExpedientes(null, null, tipusExp);
		
		existeixElementAssert("//*[@id='registre']/tbody/tr[1]/td[2]/a", "No se tenía ningún expediente");
		
		// Vamos a la tarea a tramitar del expediente
		String nomExpediente = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]/td[2]/a")).getText();
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]/td[2]/a")).click();
		
		assertTrue("No se llegó a la pantalla de tasques pendents", "Tasques pendents".equals(driver.findElement(By.xpath("//*[@id='page-title']/h2/span")).getText()));
		assertTrue("No se realizó la búsqueda con el nombre del expediente correcto", nomExpediente.equals(driver.findElement(By.xpath("//*[@id='expedient0']")).getAttribute("value")));
		
		if (existeixElement("//*[@id='registre']/tbody/tr[1]/td[1]/a")) {
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]/td[1]/a")).click();
			
			//*[@id='content']/h3
			assertTrue("No se llegó a la pantalla de tasques pendents", "Tasques pendents".equals(driver.findElement(By.xpath("//*[@id='page-title']/h2/span")).getText()));
			assertTrue("No se realizó la búsqueda con el nombre del expediente correcto", nomExpediente.equals(driver.findElement(By.xpath("//*[@id='expedient0']")).getAttribute("value")));
		}
	}

	@Test
	public void i_obrir_expedient() throws InterruptedException, ParseException {	
		carregarUrlConfiguracio();
		
		seleccionarEntorn(entorn);
		
		// Asignamos permisos
		assignarPermisosTipusExpedient(codTipusExp, usuari, "DESIGN","CREATE","SUPERVISION","WRITE","MANAGE","DELETE","READ","ADMINISTRATION");
		
		consultarExpedientes(null, null, tipusExp);
		
		existeixElementAssert("//*[@id='registre']/tbody/tr[1]/td/a/img[@src = '/helium/img/information.png']", "No exitía la imagen de ver la información de un expediente");
		
		// Abrimos el expediente
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]/td/a/img[@src = '/helium/img/information.png']")).click();
		
		existeixElementAssert("//*[@id='tabnav']/li/a[contains(text(), 'Expedient')]", "No exitía la pestaña de expediente");
	}

	@Test
	public void j_seleccio_tasques_i_acces_a_tramitacio_massiva() throws InterruptedException, ParseException {	
		carregarUrlConfiguracio();
		
		seleccionarEntorn(entorn);
		
		consultarExpedientes(null, null, tipusExp);
		
		// Número de páginas
		int numPages = 1;
		if (existeixElement("//*[@id='content']/span[@class='pageLinks']/a")) {
			// El número máximo de páginas lo dejamos en 8
			numPages = driver.findElements(By.xpath("//*[@id='content']/span[@class='pageLinks']/a")).size()-1;
		}
		
		// Número de elementos por página
		int numElementosPagina = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr")).size();
		
		// Número de expedientes
		String numExpedientes = driver.findElement(By.xpath("//*[@id='content']/span[1]")).getText();
		numExpedientes = numExpedientes.substring("S'han trobat ".length(), numExpedientes.indexOf(" items,"));
		
		Map<String, String> expedientSeleccionados = new HashMap<String, String>();
		
		// Seleccionar todo
		driver.findElement(By.xpath("//*[@id='selTots']")).click();
		if (!driver.findElement(By.xpath("//*[@id='selTots']")).isSelected()) {
			driver.findElement(By.xpath("//*[@id='selTots']")).click();
		}
		
		int i = 1;
		while (existeixElement("//*[@id='registre']/tbody/tr["+i+"]/td[1]/input[@type='checkbox']")) {
			if (driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+i+"]/td[1]/input[@type='checkbox']")).isSelected()) {
				String idExpedient = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+i+"]/td[1]/input[@type='checkbox']")).getAttribute("value");
				String nomExpedient = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+i+"]/td[2]/a")).getText();
				expedientSeleccionados.put(idExpedient,nomExpedient);
			}
			i++;
		}
		
		assertTrue("El número de elementos al pulsar seleccionar todos no coincidía", expedientSeleccionados.size() == numElementosPagina);
		
		int j = 2;
		while (j <= numPages) {
			driver.findElement(By.xpath("//*[@id='content']/span[2]/a[contains(text(), '"+j+"')]")).click();
			numElementosPagina = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr")).size();
			// Pulsamos 3 elementos como máximo
			for (int k = 0; k < 3 && k < numElementosPagina; k++) {
				int nextElement = new Random().nextInt(numElementosPagina-1) + 1;
				String idExpedient = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+nextElement+"]/td[1]/input[@type='checkbox']")).getAttribute("value");
				String nomExpedient = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+nextElement+"]/td[2]/a")).getText();
				if (!expedientSeleccionados.containsKey(idExpedient) && !driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+nextElement+"]/td[1]/input[@type='checkbox']")).isSelected()) {
					driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+nextElement+"]/td[1]/input[@type='checkbox']")).click();
					expedientSeleccionados.put(idExpedient,nomExpedient);
				}
			}
			j++;
		}
		
		// Pasamos página por página para comprobar que los elementos marcados son los que deben estar
		if (existeixElement("//*[@id='content']/span[@class='pageLinks']/a[contains(text(), 'Primer')]")) {
			driver.findElement(By.xpath("//*[@id='content']/span[@class='pageLinks']/a[contains(text(), 'Primer')]")).click();
		}
		j = 1;
		while (j <= numPages) {
			i = 1;
			while (existeixElement("//*[@id='registre']/tbody/tr["+i+"]/td[1]/input[@type='checkbox']")) {
				String idExpedient = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+i+"]/td[1]/input[@type='checkbox']")).getAttribute("value");
				if (driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+i+"]/td[1]/input[@type='checkbox']")).isSelected()) {
					assertTrue("El elemento '"+idExpedient+"' debía esta seleccionado", expedientSeleccionados.containsKey(idExpedient));
				} else {
					assertTrue("El elemento '"+idExpedient+"' no debía esta seleccionado", !expedientSeleccionados.containsKey(idExpedient));
				}				
				i++;
			}			
			j++;
			driver.findElement(By.xpath("//*[@id='content']/span[2]/a[contains(text(), '"+j+"')]")).click();
		}
		
		// Acceés a tramitació massiva
		
		// Seleccionar
		driver.findElement(By.xpath("//button[contains(text(), 'Seleccionats')]")).click();		
		driver.findElement(By.xpath("//img[@src='/helium/img/magnifier_zoom_in.png']")).click();		
		i = 1;
		while (existeixElement("//*[@id='registre']/tbody/tr["+i+"]/td[1]")) {
			String nomExpedient = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+i+"]/td[1]")).getText();
			assertTrue("El elemento '"+nomExpedient+"' no debía estar en la lista", expedientSeleccionados.containsValue(nomExpedient));
			i++;
		}		
		assertTrue("El número de elementos en la tramitación masiva no coincidía", expedientSeleccionados.size() == (i-1));
		
		driver.findElement(By.xpath("//button[contains(text(), 'Canviar selecció')]")).click();
		
		// Tots
		driver.findElement(By.xpath("//button[contains(text(), 'Tots')]")).click();
		driver.findElement(By.xpath("//img[@src='/helium/img/magnifier_zoom_in.png']")).click();		
		i = 1;
		while (existeixElement("//*[@id='registre']/tbody/tr["+i+"]/td[1]")) {
			String nomExpedient = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+i+"]/td[1]")).getText();
			String tipoExpedient = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+i+"]/td[3]")).getText();
			assertTrue("El elemento '"+nomExpedient+"' no tenía el tipo de expediente correcto", tipoExpedient.equals(tipusExp));
			i++;
		}		
		assertTrue("El número de elementos en la tramitación masiva no coincidía", Integer.valueOf(numExpedientes) == (i-1));
		
		driver.findElement(By.xpath("//button[contains(text(), 'Canviar selecció')]")).click();
		
		// Executar accions massives sobre el tipus d'expedient
		driver.findElement(By.xpath("//*[@id='ejecucionMasivaTotsTipus']")).click();
		driver.findElement(By.xpath("//img[@src='/helium/img/magnifier_zoom_in.png']")).click();		
		i = 1;
		while (existeixElement("//*[@id='registre']/tbody/tr["+i+"]/td[1]")) {
			String nomExpedient = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+i+"]/td[1]")).getText();
			String tipoExpedient = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+i+"]/td[3]")).getText();
			assertTrue("El elemento '"+nomExpedient+"' no tenía el tipo de expediente correcto", tipoExpedient.equals(tipusExp));
			i++;
		}		
		assertTrue("El número de elementos en la tramitación masiva no coincidía", Integer.valueOf(numExpedientes) == (i-1));
	}

	@Test
	public void l_aturar_expedient() throws InterruptedException, ParseException {
		carregarUrlConfiguracio();
		
		seleccionarEntorn(entorn);
		
		// Comprobamos permisos de borrado
		
		// Los quitamos
		assignarPermisosTipusExpedient(codTipusExp, usuari, "READ");
		
		consultarExpedientes(null, null, tipusExp);
		
		existeixElementAssert("//*[@id='registre']/tbody/tr[1]/td/a/img[@src = '/helium/img/information.png']", "No exitía la imagen de ver la información de un expediente");
		
		// Abrimos el expediente
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]/td/a/img[@src = '/helium/img/information.png']")).click();
		
		noExisteixElementAssert("//*[@id='tabnav']/li/a[contains(text(), 'Eines')]", "Tenía permisos de eines");
		
		// Los ponemos
		assignarPermisosTipusExpedient(codTipusExp, usuari, "DESIGN","CREATE","SUPERVISION","WRITE","MANAGE","DELETE","READ","ADMINISTRATION");
		
		consultarExpedientes(null, null, tipusExp);
		
		// Abrimos el expediente
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]/td/a/img[@src = '/helium/img/information.png']")).click();
		
		existeixElementAssert("//*[@id='tabnav']/li/a[contains(text(), 'Eines')]", "No tenía permisos de eines");
		
		noExisteixElementAssert("//*[@id='content']/div[@class='missatgesAturat']", "El expediente ya estaba aturado");
		
		// Aturamos el expediente
		driver.findElement(By.xpath("//*[@id='tabnav']/li/a[contains(text(), 'Eines')]")).click();
		driver.findElement(By.xpath("//*[@id='content']/div/h3[1]/a")).click();
		driver.findElement(By.xpath("//*[@id='motiu0']")).sendKeys("El motivo");
		driver.findElement(By.xpath("//button[contains(text(), 'Aturar')]")).click();
		acceptarAlerta();
		
		existeixElementAssert("//*[@id='infos']/p", "No aturo el expediente correctamente");
		existeixElementAssert("//*[@id='content']/div[@class='missatgesAturat']", "No existía el mensaje de aturar expediente");
		
		consultarExpedientes(null, null, tipusExp);
		
		existeixElementAssert("//*[@id='registre']/tbody/tr[1]/td/img[@src = '/helium/img/stop.png']", "No exitía la imagen de expediente aturado");
	}

	@Test
	public void n_borrar_expedient() throws InterruptedException, ParseException {	
		carregarUrlConfiguracio();
		
		seleccionarEntorn(entorn);
		
		// Comprobamos permisos de borrado
		
		// Los quitamos
		assignarPermisosTipusExpedient(codTipusExp, usuari, "DESIGN","CREATE","SUPERVISION","WRITE","MANAGE","READ","ADMINISTRATION");
		
		consultarExpedientes(null, null, tipusExp);
		
		noExisteixElementAssert("//*[@id='registre']/tbody/tr[1]/td[contains(a/img/@src,'/helium/img/cross.png')]/a/img", "Tenía permisos de borrado");
		
		// Los ponemos
		assignarPermisosTipusExpedient(codTipusExp, usuari, "DESIGN","CREATE","SUPERVISION","WRITE","MANAGE","DELETE","READ","ADMINISTRATION");
		
		consultarExpedientes(null, null, tipusExp);
		
		existeixElementAssert("//*[@id='registre']/tbody/tr[1]/td[contains(a/img/@src,'/helium/img/cross.png')]/a/img", "No tenía permisos de borrado");
		
		// Borramos el primer expediente
		borrarPrimerExpediente();
	}
}
