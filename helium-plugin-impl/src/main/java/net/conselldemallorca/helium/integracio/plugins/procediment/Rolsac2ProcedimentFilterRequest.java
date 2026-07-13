package net.conselldemallorca.helium.integracio.plugins.procediment;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Rolsac2ProcedimentFilterRequest implements Cloneable {
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
	 * Possibles valors: 0/1
	 * corresponde a visible en SEDE
	 */
	private Integer activo;
	/*
	 * Possibles valors: 0/1
	 */
	private Integer buscarEnDescendientesUA;
	private Rolsac2FiltrePaginacio filtroPaginacion;
	private Rolsac2FiltreOrden orden;

	@Override
	public Rolsac2ProcedimentFilterRequest clone() throws CloneNotSupportedException {
		return (Rolsac2ProcedimentFilterRequest) super.clone();
	}
}
