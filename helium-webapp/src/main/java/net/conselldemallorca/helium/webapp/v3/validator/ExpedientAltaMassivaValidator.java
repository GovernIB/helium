package net.conselldemallorca.helium.webapp.v3.validator;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;

import net.conselldemallorca.helium.core.util.CsvHelper;
import net.conselldemallorca.helium.v3.core.api.dto.CampDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExecucioMassivaListDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.service.DissenyService;
import net.conselldemallorca.helium.v3.core.api.service.ExecucioMassivaService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientTipusService;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientAltaMassivaCommand;
import net.conselldemallorca.helium.webapp.v3.helper.MessageHelper;

/**
 * Validador per a la comanda d'alta massiva d'expedients per CSV.
 */
public class ExpedientAltaMassivaValidator implements ConstraintValidator<ExpedientAltaMassiva, ExpedientAltaMassivaCommand>{

	private String codiMissatge;
	
	@Autowired
	ExpedientTipusService expedientTipusService;
	@Autowired
	ExecucioMassivaService execucioMassivaService;
	@Autowired
	DissenyService dissenyService;
	
	/** Màxim de 1000 expedients per alta programada. */
	private int MAX_EXPEDIENTS = 1000; 

	/** Màxim de 20 expedients per alta sense paràmetre de programació, a partir de 20 s'ha de programar després de les 15h. */
	private int MAX_EXPEDIENTS_SENSE_PROGRAMACIO = 20;
	private int PROGRAMACIO_HORA_MINIMA_HH = 15; 
	private int PROGRAMACIO_HORA_MINIMA_MM = 30; 

	@Override
	public void initialize(ExpedientAltaMassiva anotacio) {
		codiMissatge = anotacio.message();
	}

	@Override
	public boolean isValid(ExpedientAltaMassivaCommand command, ConstraintValidatorContext context) {
		boolean valid = true;
		
		String[][] contingutCsv = null;
		try {
			// Valida que no hi hagi ja una alta massiva en progrés
			if (command.getExpedientTipusId() != null) {
				ExecucioMassivaListDto darreraExecucioMassiva = execucioMassivaService.getDarreraAltaMassiva(command.getExpedientTipusId());
				if (darreraExecucioMassiva != null && darreraExecucioMassiva.getDataFi() == null) {
					context.buildConstraintViolationWithTemplate(
							MessageHelper.getInstance().getMessage( this.codiMissatge + ".alta.en.proces", new Object[]{
									new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(darreraExecucioMassiva.getDataInici()),
									darreraExecucioMassiva.getUsuari(),
									darreraExecucioMassiva.getTotal()
							}))
					.addNode("expedientTipusId")
					.addConstraintViolation();
					valid = false;
				}
			}
			// Valila el format de la data
			Date dataInici = null;
			if (!StringUtils.isEmpty(command.getDataInici())) {
				try {
					dataInici = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(command.getDataInici());
				} catch (Exception e) {
					context.buildConstraintViolationWithTemplate(
							MessageHelper.getInstance().getMessage( this.codiMissatge + ".dataInci.format.invalid"))
					.addNode("dataInici")
					.addConstraintViolation();
					valid = false;
				}
			}
			// Valida l'arxiu
			if (command.getFile().getBytes() == null || command.getFile().getBytes().length == 0) {
				context.buildConstraintViolationWithTemplate(
						MessageHelper.getInstance().getMessage( this.codiMissatge + ".arxiu.buit"))
				.addNode("file")
				.addConstraintViolation();
				valid = false;
			} else {
				// Valida l'Arxiu
				try {
					CsvHelper csvHelper = new CsvHelper();
					contingutCsv = csvHelper.parse(command.getFile().getBytes());
					if (contingutCsv.length > 1) {
						// Valida el número de columnes
						int nColumnes = contingutCsv[0].length;
						if (nColumnes < 3) {
							// El CSV ha de tenir com a mínim les 3 primeres columnes
							context.buildConstraintViolationWithTemplate(
									MessageHelper.getInstance().getMessage( this.codiMissatge + ".arxiu.minim.columnes"))
							.addNode("file")
							.addConstraintViolation();
							valid = false;							
						} else {
							// Valida la primera fila
							Set<String> camps = this.getCamps(command.getExpedientTipusId());
							List<String> columnesInvalides = new ArrayList<String>();
							for (int i = 3; i < nColumnes; i++) {
								if (!camps.contains(contingutCsv[0][i]))
									columnesInvalides.add(contingutCsv[0][i]);
							}
							if (columnesInvalides.size() > 0) {
								// El CSV conté columnes que no estan definides com a camps de l'expedient
								context.buildConstraintViolationWithTemplate(
										MessageHelper.getInstance().getMessage( this.codiMissatge + ".arxiu.columnes.invalides", new Object[] {StringUtils.join(columnesInvalides.toArray())}))
								.addNode("file")
								.addConstraintViolation();
								valid = false;							
							}
							
							// Valida que totes les files tinguin el mateix número de columnes.
							for (int i= 0; i < contingutCsv.length; i++) {
								if (contingutCsv[i].length != nColumnes) {
									// Totes les files del CSV han de tenir el mateix número de columnes
									context.buildConstraintViolationWithTemplate(
											MessageHelper.getInstance().getMessage( this.codiMissatge + ".arxiu.minim.columnes"))
									.addNode("file")
									.addConstraintViolation();
									valid = false;							
									break;
								}
							}
						}
						// Valida que no se superin el màxim de registres per alta programada
						if (contingutCsv.length > MAX_EXPEDIENTS) {
							// No es poden donar d''alta més de {0} expedients per alta massiva.
							context.buildConstraintViolationWithTemplate(
									MessageHelper.getInstance().getMessage( this.codiMissatge + ".arxiu.max.registres", new Object[] {MAX_EXPEDIENTS}))
							.addNode("file")
							.addConstraintViolation();
							valid = false;							
						}
						if (contingutCsv.length > MAX_EXPEDIENTS_SENSE_PROGRAMACIO) {
							Calendar horaLimit = new GregorianCalendar();
							horaLimit.setTime(DateUtils.truncate(dataInici != null ? dataInici : new Date(), Calendar.DATE));
							horaLimit.set(Calendar.HOUR_OF_DAY, PROGRAMACIO_HORA_MINIMA_HH);
							horaLimit.set(Calendar.MINUTE, PROGRAMACIO_HORA_MINIMA_MM);
							if (dataInici == null || dataInici.before(horaLimit.getTime()) ) {
								context.buildConstraintViolationWithTemplate(
										MessageHelper.getInstance().getMessage( 
												this.codiMissatge + ".arxiu.max.registres.no.programats", 
												new Object[] {MAX_EXPEDIENTS_SENSE_PROGRAMACIO, PROGRAMACIO_HORA_MINIMA_HH, PROGRAMACIO_HORA_MINIMA_MM}))
								.addNode("dataInici")
								.addConstraintViolation();
								valid = false;
							}
						}
					} else {
						// L'arxiu no té cap fila
						context.buildConstraintViolationWithTemplate(
								MessageHelper.getInstance().getMessage( this.codiMissatge + ".arxiu.buit"))
						.addNode("file")
						.addConstraintViolation();
						valid = false;						
					}
				} catch(Exception e) {
					context.buildConstraintViolationWithTemplate(
							MessageHelper.getInstance().getMessage( this.codiMissatge + ".arxiu.error", new Object[] {e.getMessage()}))
					.addNode("file")
					.addConstraintViolation();
					valid = false;
				}
			}
		} catch (IOException e) {
			context.buildConstraintViolationWithTemplate(
					MessageHelper.getInstance().getMessage( this.codiMissatge + ".error.lectura"))
			.addNode("file")
			.addConstraintViolation();
			valid = false;
		}
		if (!valid)
			context.disableDefaultConstraintViolation();
		else
			// Es posa el contingut parsejat per no haver de tornar a processar l'arxiu CSV
			command.setContingutCsv(contingutCsv);
		
		return valid;
	}

	/** Mètode per obtenir el conjunt de codis de camps per un tipus d'expedient.
	 * 
	 * @param expedientTipusId
	 * @return
	 */
	private Set<String> getCamps(Long expedientTipusId) {
		Set<String> camps = new HashSet<String>();
		ExpedientTipusDto expedientTipus = expedientTipusService.findAmbId(expedientTipusId);
		Long definicioProcesId = expedientTipus.isAmbInfoPropia() ? 
				null 
				: dissenyService.findDarreraDefinicioProcesForExpedientTipus(expedientTipusId).getId();
		for (CampDto camp : dissenyService.findCampsOrdenatsPerCodi(
																expedientTipusId, 
																definicioProcesId, 
																expedientTipus.isAmbHerencia())) {
			camps.add(camp.getCodi());
		}
		return camps;
	}

}
