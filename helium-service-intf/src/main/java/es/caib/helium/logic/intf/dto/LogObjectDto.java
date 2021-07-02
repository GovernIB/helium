package es.caib.helium.logic.intf.dto;

import java.util.ArrayList;
import java.util.List;

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
	public long getObjectId() {
		return objectId;
	}
	public void setObjectId(long objectId) {
		this.objectId = objectId;
	}
	public long getLogId() {
		return logId;
	}
	public void setLogId(long logId) {
		this.logId = logId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(long processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	public long getTokenId() {
		return tokenId;
	}
	public void setTokenId(long tokenId) {
		this.tokenId = tokenId;
	}
	public long getTaskInstanceId() {
		return taskInstanceId;
	}
	public void setTaskInstanceId(long taskInstanceId) {
		this.taskInstanceId = taskInstanceId;
	}
	public int getTipus() {
		return tipus;
	}
	public void setTipus(int tipus) {
		this.tipus = tipus;
	}
	public List<String> getAccions() {
		return accions;
	}
	public void setAccions(List<String> accions) {
		this.accions = accions;
	}
	public void addAccio(String accio) {
		accions.add(accio);
	}
	public Object getValorInicial() {
		return valorInicial;
	}
	public void setValorInicial(Object valorInicial) {
		if (this.valorInicial == null)
			this.valorInicial = valorInicial;
	}
}
