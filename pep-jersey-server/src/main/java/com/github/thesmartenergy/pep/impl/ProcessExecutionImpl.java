package com.github.thesmartenergy.pep.impl;

/*
 * Copyright 2016 ITEA 12004 Project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import com.github.thesmartenergy.pep.*;
import java.util.concurrent.Future;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.vocabulary.RDF;

/**
 *
 * @author maxime.lefrancois
 */
public class ProcessExecutionImpl implements ProcessExecution {
    
    private final String BASE;

    private final String containerPath;

    private final String id;

    private Model model = null;

    private final Model input;

    private final Future<Model> futureOutput;

    public ProcessExecutionImpl(String BASE, String containerPath, String id, Model input, Future<Model> futureOutput) {
        this.BASE = BASE;
        this.containerPath = containerPath;
        this.id = id;
        this.input = input;
        this.futureOutput = futureOutput;
    }

    public String getId() {
        return id;
    }

    public String getContainerPath() {
        return containerPath;
    }

    public String getResourcePath() {
        return containerPath + "/" + id;
    }

    public Model getInput() {
        return input;
    }

    public Future<Model> getOutput() {
        return futureOutput;
    }
    
    public String getInputResourcePath() {
        return getResourcePath() + "/input";
    }
    
    public String getOutputResourcePath() {
        return getResourcePath() + "/output";
    }

    public Model getModel() {
        if (model != null) {
            return model;
        }
        model = ModelFactory.createDefaultModel();
        model.setNsPrefix("", BASE);
        model.setNsPrefix("pep", PEP.NS);
        model.setNsPrefix("ldp", LDP.NS);

        model.add(model.getResource(getResourcePath()),
                RDF.type,
                PEP.ProcessExecution);
        model.add(model.getResource(getResourcePath()),
                PEP.methodUsed,
                model.getResource("SmartChargingAlgorithm"));
        model.add(model.getResource(getContainerPath()),
                LDP.contains,
                model.getResource(getResourcePath()));
        model.add(model.getResource(getResourcePath()),
                PEP.generatedBy,
                model.getResource("SmartChargingProvider"));
        model.add(model.getResource(getResourcePath()),
                PEP.hasInput,
                model.getResource(getResourcePath() + "/input"));
//        if (requestDate != null) {
//            Calendar cal = Calendar.getInstance();
//            cal.setTime(requestDate);
//            model.add(model.getResource(getResourcePath() + "/input"),
//                    PROV.generatedAtTime,
//                    model.createTypedLiteral(cal));
//        }
        model.add(model.getResource(getResourcePath()),
                PEP.hasOutput,
                model.getResource(getResourcePath() + "/output"));
//        if (responseDate != null) {
//            Calendar cal = Calendar.getInstance();
//            cal.setTime(responseDate);
//            model.add(model.getResource(getResourcePath() + "/output"),
//                    PROV.generatedAtTime,
//                    model.createTypedLiteral(cal));
//        }
        return model;
    }
}
