package es.caib.helium.client.engine.variableInstance;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class VariableInstanceClientImpl implements VariableInstanceClient {

	private final String missatgeLog = "Cridant Engine Service - VariableInstance - ";

	private final VariableInstanceFeignClient variableInstanceClient;
}
