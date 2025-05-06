package net.conselldemallorca.helium.webapp.v3.command;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SuggestItemCommand {
	private String codi;
	private String nom;

}
