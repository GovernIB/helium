package net.conselldemallorca.helium.suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import net.conselldemallorca.helium.test.integracio.FormularisExterns;
import net.conselldemallorca.helium.test.integracio.PortasignaturesCustodia;
import net.conselldemallorca.helium.test.integracio.Registre;
import net.conselldemallorca.helium.test.integracio.TramitacioExterna;
/** 
 * Suite d'execució dels tests d'integració amb serveis externs a Helium.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
	FormularisExterns.class,
	Registre.class,
	TramitacioExterna.class,
	PortasignaturesCustodia.class
})
public class IntegracioSuite {}
