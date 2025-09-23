package net.conselldemallorca.helium.v3.core.api.dto.comanda;

public enum DiaSetmanaEnum {
	DL,
	DM,
	DC,
	DJ,
	DV,
	DS,
	DG;

	public static DiaSetmanaEnum valueOfData(int dayOfWeek) {
		switch (dayOfWeek) {
			case 0: return DG;
			case 2: return DL;
			case 3: return DM;
			case 4: return DC;
			case 5: return DJ;
			case 6: return DV;
			case 7: return DS;
			default: return null;
		}
	}
}
