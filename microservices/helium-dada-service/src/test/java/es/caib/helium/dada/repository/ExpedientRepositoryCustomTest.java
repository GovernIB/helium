package es.caib.helium.dada.repository;

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
import es.caib.helium.dada.model.Valor;
import es.caib.helium.dada.model.ValorRegistre;
import es.caib.helium.dada.model.ValorSimple;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataMongoTest
public class ExpedientRepositoryCustomTest {

	@Autowired
	private ExpedientRepository expedientRepository;
	@Autowired
	private DadaRepository dadaRepository;

	private Consulta consultaMock;
	private List<Long> expedientIds;
	private List<Expedient> expedients;
	private List<Long> expedientsFiltratsIds;

	private final String titol = "Titol";
	private final String codiConsulta = "codi1";
	private final Long unLong = 1l;
	private final Long zeroLong = 0l;
	private final int page = 0;
	private final int size = 10;

	@BeforeEach
	public void beforeEach() {

		expedientIds = new ArrayList<>();
		expedients = new ArrayList<>();
		expedientsFiltratsIds = new ArrayList<>();
		for (var foo = 1; foo < 1000; foo++) {
			var expedient = new Expedient();
			expedient.setExpedientId(Long.parseLong(foo + ""));
			expedient.setTipusId(unLong);
			expedient.setEntornId(unLong);
			expedient.setDataInici(new Date());
			expedient.setProcesPrincipalId(unLong + "");
			expedient.setTitol(titol);
			if (foo % 2 == 0) {
				expedientsFiltratsIds.add(Long.valueOf(foo));
			}
			if (foo == 1) {
				crearDades();
			}
			expedientRepository.save(expedient);
			expedientIds.add(Long.parseLong(foo + ""));
			expedients.add(expedient);
		}
		crearConsulta();
	}

	private void crearDades() {
		var codi = "codi";
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
			}
			dadaRepository.save(dada);
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
		filtreValor.setCodi(codiConsulta);
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

	@AfterEach
	public void afterEach() {

		expedientRepository.deleteAll();
	}

	@Test
	@DisplayName("test_findByFiltres")
	public void test_findByFiltres() throws Exception {
		
		var expedients = expedientRepository.findByFiltres(consultaMock);
		assertThat(expedients).isNotNull().hasSize(1);
		assertThat(expedients.get(0).getDades().stream().filter(dada -> dada.getCodi().equals(codiConsulta))).hasSize(1);
	}

	@Test
	@DisplayName("test_findByFiltres - No results")
	public void test_findByFiltres_noResults() throws Exception {

		consultaMock.setEntornId(11111);
		var expedients = expedientRepository.findByFiltres(consultaMock);
		assertThat(expedients).isNotNull().isEmpty();
	}

	@Test
	@DisplayName("test_esborrarExpedientCascade")
	public void test_esborrarExpedientCascade() throws Exception {

		assertThat(expedientRepository.esborrarExpedientCascade(unLong)).isEqualTo(1l);
		assertThat(expedientRepository.findByExpedientId(unLong)).isNotNull().isEmpty();
	}

	@Test
	@DisplayName("test_esborrarExpedientCascade - Not found")
	public void test_esborrarExpedientCascade_notFound() throws Exception {
		assertThat(expedientRepository.esborrarExpedientCascade(zeroLong)).isEqualTo(zeroLong);
	}

	@Test
	@DisplayName("test_esborrarExpedientsCascade")
	public void test_esborrarExpedientsCascade() throws Exception {

		assertThat(expedientRepository.esborrarExpedientsCascade(expedientsFiltratsIds))
				.isEqualTo(expedientsFiltratsIds.size());
	}

}
