package net.conselldemallorca.helium.test.informes;

import static org.junit.Assert.fail;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;

import net.conselldemallorca.helium.test.util.BaseTest;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class InformesExpedients extends BaseTest {

	String entorn = carregarPropietat("informe.entorn.nom", "Nom de l'entorn de proves no configurat al fitxer de properties");
	String titolEntorn = carregarPropietat("informe.entorn.titol", "Titol de l'entorn de proves no configurat al fitxer de properties");
	String usuariAdmin = carregarPropietat("test.base.usuari.configuracio", "Usuari configuració de l'entorn de proves no configurat al fitxer de properties");
	String usuari = carregarPropietat("test.base.usuari.feina", "Usuari feina de l'entorn de proves no configurat al fitxer de properties");
	String pathDefProc = carregarPropietatPath("informe.deploy.definicio.proces.path", "Path de la definició de procés de proves no configurat al fitxer de properties");
	String nomDefProc = carregarPropietat("informe.deploy.definicio.proces.nom", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String codTipusExp1 = carregarPropietat("informe.deploy.tipus.expedient.codi.1", "Codi del tipus d'expedient de proves 1 no configurat al fitxer de properties");
	String pathExportEntorn = carregarPropietatPath("informe.deploy.entorn.path", "Path de l'entorn de proves no configurat al fitxer de properties");
	String codTipusExp = "provesPep"; //carregarPropietat("defproc.deploy.tipus.expedient.codi", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	String tipusExp = "Proves pep"; //carregarPropietat("defproc.deploy.tipus.expedient.nom", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	String codi = carregarPropietat("informe.consulta.codi", "Codi de la consulta");
	String titol = carregarPropietat("informe.consulta.titol", "titol de la consulta");
	String jrxml = carregarPropietat("informe.consulta.informe.path", "informe en format jrxml");
	String nomInforme = carregarPropietat("informe.consulta.titol", "Nom de l'informe");
	String codiTipusExp = carregarPropietat("informe.deploy.tipus.expedient.codi.1", "Codi del tipus d'expedient de l'informe");
	
	@Test
	public void a_inicialitzacio() {
		carregarUrlConfiguracio();
		crearEntorn(entorn, titolEntorn);
		assignarPermisosEntorn(entorn, usuariAdmin, "DESIGN", "ORGANIZATION", "READ", "ADMINISTRATION");
		assignarPermisosEntorn(entorn, usuari, "DESIGN", "ORGANIZATION", "READ");
		marcarEntornDefecte(titolEntorn);
		seleccionarEntorn(titolEntorn);
		crearTipusExpedient(codTipusExp1, codTipusExp);
		seleccionarTipExp(codTipusExp1);
		assignarPermisosTipusExpedient(codTipusExp1, usuariAdmin, "DESIGN","CREATE","SUPERVISION","WRITE","MANAGE","DELETE","READ","ADMINISTRATION");
		assignarPermisosTipusExpedient(codTipusExp1, usuari, "CREATE","WRITE","DELETE","READ");
		desplegarDP(nomDefProc,pathDefProc, codTipusExp);
	}
	
	
	
	@Test
	public void seleccioConsulta() throws InterruptedException, ParseException {
		
		carregarUrlConfiguracio();
		seleccionarEntorn(entorn);
		assignarPermisosTipusExpedient(codTipusExp, usuari, "DESIGN","CREATE","SUPERVISION","WRITE","MANAGE","DELETE","READ","ADMINISTRATION");
		crearConsultaInforme(codi, titol, tipusExp, jrxml);
		
	}
	
	//@Test
	public void anular_expedient() throws InterruptedException, ParseException {	
		carregarUrlConfiguracio();
		assignarPermisosTipusExpedient(codTipusExp, usuari, "DESIGN","CREATE","SUPERVISION","DELETE","MANAGE","READ");
		
		//crearConsultaInforme(codi, titol, codiTipusExp, jrxml);
		
		//consultarExpedientsInformes(null, null, tipusExp, nomConsulta);
		
		noExisteixElementAssert("//*[@id='registre']/tbody/tr[1]/td[contains(a/img/@src,'/helium/img/delete.png')]/a/img", "Tenía permisos de anulado");
		
		// Los ponemos
		assignarPermisosTipusExpedient(codTipusExp, usuari, "DESIGN","CREATE","SUPERVISION","WRITE","MANAGE","DELETE","READ","ADMINISTRATION");
		
		consultarExpedientes(null, null, tipusExp);
		
		existeixElementAssert("//*[@id='registre']/tbody/tr[1]/td[contains(a/img/@src,'/helium/img/delete.png')]/a/img", "No tenía permisos de anulado");
		
		// Anulamos el primer expediente
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]/td[7]/a")).click();
		acceptarConfirm("El motivo");
	}

//	@Test
	public void borrar_expedient() throws InterruptedException, ParseException {	
		carregarUrlConfiguracio();
		
		seleccionarEntorn(entorn);
		
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