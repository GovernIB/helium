package es.caib.helium.back.helper;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

//@ComponentScan
@Data
@Builder
public class AnnotationData {
    String annotationName;
    Map<String, String> members;
}
