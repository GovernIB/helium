package es.caib.helium.client.integracio.arxiu.enums;

public enum DocumentEstatElaboracio {

    ORIGINAL("EE01"),
    COPIA_CF("EE02"),
    COPIA_DP("EE03"),
    COPIA_PR("EE04"),
    ALTRES("EE99");

    private String str;

    private DocumentEstatElaboracio(String str) {
        this.str = str;
    }

    public static DocumentEstatElaboracio toEnum(String str) {
        if (str != null) {
            DocumentEstatElaboracio[] var4;
            int var3 = (var4 = values()).length;

            for(int var2 = 0; var2 < var3; ++var2) {
                DocumentEstatElaboracio valor = var4[var2];
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
