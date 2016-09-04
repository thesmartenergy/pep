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
package com.github.thesmartenergy.pep.website;

import com.github.thesmartenergy.pep.ContainerPath;
import com.github.thesmartenergy.pep.PEPException;
import com.github.thesmartenergy.pep.ProcessExecutorMap;
import com.github.thesmartenergy.rdfp.BaseURI;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

/**
 *
 * @author maxime.lefrancois
 */
@ContainerPath("RandomNumberGeneration")
public class RandomNumberGenerator extends ProcessExecutorMap {
    
    private static final Logger LOG = Logger.getLogger(RandomNumberGenerator.class.getSimpleName());

    private static final String EX = "http://example.com/";
    private static final Property MIN = ResourceFactory.createProperty(EX + "min");
    private static final Property MAX = ResourceFactory.createProperty(EX + "max");
    private static final Property VALUE = ResourceFactory.createProperty(EX + "value");
    
    @Inject
    @BaseURI
    String BASE;

    @Override
    public Future<Model> execute(Model input) throws PEPException {
        
        final Set<CompletableFuture> futures = new HashSet<>();
        final Model output = ModelFactory.createDefaultModel();
        output.setNsPrefixes(input.getNsPrefixMap());
        
        ResIterator rit = input.listSubjects();
        while (rit.hasNext()) {
            final Resource r = rit.next();
            final float min = input.getProperty(r, MIN).getFloat();
            final float max = input.getProperty(r, MAX).getFloat();

            final Random random = new Random();

            futures.add(CompletableFuture.runAsync(new Runnable() {
                @Override
                public void run() {
                    // sleep for a random number of millis between 1 s and 3 s.
                    try {
                        int time = (int) (1000 + random.nextFloat() * 2000);
                        Thread.sleep(time);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(RandomNumberGenerator.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    float value = min + random.nextFloat() * (max - min);
                    output.add(r, VALUE, Float.toString(value), XSDDatatype.XSDdecimal);
                }
            }));
        }
        return CompletableFuture.supplyAsync(new Supplier<Model>() {
            @Override
            public Model get() {
                try {
                    CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();
                } catch (InterruptedException | ExecutionException ex) {
                    Logger.getLogger(RandomNumberGenerator.class.getName()).log(Level.SEVERE, null, ex);
                }
                return output;
            }
        });
    }

}
