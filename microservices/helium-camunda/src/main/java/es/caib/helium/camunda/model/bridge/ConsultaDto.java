/**
 * 
 */
package es.caib.helium.camunda.model.bridge;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Objecte de domini que representa una consulta d'expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
@NoArgsConstructor
public class ConsultaDto implements Serializable, Comparable<ConsultaDto> {

	private Long id;
	private String codi;
	private String nom;
	private String descripcio;
	private String valorsPredefinits;
	private String informeNom;
	private byte[] informeContingut;
	private boolean exportarActiu;
	private boolean ocultarActiu;
	private boolean generica;
	private int ordre;
	private ExpedientTipusDto expedientTipus;	

	private Set<ConsultaCampDto> camps = new HashSet<ConsultaCampDto>();
	private String formatExport;
	
	/** Per mostrar el comptador de variables de tipus filtre */
	private int varsFiltreCount = 0;
	/** Per mostrar el comptador de variables de tipus informe */
	private int varsInformeCount = 0;
	/** Per mostrar el comptador de variables de tipus paràmetre */
	private int parametresCount = 0;

	public ConsultaDto(String codi, String nom) {
		this.codi = codi;
		this.nom = nom;
	}
	
	public void addCamp(ConsultaCampDto camp) {
		getCamps().add(camp);
	}
	public void removeCamp(ConsultaCampDto camp) {
		getCamps().remove(camp);
	}

	@Override
	public int compareTo(ConsultaDto o) {
		return this.getNom().compareTo(o.getNom());
	}
	/** Mètode per convertir com a Map els valors predefinits.*/
	public Map<String, String> getMapValorsPredefinits() {
		Map<String, String> mapValorsPredefinits = new HashMap<String, String>();
		if (valorsPredefinits != null) {
			String[] parelles = valorsPredefinits.split(",");
			for (int i = 0; i < parelles.length; i++) {
				String[] parella = (parelles[i].contains(":")) ? parelles[i].split(":") : parelles[i].split("=");
				if (parella.length == 2) {
					mapValorsPredefinits.put(parella[0], parella[1]);
				}
			}
		}
		return mapValorsPredefinits;
	}
	
	private static final long serialVersionUID = 1L;

}
