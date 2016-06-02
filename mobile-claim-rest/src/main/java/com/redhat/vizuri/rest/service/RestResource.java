package com.redhat.vizuri.rest.service;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.drools.core.command.runtime.process.SignalEventCommand;
import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironmentBuilder;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Task;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.vizuri.brms.service.RuleProcessor;
import com.redhat.vizuri.insurance.Incident;
import com.redhat.vizuri.insurance.Questionnaire;

@Path("/vizuri/summit")

public class RestResource {

	@Context
	 private HttpServletRequest httpRequest;
	 
	@PersistenceUnit(unitName = "com.redhat.vizuri.jbpm.domain")
	private EntityManagerFactory emf;
	
	private static final Logger LOG = LoggerFactory.getLogger(RestResource.class);
	private static String  ADJUSTER_REVIEW_SIGNAL ="Adjuster Review";
	private RuleProcessor ruleProcessor = null;
	
	@POST
	@Path("/startprocess")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	public Long startProcess(){
		
		RuntimeEngine engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get());

		KieSession kieSession = engine.getKieSession();
		Map<String, Object> params = new HashMap<String,Object>();
		
		ProcessInstance instance = kieSession.startProcess("mobile-claims-bpm.mobile-claim-process", params);
		LOG.info("instance id : " + instance.getId());
		return instance.getId();
	}
	
	
	 
	@SuppressWarnings("rawtypes")
	@POST
	@Path("/doadjuster/{processInstanceId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response doAdjuster(Map<String,Object> taskContent, @PathParam("processInstanceId") Long processInstanceId){
		
		LOG.info("inside doAdjuster : taskContent >> {}, processInstanceId >> {}",taskContent,processInstanceId);
		RuntimeEngine engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get(processInstanceId));
		/**
		 * task_complete
		 * task_adjustedAmount
		 * task_approved
		 * task_comment
		 */
		KieSession kieSession = engine.getKieSession();
		SignalEventCommand command = new SignalEventCommand();
		command.setProcessInstanceId(processInstanceId);
		Map<String, Object> params = new HashMap<String, Object>();
		command.setEventType(ADJUSTER_REVIEW_SIGNAL);
		command.setEvent(params);

		Object commandReturn = kieSession.execute(command);
		LOG.info("commandReturn {}", commandReturn);
		
		TaskService taskService = engine.getTaskService();
		String caseworker = "caseworker";
		List<Long> tasksList = taskService.getTasksByProcessInstanceId(processInstanceId);
		
		
		for (Long taskId : tasksList) {
			LOG.info("task id {}", taskId);

			//Map<String, Object> taskContent = new HashMap<String, Object>();
			//taskContent.put("in_processRequest", "yes");
			try {
				Map<String,Object> content = taskService.getTaskContent(taskId);
				if(! ADJUSTER_REVIEW_SIGNAL.equals(content.get("NodeName") ) ){
					LOG.info("not a adjuster review skipping");
				}
				taskService.claim(taskId, caseworker);
				//taskContent = taskService.getTaskContent(taskId);
				//LOG.info("taskContent : {}",taskContent);
				LOG.info("claim successful : " + taskId);
			} catch (Exception e) {
				LOG.error("error : " + e.getMessage());
				continue;
			}
			
			LOG.info("now starting taskId {}",taskId);
			taskService.start(taskId, caseworker);
			LOG.info("taskId {} started",taskId);
			
			taskService.complete(taskId, caseworker, taskContent);
			LOG.info("complete taskId >> {}",taskId);
			taskContent = taskService.getTaskContent(taskId);

			
		}
		
		LOG.info("done doAdjuster");
		
		return sendResponse(200, taskContent);
	//	return taskContent;
	}
	
	
	
	@POST
	@Path("/customer-incident")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response initCustomerQuestionnaire(Incident incident) {
		try {
			Questionnaire questionnaire = ruleProcessor.getQuestionnaireForCustomer(incident);
			LOG.info("Created questionnaire: " + questionnaire);
			return sendResponse(200, questionnaire);
		} catch (Exception ex) {
			LOG.error("Exception in initCustomerQuestionnaire", ex);
			return Response.serverError().entity(new ErrorResponse("Exception in initCustomerQuestionnaire, error: " + ex.getMessage() + "\n" + ex.getStackTrace())).build();
		}
	}
	
	@POST
	@Path("/update-questions")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateQuestions(Questionnaire questionnaire) {
		try {
			ruleProcessor.updateQuestionnaire(questionnaire);
			LOG.info("Updated questionnaire: " + questionnaire);
			return sendResponse(200,  questionnaire);
		} catch (Exception ex) {
			LOG.error("Exception in updateQuestions", ex);
			return Response.serverError().entity(new ErrorResponse("Exception in updateQuestions, error: " + ex.getMessage() + "\n" + ex.getStackTrace())).build();
		}
	}
	
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public Response testDummy() {
		return Response.ok("success", MediaType.TEXT_PLAIN).build();
	}
	
	private Response sendResponse(int status, Object result){
		
		return Response.status(status).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209601").entity(result).build();
	}
	
	
	private static RuntimeManager manager;
	
	@PostConstruct
	public void init(){
		buildRunTime();
		ruleProcessor = new RuleProcessor();
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
