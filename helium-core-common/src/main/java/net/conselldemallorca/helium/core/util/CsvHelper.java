package net.conselldemallorca.helium.core.util;

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
	private char separador = ';';
	private char delimitadorString = '\"';
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
		        String[] tokens = this.getCsvTokens(line, this.separador, this.delimitadorString);
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
	
	public String[] getCsvTokens(String line, char separador, char delimitadorString) {
		List<String> tokens = new ArrayList<String>();
		// si és un nou token i comença per delimitadorString llavors llegeix fins al següent delimitador d'string i guarda'l com un nou token
		// altrament comença un nou string token i llegeix fins al següent separador
		StringBuilder token = new StringBuilder();
		if (line != null) {
			for (int i=0; i<line.length(); i++) {
				if(i!=line.length()-1) {
					if (line.charAt(i) == delimitadorString) {
						for(int j=i+1; j<line.length();j++) {
							if(line.charAt(j)!=delimitadorString && j!=line.length()-1)  {
								token.append(line.charAt(j));
							}else {
								i=j+1;
								break;
							}
						}
						tokens.add(token.toString());
						token = new StringBuilder();
					} else {
						if(line.charAt(i)!=separador) {
							for(int j=i; j<line.length();j++) {
								if(line.charAt(j)!=separador && j!=line.length()-1) 
									token.append(line.charAt(j));
								else if (j==line.length()-1) {
									token.append(line.charAt(j));
									i=j;
								}
								else {
									i=j;
									break;
								}
							}
							tokens.add(token.toString());
							token = new StringBuilder();
						} else {
							token.append("");
							tokens.add(token.toString());
							token = new StringBuilder();
						}
					}
				} else {
					token.append(line.charAt(i));
					tokens.add(token.toString());
					break;
				}
					
			}		
		}
		return tokens.toArray(new String[0]);
	}
	
	public static void main(String[] args) {
		System.out.println("Inici prova");
		CsvHelper csvHelper = new CsvHelper();
		//String line = "2022;22;Expedient 202205260922;\"El valor de text té el caràcter ';' que fa tanta nosa\";2;2022-05-26 12:3:4.567;-65;S";
		//String line = "\"Valor1\";\"Valor ; 2\";2";
		//String line = "Any;Número;Títol;var_string;var_float;var_data;var_preu;var_bool";
		//String line = ";;\"El valor de text té el caràcter ';' que fa tanta nosa\";var_string;var_float;var_data;var_preu;var_bool";
		String line = ";;Expedient 2025052609111202523;Text 1;1.1;26/05/22;1234.56;false";
		String[] tokens = csvHelper.getCsvTokens(line, ';', '"');
		for (int i=0; i< tokens.length; i++) {
			System.out.println(i + " " + tokens[i]);
		}
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

	public char getSeparador() {
		return separador;
	}

	public void setSeparador(char separador) {
		this.separador = separador;
	}

	public char getDelimitadorString() {
		return delimitadorString;
	}

	public void setDelimitadorString(char delimitadorString) {
		this.delimitadorString = delimitadorString;
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
