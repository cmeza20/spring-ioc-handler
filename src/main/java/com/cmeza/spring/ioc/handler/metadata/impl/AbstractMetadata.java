package com.cmeza.spring.ioc.handler.metadata.impl;

import com.cmeza.spring.ioc.handler.metadata.AnnotationMetadata;
import com.cmeza.spring.ioc.handler.metadata.Metadata;
import lombok.Data;

import java.lang.annotation.Annotation;
import java.util.*;

@Data
@SuppressWarnings("unchecked")
public class AbstractMetadata implements Metadata {

    private final Map<String, Object> attributes = new LinkedHashMap<>();
    private final List<AnnotationMetadata<? extends Annotation>> annotations = new LinkedList<>();
    private final Map<Class<? extends Annotation>, Annotation> processorsResult = new LinkedHashMap<>();
    private String name;

    @Override
    public <T> T getAttribute(String key, Class<T> clazz) {
        return (T)attributes.get(key);
    }

    @Override
    public <T> T getAttribute(String key, Class<T> clazz, T defaultValue) {
        T result = getAttribute(key, clazz);
        return Objects.isNull(result) ? defaultValue : result;
    }

    @Override
    public boolean hasAttribute(String key) {
        return Objects.nonNull(attributes.get(key));
    }

    @Override
    public <T extends Annotation> AnnotationMetadata<T> getAnnotation(Class<T> clazz) {
        return (AnnotationMetadata<T>) annotations.stream().filter(ann -> ann.getAnnotation().annotationType().isAssignableFrom(clazz)).findAny().orElse(null);
    }


    @Override
    public <T extends Annotation> T getProcessorResult(Class<T> annotationClass) {
        return (T) processorsResult.get(annotationClass);
    }

    @Override
    public Metadata addAttribute(String key, Object value) {
        attributes.put(key, value);
        return this;
    }

    public <T extends Annotation> Metadata addAnnotation(AnnotationMetadata<T> annotationMetadata) {
        annotations.add(annotationMetadata);
        return this;
    }

    public Metadata addProcessorResult(Class<? extends Annotation> annotationType, Annotation process) {
        this.processorsResult.put(annotationType, process);
        return this;
    }
}
