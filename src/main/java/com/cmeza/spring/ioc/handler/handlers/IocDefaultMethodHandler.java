package com.cmeza.spring.ioc.handler.handlers;

import com.cmeza.spring.ioc.handler.factory.IocInvocationHandlerFactory;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;

public final class IocDefaultMethodHandler implements IocInvocationHandlerFactory.MethodHandler {
    private final MethodHandle unboundHandle;
    private MethodHandle handle;

    public IocDefaultMethodHandler(Method defaultMethod) {
        try {
            Class<?> declaringClass = defaultMethod.getDeclaringClass();
            Field field = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
            field.setAccessible(true);
            MethodHandles.Lookup lookup = (MethodHandles.Lookup) field.get(null);
            this.unboundHandle = lookup.unreflectSpecial(defaultMethod, declaringClass);
        } catch (NoSuchFieldException | IllegalAccessException var5) {
            throw new IllegalStateException(var5);
        }
    }

    public void bindTo(Object proxy) {
        if (Objects.nonNull(this.handle)) {
            throw new IllegalStateException("Attempted to rebind a default method handler that was already bound");
        } else {
            this.handle = this.unboundHandle.bindTo(proxy);
        }
    }

    public Object invoke(Object[] argv) throws Throwable {
        if (this.handle == null) {
            throw new IllegalStateException("Default method handler invoked before proxy has been bound.");
        } else {
            return this.handle.invokeWithArguments(argv);
        }
    }
}