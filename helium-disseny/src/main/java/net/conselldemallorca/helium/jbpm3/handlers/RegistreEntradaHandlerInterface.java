/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per a interactuar amb el registre d'entrada.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface RegistreEntradaHandlerInterface extends ActionHandler {

	public void execute(ExecutionContext executionContext) throws Exception;

	public void setOficina(String oficina);
	public void setVarOficina(String varOficina);
	public void setOficinaFisica(String oficinaFisica);
	public void setVarOficinaFisica(String varOficinaFisica);
	public void setRemitentCodiEntitat(String remitentCodiEntitat);
	public void setVarRemitentCodiEntitat(String varRemitentCodiEntitat);
	public void setRemitentNomEntitat(String remitentNomEntitat);
	public void setVarRemitentNomEntitat(String varRemitentNomEntitat);
	public void setRemitentCodiGeografic(String remitentCodiGeografic);
	public void setVarRemitentCodiGeografic(String varRemitentCodiGeografic);
	public void setRemitentNomGeografic(String remitentNomGeografic);
	public void setVarRemitentNomGeografic(String varRemitentNomGeografic);
	public void setRemitentRegistreNumero(String remitentRegistreNumero);
	public void setVarRemitentRegistreNumero(String varRemitentRegistreNumero);
	public void setRemitentRegistreAny(String remitentRegistreAny);
	public void setVarRemitentRegistreAny(String varRemitentRegistreAny);
	public void setDestinatariCodiEntitat(String destinatariCodiEntitat);
	public void setVarDestinatariCodiEntitat(String varDestinatariCodiEntitat);
	public void setDestinatariNomEntitat(String destinatariNomEntitat);
	public void setVarDestinatariNomEntitat(String varDestinatariNomEntitat);
	public void setDestinatariCodiGeografic(String destinatariCodiGeografic);
	public void setVarDestinatariCodiGeografic(String varDestinatariCodiGeografic);
	public void setDestinatariNomGeografic(String destinatariNomGeografic);
	public void setVarDestinatariNomGeografic(String varDestinatariNomGeografic);
	public void setDestinatariRegistreNumero(String destinatariRegistreNumero);
	public void setVarDestinatariRegistreNumero(String varDestinatariRegistreNumero);
	public void setDestinatariRegistreAny(String destinatariRegistreAny);
	public void setVarDestinatariRegistreAny(String varDestinatariRegistreAny);
	public void setDocumentTipus(String documentTipus);
	public void setVarDocumentTipus(String varDocumentTipus);
	public void setDocumentIdiomaDocument(String documentIdiomaDocument);
	public void setVarDocumentIdiomaDocument(String varDocumentIdiomaDocument);
	public void setDocumentIdiomaExtracte(String documentIdiomaExtracte);
	public void setVarDocumentIdiomaExtracte(String varDocumentIdiomaExtracte);
	public void setVarDocument(String varDocument);
	public void setVarNumeroRegistre(String varNumeroRegistre);
	public void setVarNumeroAnyRegistre(String varNumeroAnyRegistre);
	public void setVarDataRegistre(String varDataRegistre);
	public void setVarData(String varData);

}
