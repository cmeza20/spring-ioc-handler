package com.cmeza.spring.ioc.handler.factory;

import com.cmeza.spring.ioc.handler.contracts.IocContract;
import com.cmeza.spring.ioc.handler.handlers.IocMethodInterceptor;
import com.cmeza.spring.ioc.handler.processors.AnnotatedClassProcessor;
import com.cmeza.spring.ioc.handler.processors.AnnotatedMethodProcessor;
import com.cmeza.spring.ioc.handler.processors.AnnotatedParameterProcessor;
import com.cmeza.spring.ioc.handler.processors.SimpleParameterProcessor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.lang.annotation.Annotation;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public abstract class IocNamedContextFactory implements DisposableBean, ApplicationContextAware {
    private final Map<String, AnnotationConfigApplicationContext> contexts = new ConcurrentHashMap<>();
    private final Class<?> defaultConfigType;
    private final Map<Class<? extends Annotation>, IocContract<?>> contracts = new ConcurrentHashMap<>();
    private List<AnnotatedClassProcessor<?>> classProcessors = new LinkedList<>();
    private List<AnnotatedMethodProcessor<?>> methodProcessors = new LinkedList<>();
    private List<AnnotatedParameterProcessor<?>> parameterProcessors = new LinkedList<>();
    private List<IocMethodInterceptor<?>> methodInterceptors = new LinkedList<>();
    private List<SimpleParameterProcessor> simpleParameterProcessors = new LinkedList<>();
    private ApplicationContext parent;

    protected IocNamedContextFactory(Class<?> defaultConfigType) {
        this.defaultConfigType = defaultConfigType;
    }

    @Override
    public void setApplicationContext(ApplicationContext parent) throws BeansException {
        this.parent = parent;
    }

    @Override
    public void destroy() {
        this.contexts.values().forEach(AbstractApplicationContext::close);
        this.contexts.clear();
    }

    protected AnnotationConfigApplicationContext getContext(String name) {
        if (!this.contexts.containsKey(name)) {
            synchronized (this.contexts) {
                if (!this.contexts.containsKey(name)) {
                    this.contexts.put(name, this.createContext(name));
                }
            }
        }
        return this.contexts.get(name);
    }

    protected AnnotationConfigApplicationContext createContext(String name) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(PropertyPlaceholderAutoConfiguration.class, this.defaultConfigType);
        if (this.parent != null) {
            context.setParent(this.parent);
            context.setClassLoader(this.parent.getClassLoader());
        }
        context.setDisplayName(this.generateDisplayName(name));
        context.refresh();
        return context;
    }

    protected String generateDisplayName(String name) {
        return this.getClass().getSimpleName() + "-" + name;
    }

    public <T> T getInstance(String name, Class<T> type) {
        AnnotationConfigApplicationContext context = this.getContext(name);
        return BeanFactoryUtils.beanNamesForTypeIncludingAncestors(context, type).length > 0 ? context.getBean(type) : null;
    }

    public <T> ObjectProvider<T> getProvider(String name, Class<T> type) {
        AnnotationConfigApplicationContext context = this.getContext(name);
        return context.getBeanProvider(type);
    }

    public void setClassProcessor(List<AnnotatedClassProcessor<?>> classProcessors) {
        this.classProcessors = classProcessors;
    }

    public void setMethodProcessor(List<AnnotatedMethodProcessor<?>> methodProcessors) {
        this.methodProcessors = methodProcessors;
    }

    public void setParameterProcessor(List<AnnotatedParameterProcessor<?>> parameterProcessors) {
        this.parameterProcessors = parameterProcessors;
    }

    public void setSimpleParameterProcessor(List<SimpleParameterProcessor> simpleParameterProcessors) {
        this.simpleParameterProcessors = simpleParameterProcessors;
    }

    public Map<Class<? extends Annotation>, IocContract<?>> getContracts() {
        return contracts;
    }

    public <C extends IocContract<? extends Annotation>> void setContracts(List<C> contracts) {
        if (Objects.nonNull(contracts)) {
            contracts.forEach(c -> this.contracts.put(c.getAnnotationType(), c));
        }
    }

    public List<AnnotatedClassProcessor<?>> getClassProcessors() {
        return classProcessors;
    }

    public List<AnnotatedMethodProcessor<?>> getMethodProcessors() {
        return methodProcessors;
    }

    public List<AnnotatedParameterProcessor<?>> getParameterProcessors() {
        return parameterProcessors;
    }

    public List<SimpleParameterProcessor> getSimpleParameterProcessors() { return simpleParameterProcessors; }

    public List<IocMethodInterceptor<?>> getMethodInterceptors() {
        return methodInterceptors;
    }

    public void setMethodInterceptors(List<IocMethodInterceptor<?>> methodInterceptors) {
        this.methodInterceptors = methodInterceptors;
    }

}