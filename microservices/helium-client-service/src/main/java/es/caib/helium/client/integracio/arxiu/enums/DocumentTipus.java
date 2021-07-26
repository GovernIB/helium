package es.caib.helium.client.integracio.arxiu.enums;

public enum DocumentTipus {

    RESOLUCIO("TD01"),
    ACORD("TD02"),
    CONTRACTE("TD03"),
    CONVENI("TD04"),
    DECLARACIO("TD05"),
    COMUNICACIO("TD06"),
    NOTIFICACIO("TD07"),
    PUBLICACIO("TD08"),
    JUSTIFICANT_RECEPCIO("TD09"),
    ACTA("TD10"),
    CERTIFICAT("TD11"),
    DILIGENCIA("TD12"),
    INFORME("TD13"),
    SOLICITUD("TD14"),
    DENUNCIA("TD15"),
    ALEGACIO("TD16"),
    RECURS("TD17"),
    COMUNICACIO_CIUTADA("TD18"),
    FACTURA("TD19"),
    ALTRES_INCAUTATS("TD20"),
    LLEI("TD51"),
    MOCIO("TD52"),
    INSTRUCCIO("TD53"),
    CONVOCATORIA("TD54"),
    ORDRE_DIA("TD55"),
    INFORME_PONENCIA("TD56"),
    DICTAMEN_COMISSIO("TD57"),
    INICIATIVA_LEGISLATIVA("TD58"),
    PREGUNTA("TD59"),
    INTERPELACIO("TD60"),
    RESPOSTA("TD61"),
    PROPOSICIO_NO_LLEI("TD62"),
    ESQUEMA("TD63"),
    PROPOSTA_RESOLUCIO("TD64"),
    COMPAREIXENSA("TD65"),
    SOLICITUD_INFORMACIO("TD66"),
    ESCRIT("TD67"),
    INICIATIVA__LEGISLATIVA("TD68"),
    PETICIO("TD69"),
    ALTRES("TD99");

    private String str;

    private DocumentTipus(String str) {
        this.str = str;
    }

    public static DocumentTipus toEnum(String str) {
        if (str != null) {
            DocumentTipus[] var4;
            int var3 = (var4 = values()).length;

            for(int var2 = 0; var2 < var3; ++var2) {
                DocumentTipus valor = var4[var2];
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
