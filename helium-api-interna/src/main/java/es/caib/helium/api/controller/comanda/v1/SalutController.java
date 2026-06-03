package es.caib.helium.api.controller.comanda.v1;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import es.caib.comanda.model.server.monitoring.AppInfo;
import es.caib.comanda.model.server.monitoring.SalutInfo;
import es.caib.helium.logic.intf.service.SalutService;
import es.caib.helium.logic.intf.util.DatesUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/v1/salut")
@Tag(
	name = "Salut",
	description = "API REST de consulta de salut de Helium per mostrar a l'aplicació Comanda.")
public class SalutController {

	@Autowired
	private SalutService salutService;
	@Autowired
	private ServletContext servletContext;

	ManifestInfo manifestInfo;

	@GetMapping("")
	public SalutInfo getAppInfo(HttpServletRequest request) {
		ManifestInfo manifestInfo = getManifestInfo();
		return salutService.checkSalut(
			manifestInfo.getVersion(),
			request.getRequestURL().toString() + "Performance");
	}

	@GetMapping("/performance")
	@ResponseBody
	public Health healthCheck() {
		return Health.up().build();
	}

	@GetMapping("/info")
	public AppInfo appInfo() {
		ManifestInfo manifestInfo = getManifestInfo();
		return new AppInfo()
			.codi("HEL")
			.nom("Helium")
			.data(DatesUtils.toOffsetDateTime(manifestInfo.getBuildDate()))
			.versio(manifestInfo.getVersion())
			.revisio(manifestInfo.getBuildScmRevision())
			.jdkVersion(manifestInfo.getBuildJDK())
			.integracions(salutService.getIntegracions())
			.subsistemes(salutService.getSubsistemes())
			.contexts(salutService.getContexts());
	}

	private ManifestInfo getManifestInfo() {
		if (manifestInfo == null)
			manifestInfo = buildManifestInfo();
		return manifestInfo;
	}

	private ManifestInfo buildManifestInfo() {
		ManifestInfo manifestInfo = ManifestInfo.builder().build();
		try {
			try (InputStream is = servletContext.getResourceAsStream("/META-INF/MANIFEST.MF")) {
				if (is != null) {
					Manifest manifest = new Manifest(is);
					Attributes attributes = manifest.getMainAttributes();
					Map<String, Object> manifestAtributsMap = new HashMap<>();
					for (Map.Entry<Object, Object> entry : attributes.entrySet()) {
						manifestAtributsMap.put(entry.getKey().toString(), entry.getValue());
					}
					if (!manifestAtributsMap.isEmpty()) {
						var version = manifestAtributsMap.get("Implementation-Version");
						var buildDate = manifestAtributsMap.get("Build-Timestamp");
						var buildJDK = manifestAtributsMap.get("Build-Jdk-Spec");
						var buildScmBranch = manifestAtributsMap.get("Implementation-SCM-Branch");
						var buildScmRevision = manifestAtributsMap.get("Implementation-SCM-Revision");
						manifestInfo = ManifestInfo.builder()
							.version(version != null ? version.toString() : null)
							.buildDate(buildDate != null ? getDate(buildDate.toString()) : null)
							.buildJDK(buildJDK != null ? buildJDK.toString() : null)
							.buildScmBranch(buildScmBranch != null ? buildScmBranch.toString() : null)
							.buildScmRevision(buildScmRevision != null ? buildScmRevision.toString() : null)
							.build();
					}
				}
			}
		} catch (IOException ex) {
			log.error("Couldn't read MANIFEST.MF", ex);
		}
		return manifestInfo;
	}

	public static Date getDate(String isoDate) {
		try {
			Instant instant = Instant.parse(isoDate);
			return Date.from(instant);
		} catch (Exception e) {
			log.error("El format de la data és incorrecte", e);
		}
		return null;
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
