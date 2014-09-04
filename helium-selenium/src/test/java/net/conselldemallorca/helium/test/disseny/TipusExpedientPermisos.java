package net.conselldemallorca.helium.test.disseny;

import net.conselldemallorca.helium.test.util.BaseTest;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TipusExpedientPermisos extends BaseTest {

								//TEX.2 - Permisos
									//TEX.2.1 - Asignar permisos a un usuari
									//TEX.2.2 - Asignar permisos a un rol
									//TEX.2.3 - Desassignar permisos a un usuari
									//TEX.2.4 - Desasignar permisos a un rol	
									//TEX.2.5 - Provar permisos ** Es farà una classe a parte de proves de permisos **	
	
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
		crearTipusExpedient(nomTipusExp, codTipusExp, "tipusExpedient/permisos/b1_");
	}
	
	@Test
	public void c1_assignar_permisos_usuari() {
		carregarUrlConfiguracio();
		seleccionarEntorn(titolEntorn);
		assignarPermisosTipusExpedient(codTipusExp, usuari, "tipusExpedient/permisos/c1_asginar_usuari", false, "DESIGN", "MANAGE", "CREATE", "DELETE", "SUPERVISION", "READ", "WRITE", "REASSIGNMENT", "ADMINISTRATION");
	}

	@Test
	public void d1_assignar_permisos_rol() {
		carregarUrlConfiguracio();
		seleccionarEntorn(titolEntorn);
		assignarPermisosTipusExpedient(codTipusExp, rol, "tipusExpedient/permisos/d1_asginar_rol", true, "DESIGN", "MANAGE", "CREATE", "DELETE", "SUPERVISION", "READ", "WRITE", "REASSIGNMENT", "ADMINISTRATION");
	}	
	
	@Test
	public void e1_desassignar_permisos_usuari() {
		carregarUrlConfiguracio();
		seleccionarEntorn(titolEntorn);
		desasignarPermisosTipusExpedient(codTipusExp, usuari, "tipusExpedient/permisos/e1_desasignar_usuari");
	}

	@Test
	public void f1_desassignar_permisos_rol() {
		carregarUrlConfiguracio();
		seleccionarEntorn(titolEntorn);
		desasignarPermisosTipusExpedient(codTipusExp, rol, "tipusExpedient/permisos/e2_desasignar_rol");
	}
	
	@Test
	public void z0_finalitzacio() {
		carregarUrlConfiguracio();
		eliminarTipusExpedient(codTipusExp);
		eliminarRol(rol);
		eliminarEntorn(entorn);
	}
}