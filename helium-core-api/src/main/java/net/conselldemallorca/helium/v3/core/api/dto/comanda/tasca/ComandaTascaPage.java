package net.conselldemallorca.helium.v3.core.api.dto.comanda.tasca;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ComandaTascaPage {
	// Contingut de la pàgina
	private List<ComandaTasca> content;

	// Metadades de paginació
	private PageMetadata page;

	// Enllaços HATEOAS
	private List<Link> links;

	@Builder
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class PageMetadata {
		private long number; // número de pàgina
		private long size; // mida de pàgina
		private long totalElements;
		private long totalPages;
	}

	@Builder
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Link {
		private String rel;
		private String href;
	}
}
