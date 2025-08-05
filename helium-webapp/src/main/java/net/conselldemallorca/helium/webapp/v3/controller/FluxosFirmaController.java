package net.conselldemallorca.helium.webapp.v3.controller;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.emory.mathcs.backport.java.util.Collections;
import net.conselldemallorca.helium.core.helper.AlertaHelper;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto.OrdreDireccioDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto.OrdreDto;
import net.conselldemallorca.helium.v3.core.api.dto.PortafirmesFluxRespostaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PortafirmesIniciFluxRespostaDto;
import net.conselldemallorca.helium.v3.core.api.service.PortafirmesFluxService;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.ModalHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper.DatatablesResponse;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;
import net.conselldemallorca.helium.webapp.v3.helper.UrlHelper;

@Controller
@RequestMapping("/v3/fluxeFirma")
public class FluxosFirmaController extends BaseExpedientController {

	@Autowired
	private PortafirmesFluxService portafirmesFluxService;
	
	@RequestMapping(method = RequestMethod.GET)
	public String llistat(
			HttpServletRequest request,
			Model model) {
		return "v3/fluxeFirmaLlistat";
	}
	
	@RequestMapping(value="/datatable", method = RequestMethod.GET)
	@ResponseBody
	DatatablesResponse datatable(
			HttpServletRequest request,
			Model model) {
//		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		List<PortafirmesFluxRespostaDto> resposta = portafirmesFluxService.recuperarPlantillesDisponibles(
				null,
				null,
				SecurityContextHolder.getContext().getAuthentication().getName());
		
		List<PortafirmesFluxRespostaDto> llistaFiltrada = new ArrayList<PortafirmesFluxRespostaDto>();
		boolean filtreAlpicat = false;
		if (paginacioParams!=null) {
			
			//FILTRE
			if (paginacioParams.getFiltre()!=null && !"".equals(paginacioParams.getFiltre())) {
				filtreAlpicat = true;
				for (PortafirmesFluxRespostaDto aux: resposta) {
					if (aux.getFluxId().contains(paginacioParams.getFiltre()) || aux.getNom().contains(paginacioParams.getFiltre())) {
						llistaFiltrada.add(aux);
					}
				}
			}
			
			if (!filtreAlpicat) { llistaFiltrada = resposta; }
			
			//ORDENACIO
			if (paginacioParams.getOrdres()!=null && paginacioParams.getOrdres().size()>0) {
				OrdreDto order = paginacioParams.getOrdres().get(0);
				if ("fluxId".equals(order.getCamp())) {
					if (OrdreDireccioDto.ASCENDENT.equals(order.getDireccio())) {
						Collections.sort(llistaFiltrada, new Comparator<PortafirmesFluxRespostaDto>() {
						   public int compare(PortafirmesFluxRespostaDto o1, PortafirmesFluxRespostaDto o2){
							      return o1.getFluxId().toLowerCase().compareTo(o2.getFluxId().toLowerCase());
							   }
							});
					} else {
						Collections.sort(llistaFiltrada, new Comparator<PortafirmesFluxRespostaDto>() {
						   public int compare(PortafirmesFluxRespostaDto o1, PortafirmesFluxRespostaDto o2){
							      return o2.getFluxId().toLowerCase().compareTo(o1.getFluxId().toLowerCase());
							   }
							});
					}
				} else if ("nom".equals(order.getCamp())) {
					if (OrdreDireccioDto.ASCENDENT.equals(order.getDireccio())) {
						Collections.sort(llistaFiltrada, new Comparator<PortafirmesFluxRespostaDto>() {
						   public int compare(PortafirmesFluxRespostaDto o1, PortafirmesFluxRespostaDto o2){
							      return o1.getNom().toLowerCase().compareTo(o2.getNom().toLowerCase());
							   }
							});
					} else {
						Collections.sort(llistaFiltrada, new Comparator<PortafirmesFluxRespostaDto>() {
						   public int compare(PortafirmesFluxRespostaDto o1, PortafirmesFluxRespostaDto o2){
							      return o2.getNom().toLowerCase().compareTo(o1.getNom().toLowerCase());
							   }
							});
					}
				}
			}
		}
		
		if (filtreAlpicat) {
			return DatatablesHelper.getDatatableResponse(request, null, llistaFiltrada);
		} else {
			return DatatablesHelper.getDatatableResponse(request, null, resposta);
		}
	}
	
	@RequestMapping(value="/createOrUpdateFlux/{fluxId}", method = RequestMethod.GET)
	@ResponseBody
	public PortafirmesIniciFluxRespostaDto createOrUpdateFlux(
			HttpServletRequest request,
			@PathVariable String fluxId, 
			Model model) throws UnsupportedEncodingException {
		String urlReturn;
		PortafirmesIniciFluxRespostaDto transaccioResponse = null;
		try {
			urlReturn = UrlHelper.getAbsoluteControllerBase(
					request,
					(ModalHelper.isModal(request) ? "/modal" : "") + "/v3/fluxeFirma/returnurl/");
			
			if (fluxId==null || "NULL".equalsIgnoreCase(fluxId)) {
				transaccioResponse = portafirmesFluxService.iniciarFluxFirma(
						null, 
						null,
						SecurityContextHolder.getContext().getAuthentication().getName(), 
						urlReturn, 
						true);
			} else {
				transaccioResponse = new PortafirmesIniciFluxRespostaDto();
				transaccioResponse.setUrlRedireccio(portafirmesFluxService.recuperarUrlEdicioPlantilla(fluxId, urlReturn));
			}
		} catch (Exception ex) {
			transaccioResponse = new PortafirmesIniciFluxRespostaDto();
			transaccioResponse.setError(true);
			transaccioResponse.setErrorDescripcio(ex.getMessage());
		}
		return transaccioResponse;
	}
	
	@RequestMapping(value="/delete/{fluxId}", method = RequestMethod.GET)
	public String eliminarFluxFirma(
			HttpServletRequest request,
			@PathVariable String fluxId, 
			Model model) throws UnsupportedEncodingException {
		try {
			portafirmesFluxService.esborrarPlantilla(fluxId);
			MissatgesHelper.success(request, getMessage(request, "fluxosFirma.taula.boto.eliminar.ok"));
		} catch (Exception ex) {
			MissatgesHelper.error(
					request,
					getMessage(request, "fluxosFirma.taula.boto.eliminar.ko") + ": " + ex.getMessage(),
					ex);
		}
		return "redirect:/v3/fluxeFirma";
	}
	
	@RequestMapping(value = "/returnurl", method = RequestMethod.GET)
	public String portasigTransaccioEstat(
			HttpServletRequest request, 
			Model model) {
		//AL modificar un flux de firma, la URL de retorn no afegeix el ID de transacció
		//No podrem cridar a recuperarFluxFirma, pero da igual, retornarem igualment el portafirmesModalTancar
		return portasigTransaccioEstat(request, null, model);
	}
	
	@RequestMapping(value = "/returnurl/{transactionId}", method = RequestMethod.GET)
	public String portasigTransaccioEstat(
			HttpServletRequest request, 
			@PathVariable String transactionId, 
			Model model) {
		
		if (transactionId!=null) {
			PortafirmesFluxRespostaDto resposta = portafirmesFluxService.recuperarFluxFirma(transactionId);
	
			if (resposta.isError() && resposta.getEstat() != null) {
				model.addAttribute(
						"FluxError",
						getMessage(request, "expedient.tipus.document.form.camp.portafirmes.flux.enum." + resposta.getEstat()));
			} else {
				model.addAttribute(
						"FluxCreat",
						getMessage(request, "expedient.tipus.document.form.camp.portafirmes.flux.enum.FINAL_OK"));
				model.addAttribute("fluxId", resposta.getFluxId());
				model.addAttribute("FluxNom", resposta.getNom());
			}
		} else {
			model.addAttribute(
					"FluxCreat",
					getMessage(request, "fluxosFirma.taula.boto.crear.ok"));
		}
		
		model.addAttribute("OrigenFlux", "FluxUsuariList");		
		return "v3/portafirmesModalTancar";
	}
}