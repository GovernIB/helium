package es.caib.helium.integracio.plugins.signatura;

import es.caib.helium.client.integracio.validacio.ValidacioClientService;
import es.caib.helium.client.integracio.validacio.model.VerificacioFirma;
import es.caib.helium.client.model.RespostaValidacioSignatura;
import org.springframework.beans.factory.annotation.Autowired;

public class SignaturaPluginImpl implements SignaturaPlugin {

    @Autowired
    private ValidacioClientService validacioClientService;

    @Override
    public RespostaValidacioSignatura verificarSignatura(
            byte[] document,
            byte[] signatura,
            boolean obtenirDadesCertificat) throws SignaturaPluginException {

        var verificacioFirma = new VerificacioFirma();
        verificacioFirma.setDocumentContingut(document);
        verificacioFirma.setFirmaContingut(signatura);
        verificacioFirma.setObtenirDadesCertificat(obtenirDadesCertificat);
        return validacioClientService.verificacio(verificacioFirma);
    }
}
