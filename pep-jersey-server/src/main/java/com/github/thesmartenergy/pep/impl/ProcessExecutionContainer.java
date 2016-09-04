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

import com.github.thesmartenergy.pep.ProcessExecutor;
import com.github.thesmartenergy.rdfp.preneg.GraphDescription;
import java.lang.annotation.Annotation;

/**
 *
 * @author Maxime Lefrançois <maxime.lefrancois at emse.fr>
 */

public class ProcessExecutionContainer {

    private final String containerPath;

    private final ProcessExecutor processExecutor;

    private final String outputGraphURI;

    private final String inputGraphURI;

    private final GraphDescription inputGraphAnnotation;

    private final GraphDescription outputGraphAnnotation;

    public ProcessExecutionContainer(ProcessExecutor processExecutor, String containerPath, String inputGraphURI, String outputGraphURI) {
        this.containerPath = containerPath;
        this.processExecutor = processExecutor;
        this.inputGraphURI = inputGraphURI;
        this.outputGraphURI = outputGraphURI;
        inputGraphAnnotation = getGraphDescription(inputGraphURI);
        outputGraphAnnotation = getGraphDescription(outputGraphURI);
    }

    public String getContainerPath() {
        return containerPath;
    }

    public GraphDescription getInputGraphAnnotation() {
        return inputGraphAnnotation;
    }

    public String getInputGraphURI() {
        return inputGraphURI;
    }

    public GraphDescription getOutputGraphAnnotation() {
        return outputGraphAnnotation;
    }

    public String getOutputGraphURI() {
        return outputGraphURI;
    }

    public ProcessExecutor getProcessExecutor() {
        return processExecutor;
    }

   private static GraphDescription getGraphDescription(final String uri) {
        return new GraphDescription() {
            @Override
            public String value() {
                return uri;
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return GraphDescription.class;
            }
        };
    }

}