package es.caib.helium.logic.intf.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe amb utilitzats per interpretar arxius CSV.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class CsvHelper {
	
	/** Separador de columnes. */
	private String separador = ";";
	/** Codificador per llegir el contingut. */
	private String codificacio ="UTF-8";
	/** Indica si el text està delimitat entre corxets. */
	private boolean textEnCorxets = true;
			
	public CsvHelper() {
		
	}
	
	/** Mètode per interpretar el contingut en bytes i retornar una matriu resultant.
	 * 
	 * @param contingut Paràmetre amb el contingut
	 * @return Retorna una matriu amb el resultat on cada fila hauria de contenir les mateixes columnes.
	 * @throws Exception
	 */
	public String[][] parse(byte[] contingut)  throws Exception{
		String[][] resultat = null;
		if (contingut != null && contingut.length > 0) {
			BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(contingut), codificacio));
			List<String[]> files = new ArrayList<String[]>();
			// 1a fila 
			String line;
		    while ((line = br.readLine()) != null) {
		        String[] tokens = line.split(this.separador);
		        if (this.textEnCorxets) {
		        	for (int i=0; i<tokens.length; i++) {
		        		if (tokens[i].startsWith("\"") && tokens[i].endsWith("\""))
		        			tokens[i] = tokens[i].substring(1, tokens[i].length()-1);
		        	}
		        }
		        files.add(tokens);
		    }
		    // close the reader
		    br.close();
		    resultat = new String[files.size()][];
		    for (int i = 0; i < files.size(); i++) {
		    	resultat[i] = files.get(i);
		    }
		}
		return resultat;
	}

	/** Mètode per escriure en un arxiu CSV per files i columnes el contingut
	 * passat com a paràmetre.
	 * 
	 * @param informacioCsv
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public byte[] toCsv(String[][] informacioCsv) throws UnsupportedEncodingException {
		StringBuilder csvBuilder = new StringBuilder();
		for (int i=0; i<informacioCsv.length; i++) {
			for(int j=0; j<informacioCsv[i].length; j++) {
				if (informacioCsv[i][j] != null)
					csvBuilder.append("\"").append(informacioCsv[i][j]).append("\"");
				if (j<informacioCsv[i].length+1)
					csvBuilder.append(separador);
			}
			if (i<informacioCsv.length + 1)
				csvBuilder.append("\n");
		}
		return csvBuilder.toString().getBytes(codificacio);
	}


	public String getSeparador() {
		return separador;
	}

	public void setSeparador(String separador) {
		this.separador = separador;
	}

	public String getCodificacio() {
		return codificacio;
	}

	public void setCodificacio(String codificacio) {
		this.codificacio = codificacio;
	}

	public boolean isTextEnCorxets() {
		return textEnCorxets;
	}

	public void setTextEnCorxets(boolean textEnCorxets) {
		this.textEnCorxets = textEnCorxets;
	}
}
