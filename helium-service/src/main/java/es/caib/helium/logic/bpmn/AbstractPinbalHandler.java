package es.caib.helium.logic.bpmn;

import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.exception.HeliumHandlerException;
import es.caib.helium.bpmn.handler.AbstractHeliumActionHandler;
import es.caib.helium.bpmn.model.ConsultaPinbal;
import lombok.Setter;

/**
 * Base dels handlers de consulta a PINBAL.
 *
 * <p>El titular es pot indicar amb el codi d'un interessat de l'expedient ({@code interessatCodi}) o amb les seves
 * dades; el tipus de documentació sempre s'ha d'indicar. El justificant es desa al document {@code documentCodi}.
 * {@code consentiment} és {@code SI} o {@code LLEI}.</p>
 *
 * <p>Si {@code asincrona} és {@code true}, el procés ha d'esperar la resposta en una <i>receive task</i> just després
 * de la tasca del handler: quan arriba, continua per {@code transicioOK} (id d'un flux de sortida de la
 * <i>receive task</i> o de l'activitat destí). Mentrestant el document conté un justificant provisional. Una consulta
 * síncrona no espera.</p>
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Setter
public abstract class AbstractPinbalHandler extends AbstractHeliumActionHandler {

	private String documentCodi;
	private String finalitat;
	private String consentiment;
	private String interessatCodi;
	private String titularTipusDocumentacio;
	private String titularDocumentacio;
	private String titularNom;
	private String titularLlinatge1;
	private String titularLlinatge2;
	private String asincrona;
	private String transicioOK;
	private String transicioKO;

	@Override
	public void execute(HeliumApi heliumApi) throws HeliumHandlerException {
		ConsultaPinbal consulta = new ConsultaPinbal();
		consulta.setServeiCodi(getServeiCodi());
		consulta.setDocumentCodi(text(documentCodi));
		consulta.setFinalitat(text(finalitat));
		consulta.setConsentiment(text(consentiment));
		consulta.setInteressatCodi(text(interessatCodi));
		consulta.setTitularTipusDocumentacio(text(titularTipusDocumentacio));
		consulta.setTitularDocumentacio(text(titularDocumentacio));
		consulta.setTitularNom(text(titularNom));
		consulta.setTitularLlinatge1(text(titularLlinatge1));
		consulta.setTitularLlinatge2(text(titularLlinatge2));
		consulta.setAsincrona(asBoolean(asincrona));
		consulta.setTransicioOK(text(transicioOK));
		consulta.setTransicioKO(text(transicioKO));
		completar(consulta);
		heliumApi.consultaPinbal(consulta);
	}

	/** Codi del servei de PINBAL que es consulta. */
	protected abstract String getServeiCodi();

	/** Dades pròpies del servei. */
	protected void completar(ConsultaPinbal consulta) {
	}

	protected String getTitularNom() {
		return text(titularNom);
	}

	protected static String text(String valor) {
		return isBuit(valor) ? null : valor.trim();
	}

}
