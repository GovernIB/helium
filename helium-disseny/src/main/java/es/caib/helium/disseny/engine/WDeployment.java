package es.caib.helium.disseny.engine;


import lombok.Data;

/** Objecte comú per representar la informació d'un desplegament en un workflow engine.
 *
 */
@Data
public class WDeployment {

	private String id;
    private String key;
    public String version;
}
