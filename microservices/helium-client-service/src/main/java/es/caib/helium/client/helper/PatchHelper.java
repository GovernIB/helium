package es.caib.helium.client.helper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import javax.json.JsonPatchBuilder;
import javax.json.JsonValue;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class PatchHelper {

    public static boolean replaceStringProperty(
            JsonPatchBuilder jpb,
            String propertyName,
            String finalValue) {
        if (finalValue == null) {
            jpb.replace("/" + propertyName, JsonValue.NULL);
        } else {
            jpb.replace("/" + propertyName, finalValue);
        }
        return true;
    }
    public static boolean replaceStringProperty(
            JsonPatchBuilder jpb,
            String propertyName,
            String originalValue,
            String finalValue) {
        if (!Objects.equals(originalValue, finalValue)) {
            if (finalValue == null) {
                jpb.replace("/" + propertyName, JsonValue.NULL);
            } else {
                jpb.replace("/" + propertyName, finalValue);
            }
            return true;
        }
        return false;
    }

    public static boolean replaceIntegerProperty(
            JsonPatchBuilder jpb,
            String propertyName,
            Integer finalValue) {
        if (finalValue == null) {
            jpb.replace("/" + propertyName, JsonValue.NULL);
        } else {
            jpb.replace("/" + propertyName, finalValue);
        }
        return true;
    }
    public static boolean replaceIntegerProperty(
            JsonPatchBuilder jpb,
            String propertyName,
            Integer originalValue,
            Integer finalValue) {
        if (!Objects.equals(originalValue, finalValue)) {
            if (finalValue == null) {
                jpb.replace("/" + propertyName, JsonValue.NULL);
            } else {
                jpb.replace("/" + propertyName, finalValue);
            }
            return true;
        }
        return false;
    }

    public static boolean replaceLongProperty(
            JsonPatchBuilder jpb,
            String propertyName,
            Long finalValue) {
        if (finalValue == null) {
            jpb.replace("/" + propertyName, JsonValue.NULL);
        } else {
            jpb.replace("/" + propertyName, finalValue.toString());
        }
        return true;
    }
    public static boolean replaceLongProperty(
            JsonPatchBuilder jpb,
            String propertyName,
            Long originalValue,
            Long finalValue) {
        if (!Objects.equals(originalValue, finalValue)) {
            if (finalValue == null) {
                jpb.replace("/" + propertyName, JsonValue.NULL);
            } else {
                jpb.replace("/" + propertyName, finalValue.toString());
            }
            return true;
        }
        return false;
    }

    public static boolean replaceDateProperty(
            JsonPatchBuilder jpb,
            String propertyName,
            Date finalValue) {
        if (finalValue == null) {
            jpb.replace("/" + propertyName, JsonValue.NULL);
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            jpb.replace("/" + propertyName, sdf.format(finalValue));
        }
        return true;
    }
    public static boolean replaceDateProperty(
            JsonPatchBuilder jpb,
            String propertyName,
            Date originalValue,
            Date finalValue) {
        if (!Objects.equals(originalValue, finalValue)) {
            if (finalValue == null) {
                jpb.replace("/" + propertyName, JsonValue.NULL);
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                jpb.replace("/" + propertyName, sdf.format(finalValue));
            }
            return true;
        }
        return false;
    }

    public static boolean replaceBooleanProperty(
            JsonPatchBuilder jpb,
            String propertyName,
            Boolean finalValue) {
        if (finalValue == null) {
            jpb.replace("/" + propertyName, JsonValue.NULL);
        } else {
            jpb.replace("/" + propertyName, finalValue);
        }
        return true;
    }
    public static boolean replaceBooleanProperty(
            JsonPatchBuilder jpb,
            String propertyName,
            Boolean originalValue,
            Boolean finalValue) {
        if (!Objects.equals(originalValue, finalValue)) {
            if (finalValue == null) {
                jpb.replace("/" + propertyName, JsonValue.NULL);
            } else {
                jpb.replace("/" + propertyName, finalValue);
            }
            return true;
        }
        return false;
    }

    @SneakyThrows
    public static boolean replaceArrayProperty(
            JsonPatchBuilder jpb,
            String propertyName,
            List finalValue) {
        if (finalValue == null) {
            jpb.replace("/" + propertyName, JsonValue.NULL);
        } else {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonArray = objectMapper.writeValueAsString(finalValue);
            jpb.replace("/" + propertyName, jsonArray);
        }
        return true;
    }
    @SneakyThrows
    public static boolean replaceArrayProperty(
            JsonPatchBuilder jpb,
            String propertyName,
            List originalValue,
            List finalValue) {
        if (!Objects.equals(originalValue, finalValue)) {
            if (finalValue == null) {
                jpb.replace("/" + propertyName, JsonValue.NULL);
            } else {
                ObjectMapper objectMapper = new ObjectMapper();
                String jsonArray = objectMapper.writeValueAsString(finalValue);
                jpb.replace("/" + propertyName, jsonArray);
            }
            return true;
        }
        return false;
    }

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
