
package net.conselldemallorca.helium.v3.core.regles.que;

import net.conselldemallorca.helium.v3.core.api.dto.regles.QueEnum;
import net.conselldemallorca.helium.v3.core.api.dto.regles.TipusVarEnum;
import net.conselldemallorca.helium.v3.core.api.dto.regles.VariableFact;
import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Rule;
import org.jeasy.rules.api.Facts;

@Rule(priority = 13)
public class RuleQueDocuements {

    @Condition
    public boolean when(@Fact("fact") VariableFact fact) {
        if (fact.isAplicaReglaQui() && QueEnum.DOCUMENTS.equals(fact.getQue()) && TipusVarEnum.DOCUMENT.equals(fact.getTipus()))
            return true;
        return false;
    }

    @Action
    public void then(Facts facts) {
        VariableFact fact = facts.get("fact");
        fact.setAplicaReglaQue(true);
    }

}
