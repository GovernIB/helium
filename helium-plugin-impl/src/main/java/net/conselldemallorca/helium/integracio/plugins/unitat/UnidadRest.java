package net.conselldemallorca.helium.integracio.plugins.unitat;

import es.caib.dir3caib.ws.api.unidad.UnidadTF;



public class UnidadRest extends UnidadTF{

	private String nifCif;
	private String denominacionCooficial;
	private String version;
	public String getNifCif() {
		return nifCif;
	}
	public void setNifCif(String nifCif) {
		this.nifCif = nifCif;
	}
	public String getDenominacionCooficial() {
		return denominacionCooficial;
	}
	public void setDenominacionCooficial(String denominacionCooficial) {
		this.denominacionCooficial = denominacionCooficial;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}


}
