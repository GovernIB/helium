package es.caib.helium.logic.bpmn;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.exception.HeliumHandlerException;
import es.caib.helium.bpmn.model.FilaResultat;
import es.caib.helium.bpmn.model.ParellaCodiValor;
import es.caib.helium.logic.helper.AlertaHelper;
import es.caib.helium.logic.helper.DocumentHelperV3;
import es.caib.helium.logic.helper.DominiHelper;
import es.caib.helium.logic.helper.ExpedientDadaHelper;
import es.caib.helium.logic.helper.ExpedientDocumentHelper;
import es.caib.helium.logic.helper.ExpedientHelper;
import es.caib.helium.logic.helper.ExpedientInteressatHelper;
import es.caib.helium.logic.helper.MailHelper;
import es.caib.helium.logic.helper.PluginHelper;
import es.caib.helium.persistence.entity.Domini;
import es.caib.helium.persistence.entity.Entorn;
import es.caib.helium.persistence.entity.Enumeracio;
import es.caib.helium.persistence.entity.EnumeracioValors;
import es.caib.helium.persistence.entity.Expedient;
import es.caib.helium.persistence.entity.ExpedientTipus;
import es.caib.helium.persistence.repository.DominiRepository;
import es.caib.helium.persistence.repository.EnumeracioRepository;
import es.caib.helium.persistence.repository.EnumeracioValorsRepository;

/**
 * Enumeracions i dominis de la {@link HeliumApiImpl} (recuperats de la HeliumApi de Helium 3): cerca primer al tipus
 * d'expedient i després a l'entorn, valors en ordre, modificació de valors i errors quan no existeixen.
 */
public class HeliumApiImplEnumeracionsDominisTest {

	private Entorn entorn;
	private ExpedientTipus expedientTipus;
	private EnumeracioRepository enumeracioRepository;
	private EnumeracioValorsRepository enumeracioValorsRepository;
	private DominiRepository dominiRepository;
	private DominiHelper dominiHelper;
	private HeliumApi api;

	@BeforeEach
	void setUp() {
		entorn = new Entorn();
		entorn.setId(1L);
		expedientTipus = new ExpedientTipus();
		expedientTipus.setId(3L);
		Expedient expedient = new Expedient();
		expedient.setId(7L);
		expedient.setEntorn(entorn);
		expedient.setTipus(expedientTipus);
		expedient.setProcessInstanceId("pi-1");

		enumeracioRepository = mock(EnumeracioRepository.class);
		enumeracioValorsRepository = mock(EnumeracioValorsRepository.class);
		dominiRepository = mock(DominiRepository.class);
		dominiHelper = mock(DominiHelper.class);
		HeliumApiServeis serveis = mock(HeliumApiServeis.class);
		when(serveis.getEnumeracioRepository()).thenReturn(enumeracioRepository);
		when(serveis.getEnumeracioValorsRepository()).thenReturn(enumeracioValorsRepository);
		when(serveis.getDominiRepository()).thenReturn(dominiRepository);
		when(serveis.getDominiHelper()).thenReturn(dominiHelper);

		api = new HeliumApiImpl(
			expedient,
			"pi-1",
			null,
			mock(ExpedientHelper.class),
			mock(ExpedientDadaHelper.class),
			mock(ExpedientDocumentHelper.class),
			mock(DocumentHelperV3.class),
			mock(PluginHelper.class),
			mock(AlertaHelper.class),
			mock(ExpedientInteressatHelper.class),
			mock(MailHelper.class),
			serveis);
	}

	@Test
	void enumeracioDelTipusDExpedientAmbElsValorsEnOrdre() {
		Enumeracio enumeracio = new Enumeracio();
		when(enumeracioRepository.findByEntornAndExpedientTipusAndCodi(entorn, expedientTipus, "test_params")).thenReturn(enumeracio);
		when(enumeracioValorsRepository.findByEnumeracioOrderByOrdreAsc(enumeracio)).thenReturn(List.of(
			valor("psigna_responsable", "hel_admin"),
			valor("psigna_importancia", "normal")));

		List<ParellaCodiValor> valors = api.consultaEnumeracio("test_params");

		assertEquals(2, valors.size());
		assertEquals("psigna_responsable", valors.get(0).getCodi());
		assertEquals("hel_admin", valors.get(0).getValor());
		assertEquals("psigna_importancia", valors.get(1).getCodi());
		verify(enumeracioRepository, never()).findByEntornAndCodi(any(), any());
	}

	@Test
	void siElTipusNoTeLEnumeracioEsCercaALEntorn() {
		Enumeracio global = new Enumeracio();
		when(enumeracioRepository.findByEntornAndCodi(entorn, "comptador")).thenReturn(global);
		EnumeracioValors c1 = valor("c1", "4");
		when(enumeracioValorsRepository.findByEnumeracioAndCodi(global, "c1")).thenReturn(c1);

		assertEquals("4", api.enumeracioGetValor("comptador", "c1"));
		assertNull(api.enumeracioGetValor("comptador", "c9"));

		api.enumeracioSetValor("comptador", "c1", "5");
		assertEquals("5", c1.getNom());
		verify(enumeracioValorsRepository).save(c1);
	}

	@Test
	void errorsSiNoExisteixLEnumeracioOElValor() {
		HeliumHandlerException ex = assertThrows(HeliumHandlerException.class, () -> api.consultaEnumeracio("inexistent"));
		assertEquals("No existeix cap enumeració amb el codi 'inexistent'", ex.getMessage());

		when(enumeracioRepository.findByEntornAndCodi(entorn, "comptador")).thenReturn(new Enumeracio());
		assertThrows(HeliumHandlerException.class, () -> api.enumeracioSetValor("comptador", "c9", "1"));
		verify(enumeracioValorsRepository, never()).save(any());
	}

	@Test
	void consultaDominiDelTipusOGlobalIConverteixLesFiles() {
		Domini domini = new Domini();
		when(dominiRepository.findByEntornAndCodi(entorn, "intern")).thenReturn(domini);
		Map<String, Object> parametres = Map.of("area", "pruebas");
		when(dominiHelper.consultar(domini, "PERSONES_AMB_AREA", parametres)).thenReturn(List.of(fila("codi", "u1", "nom", "Usuari 1")));

		List<FilaResultat> files = api.consultaDomini("intern", "PERSONES_AMB_AREA", parametres);

		assertEquals(1, files.size());
		assertEquals("u1", files.get(0).getValor("codi"));
		assertEquals("Usuari 1", files.get(0).getValor("nom"));
		verify(dominiRepository).findByExpedientTipusAndCodiAmbHerencia(3L, "intern");

		assertThrows(HeliumHandlerException.class, () -> api.consultaDomini("inexistent", "x", null));
	}

	@Test
	void consultaDominiIntern() {
		when(dominiHelper.consultarIntern(eq(entorn), eq(expedientTipus), eq("PERSONA_AMB_CODI"), any()))
			.thenReturn(List.of(fila("codi", "hel_admin")));

		List<FilaResultat> files = api.consultaDominiIntern("PERSONA_AMB_CODI", Map.of("persona", "hel_admin"));

		assertEquals("hel_admin", files.get(0).getValor("codi"));
	}

	private static EnumeracioValors valor(String codi, String nom) {
		EnumeracioValors valor = new EnumeracioValors();
		valor.setCodi(codi);
		valor.setNom(nom);
		return valor;
	}

	private static es.caib.helium.commons.domini.FilaResultat fila(Object... codiValor) {
		es.caib.helium.commons.domini.FilaResultat fila = new es.caib.helium.commons.domini.FilaResultat();
		for (int i = 0; i < codiValor.length; i += 2) {
			fila.addColumna(new es.caib.helium.commons.domini.ParellaCodiValor((String) codiValor[i], codiValor[i + 1]));
		}
		return fila;
	}

}
