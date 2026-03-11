
package es.caib.helium.service.qui;

import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Rule;
import org.jeasy.rules.api.Facts;

import es.caib.helium.commons.dto.regles.QuiEnum;
import es.caib.helium.commons.dto.regles.VariableFact;

@Rule(priority = 2)
public class RuleQuiUsuari {

    @Condition
    public boolean when(@Fact("fact") VariableFact fact) {
        if (QuiEnum.USUARI.equals(fact.getQui()) && fact.getQuiValors().contains(fact.getUsuariCodi()))
            return true;
        return false;
    }

    @Action
    public void then(Facts facts) {
        VariableFact fact = facts.get("fact");
        fact.setAplicaReglaQui(true);
    }

}
