/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per a interactuar amb el registre de sortida.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface NotificacioAltaHandlerInterface extends ActionHandler {

	public void execute(ExecutionContext executionContext) throws Exception;

	public void setEmisorDir3Codi(String emisorDir3Codi); 
	public void setVarEmisorDir3Codi(String varEmisorDir3Codi); 
	public void setEnviamentTipus(String enviamentTipus); 
	public void setVarEnviamentTipus(String varEnviamentTipus); 
	public void setConcepte(String concepte); 
	public void setVarConcepte(String varConcepte); 
	public void setDescripcio(String descripcio); 
	public void setVarDescripcio(String varDescripcio); 
	public void setEnviamentDataProgramada(String enviamentDataProgramada); 
	public void setVarEnviamentDataProgramada(String varEnviamentDataProgramada); 
	public void setRetard(String retard); 
	public void setVarRetard(String varRetard); 
	public void setCaducitat(String caducitat); 
	public void setVarCaducitat(String varCaducitat); 
	public void setDocument(String document); 
	public void setVarDocument(String varDocument); 
	public void setProcedimentCodi(String procedimentCodi); 
	public void setVarProcedimentCodi(String varProcedimentCodi); 
	public void setServeiTipus(String serveiTipus); 
	public void setVarServeiTipus(String varServeiTipus); 
	public void setTitularTipus(String titularTipus); 
	public void setVarTitularTipus(String varTitularTipus); 
	public void setTitularNif(String titularNif); 
	public void setVarTitularNif(String varTitularNif); 
	public void setTitularNom(String titularNom); 
	public void setVarTitularNom(String varTitularNom); 
	public void setTitularLlin1(String titularLlin1); 
	public void setVarTitularLlin1(String varTitularLlin1); 
	public void setTitularLlin2(String titularLlin2); 
	public void setVarTitularLlin2(String varTitularLlin2); 
	public void setTitularEmail(String titularEmail); 
	public void setVarTitularEmail(String varTitularEmail); 
	public void setTitularMobil(String titularMobil); 
	public void setVarTitularMobil(String varTitularMobil); 
	public void setDestinatariNif(String destinatariNif); 
	public void setVarDestinatariNif(String varDestinatariNif); 
	public void setDestinatariNom(String destinatariNom); 
	public void setVarDestinatariNom(String varDestinatariNom); 
	public void setDestinatariLlin1(String destinatariLlin1); 
	public void setVarDestinatariLlin1(String varDestinatariLlin1); 
	public void setDestinatariLlin2(String destinatariLlin2); 
	public void setVarDestinatariLlin2(String varDestinatariLlin2); 
	public void setDestinatariEmail(String destinatariEmail); 
	public void setVarDestinatariEmail(String varDestinatariEmail); 
	public void setDestinatariMobil(String destinatariMobil); 
	public void setVarDestinatariMobil(String varDestinatariMobil); 
	public void setDestinatariTipus(String destinatariTipus);
	public void setVarDestinatariTipus(String varDestinatariTipus);
	public void setEntregaPostalTipus(String entregaPostalTipus); 
	public void setVarEntregaPostalTipus(String varEntregaPostalTipus); 
	public void setEntregaPostalViaTipus(String entregaPostalViaTipus); 
	public void setVarEntregaPostalViaTipus(String varEntregaPostalViaTipus); 
	public void setEntregaPostalViaNom(String entregaPostalViaNom); 
	public void setVarEntregaPostalViaNom(String varEntregaPostalViaNom); 
	public void setEntregaPostalNumeroCasa(String entregaPostalNumeroCasa); 
	public void setVarEntregaPostalNumeroCasa(String varEntregaPostalNumeroCasa); 
	public void setEntregaPostalNumeroQualificador(String entregaPostalNumeroQualificador); 
	public void setVarEntregaPostalNumeroQualificador(String varEntregaPostalNumeroQualificador); 
	public void setEntregaPostalPuntKm(String entregaPostalPuntKm); 
	public void setVarEntregaPostalPuntKm(String varEntregaPostalPuntKm); 
	public void setEntregaPostalApartatCorreus(String entregaPostalApartatCorreus); 
	public void setVarEntregaPostalApartatCorreus(String varEntregaPostalApartatCorreus); 
	public void setEntregaPostalPortal(String entregaPostalPortal); 
	public void setVarEntregaPostalPortal(String varEntregaPostalPortal); 
	public void setEntregaPostalEscala(String entregaPostalEscala); 
	public void setVarEntregaPostalEscala(String varEntregaPostalEscala); 
	public void setEntregaPostalPlanta(String entregaPostalPlanta); 
	public void setVarEntregaPostalPlanta(String varEntregaPostalPlanta); 
	public void setEntregaPostalPorta(String entregaPostalPorta); 
	public void setVarEntregaPostalPorta(String varEntregaPostalPorta); 
	public void setEntregaPostalBloc(String entregaPostalBloc); 
	public void setVarEntregaPostalBloc(String varEntregaPostalBloc); 
	public void setEntregaPostalComplement(String entregaPostalComplement); 
	public void setVarEntregaPostalComplement(String varEntregaPostalComplement); 
	public void setEntregaPostalCodiPostal(String entregaPostalCodiPostal); 
	public void setVarEntregaPostalCodiPostal(String varEntregaPostalCodiPostal); 
	public void setEntregaPostalPoblacio(String entregaPostalPoblacio); 
	public void setVarEntregaPostalPoblacio(String varEntregaPostalPoblacio); 
	public void setEntregaPostalMunicipiCodi(String entregaPostalMunicipiCodi); 
	public void setVarEntregaPostalMunicipiCodi(String varEntregaPostalMunicipiCodi); 
	public void setEntregaPostalProvinciaCodi(String entregaPostalProvinciaCodi); 
	public void setVarEntregaPostalProvinciaCodi(String varEntregaPostalProvinciaCodi); 
	public void setEntregaPostalPaisCodi(String entregaPostalPaisCodi); 
	public void setVarEntregaPostalPaisCodi(String varEntregaPostalPaisCodi); 
	public void setEntregaPostalLinea1(String entregaPostalLinea1); 
	public void setVarEntregaPostalLinea1(String varEntregaPostalLinea1); 
	public void setEntregaPostalLinea2(String entregaPostalLinea2); 
	public void setVarEntregaPostalLinea2(String varEntregaPostalLinea2); 
	public void setEntregaPostalCie(String entregaPostalCie); 
	public void setVarEntregaPostalCie(String varEntregaPostalCie); 
	public void setEntregaPostalFormatSobre(String entregaPostalFormatSobre); 
	public void setVarEntregaPostalFormatSobre(String varEntregaPostalFormatSobre); 
	public void setEntregaPostalFormatFulla(String entregaPostalFormatFulla); 
	public void setVarEntregaPostalFormatFulla(String varEntregaPostalFormatFulla); 
	public void setEntregaDehObligat(String entregaDehObligat); 
	public void setVarEntregaDehObligat(String varEntregaDehObligat); 
	public void setEntregaDehProcedimentCodi(String entregaDehProcedimentCodi); 
	public void setVarEntregaDehProcedimentCodi(String varEntregaDehProcedimentCodi); 
		
}
