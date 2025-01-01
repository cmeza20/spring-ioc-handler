package com.cmeza.spring.ioc.handler;

import com.cmeza.spring.ioc.handler.annotations.EnableIocHandlers;
import com.cmeza.spring.ioc.handler.exceptions.IocException;
import com.cmeza.spring.ioc.handler.factory.IocFactoryBean;
import com.cmeza.spring.ioc.handler.providers.CustomScanningCandidateComponentProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.beans.Introspector;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class IocHandlerRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware {

    private ResourceLoader resourceLoader;
    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        Assert.notNull(importingClassMetadata, "AnnotationMetadata must not be null!");
        Assert.notNull(registry, "BeanDefinitionRegistry must not be null!");

        AnnotationAttributes annotationAttributes = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(this.getAnnotationClass().getName(), false));
        if (Objects.nonNull(annotationAttributes) && !annotationAttributes.isEmpty()) {

            List<Class<Annotation>> handlers = extractAnnotations(annotationAttributes);
            Set<String> basePackages = this.getBasePackages(importingClassMetadata, annotationAttributes);

            if (!handlers.isEmpty()) {
                ClassPathScanningCandidateComponentProvider scanner = new CustomScanningCandidateComponentProvider(environment);
                scanner.setResourceLoader(this.resourceLoader);

                handlers.forEach(handler -> {
                    scanner.addIncludeFilter(new AnnotationTypeFilter(handler));
                    basePackages.forEach(basePackage -> scanner.findCandidateComponents(basePackage).forEach(candidateComponent -> {
                        if (candidateComponent instanceof AnnotatedBeanDefinition) {
                            AnnotatedBeanDefinition beanDefinition = (AnnotatedBeanDefinition) candidateComponent;
                            AnnotationMetadata annotationMetadata = beanDefinition.getMetadata();
                            Assert.isTrue(annotationMetadata.isInterface(), "@Annotation can only be specified on an interface");

                            this.registerBeanDefinition(registry, annotationMetadata, handler);
                        }
                    }));
                });

            } else {
                log.warn("@EnableIocHandlers: No interface was declared");
            }
        }
    }

    private Set<String> getBasePackages(AnnotationMetadata importingClassMetadata, AnnotationAttributes attributes) {
        Set<String> basePackages = Stream.of(attributes.getStringArray("basePackages"))
                .filter(StringUtils::hasText)
                .collect(Collectors.toSet());

        if (basePackages.isEmpty()) {
            basePackages.add(ClassUtils.getPackageName(importingClassMetadata.getClassName()));
        }
        return basePackages;
    }

    private void registerBeanDefinition(BeanDefinitionRegistry registry, AnnotationMetadata annotationMetadata, Class<?> handler) {
        try {
            String className = annotationMetadata.getClassName();

            BeanDefinitionBuilder definition = BeanDefinitionBuilder.genericBeanDefinition(IocFactoryBean.class);

            definition.addPropertyValue("name", className);
            definition.addPropertyValue("type", className);
            definition.addPropertyValue("handler", handler);
            definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);

            AbstractBeanDefinition beanDefinition = definition.getBeanDefinition();
            beanDefinition.setPrimary(true);
            beanDefinition.setAutowireCandidate(true);
            beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);

            beanDefinition.setAttribute(FactoryBean.OBJECT_TYPE_ATTRIBUTE, Class.forName(className));
            beanDefinition.setLazyInit(true);

            String shortName = ClassUtils.getShortName(className);
            String alias = Introspector.decapitalize(shortName);

            BeanDefinitionHolder holder = new BeanDefinitionHolder(beanDefinition, className, new String[]{alias});
            BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);

            log.trace("Bean '{}' registered successfully", annotationMetadata.getClassName());
        } catch (ClassNotFoundException e) {
            throw new IocException(e);
        }

    }

    @SuppressWarnings("unchecked")
    private <T extends Annotation> List<Class<T>> extractAnnotations(AnnotationAttributes annotationAttributes) {
        return Arrays.stream(annotationAttributes.getClassArray("value"))
                .map(clazz -> (Class<T>) clazz)
                .collect(Collectors.toList());
    }

    private Class<?> getAnnotationClass() {
        return EnableIocHandlers.class;
    }

}
