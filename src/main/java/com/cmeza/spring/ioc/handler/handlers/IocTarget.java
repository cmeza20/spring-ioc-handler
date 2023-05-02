package com.cmeza.spring.ioc.handler.handlers;

import com.cmeza.spring.ioc.handler.utils.IocUtil;
import lombok.Getter;
import org.springframework.util.Assert;

public interface IocTarget<T> {
    Class<T> getType();

    String getName();

    @Getter
    class Default<T> implements IocTarget<T> {
        private final Class<T> type;
        private final String name;

        public Default(Class<T> type, String name) {
            Assert.notNull(type, "Type required");
            Assert.notNull(name, "Name required");
            IocUtil.notEmpty(name, "Name is empty");
            this.type = type;
            this.name = name;
        }
    }
}