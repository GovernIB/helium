package es.caib.helium.bpmn.api;

import java.util.Date;
import java.util.List;

public interface HeliumApi {

	Object getVariable(String codi);
	void setVariable(String codi, Object valor);
	<T> T getVariableDefaultValue(String codi, T defaultValue);

	Date getVariableDefaultValueAsDate(String codi, Object defaultValue);

	Boolean getVariableDefaultValueAsBoolean(String codi, Object defaultValue);

	void alertaCrear(String usuariCodi, String text);

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

}
