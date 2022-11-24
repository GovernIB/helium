package net.conselldemallorca.helium.v3.core.regles.qui;

import net.conselldemallorca.helium.v3.core.api.dto.regles.QuiEnum;
import net.conselldemallorca.helium.v3.core.api.dto.regles.VariableFact;
import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Rule;
import org.jeasy.rules.api.Facts;

@Rule(priority = 1)
public class RuleQuiTothom {

    @Condition
    public boolean when(@Fact("fact") VariableFact fact) {
        return QuiEnum.TOTHOM.equals(fact.getQui());
    }

    @Action
    public void then(Facts facts) {
        VariableFact fact = facts.get("fact");
        fact.setAplicaReglaQui(true);
    }
}
