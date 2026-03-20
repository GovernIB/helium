package es.caib.helium.back.helper;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class Alert {
	private String text;
	private String trace;
}
