/**
 * 
 */
package es.caib.helium.back.command;

import es.caib.helium.back.validator.DefinicioProcesDesplegar;
import es.caib.helium.logic.intf.dto.MotorTipusEnum;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

/**
 * Command pel desplegament d'un arxiu .par d'una definició de procés.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@DefinicioProcesDesplegar(groups = {DefinicioProcesDesplegarCommand.Desplegament.class})
public class DefinicioProcesDesplegarCommand {
	
	/** Enumeració per distingir la acció a realitzar amb el desplegament JBPM. */
	public enum ACCIO_JBPM {
		// Realitza un desplegament normal	
		JBPM_DESPLEGAR,
		// Sobreescriu els handlers
		JBPM_ACTUALITZAR;
	}

	/** Id de la definició de procés sobre la que es desplega la definició de procés. */
	private Long id = null;	
	/** Id de l'entorn on es desplega la definició de procés. */
	private Long entornId;
	/** Id del tipus d'expedient on es desplega la definició de procés. */
	private Long expedientTipusId;
	/** Etiqueta que s'assignarà a la nova definició de procés.*/
	private String etiqueta;
	/** Indica si s'iniciarà una acció massiva per actualitzar els expedients actius. */
	private boolean actualitzarExpedientsActius;
	/** Contingut del fitxer */
	private MultipartFile file;
	/** Indica si augmentar la versió o sobre escriure els handlers. */
	private ACCIO_JBPM accio;

	@NotNull
	private MotorTipusEnum motorTipus;
		
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getEntornId() {
		return entornId;
	}
	public void setEntornId(Long entornId) {
		this.entornId = entornId;
	}
	public Long getExpedientTipusId() {
		return expedientTipusId;
	}
	public void setExpedientTipusId(Long expedientTipusId) {
		this.expedientTipusId = expedientTipusId;
	}
	public String getEtiqueta() {
		return etiqueta;
	}
	public void setEtiqueta(String etiqueta) {
		this.etiqueta = etiqueta;
	}
	public boolean isActualitzarExpedientsActius() {
		return actualitzarExpedientsActius;
	}
	public void setActualitzarExpedientsActius(boolean actualitzarExpedientsActius) {
		this.actualitzarExpedientsActius = actualitzarExpedientsActius;
	}
	public MultipartFile getFile() {
		return file;
	}
	public void setFile(MultipartFile file) {
		this.file = file;
	}
	
	public ACCIO_JBPM getAccio() {
		return accio;
	}
	public void setAccio(ACCIO_JBPM accio) {
		this.accio = accio;
	}

	public MotorTipusEnum getMotorTipus() {
		return motorTipus;
	}
	public void setMotorTipus(MotorTipusEnum motorTipus) {
		this.motorTipus = motorTipus;
	}

	public interface Desplegament {}
}
