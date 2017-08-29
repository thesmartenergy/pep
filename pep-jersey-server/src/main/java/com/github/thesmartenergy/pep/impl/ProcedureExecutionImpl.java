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
public class ProcedureExecutionImpl implements ProcedureExecution {
    
    private final String BASE;

    private final String containerPath;

    private final String id;

    private Model model = null;

    private final Model command;

    private final Future<Model> futureResult;

    public ProcedureExecutionImpl(String BASE, String containerPath, String id, Model command, Future<Model> futureResult) {
        this.BASE = BASE;
        this.containerPath = containerPath;
        this.id = id;
        this.command = command;
        this.futureResult = futureResult;
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

    public Model getCommand() {
        return command;
    }

    public Future<Model> getResult() {
        return futureResult;
    }
    
    public String getCommandResourcePath() {
        return getResourcePath() + "/command";
    }
    
    public String getResultResourcePath() {
        return getResourcePath() + "/result";
    }

    public Model getModel() {
        if (model != null) {
            return model;
        }
        model = ModelFactory.createDefaultModel();
        model.setNsPrefix("", BASE);
        model.setNsPrefix("pep", PEP.NS);
        model.setNsPrefix("ldp", LDP.NS);
        model.setNsPrefix("sosa", SOSA.NS);
        model.setNsPrefix("ssn", SSN.NS);

        model.add(model.getResource(getResourcePath()),
                RDF.type,
                PEP.ProcedureExecution);
        model.add(model.getResource(getContainerPath()),
                LDP.contains,
                model.getResource(getResourcePath()));
        model.add(model.getResource(getResourcePath()),
                PEP.hasCommand,
                model.getResource(getResourcePath() + "/command"));
        model.add(model.getResource(getResourcePath()),
                PEP.hasResult,
                model.getResource(getResourcePath() + "/result"));
        model.add(model.getResource(getResourcePath()),
                SOSA.hasResult,
                model.getResource(getResourcePath() + "/result"));
        return model;
    }
}
