package com.cmeza.spring.ioc.handler.builders;

import com.cmeza.spring.ioc.handler.contracts.IocContract;
import com.cmeza.spring.ioc.handler.factory.IocInvocationHandlerFactory;
import com.cmeza.spring.ioc.handler.handlers.*;
import com.cmeza.spring.ioc.handler.utils.IocUtil;
import lombok.AllArgsConstructor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
public class IocReflective extends Ioc {
    private final IocParseHandlersByName targetToHandlersByName;
    private final IocInvocationHandlerFactory factory;
    private final List<IocMethodInterceptor<?>> methodInterceptors;

    @Override
    @SuppressWarnings("unchecked")
    public <T> T newInstance(IocTarget<T> target, IocContract<?> contract) {
        Map<String, IocInvocationHandlerFactory.MethodHandler> nameToHandler = this.targetToHandlersByName.apply(target);
        List<IocDefaultMethodHandler> defaultMethodHandlers = new LinkedList<>();

        Map<Method, IocInvocationHandlerFactory.MethodHandler> methodToHandler = Arrays.stream(contract.onlyDeclaredMethods() ? target.getType().getDeclaredMethods() : target.getType().getMethods())
                .map(method -> {
                    Map.Entry<Method, IocInvocationHandlerFactory.MethodHandler> entry = null;
                    if (method.getDeclaringClass() != Object.class) {
                        if (IocUtil.isDefault(method)) {
                            IocDefaultMethodHandler handler = new IocDefaultMethodHandler(method);
                            defaultMethodHandlers.add(handler);
                            entry = new AbstractMap.SimpleEntry<>(method, handler);
                        } else {
                            entry = new AbstractMap.SimpleEntry<>(method, nameToHandler.get(IocUtil.configKey(target.getType(), method)));
                        }
                    }
                    return entry;
                }).filter(Objects::nonNull)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        InvocationHandler handler = this.factory.create(target, methodToHandler, methodInterceptors);
        final T proxy = (T) Proxy.newProxyInstance(target.getType().getClassLoader(), new Class[]{target.getType()}, handler);

        defaultMethodHandlers.forEach(dmh -> dmh.bindTo(proxy));

        return proxy;
    }
}
