package com.redhat.vizuri.test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.remote.client.api.RemoteRuntimeEngineFactory;
import org.kie.remote.client.jaxb.ConversionUtil;
import org.kie.remote.jaxb.gen.JaxbStringObjectPairArray;
import org.kie.remote.jaxb.gen.SignalEventCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessTestRemote {

	private static final Logger LOG = LoggerFactory.getLogger(ProcessTestRemote.class);

	private URL instanceUrl;
	private String user = "ashakya";
	private String password = "password99!";
	private String deploymentId = "com.redhat.vizuri.insurance:mobile-claims-bpm:1.0-SNAPSHOT";

	public RuntimeEngine buildRunTimeEngineWithUser(String user, String password) {

		try {
			instanceUrl = new URL("http://localhost:8080/business-central/");
		} catch (MalformedURLException e) {

			e.printStackTrace();
		}
		RuntimeEngine engine = RemoteRuntimeEngineFactory.newRestBuilder().addUrl(instanceUrl).addUserName(user)
				.addPassword(password).addDeploymentId(deploymentId).build();

		return engine;
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
		return buildRunTimeEngineWithUser(this.user, this.password);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSignalToFinishJob() {
		RuntimeEngine engine = buildRunTimeEngine();
		KieSession kieSession = engine.getKieSession();
		SignalEventCommand command = new SignalEventCommand();
		command.setProcessInstanceId(165l);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("processRequest", "no");

		command.setEventType("finishjob");

		JaxbStringObjectPairArray arrayMap = ConversionUtil.convertMapToJaxbStringObjectPairArray(params);
		command.setEvent(arrayMap);

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
		SignalEventCommand command = new SignalEventCommand();
		command.setProcessInstanceId(166l);

		command.setEventType("insidehuman");

		Object commandReturn = kieSession.execute(command);
		LOG.info("commandReturn {}", commandReturn);
	}

	@Test
	public void testClaimHumanTask() {
		RuntimeEngine engine = buildRunTimeEngine();// buildRunTimeEngineWithUser("claimadjuster","password99!");
		String caseworker = "caseworker";
		TaskService taskService = engine.getTaskService();
		List<Long> tasksList = taskService.getTasksByProcessInstanceId(166l);

		engine = buildRunTimeEngineWithUser(caseworker, "password99!");
		for (Long taskId : tasksList) {
			LOG.info("task id {}", taskId);

			Map<String,Object> taskContent = new HashMap<String,Object>();
			taskContent.put("in_processRequest", "yes");
			try {
				taskService.claim(taskId, caseworker);
				LOG.info("claim successful : " + taskId);
			} catch (Exception e) {
				LOG.error("error : " + e.getMessage());
				continue;
			}

			taskService.complete(taskId, caseworker, taskContent);
			taskContent = taskService.getTaskContent(taskId);

			LOG.info("taskContent {}", taskContent);
		}

	}
}
