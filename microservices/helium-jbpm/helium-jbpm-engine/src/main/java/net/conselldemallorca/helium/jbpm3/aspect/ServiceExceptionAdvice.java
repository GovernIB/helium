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
    		"within(net.conselldemallorca.helium.jbpm3.integracio.*) || " +
    		"within(net.conselldemallorca.helium.jbpm3.helper.*) || " +
    		"execution(* net.conselldemallorca.helium.jbpm3.helper.TasquesHelper.*(..)) || " + 
            "execution(* net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper.*(..))")
    public Object swallowRuntimeException(ProceedingJoinPoint pjp) throws HeliumJbpmException {
        try {
            return pjp.proceed();
        } catch (Throwable t) {
//            if (t instanceof RuntimeException || t instanceof Error) {
                log.error("S'ha produït una excepció no controlada: " + t.getClass() + " :" + t.getMessage(), t);
                throw new HeliumJbpmException(t);
//            } else {
//                throw t;
//            }
        }
    }

}
