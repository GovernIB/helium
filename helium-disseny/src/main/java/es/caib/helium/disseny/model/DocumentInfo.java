package es.caib.helium.disseny.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Informació d'un document de l'expedient.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class DocumentInfo {

	private Long id;
	private String titol;
	private Date dataCreacio;
	private Date dataDocument;
	private String arxiuNom;
	private byte[] arxiuContingut;
	private String arxiuUuid;
	private boolean signat = false;
	private String registreNumero;
	private Date registreData;
	private String registreOficinaCodi;
	private String registreOficinaNom;
	private boolean registreEntrada = true;
	private boolean registrat = false;
	private String processInstanceId;
	private String processInstanceTitol;
	private String codiDocument;
	private String tipusDocument;
	private String tipusDocumental;
	private Integer origen;
	private Integer modeFirma;
	private String validesa;
	private String observacions;
	private String csv;
	private String urlVerificacioSignatures;

	public String getArxiuExtensio() {
		if (getArxiuNom() == null)
			return null;
		int indexPunt = getArxiuNom().lastIndexOf(".");
		if (indexPunt != -1) {
			return getArxiuNom().substring(indexPunt + 1);
		} else {
			return null;
		}
	}

}
