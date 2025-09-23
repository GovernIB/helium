package net.conselldemallorca.helium.webapp.v3.rest.comanda;

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

import org.springframework.beans.factory.annotation.Autowired;

import lombok.Builder;
import lombok.Getter;

public abstract class ComandaBaseController {
	
	@Autowired
	private ServletContext servletContext;
	private ManifestInfo manifestInfo;
	
	private static DateFormat isoDateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	
	protected ManifestInfo getManifestInfo() throws IOException {
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
