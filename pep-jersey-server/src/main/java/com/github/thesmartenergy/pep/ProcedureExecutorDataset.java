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
package com.github.thesmartenergy.pep;

import com.github.thesmartenergy.pep.impl.ProcedureExecutionImpl;
import com.github.thesmartenergy.rdfp.BaseURI;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.tdb.TDB;
import org.apache.jena.vocabulary.RDF;

/**
 * Abstract implementation of a <code>ProcedureExecutor</code> that uses a Jena
 * <code>Dataset</code> to store the procedure executions.
 *
 * @author Maxime Lefrançois <maxime.lefrancois at emse.fr>
 */
public abstract class ProcedureExecutorDataset implements ProcedureExecutor {

    private static final Logger LOG = Logger.getLogger(ProcedureExecutorDataset.class.getSimpleName());

    @Inject
    @BaseURI
    private String BASE;

    @Inject
    private Dataset dataset;

    private String containerUri;

    @PostConstruct
    public void construct() {
        ContainerPath containerPath = this.getClass().getAnnotation(ContainerPath.class);
        if (containerPath != null) {
            containerUri = BASE + containerPath.value();
        }
        initDataset();
    }

    private final void initDataset() {
        dataset.begin(ReadWrite.READ);
        boolean exists = dataset.containsNamedModel(containerUri);
        dataset.end();
        if (!exists) {
            dataset.begin(ReadWrite.WRITE);
            Model model = ModelFactory.createDefaultModel();
            model.add(model.getResource(containerUri),
                    RDF.type,
                    LDP.BasicContainer);
            dataset.addNamedModel(containerUri, model);
            dataset.commit();
            dataset.end();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void create(ProcedureExecution procedureExecution) throws PEPException {
        String resourceUri = BASE + procedureExecution.getResourcePath();
        dataset.begin(ReadWrite.READ);
        boolean exists = dataset.containsNamedModel(resourceUri);
        dataset.end();
        if (exists) {
            throw new PEPException("Procedure execution with id " + procedureExecution.getId() + " already exists. Edit it instead.");
        }
        try {
            dataset.begin(ReadWrite.WRITE);
            Model model = dataset.getNamedModel(containerUri);
            model.add(model.getResource(containerUri),
                    LDP.contains,
                    model.getResource(resourceUri));
            dataset.addNamedModel(resourceUri, procedureExecution.getModel());
            dataset.addNamedModel(BASE + procedureExecution.getCommandResourcePath(), procedureExecution.getCommand());

            Future<Model> futureModel = procedureExecution.getResult();
            if (futureModel != null) {
                if (futureModel.isDone()) {
                    try {
                        dataset.addNamedModel(BASE + procedureExecution.getResultResourcePath(), futureModel.get());
                    } catch (ExecutionException | InterruptedException ex) {
                    }
                } else {
                    ProcedureExecutionCompletor.ongoingExecutions.add(procedureExecution);
                }
            }
            dataset.commit();
            dataset.end();
            TDB.sync(dataset);
        } catch (Exception ex) {
            throw new PEPException("Error while creating procedure execution with id " + procedureExecution.getId() + ": " + ex.getClass().getName() + ": " + ex.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProcedureExecution find(String id) {
        String resourceUri = containerUri + "/" + id;
        dataset.begin(ReadWrite.READ);
        boolean exists = dataset.containsNamedModel(resourceUri);
        dataset.end();
        if (!exists) {
            return null;
        }
        dataset.begin(ReadWrite.READ);
        Model input = dataset.getNamedModel(resourceUri + "/command");
        Model output = dataset.getNamedModel(resourceUri + "/result");
        Future<Model> futureOutput = null;
        if (output != null) {
            futureOutput = CompletableFuture.completedFuture(output);
        }
        dataset.end();
        return new ProcedureExecutionImpl(BASE, containerUri.substring(BASE.length()), id, input, futureOutput);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(ProcedureExecution procedureExecution) throws PEPException {
        String resourceUri = BASE + procedureExecution.getResourcePath();
        dataset.begin(ReadWrite.READ);
        boolean exists = dataset.containsNamedModel(resourceUri);
        dataset.end();
        if (exists) {
            throw new PEPException("Procedure execution with id " + procedureExecution.getId() + " does not exist. Create it first.");
        }
        try {
            dataset.begin(ReadWrite.WRITE);
            dataset.replaceNamedModel(resourceUri, procedureExecution.getModel());
            dataset.replaceNamedModel(BASE + procedureExecution.getCommandResourcePath(), procedureExecution.getCommand());
            Future<Model> futureModel = procedureExecution.getResult();
            if (futureModel != null && futureModel.isDone()) {
                try {
                    dataset.addNamedModel(BASE + procedureExecution.getResultResourcePath(), futureModel.get());
                } catch (ExecutionException | InterruptedException ex) {
                }
            }
            dataset.commit();
            dataset.end();
            TDB.sync(dataset);
        } catch (Exception ex) {
            throw new PEPException("Error while editing procedure execution with id " + procedureExecution.getId() + ": " + ex.getClass().getName() + ": " + ex.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy(ProcedureExecution procedureExecution) throws PEPException {
        dataset.begin(ReadWrite.READ);
        boolean exists = dataset.containsNamedModel(BASE + procedureExecution.getResourcePath());
        dataset.end();
        if (!exists) {
            throw new PEPException("Procedure execution with id " + procedureExecution.getId() + " did not exist.");
        }
        try {
            dataset.begin(ReadWrite.WRITE);
            Model model = dataset.getNamedModel(containerUri);
            model.remove(model.getResource(containerUri),
                    LDP.contains,
                    model.getResource(BASE + procedureExecution.getResourcePath()));
            dataset.replaceNamedModel(containerUri, model);
            dataset.removeNamedModel(BASE + procedureExecution.getResourcePath());
            dataset.removeNamedModel(BASE + procedureExecution.getCommandResourcePath());
            dataset.removeNamedModel(BASE + procedureExecution.getResultResourcePath());
            dataset.commit();
            dataset.end();
            TDB.sync(dataset);
        } catch (Exception ex) {
            throw new PEPException("Error while editing procedure execution with id " + procedureExecution.getId() + ": " + ex.getClass().getName() + ": " + ex.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int count() {
        dataset.begin(ReadWrite.READ);
        Model model = dataset.getNamedModel(containerUri);
        StmtIterator sit = model.listStatements(model.getResource(containerUri), LDP.contains, (RDFNode) null);
        int i = 0;
        while (sit.hasNext()) {
            sit.next();
            i++;
        }
        dataset.end();
        return i;
    }

    private static ExecutorService EXECUTOR;

    @WebListener
    public static class AppListener implements ServletContextListener {

        @Override
        public void contextInitialized(ServletContextEvent sce) {
            int threadPoolSize = 3;
            EXECUTOR = Executors.newFixedThreadPool(threadPoolSize);
            for (int i = 0; i < threadPoolSize; i++) {
                BeanManager manager = CDI.current().getBeanManager();
                Bean bean = manager.resolve(manager.getBeans(ProcedureExecutionCompletor.class));
                if (bean != null) {
                    CreationalContext creationalContext = manager.createCreationalContext(null);
                    if (creationalContext != null) {
                        ProcedureExecutionCompletor instance = (ProcedureExecutionCompletor) bean.create(creationalContext);
                        EXECUTOR.execute(instance);
                    }
                }
                try {
                    Thread.sleep(2000 / threadPoolSize);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        @Override
        public void contextDestroyed(ServletContextEvent sce) {
            EXECUTOR.shutdownNow();
            try {
                EXECUTOR.awaitTermination(10, TimeUnit.SECONDS);
            } catch (InterruptedException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        }

    }

    private static class ProcedureExecutionCompletor implements Runnable {

        @Inject
        @BaseURI
        private String BASE;

        @Inject
        private Dataset dataset;

        private static final Set<ProcedureExecution> ongoingExecutions = new HashSet<>();

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                Iterator<ProcedureExecution> eit = ongoingExecutions.iterator();
                while (eit.hasNext()) {
                    ProcedureExecution procedureExecution = eit.next();
                    Future<Model> futureModel = procedureExecution.getResult();
                    if (futureModel == null ) {
                        ongoingExecutions.remove(procedureExecution);
                    } else {
                        if (futureModel.isDone()) {
                            try {
                                dataset.begin(ReadWrite.WRITE);
                                dataset.addNamedModel(BASE + procedureExecution.getResultResourcePath(), futureModel.get());
                                dataset.commit();
                                dataset.end();
                                TDB.sync(dataset);
                            } catch (ExecutionException | InterruptedException ex) {
                            }
                            ongoingExecutions.remove(procedureExecution);
                        }
                    }
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        
    }

}
