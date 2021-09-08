package es.caib.helium.expedient;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.ArrayList;
import java.util.Date;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import es.caib.helium.expedient.domain.Grup;
import es.caib.helium.expedient.domain.Proces;
import es.caib.helium.expedient.domain.Responsable;
import es.caib.helium.expedient.domain.Tasca;
import es.caib.helium.expedient.model.TascaDto;

public class TascaTestHelper {
	
	@Test
    @DisplayName("Prova de generar tasques iguals")
	public void generateTascaTest() {
		Tasca tasca1 = TascaTestHelper.generateTasca(0, "1", null, "nom", "titol");
		Tasca tasca2 = TascaTestHelper.generateTasca(0, "1", null, "nom", "titol");
		TascaTestHelper.comprovaTasca(tasca1, tasca2);
		assertNotEquals(tasca1, tasca2);
		assertNotEquals(tasca1.hashCode(), tasca2.hashCode());
	}

	@Test
    @DisplayName("Prova de generar tasques DtO iguals")
	public void generateTascaDtoTest() {
		TascaDto tasca1 = TascaTestHelper.generateTascaDto(0, "1", "p1", "nom", "titol");
		TascaDto tasca2 = TascaTestHelper.generateTascaDto(0, "1", "p1", "nom", "titol");
		TascaTestHelper.comprovaTasca(tasca1, tasca2);
		assertEquals(tasca1, tasca2);
		assertEquals(tasca1.hashCode(), tasca2.hashCode());
	}

    public static Tasca generateTasca(
            int index,
            String tascaId,
            Proces proces,
            String nom,
            String titol
    ) {
        Tasca tasca = Tasca.builder()
        		.tascaId(tascaId)
        		.proces(proces)
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
        		.errorFinalitzacio(null)
        		.iniciFinalitzacio(null)
        		.marcadaFinalitzar(null)
        		.suspesa(false)
        		.usuariAssignat(null)
        		.prioritat(3)
        		
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
            String tascaId,
            String procesId,
            String nom,
            String titol
    ) {
    	TascaDto tasca = TascaDto.builder()
        		.tascaId(tascaId)
        		.procesId(procesId)
        		.nom(nom)
        		.titol(titol)
        		.dataCreacio(new Date())
        		.responsables(new ArrayList<String>())
        		
        		.afagada(false)
        		.assignada(false)
        		.cancelada(false)
        		.completada(false)
        		.dataFi(null)
        		.dataFins(null)
        		.errorFinalitzacio(null)
        		.iniciFinalitzacio(null)
        		.marcadaFinalitzar(null)
        		.suspesa(false)
        		.usuariAssignat(null)
        		.prioritat(3)

                .build();
    	if (index > 0) {
	    	for (long i = 0; i < index; i ++) {
	    		tasca.getResponsables().add("usuari" + i);
	    	}
    	}
    	return tasca;
    }

    public static void comprovaTasca(Tasca tasca, Tasca trobat) {
        assertAll("Comprovar dades del tasca",
                () -> assertEquals(tasca.getId(), trobat.getId(), "Tasca id incorrecte"),
                () -> assertEquals(tasca.getProces(), trobat.getProces(), "Proces incorrecte"),
                () -> assertEquals(tasca.getNom(), trobat.getNom(), "Nom incorrecte"),
                () -> assertEquals(tasca.getTitol(), trobat.getTitol(), "Titol incorrecte"),
                () -> assertEquals(tasca.isAfagada(), trobat.isAfagada(), "Agafada incorrecte"),
                () -> assertEquals(tasca.isAssignada(), trobat.isAssignada(), "Assignada incorrecte"),
                () -> assertEquals(tasca.isCancelada(), trobat.isCancelada(), "Cancel·lada incorrecte"),
                () -> assertEquals(tasca.isSuspesa(), trobat.isSuspesa(), "Suspesa incorrecte"),
                () -> assertEquals(tasca.isCompletada(), trobat.isCompletada(), "Completada incorrecte"),
                () -> assertEquals(tasca.getMarcadaFinalitzar(), trobat.getMarcadaFinalitzar(), "Marcada finalitzar incorrecte"),
                () -> assertEquals(tasca.getErrorFinalitzacio(), trobat.getErrorFinalitzacio(), "Error finalització incorrecte"),
                () -> assertEquals(tasca.getDataFins(), trobat.getDataFins(), "Data fins incorrecte"),
				() -> assertEquals(tasca.getIniciFinalitzacio(), trobat.getIniciFinalitzacio(), "Data d'inici finalització incorrecte"),
				() -> assertEquals(tasca.getDataCreacio(), trobat.getDataCreacio(), "Data fins incorrecte"),
				() -> assertEquals(tasca.getUsuariAssignat(), trobat.getUsuariAssignat(), "Usuari assignat incorrecte"),
				() -> assertEquals(tasca.getPrioritat(), trobat.getPrioritat(), "Prioritat incorrecte"),
				() -> {
					if (tasca.getResponsables() != null || trobat.getResponsables() != null) {
						assertTrue("Un dels llistats de responsables és null i l'altre no", tasca.getResponsables() != null && trobat.getResponsables() != null);
						assertEquals(tasca.getResponsables().size(), trobat.getResponsables().size(), "El número de responsables és diferent");
						for(Responsable responsableTasca : tasca.getResponsables()) {
							assertTrue("El resopnsable " + responsableTasca.getUsuariCodi() + " no es troba a les dues llistes", trobat.getResponsables().contains(responsableTasca));
						}
					} 
				},
				() -> {
					if (tasca.getGrups() != null || trobat.getGrups() != null) {
						assertTrue("Un dels llistats de grups és null i l'altre no", tasca.getGrups() != null && trobat.getGrups() != null);
						assertEquals(tasca.getGrups().size(), trobat.getGrups().size(), "El número de grups és diferent");
						for(Grup grupTasca : tasca.getGrups()) {
							assertTrue("El grup " + grupTasca.getGrupCodi() + " no es troba a les dues llistes", trobat.getGrups().contains(grupTasca));
						}
					} 
				}
        );
    }

    public static void comprovaTasca(TascaDto tasca, TascaDto trobat) {
        assertAll("Comprovar dades del tasca DTO",
                () -> assertEquals(tasca.getId(), trobat.getId(), "Tasca id incorrecte"),
                () -> assertEquals(tasca.getProcesId(), trobat.getProcesId(), "Proces incorrecte"),
                () -> assertEquals(tasca.getNom(), trobat.getNom(), "Nom incorrecte"),
                () -> assertEquals(tasca.getTitol(), trobat.getTitol(), "Titol incorrecte"),
                () -> assertEquals(tasca.isAfagada(), trobat.isAfagada(), "Agafada incorrecte"),
                () -> assertEquals(tasca.isAssignada(), trobat.isAssignada(), "Assignada incorrecte"),
                () -> assertEquals(tasca.isCancelada(), trobat.isCancelada(), "Cancel·lada incorrecte"),
                () -> assertEquals(tasca.isSuspesa(), trobat.isSuspesa(), "Suspesa incorrecte"),
                () -> assertEquals(tasca.isCompletada(), trobat.isCompletada(), "Completada incorrecte"),
                () -> assertEquals(tasca.getMarcadaFinalitzar(), trobat.getMarcadaFinalitzar(), "Marcada finalitzar incorrecte"),
                () -> assertEquals(tasca.getErrorFinalitzacio(), trobat.getErrorFinalitzacio(), "Error finalització incorrecte"),
                () -> assertEquals(tasca.getDataFins(), trobat.getDataFins(), "Data fins incorrecte"),
				() -> assertEquals(tasca.getIniciFinalitzacio(), trobat.getIniciFinalitzacio(), "Data d'inici finalització incorrecte"),
				() -> assertEquals(tasca.getDataCreacio(), trobat.getDataCreacio(), "Data fins incorrecte"),
				() -> assertEquals(tasca.getUsuariAssignat(), trobat.getUsuariAssignat(), "Usuari assignat incorrecte"),
				() -> {
					if (tasca.getResponsables() != null || trobat.getResponsables() != null) {
						assertEquals(
								tasca.getResponsables() != null ? tasca.getResponsables().size() : 0, 
								trobat.getResponsables() != null ? trobat.getResponsables().size() : 0, 
								"El número de responsables és diferent");
						if (tasca.getResponsables() != null && trobat.getResponsables() != null) {
							for(String responsableTasca : tasca.getResponsables()) {
								assertTrue("El responsable " + responsableTasca + " no es troba a les dues llistes", trobat.getResponsables().contains(responsableTasca));
							}							
						}
					} 
				},
				() -> {
					if (tasca.getGrups() != null || trobat.getGrups() != null) {
						assertEquals(
								tasca.getGrups() != null ? tasca.getGrups().size() : 0, 
								trobat.getGrups() != null ? trobat.getGrups().size() : 0, 
								"El número de grups és diferent");
						if (tasca.getGrups() != null && trobat.getGrups() != null) {
							for(String grupTasca : tasca.getGrups()) {
								assertTrue("El grup " + grupTasca + " no es troba a les dues llistes", trobat.getGrups().contains(grupTasca));
							}							
						}
					} 
				}
        );
    }    
}
