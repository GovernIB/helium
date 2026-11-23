package es.caib.helium.logic.bpmn;

import es.caib.helium.bpmn.handler.AbstractHeliumActionHandler;
import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.exception.HeliumHandlerException;
import lombok.Setter;

/**
 * Implementació del handler per a crear un nou interessat a l'expedient.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Setter
public class InteressatCrearHandler extends AbstractHeliumActionHandler implements es.caib.helium.disseny.handler.InteressatCrearHandler {

	private String codi;
	private String nom;
	private String nif;
	private String dir3Codi;
	private String llinatge1;
	private String llinatge2;
	private String tipus;
	private String email;
	private String telefon;
	private String entregaPostal;
	private String entregaTipus;
	private String linia1;
	private String linia2;
	private String entregaDeh;
	private String entregaDehObligat;
	private String tipusDocIdent;
	private String direccio;
	private String pais;
	private String provincia;
	private String municipi;
	private String canalNotif;

	@Override
	public void execute(HeliumApi heliumApi) throws HeliumHandlerException {
		heliumApi.interessatCrear(
			codi,
			nom,
			tipusDocIdent,
			nif,
			dir3Codi,
			llinatge1,
			llinatge2,
			tipus,
			email,
			telefon,
			asBoolean(entregaPostal),
			entregaTipus,
			linia1,
			linia2,
			asBoolean(entregaDeh),
			asBoolean(entregaDehObligat),
			direccio,
			pais,
			provincia,
			municipi,
			canalNotif
		);
	}

}
