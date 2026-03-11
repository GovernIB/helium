package es.caib.helium.commons.regles.que;

import org.jeasy.rules.support.ActivationRuleGroup;

public class RuleQue extends ActivationRuleGroup {

    public RuleQue() {
        addRule(new RuleQueTot());
        addRule(new RuleQueDades());
        addRule(new RuleQueDocuements());
        addRule(new RuleQueAgrupacio());
        addRule(new RuleQueDada());
        addRule(new RuleQueDocument());
        addRule(new RuleQueTermini());
        addRule(new RuleQueTerminis());
    }

    @Override
    public int getPriority() {
        return 10;
    }
}
