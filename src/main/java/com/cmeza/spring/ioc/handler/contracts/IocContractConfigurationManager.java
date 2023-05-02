package com.cmeza.spring.ioc.handler.contracts;

import com.cmeza.spring.ioc.handler.processors.AnnotatedClassProcessor;
import com.cmeza.spring.ioc.handler.processors.AnnotatedMethodProcessor;
import com.cmeza.spring.ioc.handler.processors.AnnotatedParameterProcessor;

import java.lang.annotation.Annotation;

public class IocContractConfigurationManager implements IocContractConfiguration {
    @Override
    public <A extends Annotation> IocContractConfiguration addAnnotatedClassProcessor(AnnotatedClassProcessor<A> annotatedClassProcessor) {
        return null;
    }

    @Override
    public <A extends Annotation> IocContractConfiguration addAnnotatedMethodProcessor(AnnotatedMethodProcessor<A> annotatedMethodProcessor) {
        return null;
    }

    @Override
    public <A extends Annotation> IocContractConfiguration addAnnotatedParameterProcessor(AnnotatedParameterProcessor<A> annotatedParameterProcessor) {
        return null;
    }
}
