package es.caib.helium.base.service;

import es.caib.helium.base.config.JmsConfig;
import es.caib.helium.base.events.MessageEvent;
import es.caib.helium.base.model.ExempleDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class CuaService {

    private final JmsTemplate jmsTemplate;

    public void sendEvent() {
        ExempleDto baseDto = ExempleDto.builder()
                .id(1L)
                .codi("codi")
                .nom("nom")
                .build();
        jmsTemplate.convertAndSend(JmsConfig.BASE_CUE, new MessageEvent(baseDto));
    }

}
