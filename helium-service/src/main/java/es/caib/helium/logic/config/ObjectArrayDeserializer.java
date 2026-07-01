package es.caib.helium.logic.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class ObjectArrayDeserializer extends JsonDeserializer<Object> {
	@Override
	public Object deserialize(JsonParser p, DeserializationContext ctxt)
		throws IOException {
		JsonNode node = p.getCodec().readTree(p);
		return convert(node);
	}

	private Object convert(JsonNode node) {
		if (node.isArray()) {
			Object[] result = new Object[node.size()];

			for (int i = 0; i < node.size(); i++) {
				result[i] = convert(node.get(i));
			}

			return result;
		}

		if (node.isObject()) {
			Map<String, Object> map = new LinkedHashMap<>();

			node.fields().forEachRemaining(entry ->
				map.put(entry.getKey(), convert(entry.getValue()))
			);

			return map;
		}

		if (node.isTextual()) {
			return node.textValue();
		}

		if (node.isInt()) {
			return node.intValue();
		}

		if (node.isLong()) {
			return node.longValue();
		}

		if (node.isDouble()) {
			return node.doubleValue();
		}

		if (node.isBoolean()) {
			return node.booleanValue();
		}

		if (node.isNull()) {
			return null;
		}

		return node.asText();
	}
}
