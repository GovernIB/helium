package es.caib.helium.integracio.plugins.firma;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.caib.helium.client.integracio.firma.FirmaClientService;
import es.caib.helium.client.integracio.firma.model.FirmaPost;
import es.caib.helium.client.integracio.firma.model.FirmaResposta;
import es.caib.helium.integracio.plugins.SistemaExternException;

@Component
public class FirmaPluginImpl implements FirmaPlugin {

    @Autowired
    private FirmaClientService firmaClientService;

    @Override
    public FirmaResposta firmar(
    		String id,
			String nom,
			String motiu,
			byte[] contingut, 
			String mime,
			String tipusDocumental,
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
        firmaPost.setId(id);
        firmaPost.setNom(nom);
        firmaPost.setMotiu(motiu);
        firmaPost.setContingut(contingut);
        firmaPost.setMime(mime);
        firmaPost.setTipusDocumental(tipusDocumental);
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
