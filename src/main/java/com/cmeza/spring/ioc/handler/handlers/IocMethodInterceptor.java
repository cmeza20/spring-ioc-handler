package com.cmeza.spring.ioc.handler.handlers;

import com.cmeza.spring.ioc.handler.utils.IocUtil;

import java.lang.reflect.Method;
import java.util.Optional;

@SuppressWarnings("unchecked")
public interface IocMethodInterceptor<T> {
    Optional<Object> invoke(Object proxy, IocTarget<?> target, Method method, Object[] args);

    default Class<T> getInterceptResultTypes() {
        return (Class<T>) IocUtil.getGenericInterface(this);
    }

    default boolean accept(Class<?> clazz) {
        return getInterceptResultTypes() != null && getInterceptResultTypes().isAssignableFrom(clazz);
    }
}
