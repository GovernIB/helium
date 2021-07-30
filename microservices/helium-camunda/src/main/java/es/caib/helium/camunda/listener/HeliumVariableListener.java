package es.caib.helium.camunda.listener;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.impl.core.variable.scope.AbstractVariableScope;
import org.camunda.bpm.engine.impl.core.variable.scope.VariableInstanceLifecycleListener;
import org.camunda.bpm.engine.impl.persistence.entity.VariableInstanceEntity;

@Slf4j
public class HeliumVariableListener implements VariableInstanceLifecycleListener<VariableInstanceEntity> {

    @Override
    public void onCreate(VariableInstanceEntity variableInstanceEntity, AbstractVariableScope abstractVariableScope) {
        log.info("Creant variable");
    }

    @Override
    public void onDelete(VariableInstanceEntity variableInstanceEntity, AbstractVariableScope abstractVariableScope) {
        log.info("Borrant variable");
    }

    @Override
    public void onUpdate(VariableInstanceEntity variableInstanceEntity, AbstractVariableScope abstractVariableScope) {
        log.info("Modificant variable");
    }
}
