package es.caib.helium.domini.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.caib.helium.domini.DominiTestHelper;
import es.caib.helium.domini.model.DominiDto;
import es.caib.helium.domini.model.PagedList;
import es.caib.helium.domini.model.ResultatDomini;
import es.caib.helium.domini.model.TipusAuthEnum;
import es.caib.helium.domini.model.TipusDominiEnum;
import es.caib.helium.domini.service.DominiService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.reset;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = DominiController.class,
        excludeAutoConfiguration = {
            org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
            org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration.class})
class DominiControllerTest {

    @MockBean
    DominiService dominiService;

    @Spy
    ObjectMapper objectMapper = new ObjectMapper();
//    SmartValidator smartValidator = new LocalValidatorFactoryBean();

    @Autowired
    MockMvc mockMvc;

    @Captor
    ArgumentCaptor<DominiDto> dominiCaptor;
    @Captor
    ArgumentCaptor<String> identificadorCaptor;

    DominiDto dominiDto;
    PagedList<DominiDto> dominiDtoPagedList;

    @BeforeEach
    void setUp() {
        dominiDto = DominiTestHelper.generateDominiDto(
                1,
                "CD1",
                1L,
                2L,
                TipusDominiEnum.CONSULTA_REST,
                TipusAuthEnum.HTTP_BASIC);

        dominiDtoPagedList = new PagedList<>(
                List.of(dominiDto),
                PageRequest.of(0, 10),
                1L);
    }

    @Test
    @DisplayName("Consulta de dominis")
    void whenListDominisV1_thenReturnList() throws Exception {
        given(dominiService.listDominis(
                anyLong(),
                nullable(Long.class),
                nullable(Long.class),
                nullable(String.class),
                nullable(Pageable.class),
                nullable(Sort.class))).willReturn(dominiDtoPagedList);

        mockMvc.perform(get("/api/v1/dominis").param("entornId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].nom").value("Domini_nom1"));
    }

    @Test
    @DisplayName("Consulta de dominis - Sense resultat")
    void whenListDominisV1_thenReturnNoContent() throws Exception {
        given(dominiService.listDominis(
                anyLong(),
                nullable(Long.class),
                nullable(Long.class),
                nullable(String.class),
                nullable(Pageable.class),
                nullable(Sort.class))).willReturn(new PagedList<DominiDto>(new ArrayList<>()));

        mockMvc.perform(get("/api/v1/dominis").param("entornId", "1"))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.content").doesNotHaveJsonPath());
    }

    @Test
    @DisplayName("Consulta de domini per id")
    void whenGetDominiV1_thenReturnDomini() throws Exception {
        given(dominiService.getById(anyLong())).willReturn(dominiDto);

        mockMvc.perform(get("/api/v1/dominis/{dominiId}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.nom").value("Domini_nom1"));
    }

    @Test
    @DisplayName("Consulta de domini - Error no trobat")
    void whenGetDominiV1_thenReturnNotFound() throws Exception {
        given(dominiService.getById(anyLong()))
                .willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found. Id: 1"));

        mockMvc.perform(get("/api/v1/dominis/{dominiId}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").doesNotHaveJsonPath());
    }

    @Test
    @DisplayName("Crear domini")
    void whenCreateDominiV1_thenReturnLocation() throws Exception {
        String dominiJson = asJsonString(dominiDto);
        dominiDto.setId(777L);
        given(dominiService.createDomini(any(DominiDto.class))).willReturn(dominiDto);

        mockMvc.perform(post("/api/v1/dominis")
                .content(dominiJson)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/dominis/777"));
    }

    @Test
    @DisplayName("Crear domini - error de validació")
    void whenCreateDominiV1_thenReturnBadRequestIntegrity() throws Exception {
        String dominiJson = asJsonString(dominiDto);
        dominiDto.setId(777L);
        given(dominiService.createDomini(any(DominiDto.class))).willThrow(DataIntegrityViolationException.class);

        mockMvc.perform(post("/api/v1/dominis")
                .content(dominiJson)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").doesNotHaveJsonPath());
    }

    @Test
    @DisplayName("Crear domini - error entorn null")
    void whenCreateDominiV1_thenReturnBadRequestEntorn() throws Exception {
        dominiDto.setEntorn(null);
        String dominiJson = asJsonString(dominiDto);
        dominiDto.setId(777L);
        given(dominiService.createDomini(any(DominiDto.class))).willReturn(dominiDto);

        mockMvc.perform(post("/api/v1/dominis")
                .content(dominiJson)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").doesNotHaveJsonPath());
    }

    @Test
    @DisplayName("Actualitzar domini")
    void whenUpdateDominiV1_thenReturnNoContent() throws Exception {
        String dominiJson = asJsonString(dominiDto);
        given(dominiService.updateDomini(anyLong(), any(DominiDto.class))).willReturn(dominiDto);

        mockMvc.perform(put("/api/v1/dominis/{dominiId}", 5L)
                .content(dominiJson)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Actualitzar domini - Error de validació")
    void whenUpdateDominiV1_thenReturnBadRequest() throws Exception {
        String dominiJson = asJsonString(dominiDto);
        given(dominiService.updateDomini(anyLong(), any(DominiDto.class))).willThrow(DataIntegrityViolationException.class);

        mockMvc.perform(put("/api/v1/dominis/{dominiId}", 5L)
                .content(dominiJson)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Patch domini")
    void whenPatchDominiV1_thenUpdate() throws Exception {
        final String NOM = "Nom modificat";
        String jsonString =
                "[" +
                    "{\"op\":\"replace\", \"path\":\"/nom\", \"value\":\"" + NOM + "\"}" +
                "]";
        JsonNode dominiJson = objectMapper.readTree(jsonString);

        given(dominiService.getById(anyLong())).willReturn(dominiDto);
        given(dominiService.updateDomini(anyLong(), dominiCaptor.capture())).willReturn(dominiDto);

        mockMvc.perform(patch("/api/v1/dominis/{dominiId}", 5L)
                .content(String.valueOf(dominiJson))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isNoContent());

        assertThat(dominiCaptor.getValue().getNom()).isEqualTo(NOM);
    }

    @Test
    @DisplayName("Patch domini - Error de validació")
    void whenPatchDominiV1_thenErrorValidacio() throws Exception {
        final String NOM = "Nom modificat";
        String jsonString =
                "[" +
                    "{\"op\":\"replace\", \"path\":\"/nom\", \"value\":\"\"}" +
                "]";
        JsonNode dominiJson = objectMapper.readTree(jsonString);

        given(dominiService.getById(anyLong())).willReturn(dominiDto);

        mockMvc.perform(patch("/api/v1/dominis/{dominiId}", 5L)
                .content(String.valueOf(dominiJson))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Eliminar domini")
    void whenDeleteDominiV1_thenDelete() throws Exception {

        given(dominiService.getById(anyLong())).willReturn(dominiDto);
        willDoNothing().given(dominiService).delete(anyLong());

        mockMvc.perform(delete("/api/v1/dominis/{dominiId}", 5L))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Eliminar domini - Error no trobat")
    void whenDeleteDominiV1_thenNotFound() throws Exception {

        given(dominiService.getById(anyLong()))
                .willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found."));

        mockMvc.perform(get("/api/v1/dominis/{dominiId}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").doesNotHaveJsonPath());
    }

    @Test
    @DisplayName("Consulta de dades de domini")
    void whenConsultaDominiV1_thenReturn() throws Exception {
        ResultatDomini resultatDomini = new ResultatDomini();
        given(dominiService.consultaDomini(anyLong(), identificadorCaptor.capture(), anyMap())).willReturn(resultatDomini);

        final String idf = "IDF";
        mockMvc.perform(get("/api/v1/dominis/{dominiId}/resultats", 1L)
                    .param("identificador", idf))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        assertEquals(idf, identificadorCaptor.getValue());
    }

    @Test
    @DisplayName("Consulta de dades de domini - Error no trobat")
    void whenConsultaDominiV1_thenNotFound() throws Exception {

        given(dominiService.consultaDomini(anyLong(), identificadorCaptor.capture(), anyMap()))
                .willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found."));
        final String idf = "IDF";
        mockMvc.perform(get("/api/v1/dominis/{dominiId}/resultats", 3L)
                .param("identificador", idf))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").doesNotHaveJsonPath());

        assertEquals(idf, identificadorCaptor.getValue());
    }

    @AfterEach
    void tearDown() {
        reset(dominiService);
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}