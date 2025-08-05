package net.conselldemallorca.helium.webapp.v3.helper;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class Alert {
	private String text;
	private String trace;
}
