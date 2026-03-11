package es.caib.helium.logic.intf.dto.engine;

import java.util.Map;
import java.util.zip.ZipInputStream;

import org.flowable.engine.repository.ProcessDefinition;

public class WProcessDefinitionImpl implements WProcessDefinition {

	public WProcessDefinitionImpl(WProcessDefinition processDefinition) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getDeploymentId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getVersion() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getCategory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WProcessDefinition getProcessDefinition() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, byte[]> getFiles() {
		// TODO Auto-generated method stub
		return null;
	}

	public static WProcessDefinition parse(ZipInputStream zipInputStream) {
		// TODO Auto-generated method stub
		return null;
	}

	public static ProcessDefinition parseParZipInputStream(ZipInputStream zipInputStream) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WFileDefinition getFileDefinition() {
		// TODO Auto-generated method stub
		return null;
	}

}
