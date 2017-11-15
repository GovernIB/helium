package net.conselldemallorca.helium.webapp.v3.helper;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

/**
 * Mètode de suport per mantenir la informació referent a la generació d'informes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 *
 */
@Component 
public class InformeHelper {
	
	/** Diferents estats en els que pot estar la generació del report. */
	public enum Estat {
		/** No existeix cap generació per aquesta clau de consulta. */
		NO_TROBAT,
		/** S'estan consultant les dades. */
		INICIALITZANT,
		/** S'està emplenant el report. */
		GENERANT,
		/** S'ha acabat de generar. */
		FINALITZAT,
		/** S'ha cancel·lat la generació per part de l'usuarari. */
		CANCELLAT,
		/** Hi ha hagut un error i s'ha informat el missatge amb l'error. */
		ERROR
	}
	
	
	/** Propietat on es guarda la informació de les execucions de generació d'informe per usuari. Map<sessio_id,info>. */
	private Map<String, InformeInfo> informes = new HashMap<String, InformeInfo>();	
	
	/** Obté la informació de la consulta per usuari. */
	public InformeInfo getConsulta(String id) {
		InformeInfo ret = informes.get(id);
		if (ret == null) {
			ret = new InformeInfo();
			ret.setEstat(Estat.NO_TROBAT);
		}
		return ret;
	}
	public void setConsulta(String id, InformeInfo ret) {
		this.informes.put(id, ret);		
	}	

	
	/** Classe per tenir la informació relativa a a la generació de consultes de forma asíncrona. */
	public class InformeInfo {
		
		private String id;
		private String consulta;
		private Estat estat;
		private String msg;
		private int numeroRegistres;
		private String formatExportacio;

		public InformeInfo() {
			id = "";
		}

		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getConsulta() {
			return consulta;
		}
		public void setConsulta(String consulta) {
			this.consulta = consulta;
		}
		public void setEstat(Estat estat) {
			synchronized(this){
				this.estat = estat;
			}
		}
		public Estat getEstat() {
			Estat e;
			synchronized(this){
				e = this.estat;
			}
			return e;
		}
		public void setMissatge(String msg) {
			this.msg = msg;
		}
		public String getMissatge() {
			return msg;
		}
		public void setNumeroRegistres(int numeroRegistres) {
			this.numeroRegistres = numeroRegistres;
		}
		public int getNumeroRegistres() {
			return numeroRegistres;
		}
		public void setFormatExportacio(String formatExportacio) {
			this.formatExportacio = formatExportacio;
		}
		public String getFormatExportacio() {
			return formatExportacio;
		}
		
		/** Posa l'estat com error i actualitza el missatge.*/
		public void setError(Throwable t) {
			this.estat = Estat.ERROR;
			this.msg = t.getMessage();
		}
	}
}
