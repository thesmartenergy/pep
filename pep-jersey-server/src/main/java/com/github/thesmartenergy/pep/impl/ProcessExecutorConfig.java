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
import com.github.thesmartenergy.pep.ProcessExecutor;
import com.github.thesmartenergy.rdfp.BaseURI;
import com.github.thesmartenergy.rdfp.resources.ResourceDescription;
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
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.model.Resource;
import static jersey.repackaged.com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * @author Maxime Lefrançois <maxime.lefrancois at emse.fr>
 */
@ApplicationPath("_pep")
public class ProcessExecutorConfig extends ResourceConfig {

    private static final Logger LOG = Logger.getLogger(ProcessExecutorConfig.class.getSimpleName());

    public static final Set<String> processExecutionContainerPaths = new HashSet<>();

    @Inject
    @BaseURI
    String BASE;

    @Inject
    @Any
    Instance<ProcessExecutor> processExecutors;

    private static Map<String, ProcessExecutionContainer> processExecutionContainers = new HashMap<>();

    public ProcessExecutorConfig() {
        packages("com.github.thesmartenergy.rdfp");
        packages(true, "com.github.thesmartenergy.pep");
    }

    @PostConstruct
    private void postConstruct() {
        Iterator<ProcessExecutor> it = processExecutors.iterator();
        while (it.hasNext()) {
            final ProcessExecutor processExecutor = it.next();
            final ContainerPath container = processExecutor.getClass().getAnnotation(ContainerPath.class);
            if (container == null) {
                continue;
            }
            String resourcePath = container.value();
            try {
                registerProcessExecutor(processExecutor, resourcePath);
            } catch (PEPException ex) {
                LOG.log(Level.WARNING, "Error while registering process executor " + processExecutor.getClass() + ": " + ex.getMessage());
            }
        }
    }

    private void registerProcessExecutor(ProcessExecutor processExecutor, String containerPath) throws PEPException {
        checkNotNull(processExecutor);
        checkNotNull(containerPath);
        String uri = BASE + containerPath;
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
            org.apache.jena.rdf.model.Resource process = pecModel.getProperty(pec.getNode().asResource(), PEP.forProcess).getObject().asResource();
            inputGraphURI = pecModel.getProperty(process, PEP.hasInput).getObject().asResource().getURI();
            outputGraphURI = pecModel.getProperty(process, PEP.hasOutput).getObject().asResource().getURI();
        } catch (Exception ex) {
            throw new PEPException("Error while retrievign the input and output graph description URIs. Check the conformance with the vocabulary (and the conformance of this implementation).");
        }
        checkNotNull(inputGraphURI);
        checkNotNull(outputGraphURI);

        ProcessExecutionContainer handler = new ProcessExecutionContainer(processExecutor, containerPath, inputGraphURI, outputGraphURI);
        processExecutionContainers.put(containerPath, handler);

        try {
            Resource.Builder resourceBuilder;

            resourceBuilder = Resource.builder().path(containerPath);
            resourceBuilder.addMethod("POST").handledBy(ProcessExecutionHandler.class, ProcessExecutionHandler.class.getMethod("launchExecution"));
            registerResources(resourceBuilder.build());

            resourceBuilder = Resource.builder().path(containerPath + "/{id}");
            resourceBuilder.addMethod("GET").handledBy(ProcessExecutionHandler.class, ProcessExecutionHandler.class.getMethod("getExecution"));
            registerResources(resourceBuilder.build());

            resourceBuilder = Resource.builder().path(containerPath + "/{id}/input");
            resourceBuilder.addMethod("GET").handledBy(ProcessExecutionHandler.class, ProcessExecutionHandler.class.getMethod("getExecutionInput"));
            registerResources(resourceBuilder.build());

            resourceBuilder = Resource.builder().path(containerPath + "/{id}/output");
            resourceBuilder.addMethod("GET").handledBy(ProcessExecutionHandler.class, ProcessExecutionHandler.class.getMethod("getExecutionOutput"));
            registerResources(resourceBuilder.build());

            processExecutionContainerPaths.add(containerPath);

        } catch (NoSuchMethodException ex) {
            Logger.getLogger(ProcessExecutorConfig.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(ProcessExecutorConfig.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static Set<String> getContainerPaths() {
        return processExecutionContainers.keySet();
    }

    public static ProcessExecutionContainer getProcessExecutionContainer(String path) {
        for (String containerPath : getContainerPaths()) {
            if (path.startsWith(containerPath)) {
                return processExecutionContainers.get(containerPath);
            }
        }
        return null;
    }

}
