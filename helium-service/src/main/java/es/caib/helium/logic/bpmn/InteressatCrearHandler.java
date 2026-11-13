package es.caib.helium.logic.bpmn;

import es.caib.helium.bpmn.handler.AbstractHeliumActionHandler;
import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.exception.HeliumHandlerException;
import org.flowable.common.engine.api.delegate.Expression;
import lombok.Setter;

/**
 * Implementació del handler per a crear un nou interessat a l'expedient.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Setter
public class InteressatCrearHandler extends AbstractHeliumActionHandler implements es.caib.helium.disseny.handler.InteressatCrearHandler {

	private Expression codi;
	private Expression varCodi;
	private Expression nom;
	private Expression varNom;
	private Expression nif;
	private Expression varNif;
	private Expression dir3Codi;
	private Expression varDir3Codi;
	private Expression llinatge1;
	private Expression varLlinatge1;
	private Expression llinatge2;
	private Expression varLlinatge2;
	private Expression tipus;
	private Expression varTipus;
	private Expression email;
	private Expression varEmail;
	private Expression telefon;
	private Expression varTelefon;
	private Expression entregaPostal;
	private Expression varEntregaPostal;
	private Expression entregaTipus;
	private Expression varEntregaTipus;
	private Expression linia1;
	private Expression varLinia1;
	private Expression linia2;
	private Expression varLinia2;
	private Expression entregaDeh;
	private Expression varEntregaDeh;
	private Expression entregaDehObligat;
	private Expression varEntregaDehObligat;
	private Expression tipusDocIdent;
	private Expression varTipusDocIdent;
	private Expression direccio;
	private Expression varDireccio;
	private Expression pais;
	private Expression varPais;
	private Expression provincia;
	private Expression varProvincia;
	private Expression municipi;
	private Expression varMunicipi;
	private Expression canalNotif;
	private Expression varCanalNotif;

	@Override
	public void execute(HeliumApi heliumApi) throws HeliumHandlerException {
		heliumApi.interessatCrear(
			heliumApi.getVariableDefaultValue(getValue(varCodi), getValue(codi)),
			heliumApi.getVariableDefaultValue(getValue(varNom), getValue(nom)),
			heliumApi.getVariableDefaultValue(getValue(varTipusDocIdent), getValue(tipusDocIdent)),
			heliumApi.getVariableDefaultValue(getValue(varNif), getValue(nif)),
			heliumApi.getVariableDefaultValue(getValue(varDir3Codi), getValue(dir3Codi)),
			heliumApi.getVariableDefaultValue(getValue(varLlinatge1), getValue(llinatge1)),
			heliumApi.getVariableDefaultValue(getValue(varLlinatge2), getValue(llinatge2)),
			heliumApi.getVariableDefaultValue(getValue(varTipus), getValue(tipus)),
			heliumApi.getVariableDefaultValue(getValue(varEmail), getValue(email)),
			heliumApi.getVariableDefaultValue(getValue(varTelefon), getValue(telefon)),
			heliumApi.getVariableDefaultValueAsBoolean(getValue(varEntregaPostal), getValueAsBoolean(entregaPostal)),
			heliumApi.getVariableDefaultValue(getValue(varEntregaTipus), getValue(entregaTipus)),
			heliumApi.getVariableDefaultValue(getValue(varLinia1), getValue(linia1)),
			heliumApi.getVariableDefaultValue(getValue(varLinia2), getValue(linia2)),
			heliumApi.getVariableDefaultValueAsBoolean(getValue(varEntregaDeh), getValueAsBoolean(entregaDeh)),
			heliumApi.getVariableDefaultValueAsBoolean(getValue(varEntregaDehObligat), getValueAsBoolean(entregaDehObligat)),
			heliumApi.getVariableDefaultValue(getValue(varDireccio), getValue(direccio)),
			heliumApi.getVariableDefaultValue(getValue(varPais), getValue(pais)),
			heliumApi.getVariableDefaultValue(getValue(varProvincia), getValue(provincia)),
			heliumApi.getVariableDefaultValue(getValue(varMunicipi), getValue(municipi)),
			heliumApi.getVariableDefaultValue(getValue(varCanalNotif), getValue(canalNotif))
		);
	}

}
