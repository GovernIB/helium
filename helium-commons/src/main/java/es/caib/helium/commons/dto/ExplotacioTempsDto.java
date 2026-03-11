package es.caib.helium.commons.dto;

import java.util.Date;

import es.caib.comanda.model.v1.estadistica.DiaSetmanaEnum;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ExplotacioTempsDto {
	private Long id;
	private Date data;
	private Integer anualitat;
	private Integer mes;
	private Integer trimestre;
	private Integer setmana;
	private Integer dia;
	private DiaSetmanaEnum diaSetmana;

}
