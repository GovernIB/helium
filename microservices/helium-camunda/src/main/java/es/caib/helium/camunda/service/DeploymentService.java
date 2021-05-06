package es.caib.helium.camunda.service;

import es.caib.helium.camunda.model.Fitxer;
import es.caib.helium.camunda.model.WDeployment;

import javax.ws.rs.core.MultivaluedMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface DeploymentService {

    /*
     * Deployment:
     *
     * getId()
     * 		Camunda: deploymentId
     * 		Jpmb: processDefinitionId
     * getKey()
     * getVersion()
     * getProcessDefinitions()
     * 		A Camunda un desplegament pot incloure diferents definicions de procés,
     * 		així que hem substituït la cridada getProcessDefinition() utilitzada per a
     * 		obtenir les tasques de la definició de procés desplegada, per aquesta,
     * 		que retorna una llista
     *
     * 		S'han modificat els mètodes de deploy, ja que ara poden generar vàries
     * 		definicions de procés.
     */

    /**
     * Desplega un model BPMN2.0
     *
     * @param deploymentName
     * @param tenantId
     * @param fitxer
     * @return
     */
    public WDeployment desplegar(
            String deploymentName,
            String tenantId,
            Fitxer fitxer);

//    /**
//     * Desplega un model BPMN2.0 o DMN1.1 des del Camunda Modeler
//     *
//     * @param deploymentName
//     * @param enableDuplicateFiltering
//     * @param deploymentSource
//     * @param tenantId
//     * @param fitxers
//     * @return
//     */
//    public DeploymentWithDefinitionsDto desplegarModeler(
//            String deploymentName,
//            boolean enableDuplicateFiltering,
//            String deploymentSource,
//            String tenantId,
//            List<Fitxer> fitxers);

    public List<WDeployment> getDesplegaments(
            MultivaluedMap<String, String> queryParams,
            Integer firstResult,
            Integer maxResults);

    // Afegim el següent mètode per a compatibilitat amb Camunda, on un desplegament pot
    // incloure diverses definicions de procés.
    /**
     * Obté les dades d'un desplegament concret
     *
     * @param deploymentId
     * @return
     */
    public WDeployment getDesplegament(String deploymentId);

    /**
     * Elimina un desplegament concret
     *
     * @param deploymentId
     */
    public void esborrarDesplegament(String deploymentId);


    /**
     * Obté els noms dels recursos desplegats en un desplegament concret
     *
     * @param deploymentId
     * @return
     */
    public Set<String> getResourceNames(String deploymentId);

    /**
     * Obté el contingut d'un recurs d'un desplegament concret. El recurs s'identifica amb el nom
     *
     * @param deploymentId
     * @param resourceName
     * @return
     */
    public byte[] getResourceBytes(
            String deploymentId,
            String resourceName);

    /**
     * Actualitza els recursos de tipus acció, sense canviar la versió d'un desplagament
     *
     * @param deploymentId
     * @param handlers
     */
    public void updateDeploymentActions(
            Long deploymentId,
            Map<String,
                    byte[]> handlers);

}
