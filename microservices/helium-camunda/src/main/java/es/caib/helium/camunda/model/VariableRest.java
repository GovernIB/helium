package es.caib.helium.camunda.model;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

@Data
@Slf4j
public class VariableRest {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

    private String nom;
    private TipusVar tipus;
    private String valor;

    public Object convertirValor() {
        try {
            if (tipus != null) {
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
                    default:
                        return valor;
                }
            } else {
                return valor;
            }
        } catch (Exception ex) {
            log.error("Error al convertir [valor: " + valor + ", tipus: " + tipus + "]");
            return valor;
        }
    }

    public VariableRest(String nom, Object valor) {
        this.nom = nom;
        this.tipus = extractTipus(valor);
        this.valor = convertir(valor, tipus);
    }

    private TipusVar extractTipus(Object valor) {
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
        return TipusVar.STRING;
    }

    private String convertir(Object valor, TipusVar tipus) {
        try {
            if (tipus != null) {
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
                    case REGISTRE:

                        // TODO: Variables tipus REGISTRE!!!
                    case PREU:
                    default:
                        return valor.toString();
                }
            } else {
                return valor.toString();
            }
        } catch (Exception ex) {
            log.error("Error al convertir [objecte: " + valor.toString() + ", tipus: " + tipus + "]");
            return valor.toString();
        }
    }
}
