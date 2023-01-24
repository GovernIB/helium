package net.conselldemallorca.helium.v3.core.api.dto.handlers;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class HandlerDtoTest {
	
	public static void main(String... args) throws Exception {
		System.out.println("- Inici -");
				
		List<HandlerDto> handlers;
		ObjectWriter ow;
		String json;

		System.out.println("0) Map<String,String> a JSON i JSON a map: ");
		Map<String, String> predefinitDades = new HashMap<String, String>();
		for (int i = 0; i < 10; i ++) {
			predefinitDades.put("clau_" + i, "valor_" + i);
		}
		ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		json = ow.writeValueAsString(predefinitDades);
		System.out.println("Conversió a Json: " + json);
		
		Map<String, String> result =
		        new ObjectMapper().readValue(json, new TypeReference<Map<String, String>>(){});

		System.out.println("Conversió a Map: " + result);
		
		
		System.out.println("1) Exemple de crear estructura: ");

		handlers = new ArrayList<HandlerDto>();
		
		for (int i = 0; i < 10; i++) {
			HandlerDto handler = new HandlerDto();
			handler.setClasse("net....handler.class");
			handler.setNom("Nom " + i);
			handler.setDescripcio("Descripcio " + i);
			handler.setParametres(new ArrayList<HandlerParametreDto>());
			for (int j = 0; j < i; j++) {
				HandlerParametreDto parametre = new HandlerParametreDto();
				parametre.setNom("Nom " + j);				
				parametre.setCodi("codi"+j);
				parametre.setObligatori(j % 2 == 1);
				parametre.setParam("param"+j);
				parametre.setVarParam("param"+j);
				parametre.setParamDesc("Descripcio paràmetre " + j);
				parametre.setVarParamDesc("Descripcio variable paràmetre " + j);
				handler.getParametres().add(parametre);
			}
			handlers.add(handler);
		}
		ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		json = ow.writeValueAsString(handlers);
		
		System.out.println(json);
		
		System.out.println("2) Exemple de llegir des d'arxiu: ");
		
		InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("handlersPredefinits.json");
		
		ObjectMapper mapper = new ObjectMapper();
		handlers = mapper.readValue(in, new TypeReference<List<HandlerDto>>(){});
		json = ow.writeValueAsString(handlers);		
		System.out.println(json);
		
		
		System.out.println("- Fi -");
	}

}
