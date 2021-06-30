package es.caib.helium.base.events;

import java.io.Serializable;

import es.caib.helium.base.model.ExempleDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageEvent implements Serializable {

    private static final long serialVersionUID = 5233129126952049009L;

    private ExempleDto exempleDto;

}
