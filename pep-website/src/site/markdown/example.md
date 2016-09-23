# Demonstration

Resource https://w3id.org/pep/example on this website is a simple RDF Graph that describes a process executor identified by https://w3id.org/pep/RandomNumberGenerator.  (negotiate its representation with the server, or directly access the [turtle](https://w3id.org/pep/example.ttl), or a [RDF/XML](https://w3id.org/pep/example.rdf) document)

This process executor takes as input a set of random number generation requests, with a min and a max for each of the generated numbers. Once the process is over (after a random number of seconds), it outputs the generated random numbers.

The process execution container is identified https://w3id.org/pep/RandomNumberGeneration.

To request random numbers generations, one may issue a POST at http://ci.emse.fr/pep/RandomNumberGeneration. 

*note: POST at https://w3id.org/pep/RandomNumberGeneration does not work, use http://ci.emse.fr/pep/RandomNumberGeneration *

For instance, 

```
POST /pep/RandomNumberGeneration (--> POST http://ci.emse.fr/pep/RandomNumberGeneration)
Content-Type: application/json

[{
  "id": "number1",
  "min": 0.0,
  "max": 1.0
}, {
  "id": "number2",
  "min": 0.0,
  "max": 100.0
}]

```

Then the server should respond with code `201 Created`, and a set of header fields: 

```
Location: https://w3id.org/pep/RandomNumberGeneration/lhldf69mh4mudnsps04qu0g9qh
Location-Input: https://w3id.org/pep/RandomNumberGeneration/lhldf69mh4mudnsps04qu0g9qh/input
Location-Output: https://w3id.org/pep/RandomNumberGeneration/lhldf69mh4mudnsps04qu0g9qh/output
```

**Process execution**: One may access a representation of the process execution state at its URL, for example in the RDF Turtle format. This is done by issuing a GET request at its URL:


```
GET /pep/RandomNumberGeneration/lhldf69mh4mudnsps04qu0g9qh
Accept: text/turtle

```

If successful, the server should send a code 200 OK, and a payload such as: 

```
@base          <https://w3id.org/pep/> .
@prefix pep:   <https://w3id.org/pep/> .

<RandomNumberGeneration/lhldf69mh4mudnsps04qu0g9qh>
        a                pep:ProcessExecution ;
        pep:generatedBy  <RandomNumberGenerator> ;
        pep:hasInput     <RandomNumberGeneration/lhldf69mh4mudnsps04qu0g9qh/input> ;
        pep:hasOutput    <RandomNumberGeneration/lhldf69mh4mudnsps04qu0g9qh/output> ;
        pep:methodUsed   <RandomNumberGenerationAlgorithm> .
```

**Process execution input and output**: one may access a representation of the process execution input or output states at their URL, for example in the JSON format. This is done by issuing a GET request at their URL. For example for the output:

```
GET /pep/RandomNumberGeneration/lhldf69mh4mudnsps04qu0g9qh/output
Accept: text/turtle

```

If successful, the server should send a code 200 OK, and a payload such as: 

```
[{
  "id": "number1",
  "value": 0.234,
}, {
  "id": "number2",
  "value": 34.673
}]
```


## Using the RDF Presentation negotiation protocol

Although the input and the output are RDF resources, they can be presented in several ways, as defined by the [RDF Presentation](https://w3id.org/rdfp/) negotiation protocol:

Input graphs are described by https://w3id.org/pep/input. Presentations are given in Turtle, XML, JSON, but any client can define its own.

An example of an input can be found at URL https://w3id.org/rdfp/example/input. Negotiate its representation with the server, or directly access:

-  https://w3id.org/pep/input/example.json
-  https://w3id.org/pep/input/example.xml
-  https://w3id.org/pep/input/example.ttl


Output graphs are described by https://w3id.org/pep/output. Presentations are given in Turtle, XML, JSON, but any client can define its own.

An example of an output can be found at URL https://w3id.org/pep/output/example. Negotiate its representation with the server, or directly access:

-  https://w3id.org/pep/output/example.json
-  https://w3id.org/pep/output/example.xml
-  https://w3id.org/pep/output/example.ttl

