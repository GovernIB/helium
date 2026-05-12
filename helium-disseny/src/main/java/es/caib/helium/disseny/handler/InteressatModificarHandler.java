package es.caib.helium.disseny.handler;

/**
 * Handler per a modificar interessats a l'expedient.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface InteressatModificarHandler extends HeliumActionHandler {

	void setId(String id);
	void setCodi(String codi);
	void setVarCodi(String varCodi);
	void setNom(String nom);
	void setVarNom(String varNom);
	void setNif(String nif);
	void setVarNif(String varNif);
	void setDir3Codi(String dir3Codi);
	void setVarDir3Codi(String varDir3Codi);
	void setLlinatge1(String llinatge1);
	void setVarLlinatge1(String varLlinatge1);
	void setLlinatge2(String llinatge2);
	void setVarLlinatge2(String varLlinatge2);
	void setTipus(String tipus);
	void setVarTipus(String varTipus);
	void setEmail(String email);
	void setVarEmail(String varEmail);
	void setTelefon(String telefon);
	void setVarTelefon(String varTelefon);
	void setEntregaPostal(String entregaPostal);
	void setVarEntregaPostal(String varEntregaPostal);
	void setEntregaTipus(String entregaTipus);
	void setVarEntregaTipus(String varEntregaTipus);
	void setLinia1(String linia1);
	void setVarLinia1(String varLinia1);
	void setLinia2(String linia2);
	void setVarLinia2(String varLinia2);
	void setEntregaDeh(String entregaDeh);
	void setVarEntregaDeh(String varEntregaDeh);
	void setEntregaDehObligat(String entregaDehObligat);
	void setVarEntregaDehObligat(String varEntregaDehObligat);
	void setTipusDocIdent(String tipusDocIdent);
	void setVarTipusDocIdent(String varTipusDocIdent);
	void setDireccio(String direccio);
	void setVarDireccio(String varDireccio);
	void setPais(String pais);
	void setVarPais(String varPais);
	void setProvincia(String provincia);
	void setVarProvincia(String varProvincia);
	void setMunicipi(String municipi);
	void setVarMunicipi(String varMunicipi);
	void setCanalNotif(String canalNotif);
	void setVarCanalNotif(String varCanalNotif);

}
