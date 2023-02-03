package net.conselldemallorca.helium.v3.core.regles;

import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.Document;
import net.conselldemallorca.helium.core.model.hibernate.Estat;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.EstatRegla;
import net.conselldemallorca.helium.core.model.hibernate.Termini;
import net.conselldemallorca.helium.v3.core.api.dto.regles.AccioEnum;
import net.conselldemallorca.helium.v3.core.api.dto.regles.CampFormProperties;
import net.conselldemallorca.helium.v3.core.api.dto.regles.QueEnum;
import net.conselldemallorca.helium.v3.core.api.dto.regles.QuiEnum;
import net.conselldemallorca.helium.v3.core.api.dto.regles.TipusVarEnum;
import net.conselldemallorca.helium.v3.core.api.dto.regles.VariableFact;
import net.conselldemallorca.helium.v3.core.repository.CampRepository;
import net.conselldemallorca.helium.v3.core.repository.DocumentRepository;
import net.conselldemallorca.helium.v3.core.repository.EstatReglaRepository;
import net.conselldemallorca.helium.v3.core.repository.TerminiRepository;
import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class ReglaHelper {

    @Autowired
    private Rules rules;
    @Autowired
    private RulesEngine rulesEngine;
//    @Autowired
//    private DominiHelper dominiHelper;
    @Autowired
    private EstatReglaRepository estatReglaRepository;
    @Autowired
    private CampRepository campRepository;
    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private TerminiRepository terminiRepository;

    public Map<String, CampFormProperties> getCampFormProperties(ExpedientTipus expedientTipus, Estat estat) {
        Map<String, CampFormProperties> campFormPropertiesMap = new HashMap<String, CampFormProperties>();
        if (estat == null)
            return campFormPropertiesMap;

        List<EstatRegla> regles = estatReglaRepository.findByEstatOrderByOrdreAsc(estat);
        if (regles == null || regles.isEmpty())
            return campFormPropertiesMap;
        List<Camp> dades = campRepository.findByExpedientTipusOrderByCodiAsc(expedientTipus);
        if (dades == null || dades.isEmpty())
            return campFormPropertiesMap;

        String usuariCodi = SecurityContextHolder.getContext().getAuthentication().getName();
        Set<String> usuariRols = new HashSet<String>();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        for (GrantedAuthority ga: auth.getAuthorities())
            usuariRols.add(ga.getAuthority());

        // Si alguna regla és per càrrec
//        boolean hasCarrecRegla = false;
//        for (EstatRegla regla: regles) {
//            if (QuiEnum.CARREC.equals(regla.getQui())) {
//                hasCarrecRegla = true;
//                break;
//            }
//        }
//        Set<String> usuariCarrecIds = new HashSet<String>();
//        if (hasCarrecRegla) {
//            List<Carrec> carrecs = dominiHelper.getCarrecsAmbPersonaCodi(expedientTipus.getEntorn().getCodi(), usuariCodi);
//            for (Carrec carrec : carrecs)
//                usuariCarrecIds.add(carrec.getCodi());
//        }

        Facts facts = new Facts();
        for(EstatRegla regla: regles) {
            for(Camp dada: dades) {
                VariableFact variableFact = VariableFact.builder()
                        .qui(regla.getQui())
                        .quiValors(getCodiValors(regla.getQuiValor()))
                        .que(regla.getQue())
                        .queValors(getCodiValors(regla.getQueValor()))
                        .accio(regla.getAccio())
                        .usuariCodi(usuariCodi)
                        .usuariRols(usuariRols)
//                        .usuariCarrecIds(usuariCarrecIds)
                        .tipus(TipusVarEnum.DADA)
                        .varCodi(dada.getCodi())
                        .agrupacioCodi(dada.getAgrupacio() != null ? dada.getAgrupacio().getCodi() : null)
                        .build();
                facts.put("fact", variableFact);
                rulesEngine.fire(rules, facts);
                campFormPropertiesMap.put(dada.getCodi(), getCampFormProperties(variableFact));
            }
        }

        return campFormPropertiesMap;
    }

    public Map<String, CampFormProperties> getDocumentFormProperties(ExpedientTipus expedientTipus, Estat estat) {
        Map<String, CampFormProperties> campFormPropertiesMap = new HashMap<String, CampFormProperties>();
        if (estat == null)
            return campFormPropertiesMap;

        List<EstatRegla> regles = estatReglaRepository.findByEstatOrderByOrdreAsc(estat);
        if (regles == null || regles.isEmpty())
            return campFormPropertiesMap;
        List<Document> documents = documentRepository.findByExpedientTipusId(expedientTipus.getId());
        if (documents == null || documents.isEmpty())
            return campFormPropertiesMap;

        String usuariCodi = SecurityContextHolder.getContext().getAuthentication().getName();
        Set<String> usuariRols = new HashSet<String>();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        for (GrantedAuthority ga: auth.getAuthorities())
            usuariRols.add(ga.getAuthority());

        Facts facts = new Facts();
        for(EstatRegla regla: regles) {
            for(Document document: documents) {
                VariableFact variableFact = VariableFact.builder()
                        .qui(regla.getQui())
                        .quiValors(getCodiValors(regla.getQuiValor()))
                        .que(regla.getQue())
                        .queValors(getCodiValors(regla.getQueValor()))
                        .accio(regla.getAccio())
                        .usuariCodi(usuariCodi)
                        .usuariRols(usuariRols)
                        .tipus(TipusVarEnum.DOCUMENT)
                        .varCodi(document.getCodi())
                        .agrupacioCodi(null)
                        .build();
                facts.put("fact", variableFact);
                rulesEngine.fire(rules, facts);
                campFormPropertiesMap.put(document.getCodi(), getCampFormProperties(variableFact));
            }
        }

        return campFormPropertiesMap;
    }

    public Map<String, CampFormProperties> getTerminisFormProperties(ExpedientTipus expedientTipus, Estat estat) {
        Map<String, CampFormProperties> campFormPropertiesMap = new HashMap<String, CampFormProperties>();
        if (estat == null)
            return campFormPropertiesMap;

        List<EstatRegla> regles = estatReglaRepository.findByEstatOrderByOrdreAsc(estat);
        if (regles == null || regles.isEmpty())
            return campFormPropertiesMap;
        List<Termini> terminis = terminiRepository.findByExpedientTipus(expedientTipus.getId());
        if (terminis == null || terminis.isEmpty())
            return campFormPropertiesMap;

        String usuariCodi = SecurityContextHolder.getContext().getAuthentication().getName();
        Set<String> usuariRols = new HashSet<String>();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        for (GrantedAuthority ga: auth.getAuthorities())
            usuariRols.add(ga.getAuthority());

        Facts facts = new Facts();
        for(EstatRegla regla: regles) {
            for(Termini termini: terminis) {
                VariableFact variableFact = VariableFact.builder()
                        .qui(regla.getQui())
                        .quiValors(getCodiValors(regla.getQuiValor()))
                        .que(regla.getQue())
                        .queValors(getCodiValors(regla.getQueValor()))
                        .accio(regla.getAccio())
                        .usuariCodi(usuariCodi)
                        .usuariRols(usuariRols)
                        .tipus(TipusVarEnum.TERMINI)
                        .varCodi(termini.getCodi())
                        .agrupacioCodi(null)
                        .build();
                facts.put("fact", variableFact);
                rulesEngine.fire(rules, facts);
                campFormPropertiesMap.put(termini.getCodi(), getCampFormProperties(variableFact));
            }
        }

        return campFormPropertiesMap;
    }

    private CampFormProperties getCampFormProperties(VariableFact fact) {
        return CampFormProperties.builder()
                .visible(fact.isVisible())
                .editable(fact.isEditable())
                .obligatori(fact.isObligatori())
                .build();
    }

    private Set<String> getCodiValors(Set<String> valors) {
        if (valors == null)
            return null;

        Set<String> codis = new HashSet<String>();
        for(String valor: valors) {
            codis.add(valor.split(" \\| ")[0]);
        }
        return codis;
    }
}
