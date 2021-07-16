package es.caib.helium.dada.repository;

import java.util.List;

public interface DadaRepositoryCustom {

    List<Long> getExpedientIdByProcesIds(List<String> procesIds);
}
