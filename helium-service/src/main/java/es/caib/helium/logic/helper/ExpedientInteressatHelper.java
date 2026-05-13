/**
 *
 */
package es.caib.helium.logic.helper;

import es.caib.helium.commons.dto.InteressatDto;
import es.caib.helium.commons.exception.NoTrobatException;
import es.caib.helium.commons.exception.SistemaExternException;
import es.caib.helium.persistence.entity.Expedient;
import es.caib.helium.persistence.entity.Interessat;
import es.caib.helium.persistence.repository.InteressatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper per a gestionar els interessats dels expedients.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
@RequiredArgsConstructor
public class ExpedientInteressatHelper {

	private final PluginHelper pluginHelper;
	private final InteressatRepository interessatRepository;

	public Interessat create(Expedient expedient, InteressatDto interessat) {
		Interessat interessatEntity = new Interessat(
			interessat.getId(),
			interessat.getCodi(),//!=null?interessat.getCodi():interessat.getDocumentIdent(),
			interessat.getNom(),
			interessat.getDocumentIdent(),
			interessat.getDir3Codi(),
			interessat.getLlinatge1(),
			interessat.getLlinatge2(),
			interessat.getTipus(),
			interessat.getEmail(),
			interessat.getTelefon(),
			expedient,
			interessat.getEntregaPostal(),
			interessat.getEntregaTipus(),
			interessat.getLinia1(),
			interessat.getLinia2(),
			interessat.getCodiPostal(),
			interessat.getEntregaDeh(),
			interessat.getEntregaDehObligat(),
			interessat.getTipusDocIdent(),
			interessat.getDireccio(),
			interessat.getObservacions(),
			interessat.getEs_representant(),
			interessat.getRaoSocial(),
			interessat.getPais(),
			interessat.getProvincia(),
			interessat.getMunicipi(),
			interessat.getCanalNotif(),
			interessat.getCodiDire()
		);
		if (expedient.getInteressats()!=null)
			expedient.getInteressats().add(interessatEntity);
		else {
			List<Interessat> interessatsList = new ArrayList<Interessat>();
			interessatsList.add(interessatEntity);
			expedient.setInteressats(interessatsList);
		}
		interessatEntity.setTipusDocIdent(interessatEntity.getTipusDocIdent());
		return interessatRepository.save(interessatEntity);
	}

	public Interessat update(InteressatDto interessat) {
		Interessat interessatEntity = interessatRepository.findById(interessat.getId()).orElse(null);
		interessatEntity.setCodi(interessat.getCodi());
		interessatEntity.setRaoSocial(interessat.getRaoSocial());
		interessatEntity.setNom(interessat.getNom());
		interessatEntity.setDocumentIdent(interessat.getDocumentIdent());
		interessatEntity.setDir3Codi(interessat.getDir3Codi());
		interessatEntity.setCodiDire(interessat.getCodiDire());
		interessatEntity.setDocumentIdent(interessat.getDocumentIdent());
		interessatEntity.setLlinatge1(interessat.getLlinatge1());
		interessatEntity.setLlinatge2(interessat.getLlinatge2());
		interessatEntity.setTipus(interessat.getTipus());
		interessatEntity.setEmail(interessat.getEmail());
		interessatEntity.setTelefon(interessat.getTelefon());
		interessatEntity.setEntregaPostal(interessat.getEntregaPostal());
		interessatEntity.setEntregaTipus(interessat.getEntregaTipus());
		interessatEntity.setLinia1(interessat.getLinia1());
		interessatEntity.setLinia2(interessat.getLinia2());
		interessatEntity.setCodiPostal(interessat.getCodiPostal());
		interessatEntity.setEntregaDeh(interessat.getEntregaDeh());
		interessatEntity.setEntregaDehObligat(interessat.getEntregaDehObligat());
		interessatEntity.setObservacions(interessat.getObservacions());
		// interessatEntity.setTipusDocIdent(translateTipusDocIdentToSave(interessat.getTipusdocident()));
		interessatEntity.setTipusDocIdent(interessat.getTipusDocIdent());
		interessatEntity.setCodiDire(interessat.getCodiDire());
		interessatEntity.setDireccio(interessat.getDireccio());
		interessatEntity.setRaoSocial(interessat.getRaoSocial());
		interessatEntity.setEs_representant(interessat.getEs_representant());
		interessatEntity.setPais(interessat.getPais());
		interessatEntity.setProvincia(interessat.getProvincia());
		interessatEntity.setMunicipi(interessat.getMunicipi());
		interessatEntity.setCanalNotif(interessat.getCanalNotif());
		if (interessat.getRepresentant_id() != null) {
			interessatEntity.setRepresentant(interessatRepository.findById(interessat.getRepresentant_id()).orElse(null));
		}
		return interessatEntity;
	}

	public Interessat delete(
		Expedient expedient,
		Long interessatId) {
		Interessat interessat = comprovarInteressat(interessatId);
		List<Interessat> interessats = expedient.getInteressats();
		if (interessat.getRepresentant() != null) {
			// Si té respresentant primer el desassignam i després esborram l'interessat
			Interessat representant = comprovarInteressat(interessat.getRepresentant().getId());
			if (representant.getRepresentats() != null && !representant.getRepresentats().isEmpty()) {
				interessat.setRepresentant(null);
				if (representant.getRepresentats().size() == 1) {
					// Si aquest interessat té un representant que no representa a ningú més també l'esborrem (el representant)
					representant.getRepresentats().remove(interessat);
					interessatRepository.delete(representant);
					interessats.remove(representant);
				} else {
					representant.getRepresentats().remove(interessat);
					interessatRepository.save(representant);
				}
				interessatRepository.save(interessat);
			}
		}
		interessats.remove(interessat);
		expedient.setInteressats(interessats);
		return interessat;
	}

	public Interessat comprovarInteressat(
		Long interessatId) {
		Interessat interessat = interessatRepository.findById(interessatId).orElse(null);
		if (interessat == null) {
			throw new NoTrobatException(
				Interessat.class,
				interessatId);
		}
		return interessat;
	}

	public Interessat findByExpedientAndCodi(Expedient expedient, String codi) {
		return interessatRepository.findByCodiAndExpedient(codi, expedient);
	}

	public boolean arxiuPropagar(Expedient expedient, String interessatDocumentIdent) {
		boolean propagat = false;
		if (expedient.isArxiuActiu()) {
			try {
				pluginHelper.arxiuExpedientCrearOrActualitzar(expedient);
				propagat = true;
			} catch (SistemaExternException ex) {
				expedient.addErrorArxiu(
					"Error de sincronització amb arxiu al modificar l'interessat " +
						interessatDocumentIdent + ": " + ex.getPublicMessage());
			}
		}
		return propagat;
	}

}
