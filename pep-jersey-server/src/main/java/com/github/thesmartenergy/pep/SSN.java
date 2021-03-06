/*
 * Copyright 2017 ITEA 12004 SEAS Project.
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

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

/**
 *
 * @author maxime.lefrancois
 */
public class SSN {
        
    public static final String NS = "http://www.w3.org/ns/ssn/";
    
    public static final Property hasInput = ResourceFactory.createProperty(NS + "hasInput");
        
    public static final Property hasOutput = ResourceFactory.createProperty(NS + "hasOutput");
    
    public static final Property implementz = ResourceFactory.createProperty(NS + "implements");

}
