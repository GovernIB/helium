package es.caib.helium.integracio.domini.portafirmes;

import lombok.Data;

@Data
public class PasSignatura {

	private int minSignataris = 0;
	private String[] signataris;
}