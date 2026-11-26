package es.caib.helium.flowable;

import java.util.ArrayList;
import java.util.Map;

import org.flowable.common.engine.api.repository.EngineDeployment;
import org.flowable.common.engine.api.repository.EngineResource;
import org.flowable.common.engine.impl.EngineDeployer;
import org.flowable.engine.impl.persistence.entity.ResourceEntity;
import org.flowable.engine.impl.persistence.entity.ResourceEntityImpl;
import org.flowable.engine.impl.bpmn.deployer.ResourceNameUtil;

/**
 * Deployer de Flowable que s'executa abans del {@code BpmnDeployer} i tradueix els atributs
 * d'extensió de Helium als de Flowable (veure {@link HeliumBpmnXmlAdaptador}).
 *
 * <p>No modifica el recurs desplegat: el substitueix, només en memòria, per una còpia adaptada que
 * és la que llegeix el {@code BpmnDeployer}. El recurs original (BPMN 2.0 genèric) és el que es
 * desa a la base de dades i el que retorna {@code getXml} per a l'editor. Flowable torna a executar
 * els deployers quan ha de recarregar una definició que no és a la memòria cau, així que la
 * traducció s'aplica sempre.</p>
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public class HeliumBpmnPreDeployer implements EngineDeployer {

	@Override
	public void deploy(EngineDeployment deployment, Map<String, Object> deploymentSettings) {
		Map<String, EngineResource> resources = deployment.getResources();
		if (resources == null) {
			return;
		}
		for (Map.Entry<String, EngineResource> entry : new ArrayList<>(resources.entrySet())) {
			if (!esBpmn(entry.getKey()) || entry.getValue() == null) {
				continue;
			}
			byte[] original = entry.getValue().getBytes();
			byte[] adaptat = HeliumBpmnXmlAdaptador.adaptar(original);
			if (adaptat != original) {
				resources.put(entry.getKey(), copia(entry.getValue(), adaptat));
			}
		}
	}

	private static boolean esBpmn(String nom) {
		if (nom == null) {
			return false;
		}
		for (String sufix : ResourceNameUtil.BPMN_RESOURCE_SUFFIXES) {
			if (nom.endsWith(sufix)) {
				return true;
			}
		}
		return false;
	}

	/** Còpia transitòria del recurs (no es desa mai) amb el contingut adaptat. */
	private static ResourceEntity copia(EngineResource original, byte[] bytes) {
		ResourceEntityImpl copia = new ResourceEntityImpl();
		copia.setName(original.getName());
		copia.setBytes(bytes);
		copia.setDeploymentId(original.getDeploymentId());
		copia.setGenerated(original.isGenerated());
		if (original instanceof ResourceEntity) {
			copia.setId(((ResourceEntity) original).getId());
		}
		return copia;
	}

}
