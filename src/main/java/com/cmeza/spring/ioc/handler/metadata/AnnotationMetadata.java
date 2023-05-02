package com.cmeza.spring.ioc.handler.metadata;

import java.lang.annotation.Annotation;

public interface AnnotationMetadata<A extends Annotation> extends Metadata {
    A getAnnotation();

    int getIndex();
}
