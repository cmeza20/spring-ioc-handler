package com.cmeza.spring.ioc.handler.handlers;

import com.cmeza.spring.ioc.handler.contracts.IocContract;
import com.cmeza.spring.ioc.handler.factory.IocInvocationHandlerFactory;
import com.cmeza.spring.ioc.handler.metadata.ClassMetadata;
import com.cmeza.spring.ioc.handler.metadata.MethodMetadata;
import com.cmeza.spring.ioc.handler.processors.IocProcessors;
import org.springframework.util.Assert;

import java.util.Objects;

public final class IocSynchronousMethodHandler implements IocInvocationHandlerFactory.MethodHandler {
    private final ClassMetadata classMetadata;
    private final MethodMetadata methodMetadata;
    private final IocTarget<?> target;
    private final IocContract<?> contract;

    private IocSynchronousMethodHandler(IocTarget<?> target, ClassMetadata classMetadata, MethodMetadata methodMetadata, IocContract<?> contract) {
        Assert.notNull(target, "Target required");
        Assert.notNull(classMetadata, String.format("ClassMetadata for %s required", target));
        Assert.notNull(methodMetadata, String.format("MethodMetadata for %s required", target));
        this.target = target;
        this.classMetadata = classMetadata;
        this.methodMetadata = methodMetadata;
        this.contract = contract;
    }

    @Override
    public Object invoke(final Object[] argv) throws Throwable {
        if (Objects.nonNull(argv)) {
            int[] idx = {0};
            if (argv.length > 0) {
                methodMetadata.getParameterMetadata().forEach(parameterMetadata ->  {
                    int index = idx[0]++;
                    if (argv.length - 1 >= index) {
                        parameterMetadata.setValue(argv[index]);
                    }
                });
            }
        }

        return contract.execute(classMetadata, methodMetadata, Objects.nonNull(argv) ? argv : new Object[0], target);
    }

    public static class Factory {
        private final IocContract<?> contract;
        private final IocProcessors iocProcessors;

        public Factory(
                IocContract<?> contract,
                IocProcessors iocProcessors
        ) {
            this.contract = contract;
            this.iocProcessors = iocProcessors;
        }

        public IocInvocationHandlerFactory.MethodHandler create(IocTarget<?> target, ClassMetadata classMetadata, MethodMetadata methodMetadata) {
            return new IocSynchronousMethodHandler(target, classMetadata, methodMetadata, contract);
        }

        public IocProcessors getIocProcessors() {
            return iocProcessors;
        }
    }
}