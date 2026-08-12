package es.caib.helium.disseny.api;

import es.caib.helium.commons.dto.TerminiIniciatDto;
import es.caib.helium.disseny.engine.WProcessInstance;
import es.caib.helium.disseny.engine.WTaskInstance;
import es.caib.helium.disseny.model.DocumentInfo;
import es.caib.helium.disseny.model.ExpedientInfo;

import java.util.Date;
import java.util.List;

public interface HeliumApi {

	public <T> T getVariable(String codi);
	public void setVariable(String codi, Object valor);
	public <T> T getVariableDefaultValue(String codi, T defaultValue);
	public Date getVariableDefaultValueAsDate(String codi, Object defaultValue);
	public Boolean getVariableDefaultValueAsBoolean(String codi, Object defaultValue);

	public ExpedientInfo getExpedientInfo();

	public DocumentInfo getDocumentInfo(String documentCodi);

	public void setDocument(
		String documentCodi,
		String arxiuNom,
		byte[] arxiuContingut,
		Date dataDocument,
		boolean ambFirma,
		boolean firmaSeparada,
		byte[] firmaContingut);

	public void deleteDocument(String documentCodi);

	public void alertaCrear(String usuariCodi, String text);

	public void documentConsultar(
		String documentCodi,
		String varCsv,
		String varUrl);

	public void documentAdjuntar(
		String documentOrigen,
		String titol,
		Date data,
		boolean concatenarTitol,
		boolean esborrarDocument);

	public void expedientAturar(String motiu);

	public void expedientEstatModificar(String codi);

	public void expedientComentariModificar(String comentari);

	public void expedientConsultar(
		String varRegistreNumero,
		String varTitol,
		String varNumero,
		String verDataInici);

	public void expedientFinalitzar();

	public void expedientDesfinalitzar(boolean reprendre);

	public void expedientGrupModificar(String grup);

	public void expedientNumeroModificar(String numero);

	public void expedientResponsableModificar(String responsableCodi);

	public void expedientTitolModificar(String titol);

	public void interessatCrear(
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

	public void interessatModificar(
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

	public void interessatEliminar(String codi);

	public Integer portasignaturesEnviar(
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

	public void enviarEmail(
		List<String> recipients,
		List<String> ccRecipients,
		List<String> bccRecipients,
		String subject,
		String text,
		List<String> attachments);

	public WProcessInstance getProcessInstance();
	public WTaskInstance getTaskInstance();

	public TerminiIniciatDto getTerminiIniciatAmbProcessInstanceITerminiCodi(String processInstanceId, String codi);
}
