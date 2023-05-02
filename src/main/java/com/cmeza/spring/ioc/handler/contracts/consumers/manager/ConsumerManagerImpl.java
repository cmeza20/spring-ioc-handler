package com.cmeza.spring.ioc.handler.contracts.consumers.manager;

import com.cmeza.spring.ioc.handler.contracts.consumers.ClassConsumer;
import com.cmeza.spring.ioc.handler.contracts.consumers.MethodConsumer;
import com.cmeza.spring.ioc.handler.contracts.consumers.ParameterConsumer;
import com.cmeza.spring.ioc.handler.contracts.consumers.enums.ConsumerLocation;

import java.util.*;

public class ConsumerManagerImpl implements ConsumerManager {

    private final Map<ConsumerLocation, List<ClassConsumer>> classConsumers = new LinkedHashMap<>();
    private final Map<ConsumerLocation, List<MethodConsumer>> methodConsumers = new LinkedHashMap<>();
    private final Map<ConsumerLocation, List<ParameterConsumer>> parameterConsumers = new LinkedHashMap<>();

    @Override
    public ConsumerManager addClassConsumer(ConsumerLocation consumerLocation, ClassConsumer initClassConsumer) {
        this.addConsumer(consumerLocation, classConsumers, initClassConsumer);
        return this;
    }

    @Override
    public ConsumerManager addMethodConsumer(ConsumerLocation consumerLocation, MethodConsumer methodConsumer) {
        this.addConsumer(consumerLocation, methodConsumers, methodConsumer);
        return this;
    }

    @Override
    public ConsumerManager addParameterConsumer(ConsumerLocation consumerLocation, ParameterConsumer parameterConsumer) {
        this.addConsumer(consumerLocation, parameterConsumers, parameterConsumer);
        return this;
    }

    public List<ClassConsumer> getClassConsumers(ConsumerLocation consumerLocation) {
        List<ClassConsumer> consumers = classConsumers.get(consumerLocation);
        return Objects.nonNull(consumers) ? consumers : Collections.emptyList();
    }

    public List<MethodConsumer> getMethodConsumers(ConsumerLocation consumerLocation) {
        List<MethodConsumer> consumers = methodConsumers.get(consumerLocation);
        return Objects.nonNull(consumers) ? consumers : Collections.emptyList();
    }

    public List<ParameterConsumer> getParameterConsumers(ConsumerLocation consumerLocation) {
        List<ParameterConsumer> consumers = parameterConsumers.get(consumerLocation);
        return Objects.nonNull(consumers) ? consumers : Collections.emptyList();
    }

    private <T> void addConsumer(ConsumerLocation consumerLocation, Map<ConsumerLocation, List<T>> consumerList, T consumer) {
        List<T> consumers = consumerList.get(consumerLocation);
        if (Objects.isNull(consumers)) {
            consumers = new ArrayList<>();
        }
        consumers.add(consumer);
        consumerList.put(consumerLocation, consumers);
    }
}
