package net.conselldemallorca.helium.tests.integracio.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Classe per contenir la informació de petició del WS d'inici del formulari
 * i poder-la mostrar en el formulari. La informació es guarda en un map en memòria. 
 *
 */
public class IntegracioFormUtil {
	
	/** Instància singleton. */
	private static IntegracioFormUtil singleton = null;
	
	/** Mètode estàtic per obtenir la instància. */
	public static IntegracioFormUtil getInstance() {
		if (singleton == null)
			singleton = new IntegracioFormUtil();
		return singleton;
	}
	
	/** Constructor privat per evitar la creació. */
	private IntegracioFormUtil() {
		this.setPeticions(new HashMap<String, IntegracioFormUtil.IntegracioFormPeticio>());
	}
	

	/** Mapeig <formulariId, dades peticio> per poder visualitzar el formulari. 
	 * El formulariId està compost pel codi del formulari i el taskId, per exemple 
	 * "codi_taskId".*/
	private Map<String, IntegracioFormPeticio> peticions;

	public Map<String, IntegracioFormPeticio> getPeticions() {
		return peticions;
	}

	public void setPeticions(Map<String, IntegracioFormPeticio> peticions) {
		this.peticions = peticions;
	}
	
	/** Afegeix una petició al llistat i retorna l'identificador compost pel codi i el taskId. */
	public String addPeticio(
			String codi,
			String taskId,
			List<net.conselldemallorca.helium.v3.core.ws.formext.ParellaCodiValor> valors) {
		String formId = taskId;
		this.peticions.put(formId, new IntegracioFormPeticio(codi, taskId, valors));
		return formId;
	}
	
	/** Mètode per obtenir directament l'objecte amb la informació de la petició. */
	public IntegracioFormPeticio getPeticio(String formId) {
		return this.peticions.get(formId);
	}

	
	/** Classe que conté la informació d'una petició anterior.*/
	public class IntegracioFormPeticio {
		private String codi;
		private String taskId;
		private List<net.conselldemallorca.helium.v3.core.ws.formext.ParellaCodiValor> valors;
		
		/** Mètode privat per contruir l'objecte amb tota la informació. */
		public IntegracioFormPeticio(
				String codi,
				String taskId,
				List<net.conselldemallorca.helium.v3.core.ws.formext.ParellaCodiValor> valors) {
			this.codi = codi;
			this.taskId = taskId;
			this.valors = valors;
		}
		
		public String getCodi() {
			return codi;
		}
		public void setCodi(String codi) {
			this.codi = codi;
		}
		public String getTaskId() {
			return taskId;
		}
		public void setTaskId(String taskId) {
			this.taskId = taskId;
		}
		public List<net.conselldemallorca.helium.v3.core.ws.formext.ParellaCodiValor> getValors() {
			return valors;
		}
		public void setValors(List<net.conselldemallorca.helium.v3.core.ws.formext.ParellaCodiValor> valors) {
			this.valors = valors;
		}
	}

	/** Esborra la petició del map de peticions. */
	public void removePeticio(String formId) {
		this.getPeticions().remove(formId);
	}

}
