package com.redhat.vizuri.rest.service;


import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironmentBuilder;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.runtime.manager.context.EmptyContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/vizuri/summit")

public class RestResource {

	@Context
	 private HttpServletRequest httpRequest;
	 
	@PersistenceUnit(unitName = "com.redhat.vizuri.jbpm.domain")
	private EntityManagerFactory emf;
	
	private static final Logger LOG = LoggerFactory.getLogger(RestResource.class);
	
	@POST
	@Path("/startprocess")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	public Long startProcess(){
		
		RuntimeEngine engine = manager.getRuntimeEngine(EmptyContext.get());

		KieSession kieSession = engine.getKieSession();
		Map<String, Object> params = new HashMap<String,Object>();
		params.put("processReaquest", "yes");
		ProcessInstance instance = kieSession.startProcess("mobile-claims-bpm.adhoc-test", params);
		LOG.info("instance id : " + instance.getId());
		return instance.getId();
	}
	
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public Response testDummy() {
		return Response.ok("success", MediaType.TEXT_PLAIN).build();
	}
	
	
	private static RuntimeManager manager;
	
	@PostConstruct
	public void init(){
		buildRunTime();
	}
	private void buildRunTime(){
			if(manager != null){
				return;
			}
	        
			KieServices kieServices = KieServices.Factory.get();
			KieContainer kieContainer = kieServices.getKieClasspathContainer();
			LOG.info("logger : {}",kieContainer);
			RuntimeEnvironmentBuilder builder = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder();
			builder.knowledgeBase(kieContainer.getKieBase())
			.userGroupCallback(new JBossUserGroupCallbackImpl("classpath:/roles.properties"))
			.entityManagerFactory(emf)
			;
			
			
			;
			LOG.info("kieContainer.getReleaseId() {}",kieContainer.getReleaseId());
			String releaseId = "com.redhat.vizuri.insurance:mobile-claims-bpm:1.0-SNAPSHOT";
			LOG.info("builder : {}",builder);
			manager =RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager(builder.get(),releaseId);
	}
}
