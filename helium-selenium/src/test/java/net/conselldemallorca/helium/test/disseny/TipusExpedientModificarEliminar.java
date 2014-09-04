package net.conselldemallorca.helium.test.disseny;

import java.util.Calendar;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import net.conselldemallorca.helium.test.util.BaseTest;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TipusExpedientModificarEliminar extends BaseTest {

											//TEX.13 - Modificar Tipus d´Expedient
											//TEX.14 - Eliminar Tipus d´Expedient
	
	String entorn 		= carregarPropietat("tipexp.mod-del.entorn.nom", "Nom de l'entorn de proves no configurat al fitxer de properties");
	String titolEntorn	= carregarPropietat("tipexp.mod-del.entorn.titol", "Titol de l'entorn de proves no configurat al fitxer de properties");
	
	String usuari 		= carregarPropietat("test.base.usuari.disseny", "Usuari feina de l'entorn de proves no configurat al fitxer de properties");
	String usuariAdmin  = carregarPropietat("test.base.usuari.configuracio", "Usuari configuració de l'entorn de proves no configurat al fitxer de properties");

	String codTipusExp	= carregarPropietat("tipexp.deploy.info.tipus.expedient.codi", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	String nomTipusExp	= carregarPropietat("tipexp.deploy.info.tipus.expedient.nom", "Nom del tipus d'expedient de proves no configurat al fitxer de properties");

	String expressioSeq = "CZ ${seq}/${any}";
	
	int anyActual = Calendar.getInstance().get(Calendar.YEAR);
	int seqTipExp = 10;
	
	int anyActual_2 = anyActual-1;
	int seqTipExp_2 = 20;
	
	String responsableTipusExp = carregarPropietat("test.base.usuari.disseny.nom",  "Nom del responsable del tipus de expedient no configurat al fitxer de properties");
	
	
	@Test
	public void a1_inicialitzacio() {
		carregarUrlConfiguracio();
		crearEntorn(entorn, titolEntorn);
		assignarPermisosEntorn(entorn, usuariAdmin, "DESIGN", "ORGANIZATION", "READ", "ADMINISTRATION");
		assignarPermisosEntorn(entorn, usuari, "DESIGN", "ORGANIZATION", "READ", "ADMINISTRATION");
		marcarEntornDefecte(titolEntorn);
		seleccionarEntorn(titolEntorn);
	}

	@Test
	public void b1_crear_basic() {
		carregarUrlDisseny();
		seleccionarEntorn(titolEntorn);
		crearTipusExpedient(nomTipusExp, codTipusExp);
		assignarPermisosTipusExpedient(codTipusExp, usuari, "CREATE", "DESIGN", "MANAGE", "READ", "DELETE");
	}
	
	@Test
	public void c1_modificar_expedient() {
		carregarUrlDisseny();
		seleccionarEntorn(titolEntorn);
		seleccionarTipExp(codTipusExp);
		
		screenshotHelper.saveScreenshot("tipusExpedient/modificar-eliminar/c1_1_modificar_tipus_expedient-pipella_dades_generals.png");
		
		modificarTipusExpedientComplet(codTipusExp, nomTipusExp, expressioSeq, Integer.toString(anyActual), Integer.toString(seqTipExp), Integer.toString(anyActual_2), Integer.toString(seqTipExp_2), responsableTipusExp, "tipusExpedient/modificar-eliminar/c1_2_modificar_tipus_expedient-");
		
		seleccionarTipExp(codTipusExp);
				
		comprobarTipusExpedientComplet(codTipusExp, nomTipusExp, expressioSeq, Integer.toString(anyActual), Integer.toString(seqTipExp), Integer.toString(anyActual_2), Integer.toString(seqTipExp_2), responsableTipusExp);
		
		screenshotHelper.saveScreenshot("tipusExpedient/modificar-eliminar/c1_3_modificar_tipus_expedient-comprobar_modficacions.png");
	}
	
	@Test
	public void d1_eliminar_expedient() {
		carregarUrlDisseny();
		seleccionarEntorn(titolEntorn);
		eliminarTipusExpedient(codTipusExp);
		
		screenshotHelper.saveScreenshot("tipusExpedient/modificar-eliminar/d1_1_eliminar_tipus_expedient-expedient_eliminat.png");
	}
	
	@Test
	public void z0_finalitzar() {
		carregarUrlConfiguracio();
		seleccionarEntorn(titolEntorn);
		eliminarEntorn(entorn);
	}

}