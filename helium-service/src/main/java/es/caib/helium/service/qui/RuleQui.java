package es.caib.helium.service.qui;

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
