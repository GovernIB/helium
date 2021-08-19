package es.caib.helium.client.expedient.proces.model;

import es.caib.helium.client.model.PagedSortedRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/** Classe per encapsular els paràmetres de la consulta de processos del
 * micro servei d'expedients i tasques.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 *
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ConsultaProcesDades extends PagedSortedRequest {

    private String processDefinitionId;
    private String procesArrelId;
    private String filtreRsql;
}