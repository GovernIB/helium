/**
 *
 */
package es.caib.helium.logic.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import es.caib.helium.commons.dto.procediment.ProcedimentEstatEnumDto;
import es.caib.helium.commons.dto.procediment.ProcedimentTipusEnumDto;
import es.caib.helium.commons.dto.procediment.ProgresActualitzacioDto;
import es.caib.helium.commons.dto.procediment.ProgresActualitzacioDto.ActualitzacioInfo;
import es.caib.helium.commons.dto.procediment.ProgresActualitzacioDto.NivellInfo;
import es.caib.helium.commons.exception.NoTrobatException;
import es.caib.helium.integracio.plugins.procediment.UnitatAdministrativa;
import es.caib.helium.logic.intf.service.ParametreService;
import es.caib.helium.persistence.entity.Parametre;
import es.caib.helium.persistence.entity.Procediment;
import es.caib.helium.persistence.entity.UnitatOrganitzativa;
import es.caib.helium.persistence.repository.ParametreRepository;
import es.caib.helium.persistence.repository.ProcedimentRepository;
import es.caib.helium.persistence.repository.UnitatOrganitzativaRepository;

/**
 * Helper per operar amb procediments.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class ProcedimentHelper {

	@Autowired
	private ProcedimentRepository procedimentRepository;
	@Autowired
	private UnitatOrganitzativaRepository unitatOrganitzativaRepository;
	@Autowired
	private ParametreRepository parametreRepository;

	@Resource
	private PluginHelper pluginHelper;

	/** Consutla la llista de procediments de BBDD i marca com a extingits els que no hagi retornat la consulta a Rolsac.
	 *
	 * @param procedimentsRolsacMap Map amb els procediments de Rolsac.
	 * @param progres Objecte per a informar el progrés.
	 */
	@Transactional( propagation = Propagation.REQUIRES_NEW)
	public void actualtizarProcedimentsNoVigents(
			Map<String, es.caib.helium.integracio.plugins.procediment.Procediment> procedimentsRolsacMap,
			ProgresActualitzacioDto progres,
			ProcedimentTipusEnumDto tipus) {

		boolean esServeis = tipus == ProcedimentTipusEnumDto.PROCEDIMENT;
		ActualitzacioInfo info = progres.new ActualitzacioInfo();
		info.setTitol("Actualització de " + (esServeis? "serveis" : "procediments") + " no vigents");

		// Consulta els procediments vigents
		List<Procediment> procedimentsVigents = procedimentRepository.findAllByEstatAndTipus(ProcedimentEstatEnumDto.VIGENT, tipus);
		progres.addInfo("Actualment a la BBDD hi ha " + procedimentsVigents.size() + (esServeis? " serveis" : " procediments") + " vigents.");

		List<String> procedimentsExtingits = new ArrayList<String>();
		for (Procediment p : procedimentsVigents) {
			if (!procedimentsRolsacMap.containsKey(p.getCodi())) {
				p.setEstat(ProcedimentEstatEnumDto.EXTINGIT);
				procedimentsExtingits.add(p.getCodiSia() + " - " + p.getNom());
				progres.incExtingits();
			}
		}
		if (progres.getNExtingits() > 0) {
			info.setText("S'han marcat com a extingits " + progres.getNExtingits() + (esServeis? " serveis" : " procediments"));
			info.setLinies(procedimentsExtingits);
		} else {
			info.setText("No s'ha marcat cap " + (esServeis? "servei" : "procediment") + " com a extingit");
		}
		info.setText(info.getText() + " dels " + procedimentsVigents.size() + (esServeis? " serveis" : " procediments") + " que estaven vigents.");
		progres.addInfo(info);
	}

	/** Mètode per tractar per separat un procediment vigent de Rolsac. Es consultarà la serva UO i es determinarà si s'ha d'actualtizar, crear o deixar tal
	 * com està.
	 *
	 * @param procedimentRolsac
	 * 			Procediment consultat a Rolsac.
	 * @param progres
	 * 			Objecte per informar del progrés.
	 * @param unitatsOrganitzatives
	 * 			Map amb les unitats organitzatives per codi Rolsac per no haver-les de consultar per cada procediment.
	 */
	@Transactional( propagation = Propagation.REQUIRES_NEW)
	public void actualitzaProcediment(
			es.caib.helium.integracio.plugins.procediment.Procediment procedimentRolsac,
			Map<String, UnitatOrganitzativa> unitatsOrganitzatives,
			ProgresActualitzacioDto progres) {

		ActualitzacioInfo info = progres.new ActualitzacioInfo();
		info.setTitol(procedimentRolsac.getCodiSia() + " - " + procedimentRolsac.getNom());

		try {
			// Determina la unitat organitzativa
			UnitatOrganitzativa unitatOrganitzativa = this.resoldreUnitatOrganitzativa(
					unitatsOrganitzatives,
					procedimentRolsac,
					progres,
					info);
			// Consulta el procediment a la BBDD
			Procediment procediment = procedimentRepository.findByCodi(procedimentRolsac.getCodi());
			if (procediment == null) {
				// Crea el nou procediment
				procediment = Procediment.getBuilder(
						procedimentRolsac.getCodi(),
						procedimentRolsac.getNom(),
						procedimentRolsac.getCodiSia(),
						ProcedimentEstatEnumDto.VIGENT,
						procedimentRolsac.isComu(),
						unitatOrganitzativa)
						.tipus(procedimentRolsac.getTipus())
						.built();
				procedimentRepository.save(procediment);
				info.setText("Nou procediment creat");
				List<String> camps = new ArrayList<String>();
				camps.add("Codi SIA: \"" + procediment.getCodiSia() + "\"");
				camps.add("Nom: \"" + procediment.getNom() + "\"");
				camps.add("Estat: \"" + ProcedimentEstatEnumDto.VIGENT + "\"");
				camps.add("Comu: \"" + procediment.isComu() + "\"");
				camps.add("Unitat organitzativa: \"" + procediment.getUnitatOrganitzativa().getCodiAndNom() + "\"");
				info.setLinies(camps);
				progres.incNous();
			} else {
				// Procediment existent. Comprova si s'ha d'actualitzar el procediment
				List<String> campsActualtizats = new ArrayList<String>();
				if (!procedimentRolsac.getCodiSia().equals(procediment.getCodiSia())) {
					campsActualtizats.add("Codi SIA: \"" + procediment.getCodiSia() + "\" -> \"" +
										procedimentRolsac.getCodiSia() + "\"");
				}
				if (!procedimentRolsac.getNom().equals(procediment.getNom())) {
					campsActualtizats.add("Nom: \"" + procediment.getNom() + "\" -> \"" +
										procedimentRolsac.getNom() + "\"");
				}
				if (!procediment.getEstat().equals(ProcedimentEstatEnumDto.VIGENT)) {
					campsActualtizats.add("Estat: \"" + ProcedimentEstatEnumDto.EXTINGIT + "\" -> \"" +
										ProcedimentEstatEnumDto.VIGENT + "\"");
				}
				if (procedimentRolsac.isComu() != procediment.isComu()) {
					campsActualtizats.add("Comu: \"" + procediment.isComu() + "\" -> \"" +
										procedimentRolsac.isComu() + "\"");
				}
				if (procediment.getUnitatOrganitzativa() == null ||
					!unitatOrganitzativa.getId().equals(procediment.getUnitatOrganitzativa().getId())) {
					String currentUO = procediment.getUnitatOrganitzativa() == null ? "N/A" : procediment.getUnitatOrganitzativa().getCodiAndNom();
					campsActualtizats.add("Unitat organitzativa: \"" + currentUO + "\" -> \"" +
							unitatOrganitzativa.getCodiAndNom() + "\"");
				}
				if (campsActualtizats.size() > 0) {
					procediment.update(
							procedimentRolsac.getCodi(),
							procedimentRolsac.getNom(),
							procedimentRolsac.getCodiSia(),
							ProcedimentEstatEnumDto.VIGENT,
							procedimentRolsac.isComu(),
							unitatOrganitzativa);
					info.setText("Procediment actualitzat correctament");
					info.setLinies(campsActualtizats);
					progres.incCanvis();
				} else {
					info.setText("Sense canvis");
				}
			}
		} catch(Exception e) {
			String errMsg = "Error actualitzant el procediment: " + e.toString();
			info.setTipus(NivellInfo.ERROR);
			info.setText(errMsg);
			progres.incErrors();
		}
		progres.addInfo(info, true);
	}


	/** Troba la unitat organitzativa de la BBDD a partir de les dades del procediment de Rolsac. Si no troba
	 * la UO amb codi SIA del procediments afegeix un avís al progrés per a que s'actualitzi l'arbre d'unitats.
	 * @param procedimentRolsac
	 * @param progres
	 * @param info
	 * @return
	 */
	private UnitatOrganitzativa resoldreUnitatOrganitzativa(
			Map<String, UnitatOrganitzativa> unitatsOranitzatives,
			es.caib.helium.integracio.plugins.procediment.Procediment procedimentRolsac,
			ProgresActualitzacioDto progres,
			ActualitzacioInfo info) {
		UnitatOrganitzativa uo = null;
		if (procedimentRolsac.getUnitatAdministrativacodi() != null) {
			if (!unitatsOranitzatives.containsKey(procedimentRolsac.getUnitatAdministrativacodi())) {
				String codiDir3 = null;
				// Cerca a rolsac el codi dir3 de la unitat organitzativa del procediment, si no en té va cercant en els pares
				UnitatAdministrativa unitatAdministrativa = null;
				String codi = procedimentRolsac.getUnitatAdministrativacodi();
				boolean error = false;
				Exception ex = null;
				String errMsg = "-";
				do {
					// Consulta de la unitat administrativa per codi a Rolsac amb 5 reintents
					int reintents = 0;
					do {
						try {
							unitatAdministrativa =
									pluginHelper.procedimentGetUnitatAdministrativa(codi);
						} catch (Exception e) {
							reintents++;
							ex = e;
							errMsg = "Error consultant la unitat organitzativa amb codi " + codi + " a Rolsac: pel procediment " +
										procedimentRolsac.getCodiSia()  + ex.toString();
							error = reintents++ >= 5;
						}
					} while (unitatAdministrativa == null && !error);
					if (error) {
						progres.getAvisos().add(errMsg);
						info.setTipus(NivellInfo.AVIS);
					}
					if (unitatAdministrativa != null) {
						if (unitatAdministrativa.getCodiDir3() != null ) {
							codiDir3 = unitatAdministrativa.getCodiDir3();
							uo = unitatOrganitzativaRepository.findByCodi(codiDir3);
							if (uo == null && unitatAdministrativa.getPareCodi() != null) {
								codi = unitatAdministrativa.getPareCodi();
							} else {
								codi = null;
							}
						} else if (unitatAdministrativa.getPareCodi() != null) {
							// Cerca el codi Dir3 en la unitat administrativa pare
							codi = unitatAdministrativa.getPareCodi();
						} else {
							codi = null;
						}
					} else {
						codi = null;
					}
				}
				while(uo == null
						&& codi != null);

				if (uo == null) {
					try {
						Parametre parametreArrel = parametreRepository.findByCodi(ParametreService.APP_CONFIGURACIO_CODI_ARREL_UO);
						if(parametreArrel==null)
							throw new NoTrobatException(Parametre.class, ParametreService.APP_CONFIGURACIO_CODI_ARREL_UO);
						String arrel = parametreArrel.getValor();
						progres.getAvisos().add("Error, no s'ha pogut trobar la unitat administrativa amb codi " + codi + " pel procediment " + procedimentRolsac.getCodiSia() +
								". Es posarà com a unitat organitzativa l'unitat arrel amb codi " + arrel);
						uo = unitatOrganitzativaRepository.findByCodi(arrel);
						progres.incAvisos();
					}catch(Exception e) {
						logger.debug("No s'ha trobat el paràmetre amb codi "+ ParametreService.APP_CONFIGURACIO_CODI_ARREL_UO );
					}

				}
				unitatsOranitzatives.put(procedimentRolsac.getUnitatAdministrativacodi(), uo);
			} else {
				uo = unitatsOranitzatives.get(procedimentRolsac.getUnitatAdministrativacodi());
			}
		}
		return uo;
	}

	private static final Logger logger = LoggerFactory.getLogger(ProcedimentHelper.class);
}
