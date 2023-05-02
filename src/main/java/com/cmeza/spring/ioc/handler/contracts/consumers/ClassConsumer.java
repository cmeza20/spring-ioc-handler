package com.cmeza.spring.ioc.handler.contracts.consumers;

import com.cmeza.spring.ioc.handler.contracts.consumers.enums.ConsumerType;
import com.cmeza.spring.ioc.handler.metadata.ClassMetadata;

@FunctionalInterface
public interface ClassConsumer extends AbstractConsumer {
    void accept(ClassMetadata classMetadata);

    @Override
    default ConsumerType getType() {
        return ConsumerType.CLASS;
    }
}
