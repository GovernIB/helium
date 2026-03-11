package es.caib.helium.commons.regles.que;

import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Rule;
import org.jeasy.rules.api.Facts;

import es.caib.helium.commons.dto.regles.QueEnum;
import es.caib.helium.commons.dto.regles.TipusVarEnum;
import es.caib.helium.commons.dto.regles.VariableFact;

@Rule(priority = 18)
public class RuleQueTermini {

    @Condition
    public boolean when(@Fact("fact") VariableFact fact) {
        if (fact.isAplicaReglaQui() && QueEnum.TERMINI.equals(fact.getQue()) && fact.getQueValors().contains(fact.getVarCodi()) && TipusVarEnum.TERMINI.equals(fact.getTipus()))
            return true;
        return false;
    }

    @Action
    public void then(Facts facts) {
        VariableFact fact = facts.get("fact");
        fact.setAplicaReglaQue(true);
    }

}
