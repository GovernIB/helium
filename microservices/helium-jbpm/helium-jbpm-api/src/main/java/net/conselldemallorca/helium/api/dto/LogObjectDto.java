package net.conselldemallorca.helium.api.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class LogObjectDto {
	public static final int LOG_OBJECT_PROCES = 0;
	public static final int LOG_OBJECT_TOKEN = 1;
	public static final int LOG_OBJECT_TASK = 2;
	public static final int LOG_OBJECT_VARTASCA = 3;
	public static final int LOG_OBJECT_VARPROCES = 4;
	public static final int LOG_OBJECT_ACTION = 5;
	public static final int LOG_OBJECT_INFO = 6;
	public static final String LOG_ACTION_CREATE = "C";
	public static final String LOG_ACTION_UPDATE = "U";
	public static final String LOG_ACTION_DELETE = "D";
	public static final String LOG_ACTION_START = "S";
	public static final String LOG_ACTION_END = "E";
	public static final String LOG_ACTION_ASSIGN = "A";
	public static final String LOG_ACTION_EXEC = "X";

	private long objectId;
	private long logId;
	private String name;
	private long processInstanceId;
	private long tokenId;
	private long taskInstanceId;
	private int tipus;
	private List<String> accions = new ArrayList<String>();
	private Object valorInicial = null;

	public LogObjectDto(long objectId, long logId, String name, int tipus, long processInstanceId, long tokenId) {
		this.objectId = objectId;
		this.logId = logId;
		this.name = name;
		this.tipus = tipus;
		this.processInstanceId = processInstanceId;
		this.tokenId = tokenId;
	}

	public void addAccio(String accio) {
		accions.add(accio);
	}
	public void setValorInicial(Object valorInicial) {
		if (this.valorInicial == null)
			this.valorInicial = valorInicial;
	}
}
