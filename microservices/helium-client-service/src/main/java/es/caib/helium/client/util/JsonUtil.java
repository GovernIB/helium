/**
 * 
 */
package es.caib.helium.client.util;

import javax.json.JsonPatchBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/** Classe amb mètodes útils comuns.
 */
public class JsonUtil {

	/** Transforma un objecte de tipus JsonPatshBuilder a un objecte JsonNode per
	 * poder enviar-lo com a paràmetre en les crides patch. 
	 * 
	 * @param jpb
	 * @return
	 */
	public static JsonNode toJsonNode(JsonPatchBuilder jpb) {
		JsonNode patchJson = null;
		try {
			patchJson = new ObjectMapper().readTree(jpb.build().toString());
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
		return patchJson;
	}
}
