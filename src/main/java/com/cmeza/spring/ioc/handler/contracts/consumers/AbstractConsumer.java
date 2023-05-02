package com.cmeza.spring.ioc.handler.contracts.consumers;

import com.cmeza.spring.ioc.handler.contracts.consumers.enums.ConsumerType;

public interface AbstractConsumer {
    ConsumerType getType();
}
