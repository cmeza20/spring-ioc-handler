package com.cmeza.spring.ioc.handler.metadata;

import java.util.List;

public interface ClassMetadata extends Metadata {
    Class<?> getTargetClass();

    List<MethodMetadata> getMethodsMetadata();

}
