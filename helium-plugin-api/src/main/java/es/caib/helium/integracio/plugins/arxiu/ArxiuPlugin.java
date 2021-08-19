package es.caib.helium.integracio.plugins.arxiu;

import es.caib.helium.client.integracio.arxiu.model.ExpedientArxiu;
import es.caib.helium.client.integracio.arxiu.model.Expedient;
import org.springframework.stereotype.Component;

@Component
public interface ArxiuPlugin {

    Expedient expedientDetalls(String uuId, Long entornId);

    void expedientCrear(ExpedientArxiu expedient, Long entornId);

}
