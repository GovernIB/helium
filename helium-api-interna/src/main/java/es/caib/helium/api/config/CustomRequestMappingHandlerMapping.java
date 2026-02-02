package es.caib.helium.api.config;

import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;

public class CustomRequestMappingHandlerMapping extends RequestMappingHandlerMapping {

    @Override
    protected RequestCondition<?> getCustomTypeCondition(Class<?> handlerType) {
        ApiVersion apiVersion = handlerType.getAnnotation(ApiVersion.class);
        return createCondition(apiVersion);
    }

    @Override
    protected RequestCondition<?> getCustomMethodCondition(Method method) {
        ApiVersion apiVersion = method.getAnnotation(ApiVersion.class);
        return createCondition(apiVersion);
    }

    private RequestCondition<ApiVersionRequestCondition> createCondition(ApiVersion apiVersion) {
        return apiVersion == null ? null : new ApiVersionRequestCondition(apiVersion.value());
    }
}