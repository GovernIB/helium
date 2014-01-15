package net.conselldemallorca.helium.test;

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
