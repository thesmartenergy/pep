/*
 * Copyright 2016 ITEA 12004 SEAS Project.
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
package com.github.thesmartenergy.pep.impl;

import com.github.thesmartenergy.pep.ContainerPath;
import com.github.thesmartenergy.pep.PEP;
import com.github.thesmartenergy.pep.PEPException;
import com.github.thesmartenergy.pep.ProcedureExecutor;
import com.github.thesmartenergy.pep.SSN;
import com.github.thesmartenergy.rdfp.BaseURI;
import com.github.thesmartenergy.rdfp.ResourceDescription;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.ws.rs.ApplicationPath;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.model.Resource;

/**
 *
 * @author Maxime Lefrançois <maxime.lefrancois at emse.fr>
 */
@ApplicationPath("_pep")
public class ProcedureExecutorConfig extends ResourceConfig {

    private static final Logger LOG = Logger.getLogger(ProcedureExecutorConfig.class.getSimpleName());

    public static final Set<String> procedureExecutionContainerPaths = new HashSet<>();

    static void init(String BASE, Instance<ProcedureExecutor> procedureExecutors) {
        ProcedureExecutorConfig.BASE = BASE;
        ProcedureExecutorConfig.procedureExecutors = procedureExecutors;
    }

    static String BASE;

    static Instance<ProcedureExecutor> procedureExecutors;

    private static Map<String, ProcedureExecutionContainer> procedureExecutionContainers = new HashMap<>();

    public ProcedureExecutorConfig() {
        packages(true, "com.github.thesmartenergy.rdfp");
        packages(true, "com.github.thesmartenergy.pep");
        
    }

    @PostConstruct
    private void postConstruct() {
        LOG.info("constructing procedure executor config with base " + BASE);
        Iterator<ProcedureExecutor> it = procedureExecutors.iterator();
        while (it.hasNext()) {
            final ProcedureExecutor procedureExecutor = it.next();
            LOG.info("constructing procedure executor " + procedureExecutor);
            final ContainerPath container = procedureExecutor.getClass().getAnnotation(ContainerPath.class);
            if (container == null) {
                LOG.warning("procedureExecutor has no containerPath annotation. Continuing.");
                continue;
            }
            String resourcePath = container.value();
            LOG.warning("procedure executor is located at " + resourcePath);
            try {
                registerProcedureExecutor(procedureExecutor, resourcePath);
            } catch (PEPException ex) {
                LOG.log(Level.WARNING, "Error while registering procedure executor " + procedureExecutor.getClass() + ": " + ex.getMessage());
            }
        }
    }

    private void registerProcedureExecutor(ProcedureExecutor procedureExecutor, String containerPath) throws PEPException {
        checkNotNull(procedureExecutor);
        checkNotNull(containerPath);
        String uri = BASE + containerPath;
        LOG.warning("registering procedure executor at path" + uri);
        ResourceDescription pec;
        try {
            pec = new ResourceDescription(uri);
        } catch (IllegalArgumentException ex) {
            throw new PEPException("cannot get a description: " + ex.getMessage());
        }
        String inputGraphURI = null;
        String outputGraphURI = null;
        try {
            Model pecModel = pec.getModel();
            org.apache.jena.rdf.model.Resource procedure = pecModel.getProperty(pec.getNode().asResource(), PEP.forProcedure).getObject().asResource();
            Statement inputGraph = pecModel.getProperty(procedure, PEP.hasInput);
            if(inputGraph == null) {
                inputGraph = pecModel.getProperty(procedure, SSN.hasInput);
            }
            inputGraphURI = inputGraph.getObject().asResource().getURI();
            Statement outputGraph = pecModel.getProperty(procedure, PEP.hasOutput);
            if(outputGraph == null) {
                outputGraph = pecModel.getProperty(procedure, SSN.hasOutput);
            }
            outputGraphURI = outputGraph.getObject().asResource().getURI();
        } catch (Exception ex) {
            throw new PEPException("Error while retrieving the input and output graph description URIs. Check the conformance with the vocabulary (and the conformance of this implementation).");
        }
        checkNotNull(inputGraphURI);
        checkNotNull(outputGraphURI);

        ProcedureExecutionContainer handler = new ProcedureExecutionContainer(procedureExecutor, containerPath, inputGraphURI, outputGraphURI);
        procedureExecutionContainers.put(containerPath, handler);

        try {
            Resource.Builder resourceBuilder;

            resourceBuilder = Resource.builder().path(containerPath);
            resourceBuilder.addMethod("POST").handledBy(ProcedureExecutionHandler.class, ProcedureExecutionHandler.class.getMethod("launchExecution"));
            registerResources(resourceBuilder.build());

            resourceBuilder = Resource.builder().path(containerPath + "/{id}");
            resourceBuilder.addMethod("GET").handledBy(ProcedureExecutionHandler.class, ProcedureExecutionHandler.class.getMethod("getExecution"));
            registerResources(resourceBuilder.build());

            resourceBuilder = Resource.builder().path(containerPath + "/{id}/command");
            resourceBuilder.addMethod("GET").handledBy(ProcedureExecutionHandler.class, ProcedureExecutionHandler.class.getMethod("getExecutionCommand"));
            registerResources(resourceBuilder.build());

            resourceBuilder = Resource.builder().path(containerPath + "/{id}/result");
            resourceBuilder.addMethod("GET").handledBy(ProcedureExecutionHandler.class, ProcedureExecutionHandler.class.getMethod("getExecutionResult"));
            registerResources(resourceBuilder.build());

            procedureExecutionContainerPaths.add(containerPath);

        } catch (NoSuchMethodException ex) {
            Logger.getLogger(ProcedureExecutorConfig.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(ProcedureExecutorConfig.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static Set<String> getContainerPaths() {
        return procedureExecutionContainers.keySet();
    }

    public static ProcedureExecutionContainer getProcedureExecutionContainer(String path) {
        for (String containerPath : getContainerPaths()) {
            if (path.startsWith(containerPath)) {
                return procedureExecutionContainers.get(containerPath);
            }
        }
        return null;
    }

    private void checkNotNull(Object object) {
        if(object == null) {
            throw new NullPointerException();
        }
    }

}
