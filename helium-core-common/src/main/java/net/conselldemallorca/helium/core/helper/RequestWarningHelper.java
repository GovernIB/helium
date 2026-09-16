package net.conselldemallorca.helium.core.helper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

@Component
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class RequestWarningHelper {
	private final List<String> warnings = new ArrayList<String>();
	public void add(String warning) { warnings.add(warning); }
	public void add(String warning, Object ...args) {
		warnings.add(String.format(warning, args));
	}
	public List<String> getWarnings() { return warnings; }
	public void clear() { warnings.clear(); }
}
