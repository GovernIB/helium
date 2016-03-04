package net.conselldemallorca.helium.core.helper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.conselldemallorca.helium.v3.core.api.service.TascaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TascaSegonPlaHelper {
	
	@Autowired
	private TascaService tascaService;
	
	private Map<Long,InfoSegonPla> tasquesSegonPla;
	
	public void loadTasquesSegonPla () {
		tasquesSegonPla = new HashMap<Long,InfoSegonPla>();
	}
	
	public boolean isTasquesSegonPlaLoaded() {
		return tasquesSegonPla != null;
	}
	
	public boolean afegirTasca(Long taskInstanceId, Date marcadaFinalitzar, Date iniciFinalitzacio, String error) {
		if (tasquesSegonPla != null) {
			tasquesSegonPla.put(taskInstanceId, new InfoSegonPla(marcadaFinalitzar, iniciFinalitzacio, error));
			return true;
		} else {
			return false;
		}
	}
	
	public boolean afegirTasca(Long taskInstanceId, Date marcadaFinalitzar) {
		if (tasquesSegonPla != null) {
			tasquesSegonPla.put(taskInstanceId, new InfoSegonPla(marcadaFinalitzar));
			return true;
		} else {
			return false;
		}
	}
	
	public boolean eliminarTasca(Long taskInstanceId) {
		if (tasquesSegonPla != null) {
			tasquesSegonPla.remove(taskInstanceId);
			return true;
		} else {
			return false;
		}
	}
	
	public void completarTasca(Long taskInstanceId) {
		if (tasquesSegonPla != null && tasquesSegonPla.containsKey(taskInstanceId)) {
			InfoSegonPla infoSegonPla = tasquesSegonPla.get(taskInstanceId);
			infoSegonPla.completada = true;
		}
	}
	
	public void setTasquesSegonPla(Map<Long,InfoSegonPla> tasquesSegonPla) {
		this.tasquesSegonPla = tasquesSegonPla;
	}
	
	public Map<Long,InfoSegonPla> getTasquesSegonPla() {
		return this.tasquesSegonPla;
	}
	
	public void carregaTasquesSegonPla() {
		tascaService.carregaTasquesSegonPla();
	}
	
	public void completaTascaSegonPla(String tascaId, Date iniciFinalitzacio) {
		tascaService.completaTascaSegonPla(tascaId, iniciFinalitzacio);
	}
	
	public void guardarErrorFinalitzacio(String tascaId, String errorFinalitzacio) {
		tascaService.guardarErrorFinalitzacio(tascaId, errorFinalitzacio);
	}
	
	public class InfoSegonPla {
		private Date marcadaFinalitzar;
		private Date iniciFinalitzacio;
		private String error;
		private boolean completada;
		private List<String> messages;
		
		public InfoSegonPla (Date marcadaFinalitzar) {
			super();
			this.messages = new ArrayList<String>();
			this.marcadaFinalitzar = marcadaFinalitzar;
			this.completada = false;
		}
		
		public InfoSegonPla (Date marcadaFinalitzar, Date iniciFinalitzacio, String error) {
			super();
			this.messages = new ArrayList<String>();
			this.marcadaFinalitzar = marcadaFinalitzar;
			this.iniciFinalitzacio = iniciFinalitzacio;
			this.error = error;
			this.completada = false;
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

		public String getError() {
			return error;
		}

		public void setError(String error) {
			this.error = error;
		}

		public boolean isCompletada() {
			return completada;
		}

		public void setCompletada(boolean completada) {
			this.completada = completada;
		}

		public List<String> getMessages() {
			return messages;
		}
		
		public void setMessages(List<String> messages) {
			this.messages = messages;
		}

		public boolean addMessage(String message) {
			return this.messages.add(message);
		}
	}
}
