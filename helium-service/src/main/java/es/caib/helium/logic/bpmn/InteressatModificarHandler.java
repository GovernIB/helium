package es.caib.helium.logic.bpmn;

import es.caib.helium.disseny.api.HeliumApi;
import es.caib.helium.disseny.exception.HeliumHandlerException;
import lombok.Setter;

/**
 * Implementació del handler per a modificar interessats d'un expedient.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Setter
public class InteressatModificarHandler implements es.caib.helium.disseny.handler.InteressatModificarHandler {

	private String id;
	private String codi;
	private String varCodi;
	private String nom;
	private String varNom;
	private String nif;
	private String varNif;
	private String dir3Codi;
	private String varDir3Codi;
	private String llinatge1;
	private String varLlinatge1;
	private String llinatge2;
	private String varLlinatge2;
	private String tipus;
	private String varTipus;
	private String email;
	private String varEmail;
	private String telefon;
	private String varTelefon;
	private String entregaPostal;
	private String varEntregaPostal;
	private String entregaTipus;
	private String varEntregaTipus;
	private String linia1;
	private String varLinia1;
	private String linia2;
	private String varLinia2;
	private String entregaDeh;
	private String varEntregaDeh;
	private String entregaDehObligat;
	private String varEntregaDehObligat;
	private String tipusDocIdent;
	private String varTipusDocIdent;
	private String direccio;
	private String varDireccio;
	private String pais;
	private String varPais;
	private String provincia;
	private String varProvincia;
	private String municipi;
	private String varMunicipi;
	private String canalNotif;
	private String varCanalNotif;

	@Override
	public void execute(HeliumApi heliumApi) throws HeliumHandlerException {
		heliumApi.interessatModificar(
			id,
			heliumApi.getVariableDefaultValue(varCodi, codi),
			heliumApi.getVariableDefaultValue(varNom, nom),
			heliumApi.getVariableDefaultValue(varTipusDocIdent, tipusDocIdent),
			heliumApi.getVariableDefaultValue(varNif, nif),
			heliumApi.getVariableDefaultValue(varDir3Codi, dir3Codi),
			heliumApi.getVariableDefaultValue(varLlinatge1, llinatge1),
			heliumApi.getVariableDefaultValue(varLlinatge2, llinatge2),
			heliumApi.getVariableDefaultValue(varTipus, tipus),
			heliumApi.getVariableDefaultValue(varEmail, email),
			heliumApi.getVariableDefaultValue(varTelefon, telefon),
			heliumApi.getVariableDefaultValueAsBoolean(varEntregaPostal, entregaPostal),
			heliumApi.getVariableDefaultValue(varEntregaTipus, entregaTipus),
			heliumApi.getVariableDefaultValue(varLinia1, linia1),
			heliumApi.getVariableDefaultValue(varLinia2, linia2),
			heliumApi.getVariableDefaultValueAsBoolean(varEntregaDeh, entregaDeh),
			heliumApi.getVariableDefaultValueAsBoolean(varEntregaDehObligat, entregaDehObligat),
			heliumApi.getVariableDefaultValue(varDireccio, direccio),
			heliumApi.getVariableDefaultValue(varPais, pais),
			heliumApi.getVariableDefaultValue(varProvincia, provincia),
			heliumApi.getVariableDefaultValue(varMunicipi, municipi),
			heliumApi.getVariableDefaultValue(varCanalNotif, canalNotif));
	}

}
