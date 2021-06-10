package es.caib.helium.monitor.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping(MonitorController.API_PATH)
public class MonitorController {

    public static final String API_PATH = "/api/v1/monitor";

}
