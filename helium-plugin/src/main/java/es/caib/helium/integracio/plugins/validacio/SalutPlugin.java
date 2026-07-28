package es.caib.helium.integracio.plugins.validacio;

import es.caib.comanda.model.server.monitoring.EstatSalut;
import es.caib.comanda.model.server.monitoring.IntegracioPeticions;
import io.micrometer.core.instrument.MeterRegistry;

public interface SalutPlugin {

	void init(MeterRegistry registry, String codiPlugin);

    boolean teConfiguracioEspecifica();

    EstatSalut getEstatPlugin();

    IntegracioPeticions getPeticionsPlugin();

}
