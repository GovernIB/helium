package net.conselldemallorca.helium.back.rest.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.conselldemallorca.helium.back.rest.model.VariableRest;
import lombok.extern.slf4j.Slf4j;
import net.conselldemallorca.helium.api.dto.Registre;
import net.conselldemallorca.helium.api.dto.Termini;
import net.conselldemallorca.helium.api.dto.TipusVar;

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
        List<VariableRest> variablesRest = new ArrayList<VariableRest>();
        if (variables != null) {
            for (Map.Entry<String, Object> variable: variables.entrySet()) {
                variablesRest.add(objectToVariable(variable.getKey(), variable.getValue()));
            }
        }
        return variablesRest;
    }

    public static Map<String, Object> variableRestToObjectMapConvert(List<VariableRest> variables) {
        Map<String, Object> variableMap = new HashMap<String, Object>();
        if (variables != null) {
            for (VariableRest variable: variables) {
                if (variable.getValor() != null) {
                    variableMap.put(variable.getNom(), variableToObject(variable));
                }
            }
        }
        return variableMap;
    }


    public static VariableRest objectToVariable(String nom, Object valor) {
        TipusVar tipus = getTipus(valor);

        return VariableRest.builder()
                .nom(nom)
                .tipus(tipus)
                .className(valor != null ? valor.getClass().getName() : null)
                .valor(valor != null ? valorToString(valor, tipus) : null)
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
                case PREU:
                case STRING:
                    return String.valueOf(valor);
                case DATE:
                    return DATE_FORMAT.format((Date)valor);
                case TERMINI:
                    return Termini.valueFromTermini((Termini)valor);
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