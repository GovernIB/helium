package net.conselldemallorca.helium.test.suites;

import net.conselldemallorca.helium.test.disseny.DefinicioProces;
import net.conselldemallorca.helium.test.disseny.DefinicioProcesAccions;
import net.conselldemallorca.helium.test.disseny.DefinicioProcesAgrupacions;
import net.conselldemallorca.helium.test.disseny.DefinicioProcesDocs;
import net.conselldemallorca.helium.test.disseny.DefinicioProcesTasques;
import net.conselldemallorca.helium.test.disseny.DefinicioProcesTerminis;
import net.conselldemallorca.helium.test.disseny.DefinicioProcesVars;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	DefinicioProces.class,
	DefinicioProcesVars.class,
	DefinicioProcesDocs.class,
	DefinicioProcesTasques.class,
	DefinicioProcesAgrupacions.class,
	DefinicioProcesTerminis.class,
	DefinicioProcesAccions.class
})
public class ConfiguracioDefinicioProcesSuite {}
