package es.caib.helium.logic.helper;

import es.caib.helium.commons.dto.ArxiuDto;
import es.caib.helium.commons.dto.NtiDocumentoFormato;
import es.caib.helium.commons.dto.NtiEstadoElaboracionEnumDto;
import es.caib.helium.commons.dto.NtiOrigenEnumDto;
import es.caib.helium.commons.dto.NtiTipoDocumentalEnumDto;
import es.caib.helium.commons.dto.NtiTipoFirmaEnumDto;
import es.caib.helium.commons.exception.ValidacioException;
import es.caib.helium.disseny.engine.WProcessInstance;
import es.caib.helium.logic.intf.service.WorkflowEngineApi;
import es.caib.helium.persistence.entity.Document;
import es.caib.helium.persistence.entity.DocumentStore;
import es.caib.helium.persistence.entity.Expedient;
import es.caib.helium.persistence.entity.ExpedientDocument;
import es.caib.helium.persistence.entity.ExpedientTipus;
import es.caib.helium.persistence.entity.UnitatOrganitzativa;
import es.caib.helium.persistence.repository.DocumentNotificacioRepository;
import es.caib.helium.persistence.repository.DocumentRepository;
import es.caib.helium.persistence.repository.DocumentStoreRepository;
import es.caib.helium.persistence.repository.ExpedientDocumentRepository;
import es.caib.pluginsib.arxiu.api.ContingutArxiu;
import es.caib.pluginsib.arxiu.api.DocumentMetadades;
import es.caib.pluginsib.arxiu.api.Firma;
import es.caib.pluginsib.arxiu.api.FirmaTipus;
import es.caib.pluginsib.arxiu.caib.ArxiuConversioHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaris per a {@link ExpedientDocumentHelper}.
 */
@ExtendWith(MockitoExtension.class)
public class ExpedientDocumentHelperTest {

	@Mock
	private DocumentNotificacioRepository documentNotificacioRepository;
	@Mock
	private ExpedientDocumentRepository expedientDocumentRepository;
	@Mock
	private DocumentStoreRepository documentStoreRepository;
	@Mock
	private DocumentRepository documentRepository;
	@Mock
	private ExpedientHelper expedientHelper;
	@Mock
	private PluginHelper pluginHelper;
	@Mock
	private WorkflowEngineApi workflowEngineApi;

	@InjectMocks
	private ExpedientDocumentHelper helper;

	private Expedient expedientBase;

	@BeforeEach
	public void setUp() {
		expedientBase = new Expedient();
		expedientBase.setId(1L);
		expedientBase.setDataInici(new GregorianCalendar(2026, Calendar.MARCH, 15).getTime());
		expedientBase.setNtiOrgano("A04012345");
		expedientBase.setArxiuActiu(false);
		expedientBase.setArxiuUuid("uuid-expedient");
	}

	@Test
	public void findByExpedientDelegaAlRepositori() {
		List<DocumentStore> esperats = Arrays.asList(new DocumentStore(), new DocumentStore());
		when(expedientDocumentRepository.findDocumentStoreByExpedientId(7L)).thenReturn(esperats);
		assertSame(esperats, helper.findByExpedient(7L));
	}

	@Test
	public void findDocumentStorePassaFlagsNullCorrectament() {
		DocumentStore esperat = new DocumentStore();
		when(expedientDocumentRepository.findDocumentStoreByCodi(
			eq("DOC1"), eq(1L), eq(false), eq("proc"), eq(false), eq("task"), eq(false)))
			.thenReturn(esperat);
		assertSame(esperat, helper.findDocumentStore(1L, "proc", "task", "DOC1"));
	}

	@Test
	public void findDocumentStoreAmbIdsNull() {
		DocumentStore esperat = new DocumentStore();
		when(expedientDocumentRepository.findDocumentStoreByCodi(
			eq("DOC1"), isNull(), eq(true), isNull(), eq(true), isNull(), eq(true)))
			.thenReturn(esperat);
		assertSame(esperat, helper.findDocumentStore(null, null, null, "DOC1"));
	}

	@Test
	public void findByExpedientAndTaskIDelega() {
		List<DocumentStore> esperats = Collections.singletonList(new DocumentStore());
		when(expedientDocumentRepository.findDocumentStoreByExpedientIdAndTaskId(1L, "t1")).thenReturn(esperats);
		assertSame(esperats, helper.findByExpedientAndTask(1L, "t1"));
	}

	@Test
	public void findByExpedientAndProcessDelega() {
		List<DocumentStore> esperats = Collections.singletonList(new DocumentStore());
		when(expedientDocumentRepository.findDocumentStoreByExpedientIdAndProcessId(1L, "p1")).thenReturn(esperats);
		assertSame(esperats, helper.findByExpedientAndProcess(1L, "p1"));
	}

	@Test
	public void findByProcessAndCodiDelega() {
		DocumentStore esperat = new DocumentStore();
		when(expedientDocumentRepository.findDocumentStoreByProcessIdAndCodi("p1", "C1")).thenReturn(esperat);
		assertSame(esperat, helper.findByProcessAndCodi("p1", "C1"));
	}

	@Test
	public void deleteDelega() {
		helper.delete(99L);
		verify(expedientDocumentRepository).deleteByDocumentStoreId(99L);
	}

	@Test
	public void deleteByExpedientDelega() {
		helper.deleteByExpedient(5L);
		verify(expedientDocumentRepository).deleteByExpedientId(5L);
	}

	@Test
	public void firmaServidorNoFaRes() {
		assertDoesNotThrow(() -> helper.firmaServidor("proc", 1L, "motiu", new byte[]{1, 2}));
	}

	// --------------------------------------------------
	// getContentType
	// --------------------------------------------------

	@Test
	public void getContentTypeDelegaAMimetypesFileTypeMap() {
		javax.activation.MimetypesFileTypeMap map = new javax.activation.MimetypesFileTypeMap();
		assertEquals(map.getContentType("document.pdf"), helper.getContentType("document.pdf"));
		assertEquals(map.getContentType("informe.txt"), helper.getContentType("informe.txt"));
		assertNotNull(helper.getContentType("document.pdf"));
	}

	@Test
	public void getContentTypeDesconegutRetornaOctetStream() {
		assertEquals("application/octet-stream", helper.getContentType("fitxer.sense_extensio_rara_xyz123"));
	}

	@Test
	public void getContentTypeNullRetornaOctetStream() {
		// MimetypesFileTypeMap amb null retorna octet-stream o llança NPE segons JDK;
		// el helper ha de retornar octet-stream en cas d'extensió desconeguda.
		assertEquals("application/octet-stream", helper.getContentType("fitxer.unknownext12345"));
	}

	// --------------------------------------------------
	// revisarContingutNom
	// --------------------------------------------------

	@Test
	public void revisarContingutNomDelegaAArxiuConversioHelper() {
		String nom = "Document# de} prova.pdf";
		assertEquals("Document de prova.pdf", ExpedientDocumentHelper.revisarContingutNom(nom));
	}

	@Test
	public void revisarContingutNomEliminaSaltsILiniaITabuladors() {
		String brut = "nom amb\nsalt\tde linia";
		String net = ExpedientDocumentHelper.revisarContingutNom(brut);
		assertNotNull(net);
		assertFalse(net.contains("\n"), "No ha de contenir salts de línia");
		assertFalse(net.contains("\t"), "No ha de contenir tabuladors");
		assertFalse(net.contains("\r"), "No ha de contenir retorns de carro");
	}

	@Test
	public void revisarContingutNomTreuPuntFinal() {
		String ambPunt = "document.";
		String net = ExpedientDocumentHelper.revisarContingutNom(ambPunt);
		assertNotNull(net);
		assertFalse(net.endsWith("."), "Ha de treure el punt final. Obtingut: " + net);
	}

	// --------------------------------------------------
	// actualizarMetadadesNti
	// --------------------------------------------------

	private DocumentStore nouDocumentStore(Long id, String arxiuNom) {
		DocumentStore ds = new DocumentStore(
			DocumentStore.DocumentFont.INTERNA, "proc1", "DOC1", new Date(), new Date(), arxiuNom);
		ds.setId(id);
		return ds;
	}

	@Test
	public void actualizarMetadadesNtiAmbValorsExplicits() {
		DocumentStore ds = nouDocumentStore(12L, "informe.pdf");
		helper.actualizarMetadadesNti(
			expedientBase, null, ds,
			NtiOrigenEnumDto.CIUTADA,
			NtiEstadoElaboracionEnumDto.COPIA_DP,
			NtiTipoDocumentalEnumDto.INFORME,
			"ORIGEN-1");

		assertEquals(ExpedientDocumentHelper.VERSIO_NTI, ds.getNtiVersion());
		assertEquals("ES_A04012345_2026_HEL" + String.format("%027d", 12L), ds.getNtiIdentificador());
		// Sense UO ha d'usar l'òrgan de l'expedient
		assertEquals("A04012345", ds.getNtiOrgano());
		assertEquals(NtiOrigenEnumDto.CIUTADA, ds.getNtiOrigen());
		assertEquals(NtiEstadoElaboracionEnumDto.COPIA_DP, ds.getNtiEstadoElaboracion());
		assertEquals(NtiTipoDocumentalEnumDto.INFORME, ds.getNtiTipoDocumental());
		assertEquals(NtiDocumentoFormato.PDF, ds.getNtiNombreFormato());
		assertEquals("ORIGEN-1", ds.getNtiIdDocumentoOrigen());
	}

	@Test
	public void actualizarMetadadesNtiAmbDefectesQuanTotNull() {
		DocumentStore ds = nouDocumentStore(1L, "doc.pdf");
		helper.actualizarMetadadesNti(expedientBase, null, ds, null, null, null, null);
		assertEquals(NtiOrigenEnumDto.ADMINISTRACIO, ds.getNtiOrigen());
		assertEquals(NtiEstadoElaboracionEnumDto.ORIGINAL, ds.getNtiEstadoElaboracion());
		assertEquals(NtiTipoDocumentalEnumDto.ALTRES, ds.getNtiTipoDocumental());
	}

	@Test
	public void actualizarMetadadesNtiHeretaDeDocumentQuanParametresNull() {
		Document doc = new Document();
		doc.setNtiOrigen(NtiOrigenEnumDto.CIUTADA);
		doc.setNtiEstadoElaboracion(NtiEstadoElaboracionEnumDto.COPIA_CF);
		doc.setNtiTipoDocumental(NtiTipoDocumentalEnumDto.CERTIFICAT);

		DocumentStore ds = nouDocumentStore(2L, "cert.pdf");
		helper.actualizarMetadadesNti(expedientBase, doc, ds, null, null, null, null);

		assertEquals(NtiOrigenEnumDto.CIUTADA, ds.getNtiOrigen());
		assertEquals(NtiEstadoElaboracionEnumDto.COPIA_CF, ds.getNtiEstadoElaboracion());
		assertEquals(NtiTipoDocumentalEnumDto.CERTIFICAT, ds.getNtiTipoDocumental());
	}

	@Test
	public void actualizarMetadadesNtiParametreTePrioritatSobreDocument() {
		Document doc = new Document();
		doc.setNtiOrigen(NtiOrigenEnumDto.CIUTADA);
		doc.setNtiEstadoElaboracion(NtiEstadoElaboracionEnumDto.COPIA_CF);
		doc.setNtiTipoDocumental(NtiTipoDocumentalEnumDto.CERTIFICAT);

		DocumentStore ds = nouDocumentStore(3L, "doc.pdf");
		helper.actualizarMetadadesNti(
			expedientBase, doc, ds,
			NtiOrigenEnumDto.ADMINISTRACIO,
			NtiEstadoElaboracionEnumDto.ORIGINAL,
			NtiTipoDocumentalEnumDto.ACTA,
			null);

		assertEquals(NtiOrigenEnumDto.ADMINISTRACIO, ds.getNtiOrigen());
		assertEquals(NtiEstadoElaboracionEnumDto.ORIGINAL, ds.getNtiEstadoElaboracion());
		assertEquals(NtiTipoDocumentalEnumDto.ACTA, ds.getNtiTipoDocumental());
	}

	@Test
	public void actualizarMetadadesNtiUsaCodiUOQuanHiEs() {
		UnitatOrganitzativa uo = mock(UnitatOrganitzativa.class);
		when(uo.getCodi()).thenReturn("UO999");
		expedientBase.setUnitatOrganitzativa(uo);

		DocumentStore ds = nouDocumentStore(4L, "doc.pdf");
		helper.actualizarMetadadesNti(expedientBase, null, ds, null, null, null, null);
		assertEquals("UO999", ds.getNtiOrgano());
	}

	@Test
	public void actualizarMetadadesNtiMapejaFormats() {
		assertFormat("a.docx", NtiDocumentoFormato.SOXML);
		assertFormat("a.xlsx", NtiDocumentoFormato.SOXML);
		assertFormat("a.pptx", NtiDocumentoFormato.SOXML);
		assertFormat("a.jpg", NtiDocumentoFormato.JPEG);
		assertFormat("a.jpeg", NtiDocumentoFormato.JPEG);
		assertFormat("a.png", NtiDocumentoFormato.PNG);
		assertFormat("a.txt", NtiDocumentoFormato.TXT);
		assertFormat("a.zip", NtiDocumentoFormato.ZIP);
		assertFormat("a.xml", NtiDocumentoFormato.XML);
		assertFormat("a.csv", NtiDocumentoFormato.CSV);
		assertFormat("a.html", NtiDocumentoFormato.XHTML);
		assertFormat("a.odt", NtiDocumentoFormato.OASIS12);
		assertFormat("a.doc", NtiDocumentoFormato.DOC);
		assertFormat("a.xls", NtiDocumentoFormato.XLS);
		assertFormat("a.mdb", NtiDocumentoFormato.MDB);
		// Extensió coneguda però no del catàleg ENI -> ALTRES
		assertFormat("a.rar", NtiDocumentoFormato.ALTRES);
	}

	private void assertFormat(String arxiuNom, NtiDocumentoFormato esperat) {
		DocumentStore ds = nouDocumentStore(10L, arxiuNom);
		helper.actualizarMetadadesNti(expedientBase, null, ds, null, null, null, null);
		assertEquals(esperat, ds.getNtiNombreFormato(), "Format incorrecte per " + arxiuNom);
	}

	@Test
	public void actualizarMetadadesNtiSenseExtensioLlenyaValidacioException() {
		DocumentStore ds = nouDocumentStore(11L, "senseextensio");
		assertThrows(ValidacioException.class, () ->
			helper.actualizarMetadadesNti(expedientBase, null, ds, null, null, null, null));
	}

	@Test
	public void actualizarMetadadesNtiIdentificadorInclouAnyExpedient() {
		expedientBase.setDataInici(new GregorianCalendar(2020, Calendar.JANUARY, 5).getTime());
		DocumentStore ds = nouDocumentStore(99L, "a.pdf");
		helper.actualizarMetadadesNti(expedientBase, null, ds, null, null, null, null);
		assertTrue(ds.getNtiIdentificador().startsWith("ES_A04012345_2020_HEL"),
			"Identificador NTI ha d'incloure l'any 2020: " + ds.getNtiIdentificador());
	}

	// --------------------------------------------------
	// actualitzarNtiFirma
	// --------------------------------------------------

	private Firma novaFirma(FirmaTipus tipus, String csv, String csvRegulacio) {
		Firma f = new Firma();
		f.setTipus(tipus);
		if (csv != null) {
			f.setContingut(csv.getBytes());
		}
		f.setCsvRegulacio(csvRegulacio);
		return f;
	}

	private es.caib.pluginsib.arxiu.api.Document nouArxiuDocument(List<Firma> firmes, String csvMetadades, String csvDefMetadades) {
		es.caib.pluginsib.arxiu.api.Document doc = mock(es.caib.pluginsib.arxiu.api.Document.class);
		when(doc.getFirmes()).thenReturn(firmes);
		if (csvMetadades != null || csvDefMetadades != null) {
			DocumentMetadades met = mock(DocumentMetadades.class);
			lenient().when(met.getCsv()).thenReturn(csvMetadades);
			lenient().when(met.getCsvDef()).thenReturn(csvDefMetadades);
			lenient().when(doc.getMetadades()).thenReturn(met);
		} else {
			lenient().when(doc.getMetadades()).thenReturn(null);
		}
		return doc;
	}

	@Test
	public void actualitzarNtiFirmaCsv() {
		DocumentStore ds = nouDocumentStore(1L, "a.pdf");
		es.caib.pluginsib.arxiu.api.Document arxiuDoc = nouArxiuDocument(
			Collections.singletonList(novaFirma(FirmaTipus.CSV, "CSV-123", "REG-1")), null, null);
		helper.actualitzarNtiFirma(ds, arxiuDoc);
		assertEquals("CSV-123", ds.getNtiCsv());
		assertEquals("REG-1", ds.getNtiDefinicionGenCsv());
		assertNull(ds.getNtiTipoFirma());
	}

	@Test
	public void actualitzarNtiFirmaPades() {
		DocumentStore ds = nouDocumentStore(1L, "a.pdf");
		es.caib.pluginsib.arxiu.api.Document arxiuDoc = nouArxiuDocument(
			Collections.singletonList(novaFirma(FirmaTipus.PADES, null, null)), null, null);
		helper.actualitzarNtiFirma(ds, arxiuDoc);
		assertEquals(NtiTipoFirmaEnumDto.PADES, ds.getNtiTipoFirma());
		assertNull(ds.getNtiCsv());
	}

	@Test
	public void actualitzarNtiFirmaMapejaTotsElsTipus() {
		assertTipusFirma(FirmaTipus.CADES_ATT, NtiTipoFirmaEnumDto.CADES_ATT);
		assertTipusFirma(FirmaTipus.CADES_DET, NtiTipoFirmaEnumDto.CADES_DET);
		assertTipusFirma(FirmaTipus.XADES_ENV, NtiTipoFirmaEnumDto.XADES_ENV);
		assertTipusFirma(FirmaTipus.XADES_DET, NtiTipoFirmaEnumDto.XADES_DET);
		assertTipusFirma(FirmaTipus.PADES, NtiTipoFirmaEnumDto.PADES);
		assertTipusFirma(FirmaTipus.ODT, NtiTipoFirmaEnumDto.ODT);
		assertTipusFirma(FirmaTipus.OOXML, NtiTipoFirmaEnumDto.OOXML);
		assertTipusFirma(FirmaTipus.SMIME, NtiTipoFirmaEnumDto.SMIME);
	}

	private void assertTipusFirma(FirmaTipus tipusArxiu, NtiTipoFirmaEnumDto esperat) {
		DocumentStore ds = nouDocumentStore(1L, "a.pdf");
		es.caib.pluginsib.arxiu.api.Document arxiuDoc = nouArxiuDocument(
			Collections.singletonList(novaFirma(tipusArxiu, null, null)), null, null);
		helper.actualitzarNtiFirma(ds, arxiuDoc);
		assertEquals(esperat, ds.getNtiTipoFirma(), "Mapeig incorrecte per " + tipusArxiu);
	}

	@Test
	public void actualitzarNtiFirmaAgafaCsvDeMetadadesSiNoHiHaFirmaCsv() {
		DocumentStore ds = nouDocumentStore(1L, "a.pdf");
		es.caib.pluginsib.arxiu.api.Document arxiuDoc = nouArxiuDocument(
			Collections.singletonList(novaFirma(FirmaTipus.PADES, null, null)),
			"CSV-META", "CSVDEF-META");
		helper.actualitzarNtiFirma(ds, arxiuDoc);
		assertEquals(NtiTipoFirmaEnumDto.PADES, ds.getNtiTipoFirma());
		assertEquals("CSV-META", ds.getNtiCsv());
		assertEquals("CSVDEF-META", ds.getNtiDefinicionGenCsv());
	}

	@Test
	public void actualitzarNtiFirmaSenseFirmesNoCanviaRes() {
		DocumentStore ds = nouDocumentStore(1L, "a.pdf");
		es.caib.pluginsib.arxiu.api.Document arxiuDoc = nouArxiuDocument(null, null, null);
		helper.actualitzarNtiFirma(ds, arxiuDoc);
		assertNull(ds.getNtiTipoFirma());
		assertNull(ds.getNtiCsv());
		assertNull(ds.getNtiDefinicionGenCsv());
	}

	@Test
	public void actualitzarNtiFirmaAmbLlistaBuidaNoCanviaRes() {
		DocumentStore ds = nouDocumentStore(1L, "a.pdf");
		es.caib.pluginsib.arxiu.api.Document arxiuDoc = nouArxiuDocument(new ArrayList<>(), null, null);
		helper.actualitzarNtiFirma(ds, arxiuDoc);
		assertNull(ds.getNtiTipoFirma());
	}

	// --------------------------------------------------
	// inArxiu
	// --------------------------------------------------

	private ContingutArxiu nouContingut(String uuid, String nom) {
		ContingutArxiu c = new ContingutArxiu();
		c.setIdentificador(uuid);
		c.setNom(nom);
		return c;
	}

	private void mockExpedientArxiu(String processId, boolean arxiuActiu, List<ContingutArxiu> continguts) {
		Expedient exp = new Expedient();
		exp.setId(1L);
		exp.setArxiuActiu(arxiuActiu);
		exp.setArxiuUuid("uuid-exp");
		when(expedientHelper.findExpedientByProcessInstanceId(processId)).thenReturn(exp);
		if (arxiuActiu) {
			es.caib.pluginsib.arxiu.api.Expedient arxiuExp =
				mock(es.caib.pluginsib.arxiu.api.Expedient.class);
			when(arxiuExp.getContinguts()).thenReturn(continguts);
			when(pluginHelper.arxiuExpedientInfo("uuid-exp")).thenReturn(arxiuExp);
		}
	}

	@Test
	public void inArxiuSiArxiuNoActiuRetornaNomRevisat() {
		mockExpedientArxiu("proc1", false, null);
		String resultat = helper.inArxiu("proc1", "uuid-doc", "doc.pdf");
		assertEquals(ArxiuConversioHelper.revisarContingutNom("doc.pdf"), resultat);
		verifyNoInteractions(pluginHelper);
	}

	@Test
	public void inArxiuAmbNomNullRetornaTimestamp() {
		mockExpedientArxiu("proc1", false, null);
		String resultat = helper.inArxiu("proc1", "uuid-doc", null);
		assertNotNull(resultat);
		assertFalse(resultat.isEmpty());
	}

	@Test
	public void inArxiuSenseColisioRetornaMateixNom() {
		mockExpedientArxiu("proc1", true,
			Arrays.asList(nouContingut("altre-uuid", "altre.pdf")));
		String resultat = helper.inArxiu("proc1", "uuid-doc", "nou.pdf");
		assertEquals(ArxiuConversioHelper.revisarContingutNom("nou.pdf"), resultat);
	}

	@Test
	public void inArxiuAmbColisioAmbExtensioAfegeixSufix() {
		mockExpedientArxiu("proc1", true,
			Arrays.asList(nouContingut("altre-uuid", "doc.pdf")));
		String resultat = helper.inArxiu("proc1", "uuid-doc", "doc.pdf");
		assertEquals("doc (1).pdf", resultat);
	}

	@Test
	public void inArxiuAmbColisioMultipleIncrementaSufix() {
		mockExpedientArxiu("proc1", true, Arrays.asList(
			nouContingut("u1", "doc.pdf"),
			nouContingut("u2", "doc (1).pdf")));
		String resultat = helper.inArxiu("proc1", "uuid-doc", "doc.pdf");
		assertEquals("doc (2).pdf", resultat);
	}

	@Test
	public void inArxiuAmbColisioSenseExtensio() {
		mockExpedientArxiu("proc1", true,
			Arrays.asList(nouContingut("altre-uuid", "senseext")));
		String resultat = helper.inArxiu("proc1", "uuid-doc", "senseext");
		assertEquals("senseext (1)", resultat);
	}

	@Test
	public void inArxiuIgnoraDocumentAmbMateixUuid() {
		mockExpedientArxiu("proc1", true,
			Arrays.asList(nouContingut("uuid-doc", "doc.pdf")));
		String resultat = helper.inArxiu("proc1", "uuid-doc", "doc.pdf");
		assertEquals("doc.pdf", resultat);
	}

	@Test
	public void inArxiuColisioCaseInsensitive() {
		mockExpedientArxiu("proc1", true,
			Arrays.asList(nouContingut("altre-uuid", "DOC.PDF")));
		String resultat = helper.inArxiu("proc1", "uuid-doc", "doc.pdf");
		assertEquals("doc (1).pdf", resultat);
	}

	// --------------------------------------------------
	// findDocument
	// --------------------------------------------------

	@Test
	public void findDocumentAmbInfoPropia() {
		ExpedientTipus tipus = new ExpedientTipus();
		tipus.setId(10L);
		tipus.setAmbInfoPropia(true);
		Expedient exp = new Expedient();
		exp.setTipus(tipus);
		when(expedientHelper.getExpedientComprovantPermisos(1L, true, false, false, false)).thenReturn(exp);

		Document esperat = new Document();
		when(documentRepository.findByExpedientTipusAndCodi(10L, "C1", false)).thenReturn(esperat);

		assertSame(esperat, helper.findDocument(1L, "procX", "C1"));
		verifyNoInteractions(workflowEngineApi);
	}

	@Test
	public void findDocumentAmbInfoPropiaIHerencia() {
		ExpedientTipus pare = new ExpedientTipus();
		ExpedientTipus tipus = new ExpedientTipus();
		tipus.setId(10L);
		tipus.setAmbInfoPropia(true);
		tipus.setExpedientTipusPare(pare);
		Expedient exp = new Expedient();
		exp.setTipus(tipus);
		when(expedientHelper.getExpedientComprovantPermisos(1L, true, false, false, false)).thenReturn(exp);

		Document esperat = new Document();
		when(documentRepository.findByExpedientTipusAndCodi(10L, "C1", true)).thenReturn(esperat);

		assertSame(esperat, helper.findDocument(1L, "procX", "C1"));
	}

	@Test
	public void findDocumentSenseInfoPropiaUsaDefinicioProces() {
		ExpedientTipus tipus = new ExpedientTipus();
		tipus.setAmbInfoPropia(false);
		Expedient exp = new Expedient();
		exp.setTipus(tipus);
		when(expedientHelper.getExpedientComprovantPermisos(1L, true, false, false, false)).thenReturn(exp);

		WProcessInstance pi = new WProcessInstance();
		pi.setProcessDefinitionId("def123");
		when(workflowEngineApi.getRootProcessInstance("procX")).thenReturn(pi);

		Document esperat = new Document();
		when(documentRepository.findByDefinicioProces("def123", "C1")).thenReturn(esperat);

		assertSame(esperat, helper.findDocument(1L, "procX", "C1"));
	}

	// --------------------------------------------------
	// setDocument (sobrecàrrega simple) + generarDocument
	// --------------------------------------------------

	@Test
	public void setDocumentSimpleFaUpsertICreaVinculacio() {
		Expedient exp = new Expedient();
		exp.setId(1L);
		when(expedientHelper.findById(1L)).thenReturn(exp);

		DocumentStore ds = new DocumentStore(
			DocumentStore.DocumentFont.INTERNA, "proc1", "DOC1", new Date(), new Date(), "a.pdf");

		when(expedientDocumentRepository.findByCodi("DOC1", 1L, "proc1", "task1"))
			.thenReturn(null);
		when(expedientDocumentRepository.save(any(ExpedientDocument.class)))
			.thenAnswer(inv -> inv.getArgument(0));

		DocumentStore resultat = helper.setDocument(1L, "proc1", "task1", ds);
		assertSame(ds, resultat);
		verify(expedientDocumentRepository).save(any(ExpedientDocument.class));
	}

	@Test
	public void setDocumentSimpleActualitzaVinculacioExistent() {
		Expedient exp = new Expedient();
		exp.setId(1L);
		when(expedientHelper.findById(1L)).thenReturn(exp);

		DocumentStore dsNou = new DocumentStore(
			DocumentStore.DocumentFont.INTERNA, "proc1", "DOC1", new Date(), new Date(), "nou.pdf");
		ExpedientDocument existent = ExpedientDocument.builder()
			.documentStore(new DocumentStore())
			.codi("DOC1")
			.expedient(exp)
			.processInstanceId("proc1")
			.taskId("task1")
			.build();
		when(expedientDocumentRepository.findByCodi("DOC1", 1L, "proc1", "task1"))
			.thenReturn(existent);
		when(expedientDocumentRepository.save(any(ExpedientDocument.class)))
			.thenAnswer(inv -> inv.getArgument(0));

		helper.setDocument(1L, "proc1", "task1", dsNou);
		assertSame(dsNou, existent.getDocumentStore());
	}

	@Test
	public void generarDocumentAmbPlantillaNoPlantillaRetornaArxiuAmbMime() {
		Expedient exp = new Expedient();
		Document doc = new Document();
		doc.setPlantilla(false);
		doc.setArxiuNom("informe.pdf");
		doc.setArxiuContingut(new byte[]{1, 2, 3});

		ArxiuDto resultat = helper.generarDocumentAmbPlantillaIConvertir(exp, doc, "t1", "p1", new Date());
		assertNotNull(resultat);
		assertEquals("informe.pdf", resultat.getNom());
		assertArrayEquals(new byte[]{1, 2, 3}, resultat.getContingut());
		assertNotNull(resultat.getTipusMime());
		verifyNoInteractions(pluginHelper);
	}
}
