/**
 * 
 */
package es.caib.helium.back.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.caib.helium.logic.intf.service.ExempleService;

/**
 * Controlador per a la pàgina inicial.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class IndexController extends BaseController {
		
	@Autowired
	private ExempleService exempleService;

	public IndexController() {
		super();
	}

	@RequestMapping(value = "/")
	public String index(
			@RequestParam(name="name", required=false, defaultValue="World") String name,
			Model model) {
		
		model.addAttribute("name", name);
		
		return "index";
	}
	
	@RequestMapping(value = "/hola")
	public String test(
			@RequestParam(name="name", required=false, defaultValue="World") String name,
			Model model) {
		
		String text = exempleService.hola(name);
		model.addAttribute("name", text);
		
		return "index";
	}

}
