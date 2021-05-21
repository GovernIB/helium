package net.conselldemallorca.helium.ms.auth;

import java.nio.charset.StandardCharsets;

import javax.xml.bind.DatatypeConverter;

import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;

/** Autenticació bàsica
 * 
 *
 */
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2021-04-15T16:51:41.639+02:00[Europe/Paris]")
public class HttpBasicAuth implements Authentication {
	private String username;
	private String password;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public void applyToParams(MultiValueMap<String, String> queryParams, HttpHeaders headerParams) {
		if (username == null && password == null) {
			return;
		}
		String str = (username == null ? "" : username) + ":" + (password == null ? "" : password);
		headerParams.add("Authorization",
				"Basic " + DatatypeConverter.printBase64Binary(str.getBytes(StandardCharsets.UTF_8)));
	}
}
