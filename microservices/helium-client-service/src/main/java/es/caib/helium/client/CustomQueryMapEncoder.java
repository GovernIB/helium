package es.caib.helium.client;

import feign.Param;
import feign.QueryMapEncoder;
import feign.codec.EncodeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class CustomQueryMapEncoder implements QueryMapEncoder {

    private final Map<Class<?>, ObjectParamMetadata> classToMetadata = new HashMap<>();

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    @Override
    public Map<String, Object> encode(Object object) {
        if (supports(object)) {
            Map<String, Object> queryMap = new HashMap<>();

            if (object instanceof Pageable) {
                Pageable pageable = (Pageable) object;

                if (pageable.isPaged()) {
                    queryMap.put("page", pageable.getPageNumber());
                    queryMap.put("size", pageable.getPageSize());
                }

                if (pageable.getSort() != null) {
                    applySort(queryMap, pageable.getSort());
                }
            }
            else if (object instanceof Sort) {
                Sort sort = (Sort) object;
                applySort(queryMap, sort);
            }
            return queryMap;
        }
        else {
            try {
                ObjectParamMetadata metadata = getMetadata(object.getClass());
                Map<String, Object> propertyNameToValue = new HashMap<String, Object>();
                for (PropertyDescriptor pd : metadata.objectProperties) {
                    Method method = pd.getReadMethod();
                    Object value = method.invoke(object);
                    if (value != null && value != object) {
                        Param alias = method.getAnnotation(Param.class);
                        String name = alias != null ? alias.value() : pd.getName();

                        // En cas de Date aplicar el format de l'anotació DateTimeFormat.
                        // Si no s'ha definit l'anotació s'utilitzarà el format "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
                        if (value instanceof Date) {
                            try {
                                Field field = object.getClass().getDeclaredField(name);
                                var dateTimeFormatAnnotation = field.getDeclaredAnnotation(DateTimeFormat.class);
                                if (dateTimeFormatAnnotation != null) {
                                    var format = dateTimeFormatAnnotation.pattern();
                                    value = new SimpleDateFormat(format).format((Date) value);
                                } else {
                                    value = simpleDateFormat.format((Date) value);
                                }
                            } catch (Exception ex) {
                                log.error("Error al obtenir format de data a convertir", ex);
                                value = simpleDateFormat.format((Date) value);
                            }
                        }
                        propertyNameToValue.put(name, value);
                    }
                }
                return propertyNameToValue;
            } catch (IllegalAccessException | IntrospectionException | InvocationTargetException e) {
                throw new EncodeException("Failure encoding object into query map", e);
            }
        }
    }

    private ObjectParamMetadata getMetadata(Class<?> objectType) throws IntrospectionException {
        ObjectParamMetadata metadata = classToMetadata.get(objectType);
        if (metadata == null) {
            metadata = ObjectParamMetadata.parseObjectType(objectType);
            classToMetadata.put(objectType, metadata);
        }
        return metadata;
    }

    private static class ObjectParamMetadata {

        private final List<PropertyDescriptor> objectProperties;

        private ObjectParamMetadata(List<PropertyDescriptor> objectProperties) {
            this.objectProperties = Collections.unmodifiableList(objectProperties);
        }

        private static ObjectParamMetadata parseObjectType(Class<?> type)
                throws IntrospectionException {
            List<PropertyDescriptor> properties = new ArrayList<PropertyDescriptor>();

            for (PropertyDescriptor pd : Introspector.getBeanInfo(type).getPropertyDescriptors()) {
                boolean isGetterMethod = pd.getReadMethod() != null && !"class".equals(pd.getName());
                if (isGetterMethod) {
                    properties.add(pd);
                }
            }

            return new ObjectParamMetadata(properties);
        }
    }

    private void applySort(Map<String, Object> queryMap, Sort sort) {
        List<String> sortQueries = new ArrayList<>();
        for (Sort.Order order : sort) {
            sortQueries.add(order.getProperty() + "%2C" + order.getDirection());
        }
        if (!sortQueries.isEmpty()) {
            queryMap.put("sort", sortQueries);
        }
    }

    private boolean supports(Object object) {
        return object instanceof Pageable || object instanceof Sort;
    }

}
