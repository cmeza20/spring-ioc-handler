package com.cmeza.spring.ioc.handler.metadata.impl;

import com.cmeza.spring.ioc.handler.metadata.TypeMetadata;
import com.cmeza.spring.ioc.handler.utils.IocTypes;
import lombok.Getter;
import org.springframework.util.ClassUtils;
import org.springframework.util.TypeUtils;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Stream;

@Getter
public class SimpleTypeMetadata implements TypeMetadata {

    private final Class<?> argumentClass;
    private final Class<?> rawClass;
    private final boolean isVoid;
    private final boolean isCustomArgumentObject;
    private final boolean primitive;
    private final boolean list;
    private final boolean set;
    private final boolean map;
    private final boolean optional;
    private final boolean array;
    private final boolean isEnum;
    private final boolean mapEntry;
    private final boolean parameterized;
    private final boolean isStream;
    private final boolean isNativeObject;
    private final Class<?>[] argumentTypes;

    public SimpleTypeMetadata(Type type) {
        if (Objects.isNull(type)) {
            type = SimpleTypeMetadata.class;
        }
        this.rawClass = IocTypes.getRawType(type);
        this.array = rawClass.isArray();
        this.argumentClass = IocTypes.extractReturnType(type, array);
        this.isNativeObject = rawClass.getSimpleName().equals(Object.class.getSimpleName());
        this.isVoid = rawClass == void.class || rawClass == Void.TYPE;
        this.list = !isNativeObject && TypeUtils.isAssignable(type, List.class);
        this.set = !isNativeObject && TypeUtils.isAssignable(type, Set.class);
        this.map = !isNativeObject && TypeUtils.isAssignable(type, Map.class);
        this.optional = !isNativeObject && TypeUtils.isAssignable(type, Optional.class);
        this.mapEntry = !isNativeObject && TypeUtils.isAssignable(type, Map.Entry.class);
        this.isStream = !isNativeObject && TypeUtils.isAssignable(type, Stream.class);
        this.isEnum = rawClass.isEnum() || argumentClass.isEnum();
        this.primitive = !isNativeObject && (ClassUtils.isPrimitiveOrWrapper(rawClass) || TypeUtils.isAssignable(rawClass, String.class));
        this.isCustomArgumentObject = isNativeObject || (!ClassUtils.isPrimitiveOrWrapper(argumentClass) && !TypeUtils.isAssignable(rawClass, String.class) && !rawClass.isArray());
        this.argumentTypes = IocTypes.extractGenericTypes(type, array);
        this.parameterized = IocTypes.isParameterized(type);
    }

    @Override
    public boolean isAssignableFrom(Class<?> typeClass, Class<?> parameterClass) {
        return !isNativeObject && typeClass.isAssignableFrom(parameterClass);
    }


    @Override
    public TypeMetadata extractMetadata(Type type) {
        return new SimpleTypeMetadata(type);
    }

    @Override
    public String toString() {
        return "SimpleTypeMetadata{" +
                "argumentClass=" + argumentClass +
                ", rawClass=" + rawClass +
                ", isVoid=" + isVoid +
                ", isCustomArgumentObject=" + isCustomArgumentObject +
                ", primitive=" + primitive +
                ", list=" + list +
                ", set=" + set +
                ", map=" + map +
                ", optional=" + optional +
                ", array=" + array +
                ", isEnum=" + isEnum +
                ", mapEntry=" + mapEntry +
                ", parameterized=" + parameterized +
                ", isStream=" + isStream +
                ", isNativeObject=" + isNativeObject +
                ", argumentTypes=" + Arrays.toString(argumentTypes) +
                '}';
    }
}
