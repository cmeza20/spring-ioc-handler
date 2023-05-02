package com.cmeza.spring.ioc.handler.processors;

import com.cmeza.spring.ioc.handler.utils.IocUtil;

import java.lang.annotation.Annotation;

@SuppressWarnings("unchecked")
public interface AnnotatedProcessor<A extends Annotation> {
    default Class<A> getAnnotationType() {
        return (Class<A>) IocUtil.getGenericInterface(this);
    }
}
