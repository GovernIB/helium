package net.conselldemallorca.helium.integracio.plugins.persones;

public class SearchStatus {
	public static final int RESULT_OK = 0;

	public static final int RESULT_CLIENT_ERROR = -1;

	public static final int RESULT_SERVER_ERROR = -2;

	public static final int RESULT_PARTIAL_STRING_NULL_OR_EMPTY = -3;

	public static final int RESULT_PARTIAL_STRING_TOO_SHORT = -4;

	public static final int RESULT_TOO_MANY_RESULTS_MATCH = -5;

	protected int resultCode;

	protected String resultMessage;

	public SearchStatus() {
	}

	public SearchStatus(int resultCode) {
		super();
		this.resultCode = resultCode;
	}

	public SearchStatus(int resultCode, String resultMessage) {
		super();
		this.resultCode = resultCode;
		this.resultMessage = resultMessage;
	}

	public int getResultCode() {
		return resultCode;
	}

	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}

	public String getResultMessage() {
		return resultMessage;
	}

	public void setResultMessage(String resultMessage) {
		this.resultMessage = resultMessage;
	}

	public static String toString(SearchStatus ss) {
		switch (ss.resultCode) {
		case RESULT_OK:
			return "OK";
		case RESULT_CLIENT_ERROR:
			return "Client error" + (ss.getResultMessage() != null ? ": " + ss.getResultMessage() : "");
		case RESULT_SERVER_ERROR:
			return "Server error" + (ss.getResultMessage() != null ? ": " + ss.getResultMessage() : "");
		case RESULT_PARTIAL_STRING_NULL_OR_EMPTY:
			return "Partial string null or empty";
		case RESULT_PARTIAL_STRING_TOO_SHORT:
			return "Partial string too short";
		case RESULT_TOO_MANY_RESULTS_MATCH:
			return "Too many results match" + (ss.getResultMessage() != null ? ": " + ss.getResultMessage() : "");
		default:
			return "Unknown error code[" + ss.resultCode + "]: "
					+ (ss.getResultMessage() != null ? ": " + ss.getResultMessage() : "");
		}
	}
}
