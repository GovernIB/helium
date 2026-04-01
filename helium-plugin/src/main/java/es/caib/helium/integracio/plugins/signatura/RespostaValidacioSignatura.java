/**
 * 
 */
package es.caib.helium.integracio.plugins.signatura;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Resposta a una petició de validació de signatura
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

@Getter @Setter
public class RespostaValidacioSignatura {

	public static final String ESTAT_VALID = "VALID";
	public static final String ESTAT_INVALID = "INVALID";
	public static final String ESTAT_ERROR = "ERROR";

	protected String estat;
	protected String errorMsg;
	protected Throwable errorException;
	private List<DadesCertificat> dadesCertificat;

	public boolean isEstatValid() {
		return ESTAT_VALID.equals(estat);
	}
	public boolean isEstatError() {
		return ESTAT_ERROR.equals(estat);
	}
}
