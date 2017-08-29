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
import com.github.thesmartenergy.pep.ProcedureExecution;
import com.github.thesmartenergy.pep.ProcedureExecutor;
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
public class ProcedureExecutionHandler {

    @Inject
    @BaseURI
    private String BASE;

    @Inject
    private ModelReader modelReader;

    @Context
    private ContainerRequestContext containerRequestContext;

    @Context
    private UriInfo uriInfo;

    private ProcedureExecutionContainer procedureExecutionContainer;

    private ProcedureExecutor procedureExecutor;

    @PostConstruct
    void postConstruct() {
        System.out.println("CONSTRUCT FOR " + uriInfo.getPath());
        procedureExecutionContainer = ProcedureExecutorConfig.getProcedureExecutionContainer(uriInfo.getPath());
        System.out.println("Container " + procedureExecutionContainer);
        procedureExecutor = procedureExecutionContainer.getProcedureExecutor();
    }
    

    public Response launchExecution() {
        String pep = (String) containerRequestContext.getProperty("pep");
        if(pep==null || !pep.equals("pep")) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Model command;
        try {
            Annotation[] annotations = new Annotation[]{procedureExecutionContainer.getInputGraphAnnotation()};
            MediaType mediaType = containerRequestContext.getMediaType();
            MultivaluedMap<String, String> httpHeaders = containerRequestContext.getHeaders();
            InputStream in = containerRequestContext.getEntityStream();
            command = modelReader.readFrom(Model.class, null, annotations, mediaType, httpHeaders, in);
        } catch (IOException | WebApplicationException ex) {
            return sendError("Error while reading the input", ex);
        }

        Future<Model> futureResult;
        try {
            futureResult = procedureExecutor.execute(command);
        } catch (PEPException ex) {
            return sendError("Error while launching the execution", ex);
        }
        String id = new BigInteger(130, new SecureRandom()).toString(32);
        ProcedureExecution procedureExecution = new ProcedureExecutionImpl(BASE, procedureExecutionContainer.getContainerPath(), id, command, futureResult);
        try {
            procedureExecutor.create(procedureExecution);
        } catch (PEPException ex) {
            return Response.serverError().entity("could not create procedure execution. Error: " + ex.getMessage()).build();
        }

        try {
            URI uri = new URI(BASE + procedureExecution.getResourcePath());
            return Response.created(uri)
                    .header("Location-Command", BASE + procedureExecution.getCommandResourcePath())
                    .header("Location-Result", BASE + procedureExecution.getResultResourcePath())
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
            ProcedureExecution execution = procedureExecutor.find(id);
            return Response.ok(execution.getModel()).build();
        } catch (Exception ex) {
            return Response.status(Response.Status.NOT_FOUND).entity(ex.getMessage()).build();
        }
    }

    public Response getExecutionCommand() {
        String pep = (String) containerRequestContext.getProperty("pep");
        if(pep==null || !pep.equals("pep")) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        String id = uriInfo.getPathParameters().getFirst("id");
        ProcedureExecution execution = procedureExecutor.find(id);
        if (execution == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        Model command = execution.getCommand();
        if (command == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok().entity(command, new Annotation[]{procedureExecutionContainer.getInputGraphAnnotation()}).build();
    }

    public Response getExecutionResult() {
        String pep = (String) containerRequestContext.getProperty("pep");
        if(pep==null || !pep.equals("pep")) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        String id = uriInfo.getPathParameters().getFirst("id");
        ProcedureExecution execution = procedureExecutor.find(id);
        if (execution == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        Future<Model> result = execution.getResult();
        if (result == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        try {
            Model model = result.get(1, TimeUnit.SECONDS);
            Annotation[] annotations = new Annotation[]{procedureExecutionContainer.getOutputGraphAnnotation()};
            return Response.ok().entity(model, annotations).build();
        } catch (ExecutionException ex) {
            return sendError("An error occurred during the execution.", ex);
        } catch (InterruptedException ex) {
            return sendError("The procedure execution was interrupted.", ex);
        } catch (TimeoutException ex) {
            return Response.status(Response.Status.NOT_FOUND).entity("The procedure execution result is not available yet. Please try again later").build();
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
