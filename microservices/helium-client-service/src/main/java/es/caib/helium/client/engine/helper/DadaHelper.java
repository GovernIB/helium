package es.caib.helium.client.engine.helper;

import es.caib.helium.client.dada.dades.enums.Tipus;
import es.caib.helium.client.dada.dades.model.Dada;
import es.caib.helium.client.dada.dades.model.Valor;
import es.caib.helium.client.dada.dades.model.ValorRegistre;
import es.caib.helium.client.dada.dades.model.ValorSimple;
import es.caib.helium.client.engine.model.CampRegistreRest;
import es.caib.helium.client.engine.model.CampRest;
import es.caib.helium.client.engine.model.Termini;
import es.caib.helium.client.model.ParellaCodiValor;
import org.springframework.util.ObjectUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DadaHelper {

    public static final DateFormat dataFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS ");
    public static final DateFormat dataFormatLectura = new SimpleDateFormat("dd/MM/yy HH:mm:ss");

    public static List<Dada> prepararDades(Map<String, Object> variables, List<CampRest> camps) {

        if (camps == null || camps.isEmpty())
            return new ArrayList<>();
        
        return camps.stream()
                .filter(c -> variables.keySet().contains(c.getCodi()))
                .map(c -> getDada(
                                c.getCodi(),
                                variables.get(c.getCodi()),
                                c,
                                Tipus.valueOf(c.getTipus().name()),
                                c.isMultiple()))
                .collect(Collectors.toList());
    }

    public static List<Dada> prepararDades(String varCodi, Object varValor, CampRest camp) throws Exception{

        if (camp != null && !ObjectUtils.containsConstant(Tipus.values(), camp.getTipus().name())) {
            throw new Exception("Error preparant les dades camp " + camp);
        }

        var dades = new ArrayList<Dada>();
        // Nova variable de tipus String (camp == null)
        var tipus = Tipus.STRING;
        var multiple = false;

        if (camp != null) {
            tipus = Tipus.valueOf(camp.getTipus().name());
            multiple = camp.isMultiple();
        }

        dades.add(getDada(varCodi, varValor, camp, tipus, multiple));
        return dades;
    }

    private static Dada getDada(String varCodi, Object varValor, CampRest camp, Tipus tipus, boolean multiple) {
        var dada = new Dada();
        dada.setCodi(varCodi);
        dada.setTipus(tipus);
        dada.setMultiple(multiple);

        if (Tipus.REGISTRE.equals(tipus)) {
            dada.setValor(getValorsRegistre(camp.getRegistreMembres(), varValor, multiple));
        } else {
            dada.setValor(getValorDada(varValor, multiple, tipus));
        }
        return dada;
    }

    private static List<Valor> getValorsRegistre(
            List<CampRegistreRest> registreMembres,
            Object varValor,
            boolean isMultiple) {

        ArrayList<Valor> valors = new ArrayList<>();
        if (!isMultiple) {
            valors.add(getValorRegistre(registreMembres, varValor));
            return valors;
        }
        var valorsObject = (Object[]) varValor;
        for (var v : valorsObject) {
            valors.add(getValorRegistre(registreMembres, v));
        }
        return valors;
    }

    private static Valor getValorRegistre(List<CampRegistreRest> registreMembres, Object varValor) {

        var valor = new ValorRegistre();
        List<Dada> campsRegistre = new ArrayList<>();
        var valorsObject = (Object[]) varValor;
        for (int i = 0; i < registreMembres.size(); i++) {
            var dadaRegistre = new Dada();
            dadaRegistre.setCodi(registreMembres.get(i).getMembre().getCodi());
            dadaRegistre.setTipus(Tipus.valueOf(registreMembres.get(i).getMembre().getTipus().name()));
            dadaRegistre.setMultiple(false);
            dadaRegistre.setValor(getValorDada(valorsObject[i], false, dadaRegistre.getTipus()));
            campsRegistre.add(dadaRegistre);
        }
        valor.setCamps(campsRegistre);
        return valor;
    }

    private static ArrayList<Valor> getValorDada(Object varValor, boolean isMultiple, Tipus tipus) {

        ArrayList<Valor> valors = new ArrayList<>();
        if (!isMultiple) {
            valors.add(getValorSimple(varValor, tipus));
            return valors;
        }
        var valorsObject = (Object[]) varValor;
        for (var v : valorsObject) {
            valors.add(getValorSimple(v, tipus));
        }
        return valors;
    }

    private static Valor getValorSimple(Object varValor, Tipus tipus) {
        var valor = new ValorSimple();
        valor.setValor(getStringFromObject(varValor, tipus));
        valor.setValorText(getStringFormatFromObject(varValor, tipus));
        return valor;
    }

    private static String getStringFromObject(Object o, Tipus tipus) {
        if (o == null) {
            return "";
        }
        if (o instanceof Date) {
            return dataFormat.format((Date) o);
        }
        if (o instanceof Long && Tipus.DATE.equals(tipus)) {
            return dataFormat.format(new Date((Long) o));
        }
        if (o instanceof ParellaCodiValor) {
            return ((ParellaCodiValor)o).getCodi();
        }

        return o.toString();
    }

    private static String getStringFormatFromObject(Object o, Tipus tipus) {
        if (o == null) {
            return "";
        }
        switch (tipus) {
            case BOOLEAN:
                return (Boolean) o ? "Si" : "No";
            case DATE:
                if (o instanceof Long)
                    return dataFormatLectura.format(new Date((Long) o));
                return dataFormatLectura.format((Date) o);
            case TERMINI:
                Termini t = Termini.valueFromString((String)o);
                return t.toString();
            case SELECCIO:
            case SUGGEST:
                if (o instanceof ParellaCodiValor) {
                    return ((ParellaCodiValor) o).getValor().toString();
                }
            default:
                return o.toString();
        }

    }

}
