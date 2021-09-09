package es.caib.helium.integracio.plugins.notificacio;

import es.caib.helium.client.integracio.notificacio.NotificacioClient;
import es.caib.helium.client.integracio.notificacio.model.ConsultaEnviament;
import es.caib.helium.client.integracio.notificacio.model.ConsultaNotificacio;
import es.caib.helium.client.integracio.notificacio.model.DadesNotificacioDto;
import es.caib.helium.client.integracio.notificacio.model.RespostaConsultaEstatEnviament;
import es.caib.helium.client.integracio.notificacio.model.RespostaConsultaEstatNotificacio;
import es.caib.helium.client.integracio.notificacio.model.RespostaEnviar;
import org.springframework.beans.factory.annotation.Autowired;

public class NotificacioPluginImpl implements NotificacioPlugin {

    @Autowired
    private NotificacioClient notificacioClientService;

    @Override
    public RespostaEnviar enviar(DadesNotificacioDto notificacio) throws NotificacioPluginException {
        return notificacioClientService.altaNotificacio(notificacio);
    }

    @Override
    public RespostaConsultaEstatNotificacio consultarNotificacio(String identificador, ConsultaNotificacio consulta) throws NotificacioPluginException {
        return notificacioClientService.consultarNotificacio(identificador, consulta);
    }

    @Override
    public RespostaConsultaEstatEnviament consultarEnviament(String referencia, ConsultaEnviament consulta) throws NotificacioPluginException {
        return notificacioClientService.consultarEnviament(referencia, consulta);
    }
}
