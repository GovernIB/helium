package es.caib.helium.expedient;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.Date;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import es.caib.helium.expedient.domain.Expedient;
import es.caib.helium.expedient.model.ExpedientDto;
import es.caib.helium.expedient.model.ExpedientEstatTipusEnum;

public class ExpedientTestHelper {
	
	@Test
    @DisplayName("Prova de generar expedients iguals")
	public void generateExpedientTest() {
		Date dataInici = new Date();
		Expedient expedient1 = ExpedientTestHelper.generateExpedient(0, 1L, 2L, 3L, "pi1", "1/2021", "títol");
		expedient1.setDataInici(dataInici);
		Expedient expedient2 = ExpedientTestHelper.generateExpedient(0, 1L, 2L, 3L, "pi1", "1/2021", "títol");
		expedient2.setDataInici(dataInici);
		ExpedientTestHelper.comprovaExpedient(expedient1, expedient2);
		assertNotEquals(expedient1, expedient2);
		assertNotEquals(expedient1.hashCode(), expedient2.hashCode());
	}

	@Test
    @DisplayName("Prova de generar expedients DtO iguals")
	public void generateExpedientDtoTest() {
		Date dataInici = new Date();
		ExpedientDto expedient1 = ExpedientTestHelper.generateExpedientDto(0, 1L, 2L, 3L, "pi1", "1/2021", "títol");
		expedient1.setDataInici(dataInici);
		ExpedientDto expedient2 = ExpedientTestHelper.generateExpedientDto(0, 1L, 2L, 3L, "pi1", "1/2021", "títol");
		expedient2.setDataInici(dataInici);
		ExpedientTestHelper.comprovaExpedient(expedient1, expedient2);
		assertEquals(expedient1, expedient2);
		assertEquals(expedient1.hashCode(), expedient2.hashCode());
	}

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
        		.numeroDefault(expedientNumero)
        		.titol(expedientTitol)
        		.dataInici(new Date())
        		.estatTipus(ExpedientEstatTipusEnum.INICIAT)
        		
        		.comentariAnulat("comentari anul·lat")
        		.dataFi(null)
        		.estatId(1L)
        		.infoAturat("info aturat")
        		.alertesPendents(1L)
        		.alertesTotals(1L)
        		.ambErrors(false)
        		.anulat(false)

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
        		.numeroDefault(expedientNumero)
        		.titol(expedientTitol)
        		.dataInici(new Date())
        		.estatTipus(ExpedientEstatTipusEnum.INICIAT)
 
        		.comentariAnulat("comentari anul·lat")
        		.dataFi(new Date())
        		.estatId(1L)
        		.infoAturat("info aturat")
        		.alertesPendents(1L)
        		.alertesTotals(1L)
        		.ambErrors(true)
        		.anulat(false)
                .build();
    }

    public static void comprovaExpedient(Expedient expedient, Expedient trobat) {
        assertAll("Comprovar dades del expedient",
                () -> assertEquals(expedient.getEntornId(), trobat.getEntornId(), "Entorn id incorrecte"),
                () -> assertEquals(expedient.getExpedientTipusId(), trobat.getExpedientTipusId(), "Expedient tipus id incorrecte"),
                () -> assertEquals(expedient.getId(), trobat.getId(), "Expedient id incorrecte"),
                () -> assertEquals(expedient.getProcessInstanceId(), trobat.getProcessInstanceId(), "Process instance id incorrecte"),
                () -> assertEquals(expedient.getNumero(), trobat.getNumero(), "Numero incorrecte"),
                () -> assertEquals(expedient.getNumeroDefault(), trobat.getNumeroDefault(), "Numero default incorrecte"),
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
                () -> assertEquals(expedient.getNumeroDefault(), trobat.getNumeroDefault(), "Numero default incorrecte"),
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
