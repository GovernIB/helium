package es.caib.helium.handlers;

import net.conselldemallorca.helium.jbpm3.api.HeliumActionHandler;
import net.conselldemallorca.helium.jbpm3.api.HeliumApi;
import net.conselldemallorca.helium.jbpm3.handlers.exception.HeliumHandlerException;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.ActionInfo;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.ExpedientInfo;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.ProcessInstanceInfo;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.TimerInfo;

import java.util.List;

public class ArchetypeExampleApiHandler extends HeliumActionHandler {

    // Variables a configurar a la acció
    private String archetypeVariableMsg = "";
    private String archetypeVariableVarCodi = "";

    public void setArchetypeVariableMsg(String archetypeVariableMsg) {
        this.archetypeVariableMsg = archetypeVariableMsg;
    }
    public void setArchetypeVariableVarCodi(String archetypeVariableVarCodi) {
        this.archetypeVariableVarCodi = archetypeVariableVarCodi;
    }

    // Codi a executar per la acció
    @Override
    public void execute(HeliumApi api) throws HeliumHandlerException {
        try {
            ProcessInstanceInfo pi = api.getProcessInstance();
            System.out.println(">>> ProcessInstance: " + pi);

            Object val = null;

            // Variables
            System.out.println(">>> SetVariable " + archetypeVariableVarCodi + ": val01");
            api.setVariable(archetypeVariableVarCodi, "cod 02");
            val = api.getVariable(archetypeVariableVarCodi);
            System.out.println(">>> GetVariable: " + archetypeVariableVarCodi + ": " + val);

            // Expedient
            ExpedientInfo exp = api.getExpedient();
            System.out.println(">>> GetExpedient: " + exp.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void retrocedir(HeliumApi heliumApi, List<String> list) throws Exception {
        // Mètode no utilitzat en tipus d'expedient per estat
        throw new RuntimeException("Mètode no utilitzable en expedients per estat");
    }

}