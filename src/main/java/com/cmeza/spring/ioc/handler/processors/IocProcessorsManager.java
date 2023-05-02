package com.cmeza.spring.ioc.handler.processors;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class IocProcessorsManager implements IocProcessors {

    private Map<Class<? extends Annotation>, AnnotatedClassProcessor<?>> classProcessors = new ConcurrentHashMap<>();
    private Map<Class<? extends Annotation>, AnnotatedMethodProcessor<?>> methodProcessors = new ConcurrentHashMap<>();
    private Map<Class<? extends Annotation>, AnnotatedParameterProcessor<?>> parameterProcessors = new ConcurrentHashMap<>();

    @Override
    public IocProcessorsManager setAnnotatedClassProcessors(List<AnnotatedClassProcessor<? extends Annotation>> annotatedClassProcessors) {
        if (Objects.nonNull(annotatedClassProcessors)) {
            this.classProcessors = annotatedClassProcessors.stream().collect(Collectors.toMap(AnnotatedClassProcessor::getAnnotationType, processor -> processor));
        }
        return this;
    }

    @Override
    public IocProcessorsManager setAnnotatedMethodProcessors(List<AnnotatedMethodProcessor<? extends Annotation>> annotatedMethodProcessors) {
        if (Objects.nonNull(annotatedMethodProcessors)) {
            this.methodProcessors = annotatedMethodProcessors.stream().collect(Collectors.toMap(AnnotatedMethodProcessor::getAnnotationType, processor -> processor));
        }
        return this;
    }

    @Override
    public IocProcessorsManager setAnnotatedParameterProcessors(List<AnnotatedParameterProcessor<? extends Annotation>> annotatedParameterProcessors) {
        if (Objects.nonNull(annotatedParameterProcessors)) {
            this.parameterProcessors = annotatedParameterProcessors.stream().collect(Collectors.toMap(AnnotatedParameterProcessor::getAnnotationType, processor -> processor));
        }
        return this;
    }

    @Override
    public <A extends Annotation> IocProcessorsManager addAnnotatedClassProcessor(AnnotatedClassProcessor<A> annotatedClassProcessor) {
        classProcessors.put(annotatedClassProcessor.getAnnotationType(), annotatedClassProcessor);
        return this;
    }

    @Override
    public <A extends Annotation> IocProcessorsManager addAnnotatedMethodProcessor(AnnotatedMethodProcessor<A> annotatedMethodProcessor) {
        methodProcessors.put(annotatedMethodProcessor.getAnnotationType(), annotatedMethodProcessor);
        return this;
    }

    @Override
    public <A extends Annotation> IocProcessorsManager addAnnotatedParameterProcessor(AnnotatedParameterProcessor<A> annotatedParameterProcessor) {
        parameterProcessors.put(annotatedParameterProcessor.getAnnotationType(), annotatedParameterProcessor);
        return this;
    }

    @Override
    public List<AnnotatedClassProcessor<? extends Annotation>> getAnnotatedClassProcessors() {
        return new ArrayList<>(classProcessors.values());
    }

    @Override
    public List<AnnotatedMethodProcessor<? extends Annotation>> getAnnotatedMethodProcessors() {
        return new ArrayList<>(methodProcessors.values());
    }

    @Override
    public List<AnnotatedParameterProcessor<? extends Annotation>> getAnnotatedParameterProcessors() {
        return new ArrayList<>(parameterProcessors.values());
    }

    @Override
    public IocProcessorsManager clearAnnotatedClassProcessors() {
        classProcessors.clear();
        return this;
    }

    @Override
    public IocProcessorsManager clearAnnotatedMethodProcessors() {
        methodProcessors.clear();
        return this;
    }

    @Override
    public IocProcessorsManager clearAnnotatedParameterProcessors() {
        parameterProcessors.clear();
        return this;
    }

    @Override
    public Optional<AnnotatedClassProcessor<? extends Annotation>> getAnnotatedClassProcessor(Class<? extends Annotation> annotationType) {
        return Optional.ofNullable(classProcessors.get(annotationType));
    }

    @Override
    public Optional<AnnotatedMethodProcessor<? extends Annotation>> getAnnotatedMethodProcessor(Class<? extends Annotation> annotationType) {
        return Optional.ofNullable(methodProcessors.get(annotationType));
    }

    @Override
    public Optional<AnnotatedParameterProcessor<? extends Annotation>> getAnnotatedParameterProcessor(Class<? extends Annotation> annotationType) {
        return Optional.ofNullable(parameterProcessors.get(annotationType));
    }

}
