package uk.ac.ebi.fgpt.lode.servlet;

/*
 * Copyright (c) 2013 EMBL - European Bioinformatics Institute
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */


import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Simon Jupp
 * @date 08/06/2013
 * Functional Genomics Group EMBL-EBI
 *
 * Enable CORS requests to this application
 *
 */

public class CorsFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        if (request.getHeader("Access-Control-Request-Method") != null && "OPTIONS".equals(request.getMethod())) {
             // CORS "pre-flight" request
             response.addHeader("Access-Control-Allow-Origin", "*");
             response.addHeader("Access-Control-Allow-Methods", "GET, POST");
             response.addHeader("Access-Control-Allow-Headers", "Content-Type");
             response.addHeader("Access-Control-Max-Age", "1");// 30 min
//         }
         filterChain.doFilter(request, response);
    }
}
