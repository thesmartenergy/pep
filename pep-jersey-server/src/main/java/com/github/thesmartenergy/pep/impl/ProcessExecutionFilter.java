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

import com.github.thesmartenergy.pep.impl.ProcessExecutionContainer;
import com.github.thesmartenergy.pep.impl.ProcessExecutorConfig;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

/**
 * This class filters the calls to resources, and dispatches to the endpoint
 * that exposes the ontology that defines the resource.
 *
 * @author maxime.lefrancois
 */
@WebFilter(urlPatterns = {"/*"})
public class ProcessExecutionFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = ((HttpServletRequest) request);
        String contextPath = req.getContextPath() + "/";
        String requestURI = req.getRequestURI();
        if (requestURI.startsWith(contextPath)) {
            String resourcePath = requestURI.substring(contextPath.length());
            ProcessExecutionContainer container = ProcessExecutorConfig.getProcessExecutionContainer(resourcePath);
            if (container == null) {
                chain.doFilter(request, response);
                return;
            }
            if (req.getMethod().equals("GET") && resourcePath.equals(container.getContainerPath()))  {
                chain.doFilter(request, response);
                return;
            }
            String newURI = "/_pep/" + resourcePath;
            request.setAttribute("pep", "pep");
            req.getRequestDispatcher(newURI).forward(req, response);
            return;
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
