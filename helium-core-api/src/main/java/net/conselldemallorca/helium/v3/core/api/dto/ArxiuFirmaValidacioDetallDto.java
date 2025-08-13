package net.conselldemallorca.helium.v3.core.api.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ArxiuFirmaValidacioDetallDto {
	boolean valid;
	String message;
	List<ArxiuFirmaDetallDto> detalls;
}
