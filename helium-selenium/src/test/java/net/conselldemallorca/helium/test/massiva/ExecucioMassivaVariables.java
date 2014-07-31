package net.conselldemallorca.helium.test.massiva;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.conselldemallorca.helium.test.util.BaseTest;
import net.conselldemallorca.helium.test.util.VariableExpedient;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ExecucioMassivaVariables extends BaseTest {
	String entorn = carregarPropietat("tramas.entorn.nom", "Nom de l'entorn de proves no configurat al fitxer de properties");
	String titolEntorn = carregarPropietat("tramas.entorn.titol", "Titol de l'entorn de proves no configurat al fitxer de properties");
	String usuari = carregarPropietat("test.base.usuari.configuracio", "Usuari configuració de l'entorn de proves no configurat al fitxer de properties");
	String nomDefProc = carregarPropietat("defproc.deploy.definicio.proces.nom", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String nomTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.nom", "Nom del tipus d'expedient de proves no configurat al fitxer de properties");
	String codTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.codi", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	String exportTipExpProc = carregarPropietatPath("tipexp.tasca_dades_doc.exp.export.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	int numExpedientesTramMasiva = Integer.parseInt(carregarPropietat("tramas.num_expedientes_tram_masiva", "Número de espedientes para las pruebas de tramitación masiva al fitxer de properties"));
	
	@Test
	public void a0_inicialitzacio() {
		carregarUrlConfiguracio();
		crearEntorn(entorn, titolEntorn);
		assignarPermisosEntorn(entorn, usuari, "DESIGN", "ORGANIZATION", "READ", "ADMINISTRATION");
		seleccionarEntorn(titolEntorn);
		crearTipusExpedient(nomTipusExp, codTipusExp);
		assignarPermisosTipusExpedient(codTipusExp, usuari, "DESIGN","CREATE","SUPERVISION","WRITE","MANAGE","DELETE","READ","ADMINISTRATION");
	}
	
	@Test
	public void a_crear_dades() throws InterruptedException {
		carregarUrlConfiguracio();
		
		seleccionarEntorn(titolEntorn);
		
		importarDadesTipExp(codTipusExp, exportTipExpProc);
		
		screenshotHelper.saveScreenshot("tramitar/dadesexpedient/crear_dades/1.png");
	}

	@Test
	public void b_visualitzacio_variables_i_modificar_variables() throws InterruptedException {
		carregarUrlConfiguracio();
		
		seleccionarEntorn(titolEntorn);
		
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/definicioProces/llistat.html')]")));
		actions.click();
		actions.build().perform();
		
		screenshotHelper.saveScreenshot("tramitar/dadesexpedient/visualizacio_dades_process/1.png");
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'"+nomDefProc+"')]")).click();
				
		driver.findElement(By.xpath("//*[@id='tabnav']//a[contains(@href,'/definicioProces/campLlistat.html')]")).click();
		
		List<VariableExpedient> listaVariables = new ArrayList<VariableExpedient>();
		
		// Leemos las variables
		int i = 1;
		while(i <= driver.findElements(By.xpath("//*[@id='registre']/tbody/tr")).size()) {
			VariableExpedient variable = new VariableExpedient();
			
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+i+"]")).click();
			
			String codi = driver.findElement(By.xpath("//*[@id='codi0']")).getAttribute("value");
			String etiqueta = driver.findElement(By.xpath("//*[@id='etiqueta0']")).getAttribute("value");
			String tipo = driver.findElement(By.xpath("//*[@id='tipus0']")).getAttribute("value");
			String observaciones = driver.findElement(By.xpath("//*[@id='observacions0']")).getText();
			boolean multiple = driver.findElement(By.xpath("//*[@id='multiple0']")).isSelected();
			boolean oculta = driver.findElement(By.xpath("//*[@id='ocult0']")).isSelected();
			
			variable.setCodi(codi);
			variable.setEtiqueta(etiqueta);
			variable.setTipo(tipo);
			variable.setObservaciones(observaciones);
			variable.setOculta(oculta);
			variable.setMultiple(multiple);
			
			screenshotHelper.saveScreenshot("tramitar/dadesexpedient/visualizacio_dades_process/2"+i+".png");
			
			driver.findElement(By.xpath("//*[@id='command']/div[4]/button[2]")).click();
			
			if ("REGISTRE".equals(tipo)) {
				WebElement button = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+i+"]/td[6]/form/button"));
				button.click();
				int j = 1;
				while(j <= driver.findElements(By.xpath("//*[@id='registre']/tbody/tr")).size()) {
					VariableExpedient variableReg = new VariableExpedient();
					
					driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+j+"]")).click();
					
					String[] var = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+j+"]/td[1]")).getText().split("/");
					String codiReg = var[0];
					String etiquetaReg = var[1];
					boolean obligatorioReg = "Si".equals(driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+j+"]/td[3]")).getText());
					String tipoReg = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+j+"]/td[2]")).getText();

					variableReg.setCodi(codiReg);
					variableReg.setEtiqueta(etiquetaReg);
					variableReg.setTipo(tipoReg);
					variableReg.setObligatorio(obligatorioReg);
					variableReg.setMultiple(multiple); // Si lo era la variable padre del registro
					
					variable.getRegistro().add(variableReg);
					
					screenshotHelper.saveScreenshot("tramitar/dadesexpedient/visualizacio_dades_process/3"+i+"-"+j+".png");
					j++;
				}
				
				driver.findElement(By.xpath("//*[@id='command']/div[2]/button[2]")).click();
			}
			
			listaVariables.add(variable);
			
			screenshotHelper.saveScreenshot("tramitar/dadesexpedient/visualizacio_dades_process/3"+i+".png");
			i++;
		}
		
		// Vemos el resto de parámetros de la primera tarea
		driver.findElement(By.xpath("//*[@id='tabnav']//a[contains(@href,'/definicioProces/tascaLlistat.html')]")).click();
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr/td[3]/form/button")).click();
				
		Iterator<VariableExpedient> it = listaVariables.iterator();
		while(it.hasNext()) {
			VariableExpedient var = it.next();
			if (existeixElement("//td[contains(text(),'"+var.getCodi()+"/"+var.getEtiqueta()+"')]")) {
				boolean obligatorio = driver.findElement(By.xpath("//td[contains(text(),'"+var.getCodi()+"/"+var.getEtiqueta()+"')]/parent::tr/td[4]/input")).isSelected();
				var.setObligatorio(obligatorio);
				
				boolean readonly = driver.findElement(By.xpath("//td[contains(text(),'"+var.getCodi()+"/"+var.getEtiqueta()+"')]/parent::tr/td[5]/input")).isSelected();
				var.setReadOnly(readonly);
			} else {
				it.remove();
			}
		}
		
		// Iniciamos los expedientes
		List<String[]> expedientes = new ArrayList<String[]>();
		for (int j = 0; j < numExpedientesTramMasiva; j++) {
			expedientes.add(iniciarExpediente(codTipusExp,"SE-"+j+"/2014", "Expedient de prova Selenium " + (new Date()).getTime() ));
		}
		
		consultarExpedientes(null, null, nomTipusExp);
		
		driver.findElement(By.xpath("//*[@id='massivaInfoForm']/button[2]")).click();
				
		// Visualizamos que se muestren todas las variables
		WebElement selectVar = driver.findElement(By.xpath("//*[@id='var0']"));
		List<WebElement> optionsVar = selectVar.findElements(By.tagName("option"));
		optionsVar.remove(0);
		for (VariableExpedient variable : listaVariables) {
			boolean encontrado = false;
			for (WebElement var : optionsVar) {
				if (var.getAttribute("value").equals(variable.getCodi())) {
					encontrado = true;
				}
			}
			assertTrue("La variable '"+variable.getEtiqueta()+"' no se encontró en la lista de variables del expediente", encontrado);
		}
		
		// Modificamos cada variable
		for (VariableExpedient variable : listaVariables) {
			WebElement vars = driver.findElement(By.xpath("//*[@id='var0']"));
			for (WebElement var : vars.findElements(By.tagName("option"))) {
				if (var.getAttribute("value").equals(variable.getCodi())) {
					var.click();
				}
			}
			
			driver.findElement(By.xpath("//*[@id='modificarVariablesMasCommand']//button[contains(@onclick,'subvar')]")).click();
			acceptarAlerta();
			
			comprobarVariable(variable, false, false);
			
			driver.findElement(By.xpath("//*[@id='command']//button[contains(@onclick,'submit')]")).click();
			
			esperaFinExecucioMassiva(expedientes);
			
			// Buscamos la variable en cada uno de los expedientes
			for (int k = 1; k <= numExpedientesTramMasiva; k++) {
				consultarExpedientes(null, null, nomTipusExp);
				driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+k+"]//a[contains(@href,'/expedient/info.html')]")).click();
				
				driver.findElement(By.xpath("//*[@id='tabnav']//a[contains(@href,'/expedient/dades.html')]")).click();
				
				// Buscamos la variable también en las agrupaciones
				for(WebElement agrupacion : driver.findElements(By.xpath("//*[@id='dades-proces']//img[contains(@src,'/img/magnifier_zoom_in.png')]"))){
					agrupacion.click();
				}
				
				if (!variable.isOculta()) { 
					existeixElementAssert("//td[contains(text(),'"+variable.getEtiqueta()+"')]","La variable '"+variable.getEtiqueta()+"' no existía");
					
					// Comprobamos que el valor de cada variable coincida
					String valor = driver.findElement(By.xpath("//tr[contains(td/text(),'"+variable.getEtiqueta()+"')]/td[2]")).getText().trim();
					if (!variable.isMultiple() && variable.getRegistro().isEmpty())
						assertTrue("El valor de la variable '"+variable.getEtiqueta()+"' no era el esperado: Esperaba '"+variable.getValor()+"' y encontró '"+valor+"'", variable.getValor().equals(valor));
					else
						assertTrue("El valor de la variable '"+variable.getEtiqueta()+"' no era el esperado", !valor.isEmpty());
				} else {
					noExisteixElementAssert("//td[contains(text(),'"+variable.getEtiqueta()+"')]","La variable oculta '"+variable.getEtiqueta()+"' existía");
				}
			}
			
			consultarExpedientes(null, null, nomTipusExp);
			
			driver.findElement(By.xpath("//*[@id='massivaInfoForm']/button[2]")).click();
		}
		
		// Eliminamos los expedientes
		eliminarExpedient(null, null, nomTipusExp);
	}

	@Test
	public void z_limpiar() throws InterruptedException {
		carregarUrlConfiguracio();
		
		seleccionarEntorn(titolEntorn);
		
		eliminarExpedient(null, null, nomTipusExp);
		
		// Eliminar el tipo de expediente
		eliminarTipusExpedient(codTipusExp);
		
		eliminarEntorn(entorn);
		
		screenshotHelper.saveScreenshot("TasquesDadesTasca/finalizar_expedient/1.png");	
	}
}
