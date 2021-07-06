package es.caib.helium.back.rest.model;

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
