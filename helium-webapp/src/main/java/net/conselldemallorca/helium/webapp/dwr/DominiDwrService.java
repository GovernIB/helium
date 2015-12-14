package net.conselldemallorca.helium.webapp.dwr;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.conselldemallorca.helium.core.extern.domini.DominiHeliumException;
import net.conselldemallorca.helium.core.extern.domini.FilaResultat;
import net.conselldemallorca.helium.core.extern.domini.ParellaCodiValor;
import net.conselldemallorca.helium.core.model.service.DissenyService;

import org.springframework.beans.factory.annotation.Autowired;


public class DominiDwrService {

	private DissenyService dissenyService;

	@Autowired
	public DominiDwrService(DissenyService dissenyService) {
		this.dissenyService = dissenyService;
	}
	

	// Domini SQL sense paràmetres
	// -----------------------------------------------------------------
	public String ping(String domini ) throws DominiHeliumException {
		try{
			Long dominiId = Long.parseLong(domini);
			List<FilaResultat> resultat= dissenyService.consultaDomini(
					null,
					dominiId,
					(Map<String, Object>)null);
			StringBuilder sb = new StringBuilder();
			for (FilaResultat fila: resultat) {
				sb.append("[");
				for (ParellaCodiValor parella: fila.getColumnes()) {
					sb.append("(" + parella.getCodi() + ": " + parella.getValor() + ")");
					sb.append(", ");
				}
				sb.append("],");
			}
			return sb.toString();
		}catch (Exception e) {
			return "Error consultant domini: "+e.getMessage();
		}
	}
	
	// Domini SQL amb paràmetres
	// -----------------------------------------------------------------
	public String pingParams(String domini, String[] params ) throws DominiHeliumException {
		Map<String, Object> parametres= new HashMap<String, Object>();
		int j=0;
		for(int i=0;i<params.length/2;i++){
			parametres.put(params[j].toString(), params[j+1].toString());
			j+=2;
		}
		try{
			Long dominiId = Long.parseLong(domini);
			List<FilaResultat> resultat= dissenyService.consultaDomini(null, dominiId, parametres);
			StringBuilder sb = new StringBuilder();
			for (FilaResultat fila: resultat) {
				sb.append("[");
				for (ParellaCodiValor parella: fila.getColumnes()) {
					sb.append("(" + parella.getCodi() + ": " + parella.getValor() + ")");
					sb.append(", ");
				}
				sb.append("],");
			}
			return sb.toString();
		}catch (Exception e) {
			return "Error consultant domini: "+e.getMessage();
		}
	}
	
	
	// Domini WS amb paràmetres
	// -----------------------------------------------------------------
	public String pingWSParams(String domini, String idWS, String[] params ) throws DominiHeliumException {
		Map<String, Object> parametres= new HashMap<String, Object>();
		
		//convertim els valor que ens arriban
		
		int s=0;
		int j=0;
		for(int n=0;n<params.length/3;n++){
			
			if(params[s].contains("string")){
				parametres.put(params[j+2],params[j+1].toString());
			}
			if(params[s].contains("int")){
				parametres.put(params[j+2],Long.parseLong(params[j+1]));
			}
			if(params[s].contains("float")){
				parametres.put(params[j+2],Double.parseDouble(params[j+1]));
			}
			if(params[s].contains("boolean")){
				parametres.put(params[j+2],Boolean.parseBoolean(params[j+1]));
			}
			if(params[s].contains("date")){
				String[] dataSplit = params[j+1].split("/");
				Calendar data = new GregorianCalendar();
				data.set(Integer.parseInt(dataSplit[2]),Integer.parseInt(dataSplit[1]),Integer.parseInt(dataSplit[0]));
				parametres.put(params[j+2],data);
			}
			if(params[s].contains("price")){
				String dat = params[j+1];
				BigDecimal datBDecimal = new BigDecimal(new Double(dat));
				parametres.put(params[j+2], datBDecimal);
			}
			s+=3;
			j+=3;
		}

		try{
			Long dominiId = Long.parseLong(domini);
			List<FilaResultat> resultat= dissenyService.consultaDomini(null, dominiId, idWS, parametres);
			StringBuilder sb = new StringBuilder();
			for (FilaResultat fila: resultat) {
				sb.append("[");
				for (ParellaCodiValor parella: fila.getColumnes()) {
					sb.append("(" + parella.getCodi() + ": " + parella.getValor() + ")");
					sb.append(", ");
				}
				sb.append("],");
			}
			return sb.toString();
		}catch (Exception e) {
			return "Error consultant domini: "+e.getMessage();
		}
	}
	
	
	// Domini WS sense paràmetres
	// -----------------------------------------------------------------
	public String pingWS(String domini, String idWS ) throws DominiHeliumException {
		try{
			Long dominiId = Long.parseLong(domini);
			List<FilaResultat> resultat= dissenyService.consultaDomini(null, dominiId, idWS);
			StringBuilder sb = new StringBuilder();
			for (FilaResultat fila: resultat) {
				sb.append("[");
				for (ParellaCodiValor parella: fila.getColumnes()) {
					sb.append("(" + parella.getCodi() + ": " + parella.getValor() + ")");
					sb.append(", ");
				}
				sb.append("],");
			}
			return sb.toString();
		}catch (Exception e) {
			return "Error consultant domini: "+e.getMessage();
		}
	}
	
	
}
