package com.cmeza.spring.ioc.handler.factory;

import com.cmeza.spring.ioc.handler.handlers.IocInvocationHandler;
import com.cmeza.spring.ioc.handler.handlers.IocMethodInterceptor;
import com.cmeza.spring.ioc.handler.handlers.IocTarget;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public interface IocInvocationHandlerFactory {

    InvocationHandler create(IocTarget<?> target, Map<Method, IocInvocationHandlerFactory.MethodHandler> methodHandlerMap, List<IocMethodInterceptor<?>> methodInterceptors);

    interface MethodHandler {
        Object invoke(Object[] args) throws Throwable;
    }

    final class Default implements IocInvocationHandlerFactory {
        public InvocationHandler create(IocTarget<?> target, Map<Method, MethodHandler> dispatch, List<IocMethodInterceptor<?>> methodInterceptors) {
            return new IocInvocationHandler(target, dispatch, methodInterceptors);
        }
    }
}
