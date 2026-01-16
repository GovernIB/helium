package net.conselldemallorca.helium.v3.core.api.dto.comanda.tasca;

import java.net.URL;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Representa una tasca publicada a COMANDA perquè sigui processada
 * asíncronament
 * 
 * @author Limit Tecnologies <limit@limit.es>
 *
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ComandaTasca {
	
	// Codi de l'aplicació que publica la tasca, example: "PORTAFIB"
	private String appCodi;

	// Codi de l'entorn de l'aplicació, example: "DEV"
	private String entornCodi;

	// Identificador únic de la tasca en l'àmbit de l'APP, example: "TAS-2025-0001"
	private String identificador;

	// Tipus funcional de la tasca, example: "GENERAR_INFORME"
	private String tipus;

	// Nom curt de la tasca, example: "Generar informe mensual"
	private String nom;

	// Descripció detallada de la tasca, example: "Generar l'informe mensual de consums per unitat"
	private String descripcio;

	// Estat de processament de la tasca
	private ComandaTascaEstat estat;

	// Descripció de l'estat actual, example: "En cua"
	private String estatDescripcio;

	// Número d'expedient relacionat (si aplica), example: "EXP-12345/2025"
	private String numeroExpedient;

	// Prioritat de la tasca")
	private ComandaPrioritat prioritat;

	// Data d'inici prevista o real, example: "2025-11-20T09:30:00Z"
	private String dataInici;

	// Data de finalització, example: "2025-11-20T10:00:00Z"
	private String dataFi;

	// Data de caducitat límit, example: "2025-12-01T00:00:00Z"
	private String dataCaducitat;

	// URL de redirecció per accedir a la tasca, example: "https://dev.caib.es/app/tasques/TAS-2025-0001"
	private URL redireccio;

	// Usuari responsable, example = "usr1234"
	private String responsable;

	// Grup responsable, example = "GESTORS"
	private String grup;

	// Llista d'usuaris amb permís, example: ["usr1", "usr2"]
	private List<String> usuarisAmbPermis;

	// Llista de grups amb permís, example: ["GESTORS", "SUPORT"]
	private List<String> grupsAmbPermis;
}
