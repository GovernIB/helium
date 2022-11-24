package net.conselldemallorca.helium.v3.core.regles.qui;

import com.google.common.collect.Sets;
import net.conselldemallorca.helium.v3.core.api.dto.regles.QuiEnum;
import net.conselldemallorca.helium.v3.core.api.dto.regles.VariableFact;
import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Rule;
import org.jeasy.rules.api.Facts;

@Rule(priority = 4)
public class RuleQuiCarrec {

    @Condition
    public boolean when(@Fact("fact") VariableFact fact) {
//        if (QuiEnum.CARREC.equals(fact.getQui())) {
//            boolean hasFactUserCarrec = !Sets.intersection(fact.getQuiValors(), fact.getUsuariCarrecIds()).isEmpty();
//            return hasFactUserCarrec;
//        }
        return false;
    }

    @Action
    public void then(Facts facts) {
        VariableFact fact = facts.get("fact");
        fact.setAplicaReglaQui(true);
    }

}
