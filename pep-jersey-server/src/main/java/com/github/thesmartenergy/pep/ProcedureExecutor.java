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
 * Interface for procedure executors. Abstract class
 * <code>ProcedureExecutorMap</code> can be used in case procedure executions can be
 * stored in memory in a hash map.
 * 
 * @author maxime.lefrancois
 */
public interface ProcedureExecutor {

    /**
     * Lauches the procedure execution using an input RDF Graph, and returns a
     * promise for an RDF Graph.
     * 
     * @param command the input RDF graph.
     * @return the future output RDF graph
     * @throws PEPException 
     */
    Future<Model> execute(Model command) throws PEPException;

    /**
     * Stores the procedure execution for later reuse.
     * @param procedureExecution
     * @throws PEPException if the procedure execution already exists.
     */
    void create(ProcedureExecution procedureExecution) throws PEPException;

    /**
     * Edits the procedure execution.
     * @param procedureExecution
     * @throws PEPException if the procedure execution does not exist.
     */
    void update(ProcedureExecution procedureExecution) throws PEPException;

    /**
     * Return a procedure execution given its identifier.
     * @param id
     * @return 
     */
    ProcedureExecution find(String id);

    /**
     * Permanently destroy a procedure execution.
     * @param execution
     * @throws PEPException if the procedure execution does not exist.
     */
    void destroy(ProcedureExecution execution) throws PEPException;

    /**
     * Counts the number of stored procedure executions.
     * @return
     */
    int count();

}
