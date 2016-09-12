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

import java.util.concurrent.Future;
import org.apache.jena.rdf.model.Model;

/**
 * Interface for process executors. Abstract class
 * <code>ProcessExecutorMap</code> can be used in case process executions can be
 * stored in memory in a hash map.
 * 
 * @author maxime.lefrancois
 */
public interface ProcessExecutor {

    /**
     * Lauches the process execution using an input RDF Graph, and returns a
     * promise for an RDF Graph.
     * 
     * @param input the input RDF graph.
     * @return the future output RDF graph
     * @throws PEPException 
     */
    Future<Model> execute(Model input) throws PEPException;

    /**
     * Stores the process execution for later reuse.
     * @param processExecution
     * @throws PEPException if the process execution already exists.
     */
    void create(ProcessExecution processExecution) throws PEPException;

    /**
     * Edits the process execution.
     * @param processExecution
     * @throws PEPException if the process execution does not exist.
     */
    void update(ProcessExecution processExecution) throws PEPException;

    /**
     * Return a process execution given its identifier.
     * @param id
     * @return 
     */
    ProcessExecution find(String id);

    /**
     * Permanently destroy a process execution.
     * @param execution
     * @throws PEPException if the process execution does not exist.
     */
    void destroy(ProcessExecution execution) throws PEPException;

    /**
     * Counts the number of stored process executions.
     * @return
     */
    int count();

}
