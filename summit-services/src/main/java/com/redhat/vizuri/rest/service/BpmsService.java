/*
 * #%L
 * Wildfly Camel :: Example :: Camel REST
 * %%
 * Copyright (C) 2013 - 2014 RedHat
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.redhat.vizuri.rest.service;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;

import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.runtime.manager.cdi.qualifier.PerProcessInstance;
import org.kie.internal.runtime.manager.context.EmptyContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class BpmsService {
	private static final Logger logger = LoggerFactory.getLogger(BpmsService.class);

	@Inject
	@PerProcessInstance
	private RuntimeManager runtimeManager;

	public Long startProcess() {
		RuntimeEngine engine = runtimeManager.getRuntimeEngine(EmptyContext.get());

		KieSession kieSession = engine.getKieSession();
		Map<String, Object> params = new HashMap();
		params.put("processReaquest", "yes");
		ProcessInstance instance = kieSession.startProcess("mobile-claims-bpm.adhoc-test", params);
		logger.info("instance id : " + instance.getId());

		return instance.getId();

	}
}
