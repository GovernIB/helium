package es.caib.helium.back.controller;

import org.springframework.beans.factory.annotation.Autowired;

import es.caib.helium.logic.intf.service.AccioService;
import es.caib.helium.logic.intf.service.CampService;
import es.caib.helium.logic.intf.service.DefinicioProcesService;
import es.caib.helium.logic.intf.service.DissenyService;
import es.caib.helium.logic.intf.service.DocumentService;
import es.caib.helium.logic.intf.service.TerminiService;

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
