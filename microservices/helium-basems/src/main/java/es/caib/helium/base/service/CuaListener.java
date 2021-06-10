package es.caib.helium.base.service;

import es.caib.helium.base.config.JmsConfig;
import es.caib.helium.base.events.MessageEvent;
import es.caib.helium.base.model.BaseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CuaListener {

    @Transactional
    @JmsListener(destination = JmsConfig.BASE_CUE)
    public void listen(MessageEvent event) {
        BaseDto baseDto = event.getBaseDto();
    }
}
