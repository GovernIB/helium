package net.conselldemallorca.helium.v3.core.regles;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.Document;
import net.conselldemallorca.helium.core.model.hibernate.Estat;
import net.conselldemallorca.helium.core.model.hibernate.EstatRegla;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.Termini;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.regles.CampFormProperties;
import net.conselldemallorca.helium.v3.core.api.dto.regles.QueEnum;
import net.conselldemallorca.helium.v3.core.api.dto.regles.TipusVarEnum;
import net.conselldemallorca.helium.v3.core.api.dto.regles.VariableFact;
import net.conselldemallorca.helium.v3.core.repository.CampRepository;
import net.conselldemallorca.helium.v3.core.repository.DocumentRepository;
import net.conselldemallorca.helium.v3.core.repository.EstatReglaRepository;
import net.conselldemallorca.helium.v3.core.repository.TerminiRepository;

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
        for(Camp dada: dades) {
            CampFormProperties campFormProperties = getDefaultCampFormProperties();
            for(EstatRegla regla: regles) {
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
                        .visible(campFormProperties.isVisible())
                        .editable(campFormProperties.isEditable())
                        .obligatori(campFormProperties.isObligatori())
                        .obligatoriEntrada(campFormProperties.isObligatoriEntrada())
                        .build();
                facts.put("fact", variableFact);
                rulesEngine.fire(rules, facts);
                campFormProperties = getCampFormProperties(variableFact);
            }
            campFormPropertiesMap.put(dada.getCodi(), campFormProperties);
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
        for(Document document: documents) {
            CampFormProperties campFormProperties = getDefaultCampFormProperties();
            for(EstatRegla regla: regles) {
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
                        .visible(campFormProperties.isVisible())
                        .editable(campFormProperties.isEditable())
                        .obligatori(campFormProperties.isObligatori())
                        .build();
                facts.put("fact", variableFact);
                rulesEngine.fire(rules, facts);
                campFormProperties = getCampFormProperties(variableFact);
            }
            campFormPropertiesMap.put(document.getCodi(), campFormProperties);
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
        for(Termini termini: terminis) {
            CampFormProperties campFormProperties = getDefaultCampFormProperties();
            for(EstatRegla regla: regles) {
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
                        .visible(campFormProperties.isVisible())
                        .editable(campFormProperties.isEditable())
                        .obligatori(campFormProperties.isObligatori())
                        .build();
                facts.put("fact", variableFact);
                rulesEngine.fire(rules, facts);
                campFormProperties = getCampFormProperties(variableFact);
            }
            campFormPropertiesMap.put(termini.getCodi(), campFormProperties);
        }

        return campFormPropertiesMap;
    }

    private CampFormProperties getCampFormProperties(VariableFact fact) {
        return CampFormProperties.builder()
                .visible(fact.isVisible())
                .editable(fact.isEditable())
                .obligatori(fact.isObligatori())
                .obligatoriEntrada(fact.isObligatoriEntrada())
                .build();
    }

    private CampFormProperties getDefaultCampFormProperties() {
        return CampFormProperties.builder()
                .visible(true)
                .editable(true)
                .obligatori(false)
                .obligatoriEntrada(false)
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

    /** Mètode comú per actualitzar les regles d'un tipus d'expedient quan es canviï el codi o etiqueta d'un
     * camp, document, agrupació o termini.
     * 
     * @param expedientTipus
     * @param estatReglaValor
     * @param newEstatReglaValor
     * @param dada
     */
    @Transactional
	public void updateReglaValor(
			ExpedientTipus expedientTipus, 
			String estatReglaValor, 
			String newEstatReglaValor, 
			QueEnum dada) {

		// Si canvia el nom actualitza les regles que hi facin referència
		if (expedientTipus != null 
				&& expedientTipus.getTipus() == ExpedientTipusTipusEnumDto.ESTAT 
				&& !newEstatReglaValor.equals(estatReglaValor)) {
			for (EstatRegla regla : estatReglaRepository.findByExpedientTipusAndValor(expedientTipus, dada, estatReglaValor)) {
				if (regla.getQueValor().contains(estatReglaValor)) {
					regla.getQueValor().remove(estatReglaValor);
					regla.getQueValor().add(newEstatReglaValor);
				}
			}
		}		
	}
    

    /** Mètode comú per esborrar de les regles d'un tipus d'expedient els valors quan s'elimini un
     * camp, document, agrupació o termini.
     * 
     * @param expedientTipus
     * @param estatReglaValor
     * @param newEstatReglaValor
     * @param dada
     */
    @Transactional
	public void deleteReglaValor(
			ExpedientTipus expedientTipus, 
			String estatReglaValor, 
			QueEnum dada) {

		// Si canvia el nom actualitza les regles que hi facin referència
		if (expedientTipus != null 
				&& expedientTipus.getTipus() == ExpedientTipusTipusEnumDto.ESTAT) {
			for (EstatRegla regla : estatReglaRepository.findByExpedientTipusAndValor(expedientTipus, dada, estatReglaValor)) {
				Iterator<String> valorI = regla.getQueValor().iterator();
				while (valorI.hasNext()) {
					String valor = valorI.next();
					if (valor.equals(estatReglaValor)) {
						valorI.remove();
					}
				}
			}
		}
    }
}
