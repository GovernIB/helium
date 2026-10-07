package es.caib.helium.commons.utils;

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
	        		if (tokens[i].length() >= 2 && tokens[i].startsWith("\"") && tokens[i].endsWith("\""))
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

	public String[] getCsvTokens(String line, char separador, char delimitadorString) throws Exception {
		List<String> tokens = new ArrayList<String>();
		// si és un nou token i comença per delimitadorString llavors llegeix fins al següent delimitador d'string i guarda'l com un nou token
		// altrament comença un nou string token i llegeix fins al següent separador
		StringBuilder token = new StringBuilder();
		if (line != null) {
			for (int i=0; i<line.length(); i++) {
				if(i!=line.length()-1) {
					if (line.charAt(i) == delimitadorString) {
					for(int j=i+1; j<line.length();j++) {
						if(line.charAt(j)!=delimitadorString)  {
							token.append(line.charAt(j));
							if (j==line.length()-1) {
								// Sense cometa de tancament: tot el que queda és contingut
								i=j;
								break;
							}
						} else if (j+1<line.length() && line.charAt(j+1)==delimitadorString) {
							// Cometa escapada ("") dins de valor entre cometes
							token.append(delimitadorString);
							j++;
							if (j==line.length()-1) {
								i=j;
								break;
							}
						} else {
							i=j+1;
							break;
						}
					}
					tokens.add(token.toString());
					token = new StringBuilder();
					// Si el separador posterior a la cometa de tancament és l'últim
					// caràcter, cal conservar el camp buit final ("a"; -> ["a",""])
					if (i==line.length()-1 && line.charAt(i)==separador) {
						tokens.add("");
						token = new StringBuilder();
					} else if (i>=line.length()) {
						// El valor entre cometes arribava fins al final: res més a fer
					}
				} else {
					if(line.charAt(i)!=separador) {
						boolean entreCometes = false;
						for(int j=i; j<line.length();j++) {
							char c = line.charAt(j);
							if(c==delimitadorString) {
								if (entreCometes && j+1<line.length() && line.charAt(j+1)==delimitadorString) {
									token.append(delimitadorString);
									j++;
								} else {
									entreCometes = !entreCometes;
								}
								if (j==line.length()-1) {
									i=j;
								}
							}
							else if (c==separador && !entreCometes) {
								i=j;
								tokens.add(token.toString());
								token = new StringBuilder();
								continue;
							}
							else {
								token.append(c);
								if (j==line.length()-1) {
									i=j;
								}
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
				if (line.charAt(i)==separador) {
					tokens.add(token.toString());
					tokens.add("");
				} else {
					token.append(line.charAt(i));
					tokens.add(token.toString());
				}
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
//		String line = ";;\"El valor de text té el caràcter ';' que fa tanta nosa\";var_string;var_float;var_data;var_preu;var_bool";
//		String line = "Expedient 2025052609111202523;Text 1;1.1;26/05/22;1234.56;;";
//		String line = "Expedient 20;\"Text ; 1\";1.1;26/05/22;1234.56;;;;";
//		String line = "\"Text ; 1\"";
		String line = "2020;DR3; Declaració Responsable;41428809J;M. DEL CARMEN ;MARÍ;FERRER;ptorrensm76@gmail.com;;;;;609403029;;;;;;;;;;;;VIVIENDAS;8;;7;333;POL 7 PARC 333 T.M. SANTA EULÀRIA DES RIU;7-54;20000;NO;MODERADA;;13/10/20;NOIND;;378264;432146;10/11/20;6;\" - Les coordenades UTM del seu Sistema de Depuració Autònom que va indicar en la seva Declaració Responsable no manifesten localització del SAD. Se’l requereix que indiqui les coordenades UTM que corresponguin al lloc on es localitza el seu Sistema de Depuració Autònom. Per recopilar-les ho podrà realitzar mitjançant diferents aplicacions mòbils, sempre i quan tingui el seu dispositiu un GPS incorporat; també ho podrà realitzar mitjançant el visor cartogràfic https://ideib.caib.es/visor/, introduint en Cerca Avançada → Cadastre el municipi, el polígon i la parcel·la, i després fent un clic amb el botó esquerre del ratolí en el lloc aproximat on es localitza, llavors una finestra emergent li mostrarà les coordenades X i Y.    - Falta el pla de manteniment del Sistema de Depuració Autònom, generalment podrà trobar una guia de com hauria d’ésser dins la fitxa tècnica del sistema  en el manual d’instruccions.      - Falta la fitxa tècnica del fabricant del Sistema de Depuració Autònom i la informació relativa al rendiment .   \";;;960;;;;;c";

		String[] tokens;
		try {
			tokens = csvHelper.getCsvTokens(line, ';', '"');
			for (int i=0; i< tokens.length; i++) {
				System.out.println(i + " " + tokens[i]);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
}
