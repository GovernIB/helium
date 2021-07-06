package es.caib.helium.client.domini.entorn.model;

import es.caib.helium.client.domini.entorn.enums.OrigenCredencialsEnum;
import es.caib.helium.client.domini.entorn.enums.TipusAuthEnum;
import es.caib.helium.client.domini.entorn.enums.TipusDominiEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class DominiDto {

	private Long id = null;
	private String codi = null;
	private String nom = null;
	private TipusDominiEnum tipus = null;
	private String url = null;
	private TipusAuthEnum tipusAuth = null;
	private OrigenCredencialsEnum origenCredencials = null;
	private String usuari = null;
	private String contrasenya = null;
	private String sql = null;
	private String jndiDatasource = null;
	private String descripcio = null;
	private Integer cacheSegons = null;
	private Integer timeout = null;
	private String ordreParams = null;
	private Long entorn = null;
	private Long expedientTipus = null;

	/// Propietats de consulta d'herència
	
	private boolean heretat = false;
	private boolean sobreescriu = false;
}
