package es.caib.helium.expedient.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.caib.helium.expedient.model.ExpedientDto;
import es.caib.helium.expedient.service.ExpedientService;
import es.caib.helium.ms.model.PagedList;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(value = ExpedientController.class,
        excludeAutoConfiguration = {
            org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
            org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration.class})
class ExpedientControllerTest {

    @MockBean
    ExpedientService expedientService;

    @Spy
    ObjectMapper objectMapper = new ObjectMapper();
//    SmartValidator smartValidator = new LocalValidatorFactoryBean();

    @Autowired
    MockMvc mockMvc;

    @Captor
    ArgumentCaptor<ExpedientDto> expedientCaptor;
    @Captor
    ArgumentCaptor<String> identificadorCaptor;

    ExpedientDto expedientDto;
    PagedList<ExpedientDto> expedientDtoPagedList;

    @BeforeEach
    void setUp() {
//        expedientDto = ExpedientTestHelper.generateExpedientDto(
//                1,
//                "CD1",
//                1L,
//                2L,
//                TipusAuthEnum.HTTP_BASIC);
//
//        expedientDtoPagedList = new PagedList<>(
//                List.of(expedientDto),
//                PageRequest.of(0, 10),
//                1L);
    }

//    @Test
//    @DisplayName("Consulta de expedients")
//    void whenListExpedientsV1_thenReturnList() throws Exception {
//        given(expedientService.listExpedients(
//                anyLong(),
//                nullable(Long.class),
//                nullable(Long.class),
//                nullable(String.class),
//                nullable(Pageable.class),
//                nullable(Sort.class))).willReturn(expedientDtoPagedList);
//
//        mockMvc.perform(get("/api/v1/expedients").param("entornId", "1"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.content").exists())
//                .andExpect(jsonPath("$.content", hasSize(1)))
//                .andExpect(jsonPath("$.content[0].nom").value("Expedient_nom1"));
//    }
//
//    @Test
//    @DisplayName("Consulta de expedients - Sense resultat")
//    void whenListExpedientsV1_thenReturnNoContent() throws Exception {
//        given(expedientService.listExpedients(
//                anyLong(),
//                nullable(Long.class),
//                nullable(Long.class),
//                nullable(String.class),
//                nullable(Pageable.class),
//                nullable(Sort.class))).willReturn(new PagedList<ExpedientDto>(new ArrayList<>()));
//
//        mockMvc.perform(get("/api/v1/expedients").param("entornId", "1"))
//                .andExpect(status().isNoContent())
//                .andExpect(jsonPath("$.content").doesNotHaveJsonPath());
//    }
//
//    @Test
//    @DisplayName("Consulta de expedient per id")
//    void whenGetExpedientV1_thenReturnExpedient() throws Exception {
//        given(expedientService.getById(anyLong())).willReturn(expedientDto);
//
//        mockMvc.perform(get("/api/v1/expedients/{expedientId}", 1L))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.nom").value("Expedient_nom1"));
//    }
//
//    @Test
//    @DisplayName("Consulta de expedient - Error no trobat")
//    void whenGetExpedientV1_thenReturnNotFound() throws Exception {
//        given(expedientService.getById(anyLong()))
//                .willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found. Id: 1"));
//
//        mockMvc.perform(get("/api/v1/expedients/{expedientId}", 1L))
//                .andExpect(status().isNotFound())
//                .andExpect(jsonPath("$").doesNotHaveJsonPath());
//    }
//
//    @Test
//    @DisplayName("Crear expedient")
//    void whenCreateExpedientV1_thenReturnLocation() throws Exception {
//        String expedientJson = asJsonString(expedientDto);
//        expedientDto.setId(777L);
//        given(expedientService.createExpedient(any(ExpedientDto.class))).willReturn(expedientDto);
//
//        mockMvc.perform(post("/api/v1/expedients")
//                .content(expedientJson)
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON))
//
//                .andExpect(status().isCreated())
//                .andExpect(header().string("Location", "/api/v1/expedients/777"));
//    }
//
//    @Test
//    @DisplayName("Crear expedient - error de validació")
//    void whenCreateExpedientV1_thenReturnBadRequestIntegrity() throws Exception {
//        String expedientJson = asJsonString(expedientDto);
//        expedientDto.setId(777L);
//        given(expedientService.createExpedient(any(ExpedientDto.class))).willThrow(DataIntegrityViolationException.class);
//
//        mockMvc.perform(post("/api/v1/expedients")
//                .content(expedientJson)
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON))
//
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$").doesNotHaveJsonPath());
//    }
//
//    @Test
//    @DisplayName("Crear expedient - error entorn null")
//    void whenCreateExpedientV1_thenReturnBadRequestEntorn() throws Exception {
//        expedientDto.setEntorn(null);
//        String expedientJson = asJsonString(expedientDto);
//        expedientDto.setId(777L);
//        given(expedientService.createExpedient(any(ExpedientDto.class))).willReturn(expedientDto);
//
//        mockMvc.perform(post("/api/v1/expedients")
//                .content(expedientJson)
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON))
//
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$").doesNotHaveJsonPath());
//    }
//
//    @Test
//    @DisplayName("Actualitzar expedient")
//    void whenUpdateExpedientV1_thenReturnNoContent() throws Exception {
//        String expedientJson = asJsonString(expedientDto);
//        given(expedientService.updateExpedient(anyLong(), any(ExpedientDto.class))).willReturn(expedientDto);
//
//        mockMvc.perform(put("/api/v1/expedients/{expedientId}", 5L)
//                .content(expedientJson)
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON))
//
//                .andExpect(status().isNoContent());
//    }
//
//    @Test
//    @DisplayName("Actualitzar expedient - Error de validació")
//    void whenUpdateExpedientV1_thenReturnBadRequest() throws Exception {
//        String expedientJson = asJsonString(expedientDto);
//        given(expedientService.updateExpedient(anyLong(), any(ExpedientDto.class))).willThrow(DataIntegrityViolationException.class);
//
//        mockMvc.perform(put("/api/v1/expedients/{expedientId}", 5L)
//                .content(expedientJson)
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON))
//
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    @DisplayName("Patch expedient")
//    void whenPatchExpedientV1_thenUpdate() throws Exception {
//        final String NOM = "Nom modificat";
//        String jsonString =
//                "[" +
//                    "{\"op\":\"replace\", \"path\":\"/nom\", \"value\":\"" + NOM + "\"}" +
//                "]";
//        JsonNode expedientJson = objectMapper.readTree(jsonString);
//
//        given(expedientService.getById(anyLong())).willReturn(expedientDto);
//        given(expedientService.updateExpedient(anyLong(), expedientCaptor.capture())).willReturn(expedientDto);
//
//        mockMvc.perform(patch("/api/v1/expedients/{expedientId}", 5L)
//                .content(String.valueOf(expedientJson))
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON))
//
//                .andExpect(status().isNoContent());
//
//        assertThat(expedientCaptor.getValue().getNom()).isEqualTo(NOM);
//    }
//
//    @Test
//    @DisplayName("Patch expedient - Error de validació")
//    void whenPatchExpedientV1_thenErrorValidacio() throws Exception {
//        String jsonString =
//                "[" +
//                    "{\"op\":\"replace\", \"path\":\"/nom\", \"value\":\"\"}" +
//                "]";
//        JsonNode expedientJson = objectMapper.readTree(jsonString);
//
//        given(expedientService.getById(anyLong())).willReturn(expedientDto);
//
//        mockMvc.perform(patch("/api/v1/expedients/{expedientId}", 5L)
//                .content(String.valueOf(expedientJson))
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON))
//
//                .andExpect(status().isConflict());
//    }
//
//    @Test
//    @DisplayName("Eliminar expedient")
//    void whenDeleteExpedientV1_thenDelete() throws Exception {
//
//        given(expedientService.getById(anyLong())).willReturn(expedientDto);
//        willDoNothing().given(expedientService).delete(anyLong());
//
//        mockMvc.perform(delete("/api/v1/expedients/{expedientId}", 5L))
//                .andExpect(status().isNoContent());
//    }
//
//    @Test
//    @DisplayName("Eliminar expedient - Error no trobat")
//    void whenDeleteExpedientV1_thenNotFound() throws Exception {
//
//        given(expedientService.getById(anyLong()))
//                .willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found."));
//
//        mockMvc.perform(get("/api/v1/expedients/{expedientId}", 1L))
//                .andExpect(status().isNotFound())
//                .andExpect(jsonPath("$").doesNotHaveJsonPath());
//    }
//
//    @AfterEach
//    void tearDown() {
//        reset(expedientService);
//    }
//
//    public static String asJsonString(final Object obj) {
//        try {
//            return new ObjectMapper().writeValueAsString(obj);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
}