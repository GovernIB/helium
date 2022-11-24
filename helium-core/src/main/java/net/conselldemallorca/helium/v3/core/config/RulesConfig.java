package net.conselldemallorca.helium.v3.core.config;

import net.conselldemallorca.helium.v3.core.regles.accio.RuleAccio;
import net.conselldemallorca.helium.v3.core.regles.que.RuleQue;
import net.conselldemallorca.helium.v3.core.regles.qui.RuleQui;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.core.DefaultRulesEngine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RulesConfig {

    @Bean
    public RulesEngine getRulesEngine() {
        RulesEngine rulesEngine = new DefaultRulesEngine();

        return rulesEngine;
    }

    @Bean
    public Rules getRules() {
        Rules rules = new Rules();
        rules.register(new RuleQui());
        rules.register(new RuleQue());
        rules.register(new RuleAccio());

        return rules;
    }


}
