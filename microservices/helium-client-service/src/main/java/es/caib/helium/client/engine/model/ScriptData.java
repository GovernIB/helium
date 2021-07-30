package es.caib.helium.client.engine.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScriptData {

    private String scriptLanguage;
    private String script;
    private Set<String> outputNames;

}
