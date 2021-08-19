package es.caib.helium.back.helper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import static es.caib.helium.back.helper.TascaFormValidatorHelper.STRING_MAX_LENGTH;

@Slf4j
public class ValidationHelper {

    public static boolean listMembersNotEmpty(Object valors) {
        if (valors == null) {
            return false;
        }
        for (int i = 0; i < Array.getLength(valors); i++) {
            Object valor = Array.get(valors, i);
            if ((valor instanceof String && "".equals(valor)) || (!(valor instanceof String) && valor == null)) {
                return false;
            }
        }
        return true;
    }

    public static boolean terminiNotEmpty(String[] termini) {
        if (    termini == null ||
                termini.length < 3 ||
                (termini[0].equalsIgnoreCase("0") &&
                        termini[1].equalsIgnoreCase("0") &&
                        (termini[2].equalsIgnoreCase("") || termini[2] == null))) {
            return false;
        }
        return true;
    }

    public static boolean listTerminisNotEmpty(Object valors) {
        if (valors == null) {
            return false;
        }
        for (int i = 0; i < Array.getLength(valors); i++) {
            Object valor = Array.get(valors, i);
            String[] valor_arr = (String[])valor;
            if (    valor == null ||
                    (valor_arr).length < 3 ||
                    (valor_arr[0].equalsIgnoreCase("0") &&
                            valor_arr[1].equalsIgnoreCase("0") &&
                            (valor_arr[2].equalsIgnoreCase("0") || valor_arr[2] == null)))
                return false;
        }
        return true;
    }

    public static boolean registreNotEmpty(Object valorRegistre, String strCampsRequired, boolean multiple, boolean required) throws Exception {
        if (valorRegistre == null) {
            return false;
        }

        var objectMapper = new ObjectMapper();
        var campsRequired = objectMapper.readValue(strCampsRequired, new TypeReference<List<CampRegistreRequired>>(){});
        Object[] registres = multiple ? (Object[])valorRegistre : new Object[] {valorRegistre};
        int numReg = 0;
        for (Object reg: registres) {
            boolean emptyReg = true;
            for (CampRegistreRequired campRegistre : campsRequired) {
                if (campRegistre.isRequired()) {
                    boolean emptyVal = true;
                    Object oValor = PropertyUtils.getProperty(reg, campRegistre.getVarCodi());
                    if (oValor != null) {
                        // Terminis
                        if (oValor instanceof String[]) {
                            String[] oValor_arr = (String[]) oValor;
                            emptyVal = (oValor_arr.length < 3 ||
                                    (oValor_arr[0].equalsIgnoreCase("0") &&
                                            oValor_arr[1].equalsIgnoreCase("0") &&
                                            (oValor_arr[2].equalsIgnoreCase("0") || oValor_arr[2] == null)));
                        } else if (oValor instanceof String && "".equals(oValor)) {
                            emptyVal = true;
                        } else {
                            emptyVal = false;
                        }
                    }
                    if (emptyVal) {
                        if (campRegistre.isRequired()) {
                            return false;
                        }
                    } else {
                        emptyReg = false;
                    }
                } else {
                    emptyReg = false;
                }
            }
            if (emptyReg && (numReg > 0 || required))
                return false;
            numReg++;
        }

        return true;
    }

    public static boolean stringLengthValid(Object valor) {
        if (valor != null && ((String) valor).length() > STRING_MAX_LENGTH)
            return false;
        return true;
    }

    public static boolean listStringLengthValid(Object valors) {
        if (valors != null) {
            for (String valor : (String[])valors) {
                if (valor != null && valor.length() > STRING_MAX_LENGTH)
                    return false;
            }
        }
        return true;
    }

    public static boolean dataValida(Object valor) {
        try {
            if (valor != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                sdf.setLenient(false);
                sdf.parse((String) valor);
            }
        } catch (ParseException ex) {
            return false;
        }
        return true;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CampRegistreRequired {
        String varCodi;
        boolean required;
        boolean multiple;
    }

}
