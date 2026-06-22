package es.caib.helium.disseny.api;

import es.caib.helium.disseny.model.DocumentInfo;
import es.caib.helium.disseny.model.ExpedientInfo;

import java.util.Date;
import java.util.List;

public interface HeliumApi {

	<T> T getVariable(String codi);
	void setVariable(String codi, Object valor);
	<T> T getVariableDefaultValue(String codi, T defaultValue);
	Date getVariableDefaultValueAsDate(String codi, Object defaultValue);
	Boolean getVariableDefaultValueAsBoolean(String codi, Object defaultValue);

	ExpedientInfo getExpedientInfo();

	DocumentInfo getDocumentInfo(String documentCodi);

	void setDocument(
		String documentCodi,
		String arxiuNom,
		byte[] arxiuContingut,
		Date dataDocument,
		boolean ambFirma,
		boolean firmaSeparada,
		byte[] firmaContingut);

	void deleteDocument(String documentCodi);

	void alertaCrear(String usuariCodi, String text);

	void documentConsultar(
		String documentCodi,
		String varCsv,
		String varUrl);

	void documentAdjuntar(
		String documentOrigen,
		String titol,
		Date data,
		boolean concatenarTitol,
		boolean esborrarDocument);

	void expedientAturar(String motiu);

	void expedientEstatModificar(String codi);

	void expedientComentariModificar(String comentari);

	void expedientConsultar(
		String varRegistreNumero,
		String varTitol,
		String varNumero,
		String verDataInici);

	void expedientFinalitzar();

	void expedientDesfinalitzar(boolean reprendre);

	void expedientGrupModificar(String grup);

	void expedientNumeroModificar(String numero);

	void expedientResponsableModificar(String responsableCodi);

	void expedientTitolModificar(String titol);

	void interessatCrear(
		String codi,
		String nom,
		String tipusDocIdent,
		String documentIdent,
		String dir3Codi,
		String llinatge1,
		String llinatge2,
		String tipus,
		String email,
		String telefon,
		Boolean entregaPostal,
		String entregaTipus,
		String linia1,
		String linia2,
		Boolean entregaDeh,
		Boolean entregaDehObligat,
		String direccio,
		String pais,
		String provincia,
		String municipi,
		String canalNotif);

	void interessatModificar(
		String id,
		String codi,
		String nom,
		String tipusDocIdent,
		String documentIdent,
		String dir3Codi,
		String llinatge1,
		String llinatge2,
		String tipus,
		String email,
		String telefon,
		Boolean entregaPostal,
		String entregaTipus,
		String linia1,
		String linia2,
		Boolean entregaDeh,
		Boolean entregaDehObligat,
		String direccio,
		String pais,
		String provincia,
		String municipi,
		String canalNotif);

	void interessatEliminar(String codi);

	Integer portasignaturesEnviar(
		String documentCodi,
		String personaCodi,
		List<String> annexCodis,
		List<String> personaCodisPas1,
		Integer minSignatarisPas1,
		List<String> personaCodisPas2,
		Integer minSignatarisPas2,
		List<String> personaCodisPas3,
		Integer minSignatarisPas3,
		String importancia,
		Date dataLimit,
		String transicioOK,
		String transicioKO,
		String portafirmesFluxId);

	void enviarEmail(
		List<String> recipients,
		List<String> ccRecipients,
		List<String> bccRecipients,
		String subject,
		String text,
		List<String> attachments);

}
