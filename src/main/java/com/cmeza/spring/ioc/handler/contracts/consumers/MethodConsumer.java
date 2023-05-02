package com.cmeza.spring.ioc.handler.contracts.consumers;

import com.cmeza.spring.ioc.handler.contracts.consumers.enums.ConsumerType;
import com.cmeza.spring.ioc.handler.metadata.ClassMetadata;
import com.cmeza.spring.ioc.handler.metadata.MethodMetadata;

@FunctionalInterface
public interface MethodConsumer extends AbstractConsumer {
    void accept(ClassMetadata classMetadata, MethodMetadata methodMetadata);

    @Override
    default ConsumerType getType() {
        return ConsumerType.METHOD;
    }
}
