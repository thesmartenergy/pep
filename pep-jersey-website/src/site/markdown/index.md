# Procedure Execution Platform

Procedure execution platforms expose [procedure executors](https://w3id.org/pep/ProcedureExecutor) on the Web in a RESTful way.

### Interactions

A client can request to create a procedure execution by issuing a POST request to one of the *procedure execution containers* of a procedure executor. 

The content of this request is a representation of the command of the Procedure.

If successfully created, the procedure execution container returns links to the location of the created resource, and optionally the created command and result resources. 

Then, a client can interact in a CRUD-like manner with these resources.

In particular, representations of a procedure execution MUST contain links to the result resource, if available.

#### HTTP Protocol Binding

The response of a procedure execution creation request contains HTTP headers:

- `Location` that link to the location of the created resource;
- optionally, `Location-Command` links to the location of the command resource;
- optionally, `Location-Result` links to the location of the result resource.


## Procedure execution platforms are based on the Semantic Web formalisms

Procedure execution platforms use the linked data principles. Following these principles, procedure executors are identified by URIs, and an RDF description of these resources can be retrieved at their URI. From there, one may discover how to interact with them:

- the identifier of its procedure execution containers;
- the description of the command and results.

For example, this website exposes and describes a Procedure Execution Container identified by https://ci.mines-stetienne.fr/pep-platform/RandomNumberGeneration.

Moreover, procedure execution platforms assume that procedure executions, their commands and their results are RDF resources. That is, [information resources](https://www.w3.org/TR/webarch/#def-information-resource) whose essential characteristics can be conveyed in an RDF Graph. 

Note that this restriction does not imply that only RDF syntaxes can be used. Indeed, [the RDF Presentation and RDF Presentation negotiation protocol](https://w3id.org/rdfp/) allows clients and servers to negotiate the representation type of RDF Graph resources.

Procedure execution platforms MAY also be [Linked Data platforms](https://www.w3.org/TR/ldp/).

## Implementation over Jersey

[`pep-jersey-server`](get-started.html) is an Java library that eases the development of Web applications that expose procedure executors.


