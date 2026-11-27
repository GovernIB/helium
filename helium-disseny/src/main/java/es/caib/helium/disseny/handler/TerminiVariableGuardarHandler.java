package es.caib.helium.disseny.handler;

/**
 * Handler per desar una durada (anys, mesos i dies) en una dada de tipus termini.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface TerminiVariableGuardarHandler {
	public void setVarTermini(String varTermini);
	public void setAnys(String anys);
	public void setMesos(String mesos);
	public void setDies(String dies);
}
