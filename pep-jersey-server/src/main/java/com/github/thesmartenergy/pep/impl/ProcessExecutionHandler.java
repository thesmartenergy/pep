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

import com.github.thesmartenergy.pep.PEPException;
import com.github.thesmartenergy.pep.ProcessExecution;
import com.github.thesmartenergy.pep.ProcessExecutor;
import com.github.thesmartenergy.rdfp.BaseURI;
import com.github.thesmartenergy.rdfp.jersey.ModelReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.SecureRandom;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.apache.jena.rdf.model.Model;

/**
 *
 * @author Maxime Lefrançois <maxime.lefrancois at emse.fr>
 */
public class ProcessExecutionHandler {

    @Inject
    @BaseURI
    private String BASE;

    @Inject
    private ModelReader modelReader;

    @Context
    private ContainerRequestContext containerRequestContext;

    @Context
    private UriInfo uriInfo;

    private ProcessExecutionContainer processExecutionContainer;

    private ProcessExecutor processExecutor;

    @PostConstruct
    void postConstruct() {
        processExecutionContainer = ProcessExecutorConfig.getProcessExecutionContainer(uriInfo.getPath());
        processExecutor = processExecutionContainer.getProcessExecutor();
    }
    

    public Response launchExecution() {
        String pep = (String) containerRequestContext.getProperty("pep");
        if(pep==null || !pep.equals("pep")) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Model input;
        try {
            Annotation[] annotations = new Annotation[]{processExecutionContainer.getInputGraphAnnotation()};
            MediaType mediaType = containerRequestContext.getMediaType();
            MultivaluedMap<String, String> httpHeaders = containerRequestContext.getHeaders();
            InputStream in = containerRequestContext.getEntityStream();
            input = modelReader.readFrom(Model.class, null, annotations, mediaType, httpHeaders, in);
        } catch (IOException | WebApplicationException ex) {
            return sendError("Error while reading the input", ex);
        }

        Future<Model> futureOutput;
        try {
            futureOutput = processExecutor.execute(input);
        } catch (PEPException ex) {
            return sendError("Error while launching the execution", ex);
        }
        String id = new BigInteger(130, new SecureRandom()).toString(32);
        ProcessExecution processExecution = new ProcessExecutionImpl(BASE, processExecutionContainer.getContainerPath(), id, input, futureOutput);
        try {
            processExecutor.create(processExecution);
        } catch (PEPException ex) {
            return Response.serverError().entity("could not create process execution. Error: " + ex.getMessage()).build();
        }

        try {
            URI uri = new URI(BASE + processExecution.getResourcePath());
            return Response.created(uri)
                    .header("Location-Input", BASE + processExecution.getInputResourcePath())
                    .header("Location-Output", BASE + processExecution.getOutputResourcePath())
                    .build();
        } catch (URISyntaxException ex) {
            throw new IllegalArgumentException(ex.getMessage());
        }
    }

    public Response getExecution() {
        String pep = (String) containerRequestContext.getProperty("pep");
        if(pep==null || !pep.equals("pep")) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        String id = uriInfo.getPathParameters().getFirst("id");
        try {
            ProcessExecution execution = processExecutor.find(id);
            return Response.ok(execution.getModel()).build();
        } catch (Exception ex) {
            return Response.status(Response.Status.NOT_FOUND).entity(ex.getMessage()).build();
        }
    }

    public Response getExecutionInput() {
        String pep = (String) containerRequestContext.getProperty("pep");
        if(pep==null || !pep.equals("pep")) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        String id = uriInfo.getPathParameters().getFirst("id");
        ProcessExecution execution = processExecutor.find(id);
        if (execution == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        Model input = execution.getInput();
        if (input == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok().entity(input, new Annotation[]{processExecutionContainer.getInputGraphAnnotation()}).build();
    }

    public Response getExecutionOutput() {
        String pep = (String) containerRequestContext.getProperty("pep");
        if(pep==null || !pep.equals("pep")) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        String id = uriInfo.getPathParameters().getFirst("id");
        ProcessExecution execution = processExecutor.find(id);
        if (execution == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        Future<Model> output = execution.getOutput();
        if (output == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        try {
            Model model = output.get(1, TimeUnit.SECONDS);
            Annotation[] annotations = new Annotation[]{processExecutionContainer.getOutputGraphAnnotation()};
            return Response.ok().entity(model, annotations).build();
        } catch (ExecutionException ex) {
            return sendError("An error occurred during the execution.", ex);
        } catch (InterruptedException ex) {
            return sendError("The process execution was interrupted.", ex);
        } catch (TimeoutException ex) {
            return Response.status(Response.Status.NOT_FOUND).entity("The process execution output is not available yet. Please try again later").build();
        }
    }

    protected void checkNotNull(Object o) throws PEPException {
        if (o == null) {
            throw new PEPException("should not be null !");
        }
    }

    protected Response sendError(String message, Exception ex) {
        return Response.serverError().entity(message + " \n caused by: " + ex.getMessage()).build();
    }

}
