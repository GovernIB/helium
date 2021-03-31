package net.conselldemallorca.helium.core.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
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
			
	public CsvHelper () {
		
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
