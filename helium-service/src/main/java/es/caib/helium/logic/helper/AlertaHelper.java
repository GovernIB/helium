/**
 * 
 */
package es.caib.helium.logic.helper;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import es.caib.helium.persist.entity.Alerta;
import es.caib.helium.persist.entity.Entorn;
import es.caib.helium.persist.entity.Expedient;
import es.caib.helium.persist.repository.AlertaRepository;

/**
 * Helper per a enviament de correus
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class AlertaHelper {

    @Resource
    private AlertaRepository alertaRepository;

    /** Mètode per crear una alerta associada a un expedient
     * 
     * @param entorn
     * @param expedient
     * @param data
     * @param usuariCodi Pot ser null.
     * @param text
     * 
     * @return Retorna l'alerta creada.
     */
    @Transactional
    public Alerta crearAlerta(
    		Entorn entorn, 
    		Expedient expedient, 
    		Date data, 
    		String usuariCodi, 
    		String text) {
        Alerta alerta = new Alerta();
        alerta.setEntorn(entorn);
        alerta.setExpedient(expedient);
        alerta.setDataCreacio(data);
        alerta.setDestinatari(usuariCodi);
        alerta.setText(text);
        alertaRepository.save(alerta);
        return alerta;
    } 
}
