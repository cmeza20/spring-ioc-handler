package com.cmeza.spring.ioc.handler.processors;

import com.cmeza.spring.ioc.handler.metadata.ClassMetadata;
import com.cmeza.spring.ioc.handler.metadata.MethodMetadata;
import com.cmeza.spring.ioc.handler.metadata.ParameterMetadata;

import java.lang.reflect.Parameter;

public interface SimpleParameterProcessor {
    void process(Parameter parameter, ClassMetadata classMetadata, MethodMetadata methodMetadata, ParameterMetadata parameterMetadata, int index);
}
