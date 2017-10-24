package net.conselldemallorca.helium.webapp.v3.helper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import net.conselldemallorca.helium.v3.core.api.dto.DocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ParellaCodiValorDto;

/**
 * Classe helper amb mètods comuns per treballar amb dades NTi.
 * @author danielm
 *
 */
@Component
public class NtiHelper {
	
	public void omplirTipusDocumental(
			Model model) {
		List<ParellaCodiValorDto> tdlist = new ArrayList<ParellaCodiValorDto>();
		for(DocumentDto.TipoDocumental td : DocumentDto.TipoDocumental.values())
			tdlist.add(new ParellaCodiValorDto(
					td.getCodi(),
					MessageHelper.getInstance().getMessage("tipus.documental." + td)));		
		model.addAttribute("ntiTipusDocumental", tdlist);
	}
	
	public void omplirTipusFirma(
			Model model) {
		List<ParellaCodiValorDto> tdlist = new ArrayList<ParellaCodiValorDto>();
		for(ExpedientTipusDto.TipoFirma tf : ExpedientTipusDto.TipoFirma.values())
			tdlist.add(new ParellaCodiValorDto(
					tf.toString(),
					MessageHelper.getInstance().getMessage("tipo.firma." + tf)));
		
		model.addAttribute("ntiTipoFirma", tdlist);
	}
}
