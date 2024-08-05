package net.conselldemallorca.helium.webapp.v3.helper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import net.conselldemallorca.helium.v3.core.api.dto.NtiEstadoElaboracionEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.NtiOrigenEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.NtiTipoDocumentalEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.NtiTipoFirmaEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.v3.core.api.dto.PinbalConsentimentEnum;
import net.conselldemallorca.helium.v3.core.api.dto.PinbalServeiEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.Sexe;
import net.conselldemallorca.helium.v3.core.api.dto.TipusPassaportEnum;

/**
 * Classe helper amb mètods comuns per treballar amb dades NTi.
 * @author Limit Tecnologies <limit@limit.es>
 *
 */
@Component
public class NtiHelper {

	public void omplirOrigen(
			Model model) {
		List<ParellaCodiValorDto> tdlist = new ArrayList<ParellaCodiValorDto>();
		for(NtiOrigenEnumDto or: NtiOrigenEnumDto.values())
			tdlist.add(new ParellaCodiValorDto(
					or.name(),
					MessageHelper.getInstance().getMessage("nti.document.origen." + or.name())));		
		model.addAttribute("ntiOrigen", tdlist);
	}
	
	public void omplirServeisPinbal(
			Model model) {
		List<ParellaCodiValorDto> tdlist = new ArrayList<ParellaCodiValorDto>();
		for(PinbalServeiEnumDto or: PinbalServeiEnumDto.values())
			tdlist.add(new ParellaCodiValorDto(
					or.name(),
					MessageHelper.getInstance().getMessage("serveisPinbal.enum." + or.name())));		
		model.addAttribute("serveisPinbalEnum", tdlist);
	}
	
	public void omplirConsentiment(Model model) {
		List<ParellaCodiValorDto> tdlist = new ArrayList<ParellaCodiValorDto>();
		for(PinbalConsentimentEnum or: PinbalConsentimentEnum.values())
			tdlist.add(new ParellaCodiValorDto(
					or.name(),
					MessageHelper.getInstance().getMessage("consentiment.enum." + or.name())));		
		model.addAttribute("consentimentList", tdlist);
	}
	
	public void omplirTipusPassaport(Model model) {
		List<ParellaCodiValorDto> tdlist = new ArrayList<ParellaCodiValorDto>();
		for(TipusPassaportEnum or: TipusPassaportEnum.values())
			tdlist.add(new ParellaCodiValorDto(or.name(), or.name()));		
		model.addAttribute("tipusPassaportsList", tdlist);
	}

	public void omplirSexe(Model model) {
		List<ParellaCodiValorDto> tdlist = new ArrayList<ParellaCodiValorDto>();
		for(Sexe or: Sexe.values())
			tdlist.add(new ParellaCodiValorDto(
					or.name(),
					MessageHelper.getInstance().getMessage("enum.sexe." + or.name())));		
		model.addAttribute("sexes", tdlist);
	}
	
	public void omplirEstadoElaboracion(
			Model model) {
		List<ParellaCodiValorDto> tdlist = new ArrayList<ParellaCodiValorDto>();
		for(NtiEstadoElaboracionEnumDto ee: NtiEstadoElaboracionEnumDto.values())
			tdlist.add(new ParellaCodiValorDto(
					ee.name(),
					MessageHelper.getInstance().getMessage("nti.document.estado.elaboracion." + ee.name())));		
		model.addAttribute("ntiEstadoElaboracion", tdlist);
	}

	public void omplirTipoDocumental(
			Model model) {
		List<ParellaCodiValorDto> tdlist = new ArrayList<ParellaCodiValorDto>();
		for(NtiTipoDocumentalEnumDto td: NtiTipoDocumentalEnumDto.values())
			tdlist.add(new ParellaCodiValorDto(
					td.name(),
					MessageHelper.getInstance().getMessage("nti.document.tipo.documental." + td.name())));		
		model.addAttribute("ntiTipoDocumental", tdlist);
	}

	public void omplirTipoFirma(
			Model model) {
		List<ParellaCodiValorDto> tdlist = new ArrayList<ParellaCodiValorDto>();
		for(NtiTipoFirmaEnumDto tf : NtiTipoFirmaEnumDto.values())
			tdlist.add(new ParellaCodiValorDto(
					tf.name(),
					MessageHelper.getInstance().getMessage("nti.tipo.firma." + tf.name())));
		model.addAttribute("ntiTipoFirma", tdlist);
	}

}
