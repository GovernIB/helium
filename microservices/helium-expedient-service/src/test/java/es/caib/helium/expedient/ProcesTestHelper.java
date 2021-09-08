package es.caib.helium.expedient;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.Date;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import es.caib.helium.expedient.domain.Expedient;
import es.caib.helium.expedient.domain.Proces;
import es.caib.helium.expedient.model.ProcesDto;

public class ProcesTestHelper {
	
	@Test
    @DisplayName("Prova de generar processos iguals")
	public void generateProcesTest() {
		Date dataInici = new Date();
		Proces proces1 = ProcesTestHelper.generateProces(0, "1", null, "JPBM01", "dp1", "descripcio1", dataInici);
		Proces proces2 = ProcesTestHelper.generateProces(0, "1", null, "JPBM01", "dp1", "descripcio1", dataInici);
		ProcesTestHelper.comprovaProces(proces1, proces2);
		assertNotEquals(proces1, proces2);
		assertNotEquals(proces1.hashCode(), proces2.hashCode());
	}

	@Test
    @DisplayName("Prova de generar processos DtO iguals")
	public void generateProcesDtoTest() {
		ProcesDto proces1 = ProcesTestHelper.generateProcesDto(0, "1", 1L, "p1", "descripcio1");
		ProcesDto proces2 = ProcesTestHelper.generateProcesDto(0, "1", 1L, "p1", "descripcio1");
		ProcesTestHelper.comprovaProces(proces1, proces2);
		assertEquals(proces1, proces2);
		assertEquals(proces1.hashCode(), proces2.hashCode());
	}

    public static Proces generateProces(
            int index,
            String procesId,
            Expedient expedient,
            String motor,
            String processDefinitionId,
            String descripcio,
            Date dataInici
    ) {
        Proces proces = Proces.builder()
        		.procesId(procesId)
        		.expedient(expedient)
        		.motor(motor)
        		.processDefinitionId(processDefinitionId)
        		.descripcio(descripcio)
        		
        		.dataInici(dataInici)
        		.build();
        proces.setProcesArrel(proces);
        
    	return proces;
    }

    public static ProcesDto generateProcesDto(
            int index,
            String procesId,
            Long expedientId,
            String processDefinitionId,
            String descripcio) {
        ProcesDto proces = ProcesDto.builder()
        		.procesId(procesId)
        		.expedientId(expedientId)
        		.processDefinitionId(processDefinitionId)
        		.descripcio(descripcio)
        		
        		.dataInici(new Date())
        		.build();
        proces.setProcesArrelId(proces.getProcesId());
        
    	return proces;
    }

    public static void comprovaProces(Proces proces, Proces trobat) {
        assertAll("Comprovar dades del proces",
                () -> assertEquals(proces.getId(), trobat.getId(), "Proces id incorrecte"),
                () -> assertEquals(proces.getExpedient(), trobat.getExpedient(), "Expedient incorrecte"),
                () -> assertEquals(proces.getProcesArrel().getId(), trobat.getProcesArrel().getId(), "Proces arrel incorrecte"),
                () -> assertEquals(proces.getProcesPare(), trobat.getProcesPare(), "Proces pare incorrecte"),
                () -> assertEquals(proces.getProcessDefinitionId(), trobat.getProcessDefinitionId(), "ProvessDefinitionId incorrecte"),
                () -> assertEquals(proces.getDescripcio(), trobat.getDescripcio(), "Descripcio incorrecte"),
                () -> assertEquals(proces.getDataInici(), trobat.getDataInici(), "Data inici incorrecte"),
                () -> assertEquals(proces.getDataFi(), trobat.getDataFi(), "Data fi incorrecte"),
                () -> assertEquals(proces.isSuspes(), trobat.isSuspes(), "Suspes incorrecte")
        );
    }

    public static void comprovaProces(ProcesDto proces, ProcesDto trobat) {
        assertAll("Comprovar dades del proces DTO",
                () -> assertEquals(proces.getId(), trobat.getId(), "Proces id incorrecte"),
                () -> assertEquals(proces.getExpedientId(), trobat.getExpedientId(), "Expedient incorrecte"),
                () -> assertEquals(proces.getProcesArrelId(), trobat.getProcesArrelId(), "Proces arrel incorrecte"),
                () -> assertEquals(proces.getProcesPareId(), trobat.getProcesPareId(), "Proces pare incorrecte"),
                () -> assertEquals(proces.getProcessDefinitionId(), trobat.getProcessDefinitionId(), "ProvessDefinitionId incorrecte"),
                () -> assertEquals(proces.getDescripcio(), trobat.getDescripcio(), "Descripcio incorrecte"),
                () -> assertEquals(proces.getDataInici(), trobat.getDataInici(), "Data inici incorrecte"),
                () -> assertEquals(proces.getDataFi(), trobat.getDataFi(), "Data fi incorrecte"),
                () -> assertEquals(proces.isSuspes(), trobat.isSuspes(), "Suspes incorrecte")
        );
    }    
}
