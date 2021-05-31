
package net.conselldemallorca.helium.integracio.tramitacio;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.9-b14002
 * Generated source version: 2.2
 * 
 */
@WebService(name = "TramitacioService", targetNamespace = "http://tramitacio.integracio.helium.conselldemallorca.net/")
@XmlSeeAlso({
    net.conselldemallorca.helium.integracio.tramitacio.ObjectFactory.class,
    Object[].class, 
    Object[][].class}
)
public interface TramitacioService {

    /**
     * 
     * @param entornCodi
     * @param usuariCodi
     * @param expedientTipusCodi
     * @param numero
     * @param titol
     * @param valorsFormulari
     * @return processInstanceId de l'expedient creat
     * @throws TramitacioException_Exception
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "iniciExpedient", targetNamespace = "http://tramitacio.integracio.helium.conselldemallorca.net/", className = "net.conselldemallorca.helium.integracio.tramitacio.IniciExpedient")
    @ResponseWrapper(localName = "iniciExpedientResponse", targetNamespace = "http://tramitacio.integracio.helium.conselldemallorca.net/", className = "net.conselldemallorca.helium.integracio.tramitacio.IniciExpedientResponse")
    public String iniciExpedient(
        @WebParam(name = "arg0", targetNamespace = "")
        String entornCodi,
        @WebParam(name = "arg1", targetNamespace = "")
        String usuariCodi,
        @WebParam(name = "arg2", targetNamespace = "")
        String expedientTipusCodi,
        @WebParam(name = "arg3", targetNamespace = "")
        String numero,
        @WebParam(name = "arg4", targetNamespace = "")
        String titol,
        @WebParam(name = "arg5", targetNamespace = "")
        List<ParellaCodiValor> valorsFormulari)
        throws TramitacioException_Exception
    ;

    /**
     * 
     * @param entornCodi
     * @param usuariCodi
     * @return llista de tasques personals
     * @throws TramitacioException_Exception
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "consultaTasquesPersonals", targetNamespace = "http://tramitacio.integracio.helium.conselldemallorca.net/", className = "net.conselldemallorca.helium.integracio.tramitacio.ConsultaTasquesPersonals")
    @ResponseWrapper(localName = "consultaTasquesPersonalsResponse", targetNamespace = "http://tramitacio.integracio.helium.conselldemallorca.net/", className = "net.conselldemallorca.helium.integracio.tramitacio.ConsultaTasquesPersonalsResponse")
    public List<TascaTramitacio> consultaTasquesPersonals(
        @WebParam(name = "arg0", targetNamespace = "")
        String entornCodi,
        @WebParam(name = "arg1", targetNamespace = "")
        String usuariCodi)
        throws TramitacioException_Exception
    ;

    /**
     * 
     * @param entornCodi
     * @param usuariCodi
     * @return llista de de grup
     * @throws TramitacioException_Exception
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "consultaTasquesGrup", targetNamespace = "http://tramitacio.integracio.helium.conselldemallorca.net/", className = "net.conselldemallorca.helium.integracio.tramitacio.ConsultaTasquesGrup")
    @ResponseWrapper(localName = "consultaTasquesGrupResponse", targetNamespace = "http://tramitacio.integracio.helium.conselldemallorca.net/", className = "net.conselldemallorca.helium.integracio.tramitacio.ConsultaTasquesGrupResponse")
    public List<TascaTramitacio> consultaTasquesGrup(
    	@WebParam(name = "arg0", targetNamespace = "")
        String entornCodi,
        @WebParam(name = "arg1", targetNamespace = "")
        String usuariCodi)
        throws TramitacioException_Exception
    ;

    /**
     * 
     * @param entornCodi
     * @param usuariCodi
     * @param expedientNumero
     * @return llista de tasques personals
     * @throws TramitacioException_Exception
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "consultaTasquesPersonalsByCodi", targetNamespace = "http://tramitacio.integracio.helium.conselldemallorca.net/", className = "net.conselldemallorca.helium.integracio.tramitacio.ConsultaTasquesPersonalsByCodi")
    @ResponseWrapper(localName = "consultaTasquesPersonalsByCodiResponse", targetNamespace = "http://tramitacio.integracio.helium.conselldemallorca.net/", className = "net.conselldemallorca.helium.integracio.tramitacio.ConsultaTasquesPersonalsByCodiResponse")
    public List<TascaTramitacio> consultaTasquesPersonalsByCodi(
    	@WebParam(name = "arg0", targetNamespace = "")
        String entornCodi,
        @WebParam(name = "arg1", targetNamespace = "")
        String usuariCodi,
        @WebParam(name = "arg2", targetNamespace = "")
        String expedientNumero)
        throws TramitacioException_Exception
    ;

    /**
     * 
     * @param entornCodi
     * @param usuariCodi
     * @param expedientNumero
     * @return llista de de grup
     * @throws TramitacioException_Exception
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "consultaTasquesGrupByCodi", targetNamespace = "http://tramitacio.integracio.helium.conselldemallorca.net/", className = "net.conselldemallorca.helium.integracio.tramitacio.ConsultaTasquesGrupByCodi")
    @ResponseWrapper(localName = "consultaTasquesGrupByCodiResponse", targetNamespace = "http://tramitacio.integracio.helium.conselldemallorca.net/", className = "net.conselldemallorca.helium.integracio.tramitacio.ConsultaTasquesGrupByCodiResponse")
    public List<TascaTramitacio> consultaTasquesGrupByCodi(
    	@WebParam(name = "arg0", targetNamespace = "")
        String entornCodi,
        @WebParam(name = "arg1", targetNamespace = "")
        String usuariCodi,
        @WebParam(name = "arg2", targetNamespace = "")
        String expedientNumero)
        throws TramitacioException_Exception
    ;

	/**
	 * 
	 * @param entornCodi
	 * @param usuariCodi
	 * @param processInstanceId
	 * @return El llistat de tasques
	 * @throws TramitacioException
	 */
	public List<TascaTramitacio> consultaTasquesPersonalsByProces(
		@WebParam(name = "arg0", targetNamespace = "")
		String entornCodi,
		@WebParam(name = "arg1", targetNamespace = "")
		String usuariCodi,
		@WebParam(name = "arg2", targetNamespace = "")
		String processInstanceId)
		throws TramitacioException_Exception
	;

	/**
	 * 
	 * @param entornCodi
	 * @param usuariCodi
	 * @param processInstanceId
	 * @return El llistat de tasques
	 * @throws TramitacioException
	 */
	public List<TascaTramitacio> consultaTasquesGrupByProces(
		@WebParam(name = "arg0", targetNamespace = "")
		String entornCodi,
		@WebParam(name = "arg1", targetNamespace = "")
		String usuariCodi,
		@WebParam(name = "arg2", targetNamespace = "")
		String processInstanceId)
		throws TramitacioException_Exception
	;

    /**
     * 
     * @param entornCodi
     * @param usuariCodi
     * @param tascaId
     * @throws TramitacioException_Exception
     */
    @WebMethod
    @RequestWrapper(localName = "agafarTasca", targetNamespace = "http://tramitacio.integracio.helium.conselldemallorca.net/", className = "net.conselldemallorca.helium.integracio.tramitacio.AgafarTasca")
    @ResponseWrapper(localName = "agafarTascaResponse", targetNamespace = "http://tramitacio.integracio.helium.conselldemallorca.net/", className = "net.conselldemallorca.helium.integracio.tramitacio.AgafarTascaResponse")
    public void agafarTasca(
        @WebParam(name = "arg0", targetNamespace = "")
        String entornCodi,
        @WebParam(name = "arg1", targetNamespace = "")
        String usuariCodi,
        @WebParam(name = "arg2", targetNamespace = "")
        String tascaId)
        throws TramitacioException_Exception
    ;

    /**
     * 
     * @param entornCodi
     * @param usuariCodi
     * @param tascaId
     * @throws TramitacioException_Exception
     */
    @WebMethod
    @RequestWrapper(localName = "alliberarTasca", targetNamespace = "http://tramitacio.integracio.helium.conselldemallorca.net/", className = "net.conselldemallorca.helium.integracio.tramitacio.AlliberarTasca")
    @ResponseWrapper(localName = "alliberarTascaResponse", targetNamespace = "http://tramitacio.integracio.helium.conselldemallorca.net/", className = "net.conselldemallorca.helium.integracio.tramitacio.AlliberarTascaResponse")
    public void alliberarTasca(
    	@WebParam(name = "arg0", targetNamespace = "")
    	String entornCodi,
    	@WebParam(name = "arg1", targetNamespace = "")
    	String usuariCodi,
    	@WebParam(name = "arg2", targetNamespace = "")
    	String tascaId)
        throws TramitacioException_Exception
    ;

    /**
     * 
     * @param entornCodi
     * @param usuariCodi
     * @param tascaId
     * @return llista de camps del formulari de la tasca
     * @throws TramitacioException_Exception
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "consultaFormulariTasca", targetNamespace = "http://tramitacio.integracio.helium.conselldemallorca.net/", className = "net.conselldemallorca.helium.integracio.tramitacio.ConsultaFormulariTasca")
    @ResponseWrapper(localName = "consultaFormulariTascaResponse", targetNamespace = "http://tramitacio.integracio.helium.conselldemallorca.net/", className = "net.conselldemallorca.helium.integracio.tramitacio.ConsultaFormulariTascaResponse")
    public List<CampTasca> consultaFormulariTasca(
    	@WebParam(name = "arg0", targetNamespace = "")
        String entornCodi,
        @WebParam(name = "arg1", targetNamespace = "")
        String usuariCodi,
        @WebParam(name = "arg2", targetNamespace = "")
        String tascaId)
        throws TramitacioException_Exception
    ;

    /**
     * 
     * @param entornCodi
     * @param usuariCodi
     * @param tascaId
     * @param valorsFormulari
     * @throws TramitacioException_Exception
     */
    @WebMethod
    @RequestWrapper(localName = "setDadesFormulariTasca", targetNamespace = "http://tramitacio.integracio.helium.conselldemallorca.net/", className = "net.conselldemallorca.helium.integracio.tramitacio.SetDadesFormulariTasca")
    @ResponseWrapper(localName = "setDadesFormulariTascaResponse", targetNamespace = "http://tramitacio.integracio.helium.conselldemallorca.net/", className = "net.conselldemallorca.helium.integracio.tramitacio.SetDadesFormulariTascaResponse")
    public void setDadesFormulariTasca(
    	@WebParam(name = "arg0", targetNamespace = "")
        String entornCodi,
        @WebParam(name = "arg1", targetNamespace = "")
        String usuariCodi,
        @WebParam(name = "arg2", targetNamespace = "")
        String tascaId,
        @WebParam(name = "arg3", targetNamespace = "")
        List<ParellaCodiValor> valorsFormulari)
        throws TramitacioException_Exception
    ;

    /**
     * 
     * @param entornCodi
     * @param usuariCodi
     * @param tascaId
     * @return llista de documents de la tasca
     * @throws TramitacioException_Exception
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "consultaDocumentsTasca", targetNamespace = "http://tramitacio.integracio.helium.conselldemallorca.net/", className = "net.conselldemallorca.helium.integracio.tramitacio.ConsultaDocumentsTasca")
    @ResponseWrapper(localName = "consultaDocumentsTascaResponse", targetNamespace = "http://tramitacio.integracio.helium.conselldemallorca.net/", className = "net.conselldemallorca.helium.integracio.tramitacio.ConsultaDocumentsTascaResponse")
    public List<DocumentTasca> consultaDocumentsTasca(
    	@WebParam(name = "arg0", targetNamespace = "")
        String entornCodi,
        @WebParam(name = "arg1", targetNamespace = "")
        String usuariCodi,
        @WebParam(name = "arg2", targetNamespace = "")
        String tascaId)
        throws TramitacioException_Exception
    ;

    /**
     * 
     * @param entornCodi
     * @param usuariCodi
     * @param tascaId
     * @param documentCodi
     * @param arxiuNom
     * @param documentData
     * @param arxiuContingut
     * @throws TramitacioException_Exception
     */
    @WebMethod
    @RequestWrapper(localName = "setDocumentTasca", targetNamespace = "http://tramitacio.integracio.helium.conselldemallorca.net/", className = "net.conselldemallorca.helium.integracio.tramitacio.SetDocumentTasca")
    @ResponseWrapper(localName = "setDocumentTascaResponse", targetNamespace = "http://tramitacio.integracio.helium.conselldemallorca.net/", className = "net.conselldemallorca.helium.integracio.tramitacio.SetDocumentTascaResponse")
    public void setDocumentTasca(
    	@WebParam(name = "arg0", targetNamespace = "")
        String entornCodi,
        @WebParam(name = "arg1", targetNamespace = "")
        String usuariCodi,
        @WebParam(name = "arg2", targetNamespace = "")
        String tascaId,
        @WebParam(name = "arg3", targetNamespace = "")
        String documentCodi,
        @WebParam(name = "arg4", targetNamespace = "")
        String arxiuNom,
        @WebParam(name = "arg5", targetNamespace = "")
        XMLGregorianCalendar documentData,
        @WebParam(name = "arg6", targetNamespace = "")
        byte[] arxiuContingut)
        throws TramitacioException_Exception
    ;

    /**
     * 
     * @param entornCodi
     * @param usuariCodi
     * @param tascaId
     * @param documentCodi
     * @throws TramitacioException_Exception
     */
    @WebMethod
    @RequestWrapper(localName = "esborrarDocumentTasca", targetNamespace = "http://tramitacio.integracio.helium.conselldemallorca.net/", className = "net.conselldemallorca.helium.integracio.tramitacio.EsborrarDocumentTasca")
    @ResponseWrapper(localName = "esborrarDocumentTascaResponse", targetNamespace = "http://tramitacio.integracio.helium.conselldemallorca.net/", className = "net.conselldemallorca.helium.integracio.tramitacio.EsborrarDocumentTascaResponse")
    public void esborrarDocumentTasca(
    	@WebParam(name = "arg0", targetNamespace = "")
        String entornCodi,
        @WebParam(name = "arg1", targetNamespace = "")
        String usuariCodi,
        @WebParam(name = "arg2", targetNamespace = "")
        String tascaId,
        @WebParam(name = "arg3", targetNamespace = "")
        String documentCodi)
        throws TramitacioException_Exception
    ;

    /**
     * 
     * @param entornCodi
     * @param usuariCodi
     * @param tascaId
     * @param transicio
     * @throws TramitacioException_Exception
     */
    @WebMethod
    @RequestWrapper(localName = "finalitzarTasca", targetNamespace = "http://tramitacio.integracio.helium.conselldemallorca.net/", className = "net.conselldemallorca.helium.integracio.tramitacio.FinalitzarTasca")
    @ResponseWrapper(localName = "finalitzarTascaResponse", targetNamespace = "http://tramitacio.integracio.helium.conselldemallorca.net/", className = "net.conselldemallorca.helium.integracio.tramitacio.FinalitzarTascaResponse")
    public void finalitzarTasca(
    	@WebParam(name = "arg0", targetNamespace = "")
        String entornCodi,
        @WebParam(name = "arg1", targetNamespace = "")
        String usuariCodi,
        @WebParam(name = "arg2", targetNamespace = "")
        String tascaId,
        @WebParam(name = "arg3", targetNamespace = "")
        String transicio)
        throws TramitacioException_Exception
    ;

    /**
     * 
     * @param entornCodi
     * @param processInstanceId
     * @throws TramitacioException_Exception
     */
	public ExpedientInfo getExpedientInfo(
		@WebParam(name = "arg0", targetNamespace = "")
		String entornCodi,
		@WebParam(name = "arg1", targetNamespace = "")
		String usuariCodi,
		@WebParam(name = "arg2", targetNamespace = "")
		String processInstanceId)
		throws TramitacioException_Exception
	;

    /**
     * 
     * @param entornCodi
     * @param usuariCodi
     * @param processInstanceId
     * @return llista de variables de la instància de procés
     * @return
     *     returns java.util.List<net.conselldemallorca.helium.integracio.tramitacio.CampProces>
     * @throws TramitacioException_Exception
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "consultarVariablesProces", targetNamespace = "http://tramitacio.integracio.helium.conselldemallorca.net/", className = "net.conselldemallorca.helium.integracio.tramitacio.ConsultarVariablesProces")
    @ResponseWrapper(localName = "consultarVariablesProcesResponse", targetNamespace = "http://tramitacio.integracio.helium.conselldemallorca.net/", className = "net.conselldemallorca.helium.integracio.tramitacio.ConsultarVariablesProcesResponse")
    public List<CampProces> consultarVariablesProces(
    	@WebParam(name = "arg0", targetNamespace = "")
        String entornCodi,
        @WebParam(name = "arg1", targetNamespace = "")
        String usuariCodi,
        @WebParam(name = "arg2", targetNamespace = "")
        String processInstanceId)
        throws TramitacioException_Exception
    ;

    /**
     * 
     * @param entornCodi
     * @param usuariCodi
     * @param processInstanceId
     * @param varCodi
     * @param varValor
     * @throws TramitacioException_Exception
     */
    @WebMethod
    @RequestWrapper(localName = "setVariableProces", targetNamespace = "http://tramitacio.integracio.helium.conselldemallorca.net/", className = "net.conselldemallorca.helium.integracio.tramitacio.SetVariableProces")
    @ResponseWrapper(localName = "setVariableProcesResponse", targetNamespace = "http://tramitacio.integracio.helium.conselldemallorca.net/", className = "net.conselldemallorca.helium.integracio.tramitacio.SetVariableProcesResponse")
    public void setVariableProces(
    	@WebParam(name = "arg0", targetNamespace = "")
        String entornCodi,
        @WebParam(name = "arg1", targetNamespace = "")
        String usuariCodi,
        @WebParam(name = "arg2", targetNamespace = "")
        String processInstanceId,
        @WebParam(name = "arg3", targetNamespace = "")
        String varCodi,
        @WebParam(name = "arg4", targetNamespace = "")
        Object varValor)
        throws TramitacioException_Exception
    ;


    /**
     * 
     * @param entornCodi
     * @param usuariCodi
     * @param processInstanceId
     * @param varCodi
     * @throws TramitacioException_Exception
     */
    @WebMethod
    @RequestWrapper(localName = "esborrarVariableProces", targetNamespace = "http://tramitacio.integracio.helium.conselldemallorca.net/", className = "net.conselldemallorca.helium.integracio.tramitacio.EsborrarVariableProces")
    @ResponseWrapper(localName = "esborrarVariableProcesResponse", targetNamespace = "http://tramitacio.integracio.helium.conselldemallorca.net/", className = "net.conselldemallorca.helium.integracio.tramitacio.EsborrarVariableProcesResponse")
    public void esborrarVariableProces(
        @WebParam(name = "arg0", targetNamespace = "")
        String entornCodi,
        @WebParam(name = "arg1", targetNamespace = "")
        String usuariCodi,
        @WebParam(name = "arg2", targetNamespace = "")
        String processInstanceId,
        @WebParam(name = "arg3", targetNamespace = "")
        String varCodi)
        throws TramitacioException_Exception
    ;

    /**
     * 
     * @param entornCodi
     * @param usuariCodi
     * @param processInstanceId
     * @return la llista de documents de la instància de procés
     * @throws TramitacioException_Exception
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "consultarDocumentsProces", targetNamespace = "http://tramitacio.integracio.helium.conselldemallorca.net/", className = "net.conselldemallorca.helium.integracio.tramitacio.ConsultarDocumentsProces")
    @ResponseWrapper(localName = "consultarDocumentsProcesResponse", targetNamespace = "http://tramitacio.integracio.helium.conselldemallorca.net/", className = "net.conselldemallorca.helium.integracio.tramitacio.ConsultarDocumentsProcesResponse")
    public List<DocumentProces> consultarDocumentsProces(
    	@WebParam(name = "arg0", targetNamespace = "")
        String entornCodi,
        @WebParam(name = "arg1", targetNamespace = "")
        String usuariCodi,
        @WebParam(name = "arg2", targetNamespace = "")
        String processInstanceId)
        throws TramitacioException_Exception
    ;

    /**
     * 
     * @param documentId
     * @return l'arxiu associat al document
     * @throws TramitacioException_Exception
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getArxiuProces", targetNamespace = "http://tramitacio.integracio.helium.conselldemallorca.net/", className = "net.conselldemallorca.helium.integracio.tramitacio.GetArxiuProces")
    @ResponseWrapper(localName = "getArxiuProcesResponse", targetNamespace = "http://tramitacio.integracio.helium.conselldemallorca.net/", className = "net.conselldemallorca.helium.integracio.tramitacio.GetArxiuProcesResponse")
    public ArxiuProces getArxiuProces(
        @WebParam(name = "arg0", targetNamespace = "")
        Long documentId)
        throws TramitacioException_Exception
    ;

    /**
     * 
     * @param entornCodi
     * @param usuariCodi
     * @param processInstanceId
     * @param documentCodi
     * @param arxiuNom
     * @param documentData
     * @param arxiuContingut
     * @return documentId del document creat
     * @throws TramitacioException_Exception
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "setDocumentProces", targetNamespace = "http://tramitacio.integracio.helium.conselldemallorca.net/", className = "net.conselldemallorca.helium.integracio.tramitacio.SetDocumentProces")
    @ResponseWrapper(localName = "setDocumentProcesResponse", targetNamespace = "http://tramitacio.integracio.helium.conselldemallorca.net/", className = "net.conselldemallorca.helium.integracio.tramitacio.SetDocumentProcesResponse")
    public Long setDocumentProces(
    	@WebParam(name = "arg0", targetNamespace = "")
        String entornCodi,
        @WebParam(name = "arg1", targetNamespace = "")
        String usuariCodi,
        @WebParam(name = "arg2", targetNamespace = "")
        String processInstanceId,
        @WebParam(name = "arg3", targetNamespace = "")
        String documentCodi,
        @WebParam(name = "arg4", targetNamespace = "")
        String arxiuNom,
        @WebParam(name = "arg5", targetNamespace = "")
        XMLGregorianCalendar documentData,
        @WebParam(name = "arg6", targetNamespace = "")
        byte[] arxiuContingut)
        throws TramitacioException_Exception
    ;

    /**
     * 
     * @param entornCodi
     * @param usuariCodi
     * @param processInstanceId
     * @param documentCodi
     * @throws TramitacioException_Exception
     */
    @WebMethod
    @RequestWrapper(localName = "esborrarDocumentProces", targetNamespace = "http://tramitacio.integracio.helium.conselldemallorca.net/", className = "net.conselldemallorca.helium.integracio.tramitacio.EsborrarDocumentProces")
    @ResponseWrapper(localName = "esborrarDocumentProcesResponse", targetNamespace = "http://tramitacio.integracio.helium.conselldemallorca.net/", className = "net.conselldemallorca.helium.integracio.tramitacio.EsborrarDocumentProcesResponse")
    public void esborrarDocumentProces(
    	@WebParam(name = "arg0", targetNamespace = "")
        String entornCodi,
        @WebParam(name = "arg1", targetNamespace = "")
        String usuariCodi,
        @WebParam(name = "arg2", targetNamespace = "")
        String processInstanceId,
        @WebParam(name = "arg3", targetNamespace = "")
        String documentCodi)
        throws TramitacioException_Exception
    ;

    /**
     * 
     * @param entornCodi
     * @param usuariCodi
     * @param processInstanceId
     * @param accio
     * @throws TramitacioException_Exception
     */
    @WebMethod
    @RequestWrapper(localName = "executarAccioProces", targetNamespace = "http://tramitacio.integracio.helium.conselldemallorca.net/", className = "net.conselldemallorca.helium.integracio.tramitacio.ExecutarAccioProces")
    @ResponseWrapper(localName = "executarAccioProcesResponse", targetNamespace = "http://tramitacio.integracio.helium.conselldemallorca.net/", className = "net.conselldemallorca.helium.integracio.tramitacio.ExecutarAccioProcesResponse")
    public void executarAccioProces(
    	@WebParam(name = "arg0", targetNamespace = "")
        String entornCodi,
        @WebParam(name = "arg1", targetNamespace = "")
        String usuariCodi,
        @WebParam(name = "arg2", targetNamespace = "")
        String processInstanceId,
        @WebParam(name = "arg3", targetNamespace = "")
        String accio)
        throws TramitacioException_Exception
    ;

    /**
     * 
     * @param entornCodi
     * @param usuariCodi
     * @param processInstanceId
     * @param script
     * @throws TramitacioException_Exception
     */
    @WebMethod
    @RequestWrapper(localName = "executarScriptProces", targetNamespace = "http://tramitacio.integracio.helium.conselldemallorca.net/", className = "net.conselldemallorca.helium.integracio.tramitacio.ExecutarScriptProces")
    @ResponseWrapper(localName = "executarScriptProcesResponse", targetNamespace = "http://tramitacio.integracio.helium.conselldemallorca.net/", className = "net.conselldemallorca.helium.integracio.tramitacio.ExecutarScriptProcesResponse")
    public void executarScriptProces(
    	@WebParam(name = "arg0", targetNamespace = "")
        String entornCodi,
        @WebParam(name = "arg1", targetNamespace = "")
        String usuariCodi,
        @WebParam(name = "arg2", targetNamespace = "")
        String processInstanceId,
        @WebParam(name = "arg3", targetNamespace = "")
        String script)
        throws TramitacioException_Exception
    ;

    /**
     * 
     * @param entornCodi
     * @param usuariCodi
     * @param processInstanceId
     * @param motiu
     * @throws TramitacioException_Exception
     */
    @WebMethod
    @RequestWrapper(localName = "aturarExpedient", targetNamespace = "http://tramitacio.integracio.helium.conselldemallorca.net/", className = "net.conselldemallorca.helium.integracio.tramitacio.AturarExpedient")
    @ResponseWrapper(localName = "aturarExpedientResponse", targetNamespace = "http://tramitacio.integracio.helium.conselldemallorca.net/", className = "net.conselldemallorca.helium.integracio.tramitacio.AturarExpedientResponse")
    public void aturarExpedient(
    	@WebParam(name = "arg0", targetNamespace = "")
        String entornCodi,
        @WebParam(name = "arg1", targetNamespace = "")
        String usuariCodi,
        @WebParam(name = "arg2", targetNamespace = "")
        String processInstanceId,
        @WebParam(name = "arg3", targetNamespace = "")
        String motiu)
        throws TramitacioException_Exception
    ;

    /**
     * 
     * @param entornCodi
     * @param usuariCodi
     * @param processInstanceId
     * @throws TramitacioException_Exception
     */
    @WebMethod
    @RequestWrapper(localName = "reprendreExpedient", targetNamespace = "http://tramitacio.integracio.helium.conselldemallorca.net/", className = "net.conselldemallorca.helium.integracio.tramitacio.ReprendreExpedient")
    @ResponseWrapper(localName = "reprendreExpedientResponse", targetNamespace = "http://tramitacio.integracio.helium.conselldemallorca.net/", className = "net.conselldemallorca.helium.integracio.tramitacio.ReprendreExpedientResponse")
    public void reprendreExpedient(
    	@WebParam(name = "arg0", targetNamespace = "")
        String entornCodi,
        @WebParam(name = "arg1", targetNamespace = "")
        String usuariCodi,
        @WebParam(name = "arg2", targetNamespace = "")
        String processInstanceId)
        throws TramitacioException_Exception
    ;

    /**
     * 
     * @param entornCodi
     * @param usuariCodi
     * @param titol
     * @param numero
     * @param dataInici1
     * @param dataInici2
     * @param expedientTipusCodi
     * @param estatCodi
     * @param iniciat
     * @param finalitzat
     * @param geoPosX
     * @param geoPosY
     * @param geoReferencia
     * @return llista d'expedients trobats
     * @throws TramitacioException_Exception
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "consultaExpedients", targetNamespace = "http://tramitacio.integracio.helium.conselldemallorca.net/", className = "net.conselldemallorca.helium.integracio.tramitacio.ConsultaExpedients")
    @ResponseWrapper(localName = "consultaExpedientsResponse", targetNamespace = "http://tramitacio.integracio.helium.conselldemallorca.net/", className = "net.conselldemallorca.helium.integracio.tramitacio.ConsultaExpedientsResponse")
    public List<ExpedientInfo> consultaExpedients(
        @WebParam(name = "arg0", targetNamespace = "")
        String entornCodi,
        @WebParam(name = "arg1", targetNamespace = "")
        String usuariCodi,
        @WebParam(name = "arg2", targetNamespace = "")
        String titol,
        @WebParam(name = "arg3", targetNamespace = "")
        String numero,
        @WebParam(name = "arg4", targetNamespace = "")
        XMLGregorianCalendar dataInici1,
        @WebParam(name = "arg5", targetNamespace = "")
        XMLGregorianCalendar dataInici2,
        @WebParam(name = "arg6", targetNamespace = "")
        String expedientTipusCodi,
        @WebParam(name = "arg7", targetNamespace = "")
        String estatCodi,
        @WebParam(name = "arg8", targetNamespace = "")
        boolean iniciat,
        @WebParam(name = "arg9", targetNamespace = "")
        boolean finalitzat,
        @WebParam(name = "arg10", targetNamespace = "")
        Double geoPosX,
        @WebParam(name = "arg11", targetNamespace = "")
        Double geoPosY,
        @WebParam(name = "arg12", targetNamespace = "")
        String geoReferencia)
        throws TramitacioException_Exception
    ;

    /**
     * 
     * @param entornCodi
     * @param usuariCodi
     * @param processInstanceId
     * @throws TramitacioException_Exception
     */
    @WebMethod
    @RequestWrapper(localName = "deleteExpedient", targetNamespace = "http://tramitacio.integracio.helium.conselldemallorca.net/", className = "net.conselldemallorca.helium.integracio.tramitacio.DeleteExpedient")
    @ResponseWrapper(localName = "deleteExpedientResponse", targetNamespace = "http://tramitacio.integracio.helium.conselldemallorca.net/", className = "net.conselldemallorca.helium.integracio.tramitacio.DeleteExpedientResponse")
    public void deleteExpedient(
        @WebParam(name = "arg0", targetNamespace = "")
        String entornCodi,
        @WebParam(name = "arg1", targetNamespace = "")
        String usuariCodi,
        @WebParam(name = "arg2", targetNamespace = "")
        String processInstanceId)
        throws TramitacioException_Exception
    ;

}
