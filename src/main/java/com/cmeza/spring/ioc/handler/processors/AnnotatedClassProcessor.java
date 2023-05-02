package com.cmeza.spring.ioc.handler.processors;

import com.cmeza.spring.ioc.handler.metadata.AnnotationMetadata;
import com.cmeza.spring.ioc.handler.metadata.ClassMetadata;

import java.lang.annotation.Annotation;

public interface AnnotatedClassProcessor<A extends Annotation> extends AnnotatedProcessor<A> {
    A process(AnnotationMetadata<A> annotationMetadata, ClassMetadata classMetadata);
}
