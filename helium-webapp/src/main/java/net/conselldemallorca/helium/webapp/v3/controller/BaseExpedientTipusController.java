package net.conselldemallorca.helium.webapp.v3.controller;

import org.springframework.beans.factory.annotation.Autowired;

import es.caib.helium.logic.intf.service.AccioService;
import es.caib.helium.logic.intf.service.AplicacioService;
import es.caib.helium.logic.intf.service.CampService;
import es.caib.helium.logic.intf.service.DefinicioProcesService;
import es.caib.helium.logic.intf.service.DissenyService;
import es.caib.helium.logic.intf.service.DocumentService;
import es.caib.helium.logic.intf.service.ExpedientService;
import es.caib.helium.logic.intf.service.ExpedientTipusService;
import es.caib.helium.logic.intf.service.TerminiService;

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
}
