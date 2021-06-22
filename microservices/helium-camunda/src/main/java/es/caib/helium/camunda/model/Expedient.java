/**
 * 
 */
package es.caib.helium.camunda.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;


/**
 * DTO amb informació d'un expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Expedient implements Serializable {
	
	private Long id;
	private String processInstanceId;
	private String titol;
	private String numero;
	private String numeroDefault;
	private Date dataInici = new Date();
	private Date dataFi;

	private static final long serialVersionUID = 6251228896531929541L;

}
