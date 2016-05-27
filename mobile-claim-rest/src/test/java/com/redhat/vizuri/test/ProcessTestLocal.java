package com.redhat.vizuri.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

public class ProcessTestLocal {

	private static final Logger LOG = LoggerFactory.getLogger(ProcessTestLocal.class);
	private static RuntimeManager manager;

	@BeforeClass
	public static void setupBeforeClass() {

		EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.redhat.vizuri.jbpm.domain", null);

		KieServices kieServices = KieServices.Factory.get();
		KieContainer kieContainer = kieServices.getKieClasspathContainer();
		LOG.info("logger : {}", kieContainer);
		RuntimeEnvironmentBuilder builder = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder();
		builder.knowledgeBase(kieContainer.getKieBase("mobile-claim-kbase"))
				.userGroupCallback(new JBossUserGroupCallbackImpl("classpath:/roles.properties"))
				.entityManagerFactory(emf);

		LOG.info("kieContainer.getReleaseId() {}", kieContainer.getReleaseId());
		String releaseId = "com.redhat.vizuri.insurance:mobile-claims-bpm:1.0-SNAPSHOT";
		LOG.info("builder : {}", builder);
		manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(builder.get(), releaseId);

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
		params.put("processReaquest", "yes");
		ProcessInstance instance = kieSession.startProcess("mobile-claims-bpm.adhoc-test", params);
		LOG.info("instance id : " + instance.getId());

	}

	private RuntimeEngine buildRunTimeEngine() {
		return manager.getRuntimeEngine(EmptyContext.get());
	}

	@Test
	public void testSignalToFinishJob() {
		RuntimeEngine engine = buildRunTimeEngine();
		KieSession kieSession = engine.getKieSession();
		SignalEventCommand command = new SignalEventCommand();
		command.setProcessInstanceId(2l);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("processRequest", "no");

		command.setEventType("finishjob");
		command.setEvent(params);

		Object commandReturn = kieSession.execute(command);
		LOG.info("commandReturn {}", commandReturn);
	}

	@Test
	public void testSignalHumanTaskJob() {
		RuntimeEngine engine = buildRunTimeEngine();// buildRunTimeEngineWithUser("claimadjuster","password99!");

		KieSession kieSession = engine.getKieSession();
		kieSession.signalEvent("insidehuman", "no", 4l);
	}

	@Test
	public void testClaimHumanTask() {
		RuntimeEngine engine = buildRunTimeEngine();// buildRunTimeEngineWithUser("claimadjuster","password99!");
		String caseworker = "caseworker";

		TaskService taskService = engine.getTaskService();
		List<Long> tasksList = taskService.getTasksByProcessInstanceId(4l);

		engine = buildRunTimeEngineWithUser(caseworker, "password99!");
		for (Long taskId : tasksList) {
			LOG.info("task id {}", taskId);

			Map<String, Object> taskContent = new HashMap<String, Object>();
			taskContent.put("in_processRequest", "yes");
			try {
				taskService.claim(taskId, caseworker);
				LOG.info("claim successful : " + taskId);
			} catch (Exception e) {
				LOG.error("error : " + e.getMessage());
				continue;
			}

			taskService.start(taskId, caseworker);

			taskService.complete(taskId, caseworker, taskContent);
			taskContent = taskService.getTaskContent(taskId);

			LOG.info("taskContent {}", taskContent);
		}

	}

	private RuntimeEngine buildRunTimeEngineWithUser(String caseworker, String string) {
		return buildRunTimeEngine();
	}

}
