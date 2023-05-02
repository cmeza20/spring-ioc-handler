package com.cmeza.spring.ioc.handler.contracts.consumers;

import com.cmeza.spring.ioc.handler.contracts.consumers.enums.ConsumerType;
import com.cmeza.spring.ioc.handler.metadata.ClassMetadata;
import com.cmeza.spring.ioc.handler.metadata.MethodMetadata;
import com.cmeza.spring.ioc.handler.metadata.ParameterMetadata;

@FunctionalInterface
public interface ParameterConsumer extends AbstractConsumer {

    void accept(ClassMetadata classMetadata, MethodMetadata methodMetadata, ParameterMetadata parameterMetadata, int index);

    @Override
    default ConsumerType getType() {
        return ConsumerType.PARAMETER;
    }
}
