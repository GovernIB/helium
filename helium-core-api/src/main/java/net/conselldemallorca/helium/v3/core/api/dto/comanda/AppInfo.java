package net.conselldemallorca.helium.v3.core.api.dto.comanda;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AppInfo {
	private String codi;
	private String nom;
	private String versio;
	private Date data;
	private String revisio;
	private String jdkVersion;
	private List<IntegracioInfo> integracions;
	private List<AppInfo> subsistemes;
	private List<ContextInfo> contexts;
}
