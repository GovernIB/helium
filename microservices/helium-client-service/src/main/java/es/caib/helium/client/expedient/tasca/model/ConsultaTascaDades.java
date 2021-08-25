package es.caib.helium.client.expedient.tasca.model;

import java.util.Collection;
import java.util.Date;

import javax.json.bind.annotation.JsonbDateFormat;
import javax.validation.constraints.NotNull;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;

import es.caib.helium.client.model.PagedSortedRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/** Classe per ecapsular els paràmetres de la consulta de tasques al microservei 
 * d'expedients i tasques.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 *
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ConsultaTascaDades extends PagedSortedRequest {

    @NotNull
    private Long entornId;
    private Long expedientTipusId;
    private String usuariAssignat;
    private String nom;
    private String titol;
    private Long expedientId;
    private String expedientTitol;
    private String expedientNumero;
	@DateTimeFormat(pattern="yyyy-MM-dd")
    private Date dataCreacioInici;
	@DateTimeFormat(pattern="yyyy-MM-dd")
    private Date dataCreacioFi;
	@DateTimeFormat(pattern="yyyy-MM-dd")
    private Date dataLimitInici;
	@DateTimeFormat(pattern="yyyy-MM-dd")
    private Date dataLimitFi;
    boolean mostrarAssignadesUsuari;
    boolean mostrarAssignadesGrup;
    boolean nomesPendents;
    private String filtre;
}