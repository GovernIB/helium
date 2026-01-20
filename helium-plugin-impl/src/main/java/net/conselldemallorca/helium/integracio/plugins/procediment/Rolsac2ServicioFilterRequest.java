package net.conselldemallorca.helium.integracio.plugins.procediment;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Rolsac2ServicioFilterRequest {
	public Object idTramite;
	public Object plataforma;
	public Object version;
	public Object vigente;
	public Object codigoMateria;
	public Object codigoTramiteTelematico;
	public Object parametros;
	public Object versionTramiteTelematico;
	public Object estadoUA;

	private String codigoUA;
	private String codigoUADir3;
	// ids separados por comas
	private String codigos;
	// Compara con codigo, nombre, estado, tipo, codigoSia, estadoSia y
	// codigoDir3Sia
	private String textos;
	private String titulo;
	private String codigo;

	/**
	 * Possibles valors:
	 * <ul>
	 * <li>'D' = Definitivo</li>
	 * <li>'M' = Modificado</li>
	 * <li>'T' = Todos (publicado o modificado)</li>
	 * <li>'A' = Ambos (publicado y modificado)</li>
	 * </ul>
	 */
	private String estadoWF;

	/*
	 * Possibles valors: 0/1 1 = procedimientos comunes
	 */
	private Integer comun;
	private String codigoSia;
	private String codigoPlantilla;
	private String codigoPlataforma;

	/**
	 * Possibles valors:
	 * <ul>
	 * <li>'PV' = Pendent validació</li>
	 * <li>'M' = En modificació</li>
	 * <li>'P' ='Publicat</li>
	 * <li>'PT' = Pendent tancar</li>
	 * <li>'T' = Tancat</li>
	 * </ul>
	 */
	private String estado;
	/**
	 * Possibles valors:
	 * <ul>
	 * <li>'PV' = Pendent validació</li>
	 * <li>'M' = En modificació</li>
	 * <li>'P' ='Publicat</li>
	 * <li>'PT' = Pendent tancar</li>
	 * <li>'T' = Tancat</li>
	 * </ul>
	 */
	private String estados;
	/**
	 * Possibles valors:
	 * <ul>
	 * <li>'A' = Alta</li>
	 * <li>'B' = Baja</li>
	 * <li>'N' = No integrado</li>
	 * </ul>
	 */
	private String estadoSia;
	/*
	 * Possibles valors: 0/1 corresponde a visible en SEDE
	 */
	private Integer activo;
	/*
	 * Possibles valors: 0/1
	 */
	private Integer buscarEnDescendientesUA;
	/**
	 * Data amb format "DD/MM/YYYY"
	 */
	private String fechaActualizacionSia;
	private List<String> listaCodigosNormativas;
	private List<String> listaCodigosPublicosObjetivos;
	private List<String> listaCodigosMaterias;
	/**
	 * Data amb format "DD/MM/YYYY"
	 */
	private String fechaPublicacionDesde;
	/**
	 * Data amb format "DD/MM/YYYY"
	 */
	private String fechaPublicacionHasta;
	private Long idEntidad;
	
	private Rolsac2FiltrePaginacio filtroPaginacion;
	private Rolsac2FiltreOrden orden;
}
