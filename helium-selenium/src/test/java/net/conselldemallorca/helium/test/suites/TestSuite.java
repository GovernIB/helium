package net.conselldemallorca.helium.test.suites;

import net.conselldemallorca.helium.test.DefinicioProcesTests;
import net.conselldemallorca.helium.test.EntornTests;
import net.conselldemallorca.helium.test.ExpedientTests;
import net.conselldemallorca.helium.test.TipusExpedientTests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	EntornTests.class,
	DefinicioProcesTests.class,
	TipusExpedientTests.class,
	ExpedientTests.class
})
public class TestSuite {}
