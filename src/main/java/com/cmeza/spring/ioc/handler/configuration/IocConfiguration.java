package com.cmeza.spring.ioc.handler.configuration;

import com.cmeza.spring.ioc.handler.builders.Ioc;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class IocConfiguration {

    @Bean
    @Scope("prototype")
    @ConditionalOnMissingBean
    public Ioc.Factory.Builder iocBuilder() {
        return Ioc.Factory.builder();
    }

}
