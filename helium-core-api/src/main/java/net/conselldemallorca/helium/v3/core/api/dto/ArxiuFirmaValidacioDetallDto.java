package net.conselldemallorca.helium.v3.core.api.dto;

import java.util.List;

import lombok.Data;

@Data
public class ArxiuFirmaValidacioDetallDto {
	boolean valid;
	String message;
	List<ArxiuFirmaDetallDto> detalls;
}
