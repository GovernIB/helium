package net.conselldemallorca.helium.v3.core.api.dto.regles;

import java.io.Serializable;
import java.util.SortedSet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstatReglaDto implements Serializable {
	
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
