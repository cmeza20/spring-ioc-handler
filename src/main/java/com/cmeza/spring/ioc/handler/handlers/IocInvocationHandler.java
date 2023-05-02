package com.cmeza.spring.ioc.handler.handlers;

import com.cmeza.spring.ioc.handler.factory.IocInvocationHandlerFactory;
import com.cmeza.spring.ioc.handler.utils.IocUtil;
import org.springframework.util.Assert;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class IocInvocationHandler implements InvocationHandler {
    private static final String EQUALS = "equals";
    private static final String HASHCODE = "hashCode";
    private static final String TOSTRING = "toString";

    private final IocTarget<?> target;
    private final Map<Method, IocInvocationHandlerFactory.MethodHandler> dispatch;
    private final List<IocMethodInterceptor<?>> methodInterceptors;

    public IocInvocationHandler(IocTarget<?> target, Map<Method, IocInvocationHandlerFactory.MethodHandler> dispatch, List<IocMethodInterceptor<?>> methodInterceptors) {
        Assert.notNull(target, "Target required");
        Assert.notNull(dispatch, String.format("Dispatch for %s required", target));
        Assert.notNull(methodInterceptors, "IocMethodInterceptors required");
        this.target = target;
        this.dispatch = dispatch;
        this.methodInterceptors = methodInterceptors;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Objects.isNull(args)) {
            args = new Object[0];
        }

        if (EQUALS.equals(method.getName())) {
            return this.equalsHandler(args);
        }

        if (HASHCODE.equals(method.getName())) {
            return this.hashCode();
        }

        if (TOSTRING.equals(method.getName())) {
            return this.toString();
        }

        for (IocMethodInterceptor<?> interceptor : methodInterceptors) {
            if (interceptor.accept(method.getReturnType())) {
                Optional<Object> invokeOptional = interceptor.invoke(proxy, this.target, method, args);
                if (invokeOptional.isPresent()) {
                    return invokeOptional.get();
                }
            }
        }

        IocInvocationHandlerFactory.MethodHandler methodHandler = this.dispatch.get(method);
        if (Objects.isNull(methodHandler)) {
            String configKey = IocUtil.configKey(method.getDeclaringClass(), method);
            throw new IllegalArgumentException(configKey + " - was not defined as a bean");
        }
        return methodHandler.invoke(args);
    }

    public boolean equals(Object obj) {
        if (obj instanceof IocInvocationHandler) {
            IocInvocationHandler other = (IocInvocationHandler) obj;
            return this.target.equals(other.target);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return this.target.hashCode();
    }

    public String toString() {
        return this.target.toString();
    }

    private boolean equalsHandler(Object[] args) {
        try {
            Object otherHandler = args.length > 0 && args[0] != null ? Proxy.getInvocationHandler(args[0]) : null;
            return this.equals(otherHandler);
        } catch (IllegalArgumentException var5) {
            return false;
        }
    }
}