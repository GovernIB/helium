package net.conselldemallorca.helium.v3.core.api.dto;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.exception.ExceptionUtils;

public class ExcepcioLogDto implements Serializable {

	private Long index;
	private Date data = new Date();
	private Class<?> objectClass;
	private String params;
	private String message;
	private String stacktrace;
	private String peticio;

	public ExcepcioLogDto (String peticio, String params, Throwable exception) {
		
		if (exception != null) {
			this.objectClass = exception.getClass();
			if (exception.getMessage()!=null) {
				this.setMessage(exception.getMessage().substring(
								0,
								Math.min(exception.getMessage().contains("\n") ? exception.getMessage().indexOf("\n") : exception.getMessage().length(),
										1024)));
			} else {
				this.setMessage("null");
			}
			this.setStacktrace(ExceptionUtils.getStackTrace(exception));
		}
		
		this.params = params;
		this.peticio = peticio;
		this.data = Calendar.getInstance().getTime();
	}

	public Long getIndex() {
		return index;
	}
	public void setIndex(Long index) {
		this.index = index;
	}
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}
	public Class<?> getObjectClass() {
		return objectClass;
	}
	public void setObjectClass(Class<?> objectClass) {
		this.objectClass = objectClass;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getStacktrace() {
		return stacktrace;
	}
	public void setStacktrace(String stacktrace) {
		this.stacktrace = stacktrace;
	}
	public String getPeticio() {
		return peticio;
	}
	public void setPeticio(String peticio) {
		this.peticio = peticio;
	}
	public String getParams() {
		return params;
	}
	public void setParams(String params) {
		this.params = params;
	}

	private static final long serialVersionUID = -139254994389509932L;
}