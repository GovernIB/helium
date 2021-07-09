/**
 * 
 */
package es.caib.helium.back.interceptor;

import org.springframework.web.servlet.mvc.WebContentInterceptor;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filtre per a gestionar la cache en respostes HTTP
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ResponseCachingFilter extends WebContentInterceptor implements Filter {

	public void doFilter(
			ServletRequest request,
			ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		try {
			preHandle(
					(HttpServletRequest)request,
					(HttpServletResponse)response,
					chain);
		} catch (Exception e) {
			throw new ServletException(e);
		}
		chain.doFilter(request, response);
	}

	public void init(FilterConfig config) throws ServletException {
	}

	public void destroy() {
	}

}
