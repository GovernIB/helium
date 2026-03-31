/**
 * 
 */
package es.caib.helium.back.command;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.helium.commons.dto.EnviamentTipusEnumDto;
import es.caib.helium.commons.dto.NotificacioEnviamentEstatEnumDto;
import es.caib.helium.commons.dto.NotificacioEstatEnumDto;
import lombok.Getter;
import lombok.Setter;

/**
 * Command per al filtre d'expedients dels arxius.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class NotificacioFiltreCommand implements Serializable {
	private static final long serialVersionUID = 5351582872698404634L;
	private EnviamentTipusEnumDto tipus;
	private String concepte;
	private NotificacioEstatEnumDto estat;
	private NotificacioEnviamentEstatEnumDto enviamentDatatEstat;
	private Date dataInicial;
	private Date dataFinal;
	private String interessat;
	private String expedientNumero;
	private Long expedientTipusId;
	private String nomDocument;
	private String unitatOrganitzativaCodi;
	private String procedimentCodi;
	private Long expedientId;
	private Long entornId;
	private Long tipusId;

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
