/**
 * 
 */
package net.conselldemallorca.helium.api.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * Objecte de domini que representa un domini per fer consultes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
@NoArgsConstructor
@EqualsAndHashCode(of = {"codi", "entorn"})
public class DominiDto extends HeretableDto implements Serializable {

	public enum TipusDomini {
		CONSULTA_SQL,
		CONSULTA_WS,
		CONSULTA_REST
	}
	public enum TipusAuthDomini {
		NONE,
		HTTP_BASIC,
		USERNAMETOKEN
	}
	public enum OrigenCredencials {
		ATRIBUTS,
		PROPERTIES
	}

	private Long id;
	private String codi;
	private String nom;
	private TipusDomini tipus;
	private String url;
	private TipusAuthDomini tipusAuth;
	private OrigenCredencials origenCredencials;
	private String usuari;
	private String contrasenya;
	private String sql;
	private String jndiDatasource;
	private String descripcio;
	private int cacheSegons = 0;
	private Integer timeout = 0;
	private String ordreParams;
	private EntornDto entorn;
	private int numErrors;



	public DominiDto(String codi, String nom) {
		this.codi = codi;
		this.nom = nom;
	}
	public DominiDto(String codi, String nom, EntornDto entorn) {
		this.codi = codi;
		this.nom = nom;
		this.entorn = entorn;
	}

	private static final long serialVersionUID = 1L;

}
