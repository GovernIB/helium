
package net.conselldemallorca.helium.v3.core.regles.accio;

import net.conselldemallorca.helium.v3.core.api.dto.regles.AccioEnum;
import net.conselldemallorca.helium.v3.core.api.dto.regles.VariableFact;
import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Rule;
import org.jeasy.rules.api.Facts;

@Rule(priority = 24)
public class RuleAccioBloquejar {

    @Condition
    public boolean when(@Fact("fact") VariableFact fact) {
        if (fact.isAplicaReglaQue() && AccioEnum.BLOQUEJAR.equals(fact.getAccio()))
            return true;
        return false;
    }

    @Action
    public void then(Facts facts) {
        VariableFact fact = facts.get("fact");
        fact.setVisible(true);
        fact.setEditable(false);
    }

}