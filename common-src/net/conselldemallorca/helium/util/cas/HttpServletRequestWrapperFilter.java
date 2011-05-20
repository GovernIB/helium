/**
 * 
 */
package net.conselldemallorca.helium.util.cas;

import java.io.IOException;
import java.security.Principal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.cas.client.authentication.SimplePrincipal;
import org.jasig.cas.client.util.AbstractCasFilter;
import org.jasig.cas.client.validation.Assertion;


/**
 * Filtre integració CAS.
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class HttpServletRequestWrapperFilter implements Filter {

	private static final String SCOPE_ATTRIBUTE_ROLES = "HEL_SESSION_ROLES";

	public void destroy() {
	}

	public void doFilter(
			ServletRequest servletRequest,
			ServletResponse servletResponse,
			FilterChain filterChain) throws IOException, ServletException {
		Principal principal = retrievePrincipalFromSessionOrRequest(servletRequest);
		String[] roles = retrieveRolesFromSessionOrRequest(servletRequest);
		if (roles == null) {
			roles = getAdditionalRoles(principal.getName());
			logger.info(MessageFormat.format("Para el usuario {0} tenemos los siguientes roles: {1}", new Object[] { principal.getName(), roles.toString() }));
			HttpServletRequest request = (HttpServletRequest)servletRequest;
			HttpSession session = request.getSession(false);
			if (session != null)
				session.setAttribute(SCOPE_ATTRIBUTE_ROLES, roles);
		}
		filterChain.doFilter(
				new CasHttpServletRequestWrapper(
						(HttpServletRequest)servletRequest,
						principal,
						roles), 
				servletResponse);
	}

	public void init(FilterConfig filterConfig) throws ServletException {
	}

	public String[] getAdditionalRoles(String userName) {
    	Connection conn = getConnection();
    	if (conn != null) {
    		List<String> roles = new ArrayList<String>();
    		PreparedStatement ps = null;
    		ResultSet rs = null;
    		try {
    			ps = conn.prepareStatement(
    					"select permis from hel_usuari_permis where codi=?");
    			ps.setString(1, userName);
    			rs = ps.executeQuery();
    			while (rs.next()) {
    				String role = rs.getString(1);
    				roles.add(role);
    			}
    		} catch (Exception ex) {
    			logger.error("Error en la consulta de rols", ex);
    		} finally {
    			try {
    				if (rs != null)
    					rs.close();
    				if (ps != null)
    					ps.close();
    				if (conn != null)
    					conn.close();
    			} catch (Exception ex) {
    				logger.error("Error al alliberar recursos de la connexió", ex);
    			}
    		}
    		return roles.toArray(new String[roles.size()]);
    	} else {
    		return null;
    	}
    }

	String[] retrieveRolesFromSessionOrRequest(ServletRequest servletRequest) {
		HttpServletRequest request = (HttpServletRequest)servletRequest;
		HttpSession session = request.getSession(false);
		String[] roles = (String[])((session == null) ? request.getAttribute(SCOPE_ATTRIBUTE_ROLES) : session.getAttribute(SCOPE_ATTRIBUTE_ROLES));
		return roles;
	}

	protected Principal retrievePrincipalFromSessionOrRequest(ServletRequest servletRequest) {
		final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpSession session = request.getSession(false);
        final Assertion assertion = (Assertion) (session == null ? request.getAttribute(AbstractCasFilter.CONST_CAS_ASSERTION) : session.getAttribute(AbstractCasFilter.CONST_CAS_ASSERTION));
        return assertion == null ? null : assertion.getPrincipal();
	}

	final class CasHttpServletRequestWrapper extends HttpServletRequestWrapper {
		private final Principal principal;
		private final String[] roles;

		CasHttpServletRequestWrapper(HttpServletRequest paramHttpServletRequest, Principal paramPrincipal, String[] paramArrayOfString) {
			super(paramHttpServletRequest);
			this.principal = new SimplePrincipal(paramPrincipal.getName());
			this.roles = paramArrayOfString;
		}
		public Principal getUserPrincipal() {
			return this.principal;
		}

		public String getRemoteUser() {
			return getUserPrincipal().getName();
		}
		public boolean isUserInRole(String rol) {
			for (int i = 0; i < this.roles.length; ++i) {
				if (rol.equals(this.roles[i]))
					return true;
			}
			return false;
		}
	}

	private Connection getConnection() {
		Connection connection = null;
		try {
			InitialContext initialContext = new InitialContext();
			Context context = (Context)initialContext.lookup("java:comp/env");
			DataSource dataSource = (DataSource)context.lookup("jdbc/HeliumDS");
			connection = dataSource.getConnection();
		} catch (NamingException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return connection;
	}

	private static final Log logger = LogFactory.getLog(HttpServletRequestWrapperFilter.class);

}
