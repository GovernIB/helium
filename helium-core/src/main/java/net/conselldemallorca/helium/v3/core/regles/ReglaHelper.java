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

        List<EstatRegla> regles = estatReglaRepository.findByExpedientTipusAndEstat(expedientTipus, estat);
        List<Camp> dades = campRepository.findByExpedientTipusOrderByCodiAsc(expedientTipus);
        List<Document> documents = documentRepository.findByExpedientTipusId(expedientTipus.getId());
        List<Termini> terminis = terminiRepository.findByExpedientTipus(expedientTipus.getId());


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
            QuiEnum qui = regla.getQui();
            QueEnum que = regla.getQue();
            AccioEnum accio = regla.getAccio();
            Set<String> quiValors = regla.getQuiValor(); // getReglaValors(regla.getQuiValor());
            Set<String> queValors = regla.getQueValor(); // getReglaValors(regla.getQueValor());

            for(Camp dada: dades) {
                VariableFact variableFact = VariableFact.builder()
                        .qui(qui)
                        .quiValors(quiValors)
                        .que(que)
                        .queValors(queValors)
                        .accio(accio)
                        .usuariCodi(usuariCodi)
                        .usuariRols(usuariRols)
//                        .usuariCarrecIds(usuariCarrecIds)
                        .tipus(TipusVarEnum.DADA)
                        .varCodi(dada.getCodi())
                        .agrupacioCodi(dada.getAgrupacio() != null ? dada.getAgrupacio().getCodi() : null)
                        .build();
                facts.put(dada.getCodi(), variableFact);
            }
            for(Document document: documents) {
                VariableFact variableFact = VariableFact.builder()
                        .qui(regla.getQui())
                        .quiValors(quiValors)
                        .que(regla.getQue())
                        .queValors(queValors)
                        .accio(regla.getAccio())
                        .usuariCodi(usuariCodi)
                        .usuariRols(usuariRols)
//                        .usuariCarrecIds(usuariCarrecIds)
                        .tipus(TipusVarEnum.DOCUMENT)
                        .varCodi(document.getCodi())
                        .agrupacioCodi(null)
                        .build();
                facts.put(document.getCodi(), variableFact);
            }
            for(Termini termini: terminis) {
                VariableFact variableFact = VariableFact.builder()
                        .qui(regla.getQui())
                        .quiValors(quiValors)
                        .que(regla.getQue())
                        .queValors(queValors)
                        .accio(regla.getAccio())
                        .usuariCodi(usuariCodi)
                        .usuariRols(usuariRols)
//                        .usuariCarrecIds(usuariCarrecIds)
                        .tipus(TipusVarEnum.TERMINI)
                        .varCodi(termini.getCodi())
                        .agrupacioCodi(null)
                        .build();
                facts.put(termini.getCodi(), variableFact);
            }
        }

        rulesEngine.fire(rules, facts);
        Map<String, CampFormProperties> campFormPropertiesMap = new HashMap<String, CampFormProperties>();
        while (facts.iterator().hasNext()) {
            Map.Entry<String, Object> fact = facts.iterator().next();
            campFormPropertiesMap.put(fact.getKey(), getCampFormProperties((VariableFact) fact.getValue()));
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
}
