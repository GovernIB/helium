package es.caib.helium.disseny.handler;


import org.flowable.common.engine.api.delegate.Expression;

/**
 * Handler per a crear un nou interessat a l'expedient.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface InteressatCrearHandler {

	void setCodi(Expression codi);
	void setVarCodi(Expression varCodi);
	void setNom(Expression nom);
	void setVarNom(Expression varNom);
	void setNif(Expression nif);
	void setVarNif(Expression varNif);
	void setDir3Codi(Expression dir3Codi);
	void setVarDir3Codi(Expression varDir3Codi);
	void setLlinatge1(Expression llinatge1);
	void setVarLlinatge1(Expression varLlinatge1);
	void setLlinatge2(Expression llinatge2);
	void setVarLlinatge2(Expression varLlinatge2);
	void setTipus(Expression tipus);
	void setVarTipus(Expression varTipus);
	void setEmail(Expression email);
	void setVarEmail(Expression varEmail);
	void setTelefon(Expression telefon);
	void setVarTelefon(Expression varTelefon);
	void setEntregaPostal(Expression entregaPostal);
	void setVarEntregaPostal(Expression varEntregaPostal);
	void setEntregaTipus(Expression entregaTipus);
	void setVarEntregaTipus(Expression varEntregaTipus);
	void setLinia1(Expression linia1);
	void setVarLinia1(Expression varLinia1);
	void setLinia2(Expression linia2);
	void setVarLinia2(Expression varLinia2);
	void setEntregaDeh(Expression entregaDeh);
	void setVarEntregaDeh(Expression varEntregaDeh);
	void setEntregaDehObligat(Expression entregaDehObligat);
	void setVarEntregaDehObligat(Expression varEntregaDehObligat);
	void setTipusDocIdent(Expression tipusDocIdent);
	void setVarTipusDocIdent(Expression varTipusDocIdent);
	void setDireccio(Expression direccio);
	void setVarDireccio(Expression varDireccio);
	void setPais(Expression pais);
	void setVarPais(Expression varPais);
	void setProvincia(Expression provincia);
	void setVarProvincia(Expression varProvincia);
	void setMunicipi(Expression municipi);
	void setVarMunicipi(Expression varMunicipi);
	void setCanalNotif(Expression canalNotif);
	void setVarCanalNotif(Expression varCanalNotif);

}
