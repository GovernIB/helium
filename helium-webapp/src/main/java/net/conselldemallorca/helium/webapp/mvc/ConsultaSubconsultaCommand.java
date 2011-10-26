/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import net.conselldemallorca.helium.webapp.mvc.util.BaseController;

import org.springframework.stereotype.Controller;

/**
 * Command per a gestionar les subconsultes
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class ConsultaSubconsultaCommand extends BaseController {

	private Long id;
	private Long subconsultaId;



	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getSubconsultaId() {
		return subconsultaId;
	}
	public void setSubconsultaId(Long subconsultaId) {
		this.subconsultaId = subconsultaId;
	}

}
