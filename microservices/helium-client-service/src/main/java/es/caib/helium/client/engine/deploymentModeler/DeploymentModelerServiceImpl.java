package es.caib.helium.client.engine.deploymentModeler;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeploymentModelerServiceImpl implements DeploymentModelerService {

	private final String missatgeLog = "Cridant Engine Service - DeploymentModeler - ";

	private DeploymentModelerFeignClient deploymentModelerClient;
}
