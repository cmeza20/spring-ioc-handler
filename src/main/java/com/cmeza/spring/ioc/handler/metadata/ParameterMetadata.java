package com.cmeza.spring.ioc.handler.metadata;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

public interface ParameterMetadata extends Metadata {
    Parameter getParameter();

    TypeMetadata getTypeMetadata();

    Object getValue();

    ParameterMetadata setValue(Object value);
}
