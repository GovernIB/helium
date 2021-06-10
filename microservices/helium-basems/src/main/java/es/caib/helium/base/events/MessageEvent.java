package es.caib.helium.base.events;

import es.caib.helium.base.model.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@Data
@RequiredArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageEvent implements Serializable {

    private static final long serialVersionUID = 5233129126952049009L;

    private BaseDto baseDto;

}
