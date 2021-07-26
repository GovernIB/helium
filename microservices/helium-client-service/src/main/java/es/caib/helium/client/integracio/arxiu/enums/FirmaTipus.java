package es.caib.helium.client.integracio.arxiu.enums;

public enum FirmaTipus {

    CSV("TF01"),
    XADES_DET("TF02"),
    XADES_ENV("TF03"),
    CADES_DET("TF04"),
    CADES_ATT("TF05"),
    PADES("TF06"),
    SMIME("TF07"),
    ODT("TF08"),
    OOXML("TF09");

    private String str;

    private FirmaTipus(String str) {
        this.str = str;
    }

    public static FirmaTipus toEnum(String str) {
        if (str != null) {
            FirmaTipus[] var4;
            int var3 = (var4 = values()).length;

            for(int var2 = 0; var2 < var3; ++var2) {
                FirmaTipus valor = var4[var2];
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
