package es.caib.helium.expedient;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;

import es.caib.helium.expedient.domain.Expedient;
import es.caib.helium.expedient.model.ExpedientDto;
import es.caib.helium.expedient.model.ExpedientEstatTipusEnum;

public class ExpedientTestHelper {

    public static Expedient generateExpedient(
            int index,
            Long entornId,
            Long expedientTipusId,
            Long expedientId,
            String expedientProcessInstanceId,
            String expedientNumero,
            String expedientTitol
    ) {
        return Expedient.builder()
        		.entornId(entornId)
        		.expedientTipusId(expedientTipusId)
        		.id(expedientId)
        		.processInstanceId(expedientProcessInstanceId)
        		.numero(expedientNumero)
        		.titol(expedientTitol)
        		.dataInici(new Date())
        		.estatTipus(ExpedientEstatTipusEnum.INICIAT)
                .build();
    }

    public static ExpedientDto generateExpedientDto(
            int index,
            Long entornId,
            Long expedientTipusId,
            Long expedientId,
            String expedientProcessInstanceId,
            String expedientNumero,
            String expedientTitol
    ) {
        return ExpedientDto.builder()
        		.entornId(entornId)
        		.expedientTipusId(expedientTipusId)
        		.id(expedientId)
        		.processInstanceId(expedientProcessInstanceId)
        		.numero(expedientNumero)
        		.titol(expedientTitol)
        		.dataInici(new Date())
        		.estatTipus(ExpedientEstatTipusEnum.INICIAT)
                .build();
    }

    public static void comprovaExpedient(Expedient expedient, Expedient trobat) {
        assertAll("Comprovar dades del expedient",
                () -> assertEquals(expedient.getEntornId(), trobat.getEntornId(), "Entorn id incorrecte"),
                () -> assertEquals(expedient.getExpedientTipusId(), trobat.getExpedientTipusId(), "Expedient tipus id incorrecte"),
                () -> assertEquals(expedient.getId(), trobat.getId(), "Expedient id incorrecte"),
                () -> assertEquals(expedient.getProcessInstanceId(), trobat.getProcessInstanceId(), "Process instance id incorrecte"),
                () -> assertEquals(expedient.getNumero(), trobat.getNumero(), "Numero incorrecte"),
                () -> assertEquals(expedient.getTitol(), trobat.getTitol(), "Titol incorrecte"),
                () -> assertEquals(expedient.getDataInici(), trobat.getDataInici(), "Data inici incorrecte"),
                () -> assertEquals(expedient.getDataFi(), trobat.getDataFi(), "Data fi incorrecte"),
                () -> assertEquals(expedient.getEstatTipus(), trobat.getEstatTipus(), "Estat especific incorrecte"),
                () -> assertEquals(expedient.getEstatId(), trobat.getEstatId(), "Estat id incorrecte"),
                () -> assertEquals(expedient.isAturat(), trobat.isAturat(), "Aturat incorrecte"),
                () -> assertEquals(expedient.getInfoAturat(), trobat.getInfoAturat(), "Info aturat incorrecte"),
                () -> assertEquals(expedient.isAnulat(), trobat.isAnulat(), "Anul·lat incorrecte"),
                () -> assertEquals(expedient.getComentariAnulat(), trobat.getComentariAnulat(), "ComentariAnulat incorrecte"),
                () -> assertEquals(expedient.getAlertesTotals(), trobat.getAlertesTotals(), "Alertes totals incorrecte"),
                () -> assertEquals(expedient.getAlertesPendents(), trobat.getAlertesTotals(), "Alertes pendents incorrecte"),
                () -> assertEquals(expedient.isAmbErrors(), trobat.isAmbErrors(), "Amb errors incorrecte")
        );
    }

    public static void comprovaExpedient(ExpedientDto expedient, ExpedientDto trobat) {
        assertAll("Comprovar dades del expedient",
                () -> assertEquals(expedient.getEntornId(), trobat.getEntornId(), "Entorn id incorrecte"),
                () -> assertEquals(expedient.getExpedientTipusId(), trobat.getExpedientTipusId(), "Expedient tipus id incorrecte"),
                () -> assertEquals(expedient.getId(), trobat.getId(), "Expedient id incorrecte"),
                () -> assertEquals(expedient.getProcessInstanceId(), trobat.getProcessInstanceId(), "Process instance id incorrecte"),
                () -> assertEquals(expedient.getNumero(), trobat.getNumero(), "Numero incorrecte"),
                () -> assertEquals(expedient.getTitol(), trobat.getTitol(), "Titol incorrecte"),
                () -> assertEquals(expedient.getDataInici(), trobat.getDataInici(), "Data inici incorrecte"),
                () -> assertEquals(expedient.getDataFi(), trobat.getDataFi(), "Data fi incorrecte"),
                () -> assertEquals(expedient.getEstatTipus(), trobat.getEstatTipus(), "Estat especific incorrecte"),
                () -> assertEquals(expedient.getEstatId(), trobat.getEstatId(), "Estat id incorrecte"),
                () -> assertEquals(expedient.isAturat(), trobat.isAturat(), "Aturat incorrecte"),
                () -> assertEquals(expedient.getInfoAturat(), trobat.getInfoAturat(), "Info aturat incorrecte"),
                () -> assertEquals(expedient.isAnulat(), trobat.isAnulat(), "Anul·lat incorrecte"),
                () -> assertEquals(expedient.getComentariAnulat(), trobat.getComentariAnulat(), "ComentariAnulat incorrecte"),
                () -> assertEquals(expedient.getAlertesTotals(), trobat.getAlertesTotals(), "Alertes totals incorrecte"),
                () -> assertEquals(expedient.getAlertesPendents(), trobat.getAlertesTotals(), "Alertes pendents incorrecte"),
                () -> assertEquals(expedient.isAmbErrors(), trobat.isAmbErrors(), "Amb errors incorrecte")
        );
    }    
}
