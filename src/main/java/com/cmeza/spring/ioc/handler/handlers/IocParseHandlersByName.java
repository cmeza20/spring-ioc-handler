package com.cmeza.spring.ioc.handler.handlers;

import com.cmeza.spring.ioc.handler.contracts.IocContract;
import com.cmeza.spring.ioc.handler.contracts.IocContractManager;
import com.cmeza.spring.ioc.handler.factory.IocInvocationHandlerFactory;
import com.cmeza.spring.ioc.handler.metadata.ClassMetadata;
import com.cmeza.spring.ioc.handler.metadata.MethodMetadata;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class IocParseHandlersByName {
    private final IocContractManager manager;
    private final IocSynchronousMethodHandler.Factory factory;

    public IocParseHandlersByName(IocContract<?> contract, IocSynchronousMethodHandler.Factory factory, List<IocMethodInterceptor<?>> methodInterceptors) {
        this.manager = new IocContractManager(contract, factory.getIocProcessors(), methodInterceptors);
        this.factory = factory;
    }

    public Map<String, IocInvocationHandlerFactory.MethodHandler> apply(IocTarget<?> target) {
        ClassMetadata classMetadata = this.manager.parseAndValidateClassMetadata(target.getType());
        return classMetadata.getMethodsMetadata().stream()
                .collect(Collectors.toMap(MethodMetadata::getConfigKey, metadata -> this.factory.create(target, classMetadata, metadata)));
    }
}
