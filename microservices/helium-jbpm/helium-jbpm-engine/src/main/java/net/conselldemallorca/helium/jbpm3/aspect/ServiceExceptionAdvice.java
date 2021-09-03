package net.conselldemallorca.helium.jbpm3.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.conselldemallorca.helium.api.exception.HeliumJbpmException;

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
        } catch (RuntimeException e) {
            log.error("S'ha produït una excepció no controlada: " + e.getClass() + " :" + e.getMessage(), e);
            throw new HeliumJbpmException(e);
        }
    }

}
