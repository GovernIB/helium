package es.caib.helium.jms.domini;

import es.caib.helium.jms.events.IntegracioEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageEvent {

    private IntegracioEvent event;
}