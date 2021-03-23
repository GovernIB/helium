package es.caib.helium.domini;

import es.caib.helium.domini.domain.Domini;
import es.caib.helium.domini.model.DominiDto;
import es.caib.helium.domini.model.FilaResultat;
import es.caib.helium.domini.model.OrigenCredencialsEnum;
import es.caib.helium.domini.model.ParellaCodiValor;
import es.caib.helium.domini.model.ResultatDomini;
import es.caib.helium.domini.model.TipusAuthEnum;
import es.caib.helium.domini.model.TipusDominiEnum;
import es.caib.helium.domini.ws.ConsultaDominiResponse;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DominiTestHelper {

    public static final String CODI = "codi";
    public static final String VALOR = "valor";
    public static final String SOAP_RESPONSE =
            "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "<soap:Body>" +
                    "<ns2:consultaDominiResponse xmlns:ns2=\"http://domini.integracio.helium.conselldemallorca.net/\">" +
                        "<return>" +
                            "<columnes>" +
                                "<codi>codiDenominacio</codi>" +
                                "<valor xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xsi:type=\"xs:string\">A04003003 - Gobierno de las Islas Baleares</valor>" +
                            "</columnes>" +
                            "<columnes>" +
                                "<codi>codi</codi>" +
                                "<valor xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xsi:type=\"xs:string\">A04003003</valor>" +
                            "</columnes>" +
                            "<columnes>" +
                                "<codi>denominacio</codi>" +
                                "<valor xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xsi:type=\"xs:string\">Gobierno de las Islas Baleares</valor>" +
                            "</columnes>" +
                            "<columnes>" +
                                "<codi>tipusEntitatPublica</codi>" +
                            "</columnes>" +
                            "<columnes>" +
                                "<codi>tipusUnitatOrganica</codi>" +
                            "</columnes>" +
                            "<columnes>" +
                                "<codi>sigles</codi>" +
                            "</columnes>" +
                            "<columnes>" +
                                "<codi>codiUnitatSuperior</codi>" +
                                "<valor xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xsi:type=\"xs:string\">A99999999</valor>" +
                            "</columnes>" +
                            "<columnes>" +
                                "<codi>codiUnitatArrel</codi>" +
                                "<valor xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xsi:type=\"xs:string\">A04003003</valor>" +
                            "</columnes>" +
                            "<columnes>" +
                                "<codi>estat</codi>" +
                                "<valor xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xsi:type=\"xs:string\">V</valor>" +
                            "</columnes>" +
                        "</return>" +
                    "</ns2:consultaDominiResponse>" +
                "</soap:Body>" +
            "</soap:Envelope>";

    public static Domini generateDomini(
            int index,
            String dominiCodi,
            Long entorn,
            Long expedientTipus
    ) {
        return generateDomini(
                index,
                dominiCodi,
                entorn,
                expedientTipus,
                null,
                null);
    }

    public static Domini generateDomini(
            int index,
            String dominiCodi,
            Long entorn,
            Long expedientTipus,
            TipusDominiEnum tipusDomini,
            TipusAuthEnum tipusAuth
    ) {
        return Domini.builder()
                .codi(dominiCodi)
                .nom("Domini_nom" + index)
                .tipus(tipusDomini != null ? tipusDomini : TipusDominiEnum.values()[index%3])
                .url("http://localhost:8080/api/rest" + index)
                .tipusAuth(tipusAuth != null ? tipusAuth : TipusAuthEnum.values()[index%3])
                .origenCredencials(OrigenCredencialsEnum.values()[index%2])
                .usuari("usuari" + index)
                .contrasenya("password" + index)
                .sql("select * from hel_domini" + index)
                .jndiDatasource("java:/es.caib.helium.db" + index)
                .descripcio("Domini_descripcio" + index)
                .cacheSegons(3600)
                .timeout(300)
                .ordreParams("none")
                .entorn(entorn)
                .expedientTipus(expedientTipus)
                .build();
    }

    public static DominiDto generateDominiDto(
            int index,
            String dominiCodi,
            Long entorn,
            Long expedientTipus,
            TipusDominiEnum tipusDomini,
            TipusAuthEnum tipusAuth
    ) {
        return DominiDto.builder()
                .codi(dominiCodi)
                .nom("Domini_nom" + index)
                .tipus(tipusDomini != null ? tipusDomini : TipusDominiEnum.values()[index%3])
                .url("http://localhost:8080/api/rest" + index)
                .tipusAuth(tipusAuth != null ? tipusAuth : TipusAuthEnum.values()[index%3])
                .origenCredencials(OrigenCredencialsEnum.values()[index%2])
                .usuari("usuari" + index)
                .contrasenya("password" + index)
                .sql("select * from hel_domini" + index)
                .jndiDatasource("java:/es.caib.helium.db" + index)
                .descripcio("Domini_descripcio" + index)
                .cacheSegons(3600)
                .timeout(300)
                .ordreParams("none")
                .entorn(entorn)
                .expedientTipus(expedientTipus)
                .build();
    }

    public static void comprovaDomini(Domini domini, Domini trobat) {
        assertAll("Comprovar dades del domini",
                () -> assertEquals(domini.getCodi(), trobat.getCodi(), "Codi incorrecte"),
                () -> assertEquals(domini.getNom(), trobat.getNom(), "Nom incorrecte"),
                () -> assertEquals(domini.getTipus(), trobat.getTipus(), "Tipus incorrecte"),
                () -> assertEquals(domini.getUrl(), trobat.getUrl(), "Url incorrecte"),
                () -> assertEquals(domini.getTipusAuth(), trobat.getTipusAuth(), "Tipus autenticació incorrecte"),
                () -> assertEquals(domini.getOrigenCredencials(), trobat.getOrigenCredencials(), "Origen credencials incorrecte"),
                () -> assertEquals(domini.getUsuari(), trobat.getUsuari(), "Usuari incorrecte"),
                () -> assertEquals(domini.getContrasenya(), trobat.getContrasenya(), "Contrasenya incorrecte"),
                () -> assertEquals(domini.getSql(), trobat.getSql(), "Sql incorrecte"),
                () -> assertEquals(domini.getJndiDatasource(), trobat.getJndiDatasource(), "Jndi incorrecte"),
                () -> assertEquals(domini.getDescripcio(), trobat.getDescripcio(), "Descripció incorrecte"),
                () -> assertEquals(domini.getCacheSegons(), trobat.getCacheSegons(), "Cache incorrecte"),
                () -> assertEquals(domini.getTimeout(), trobat.getTimeout(), "Timeout incorrecte"),
                () -> assertEquals(domini.getOrdreParams(), trobat.getOrdreParams(), "Ordre incorrecte"),
                () -> assertEquals(domini.getEntorn(), trobat.getEntorn(), "Entorn incorrecte"),
                () -> assertEquals(domini.getExpedientTipus(), trobat.getExpedientTipus(), "Tipus d'expedient incorrecte")
        );
    }

    public static void comprovaDomini(DominiDto domini, DominiDto trobat) {
        assertAll("Comprovar dades del domini",
                () -> assertEquals(domini.getCodi(), trobat.getCodi(), "Codi incorrecte"),
                () -> assertEquals(domini.getNom(), trobat.getNom(), "Nom incorrecte"),
                () -> assertEquals(domini.getTipus(), trobat.getTipus(), "Tipus incorrecte"),
                () -> assertEquals(domini.getUrl(), trobat.getUrl(), "Url incorrecte"),
                () -> assertEquals(domini.getTipusAuth(), trobat.getTipusAuth(), "Tipus autenticació incorrecte"),
                () -> assertEquals(domini.getOrigenCredencials(), trobat.getOrigenCredencials(), "Origen credencials incorrecte"),
                () -> assertEquals(domini.getUsuari(), trobat.getUsuari(), "Usuari incorrecte"),
                () -> assertEquals(domini.getContrasenya(), trobat.getContrasenya(), "Contrasenya incorrecte"),
                () -> assertEquals(domini.getSql(), trobat.getSql(), "Sql incorrecte"),
                () -> assertEquals(domini.getJndiDatasource(), trobat.getJndiDatasource(), "Jndi incorrecte"),
                () -> assertEquals(domini.getDescripcio(), trobat.getDescripcio(), "Descripció incorrecte"),
                () -> assertEquals(domini.getCacheSegons(), trobat.getCacheSegons(), "Cache incorrecte"),
                () -> assertEquals(domini.getTimeout(), trobat.getTimeout(), "Timeout incorrecte"),
                () -> assertEquals(domini.getOrdreParams(), trobat.getOrdreParams(), "Ordre incorrecte"),
                () -> assertEquals(domini.getEntorn(), trobat.getEntorn(), "Entorn incorrecte"),
                () -> assertEquals(domini.getExpedientTipus(), trobat.getExpedientTipus(), "Tipus d'expedient incorrecte")
        );
    }

    public static ResultatDomini generateResultatDomini() {
        ResultatDomini resultatDomini = new ResultatDomini();
        FilaResultat fila = new FilaResultat();
        List<ParellaCodiValor> columnes = new ArrayList<>();
        columnes.add(ParellaCodiValor.builder().codi(CODI).valor(VALOR).build());
        fila.setColumnes(columnes);
        resultatDomini.add(fila);
        return resultatDomini;
    }
    public static ConsultaDominiResponse generateConsultaDominiResponse() {
        ConsultaDominiResponse consultaDominiResponse = new ConsultaDominiResponse();
        consultaDominiResponse.getReturn().addAll(generateResultatDomini());
        return consultaDominiResponse;
    }

    public static Map<String, String> generateParams() {
        Map<String, String> parametres = new HashMap<>();
        parametres.put("param1", "valor1");
        parametres.put("param2", "valor2");
        return parametres;
    }

    public static String generateJsonResponse(Map<String, String> params, String identificador) {

        String paramResponse = params.entrySet().stream().map(
                param -> "{\"codi\":\"" + param.getKey() + "\",\"valor\":\"" + param.getValue() + "\"}"
        ).collect(Collectors.joining(
                ",",
                "{\"columnes\":[",
                ",{\"codi\":\"dominicodi\",\"valor\":\"" + identificador + "\"}]}"));
        return "[" +
                    paramResponse + "," +
                    paramResponse + "," +
                    paramResponse +
                "]";
    }

    public static String encodeBasicAuth(Domini domini) {
        Charset charset = StandardCharsets.ISO_8859_1;
        CharsetEncoder encoder = charset.newEncoder();
        String credentialsString = domini.getUsuari() + ":" + domini.getContrasenya();
        byte[] encodedBytes = Base64.getEncoder().encode(credentialsString.getBytes(charset));
        return "Basic " + new String(encodedBytes, charset);
    }
}
