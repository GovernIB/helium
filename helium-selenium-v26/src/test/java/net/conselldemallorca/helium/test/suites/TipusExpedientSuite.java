package net.conselldemallorca.helium.test.suites;

import net.conselldemallorca.helium.test.disseny.TipusExpedient;
import net.conselldemallorca.helium.test.disseny.TipusExpedientConsultes;
import net.conselldemallorca.helium.test.disseny.TipusExpedientDefProc;
import net.conselldemallorca.helium.test.disseny.TipusExpedientDocuments;
import net.conselldemallorca.helium.test.disseny.TipusExpedientDominis;
import net.conselldemallorca.helium.test.disseny.TipusExpedientEnumeracions;
import net.conselldemallorca.helium.test.disseny.TipusExpedientEstats;
import net.conselldemallorca.helium.test.disseny.TipusExpedientForms;
import net.conselldemallorca.helium.test.disseny.TipusExpedientInformacio;
import net.conselldemallorca.helium.test.disseny.TipusExpedientModificarEliminar;
import net.conselldemallorca.helium.test.disseny.TipusExpedientPermisos;
import net.conselldemallorca.helium.test.disseny.TipusExpedientRedireccio;
import net.conselldemallorca.helium.test.disseny.TipusExpedientTramits;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	TipusExpedient.class,
	TipusExpedientPermisos.class,
	TipusExpedientInformacio.class,
	TipusExpedientEstats.class,
	TipusExpedientDefProc.class,
	TipusExpedientEnumeracions.class,
	TipusExpedientDominis.class,
	TipusExpedientDocuments.class,
	TipusExpedientConsultes.class,
	TipusExpedientRedireccio.class,
	TipusExpedientTramits.class,
	TipusExpedientForms.class,
	TipusExpedientModificarEliminar.class
})
public class TipusExpedientSuite {}