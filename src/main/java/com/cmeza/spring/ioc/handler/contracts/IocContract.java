package com.cmeza.spring.ioc.handler.contracts;

import com.cmeza.spring.ioc.handler.contracts.consumers.manager.ConsumerManager;
import com.cmeza.spring.ioc.handler.handlers.IocTarget;
import com.cmeza.spring.ioc.handler.metadata.ClassMetadata;
import com.cmeza.spring.ioc.handler.metadata.MethodMetadata;
import com.cmeza.spring.ioc.handler.processors.IocProcessors;
import com.cmeza.spring.ioc.handler.utils.IocUtil;

import java.lang.annotation.Annotation;

@SuppressWarnings("unchecked")
public interface IocContract<B extends Annotation> {

    void configure(ConsumerManager consumerManager);

    Object execute(ClassMetadata classMetadata, MethodMetadata methodMetadata, Object[] arguments, IocTarget<?> target);

    default Class<B> getAnnotationType() {
        return (Class<B>) IocUtil.getGenericInterface(this);
    }

    default void processors(IocProcessors processors) {
    }

    default boolean onlyDeclaredMethods() {
        return false;
    }

    default boolean onlyMethodDeclaredAnnotations() {
        return false;
    }

    default boolean onlyParameterDeclaredAnnotations() {
        return false;
    }

}
