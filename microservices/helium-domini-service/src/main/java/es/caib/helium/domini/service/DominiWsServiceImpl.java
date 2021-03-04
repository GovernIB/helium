package es.caib.helium.domini.service;

import es.caib.helium.domini.domain.Domini;
import es.caib.helium.domini.model.OrigenCredencialsEnum;
import es.caib.helium.domini.model.ParellaCodiValor;
import es.caib.helium.domini.model.ResultatDomini;
import es.caib.helium.domini.model.TipusAuthEnum;
import es.caib.helium.domini.ws.ConsultaDomini;
import es.caib.helium.domini.ws.ConsultaDominiResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.ws.security.WSConstants;
import org.apache.ws.security.handler.WSHandlerConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.soap.security.wss4j2.Wss4jSecurityInterceptor;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class DominiWsServiceImpl extends WebServiceGatewaySupport implements DominiWsService {

    @Autowired
    private Environment environment;

    public ResultatDomini consultaDomini(
            Domini domini,
            String identificador,
            Map<String, Object> parametres) {

        // TODO: Afegir monitor d' integracions

        List<ParellaCodiValor> paramsConsulta = new ArrayList<>();
        if (parametres != null) {
            for (String codi: parametres.keySet()) {
                paramsConsulta.add(ParellaCodiValor.builder().codi(codi).valor(parametres.get(codi)).build());
            }
        }

        ConsultaDomini request = new ConsultaDomini();
        request.setArg0(identificador);
        request.getArg1().addAll(paramsConsulta);

        String username = null;
        String password = null;
        if (domini.getTipusAuth() != null && !TipusAuthEnum.NONE.equals(domini.getTipusAuth())) {
            if (OrigenCredencialsEnum.PROPERTIES.equals(domini.getOrigenCredencials())) {
                username = environment.getProperty(domini.getUsuari());
                password = environment.getProperty(domini.getContrasenya());
            } else {
                username = domini.getUsuari();
                password = domini.getContrasenya();
            }
        }

        WebServiceTemplate webServiceTemplate = getWebServiceTemplate();
        if (TipusAuthEnum.HTTP_BASIC.equals(domini.getTipusAuth())) {
            webServiceTemplate.setMessageSender(httpComponentsMessageSender(username, password));
        } else if (TipusAuthEnum.USERNAMETOKEN.equals(domini.getTipusAuth())) {
            webServiceTemplate.setInterceptors(new ClientInterceptor[]{ securityInterceptor(username, password) });
        }

        ConsultaDominiResponse response = (ConsultaDominiResponse) webServiceTemplate
                .marshalSendAndReceive(
                        domini.getUrl(),
                        request);

        return (ResultatDomini) response.getReturn();
    }

    private HttpComponentsMessageSender httpComponentsMessageSender(String username, String password) {
        HttpComponentsMessageSender httpComponentsMessageSender = new HttpComponentsMessageSender();
        httpComponentsMessageSender.setCredentials(new UsernamePasswordCredentials(username, password));
        return httpComponentsMessageSender;
    }

    private Wss4jSecurityInterceptor securityInterceptor(String username, String password) {
        Wss4jSecurityInterceptor security = new Wss4jSecurityInterceptor();
        security.setSecurementActions(WSHandlerConstants.TIMESTAMP + " " + WSHandlerConstants.USERNAME_TOKEN);
        security.setSecurementPasswordType(WSConstants.PW_TEXT);
        security.setSecurementUsername(username);
        security.setSecurementPassword(password);
        return security;
    }
}
