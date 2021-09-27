package es.caib.helium.client.engine.bridge;

import es.caib.helium.client.engine.model.CampTascaRest;
import es.caib.helium.client.engine.model.DocumentTasca;
import es.caib.helium.client.engine.model.Reassignacio;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class WorkflowBridgeClientServiceImpl implements WorkflowBridgeClientService {

    private final WorkflowBridgeFeignClient workflowBridgeFeignClient;

//    @Autowired
//    ExpedientsHelper expedientsHelper;
//    @Autowired
//    TasquesHelper tasquesHelper;
//    @Autowired
//    DocumentsHelper documentsHelper;
//    @Autowired
//    DefinicioProcesHelper definicioProcesHelper;
//    @Autowired
//    AlertesHelper alertesHelper;
//    @Autowired
//    EnumeracionsHelper enumeracionsHelper;
//    @Autowired
//    EstatsHelper estatsHelper;
//    @Autowired
//    DominisHelper dominisHelper;
//    @Autowired
//    InteressatsHelper interessatsHelper;
//    @Autowired
//    TerminisHelper terminisHelper;
//    @Autowired
//    VariablesHelper variablesHelper;
//    @Autowired
//    GenericsHelper genericsHelper;
//
//    @Autowired
//    CommandService commandService;
//    @Autowired
//    WorkflowEngineApi workflowEngineApi;


    // EXPEDIENTS
    ////////////////////////////////////////////////////////////////////////////////


//    /**
//     * Realitza una consulta per a obtenir tots els expedients que compleixen amb el filtre indicat
//     *
//     * @param entornId Identificador de l'entorn. Valor obligatori.
//     * @param titol Títol de l'expedient. Es pot indicar només una part del títol.
//     * @param numero Número de l'expedient. Es pot indicar només una part del número.
//     * @param dataInici1 Data igual o anterior a l'inici de l'expedient.
//     * @param dataInici2 Data igual o posterior a l'inici de l'expedient.
//     * @param expedientTipusId Identificador del tipus d'expedient.
//     * @param estatId Identificador de l'estat de l'expedient.
//     * @param nomesIniciats Indica si només es volen obtenir els expedients que no es trobin finalitzats. Valor obligatori.
//     * @param nomesFinalitzats Indica si només es volen obtenir els expedients que es trobin finalitzats. Valor obligatori.
//     * @return Llista d'expedients segons el filtre indicat.
//     * @exception NoTrobatException en el cas que no existeixi un entorn o estat amb els identificadors indicats (si s'han informat)
//     */
//    @Override
//    public List<ExpedientInfoDto> findExpedientsConsultaGeneral(
//            Long entornId,
//            String titol,
//            String numero,
//            Date dataInici1,
//            Date dataInici2,
//            Long expedientTipusId,
//            Long estatId,
//            boolean nomesIniciats,
//            boolean nomesFinalitzats) {
//        return expedientsHelper.findExpedientsConsultaGeneral(
//                entornId,
//                titol,
//                numero,
//                dataInici1,
//                dataInici2,
//                expedientTipusId,
//                estatId,
//                nomesIniciats,
//                nomesFinalitzats);
//    }
//
//    /**
//     * Consulta un expedient donat el seu entorn, tipus i número
//     * @param entornId Identificador de l'entorn.
//     * @param expedientTipusCodi Codi del tipus d'expedient.
//     * @param numero Número de l'expedient.
//     * @return Expedient amb els paràmetres indicats. Null en cas que no es trobi cap expedient.
//     * @exception NoTrobatException en el cas que no existeixi un entorn o tipus d'expedient amb els valors indicats
//     */
//    @Override
//    public ExpedientDto getExpedientAmbEntornITipusINumero(
//            Long entornId,
//            String expedientTipusCodi,
//            String numero) {
//        return expedientsHelper.getExpedientAmbEntornITipusINumero(
//                entornId,
//                expedientTipusCodi,
//                numero);
//    }
//
//    @Override
//    public String getProcessInstanceIdAmbEntornITipusINumero(
//            Long entornId,
//            String expedientTipusCodi,
//            String numero) {
//        return expedientsHelper.getProcessInstanceIdAmbEntornITipusINumero(
//                entornId,
//                expedientTipusCodi,
//                numero);
//    }
//
//    // Mètode utilitzat només pel AbstractHeliumActionHandler --> GetExpedientActual
//    @Override
//    public ExpedientDto getExpedientArrelAmbProcessInstanceId(
//            String processInstanceId) {
//        return expedientsHelper.getExpedientArrelAmbProcessInstanceId(processInstanceId);
//    }
//
//    @Override
//    public void expedientRelacionar(
//            Long expedientIdOrigen,
//            Long expedientIdDesti) {
//        expedientsHelper.expedientRelacionar(expedientIdOrigen, expedientIdDesti);
//    }
//
//    @Override
//    public void expedientAturar(
//            String processInstanceId,
//            String motiu) {
//
//        workflowEngineApi.suspendProcessInstances(getExpedientProcessInstances(processInstanceId));
//        expedientsHelper.expedientAturar(processInstanceId, motiu);
//    }
//
//    @Override
//    public void expedientReprendre(String processInstanceId) {
//
//        workflowEngineApi.resumeProcessInstances(getExpedientProcessInstances(processInstanceId));
//        expedientsHelper.expedientReprendre(processInstanceId);
//    }
//
//    @Override
//    public void expedientModificarEstat(
//            String processInstanceId,
//            String estatCodi) {
//        expedientsHelper.expedientModificarEstat(processInstanceId, estatCodi);
//    }
//
//    @Override
//    public void expedientModificarEstat(String processInstanceId, Long estatId) {
//        expedientsHelper.expedientModificarEstat(processInstanceId, estatId);
//    }
//
//    @Override
//    public void expedientModificarComentari(
//            String processInstanceId,
//            String comentari) {
//        expedientsHelper.expedientModificarComentari(processInstanceId, comentari);
//    }
//
//    @Override
//    public void expedientModificarNumero(
//            String processInstanceId,
//            String numero) {
//        expedientsHelper.expedientModificarNumero(processInstanceId, numero);
//    }
//
//    @Override
//    public void expedientModificarTitol(
//            String processInstanceId,
//            String titol) {
//        expedientsHelper.expedientModificarTitol(processInstanceId, titol);
//    }
//
//    @Override
//    public void expedientModificarGeoref(
//            String processInstanceId,
//            Double posx,
//            Double posy,
//            String referencia) {
//        Georeferencia georeferencia = Georeferencia.builder()
//                .posx(posx)
//                .posy(posy)
//                .referencia(referencia).build();
//        expedientsHelper.expedientModificarGeoref(processInstanceId, georeferencia);
//    }
//
//    @Override
//    public void expedientModificarGeoreferencia(
//            String processInstanceId,
//            String referencia) {
//        expedientsHelper.expedientModificarGeoreferencia(processInstanceId, referencia);
//    }
//
//    @Override
//    public void expedientModificarGeoX(
//            String processInstanceId,
//            Double posx) {
//        expedientsHelper.expedientModificarGeoX(processInstanceId, posx);
//    }
//
//    @Override
//    public void expedientModificarGeoY(
//            String processInstanceId,
//            Double posy) {
//        expedientsHelper.expedientModificarGeoY(processInstanceId, posy);
//    }
//
//    @Override
//    public void expedientModificarDataInici(
//            String processInstanceId,
//            Date dataInici) {
//        expedientsHelper.expedientModificarDataInici(processInstanceId, dataInici);
//    }
//
//    @Override
//    public void expedientModificarGrup(
//            String processInstanceId,
//            String grupCodi) {
//        expedientsHelper.expedientModificarGrup(processInstanceId, grupCodi);
//    }
//
//    @Override
//    public void expedientModificarResponsable(
//            String processInstanceId,
//            String responsableCodi) {
//
//        if (getPersonaAmbCodi(responsableCodi) == null)
//            throw new NoTrobatException(PersonaDto .class, responsableCodi);
//
//        expedientsHelper.expedientModificarResponsable(processInstanceId, responsableCodi);
//    }
//
//    @Override
//    public void finalitzarExpedient(String processInstanceId) {
//
//        Date dataFinalitzacio = new Date();
//        workflowEngineApi.finalitzarExpedient(
//                getExpedientProcessInstances(processInstanceId),
//                dataFinalitzacio);
//        expedientsHelper.finalitzarExpedient(processInstanceId, dataFinalitzacio);
//    }
//
//    @Override
//    public void desfinalitzarExpedient(String processInstanceId) {
//        workflowEngineApi.desfinalitzarExpedient(processInstanceId);
//        expedientsHelper.desfinalitzarExpedient(processInstanceId);
//    }
//
//    @Override
//    public void updateExpedientError(
//            Long jobId,
//            Long expedientId,
//            String errorDesc,
//            String errorFull) {
//        ExpedientError expedientError = ExpedientError.builder()
//                .jobId(jobId)
//                .errorDesc(errorDesc)
//                .errorFull(errorFull)
//                .build();
//        expedientsHelper.updateExpedientError(expedientId, expedientError);
//    }
//
//    @Override
//    public List<ExpedientInfoDto> findExpedientsConsultaDadesIndexades(
//            Long entornId,
//            String expedientTipusCodi,
//            Map<String, Object> filtreValors) {
//
//        // Convertir Map<String, Object> a Map<String, String>
//        return expedientsHelper.findExpedientsConsultaDades(
//                entornId,
//                expedientTipusCodi,
//                convertVariablesMap(filtreValors));
//    }
//
//    @Override
//    public String getDadaPerProcessInstance(
//            String processInstanceId,
//            String varCodi) {
//        return expedientsHelper.getDadaPerProcessInstance(processInstanceId, varCodi);
//    }


    // TASQUES
    ////////////////////////////////////////////////////////////////////////////////

////    @Override
////    public boolean isTascaEnSegonPla(Long taskId) {
////        return tasquesHelper.isTascaEnSegonPla(taskId);
////    }
//
//    @Override
//    public void addMissatgeExecucioTascaSegonPla(Long taskId, String[] message) {
//        tasquesHelper.addMissatgeExecucioTascaSegonPla(taskId, message);
//    }
//
//    @Override
//    public void setErrorTascaSegonPla(Long taskId, Exception ex) {
//        String error = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
//        tasquesHelper.setErrorTascaSegonPla(taskId, error);
//    }

    @Override
    public List<CampTascaRest> findCampsPerTaskInstance(
            String processInstanceId,
            String processDefinitionId,
            String taskName) {
        var responseEntity = workflowBridgeFeignClient.findCampsPerTaskInstance(
                processInstanceId,
                processDefinitionId,
                taskName);
        return responseEntity.getBody();
    }

    @Override
    public List<DocumentTasca> findDocumentsPerTaskInstance(
            String processInstanceId,
            String processDefinitionId,
            String taskName) {
        var responseEntity = workflowBridgeFeignClient.findDocumentsPerTaskInstance(
                processInstanceId,
                processDefinitionId,
                taskName);
        return responseEntity.getBody();
    }

//    @Override
//    public String getDadaPerTaskInstance(String processInstanceId, String taskInstanceId, String varCodi) {
//        return tasquesHelper.getDadaPerTaskInstance(
//                processInstanceId,
//                taskInstanceId,
//                varCodi);
//    }
//
//    // TODO: Utilitzada en retroacció. Només necessita saber si existeix
//    @Override
//    public CampTascaDto getCampTascaPerInstanciaTasca(
//            String taskName,
//            String processDefinitionId,
//            String processInstanceId,
//            String name) {
//        return tasquesHelper.getCampTascaPerInstanciaTasca(
//                taskName,
//                processDefinitionId,
//                processInstanceId,
//                name);
//    }
//
//
//    // TODO: LOCAL --> updateTaskInstanceInfoCache o eliminar????
//    @Override
//    public void createDadesTasca(Long taskId) {
//
//    }
//
//    // DOCUMENTS
//    ////////////////////////////////////////////////////////////////////////////////
//
//
//    @Override
//    public DocumentDissenyDto getDocumentDisseny(
//            Long definicioProcesId,
//            String processInstanceId,
//            String documentCodi) {
//        return documentsHelper.getDocumentDisseny(
//                definicioProcesId,
//                processInstanceId,
//                documentCodi);
//    }
//
//    // TODO: Retornar el documentInfo
//    @Override
//    public DocumentDto getDocumentInfo(Long documentStoreId) {
//        return documentsHelper.getDocumentInfo(documentStoreId);
//    }
//
//    // TODO: Retornar el documentInfo
//    @Override
//    public DocumentDto getDocumentInfo(
//            Long documentStoreId,
//            boolean ambContingutOriginal,
//            boolean ambContingutSignat,
//            boolean ambContingutVista,
//            boolean perSignar,
//            boolean perNotificar,
//            boolean ambSegellSignatura) {
//        return documentsHelper.getDocumentInfo(
//                documentStoreId,
//                ambContingutOriginal,
//                ambContingutSignat,
//                ambContingutVista,
//                perSignar,
//                perNotificar,
//                ambSegellSignatura);
//    }
//
//    @Override
//    public String getCodiVariablePerDocumentCodi(String documentCodi) {
//        return JbpmVars.PREFIX_DOCUMENT + documentCodi;
////        return documentsHelper.getCodiVariablePerDocumentCodi(documentCodi);
//    }
//
//    @Override
//    public ArxiuDto getArxiuPerMostrar(Long documentStoreId) {
//        return documentsHelper.getArxiuPerMostrar(documentStoreId);
//    }
//
//    @Override
//    public ArxiuDto documentGenerarAmbPlantilla(
//            String taskInstanceId,
//            String processDefinitionId,
//            String processInstanceId,
//            String documentCodi,
//            Date dataDocument) {
//        DocumentGenerar documentGenerar = DocumentGenerar.builder()
//                .taskInstanceId(taskInstanceId)
//                .processDefinitionId(processDefinitionId)
//                .processInstanceId(processInstanceId)
//                .dataDocument(dataDocument).build();
//        return documentsHelper.documentGenerarAmbPlantilla(documentCodi, documentGenerar);
//    }
//
//    @Override
//    public Long documentExpedientCrear(
//            String taskInstanceId,
//            String processInstanceId,
//            String documentCodi,
//            Date documentData,
//            boolean isAdjunt,
//            String adjuntTitol,
//            String arxiuNom,
//            byte[] arxiuContingut) {
//        DocumentCrear documentCrear = DocumentCrear.builder()
//                .taskInstanceId(taskInstanceId)
//                .processInstanceId(processInstanceId)
//                .documentData(documentData)
//                .isAdjunt(isAdjunt)
//                .adjuntTitol(adjuntTitol)
//                .arxiuNom(arxiuNom)
//                .arxiuContingut(arxiuContingut).build();
//        return documentsHelper.documentExpedientCrear(documentCodi, documentCrear);
//    }
//
//    @Override
//    public Long documentExpedientGuardar(
//            String processInstanceId,
//            String documentCodi,
//            Date data,
//            String arxiuNom,
//            byte[] arxiuContingut) {
//        DocumentGuardar documentGuardar = DocumentGuardar.builder()
//                .processInstanceId(processInstanceId)
//                .data(data)
//                .arxiuNom(arxiuNom)
//                .arxiuContingut(arxiuContingut).build();
//        return documentsHelper.documentExpedientGuardar(documentCodi, documentGuardar);
//    }
//
//    @Override
//    public Long documentExpedientAdjuntar(
//            String processInstanceId,
//            String adjuntId,
//            String adjuntTitol,
//            Date adjuntData,
//            String arxiuNom,
//            byte[] arxiuContingut) {
//        DocumentAdjunt documentAdjunt = DocumentAdjunt.builder()
//                .adjuntId(adjuntId)
//                .adjuntTitol(adjuntTitol)
//                .adjuntData(adjuntData)
//                .arxiuNom(arxiuNom)
//                .arxiuContingut(arxiuContingut).build();
//        return documentsHelper.documentExpedientAdjuntar(processInstanceId, documentAdjunt);
//    }
//
//    @Override
//    public void documentExpedientGuardarDadesRegistre(
//            Long documentStoreId,
//            String registreNumero,
//            Date registreData,
//            String registreOficinaCodi,
//            String registreOficinaNom,
//            boolean registreEntrada) {
//        DocumentRegistre registre = DocumentRegistre.builder()
//                .numero(registreNumero)
//                .data(registreData)
//                .oficinaCodi(registreOficinaCodi)
//                .oficinaNom(registreOficinaNom)
//                .entrada(registreEntrada)
//                .build();
//        documentsHelper.documentExpedientGuardarDadesRegistre(documentStoreId, registre);
//    }
//
//    @Override
//    public void documentExpedientEsborrar(
//            String taskInstanceId,
//            String processInstanceId,
//            String documentCodi) {
//
//        Object varValor = null;
//        String varCodi = JbpmVars.PREFIX_DOCUMENT + documentCodi;
//        if (taskInstanceId != null) {
//            varValor = workflowEngineApi.getTaskInstanceVariable(
//                    taskInstanceId,
//                    varCodi);
//        } else if (processInstanceId != null) {
//            varValor = workflowEngineApi.getProcessInstanceVariable(
//                    processInstanceId,
//                    varCodi);
//        }
//        if (varValor != null && varValor instanceof Long) {
//            documentsHelper.documentExpedientEsborrar((Long)varValor, processInstanceId);
//
//            if (taskInstanceId != null) {
//                workflowEngineApi.deleteTaskInstanceVariable(
//                        taskInstanceId,
//                        varCodi);
//                workflowEngineApi.deleteTaskInstanceVariable(
//                        taskInstanceId,
//                        JbpmVars.PREFIX_SIGNATURA + documentCodi);
//            }
//            if (processInstanceId != null) {
//                workflowEngineApi.deleteProcessInstanceVariable(
//                        processInstanceId,
//                        varCodi);
//            }
//        }
//    }
//
//    // TERMINIS
//    ////////////////////////////////////////////////////////////////////////////////
//
//    @Override
//    public TerminiDto getTerminiAmbProcessInstanceICodi(
//            String processInstanceId,
//            String terminiCodi) {
//        return terminisHelper.getTerminiAmbProcessInstanceICodi(processInstanceId, terminiCodi);
//    }
//
//    @Override
//    public TerminiIniciatDto getTerminiIniciatAmbProcessInstanceITerminiCodi(
//            String processDefinitionId,
//            String processInstanceId,
//            String terminiCodi) {
//        return terminisHelper.getTerminiIniciatAmbProcessInstanceITerminiCodi(
//                processDefinitionId,
//                processInstanceId,
//                terminiCodi);
//    }
//
//    @Override
//    public void terminiIniciar(
//            String terminiCodi,
//            String processInstanceId,
//            Date data,
//            Integer anys,
//            Integer mesos,
//            Integer dies,
//            boolean esDataFi) {
//        TerminiInici terminiInici = TerminiInici.builder()
//                .processInstanceId(processInstanceId)
//                .data(data)
//                .anys(anys)
//                .mesos(mesos)
//                .dies(dies)
//                .esDataFi(esDataFi).build();
//        terminisHelper.terminiIniciar(terminiCodi, terminiInici);
//    }
//
//    @Override
//    public void terminiPausar(Long terminiIniciatId, Date data) {
//        terminisHelper.terminiPausar(terminiIniciatId, data);
//    }
//
//    @Override
//    public void terminiContinuar(Long terminiIniciatId, Date data) {
//        terminisHelper.terminiContinuar(terminiIniciatId, data);
//    }
//
//    @Override
//    public void terminiCancelar(Long terminiIniciatId, Date data) {
//        terminisHelper.terminiCancelar(terminiIniciatId, data);
//    }
//
//    @Override
//    public Date terminiCalcularDataInici(Date fi, int anys, int mesos, int dies, boolean laborable, String processInstanceId) {
//        TerminiCalcul terminiCalcul = TerminiCalcul.builder()
//                .processInstanceId(processInstanceId)
//                .data(fi)
//                .anys(anys)
//                .mesos(mesos)
//                .dies(dies)
//                .laborable(laborable).build();
//        return terminisHelper.terminiCalcularDataInici(terminiCalcul);
//    }
//
//    @Override
//    public Date terminiCalcularDataFi(Date inici, int anys, int mesos, int dies, boolean laborable, String processInstanceId) {
//        TerminiCalcul terminiCalcul = TerminiCalcul.builder()
//                .processInstanceId(processInstanceId)
//                .data(inici)
//                .anys(anys)
//                .mesos(mesos)
//                .dies(dies)
//                .laborable(laborable).build();
//        return terminisHelper.terminiCalcularDataFi(terminiCalcul);
//    }
//
//    @Override
//    public void configurarTerminiIniciatAmbDadesWf(Long terminiIniciatId, String taskInstanceId, Long timerId) {
//        TerminiConfigurar terminiConfigurar = TerminiConfigurar.builder()
//                .taskInstanceId(taskInstanceId)
//                .timerId(timerId).build();
//        terminisHelper.configurarTerminiIniciatAmbDadesWf(terminiIniciatId, terminiConfigurar);
//    }
//
//    // ALERTES
//    ////////////////////////////////////////////////////////////////////////////////
//
//    @Override
//    public void alertaCrear(Long entornId, Long expedientId, Date data, String usuariCodi, String text) {
//        Alerta alerta = Alerta.builder()
//                .entornId(entornId)
//                .expedientId(expedientId)
//                .data(data)
//                .usuariCodi(usuariCodi)
//                .text(text).build();
//        alertesHelper.alertaCrear(alerta);
//    }
//
//    @Override
//    public void alertaEsborrarAmbTaskInstanceId(long taskInstanceId) {
//        alertesHelper.alertaEsborrar(taskInstanceId);
//    }
//
//    // ENUMERACIONS
//    ////////////////////////////////////////////////////////////////////////////////
//
//    @Override
//    public List<EnumeracioValorDto> enumeracioConsultar(String processInstanceId, String enumeracioCodi) {
//        return enumeracionsHelper.enumeracioConsultar(processInstanceId, enumeracioCodi);
//    }
//
//    @Override
//    public void enumeracioSetValor(String processInstanceId, String enumeracioCodi, String codi, String valor) {
//        Enumeracio enumeracio = Enumeracio.builder()
//                .processInstanceId(processInstanceId)
//                .enumeracioCodi(enumeracioCodi)
//                .codi(codi)
//                .valor(valor).build();
//        enumeracionsHelper.enumeracioSetValor(enumeracio);
//    }
//
//    // ESTATS
//    ////////////////////////////////////////////////////////////////////////////////
//
//    // TODO: Modificar findExpedientsConsultaGeneral per a que accepti el codi de l'estat, i eliminar aquest mètode
//    @Override
//    public EstatDto findEstatAmbEntornIExpedientTipusICodi(Long entornId, String expedientTipusCodi, String estatCodi) {
//        return estatsHelper.findEstatAmbEntornIExpedientTipusICodi(entornId, expedientTipusCodi, estatCodi);
//    }
//
//    // DOMINIS (MS DOMINIS)
//    ////////////////////////////////////////////////////////////////////////////////
//
//    @Override
//    public List<DominiRespostaFilaDto> dominiConsultar(String processInstanceId, String dominiCodi, String dominiId, Map<String, Object> parametres) {
//        return dominisHelper.dominiConsultar(processInstanceId, dominiCodi, dominiId, parametres);
//    }
//
//    @Override
//    public List<DominiRespostaFilaDto> dominiInternConsultar(String processInstanceId, String dominiId, Map<String, Object> parametres) {
//        return dominisHelper.dominiConsultar(processInstanceId, dominiId, "0", parametres);
//    }
//
//    // INTERESSATS
//    ////////////////////////////////////////////////////////////////////////////////
//
//    @Override
//    public void interessatCrear(InteressatDto interessat) {
//        interessatsHelper.crear(interessat);
//    }
//
//    @Override
//    public void interessatModificar(InteressatDto interessat) {
//        interessatsHelper.modificar(interessat);
//    }
//
//    @Override
//    public void interessatEliminar(String interessatCodi, Long expedientId) {
//        interessatsHelper.eliminar(interessatCodi, expedientId);
//    }
//
//    // GENERICS
//    ////////////////////////////////////////////////////////////////////////////////
//
//    // TODO: Permetre enviar documentStoreId enlloc dels Arxius
//    @Override
//    public void emailSend(
//            String fromAddress,
//            List<String> recipients,
//            List<String> ccRecipients,
//            List<String> bccRecipients,
//            String subject,
//            String text,
//            List<ArxiuDto> attachments) {
//        Email email = Email.builder()
//                .fromAddress(fromAddress)
//                .recipients(recipients)
//                .ccRecipients(ccRecipients)
//                .bccRecipients(bccRecipients)
//                .subject(subject)
//                .text(text)
//                .attachments(attachments).build();
//        genericsHelper.emailSend(email);
//    }
//
//    @Override
//    public String getHeliumProperty(String propertyName) {
//        return PropertiesHelper.getInstance().getProperty(propertyName);
//    }
//
//    @Override
//    public String getUsuariCodiActual() {
//        // TODO: Obtenir usuari actual. Id de sessió al header??
//        return null;
//    }
//
//    @Override
//    public ExpedientDto getExpedientIniciant() {
//        // TODO: Obtenir expedient iniciant actual. Id de sessió al header??
//        return null;
//    }
//
//    @Override
//    public EntornDto getEntornActual() {
//        // TODO: Obtenir entorn actual. Id de sessió al header??
//        return null;
//    }
//
//    @Override
//    public List<FestiuDto> findFestiusAll() {
//        return genericsHelper.getFestius();
//    }

    @Override
    public Reassignacio findReassignacioActivaPerUsuariOrigen(String processInstanceId, String usuariCodi) {
        var responseEntity = workflowBridgeFeignClient.getReassignacio(usuariCodi, processInstanceId);
        if (responseEntity != null) {
            return responseEntity.getBody();
        }
        return null;
    }

//    // TODO:
//    // REGISTRE (MS Integració)
//    ////////////////////////////////////////////////////////////////////////////////
//
//    @Override
//    public boolean isRegistreActiu() {
//        return false;
//    }
//
//    @Override
//    public RegistreIdDto registreAnotacioEntrada(RegistreAnotacioDto anotacio, Long expedientId) {
//        return null;
//    }
//
//    @Override
//    public RegistreIdDto registreAnotacioSortida(RegistreAnotacioDto anotacio, Long expedientId) {
//        return null;
//    }
//
//    @Override
//    public Date registreNotificacioComprovarRecepcio(String registreNumero, Long expedientId) {
//        return null;
//    }
//
//    @Override
//    public String registreObtenirOficinaNom(String oficinaCodi, Long expedientId) {
//        return null;
//    }
//
//    // Regweb3
//
//    @Override
//    public boolean isRegistreRegWeb3Actiu() {
//        return false;
//    }
//
//    @Override
//    public RegistreIdDto registreAnotacioSortida(RegistreAnotacio anotacio, Long expedientId) {
//        return null;
//    }
//
//    @Override
//    public String registreObtenirOficinaNom(String numRegistre, String usuariCodi, String entitatCodi, Long expedientId) {
//        return null;
//    }
//
//    // TODO:
//    // TRAMITACIÓ (MS Integració)
//    ////////////////////////////////////////////////////////////////////////////////
//
//    @Override
//    public TramitDto getTramit(String numero, String clau) {
//        return null;
//    }
//
//    @Override
//    public void zonaperExpedientCrear(ExpedientDto expedient, ZonaperExpedientDto dadesExpedient) {
//
//    }
//
//    @Override
//    public void zonaperEventCrear(String processInstanceId, ZonaperEventDto dadesEvent) {
//
//    }
//
//    @Override
//    public RegistreIdDto notificacioCrear(RegistreNotificacioDto notificacio, Long expedientId, boolean crearExpedient) {
//        return null;
//    }
//
//    @Override
//    public void notificacioGuardar(ExpedientDto expedient, NotificacioDto notificacio) {
//
//    }
//
//    @Override
//    public boolean notificacioEsborrar(String numero, String clave, Long codigo) {
//        return false;
//    }
//
//    @Override
//    public RespostaJustificantDetallRecepcioDto notificacioElectronicaJustificantDetall(String registreNumero) {
//        return null;
//    }
//
//    @Override
//    public RespostaJustificantRecepcioDto notificacioElectronicaJustificant(String registreNumero) {
//        return null;
//    }
//
//    // TODO:
//    // NOTIFICACIÓ (MS Integració)
//    ////////////////////////////////////////////////////////////////////////////////
//
//    @Override
//    public RespostaNotificacio altaNotificacio(DadesNotificacioDto dadesNotificacio) {
//        return null;
//    }
//
//    // TODO:
//    // GESTIÓ DOCUMENTAL (MS Integració)
//    ////////////////////////////////////////////////////////////////////////////////
//
//    @Override
//    public ArxiuDto getArxiuGestorDocumental(String id) {
//        return null;
//    }
//
//    //TODO:
//    // FIRMA SERVIDOR (MS Integració)
//    ////////////////////////////////////////////////////////////////////////////////
//
//    @Override
//    public void documentFirmaServidor(String processInstanceId, String documentCodi, String motiu) {
//
//    }
//
//    // TODO:
//    // PERSONES (MS Integració)
//    ////////////////////////////////////////////////////////////////////////////////
//
//    @Override
//    public PersonaDto getPersonaAmbCodi(String codi) {
//        return null;
//    }
//
//    @Override
//    public List<String> getRolsByCodi(String codi) {
//        return null;
//    }
//
//    // TODO:
//    // PORTAFIRMES (MS Integració)
//    ////////////////////////////////////////////////////////////////////////////////
//
//    @Override
//    public Integer portasignaturesEnviar(Long documentId, List<Long> annexosId, PersonaDto persona, List<PersonaDto> personesPas1, int minSignatarisPas1, List<PersonaDto> personesPas2, int minSignatarisPas2, List<PersonaDto> personesPas3, int minSignatarisPas3, Long expedientId, String importancia, Date dataLimit, Long tokenId, Long processInstanceId, String transicioOK, String transicioKO) {
//        return null;
//    }
//
//    @Override
//    public void portasignaturesEliminar(List<Integer> documentIds) {
//
//    }
//
//    // DEFINICIO PROCES
//    ////////////////////////////////////////////////////////////////////////////////
//
//    @Override
//    public Integer getDefinicioProcesVersioAmbJbpmKeyIProcessInstanceId(
//            String jbpmKey,
//            String processInstanceId) {
//        return definicioProcesHelper.getDefinicioProcesVersioAmbJbpmKeyIProcessInstanceId(
//                jbpmKey,
//                processInstanceId);
//    }
//
//    @Override
//    public DefinicioProcesDto getDefinicioProcesPerProcessInstanceId(String processInstanceId) {
//        return definicioProcesHelper.getDefinicioProcesPerProcessInstanceId(processInstanceId);
//    }
//
//    @Override
//    public Long getDefinicioProcesIdPerProcessInstanceId(String processInstanceId) {
//        return definicioProcesHelper.getDefinicioProcesIdPerProcessInstanceId(processInstanceId);
//    }
//
//    @Override
//    public Long getDefinicioProcesEntornAmbJbpmKeyIVersio(
//            String jbpmKey,
//            int version) {
//        return definicioProcesHelper.getDefinicioProcesEntornAmbJbpmKeyIVersio(
//                jbpmKey,
//                version);
//    }
//
//    @Override
//    public Long getDarreraVersioEntornAmbEntornIJbpmKey(
//            Long entornId,
//            String jbpmKey) {
//        return definicioProcesHelper.getDarreraVersioEntornAmbEntornIJbpmKey(
//                entornId,
//                jbpmKey);
//    }
//
//    @Override
//    public void initializeDefinicionsProces() {
//        definicioProcesHelper.initializeDefinicionsProces();
//    }
//
//    @Override
//    public String getProcessDefinitionIdHeretadaAmbPid(String processInstanceId) {
//        return definicioProcesHelper.getProcessDefinitionIdHeretadaAmbPid(processInstanceId);
//    }
//
//    // VARIABLES
//    ////////////////////////////////////////////////////////////////////////////////
//    public CampTipusIgnored getCampAndIgnored(
//            String processDefinitionId,
//            Long expedientId,
//            String varCodi) {
//        return variablesHelper.getCampAndIgnored(
//                processDefinitionId,
//                expedientId,
//                varCodi);
//    }
//
//    // TODO:
//    // CARRECS
//    ////////////////////////////////////////////////////////////////////////////////
//
//    @Override
//    public AreaDto getAreaAmbEntornICodi(Long entornId, String codi) {
//        return null;
//    }
//
//    @Override
//    public CarrecDto getCarrecAmbEntornIAreaICodi(Long entornId, String areaCodi, String carrecCodi) {
//        return null;
//    }
//
//    // TODO:
//    // MONITOR (Mètriques)
//    ////////////////////////////////////////////////////////////////////////////////
//
//    @Override
//    public Long startMetric(Class clazz, String titol, Long entornId, Long expedientTipusId) {
//        return null;
//    }
//
//    @Override
//    public void endMetric(Long metricId) {
//
//    }
//
//    // TODO:
//    // LOCALS
//    ////////////////////////////////////////////////////////////////////////////////
//
//    @Override
//    public Long getTaskInstanceIdByExecutionTokenId(Long executionTokenId) {
//        FindTaskInstanceIdForTokenIdCommand command = new FindTaskInstanceIdForTokenIdCommand(executionTokenId);
//        return (Long)commandService.execute(command);
//    }
//
//    @Override
//    public boolean expedientReindexar(String processInstanceId) {
//        return true;
//    }
//
//    @Override
//    public void tokenRedirigir(long tokenId, String nodeName, boolean cancelarTasques) {
//        TokenRedirectCommand command = new TokenRedirectCommand(tokenId, nodeName);
//        command.setCancelTasks(cancelarTasques);
//        command.setEnterNodeIfTask(true);
//        command.setExecuteNode(false);
//        AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
//                command,
//                tokenId,
//                AddToAutoSaveCommand.TIPUS_TOKEN);
//        commandService.execute(autoSaveCommand);
//        // TODO:
////        ExpedientDto piexp = workflowEngineApi.expedientFindByProcessInstanceId(
////                token.getProcessInstanceId());
////        expedientRegistreHelper.crearRegistreRedirigirToken(
////                piexp.getId(),
////                token.getProcessInstanceId(),
////                SecurityContextHolder.getContext().getAuthentication().getName(),
////                token.getFullName(),
////                nodeNameVell,
////                nodeName);
//    }
//
//    @Override
//    public boolean tokenActivar(long tokenId, boolean activar) {
//        try {
//            TokenActivarCommand command = new TokenActivarCommand(tokenId, activar);
//            AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
//                    command,
//                    tokenId,
//                    AddToAutoSaveCommand.TIPUS_TOKEN);
//            return true;
//        } catch (Exception ex) {
//            return false;
//        }
//    }
//
//    @Override
//    public void expedientEliminaInformacioRetroaccio(String processInstanceId) {
//        WProcessInstance pi = workflowEngineApi.getRootProcessInstance(processInstanceId);
//        GetProcessInstancesTreeCommand command = new GetProcessInstancesTreeCommand(new Long(pi.getId()));
//        for (ProcessInstance pd: (List<ProcessInstance>)commandService.execute(command)) {
//            DeleteProcessInstanceLogsCommand deleteCommand = new DeleteProcessInstanceLogsCommand(pd);
//            commandService.execute(deleteCommand);
//        }
//    }
//
//    @Override
//    public void afegirInstanciaProcesPerVerificarFinalitzacio(String processInstanceId) {
//        // TODO:
//        // ThreadLocalInfo.addProcessInstanceFinalitzatIds(processInstanceId);
//    }
//
//
//    private Map<String, String> convertVariablesMap(Map<String, Object> mapaVariables) {
//
//        DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
//        Map<String, String> mapaVariablesString = new HashMap<String, String>();
//
//        for (Map.Entry<String, Object> var: mapaVariables.entrySet()) {
//            if (var.getValue() instanceof String) {
//                mapaVariablesString.put(var.getKey(), (String)var.getValue());
//            } else if (var.getValue() instanceof Date) {
//                mapaVariablesString.put(var.getKey(), df.format((Date)var.getValue()));
////            } else if (var.getValue() instanceof BigDecimal) {
////                mapaVariablesString.put(var.getKey(), var.getValue().toString());
////            } else if (var.getValue() instanceof Boolean) {
////                mapaVariablesString.put(var.getKey(), var.getValue().toString());
//            } else {
//                mapaVariablesString.put(var.getKey(), var.getValue().toString());
//            }
//        }
//        return mapaVariablesString;
//    }
//
//    private String[] getExpedientProcessInstances(String processInstanceId) {
//        List<WProcessInstance> processInstancesTree = workflowEngineApi.getProcessInstanceTree(
//                workflowEngineApi.getRootProcessInstance(processInstanceId).getId());
//        String[] ids = new String[processInstancesTree.size()];
//        int i = 0;
//        for (WProcessInstance pi: processInstancesTree) {
//            ids[i++] = pi.getId();
//        }
//        return ids;
//    }

}
