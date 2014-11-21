package net.conselldemallorca.helium.test.suites;

import net.conselldemallorca.helium.test.configuracio.ConfiguracioFestius;
import net.conselldemallorca.helium.test.configuracio.ConfiguracioPersones;
import net.conselldemallorca.helium.test.configuracio.ConfiguracioReassignacions;
import net.conselldemallorca.helium.test.configuracio.ConfiguracioRols;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	//ConfiguracioPersones.class, //Si es va per seycon, aquesta prova no aplica
	//ConfiguracioRols.class, //Si es va per seycon, aquesta prova no aplica
	ConfiguracioFestius.class,
	ConfiguracioReassignacions.class
})
public class ConfiguracioAplicacioSuite {}
