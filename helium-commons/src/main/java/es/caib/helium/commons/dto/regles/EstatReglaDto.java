package es.caib.helium.commons.dto.regles;

import java.io.Serializable;
import java.util.SortedSet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import es.caib.helium.commons.dto.HeretableDto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstatReglaDto extends HeretableDto implements Serializable {
	
	private static final long serialVersionUID = -8989965902752604366L;

	private Long id;
    private int ordre;
    private String nom;
    private Long expedientTipusId;
    private Long estatId;
    private QuiEnum qui;
    private SortedSet<String> quiValor;
    private QueEnum que;
    private SortedSet<String> queValor;
    private AccioEnum accio;
}
