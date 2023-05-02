package com.cmeza.spring.ioc.handler.metadata;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

public interface Metadata {
    String getName();

    Map<String, Object> getAttributes();

    <T> T getAttribute(String key, Class<T> clazz);

    <T> T getAttribute(String key, Class<T> clazz, T defaultValue);

    boolean hasAttribute(String key);

    Metadata addAttribute(String key, Object value);

    List<AnnotationMetadata<? extends Annotation>> getAnnotations();

    <T extends Annotation> AnnotationMetadata<T> getAnnotation(Class<T> clazz);

    Map<Class<? extends Annotation>, Annotation> getProcessorsResult();

    <T extends Annotation> T getProcessorResult(Class<T> annotationClass);

}
