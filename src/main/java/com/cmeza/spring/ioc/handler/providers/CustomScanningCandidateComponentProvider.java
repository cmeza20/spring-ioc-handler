package com.cmeza.spring.ioc.handler.providers;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.env.Environment;

public class CustomScanningCandidateComponentProvider extends ClassPathScanningCandidateComponentProvider {

    public CustomScanningCandidateComponentProvider(Environment environment) {
        super(false, environment);
    }

    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return beanDefinition.getMetadata().isIndependent() && !beanDefinition.getMetadata().isAnnotation();
    }
}
