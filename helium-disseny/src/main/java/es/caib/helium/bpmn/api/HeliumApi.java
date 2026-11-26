package es.caib.helium.bpmn.api;

import java.util.Date;
import java.util.List;

import es.caib.helium.bpmn.engine.WProcessInstance;
import es.caib.helium.bpmn.engine.WTaskInstance;
import es.caib.helium.bpmn.model.*;

public interface HeliumApi {

	public <T> T getVariable(String codi);
	public void setVariable(String codi, Object valor);
	public <T> T getVariableDefaultValue(String codi, T defaultValue);
	public Date getVariableDefaultValueAsDate(String codi, Object defaultValue);
	public Integer getVariableDefaultValueAsInteger(String codi, Object defaultValue);
	public Boolean getVariableDefaultValueAsBoolean(String codi, Object defaultValue);

	public ExpedientInfo getExpedientInfo();
	public ExpedientTipusInfo getExpedientTipusInfo();

	public DocumentInfo getDocumentInfo(String documentCodi);
	public DocumentInfo getDocumentInfo(String documentCodi, boolean ambCongingut);

	public DocumentInfo setDocument(
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

	/** Igual que la versió amb {@code codiPostal}, sense codi postal. */
	public default void interessatCrear(
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
		String canalNotif) {
		interessatCrear(codi, nom, tipusDocIdent, documentIdent, dir3Codi, llinatge1, llinatge2, tipus, email, telefon, entregaPostal, entregaTipus, linia1, linia2, entregaDeh, entregaDehObligat, direccio, pais, provincia, municipi, canalNotif, null);
	}
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
		String canalNotif,
		String codiPostal);

	/** Igual que la versió amb {@code codiPostal}, sense codi postal. */
	public default void interessatModificar(
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
		String canalNotif) {
		interessatModificar(codi, nom, tipusDocIdent, documentIdent, dir3Codi, llinatge1, llinatge2, tipus, email, telefon, entregaPostal, entregaTipus, linia1, linia2, entregaDeh, entregaDehObligat, direccio, pais, provincia, municipi, canalNotif, null);
	}
	public void interessatModificar(
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
		String canalNotif,
		String codiPostal);

	public void interessatEliminar(String codi);

	/**
	 * Envia un document al portasignatures. El procés ha d'esperar la resposta en una <i>receive task</i> just després
	 * de la tasca del handler: quan es signa o es rebutja, es reprèn per {@code transicioOK} o {@code transicioKO}.
	 */
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

	public void notificacioCrear(long expedientId, DadesNotificacio notificacio);

	public void enviarEmail(
		List<String> recipients,
		List<String> ccRecipients,
		List<String> bccRecipients,
		String subject,
		String text,
		List<String> attachments);

	public WProcessInstance getProcessInstance();
	public WTaskInstance getTaskInstance();

	/**
	 * Termini iniciat amb aquest codi a la instància de procés indicada (p. ex. la del procés pare).
	 *
	 * @return el termini iniciat, o null si encara no s'ha iniciat.
	 * @throws es.caib.helium.bpmn.exception.HeliumHandlerException si no hi ha cap termini amb aquest codi.
	 */
	public TerminiIniciatInfo getTerminiIniciatAmbProcessInstanceITerminiCodi(String processInstanceId, String codi);

	/*
	 * Terminis. Els terminis es defineixen al tipus d'expedient (o a la definició de procés, si el tipus no té
	 * informació pròpia) i s'inicien a la instància de procés on s'executa el handler. Si el codi no correspon a cap
	 * termini, es llança HeliumHandlerException. Si la data és null, s'agafa la data actual.
	 */

	/**
	 * Termini iniciat amb aquest codi a la instància de procés del handler.
	 *
	 * @return el termini iniciat, o null si encara no s'ha iniciat.
	 */
	public TerminiIniciatInfo getTerminiIniciat(String terminiCodi);

	public TerminiInfo getTermini(String terminiCodi);

	/**
	 * Inicia el termini amb la durada configurada (o la del termini iniciat, si ja s'havia iniciat abans). Si ja
	 * estava iniciat, es torna a iniciar.
	 *
	 * @param data data d'inici, o de fi si {@code esDataFi} és true (la d'inici es calcula a partir de la de fi).
	 */
	public void terminiIniciar(String terminiCodi, Date data, boolean esDataFi);

	/** Inicia el termini amb la durada indicada. Vegeu {@link #terminiIniciar(String, Date, boolean)}. */
	public void terminiIniciar(String terminiCodi, Date data, int anys, int mesos, int dies, boolean esDataFi);

	/** Pausa el termini iniciat. Llança HeliumHandlerException si el termini no s'ha iniciat. */
	public void terminiPausar(String terminiCodi, Date data);

	/** Continua el termini pausat. Llança HeliumHandlerException si el termini no s'ha iniciat o no està pausat. */
	public void terminiContinuar(String terminiCodi, Date data);

	/** Cancel·la el termini iniciat. Llança HeliumHandlerException si el termini no s'ha iniciat. */
	public void terminiCancelar(String terminiCodi, Date data);

	/**
	 * Calcula la data de fi d'un termini que comença a {@code inici}, tenint en compte els festius del tipus
	 * d'expedient si és laborable.
	 */
	public Date terminiCalcularDataFi(Date inici, int anys, int mesos, int dies, boolean laborable);

	/**
	 * Calcula la data d'inici d'un termini que acaba a {@code fi}, tenint en compte els festius del tipus
	 * d'expedient si és laborable.
	 */
	public Date terminiCalcularDataInici(Date fi, int anys, int mesos, int dies, boolean laborable);

	/** Modifica la georeferència de l'expedient. */
	public void expedientGeorefModificar(Double posx, Double posy, String referencia);

	/** Reprèn l'expedient aturat. */
	public void expedientReprendre();

	/** Modifica el títol de la instància de procés on s'executa el handler (p. ex. d'un subprocés). */
	public void procesTitolModificar(String titol);

	/**
	 * Fa que el procés pare tingui el document de la instància de procés actual (el mateix document, no una
	 * còpia). Si el document no existeix no fa res.
	 */
	public void documentEscriureProcesPare(String documentCodi);

	/** Fa que la instància de procés actual tingui el document del procés pare (el mateix document). */
	public void documentLlegirProcesPare(String documentCodi);

	/**
	 * Copia un document de l'expedient actual a un altre expedient del mateix entorn.
	 *
	 * @param documentCodi codi del document a copiar.
	 * @param expedientTipusCodi tipus de l'expedient destí.
	 * @param expedientNumero número de l'expedient destí.
	 * @param documentDestiCodi codi del document a l'expedient destí (si és null, el mateix codi).
	 */
	public void documentCopiarAExpedient(
			String documentCodi,
			String expedientTipusCodi,
			String expedientNumero,
			String documentDestiCodi);

	/**
	 * Copia un document d'un altre expedient del mateix entorn a l'expedient actual.
	 *
	 * @param expedientTipusCodi tipus de l'expedient origen.
	 * @param expedientNumero número de l'expedient origen.
	 * @param documentOrigenCodi codi del document a l'expedient origen (si és null, el mateix codi).
	 * @param documentCodi codi del document a l'expedient actual.
	 */
	public void documentCopiarDesDExpedient(
			String expedientTipusCodi,
			String expedientNumero,
			String documentOrigenCodi,
			String documentCodi);

	/** Genera el document a partir de la seva plantilla i el desa a la instància de procés actual. */
	public DocumentInfo documentGenerar(String documentCodi, Date data);

	/** Registra la sortida d'un document de l'expedient (RegWeb3) i desa les dades de registre al document. */
	public RespostaRegistre registreSortida(DadesRegistreSortida dades);

	/**
	 * Consulta PINBAL i desa el justificant al document indicat. Si la consulta és asíncrona, el procés ha d'esperar
	 * la resposta en una <i>receive task</i> just després de la tasca del handler: quan arriba, es reprèn per
	 * {@code transicioOK}.
	 */
	public void consultaPinbal(ConsultaPinbal consulta);
}
