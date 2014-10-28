package net.conselldemallorca.helium.test.suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	ExecucioMassivaSuite.class,
	TipusExpedientSuite.class,
	TramitacioSuite.class,
	ConfiguracioAplicacioSuite.class,
	ConfiguracioDefinicioProcesSuite.class,
	ConfiguracioEntornSuite.class,
	ConsultaExpedientSuite.class,
	InformesSuite.class,
	IntegracioSuite.class
})
public class TestSuite {}
