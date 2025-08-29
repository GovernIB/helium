package net.conselldemallorca.helium.v3.core.api.dto.salut;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Synchronized;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class IntegracioPeticions {
	private long totalOk;
	private long totalError;
	@Builder.Default
	private Map<String, Long> organOk = new HashMap<String, Long>();
	@Builder.Default
	private Map<String, Long> organError = new HashMap<String, Long>();

	@Synchronized
	public void addPeticioTotal(String organ) {
		totalOk++;
		if (organ != null && organ.trim().length() > 0) {
			Long count = organOk.get(organ);
			if (count == null) {
				count = 0L;
			}
			organOk.put(organ, count + 1);
		}
	}

	@Synchronized
	public void addPeticioError(String organ) {
		totalError++;
		if (organ != null && organ.trim().length() > 0) {
			Long count = organError.get(organ);
			if (count == null) {
				count = 0L;
			}
			organError.put(organ, count + 1);
		}
	}

	@Synchronized
	public void reset() {
		totalOk = 0;
		totalError = 0;
		organOk.clear();
		organError.clear();
	}
}
