package es.caib.helium.client.tramitacio.v1;

import java.util.ArrayList;
import java.util.List;

public class QueryParams {
	private final List<String> params = new ArrayList<>();

	public static QueryParams newQueryParams() {
		return new QueryParams();
	}
	public QueryParams add(String key, Object value) {
		if(value == null)
			return this;
		params.add(key + "=" + value);
		return this;
	}
	public String build() {
		return "?" + String.join("&", params);
	}
}
