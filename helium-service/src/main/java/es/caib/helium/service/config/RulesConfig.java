package es.caib.helium.service.config;

import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.core.DefaultRulesEngine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import es.caib.helium.commons.regles.accio.RuleAccio;
import es.caib.helium.commons.regles.que.RuleQue;
import es.caib.helium.service.qui.RuleQui;

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
