package es.caib.helium.disseny.handler;



/**
 * Handler per a crear un nou interessat a l'expedient.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface InteressatCrearHandler {

	void setCodi(String codi);
	void setNom(String nom);
	void setNif(String nif);
	void setDir3Codi(String dir3Codi);
	void setLlinatge1(String llinatge1);
	void setLlinatge2(String llinatge2);
	void setTipus(String tipus);
	void setEmail(String email);
	void setTelefon(String telefon);
	void setEntregaPostal(String entregaPostal);
	void setEntregaTipus(String entregaTipus);
	void setLinia1(String linia1);
	void setLinia2(String linia2);
	void setEntregaDeh(String entregaDeh);
	void setEntregaDehObligat(String entregaDehObligat);
	void setTipusDocIdent(String tipusDocIdent);
	void setDireccio(String direccio);
	void setPais(String pais);
	void setProvincia(String provincia);
	void setMunicipi(String municipi);
	void setCanalNotif(String canalNotif);

}
