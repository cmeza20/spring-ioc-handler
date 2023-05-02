package com.cmeza.spring.ioc.handler.contracts.consumers.manager;

import com.cmeza.spring.ioc.handler.contracts.consumers.ClassConsumer;
import com.cmeza.spring.ioc.handler.contracts.consumers.MethodConsumer;
import com.cmeza.spring.ioc.handler.contracts.consumers.ParameterConsumer;
import com.cmeza.spring.ioc.handler.contracts.consumers.enums.ConsumerLocation;

public interface ConsumerManager {
    ConsumerManager addClassConsumer(ConsumerLocation consumerLocation, ClassConsumer initClassConsumer);

    ConsumerManager addMethodConsumer(ConsumerLocation consumerLocation, MethodConsumer methodConsumer);

    ConsumerManager addParameterConsumer(ConsumerLocation consumerLocation, ParameterConsumer parameterConsumer);
}
