package es.caib.helium.client.integracio.arxiu.model;

import es.caib.helium.client.integracio.arxiu.enums.ExpedientEstat;
import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class ExpedientMetadades {

    private String identificador;
    private String versioNti;
    private List<String> organs;
    private Date dataObertura;
    private String classificacio;
    private ExpedientEstat estat;
    private List<String> interessats;
    private String serieDocumental;
    private Map<String, Object> metadadesAddicionals;

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
