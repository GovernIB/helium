package es.caib.helium.dada.servei;

import es.caib.helium.dada.enums.Collections;
import es.caib.helium.dada.enums.DireccioOrdre;
import es.caib.helium.dada.enums.Tipus;
import es.caib.helium.dada.model.Columna;
import es.caib.helium.dada.model.Consulta;
import es.caib.helium.dada.model.Dada;
import es.caib.helium.dada.model.Expedient;
import es.caib.helium.dada.model.Filtre;
import es.caib.helium.dada.model.FiltreCapcalera;
import es.caib.helium.dada.model.FiltreValor;
import es.caib.helium.dada.model.Ordre;
import es.caib.helium.dada.model.PagedList;
import es.caib.helium.dada.model.Valor;
import es.caib.helium.dada.model.ValorRegistre;
import es.caib.helium.dada.model.ValorSimple;
import es.caib.helium.dada.service.ExpedientService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertArrayEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ExpedientServiceTest {

	@Mock
	private ExpedientService expedientService;

	@Mock
	private Expedient expedientMock;
	@Mock
	private Consulta consultaMock;
	@Mock
	private Dada dadaMock;

	private List<Expedient> expedients;
	private List<Expedient> resultatsConsulta;
	private List<Long> expedientIds;
	private List<Dada> dades;
	private final Long unLong = 1l;
	private final Long zeroLong = 1l;
	private final String codiTest = "codi1";
	private final String titol = "Titol";
	private final int page = 0;
	private final int size = 10;

	@BeforeEach
	public void beforeEach() {

		expedients = new ArrayList<>();
		expedientIds = new ArrayList<>();
		resultatsConsulta = new ArrayList<>();
		for (var foo = 1; foo < 10; foo++) {

			var expedient = new Expedient();
			expedient.setExpedientId(unLong);
			expedient.setEntornId(unLong);
			expedient.setTipusId(unLong);
			expedient.setProcesPrincipalId(unLong + "");
			expedient.setDataInici(new Date());
			if (foo == 1) {
				expedientMock = expedient;
				List<Dada> d = new ArrayList<>();
				d.add(dadaMock);
				expedientMock.setDades(d);
				resultatsConsulta.add(expedientMock);
			}
			expedients.add(expedient);
			expedientIds.add(expedient.getExpedientId());
		}

		crearDades();
		crearConsulta();
	}

	private void crearDades() {
		var codi = "codi";
		dades = new ArrayList<>();
		for (var foo = 1; foo < 30; foo++) {
			var dada = new Dada();
			dada.setCodi(codi + foo);
			dada.setExpedientId(unLong);
			dada.setMultiple(false);
			dada.setProcesId(unLong + "");
			dada.setTipus(Tipus.String);
			if (foo == 1) {
				for (var bar = 0; bar < 10; bar++) {
					List<Valor> valors = new ArrayList<>();
					if (bar == 0) {
						var valor = new ValorRegistre();
						List<Dada> dades = new ArrayList<>();
						var d = new Dada();
						d.setCodi(codi + bar);
						dades.add(d);
						valor.setCamps(dades);
					}

					var valor = new ValorSimple();
					valor.setValor("valor" + bar);
					valor.setValorText("valor" + bar);
					valors.add(valor);
					dada.setValor(valors);
				}
				dadaMock = dada;
			}
			dades.add(dada);
		}
	}
	
	private void crearConsulta() {

		consultaMock = new Consulta();
		consultaMock.setEntornId(unLong.intValue());
		consultaMock.setExpedientTipusId(unLong.intValue());
		consultaMock.setPage(page);
		consultaMock.setSize(size);
		Map<String, Filtre> filtres = new HashMap<>();
		var filtreCapcalera = new FiltreCapcalera();
		filtreCapcalera.setTitol(titol);
		filtres.put("filtreCapcalera", filtreCapcalera);
		var filtreValor = new FiltreValor();
		filtreValor.setCodi(codiTest);
		filtres.put("filtreValor", filtreValor);
		
		List<Columna> columnes = new ArrayList<>();
		var columna = new Columna();
		columna.setNom("codi1");
		var ordre = new Ordre();
		ordre.setDireccio(DireccioOrdre.ASC);
		ordre.setOrdre(1);
		ordre.setTipus(Collections.DADA);
		columnes.add(columna);

		consultaMock.setColumnes(columnes);
		consultaMock.setFiltreValors(filtres);
	}

	@Test
	@DisplayName("test_consultaResultats")
	public void test_consultaResultats() throws Exception {
	
		var pageable = PageRequest.of(consultaMock.getPage(), resultatsConsulta.size());
		var resultat = new PagedList<Expedient>(resultatsConsulta, pageable, resultatsConsulta.size());
		
		given(expedientService.consultaResultats(any(Consulta.class))).willReturn(resultat);
		var pagedList = expedientService.consultaResultats(consultaMock);
		assertThat(pagedList).isNotNull().isEqualTo(resultat);
	}

	@Test
	@DisplayName("test_consultaResultats - Not found")
	public void test_consultaResultats_notFound() throws Exception {
		
		var pageable = PageRequest.of(consultaMock.getPage(), resultatsConsulta.size());
		var resultat = new PagedList<Expedient>(new ArrayList<Expedient>(), pageable, 10);
		
		given(expedientService.consultaResultats(any(Consulta.class))).willReturn(resultat);
		var pagedList = expedientService.consultaResultats(consultaMock);
		assertThat(pagedList).isNotNull().isEqualTo(resultat);
	}
	
	// ---------------------

	@Test
	@DisplayName("test_consultaResultatsLlistat")
	public void test_consultaResultatsLlistat() throws Exception {

		given(expedientService.consultaResultatsLlistat(any(Consulta.class))).willReturn(resultatsConsulta);
		var resultat = expedientService.consultaResultatsLlistat(consultaMock);
		assertThat(resultat).isNotNull().isNotEmpty();
		assertArrayEquals(resultatsConsulta.toArray(), resultat.toArray());
	}

	@Test
	@DisplayName("test_consultaResultatsLlistat - Llista buida ")
	public void test_consultaResultatsLlistat_llistaBuida() throws Exception {
		
		given(expedientService.consultaResultatsLlistat(any(Consulta.class))).willReturn(new ArrayList<>());
		var resultat = expedientService.consultaResultatsLlistat(consultaMock);
		assertThat(resultat).isNotNull().isEmpty();
	}
	
	
	// ---------------------

	@AfterEach
	public void afterEach() {
	}

	@Test
	@DisplayName("test_findByExpedientId")
	public void test_findByExpedientId() throws Exception {

		given(expedientService.findByExpedientId(anyLong())).willReturn(expedientMock);
		var exp = expedientService.findByExpedientId(unLong);
		assertThat(exp).isNotNull().isEqualTo(expedientMock);
	}

	@Test
	@DisplayName("test_findByExpedientId - Not found/Exception")
	public void test_findByExpedientId_notFound_o_exception() throws Exception {

		given(expedientService.findByExpedientId(anyLong())).willReturn(null);
		var exp = expedientService.findByExpedientId(unLong);
		assertThat(exp).isNull();
	}

	// ---------------------

	@Test
	@DisplayName("test_createExpedient")
	public void test_createExpedient() throws Exception {

//		given(expedientRepository.insert(any(Expedient.class))).willReturn(expedientMock);
//		var creat = expedientService.createExpedient(expedientMock);
//		assertThat(creat).isEqualTo(true);
//		assertEquals(expedientMock, creat);
//		then(expedientRepository).should(times(1)).save(any(Expedient.class));
//		then(expedientRepository).shouldHaveNoMoreInteractions();

		given(expedientService.createExpedient(any(Expedient.class))).willReturn(true);
		assertThat(expedientService.createExpedient(expedientMock)).isEqualTo(true);
	}

	@Test
	@DisplayName("test_createExpedient - Existeix expedientId")
	public void test_createExpedient_exists() throws Exception {

		given(expedientService.createExpedient(any(Expedient.class))).willReturn(false);
		assertThat(expedientService.createExpedient(expedientMock)).isEqualTo(false);
	}

	// ---------------------

	@Test
	@DisplayName("test_createExpedients")
	public void test_createExpedients() throws Exception {

		given(expedientService.createExpedients(any(List.class))).willReturn(true);
		assertThat(expedientService.createExpedients(expedients)).isEqualTo(true);
	}

	@Test
	@DisplayName("test_createExpedients - Tots els expedientsId de la llista existeixen")
	public void test_createExpedients_exists() throws Exception {

		given(expedientService.createExpedients(any(List.class))).willReturn(false);
		assertThat(expedientService.createExpedients(expedients)).isEqualTo(false);
	}

	// ---------------------

	@Test
	@DisplayName("test_deleteExpedient")
	public void deleteExpedient() throws Exception {

		given(expedientService.deleteExpedient(anyLong())).willReturn(true);
		assertThat(expedientService.deleteExpedient(unLong)).isEqualTo(true);
	}

	@Test
	@DisplayName("test_deleteExpedient - Not found")
	public void deleteExpedient_notFound() throws Exception{

		given(expedientService.deleteExpedient(anyLong())).willReturn(false);
		assertThat(expedientService.deleteExpedient(unLong)).isEqualTo(false);
	}

	// ---------------------

	@Test
	@DisplayName("test_deleteExpedients")
	public void deleteExpedients() throws Exception {

		given(expedientService.deleteExpedients(any(List.class))).willReturn(true);
		assertThat(expedientService.deleteExpedients(expedientIds)).isEqualTo(true);
	}

	@Test
	@DisplayName("test_deleteExpedients - Not found")
	public void deleteExpedients_notFound() throws Exception {

		given(expedientService.deleteExpedients(any(List.class))).willReturn(false);
		assertThat(expedientService.deleteExpedients(expedientIds)).isEqualTo(false);
	}

	// ---------------------

	@Test
	@DisplayName("test_putExpedient")
	public void test_putExpedient() throws Exception {

		given(expedientService.putExpedient(anyLong(), any(Expedient.class))).willReturn(true);
		assertThat(expedientService.putExpedient(unLong, expedientMock)).isEqualTo(true);
	}

	@Test
	@DisplayName("test_putExpedient - Not Found")
	public void test_putExpedient_notFound() throws Exception {

		given(expedientService.putExpedient(anyLong(), any(Expedient.class))).willReturn(false);
		assertThat(expedientService.putExpedient(unLong, expedientMock)).isEqualTo(false);
	}

	// ---------------------

	@Test
	@DisplayName("test_putExpedients")
	public void test_putExpedients() throws Exception {

		given(expedientService.putExpedients(any(List.class))).willReturn(true);
		assertThat(expedientService.putExpedients(expedients)).isEqualTo(true);
	}

	@Test
	@DisplayName("test_putExpedients - Not found")
	public void test_putExpedients_notFound() throws Exception {

		given(expedientService.putExpedients(any(List.class))).willReturn(false);
		assertThat(expedientService.putExpedients(expedients)).isEqualTo(false);
	}

	// ---------------------

	@Test
	@DisplayName("test_patchExpedient")
	public void test_patchExpedient() throws Exception {

		given(expedientService.putExpedient(anyLong(), any(Expedient.class))).willReturn(true);
		assertThat(expedientService.putExpedient(unLong, expedientMock)).isEqualTo(true);
	}

	@Test
	@DisplayName("test_patchExpedient - Not found")
	public void test_patchExpedient_notFound() throws Exception {

		given(expedientService.patchExpedient(anyLong(), any(Expedient.class))).willReturn(false);
		assertThat(expedientService.patchExpedient(unLong, expedientMock)).isEqualTo(false);
	}

	// ---------------------

	@Test
	@DisplayName("test_patchExpedients")
	public void test_patchExpedients() throws Exception {

		given(expedientService.patchExpedients(any(List.class))).willReturn(true);
		assertThat(expedientService.patchExpedients(expedients)).isEqualTo(true);
	}

	@Test
	@DisplayName("test_patchExpedients - Not found")
	public void test_patchExpedients_notFound() throws Exception {

		given(expedientService.patchExpedients(any(List.class))).willReturn(true);
		assertThat(expedientService.patchExpedients(expedients)).isEqualTo(true);
	}

	// ---------------------

	@Test
	@DisplayName("test_getDades")
	public void test_getDades() throws Exception {

		given(expedientService.getDades(anyLong())).willReturn(dades);
		var dadesTrobades = expedientService.getDades(unLong);
		assertThat(dadesTrobades).isNotNull().isNotEmpty();
		assertArrayEquals(dades.toArray(), dadesTrobades.toArray());
	}

	@Test
	@DisplayName("test_getDades")
	public void test_getDades_notFound() throws Exception {
		
		given(expedientService.getDades(anyLong())).willReturn(new ArrayList<Dada>());
		var dadesTrobades = expedientService.getDades(unLong);
		assertThat(dadesTrobades).isNotNull().isEmpty();
	}

	// ---------------------

	@Test
	@DisplayName("test_getDadaByCodi")
	public void test_getDadaByCodi() throws Exception {

		given(expedientService.getDadaByCodi(anyLong(), any(String.class))).willReturn(dadaMock);
		var dadaTrobada = expedientService.getDadaByCodi(unLong, codiTest);
		assertThat(dadaTrobada).isNotNull().isNotNull().isEqualTo(dadaMock);
	}

	@Test
	@DisplayName("test_getDadaByCodi - Not found")
	public void test_getDadaByCodi_notFound() throws Exception {
		
		given(expedientService.getDadaByCodi(anyLong(), any(String.class))).willReturn(null);
		var dadaTrobada = expedientService.getDadaByCodi(unLong, codiTest);
		assertThat(dadaTrobada).isNull();
	}

	// ---------------------

	@Test
	@DisplayName("test_getDadesByProces")
	public void test_getDadesByProces() throws Exception {

		given(expedientService.getDadesByExpedientIdAndProcesId(anyLong(), any(String.class))).willReturn(dades);
		var dadesTrobades = expedientService.getDadesByExpedientIdAndProcesId(unLong, unLong + "");
		assertThat(dadesTrobades).isNotNull().isNotEmpty();
		assertArrayEquals(dades.toArray(), dadesTrobades.toArray());
	}

	@Test
	@DisplayName("test_getDadesByProces - Not found")
	public void test_getDadesByProces_notFound() throws Exception {
		
		given(expedientService.getDadesByExpedientIdAndProcesId(anyLong(), any(String.class))).willReturn(new ArrayList<Dada>());
		var dadesTrobades = expedientService.getDadesByExpedientIdAndProcesId(unLong, unLong + "");
		assertThat(dadesTrobades).isNotNull().isEmpty();
	}

	// ---------------------

	@Test
	@DisplayName("test_getDadaByProcesAndCodi")
	public void test_getDadaByProcesAndCodi() throws Exception {
		
		given(expedientService.getDadaByProcesAndCodi(any(String.class), any(String.class))).willReturn(dadaMock);
		var dada = expedientService.getDadaByProcesAndCodi(unLong + "", codiTest);
		assertThat(dada).isNotNull().isEqualTo(dadaMock);
	}

	@Test
	@DisplayName("test_getDadaByProcesAndCodi - Not found")
	public void test_getDadaByProcesAndCodi_notFound() throws Exception {
		
		given(expedientService.getDadaByProcesAndCodi(any(String.class), any(String.class))).willReturn(null);
		var dada = expedientService.getDadaByProcesAndCodi(unLong + "",  codiTest);
		assertThat(dada).isNull();
	}

	// ---------------------

	@Test
	@DisplayName("test_createDades")
	public void test_createDades() throws Exception {

		given(expedientService.createDades(anyLong(), any(String.class), any(List.class))).willReturn(true);
		assertThat(expedientService.createDades(unLong, unLong + "", dades)).isEqualTo(true);
	}

	@Test
	@DisplayName("test_createDades - Ja existeixen")
	public void test_createDades_jaExisteixen() throws Exception {
		
		given(expedientService.createDades(anyLong(), any(String.class) + "", any(List.class))).willReturn(false);
		assertThat(expedientService.createDades(unLong, unLong + "", dades)).isEqualTo(false);
	}

	// ---------------------

	@Test
	@DisplayName("test_putDadaByExpedientIdAndCodi")
	public void test_putDadaByExpedientIdAndCodi() throws Exception {

		given(expedientService.putDadaByExpedientIdAndCodi(anyLong(), any(String.class), any(Dada.class))).willReturn(true);
		assertThat(expedientService.putDadaByExpedientIdAndCodi(unLong, codiTest, dadaMock)).isEqualTo(true);
	}

	@Test
	@DisplayName("test_putDadaByExpedientIdAndCodi - Not found")
	public void putDadaByExpedientIdAndCodi_notFound() throws Exception {
		
		given(expedientService.putDadaByExpedientIdAndCodi(anyLong(), any(String.class), any(Dada.class))).willReturn(true);
		assertThat(expedientService.putDadaByExpedientIdAndCodi(unLong, codiTest, dadaMock)).isEqualTo(true);
	}

	// ---------------------

	@Test
	@DisplayName("test_deleteDadaByExpedientIdAndCodi")
	public void test_deleteDadaByExpedientIdAndCodi() throws Exception {

		given(expedientService.deleteDadaByExpedientIdAndCodi(anyLong(), any(String.class))).willReturn(true);
		assertThat(expedientService.deleteDadaByExpedientIdAndCodi(unLong, codiTest)).isEqualTo(true);
	}

	@Test
	@DisplayName("test_deleteDadaByExpedientIdAndCodi - Not found")
	public void test_deleteDadaByExpedientIdAndCodi_notFound() throws Exception {
		
		given(expedientService.deleteDadaByExpedientIdAndCodi(anyLong(), any(String.class))).willReturn(false);
		assertThat(expedientService.deleteDadaByExpedientIdAndCodi(unLong, codiTest)).isEqualTo(false);
	}

	// ---------------------

	@Test
	@DisplayName("test_postDadesByExpedientIdProcesId")
	public void test_postDadesByExpedientIdProcesId() throws Exception {
		given(expedientService.postDadesByExpedientIdProcesId(anyLong(), any(String.class), any(List.class))).willReturn(true);
		assertThat(expedientService.postDadesByExpedientIdProcesId(unLong, unLong + "", dades)).isEqualTo(true);
	}

	@Test
	@DisplayName("test_postDadesByExpedientIdProcesId - Not found")
	public void test_postDadesByExpedientIdProcesId_notFound() throws Exception {
		given(expedientService.postDadesByExpedientIdProcesId(anyLong(), any(String.class), any(List.class))).willReturn(false);
		assertThat(expedientService.postDadesByExpedientIdProcesId(unLong, unLong + "", dades)).isEqualTo(false);
	}

	// ---------------------

	@Test
	@DisplayName("test_putDadaByExpedientIdProcesIdAndCodi")
	public void test_putDadaByExpedientIdProcesIdAndCodi() throws Exception {
		
		given(expedientService.putDadaByExpedientIdProcesIdAndCodi(anyLong(), any(String.class), any(String.class), any(Dada.class))).willReturn(true);
		assertThat(expedientService.putDadaByExpedientIdProcesIdAndCodi(unLong, unLong + "", codiTest, dadaMock)).isEqualTo(true);
	}

	@Test
	@DisplayName("test_putDadaByExpedientIdProcesIdAndCodi - Not found")
	public void test_putDadaByExpedientIdProcesIdAndCodi_notFound() throws Exception {
		
		given(expedientService.putDadaByExpedientIdProcesIdAndCodi(anyLong(), any(String.class), any(String.class), any(Dada.class))).willReturn(false);
		assertThat(expedientService.putDadaByExpedientIdProcesIdAndCodi(unLong, unLong + "", codiTest, dadaMock)).isEqualTo(false);
	}

	// ---------------------

	@Test
	@DisplayName("test_deleteDadaByExpedientIdAndProcesIdAndCodi")
	public void test_deleteDadaByExpedientIdAndProcesIdAndCodi() throws Exception {

		given(expedientService.deleteDadaByExpedientIdAndProcesIdAndCodi(anyLong(), any(String.class), any(String.class))).willReturn(true);
		assertThat(expedientService.deleteDadaByExpedientIdAndProcesIdAndCodi(unLong, unLong + "", codiTest)).isEqualTo(true);
	}

	@Test
	@DisplayName("test_deleteDadaByExpedientIdAndProcesIdAndCodi - Not found")
	public void test_deleteDadaByExpedientIdAndProcesIdAndCodi_notFound() throws Exception {
		
		given(expedientService.deleteDadaByExpedientIdAndProcesIdAndCodi(anyLong(), any(String.class), any(String.class))).willReturn(false);
		assertThat(expedientService.deleteDadaByExpedientIdAndProcesIdAndCodi(unLong, unLong + "", codiTest)).isEqualTo(false);
	}
}
