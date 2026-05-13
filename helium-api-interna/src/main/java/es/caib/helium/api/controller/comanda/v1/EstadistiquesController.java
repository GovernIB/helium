package es.caib.helium.api.controller.comanda.v1;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import es.caib.comanda.model.server.monitoring.DimensioDesc;
import es.caib.comanda.model.server.monitoring.EstadistiquesInfo;
import es.caib.comanda.model.server.monitoring.IndicadorDesc;
import es.caib.comanda.model.server.monitoring.RegistresEstadistics;
import es.caib.helium.logic.intf.service.EstadisticaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/estadistiques")
@Tag(
	name = "Estadístiques",
	description = "API REST de consulta de les estadístiques de Helium per mostrar a l'aplicació Comanda.")
public class EstadistiquesController {

	@Autowired
	private EstadisticaService estadisticaService;

	@GetMapping
	public RegistresEstadistics estadistiques() {
		return estadisticaService.consultaDarreresEstadistiques();
	}

	@GetMapping("/of/{data}")
	public RegistresEstadistics estadistiques(@PathVariable String data) throws Exception {
		LocalDate parsedData = LocalDate.parse(data, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
		return estadisticaService.consultaEstadistiques(asDate(parsedData));
	}

	@GetMapping("/from/{dataInici}/to/{dataFi}")
	public List<RegistresEstadistics> estadistiques(HttpServletRequest request, @PathVariable String dataInici, @PathVariable String dataFi) throws Exception {
		LocalDate dataFrom = LocalDate.parse(dataInici, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
		LocalDate dataTo = LocalDate.parse(dataFi, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
		LocalDate startLocalData = dataFrom.isBefore(dataTo) ? dataFrom : dataTo;
		LocalDate endLocalData = dataFrom.isBefore(dataTo) ? dataTo : dataFrom;
		LocalDate ahir = LocalDate.now().minusDays(1);
		if (endLocalData.isAfter(ahir)) {
			endLocalData = ahir;
		}
		return estadisticaService.consultaEstadistiques(asDate(startLocalData), asDate(endLocalData));
	}

	@GetMapping("/info")
	public EstadistiquesInfo statsInfo() {
		List<DimensioDesc> dimensions = estadisticaService.getDimensions();
		List<IndicadorDesc> indicadors = estadisticaService.getIndicadors();
		return new EstadistiquesInfo()
			.codi("HEL")
			.dimensions(dimensions)
			.indicadors(indicadors);
	}

	private Date asDate(LocalDate localDate) {
		return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
	}

}
