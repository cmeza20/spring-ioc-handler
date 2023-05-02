package com.cmeza.spring.ioc.handler.factory;

import com.cmeza.spring.ioc.handler.IocContext;
import com.cmeza.spring.ioc.handler.builders.Ioc;
import com.cmeza.spring.ioc.handler.contracts.IocContract;
import com.cmeza.spring.ioc.handler.contracts.IocContractDefault;
import com.cmeza.spring.ioc.handler.handlers.IocTarget;
import com.cmeza.spring.ioc.handler.processors.IocProcessorsManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

@Slf4j
public class IocFactoryBean<T, A> implements FactoryBean<T>, ApplicationContextAware {

    private Class<T> type;
    private Class<?> handler;
    private String name;
    private ApplicationContext applicationContext;

    @Override
    public T getObject() throws Exception {
        IocContext context = this.applicationContext.getBean(IocContext.class);
        Ioc.Factory.Builder factoryBuilder = this.getFactoryBuilder(context);
        configureIoc(context, factoryBuilder);
        return factoryBuilder.build().target(new IocTarget.Default<>(this.type, this.name));
    }

    @Override
    public Class<?> getObjectType() {
        return this.type;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void setType(Class<T> type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setHandler(Class<A> handler) {
        this.handler = handler;
    }

    protected Ioc.Factory.Builder getFactoryBuilder(IocContext context) {
        Ioc.Factory.Builder instance = context.getInstance(this.name, Ioc.Factory.Builder.class);
        if (instance == null) {
            throw new IllegalStateException("No bean found of type " + Ioc.Factory.Builder.class + " for " + this.name);
        }
        return instance;
    }

    protected void configureIoc(IocContext context, Ioc.Factory.Builder factoryBuilder) {
        IocContract<?> contract;
        if (context.getContracts().containsKey(handler)) {
            contract = context.getContracts().get(handler);
        } else {
            contract = new IocContractDefault();
            log.info("No contract found for '{}', default contract is applied", type);
        }

        factoryBuilder
                .invocationHandlerFactory(new IocInvocationHandlerFactory.Default())
                .contract(contract)
                .processors(new IocProcessorsManager()
                        .setAnnotatedClassProcessors(context.getClassProcessors())
                        .setAnnotatedMethodProcessors(context.getMethodProcessors())
                        .setAnnotatedParameterProcessors(context.getParameterProcessors()))
                .methodInterceptors(context.getMethodInterceptors());
    }
}
