package net.conselldemallorca.helium.test.disseny;

import net.conselldemallorca.helium.test.util.BaseTest;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TipusExpedientPermisos extends BaseTest {

	String entorn 		= carregarPropietat("tipexp.entorn.nom", "Nom de l'entorn de proves no configurat al fitxer de properties");
	String titolEntorn	= carregarPropietat("tipexp.entorn.titol", "Titol de l'entorn de proves no configurat al fitxer de properties");
	
	String rol 		= carregarPropietat("test.base.rol.proves", "Usuari feina de l'entorn de proves no configurat al fitxer de properties");
	
	String usuari 		= carregarPropietat("test.base.usuari.disseny", "Usuari feina de l'entorn de proves no configurat al fitxer de properties");
	String usuariAdmin  = carregarPropietat("test.base.usuari.configuracio", "Usuari configuració de l'entorn de proves no configurat al fitxer de properties");
	
	String nomTipusExp	= carregarPropietat("tipexp.deploy.tipus.expedient.nom", "Nom del tipus d'expedient de proves no configurat al fitxer de properties");
	String codTipusExp	= carregarPropietat("tipexp.deploy.tipus.expedient.codi", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	String pathExport	= carregarPropietatPath("tipexp.deploy.arxiu.path", "Ruta de l´arxiu del tipus d´expedient exportat no configurat al fitxer de properties");
	
	String defProcNom	= carregarPropietat("informe.deploy.definicio.proces.nom", "Codi de la definicio de proces de proves no configurat al fitxer de properties");
	String defProcPath	= carregarPropietatPath("informe.deploy.definicio.proces.path", "Ruta de l´arxiu de definicio de proces exportada no configurat al fitxer de properties");
	
	//XPATHS
	String botoNouTipusExpedient = "//*[@id='content']/form/button";
	
	@Test
	public void a1_inicialitzacio() {
		carregarUrlConfiguracio();
		crearEntorn(entorn, titolEntorn);
		assignarPermisosEntorn(entorn, usuariAdmin, "DESIGN", "ORGANIZATION", "READ", "ADMINISTRATION");
		assignarPermisosEntorn(entorn, usuari, "DESIGN", "ORGANIZATION", "READ");
		marcarEntornDefecte(titolEntorn);
		seleccionarEntorn(titolEntorn);
		crearRol(rol, "Descripcio Rol proves");
	}
	
	@Test
	public void b1_crear_basic() {
		carregarUrlConfiguracio();
		seleccionarEntorn(titolEntorn);
		crearTipusExpedient(nomTipusExp, codTipusExp);
	}
	
	@Test
	public void c1_assignar_permisos_usuari() {
		carregarUrlConfiguracio();
		seleccionarEntorn(titolEntorn);
		assignarPermisosTipusExpedient(codTipusExp, usuari, false, "DESIGN", "MANAGE", "CREATE", "DELETE", "SUPERVISION", "READ", "WRITE", "REASSIGNMENT", "ADMINISTRATION");
	}

	@Test
	public void d1_assignar_permisos_rol() {
		carregarUrlConfiguracio();
		seleccionarEntorn(titolEntorn);
		assignarPermisosTipusExpedient(codTipusExp, rol, true, "DESIGN", "MANAGE", "CREATE", "DELETE", "SUPERVISION", "READ", "WRITE", "REASSIGNMENT", "ADMINISTRATION");
	}	
	
	@Test
	public void e1_desassignar_permisos_usuari() {
		carregarUrlConfiguracio();
		seleccionarEntorn(titolEntorn);
		desasignarPermisosTipusExpedient(codTipusExp, usuari);
	}

	@Test
	public void f1_desassignar_permisos_rol() {
		carregarUrlConfiguracio();
		seleccionarEntorn(titolEntorn);
		desasignarPermisosTipusExpedient(codTipusExp, rol);
	}
	
	@Test
	public void z0_finalitzacio() {
		carregarUrlConfiguracio();
		eliminarTipusExpedient(codTipusExp);
		eliminarRol(rol);
		eliminarEntorn(entorn);
	}
}