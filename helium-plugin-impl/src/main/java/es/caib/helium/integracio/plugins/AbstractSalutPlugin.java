package es.caib.helium.integracio.plugins;

import com.google.common.base.Strings;
import es.caib.comanda.model.server.monitoring.EstatSalut;
import es.caib.comanda.model.server.monitoring.EstatSalutEnum;
import es.caib.comanda.model.server.monitoring.IntegracioPeticions;
import es.caib.comanda.ms.salut.helper.EstatHelper;
import es.caib.helium.integracio.plugins.utils.CuaFifoBool;
import es.caib.helium.integracio.plugins.validacio.SalutPlugin;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import lombok.Setter;
import lombok.Synchronized;

import java.util.concurrent.TimeUnit;

public class AbstractSalutPlugin implements SalutPlugin {

    // Mètodes de SALUT
    // /////////////////////////////////////////////////////////////////////////////////////////////

    private MeterRegistry registry;
    private MeterRegistry localRegistry;
    private String codiPlugin;
    @Setter
    protected String urlPlugin;
    @Setter
    protected String codiEntitat;

    @Setter
    protected boolean configuracioEspecifica = false;
    private EstatSalutEnum darrerEstat = EstatSalutEnum.UNKNOWN;

    private Timer timerOkGlobal;
    private Counter counterErrorGlobal;
    private Timer timerOk;
    private Counter counterError;

    private CuaFifoBool cuaPeticions;

    public void init(MeterRegistry registry, String codiPlugin) {

        this.registry = registry;
        this.codiPlugin = codiPlugin;
        var timerBuilder = Timer.builder("plugin." + codiPlugin)
                .tag("result", "success")
                .publishPercentiles(0.5, 0.75, 0.95, 0.99)
                .publishPercentileHistogram();
        if (configuracioEspecifica && !Strings.isNullOrEmpty(codiEntitat)) {
            timerBuilder.tag("entitat", codiEntitat);
        }
        timerOkGlobal = timerBuilder.register(registry);
        counterErrorGlobal = Counter.builder("plugin." + codiPlugin).register(registry);
        cuaPeticions = new CuaFifoBool(20);
        initializeLocalTimers();
    }

    private void initializeLocalTimers() {

        if (localRegistry != null) {
            localRegistry.close(); // descarta mètriques existents
        }
        localRegistry = new SimpleMeterRegistry();
        timerOk = Timer.builder("plugin.peticions.ok")
                .tags("result", "success")
                .publishPercentiles(0.5, 0.75, 0.95, 0.99)
                .publishPercentileHistogram()
                .register(localRegistry);
        counterError = Counter.builder("plugin.peticions.error").register(localRegistry);
    }

    @Synchronized
    public void incrementarOperacioOk(Long durada) {

        timerOkGlobal.record(durada, TimeUnit.MILLISECONDS);
        timerOk.record(durada, TimeUnit.MILLISECONDS);
        cuaPeticions.add(true);
    }

    @Synchronized
    public void incrementarOperacioError() {

        counterErrorGlobal.increment();
        counterError.increment();
        cuaPeticions.add(false);
    }

    @Synchronized
    private void resetComptadors() {
        initializeLocalTimers();
    }

    @Override
    public boolean teConfiguracioEspecifica() {
        return this.configuracioEspecifica;
    }

    @Override
    public EstatSalut getEstatPlugin() {

        Integer duradaMitja = null;
        Long totalPeticionsOk = null;
        Long totalPeticionsError = null;

        if (timerOk != null) {
            duradaMitja = (int) timerOk.mean(TimeUnit.MILLISECONDS);
            totalPeticionsOk = timerOk.count();
        }
        if (counterError != null) {
            totalPeticionsError = (long) counterError.count();
        }
        final EstatSalutEnum estatCalculat = calculaEstat(totalPeticionsOk, totalPeticionsError);
        darrerEstat = estatCalculat;

        return new EstatSalut()
                .latencia(duradaMitja)
                .estat(estatCalculat);
    }

    private EstatSalutEnum calculaEstat(Long totalPeticionsOk, Long totalPeticionsError) {
        var totalPeticions = totalPeticionsOk + totalPeticionsError;
        if (totalPeticions == 0L) {
            return darrerEstat;
        }
        long peticionsOkSegures;
        long peticionsErrorSegures;
        if (totalPeticions >= 20) {
            peticionsOkSegures = totalPeticionsOk;
            peticionsErrorSegures = totalPeticionsError;
        } else {
            peticionsOkSegures = !cuaPeticions.isEmpty() ? cuaPeticions.getOk() : 0L;
            peticionsErrorSegures = !cuaPeticions.isEmpty() ? cuaPeticions.getError() : 0L;
        }
        final long totalOperacions = peticionsOkSegures + peticionsErrorSegures;
        // Percentatge d'errors arrodonit correctament evitant divisió d'enters
        final int errorRatePct = (int) Math.round((peticionsErrorSegures * 100.0) / totalOperacions);
        return EstatHelper.calculaEstat(errorRatePct);
    }

    @Override
    public IntegracioPeticions getPeticionsPlugin() {

        final int tempsMigGlobal = timerOkGlobal != null ? (int) timerOkGlobal.mean(TimeUnit.MILLISECONDS) : 0;
        Long peticionsOkGlobal = timerOkGlobal != null ? timerOkGlobal.count() : null;
        Long peticionsErrorGlobal = counterErrorGlobal != null ? (long) counterErrorGlobal.count() : null;

        final int tempsMigPeriode = timerOk != null ? (int) timerOk.mean(TimeUnit.MILLISECONDS) : 0;
        Long peticionsOkUltimPeriode = timerOk != null ? timerOk.count() : null;
        Long peticionsErrorUltimPeriode = counterError != null ? (long) counterError.count() : null;

        var integracioPeticions = new IntegracioPeticions()
                .totalOk(peticionsOkGlobal)
                .totalError(peticionsErrorGlobal)
                .totalTempsMig(tempsMigGlobal)
                .peticionsOkUltimPeriode(peticionsOkUltimPeriode)
                .peticionsErrorUltimPeriode(peticionsErrorUltimPeriode)
                .tempsMigUltimPeriode(tempsMigPeriode)
//                .peticionsPerEntorn()
                .endpoint(urlPlugin);
        resetComptadors();
        return integracioPeticions;
    }

}
