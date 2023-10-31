# Spring IOC Handler [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.cmeza/spring-ioc-handler/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.cmeza/spring-ioc-handler)

Abstract contract generator for interfaces, inspired by spring data jpa and feign

## Features ##

* Generate abstract contract
* Class process
* Method process
* Parameter process


## Dependencies ##

* **Java >= 8**
* **spring-boot-starter-parent < 3.0.0**

## Maven Integration ##

```xml

<dependency>
    <groupId>com.cmeza</groupId>
    <artifactId>spring-ioc-handler</artifactId>
    <version>1.0.1</version>
</dependency>
```

## @EnableIocHandlers Annotation ##

| PARAMETER    | TYPE                          | REQUIRED | DEFAULT VALUE | DESCRIPTION                                 |
|--------------|-------------------------------|----------|---------------|---------------------------------------------|
| value        | Class<? extends Annotation>[] | YES      |               | Annotation that is injected into the IOC    |
| basePackages | String[]                      | NO       | []            | Packages where annotations will be searched |

## Example  ##
For the example we will use a JDBC query simulation. 
Note that we will use an example to explain the operation, the JdbcRepository annotation could be any other.

```java
@SpringBootApplication
@EnableIocHandlers(
        value = {JdbcRepository.class}, 
        basePackages = {"com.example.ioc", "com.example.test"}
)
public class App {

  public static void main(String[] args) {
    SpringApplication.run(App.class, args);
  }

}
```

```java
//PersonRepository is annotated with @JdbcRepository, so it will be registered as a bean
@JdbcRepository(name = "CMRepositoryName")
public interface PersonRepository {

    //Method "list" is annotated with @JdbcMethod.
    @JdbcMethod(query = "select * from dbo.person")
    List<Person> list();

    //The "get" method has a parameter annotated with @JdbcParameter.
    @JdbcMethod(query = "select * from dbo.person where id = :id")
    Person get(@JdbcParameter("id") Long id);
    
    //Intercepted (see at the end)
    JdbcContext getJdbcContext();
}
```

## IocContract ##
Implementation of the "IocContract" interface

```java
@Component
public class JdbcContract implements IocContract<JdbcRepository> {
//  ...
}
```

### Override Methods ###

#### 1. ANNOTATION TYPE ####
Returns the class of the generic annotation

```java
@Component
public class JdbcContract implements IocContract<JdbcRepository> {
    
  @Override
  public Class<JdbcRepository> getAnnotationType() {
    return JdbcRepository.class;
  }
}
```

#### 2. CONFIGURE ####
Configuration on the interface, method and parameter

```java
@Component
public class JdbcContract implements IocContract<JdbcRepository> {
    
  @Override
  public void configure(ConsumerManager consumerManager) {
    ClassConsumer classConsumer = classMetadata -> {
      LOGGER.info("JdbcRepository: {}", classMetadata.getProcessorResult(JdbcRepository.class).toString());
      LOGGER.info("classAnnotationName: {}", classMetadata.getAttribute("classAnnotationName", String.class));
    };

    MethodConsumer methodConsumer = (classMetadata, methodMetadata) -> {
      LOGGER.info("JdbcMethod: {}", methodMetadata.getProcessorResult(JdbcMethod.class).toString());
      LOGGER.info("methodAnnotationQuery: {}", methodMetadata.getAttribute("methodAnnotationQuery", String.class));
    };

    ParameterConsumer parameterConsumer = (classMetadata, methodMetadata, parameterMetadata, i) -> {
      LOGGER.info("JdbcParameter: {}", parameterMetadata.getProcessorResult(JdbcParameter.class).toString());
      LOGGER.info("parameterAnnotationName: {}", parameterMetadata.getAttribute("parameterAnnotationName", String.class));
    };

    consumerManager.addClassConsumer(ConsumerLocation.AFTER_ANNOTATION_PROCESSOR, classConsumer);
    consumerManager.addMethodConsumer(ConsumerLocation.AFTER_ANNOTATION_PROCESSOR, methodConsumer);
    consumerManager.addParameterConsumer(ConsumerLocation.AFTER_ANNOTATION_PROCESSOR, parameterConsumer);
  }
}
```


- **ConsumerManager**

| METHOD               | PARAMETERS                          | DESCRIPTION                                                  |
|----------------------|-------------------------------------|--------------------------------------------------------------|
| addClassConsumer     | ConsumerLocation, ClassConsumer     | Add a configurator based on location (ConsumerLocation enum) |
| addMethodConsumer    | ConsumerLocation, MethodConsumer    | Add a configurator based on location (ConsumerLocation enum) |
| addParameterConsumer | ConsumerLocation, ParameterConsumer | Add a configurator based on location (ConsumerLocation enum) |

- **ConsumerLocation**

| ENUM VALUE                  | DESCRIPTION                                                                      |
|-----------------------------|----------------------------------------------------------------------------------|
| ON_START                    | When starting the block of (class, method, parameter)                            |
| ON_END                      | At the end of the block of (class, method, parameter)                            |
| BEFORE_ANNOTATION_PROCESSOR | After starting and before executing the processes of (class, method, parameter)  |
| AFTER_ANNOTATION_PROCESSOR  | Before finishing and after executing the processes of (class, method, parameter) |

- **ClassConsumer** (Function interface that receives "ClassMetadata")
```java
@FunctionalInterface
public interface ClassConsumer extends AbstractConsumer {
    void accept(ClassMetadata var1);
}
```

- **MethodConsumer** (Function interface that receives "ClassMetadata and MethodMetadata")
```java
@FunctionalInterface
public interface MethodConsumer extends AbstractConsumer {
  void accept(ClassMetadata var1, MethodMetadata var2);
}
```

- **ParameterConsumer** (Function interface that receives "ClassMetadata, MethodMetadata, ParameterMetadata and parameter position")
```java
@FunctionalInterface
public interface ParameterConsumer extends AbstractConsumer {
  void accept(ClassMetadata var1, MethodMetadata var2, ParameterMetadata var3, int var4);
}
```

- **Metadata** (Parent class for ClassMetadata, MethodMetadata and ParameterMetadata)

| METHOD                                          | RETURN TYPE                                  | DESCRIPTION                                                                                                                                        |
|-------------------------------------------------|----------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------|
| getName                                         | String                                       | Returns the name of the class, method or parameter                                                                                                 |
| addAttribute                                    | Metadata                                     | Add a custom attribute to the context of the class, method or parameter                                                                            |
| hasAttribute                                    | Boolean                                      | Verifies that the custom attribute exists in the context of the class, method, or parameter                                                        |
| getAttributes                                   | Map<String, Object>                          | Returns all custom attributes stored in the context of the class, method, or parameter                                                             |
| getAttribute(String, Class<T>)                  | T                                            | Returns an object stored in the context by its key, transforming to the class it passes as a parameter                                             |
| getAttribute(String, Class<T>, T)               | T                                            | Returns an object stored in the context by its key, transforming to the class it passes as a parameter, with a default value if it does not exist. |
| getAnnotations                                  | AnnotationMetadata<?>                        | Returns all processed AnnotationMetadata objects                                                                                                   |
| getAnnotation(Class<T>)                         | AnnotationMetadata<T>                        | Returns the AnnotationMetadata object processed by the class                                                                                       |
| getProcessorsResult                             | Map<Class<? extends Annotation>, Annotation> | Returns all annotations resulting from the process                                                                                                 |
| getProcessorResult(Class<T extends Annotation>) | T, Annotation>                               | Returns the annotation resulting from the process by the class                                                                                     |


- **ClassMetadata**

| METHOD             | RETURN TYPE          | DESCRIPTION                                                                                                                     |
|--------------------|----------------------|---------------------------------------------------------------------------------------------------------------------------------|
| getTargetClass     | Class<?>             | Returns the annotated class, in the case of the example it would be the interface annotated with the @JdbcRepository annotation |
| getMethodsMetadata | List<MethodMetadata> | Returns the list of MethodMetadata                                                                                              |

- **MethodMetadata**

| METHOD               | RETURN TYPE             | DESCRIPTION                                                                                      |
|----------------------|-------------------------|--------------------------------------------------------------------------------------------------|
| getMethod            | Method                  | Returns the current method obtained by reflection                                                |
| getConfigKey         | String                  | returns a key constructed by the method + parameters, example: PersonRepository::getPerson(Long) |
| getTypeMetadata      | TypeMetadata            | TypeMetadata is the information obtained about the return value                                  |
| getParameterMetadata | List<ParameterMetadata> | Returns the list of ParameterMetadata                                                            |
| isIntercepted        | Boolean                 | Specifies if the method was intercepted before going to process                                  |

- **ParameterMetadata**

| METHOD          | RETURN TYPE       | DESCRIPTION                                                                                              |
|-----------------|-------------------|----------------------------------------------------------------------------------------------------------|
| getParameter    | Parameter         | Returns the current Parameter obtained by reflection                                                     |
| getTypeMetadata | TypeMetadata      | TypeMetadata is the information obtained about the return value                                          |
| getValue        | Object            | Returns the value of the parameter, it is only accessible when the method is executed                    |
| setValue        | ParameterMetadata | sets the value of the parameter, it is for internal use but can be used to override the value at runtime |

- **TypeMetadata**

| METHOD                         | RETURN TYPE  | DESCRIPTION                                                                                           |
|--------------------------------|--------------|-------------------------------------------------------------------------------------------------------|
| getArgumentClass               | Class<?>     | Returns the class of the generic argument if any, otherwise it returns the flat class                 |
| getRawClass                    | Class<?>     | Returns the flat class regardless of whether it is generic                                            |
| isParameterized                | Boolean      | Returns if the class has a generic, example List<Person> would return true, String would return false |
| isPrimitive                    | Boolean      | Returns true if the class is primitive.                                                               |
| isCustomArgumentObject         | Boolean      | Returns true if the class is defined by the user, example: Person, Car, Pet, etc.                     |
| isList                         | Boolean      | Returns true if the class is a collection of type List                                                |
| isSet                          | Boolean      | Returns true if the class is a collection of type Set.                                                |
| isMap                          | Boolean      | Returns true if the class is an object of type Map.                                                   |
| isOptional                     | Boolean      | Returns true if the class is an object of type java.util.Optional                                     |
| isArray                        | Boolean      | Returns true if the class is an object of type Array.                                                 |
| isVoid                         | Boolean      | Returns true if the class or method is of type void.                                                  |
| isEnum                         | Boolean      | Returns true if the class is an enumerator.                                                           |
| isMapEntry                     | Boolean      | Returns true if the class is of type java.util.Map.Entry.                                             |
| isStream                       | Boolean      | Returns true if the class is of type java.util.stream.Stream.                                         |
| isNativeObject                 | Boolean      | Returns true if the class is of native Object type.                                                   |
| isAssignableFrom(Class, Class) | Boolean      | Returns true if the classes indicated as parameters are assignable.                                   |
| getArgumentTypes               | Class<?>[]   | Returns an array of classes (only applies to generic classes)                                         |
| extractMetadata(Type)          | TypeMetadata | Returns a TypeMetadata built from the class set as parameter                                          |


#### 3. EXECUTE ####
At this point, all the information of the class, method and parameters in their respective classes (ClassMetadata, MethodMetadata and ParameterMetadata) have already been processed. With this information it would be possible to process what you want.
```java
@Component
public class JdbcContract implements IocContract<JdbcRepository> {
  @Override
  public Object execute(ClassMetadata classMetadata, MethodMetadata methodMetadata, Object[] objects, IocTarget<?> iocTarget) {
      return null;
  }
}
```

#### 4. PROCESSORS ####
Here you can manipulate the Processors, delete, add, etc. (The Processors are explained later)
```java
@Component
public class JdbcContract implements IocContract<JdbcRepository> {
  @Override
  public void processors(IocProcessors processors) {
        processors.clearAnnotatedClassProcessors();
        processors.clearAnnotatedMethodProcessors();
        processors.clearAnnotatedParameterProcessors();
        processors.setAnnotatedClassProcessors(classProcessors);
        processors.setAnnotatedMethodProcessors(methodProcessors);
        processors.setAnnotatedParameterProcessors(parameterProcessors);
  }
}
```

#### 5. FLAGS ####
Here you can set how the "reflection" extracts the information from the class, method or parameter
```java
@Component
public class JdbcContract implements IocContract<JdbcRepository> {
    
  //By default it has the value of "false" if it is changed to "true" you will not get inherited methods  
  @Override
  public boolean onlyDeclaredMethods() {
    return false;
  }

  //By default it has the value of "false" if it is changed to "true" you will not get annotations inherited from the method
  @Override
  public boolean onlyMethodDeclaredAnnotations() {
    return false;
  }

  //By default it has the value of "false" if it is changed to "true" you will not get annotations inherited from the parameter
  @Override
  public boolean onlyParameterDeclaredAnnotations() {
    return false;
  }
}
```

## PROCESSORS ##

- **AnnotatedClassProcessor**
Class processor, which parses the defined annotation, in this case @JdbcRepository.
The process method returns the annotation, in normal or modified form, as well as storing attributes in the context of ClassMetadata.
```java
@Component
public class JdbcClassProcessor implements AnnotatedClassProcessor<JdbcRepository> {

  @Override
  public JdbcRepository process(AnnotationMetadata<JdbcRepository> annotationMetadata, ClassMetadata classMetadata) {
    JdbcRepository annotation = annotationMetadata.getAnnotation();
    //Save extra parameters in metadata
    classMetadata.addAttribute("classAnnotationName", annotation.name());
    return annotation;
  }
}
```

- **AnnotatedMethodProcessor**
  Method processor, which parses the defined annotation, in this case @JdbcMethod.
  The process method returns the annotation, in normal or modified form, as well as storing attributes in the context of MethodMetadata.
```java
@Component
public class JdbcMethodProcessor implements AnnotatedMethodProcessor<JdbcMethod> {

  @Override
  public JdbcMethod process(AnnotationMetadata<JdbcMethod> annotationMetadata, ClassMetadata classMetadata, MethodMetadata methodMetadata) {
    JdbcMethod annotation = annotationMetadata.getAnnotation();
    //Save extra parameters in metadata
    methodMetadata.addAttribute("methodAnnotationQuery", annotation.query());
    return annotation;
  }
}
```

- **AnnotatedParameterProcessor**
  Parameter processor, which parses the defined annotation, in this case @JdbcParameter.
  The process method returns the annotation, in normal or modified form, as well as storing attributes in the context of ParameterMetadata.
```java
@Component
public class JdbcParameterProcessor implements AnnotatedParameterProcessor<JdbcParameter> {

  @Override
  public JdbcParameter process(AnnotationMetadata<JdbcParameter> annotationMetadata, ClassMetadata classMetadata, MethodMetadata methodMetadata, ParameterMetadata parameterMetadata) {
    JdbcParameter annotation = annotationMetadata.getAnnotation();
    //Save extra parameters in metadata
    parameterMetadata.addAttribute("parameterAnnotationName", annotation.value());
    return annotation;
  }
}
```

## INTERCEPTORS ##
A method can be intercepted and not processed by the contract.
A method can be intercepted by the attributes: IocTarget and method name, if it returns an empty Optional, the interceptor is not applied.
- **IocMethodInterceptor**
```java
public interface IocMethodInterceptor<T> {
  Optional<Object> invoke(Object proxy, IocTarget<?> target, Method method, Object[] args);
}
```

```java
@Component
public class JdbcInterceptor implements IocMethodInterceptor<JdbcContext> {
  @Override
  public Optional<Object> invoke(Object o, IocTarget<?> iocTarget, Method method, Object[] objects) {
    //intercepts method name "getJdbcContext"
    if (method.getName().equals("getJdbcContext")) {
      return Optional.of(JdbcContext.getInstance());
    }
    return Optional.empty();
  }
}
```

License
----

MIT
