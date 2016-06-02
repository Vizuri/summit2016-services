package com.redhat.vizuri.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.drools.core.command.runtime.process.SetProcessInstanceVariablesCommand;
import org.drools.core.command.runtime.process.SignalEventCommand;
import org.drools.core.marshalling.impl.ClassObjectMarshallingStrategyAcceptor;
import org.drools.core.marshalling.impl.SerializablePlaceholderResolverStrategy;
import org.drools.persistence.jpa.marshaller.JPAPlaceholderResolverStrategy;
import org.jbpm.bpmn2.handler.ReceiveTaskHandler;
import org.jbpm.document.Document;
import org.jbpm.document.marshalling.DocumentMarshallingStrategy;
import org.jbpm.document.service.impl.DocumentStorageServiceImpl;
import org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.jbpm.runtime.manager.impl.DefaultRegisterableItemsFactory;
import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironmentBuilder;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.api.runtime.manager.audit.VariableInstanceLog;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkflowProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bitronix.tm.resource.ResourceRegistrar;
import bitronix.tm.resource.common.XAResourceProducer;
import bitronix.tm.resource.jdbc.PoolingDataSource;


public class ProcessTestLocal {

	private static final Logger LOG = LoggerFactory.getLogger(ProcessTestLocal.class);
	private static final  String  ADJUSTER_REVIEW_SIGNAL ="Adjuster Review";
	private static RuntimeManager manager;
	
	@BeforeClass
	public static void setupBeforeClass() {
		setupPoolingDataSource();
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.redhat.vizuri.jbpm.domain", null);

		KieServices kieServices = KieServices.Factory.get();
		KieContainer kieContainer = kieServices.getKieClasspathContainer();
		LOG.info("logger : {}", kieContainer);
		
		DefaultRegisterableItemsFactory df = new DefaultRegisterableItemsFactory();
		//WorkItemHandler restWorkItemHandler = new RESTWorkItemHandler(this.getClass().getClassLoader() ) ;
		df.addWorkItemHandler("Receive Task", ReceiveTaskHandler.class);
		df.addWorkItemHandler("Manual Task", SystemOutWorkItemHandler.class);
		//ObjectMarshallingStrategy doc = new DocumentMarshallingStrategy();
		RuntimeEnvironmentBuilder builder = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder();
		builder.knowledgeBase(kieContainer.getKieBase("mobile-claim-kbase"))
				.userGroupCallback(new JBossUserGroupCallbackImpl("classpath:/roles.properties"))
				.entityManagerFactory(emf)
				.registerableItemsFactory(df)
				.addEnvironmentEntry(EnvironmentName.OBJECT_MARSHALLING_STRATEGIES, new ObjectMarshallingStrategy[]{
						new JPAPlaceholderResolverStrategy(emf),
						//copy,
						new DocumentMarshallingStrategy(),
						//new DocumentMarshallingStrategyCopy(),
						new SerializablePlaceholderResolverStrategy( 
		                          ClassObjectMarshallingStrategyAcceptor.DEFAULT  )
				});
				;
			

		LOG.info("kieContainer.getReleaseId() {}", kieContainer.getReleaseId());
		String releaseId = "com.redhat.vizuri.insurance:mobile-claims-bpm:1.0-SNAPSHOT";
		LOG.info("builder : {}", builder);
		manager = RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager(builder.get(), releaseId);

	}

	@Test
	public void test() {
		LOG.info("starting test");
	}

	@Test
	public void testStartProcess() {
		RuntimeEngine engine = buildRunTimeEngine();

		KieSession kieSession = engine.getKieSession();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("adjustedAmount", "1000");
		ProcessInstance instance = kieSession.startProcess("mobile-claims-bpm.mobile-claim-process", params);
		LOG.info("instance id : " + instance.getId());

	}

	private RuntimeEngine buildRunTimeEngine() {
		return manager.getRuntimeEngine(ProcessInstanceIdContext.get());
	}
	
	@Test
	public void testUpdateProcessVariableComments(){
		long processInstanceId = 7l;
		RuntimeEngine engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get(processInstanceId));
		KieSession kieSession = engine.getKieSession();
		ProcessInstance processInstance = kieSession.getProcessInstance(processInstanceId);
		//List<VariableInstanceLog> logs = (List<VariableInstanceLog>) engine.getAuditService().findVariableInstances(processInstanceId, "claimComments");
		WorkflowProcessInstance workflowProcessInstance = (WorkflowProcessInstance) processInstance;
		ArrayList claimComments = (ArrayList) workflowProcessInstance.getVariable("claimComments");
		if(claimComments == null){
			claimComments = new ArrayList();
		}
		LOG.info("claimComments {}",claimComments.getClass());
				
		SetProcessInstanceVariablesCommand cmd = new SetProcessInstanceVariablesCommand();
		cmd.setProcessInstanceId(processInstanceId);
		Map<String,Object> variables = new HashMap();
		
		claimComments.add("hello"+System.currentTimeMillis());
		variables.put("claimComments", claimComments);
		cmd.setVariables(variables);
		
		kieSession.execute(cmd);
		
	}
	
	@Test
	public void testUpdateProcessVariable(){
		long processInstanceId = 6l;
		RuntimeEngine engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get(processInstanceId));
		KieSession kieSession = engine.getKieSession();
		//ProcessInstance processInstance = kieSession.getProcessInstance(processInstanceId);
		//engine.getAuditService().findVariableInstances(processInstanceId, "claimComments")
		SetProcessInstanceVariablesCommand cmd = new SetProcessInstanceVariablesCommand();
		cmd.setProcessInstanceId(processInstanceId);
		Map<String,Object> variables = new HashMap();
		variables.put("adjustedAmount", 1234);
		cmd.setVariables(variables);
		
		kieSession.execute(cmd);
		
	}
	
	@Test
	public void testPutPhoto(){
		long processInstanceId = 6l;
		RuntimeEngine engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get(processInstanceId));
		KieSession kieSession = engine.getKieSession();
		//ProcessInstance processInstance = kieSession.getProcessInstance(processInstanceId);
		SetProcessInstanceVariablesCommand cmd = new SetProcessInstanceVariablesCommand();
		cmd.setProcessInstanceId(processInstanceId);
		Map<String,Object> variables = new HashMap();
		byte[] content = "yet another document content".getBytes();
		DocumentStorageServiceImpl docServ = new DocumentStorageServiceImpl();
		Document photo = docServ.buildDocument("mydoc", content.length, new Date(), new HashMap<String, String>());
		
		docServ.saveDocument(photo, content);
		Document fromStorage = docServ.getDocument(photo.getIdentifier());
		
		variables.put("photo", fromStorage);
		cmd.setVariables(variables);
		
		kieSession.execute(cmd);
	}
	
//	@Test
//	public void testSignalAddComment() {
//		RuntimeEngine engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get(5l));//buildRunTimeEngine();
//	
//		KieSession kieSession = engine.getKieSession();
//		SignalEventCommand command = new SignalEventCommand();
//		command.setProcessInstanceId(5l);
//		Map<String, Object> params = new HashMap<String, Object>();
//		ArrayList<String> arr = new ArrayList<String>();
//		arr.add("nocomment");
//		params.put("in_comment", "hello");
//
//		command.setEventType("add-comments");
//		command.setEvent(params);
//
//		Object commandReturn = kieSession.execute(command);
//		LOG.info("commandReturn {}", commandReturn);
//	}
//
//	@Test
//	public void testSignalHumanTaskJob() {
//		RuntimeEngine engine = buildRunTimeEngine();// buildRunTimeEngineWithUser("claimadjuster","password99!");
//
//		KieSession kieSession = engine.getKieSession();
//		kieSession.signalEvent("Adjuster Review", "no", 2l);
//	}

	
	@Test
	public void testAdjusterReview(){
		Map params = new HashMap();
		params.put("task_adjustedAmount", "205");
		completeHumanTask("Adjuster Review", params, 5l,ADJUSTER_REVIEW_SIGNAL);
	}
	//@Test
	public void completeHumanTask(String taskName, Map params, Long processId,String signalName) {
		RuntimeEngine engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get(processId));//buildRunTimeEngine();// buildRunTimeEngineWithUser("claimadjuster","password99!");
		
		
		String caseworker = "caseworker";
		KieSession kieSession = engine.getKieSession();
		
		SignalEventCommand command = new SignalEventCommand();
		command.setProcessInstanceId(processId);
		
		command.setEventType(signalName);
		//command.setEvent(params);
		Object commandReturn = kieSession.execute(command);
		LOG.info("commandReturn {}", commandReturn);
		
		TaskService taskService = engine.getTaskService();
		
		List<Long> tasksList = taskService.getTasksByProcessInstanceId(processId);

		//engine = buildRunTimeEngineWithUser(caseworker, "password99!");
		for (Long taskId : tasksList) {
			LOG.info("task id {}", taskId);

			Map<String, Object> taskContent = new HashMap<String, Object>();
			//taskContent.put("in_processRequest", "yes");
			Map<String,Object> content = taskService.getTaskContent(taskId);
			if(! taskName.equals(content.get("NodeName") ) ){
				LOG.info("not a valid task skipping");
			}
			try {
				taskService.claim(taskId, caseworker);
				taskContent = taskService.getTaskContent(taskId);
				LOG.info("taskContent : {}",taskContent);
				LOG.info("claim successful : " + taskId);
			} catch (Exception e) {
				LOG.error("error : " + e.getMessage());
				continue;
			}

			taskService.start(taskId, caseworker);
//			taskContent.put("task_adjustedAmount", "200");
			taskService.complete(taskId, caseworker, params);
			taskContent = taskService.getTaskContent(taskId);

			LOG.info("taskContent {}", taskContent);
		}

	}

	private RuntimeEngine buildRunTimeEngineWithUser(String caseworker, String string) {
		return buildRunTimeEngine();
	}
	
	private static PoolingDataSource setupPoolingDataSource() {
		
//		PoolingDataSource pds = new PoolingDataSource();
//		pds.setUniqueName("jdbc/jbpm-ds");
//		pds.setClassName("bitronix.tm.resource.jdbc.lrc.LrcXADataSource");
//		pds.setMaxPoolSize(5);
//		pds.setAllowLocalTransactions(true);
//		pds.getDriverProperties().put("user", "postgres");
//		pds.getDriverProperties().put("password", "password99!");
//		pds.getDriverProperties().put("url", "jdbc:postgresql://localhost:5432/bpms_demo");
//		pds.getDriverProperties().put("driverClassName", "org.postgresql.Driver");
//		pds.init();
//
//		//emf = Persistence.createEntityManagerFactory("org.jbpm.persistence.jpa.updated");
//
//		return pds;
		
	        PoolingDataSource pds = new PoolingDataSource();
	        pds.setUniqueName("jdbc/jbpm-ds");
	        pds.setClassName("bitronix.tm.resource.jdbc.lrc.LrcXADataSource");
	        pds.setMaxPoolSize(5);
	        pds.setAllowLocalTransactions(true);
	        pds.getDriverProperties().put("user", "sa");
	        pds.getDriverProperties().put("password", "");
	        pds.getDriverProperties().put("url", "jdbc:h2:~/vizuri_summit;AUTO_SERVER=TRUE");
	        pds.getDriverProperties().put("driverClassName", "org.h2.Driver");
	        try {
	            pds.init();
	        } catch (Exception e) {
	            LOG.warn("DBPOOL_MGR:Looks like there is an issue with creating db pool because of " + e.getMessage() + " cleaing up...");
	            Set<String> resources = ResourceRegistrar.getResourcesUniqueNames();
	            for (String resource : resources) {
	                XAResourceProducer producer = ResourceRegistrar.get(resource);
	                producer.close();
	                ResourceRegistrar.unregister(producer);
	                LOG.debug("DBPOOL_MGR:Removed resource " + resource);
	            }
	            LOG.debug("DBPOOL_MGR: attempting to create db pool again...");
	            pds = new PoolingDataSource();
	            pds.setUniqueName("jdbc/jbpm-ds");
	            pds.setClassName("bitronix.tm.resource.jdbc.lrc.LrcXADataSource");
	            pds.setMaxPoolSize(5);
	            pds.setAllowLocalTransactions(true);
	            pds.getDriverProperties().put("user", "sa");
	            pds.getDriverProperties().put("password", "");
	            pds.getDriverProperties().put("url", "jdbc:h2:~/vizuri_summit;AUTO_SERVER=TRUE");
	            pds.getDriverProperties().put("driverClassName", "org.h2.Driver");
	            pds.init();         
	            LOG.debug("DBPOOL_MGR:Pool created after cleanup of leftover resources");
	        }
	        return pds;
	    }

}
