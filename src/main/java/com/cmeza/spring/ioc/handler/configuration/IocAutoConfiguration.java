package com.cmeza.spring.ioc.handler.configuration;

import com.cmeza.spring.ioc.handler.IocContext;
import com.cmeza.spring.ioc.handler.builders.Ioc;
import com.cmeza.spring.ioc.handler.contracts.IocContract;
import com.cmeza.spring.ioc.handler.handlers.IocMethodInterceptor;
import com.cmeza.spring.ioc.handler.processors.AnnotatedClassProcessor;
import com.cmeza.spring.ioc.handler.processors.AnnotatedMethodProcessor;
import com.cmeza.spring.ioc.handler.processors.AnnotatedParameterProcessor;
import com.cmeza.spring.ioc.handler.processors.SimpleParameterProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;

@AutoConfiguration
@AutoConfigureOrder(Ioc.IOC_ORDER)
@ConditionalOnClass(Ioc.class)
@RequiredArgsConstructor
public class IocAutoConfiguration {

    @Autowired(required = false)
    private List<IocContract<?>> contracts = new ArrayList<>();

    @Autowired(required = false)
    private List<AnnotatedClassProcessor<?>> classProcessors = new ArrayList<>();

    @Autowired(required = false)
    private List<AnnotatedMethodProcessor<?>> methodProcessors = new ArrayList<>();

    @Autowired(required = false)
    private List<AnnotatedParameterProcessor<?>> parameterProcessors = new ArrayList<>();

    @Autowired(required = false)
    private List<SimpleParameterProcessor> simpleParameterProcessors = new ArrayList<>();

    @Autowired(required = false)
    private List<IocMethodInterceptor<?>> methodInterceptors = new ArrayList<>();

    @Bean
    public IocContext iocContext() {
        IocContext context = new IocContext();
        context.setContracts(this.contracts);
        context.setClassProcessor(this.classProcessors);
        context.setMethodProcessor(this.methodProcessors);
        context.setParameterProcessor(this.parameterProcessors);
        context.setMethodInterceptors(this.methodInterceptors);
        context.setSimpleParameterProcessor(this.simpleParameterProcessors);
        return context;
    }

}
