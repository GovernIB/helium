/**
 * 
 */
package es.caib.helium.back.command;

import es.caib.helium.back.validator.AnotacioAcceptar;
import es.caib.helium.logic.intf.dto.AnotacioAccioEnumDto;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Command per acceptar una petició d'anotació i processar-la creant un expedient o incorporant-la 
 * a un d'existent.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@AnotacioAcceptar(groups = {AnotacioAcceptarCommand.CrearIncorporar.class})
public class AnotacioAcceptarCommand {

	private Long entornId;
	@NotNull(groups = {CrearIncorporar.class})
	private Long id;
	@NotNull(groups = {CrearIncorporar.class})
	private AnotacioAccioEnumDto accio;
	private Long expedientTipusId;
	private Long expedientId;
	@Size(max = 255, groups = {CrearIncorporar.class})
	private String titol;
	private Integer any;
	private String numero;
	private boolean associarInteressats;
	
	public Long getEntornId() {
		return entornId;
	}
	public void setEntornId(Long entornId) {
		this.entornId = entornId;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getExpedientTipusId() {
		return expedientTipusId;
	}
	public void setExpedientTipusId(Long expedientTipusId) {
		this.expedientTipusId = expedientTipusId;
	}
	public Long getExpedientId() {
		return expedientId;
	}
	public void setExpedientId(Long expedientId) {
		this.expedientId = expedientId;
	}
	public String getTitol() {
		return titol;
	}
	public void setTitol(String titol) {
		this.titol = titol;
	}
	public Integer getAny() {
		return any;
	}
	public void setAny(Integer any) {
		this.any = any;
	}
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	public boolean isAssociarInteressats() {
		return associarInteressats;
	}
	public void setAssociarInteressats(boolean associarInteressats) {
		this.associarInteressats = associarInteressats;
	}
	public AnotacioAccioEnumDto getAccio() {
		return accio;
	}
	public void setAccio(AnotacioAccioEnumDto accio) {
		this.accio = accio;
	}
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(
				this);
	}
	
	public interface CrearIncorporar {}
}
