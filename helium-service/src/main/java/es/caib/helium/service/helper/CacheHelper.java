/**
 *
 */
package es.caib.helium.service.helper;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import es.caib.helium.commons.dto.MunicipiDto;
import es.caib.helium.commons.dto.PaisDto;
import es.caib.helium.commons.dto.ProvinciaDto;
import es.caib.helium.commons.dto.TipusViaDto;

/**
 * Utilitat per a accedir a les caches. Els mètodes cacheables es
 * defineixen aquí per evitar la impossibilitat de fer funcionar
 * l'anotació @Cacheable als mètodes privats degut a limitacions
 * AOP.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class CacheHelper {


	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private DadesExternesHelper dadesExternesHelper;
	

	@Cacheable(value = "paisos")
	public List<PaisDto> findPaisos() {
		return conversioTipusHelper.convertirList(
				dadesExternesHelper.dadesExternesPaisosFindAll(),
				PaisDto.class);
	}

	@Cacheable(value = "provincies")
	public List<ProvinciaDto> findProvincies() {
		return conversioTipusHelper.convertirList(
				dadesExternesHelper.dadesExternesProvinciesFindAll(),
				ProvinciaDto.class);
	}
	
	@Cacheable(value = "provinciesPerComunitat", key="#comunitatCodi")
	public List<ProvinciaDto> findProvinciesPerComunitat(String comunitatCodi) {
		return conversioTipusHelper.convertirList(
				dadesExternesHelper.dadesExternesProvinciesFindAmbComunitat(comunitatCodi),
				ProvinciaDto.class);
	}

	@Cacheable(value = "municipisPerProvincia", key="#provinciaCodi")
	public List<MunicipiDto> findMunicipisPerProvincia(String provinciaCodi) {
		return conversioTipusHelper.convertirList(
				dadesExternesHelper.dadesExternesMunicipisFindAmbProvincia(provinciaCodi),
				MunicipiDto.class);
	}

	@Cacheable(value = "tipusVia")
	public List<TipusViaDto	> findTipusVia() {
		return conversioTipusHelper.convertirList(
				dadesExternesHelper.dadesExternesTipusViaFindAll(),
				TipusViaDto.class);
	}

}