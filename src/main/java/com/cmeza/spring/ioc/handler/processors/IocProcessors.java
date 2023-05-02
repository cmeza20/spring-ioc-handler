package com.cmeza.spring.ioc.handler.processors;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;

public interface IocProcessors {

    IocProcessors setAnnotatedClassProcessors(List<AnnotatedClassProcessor<? extends Annotation>> annotatedClassProcessors);

    IocProcessors setAnnotatedMethodProcessors(List<AnnotatedMethodProcessor<? extends Annotation>> annotatedMethodProcessors);

    IocProcessors setAnnotatedParameterProcessors(List<AnnotatedParameterProcessor<? extends Annotation>> annotatedParameterProcessors);

    <A extends Annotation> IocProcessors addAnnotatedClassProcessor(AnnotatedClassProcessor<A> annotatedClassProcessor);

    <A extends Annotation> IocProcessors addAnnotatedMethodProcessor(AnnotatedMethodProcessor<A> annotatedMethodProcessor);

    <A extends Annotation> IocProcessors addAnnotatedParameterProcessor(AnnotatedParameterProcessor<A> annotatedParameterProcessor);

    List<AnnotatedClassProcessor<? extends Annotation>> getAnnotatedClassProcessors();

    List<AnnotatedMethodProcessor<? extends Annotation>> getAnnotatedMethodProcessors();

    List<AnnotatedParameterProcessor<? extends Annotation>> getAnnotatedParameterProcessors();

    IocProcessors clearAnnotatedClassProcessors();

    IocProcessors clearAnnotatedMethodProcessors();

    IocProcessors clearAnnotatedParameterProcessors();

    Optional<AnnotatedClassProcessor<? extends Annotation>> getAnnotatedClassProcessor(Class<? extends Annotation> annotationType);

    Optional<AnnotatedMethodProcessor<? extends Annotation>> getAnnotatedMethodProcessor(Class<? extends Annotation> annotationType);

    Optional<AnnotatedParameterProcessor<? extends Annotation>> getAnnotatedParameterProcessor(Class<? extends Annotation> annotationType);
}
