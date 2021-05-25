package es.caib.helium.jbpm3.helper;

import es.caib.helium.api.dto.InteressatDto;
import es.caib.helium.api.dto.TerminiDto;
import es.caib.helium.api.dto.TerminiIniciatDto;
import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

@Component
public class TerminisHelper {

    @Value("${es.caib.helium.jbpm.bridge.service.host}")
    String bridgeAdress;

    @Autowired
    RestTemplate restTemplate;

    public TerminiDto getTerminiAmbProcessInstanceICodi(
            String processInstanceId,
            String terminiCodi) {
        return  restTemplate.getForObject(
                getTerminisBridgeAddress() + "/{terminiCodi}?processInstanceId={processInstanceId}",
                TerminiDto.class,
                terminiCodi,
                processInstanceId);
    }

    public TerminiIniciatDto getTerminiIniciatAmbProcessInstanceITerminiCodi(
            String processDefinitionId,
            String processInstanceId,
            String terminiCodi) {
        return  restTemplate.getForObject(
                getTerminisBridgeAddress() + "/iniciat?processDefinitionId={processDefinitionId}&processInstanceId={processInstanceId}&terminiCodi={terminiCodi}",
                TerminiIniciatDto.class,
                processDefinitionId,
                processInstanceId,
                terminiCodi);
    }

    public void terminiIniciar(
            String terminiCodi,
            TerminiInici terminiInici) {
        restTemplate.postForLocation(
                getTerminisBridgeAddress() + "/{terminiCodi}/iniciar",
                terminiInici,
                terminiCodi);
    }

    public void terminiPausar(Long terminiIniciatId, Date data) {
        restTemplate.postForLocation(
                getTerminisBridgeAddress() + "/{terminiId}/pausar",
                data,
                terminiIniciatId);
    }

    public void terminiContinuar(Long terminiIniciatId, Date data) {
        restTemplate.postForLocation(
                getTerminisBridgeAddress() + "/{terminiId}/continuar",
                data,
                terminiIniciatId);
    }

    public void terminiCancelar(Long terminiIniciatId, Date data) {
        restTemplate.postForLocation(
                getTerminisBridgeAddress() + "/{terminiId}/cancelar",
                data,
                terminiIniciatId);
    }

    public Date terminiCalcularDataInici(TerminiCalcul terminiCalcul) {
        return restTemplate.postForObject(
                getTerminisBridgeAddress() + "/calcularInici",
                terminiCalcul,
                Date.class);
    }

    public Date terminiCalcularDataFi(TerminiCalcul terminiCalcul) {
        return restTemplate.postForObject(
                getTerminisBridgeAddress() + "/calcularFi",
                terminiCalcul,
                Date.class);
    }

    public void configurarTerminiIniciatAmbDadesWf(Long terminiIniciatId, TerminiConfigurar terminiConfigurar) {
        restTemplate.postForLocation(
                getTerminisBridgeAddress() + "/{terminiId}/configurar",
                terminiConfigurar,
                terminiIniciatId);
    }

    public void crear(InteressatDto interessat) {
        restTemplate.postForLocation(getTerminisBridgeAddress(), interessat);
    }

    public void modificar(InteressatDto interessat) {
        restTemplate.put(getTerminisBridgeAddress(), interessat);
    }

    public void eliminar(String interessatCodi, Long expedientId) {
        restTemplate.delete(getTerminisBridgeAddress() + "/{interessatCodi}/expedient/{expedientId}",
                interessatCodi,
                expedientId);
    }

    private String getTerminisBridgeAddress() {
        return bridgeAdress + "/terminis";
    }

    @Data @Builder
    public class TerminiInici {
        private String processInstanceId;
        private Date data;
        private int anys;
        private int mesos;
        private int dies;
        private boolean esDataFi;
    }

    @Data @Builder
    public class TerminiCalcul {
        private Date data;
        private int anys;
        private int mesos;
        private int dies;
        private boolean laborable;
        private String processInstanceId;
    }

    @Data @Builder
    public class TerminiConfigurar {
        private String taskInstanceId;
        private Long timerId;
    }

}
