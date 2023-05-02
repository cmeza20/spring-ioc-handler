package com.cmeza.spring.ioc.handler.builders;

import com.cmeza.spring.ioc.handler.contracts.IocContract;
import com.cmeza.spring.ioc.handler.factory.IocInvocationHandlerFactory;
import com.cmeza.spring.ioc.handler.handlers.IocMethodInterceptor;
import com.cmeza.spring.ioc.handler.handlers.IocParseHandlersByName;
import com.cmeza.spring.ioc.handler.handlers.IocSynchronousMethodHandler;
import com.cmeza.spring.ioc.handler.handlers.IocTarget;
import com.cmeza.spring.ioc.handler.processors.IocProcessors;
import lombok.Builder;

import java.util.List;

public abstract class Ioc {
    public static final int IOC_ORDER = 100;

    public abstract <T> T newInstance(IocTarget<T> target, IocContract<?> contract);

    @Builder(toBuilder = true, builderClassName = "Builder")
    public static class Factory {
        private IocContract<?> contract;
        private IocProcessors processors;
        private IocInvocationHandlerFactory invocationHandlerFactory;
        private List<IocMethodInterceptor<?>> methodInterceptors;

        public <T> T target(IocTarget<T> target) {
            return this.build().newInstance(target, contract);
        }

        public Ioc build() {
            IocSynchronousMethodHandler.Factory synchronousMethodHandlerFactory = new IocSynchronousMethodHandler.Factory(contract, processors);
            IocParseHandlersByName handlersByName = new IocParseHandlersByName(this.contract, synchronousMethodHandlerFactory, methodInterceptors);
            return new IocReflective(handlersByName, this.invocationHandlerFactory, this.methodInterceptors);
        }
    }
}
