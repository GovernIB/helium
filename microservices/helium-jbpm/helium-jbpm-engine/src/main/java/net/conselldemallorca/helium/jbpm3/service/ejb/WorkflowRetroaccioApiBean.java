package net.conselldemallorca.helium.jbpm3.service.ejb;

import net.conselldemallorca.helium.api.dto.ExpedientTascaDto;
import net.conselldemallorca.helium.api.dto.InformacioRetroaccioDto;
import net.conselldemallorca.helium.api.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.api.exception.NoTrobatException;
import net.conselldemallorca.helium.api.exception.PermisDenegatException;
import net.conselldemallorca.helium.api.service.WorkflowRetroaccioApi;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.SortedSet;

public class WorkflowRetroaccioApiBean implements WorkflowRetroaccioApi {

    @Autowired
    WorkflowRetroaccioApi delegate;

    @Override
    public SortedSet<Map.Entry<InstanciaProcesDto, List<InformacioRetroaccioDto>>> findInformacioRetroaccioExpedientOrdenatPerData(Long expedientId, String instanciaProcesId, boolean detall) {
        return delegate.findInformacioRetroaccioExpedientOrdenatPerData(expedientId, instanciaProcesId, detall);
    }

    @Override
    public Map<String, ExpedientTascaDto> findTasquesExpedientPerRetroaccio(Long expedientId) {
        return delegate.findTasquesExpedientPerRetroaccio(expedientId);
    }

    @Override
    public void executaRetroaccio(Long informacioRetroaccioId, boolean retrocedirPerTasques) {
        delegate.executaRetroaccio(informacioRetroaccioId, retrocedirPerTasques);
    }

    @Override
    public void eliminaInformacioRetroaccio(String processInstanceId) {
        delegate.eliminaInformacioRetroaccio(processInstanceId);
    }

    @Override
    public List<InformacioRetroaccioDto> findInformacioRetroaccioTascaOrdenatPerData(Long informacioRetroaccioId) {
        return delegate.findInformacioRetroaccioTascaOrdenatPerData(informacioRetroaccioId);
    }

    @Override
    public List<InformacioRetroaccioDto> findInformacioRetroaccioAccioRetrocesOrdenatsPerData(Long informacioRetroaccioId) {
        return delegate.findInformacioRetroaccioAccioRetrocesOrdenatsPerData(informacioRetroaccioId);
    }

    @Override
    public InformacioRetroaccioDto findInformacioRetroaccioById(Long informacioRetroaccioId) throws NoTrobatException, PermisDenegatException {
        return delegate.findInformacioRetroaccioById(informacioRetroaccioId);
    }

    @Override
    public Long afegirInformacioRetroaccioPerExpedient(boolean ambRetroaccio, String processInstanceId, String message) {
        return delegate.afegirInformacioRetroaccioPerExpedient(ambRetroaccio, processInstanceId, message);
    }

    @Override
    public Long afegirInformacioRetroaccioPerExpedient(Long expedientId, ExpedientRetroaccioTipus tipus, String accioParams) {
        return delegate.afegirInformacioRetroaccioPerExpedient(expedientId, tipus, accioParams);
    }

    @Override
    public Long afegirInformacioRetroaccioPerExpedient(Long expedientId, ExpedientRetroaccioTipus tipus, String accioParams, ExpedientRetroaccioEstat estat) {
        return delegate.afegirInformacioRetroaccioPerExpedient(expedientId, tipus, accioParams, estat);
    }

    @Override
    public Long afegirInformacioRetroaccioPerTasca(String taskInstanceId, ExpedientRetroaccioTipus tipus, String accioParams) {
        return delegate.afegirInformacioRetroaccioPerTasca(taskInstanceId, tipus, accioParams);
    }

    @Override
    public Long afegirInformacioRetroaccioPerTasca(String taskInstanceId, ExpedientRetroaccioTipus tipus, String accioParams, String user) {
        return delegate.afegirInformacioRetroaccioPerTasca(taskInstanceId, tipus, accioParams, user);
    }

    @Override
    public Long afegirInformacioRetroaccioPerTasca(String taskInstanceId, Long expedientId, ExpedientRetroaccioTipus tipus, String accioParams, String user) {
        return delegate.afegirInformacioRetroaccioPerTasca(taskInstanceId, expedientId, tipus, accioParams, user);
    }

    @Override
    public Long afegirInformacioRetroaccioPerProces(String processInstanceId, ExpedientRetroaccioTipus tipus, String accioParams) {
        return delegate.afegirInformacioRetroaccioPerProces(processInstanceId, tipus, accioParams);
    }

    @Override
    public Long afegirInformacioRetroaccioPerProces(String processInstanceId, ExpedientRetroaccioTipus tipus, String accioParams, ExpedientRetroaccioEstat estat) {
        return delegate.afegirInformacioRetroaccioPerProces(processInstanceId, tipus, accioParams, estat);
    }

    @Override
    public void actualitzaEstatInformacioRetroaccio(Long informacioRetroaccioId, ExpedientRetroaccioEstat estat) {
        delegate.actualitzaEstatInformacioRetroaccio(informacioRetroaccioId, estat);
    }

    @Override
    public void actualitzaParametresAccioInformacioRetroaccio(Long informacioRetroaccioId, String parametresAccio) {
        delegate.actualitzaParametresAccioInformacioRetroaccio(informacioRetroaccioId, parametresAccio);
    }
}
