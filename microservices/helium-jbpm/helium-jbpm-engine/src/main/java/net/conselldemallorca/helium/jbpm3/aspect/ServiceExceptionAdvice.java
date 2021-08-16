package net.conselldemallorca.helium.jbpm3.aspect;

import lombok.extern.slf4j.Slf4j;
import net.conselldemallorca.helium.api.exception.HeliumJbpmException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Order(0)
@Component
public class ServiceExceptionAdvice {

    @Around("within(net.conselldemallorca.helium.jbpm3.service.*) || " +
            "execution(* net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper.*(..))")
    public Object swallowRuntimeException(ProceedingJoinPoint pjp) throws Throwable {
        try {
            return pjp.proceed();
        } catch (RuntimeException exception) {
            log.error("S'ha produït una excepció no controlada", exception);
            throw new HeliumJbpmException(HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getMessage());
        }
    }

}
