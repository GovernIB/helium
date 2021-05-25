package es.caib.helium.jbpm3.helper;

import es.caib.helium.api.dto.ArxiuDto;
import es.caib.helium.api.dto.DocumentDissenyDto;
import es.caib.helium.api.dto.DocumentDto;
import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

@Component
public class DocumentsHelper {

    @Value("${es.caib.helium.jbpm.bridge.service.host}")
    String bridgeAdress;

    @Autowired
    RestTemplate restTemplate;

    public DocumentDissenyDto getDocumentDisseny(
            Long definicioProcesId,
            String processInstanceId,
            String documentCodi) {
        return restTemplate.getForObject(
                getDocumentsBridgeAddress() + "/{documentCodi}/proces/{processInstanceId}/disseny?definicioProcesId={definicioProcesId}",
                DocumentDissenyDto.class,
                documentCodi,
                processInstanceId,
                definicioProcesId);
    }

    public DocumentDto getDocumentInfo(Long documentStoreId) {
        return restTemplate.getForObject(
                getDocumentsBridgeAddress() + "/{documentStoreId}/info",
                DocumentDto.class,
                documentStoreId);
    }

    public DocumentDto getDocumentInfo(Long documentStoreId,
                                       boolean ambContingutOriginal,
                                       boolean ambContingutSignat,
                                       boolean ambContingutVista,
                                       boolean perSignar,
                                       boolean perNotificar,
                                       boolean ambSegellSignatura) {
        return restTemplate.getForObject(
                getDocumentsBridgeAddress() + "/{documentStoreId}/infoFiltre" +
                        "?ambContingutOriginal={ambContingutOriginal}" +
                        "&ambContingutSignat={ambContingutSignat}" +
                        "&ambContingutVista={ambContingutVista}" +
                        "&perSignar={perSignar}" +
                        "&perNotificar={perNotificar}" +
                        "&ambSegellSignatura={ambSegellSignatura}",
                DocumentDto.class,
                documentStoreId,
                ambContingutOriginal,
                ambContingutSignat,
                ambContingutVista,
                perSignar,
                perNotificar,
                ambSegellSignatura);
    }

    public String getCodiVariablePerDocumentCodi(String documentCodi) {
        return restTemplate.getForObject(
                getDocumentsBridgeAddress() + "/{documentCodi}/codi",
                String.class,
                documentCodi);
    }

    public ArxiuDto getArxiuPerMostrar(Long documentStoreId) {
        return restTemplate.getForObject(
                getDocumentsBridgeAddress() + "/{documentStoreId}",
                ArxiuDto.class,
                documentStoreId);
    }

    public ArxiuDto documentGenerarAmbPlantilla(
            String documentCodi,
            DocumentGenerar documentGenerar) {
        return restTemplate.postForObject(
                getDocumentsBridgeAddress() + "/{documentCodi}/generar",
                documentGenerar,
                ArxiuDto.class,
                documentCodi);
    }

    public Long documentExpedientGuardar(
            String documentCodi,
            DocumentGuardar documentGuardar) {
        return restTemplate.postForObject(
                getDocumentsBridgeAddress() + "/{documentCodi}",
                documentGuardar,
                Long.class,
                documentCodi);
    }

    public Long documentExpedientAdjuntar(
            String processInstanceId,
            DocumentAdjunt adjunt) {
        return restTemplate.postForObject(
                getDocumentsBridgeAddress() + "/proces/{processInstanceId}/adjuntar",
                adjunt,
                Long.class,
                processInstanceId);
    }

    public void documentExpedientGuardarDadesRegistre(
            Long documentStoreId,
            DocumentRegistre registre) {
        restTemplate.postForLocation(
                getDocumentsBridgeAddress() + "/{documentStoreId}/registre",
                registre,
                documentStoreId);
    }

    public void documentExpedientEsborrar(
            String documentCodi,
            DocumentTasca tasca) {
        restTemplate.postForLocation(
                getDocumentsBridgeAddress() + "/{documentCodi}/esborrar",
                tasca,
                documentCodi);
    }


    private String getDocumentsBridgeAddress() {
        return bridgeAdress + "/documents";
    }

    @Data @Builder
    public class DocumentRegistre {
        private String numero;
        private Date data;
        private String oficinaCodi;
        private String oficinaNom;
        private boolean entrada;
    }

    @Data @Builder
    public class DocumentTasca {
        private String processInstanceId;
        private String taskInstanceId;
    }

    @Data @Builder
    public class DocumentAdjunt {
        private String adjuntId;
        private String adjuntTitol;
        private Date adjuntData;
        private String arxiuNom;
        private byte[] arxiuContingut;
    }

    @Data @Builder
    public class DocumentGuardar {
        private String processInstanceId;
        private Date data;
        private String arxiuNom;
        private byte[] arxiuContingut;
    }

    @Data @Builder
    public class DocumentGenerar {
        private String taskInstanceId;
        private String processDefinitionId;
        private String processInstanceId;
        private Date dataDocument;
    }

}
