/**
 * 
 */
package net.conselldemallorca.helium.core.helper;

import java.util.Date;

import javax.annotation.Resource;

import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.Registre;
import net.conselldemallorca.helium.v3.core.repository.ExpedientRepository;
import net.conselldemallorca.helium.v3.core.repository.RegistreRepository;

import org.springframework.stereotype.Component;

/**
 * Helper per a gestionar el registre d'accions dels expedients
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class ExpedientRegistreHelper {

	@Resource
	RegistreRepository registreRepository;
	@Resource
	ExpedientRepository expedientRepository;



	public Registre crearRegistreIniciarExpedient(
			Long expedientId,
			String responsableCodi) {
		Registre registre = new Registre(
				new Date(),
				expedientId,
				responsableCodi,
				Registre.Accio.INICIAR,
				Registre.Entitat.EXPEDIENT,
				getExpedientId(expedientId));
		return registreRepository.save(registre);
	}
	public Registre crearRegistreModificarExpedient(
			Long expedientId,
			String responsableCodi,
			String valorVell,
			String valorNou) {
		Registre registre = new Registre(
				new Date(),
				expedientId,
				responsableCodi,
				Registre.Accio.MODIFICAR,
				Registre.Entitat.EXPEDIENT,
				getExpedientId(expedientId));
		registre.setValorVell(valorVell);
		registre.setValorNou(valorNou);
		return registreRepository.save(registre);
	}
	public Registre crearRegistreAturarExpedient(
			Long expedientId,
			String responsableCodi,
			String comentari) {
		Registre registre = new Registre(
				new Date(),
				expedientId,
				responsableCodi,
				Registre.Accio.ATURAR,
				Registre.Entitat.EXPEDIENT,
				getExpedientId(expedientId));
		registre.setMissatge(comentari);
		return registreRepository.save(registre);
	}
	public Registre crearRegistreReprendreExpedient(
			Long expedientId,
			String responsableCodi) {
		Registre registre = new Registre(
				new Date(),
				expedientId,
				responsableCodi,
				Registre.Accio.REPRENDRE,
				Registre.Entitat.EXPEDIENT,
				getExpedientId(expedientId));
		return registreRepository.save(registre);
	}
	public Registre crearRegistreAnularExpedient(
			Long expedientId,
			String responsableCodi) {
		Registre registre = new Registre(
				new Date(),
				expedientId,
				responsableCodi,
				Registre.Accio.ANULAR,
				Registre.Entitat.EXPEDIENT,
				getExpedientId(expedientId));
		return registreRepository.save(registre);
	}
	public Registre crearRegistreEsborrarExpedient(
			Long expedientId,
			String responsableCodi) {
		Registre registre = new Registre(
				new Date(),
				expedientId,
				responsableCodi,
				Registre.Accio.ESBORRAR,
				Registre.Entitat.EXPEDIENT,
				getExpedientId(expedientId));
		return registreRepository.save(registre);
	}
	public Registre crearRegistreFinalitzarExpedient(
			Long expedientId,
			String responsableCodi) {
		Registre registre = new Registre(
				new Date(),
				expedientId,
				responsableCodi,
				Registre.Accio.FINALITZAR,
				Registre.Entitat.EXPEDIENT,
				getExpedientId(expedientId));
		return registreRepository.save(registre);
	}
	public Registre crearRegistreCrearTasca(
			Long expedientId,
			String tascaId,
			String responsableCodi,
			String missatge) {
		Registre registre = new Registre(
				new Date(),
				expedientId,
				responsableCodi,
				Registre.Accio.CREAR,
				Registre.Entitat.TASCA,
				tascaId);
		registre.setMissatge(missatge);
		return registreRepository.save(registre);
	}
	public Registre crearRegistreIniciarTasca(
			Long expedientId,
			String tascaId,
			String responsableCodi,
			String missatge) {
		Registre registre = new Registre(
				new Date(),
				expedientId,
				responsableCodi,
				Registre.Accio.INICIAR,
				Registre.Entitat.TASCA,
				tascaId);
		registre.setMissatge(missatge);
		return registreRepository.save(registre);
	}
	public Registre crearRegistreModificarTasca(
			Long expedientId,
			String tascaId,
			String responsableCodi,
			String missatge) {
		Registre registre = new Registre(
				new Date(),
				expedientId,
				responsableCodi,
				Registre.Accio.MODIFICAR,
				Registre.Entitat.TASCA,
				tascaId);
		registre.setMissatge(missatge);
		return registreRepository.save(registre);
	}
	public Registre crearRegistreSuspendreTasca(
			Long expedientId,
			String tascaId,
			String responsableCodi) {
		Registre registre = new Registre(
				new Date(),
				expedientId,
				responsableCodi,
				Registre.Accio.ATURAR,
				Registre.Entitat.TASCA,
				tascaId);
		return registreRepository.save(registre);
	}
	public Registre crearRegistreReprendreTasca(
			Long expedientId,
			String tascaId,
			String responsableCodi) {
		Registre registre = new Registre(
				new Date(),
				expedientId,
				responsableCodi,
				Registre.Accio.REPRENDRE,
				Registre.Entitat.TASCA,
				tascaId);
		return registreRepository.save(registre);
	}
	public Registre crearRegistreCancelarTasca(
			Long expedientId,
			String tascaId,
			String responsableCodi) {
		Registre registre = new Registre(
				new Date(),
				expedientId,
				responsableCodi,
				Registre.Accio.CANCELAR,
				Registre.Entitat.TASCA,
				tascaId);
		return registreRepository.save(registre);
	}
	public Registre crearRegistreRedirigirTasca(
			Long expedientId,
			String tascaId,
			String responsableCodi,
			String expression) {
		Registre registre = new Registre(
				new Date(),
				expedientId,
				responsableCodi,
				Registre.Accio.MODIFICAR,
				Registre.Entitat.TASCA,
				tascaId);
		registre.setMissatge("Redirecció de tasca amb expressió \"" + expression + "\"");
		return registreRepository.save(registre);
	}
	public Registre crearRegistreFinalitzarTasca(
			Long expedientId,
			String tascaId,
			String responsableCodi,
			String missatge) {
		Registre registre = new Registre(
				new Date(),
				expedientId,
				responsableCodi,
				Registre.Accio.FINALITZAR,
				Registre.Entitat.TASCA,
				tascaId);
		registre.setMissatge(missatge);
		return registreRepository.save(registre);
	}

	public Registre crearRegistreCrearVariableInstanciaProces(
			Long expedientId,
			String processInstanceId,
			String responsableCodi,
			String varName,
			Object valor) {
		Registre registre = new Registre(
				new Date(),
				expedientId,
				responsableCodi,
				Registre.Accio.MODIFICAR,
				Registre.Entitat.INSTANCIA_PROCES,
				processInstanceId);
		registre.setMissatge("Crear variable '" + varName + "'");
		if (valor != null)
			registre.setValorNou(valor.toString());
		return registreRepository.save(registre);
	}
	public Registre crearRegistreModificarVariableInstanciaProces(
			Long expedientId,
			String processInstanceId,
			String responsableCodi,
			String varName,
			Object valorVell,
			Object valorNou) {
		Registre registre = new Registre(
				new Date(),
				expedientId,
				responsableCodi,
				Registre.Accio.MODIFICAR,
				Registre.Entitat.INSTANCIA_PROCES,
				processInstanceId);
		registre.setMissatge("Modificar variable '" + varName + "'");
		if (valorVell != null)
			registre.setValorVell(valorVell.toString());
		if (valorNou != null)
			registre.setValorNou(valorNou.toString());
		return registreRepository.save(registre);
	}
	public Registre crearRegistreEsborrarVariableInstanciaProces(
			Long expedientId,
			String processInstanceId,
			String responsableCodi,
			String varName) {
		Registre registre = new Registre(
				new Date(),
				expedientId,
				responsableCodi,
				Registre.Accio.MODIFICAR,
				Registre.Entitat.INSTANCIA_PROCES,
				processInstanceId);
		registre.setMissatge("Esborrar variable '" + varName + "'");
		return registreRepository.save(registre);
	}
	public Registre crearRegistreCrearVariableTasca(
			Long expedientId,
			String taskId,
			String responsableCodi,
			String varName,
			Object valor) {
		Registre registre = new Registre(
				new Date(),
				expedientId,
				responsableCodi,
				Registre.Accio.MODIFICAR,
				Registre.Entitat.TASCA,
				taskId);
		registre.setMissatge("Crear variable '" + varName + "'");
		if (valor != null)
			registre.setValorNou(valor.toString());
		return registreRepository.save(registre);
	}
	public Registre crearRegistreModificarVariableTasca(
			Long expedientId,
			String taskId,
			String responsableCodi,
			String varName,
			Object valorVell,
			Object valorNou) {
		Registre registre = new Registre(
				new Date(),
				expedientId,
				responsableCodi,
				Registre.Accio.MODIFICAR,
				Registre.Entitat.TASCA,
				taskId);
		registre.setMissatge("Modificar variable '" + varName + "'");
		if (valorVell != null)
			registre.setValorVell(valorVell.toString());
		if (valorNou != null)
			registre.setValorNou(valorNou.toString());
		return registreRepository.save(registre);
	}
	public Registre crearRegistreEsborrarVariableTasca(
			Long expedientId,
			String taskId,
			String responsableCodi,
			String varName) {
		Registre registre = new Registre(
				new Date(),
				expedientId,
				responsableCodi,
				Registre.Accio.MODIFICAR,
				Registre.Entitat.TASCA,
				taskId);
		registre.setMissatge("Esborrar variable '" + varName + "'");
		return registreRepository.save(registre);
	}

	public Registre crearRegistreCrearDocumentInstanciaProces(
			Long expedientId,
			String processInstanceId,
			String responsableCodi,
			String documentCodi,
			String valor) {
		Registre registre = new Registre(
				new Date(),
				expedientId,
				responsableCodi,
				Registre.Accio.MODIFICAR,
				Registre.Entitat.INSTANCIA_PROCES,
				processInstanceId);
		registre.setMissatge("Crear document '" + documentCodi + "'");
		registre.setValorNou(valor);
		return registreRepository.save(registre);
	}
	public Registre crearRegistreModificarDocumentInstanciaProces(
			Long expedientId,
			String processInstanceId,
			String responsableCodi,
			String documentCodi,
			String valorVell,
			String valorNou) {
		Registre registre = new Registre(
				new Date(),
				expedientId,
				responsableCodi,
				Registre.Accio.MODIFICAR,
				Registre.Entitat.INSTANCIA_PROCES,
				processInstanceId);
		registre.setMissatge("Modificar document '" + documentCodi + "'");
		registre.setValorVell(valorVell);
		registre.setValorNou(valorNou);
		return registreRepository.save(registre);
	}
	public Registre crearRegistreEsborrarDocumentInstanciaProces(
			Long expedientId,
			String processInstanceId,
			String responsableCodi,
			String documentCodi) {
		Registre registre = new Registre(
				new Date(),
				expedientId,
				responsableCodi,
				Registre.Accio.MODIFICAR,
				Registre.Entitat.INSTANCIA_PROCES,
				processInstanceId);
		registre.setMissatge("Esborrar document '" + documentCodi + "'");
		return registreRepository.save(registre);
	}
	public Registre crearRegistreCrearDocumentTasca(
			Long expedientId,
			String taskId,
			String responsableCodi,
			String documentCodi,
			String nom) {
		Registre registre = new Registre(
				new Date(),
				expedientId,
				responsableCodi,
				Registre.Accio.MODIFICAR,
				Registre.Entitat.TASCA,
				taskId);
		registre.setMissatge("Crear document '" + documentCodi + "'");
		registre.setValorNou(nom);
		return registreRepository.save(registre);
	}
	public Registre crearRegistreModificarDocumentTasca(
			Long expedientId,
			String taskId,
			String responsableCodi,
			String documentCodi,
			String valorVell,
			String valorNou) {
		Registre registre = new Registre(
				new Date(),
				expedientId,
				responsableCodi,
				Registre.Accio.MODIFICAR,
				Registre.Entitat.TASCA,
				taskId);
		registre.setMissatge("Modificar document '" + documentCodi + "'");
		registre.setValorVell(valorVell);
		registre.setValorNou(valorNou);
		return registreRepository.save(registre);
	}
	public Registre crearRegistreEsborrarDocumentTasca(
			Long expedientId,
			String taskId,
			String responsableCodi,
			String documentCodi) {
		Registre registre = new Registre(
				new Date(),
				expedientId,
				responsableCodi,
				Registre.Accio.MODIFICAR,
				Registre.Entitat.TASCA,
				taskId);
		registre.setMissatge("Esborrar document '" + documentCodi + "'");
		return registreRepository.save(registre);
	}

	public Registre crearRegistreSignarDocument(
			String processInstanceId,
			String responsableCodi,
			String documentCodi) {
		Expedient expedient = expedientRepository.findByProcessInstanceId(processInstanceId);
		Registre registre = new Registre(
				new Date(),
				expedient.getId(),
				responsableCodi,
				Registre.Accio.MODIFICAR,
				Registre.Entitat.INSTANCIA_PROCES,
				processInstanceId);
		registre.setMissatge("Signatura del document '" + documentCodi + "'");
		return registreRepository.save(registre);
	}
	public Registre crearRegistreEsborrarSignatura(
			Long expedientId,
			String processInstanceId,
			String responsableCodi,
			String documentCodi) {
		Registre registre = new Registre(
				new Date(),
				expedientId,
				responsableCodi,
				Registre.Accio.MODIFICAR,
				Registre.Entitat.INSTANCIA_PROCES,
				processInstanceId);
		registre.setMissatge("Cancel·lació signatura del document '" + documentCodi + "'");
		return registreRepository.save(registre);
	}

	public Registre crearRegistreRedirigirToken(
			Long expedientId,
			String processInstanceId,
			String responsableCodi,
			String tokenName,
			String nodeOrigen,
			String nodeDesti) {
		Registre registre = new Registre(
				new Date(),
				expedientId,
				responsableCodi,
				Registre.Accio.MODIFICAR,
				Registre.Entitat.INSTANCIA_PROCES,
				processInstanceId);
		registre.setMissatge("Redirecció del token \"" + tokenName + "\": " + nodeOrigen + "->" + nodeDesti);
		return registreRepository.save(registre);
	}

	public Registre crearRegistreIniciarTermini(
			Long expedientId,
			String processInstanceId,
			String terminiId,
			String responsableCodi) {
		Registre registre = new Registre(
				new Date(),
				expedientId,
				responsableCodi,
				Registre.Accio.INICIAR,
				Registre.Entitat.TERMINI,
				getExpedientId(expedientId));
		registre.setProcessInstanceId(processInstanceId);
		return registreRepository.save(registre);
	}
	public Registre crearRegistreAturarTermini(
			Long expedientId,
			String processInstanceId,
			String terminiId,
			String responsableCodi) {
		Registre registre = new Registre(
				new Date(),
				expedientId,
				responsableCodi,
				Registre.Accio.ATURAR,
				Registre.Entitat.TERMINI,
				getExpedientId(expedientId));
		registre.setProcessInstanceId(processInstanceId);
		return registreRepository.save(registre);
	}
	public Registre crearRegistreReprendreTermini(
			Long expedientId,
			String processInstanceId,
			String terminiId,
			String responsableCodi) {
		Registre registre = new Registre(
				new Date(),
				expedientId,
				responsableCodi,
				Registre.Accio.REPRENDRE,
				Registre.Entitat.TERMINI,
				getExpedientId(expedientId));
		registre.setProcessInstanceId(processInstanceId);
		return registreRepository.save(registre);
	}
	public Registre crearRegistreCancelarTermini(
			Long expedientId,
			String processInstanceId,
			String terminiId,
			String responsableCodi) {
		Registre registre = new Registre(
				new Date(),
				expedientId,
				responsableCodi,
				Registre.Accio.CANCELAR,
				Registre.Entitat.TERMINI,
				getExpedientId(expedientId));
		registre.setProcessInstanceId(processInstanceId);
		return registreRepository.save(registre);
	}
	public Registre crearRegistreFinalitzarTermini(
			Long expedientId,
			String processInstanceId,
			String terminiId,
			String responsableCodi) {
		Registre registre = new Registre(
				new Date(),
				expedientId,
				responsableCodi,
				Registre.Accio.FINALITZAR,
				Registre.Entitat.TERMINI,
				getExpedientId(expedientId));
		registre.setProcessInstanceId(processInstanceId);
		return registreRepository.save(registre);
	}



	private String getExpedientId(Long id) {
		if (id != null)
			return id.toString();
		return null;
	}

	//private static final Log logger = LogFactory.getLog(ExpedientRegistreHelper.class);

}
