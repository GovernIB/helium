/**
 * 
 */
package net.conselldemallorca.helium.integracio.domini;

import java.util.ArrayList;
import java.util.List;

import javax.jws.WebService;

/**
 * Domini d'exemple
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@WebService(endpointInterface = "net.conselldemallorca.helium.integracio.domini.DominiHelium")
public class DominiExempleImpl implements DominiHelium {

	public List<FilaResultat> consultaDomini(
			String id,
			List<ParellaCodiValor> parametres) throws DominiHeliumException {
		List<FilaResultat> resposta = new ArrayList<FilaResultat>();
		resposta.add(novaFilaMunicipi("alr", "Alaró", 4869, 1));
		resposta.add(novaFilaMunicipi("alc", "Alcúdia", 16176, 1));
		resposta.add(novaFilaMunicipi("alg", "Algaida", 4339, 1));
		resposta.add(novaFilaMunicipi("and", "Andratx", 10410, 1));
		resposta.add(novaFilaMunicipi("ari", "Ariany", 766, 1));
		resposta.add(novaFilaMunicipi("art", "Artà", 6730, 1));
		resposta.add(novaFilaMunicipi("ban", "Banyalbufar", 575, 1));
		resposta.add(novaFilaMunicipi("bin", "Binissalem", 6475, 1));
		resposta.add(novaFilaMunicipi("bug", "Búger", 1063, 1));
		resposta.add(novaFilaMunicipi("bun", "Bunyola", 5574, 1));
		resposta.add(novaFilaMunicipi("cal", "Calvià", 45284, 1));
		resposta.add(novaFilaMunicipi("cpn", "Campanet", 2507, 1));
		resposta.add(novaFilaMunicipi("cmp", "Campos", 8296, 1));
		resposta.add(novaFilaMunicipi("cap", "Capdepera", 11074, 1));
		resposta.add(novaFilaMunicipi("con", "Consell", 3045, 1));
		resposta.add(novaFilaMunicipi("cos", "Costitx", 976, 1));
		resposta.add(novaFilaMunicipi("dei", "Deià", 708, 1));
		resposta.add(novaFilaMunicipi("esc", "Escorca", 307, 1));
		resposta.add(novaFilaMunicipi("esp", "Esporles", 4546, 1));
		resposta.add(novaFilaMunicipi("est", "Estellencs", 399, 1));
		resposta.add(novaFilaMunicipi("fel", "Felanitx", 16948, 1));
		resposta.add(novaFilaMunicipi("for", "Fornalutx", 717, 1));
		resposta.add(novaFilaMunicipi("inc", "Inca", 27301, 1));
		resposta.add(novaFilaMunicipi("llv", "Lloret de Vistalegre", 1149, 1));
		resposta.add(novaFilaMunicipi("lls", "Lloseta", 5375, 1));
		resposta.add(novaFilaMunicipi("llb", "Llubí", 2042, 1));
		resposta.add(novaFilaMunicipi("llc", "Llucmajor", 31381, 1));
		resposta.add(novaFilaMunicipi("man", "Manacor", 37165, 1));
		resposta.add(novaFilaMunicipi("mcr", "Mancor de la Vall", 991, 1));
		resposta.add(novaFilaMunicipi("msa", "Maria de la Salut", 2141, 1));
		resposta.add(novaFilaMunicipi("mar", "Marratxí", 29742, 1));
		resposta.add(novaFilaMunicipi("mon", "Montuïri", 2576, 1));
		resposta.add(novaFilaMunicipi("mur", "Muro", 6717, 1));
		resposta.add(novaFilaMunicipi("pal", "Palma", 375048, 1));
		resposta.add(novaFilaMunicipi("pet", "Petra", 2744, 1));
		resposta.add(novaFilaMunicipi("spb", "Sa Pobla", 12122, 1));
		resposta.add(novaFilaMunicipi("pol", "Pollença", 16398, 1));
		resposta.add(novaFilaMunicipi("por", "Porreres", 4848, 1));
		resposta.add(novaFilaMunicipi("pui", "Puigpunyent", 1631, 1));
		resposta.add(novaFilaMunicipi("sal", "Ses Salines", 4502, 1));
		resposta.add(novaFilaMunicipi("sjo", "Sant Joan", 1853, 1));
		resposta.add(novaFilaMunicipi("sll", "Sant Llorenç des Cardassar", 7738, 1));
		resposta.add(novaFilaMunicipi("seu", "Santa Eugènia", 1439, 1));
		resposta.add(novaFilaMunicipi("sma", "Santa Margalida", 10204, 1));
		resposta.add(novaFilaMunicipi("smr", "Santa Maria del Camí", 5323, 1));
		resposta.add(novaFilaMunicipi("san", "Santanyí", 11172, 1));
		resposta.add(novaFilaMunicipi("slv", "Selva", 3203, 1));
		resposta.add(novaFilaMunicipi("sen", "Sencelles", 2743, 1));
		resposta.add(novaFilaMunicipi("sin", "Sineu", 3133, 1));
		resposta.add(novaFilaMunicipi("sol", "Sóller", 12847, 1));
		resposta.add(novaFilaMunicipi("ssv", "Son Servera", 10951, 1));
		resposta.add(novaFilaMunicipi("val", "Valldemossa", 1930, 1));
		resposta.add(novaFilaMunicipi("vil", "Vilafranca de Bonany", 2573, 1));
		resposta.add(novaFilaMunicipi("ala", "Alaior", 8933, 2));
		resposta.add(novaFilaMunicipi("ecs", "Es Castell", 7475, 2));
		resposta.add(novaFilaMunicipi("ciu", "Ciutadella de Menorca", 27468, 2));
		resposta.add(novaFilaMunicipi("fer", "Ferreries", 4476, 2));
		resposta.add(novaFilaMunicipi("mao", "Maó", 27893, 2));
		resposta.add(novaFilaMunicipi("emc", "Es Mercadal", 4504, 2));
		resposta.add(novaFilaMunicipi("emg", "Es Migjorn Gran", 1503, 2));
		resposta.add(novaFilaMunicipi("slu", "Sant Lluís", 6182, 2));
		resposta.add(novaFilaMunicipi("eiv", "Eivissa", 42884, 3));
		resposta.add(novaFilaMunicipi("sap", "Sant Antoni de Portmany", 19673, 3));
		resposta.add(novaFilaMunicipi("sjl", "Sant Joan de Labritja", 4975, 3));
		resposta.add(novaFilaMunicipi("sjt", "Sant Josep de sa Talaia", 19224, 3));
		resposta.add(novaFilaMunicipi("ser", "Santa Eulària des Riu", 27152, 3));
		resposta.add(novaFilaMunicipi("frm", "Formentera", 7957, 4));
		return resposta;
	}

	private FilaResultat novaFilaMunicipi(String codi, String nom, int habitants, int illa) {
		FilaResultat resposta = new FilaResultat();
		resposta.addColumna(new ParellaCodiValor("codi", codi));
		resposta.addColumna(new ParellaCodiValor("nom", nom));
		resposta.addColumna(new ParellaCodiValor("habitants", habitants));
		resposta.addColumna(new ParellaCodiValor("illa", illa));
		return resposta;
	}

}
