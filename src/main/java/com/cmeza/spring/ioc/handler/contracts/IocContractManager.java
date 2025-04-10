package com.cmeza.spring.ioc.handler.contracts;

import com.cmeza.spring.ioc.handler.contracts.consumers.enums.ConsumerLocation;
import com.cmeza.spring.ioc.handler.contracts.consumers.manager.ConsumerManagerImpl;
import com.cmeza.spring.ioc.handler.handlers.IocMethodInterceptor;
import com.cmeza.spring.ioc.handler.metadata.ClassMetadata;
import com.cmeza.spring.ioc.handler.metadata.MethodMetadata;
import com.cmeza.spring.ioc.handler.metadata.impl.SimpleAnnotationMetadata;
import com.cmeza.spring.ioc.handler.metadata.impl.SimpleClassMetadata;
import com.cmeza.spring.ioc.handler.metadata.impl.SimpleMethodMetadata;
import com.cmeza.spring.ioc.handler.metadata.impl.SimpleParameterMetadata;
import com.cmeza.spring.ioc.handler.processors.IocProcessors;
import com.cmeza.spring.ioc.handler.utils.IocTypes;
import com.cmeza.spring.ioc.handler.utils.IocUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@SuppressWarnings({"unchecked", "rawtypes"})
public class IocContractManager {
    private final IocContract<?> contract;
    private final IocProcessors processors;
    private final List<IocMethodInterceptor<?>> methodInterceptors;

    public ClassMetadata parseAndValidateClassMetadata(Class<?> targetType) {
        IocUtil.checkState(targetType.getTypeParameters().length == 0, "Parameterized types unsupported: %s", targetType.getSimpleName());
        IocUtil.checkState(targetType.getInterfaces().length <= 1, "Only single inheritance supported: %s", targetType.getSimpleName());
        if (targetType.getInterfaces().length == 1) {
            IocUtil.checkState(targetType.getInterfaces()[0].getInterfaces().length == 0, "Only single-level inheritance supported: %s", targetType.getSimpleName());
        }

        ConsumerManagerImpl consumerManager = new ConsumerManagerImpl();
        contract.configure(consumerManager);

        //Processors configuration
        contract.processors(processors);

        SimpleClassMetadata classMetadata = new SimpleClassMetadata(targetType);

        //Class Annotation process
        consumerManager.getClassConsumers(ConsumerLocation.ON_START).forEach(consumer -> consumer.accept(classMetadata));
        consumerManager.getClassConsumers(ConsumerLocation.BEFORE_ANNOTATION_PROCESSOR).forEach(consumer -> consumer.accept(classMetadata));
        this.executeClassAnnotatedProcessors(contract.onlyMethodDeclaredAnnotations() ? targetType.getDeclaredAnnotations() : targetType.getAnnotations(), classMetadata);
        consumerManager.getClassConsumers(ConsumerLocation.AFTER_ANNOTATION_PROCESSOR).forEach(consumer -> consumer.accept(classMetadata));

        for (Method method : contract.onlyDeclaredMethods() ? targetType.getDeclaredMethods() : targetType.getMethods()) {
            if (method.getDeclaringClass() != Object.class && (method.getModifiers() & 8) == 0 && !IocUtil.isDefault(method)) {
                MethodMetadata metadata = this.parseAndValidateMethodMetadata(classMetadata, method, consumerManager);

                IocUtil.checkState(!classMetadata.existsMethodMetadata(metadata), "Overrides unsupported: %s", metadata.getConfigKey());
                classMetadata.addMethodMetadata(metadata);
            }
        }

        //Class Annotation process
        consumerManager.getClassConsumers(ConsumerLocation.ON_END).forEach(consumer -> consumer.accept(classMetadata));

        return classMetadata;
    }

    private MethodMetadata parseAndValidateMethodMetadata(SimpleClassMetadata classMetadata, Method method, ConsumerManagerImpl consumerManager) {
        SimpleMethodMetadata methodMetadata = new SimpleMethodMetadata(method, IocTypes.resolve(classMetadata.getTargetClass(), classMetadata.getTargetClass(), method.getGenericReturnType()));
        methodMetadata.setConfigKey(IocUtil.configKey(classMetadata.getTargetClass(), method));
        methodMetadata.setIntercepted(methodInterceptors.stream().anyMatch(i -> i.accept(method.getReturnType())));

        //Method Annotation process
        consumerManager.getMethodConsumers(ConsumerLocation.ON_START).forEach(consumer -> consumer.accept(classMetadata, methodMetadata));
        consumerManager.getMethodConsumers(ConsumerLocation.BEFORE_ANNOTATION_PROCESSOR).forEach(consumer -> consumer.accept(classMetadata, methodMetadata));
        this.executeMethodAnnotatedProcessors(contract.onlyMethodDeclaredAnnotations() ? method.getDeclaredAnnotations() : method.getAnnotations(), classMetadata, methodMetadata);
        consumerManager.getMethodConsumers(ConsumerLocation.AFTER_ANNOTATION_PROCESSOR).forEach(consumer -> consumer.accept(classMetadata, methodMetadata));

        int[] idx = {0};
        Arrays.stream(method.getParameters())
                .forEach(parameter -> {
                    int index = idx[0]++;

                    if (!parameter.isNamePresent()) {
                        log.warn("[{}] Parameter name of method '{}' is not present", parameter.getName(), methodMetadata.getConfigKey());
                    }

                    SimpleParameterMetadata parameterMetadata = new SimpleParameterMetadata(parameter, parameter.getParameterizedType());


                    //Parameter Annotation process
                    consumerManager.getParameterConsumers(ConsumerLocation.ON_START).forEach(consumer -> consumer.accept(classMetadata, methodMetadata, parameterMetadata, index));
                    consumerManager.getParameterConsumers(ConsumerLocation.BEFORE_ANNOTATION_PROCESSOR).forEach(consumer -> consumer.accept(classMetadata, methodMetadata, parameterMetadata, index));
                    this.executeAnnotatedParameterAnnotatedProcessors(contract.onlyParameterDeclaredAnnotations() ? parameter.getDeclaredAnnotations() : parameter.getAnnotations(), classMetadata, methodMetadata, parameterMetadata, index);
                    this.executeSimpleParameterAnnotatedProcessors(parameter, classMetadata, methodMetadata, parameterMetadata, index);
                    consumerManager.getParameterConsumers(ConsumerLocation.AFTER_ANNOTATION_PROCESSOR).forEach(consumer -> consumer.accept(classMetadata, methodMetadata, parameterMetadata, index));
                    consumerManager.getParameterConsumers(ConsumerLocation.ON_END).forEach(consumer -> consumer.accept(classMetadata, methodMetadata, parameterMetadata, index));

                    methodMetadata.addParameterMetadata(parameterMetadata);
                });

        //Method Annotation process
        consumerManager.getMethodConsumers(ConsumerLocation.ON_END).forEach(consumer -> consumer.accept(classMetadata, methodMetadata));

        return methodMetadata;
    }

    private void executeClassAnnotatedProcessors(Annotation[] annotations, SimpleClassMetadata classMetadata) {
        int[] idx = {0};
        Arrays.stream(annotations).forEach(annotation -> {
            int index = idx[0]++;
            processors.getAnnotatedClassProcessor(annotation.annotationType()).ifPresent(classProcessor -> {
                SimpleAnnotationMetadata annotationMetadata = new SimpleAnnotationMetadata(annotation, index);
                classMetadata.addProcessorResult(annotation.annotationType(), classProcessor.process(annotationMetadata, classMetadata));
                classMetadata.addAnnotation(annotationMetadata);
            });
        });
    }

    private void executeMethodAnnotatedProcessors(Annotation[] annotations, SimpleClassMetadata classMetadata, SimpleMethodMetadata methodMetadata) {
        int[] idx = {0};
        Arrays.stream(annotations).forEach(annotation -> {
            int index = idx[0]++;
            processors.getAnnotatedMethodProcessor(annotation.annotationType()).ifPresent(methodProcessor -> {
                SimpleAnnotationMetadata annotationMetadata = new SimpleAnnotationMetadata(annotation, index);
                methodMetadata.addProcessorResult(annotation.annotationType(), methodProcessor.process(annotationMetadata, classMetadata, methodMetadata));
                methodMetadata.addAnnotation(annotationMetadata);
            });
        });
    }

    private void executeAnnotatedParameterAnnotatedProcessors(Annotation[] annotations, SimpleClassMetadata classMetadata, SimpleMethodMetadata methodMetadata, SimpleParameterMetadata parameterMetadata, int index) {
        Arrays.stream(annotations).forEach(annotation -> processors.getAnnotatedParameterProcessor(annotation.annotationType()).ifPresent(parameterProcessor -> {
            SimpleAnnotationMetadata annotationMetadata = new SimpleAnnotationMetadata(annotation, index);
            parameterMetadata.addProcessorResult(annotation.annotationType(), parameterProcessor.process(annotationMetadata, classMetadata, methodMetadata, parameterMetadata));
            parameterMetadata.addAnnotation(annotationMetadata);
        }));
    }

    private void executeSimpleParameterAnnotatedProcessors(Parameter parameter, SimpleClassMetadata classMetadata, SimpleMethodMetadata methodMetadata, SimpleParameterMetadata parameterMetadata, int index) {
        processors.getSimpleParameterProcessors().forEach(parameterProcessor -> parameterProcessor.process(parameter, classMetadata, methodMetadata, parameterMetadata, index));
    }
}
