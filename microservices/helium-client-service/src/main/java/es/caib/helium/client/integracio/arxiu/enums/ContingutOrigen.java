package es.caib.helium.client.integracio.arxiu.enums;

public enum ContingutOrigen {

    CIUTADA("0"),
    ADMINISTRACIO("1");

    private String str;

    private ContingutOrigen(String str) {
        this.str = str;
    }

    public static ContingutOrigen toEnum(String str) {
        if (str != null) {
            ContingutOrigen[] var4;
            int var3 = (var4 = values()).length;

            for(int var2 = 0; var2 < var3; ++var2) {
                ContingutOrigen valor = var4[var2];
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
