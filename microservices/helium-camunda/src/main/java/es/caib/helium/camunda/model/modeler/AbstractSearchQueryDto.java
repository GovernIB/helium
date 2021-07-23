package es.caib.helium.camunda.model.modeler;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AbstractSearchQueryDto {
    protected ObjectMapper objectMapper;

    public AbstractSearchQueryDto() {}

    public AbstractSearchQueryDto(ObjectMapper objectMapper, MultivaluedMap<String, String> queryParameters) {
        this.objectMapper = objectMapper;
        for (Map.Entry<String, List<String>> param : (Iterable<Map.Entry<String, List<String>>>)queryParameters.entrySet()) {
            String key = param.getKey();
            String value = ((List<String>)param.getValue()).iterator().next();
            setValueBasedOnAnnotation(key, value);
        }
    }

    @JsonIgnore
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    protected void setValueBasedOnAnnotation(String key, String value) {
        List<Method> matchingMethods = findMatchingAnnotatedMethods(key);
        for (Method method : matchingMethods) {
            Class<? extends JacksonAwareStringToTypeConverter<?>> converterClass = findAnnotatedTypeConverter(method);
            if (converterClass == null)
                continue;
            JacksonAwareStringToTypeConverter<?> converter = null;
            try {
                converter = converterClass.newInstance();
                converter.setObjectMapper(this.objectMapper);
                Object convertedValue = converter.convertQueryParameterToType(value);
                method.invoke(this, new Object[] { convertedValue });
            } catch (InstantiationException e) {
                throw new RestException(Response.Status.INTERNAL_SERVER_ERROR, e, "Server error.");
            } catch (IllegalAccessException e) {
                throw new RestException(Response.Status.INTERNAL_SERVER_ERROR, e, "Server error.");
            } catch (InvocationTargetException e) {
                throw new InvalidRequestException(Response.Status.BAD_REQUEST, e, "Cannot set query parameter '" + key + "' to value '" + value + "'");
            } catch (RestException e) {
                throw new InvalidRequestException(e.getStatus(), e, "Cannot set query parameter '" + key + "' to value '" + value + "': " + e
                        .getMessage());
            }
        }
    }

    private List<Method> findMatchingAnnotatedMethods(String parameterName) {
        List<Method> result = new ArrayList<>();
        Method[] methods = getClass().getMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            Annotation[] methodAnnotations = method.getAnnotations();
            for (int j = 0; j < methodAnnotations.length; j++) {
                Annotation annotation = methodAnnotations[j];
                if (annotation instanceof CamundaQueryParam) {
                    CamundaQueryParam parameterAnnotation = (CamundaQueryParam)annotation;
                    if (parameterAnnotation.value().equals(parameterName))
                        result.add(method);
                }
            }
        }
        return result;
    }

    private Class<? extends JacksonAwareStringToTypeConverter<?>> findAnnotatedTypeConverter(Method method) {
        Annotation[] methodAnnotations = method.getAnnotations();
        for (int j = 0; j < methodAnnotations.length; j++) {
            Annotation annotation = methodAnnotations[j];
            if (annotation instanceof CamundaQueryParam) {
                CamundaQueryParam parameterAnnotation = (CamundaQueryParam)annotation;
                return parameterAnnotation.converter();
            }
        }
        return null;
    }
}

