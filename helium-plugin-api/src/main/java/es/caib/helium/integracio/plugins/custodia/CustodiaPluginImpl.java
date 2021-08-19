package es.caib.helium.integracio.plugins.custodia;

import es.caib.helium.client.integracio.custodia.CustodiaClientService;
import es.caib.helium.client.integracio.custodia.model.CustodiaRequest;
import es.caib.helium.client.model.RespostaValidacioSignatura;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CustodiaPluginImpl implements CustodiaPlugin {

    @Autowired
    private CustodiaClientService custodiaClientService;

    @Override
    public String addSignature(
            String id,
            String gesdocId,
            String arxiuNom,
            String tipusDocument,
            byte[] signatura,
            Long entornId) throws CustodiaPluginException {

        var custodiaRequest = new CustodiaRequest();
        custodiaRequest.setDocumentId(id);
        custodiaRequest.setDocumentId(gesdocId);
        custodiaRequest.setNomArxiuSignat(arxiuNom);
        custodiaRequest.setCodiTipusCustodia(tipusDocument);
        custodiaRequest.setSignatura(signatura);
        return custodiaClientService.afegirSignatura(custodiaRequest, entornId);
    }

    @Override
    public List<byte[]> getSignatures(String id, Long entornId) throws CustodiaPluginException {
        return custodiaClientService.getSignatures(id, entornId);
    }

    @Override
    public byte[] getSignaturesAmbArxiu(String id, Long entornId) throws CustodiaPluginException {
        return custodiaClientService.getSignaturesAmbArxiu(id, entornId);
    }

    @Override
    public void deleteSignatures(String id, Long entornId) throws CustodiaPluginException {
        custodiaClientService.deleteSignatures(id, entornId);
    }

    @Override
    public List<RespostaValidacioSignatura> dadesValidacioSignatura(String id, Long entornId) throws CustodiaPluginException {
        return custodiaClientService.getDadesValidacioSignatura(id, entornId);
    }

    @Override
    public boolean potObtenirInfoSignatures() {
        return true; //TODO passar el mètode al microservei encara que torni sempre true
    }

    @Override
    public boolean isValidacioImplicita() {
        return true; //TODO passar el mètode al microservei encara que torni sempre true
    }

    @Override
    public String getUrlComprovacioSignatura(String id, Long entornId) throws CustodiaPluginException {
        return custodiaClientService.getUrlComprovacioSignatures(id, entornId);
    }
}
