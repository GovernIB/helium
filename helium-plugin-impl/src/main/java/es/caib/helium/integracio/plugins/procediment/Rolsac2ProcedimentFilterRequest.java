package es.caib.helium.integracio.plugins.procediment;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Rolsac2ProcedimentFilterRequest implements Cloneable {
	private String codigoUA;
	private String codigoUADir3;
	private String codigoPublicoObjetivo;
	// ids separados por comas
	private String codigos;
	// Compara con codigo, nombre, estado, tipo, codigoSia, estadoSia y codigoDir3Sia
	private String textos;
	private String codigoFormaInicio;
	private String titulo;
	private String codigoTipoProcedimiento;
	private String codigoSilencioAdministrativo;
	private String codigoFinVia;
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
	 * Possibles valors: 0/1
	 * 1 = procedimientos comunes
	 */
	private Integer comun;
	private String codigoSia;
	private String codigoTram;
	private String codigoPlantilla;
	private String codigoPlataforma;
	/**
	 * Possibles valors:
	 * <ul>
	 * <li>'S'= Si</li>
	 * <li>'N'= No</li>
	 * </ul>
	 */
	private String tramiteVigente;
	private String canalPresentacion;
	
	
	/**
	 * Possibles valors:
	 * <ul>
	 * <li>'PV'	= Pendent validació</li>
	 * <li>'M'	= En modificació</li>
	 * <li>'P'	='Publicat</li>
	 * <li>'PT'	= Pendent tancar</li>
	 * <li>'T'	= Tancat</li>
	 * </ul>
	 */
	private String estado;
	private Boolean esPdu;
	/**
	 * Possibles valors:
	 * <ul>
	 * <li>'PV'	= Pendent validació</li>
	 * <li>'M'	= En modificació</li>
	 * <li>'P'	='Publicat</li>
	 * <li>'PT'	= Pendent tancar</li>
	 * <li>'T'	= Tancat</li>
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
	 * Possibles valors: 0/1
	 * corresponde a visible en SEDE
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
	private List<Long> codigosNormativas;
    private List<Long> codigosPublicosObjetivos;
    private List<Long> codigosMaterias;
    private Long codigoMateria;
	/**
	 * Data amb format "DD/MM/YYYY"
	 */
	private String fechaPublicacionDesde;
	/**
	 * Data amb format "DD/MM/YYYY"
	 */
	private String fechaPublicacionHasta;
	/*
	 * Possibles valors: 0/1
	 * 1 = telematico
	 */
	private Integer telematico;
	/**
	 * Possibles valors:
	 * <ul>
	 * <li>0 = No habilitado</li>
	 * <li>1 = Sí habilitado</li>
	 * <li>null no filtra por campo</li>
	 * <ul>
	 */
	private Integer disponibleFuncionarioHabilitado;
	/**
	 * Possibles valors:
	 * <ul>
	 * <li>0 = No habilitado</li>
	 * <li>1 = Sí habilitado</li>
	 * <li>null no filtra por campo</li>
	 * <ul>
	 */
	private Integer disponibleApoderadoHabilitado;
	Long idEntidad;
	Rolsac2FiltrePaginacio filtroPaginacion;
	Rolsac2FiltreOrden orden;
	
	@Override
	public Rolsac2ProcedimentFilterRequest clone() throws CloneNotSupportedException {
		return (Rolsac2ProcedimentFilterRequest) super.clone();
	}
}
