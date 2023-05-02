package com.cmeza.spring.ioc.handler;

import com.cmeza.spring.ioc.handler.configuration.IocConfiguration;
import com.cmeza.spring.ioc.handler.factory.IocNamedContextFactory;

public class IocContext extends IocNamedContextFactory {

    public IocContext() {
        super(IocConfiguration.class);
    }

}