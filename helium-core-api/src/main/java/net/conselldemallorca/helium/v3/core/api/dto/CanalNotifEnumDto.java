package net.conselldemallorca.helium.v3.core.api.dto;

public enum CanalNotifEnumDto {
		DIRECCION_POSTAL("01", "Direcció Postal"),
	    DIRECCION_ELECTRONICA_HABILITADA("02", "Direcció electrònica habilitada"),
	    COMPARECENCIA_ELECTRONICA("03", "Compareixença electrònica");

	    private final String value;
	    private final String name;


	    CanalNotifEnumDto(String value, String name) {
	        this.value = value;
	        this.name = name;
	    }

	    public String getValue() {
	        return value;
	    }

	    public String getName() {
	        return name;
	    }

	    public static CanalNotifEnumDto getCanalNotificacion(String value) {

	        if (value != null) {

	            for (CanalNotifEnumDto canalNotificacion : CanalNotifEnumDto.values()) {
	                if (value.equals(canalNotificacion.getValue())) return canalNotificacion;
	            }

	        }

	        return null;
	    }

	    public static String getCanalNotificacionValue(String value) {

	    	CanalNotifEnumDto canalNotificacion = getCanalNotificacion(value);

	        if (canalNotificacion != null) {

	            return canalNotificacion.getValue();
	        }

	        return null;
	    }

}
