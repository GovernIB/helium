package net.conselldemallorca.helium.webapp.v3.controller;

import org.springframework.beans.factory.annotation.Autowired;

import net.conselldemallorca.helium.v3.core.api.service.AccioService;
import net.conselldemallorca.helium.v3.core.api.service.AplicacioService;
import net.conselldemallorca.helium.v3.core.api.service.CampService;
import net.conselldemallorca.helium.v3.core.api.service.DefinicioProcesService;
import net.conselldemallorca.helium.v3.core.api.service.DissenyService;
import net.conselldemallorca.helium.v3.core.api.service.DocumentService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientTipusService;
import net.conselldemallorca.helium.v3.core.api.service.TerminiService;
import net.conselldemallorca.helium.v3.core.api.service.UnitatOrganitzativaService;

/**
 * Controlador base per a les pestanyes del tipus d'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BaseExpedientTipusController extends BaseDissenyController {
	
	@Autowired
	protected ExpedientTipusService expedientTipusService;
	@Autowired
	protected ExpedientService expedientService;
	@Autowired
	protected AplicacioService aplicacioService;
	@Autowired
	protected DissenyService dissenyService;
	@Autowired
	protected DefinicioProcesService definicioProcesService;
	@Autowired
	protected CampService campService;
	@Autowired
	protected DocumentService documentService;
	@Autowired
	protected AccioService accioService;
	@Autowired
	protected TerminiService terminiService;
	@Autowired 
	protected UnitatOrganitzativaService unitatOrganitzativaService;
}
