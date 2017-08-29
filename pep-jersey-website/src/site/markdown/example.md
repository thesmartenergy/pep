# Demonstration

Resource https://ci.mines-stetienne.fr/pep-platform/example on this website is a simple RDF Graph that describes a procedure executor identified by https://ci.mines-stetienne.fr/pep-platform/RandomNumberGenerator.  (negotiate its representation with the server, or directly access the [turtle](https://ci.mines-stetienne.fr/pep-platform/example.ttl), or a [RDF/XML](https://ci.mines-stetienne.fr/pep-platform/example.rdf) document)

This procedure executor takes as input a set of random number generation requests, with a min and a max for each of the generated numbers. Once the procedure is over (after a random number of seconds), it outputs the generated random numbers.

The procedure execution container is identified https://ci.mines-stetienne.fr/pep-platform/RandomNumberGeneration.

To request random numbers generations, one may issue a POST at https://ci.mines-stetienne.fr/pep-platform/RandomNumberGeneration. 

For instance, 

```
POST /pep-platform/RandomNumberGeneration (--> POST https://ci.mines-stetienne.fr/pep-platform/RandomNumberGeneration)
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
Location: https://ci.mines-stetienne.fr/pep-platform/RandomNumberGeneration/lhldf69mh4mudnsps04qu0g9qh
Location-Input: https://ci.mines-stetienne.fr/pep-platform/RandomNumberGeneration/lhldf69mh4mudnsps04qu0g9qh/input
Location-Output: https://ci.mines-stetienne.fr/pep-platform/RandomNumberGeneration/lhldf69mh4mudnsps04qu0g9qh/output
```

**Process execution**: One may access a representation of the procedure execution state at its URL, for example in the RDF Turtle format. This is done by issuing a GET request at its URL:


```
GET /pep-platform/RandomNumberGeneration/lhldf69mh4mudnsps04qu0g9qh
Accept: text/turtle

```

If successful, the server should send a code 200 OK, and a payload such as: 

```
@prefix pep:   <https://w3id.org/pep/> .
@prefix sosa:   <http://www.w3.org/ns/sosa/> .

@base          <https://ci.mines-stetienne.fr/pep-platform/> .

<RandomNumberGeneration/lhldf69mh4mudnsps04qu0g9qh>
        a                  pep:ProcessExecution ;
        pep:generatedBy    <RandomNumberGenerator> ;
        pep:hasCommand     <RandomNumberGeneration/lhldf69mh4mudnsps04qu0g9qh/input> ;
        pep:hasResult      <RandomNumberGeneration/lhldf69mh4mudnsps04qu0g9qh/output> ;
        sosa:hasResult      <RandomNumberGeneration/lhldf69mh4mudnsps04qu0g9qh/output> ;
        sosa:usedProcedure  <RandomNumberGenerationAlgorithm> ;
        pep:usedProcedure  <RandomNumberGenerationAlgorithm> .
```

**Process execution command and result**: one may access a representation of the procedure execution command or result states at their URL, for example in the JSON format. This is done by issuing a GET request at their URL. For example for the result:

```
GET /pep-platform/RandomNumberGeneration/lhldf69mh4mudnsps04qu0g9qh/output
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

Commands and results can be presented in several ways using the [RDF Presentation](https://w3id.org/rdfp/) negotiation protocol. They are RDF graphs that are described by the input and output of the procedure,  respectively:

Command graphs are described by the input that the procedure execution used. Representations of the command graphs can be given in Turtle, XML, JSON, or any other format at the discretion of the developer.

An example of a command graph can be found at URL https://ci.mines-stetienne.fr/pep-platform/command/example. It has multiple representations:

-  https://ci.mines-stetienne.fr/pep-platform/command/example.json
-  https://ci.mines-stetienne.fr/pep-platform/command/example.xml
-  https://ci.mines-stetienne.fr/pep-platform/command/example.ttl


Result graphs are described by the output that the procedure execution used. Representations of the result graphs can be given in Turtle, XML, JSON, or any other format at the discretion of the developer.

An example of a result graph can be found at URL https://ci.mines-stetienne.fr/pep-platform/result/example. It has multiple representations:

-  https://ci.mines-stetienne.fr/pep-platform/result/example.json
-  https://ci.mines-stetienne.fr/pep-platform/result/example.xml
-  https://ci.mines-stetienne.fr/pep-platform/result/example.ttl
