package es.caib.helium.dada.repository;


import es.caib.helium.dada.model.Dada;

import java.util.List;

public interface DadaRepositoryCustom {

    List<Long> getExpedientIdByProcesIds(List<String> procesIds);

    void updateDada(Dada dada);
}
