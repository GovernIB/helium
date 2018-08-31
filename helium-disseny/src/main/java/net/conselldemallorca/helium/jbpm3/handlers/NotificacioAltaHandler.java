package net.conselldemallorca.helium.jbpm3.handlers;

import java.util.Date;
import java.util.List;

import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per a interactuar amb el registre de sortida.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

@SuppressWarnings("serial")
public class NotificacioAltaHandler extends BasicActionHandler implements NotificacioAltaHandlerInterface {

	public NotificacioAltaHandler() {}

	public void execute(ExecutionContext executionContext) throws Exception {}
//	public void retrocedir(ExecutionContext context, List<String> parametres) throws Exception {}

	public void setEmisorDir3Codi(String emisorDir3Codi) {}
	public void setVarEmisorDir3Codi(String varEmisorDir3Codi) {}
	public void setEnviamentTipus(String enviamentTipus) {}
	public void setVarEnviamentTipus(String varEnviamentTipus) {}
	public void setConcepte(String concepte) {}
	public void setVarConcepte(String varConcepte) {}
	public void setDescripcio(String descripcio) {}
	public void setVarDescripcio(String varDescripcio) {}
	public void setEnviamentDataProgramada(String enviamentDataProgramada) {}
	public void setVarEnviamentDataProgramada(String varEnviamentDataProgramada) {}
	public void setRetard(String retard) {}
	public void setVarRetard(String varRetard) {}
	public void setCaducitat(String caducitat) {}
	public void setVarCaducitat(String varCaducitat) {}
	public void setDocument(String document) {}
	public void setVarDocument(String varDocument) {}
	public void setProcedimentCodi(String procedimentCodi) {}
	public void setVarProcedimentCodi(String varProcedimentCodi) {}
	public void setPagadorPostalDir3Codi(String pagadorPostalDir3Codi) {}
	public void setVarPagadorPostalDir3Codi(String varPagadorPostalDir3Codi) {}
	public void setPagadorPostalContracteNum(String pagadorPostalContracteNum) {}
	public void setVarPagadorPostalContracteNum(String varPagadorPostalContracteNum) {}
	public void setPagadorPostalContracteDataVigencia(String pagadorPostalContracteDataVigencia) {}
	public void setVarPagadorPostalContracteDataVigencia(String varPagadorPostalContracteDataVigencia) {}
	public void setPagadorPostalFacturacioClientCodi(String pagadorPostalFacturacioClientCodi) {}
	public void setVarPagadorPostalFacturacioClientCodi(String varPagadorPostalFacturacioClientCodi) {}
	public void setPagadorCieDir3Codi(String pagadorCieDir3Codi) {}
	public void setVarPagadorCieDir3Codi(String varPagadorCieDir3Codi) {}
	public void setPagadorCieContracteDataVigencia(String pagadorCieContracteDataVigencia) {}
	public void setVarPagadorCieContracteDataVigencia(String varPagadorCieContracteDataVigencia) {}
	public void setSeuProcedimentCodi(String seuProcedimentCodi) {}
	public void setVarSeuProcedimentCodi(String varSeuProcedimentCodi) {}
	public void setSeuExpedientSerieDocumental(String seuExpedientSerieDocumental) {}
	public void setVarSeuExpedientSerieDocumental(String varSeuExpedientSerieDocumental) {}
	public void setSeuExpedientUnitatOrganitzativa(String seuExpedientUnitatOrganitzativa) {}
	public void setVarSeuExpedientUnitatOrganitzativa(String varSeuExpedientUnitatOrganitzativa) {}
	public void setSeuExpedientIdentificadorEni(String seuExpedientIdentificadorEni) {}
	public void setVarSeuExpedientIdentificadorEni(String varSeuExpedientIdentificadorEni) {}
	public void setSeuExpedientTitol(String seuExpedientTitol) {}
	public void setVarSeuExpedientTitol(String varSeuExpedientTitol) {}
	public void setSeuRegistreOficina(String seuRegistreOficina) {}
	public void setVarSeuRegistreOficina(String varSeuRegistreOficina) {}
	public void setSeuRegistreLlibre(String seuRegistreLlibre) {}
	public void setVarSeuRegistreLlibre(String varSeuRegistreLlibre) {}
	public void setSeuRegistreOrgan(String seuRegistreOrgan) {}
	public void setVarSeuRegistreOrgan(String varSeuRegistreOrgan) {}
	public void setSeuIdioma(String seuIdioma) {}
	public void setVarSeuIdioma(String varSeuIdioma) {}
	public void setSeuAvisTitol(String seuAvisTitol) {}
	public void setVarSeuAvisTitol(String varSeuAvisTitol) {}
	public void setSeuAvisText(String seuAvisText) {}
	public void setVarSeuAvisText(String varSeuAvisText) {}
	public void setSeuAvisTextMobil(String seuAvisTextMobil) {}
	public void setVarSeuAvisTextMobil(String varSeuAvisTextMobil) {}
	public void setSeuOficiTitol(String seuOficiTitol) {}
	public void setVarSeuOficiTitol(String varSeuOficiTitol) {}
	public void setSeuOficiText(String seuOficiText) {}
	public void setVarSeuOficiText(String varSeuOficiText) {}
	public void setTitularNif(String titularNif) {}
	public void setVarTitularNif(String varTitularNif) {}
	public void setTitularNom(String titularNom) {}
	public void setVarTitularNom(String varTitularNom) {}
	public void setTitularLlin1(String titularLlin1) {}
	public void setVarTitularLlin1(String varTitularLlin1) {}
	public void setTitularLlin2(String titularLlin2) {}
	public void setVarTitularLlin2(String varTitularLlin2) {}
	public void setTitularEmail(String titularEmail) {}
	public void setVarTitularEmail(String varTitularEmail) {}
	public void setTitularMobil(String titularMobil) {}
	public void setVarTitularMobil(String varTitularMobil) {}
	public void setDestinatariNif(String destinatariNif) {}
	public void setVarDestinatariNif(String varDestinatariNif) {}
	public void setDestinatariNom(String destinatariNom) {}
	public void setVarDestinatariNom(String varDestinatariNom) {}
	public void setDestinatariLlin1(String destinatariLlin1) {}
	public void setVarDestinatariLlin1(String varDestinatariLlin1) {}
	public void setDestinatariLlin2(String destinatariLlin2) {}
	public void setVarDestinatariLlin2(String varDestinatariLlin2) {}
	public void setDestinatariEmail(String destinatariEmail) {}
	public void setVarDestinatariEmail(String varDestinatariEmail) {}
	public void setDestinatariMobil(String destinatariMobil) {}
	public void setVarDestinatariMobil(String varDestinatariMobil) {}
	public void setEntregaPostalTipus(String entregaPostalTipus) {}
	public void setVarEntregaPostalTipus(String varEntregaPostalTipus) {}
	public void setEntregaPostalViaTipus(String entregaPostalViaTipus) {}
	public void setVarEntregaPostalViaTipus(String varEntregaPostalViaTipus) {}
	public void setEntregaPostalViaNom(String entregaPostalViaNom) {}
	public void setVarEntregaPostalViaNom(String varEntregaPostalViaNom) {}
	public void setEntregaPostalNumeroCasa(String entregaPostalNumeroCasa) {}
	public void setVarEntregaPostalNumeroCasa(String varEntregaPostalNumeroCasa) {}
	public void setEntregaPostalNumeroQualificador(String entregaPostalNumeroQualificador) {}
	public void setVarEntregaPostalNumeroQualificador(String varEntregaPostalNumeroQualificador) {}
	public void setEntregaPostalPuntKm(String entregaPostalPuntKm) {}
	public void setVarEntregaPostalPuntKm(String varEntregaPostalPuntKm) {}
	public void setEntregaPostalApartatCorreus(String entregaPostalApartatCorreus) {}
	public void setVarEntregaPostalApartatCorreus(String varEntregaPostalApartatCorreus) {}
	public void setEntregaPostalPortal(String entregaPostalPortal) {}
	public void setVarEntregaPostalPortal(String varEntregaPostalPortal) {}
	public void setEntregaPostalEscala(String entregaPostalEscala) {}
	public void setVarEntregaPostalEscala(String varEntregaPostalEscala) {}
	public void setEntregaPostalPlanta(String entregaPostalPlanta) {}
	public void setVarEntregaPostalPlanta(String varEntregaPostalPlanta) {}
	public void setEntregaPostalPorta(String entregaPostalPorta) {}
	public void setVarEntregaPostalPorta(String varEntregaPostalPorta) {}
	public void setEntregaPostalBloc(String entregaPostalBloc) {}
	public void setVarEntregaPostalBloc(String varEntregaPostalBloc) {}
	public void setEntregaPostalComplement(String entregaPostalComplement) {}
	public void setVarEntregaPostalComplement(String varEntregaPostalComplement) {}
	public void setEntregaPostalCodiPostal(String entregaPostalCodiPostal) {}
	public void setVarEntregaPostalCodiPostal(String varEntregaPostalCodiPostal) {}
	public void setEntregaPostalPoblacio(String entregaPostalPoblacio) {}
	public void setVarEntregaPostalPoblacio(String varEntregaPostalPoblacio) {}
	public void setEntregaPostalMunicipiCodi(String entregaPostalMunicipiCodi) {}
	public void setVarEntregaPostalMunicipiCodi(String varEntregaPostalMunicipiCodi) {}
	public void setEntregaPostalProvinciaCodi(String entregaPostalProvinciaCodi) {}
	public void setVarEntregaPostalProvinciaCodi(String varEntregaPostalProvinciaCodi) {}
	public void setEntregaPostalPaisCodi(String entregaPostalPaisCodi) {}
	public void setVarEntregaPostalPaisCodi(String varEntregaPostalPaisCodi) {}
	public void setEntregaPostalLinea1(String entregaPostalLinea1) {}
	public void setVarEntregaPostalLinea1(String varEntregaPostalLinea1) {}
	public void setEntregaPostalLinea2(String entregaPostalLinea2) {}
	public void setVarEntregaPostalLinea2(String varEntregaPostalLinea2) {}
	public void setEntregaPostalCie(String entregaPostalCie) {}
	public void setVarEntregaPostalCie(String varEntregaPostalCie) {}
	public void setEntregaPostalFormatSobre(String entregaPostalFormatSobre) {}
	public void setVarEntregaPostalFormatSobre(String varEntregaPostalFormatSobre) {}
	public void setEntregaPostalFormatFulla(String entregaPostalFormatFulla) {}
	public void setVarEntregaPostalFormatFulla(String varEntregaPostalFormatFulla) {}
	public void setEntregaDehObligat(String entregaDehObligat) {}
	public void setVarEntregaDehObligat(String varEntregaDehObligat) {}
	public void setEntregaDehProcedimentCodi(String entregaDehProcedimentCodi) {}
	public void setVarEntregaDehProcedimentCodi(String varEntregaDehProcedimentCodi) {}
	
}
