package es.caib.helium.commons.dto;


/**
 * Resposta a una petició Pinbal, tant síncrona com asíncrona.
 * La covnersió de la resposta de la llibreria de pinbal a les classes de Helium es fa a PinbalPlugin.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ScspConfirmacioPeticioPinbal {
	
	private ScspAtributos atributos;

	public ScspAtributos getAtributos() {
		return this.atributos;
	}

	public void setAtributos(ScspAtributos atributos) {
		this.atributos = atributos;
	}
}