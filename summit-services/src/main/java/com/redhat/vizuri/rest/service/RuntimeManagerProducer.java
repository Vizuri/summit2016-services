package com.redhat.vizuri.rest.service;

import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.internal.runtime.manager.cdi.qualifier.PerProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * RuntimeManagerProducer CDI Producer class.
 * Responsible for providing a BPMS RuntimeManager for interacting with BPMS processes.
 * 
 * @author Kent Eudy : Vizuri
 *
 */
@Startup
@ApplicationScoped
public class RuntimeManagerProducer {
	private static final Logger logger = LoggerFactory.getLogger(RuntimeManagerProducer.class);
//	
//	@Inject 	
//	private Properties properties;
	
	@Inject @PerProcessInstance
	private RuntimeEnvironment runtimeEnvironment;
	
    @Produces
    @PerProcessInstance
    public RuntimeManager produceRuntimeManager() {
    	logger.info("In produceRuntimeManager");
    	String deploymentId = "com.redhat.vizuri.jbpm.domain";// properties.getProperty("bpms.kjar.deploymentid");
		
		logger.info("deploymentId:" + deploymentId);
     	return RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager(runtimeEnvironment,deploymentId);
    }
    
    
}
