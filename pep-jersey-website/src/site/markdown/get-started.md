# Get Started

`pep-jersey-server` eases the development of [Procedure Execution Platforms](platform.html). 

All the developper has to do is develop a class that extends `ProcedureExecutor` and is annotated with `@ContainerPath`:

```java
@ContainerPath("RandomNumberGeneration") // the local path of the container on the server
public class RandomNumberGenerator implements ProcedureExecutor {

    public Future<Model> execute(Model command) throws PEPException {...}

    public void create(ProcedureExecution procedureExecution) throws PEPException {...}

    public ProcedureExecution find(String id) {...}

    ...

}
```

Two utility abstract classes further ease the implementation of the `ProcedureExecutor` interface:

- `ProcedureExecutorMap` stores procedure executions in a HashMap
- `ProcedureExecutorDataset` stores procedure executions in a Dataset.

## Maven project

Binaries, sources and documentation for `pep-jersey-server` are available for download at [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Cpep-jersey%22). To use it in your Maven project, add the following dependency declaration to your Maven project file ( `*.pom` file):
 
```xml
<dependency>
    <groupId>com.github.thesmartenergy</groupId>
    <artifactId>pep-jersey-server</artifactId>
    <version>${pep-jersey.version}</version>
</dependency>
```

The [javadoc](https://w3id.org/pep/apidocs/index.html) contains comprehensive documentations and examples, and the [sources of this website](https://github.com/thesmartenergy/pep/tree/master/rdfp-jersey-website) is a simple web project to get started with `pep-jersey-server`. 

