/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controlador per a la pàgina inicial (index).
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/index")
public class IndexV3Controller {

	@RequestMapping(method = RequestMethod.GET)
	public String get(HttpServletRequest request) {
		return "v3/index";
	}

}
