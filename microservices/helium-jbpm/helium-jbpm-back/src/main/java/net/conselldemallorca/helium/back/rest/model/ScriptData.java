package net.conselldemallorca.helium.back.rest.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScriptData {

    private String scriptLanguage;
    private String script;
    private Set<String> outputNames;

}
