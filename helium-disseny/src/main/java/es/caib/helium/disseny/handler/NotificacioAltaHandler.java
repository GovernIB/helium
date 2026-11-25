package es.caib.helium.disseny.handler;



/**
 * Handler per a interactuar amb el registre de sortida.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface NotificacioAltaHandler {
	public void setEmisorDir3Codi(String emisorDir3Codi);
	public void setEnviamentTipus(String enviamentTipus);
	public void setConcepte(String concepte);
	public void setDescripcio(String descripcio);
	public void setEnviamentDataProgramada(String enviamentDataProgramada);
	public void setRetard(String retard);
	public void setCaducitat(String caducitat);
	public void setDocument(String document);
	public void setProcedimentCodi(String procedimentCodi);
	public void setServeiTipus(String serveiTipus);
	public void setIdioma(String idioma);
	public void setTitularTipus(String titularTipus);
	public void setTitularNif(String titularNif);
	public void setTitularNom(String titularNom);
	public void setTitularLlin1(String titularLlin1);
	public void setTitularLlin2(String titularLlin2);
	public void setTitularEmail(String titularEmail);
	public void setTitularMobil(String titularMobil);
	public void setDestinatariNif(String destinatariNif);
	public void setDestinatariNom(String destinatariNom);
	public void setDestinatariLlin1(String destinatariLlin1);
	public void setDestinatariLlin2(String destinatariLlin2);
	public void setDestinatariEmail(String destinatariEmail);
	public void setDestinatariMobil(String destinatariMobil);
	public void setDestinatariTipus(String destinatariTipus);
	public void setEntregaPostalTipus(String entregaPostalTipus);
	public void setEntregaPostalViaTipus(String entregaPostalViaTipus);
	public void setEntregaPostalViaNom(String entregaPostalViaNom);
	public void setEntregaPostalNumeroCasa(String entregaPostalNumeroCasa);
	public void setEntregaPostalNumeroQualificador(String entregaPostalNumeroQualificador);
	public void setEntregaPostalPuntKm(String entregaPostalPuntKm);
	public void setEntregaPostalApartatCorreus(String entregaPostalApartatCorreus);
	public void setEntregaPostalPortal(String entregaPostalPortal);
	public void setEntregaPostalEscala(String entregaPostalEscala);
	public void setEntregaPostalPlanta(String entregaPostalPlanta);
	public void setEntregaPostalPorta(String entregaPostalPorta);
	public void setEntregaPostalBloc(String entregaPostalBloc);
	public void setEntregaPostalComplement(String entregaPostalComplement);
	public void setEntregaPostalCodiPostal(String entregaPostalCodiPostal);
	public void setEntregaPostalPoblacio(String entregaPostalPoblacio);
	public void setEntregaPostalMunicipiCodi(String entregaPostalMunicipiCodi);
	public void setEntregaPostalProvinciaCodi(String entregaPostalProvinciaCodi);
	public void setEntregaPostalPaisCodi(String entregaPostalPaisCodi);
	public void setEntregaPostalLinea1(String entregaPostalLinea1);
	public void setEntregaPostalLinea2(String entregaPostalLinea2);
	public void setEntregaPostalCie(String entregaPostalCie);
	public void setEntregaPostalFormatSobre(String entregaPostalFormatSobre);
	public void setEntregaPostalFormatFulla(String entregaPostalFormatFulla);
	public void setEntregaDehObligat(String entregaDehObligat);
	public void setEntregaDehProcedimentCodi(String entregaDehProcedimentCodi);
	void setGrupCodi(String grupCodi);
	void setTitularDir3Codi(String titularDir3Codi);
	void setDestinatariDir3Codi(String destinatariDir3Codi);
	void setEntregaPostalActiva(String entregaPostalActiva);
	void setEntregaDehActiva(String entregaDehActiva);
}
