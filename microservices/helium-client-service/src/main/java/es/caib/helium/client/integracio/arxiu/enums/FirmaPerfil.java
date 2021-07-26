package es.caib.helium.client.integracio.arxiu.enums;

public enum FirmaPerfil {

    BES("BES"),
    EPES("EPES"),
    LTV("LTV"),
    T("T"),
    C("C"),
    X("X"),
    XL("XL"),
    A("A"),
    BASIC("BASIC"),
    Basic("Basic"),
    BASELINE_B_LEVEL("BASELINE_B_LEVEL"),
    BASELINE_T_LEVEL("BASELINE_T_LEVEL"),
    BASELINE_LT_LEVEL("BASELINE_LT_LEVEL"),
    BASELINE_LTA_LEVEL("BASELINE_LTA_LEVEL"),
    BASELINE_T("BASELINE_T"),
    LTA("LTA");

    private String str;

    private FirmaPerfil(String str) {
        this.str = str;
    }

    public static FirmaPerfil toEnum(String str) {
        if (str != null) {
            FirmaPerfil[] var4;
            int var3 = (var4 = values()).length;

            for(int var2 = 0; var2 < var3; ++var2) {
                FirmaPerfil valor = var4[var2];
                if (valor.toString().equals(str)) {
                    return valor;
                }
            }
        }

        return null;
    }

    public String toString() {
        return this.str;
    }
}
