package net.conselldemallorca.helium.v3.core.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import net.conselldemallorca.helium.core.api.WorkflowBridgeService;
import net.conselldemallorca.helium.core.common.ThreadLocalInfo;
import net.conselldemallorca.helium.core.extern.domini.FilaResultat;
import net.conselldemallorca.helium.core.extern.domini.ParellaCodiValor;
import net.conselldemallorca.helium.core.helper.AlertaHelper;
import net.conselldemallorca.helium.core.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.core.helper.DocumentHelperV3;
import net.conselldemallorca.helium.core.helper.DominiHelper;
import net.conselldemallorca.helium.core.helper.ExpedientHelper;
import net.conselldemallorca.helium.core.helper.IndexHelper;
import net.conselldemallorca.helium.core.helper.MailHelper;
import net.conselldemallorca.helium.core.helper.TascaSegonPlaHelper;
import net.conselldemallorca.helium.core.helper.TerminiHelper;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.Document;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.Enumeracio;
import net.conselldemallorca.helium.core.model.hibernate.EnumeracioValors;
import net.conselldemallorca.helium.core.model.hibernate.Estat;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.Interessat;
import net.conselldemallorca.helium.core.model.hibernate.TerminiIniciat;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.ms.domini.DominiMs;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentDissenyDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.DominiDto;
import net.conselldemallorca.helium.v3.core.api.dto.DominiRespostaColumnaDto;
import net.conselldemallorca.helium.v3.core.api.dto.DominiRespostaFilaDto;
import net.conselldemallorca.helium.v3.core.api.dto.EnumeracioValorDto;
import net.conselldemallorca.helium.v3.core.api.dto.EstatDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.InteressatDto;
import net.conselldemallorca.helium.v3.core.api.dto.InteressatTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.TerminiIniciatDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.SistemaExternException;
import net.conselldemallorca.helium.v3.core.api.exception.ValidacioException;
import net.conselldemallorca.helium.v3.core.repository.DefinicioProcesRepository;
import net.conselldemallorca.helium.v3.core.repository.DocumentRepository;
import net.conselldemallorca.helium.v3.core.repository.EntornRepository;
import net.conselldemallorca.helium.v3.core.repository.EnumeracioRepository;
import net.conselldemallorca.helium.v3.core.repository.EnumeracioValorsRepository;
import net.conselldemallorca.helium.v3.core.repository.EstatRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientTipusRepository;
import net.conselldemallorca.helium.v3.core.repository.InteressatRepository;

@Service
public class WorkflowBridgeServiceImpl implements WorkflowBridgeService {

    @Resource
    private DefinicioProcesRepository definicioProcesRepository;
    @Resource
    private EntornRepository entornRepository;
    @Resource
    private ExpedientTipusRepository expedientTipusRepository;
    @Resource
    private ExpedientRepository expedientRepository;
    @Resource
    private EstatRepository estatRepository;
    @Resource
    private DocumentRepository documentRepository;
    @Resource
    private EnumeracioRepository enumeracioRepository;
    @Resource
    private EnumeracioValorsRepository enumeracioValorsRepository;
    @Resource
    private DominiMs dominiMs;
    @Resource
    private InteressatRepository interessatRepository;

    @Resource
    private ExpedientHelper expedientHelper;
    @Resource
    private TerminiHelper terminiHelper;
    @Resource
    private TascaSegonPlaHelper tascaSegonPlaHelper;
    @Resource(name = "documentHelperV3")
    private DocumentHelperV3 documentHelper;
    @Resource
    private AlertaHelper alertaHelper;
    @Resource
    private DominiHelper dominiHelper;
    @Resource
    private MailHelper mailHelper;

    @Resource
    private IndexHelper indexHelper;
    @Resource
    private ConversioTipusHelper conversioTipusHelper;


    // EXPEDIENTS
    ////////////////////////////////////////////////////////////////////////////////

    // TODO: Modificar utilitzant el MS d'expedients i tasques
    @Override
    public List<ExpedientDto> findExpedientsConsultaGeneral(
            Long entornId,
            String titol,
            String numero,
            Date dataInici1,
            Date dataInici2,
            Long expedientTipusId,
            Long estatId,
            boolean nomesIniciats,
            boolean nomesFinalitzats) {
        logger.debug("Consultant expedients (" +
                "entornId=" + entornId + ", " +
                "titol=" + titol + ", " +
                "numero=" + numero + ", " +
                "dataInici1=" + dataInici1 + ", " +
                "dataInici2=" + dataInici2 + ", " +
                "expedientTipusId=" + expedientTipusId + ", " +
                "estatId=" + estatId + ", " +
                "nomesFinalitzats=" + nomesFinalitzats + ", " +
                "nomesFinalitzats=" + nomesFinalitzats + ")");
        Entorn entorn = entornRepository.findOne(entornId);
        if (entorn == null)
            throw new NoTrobatException(Entorn.class, entornId);
        ExpedientTipus expedientTipus = null;
        Estat estat = null;
        if (expedientTipusId != null) {
            expedientTipus = expedientTipusRepository.findOne(expedientTipusId);
            if (expedientTipus == null)
                throw new NoTrobatException(ExpedientTipus.class, expedientTipusId);
            if (estatId != null) {
                estat = estatRepository.findOne(estatId);
                if (estat == null)
                    throw new NoTrobatException(Estat.class, estatId);
            }
        }
        return conversioTipusHelper.convertirList(
                expedientHelper.findByFiltreGeneral(
                        entorn,
                        titol,
                        numero,
                        dataInici1,
                        dataInici2,
                        expedientTipus,
                        estat,
                        nomesIniciats,
                        nomesFinalitzats),
                ExpedientDto.class);
    }

    // TODO: Modificar amb el MS de dades
    @Override
    public List<ExpedientDto> findExpedientsConsultaDadesIndexades(
            Long entornId,
            String expedientTipusCodi,
            Map<String, Object> filtreValors) {
        logger.debug("Consultant expedients a Lucene (" +
                "entornId=" + entornId + ", " +
                "expedientTipusCodi=" + expedientTipusCodi + ", " +
                "filtreValors=" + filtreValors + ")");
        List<ExpedientDto> resposta = new ArrayList<ExpedientDto>();

        Entorn entorn = entornRepository.findOne(entornId);
        if (entorn == null)
            throw new NoTrobatException(Entorn.class, entornId);
        // comprovar l'accés a l'entorn
        ExpedientTipus expedientTipus = expedientTipusRepository.findByEntornAndCodi(entorn, expedientTipusCodi);
        if (expedientTipus == null)
            throw new NoTrobatException(ExpedientTipus.class, expedientTipusCodi);
        // comprovar l'accés al tipus d'expedient

        // Construeix la llista de camps
        Map<String, Camp> campsIndexatsPerCodi = new HashMap<String, Camp>();
        Set<Camp> camps = null;
        if (expedientTipus.isAmbInfoPropia()) {
            if (expedientTipus.getExpedientTipusPare() != null) {
                // Camps heretats
                for (Camp camp: expedientTipus.getExpedientTipusPare().getCamps())
                    campsIndexatsPerCodi.put(camp.getCodi(), camp);
            }
            camps = expedientTipus.getCamps();
        } else {
            // Troba la definició de procés principial
            for (DefinicioProces definicioProces : expedientTipus.getDefinicionsProces())
                if (expedientTipus.getJbpmProcessDefinitionKey() != null
                        && expedientTipus.getJbpmProcessDefinitionKey().equals(definicioProces.getJbpmKey())) {
                    camps = definicioProces.getCamps();
                    break;
                }
        }
        // Els camps del TE o de la DP sobreescriuen els heretats
        if (camps != null)
            for (Camp camp: camps)
                campsIndexatsPerCodi.put(camp.getCodi(), camp);
        List<Camp> filtreCamps = new ArrayList<Camp>(campsIndexatsPerCodi.values());

        // consultar a l'índex
        List<Long> expedientsIds = indexHelper.findExpedientsIdsByFiltre(
                entorn,
                expedientTipus,
                filtreCamps,
                filtreValors);
        Expedient expedient;
        for (Long expedientId : expedientsIds) {
            expedient = expedientHelper.findAmbEntornIId(entornId, expedientId);
            if (expedient != null
                    && !expedient.isAnulat())
                resposta.add(conversioTipusHelper.convertir(expedient, ExpedientDto.class));
        }
        return resposta;
    }

    @Override
    public ExpedientDto getExpedientAmbEntornITipusINumero(
            Long entornId,
            String expedientTipusCodi,
            String numero) {
        logger.debug("Obtenint expedient donat entorn, tipus i número (" +
                "entornId=" + entornId + ", " +
                "expedientTipusCodi=" + expedientTipusCodi + ", " +
                "numero=" + numero + ")");
        Entorn entorn = entornRepository.findOne(entornId);
        if (entorn == null)
            throw new NoTrobatException(Entorn.class, entornId);

        ExpedientTipus expedientTipus = expedientTipusRepository.findByEntornAndCodi(
                entorn,
                expedientTipusCodi);
        if (expedientTipus == null)
            throw new NoTrobatException(ExpedientTipus.class, expedientTipusCodi);
        return conversioTipusHelper.convertir(
                expedientRepository.findByEntornAndTipusAndNumero(
                        entorn,
                        expedientTipus,
                        numero),
                ExpedientDto.class);
    }

    @Override
    public void expedientRelacionar(
            Long expedientIdOrigen,
            Long expedientIdDesti) {
        logger.debug("Relacionant els expedients (" +
                "expedientIdOrigen=" + expedientIdOrigen + ", " +
                "expedientIdDesti=" + expedientIdDesti + ")");
        Expedient origen = expedientRepository.findOne(expedientIdOrigen);
        if (origen == null)
            throw new NoTrobatException(Expedient.class, expedientIdOrigen);
        Expedient desti = expedientRepository.findOne(expedientIdDesti);
        if (desti == null)
            throw new NoTrobatException(Expedient.class, expedientIdDesti);
        expedientHelper.relacioCrear(
                origen,
                desti);
    }

    @Override
    public void expedientAturar(
            String processInstanceId,
            String motiu) {
        logger.debug("Aturant expedient (processInstanceId=" + processInstanceId + ", motiu=" + motiu + ")");
        Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
        expedientHelper.aturar(
                expedient,
                motiu,
                null);
    }

    @Override
    public void expedientReprendre(
            String processInstanceId) {
        logger.debug("Reprenent expedient (processInstanceId=" + processInstanceId + ")");
        Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
        expedientHelper.reprendre(
                expedient,
                null);
    }

    @Override
    public void expedientModificarEstat(
            String processInstanceId,
            String estatCodi) {
        logger.debug("Modificant estat de l'expedient (" +
                "processInstanceId=" + processInstanceId + ", " +
                "estatCodi=" + estatCodi + ")");
        Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
        Estat estat = estatRepository.findByExpedientTipusAndCodiAmbHerencia(
                expedient.getTipus().getId(),
                estatCodi);
        if (estat == null)
            throw new NoTrobatException(Estat.class, estatCodi);
        expedientHelper.update(
                expedient,
                expedient.getNumero(),
                expedient.getTitol(),
                expedient.getResponsableCodi(),
                expedient.getDataInici(),
                expedient.getComentari(),
                estat.getId(),
                expedient.getGeoPosX(),
                expedient.getGeoPosY(),
                expedient.getGeoReferencia(),
                expedient.getGrupCodi(),
                false);
    }

    @Override
    public void expedientModificarComentari(
            String processInstanceId,
            String comentari) {
        logger.debug("Modificant comentari de l'expedient (" +
                "processInstanceId=" + processInstanceId + ", " +
                "comentari=" + comentari + ")");
        Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
        expedientHelper.update(
                expedient,
                expedient.getNumero(),
                expedient.getTitol(),
                expedient.getResponsableCodi(),
                expedient.getDataInici(),
                comentari,
                (expedient.getEstat() != null) ? expedient.getEstat().getId() : null,
                expedient.getGeoPosX(),
                expedient.getGeoPosY(),
                expedient.getGeoReferencia(),
                expedient.getGrupCodi(),
                false);
    }

    @Override
    public void expedientModificarNumero(
            String processInstanceId,
            String numero) {
        logger.debug("Modificant número de l'expedient (" +
                "processInstanceId=" + processInstanceId + ", " +
                "numero=" + numero + ")");
        Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
        expedientHelper.update(
                expedient,
                numero,
                expedient.getTitol(),
                expedient.getResponsableCodi(),
                expedient.getDataInici(),
                expedient.getComentari(),
                (expedient.getEstat() != null) ? expedient.getEstat().getId() : null,
                expedient.getGeoPosX(),
                expedient.getGeoPosY(),
                expedient.getGeoReferencia(),
                expedient.getGrupCodi(),
                false);
    }

    // TASQUES
    ////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean isTascaEnSegonPla(Long taskId) {
        boolean result = false;
        if (tascaSegonPlaHelper.isTasquesSegonPlaLoaded()) {
            Map<Long, TascaSegonPlaHelper.InfoSegonPla> map = tascaSegonPlaHelper.getTasquesSegonPla();
            result = map.containsKey(taskId);
        }

        return result;
    }

    @Override
    public void addMissatgeExecucioTascaSegonPla(Long taskId, String[] message) {
        if (tascaSegonPlaHelper.isTasquesSegonPlaLoaded()) {
            Map<Long, TascaSegonPlaHelper.InfoSegonPla> map = tascaSegonPlaHelper.getTasquesSegonPla();
            if (map.containsKey(taskId)) {
                map.get(taskId).addMessage(message);
            }
        }
    }

    @Override
    public void setErrorTascaSegonPla(Long taskId, Exception ex) {
        if (tascaSegonPlaHelper.isTasquesSegonPlaLoaded()) {
            Map<Long, TascaSegonPlaHelper.InfoSegonPla> map = tascaSegonPlaHelper.getTasquesSegonPla();
            if (map.containsKey(taskId)) {
                map.get(taskId).setError((ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage()));
            }
        }
    }

    // DOCUMENTS
    ////////////////////////////////////////////////////////////////////////////////

    @Override
    public DocumentDissenyDto getDocumentDisseny(
            Long definicioProcesId,
            String processInstanceId,
            String documentCodi) {
        logger.debug("Obtenint el document de disseny donada la definició de procés i el codi (" +
                "definicioProcesId=" + definicioProcesId + ", " +
                "documentCodi=" + documentCodi + ")");
        DefinicioProces definicioProces = definicioProcesRepository.findOne(definicioProcesId);
        Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
        if (definicioProces == null)
            throw new NoTrobatException(DefinicioProces.class, definicioProcesId);
        ExpedientTipus expedientTipus = expedient.getTipus();

        if (expedientTipus.isAmbInfoPropia())
            return conversioTipusHelper.convertir(
                    documentRepository.findByExpedientTipusAndCodi(
                            expedientTipus.getId(),
                            documentCodi,
                            expedientTipus.getExpedientTipusPare() != null),
                    DocumentDissenyDto.class);
        else
            return conversioTipusHelper.convertir(
                    documentRepository.findByDefinicioProcesAndCodi(
                            definicioProces,
                            documentCodi),
                    DocumentDissenyDto.class);
    }

    @Override
    public DocumentDto getDocumentInfo(Long documentStoreId) {
        logger.debug("Obtenint informació del document (" +
                "documentStoreId=" + documentStoreId + ")");
        return this.getDocumentInfo(
                documentStoreId,
                false,
                false,
                false,
                false,
                false, // Per notificar
                false);
    }

    @Override
    public DocumentDto getDocumentInfo(Long documentStoreId,
                                       boolean ambContingutOriginal,
                                       boolean ambContingutSignat,
                                       boolean ambContingutVista,
                                       boolean perSignar,
                                       boolean perNotificar,
                                       boolean ambSegellSignatura) {
        logger.debug("Obtenint informació del document (" +
                "documentStoreId=" + documentStoreId + ", " +
                "ambContingutOriginal=" + ambContingutOriginal + ", " +
                "ambContingutSignat=" + ambContingutSignat + ", " +
                "ambContingutVista=" + ambContingutVista + ", " +
                "perSignar=" + perSignar + ", " +
                "ambSegellSignatura=" + ambSegellSignatura + ")");
        return conversioTipusHelper.convertir(
                documentHelper.toDocumentDto(
                        documentStoreId,
                        ambContingutOriginal,
                        ambContingutSignat,
                        ambContingutVista,
                        ambContingutVista,
                        perNotificar, // Per notificar
                        ambSegellSignatura),
                DocumentDto.class);
    }

    @Override
    public String getCodiVariablePerDocumentCodi(String documentCodi) {
        logger.debug("Obtenint el codi de variable jBPM pel document (" +
                "documentCodi=" + documentCodi + ")");
        return documentHelper.getVarPerDocumentCodi(documentCodi, false);
    }

    @Override
    public ArxiuDto getArxiuPerMostrar(Long documentStoreId) {
        logger.debug("Obtenint arxiu del document (" +
                "documentStoreId=" + documentStoreId + ")");
        DocumentDto document = documentHelper.toDocumentDto(
                documentStoreId,
                false,
                false,
                false,
                false,
                false, // Per notificar
                false);
        if (document == null) {
            return null;
        }
        return documentHelper.getArxiuPerDocumentStoreId(
                documentStoreId,
                false,
                false);
    }

    @Override
    public ArxiuDto documentGenerarAmbPlantilla(
            String taskInstanceId,
            String processDefinitionId,
            String processInstanceId,
            String documentCodi,
            Date dataDocument) {
        logger.debug("Generant document amb plantilla (" +
                "taskInstanceId=" + taskInstanceId + ", " +
                "processInstanceId=" + processInstanceId + ", " +
                "documentCodi=" + documentCodi + ", " +
                "dataDocument=" + dataDocument + ")");
        DefinicioProces definicioProces = getDefinicioProcesDonatProcessInstanceId(processDefinitionId);
        if (definicioProces == null)
            throw new NoTrobatException(DefinicioProces.class, processInstanceId);
        Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
        ExpedientTipus expedientTipus = expedient.getTipus();
        Document document;
        if (expedientTipus.isAmbInfoPropia())
            document = documentRepository.findByExpedientTipusAndCodi(
                    expedientTipus.getId(),
                    documentCodi,
                    expedientTipus.getExpedientTipusPare() != null);
        else
            document = documentRepository.findByDefinicioProcesAndCodi(
                    definicioProces,
                    documentCodi);
        if (document == null) {
            throw new NoTrobatException(Document.class, documentCodi);
        }
        ArxiuDto generat = documentHelper.generarDocumentAmbPlantillaIConvertir(
                expedient,
                document,
                taskInstanceId,
                processInstanceId,
                dataDocument);
        documentHelper.crearDocument(
                taskInstanceId,
                processInstanceId,
                document.getCodi(),
                dataDocument,
                false,
                null,
                generat.getNom(),
                generat.getContingut(),
                null,
                null,
                null,
                null);
        return generat;
    }

    @Override
    public void documentExpedientGuardarDadesRegistre(
            Long documentStoreId,
            String registreNumero,
            Date registreData,
            String registreOficinaCodi,
            String registreOficinaNom,
            boolean registreEntrada) {
        logger.debug("Esborrant un document de dins l'expedient (" +
                "documentStoreId=" + documentStoreId + ", " +
                "registreNumero=" + registreNumero + ", " +
                "registreData=" + registreData + ", " +
                "registreOficinaCodi=" + registreOficinaCodi + ", " +
                "registreOficinaNom=" + registreOficinaNom + ", " +
                "registreEntrada=" + registreEntrada + ")");
        documentHelper.guardarDadesRegistre(
                documentStoreId,
                registreNumero,
                registreData,
                registreOficinaCodi,
                registreOficinaNom,
                registreEntrada);
    }

    @Override
    public void documentExpedientEsborrar(
            String taskInstanceId,
            String processInstanceId,
            String documentCodi) {
        logger.debug("Esborrant un document de dins l'expedient (" +
                "taskInstanceId=" + taskInstanceId + ", " +
                "processInstanceId=" + processInstanceId + ", " +
                "documentCodi=" + documentCodi + ")");
        documentHelper.esborrarDocument(
                taskInstanceId,
                processInstanceId,
                documentCodi);
    }

    // TERMINIS
    ////////////////////////////////////////////////////////////////////////////////

    @Override
    public TerminiIniciatDto getTerminiIniciatAmbProcessInstanceITerminiCodi(
            String processDefinitionId,
            String processInstanceId,
            String terminiCodi) {
        logger.debug("Obtenint termini iniciat donada la instància de procés i el codi (" +
                "processInstanceId=" + processInstanceId + ", " +
                "terminiCodi=" + terminiCodi + ")");
        Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
        TerminiIniciat terminiIniciat = null;
        if (expedient.getTipus().isAmbInfoPropia()) {
            terminiIniciat = terminiHelper.findIniciatAmbExpedientTipusICodi(
                    expedient.getTipus(),
                    processInstanceId,
                    terminiCodi);
        } else {
            DefinicioProces definicioProces = getDefinicioProcesDonatProcessInstanceId(processDefinitionId);
            terminiIniciat = terminiHelper.findIniciatAmbDefinicioProcesICodi(
                    definicioProces,
                    processInstanceId,
                    terminiCodi);
        }
        if (terminiIniciat == null)
            throw new NoTrobatException(TerminiIniciat.class, terminiCodi);
        return conversioTipusHelper.convertir(
                terminiIniciat,
                TerminiIniciatDto.class);
    }

    @Override
    public void terminiCancelar(
            Long terminiIniciatId,
            Date data) {
        logger.debug("Cancelant termini iniciat (" +
                "terminiIniciatId=" + terminiIniciatId + ", " +
                "data=" + data + ")");
        TerminiIniciat termini = terminiHelper.findTerminiIniciatById(terminiIniciatId);
        if (termini == null)
            throw new NoTrobatException(TerminiIniciat.class, terminiIniciatId);
        terminiHelper.cancelar(terminiIniciatId, data, false);
    }

    @Override
    public Date terminiCalcularDataFi(
            Date inici,
            int anys,
            int mesos,
            int dies,
            boolean laborable,
            String processInstanceId) {
        logger.debug("Calculant data de fi de termini a partir d'una data d'inici (" +
                "inici=" + inici + ", " +
                "anys=" + anys + ", " +
                "mesos=" + mesos + ", " +
                "dies=" + dies + ", " +
                "laborable=" + laborable + ")");
        return terminiHelper.getDataFiTermini(inici, anys, mesos, dies, laborable, processInstanceId);
    }

    @Override
    public void configurarTerminiIniciatAmbDadesWf(
            Long terminiIniciatId,
            String taskInstanceId,
            Long timerId) {
        logger.debug("Configurant termini iniciat (" +
                "terminiIniciatId=" + terminiIniciatId + ", " +
                "taskInstanceId=" + taskInstanceId + ", " +
                "timerId=" + timerId + ")");
        TerminiIniciat terminiIniciat = terminiHelper.findTerminiIniciatById(terminiIniciatId);
        if (terminiIniciat == null)
            throw new NoTrobatException(TerminiIniciat.class, terminiIniciatId);
        terminiIniciat.setTaskInstanceId(taskInstanceId);
        if (timerId != null)
            terminiIniciat.afegirTimerId(timerId.longValue());
    }

    // ALERTES
    ////////////////////////////////////////////////////////////////////////////////

    @Override
    public void alertaCrear(
            Long entornId,
            Long expedientId,
            Date data,
            String usuariCodi,
            String text) {
        logger.debug("Creant alerta (" +
                "entornId=" + entornId + ", " +
                "expedientId=" + expedientId + ", " +
                "data=" + data + ", " +
                "usuariCodi=" + usuariCodi + ", " +
                "text=" + text + ")");
        Entorn entorn = entornRepository.findOne(entornId);
        if (entorn == null)
            throw new NoTrobatException(Entorn.class, entornId);
        Expedient expedient = expedientRepository.findOne(expedientId);
        if (expedient == null)
            throw new NoTrobatException(Expedient.class, expedientId);

        alertaHelper.crearAlerta(entorn, expedient, data, usuariCodi, text);
    }

    // ENUMERACIONS
    ////////////////////////////////////////////////////////////////////////////////

    @Override
    public List<EnumeracioValorDto> enumeracioConsultar(
            String processInstanceId,
            String enumeracioCodi) {
        logger.debug("Consultant els valors d'una enumeració (" +
                "processInstanceId=" + processInstanceId + ", " +
                "enumeracioCodi=" + enumeracioCodi + ")");
        Expedient expedient = getExpedientDonatProcessInstanceId(processInstanceId);
        Enumeracio enumeracio = enumeracioRepository.findByEntornAndExpedientTipusAndCodi(
                expedient.getEntorn(),
                expedient.getTipus(),
                enumeracioCodi);
        if (enumeracio == null) {
            enumeracio = enumeracioRepository.findByEntornAndCodi(
                    expedient.getEntorn(),
                    enumeracioCodi);
        }
        if (enumeracio == null)
            throw new NoTrobatException(Enumeracio.class, enumeracioCodi);
        return conversioTipusHelper.convertirList(
                enumeracio.getEnumeracioValors(),
                EnumeracioValorDto.class);
    }


    @Override
    public void enumeracioSetValor(
            String processInstanceId,
            String enumeracioCodi,
            String codi,
            String valor) throws NoTrobatException {
        logger.debug("Fixant el valor d'una enumeració (" +
                "processInstanceId=" + processInstanceId + ", " +
                "enumeracioCodi=" + enumeracioCodi + ", " +
                "codi=" + codi + ", " +
                "valor=" + valor + ")");
        Expedient expedient = getExpedientDonatProcessInstanceId(processInstanceId);
        Enumeracio enumeracio = enumeracioRepository.findByEntornAndExpedientTipusAndCodi(
                expedient.getEntorn(),
                expedient.getTipus(),
                enumeracioCodi);
        if (enumeracio == null) {
            enumeracio = enumeracioRepository.findByEntornAndCodi(
                    expedient.getEntorn(),
                    enumeracioCodi);
        }
        if (enumeracio == null)
            throw new NoTrobatException(Enumeracio.class, enumeracioCodi);

        EnumeracioValors enumeracioValor = enumeracioValorsRepository.findByEnumeracioAndCodi(enumeracio, codi);
        if (enumeracioValor == null)
            throw new NoTrobatException(EnumeracioValors.class, codi);
        enumeracioValor.setNom(valor);
        enumeracioValorsRepository.save(enumeracioValor);
    }

    // ESTATS
    ////////////////////////////////////////////////////////////////////////////////

    @Override
    public EstatDto findEstatAmbEntornIExpedientTipusICodi(
            Long entornId,
            String expedientTipusCodi,
            String estatCodi) {
        logger.debug("Obtenint l'estat donat l'entorn, el tipus d'expedient i el codi (" +
                "entornId=" + entornId + ", " +
                "expedientTipusCodi=" + expedientTipusCodi + ", " +
                "estatCodi=" + estatCodi + ")");
        Entorn entorn = entornRepository.findOne(entornId);
        if (entorn == null)
            throw new NoTrobatException(Entorn.class, entornId);
        ExpedientTipus expedientTipus = expedientTipusRepository.findByEntornAndCodi(
                entorn,
                expedientTipusCodi);
        if (expedientTipus == null)
            throw new NoTrobatException(ExpedientTipus.class, expedientTipusCodi);
        return conversioTipusHelper.convertir(
                estatRepository.findByExpedientTipusAndCodiAmbHerencia(
                        expedientTipus.getId(),
                        estatCodi),
                EstatDto.class);
    }

    // DOMINIS
    ////////////////////////////////////////////////////////////////////////////////

    @Override
    public List<DominiRespostaFilaDto> dominiConsultar(
            String processInstanceId,
            String dominiCodi,
            String dominiId,
            Map<String, Object> parametres) {
        logger.debug("Executant una consulta de domini (" +
                "processInstanceId=" + processInstanceId + ", " +
                "dominiCodi=" + dominiCodi + ", " +
                "dominiId=" + dominiId + ", " +
                "parametres=" + parametres + ")");
        Expedient expedient = getExpedientDonatProcessInstanceId(processInstanceId);
        DominiDto domini;
        // Dominis del tipus d'expedient
        domini = dominiMs.findAmbCodi(
        		expedient.getEntorn().getId(),
        		expedient.getTipus().getId(),
        		dominiCodi);
        // Si no el troba el busca a l'entorn
        if (domini == null)
            domini = dominiMs.findAmbCodi(
            		expedient.getEntorn().getId(),
            		null,
            		dominiCodi);
        if (domini == null)
            throw new NoTrobatException(DominiDto.class, dominiCodi);
        List<FilaResultat> files = dominiHelper.consultar(
                domini.getId(),
                dominiId,
                parametres);
        List<DominiRespostaFilaDto> resposta = new ArrayList<DominiRespostaFilaDto>();
        if (files != null) {
            for (FilaResultat fila: files) {
                DominiRespostaFilaDto filaDto = new DominiRespostaFilaDto();
                for (ParellaCodiValor columna: fila.getColumnes()) {
                    DominiRespostaColumnaDto columnaDto = new DominiRespostaColumnaDto();
                    columnaDto.setCodi(columna.getCodi());
                    columnaDto.setValor(columna.getValor());
                    filaDto.getColumnes().add(columnaDto);
                }
                resposta.add(filaDto);
            }
        }
        return resposta;
    }

    // INTERESSATS
    ////////////////////////////////////////////////////////////////////////////////

    @Override
    public void interessatCrear(InteressatDto interessat) {

        this.validaInteressat(interessat);

        Expedient expedient = expedientRepository.findOne(interessat.getExpedientId());

        if (interessatRepository.findByCodiAndExpedient(interessat.getCodi(), expedient) != null) {
            throw new ValidacioException("Ja existeix un interessat amb aquest codi");
        }

        Interessat interessatEntity = new Interessat(
                interessat.getId(),
                interessat.getCodi(),
                interessat.getNom(),
                interessat.getNif(),
                interessat.getDir3Codi(),
                interessat.getLlinatge1(),
                interessat.getLlinatge2(),
                interessat.getTipus(),
                interessat.getEmail(),
                interessat.getTelefon(),
                expedient,
                interessat.getEntregaPostal(),
                interessat.getEntregaTipus(),
                interessat.getLinia1(),
                interessat.getLinia2(),
                interessat.getCodiPostal(),
                interessat.getEntregaDeh(),
                interessat.getEntregaDehObligat());

        interessatRepository.save(interessatEntity);
    }

    @Override
    public void interessatModificar(InteressatDto interessat) {

        this.validaInteressat(interessat);

        Expedient expedient = expedientRepository.findOne(interessat.getExpedientId());

        if (interessatRepository.findByCodiAndExpedient(interessat.getCodi(), expedient) == null) {
            throw new ValidacioException("Un interessat amb aquest codi no existeix");
        }

        Interessat interessatEntity = interessatRepository.findByCodiAndExpedient(
                interessat.getCodi(),
                expedient);

        interessatEntity.setNom(interessat.getNom());
        interessatEntity.setNif(interessat.getNif());
        interessatEntity.setLlinatge1(interessat.getLlinatge1());
        interessatEntity.setLlinatge2(interessat.getLlinatge2());
        interessatEntity.setTipus(interessat.getTipus());
        interessatEntity.setEmail(interessat.getEmail());
        interessatEntity.setTelefon(interessat.getTelefon());
        interessatEntity.setEntregaPostal(interessat.getEntregaPostal());
        interessatEntity.setEntregaTipus(interessat.getEntregaTipus());
        interessatEntity.setLinia1(interessat.getLinia1());
        interessatEntity.setLinia2(interessat.getLinia2());
        interessatEntity.setCodiPostal(interessat.getCodiPostal());
        interessatEntity.setEntregaDeh(interessat.getEntregaDeh());
        interessatEntity.setEntregaDehObligat(interessat.getEntregaDehObligat());
    }

    @Override
    public void interessatEliminar(InteressatDto interessat) {

        Expedient expedient = expedientRepository.findOne(interessat.getExpedientId());

        if (interessatRepository.findByCodiAndExpedient(interessat.getCodi(), expedient) == null) {
            throw new ValidacioException("Un interessat amb aquest codi no existeix");
        }

        Interessat interessatEntity = interessatRepository.findByCodiAndExpedient(
                interessat.getCodi(),
                expedient);
        List<Interessat> interessats = expedient.getInteressats();
        interessats.remove(interessatEntity);
        expedient.setInteressats(interessats);
        interessatRepository.delete(interessatEntity);

    }

    // GENERICS
    ////////////////////////////////////////////////////////////////////////////////

    @Override
    public void emailSend(
            String fromAddress,
            List<String> recipients,
            List<String> ccRecipients,
            List<String> bccRecipients,
            String subject,
            String text,
            List<ArxiuDto> attachments)  {
        logger.debug("Enviant correu (" +
                "fromAddress=" + fromAddress + ", " +
                "recipients=" + recipients + ", " +
                "ccRecipients=" + ccRecipients + ", " +
                "bccRecipients=" + bccRecipients + ", " +
                "subject=" + subject + ", " +
                "text=" + text + ")");
        try {
            mailHelper.send(
                    fromAddress,
                    recipients,
                    ccRecipients,
                    bccRecipients,
                    subject,
                    text,
                    conversioTipusHelper.convertirList(
                            attachments,
                            ArxiuDto.class));
        } catch (Exception e) {
            throw SistemaExternException.tractarSistemaExternException(
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    "(Enviament de mail '" + subject + "')",
                    e);
        }
    }

    @Override
    public String getHeliumProperty(String propertyName) {
        return GlobalProperties.getInstance().getProperty(propertyName);
    }

    @Override
    public String getUsuariCodiActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }

    @Override
    public ExpedientDto getExpedientIniciant() {
        logger.debug("Obtenint expedient en fase d'inici");
        return conversioTipusHelper.convertir(
                ThreadLocalInfo.getExpedient(),
                ExpedientDto.class);
    }

    // Mètodes privats
    private DefinicioProces getDefinicioProcesDonatProcessInstanceId(
            String processDefinitionId) {
        return definicioProcesRepository.findByJbpmId(processDefinitionId);
    }
    private Expedient getExpedientDonatProcessInstanceId(
            String processInstanceId) {
        return expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
    }
    private void validaInteressat(InteressatDto interessat) throws ValidacioException{
        List<String> errors = new ArrayList<String>();

        if (interessat.getTipus() != null) {
            switch (interessat.getTipus()) {
                case ADMINISTRACIO:
                    if (interessat.getDir3Codi() == null || interessat.getDir3Codi().isEmpty()) {
                        // Codi DIR3 per administracions
                        errors.add("El codi DIR3 és obligatori per interessats de tipus Administració");
                    }
                    break;
                case FISICA:
                case JURIDICA:
                    break;
            }
        }
        // Camps obligatoris
        if (interessat.getCodi() == null || interessat.getCodi().isEmpty())
            errors.add("El codi és obligatori");
        if (interessat.getNif() == null || interessat.getNif().isEmpty())
            errors.add("El NIF/CIF/DNI és obligatori");
        if (interessat.getNom() == null || interessat.getNom().isEmpty())
            errors.add("El nom/raó social és obligatori");
        if (interessat.getTipus() == null)
            errors.add("El tipus d'interessat és obligatori");

        // Llinatge1 per persones físiques
        if (interessat.getTipus() == InteressatTipusEnumDto.FISICA && (interessat.getLlinatge1() == null || interessat.getLlinatge1().isEmpty())) {
            errors.add("Si el tipus de persona és física llavors el llinatge és obligatori");
        }

        // Línies entrega postal
        if (interessat.getEntregaPostal()) {
            if(interessat.getTipus() == null) {
                errors.add("El tipus d'entrega postal és obligatori si està habilidada l'entrega postal");
            }
            if(interessat.getLinia1() == null || interessat.getLinia1().isEmpty()) {
                errors.add("La línia 1 és obligatòria si està habilitada l'entrega postal");
            }
            if (interessat.getLinia2() == null || interessat.getLinia2().isEmpty()) {
                errors.add("La línia 2 és obligatòria si està habilitada l'entrega postal");
            }
            if (interessat.getCodiPostal() == null || interessat.getCodiPostal().isEmpty()) {
                errors.add("El codi postal és obligatori si està habilitada l'entrega postal");
            }
        }
        // email per entregues DEH
        if (interessat.getEntregaDeh() && (interessat.getEmail() == null || interessat.getEmail().isEmpty())) {
            errors.add("L'email és obligatori si està habilitada l'entrega a la Direcció Electrònica Hablitada (DEH)");
        }

        if (!errors.isEmpty()) {
            StringBuilder errorMsg = new StringBuilder("Errors de validació de l'interessat: [");
            for (int i = 0; i < errors.size(); i++) {
                errorMsg.append(errors.get(i));
                if (i < errors.size() -1)
                    errorMsg.append(", ");
            }
            errorMsg.append("]");
            throw new ValidacioException(errorMsg.toString());
        }

    }

    private static final Logger logger = LoggerFactory.getLogger(WorkflowBridgeServiceImpl.class);
}
