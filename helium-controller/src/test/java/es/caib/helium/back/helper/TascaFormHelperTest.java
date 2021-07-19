package es.caib.helium.back.helper;

import es.caib.helium.back.validator.SpELAssert;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtNewMethod;
import javassist.Modifier;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.AnnotationMemberValue;
import javassist.bytecode.annotation.ArrayMemberValue;
import javassist.bytecode.annotation.StringMemberValue;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.beanutils.PropertyUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.ComponentScan;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;


class TascaFormHelperTest {

    @Test
    void doValidationForCglibObject() throws Exception {

        String pkgName = "es.caib.helium.back";

        ClassPool pool = ClassPool.getDefault();
        CtClass cc = pool.makeClass("TaskCommand_" + UUID.randomUUID());

        addField(pool, cc, "codi", String.class.getName());
        addField(pool, cc, "valor1", Integer.class.getName());
        addField(pool, cc, "valor2", Integer.class.getName());

        // Anotacions per validar
        ConstPool cpool = cc.getClassFile().getConstPool();
        AnnotationsAttribute attr = new AnnotationsAttribute(cpool, AnnotationsAttribute.visibleTag);

//        @SpELAssert(value = "#this > 42", message = "El valor 1 ha de ser major que 42")
//        @SpELAssert(value = "#this > 25", message = "El valor 1 ha de ser major que 25")
        addFieldAnnotations(
                cc,
                "valor1",
                List.of(
                        AnnotationData.builder()
                                .annotationName("es.caib.helium.back.validator.SpELAssert")
                                .members(Map.of(
                                                "value", "#this > 42",
                                                "message", "El valor1 ha de ser major que 42")).build(),
                        AnnotationData.builder()
                                .annotationName("es.caib.helium.back.validator.SpELAssert")
                                .members(Map.of(
                                        "value", "#this > 25",
                                        "message", "El valor1 ha de ser major que 25")).build())
        );

//        @SpELAssert(value = "valor1 > valor2", message = "El valor 1 ha de ser major que el valor 2"),
//        @SpELAssert(value = "valor2 < valor1", message = "El valor 2 ha de ser menor que el valor 1")
        addClassAnnotations(
                cc,
                List.of(
                        AnnotationData.builder()
                                .annotationName("es.caib.helium.back.validator.SpELAssert")
                                .members(Map.of(
                                        "value", "valor1 > valor2",
                                        "message", "El valor 1 ha de ser major que el valor 2")).build(),
                        AnnotationData.builder()
                                .annotationName("es.caib.helium.back.validator.SpELAssert")
                                .members(Map.of(
                                        "value", "valor2 < valor1",
                                        "message", "El valor 2 ha de ser menor que el valor 1")).build())
        );

//        ClassFilePrinter.print(cc.getClassFile());

        var taskCommand = cc.toClass().getConstructor().newInstance();
        cc.detach();

        PropertyUtils.setSimpleProperty(taskCommand, "codi", "Codi de l'objecte");
        PropertyUtils.setSimpleProperty(taskCommand, "valor1", 20);
        PropertyUtils.setSimpleProperty(taskCommand, "valor2", 60);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        var violations = validator.validate(taskCommand);

        if (!violations.isEmpty()) {
            violations.stream().forEach(v -> System.out.println(v.getMessage()));
        }

    }

    private void addField(
            ClassPool pool,
            CtClass cc,
            String name,
            String className) throws Exception {

        CtField field = new CtField(pool.get(className),name, cc);
        field.setModifiers(Modifier.PRIVATE);
        cc.addField(field);
        String capitalizedName = capitalize(name);
        cc.addMethod(CtNewMethod.setter("set" + capitalizedName, field));
        cc.addMethod(CtNewMethod.getter("get" + capitalizedName, field));
    }

    private void addFieldAnnotations(
            CtClass cc,
            String fieldName,
            List<AnnotationData> annotationData) throws Exception {

        Assertions.assertNotNull(annotationData, "No s'ha indicat cap anotació");
        Assertions.assertFalse(annotationData.isEmpty(), "No s'ha indicat cap anotació");

        CtField field = cc.getField(fieldName);
        ConstPool cpool = cc.getClassFile().getConstPool();

        AnnotationsAttribute attr = new AnnotationsAttribute(cpool, AnnotationsAttribute.visibleTag);

        if (annotationData.size() > 1) {
            Annotation rootAnnotation = new Annotation("es.caib.helium.back.validator.SpELAsserts", cpool);
            var childAnnotationsMemberList = new ArrayMemberValue(cpool);
            var childAnnotationMemberValues = new ArrayList<AnnotationMemberValue>();

            annotationData.forEach(a ->
                childAnnotationMemberValues.add(new AnnotationMemberValue(createAnnotation(cpool, a), cpool))
            );
            childAnnotationsMemberList.setValue(childAnnotationMemberValues.toArray(AnnotationMemberValue[]::new));
            rootAnnotation.addMemberValue("value", childAnnotationsMemberList);
            attr.addAnnotation(rootAnnotation);
        } else {
            AnnotationData data = annotationData.get(0);
            Annotation annotation = createAnnotation(cpool, data);
            attr.addAnnotation(annotation);
        }
        field.getFieldInfo().addAttribute(attr);
    }

    private void addClassAnnotations(
            CtClass cc,
            List<AnnotationData> annotationData) throws Exception {

        Assertions.assertNotNull(annotationData, "No s'ha indicat cap anotació");
        Assertions.assertFalse(annotationData.isEmpty(), "No s'ha indicat cap anotació");

        ConstPool cpool = cc.getClassFile().getConstPool();

        AnnotationsAttribute attr = new AnnotationsAttribute(cpool, AnnotationsAttribute.visibleTag);

        if (annotationData.size() > 1) {
            Annotation rootAnnotation = new Annotation("es.caib.helium.back.validator.SpELAsserts", cpool);
            var childAnnotationsMemberList = new ArrayMemberValue(cpool);
            var childAnnotationMemberValues = new ArrayList<AnnotationMemberValue>();

            annotationData.forEach(a ->
                    childAnnotationMemberValues.add(new AnnotationMemberValue(createAnnotation(cpool, a), cpool))
            );
            childAnnotationsMemberList.setValue(childAnnotationMemberValues.toArray(AnnotationMemberValue[]::new));
            rootAnnotation.addMemberValue("value", childAnnotationsMemberList);
            attr.addAnnotation(rootAnnotation);
        } else {
            AnnotationData data = annotationData.get(0);
            Annotation annotation = createAnnotation(cpool, data);
            attr.addAnnotation(annotation);
        }
        cc.getClassFile().addAttribute(attr);
    }

    private Annotation createAnnotation(ConstPool cpool, AnnotationData data) {
        var annotation = new Annotation(data.getAnnotationName(), cpool);
        if (data.getMembers() != null) {
            data.getMembers().entrySet().forEach(e -> annotation.addMemberValue(e.getKey(), new StringMemberValue(e.getValue(), cpool)));
        }
        return annotation;
    }

//    private void addClassAnnotations(
//            CtClass cc,
//            List<AnnotationData> annotationData) throws Exception {
//
//        ConstPool cpool = cc.getClassFile().getConstPool();
//
//        AnnotationsAttribute attr = new AnnotationsAttribute(cpool, AnnotationsAttribute.visibleTag);
//        var annotations = new ArrayList<Annotation>();
//        annotationData.forEach(a -> {
//            Annotation annotation = new Annotation(a.getAnnotationName(), cpool);
//            if (a.getMembers() != null) {
//                a.getMembers().entrySet().forEach(e -> annotation.addMemberValue(e.getKey(), new StringMemberValue(e.getValue(), cpool)));
//            }
//            annotations.add(annotation);
//        });
//        attr.setAnnotations(annotations.toArray(Annotation[]::new));
//        cc.getClassFile().addAttribute(attr);
//    }

    private String capitalize(String nom) {
        String firstLetStr = nom.substring(0, 1).toUpperCase();
        String remLetStr = nom.substring(1);
        return firstLetStr + remLetStr;
    }


@ComponentScan
    @Data
    @Builder
    public static class AnnotationData {
        String annotationName;
        Map<String, String> members;
    }













//    @Test
    void doValidationForObject() throws Exception {

        Prova obj = new Prova();
        obj.setCodi("Codi de l'objecte");
        obj.setValor1(30);
        obj.setValor2(60);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        var violations = validator.validate(obj);

        if (!violations.isEmpty()) {
            violations.stream().forEach(v -> System.out.println(v.getMessage()));
        }
    }


    @Data
    @SpELAssert.SpELAsserts(value = {
            @SpELAssert(value = "valor1 > valor2", message = "El valor 1 ha de ser major que el valor 2"),
            @SpELAssert(value = "valor2 < valor1", message = "El valor 2 ha de ser menor que el valor 1")
    })
    public static class Prova {

        String codi;
        @SpELAssert(value = "#this > 42", message = "El valor 1 ha de ser major que 42")
        Integer valor1;
        Integer valor2;

    }
}