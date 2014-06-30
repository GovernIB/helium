package net.conselldemallorca.helium.test.tramitacio;

import static org.junit.Assert.assertTrue;

import java.util.Date;

import net.conselldemallorca.helium.test.util.BaseTest;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AfegirExpedientRelacionat extends BaseTest {

	String entorn = carregarPropietat("entorn.nom", "Nom de l'entorn de proves no configurat al fitxer de properties");
	String titolEntorn = carregarPropietat("entorn.nom", "Titol de l'entorn de proves no configurat al fitxer de properties");
	String codTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.codi", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	String nomDefProc = carregarPropietat("defproc.deploy.definicio.proces.nom", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String pathDefProc = carregarPropietat("defproc.mod.exp.deploy.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String exportDefProc = carregarPropietat("defproc.mod.exp.export.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String nomTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.nom", "Nom del tipus d'expedient de proves no configurat al fitxer de properties");
	
	static String entornActual;

	@Test
	public void a_afegirExpedientRelacionat() throws InterruptedException {
		carregarUrlConfiguracio();
		
		seleccionarEntorno(entorn);
		
		desplegarDefinicioProcesEntorn(nomTipusExp, nomDefProc, pathDefProc);
		importarDadesDefPro(nomDefProc, exportDefProc);
		
		screenshotHelper.saveScreenshot("tramitar/AfegirExpedientRelacionat/1.png");
		
		String[] res_dest = iniciarExpediente(nomDefProc,codTipusExp,"SE-21/2014", "Expedient de prova Selenium " + (new Date()).getTime() );
		String[] res_orig = iniciarExpediente(nomDefProc,codTipusExp,"SE-22/2014", "Expedient de prova Selenium " + (new Date()).getTime() );

		consultarExpedientes(res_orig[0], res_orig[1], nomTipusExp);

		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]/td[6]/a/img")).click();

		driver.findElement(By.xpath("//*[@id='content']/form[2]/button")).click();

		driver.findElement(By.xpath("//*[@id='suggest_expedientIdDesti0']")).clear();
		driver.findElement(By.xpath("//*[@id='suggest_expedientIdDesti0']")).sendKeys("Expedient");
		driver.findElement(By.xpath("//*[@class='ac_results']/ul/li[1]")).click();

		driver.findElement(By.xpath("//*[@id='relacionarCommand']/div/div[3]/button[1]")).click();
		
		assertTrue("El expediente no se relacionó de forma correcta", driver.findElements(By.xpath("//*[@id='registre']/tbody/tr")).size() > 0);
		
		screenshotHelper.saveScreenshot("tramitar/AfegirExpedientRelacionat/3.png");		
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]/td[5]/a/img")).click();
		acceptarAlerta();
		
		String textoInfo = driver.findElement(By.xpath("//*[@id='infos']/p")).getText();
		assertTrue("No se borró el expediente relacionado",!textoInfo.isEmpty());
		
		screenshotHelper.saveScreenshot("tramitar/AfegirExpedientRelacionat/4.png");
		
		eliminarExpedient(res_orig[0], res_orig[1]);
		
		eliminarExpedient(res_dest[0], res_dest[1]);
		
		screenshotHelper.saveScreenshot("tramitar/AfegirExpedientRelacionat/5.png");
		
		eliminarDefinicioProces(nomDefProc);
		
		screenshotHelper.saveScreenshot("tramitar/AfegirExpedientRelacionat/6.png");
	}
}
