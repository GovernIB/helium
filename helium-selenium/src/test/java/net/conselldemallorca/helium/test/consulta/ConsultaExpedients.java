package net.conselldemallorca.helium.test.consulta;

import static org.junit.Assert.fail;
import static org.junit.Assert.assertTrue;
import net.conselldemallorca.helium.test.util.BaseTest;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ConsultaExpedients extends BaseTest {

	String entorn = carregarPropietat("consulta.entorn.nom", "Nom de l'entorn de proves no configurat al fitxer de properties");
	String titolEntorn = carregarPropietat("consulta.entorn.titol", "Titol de l'entorn de proves no configurat al fitxer de properties");
	String usuariAdmin = carregarPropietat("test.base.usuari.configuracio", "Usuari configuració de l'entorn de proves no configurat al fitxer de properties");
	String usuari = carregarPropietat("test.base.usuari.feina", "Usuari feina de l'entorn de proves no configurat al fitxer de properties");
	String pathDefProc = carregarPropietatPath("consulta.deploy.definicio.proces.path", "Path de la definició de procés de proves no configurat al fitxer de properties");
	String nomDefProc = carregarPropietat("consulta.deploy.definicio.proces.nom", "Nom de la definició de procés de proves no configurat al fitxer de properties");
//	String nomTipusExp1 = carregarPropietat("consulta.deploy.tipus.expedient.nom.1", "Nom del tipus d'expedient de proves 1 no configurat al fitxer de properties");
	String codTipusExp1 = carregarPropietat("consulta.deploy.tipus.expedient.codi.1", "Codi del tipus d'expedient de proves 1 no configurat al fitxer de properties");
//	String pathTipusExp1 = carregarPropietatPath("consulta.deploy.tipus.expedient.path.1", "Path del tipus d'expedient de proves 1 no configurat al fitxer de properties");
//	String nomTipusExp2 = carregarPropietat("consulta.deploy.tipus.expedient.nom.2", "Nom del tipus d'expedient de proves 2 no configurat al fitxer de properties");
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
		
		assignarPermisosTipusExpedient(codTipusExp1, usuariAdmin, "DESIGN","CREATE","SUPERVISION","WRITE","MANAGE","DELETE","READ","ADMINISTRATION");
		assignarPermisosTipusExpedient(codTipusExp1, usuari, "CREATE","WRITE","DELETE","READ");
		assignarPermisosTipusExpedient(codTipusExp2, usuariAdmin, "DESIGN","CREATE","SUPERVISION","WRITE","MANAGE","DELETE","READ","ADMINISTRATION");
		assignarPermisosTipusExpedient(codTipusExp2, usuari, "CREATE","WRITE","DELETE","READ");
		
		// Iniciam 12 expedients de cada tipus
		iniciarExpediente(null, codTipusExp1, null, null);
		iniciarExpediente(null, codTipusExp1, null, null);
		iniciarExpediente(null, codTipusExp1, null, null);
		iniciarExpediente(null, codTipusExp1, null, null);
		iniciarExpediente(null, codTipusExp1, null, null);
		iniciarExpediente(null, codTipusExp1, null, null);
		iniciarExpediente(null, codTipusExp1, null, null);
		iniciarExpediente(null, codTipusExp1, null, null);
		iniciarExpediente(null, codTipusExp1, null, null);
		iniciarExpediente(null, codTipusExp1, null, null);
		iniciarExpediente(null, codTipusExp1, null, null);
		iniciarExpediente(null, codTipusExp1, null, null);
		iniciarExpediente(null, codTipusExp2, null, null);
		iniciarExpediente(null, codTipusExp2, null, null);
		iniciarExpediente(null, codTipusExp2, null, null);
		iniciarExpediente(null, codTipusExp2, null, null);
		iniciarExpediente(null, codTipusExp2, null, null);
		iniciarExpediente(null, codTipusExp2, null, null);
		iniciarExpediente(null, codTipusExp2, null, null);
		iniciarExpediente(null, codTipusExp2, null, null);
		iniciarExpediente(null, codTipusExp2, null, null);
		iniciarExpediente(null, codTipusExp2, null, null);
		iniciarExpediente(null, codTipusExp2, null, null);
		iniciarExpediente(null, codTipusExp2, null, null);
		
		// Tramitam expedients
		
		
	}
}
