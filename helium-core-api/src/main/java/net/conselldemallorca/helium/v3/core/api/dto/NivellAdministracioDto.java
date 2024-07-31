package net.conselldemallorca.helium.v3.core.api.dto;

public class NivellAdministracioDto implements Comparable<NivellAdministracioDto> {
	private Long codi;
	private String descripcio;

	public NivellAdministracioDto(
			Long codi,
			String descripcio) {
		this.codi = codi;
		this.descripcio = descripcio;
	}

	public Long getCodi() {
		return codi;
	}
	public void setCodi(Long codi) {
		this.codi = codi;
	}

	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}
	
	@Override
	public int compareTo(NivellAdministracioDto o) {
		return descripcio.compareToIgnoreCase(o.getDescripcio());
	}
}
