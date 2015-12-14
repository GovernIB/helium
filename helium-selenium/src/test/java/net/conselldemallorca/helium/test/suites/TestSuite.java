package net.conselldemallorca.helium.test.suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	TipusExpedientSuite.class,
	ConfiguracioAplicacioSuite.class,
	ConfiguracioDefinicioProcesSuite.class,
	ConfiguracioEntornSuite.class,
	ConsultaExpedientSuite.class,
	InformesSuite.class,
	ExecucioMassivaSuite.class,
	TramitacioSuite.class,
	IntegracioSuite.class
})
public class TestSuite {}
