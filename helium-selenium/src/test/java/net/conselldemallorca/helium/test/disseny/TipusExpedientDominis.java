package net.conselldemallorca.helium.test.disseny;

import static org.junit.Assert.fail;
import net.conselldemallorca.helium.test.util.BaseTest;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TipusExpedientDominis extends BaseTest {
	
	String entorn 		= carregarPropietat("tipexp.entorn.nom", "Nom de l'entorn de proves no configurat al fitxer de properties");
	String titolEntorn	= carregarPropietat("tipexp.entorn.titol", "Titol de l'entorn de proves no configurat al fitxer de properties");
	
	String usuari 		= carregarPropietat("test.base.usuari.disseny", "Usuari feina de l'entorn de proves no configurat al fitxer de properties");
	String usuariAdmin  = carregarPropietat("test.base.usuari.configuracio", "Usuari configuració de l'entorn de proves no configurat al fitxer de properties");
	
	String nomTipusExp	= carregarPropietat("tipexp.dominis.tipus.expedient.nom", "Nom del tipus d'expedient de proves no configurat al fitxer de properties");
	String codTipusExp	= carregarPropietat("tipexp.dominis.tipus.expedient.codi", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	
	//XPATHS DE ENUMERACIONS
	String pestanyaDominis	= "//a[contains(@href, '/helium/expedientTipus/dominiLlistat.html')]";
	String botoNouDomini	= "//*[@id='content']/form/button";
	String botoGuardaDomini	= "//*[@id='command']/div[4]/button[1]";
	String botoCancelarDomini = "//*[@id='command']/div[4]/button[2]";
	String codiDominiSQL	= "domSQLid";
	String codiDominiWS		= "domWSid";
	String linkDominiSQL	= "//*[@id='registre']/tbody/tr/td[1]/a[text() = '"+codiDominiSQL+"']";
	String linkDominiWS		= "//*[@id='registre']/tbody/tr/td[1]/a[text() = '"+codiDominiWS+"']";
	String linkDominiSQL_m	= "//*[@id='registre']/tbody/tr/td[1]/a[text() = '"+codiDominiSQL+"_mod']";
	String linkDominiWS_m	= "//*[@id='registre']/tbody/tr/td[1]/a[text() = '"+codiDominiWS+"_mod']";
	//Enllaç per eliminar el primer domini de la llista
	String enllaçElimDomini = "//*[@id='registre']/tbody/tr/td/a[contains(@href, '/expedientTipus/dominiEsborrar.html')]";
	
	@Test
	public void a1_inicialitzacio() {
		carregarUrlConfiguracio();
		crearEntorn(entorn, titolEntorn);
		assignarPermisosEntorn(entorn, usuariAdmin, "DESIGN", "ORGANIZATION", "READ", "ADMINISTRATION");
		assignarPermisosEntorn(entorn, usuari, "DESIGN", "ORGANIZATION", "READ", "WRITE", "MANAGE", "ADMINISTRATION");
		marcarEntornDefecte(titolEntorn);
		seleccionarEntorn(titolEntorn);
		crearTipusExpedient(nomTipusExp, codTipusExp);
		assignarPermisosTipusExpedient(codTipusExp, usuari, "CREATE", "DESIGN", "MANAGE", "WRITE", "READ", "DELETE");
	}
	
	@Test
	public void b1_crear_domini_SQL() {
		
		carregarUrlDisseny();
		
		seleccionarEntorn(titolEntorn);
		
		seleccionarTipExp(codTipusExp);
		
		driver.findElement(By.xpath(pestanyaDominis)).click();
		
		driver.findElement(By.xpath(botoNouDomini)).click();
		
		emplenaDadesDominiSQL(codiDominiSQL, "Nom consulta SQL", "30", "Descripció consulta SQL", "jndi://datasource/", "Select * from clients;");
		
		driver.findElement(By.xpath(botoGuardaDomini)).click();
		
		existeixElementAssert("//*[@class='missatgesOk']", "No s'ha pogut crear el domini SQL "+codiDominiSQL+" per el tipus d´expedient "+codTipusExp+".");
	}
	
	@Test
	public void c1_crear_domini_WS() {
		
		carregarUrlDisseny();
		
		seleccionarEntorn(titolEntorn);
		
		seleccionarTipExp(codTipusExp);
		
		driver.findElement(By.xpath(pestanyaDominis)).click();
		
		driver.findElement(By.xpath(botoNouDomini)).click();
		
		emplenaDadesDominiWS(codiDominiWS, "Nom consulta WS", "35", "Descripció consulta WS", "http://webService/service/", "HTTP_BASIC", "PROPERTIES", "usuariWS", "passWS");
		
		driver.findElement(By.xpath(botoGuardaDomini)).click();
		
		existeixElementAssert("//*[@class='missatgesOk']", "No s'ha pogut crear el domini WS "+codiDominiWS+" per el tipus d´expedient "+codTipusExp+".");
	}
	
	@Test
	public void d1_comprobar_creacio_dominis() {
		
		carregarUrlDisseny();
		
		seleccionarEntorn(titolEntorn);
		
		seleccionarTipExp(codTipusExp);
		
		driver.findElement(By.xpath(pestanyaDominis)).click();
		
		driver.findElement(By.xpath(linkDominiSQL)).click();
		
		if (!codiDominiSQL.equals(driver.findElement(By.id("codi0")).getAttribute("value")))  { fail("El valor de codi del domini SQL per el tipus d´expedient "+codTipusExp+" no es l'esperat!"); }
		if (!"Nom consulta SQL".equals(driver.findElement(By.id("nom0")).getAttribute("value")))  { fail("El valor del nom del domini SQL per el tipus d´expedient "+codTipusExp+" no es l'esperat!"); }		
		if (!"CONSULTA_SQL".equals(driver.findElement(By.id("tipus0")).getAttribute("value")))  { fail("El valor del tipus del domini SQL per el tipus d´expedient "+codTipusExp+" no es l'esperat!"); }
		if (!"30".equals(driver.findElement(By.id("cacheSegons0")).getAttribute("value")))  { fail("El valor del temps en cache del domini SQL per el tipus d´expedient "+codTipusExp+" no es l'esperat!"); }
		if (!"Descripció consulta SQL".equals(driver.findElement(By.id("descripcio0")).getAttribute("value")))  { fail("El valor de la descripcio del domini SQL per el tipus d´expedient "+codTipusExp+" no es l'esperat!"); }
		
		if (!"jndi://datasource/".equals(driver.findElement(By.id("jndiDatasource0")).getAttribute("value")))  { fail("El valor del JNDI del datasource del domini SQL per el tipus d´expedient "+codTipusExp+" no es l'esperat!"); }
		if (!"Select * from clients;".equals(driver.findElement(By.id("sql0")).getAttribute("value")))  { fail("El valor del SQL del domini SQL per el tipus d´expedient "+codTipusExp+" no es l'esperat!"); }
		
		//Tornam arrere a comprobar l´altre domini
		driver.findElement(By.xpath(botoCancelarDomini)).click();
		driver.findElement(By.xpath(linkDominiWS)).click();
		
		if (!codiDominiWS.equals(driver.findElement(By.id("codi0")).getAttribute("value")))  { fail("El valor de codi del domini WS per el tipus d´expedient "+codTipusExp+" no es l'esperat!"); }
		if (!"Nom consulta WS".equals(driver.findElement(By.id("nom0")).getAttribute("value")))  { fail("El valor del nom del domini WS per el tipus d´expedient "+codTipusExp+" no es l'esperat!"); }		
		if (!"CONSULTA_WS".equals(driver.findElement(By.id("tipus0")).getAttribute("value")))  { fail("El valor del tipus del domini WS per el tipus d´expedient "+codTipusExp+" no es l'esperat!"); }
		if (!"35".equals(driver.findElement(By.id("cacheSegons0")).getAttribute("value")))  { fail("El valor del temps en cache del domini WS per el tipus d´expedient "+codTipusExp+" no es l'esperat!"); }
		if (!"Descripció consulta WS".equals(driver.findElement(By.id("descripcio0")).getAttribute("value")))  { fail("El valor de la descripcio del domini WS per el tipus d´expedient "+codTipusExp+" no es l'esperat!"); }
		
		if (!"http://webService/service/".equals(driver.findElement(By.id("url0")).getAttribute("value")))  { fail("El valor de la URL de conexio del domini WS per el tipus d´expedient "+codTipusExp+" no es l'esperat!"); }
		if (!"HTTP_BASIC".equals(driver.findElement(By.id("tipusAuth0")).getAttribute("value")))  { fail("El valor del tipus autenticacio del domini WS per el tipus d´expedient "+codTipusExp+" no es l'esperat!"); }
		if (!"PROPERTIES".equals(driver.findElement(By.id("origenCredencials0")).getAttribute("value")))  { fail("El valor de origen de credencials del domini WS per el tipus d´expedient "+codTipusExp+" no es l'esperat!"); }
		if (!"usuariWS".equals(driver.findElement(By.id("usuari0")).getAttribute("value")))  { fail("El valor de usuari del domini WS per el tipus d´expedient "+codTipusExp+" no es l'esperat!"); }
		if (!"passWS".equals(driver.findElement(By.id("contrasenya0")).getAttribute("value")))  { fail("El valor de password del domini WS per el tipus d´expedient "+codTipusExp+" no es l'esperat!"); }
	}
	
	@Test
	public void e1_modificar_dominis() {
		
		carregarUrlDisseny();
		
		seleccionarEntorn(titolEntorn);
		
		seleccionarTipExp(codTipusExp);
		
		driver.findElement(By.xpath(pestanyaDominis)).click();
		
		driver.findElement(By.xpath(linkDominiSQL)).click();
		
		emplenaDadesDominiSQL(codiDominiSQL + "_mod", "Nom consulta SQL_mod", "35", "Descripció consulta SQL_mod", "jndi://datasource/_mod", "Select * from clients;_mod");
				
		driver.findElement(By.xpath(botoGuardaDomini)).click();
		
		existeixElementAssert("//*[@class='missatgesOk']", "Error al modificar el domini SQL "+codiDominiSQL+" per el tipus d´expedient "+codTipusExp+".");
		
		driver.findElement(By.xpath(linkDominiWS)).click();
		
		emplenaDadesDominiWS(codiDominiWS+"_mod", "Nom consulta WS_mod", "45", "Descripció consulta WS_mod", "http://webService/service/_mod", "USERNAMETOKEN", "ATRIBUTS", "usuariWS_mod", "passWS_mod");
				
		driver.findElement(By.xpath(botoGuardaDomini)).click();
		
		existeixElementAssert("//*[@class='missatgesOk']", "Error al modificar el domini WS "+codiDominiWS+" per el tipus d´expedient "+codTipusExp+".");
	}
	
	@Test
	public void f1_comprobar_modificacio_dominis() {
		
		carregarUrlDisseny();
		
		seleccionarEntorn(titolEntorn);
		
		seleccionarTipExp(codTipusExp);
		
		driver.findElement(By.xpath(pestanyaDominis)).click();
		
		driver.findElement(By.xpath(linkDominiSQL_m)).click();
		
		String codDominiSQLMod = codiDominiSQL + "_mod";
		if (!codDominiSQLMod.equals(driver.findElement(By.id("codi0")).getAttribute("value")))  { fail("El valor de codi del domini SQL per el tipus d´expedient "+codTipusExp+" no es l'esperat!"); }
		if (!"Nom consulta SQL_mod".equals(driver.findElement(By.id("nom0")).getAttribute("value")))  { fail("El valor del nom del domini SQL per el tipus d´expedient "+codTipusExp+" no es l'esperat!"); }		
		if (!"35".equals(driver.findElement(By.id("cacheSegons0")).getAttribute("value")))  { fail("El valor del temps en cache del domini SQL per el tipus d´expedient "+codTipusExp+" no es l'esperat!"); }
		if (!"Descripció consulta SQL_mod".equals(driver.findElement(By.id("descripcio0")).getAttribute("value")))  { fail("El valor de la descripcio del domini SQL per el tipus d´expedient "+codTipusExp+" no es l'esperat!"); }
		
		if (!"jndi://datasource/_mod".equals(driver.findElement(By.id("jndiDatasource0")).getAttribute("value")))  { fail("El valor del JNDI del datasource del domini SQL per el tipus d´expedient "+codTipusExp+" no es l'esperat!"); }
		if (!"Select * from clients;_mod".equals(driver.findElement(By.id("sql0")).getAttribute("value")))  { fail("El valor del SQL del domini SQL per el tipus d´expedient "+codTipusExp+" no es l'esperat!"); }
		
		//Tornam arrere a comprobar l´altre domini
		driver.findElement(By.xpath(botoCancelarDomini)).click();
		driver.findElement(By.xpath(linkDominiWS_m)).click();
		
		String codiDominiWSMod = codiDominiWS + "_mod";
		if (!codiDominiWSMod.equals(driver.findElement(By.id("codi0")).getAttribute("value")))  { fail("El valor de codi del domini WS per el tipus d´expedient "+codTipusExp+" no es l'esperat!"); }
		if (!"Nom consulta WS_mod".equals(driver.findElement(By.id("nom0")).getAttribute("value")))  { fail("El valor del nom del domini WS per el tipus d´expedient "+codTipusExp+" no es l'esperat!"); }		
		if (!"45".equals(driver.findElement(By.id("cacheSegons0")).getAttribute("value")))  { fail("El valor del temps en cache del domini WS per el tipus d´expedient "+codTipusExp+" no es l'esperat!"); }
		if (!"Descripció consulta WS_mod".equals(driver.findElement(By.id("descripcio0")).getAttribute("value")))  { fail("El valor de la descripcio del domini WS per el tipus d´expedient "+codTipusExp+" no es l'esperat!"); }
		
		if (!"http://webService/service/_mod".equals(driver.findElement(By.id("url0")).getAttribute("value")))  { fail("El valor de la URL de conexio del domini WS per el tipus d´expedient "+codTipusExp+" no es l'esperat!"); }
		if (!"USERNAMETOKEN".equals(driver.findElement(By.id("tipusAuth0")).getAttribute("value")))  { fail("El valor del tipus autenticacio del domini WS per el tipus d´expedient "+codTipusExp+" no es l'esperat!"); }
		if (!"ATRIBUTS".equals(driver.findElement(By.id("origenCredencials0")).getAttribute("value")))  { fail("El valor de origen de credencials del domini WS per el tipus d´expedient "+codTipusExp+" no es l'esperat!"); }
		if (!"usuariWS_mod".equals(driver.findElement(By.id("usuari0")).getAttribute("value")))  { fail("El valor de usuari del domini WS per el tipus d´expedient "+codTipusExp+" no es l'esperat!"); }
		if (!"passWS_mod".equals(driver.findElement(By.id("contrasenya0")).getAttribute("value")))  { fail("El valor de password del domini WS per el tipus d´expedient "+codTipusExp+" no es l'esperat!"); }
	}
	
	//public void g1_provar_dominis() { //TRASPASSAT A ENT 9 }
	
	//@Test
	public void h1_eliminar_dominis() {
		
		carregarUrlDisseny();
		
		seleccionarEntorn(titolEntorn);
		
		seleccionarTipExp(codTipusExp);
		
		driver.findElement(By.xpath(pestanyaDominis)).click();
		
		while (existeixElement(enllaçElimDomini)) {
			driver.findElement(By.xpath(enllaçElimDomini)).click();
			if (isAlertPresent()) {acceptarAlerta();}
			existeixElementAssert("//*[@class='missatgesOk']", "Error al esborrar un domini per el tipus d´expedient "+codTipusExp+".");
		}
	}
	
	//@Test
	public void z0_finalitzacio() {
		carregarUrlConfiguracio();
		eliminarTipusExpedient(codTipusExp);
		eliminarEntorn(entorn);
	}
}
