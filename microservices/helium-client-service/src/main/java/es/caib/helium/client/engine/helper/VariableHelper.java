package es.caib.helium.client.engine.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.caib.helium.client.engine.model.Registre;
import es.caib.helium.client.engine.model.Termini;
import es.caib.helium.client.engine.model.TipusVar;
import es.caib.helium.client.engine.model.VariableRest;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class VariableHelper {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

    public static List<VariableRest> objectMapToVariableRestConvert(Map<String, Object> variables) {
        var variablesRest = new ArrayList<VariableRest>();
        if (variables != null) {
            variables.entrySet().stream().forEach(v -> variablesRest.add(
                    objectToVariable(v.getKey(), v.getValue())
            ));
        }
        return variablesRest;
    }

    public static Map<String, Object> variableRestToObjectMapConvert(List<VariableRest> variables) {
        var variableMap = new HashMap<String, Object>();
        if (variables != null) {
            variables.stream()
                    .filter(v -> v.getValor() != null)
                    .forEach(v -> variableMap.put(v.getNom(), variableToObject(v)));
        }
        return variableMap;
    }


    public static VariableRest objectToVariable(String nom, Object valor) {
        var tipus = getTipus(valor);

        return VariableRest.builder()
                .nom(nom)
                .tipus(tipus)
                .className(valor.getClass().getName())
                .valor(valorToString(valor, tipus))
                .build();
    }

    public static Object variableToObject(VariableRest variable) {
        return stringToValor(
                variable.getTipus(),
                variable.getClassName(),
                variable.getValor());
    }

    private static TipusVar getTipus(Object valor) {
        if (valor instanceof String)
            return TipusVar.STRING;
        if (valor instanceof Integer)
            return TipusVar.INTEGER;
        if (valor instanceof Long)
            return TipusVar.LONG;
        if (valor instanceof Date)
            return TipusVar.DATE;
        if (valor instanceof Float || valor instanceof Double)
            return TipusVar.FLOAT;
        if (valor instanceof BigDecimal)
            return TipusVar.PREU;
        if (valor instanceof Termini)
            return TipusVar.TERMINI;
        if (valor instanceof Boolean)
            return TipusVar.BOOLEAN;
        if (valor instanceof Registre)
            return TipusVar.REGISTRE;
        return TipusVar.OBJECT;
    }

    private static Object stringToValor(TipusVar tipus, String className, String valor) {
        try {
            switch (tipus) {
                case INTEGER:
                case LONG:
                    return Long.parseLong(valor);
                case FLOAT:
                    return Double.parseDouble(valor);
                case BOOLEAN:
                    return Boolean.valueOf(valor);
                case DATE:
                    String[] dataSplit = valor.split("/");
                    Calendar data = new GregorianCalendar();
                    data.set(Integer.parseInt(dataSplit[2]),Integer.parseInt(dataSplit[1]) - 1,Integer.parseInt(dataSplit[0]));
                    return data.getTime();
                case PREU:
                    return BigDecimal.valueOf(Double.parseDouble(valor));
                case TERMINI:
                    return Termini.valueFromString(valor);
                case REGISTRE:
                case OBJECT:
                    ObjectMapper mapper = new ObjectMapper();
                    return mapper.readValue(valor, Class.forName(className));
                default:
                    return valor;
            }
        } catch (Exception ex) {
            log.error("Error al convertir [valor: " + valor + ", tipus: " + tipus + "]");
            return valor;
        }
    }

    public static String valorToString(Object valor, TipusVar tipus) {
        try {
            switch (tipus) {
                case INTEGER:
                case LONG:
                case FLOAT:
                case BOOLEAN:
                    return String.valueOf(valor);
                case DATE:
                    return DATE_FORMAT.format((Date)valor);
                case TERMINI:
                    return Termini.valueFromTermini((Termini)valor);
                case PREU:
                    return valor.toString();
                case REGISTRE:
                    // TODO: Variables tipus REGISTRE!!!
                case OBJECT:
                default:
                    ObjectMapper mapper = new ObjectMapper();
                    return mapper.writeValueAsString(valor);
            }
        } catch (Exception ex) {
            log.error("Error al convertir [objecte: " + valor.toString() + ", tipus: " + tipus + "]");
            return valor.toString();
        }
    }
}
