package com.cmeza.spring.ioc.handler.contracts;

import com.cmeza.spring.ioc.handler.processors.AnnotatedClassProcessor;
import com.cmeza.spring.ioc.handler.processors.AnnotatedMethodProcessor;
import com.cmeza.spring.ioc.handler.processors.AnnotatedParameterProcessor;

import java.lang.annotation.Annotation;

public interface IocContractConfiguration {
    <A extends Annotation> IocContractConfiguration addAnnotatedClassProcessor(AnnotatedClassProcessor<A> annotatedClassProcessor);

    <A extends Annotation> IocContractConfiguration addAnnotatedMethodProcessor(AnnotatedMethodProcessor<A> annotatedMethodProcessor);

    <A extends Annotation> IocContractConfiguration addAnnotatedParameterProcessor(AnnotatedParameterProcessor<A> annotatedParameterProcessor);
}
