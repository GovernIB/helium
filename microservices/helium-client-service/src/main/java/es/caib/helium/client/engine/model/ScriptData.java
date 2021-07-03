package es.caib.helium.client.engine.model;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class ScriptData {

    private String scriptLanguage;
    private String script;
    private Set<String> outputNames;

}
