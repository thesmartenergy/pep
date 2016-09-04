package com.github.thesmartenergy.pep;

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


import java.util.concurrent.Future;
import org.apache.jena.rdf.model.Model;

/**
 * Interface for process executions.
 * 
 * The current implementation does not allow other implementations than
 * <code>ProcessExecutionImpl</code>. Please contact us if you find any
 * limitation with this choice.
 * 
 * @author maxime.lefrancois
 */
public interface ProcessExecution {
    
    /**
     * The path of the URI of the process execution container that
     * contains this process execution.
     * 
     * @return 
     */
    public String getContainerPath();

    /**
     * The ID of the process execution in the container.
     * 
     * @return 
     */
    public String getId();

    /**
     * The path of the URI of the process execution.
     * 
     * @return 
     */
    public String getResourcePath();

    /**
     * The input of the process execution.
     * 
     * @return 
     */
    public Model getInput();

    /**
     * The output of the process execution. A promise.
     * 
     * @return 
     */
    public Future<Model> getOutput();
    
    /**
     * The process execution input URI path.
     * 
     * @return 
     */
    public String getInputResourcePath();
    
    /**
     * The process execution output URI path.
     * 
     * @return 
     */
    public String getOutputResourcePath();

    /**
     * The model that describes the process execution.
     * 
     * @return 
     */
    public Model getModel();

}
