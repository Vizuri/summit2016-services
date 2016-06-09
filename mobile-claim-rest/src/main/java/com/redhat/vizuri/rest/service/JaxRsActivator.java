package com.redhat.vizuri.rest.service;
/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.jaxrs.config.BeanConfig;

/**
 * A class extending {@link Application} and annotated with @ApplicationPath is the Java EE 6
 * "no XML" approach to activating JAX-RS.
 * 
 * <p>
 * Resources are served relative to the servlet path specified in the {@link ApplicationPath}
 * annotation.
 * </p>
 */
@ApplicationPath("/rest")
public class JaxRsActivator extends Application {

	private static final Logger log = LoggerFactory.getLogger(JaxRsActivator.class);
	
	public JaxRsActivator() {
        
		log.info("Starting the rest interface for Massmutual");
		BeanConfig beanConfig = new BeanConfig();
        beanConfig.setVersion("0.9-SNAPSHOT");
        beanConfig.setSchemes(new String[]{"http"});
        beanConfig.setHost("localhost:8080");
        beanConfig.setBasePath("/summit-service/rest");	//http://localhost:8080/mm-rest-app/rest/swagger.json
        beanConfig.setResourcePackage("com.redhat.vizuri.rest");
        beanConfig.setScan(true);
	}
	
	public Set<Class<?>> getClasses() {
        //return new HashSet<Class<?>>(Arrays.asList(RiskCalculatorService.class, BlenderService.class, MIBService.class, VerificationService.class, JacksonConfig.class, io.swagger.jaxrs.listing.ApiListingResource.class, io.swagger.jaxrs.listing.SwaggerSerializers.class));
    
        Set<Class<?>> resources = new HashSet<>();
        
        resources.add(RestResource.class);
      
        resources.add(io.swagger.jaxrs.listing.ApiListingResource.class);
        resources.add(io.swagger.jaxrs.listing.SwaggerSerializers.class);
        
        return resources;
	
	}
}
