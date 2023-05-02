package com.cmeza.spring.ioc.handler.contracts;

import com.cmeza.spring.ioc.handler.annotations.DefaultContract;
import com.cmeza.spring.ioc.handler.contracts.consumers.manager.ConsumerManager;
import com.cmeza.spring.ioc.handler.handlers.IocTarget;
import com.cmeza.spring.ioc.handler.metadata.ClassMetadata;
import com.cmeza.spring.ioc.handler.metadata.MethodMetadata;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IocContractDefault implements IocContract<DefaultContract> {

    @Override
    public Class<DefaultContract> getAnnotationType() {
        return DefaultContract.class;
    }


    @Override
    public void configure(ConsumerManager consumerManager) {
        // default implementation ignored
    }

    @Override
    public Object execute(ClassMetadata classMetadata, MethodMetadata methodMetadata, Object[] arguments, IocTarget<?> target) {
        log.warn("Contract was not defined for {}", methodMetadata.getConfigKey());
        return null;
    }
}
