package com.cmeza.spring.ioc.handler.metadata.impl;

import com.cmeza.spring.ioc.handler.metadata.ParameterMetadata;
import com.cmeza.spring.ioc.handler.metadata.TypeMetadata;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

@Getter
@EqualsAndHashCode
public class SimpleParameterMetadata extends AbstractMetadata implements ParameterMetadata {

    private final Parameter parameter;
    private final TypeMetadata typeMetadata;
    @Setter
    private Object value;

    public SimpleParameterMetadata(Parameter parameter, Type type) {
        this.parameter = parameter;
        this.typeMetadata = new SimpleTypeMetadata(type);
        this.setName(parameter.getName());
    }
}
