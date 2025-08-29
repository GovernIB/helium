package net.conselldemallorca.helium.webapp.v3.rest.SalutRest;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import lombok.Builder;
import lombok.Getter;
import net.conselldemallorca.helium.v3.core.api.dto.salut.AppInfo;
import net.conselldemallorca.helium.v3.core.api.dto.salut.EstatSalut;
import net.conselldemallorca.helium.v3.core.api.dto.salut.EstatSalutEnum;
import net.conselldemallorca.helium.v3.core.api.dto.salut.SalutInfo;
import net.conselldemallorca.helium.v3.core.api.service.SalutService;

@Controller
@RequestMapping("/rest")
public class SalutController {
	
	private static DateFormat isoDateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	
	@Autowired
	private ServletContext servletContext;
	@Autowired
	private SalutService salutService;
	private ManifestInfo manifestInfo;

	@RequestMapping(value = "/appInfo", method = RequestMethod.GET)
	@ResponseBody
	public AppInfo appInfo(HttpServletRequest request) throws IOException {
		ManifestInfo manifestInfo = getManifestInfo();
		return AppInfo.builder()
		.codi("NOT")
		.nom("Notib")
		.data(manifestInfo.getBuildDate())
		.versio(manifestInfo.getVersion())
		.revisio(manifestInfo.getBuildScmRevision())
		.jdkVersion(manifestInfo.getBuildJDK())
		.integracions(salutService.getIntegracions())
		.subsistemes(salutService.getSubsistemes())
		.contexts(salutService.getContexts(getBaseUrl(request)))
		.build();
	}
	
	public String getBaseUrl(HttpServletRequest request) {
		return ServletUriComponentsBuilder
				.fromRequestUri(request)
				.replacePath(null)
				.build()
				.toUriString();
	}
	
	@RequestMapping(value = "/salut", method = RequestMethod.GET)
	@ResponseBody
	public SalutInfo health(HttpServletRequest request) throws IOException {
		ManifestInfo manifestInfo = getManifestInfo();
		return salutService.checkSalut(manifestInfo.getVersion(), request.getRequestURL().toString() + "Performance");
	}

	@RequestMapping(value = "/salutPerformance", method = RequestMethod.GET)
	@ResponseBody
	public String healthCheck(HttpServletRequest request) {
		return "OK";
	}

	private ManifestInfo getManifestInfo() throws IOException {
		if (manifestInfo == null) {
			manifestInfo = buildManifestInfo();
		}
		return manifestInfo;
	}
	
	private ManifestInfo buildManifestInfo() throws IOException {
		ManifestInfo manifestInfo = ManifestInfo.builder().build();
		Manifest manifest = new Manifest(servletContext.getResourceAsStream("/" + JarFile.MANIFEST_NAME));
		Attributes manifestAtributs = manifest.getMainAttributes();
		Map<String, Object>manifestAtributsMap = new HashMap<String, Object>();
		for (Object key: new HashMap<Object,Object>(manifestAtributs).keySet()) {
			manifestAtributsMap.put(key.toString(), manifestAtributs.get(key));
		}
		if (!manifestAtributsMap.isEmpty()) {
			Object version = manifestAtributsMap.get("Implementation-Version");
			Object buildDate = manifestAtributsMap.get("Build-Timestamp");
			Object buildJDK = manifestAtributsMap.get("Build-Jdk-Spec");
			Object buildScmBranch = manifestAtributsMap.get("Implementation-SCM-Branch");
			Object buildScmRevision = manifestAtributsMap.get("Implementation-SCM-Revision");
			manifestInfo = ManifestInfo.builder()
				.version(version != null ? version.toString() : null)
				.buildDate(buildDate != null ? getDate(buildDate.toString()) : null)
				.buildJDK(buildJDK != null ? buildJDK.toString() : null)
				.buildScmBranch(buildScmBranch != null ? buildScmBranch.toString() : null)
				.buildScmRevision(buildScmRevision != null ? buildScmRevision.toString() : null)
				.build();
		}
		return manifestInfo;
	}
	
	public static Date getDate(String isoDate) {
		try {
			return isoDateFormatter.parse(isoDate);
		} catch (ParseException e) {
			System.out.println("El format de la data és incorrecte: " + e.getMessage());
			return null;
		}
	}

	@Builder
	@Getter
	public static class ManifestInfo {
		private final String version;
		private final Date buildDate;
		private final String buildJDK;
		private final String buildScmBranch;
		private final String buildScmRevision;
	}
	
}
