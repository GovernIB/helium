package net.conselldemallorca.helium.webapp.v3.rest.comanda;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import net.conselldemallorca.helium.v3.core.api.dto.comanda.DimensioDesc;
import net.conselldemallorca.helium.v3.core.api.dto.comanda.EstadistiquesInfo;
import net.conselldemallorca.helium.v3.core.api.dto.comanda.IndicadorDesc;
import net.conselldemallorca.helium.v3.core.api.dto.comanda.RegistresEstadistics;
import net.conselldemallorca.helium.v3.core.api.service.EstadisticaService;

@Controller
@RequestMapping("/rest")
public class EstadistiquesController extends ComandaBaseController {
	
	private static SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
	
	@Autowired
	private EstadisticaService estadisticaService;

	@RequestMapping(value = "/estadistiquesInfo", method = RequestMethod.GET)
	@ResponseBody
	public EstadistiquesInfo statsInfo() throws IOException {
		ManifestInfo manifestInfo = getManifestInfo();
		List<DimensioDesc> dimensions = estadisticaService.getDimensions();
		List<IndicadorDesc> indicadors = estadisticaService.getIndicadors();
		return EstadistiquesInfo
				.builder()
				.data(manifestInfo.getBuildDate())
				.versio(manifestInfo.getVersion())
				.codi("HEL")
				.dimensions(dimensions)
				.indicadors(indicadors)
				.build();
	}

	@RequestMapping(value = "/estadistiques", method = RequestMethod.GET)
	@ResponseBody
	public RegistresEstadistics estadistiques(HttpServletRequest request) throws IOException {
		return estadisticaService.consultaDarreresEstadistiques();
	}

	@RequestMapping(value = "/estadistiques/{dies}", method = RequestMethod.GET)
	@ResponseBody
	public List<RegistresEstadistics> estadistiques(HttpServletRequest request, @PathVariable Integer dies)
			throws IOException {

		List<RegistresEstadistics> result = new ArrayList<RegistresEstadistics>();
		Date data = DateUtils.addDays(new Date(), -1);
		for (int i = 0; i < dies; i++) {
			result.add(estadisticaService.consultaEstadistiques(data));
			data = DateUtils.addDays(data, -1);
		}
		return result;
	}

	@RequestMapping(value = "/estadistiques/of/{data}", method = RequestMethod.GET)
	@ResponseBody
	public RegistresEstadistics estadistiques(HttpServletRequest request, @PathVariable String data) throws Exception {
		Date date = dateFormatter.parse(data);
		return estadisticaService.consultaEstadistiques(date);
	}

	@RequestMapping(value = "/estadistiques/from/{dataInici}/to/{dataFi}", method = RequestMethod.GET)
	@ResponseBody
	public List<RegistresEstadistics> estadistiques(HttpServletRequest request, @PathVariable String dataInici,
			@PathVariable String dataFi) throws Exception {
		Date dataFrom = dateFormatter.parse(dataInici);
		Date dataTo = dateFormatter.parse(dataFi);
		Date startDate = DateUtils.truncatedCompareTo(dataFrom, dataTo, Calendar.DATE) < 0 ? dataFrom : dataTo;
		Date endDate = DateUtils.truncatedCompareTo(dataFrom, dataTo, Calendar.DATE) < 0 ? dataTo : dataFrom;
		Date ahir = DateUtils.addDays(new Date(), -1);
		
		if (DateUtils.truncatedCompareTo(endDate, ahir, Calendar.DATE) > 0) {
			endDate = ahir;
		}
		return estadisticaService.consultaEstadistiques(startDate, endDate);
	}

	@RequestMapping(value = "/generarDadesExplotacio", method = RequestMethod.GET)
	@ResponseBody
	public String generarDadesExplotacio(HttpServletRequest request) throws Exception {
		estadisticaService.generarDadesExplotacio();
		return "Done";
	}

	@RequestMapping(value = "/generarDadesExplotacio/{dies}", method = RequestMethod.GET)
	@ResponseBody
	public String generarDadesExplotacio(HttpServletRequest request, @PathVariable Integer dies) throws Exception {
		Date data = new Date();
		
		data = DateUtils.addDays(data, -1);
		for (int i = 0; i < dies; i++) {
			estadisticaService.generarDadesExplotacio(data);
			data = DateUtils.addDays(data, -1);
		}
		return "Done";
	}
}
