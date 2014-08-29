package net.conselldemallorca.helium.test.disseny;

import java.util.Calendar;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import net.conselldemallorca.helium.test.util.BaseTest;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TipusExpedientInformacio extends BaseTest {

	String entorn 		= carregarPropietat("tipexp.entorn.nom", "Nom de l'entorn de proves no configurat al fitxer de properties");
	String titolEntorn	= carregarPropietat("tipexp.entorn.titol", "Titol de l'entorn de proves no configurat al fitxer de properties");
	
	String usuari 		= carregarPropietat("test.base.usuari.disseny", "Usuari feina de l'entorn de proves no configurat al fitxer de properties");
	String usuariAdmin  = carregarPropietat("test.base.usuari.configuracio", "Usuari configuració de l'entorn de proves no configurat al fitxer de properties");
	
	String nomTipusExp	= carregarPropietat("tipexp.deploy.info.tipus.expedient.nom", "Nom del tipus d'expedient de proves no configurat al fitxer de properties");
	String codTipusExp	= carregarPropietat("tipexp.deploy.info.tipus.expedient.codi", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	String pathExport	= carregarPropietatPath("tipexp.deploy.arxiu.path", "Ruta de l´arxiu del tipus d´expedient exportat no configurat al fitxer de properties");
	
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
		assignarPermisosEntorn(entorn, usuari, "DESIGN", "ORGANIZATION", "READ");
		marcarEntornDefecte(titolEntorn);
		seleccionarEntorn(titolEntorn);
		crearTipusExpedient(nomTipusExp, codTipusExp);
	}
	
	@Test
	public void b1_crear_complet() {
		carregarUrlDisseny();
		seleccionarEntorn(titolEntorn);
		seleccionarTipExp(codTipusExp);
		modificarTipusExpedientComplet(codTipusExp, nomTipusExp, expressioSeq, Integer.toString(anyActual), Integer.toString(seqTipExp), Integer.toString(anyActual_2), Integer.toString(seqTipExp_2), responsableTipusExp);
		seleccionarTipExp(codTipusExp);
		comprobarTipusExpedientComplet(codTipusExp, nomTipusExp, expressioSeq, Integer.toString(anyActual), Integer.toString(seqTipExp), Integer.toString(anyActual_2), Integer.toString(seqTipExp_2), responsableTipusExp);
	}
	
	@Test
	public void c1_exportar_versio() {
		carregarUrlDisseny();
		seleccionarEntorn(titolEntorn);
		seleccionarTipExp(codTipusExp);
		postDownloadFile("//*[@id='content']/div/form[contains(@action, '/expedientTipus/exportar.html')]");
	}
	
	@Test
	public void d1_importar_dades_versio() {
		carregarUrlDisseny();
		seleccionarEntorn(titolEntorn);
		importarDadesTipExp(codTipusExp, pathExport);
	}
	
	@Test
	public void z0_finalitzacio() {
		carregarUrlConfiguracio();
		eliminarExpedient(null, null, nomTipusExp);
		eliminarTotsEstatsTipusExpedient(codTipusExp);
		eliminarTipusExpedient(codTipusExp);
		//eliminarDefinicioProces(defProcNom);
		eliminarEntorn(entorn);
	}
}