package es.caib.helium.expedient;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.ArrayList;
import java.util.Date;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import es.caib.helium.expedient.domain.Expedient;
import es.caib.helium.expedient.domain.Responsable;
import es.caib.helium.expedient.domain.Tasca;
import es.caib.helium.expedient.model.ResponsableDto;
import es.caib.helium.expedient.model.TascaDto;

public class TascaTestHelper {
	
	@Test
    @DisplayName("Prova de generar tasques iguals")
	public void generateTascaTest() {
		Tasca tasca1 = TascaTestHelper.generateTasca(0, 1L, null, "nom", "titol");
		Tasca tasca2 = TascaTestHelper.generateTasca(0, 1L, null, "nom", "titol");
		TascaTestHelper.comprovaTasca(tasca1, tasca2);
		assertNotEquals(tasca1, tasca2);
		assertNotEquals(tasca1.hashCode(), tasca2.hashCode());
	}

	@Test
    @DisplayName("Prova de generar tasques DtO iguals")
	public void generateTascaDtoTest() {
		TascaDto tasca1 = TascaTestHelper.generateTascaDto(0, 1L, null, "nom", "titol");
		TascaDto tasca2 = TascaTestHelper.generateTascaDto(0, 1L, null, "nom", "titol");
		TascaTestHelper.comprovaTasca(tasca1, tasca2);
		assertEquals(tasca1, tasca2);
		assertEquals(tasca1.hashCode(), tasca2.hashCode());
	}

    public static Tasca generateTasca(
            int index,
            Long tascaId,
            Expedient expedient,
            String nom,
            String titol
    ) {
        Tasca tasca = Tasca.builder()
        		.id(tascaId)
        		.expedient(expedient)
        		.nom(nom)
        		.titol(titol)
        		.dataCreacio(new Date())
        		.responsables(new ArrayList<Responsable>())
        		
        		.afagada(false)
        		.assignada(false)
        		.cancelada(false)
        		.completada(false)
        		.dataFi(null)
        		.dataFins(null)
        		.errorFinalitzacio(false)
        		.iniciFinalitzacio(null)
        		.marcadaFinalitzar(true)
        		.suspesa(false)
        		.tascaTramitacioMassiva(false)
        		.usuariAssignat(null)
        		
                .build();
    	Responsable responsable;
    	if (index > 0) {
	    	for (long i = 0; i < index; i ++) {
	    		responsable = Responsable.builder()
	    				.usuariCodi("usuari" + i)
	    				.tasca(tasca)
	    				.build();
	    		tasca.getResponsables().add(responsable);
	    	}
    	}
    	return tasca;

    }

    public static TascaDto generateTascaDto(
            int index,
            Long tascaId,
            Long expedientId,
            String nom,
            String titol
    ) {
    	TascaDto tasca = TascaDto.builder()
        		.id(tascaId)
        		.expedientId(expedientId)
        		.nom(nom)
        		.titol(titol)
        		.dataCreacio(new Date())
        		.responsables(new ArrayList<ResponsableDto>())
        		
        		.afagada(false)
        		.assignada(false)
        		.cancelada(false)
        		.completada(false)
        		.dataFi(null)
        		.dataFins(null)
        		.errorFinalitzacio(false)
        		.iniciFinalitzacio(null)
        		.marcadaFinalitzar(true)
        		.suspesa(false)
        		.tascaTramitacioMassiva(false)
        		.usuariAssignat(null)

                .build();
    	ResponsableDto responsable;
    	if (index > 0) {
	    	for (long i = 0; i < index; i ++) {
	    		responsable = new ResponsableDto();
	    		responsable.setId(i);
	    		responsable.setUsuariCodi("usuari" + i);
	    		tasca.getResponsables().add(responsable);
	    	}
    	}
    	return tasca;
    }

    public static void comprovaTasca(Tasca tasca, Tasca trobat) {
        assertAll("Comprovar dades del tasca",
                () -> assertEquals(tasca.getId(), trobat.getId(), "Tasca id incorrecte"),
                () -> assertEquals(tasca.getExpedient(), trobat.getExpedient(), "Expedient incorrecte"),
                () -> assertEquals(tasca.getNom(), trobat.getNom(), "Nom incorrecte"),
                () -> assertEquals(tasca.getTitol(), trobat.getTitol(), "Titol incorrecte"),
                () -> assertEquals(tasca.isAfagada(), trobat.isAfagada(), "Agafada incorrecte"),
                () -> assertEquals(tasca.isAssignada(), trobat.isAssignada(), "Assignada incorrecte"),
                () -> assertEquals(tasca.isCancelada(), trobat.isCancelada(), "Cancel·lada incorrecte"),
                () -> assertEquals(tasca.isSuspesa(), trobat.isSuspesa(), "Suspesa incorrecte"),
                () -> assertEquals(tasca.isCompletada(), trobat.isCompletada(), "Completada incorrecte"),
                () -> assertEquals(tasca.isMarcadaFinalitzar(), trobat.isMarcadaFinalitzar(), "Marcada finalitzar incorrecte"),
                () -> assertEquals(tasca.isErrorFinalitzacio(), trobat.isErrorFinalitzacio(), "Error finalització incorrecte"),
                () -> assertEquals(tasca.isTascaTramitacioMassiva(), trobat.isTascaTramitacioMassiva(), "Tramitacio massiva incorrecte"),
                () -> assertEquals(tasca.getDataFins(), trobat.getDataFins(), "Data fins incorrecte"),
				() -> assertEquals(tasca.getIniciFinalitzacio(), trobat.getIniciFinalitzacio(), "Data d'inici finalització incorrecte"),
				() -> assertEquals(tasca.getDataCreacio(), trobat.getDataCreacio(), "Data fins incorrecte"),
				() -> assertEquals(tasca.getUsuariAssignat(), trobat.getUsuariAssignat(), "Usuari assignat incorrecte"),
				() -> assertEquals(tasca.getGrupAssignat(), trobat.getGrupAssignat(), "Grup assignat incorrecte"),
				() -> {
					if (tasca.getResponsables() != null || trobat.getResponsables() != null) {
						assertTrue("Un dels llistats de responsables és null i l'altre no", tasca.getResponsables() != null && trobat.getResponsables() != null);
						assertEquals(tasca.getResponsables().size(), trobat.getResponsables().size(), "El número de responsables és diferent");
						for(Responsable responsableTasca : tasca.getResponsables()) {
							assertTrue("El resopnsable " + responsableTasca.getUsuariCodi() + " no es troba a les dues llistes", trobat.getResponsables().contains(responsableTasca));
						}
					} 
				}
        );
    }

    public static void comprovaTasca(TascaDto tasca, TascaDto trobat) {
        assertAll("Comprovar dades del tasca DTO",
                () -> assertEquals(tasca.getId(), trobat.getId(), "Tasca id incorrecte"),
                () -> assertEquals(tasca.getExpedientId(), trobat.getExpedientId(), "Expedient id incorrecte"),
                () -> assertEquals(tasca.getNom(), trobat.getNom(), "Nom incorrecte"),
                () -> assertEquals(tasca.getTitol(), trobat.getTitol(), "Titol incorrecte"),
                () -> assertEquals(tasca.isAfagada(), trobat.isAfagada(), "Agafada incorrecte"),
                () -> assertEquals(tasca.isAssignada(), trobat.isAssignada(), "Assignada incorrecte"),
                () -> assertEquals(tasca.isCancelada(), trobat.isCancelada(), "Cancel·lada incorrecte"),
                () -> assertEquals(tasca.isSuspesa(), trobat.isSuspesa(), "Suspesa incorrecte"),
                () -> assertEquals(tasca.isCompletada(), trobat.isCompletada(), "Completada incorrecte"),
                () -> assertEquals(tasca.isMarcadaFinalitzar(), trobat.isMarcadaFinalitzar(), "Marcada finalitzar incorrecte"),
                () -> assertEquals(tasca.isErrorFinalitzacio(), trobat.isErrorFinalitzacio(), "Error finalització incorrecte"),
                () -> assertEquals(tasca.isTascaTramitacioMassiva(), trobat.isTascaTramitacioMassiva(), "Tramitacio massiva incorrecte"),
                () -> assertEquals(tasca.getDataFins(), trobat.getDataFins(), "Data fins incorrecte"),
				() -> assertEquals(tasca.getIniciFinalitzacio(), trobat.getIniciFinalitzacio(), "Data d'inici finalització incorrecte"),
				() -> assertEquals(tasca.getDataCreacio(), trobat.getDataCreacio(), "Data fins incorrecte"),
				() -> assertEquals(tasca.getUsuariAssignat(), trobat.getUsuariAssignat(), "Usuari assignat incorrecte"),
				() -> assertEquals(tasca.getGrupAssignat(), trobat.getGrupAssignat(), "Grup assignat incorrecte"),
				() -> {
					if (tasca.getResponsables() != null || trobat.getResponsables() != null) {
						assertEquals(
								tasca.getResponsables() != null ? tasca.getResponsables().size() : 0, 
								trobat.getResponsables() != null ? trobat.getResponsables().size() : 0, 
								"El número de responsables és diferent");
						if (tasca.getResponsables() != null && trobat.getResponsables() != null) {
							for(ResponsableDto responsableTasca : tasca.getResponsables()) {
								assertTrue("El responsable " + responsableTasca.getUsuariCodi() + " no es troba a les dues llistes", trobat.getResponsables().contains(responsableTasca));
							}							
						}
					} 
				}
        );
    }    
}
