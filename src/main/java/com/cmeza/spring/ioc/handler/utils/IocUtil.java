package com.cmeza.spring.ioc.handler.utils;

import lombok.experimental.UtilityClass;

import java.lang.reflect.*;
import java.util.Arrays;

@UtilityClass
public class IocUtil {

    public void notEmpty(String string, String message) {
        if (string == null || string.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    public boolean isDefault(Method method) {
        final int SYNTHETIC = 0x00001000;
        return ((method.getModifiers()
                & (Modifier.ABSTRACT | Modifier.PUBLIC | Modifier.STATIC | SYNTHETIC)) == Modifier.PUBLIC)
                && method.getDeclaringClass().isInterface();
    }

    public void checkState(boolean expression, String errorMessageTemplate, Object... errorMessageArgs) {
        if (!expression) {
            throw new IllegalStateException(String.format(errorMessageTemplate, errorMessageArgs));
        }
    }

    public String configKey(Class<?> targetType, Method method) {
        StringBuilder builder = new StringBuilder();
        if (targetType.isAssignableFrom(method.getDeclaringClass())) {
            builder.append(targetType.getSimpleName());
        } else {
            builder.append(targetType.getSimpleName()).append("::").append(method.getDeclaringClass().getSimpleName());
        }

        builder.append("::").append(method.getName()).append('(');

        Arrays.stream(method.getGenericParameterTypes())
                .forEach(param -> builder.append(IocTypes.getRawType(param).getSimpleName()).append(','));

        if (method.getParameterTypes().length > 0) {
            builder.deleteCharAt(builder.length() - 1);
        }

        return builder.append(')').toString();
    }

    public Class<?> getGenericInterface(Object obj) {
        return getGenericInterface(obj, 0);
    }

    public Class<?> getGenericInterface(Object obj, int argumentClass) {
        Type[] types = obj.getClass().getGenericInterfaces();
        return Arrays.stream(types).filter(ParameterizedType.class::isInstance)
                .map(ParameterizedType.class::cast)
                .map(t -> {
                    Type actualTypeArgument = t.getActualTypeArguments()[argumentClass];
                    if (actualTypeArgument instanceof ParameterizedType) {
                        return (Class<?>) ((ParameterizedType)actualTypeArgument).getRawType();
                    }
                    return (Class<?>) actualTypeArgument;
                })
                .findAny().orElseThrow(() -> new RuntimeException("Type not found!"));
    }

    public Class<?> getGenericSuperClass(Object obj) {
        Type type = obj.getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            Type actualTypeArgument = ((ParameterizedType) type).getActualTypeArguments()[0];
            if (actualTypeArgument instanceof ParameterizedType) {
                return (Class<?>) ((ParameterizedType)actualTypeArgument).getRawType();
            }
            return (Class<?>) actualTypeArgument;
        }
        return (Class<?>) type;
    }

}
