package net.conselldemallorca.helium.test.tramitacio;

import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import net.conselldemallorca.helium.test.util.BaseTest;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DocumentsExpedient extends BaseTest {

	String entorn = carregarPropietat("entorn.nom", "Nom de l'entorn de proves no configurat al fitxer de properties");
	String codTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.codi", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	String nomDefProc = carregarPropietat("defproc.deploy.definicio.proces.nom", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String pathDefProc = carregarPropietat("defproc.mod.exp.deploy.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String nomTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.nom", "Nom del tipus d'expedient de proves no configurat al fitxer de properties");
	String pathArxiuPDF1 = carregarPropietat("deploy.arxiu.pdf.tramitacio_1", "Documento PDF a adjuntar 1");	
	String pathArxiuPDF2 = carregarPropietat("deploy.arxiu.pdf.tramitacio_2", "Documento PDF a adjuntar 1");
	
//	@Test
	public void a_adjuntar_documents() throws InterruptedException {
		carregarUrlConfiguracio(); 
		
		// Selecció directe
		actions.moveToElement(driver.findElement(By.id("menuEntorn")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//li[@id='menuEntorn']/ul[@class='llista-entorns']/li[contains(., '" + entorn + "')]/a")));
		actions.click();
		actions.build().perform();
		
		screenshotHelper.saveScreenshot("documentsexpedient/adjuntar_documents/1.png");
		
		desplegarDefinicioProcesEntorn(nomTipusExp, nomDefProc, pathDefProc);
		
		importarDadesExpedient(nomDefProc, properties.getProperty("defproc.mod.exp.export.arxiu.path"));
					
		String[] res = iniciarExpediente(nomDefProc,codTipusExp,"SE-22/2014", "Expedient de prova Selenium " + (new Date()).getTime() );
		
		// Adjuntamos 2 documentos
		adjuntarDocExpediente(res[0], res[1], "Título del documento 1", "12/12/2014", pathArxiuPDF1);
		adjuntarDocExpediente(res[0], res[1], "Título del documento 2", "13/12/2014", pathArxiuPDF2);
		
		screenshotHelper.saveScreenshot("documentsexpedient/adjuntar_documents/3.png");
		
		eliminarExpedient(res[0], res[1]);
			
		// Eliminar la def de proceso
		eliminarDefinicioProces(nomDefProc);
		
		screenshotHelper.saveScreenshot("documentsexpedient/adjuntar_documents/4.png");
	}
	
	@Test
	public void b_visualizacio_documents_i_descarrega() throws InterruptedException {
		carregarUrlConfiguracio(); 
		
		// Selecció directe
		actions.moveToElement(driver.findElement(By.id("menuEntorn")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//li[@id='menuEntorn']/ul[@class='llista-entorns']/li[contains(., '" + entorn + "')]/a")));
		actions.click();
		actions.build().perform();
		
		screenshotHelper.saveScreenshot("documentsexpedient/visualizacio_documents_i_descarrega/1.png");
		
		actions.moveToElement(driver.findElement(By.id("menuConsultes")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//*[@id='menuConsultes']/ul/li[1]/a")));
		actions.click();
		actions.build().perform();
		
		WebElement selectTipusExpedient = driver.findElement(By.xpath("//*[@id='expedientTipus0']"));
		List<WebElement> options = selectTipusExpedient.findElements(By.tagName("option"));
		for (WebElement option : options) {
			if (option.getText().equals(properties.getProperty("defproc.deploy.tipus.expedient.nom"))) {
				option.click();
				break;
			}
		}
		
		driver.findElement(By.xpath("//*[@id='command']/div[2]/div[6]/button[1]")).click();	
		
		screenshotHelper.saveScreenshot("documentsexpedient/adjuntar_documents/2.png");
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]/td[6]/a/img")).click();
		
		driver.findElement(By.xpath("//*[@id='tabnav']/li[3]/a")).click();
		
		// Comprobamos que haya 2 documentos
		assertTrue("No había 2 documentos adjuntos", driver.findElements(By.xpath("//*[@id='codi']/tbody/tr")).size() == 2);
		
		// Los descargamos
		
	}
}
