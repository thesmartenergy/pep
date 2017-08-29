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

import java.util.HashMap;
import java.util.Map;

/**
 * Abstract implementation of a <code>ProcedureExecutor</code> that uses a 
 * <code>Map</code> to store the procedure executions.
 * 
 * @author Maxime Lefrançois <maxime.lefrancois at emse.fr>
 */
public abstract class ProcedureExecutorMap implements ProcedureExecutor {

    private static final Map<String, ProcedureExecution> executions = new HashMap<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public void create(ProcedureExecution procedureExecution) throws PEPException {
        String id = procedureExecution.getId();
        if (executions.containsKey(id)) {
            throw new PEPException("Procedure execution with id " + id + " already exists. Edit it instead.");
        }
        executions.put(procedureExecution.getId(), procedureExecution);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProcedureExecution find(String id) {
        return executions.get(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(ProcedureExecution procedureExecution) throws PEPException {
        String id = procedureExecution.getId();
        if (!executions.containsKey(id)) {
            throw new PEPException("Procedure execution with id " + id + " does not exist. Create it first.");
        }
        executions.put(id, procedureExecution);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy(ProcedureExecution procedureExecution) throws PEPException {
        String id = procedureExecution.getId();
        procedureExecution = executions.remove(id);
        if (procedureExecution == null) {
            throw new PEPException("Procedure execution with id " + id + " did not exist.");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int count() {
        return executions.size();
    }

}
