package net.conselldemallorca.helium.test.suites;

import net.conselldemallorca.helium.test.tramitacio.Accions;
import net.conselldemallorca.helium.test.tramitacio.AfegirExpedientRelacionat;
import net.conselldemallorca.helium.test.tramitacio.DadesExpedient;
import net.conselldemallorca.helium.test.tramitacio.ExpedientPestanyaTasques;
import net.conselldemallorca.helium.test.tramitacio.ModificarExpedient;
import net.conselldemallorca.helium.test.tramitacio.ModificarVersioProces;
import net.conselldemallorca.helium.test.tramitacio.NouExpedient;
import net.conselldemallorca.helium.test.tramitacio.RegistreExpedient;
import net.conselldemallorca.helium.test.tramitacio.TasquesDadesDocumentsTasca;
import net.conselldemallorca.helium.test.tramitacio.TasquesExpedient;
import net.conselldemallorca.helium.test.tramitacio.TasquesFlux;
import net.conselldemallorca.helium.test.tramitacio.TasquesSubprocessExpedient;
import net.conselldemallorca.helium.test.tramitacio.TerminisExpedient;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	Accions.class,
	AfegirExpedientRelacionat.class,
	DadesExpedient.class, // Falla al modificar una fecha en una agrupación
	ExpedientPestanyaTasques.class,
	ModificarExpedient.class,
	ModificarVersioProces.class,
	NouExpedient.class,
	RegistreExpedient.class,
	TasquesDadesDocumentsTasca.class,
	TasquesExpedient.class,
	TasquesFlux.class,
	TasquesSubprocessExpedient.class,
	TerminisExpedient.class
})
public class TramitacioSuite {}
