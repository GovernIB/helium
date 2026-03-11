
package es.caib.helium.commons.regles.que;

import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Rule;
import org.jeasy.rules.api.Facts;

import es.caib.helium.commons.dto.regles.QueEnum;
import es.caib.helium.commons.dto.regles.VariableFact;

@Rule(priority = 11)
public class RuleQueTot {

    @Condition
    public boolean when(@Fact("fact") VariableFact fact) {
        if (fact.isAplicaReglaQui() && QueEnum.TOT.equals(fact.getQue()))
            return true;
        return false;
    }

    @Action
    public void then(Facts facts) {
        VariableFact fact = facts.get("fact");
        fact.setAplicaReglaQue(true);
    }

}
