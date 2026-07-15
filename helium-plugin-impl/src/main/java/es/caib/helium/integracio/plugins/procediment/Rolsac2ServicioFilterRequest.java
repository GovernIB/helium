package es.caib.helium.integracio.plugins.procediment;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class Rolsac2ServicioFilterRequest {
	private String codigoUADir3;

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
	private Rolsac2FiltrePaginacio filtroPaginacion;
	private Rolsac2FiltreOrden orden;
}
