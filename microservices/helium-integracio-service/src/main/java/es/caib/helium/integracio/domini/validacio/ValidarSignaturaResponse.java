package es.caib.helium.integracio.domini.validacio;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidarSignaturaResponse extends BaseResponse {

	private List<DadesCertificat> dadesCertificat;
}
