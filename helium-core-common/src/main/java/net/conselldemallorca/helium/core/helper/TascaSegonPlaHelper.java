package net.conselldemallorca.helium.core.helper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class TascaSegonPlaHelper {
	private static Map<Long,InfoSegonPla> tasquesSegonPla;
	
	public static void loadTasquesSegonPla () {
		tasquesSegonPla = new HashMap<Long,InfoSegonPla>();
	}
	
	public static boolean isTasquesSegonPlaLoaded() {
		return tasquesSegonPla != null;
	}
	
	public static boolean afegirTasca(Long taskInstanceId, InfoSegonPla info) {
		if (tasquesSegonPla != null) {
			tasquesSegonPla.put(taskInstanceId, info);
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean eliminarTasca(Long taskInstanceId) {
		if (tasquesSegonPla != null) {
			tasquesSegonPla.remove(taskInstanceId);
			return true;
		} else {
			return false;
		}
	}
	
	public static void setTasquesSegonPla(Map<Long,InfoSegonPla> tasquesSegonPla) {
		TascaSegonPlaHelper.tasquesSegonPla = tasquesSegonPla;
	}
	
	public static Map<Long,InfoSegonPla> getTasquesSegonPla() {
		return TascaSegonPlaHelper.tasquesSegonPla;
	}
	
	public static class InfoSegonPla {
		private Date marcadaFinalitzar;
		private Date iniciFinalitzacio;
		private List<String> errors;
		private List<String> messages;
		
		public InfoSegonPla (Date marcadaFinalitzar) {
			super();
			this.errors = new ArrayList<String>();
			this.messages = new ArrayList<String>();
			this.marcadaFinalitzar = marcadaFinalitzar;
		}
		
		public Date getMarcadaFinalitzar() {
			return marcadaFinalitzar;
		}

		public void setMarcadaFinalitzar(Date marcadaFinalitzar) {
			this.marcadaFinalitzar = marcadaFinalitzar;
		}

		public Date getIniciFinalitzacio() {
			return iniciFinalitzacio;
		}

		public void setIniciFinalitzacio(Date iniciFinalitzacio) {
			this.iniciFinalitzacio = iniciFinalitzacio;
		}

		public List<String> getErrors() {
			return errors;
		}

		public boolean addError(String error) {
			return this.errors.add(error);
		}

		public List<String> getMessages() {
			return messages;
		}

		public boolean addMessage(String message) {
			return this.messages.add(message);
		}
	}
}
