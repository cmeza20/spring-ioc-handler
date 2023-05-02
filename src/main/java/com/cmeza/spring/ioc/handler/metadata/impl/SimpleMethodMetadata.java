package com.cmeza.spring.ioc.handler.metadata.impl;

import com.cmeza.spring.ioc.handler.metadata.MethodMetadata;
import com.cmeza.spring.ioc.handler.metadata.ParameterMetadata;
import com.cmeza.spring.ioc.handler.metadata.TypeMetadata;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

@Getter
public class SimpleMethodMetadata extends AbstractMetadata implements MethodMetadata {

    private final Method method;
    private final List<ParameterMetadata> parameterMetadata = new LinkedList<>();
    private final TypeMetadata typeMetadata;
    private String configKey;
    @Setter
    private boolean intercepted;

    public SimpleMethodMetadata(Method method, Type genericReturnType) {
        this.method = method;
        this.typeMetadata = new SimpleTypeMetadata(genericReturnType);
        this.setName(method.getName());
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    public SimpleMethodMetadata addParameterMetadata(ParameterMetadata parameterMetadata) {
        this.parameterMetadata.add(parameterMetadata);
        return this;
    }

    @Override
    public String toString() {
        return "SimpleMethodMetadata{" +
                "method=" + method +
                ", parameterMetadata=" + parameterMetadata +
                ", typeMetadata=" + typeMetadata +
                ", configKey='" + configKey + '\'' +
                ", intercepted=" + intercepted +
                '}';
    }
}
