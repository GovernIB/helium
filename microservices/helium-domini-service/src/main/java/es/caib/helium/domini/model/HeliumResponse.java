package es.caib.helium.domini.model;

import java.sql.Date;

public class HeliumResponse<T> {

	T contingut;
	
	Date timestamp;
	String error;
	String message;
	String path;
}
