package es.caib.helium.integracio.plugins.validacio;

import es.caib.helium.commons.dto.ArxiuFirmaDetallDto;

import java.util.ArrayList;
import java.util.List;

/**
 * Informació de la resposta de la validació de les firmes.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ValidaSignaturaResposta {

	/** Indica que la firma és vàlida */
    public static final int FIRMA_VALIDA = 1;
    /** Indica que s'ha produït un error validant i la causa està en errException. */
    public static final int FIRMA_ERROR = -1;
    /** Indica que la firma és invàlida i la causa està en errMsg. */
    public static final int FIRMA_INVALIDA = -2;

	private int status;
	private String errMsg;
	private Throwable errException;

	private List<ArxiuFirmaDetallDto> firmaDetalls = new ArrayList<>();

	/** Perfil de firma */
	private String perfil;
	/** Tipus de firma */
	private String tipus;


	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getErrMsg() {
		return errMsg;
	}
	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}
	public Throwable getErrException() {
		return errException;
	}
	public void setErrException(Throwable errException) {
		this.errException = errException;
	}
	public List<ArxiuFirmaDetallDto> getFirmaDetalls() {
		return firmaDetalls;
	}
	public void setFirmaDetalls(List<ArxiuFirmaDetallDto> firmaDetalls) {
		this.firmaDetalls = firmaDetalls;
	}

	/** Mètode per determinar si la resposta conté una firma vàlida.
	 *
	 * @return Retorna true si l'estat és 1 de firma vàlida.
	 */
	public boolean isValida() {
		return FIRMA_VALIDA == getStatus();
	}

	/** Mètode per determinar la descripció de per què la firma no és vàlida.
	 *
	 * @return Si l'estat és d'error retorna el missatge de l'excepció, si no el missatge de la resposta.
	 */
	public String getCausaInvalida() {
		String causaInvalida = null;
		if (FIRMA_ERROR == getStatus() && getErrException() != null) {
			causaInvalida = getErrException().getMessage();
		} else {
			causaInvalida = getErrMsg();
		}
		return causaInvalida;
	}
	public String getPerfil() {
		return perfil;
	}
	public void setPerfil(String perfil) {
		this.perfil = perfil;
	}
	public String getTipus() {
		return tipus;
	}
	public void setTipus(String tipus) {
		this.tipus = tipus;
	}
}
