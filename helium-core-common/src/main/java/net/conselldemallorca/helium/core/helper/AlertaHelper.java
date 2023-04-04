/**
 * 
 */
package net.conselldemallorca.helium.core.helper;

import java.util.Date;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import net.conselldemallorca.helium.core.model.hibernate.Alerta;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.v3.core.repository.AlertaRepository;

/**
 * Helper per a enviament de correus
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class AlertaHelper {

	/** Llargada màxima del camp text. */
    private static final int MAX_TEXT = 1024;

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
        alerta.setText(StringUtils.left(text, MAX_TEXT));
        alertaRepository.save(alerta);
        return alerta;
    } 
}
