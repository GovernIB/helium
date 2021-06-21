/**
 * 
 */
package es.caib.helium.back.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controlador per a la pàgina inicial.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class IndexController {
	
	@RequestMapping(value = "/")
	public String index(
			@RequestParam(name="name", required=false, defaultValue="World") String name,
			Model model) {
		
		model.addAttribute("name", name);
		
		return "index";
	}
}
