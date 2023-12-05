package net.conselldemallorca.helium.integracio.plugins.procediment;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * Informació d'una unitat administrativa d'un procediment.
 * 
 */
@Getter @Setter
public class UnitatAdministrativaRolsac {
	 
	@JsonProperty("abreviatura")
    private String abreviatura;
	@JsonProperty("businessKey")
    private String businessKey;
	@JsonProperty("claveHita")
    private String claveHita;
	@JsonProperty("codigoEstandar")
    private String codigoEstandar;
	@JsonProperty("dominio")
    private String dominio;
	@JsonProperty("email")
    private String email;
	@JsonProperty("fax")
    private String fax;
	@JsonProperty("codigo")
    private Long codigo;
	@JsonProperty("nombre")
    private String nombre;
	@JsonProperty("numfoto1")
    private Integer numfoto1;
	@JsonProperty("numfoto2")
    private Integer numfoto2;
	@JsonProperty("numfoto3")
    private Integer numfoto3;
	@JsonProperty("numfoto4")
    private Integer numfoto4;
	@JsonProperty("orden")
    private Long orden;
	@JsonProperty("presentacion")
    private String presentacion;
	@JsonProperty("responsable")
    private String responsable;
	@JsonProperty("responsableEmail")
    private String responsableEmail;
	@JsonProperty("sexoResponsable")
	private Integer sexoResponsable;
	@JsonProperty("cvResponsable")
    private String cvResponsable;
	@JsonProperty("telefono")
    private String telefono;
	@JsonProperty("url")
    private String url;
	@JsonProperty("validacion")
    private Integer validacion;
	@JsonProperty("codigoDIR3")
    private String codigoDIR3;
	@JsonProperty("idioma")
	private String idioma;
	@JsonProperty("link_espacioTerritorial")
	private Link espacioTerritorial;
	@JsonProperty("link_fotog")
	private Link fotog;
	@JsonProperty("link_fotop")
	private Link fotop;
	@JsonProperty("link_logoh")
	private Link logoh;
	@JsonProperty("link_logos")
	private Link logos;
	@JsonProperty("link_logot")
	private Link logot;
	@JsonProperty("link_logov")
	private Link logov;
	@JsonProperty("link_padre")
	private Link padre;
}
