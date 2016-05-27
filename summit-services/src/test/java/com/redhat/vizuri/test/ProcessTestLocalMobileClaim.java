package com.redhat.vizuri.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.drools.core.command.runtime.process.SignalEventCommand;
import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironmentBuilder;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.internal.runtime.manager.context.EmptyContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bitronix.tm.resource.ResourceRegistrar;
import bitronix.tm.resource.common.XAResourceProducer;
import bitronix.tm.resource.jdbc.PoolingDataSource;

public class ProcessTestLocalMobileClaim {

	private static final Logger LOG = LoggerFactory.getLogger(ProcessTestLocalMobileClaim.class);
	private static RuntimeManager manager ;

	@BeforeClass
	public  static void setupBeforeClass(){
		
		PoolingDataSource ds = setupPoolingDataSource();
//		
		 EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.redhat.vizuri.jbpm.domain", null);
        
		KieServices kieServices = KieServices.Factory.get();
		KieContainer kieContainer = kieServices.getKieClasspathContainer();
		LOG.info("logger : {}",kieContainer);
		RuntimeEnvironmentBuilder builder = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder();
		builder.knowledgeBase(kieContainer.getKieBase("mobile-claim-kbase"))
		.userGroupCallback(new JBossUserGroupCallbackImpl("classpath:/roles.properties"))
		.entityManagerFactory(emf)
		
		;
		
		
		;
		LOG.info("kieContainer.getReleaseId() {}",kieContainer.getReleaseId());
		String releaseId = "com.redhat.vizuri.insurance:mobile-claims-bpm:1.0-SNAPSHOT";
		//releaseId = "vizuri-summit-2016:mobile-claim-rules:1.0-SNAPSHOT";
		LOG.info("builder : {}",builder);
		manager =RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(builder.get(),releaseId);
	//	RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment, identifier)
				
	}
	
	@Test
	public void test(){
		LOG.info("starting test");
	}
	
	@Test
	public void testStartProcess() {
		RuntimeEngine engine = buildRunTimeEngine();
		
		KieSession kieSession = engine.getKieSession();
		Map<String, Object> params = new HashMap();
		params.put("processReauest", "yes");
		String processId = "mobile-claims-bpm.mobile-claim-process";
		ProcessInstance instance = kieSession.startProcess(processId, params);
		
		
		LOG.info("instance id : " + instance.getId());

	}

	private RuntimeEngine buildRunTimeEngine() {
		// TODO Auto-generated method stub
		return manager.getRuntimeEngine(EmptyContext.get());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testSignalToFinishJob() {
		RuntimeEngine engine = buildRunTimeEngine();
		KieSession kieSession = engine.getKieSession();
		SignalEventCommand command = new SignalEventCommand();
		command.setProcessInstanceId(2l);
		Map<String, Object> params = new HashMap();
		params.put("processRequest", "no");

		command.setEventType("finishjob");
		command.setEvent(params);
		//JaxbStringObjectPairArray arrayMap = ConversionUtil.convertMapToJaxbStringObjectPairArray(params);
		//command.setEvent(arrayMap);

		// ProcessInstance instance =kieSession.getProcessInstance(161l);
		Object commandReturn = kieSession.execute(command);
		LOG.info("commandReturn {}", commandReturn);
		// kieSession.signalEvent("finishjob", params, 161l);

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSignalHumanTaskJob() {
		RuntimeEngine engine = buildRunTimeEngine();// buildRunTimeEngineWithUser("claimadjuster","password99!");

		KieSession kieSession = engine.getKieSession();
//		SignalEventCommand command = new SignalEventCommand();
//		command.setProcessInstanceId(3l);
//
//		command.setEventType("insidehuman");

		//kieSession.execute(command);

		// ProcessInstance instance =kieSession.getProcessInstance(161l);
		//Object commandReturn = kieSession.signalEvent("insidehuman", null, 1l);;// kieSession.execute(command);
		kieSession.signalEvent("insidehuman", "no", 4l);
		//LOG.info("commandReturn {}", commandReturn);

		// kieSession.signalEvent("finishjob", params, 161l);

	}

	@Test
	public void testClaimHumanTask() {
		RuntimeEngine engine = buildRunTimeEngine();//buildRunTimeEngineWithUser("claimadjuster","password99!");
	  String caseworker = "caseworker";
		KieSession kieSession = engine.getKieSession();
		//ProcessInstance instance =kieSession.getProcessInstance(165l);
		
		TaskService taskService = engine.getTaskService();
		List<Long> tasksList = taskService.getTasksByProcessInstanceId(4l);

		engine = buildRunTimeEngineWithUser(caseworker,"password99!");
		for (Long taskId : tasksList) {
			LOG.info("task id {}", taskId);
			
			Map taskContent = new HashMap();
			taskContent.put("in_processRequest", "yes");
			try {
				taskService.claim(taskId, caseworker);
				LOG.info("claim successful : "+taskId);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				LOG.error("error : "+e.getMessage());
				continue;
			}
			
			taskService.start(taskId, caseworker);
			
			taskService.complete(taskId, caseworker, taskContent);
			 taskContent = taskService.getTaskContent(taskId);
			 
			LOG.info("taskContent {}",taskContent);
//			
			//(long1, "claimadjuster");
			
	
		}

	}
	
	 private RuntimeEngine buildRunTimeEngineWithUser(String caseworker, String string) {
		// TODO Auto-generated method stub
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
