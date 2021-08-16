package es.caib.helium.domini.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import java.lang.reflect.Method;

@Slf4j
public class CustomAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

    @Override
    public void handleUncaughtException(Throwable throwable, Method method, Object... obj) {

        log.info("Exception message - " + throwable.getMessage(), throwable);
        log.debug("Method name - " + method.getName());
        for (Object param : obj) {
            log.debug("Parameter value - " + param);
        }
    }

}
