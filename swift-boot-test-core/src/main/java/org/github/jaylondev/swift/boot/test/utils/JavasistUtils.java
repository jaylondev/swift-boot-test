package org.github.jaylondev.swift.boot.test.utils;

import javassist.*;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.AttributeInfo;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import net.bytebuddy.agent.ByteBuddyAgent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

/**
 * @author jaylon 2023/9/13 23:44
 */
public class JavasistUtils {

    public static void addAnnotation2Field(Class<?> clazz, String fieldName, Class<?> annotationClass,
                                           BiConsumer<Annotation, ConstPool> initAnnotation) {
        ClassPool pool = ClassPool.getDefault();
        CtClass ctClass;
        try {
            ClassClassPath classPath = new ClassClassPath(JavasistUtils.class);
            pool.insertClassPath(classPath);
            ctClass = pool.get(clazz.getName());
            if (ctClass.isFrozen()) {
                ctClass.defrost();
            }
            CtField ctField = getCtField(fieldName, ctClass);
            if (ctField == null) {
                return;
            }
            ConstPool constPool = ctClass.getClassFile().getConstPool();
            Annotation annotation = new Annotation(annotationClass.getName(), constPool);
            if (initAnnotation != null) {
                initAnnotation.accept(annotation, constPool);
            }
            AnnotationsAttribute attr = getAnnotationAttributeFromField(ctField);
            if (attr == null) {
                attr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
                ctField.getFieldInfo().addAttribute(attr);
            }
            attr.addAnnotation(annotation);
            retransformClass(clazz, ctClass.toBytecode());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static CtField getCtField(String fieldName, CtClass ctClass) throws NotFoundException {
        List<CtField> ctFieldsIncludeSuper = getCtFieldIncludeSuper(ctClass);
        for (CtField ctField : ctFieldsIncludeSuper) {
            if (ctField.getFieldInfo().getName().equals(fieldName)) {
                return ctField;
            }
        }
        return null;
    }

    private static List<CtField> getCtFieldIncludeSuper(CtClass ctClass) throws NotFoundException {
        CtField[] declaredFields = ctClass.getDeclaredFields();
        List<CtField> list = new ArrayList<>(Arrays.asList(declaredFields));
        CtClass supperClass = ctClass.getSuperclass();
        if (!supperClass.getSimpleName().equals(Object.class.getSimpleName())) {
            List<CtField> fields = getCtFieldIncludeSuper(supperClass);
            list.addAll(fields);
        }
        return list;
    }

    private static AnnotationsAttribute getAnnotationAttributeFromField(CtField ctField) {
        List<AttributeInfo> attrs = ctField.getFieldInfo().getAttributes();
        AnnotationsAttribute attr = null;
        if (attrs != null) {
            Optional<AttributeInfo> optional = attrs.stream()
                    .filter(AnnotationsAttribute.class::isInstance)
                    .findFirst();
            if (optional.isPresent()) {
                attr = (AnnotationsAttribute) optional.get();
            }
        }
        return attr;
    }

    private static void retransformClass(Class<?> clazz, byte[] byteCode) {
        ClassFileTransformer cft = new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                                    ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
                return byteCode;
            }
        };
        Instrumentation instrumentation = ByteBuddyAgent.install();
        try {
            instrumentation.addTransformer(cft, true);
            instrumentation.retransformClasses(clazz);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            instrumentation.removeTransformer(cft);
        }
    }

}
