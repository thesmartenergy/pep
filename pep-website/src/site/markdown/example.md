# Demonstration

Resource https://w3id.org/pep/example on this website is a simple RDF Graph that describes a process executor. The container of its executions is resource https://w3id.org/pep/ProcessExecutionContainer , and this website demonstrates the use of the `pep-jersey-server` implementation.

The resource takes as input a set of random number generation requests, with a min and a max for each of the generated numbers. It outputs the generated random numbers.

The input and the output can be presented in several ways, as defined by the [RDF Presentation](https://w3id.org/rdfp/) negociation protocol:

 - input graphs are described by https://w3id.org/pep/input (negotiate its representation with the server, or directly access the [turtle](https://w3id.org/pep/example.ttl), or a [RDF/XML](https://w3id.org/pep/example.rdf) document). Presentations are given in RDF/XML, Turtle, XML, JSON, and any client can define its own.
 - output graphs are described by https://w3id.org/pep/output. Presentations are given in RDF/XML, Turtle, XML, JSON, and any client can define its own.


## requesting random number generations

In order to request random number generations, one may operate a POST at https://w3id.org/pep/ProcessExecutionContainer. Examples of inputs can be found at URL https://w3id.org/rdfp/example/input. Negotiate its representation with the server, or directly access:
-  https://w3id.org/pep/input/example.txt
-  https://w3id.org/pep/input/example.json
-  https://w3id.org/pep/input/example.xml
-  https://w3id.org/pep/input/example.ttl

Then as a proper REST application, the server should respond with code `201 Created`, and HTTP header field `Location` set to the location of the newly created process execution. For instance, https://w3id.org/pep/RandomNumberGeneration/lhldf69mh4mudnsps04qu0g9qh .

## accessing the process execution

One may request for the RDF description of the process execution at its URL. For example (example in Turtle):

```
@base          <https://w3id.org/pep/> .
@prefix :      <https://w3id.org/pep/> .
@prefix pep:   <https://w3id.org/pep/> .

<RandomNumberGeneration/lhldf69mh4mudnsps04qu0g9qh>
        a                pep:ProcessExecution ;
        pep:generatedBy  <SmartChargingProvider> ;
        pep:hasInput     <RandomNumberGeneration/lhldf69mh4mudnsps04qu0g9qh/input> ;
        pep:hasOutput    <RandomNumberGeneration/lhldf69mh4mudnsps04qu0g9qh/output> ;
        pep:methodUsed   <SmartChargingAlgorithm> .
```


## accessing the input and output of the process execution

One may request for the RDF description of the input and output of the process execution at their URLs. 

As mentionned previously, t he input and the output can be presented in several ways, as defined by the [RDF Presentation](https://w3id.org/rdfp/) negociation protocol.

 - input graphs are described by https://w3id.org/pep/input (negotiate its representation with the server, or directly access the [turtle](https://w3id.org/pep/example.ttl), or a [RDF/XML](https://w3id.org/pep/example.rdf) document). Presentations are given in RDF/XML, Turtle, XML, JSON, and any client can define its own.
 - output graphs are described by https://w3id.org/pep/output. Presentations are given in RDF/XML, Turtle, XML, JSON, and any client can define its own.

Examples of outputs can be found at URL https://w3id.org/pep/output/example. Negotiate its representation with the server, or directly access:
-  https://w3id.org/pep/output/example.txt
-  https://w3id.org/pep/output/example.json
-  https://w3id.org/pep/output/example.xml
-  https://w3id.org/pep/output/example.ttl


