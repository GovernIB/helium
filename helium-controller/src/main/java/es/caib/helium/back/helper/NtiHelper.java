package es.caib.helium.back.helper;

import es.caib.helium.client.model.ParellaCodiValor;
import es.caib.helium.logic.intf.dto.NtiEstadoElaboracionEnumDto;
import es.caib.helium.logic.intf.dto.NtiOrigenEnumDto;
import es.caib.helium.logic.intf.dto.NtiTipoDocumentalEnumDto;
import es.caib.helium.logic.intf.dto.NtiTipoFirmaEnumDto;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe helper amb mètods comuns per treballar amb dades NTi.
 * @author Limit Tecnologies <limit@limit.es>
 *
 */
@Component
public class NtiHelper {

	public void omplirOrigen(
			Model model) {
		List<ParellaCodiValor> tdlist = new ArrayList<ParellaCodiValor>();
		for(NtiOrigenEnumDto or: NtiOrigenEnumDto.values())
			tdlist.add(new ParellaCodiValor(
					or.name(),
					MessageHelper.getInstance().getMessage("nti.document.origen." + or.name())));		
		model.addAttribute("ntiOrigen", tdlist);
	}

	public void omplirEstadoElaboracion(
			Model model) {
		List<ParellaCodiValor> tdlist = new ArrayList<ParellaCodiValor>();
		for(NtiEstadoElaboracionEnumDto ee: NtiEstadoElaboracionEnumDto.values())
			tdlist.add(new ParellaCodiValor(
					ee.name(),
					MessageHelper.getInstance().getMessage("nti.document.estado.elaboracion." + ee.name())));		
		model.addAttribute("ntiEstadoElaboracion", tdlist);
	}

	public void omplirTipoDocumental(
			Model model) {
		List<ParellaCodiValor> tdlist = new ArrayList<ParellaCodiValor>();
		for(NtiTipoDocumentalEnumDto td: NtiTipoDocumentalEnumDto.values())
			tdlist.add(new ParellaCodiValor(
					td.name(),
					MessageHelper.getInstance().getMessage("nti.document.tipo.documental." + td.name())));		
		model.addAttribute("ntiTipoDocumental", tdlist);
	}

	public void omplirTipoFirma(
			Model model) {
		List<ParellaCodiValor> tdlist = new ArrayList<ParellaCodiValor>();
		for(NtiTipoFirmaEnumDto tf : NtiTipoFirmaEnumDto.values())
			tdlist.add(new ParellaCodiValor(
					tf.name(),
					MessageHelper.getInstance().getMessage("nti.tipo.firma." + tf.name())));
		model.addAttribute("ntiTipoFirma", tdlist);
	}

}
