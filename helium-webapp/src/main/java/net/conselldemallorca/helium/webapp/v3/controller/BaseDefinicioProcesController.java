package net.conselldemallorca.helium.webapp.v3.controller;

import org.springframework.beans.factory.annotation.Autowired;

import net.conselldemallorca.helium.v3.core.api.service.AccioService;
import net.conselldemallorca.helium.v3.core.api.service.CampService;
import net.conselldemallorca.helium.v3.core.api.service.DefinicioProcesService;
import net.conselldemallorca.helium.v3.core.api.service.DissenyService;
import net.conselldemallorca.helium.v3.core.api.service.DocumentService;
import net.conselldemallorca.helium.v3.core.api.service.TerminiService;

/**
 * Controlador base per a les pestanyes de la definició de procés.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BaseDefinicioProcesController extends BaseDissenyController {
	
	@Autowired
	protected DefinicioProcesService definicioProcesService;
	@Autowired
	protected DissenyService dissenyService;
	@Autowired
	protected CampService campService;
	@Autowired
	protected DocumentService documentService;
	@Autowired
	protected AccioService accioService;
	@Autowired
	protected TerminiService terminiService;
}
