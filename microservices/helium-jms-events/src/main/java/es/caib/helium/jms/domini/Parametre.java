package es.caib.helium.jms.domini;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Parametre {

	@NotNull
	private String nom;
	private String valor;
}
