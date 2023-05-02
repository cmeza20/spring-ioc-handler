package com.cmeza.spring.ioc.handler.metadata;

import java.lang.reflect.Method;
import java.util.List;

public interface MethodMetadata extends Metadata {
    Method getMethod();

    String getConfigKey();

    TypeMetadata getTypeMetadata();

    List<ParameterMetadata> getParameterMetadata();

    boolean isIntercepted();
}
