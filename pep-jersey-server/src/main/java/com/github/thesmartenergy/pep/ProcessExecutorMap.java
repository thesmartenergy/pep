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
 * Abstract implementation of a <code>ProcessExecutor</code> that uses a 
 * <code>Map</code> to store the process executions.
 * 
 * @author Maxime Lefrançois <maxime.lefrancois at emse.fr>
 */
public abstract class ProcessExecutorMap implements ProcessExecutor {

    private static final Map<String, ProcessExecution> executions = new HashMap<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public void create(ProcessExecution processExecution) throws PEPException {
        String id = processExecution.getId();
        if (executions.containsKey(id)) {
            throw new PEPException("Process execution with id " + id + " already exists. Edit it instead.");
        }
        executions.put(processExecution.getId(), processExecution);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProcessExecution find(String id) {
        return executions.get(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(ProcessExecution processExecution) throws PEPException {
        String id = processExecution.getId();
        if (!executions.containsKey(id)) {
            throw new PEPException("Process execution with id " + id + " does not exist. Create it first.");
        }
        executions.put(id, processExecution);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy(ProcessExecution processExecution) throws PEPException {
        String id = processExecution.getId();
        processExecution = executions.remove(id);
        if (processExecution == null) {
            throw new PEPException("Process execution with id " + id + " did not exist.");
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
