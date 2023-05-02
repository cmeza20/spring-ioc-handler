package com.cmeza.spring.ioc.handler.metadata.impl;

import com.cmeza.spring.ioc.handler.metadata.ClassMetadata;
import com.cmeza.spring.ioc.handler.metadata.MethodMetadata;
import lombok.Getter;

import java.util.LinkedList;
import java.util.List;

@Getter
public class SimpleClassMetadata extends AbstractMetadata implements ClassMetadata {

    private final Class<?> targetClass;
    private final List<MethodMetadata> methodsMetadata = new LinkedList<>();

    public SimpleClassMetadata(Class<?> targetClass) {
        this.targetClass = targetClass;
        this.setName(targetClass.getName());
    }

    public SimpleClassMetadata addMethodMetadata(MethodMetadata methodMetadata) {
        this.methodsMetadata.add(methodMetadata);
        return this;
    }

    public boolean existsMethodMetadata(MethodMetadata methodMetadata) {
        return methodsMetadata.stream().anyMatch(m -> m.getConfigKey().equals(methodMetadata.getConfigKey()));
    }
}
