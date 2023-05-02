package com.cmeza.spring.ioc.handler.annotations;

import com.cmeza.spring.ioc.handler.IocHandlerRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Inherited
@Import(IocHandlerRegistrar.class)
public @interface EnableIocHandlers {

    Class<? extends Annotation>[] value();

    String[] basePackages() default {};

}
