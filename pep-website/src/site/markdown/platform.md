# Process Execution Platform

Process execution platforms expose [process executors](https://w3id.org/pep/ProcessExecutor) on the Web in a RESTful way.

### Interactions

a client can request to create a process execution by issuing a POST request to one of the *process execution containers* of a process executor. 

The content of this request is a representation of the input of the Process.

If successfully created, the process execution container returns links to the location of the created resource, and optionally the created input and output resources. 

Then, a client can interact in a CRUD-like manner with these resources.

In particular, representations of a process execution MUST contain links to the output resource, if available.

#### HTTP Protocol Binding

The response of a process execution creation request contains HTTP headers:

- `Location` that link to the location of the created resource;
- optionally, `Location-Input` links to the location of the input resource;
- optionally, `Location-Output` links to the location of the output resource.


## Process execution platforms are based on the Semantic Web formalisms

Process execution platforms use the linked data principles. Following these principles, process executors are identified by URIs, and an RDF description of these resources can be retrieved at their URI. From there, one may discover how to interact with them:

- the identifier of its process execution containers;
- the description of the input and outputs.

For example, this website exposes and describes a Process Execution Container identified by https://w3id.org/pep/RandomNumberGeneration.

Moreover, process execution platforms assume that process executions, their inputs and their outputs are RDF resources. That is, [information resources](https://www.w3.org/TR/webarch/#def-information-resource) whose essential characteristics can be conveyed in an RDF Graph. 

Note that this restriction does not imply that only RDF syntaxes can be used. Indeed, [the RDF Presentation and RDF Presentation negotiation protocol](https://w3id.org/rdfp/) allows clients and servers to negotiate the representation type of RDF Graph resources.

Process execution platforms MAY also be [Linked Data platforms](https://www.w3.org/TR/ldp/).

## Implementation over Jersey

[`pep-jersey-server`](get-started.html) is an Java library that eases the development of Web applications that expose process executors.


