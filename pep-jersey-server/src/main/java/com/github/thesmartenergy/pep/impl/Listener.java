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

import com.github.thesmartenergy.pep.ProcedureExecutor;
import com.github.thesmartenergy.rdfp.BaseURI;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 *
 * @author Maxime Lefrançois <maxime.lefrancois at emse.fr>
 */
@WebListener
public class Listener implements ServletContextListener{

    @Inject
    @BaseURI
    String BASE;

    @Inject
    @Any
    Instance<ProcedureExecutor> procedureExecutors;
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // force resource map initialization
        ProcedureExecutorConfig.init(BASE, procedureExecutors);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
    
}
