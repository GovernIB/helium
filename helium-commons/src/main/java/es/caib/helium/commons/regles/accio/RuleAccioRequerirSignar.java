
package es.caib.helium.commons.regles.accio;

import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Rule;
import org.jeasy.rules.api.Facts;

import es.caib.helium.commons.dto.regles.AccioEnum;
import es.caib.helium.commons.dto.regles.VariableFact;

@Rule(priority = 30)
public class RuleAccioRequerirSignar {

    @Condition
    public boolean when(@Fact("fact") VariableFact fact) {
        if (fact.isAplicaReglaQue() && AccioEnum.SIGNAT.equals(fact.getAccio()))
            return true;
        return false;
    }

    @Action
    public void then(Facts facts) {
        VariableFact fact = facts.get("fact");
        fact.setVisible(true);
        fact.setObligatoriSignat(true);
    }

}
