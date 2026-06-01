package es.caib.helium.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * Controlador que exposa la documentació de la API REST.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/")
public class ApiRestController {

	@GetMapping
	public String documentacio(HttpServletRequest request) {
		return "restDoc";
	}

}
