package es.caib.helium.client.expedient.expedient.model;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import es.caib.helium.client.model.PagedSortedRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/** Classe per ecapsular els paràmetres de la consulta d'expedients al microservei 
 * d'expedients i tasques.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 *
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ConsultaExpedientDades extends PagedSortedRequest {

    @NotNull
    private Long entornId;
    private String filtreRsql;
    private String actorId;
    private List<String> grups;
    private Collection<Long> tipusIdPermesos;
    private String titol;
    private String numero;
    private Long tipusId;
	@DateTimeFormat(pattern="yyyy-MM-dd")
    private Date dataCreacioInici;
	@DateTimeFormat(pattern="yyyy-MM-dd")
    private Date dataCreacioFi;
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date dataFiInici;
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date dataFiFi;
    private Long estatId;
    private Double geoPosX;
    private Double geoPosY;
    private String geoReferencia;
    private boolean nomesIniciats;
    private boolean nomesFinalitzats;
    private boolean mostrarAnulats;
    private boolean mostrarNomesAnulats;
    private boolean nomesAlertes;
    private boolean nomesErrors;
    private boolean nomesTasquesPersonals;
    private boolean nomesTasquesGrup;
    private boolean nomesTasquesMeves;
    private boolean nomesCount;
}