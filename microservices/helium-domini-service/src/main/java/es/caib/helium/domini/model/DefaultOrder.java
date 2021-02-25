package es.caib.helium.domini.model;

import org.springframework.data.domain.Sort;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DefaultOrder {

    public String field() default "";
    public Sort.Direction direction() default Sort.Direction.ASC;

}
