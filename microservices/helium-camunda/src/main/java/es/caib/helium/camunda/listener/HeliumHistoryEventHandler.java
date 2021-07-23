package es.caib.helium.camunda.listener;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.impl.history.event.HistoryEvent;
import org.camunda.bpm.engine.impl.history.handler.HistoryEventHandler;

import java.util.List;

@Slf4j
public class HeliumHistoryEventHandler implements HistoryEventHandler {

    @Override
    public void handleEvent(HistoryEvent historyEvent) {
      log.info("Event: " + historyEvent.toString());
    }

    @Override
    public void handleEvents(List<HistoryEvent> list) {
        log.info("Events: ");
        list.stream().forEach(e -> log.info("Events: " + e.toString()));
    }
}
