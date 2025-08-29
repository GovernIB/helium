package net.conselldemallorca.helium.v3.core.api.dto.salut;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class IntegracioInfo {
	private String codi;
	private String nom;

	// Custom builder
	public static class IntegracioInfoBuilder {
		public IntegracioInfoBuilder integracioApp(IntegracioApp app) {
			this.codi = app.name();
			this.nom = app.getNom();
			return this;
		}
	}
}
