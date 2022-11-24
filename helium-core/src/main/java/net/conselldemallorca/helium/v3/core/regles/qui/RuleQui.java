package net.conselldemallorca.helium.v3.core.regles.qui;

import org.jeasy.rules.support.ActivationRuleGroup;

public class RuleQui extends ActivationRuleGroup {

    public RuleQui() {
        addRule(new RuleQuiTothom());
        addRule(new RuleQuiUsuari());
        addRule(new RuleQuiRol());
//        addRule(new RuleQuiCarrec());
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
