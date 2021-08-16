package es.caib.helium.integracio.plugins.firma;

import es.caib.helium.client.integracio.firma.FirmaClientService;
import es.caib.helium.client.integracio.firma.model.FirmaPost;
import es.caib.helium.integracio.plugins.SistemaExternException;
import org.springframework.beans.factory.annotation.Autowired;
import es.caib.helium.client.integracio.firma.enums.FirmaTipus;
import org.springframework.stereotype.Component;

@Component
public class FirmaPluginImpl implements FirmaPlugin {

    @Autowired
    private FirmaClientService firmaClientService;

    @Override
    public byte[] firmar(
            FirmaTipus firmaTipus,
            String motiu,
            String arxiuNom,
            byte[] arxiuContingut,
            Long tamany,
            Long entornId,
            String expedientIdentificador,
            String expedientNumero,
            Long expedientTipusId,
            String expedientTipusCodi,
            String expedientTipusNom,
            String codiDocument
    )throws SistemaExternException {

        var firmaPost = new FirmaPost();
        firmaPost.setFirmaTipus(firmaTipus);
        firmaPost.setMotiu(motiu);
        firmaPost.setArxiuNom(arxiuNom);
        firmaPost.setArxiuContingut(arxiuContingut);
        firmaPost.setTamany(tamany);
        firmaPost.setEntornId(entornId);
        firmaPost.setExpedientIdentificador(expedientIdentificador);
        firmaPost.setExpedientNumero(expedientNumero);
        firmaPost.setExpedientTipusId(expedientTipusId);
        firmaPost.setExpedientTipusCodi(expedientTipusCodi);
        firmaPost.setExpedientTipusNom(expedientTipusNom);
        firmaPost.setCodiDocument(codiDocument);
        return firmaClientService.firmar(firmaPost, entornId);
    }
}
