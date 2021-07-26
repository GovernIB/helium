package es.caib.helium.client.integracio.arxiu.enums;

public enum ExpedientEstat {

    OBERT("E01"),
    TANCAT("E02"),
    INDEX_REMISSIO("E03");

    private String str;

    private ExpedientEstat(String str) {
        this.str = str;
    }

    public static ExpedientEstat toEnum(String str) {
        if (str != null) {
            ExpedientEstat[] var4;
            int var3 = (var4 = values()).length;

            for(int var2 = 0; var2 < var3; ++var2) {
                ExpedientEstat valor = var4[var2];
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
