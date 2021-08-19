package es.caib.helium.integracio.plugins.arxiu;

import es.caib.helium.client.integracio.arxiu.ArxiuClientService;
import es.caib.helium.client.integracio.arxiu.model.ExpedientArxiu;
import es.caib.helium.client.integracio.arxiu.model.Expedient;
import org.springframework.beans.factory.annotation.Autowired;

public class ArxiuPluginImpl implements ArxiuPlugin {

    @Autowired
    private ArxiuClientService arxiuClientService;

    @Override
    public Expedient expedientDetalls(String uuId, Long entornId) {
        var exp = arxiuClientService.getExpedientsByUuId(uuId, entornId);
        var expedient = new Expedient();
        expedient.setContinguts(exp.getContinguts());

        return expedient;
    }

    @Override
    public void expedientCrear(ExpedientArxiu expedient, Long entornId) {
        arxiuClientService.postExpedient(expedient, entornId);
    }
}
