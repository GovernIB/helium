package net.conselldemallorca.helium.v3.core.api.dto.comanda;

public enum DiaSetmanaEnum {
	DL, DM, DC, DJ, DV, DS, DG;

	public static DiaSetmanaEnum valueOf(int dayOfWeek) {
		switch (dayOfWeek) {
		case 2:
			return DL;
		case 3:
			return DM;
		case 4:
			return DC;
		case 5:
			return DJ;
		case 6:
			return DV;
		case 7:
			return DS;
		case 1:
			return DG;
		default:
			return null;
		}
	}
}
