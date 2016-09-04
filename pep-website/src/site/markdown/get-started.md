# Get Started

`pep-jersey-server` eases the development of Web applications that expose Process Execution Containers. 
All the developper has to do is develop a class that extends `ProcessExecutor` and is annotated with `@ContainerPath`:

```java
@ContainerPath("RandomNumberGeneration") // the local path of the container on the server
public class RandomNumberGenerator implements ProcessExecutor {

    public Future<Model> execute(Model input) throws PEPException {...}

    public void create(ProcessExecution processExecution) throws PEPException {...}

    public ProcessExecution find(String id) {...}

    ...

}
```

## Maven project

Binaries, sources and documentation for `pep-jersey-server` are available for download at [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Cpep-jersey%22). To use it in your Maven project, add the following dependency declaration to your Maven project file ( `*.pom` file):
 
```xml
<dependency>
    <groupId>com.github.thesmartenergy</groupId>
    <artifactId>pep-jersey-server</artifactId>
    <version>${pep-jersey.version}</version>
</dependency>
```

The [javadoc](https://w3id.org/pep/apidocs/index.html) contains comprehensive documentations and examples, and the [sources of this website](https://github.com/thesmartenergy/pep/tree/master/rdfp-website) is a simple web project to get started with `pep-jersey-server`. 

