package es.caib.helium.domini.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.caib.helium.domini.DominiTestHelper;
import es.caib.helium.domini.model.DominiDto;
import es.caib.helium.domini.model.PagedList;
import es.caib.helium.domini.model.TipusAuthEnum;
import es.caib.helium.domini.model.TipusDominiEnum;
import es.caib.helium.domini.service.DominiService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.reset;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(value = ExpedientTipusDominiController.class,
        excludeAutoConfiguration = {
                org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
                org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration.class})
class ExpedientTipusDominiControllerTest {

    @MockBean
    DominiService dominiService;

    @Spy
    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    MockMvc mockMvc;

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
    @DisplayName("Consulta dominis per tipus d'expedient")
    void whenLlistaDominiByExpedientTipus_thenReturnList() throws Exception {
        given(dominiService.listDominisByExpedientTipus(
                anyLong(),
                nullable(String.class),
                nullable(Pageable.class),
                nullable(Sort.class))).willReturn(dominiDtoPagedList);

        mockMvc.perform(get("/api/v1/expedientTipus/{expedientTipusId}/dominis", 2L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].nom").value("Domini_nom1"));
    }

    @Test
    @DisplayName("Consulta de dominis per tipus d'expedient - Sense resultat")
    void whenLlistaDominiByExpedientTipus_thenReturnNoContent() throws Exception {
        given(dominiService.listDominisByExpedientTipus(
                anyLong(),
                nullable(String.class),
                nullable(Pageable.class),
                nullable(Sort.class))).willReturn(new PagedList<DominiDto>(new ArrayList<>()));

        mockMvc.perform(get("/api/v1/expedientTipus/{expedientTipusId}/dominis", 2L))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.content").doesNotHaveJsonPath());
    }

    @Test
    @DisplayName("Consulta de domini per tipus d'expedient i codi")
    void whenGetDominiByExpedientTipusAndCodi_thenReturnDomini() throws Exception {
        given(dominiService.getByExpedientTipusAndCodi(anyLong(), nullable(Long.class), anyString())).willReturn(dominiDto);

        mockMvc.perform(get("/api/v1/expedientTipus/{expedientTipusId}/dominis/{codi}", 2L, "CD1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.nom").value("Domini_nom1"));
    }

    @Test
    @DisplayName("Consulta de domini per tipus d'expedient i codi - Domini no trobat")
    void whenGetDominiByExpedientTipusAndCodi_thenReturnNotFound() throws Exception {
        given(dominiService.getByExpedientTipusAndCodi(anyLong(), nullable(Long.class), anyString()))
                .willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found."));

        mockMvc.perform(get("/api/v1/expedientTipus/{expedientTipusId}/dominis/{codi}", 2L, "CD2"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").doesNotHaveJsonPath());
    }

    @AfterEach
    void tearDown() {
        reset(dominiService);
    }
}