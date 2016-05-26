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
import javax.ws.rs.PathParam;
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

// @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
// @RequestScoped
public class RestResource {

	@Context
	 private HttpServletRequest httpRequest;
	 
	
	@PersistenceUnit(unitName = "com.redhat.vizuri.jbpm.domain")
	private EntityManagerFactory emf;
	
	private static final Logger LOG = LoggerFactory.getLogger(RestResource.class);
	
	private static final String deploymentId = "com.vizuri.demo.freedommortgage:RetailMortgageUnderwriting:1.0-SNAPSHOT";
	private static final String applicationContext = "http://localhost:8080/business-central";

	//String processInstanceId = "RetailMortgageUnderwriting.UnderWritingStart";
	
//	@Inject
//	BpmsService bpmService;
	
	
	@POST
	@Path("/startprocess")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	public Long startProcess(Map jsonMap){
		
		RuntimeEngine engine = manager.getRuntimeEngine(EmptyContext.get());

		KieSession kieSession = engine.getKieSession();
		Map<String, Object> params = new HashMap();
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
		 	//EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.redhat.vizuri.jbpm.domain", null);
	        
			KieServices kieServices = KieServices.Factory.get();
			KieContainer kieContainer = kieServices.getKieClasspathContainer();
			LOG.info("logger : {}",kieContainer);
			RuntimeEnvironmentBuilder builder = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder();
			builder.knowledgeBase(kieContainer.getKieBase())
			.userGroupCallback(new JBossUserGroupCallbackImpl("classpath:/roles.properties"))
			.entityManagerFactory(emf)
			//.persistence(true)
		//	.addEnvironmentEntry(EnvironmentName.TRANSACTION_MANAGER, TransactionManagerServices.getTransactionManager());
			//.persistence(true)
			;
			
			
			;
			LOG.info("kieContainer.getReleaseId() {}",kieContainer.getReleaseId());
			String releaseId = "com.redhat.vizuri.insurance:mobile-claims-bpm:1.0-SNAPSHOT";
			//releaseId = "vizuri-summit-2016:mobile-claim-rules:1.0-SNAPSHOT";
			LOG.info("builder : {}",builder);
			manager =RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager(builder.get(),releaseId);
	}
//	@GET
//	public Response testDummy() {
//		return Response.ok("success", MediaType.TEXT_PLAIN).build();
//	}
//
//	@GET
//	@Path("/activeProcesses")
//	@Produces(MediaType.APPLICATION_JSON)
//	public Response activeProcesses() {
//		RuntimeEngine runtimeEngine = getRuntimeEngine(applicationContext, deploymentId, "krisv", "password99!");
//		List<ProcessInstanceLog> logs = (List<ProcessInstanceLog>) runtimeEngine.getAuditService().findActiveProcessInstances("RetailMortgageUnderwriting.UnderWritingStart");
//		System.out.println("logs : " + logs.size());
//
//		return Response.ok(logs, MediaType.APPLICATION_JSON).build();
//	}
//
//	@GET
//	@Path("/taskList")
//	@Produces(MediaType.APPLICATION_JSON)
//	public Response taskList(@QueryParam("userId") String userId) {
//		log.info("inside taskList");
//		RuntimeEngine runtimeEngine = getRuntimeEngine(applicationContext, deploymentId, userId, "password99!");
//
//		TaskService taskService = runtimeEngine.getTaskService();
//		List<TaskSummary> taskSummaryListPotential = taskService.getTasksAssignedAsPotentialOwner(userId, "en");
//
//		Map<Object, TaskSummary> trackTaskSummaries = new HashMap<Object, TaskSummary>();
//		for (TaskSummary taskSummary : taskSummaryListPotential) {
//			trackTaskSummaries.put(taskSummary.getId(), taskSummary);
//		}
//
//		List<TaskSummary> taskSummaryList = taskService.getTasksOwnedByStatus(userId, Arrays.asList(Status.InProgress, Status.Ready, Status.Reserved), "en");
//		for (TaskSummary taskSummary : taskSummaryList) {
//			trackTaskSummaries.put(taskSummary.getId(), taskSummary);
//		}
//
//		return Response.ok(trackTaskSummaries.values(), MediaType.APPLICATION_JSON).build();
//	}
//
//	@POST
//	@Path("/startProcess")
//	@Consumes(MediaType.APPLICATION_JSON)
//	@Produces(MediaType.APPLICATION_JSON)
//	public Response startProcess(ProcessWrapper wrapper) {
//		log.info("startProcess method entry");
//		RuntimeEngine runtime = getRuntimeEngine(applicationContext, deploymentId, "krisv", "password99!");
//
//		Map<String, Object> data = new HashMap();
//		LoanInformation information = wrapper.getLoanInformation();
//		/*
//		 * information.setLoanNumber("1"); information.setLoanType("FHA");
//		 * information.setPropertyValue(49000D);
//		 * information.setLoanAmount(50000d);
//		 */
//
//		data.put("loanInformation", information);
//		data.put("underWriterGroup", "");
//		data.put("loanDisposition", "");
//		data.put("loanStatus", "");
//		log.info("data : " + data);
//		log.info("loanInformation request : "+information.buildRequestString());
//		
//		runtime.getKieSession().startProcess("RetailMortgageUnderwriting.UnderWritingStart", data);
//		log.info("startProcess method  done");
//		return Response.ok(wrapper, MediaType.APPLICATION_JSON).build();
//	}
//
//	@POST
//	@Path("/task")
//	@Consumes(MediaType.APPLICATION_JSON)
//	@Produces(MediaType.APPLICATION_JSON)
//	public Response doTask(ProcessWrapper wrapper) {
//		RuntimeEngine runtime = getRuntimeEngine(applicationContext, deploymentId, "krisv", "password99!");
//		TaskService taskService = runtime.getTaskService();
//
//		String taskType = wrapper.getTaskType();
//		if ("start".equals(taskType)) {
//			taskService.start(wrapper.getTaskId(), wrapper.getTaskUserId());
//		} else if ("claim".equals(taskType)) {
//			taskService.claim(wrapper.getTaskId(), wrapper.getTaskUserId());
//
//		} else if ("complete".equals(taskType)) {
//			Map<String, Object> data = new HashMap<String, Object>();
//			data.put("out_loanInformation", wrapper.getLoanInformation());
//
//			taskService.complete(wrapper.getTaskId(), wrapper.getTaskUserId(), data);
//		} else if ("exit".equals(taskType)) {
//			taskService.exit(wrapper.getTaskId(), wrapper.getTaskUserId());
//		} else if ("suspend".equals(taskType)) {
//			taskService.suspend(wrapper.getTaskId(), wrapper.getTaskUserId());
//		}
//
//		return Response.ok(wrapper, MediaType.APPLICATION_JSON).build();
//	}
//
//	@GET
//	@Path("/taskcontent")
//	@Produces(MediaType.APPLICATION_JSON)
//	public Response doGetTaskContent(@QueryParam("userId") String userId, @QueryParam("taskId") long taskId) {
//		ProcessWrapper wrapper = new ProcessWrapper();
//		RuntimeEngine runtime = getRuntimeEngine(applicationContext, deploymentId, "krisv", "password99!");
//		TaskService taskService = runtime.getTaskService();
//		Map<String, Object> taskContent = taskService.getTaskContent(taskId);
//
//		try {
//			wrapper.setLoanInformation((LoanInformation) taskContent.get("in_loanInformation"));
//		} catch (Exception e) {
//
//			e.printStackTrace();
//		}
//		return Response.ok(wrapper, MediaType.APPLICATION_JSON).build();
//	}
//
//	// http://localhost:8080/underwriter-ui/rest/bpm/updateLoan
//	 
//	 
//	 @POST
//     @Path("/updateLoan")
//     @Produces(MediaType.TEXT_PLAIN)
//	 @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
//     public Response updateLoan(){
//    	 log.info("update Loan Encountered");
//    	 LoanInformation loanInfo = null;
//    	
//    	 Map<String, String [] > multiValued = httpRequest.getParameterMap();
//    	
//    	 
//    	 log.info("multiValued : "+multiValued);
//    	 try {
//			//BeanUtils.populate(rec, multiValued);
//    		 String [] value = multiValued.get("id");
//			Long id = System.nanoTime();
//			try {
//				id = value != null && value.length > 0 ? Long.valueOf(value[0]) : id;
//			} catch (Exception e) { 
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
//			 List<LoanRecord> listLoanRect= em.createQuery("select l from LoanRecord l where l.id = "
//			 +id).getResultList();
//			 Map properties = new HashMap();
//			 for (String str : multiValued.keySet()) {
//				 String [] val = multiValued.get(str);
//				 if(val!= null && val.length > 0){
//					 properties.put(str, val[0] );
//				 }
//				 
//			}
//			 if(listLoanRect != null && listLoanRect.size() > 0){
//				 BeanUtils.copyProperties(properties, listLoanRect.get(0) );
//				 em.persist(listLoanRect.get(0) );
//			 }else{
//				 
//				 LoanRecord rec = new LoanRecord();
//				 BeanUtils.populate(rec, multiValued);
//				 em.persist(rec);
//				 
//			 }
//			 
//			 
//			
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//    	
//    	 
//    	 
//    	 return Response.ok(Boolean.TRUE).build();
//     }

//	private RuntimeEngine getRuntimeEngine(String applicationContext, String deploymentId, String userId, String password) {
//		try {
//			URL jbpmURL = new URL(applicationContext);
//			RuntimeEngine runtimeEngine = RemoteRuntimeEngineFactory.newRestBuilder().addUrl(jbpmURL).addDeploymentId(deploymentId).addUserName(userId).addPassword(password).build();
//
//			;
//
//			return runtimeEngine;
//		} catch (MalformedURLException e) {
//			throw new IllegalStateException("This URL is always expected to be valid!", e);
//		}
//	}
}
