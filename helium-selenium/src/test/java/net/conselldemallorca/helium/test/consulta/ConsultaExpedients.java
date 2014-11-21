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
public class ConsultaExpedients extends BaseTest {

	String entorn = carregarPropietat("consulta.entorn.nom", "Nom de l'entorn de proves no configurat al fitxer de properties");
	String titolEntorn = carregarPropietat("consulta.entorn.titol", "Titol de l'entorn de proves no configurat al fitxer de properties");
	String usuariAdmin = carregarPropietat("test.base.usuari.configuracio", "Usuari configuració de l'entorn de proves no configurat al fitxer de properties");
	String usuari = carregarPropietat("test.base.usuari.disseny", "Usuari feina de l'entorn de proves no configurat al fitxer de properties");
	String pathDefProc = carregarPropietatPath("consulta.deploy.definicio.proces.path", "Path de la definició de procés de proves no configurat al fitxer de properties");
	String nomDefProc = carregarPropietat("consulta.deploy.definicio.proces.nom", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String nomTipusExp1 = carregarPropietat("consulta.deploy.tipus.expedient.nom.1", "Nom del tipus d'expedient de proves 1 no configurat al fitxer de properties");
	String codTipusExp1 = carregarPropietat("consulta.deploy.tipus.expedient.codi.1", "Codi del tipus d'expedient de proves 1 no configurat al fitxer de properties");
//	String pathTipusExp1 = carregarPropietatPath("consulta.deploy.tipus.expedient.path.1", "Path del tipus d'expedient de proves 1 no configurat al fitxer de properties");
	String nomTipusExp2 = carregarPropietat("consulta.deploy.tipus.expedient.nom.2", "Nom del tipus d'expedient de proves 2 no configurat al fitxer de properties");
	String codTipusExp2 = carregarPropietat("consulta.deploy.tipus.expedient.codi.2", "Codi del tipus d'expedient de proves 2 no configurat al fitxer de properties");
//	String pathTipusExp2 = carregarPropietatPath("consulta.deploy.tipus.expedient.path.2", "Path del tipus d'expedient de proves 2 no configurat al fitxer de properties");
	String pathExportEntorn = carregarPropietatPath("consulta.deploy.entorn.path", "Path de l'entorn de proves no configurat al fitxer de properties");
	
	@Test
	public void a_inicialitzacio() {
		carregarUrlConfiguracio();
		crearEntorn(entorn, titolEntorn);
		assignarPermisosEntorn(entorn, usuariAdmin, "DESIGN", "ORGANIZATION", "READ", "ADMINISTRATION");
		assignarPermisosEntorn(entorn, usuari, "DESIGN", "ORGANIZATION", "READ");
		marcarEntornDefecte(titolEntorn);
		seleccionarEntorn(titolEntorn);
		desplegarDefPro(TipusDesplegament.EXPORTDEFPRC, nomDefProc, null, pathDefProc, null, false, false);
		importarDadesEntorn(entorn, pathExportEntorn);
		
		try {Thread.sleep(5000);}catch(Exception ex) {}
		
		assignarPermisosTipusExpedient(codTipusExp1, usuariAdmin, "DESIGN","CREATE","SUPERVISION","WRITE","MANAGE","DELETE","READ","ADMINISTRATION");
		assignarPermisosTipusExpedient(codTipusExp1, usuari, "CREATE","WRITE","DELETE","READ");
		assignarPermisosTipusExpedient(codTipusExp2, usuariAdmin, "DESIGN","CREATE","SUPERVISION","WRITE","MANAGE","DELETE","READ","ADMINISTRATION");
		assignarPermisosTipusExpedient(codTipusExp2, usuari, "CREATE","WRITE","DELETE","READ");
	}
	
	@Test
	public void b_inicialitzacio() {
		carregarUrlDisseny();
		seleccionarEntorn(titolEntorn);
		
		// Iniciam 12 expedients de cada tipus d'expedient
		for (int i = 1; i <= 12; i++) {
			iniciarExpediente(codTipusExp1, null, null);
			if (i < 8) tramitarTasca(1, i);	// Tramitam la 1a tasca (assigna titol d'expedient i canvia estat)
			if (i < 4) tramitarTasca(2, i);	// Tramitam la 2a tasca (canvia estat)
			if (i < 2) tramitarTasca(3, i);	// Tramitam la 3a tasca (finalitza)
		}
		for (int i = 1; i <= 12; i++) {
			iniciarExpediente(codTipusExp2, null, null);
			if (i < 6) tramitarTasca(1, i);	// Tramitam la 1a tasca (assigna titol d'expedient i canvia estat)
			if (i < 3) tramitarTasca(2, i);	// Tramitam la 2a tasca (canvia estat)
			if (i < 1) tramitarTasca(3, i);	// Tramitam la 3a tasca (finalitza)
		}
	}
	
	@Test
	public void c_netejar_filtre() throws InterruptedException, ParseException {	
		carregarUrlDisseny();
		seleccionarEntorn(titolEntorn);
		
		actions.moveToElement(driver.findElement(By.id("menuConsultes")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/expedient/consulta.html')]")));
		actions.click();
		actions.build().perform();
		
		// Rellenamos los datos
		Calendar calendar = Calendar.getInstance();
		for (WebElement input : driver.findElements(By.xpath("//*[@id='command']//input"))) {
			if (input.getAttribute("class").contains("hasDatepicker")) {
				calendar.add(Calendar.DATE, 1);
				input.sendKeys(new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime()));
			} else {
				input.sendKeys(input.getAttribute("name"));
			}
		}
		
		Thread.sleep(3000);
		
		List<WebElement> options = driver.findElement(By.id("expedientTipus0")).findElements(By.tagName("option"));
		options.get(options.size()-1).click();
		
		Thread.sleep(3000);
		
		List<WebElement> options2 = driver.findElement(By.id("estat0")).findElements(By.tagName("option"));
		options2.get(options2.size()-1).click();
		
		Thread.sleep(3000);
		
		List<WebElement> options4 = driver.findElement(By.id("mostrarAnulats0")).findElements(By.tagName("option"));
		options4.get(options4.size()-1).click();
		
		driver.findElement(By.xpath("//*[@id='command']//button[@value='submit']")).click();
		
		// Limpiamos los datos
		driver.findElement(By.xpath("//*[@id='command']//button[@value='clean']")).click();
		
		// Comprobamos que están vacíos
		for (WebElement input : driver.findElements(By.xpath("//*[@id='command']//input"))) {
			assertTrue("El campo '"+input.getAttribute("name")+"' no se limpió", input.getAttribute("value").isEmpty());
		}
		for (WebElement select : driver.findElements(By.xpath("//*[@id='command']//select"))) {
			List<WebElement> options5 = select.findElements(By.tagName("option"));
			int i = 0;
			for (WebElement option : options5) {
				if (option.isSelected()) {
					break;
				}
				i++;
			}
			assertTrue("El campo '"+select.getAttribute("name")+"' no se limpió", i == 0);
		}		
		driver.findElement(By.xpath("//*[@id='command']//button[@value='submit']")).click();
		
	}
	
	@Test
	public void d_anular_expedient() throws InterruptedException, ParseException {	
		carregarUrlDisseny();
		seleccionarEntorn(titolEntorn);

		consultarExpedientes(null, null, nomTipusExp1);
		existeixElementAssert("//*[@id='registre']/tbody/tr[1]/td[contains(a/img/@src,'/helium/img/delete.png')]/a/img", "No tenía permisos de anulado");
		
		Thread.sleep(2000);
		
		// Anulamos el primer expediente
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]/td[7]/a")).click();
		
		acceptarConfirm("El motivo");
	}

	@Test
	public void e_mostrar_anulats() throws InterruptedException, ParseException {
		carregarUrlDisseny();
		seleccionarEntorn(titolEntorn);
		
		actions.moveToElement(driver.findElement(By.id("menuConsultes")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/expedient/consulta.html')]")));
		actions.click();
		actions.build().perform();
		
		WebElement selectTipusExpedient = driver.findElement(By.xpath("//*[@id='expedientTipus0']"));
		List<WebElement> options = selectTipusExpedient.findElements(By.tagName("option"));
		for (WebElement option : options) {
			if (option.getText().equals(nomTipusExp1)) {
				option.click();
				break;
			}
		}
		
		Thread.sleep(3000);
		
		// Seleccionamos los activos y anulados
		List<WebElement> options4 = driver.findElement(By.id("mostrarAnulats0")).findElements(By.tagName("option"));
		options4.get(options4.size()-1).click();
		
		driver.findElement(By.xpath("//*[@id='command']//button[@value='submit']")).click();
		
		// Seleccionamos todos
		List<WebElement> optionsP = driver.findElement(By.id("objectsPerPage")).findElements(By.tagName("option"));
		optionsP.get(optionsP.size()-1).click();
		
		driver.findElement(By.xpath("//*[@id='command']//button[@value='submit']")).click();
		
		// Contamos los anulados
		List<String> idsExpedient = new ArrayList<String>();
		
		for (WebElement input : driver.findElements(By.xpath("//img[@src='/helium/img/arrow_undo.png']/parent::a/parent::td/parent::tr/td[1]/input[@type='checkbox']"))) {
			idsExpedient.add(input.getAttribute("value"));
		}
		
		if (!idsExpedient.isEmpty()) {
			// Seleccionamos los anulados
			List<WebElement> options5 = driver.findElement(By.id("mostrarAnulats0")).findElements(By.tagName("option"));
			options5.get(1).click();
			
			driver.findElement(By.xpath("//*[@id='command']//button[@value='submit']")).click();
			
			// Número de expedientes
			String numExpedientes = driver.findElement(By.xpath("//*[@id='content']/span[1]")).getText();
			if (numExpedientes.contains("trobat un item")) {
				assertTrue("El número de expedientes anulados no coincidía", idsExpedient.size() == 1);
			} else {
				numExpedientes = numExpedientes.substring("S'han trobat ".length(), numExpedientes.indexOf(" items,"));
				assertTrue("El número de expedientes anulados no coincidía", idsExpedient.size() == Integer.valueOf(numExpedientes));
			}
			for (WebElement input : driver.findElements(By.xpath("//img[@src='/helium/img/arrow_undo.png']/parent::a/parent::td/parent::tr/td[1]/input[@type='checkbox']"))) {
				String idExpedient = input.getAttribute("value");
				assertTrue("El expediente con ID '"+idExpedient+"' no se encontró en la anterior pantalla de activos y anulados", idsExpedient.contains(idExpedient));
			}
		}
	}

	//@Test
	public void m_filtrar_nomes_amb_tasques_pendents() throws InterruptedException, ParseException {
		// Funcionalirar nomes disponible per la versió 3 (interfície nova)
	}

	@Test
	public void g_resultats_per_pagina() throws InterruptedException, ParseException {
		
		carregarUrlDisseny();
		seleccionarEntorn(titolEntorn);		
		consultarExpedientes(null, null, null);
		
		// Número de expedientes
		String numExpedientes = driver.findElement(By.xpath("//*[@id='content']/span[1]")).getText();
		if (numExpedientes.contains("trobat un item")) {
			numExpedientes = "1";
		} else {
			numExpedientes = numExpedientes.substring("S'han trobat ".length(), numExpedientes.indexOf(" items,"));
		}
						
		// Seleccionamos página por página
		int i = 0;
		int numPages = driver.findElement(By.xpath("//*[@id='objectsPerPage']")).findElements(By.tagName("option")).size()-1;
		while (i < numPages) {
			driver.findElement(By.xpath("//*[@id='objectsPerPage']")).findElements(By.tagName("option")).get(i).click();
			
			driver.findElement(By.xpath("//*[@id='command']//button[@value='submit']")).click();
			
			int numElementsPage = Integer.valueOf(driver.findElement(By.xpath("//*[@id='objectsPerPage']")).findElements(By.tagName("option")).get(i).getAttribute("value"));
			
			if (Integer.valueOf(numExpedientes) >= numElementsPage) {
				assertTrue("El número de expedientes por página no coincidía", numElementsPage == driver.findElements(By.xpath("//*[@id='registre']/tbody/tr")).size());
			} else {
				assertTrue("El número de expedientes encontrados era mayor a la página", numElementsPage > driver.findElements(By.xpath("//*[@id='registre']/tbody/tr")).size());
				assertTrue("El número de expedientes encontrados no coincide con los mostrados", Integer.parseInt(numExpedientes) == driver.findElements(By.xpath("//*[@id='registre']/tbody/tr")).size());
			}
			
			i++;
		}
	}

	@Test
	public void f_desplegar_tasques_expedient() throws InterruptedException, ParseException {	
		carregarUrlDisseny();
		seleccionarEntorn(titolEntorn);
		
//		// Asignamos permisos
//		assignarPermisosTipusExpedient(codTipusExp1, usuari, "DESIGN","CREATE","SUPERVISION","WRITE","MANAGE","DELETE","READ","ADMINISTRATION");
		
		consultarExpedientes(null, null, nomTipusExp1);
		existeixElementAssert("//*[@id='registre']/tbody/tr[1]/td[2]/a", "No se tenía ningún expediente");
		
		// Vamos a la tarea a tramitar del expediente
		String nomExpediente = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]/td[2]/a")).getText();
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]/td[2]/a")).click();
		
		assertTrue("No se llegó a la pantalla de tasques pendents", "Tasques pendents".equals(driver.findElement(By.xpath("//*[@id='page-title']/h2/span")).getText()));
		
		//TODO: Pendiente de solucionar la codificacion del enlace para titulos de expediente con acentos
		//assertTrue("No se realizó la búsqueda con el nombre del expediente correcto", nomExpediente.equals(driver.findElement(By.xpath("//*[@id='expedient0']")).getAttribute("value")));
	}
	
	@Test
	public void h_acces_a_tramitacio_de_tasca() throws InterruptedException, ParseException {	
		carregarUrlDisseny();
		seleccionarEntorn(titolEntorn);
		
//		// Asignamos permisos
//		assignarPermisosTipusExpedient(codTipusExp1, usuari, "DESIGN","CREATE","SUPERVISION","WRITE","MANAGE","DELETE","READ","ADMINISTRATION");
		
		consultarExpedientes(null, null, nomTipusExp1);
		existeixElementAssert("//*[@id='registre']/tbody/tr[1]/td[2]/a", "No se tenía ningún expediente");
		
		// Vamos a la tarea a tramitar del expediente
		String nomExpediente = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]/td[2]/a")).getText();
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]/td[2]/a")).click();
		
		assertTrue("No se llegó a la pantalla de tasques pendents", "Tasques pendents".equals(driver.findElement(By.xpath("//*[@id='page-title']/h2/span")).getText()));
		
		//TODO: Pendiente de solucionar la codificacion del enlace para titulos de expediente con acentos
		//assertTrue("No se realizó la búsqueda con el nombre del expediente correcto", nomExpediente.equals(driver.findElement(By.xpath("//*[@id='expedient0']")).getAttribute("value")));
		
		if (existeixElement("//*[@id='registre']/tbody/tr[1]/td[1]/a")) {
			String nomTasca = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]/td[1]/a")).getText();
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]/td[1]/a")).click();
			
			assertTrue("No se llegó a la pantalla de tramitación de la tarea", nomTasca.equals(driver.findElement(By.xpath("//*[@id='page-title']/h2/span")).getText()));
		}
	}

	@Test
	public void i_obrir_expedient() throws InterruptedException, ParseException {	
		carregarUrlDisseny();
		seleccionarEntorn(titolEntorn);
		
//		// Asignamos permisos
//		assignarPermisosTipusExpedient(codTipusExp1, usuari, "DESIGN","CREATE","SUPERVISION","WRITE","MANAGE","DELETE","READ","ADMINISTRATION");
		
		consultarExpedientes(null, null, nomTipusExp1);
		existeixElementAssert("//*[@id='registre']/tbody/tr[1]/td/a/img[@src = '/helium/img/information.png']", "No exitía la imagen de ver la información de un expediente");
		
		// Abrimos el expediente
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]/td/a/img[@src = '/helium/img/information.png']")).click();
		existeixElementAssert("//*[@id='tabnav']/li/a[contains(text(), 'Expedient')]", "No exitía la pestaña de expediente");
	}

	@Test
	public void j_seleccio_tasques_i_acces_a_tramitacio_massiva() throws InterruptedException, ParseException {	
		carregarUrlDisseny();
		seleccionarEntorn(titolEntorn);
		
		consultarExpedientes(null, null, nomTipusExp1);
		
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
		if (numExpedientes.contains("trobat un item")) {
			numExpedientes = "1";
		} else {
			numExpedientes = numExpedientes.substring("S'han trobat ".length(), numExpedientes.indexOf(" items,"));
		}
		
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
			if (++j <= numPages)
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
			assertTrue("El elemento '"+nomExpedient+"' no tenía el tipo de expediente correcto", tipoExpedient.equals(nomTipusExp1));
			i++;
		}		
		assertTrue("El número de elementos en la tramitación masiva no coincidía", Integer.valueOf(numExpedientes) == (i-1));
		
		driver.findElement(By.xpath("//button[contains(text(), 'Canviar selecció')]")).click();
		
		// Si es vol provar la funcionalitat del botó de execucions massives per tipus d'expedient
		// cal donar permisos d'administració a l'entorn a l'usuari
		
//		// Executar accions massives sobre el tipus d'expedient
//		driver.findElement(By.xpath("//*[@id='ejecucionMasivaTotsTipus']")).click();
//		driver.findElement(By.xpath("//img[@src='/helium/img/magnifier_zoom_in.png']")).click();		
//		i = 1;
//		while (existeixElement("//*[@id='registre']/tbody/tr["+i+"]/td[1]")) {
//			String nomExpedient = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+i+"]/td[1]")).getText();
//			String tipoExpedient = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+i+"]/td[3]")).getText();
//			assertTrue("El elemento '"+nomExpedient+"' no tenía el tipo de expediente correcto", tipoExpedient.equals(nomTipusExp1));
//			i++;
//		}		
//		assertTrue("El número de elementos en la tramitación masiva no coincidía", Integer.valueOf(numExpedientes) == (i-1));
	}

	@Test
	public void l1_aturar_expedient() throws InterruptedException, ParseException {
		carregarUrlConfiguracio();
		seleccionarEntorn(titolEntorn);
		
		// Comprobamos permisos de borrado
		// Los quitamos
		assignarPermisosTipusExpedient(codTipusExp1, usuari, "READ");
	}
	@Test
	public void l2_aturar_expedient() throws InterruptedException, ParseException {
		carregarUrlDisseny();
		seleccionarEntorn(titolEntorn);
		
		// Comprobamos permisos de borrado
		consultarExpedientes(null, null, nomTipusExp1);
		
		existeixElementAssert("//*[@id='registre']/tbody/tr[1]/td/a/img[@src = '/helium/img/information.png']", "No exitía la imagen de ver la información de un expediente");
		
		// Abrimos el expediente
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]/td/a/img[@src = '/helium/img/information.png']")).click();
		
		noExisteixElementAssert("//*[@id='tabnav']/li/a[contains(text(), 'Eines')]", "Tenía permisos de eines");
	}
	@Test
	public void l3_aturar_expedient() throws InterruptedException, ParseException {
		carregarUrlConfiguracio();
		seleccionarEntorn(titolEntorn);
		
		// Comprobamos permisos de borrado
		// Ponemos los permisos
		assignarPermisosTipusExpedient(codTipusExp1, usuari, "CREATE","WRITE","DELETE","READ");
	}
	@Test
	public void l4_aturar_expedient() throws InterruptedException, ParseException {
		carregarUrlDisseny();
		seleccionarEntorn(titolEntorn);
		
		consultarExpedientes(null, null, nomTipusExp1);
		
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
		
		consultarExpedientes(null, null, nomTipusExp1);
		
		existeixElementAssert("//*[@id='registre']/tbody/tr[1]/td/img[@src = '/helium/img/stop.png']", "No exitía la imagen de expediente aturado");
	}
	
//	@Test
//	public void l_aturar_expedient() throws InterruptedException, ParseException {
//		carregarUrlDisseny();
//		seleccionarEntorn(titolEntorn);
//		
//		// Comprobamos permisos de borrado
//		
//		// Los quitamos
//		assignarPermisosTipusExpedient(codTipusExp1, usuari, "READ");
//		
//		consultarExpedientes(null, null, nomTipusExp1);
//		
//		existeixElementAssert("//*[@id='registre']/tbody/tr[1]/td/a/img[@src = '/helium/img/information.png']", "No exitía la imagen de ver la información de un expediente");
//		
//		// Abrimos el expediente
//		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]/td/a/img[@src = '/helium/img/information.png']")).click();
//		
//		noExisteixElementAssert("//*[@id='tabnav']/li/a[contains(text(), 'Eines')]", "Tenía permisos de eines");
//		
//		// Los ponemos
//		assignarPermisosTipusExpedient(codTipusExp1, usuari, "CREATE","WRITE","DELETE","READ");
//		
//		consultarExpedientes(null, null, nomTipusExp1);
//		
//		// Abrimos el expediente
//		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]/td/a/img[@src = '/helium/img/information.png']")).click();
//		
//		existeixElementAssert("//*[@id='tabnav']/li/a[contains(text(), 'Eines')]", "No tenía permisos de eines");
//		
//		noExisteixElementAssert("//*[@id='content']/div[@class='missatgesAturat']", "El expediente ya estaba aturado");
//		
//		// Aturamos el expediente
//		driver.findElement(By.xpath("//*[@id='tabnav']/li/a[contains(text(), 'Eines')]")).click();
//		driver.findElement(By.xpath("//*[@id='content']/div/h3[1]/a")).click();
//		driver.findElement(By.xpath("//*[@id='motiu0']")).sendKeys("El motivo");
//		driver.findElement(By.xpath("//button[contains(text(), 'Aturar')]")).click();
//		acceptarAlerta();
//		
//		existeixElementAssert("//*[@id='infos']/p", "No aturo el expediente correctamente");
//		existeixElementAssert("//*[@id='content']/div[@class='missatgesAturat']", "No existía el mensaje de aturar expediente");
//		
//		consultarExpedientes(null, null, nomTipusExp1);
//		
//		existeixElementAssert("//*[@id='registre']/tbody/tr[1]/td/img[@src = '/helium/img/stop.png']", "No exitía la imagen de expediente aturado");
//	}

	@Test
	public void n1_borrar_expedient() throws InterruptedException, ParseException {	
		carregarUrlConfiguracio();
		seleccionarEntorn(titolEntorn);
		
		// Comprobamos permisos de borrado
		// Los quitamos
		assignarPermisosTipusExpedient(codTipusExp1, usuari, "DESIGN","CREATE","SUPERVISION","WRITE","MANAGE","READ","ADMINISTRATION");
		
	}
	@Test
	public void n2_borrar_expedient() throws InterruptedException, ParseException {	
		carregarUrlDisseny();
		seleccionarEntorn(titolEntorn);
		
		// Comprobamos permisos de borrado
		consultarExpedientes(null, null, nomTipusExp1);
		
		noExisteixElementAssert("//*[@id='registre']/tbody/tr[1]/td[contains(a/img/@src,'/helium/img/cross.png')]/a/img", "Tenía permisos de borrado");
	}		
	@Test
	public void n3_borrar_expedient() throws InterruptedException, ParseException {	
		carregarUrlConfiguracio();
		seleccionarEntorn(titolEntorn);
		
		// Comprobamos permisos de borrado
		// Ponemos los permisos
		assignarPermisosTipusExpedient(codTipusExp1, usuari, "DESIGN","CREATE","SUPERVISION","WRITE","MANAGE","DELETE","READ","ADMINISTRATION");
	}
	
	@Test
	public void n4_borrar_expedient() throws InterruptedException, ParseException {	
		carregarUrlDisseny();
		seleccionarEntorn(titolEntorn);
		
		consultarExpedientes(null, null, nomTipusExp1);
		
		try { Thread.sleep(3000); }catch (Exception ex){}
		
		existeixElementAssert("//*[@id='registre']/tbody/tr[1]/td[contains(a/img/@src,'/helium/img/cross.png')]/a/img", "No tenía permisos de borrado");
		
		// Borramos el primer expediente
		borrarPrimerExpediente();
	}
	
//	@Test
//	public void n_borrar_expedient() throws InterruptedException, ParseException {	
//		carregarUrlDisseny();
//		seleccionarEntorn(titolEntorn);
//		
//		// Comprobamos permisos de borrado
//		
//		// Los quitamos
//		assignarPermisosTipusExpedient(codTipusExp1, usuari, "DESIGN","CREATE","SUPERVISION","WRITE","MANAGE","READ","ADMINISTRATION");
//		
//		consultarExpedientes(null, null, nomTipusExp1);
//		
//		noExisteixElementAssert("//*[@id='registre']/tbody/tr[1]/td[contains(a/img/@src,'/helium/img/cross.png')]/a/img", "Tenía permisos de borrado");
//		
//		// Los ponemos
//		assignarPermisosTipusExpedient(codTipusExp1, usuari, "DESIGN","CREATE","SUPERVISION","WRITE","MANAGE","DELETE","READ","ADMINISTRATION");
//		
//		consultarExpedientes(null, null, nomTipusExp1);
//		
//		existeixElementAssert("//*[@id='registre']/tbody/tr[1]/td[contains(a/img/@src,'/helium/img/cross.png')]/a/img", "No tenía permisos de borrado");
//		
//		// Borramos el primer expediente
//		borrarPrimerExpediente();
//	}
	
	
	@Test
	public void z0_finalitzacio() {
		carregarUrlDisseny();
		seleccionarEntorn(titolEntorn);
		// Eliminam tots els expedients
		consultarExpedientes(null, null, nomTipusExp1, true);
		while (existeixElement("//*[@id='registre']/tbody/tr[1]/td[contains(a/img/@src,'/helium/img/cross.png')]/a/img")) {
			borrarPrimerExpediente();
		}
		consultarExpedientes(null, null, nomTipusExp2, true);
		while (existeixElement("//*[@id='registre']/tbody/tr[1]/td[contains(a/img/@src,'/helium/img/cross.png')]/a/img")) {
			borrarPrimerExpediente();
		}
	}
	
	@Test
	public void z1_finalitzacio() {
		carregarUrlConfiguracio();
		seleccionarEntorn(titolEntorn);
		eliminarDefinicioProces(nomDefProc);
		eliminarTipusExpedient(codTipusExp1);
		eliminarTipusExpedient(codTipusExp2);
		eliminarEntorn(entorn);
	}
	
	private void tramitarTasca(int numTasca, int i) {
		actions.moveToElement(driver.findElement(By.id("menuTasques")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/tasca/personaLlistat.html')]")));
		actions.click();
		actions.build().perform();
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]/td[1]")).click();
		
		switch (numTasca) {
		case 1:
			driver.findElement(By.id("v10")).clear();
			driver.findElement(By.id("v10")).sendKeys("Variable 1 - " + i);
			driver.findElement(By.id("vtitol0")).clear();
			driver.findElement(By.id("vtitol0")).sendKeys("Titol expedient " + i);
			break;
		case 2:
			driver.findElement(By.id("v20")).clear();
			driver.findElement(By.id("v20")).sendKeys("Variable 2 -" + i);
			break;
		case 3:
			driver.findElement(By.id("v30")).clear();
			driver.findElement(By.id("v30")).sendKeys("Variable 3 -" + i);
			break;
		}
						
		driver.findElement(By.xpath("//*/button[contains(text(),'Finalitzar')]")).click();
		if (isAlertPresent()) {acceptarAlerta();}
		
		try {Thread.sleep(3000);}catch (Exception ex) {}
		
		existeixElementAssert("//*[@id='infos']/p", "No se finalizó correctamente");
	}
}
