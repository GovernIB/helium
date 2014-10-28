package net.conselldemallorca.helium.test.suites;

import net.conselldemallorca.helium.test.integracio.Custodia;
import net.conselldemallorca.helium.test.integracio.FormularisExterns;
import net.conselldemallorca.helium.test.integracio.Portasignatures;
import net.conselldemallorca.helium.test.integracio.Registre;
import net.conselldemallorca.helium.test.integracio.Signature;
import net.conselldemallorca.helium.test.integracio.Sistra;
import net.conselldemallorca.helium.test.integracio.Tramitacion;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	Custodia.class,
	Portasignatures.class,
	Registre.class,
	//Signature.class,
	Tramitacion.class,
	FormularisExterns.class,
	Sistra.class
})
public class IntegracioSuite {}
