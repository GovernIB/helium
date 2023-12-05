/**
 * 
 */
package net.conselldemallorca.helium.core.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import net.conselldemallorca.helium.core.model.hibernate.Procediment;
import net.conselldemallorca.helium.core.model.hibernate.UnitatOrganitzativa;
import net.conselldemallorca.helium.integracio.plugins.procediment.UnitatAdministrativa;
import net.conselldemallorca.helium.v3.core.api.dto.procediment.ProcedimentEstatEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.procediment.ProgresActualitzacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.procediment.ProgresActualitzacioDto.ActualitzacioInfo;
import net.conselldemallorca.helium.v3.core.api.dto.procediment.ProgresActualitzacioDto.NivellInfo;
import net.conselldemallorca.helium.v3.core.repository.ProcedimentRepository;
import net.conselldemallorca.helium.v3.core.repository.UnitatOrganitzativaRepository;

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

	@Resource
	private PluginHelper pluginHelper;

	/** Consutla la llista de procediments de BBDD i marca com a extingits els que no hagi retornat la consulta a Rolsac.
	 * 
	 * @param procedimentsRolsacMap Map amb els procediments de Rolsac.
	 * @param progres Objecte per a informar el progrés.
	 */
	@Transactional( propagation = Propagation.REQUIRES_NEW)
	public void actualtizarProcedimentsNoVigents(
			Map<String, net.conselldemallorca.helium.integracio.plugins.procediment.Procediment> procedimentsRolsacMap,
			ProgresActualitzacioDto progres) {

		ActualitzacioInfo info = progres.new ActualitzacioInfo();
		info.setTitol("Actualització de procediments no vigents");

		// Consulta els procediments vigents
		List<Procediment> procedimentsVigents = procedimentRepository.findAllByEstat(ProcedimentEstatEnumDto.VIGENT);
		progres.addInfo("Actualment a la BBDD hi ha " + procedimentsVigents.size() + " procediments vigents.");
	
		List<String> procedimentsExtingits = new ArrayList<String>();
		for (Procediment p : procedimentsVigents) {
			if (!procedimentsRolsacMap.containsKey(p.getCodi())) {
				p.setEstat(ProcedimentEstatEnumDto.EXTINGIT);
				procedimentsExtingits.add(p.getCodiSia() + " - " + p.getNom());
				progres.incExtingits();
			}
		}
		if (progres.getNExtingits() > 0) {
			info.setText("S'han marcat com a extingits " + progres.getNExtingits() + " procediments");
			info.setLinies(procedimentsExtingits);
		} else {
			info.setText("No s'ha marcat cap procediment com a extingit");
		}
		info.setText(info.getText() + " dels " + procedimentsVigents.size() + " procediments que estaven vigents.");
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
			net.conselldemallorca.helium.integracio.plugins.procediment.Procediment procedimentRolsac, 
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
				procediment = net.conselldemallorca.helium.core.model.hibernate.Procediment.getBuilder(
						procedimentRolsac.getCodi(),
						procedimentRolsac.getNom(),
						procedimentRolsac.getCodiSia(),
						ProcedimentEstatEnumDto.VIGENT,
						procedimentRolsac.isComu(),
						unitatOrganitzativa).built();
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
				if (!unitatOrganitzativa.getId().equals(procediment.getUnitatOrganitzativa().getId())) {
					campsActualtizats.add("Unitat organitzativa: \"" + procediment.getUnitatOrganitzativa().getCodiAndNom() + "\" -> \"" +
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
			net.conselldemallorca.helium.integracio.plugins.procediment.Procediment procedimentRolsac, 
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
					//TODO: no posar l'arrel fixa
					progres.getAvisos().add("Error, no s'ha pogut trobar la unitat administrativa amb codi " + codi + " pel procediment " + procedimentRolsac.getCodiSia() +
							". Es posarà com a unitat organitzativa l'unitat arrel amb codi " + "A04003003");
					uo = unitatOrganitzativaRepository.findByCodi("A04003003");
					progres.incAvisos();
				}
				unitatsOranitzatives.put(procedimentRolsac.getUnitatAdministrativacodi(), uo);				
			} else {
				uo = unitatsOranitzatives.get(procedimentRolsac.getUnitatAdministrativacodi());
			}
		}
		return uo;
	}
}