package com.cmeza.spring.ioc.handler.metadata;

import java.lang.reflect.Type;

public interface TypeMetadata {
    Class<?> getArgumentClass();

    Class<?> getRawClass();

    boolean isParameterized();

    boolean isPrimitive();

    boolean isCustomArgumentObject();

    boolean isList();

    boolean isSet();

    boolean isMap();

    boolean isOptional();

    boolean isArray();

    boolean isVoid();

    boolean isEnum();

    boolean isMapEntry();

    boolean isStream();

    boolean isNativeObject();

    boolean isAssignableFrom(Class<?> typeClass, Class<?> parameterClass);
    Class<?>[] getArgumentTypes();

    TypeMetadata extractMetadata(Type type);
}
