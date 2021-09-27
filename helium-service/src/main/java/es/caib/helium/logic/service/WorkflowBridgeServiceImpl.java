package es.caib.helium.logic.service;

import es.caib.helium.client.engine.model.CampRegistreRest;
import es.caib.helium.client.engine.model.CampRest;
import es.caib.helium.client.engine.model.CampTascaRest;
import es.caib.helium.client.engine.model.CampTipus;
import es.caib.helium.client.model.ParellaCodiValor;
import es.caib.helium.logic.helper.AlertaHelper;
import es.caib.helium.logic.helper.ConversioTipusServiceHelper;
import es.caib.helium.logic.helper.DefinicioProcesHelper;
import es.caib.helium.logic.helper.DocumentHelper;
import es.caib.helium.logic.helper.DominiHelper;
import es.caib.helium.logic.helper.ExpedientHelper;
import es.caib.helium.logic.helper.ExpedientRegistreHelper;
import es.caib.helium.logic.helper.ExpedientTipusHelper;
import es.caib.helium.logic.helper.HerenciaHelper;
import es.caib.helium.logic.helper.IndexHelper;
import es.caib.helium.logic.helper.MailHelper;
import es.caib.helium.logic.helper.TascaSegonPlaHelper;
import es.caib.helium.logic.helper.TerminiHelper;
import es.caib.helium.logic.helper.VariableHelper;
import es.caib.helium.logic.intf.WorkflowEngineApi;
import es.caib.helium.logic.intf.WorkflowRetroaccioApi;
import es.caib.helium.logic.intf.WorkflowRetroaccioApi.ExpedientRetroaccioEstat;
import es.caib.helium.logic.intf.WorkflowRetroaccioApi.ExpedientRetroaccioTipus;
import es.caib.helium.logic.intf.dto.ArxiuDto;
import es.caib.helium.logic.intf.dto.CampTascaDto;
import es.caib.helium.logic.intf.dto.CampTipusDto;
import es.caib.helium.logic.intf.dto.CampTipusIgnored;
import es.caib.helium.logic.intf.dto.DefinicioProcesDto;
import es.caib.helium.logic.intf.dto.DocumentDissenyDto;
import es.caib.helium.logic.intf.dto.DocumentDto;
import es.caib.helium.logic.intf.dto.DocumentTascaDto;
import es.caib.helium.logic.intf.dto.DominiDto;
import es.caib.helium.logic.intf.dto.DominiRespostaColumnaDto;
import es.caib.helium.logic.intf.dto.DominiRespostaFilaDto;
import es.caib.helium.logic.intf.dto.EnumeracioValorDto;
import es.caib.helium.logic.intf.dto.EstatDto;
import es.caib.helium.logic.intf.dto.ExpedientDadaDto;
import es.caib.helium.logic.intf.dto.ExpedientDto;
import es.caib.helium.logic.intf.dto.FestiuDto;
import es.caib.helium.logic.intf.dto.InteressatDto;
import es.caib.helium.logic.intf.dto.InteressatTipusEnumDto;
import es.caib.helium.logic.intf.dto.ReassignacioDto;
import es.caib.helium.logic.intf.dto.TascaDadaDto;
import es.caib.helium.logic.intf.dto.TerminiDto;
import es.caib.helium.logic.intf.dto.TerminiIniciatDto;
import es.caib.helium.logic.intf.dto.expedient.ExpedientInfoDto;
import es.caib.helium.logic.intf.exception.NoTrobatException;
import es.caib.helium.logic.intf.exception.SistemaExternException;
import es.caib.helium.logic.intf.exception.ValidacioException;
import es.caib.helium.logic.intf.extern.domini.FilaResultat;
import es.caib.helium.logic.intf.service.WorkflowBridgeService;
import es.caib.helium.logic.intf.util.Constants;
import es.caib.helium.logic.intf.util.GlobalProperties;
import es.caib.helium.logic.ms.DominiMs;
import es.caib.helium.logic.security.ExtendedPermission;
import es.caib.helium.persist.entity.Alerta;
import es.caib.helium.persist.entity.Camp;
import es.caib.helium.persist.entity.CampTasca;
import es.caib.helium.persist.entity.DefinicioProces;
import es.caib.helium.persist.entity.Document;
import es.caib.helium.persist.entity.DocumentTasca;
import es.caib.helium.persist.entity.Entorn;
import es.caib.helium.persist.entity.Enumeracio;
import es.caib.helium.persist.entity.EnumeracioValors;
import es.caib.helium.persist.entity.Estat;
import es.caib.helium.persist.entity.Expedient;
import es.caib.helium.persist.entity.ExpedientTipus;
import es.caib.helium.persist.entity.Interessat;
import es.caib.helium.persist.entity.Reassignacio;
import es.caib.helium.persist.entity.Registre;
import es.caib.helium.persist.entity.Tasca;
import es.caib.helium.persist.entity.Termini;
import es.caib.helium.persist.entity.TerminiIniciat;
import es.caib.helium.persist.repository.CampRepository;
import es.caib.helium.persist.repository.CampTascaRepository;
import es.caib.helium.persist.repository.DefinicioProcesRepository;
import es.caib.helium.persist.repository.DocumentRepository;
import es.caib.helium.persist.repository.DocumentTascaRepository;
import es.caib.helium.persist.repository.EntornRepository;
import es.caib.helium.persist.repository.EnumeracioRepository;
import es.caib.helium.persist.repository.EnumeracioValorsRepository;
import es.caib.helium.persist.repository.EstatRepository;
import es.caib.helium.persist.repository.ExpedientRepository;
import es.caib.helium.persist.repository.ExpedientTipusRepository;
import es.caib.helium.persist.repository.FestiuRepository;
import es.caib.helium.persist.repository.InteressatRepository;
import es.caib.helium.persist.repository.ReassignacioRepository;
import es.caib.helium.persist.repository.TascaRepository;
import es.caib.helium.persist.repository.TerminiIniciatRepository;
import es.caib.helium.persist.util.ThreadLocalInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    private TerminiIniciatRepository terminiIniciatRepository;
    @Resource
    private FestiuRepository festiuRepository;
    @Resource
    private ReassignacioRepository reassignacioRepository;
    @Resource
    private TascaRepository tascaRepository;
    @Resource
    private CampRepository campRepository;
    @Resource
    private CampTascaRepository campTascaRepository;
    @Resource
    private DocumentTascaRepository documentTascaRepository;

    @Resource
    private ExpedientHelper expedientHelper;
    @Resource
    private ExpedientTipusHelper expedientTipusHelper;
    @Resource
    private TerminiHelper terminiHelper;
    @Resource
    private TascaSegonPlaHelper tascaSegonPlaHelper;
    @Resource
    private DocumentHelper documentHelper;
    @Resource
    private AlertaHelper alertaHelper;
    @Resource
    private DominiHelper dominiHelper;
    @Resource
    private VariableHelper variableHelper;
    @Resource
    private DefinicioProcesHelper definicioProcesHelper;
    @Resource
    private HerenciaHelper herenciaHelper;
    @Resource
    private MailHelper mailHelper;
    @Resource
    private ExpedientRegistreHelper expedientRegistreHelper;

    @Resource
    private IndexHelper indexHelper;
    @Resource
    private ConversioTipusServiceHelper conversioTipusServiceHelper;
    @Resource
    private GlobalProperties globalProperties;

    @Resource
    private WorkflowEngineApi workflowEngineApi;
    @Resource
    private WorkflowRetroaccioApi workflowRetroaccioApi;


    // EXPEDIENTS
    ////////////////////////////////////////////////////////////////////////////////

    // TODO: Modificar utilitzant el MS d'expedients i tasques
    @Override
    public List<ExpedientInfoDto> findExpedientsConsultaGeneral(
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
        Entorn entorn = entornRepository.findById(entornId)
                .orElseThrow(() -> new NoTrobatException(Entorn.class, entornId));
        ExpedientTipus expedientTipus = null;
        Estat estat = null;
        if (expedientTipusId != null) {
            expedientTipus = expedientTipusRepository.findById(expedientTipusId)
                    .orElseThrow(() -> new NoTrobatException(ExpedientTipus.class, expedientTipusId));
            if (estatId != null) {
                estat = estatRepository.findById(estatId)
                        .orElseThrow(() -> new NoTrobatException(Estat.class, estatId));
            }
        }
        List<Expedient> expedients = expedientHelper.findByFiltreGeneral(
                        entorn,
                        titol,
                        numero,
                        dataInici1,
                        dataInici2,
                        expedientTipus,
                        estat,
                        nomesIniciats,
                        nomesFinalitzats);

        List<ExpedientInfoDto> expedientInfos = new ArrayList<ExpedientInfoDto>();
        if (expedients != null) {
            for (Expedient expedient : expedients) {
                expedientInfos.add(expedientHelper.toExpedientInfo(expedient));
            }
        }
        return expedientInfos;
    }

    // TODO: Modificar amb el MS de dades
    @Override
    public List<ExpedientInfoDto> findExpedientsConsultaDadesIndexades(
            Long entornId,
            String expedientTipusCodi,
            Map<String, String> filtreValors) {
        logger.debug("Consultant expedients a Lucene (" +
                "entornId=" + entornId + ", " +
                "expedientTipusCodi=" + expedientTipusCodi + ", " +
                "filtreValors=" + filtreValors + ")");
        List<ExpedientInfoDto> resposta = new ArrayList<ExpedientInfoDto>();

        Entorn entorn = entornRepository.findById(entornId)
                .orElseThrow(() -> new NoTrobatException(Entorn.class, entornId));
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
        Map<String, Object> filtreValorsObject = new HashMap<String, Object>();
        if (filtreValors != null) {
            for (Map.Entry<String, String> filtre: filtreValors.entrySet()) {
                filtreValorsObject.put(filtre.getKey(), filtre.getValue());
            }
        }
        List<Long> expedientsIds = indexHelper.findExpedientsIdsByFiltre(
                entorn,
                expedientTipus,
                filtreCamps,
                filtreValorsObject);
//                filtreValors);
        Expedient expedient;
        for (Long expedientId : expedientsIds) {
            expedient = expedientHelper.findAmbEntornIId(entornId, expedientId);
            if (expedient != null
                    && !expedient.isAnulat())
//                resposta.add(conversioTipusHelper.convertir(expedient, ExpedientDto.class));
                resposta.add(expedientHelper.toExpedientInfo(expedient));
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
        Entorn entorn = entornRepository.findById(entornId)
                .orElseThrow(() -> new NoTrobatException(Entorn.class, entornId));

        ExpedientTipus expedientTipus = expedientTipusRepository.findByEntornAndCodi(
                entorn,
                expedientTipusCodi);
        if (expedientTipus == null)
            throw new NoTrobatException(ExpedientTipus.class, expedientTipusCodi);
        return conversioTipusServiceHelper.convertir(
                expedientRepository.findByEntornAndTipusAndNumero(
                        entorn,
                        expedientTipus,
                        numero),
                ExpedientDto.class);
    }

    @Override
    public String getProcessInstanceIdAmbEntornITipusINumero(
            Long entornId,
            String expedientTipusCodi,
            String numero) {
        logger.debug("Obtenint expedient donat entorn, tipus i número (" +
                "entornId=" + entornId + ", " +
                "expedientTipusCodi=" + expedientTipusCodi + ", " +
                "numero=" + numero + ")");
        Entorn entorn = entornRepository.findById(entornId)
                .orElseThrow(() -> new NoTrobatException(Entorn.class, entornId));

        ExpedientTipus expedientTipus = expedientTipusRepository.findByEntornAndCodi(
                entorn,
                expedientTipusCodi);
        if (expedientTipus == null)
            throw new NoTrobatException(ExpedientTipus.class, expedientTipusCodi);

        Expedient expedient = expedientRepository.findByEntornAndTipusAndNumero(
                entorn,
                expedientTipus,
                numero);
        if (expedient == null)
            return null;

        return expedient.getProcessInstanceId();
    }

    @Override
    public ExpedientDto getExpedientArrelAmbProcessInstanceId(
            String processInstanceId) {
        logger.debug("Obtenint expedient donada una instància de procés (processInstanceId=" + processInstanceId + ")");
        return conversioTipusServiceHelper.convertir(
                getExpedientDonatProcessInstanceId(processInstanceId),
                ExpedientDto.class);
    }

    @Override
    public void expedientRelacionar(
            Long expedientIdOrigen,
            Long expedientIdDesti) {
        logger.debug("Relacionant els expedients (" +
                "expedientIdOrigen=" + expedientIdOrigen + ", " +
                "expedientIdDesti=" + expedientIdDesti + ")");
        Expedient origen = expedientRepository.findById(expedientIdOrigen)
                .orElseThrow(() -> new NoTrobatException(Expedient.class, expedientIdOrigen));
        Expedient desti = expedientRepository.findById(expedientIdDesti)
                .orElseThrow(() -> new NoTrobatException(Expedient.class, expedientIdDesti));
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
        workflowRetroaccioApi.afegirInformacioRetroaccioPerExpedient(
                expedient.getId(),
                ExpedientRetroaccioTipus.EXPEDIENT_ATURAR,
                motiu,
                ExpedientRetroaccioEstat.IGNORAR);
        expedient.setInfoAturat(motiu);
        expedientRegistreHelper.crearRegistreAturarExpedient(
                expedient.getId(),
                SecurityContextHolder.getContext().getAuthentication().getName(),
                motiu);
    }

    @Override
    public void expedientReprendre(
            String processInstanceId) {
        logger.debug("Reprenent expedient (processInstanceId=" + processInstanceId + ")");
        Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
        workflowRetroaccioApi.afegirInformacioRetroaccioPerExpedient(
                expedient.getId(),
                ExpedientRetroaccioTipus.EXPEDIENT_REPRENDRE,
                null,
                ExpedientRetroaccioEstat.IGNORAR);
        expedient.setInfoAturat(null);
        expedientRegistreHelper.crearRegistreReprendreExpedient(
                expedient.getId(),
                SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Override
    public void desfinalitzarExpedient(String processInstanceId) {
        logger.debug("Desfinalitzant expedient(processInstanceId=" + processInstanceId + ")");
        Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
        workflowRetroaccioApi.afegirInformacioRetroaccioPerExpedient(
                expedient.getId(),
                ExpedientRetroaccioTipus.EXPEDIENT_DESFINALITZAR,
                null,
                ExpedientRetroaccioEstat.IGNORAR);
        expedient.setDataFi(null);
        expedientRegistreHelper.crearRegistreReprendreExpedient(
                expedient.getId(),
                (expedient.getResponsableCodi() != null) ? expedient.getResponsableCodi() : SecurityContextHolder.getContext().getAuthentication().getName());

        //tancam l'expedient de l'arxiu si escau
        if (expedient.isArxiuActiu()) {
            expedientHelper.reobrirExpedient(expedient);
        }
//        expedientHelper.desfinalitzar(
//                expedient,
//                null);
    }

    @Override
    public void finalitzarExpedient(
            String processInstanceId,
            Date dataFinalitzacio) {
        logger.debug("Finalitzant expedient (processInstanceId=" + processInstanceId + ")");
        Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
        expedient = expedientHelper.getExpedientComprovantPermisos(
                expedient.getId(),
                new Permission[] {
                        ExtendedPermission.WRITE,
                        ExtendedPermission.ADMINISTRATION});
        workflowRetroaccioApi.afegirInformacioRetroaccioPerExpedient(
                expedient.getId(),
                ExpedientRetroaccioTipus.EXPEDIENT_FINALITZAR,
                null);
        expedient.setDataFi(dataFinalitzacio);

        //tancam l'expedient de l'arxiu si escau
        if (expedient.isArxiuActiu() && expedient.getArxiuUuid() != null) {
            expedientHelper.tancarExpedientArxiu(expedient);
        }
        expedientHelper.crearRegistreExpedient(
                expedient.getId(),
                SecurityContextHolder.getContext().getAuthentication().getName(),
                Registre.Accio.FINALITZAR);
//        expedientHelper.finalitzar(expedient.getId());
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
    public void expedientModificarEstatId(
            String processInstanceId,
            Long estatId) {
        logger.debug("Modificant estat de l'expedient (" +
                "processInstanceId=" + processInstanceId + ", " +
                "estatId=" + estatId + ")");
        Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
//        Estat estat = estatRepository.findByExpedientTipusAndIdAmbHerencia(
//                expedient.getTipus().getId(),
//                estatId);
//        if (estat == null)
//            throw new NoTrobatException(Estat.class, estatId);
        expedientHelper.update(
                expedient,
                expedient.getNumero(),
                expedient.getTitol(),
                expedient.getResponsableCodi(),
                expedient.getDataInici(),
                expedient.getComentari(),
                estatId, // estat.getId(),
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

    @Override
    public void expedientModificarTitol(
            String processInstanceId,
            String titol) {
        logger.debug("Modificant títol de l'expedient (" +
                "processInstanceId=" + processInstanceId + ", " +
                "titol=" + titol + ")");
        Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
        expedientHelper.update(
                expedient,
                expedient.getNumero(),
                titol,
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

    @Override
    public void expedientModificarGeoref(
            String processInstanceId,
            Double posx,
            Double posy,
            String referencia) {
        logger.debug("Modificant georeferència de l'expedient (" +
                "processInstanceId=" + processInstanceId + ", " +
                "posx=" + posx + ", " +
                "posy=" + posy + ", " +
                "referencia=" + referencia + ")");
        Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
        expedientHelper.update(
                expedient,
                expedient.getNumero(),
                expedient.getTitol(),
                expedient.getResponsableCodi(),
                expedient.getDataInici(),
                expedient.getComentari(),
                (expedient.getEstat() != null) ? expedient.getEstat().getId() : null,
                posx,
                posy,
                referencia,
                expedient.getGrupCodi(),
                false);
    }

    public void expedientModificarGeoreferencia(
            String processInstanceId,
            String geoReferencia) {
        logger.debug("Modificant georeferencia de l'expedient (" +
                "processInstanceId=" + processInstanceId + ", " +
                "geoReferencia=" + geoReferencia + ")");
        Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
        expedientHelper.update(
                expedient,
                expedient.getNumero(),
                expedient.getTitol(),
                expedient.getResponsableCodi(),
                expedient.getDataInici(),
                expedient.getComentari(),
                (expedient.getEstat() != null) ? expedient.getEstat().getId() : null,
                expedient.getGeoPosX(),
                expedient.getGeoPosY(),
                geoReferencia,
                expedient.getGrupCodi(),
                false);
    }

    public void expedientModificarGeoX(
            String processInstanceId,
            Double posx) {
        logger.debug("Modificant geoPosX de l'expedient (" +
                "processInstanceId=" + processInstanceId + ", " +
                "geoPosX=" + posx + ")");
        Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
        expedientHelper.update(
                expedient,
                expedient.getNumero(),
                expedient.getTitol(),
                expedient.getResponsableCodi(),
                expedient.getDataInici(),
                expedient.getComentari(),
                (expedient.getEstat() != null) ? expedient.getEstat().getId() : null,
                posx,
                expedient.getGeoPosY(),
                expedient.getGeoReferencia(),
                expedient.getGrupCodi(),
                false);
    }

    public void expedientModificarGeoY(
            String processInstanceId,
            Double posy) {
        logger.debug("Modificant geoPosY de l'expedient (" +
                "processInstanceId=" + processInstanceId + ", " +
                "geoPosY=" + posy + ")");
        Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
        expedientHelper.update(
                expedient,
                expedient.getNumero(),
                expedient.getTitol(),
                expedient.getResponsableCodi(),
                expedient.getDataInici(),
                expedient.getComentari(),
                (expedient.getEstat() != null) ? expedient.getEstat().getId() : null,
                expedient.getGeoPosX(),
                posy,
                expedient.getGeoReferencia(),
                expedient.getGrupCodi(),
                false);
    }

    public void expedientModificarDataInici(
            String processInstanceId,
            Date dataInici) {
        logger.debug("Modificant dataInici de l'expedient (" +
                "processInstanceId=" + processInstanceId + ", " +
                "dataInici=" + dataInici + ")");
        Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
        expedientHelper.update(
                expedient,
                expedient.getNumero(),
                expedient.getTitol(),
                expedient.getResponsableCodi(),
                dataInici,
                expedient.getComentari(),
                (expedient.getEstat() != null) ? expedient.getEstat().getId() : null,
                expedient.getGeoPosX(),
                expedient.getGeoPosY(),
                expedient.getGeoReferencia(),
                expedient.getGrupCodi(),
                false);
    }

    @Override
    public void expedientModificarGrup(
            String processInstanceId,
            String grupCodi) {
        logger.debug("Modificant grup de l'expedient (" +
                "processInstanceId=" + processInstanceId + ", " +
                "grupCodi=" + grupCodi + ")");
        Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
        expedientHelper.update(
                expedient,
                expedient.getNumero(),
                expedient.getTitol(),
                expedient.getResponsableCodi(),
                expedient.getDataInici(),
                expedient.getComentari(),
                (expedient.getEstat() != null) ? expedient.getEstat().getId() : null,
                expedient.getGeoPosX(),
                expedient.getGeoPosY(),
                expedient.getGeoReferencia(),
                grupCodi,
                false);
    }

    @Override
    public void expedientModificarResponsable(
            String processInstanceId,
            String responsableCodi) {
        logger.debug("Modificant responsable de l'expedient (" +
                "processInstanceId=" + processInstanceId + ", " +
                "responsableCodi=" + responsableCodi + ")");
//        if (pluginHelper.personaFindAmbCodi(responsableCodi) == null)
//            throw new NoTrobatException(PersonaDto.class, responsableCodi);
        Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
        expedientHelper.update(
                expedient,
                expedient.getNumero(),
                expedient.getTitol(),
                responsableCodi,
                expedient.getDataInici(),
                expedient.getComentari(),
                (expedient.getEstat() != null) ? expedient.getEstat().getId() : null,
                expedient.getGeoPosX(),
                expedient.getGeoPosY(),
                expedient.getGeoReferencia(),
                expedient.getGrupCodi(),
                false);
    }

    @Override
    public void updateExpedientError(
            Long expedientId,
            Long jobId,
            String errorDesc,
            String errorFull) {
        logger.error("JOB (" + jobId + "): Actualitzant error de l'expedient");
        Expedient expedient = expedientRepository.getById(expedientId);
        expedient.setErrorDesc(errorDesc);
        expedient.setErrorFull(errorFull);
        expedientRepository.save(expedient);
    }

    @Override
    public ExpedientDadaDto getDadaPerProcessInstance(
            String processInstanceId,
            String varCodi) throws Exception {
        logger.debug("Obtenint la dada de l'instància de procés (processInstanceId=" + processInstanceId + ")");
        DefinicioProces definicioProces = getDefinicioProcesDonatProcessInstanceId(
                processInstanceId);
        if (definicioProces == null)
            throw new NoTrobatException(DefinicioProces.class, processInstanceId);
        Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
        ExpedientTipus expedientTipus = expedient.getTipus();
        Camp camp;
        if (expedientTipus.isAmbInfoPropia()) {
            camp = campRepository.findByExpedientTipusAndCodi(
                    expedientTipus.getId(),
                    varCodi,
                    expedientTipus.getExpedientTipusPare() != null);
        } else {
            camp = campRepository.findByDefinicioProcesAndCodi(
                    definicioProces,
                    varCodi);
        }

        if (camp == null)
            throw new NoTrobatException(Camp.class, varCodi);
        ExpedientDadaDto resposta = new ExpedientDadaDto();
        Object valor = workflowEngineApi.getProcessInstanceVariable(
                processInstanceId,
                varCodi);
        resposta.setText(
                variableHelper.getTextPerCamp(
                        camp,
                        valor,
                        null,
                        null,
                        processInstanceId,
                        null,
                        null));
        return resposta;
    }

    // TASQUES
    ////////////////////////////////////////////////////////////////////////////////

//    @Override
//    public boolean isTascaEnSegonPla(Long taskId) {
//        boolean result = false;
//        if (tascaSegonPlaHelper.isTasquesSegonPlaLoaded()) {
//            Map<Long, TascaSegonPlaHelper.InfoSegonPla> map = tascaSegonPlaHelper.getTasquesSegonPla();
//            result = map.containsKey(taskId);
//        }
//
//        return result;
//    }

    @Override
    public void addMissatgeExecucioTascaSegonPla(String taskId, String[] message) {
        if (tascaSegonPlaHelper.isTasquesSegonPlaLoaded()) {
            Map<String, TascaSegonPlaHelper.InfoSegonPla> map = tascaSegonPlaHelper.getTasquesSegonPla();
            if (map.containsKey(taskId)) {
                map.get(taskId).addMessage(message);
            }
        }
    }

    @Override
    public void setErrorTascaSegonPla(String taskId, String error) {
        if (tascaSegonPlaHelper.isTasquesSegonPlaLoaded()) {
            Map<String, TascaSegonPlaHelper.InfoSegonPla> map = tascaSegonPlaHelper.getTasquesSegonPla();
            if (map.containsKey(taskId)) {
                map.get(taskId).setError(error);
            }
        }
    }

    @Override
    public List<CampTascaRest> findCampsPerTaskInstance(
            String processInstanceId,
            String processDefinitionId,
            String taskName) {
        DefinicioProces definicioProces = definicioProcesRepository.findByJbpmId(
                processDefinitionId);
        if (definicioProces == null)
            throw new NoTrobatException(DefinicioProces.class, processDefinitionId);
        Tasca tasca = tascaRepository.findByJbpmNameAndDefinicioProces(
                taskName,
                definicioProces);
        if (tasca == null)
            throw new NoTrobatException(Tasca.class, taskName);
        ExpedientTipus tipus;
        if (definicioProces.getExpedientTipus() == null ) {
        	tipus = expedientTipusHelper.findAmbProcessInstanceId(processInstanceId);
        } else {
        	tipus = definicioProces.getExpedientTipus();
        }
        if (tipus == null)
            throw new NoTrobatException(ExpedientTipus.class, taskName);

        List<CampTascaRest> campsTascaRest = new ArrayList<>();
        List<CampTasca> campsTasca = campTascaRepository.findAmbTascaIdOrdenats(tasca.getId(), tipus.getId());
        if (campsTasca != null) {
          for (CampTasca campTasca: campsTasca) {
              CampRest.CampRestBuilder campRestBuilder = CampRest.builder();
              if (campTasca.getCamp() != null) {
                  campRestBuilder.codi(campTasca.getCamp().getCodi());
                  campRestBuilder.multiple(campTasca.getCamp().isMultiple());
                  campRestBuilder.tipus(
                          CampTipus.valueOf(
                                  campTasca.getCamp().getTipus().toString()));
                  // Registre
                  if (campTasca.getCamp().getRegistreMembres() != null) {
                      List<CampRegistreRest> campsRegistresRest = new ArrayList<>();
                      campTasca.getCamp().getRegistreMembres()
                              .forEach(r -> campsRegistresRest.add(
                                      CampRegistreRest.builder()
                                              .id(r.getId())
                                              .obligatori(r.isObligatori())
                                              .membre(CampRest.builder()
                                                      .codi(r.getMembre().getCodi())
                                                      .tipus(CampTipus.valueOf(
                                                              r.getMembre().getTipus().toString()))
                                                      .build())
                                              .build()));
                      campRestBuilder.registreMembres(campsRegistresRest);
                  }
              }
              campsTascaRest.add(
                      CampTascaRest.builder()
                              .camp(campRestBuilder.build())
                              .readFrom(campTasca.isReadFrom())
                              .writeTo(campTasca.isWriteTo())
                              .required(campTasca.isRequired())
                              .readOnly(campTasca.isReadOnly())
                              .order(campTasca.getOrder())
                              .ampleCols(campTasca.getAmpleCols())
                              .buitCols(campTasca.getBuitCols())
                              .build());
          }
        }
        return campsTascaRest;
//        return conversioTipusHelper.convertirList(
//                campTascaRepository.findAmbTascaIdOrdenats(tasca.getId(), tipus.getId()),
//                CampTascaDto.class);
    }

    @Override
    public List<DocumentTascaDto> findDocumentsPerTaskInstance(
            String processInstanceId,
            String processDefinitionId,
            String taskName) {
        DefinicioProces definicioProces = definicioProcesRepository.findByJbpmId(
                processDefinitionId);
        if (definicioProces == null)
            throw new NoTrobatException(DefinicioProces.class, processDefinitionId);
        Tasca tasca = tascaRepository.findByJbpmNameAndDefinicioProces(
                taskName,
                definicioProces);
        if (tasca == null)
            throw new NoTrobatException(Tasca.class, taskName);
        ExpedientTipus tipus;
        if (definicioProces.getExpedientTipus() == null ) {
        	tipus = expedientTipusHelper.findAmbProcessInstanceId(processInstanceId);
        } else {
        	tipus = definicioProces.getExpedientTipus();
        }
        if (tipus == null)
            throw new NoTrobatException(ExpedientTipus.class, taskName);

        List<DocumentTascaDto> documentsTascaDto = new ArrayList<DocumentTascaDto>();
        List<DocumentTasca> documentsTasca = documentTascaRepository.findAmbTascaOrdenats(tasca.getId(), tipus.getId());
        if (documentsTasca != null) {
            for (DocumentTasca documentTasca: documentsTasca) {
                DocumentDto documentDto = new DocumentDto();
                if (documentTasca.getDocument() != null) {
                    documentDto.setCodi(documentTasca.getDocument().getCodi());
                }
                DocumentTascaDto documentTascaDto = new DocumentTascaDto();
                documentTascaDto.setDocument(documentDto);
                documentTascaDto.setReadOnly(documentTasca.isReadOnly());
                documentTascaDto.setRequired(documentTasca.isRequired());
                documentTascaDto.setOrder(documentTasca.getOrder());
                documentsTascaDto.add(documentTascaDto);
            }
        }
        return documentsTascaDto;

//        return conversioTipusHelper.convertirList(
//                documentTascaRepository.findAmbTascaOrdenats(tasca.getId(), tipus.getId()),
//                DocumentTascaDto.class);
    }

    @Override
    public String getDadaPerTaskInstance(
            String processInstanceId,
            String taskInstanceId,
            String varCodi) throws Exception {
        logger.debug("Obtenint la dada de l'instància de tasca (taskInstanceId=" + taskInstanceId + ")");
        DefinicioProces definicioProces = getDefinicioProcesDonatProcessInstanceId(
                processInstanceId);
        if (definicioProces == null)
            throw new NoTrobatException(DefinicioProces.class);
        Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
        ExpedientTipus expedientTipus = expedient.getTipus();
        Camp camp;
        if (expedientTipus.isAmbInfoPropia()) {
            camp = campRepository.findByExpedientTipusAndCodi(
                    expedientTipus.getId(),
                    varCodi,
                    expedientTipus.getExpedientTipusPare() != null);
        } else {
            camp = campRepository.findByDefinicioProcesAndCodi(
                    definicioProces,
                    varCodi);
        }

        if (camp == null)
            throw new NoTrobatException(Camp.class,varCodi);
        TascaDadaDto resposta = new TascaDadaDto();
        Object valor = workflowEngineApi.getTaskInstanceVariable(
                taskInstanceId,
                varCodi);
        return variableHelper.getTextPerCamp(
                        camp,
                        valor,
                        null,
                        taskInstanceId,
                        null, 
                        null, 
                        null);
    }

    public CampTascaDto getCampTascaPerInstanciaTasca(
            String taskName,
            String processDefinitionId,
            String processInstanceId,
            String varCodi) {
        return variableHelper.getCampTascaPerInstanciaTasca(
                taskName,
                processDefinitionId,
                processInstanceId,
                varCodi);
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
        DefinicioProces definicioProces = definicioProcesRepository.findById(definicioProcesId)
                .orElseThrow(() -> new NoTrobatException(DefinicioProces.class, definicioProcesId));
        Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
        ExpedientTipus expedientTipus = expedient.getTipus();

        if (expedientTipus.isAmbInfoPropia())
            return conversioTipusServiceHelper.convertir(
                    documentRepository.findByExpedientTipusAndCodi(
                            expedientTipus.getId(),
                            documentCodi,
                            expedientTipus.getExpedientTipusPare() != null),
                    DocumentDissenyDto.class);
        else
            return conversioTipusServiceHelper.convertir(
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
        return conversioTipusServiceHelper.convertir(
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
    public Long documentExpedientCrear(
            String taskInstanceId,
            String processInstanceId,
            String documentCodi,
            Date documentData,
            boolean isAdjunt,
            String adjuntTitol,
            String arxiuNom,
            byte[] arxiuContingut) {
        logger.debug("Guardant un document a dins l'expedient (" +
                "processInstanceId=" + processInstanceId + ", " +
                "documentCodi=" + documentCodi + ", " +
                "data=" + documentData + ", " +
                "arxiuNom=" + arxiuNom + ", " +
                "arxiuContingut=" + arxiuContingut + ")");
        return documentHelper.crearDocument(
                taskInstanceId,
                processInstanceId,
                documentCodi,
                documentData,
                isAdjunt,
                adjuntTitol,
                arxiuNom,
                arxiuContingut,
                null,
                null,
                null,
                null);
    }

    @Override
    public Long documentExpedientGuardar(
            String processInstanceId,
            String documentCodi,
            Date data,
            String arxiuNom,
            byte[] arxiuContingut) throws Exception {
        logger.debug("Guardant un document a dins l'expedient (" +
                "processInstanceId=" + processInstanceId + ", " +
                "documentCodi=" + documentCodi + ", " +
                "data=" + data + ", " +
                "arxiuNom=" + arxiuNom + ", " +
                "arxiuContingut=" + arxiuContingut + ")");
        return documentHelper.crearActualitzarDocument(
                null,
                processInstanceId,
                documentCodi,
                data,
                arxiuNom,
                arxiuContingut,
                null,
                null,
                null,
                null);
    }

    @Override
    public Long documentExpedientAdjuntar(
            String processInstanceId,
            String adjuntId,
            String adjuntTitol,
            Date adjuntData,
            String arxiuNom,
            byte[] arxiuContingut) {
        logger.debug("Guardant un document adjunt a dins l'expedient (" +
                "processInstanceId=" + processInstanceId + ", " +
                "adjuntId=" + adjuntId + ", " +
                "adjuntTitol=" + adjuntTitol + ", " +
                "adjuntData=" + adjuntData + ", " +
                "arxiuNom=" + arxiuNom + ", " +
                "arxiuContingut=" + arxiuContingut + ")");
        return documentHelper.crearDocument(
                null,
                processInstanceId,
                null,
                adjuntData,
                true,
                adjuntTitol,
                arxiuNom,
                arxiuContingut,
                null,
                null,
                null,
                null);
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
            String processInstanceId,
            Long documentStoreId) {
        logger.debug("Esborrant un document de dins l'expedient (" +
                "processInstanceId=" + processInstanceId + ", " +
                "documentStoreId=" + documentStoreId + ")");

        documentHelper.esborrarDocumentExtern(
                processInstanceId,
                documentStoreId);
    }

    // TERMINIS
    ////////////////////////////////////////////////////////////////////////////////

    @Override
    public TerminiDto getTerminiAmbProcessInstanceICodi(
            String processInstanceId,
            String terminiCodi) {
        logger.debug("Obtenint termini donada la instància de procés i el codi (" +
                "processInstanceId=" + processInstanceId + "," +
                "terminiCodi=" + terminiCodi + ")");
        Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
        Termini termini = null;
        if (expedient.getTipus().isAmbInfoPropia()) {
            termini = terminiHelper.findAmbExpedientTipusICodi(
                    expedient.getTipus(),
                    terminiCodi);
        } else {
            DefinicioProces definicioProces = getDefinicioProcesDonatProcessInstanceId(processInstanceId);
            termini = terminiHelper.findAmbDefinicioProcesICodi(
                    definicioProces,
                    terminiCodi);
        }
        if (termini == null)
            throw new NoTrobatException(Termini.class, terminiCodi);
        return conversioTipusServiceHelper.convertir(
                termini,
                TerminiDto.class);
    }

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
        return conversioTipusServiceHelper.convertir(
                terminiIniciat,
                TerminiIniciatDto.class);
    }

    @Override
    public void terminiIniciar(
            String terminiCodi,
            String processInstanceId,
            Date data,
            Integer anys,
            Integer mesos,
            Integer dies,
            boolean esDataFi) {
        logger.debug("Iniciant termini (" +
                "terminiCodi=" + terminiCodi + ", " +
                "processInstanceId=" + processInstanceId + ", " +
                "data=" + data + ", " +
                "anys=" + anys + ", " +
                "mesos=" + mesos + ", " +
                "dies=" + dies + ", " +
                "esDataFi=" + esDataFi + ")");
        DefinicioProces definicioProces = getDefinicioProcesDonatProcessInstanceId(processInstanceId);
        ExpedientTipus expedientTipus = definicioProces.getExpedientTipus() != null?
                definicioProces.getExpedientTipus()
                : null;

        Termini termini = null;
        if (expedientTipus != null && expedientTipus.isAmbInfoPropia()) {
            termini = terminiHelper.findAmbExpedientTipusICodi(
                    expedientTipus,
                    terminiCodi);
        } else {
            termini = terminiHelper.findAmbDefinicioProcesICodi(
                    definicioProces,
                    terminiCodi);
        }
        if (termini == null)
            throw new NoTrobatException(Termini.class, terminiCodi);

        if (dies != null && mesos != null && anys != null) {
            terminiHelper.iniciar(
                    termini.getId(),
                    processInstanceId,
                    data,
                    anys,
                    mesos,
                    dies,
                    esDataFi,
                    false);
        } else {
            terminiHelper.iniciar(
                    termini.getId(),
                    processInstanceId,
                    data,
                    esDataFi,
                    false);
        }

    }

    @Override
    public void terminiPausar(
            Long terminiIniciatId,
            Date data) {
        logger.debug("Pausant termini iniciat (" +
                "terminiIniciatId=" + terminiIniciatId + ", " +
                "data=" + data + ")");
        TerminiIniciat termini = terminiHelper.findTerminiIniciatById(terminiIniciatId);
        if (termini == null)
            throw new NoTrobatException(TerminiIniciat.class, terminiIniciatId);
        terminiHelper.pausar(terminiIniciatId, data, false);
    }

    @Override
    public void terminiContinuar(
            Long terminiIniciatId,
            Date data) {
        logger.debug("Continuant termini iniciat (" +
                "terminiIniciatId=" + terminiIniciatId + ", " +
                "data=" + data + ")");
        TerminiIniciat termini = terminiHelper.findTerminiIniciatById(terminiIniciatId);
        if (termini == null)
            throw new NoTrobatException(TerminiIniciat.class, terminiIniciatId);
        terminiHelper.continuar(terminiIniciatId, data, false);
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
    public Date terminiCalcularDataInici(
            Date fi,
            int anys,
            int mesos,
            int dies,
            boolean laborable,
            String processInstanceId) {
        logger.debug("Calculant data d'inici de termini a partir d'una data de fi (" +
                "fi=" + fi + ", " +
                "anys=" + anys + ", " +
                "mesos=" + mesos + ", " +
                "dies=" + dies + ", " +
                "laborable=" + laborable + ")");
        return terminiHelper.getDataIniciTermini(
                fi,
                anys,
                mesos,
                dies,
                laborable,
                processInstanceId);
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
        Entorn entorn = entornRepository.findById(entornId)
                .orElseThrow(() -> new NoTrobatException(Entorn.class, entornId));
        Expedient expedient = expedientRepository.findById(expedientId)
                .orElseThrow(() -> new NoTrobatException(Expedient.class, expedientId));

        alertaHelper.crearAlerta(entorn, expedient, data, usuariCodi, text);
    }

    @Override
    public void alertaEsborrarAmbTaskInstanceId(long taskInstanceId) {
        logger.debug("Esborrant alertes amb taskInstance (" +
                "taskInstanceId=" + taskInstanceId + ")");
        Date ara = new Date();
        List<TerminiIniciat> terminis = terminiIniciatRepository.findByTaskInstanceId(
                new Long(taskInstanceId).toString());
        for (TerminiIniciat termini: terminis) {
            for (Alerta alerta: termini.getAlertes()) {
                alerta.setDataEliminacio(ara);
            }
        }
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
        return conversioTipusServiceHelper.convertirList(
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
        Entorn entorn = entornRepository.findById(entornId)
                .orElseThrow(() -> new NoTrobatException(Entorn.class, entornId));
        ExpedientTipus expedientTipus = expedientTipusRepository.findByEntornAndCodi(
                entorn,
                expedientTipusCodi);
        if (expedientTipus == null)
            throw new NoTrobatException(ExpedientTipus.class, expedientTipusCodi);
        return conversioTipusServiceHelper.convertir(
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

        Expedient expedient = expedientRepository.getById(interessat.getExpedientId());

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
                InteressatTipusEnumDto.valueOf(interessat.getTipus().toString()),
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

        Expedient expedient = expedientRepository.getById(interessat.getExpedientId());

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
        interessatEntity.setTipus(InteressatTipusEnumDto.valueOf(interessat.getTipus().toString()));
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
    public void interessatEliminar(String interessatCodi, Long expedientId) {

        Expedient expedient = expedientRepository.getById(expedientId);

        if (interessatRepository.findByCodiAndExpedient(interessatCodi, expedient) == null) {
            throw new ValidacioException("Un interessat amb aquest codi no existeix");
        }

        Interessat interessatEntity = interessatRepository.findByCodiAndExpedient(
                interessatCodi,
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
                    conversioTipusServiceHelper.convertirList(
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
        return globalProperties.getProperty(propertyName);
    }

    @Override
    public String getUsuariCodiActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }

    @Override
    public ExpedientDto getExpedientIniciant() {
        logger.debug("Obtenint expedient en fase d'inici");
        return conversioTipusServiceHelper.convertir(
                ThreadLocalInfo.getExpedient(),
                ExpedientDto.class);
    }

    @Override
    public List<FestiuDto> getFestiusAll() {
        logger.debug("Obtenint la llista de tots els festius");
        return conversioTipusServiceHelper.convertirList(
                festiuRepository.findAll(),
                FestiuDto.class);
    }

    @Override
    public ReassignacioDto findReassignacioActivaPerUsuariOrigen(
            String processInstanceId,
            String usuariCodi) {
        logger.debug("Obtenint reassignació activa per a l'usuari ("
                + "processInstanceId=" + processInstanceId + ", "
                + "usuariCodi=" + usuariCodi + ")");
        Date ara = new Date();

        Reassignacio reassignacio = null;
        // Cerca primer pel tipus d'expedient
        if (processInstanceId != null && !"".equals(processInstanceId.trim())) {
            Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
            if (expedient != null) {
                reassignacio = reassignacioRepository.findByUsuariAndTipusExpedientId(
                        usuariCodi,
                        expedient.getTipus().getId(),
                        ara,
                        ara);
            }
        }
        // Si no es troba cerca una redirecció global
        if (reassignacio == null) {
            reassignacio = reassignacioRepository.findByUsuari(
                    usuariCodi,
                    ara,
                    ara);
        }
        return conversioTipusServiceHelper.convertir(
                reassignacio,
                ReassignacioDto.class);
    }

    // DEFINICIONS DE PROCES
    ////////////////////////////////////////////////////////////////////////////////

    @Override
    public Integer getDefinicioProcesVersioAmbJbpmKeyIProcessInstanceId(
            String jbpmKey,
            String processInstanceId) {
        logger.debug("Obtenint la darrera versió de la definició de procés donat el codi jBPM i el processInstanceId (jbpmKey=" + jbpmKey + ", processInstanceId=" + processInstanceId +")");
        Expedient expedient = getExpedientDonatProcessInstanceId(processInstanceId);
        DefinicioProces defincioProces = definicioProcesHelper.findDarreraVersioDefinicioProces(
                expedient.getTipus(),
                jbpmKey);
        if (defincioProces != null) {
            return defincioProces.getVersio();
        }
        return null;
    }

    @Override
    public DefinicioProcesDto getDefinicioProcesPerProcessInstanceId(String processInstanceId) {
        logger.debug("Obtenint la definició de procés donada la instància de procés (processInstanceId=" + processInstanceId + ")");
        return conversioTipusServiceHelper.convertir(
                getDefinicioProcesDonatProcessInstanceId(processInstanceId),
                DefinicioProcesDto.class);
    }

    @Override
    public Long getDefinicioProcesIdPerProcessInstanceId(String processInstanceId) {
        DefinicioProces definicioProces = getDefinicioProcesDonatProcessInstanceId(processInstanceId);
        if (definicioProces == null)
            return null;
        return definicioProces.getId();
    }

    @Override
    public Long getDefinicioProcesEntornAmbJbpmKeyIVersio(
            String jbpmKey,
            Integer version) {
        logger.debug("Obtenint la definició de procés donat el codi jBPM i la versió (jbpmKey=" + jbpmKey + ", version=" + version +")");
        DefinicioProces defincioProces = definicioProcesRepository.findByJbpmKeyAndVersio(
                        jbpmKey,
                        version);
        if (defincioProces != null && defincioProces.getEntorn() != null) {
            return defincioProces.getEntorn().getId();
        }
        return null;
    }

    @Override
    public Long getDarreraVersioEntornAmbEntornIJbpmKey(
            Long entornId,
            String jbpmKey) {
        logger.debug("Obtenint la darrera versió de la definició de procés donat l'entorn i el codi jBPM (entornId=" + entornId + ", jbpmKey=" + jbpmKey + ")");
        DefinicioProces defincioProces = definicioProcesRepository.findDarreraVersioAmbEntornIJbpmKey(
                        entornId,
                        jbpmKey);
        if (defincioProces != null && defincioProces.getEntorn() != null) {
            return defincioProces.getEntorn().getId();
        }
        return null;
    }

    @Override
    public void initializeDefinicionsProces() {
        expedientTipusHelper.initializeDefinicionsProces();
    }

    @Override
    public String getProcessDefinitionIdHeretadaAmbPid(String processInstanceId) {
        return herenciaHelper.getProcessDefinitionIdHeretadaAmbPid(processInstanceId);
    }

    @Override
    public CampTipusIgnored getCampAndIgnored(
            String processDefinitionId,
            Long expedientId,
            String varCodi) {

        Camp camp = null;
        boolean ignored = false;

        DefinicioProces pDef = definicioProcesRepository.findByJbpmId(processDefinitionId);
        Expedient expedient = expedientRepository.getById(expedientId);
        ExpedientTipus expedientTipus = expedient != null ? expedient.getTipus() : null;
        if(varCodi.startsWith(Constants.PREFIX_DOCUMENT)) {
            // Document
            varCodi = varCodi.substring((Constants.PREFIX_DOCUMENT).length());
            // Cerca el document per veure si està marcat per ignorar
            Document document = null;
            if (expedientTipus != null && expedientTipus.isAmbInfoPropia()) {
                document = documentRepository.findByExpedientTipusAndCodi(
                        expedientTipus.getId(),
                        varCodi,
                        expedientTipus.getExpedientTipusPare() != null);
            } else {
                document = documentRepository.findByDefinicioProcesAndCodi(
                        pDef,
                        varCodi);
            }
            if(document != null){
                ignored = document.isIgnored();
            }
        } else {
            // Variable
            if (expedientTipus != null && expedientTipus.isAmbInfoPropia()) {
                camp = campRepository.findByExpedientTipusAndCodi(
                        expedientTipus.getId(),
                        varCodi,
                        true);
            } else {
                camp = campRepository.findByDefinicioProcesAndCodi(
                        pDef,
                        varCodi);
            }
            if (camp != null) {
                ignored = camp.isIgnored();
            }

        }

        CampTipusIgnored campTipusIgnored = new CampTipusIgnored();
        campTipusIgnored.setIgnored(ignored);
        if (camp != null) {
            campTipusIgnored.setTipus(CampTipusDto.valueOf(camp.getTipus().name()));
        }
        return campTipusIgnored;
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
        if (interessat.getTipus() == es.caib.helium.logic.intf.integracio.notificacio.InteressatTipusEnum.FISICA 
        		&& (interessat.getLlinatge1() == null || interessat.getLlinatge1().isEmpty())) {
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
