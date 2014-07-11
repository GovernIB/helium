package net.conselldemallorca.helium.test.suites;

import net.conselldemallorca.helium.test.massiva.ExecucioMassiva;
import net.conselldemallorca.helium.test.massiva.ExecucioMassivaDocuments;
import net.conselldemallorca.helium.test.massiva.ExecucioMassivaVariables;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	ExecucioMassiva.class,
	ExecucioMassivaDocuments.class,
	ExecucioMassivaVariables.class
})
public class ExecucioMassivaSuite {}
