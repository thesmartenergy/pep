# Process Execution Platform

We propose a REST-compliant way to expose processes whose inputs and outputs are described as RDF graphs.

These are called Process Execution Platforms.

There are process execution containers.

## Process Execution Containers description 

Following the Linked Data principles, Process Execution Containers are given URI identifiers, and a RDF description of these resources can be retrieved at their URI.

The PEP vocabulary can be used to describe Process Executors, the Process they implement with their input and output graph descriptions, and the Process Executions they generate with their actual input and output graphs. This vocabulary can be accessed at this URI: https://w3id.org/pep/ using content negociation (i.e., set HTTP Header field `Accept` to one of the RDF syntaxes media types, or access it directly in [turtle](.ttl), or in [RDF/XML](.rdf).

This vocabulary reuses but extends the [W3C Semantic Sensor Network ontology](https://www.w3.org/TR/vocab-ssn/), and the [SAN ontology](http://www.irit.fr/recherches/MELODI/ontologies/SAN.owl#). Instead of having a `ssn:Sensor` (resp. `san:Actuator`) that implements a `ssn:Sensing` (resp. `san:Acting`) method and generates `ssn:Observation`s (resp. `san:Actuation`s), one describe a `pep:ProcessExecutor` that implements a `pep:Process` and generates `pep:ProcessExecution`s.

For example, this website exposes and describes a Process Execution Container identified by https://w3id.org/pep/ProcessExecutionContainer Its full description uses the PEP vocabulary and can be found at URI https://w3id.org/pep/example. Use content negociation, or access it directly in [turtle](https://w3id.org/pep/example.ttl), or in [RDF/XML](https://w3id.org/pep/example.rdf).

## A Process Execution Platform

Do POST on the Process Execution Container, then ...

## Implementation over Jersey

[`pep-jersey-server`](get-started.html) is an extension of Jersey that eases the development of Process Executors.
