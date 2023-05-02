package com.cmeza.spring.ioc.handler.metadata.impl;

import com.cmeza.spring.ioc.handler.metadata.AnnotationMetadata;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.annotation.Annotation;

@Getter
@RequiredArgsConstructor
public class SimpleAnnotationMetadata<A extends Annotation> extends AbstractMetadata implements AnnotationMetadata<A> {

    private final A annotation;
    private final int index;

}
