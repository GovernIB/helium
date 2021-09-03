package es.caib.helium.client.engine.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReassignTaskData {

    public static enum ScriptLanguage {
        JUEL_SCRIPTING_LANGUAGE("juel"),
        GROOVY_SCRIPTING_LANGUAGE("groovy"),
        JAVASCRIPT_SCRIPTING_LANGUAGE("javascript"),
        ECMASCRIPT_SCRIPTING_LANGUAGE("ecmascript");

        public final String language;

        private ScriptLanguage(String language) {
            this.language = language;
        }
    }

    private ScriptLanguage expressionLanguage;
    private String expression;
    private Long entornId;

}
