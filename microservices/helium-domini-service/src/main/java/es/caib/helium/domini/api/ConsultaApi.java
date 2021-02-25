package es.caib.helium.domini.api;

import es.caib.helium.domini.model.ResultatDomini;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.Map;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2021-02-12T13:24:13.511Z[GMT]")
@Tag(name = "Consulta", description = "the Consulta API")
@RequestMapping("/v1/dominis")
public interface ConsultaApi {

    @Operation(summary = "Consulta un domini", description = "Realitza la consulta del domini amb l'identificador passat al paràmetre `dominiId`.   Per a realitzar la consulta utilitza la configuració del domini, juntament amb els paràmetres passats.   Els paràmetres es passaran com un mapa amb la clau de tipus string, i un valor de tipus objecte ", tags={ "Consulta" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok", content = @Content(schema = @Schema(implementation = ResultatDomini.class))) })
    @RequestMapping(value = "/v1/dominis/{dominiId}/resultats",
            produces = { "application/json" },
            method = RequestMethod.GET)
    ResponseEntity<ResultatDomini> consultaDominiV1(
            @Parameter(in = ParameterIn.PATH, description = "Identificador del domini", required=true, schema=@Schema()) @PathVariable("dominiId") Long dominiId,
            @Parameter(in = ParameterIn.QUERY, description = "identificador del domini al sistema remot" ,schema=@Schema()) @Valid @RequestParam(value = "identificador", required = false) String identificador,
            @Parameter(in = ParameterIn.QUERY, description = "Map<String, Object> amb els paràmetres que es volen passar en la consulta del domini" ,schema=@Schema()) @Valid @RequestParam(value = "parametres", required = false) Map<String, Object> parametres);

}
