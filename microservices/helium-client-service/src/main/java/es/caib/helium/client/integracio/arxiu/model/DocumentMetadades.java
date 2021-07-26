package es.caib.helium.client.integracio.arxiu.model;

import es.caib.helium.client.integracio.arxiu.enums.ContingutOrigen;
import es.caib.helium.client.integracio.arxiu.enums.DocumentEstatElaboracio;
import es.caib.helium.client.integracio.arxiu.enums.DocumentExtensio;
import es.caib.helium.client.integracio.arxiu.enums.DocumentFormat;
import es.caib.helium.client.integracio.arxiu.enums.DocumentTipus;
import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class DocumentMetadades {

    private String identificador;
    private String versioNti;
    private ContingutOrigen origen;
    private List<String> organs;
    private Date dataCaptura;
    private DocumentEstatElaboracio estatElaboracio;
    private DocumentTipus tipusDocumental;
    private DocumentFormat format;
    private DocumentExtensio extensio;
    private String identificadorOrigen;
    private Map<String, Object> metadadesAddicionals;
    String serieDocumental;
    private String csv;
    private String csvDef;
    private String tipusDocumentalAddicional;

    public Object getMetadadaAddicional(String clau) {
        return this.metadadesAddicionals == null ? null : this.metadadesAddicionals.get(clau);
    }

    public void addMetadadaAddicional(String clau, Object valor) {
        if (this.metadadesAddicionals == null) {
            this.metadadesAddicionals = new HashMap();
        }

        this.metadadesAddicionals.put(clau, valor);
    }
}
